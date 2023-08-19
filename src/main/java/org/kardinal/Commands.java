package org.kardinal;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
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
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role r = event.getGuild().getRolesByName("verify", true).get(0);
        event.getMember().getRoles().add(r);
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getFullCommandName().equals("verify")) {
            MessageCreateData ms = new MessageCreateBuilder().addContent("Bitet Verifiziere dich :)").addActionRow(ActionRow.of(Button.danger("verify", "Verifizieren")).getComponents()).build();
            return;
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        switch (event.getButton().getId()) {
            case "verify":
                Modal m = Modal.create("verify", "Verification").addActionRows(
                        ActionRow.of(TextInput.create("age", "Age", TextInputStyle.SHORT).setRequired(true).build()),
                        ActionRow.of(TextInput.create("gender", "Geschlecht", TextInputStyle.SHORT).build()),
                        ActionRow.of(TextInput.create("prefix", "Pronomen", TextInputStyle.SHORT).build()),
                        ActionRow.of(TextInput.create("sexuality", "Sexualität", TextInputStyle.SHORT).build())

                ).build();
                event.replyModal(m).queue();
                break;
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
    }
}
