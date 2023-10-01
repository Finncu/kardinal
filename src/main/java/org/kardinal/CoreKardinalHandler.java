package org.kardinal;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.interactions.modal.ModalImpl;
import org.jetbrains.annotations.NotNull;
import org.kardinal.controller.TicketController;
import org.kardinal.prop.V;
import org.kardinal.types.form.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.swing.*;

@AutoConfiguration
public class CoreKardinalHandler extends ListenerAdapter {
    TicketController ticketController;

    @Value("${spring.application.version}")
    private String version;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
    }

    public void log(String log) {
    	System.out.println(log);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        ticketController.registerNewUser(event.getMember());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        MessageCreateBuilder ms = new MessageCreateBuilder();
        switch (event.getFullCommandName()) {
            case "verify":
                ticketController.registerNewUser(event.getMember());
                break;
            case "spawn-verify":
                event.getChannel().sendMessage(new MessageCreateBuilder().addEmbeds(new EmbedBuilder()
                                .setTitle("Verification Starten")
                                .setThumbnail(event.getGuild().getIconUrl())
                        .build()).addActionRow(Button.success("kardl-verify", "Verify öffnen")).build()).queue();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        ticketController.registerLeaveAction(event);
        log("Channels of " + event.getMember().getUser().getName() + "deleted!");
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().equals("kardl-verify")) {
            if (!ticketController.memberHasOpenVerify(event.getMember()))
                ticketController.registerNewUser(event.getMember());
            event.deferEdit().queue();
        }
        if (ticketController.channelRegistered(event.getChannel().getIdLong()))
            ticketController.registerAction(event);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (ticketController.channelRegistered(event.getChannel().getIdLong()))
            ticketController.registerAction(event);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (ticketController.channelRegistered(event.getChannel().getIdLong()))
            ticketController.registerAction(event);
    }

    public CoreKardinalHandler addController(TicketController vc) {
        ticketController = vc;
        return this;
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        MessageCreateBuilder m = new MessageCreateBuilder();
        TextChannel tx = event.getGuild().getTextChannelById(V.g(event.getGuild()).BOT_LOG_CHANNEL_ID);
        EmbedBuilder eb = new EmbedBuilder();
        SelfUser bot = ticketController.getBot().getSelfUser();
        log(bot.getAvatarUrl().toString());
        eb.setTitle(bot.getEffectiveName() + " online");
        eb.setThumbnail(bot.getAvatarUrl().toString());
        eb.setDescription("Selfrole-Channel id aktualisiert");
        tx.sendMessage(m.setEmbeds(eb.build()).build()).queue();
    }
}
