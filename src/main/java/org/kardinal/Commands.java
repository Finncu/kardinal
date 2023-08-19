package org.kardinal;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collection;

public class Commands extends ListenerAdapter {
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MessageCreateData ms = new MessageCreateBuilder().addContent("test").addActionRow(ActionRow.of(Button.danger("test", "test")).getComponents()).build();
            event.getJDA().getChannelById(MessageChannel.class, "1013463993450508439").sendMessage(ms).queue();
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        MessageCreateData ms = new MessageCreateBuilder().addContent("test").addActionRow(ActionRow.of(Button.danger("test", "test")).getComponents()).build();
        event.getChannel().asMessageChannel().sendMessage(ms).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Modal m = Modal.create("tes", "TESt").addActionRows(ActionRow.of(TextInput.create("name", "name", TextInputStyle.SHORT).build()), ActionRow.of(TextInput.create("lastname", "lastname", TextInputStyle.SHORT).build())).build();
        event.replyModal(m).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Modal m = Modal.create("tes", "TESt").addActionRows(ActionRow.of(TextInput.create("name", "name", TextInputStyle.SHORT).build()), ActionRow.of(TextInput.create("lastname", "lastname", TextInputStyle.SHORT).build())).build();
        event.replyModal(m).queue();
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (!event.getModalId().isBlank())
            event.reply("data: " + event.getValue("name").getAsString() + " " + event.getValue("lastname").getAsString()).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        System.out.println("rarr");
        MessageCreateData ms = new MessageCreateBuilder().addContent("test").addActionRow(ActionRow.of(Button.danger("test", "test")).getComponents()).build();
        if (!event.getAuthor().isBot() && event.getMessage().getContentStripped().contains("test"))
            event.getChannel().sendMessage(ms).queue();

    }
}
