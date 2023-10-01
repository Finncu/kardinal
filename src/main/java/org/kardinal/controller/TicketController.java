package org.kardinal.controller;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.kardinal.prop.V;
import org.kardinal.prop.VAR;
import org.kardinal.types.ticket.ServerVerify;
import org.kardinal.types.TicketChannel;
import org.kardinal.types.TicketType;
import org.kardinal.types.ticket.Ticket;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TicketController {
    private final LinkedList<Member> openMembers = new LinkedList<>();
    private final LinkedList<Long> registeredChannels = new LinkedList<>();

    private final LinkedHashMap<Long, Map<Long, ServerVerify>> channelRegistryPerServer = new LinkedHashMap<>();
    private final LinkedHashMap<Long, LinkedHashMap<Long, ServerVerify>> verifiesPerServer = new LinkedHashMap();
    private JDA bot;

    public void registerNewUser(Member pMember) {
        Guild guild = pMember.getGuild();
        VAR v = V.g(guild);
        if (!verifiesPerServer.containsValue(guild.getIdLong()))
            verifiesPerServer.put(guild.getIdLong(), new LinkedHashMap<>());
        if (!channelRegistryPerServer.containsKey(guild.getIdLong()))
            channelRegistryPerServer.put(guild.getIdLong(), new HashMap());
        openMembers.add(pMember);
        ServerVerify verify = new ServerVerify(pMember);
        verifiesPerServer.get(guild.getIdLong()).put(pMember.getIdLong(), verify);
        guild.createTextChannel(pMember + "_verify")
                .setParent(guild.getCategoryById(v.VERIFICATION_CATEGORY_ID))
                .addPermissionOverride(pMember, EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(guild.getRoleById(v.ADMIN_ROLE_ID), EnumSet.of(Permission.VIEW_CHANNEL), null)
                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL))
                .setTopic(TicketType.VERIFICATION)
                .map(n -> {
                    registerChannel(n, verify);
                    guild.getForumChannelById(v.TICKET_BOARD_ID).createForumPost(
                                    TicketType.VERIFICATION + " - " + pMember.getUser().getName(), verify.getStatusMessage()).setTags(guild.getForumChannelById(v.TICKET_BOARD_ID).getAvailableTagsByName("Verify", true).stream().findFirst().orElse(null))
                            .map(m -> registerChannel(m, verify)).queue();
                    return n;
                }).queue();

        guild.addRoleToMember(pMember, guild.getRoleById(v.VERIFY_ROLE)).queue();
    }

    private ForumPost registerChannel(ForumPost n, ServerVerify pVerify) {
        channelRegistryPerServer.get(n.getThreadChannel().getGuild().getIdLong()).put(n.getThreadChannel().getIdLong(), pVerify);
        pVerify.setChannel(n.getThreadChannel()).build().send().queue();
        registeredChannels.add(n.getThreadChannel().getIdLong());
        return n;
    }

    private TextChannel registerChannel(TextChannel pChannel, ServerVerify pVerify) {
        channelRegistryPerServer.get(pChannel.getGuild().getIdLong()).put(pChannel.getIdLong(), pVerify);
        pVerify.registerAuthorChannel(pChannel);
        registeredChannels.add(pChannel.getIdLong());
        return pChannel;
    }

    public void registerAction(ButtonInteractionEvent event) {
        ServerVerify verify = channelRegistryPerServer.get(event.getGuild().getIdLong()).get(event.getChannel().getIdLong());
        Member member = verify.getAuthor();
        Guild guild = event.getGuild();
        String command = event.getComponentId();
        VAR v = verify.v;
        switch (command) {
            case "verify.0", "verify.1", "ignore":
                event.replyModal(verify.getModal(command)).queue();
                break;
            case "accept":
                guild.addRoleToMember(member, guild.getRoleById(v.MEMBER_ROLE_ID)).queue();
                guild.removeRoleFromMember(member, guild.getRoleById(v.VERIFY_ROLE)).queue();
                member.getGuild().getTextChannelById(v.CHAT_CHANNEL_ID).sendMessage(verify.getMessage(command)).queue();
                event.deferEdit().queue();
                verify.setEditor(event.getMember());
                close(verify.close("accepted"));
                break;
            case "ban":
                event.replyModal(verify.getModal("ban")).queue();
                break;
        }
    }

    public boolean memberHasOpenVerify(Member pMember) {
        return openMembers.contains(pMember);
    }

    public void registerAction(ModalInteractionEvent event) {
        ServerVerify verify = channelRegistryPerServer.get(event.getGuild().getIdLong()).get(event.getChannel().getIdLong());
        User user = verify.getAuthor().getUser();
        Guild guild = event.getGuild();
        String command = event.getModalId();
        VAR v = verify.v;
        switch (command) {
            case "verify.1", "verify.2":
                verify.addInfos(event.getValues()).queue();
                if (verify.getStatus() < Integer.parseInt(command.split("\\.")[1])) {
                    verify.nextStatus();
                    event.getChannel().sendMessage(verify.getMessage(command)).queue();
                }
                break;
            case "ignore", "ban":
                verify.setEditor(event.getMember());
                close(verify.close(event.getValues()));
                user.openPrivateChannel().flatMap(n -> n.sendMessage(verify.getMessage(command))).queue();
                if (command.equals("ban"))
                    guild.ban(verify.getAuthor().getUser(), 0, TimeUnit.DAYS).queueAfter(10, TimeUnit.SECONDS);
                break;
        }
        event.deferEdit().queue();
    }

    private void close(Ticket verify) {
        long serverId = verify.getAuthor().getGuild().getIdLong();
        channelRegistryPerServer.get(serverId).remove(verify.getTicketChannel().getIdLong());
        channelRegistryPerServer.get(serverId).remove(verify.getAuthorChannel().getIdLong());
        registeredChannels.remove(verify.getTicketChannel().getIdLong());
        registeredChannels.remove(verify.getAuthorChannel().getIdLong());
        verifiesPerServer.get(serverId).remove(verify.getAuthor().getIdLong());
        verify.getAuthor().getGuild().getTextChannelById(V.g(verify.getAuthor().getGuild()).LOG_CHANNEL_ID).sendMessage(verify.getTicketMessage().getContent()).queue();
        openMembers.remove(verify.getAuthor());
    }

    public void registerAction(MessageReceivedEvent event) {
        ServerVerify verify = channelRegistryPerServer.get(event.getGuild().getIdLong()).get(event.getChannel().getIdLong());
        if (verify.getStatus() > 1 && (" " + event.getMessage().getContentStripped() + " ").contains(" " + verify.v.AUTH_CODE + " ")) {
            verify.getAuthorChannel().sendMessage(verify.getMessage("codeReceived")).queue();
            verify.getTicketMessage().setActionRow(verify.getActionRow("enabledControl"));
            verify.reSend().queue();
        }
    }

    public boolean channelRegistered(long id) {
        return registeredChannels.contains(id);
    }

    public void registerLeaveAction(GuildMemberRemoveEvent event) {
        close(verifiesPerServer.get(event.getGuild().getIdLong()).get(event.getMember().getIdLong()).close("leaved"));
    }

    public void setBot(JDA bot) {
        this.bot = bot;
    }

    public JDA getBot() {
        return bot;
    }
}
