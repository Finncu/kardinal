package org.kardinal.types.ticket;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ServerVerify extends Ticket {

    private Map<String, MessageCreateBuilder> messages = Map.ofEntries(
            Map.entry("accept",
                    new MessageCreateBuilder()
                    .addContent(
                            ">>> {author} hat den Weg ins Gay-Universe gefunden. heißt ihn gerne Willkomen\n" +
                            "_Vielen Dank für deine Gedult_\n" +
                            "{channels}" +
                            "*Vergiss bitte nicht dir deine Pflichtrollen zu holen*"
                    )),
            Map.entry("authorChannel",
                    new MessageCreateBuilder()
                            .addContent("Hallo {author}, in diesem Kanal stellt dir das Server Team Fragen zu deinen Angaben. Habe bitte Verständnis dafür. Dieser Chat wird nach der Verifikation gelöscht. :)")
                            .addActionRow(Button.success("verify.0", "Verifizieren"))),
            Map.entry("verify.1",
                    new MessageCreateBuilder()
                            .setContent("Ein Formular noch dann bist du fast fertig")
                            .addActionRow(Button.success("verify.1", "Weiter"))),
            Map.entry("verify.2",
                    new MessageCreateBuilder()
                            .setContent("Bitte lese dir als letztes die Regeln durch und schicke den dort versteckten 4-stelligen Code (z.b 0000) in den Chat ")),
            Map.entry("ignore",
                    new MessageCreateBuilder()
                            .addContent(">>> Leider haben unsere Admins dich aus folgendem Grund abgelehnt:\n{Ablehnungsgrund}")),
            Map.entry("ban",
                    new MessageCreateBuilder()
                            .addContent(">>> Leider haben unsere Admins dich aus folgendem Grund gebannt:\n{Bannungsgrund}")),
            Map.entry("codeReceived",
                    new MessageCreateBuilder()
                            .addContent("Bitte habe geduld. Das Server Team wird deine Verifikation so schnell wie möglich Bearbeiten. Das Pingen von Team Mitglieder ist untersagt und verlängert nur die Wartezeit. Danke für dein Verständnis :)")),
            Map.entry("close",
                    new MessageCreateBuilder()
                            .addContent("\n_Ticket durch {editor} geschlossen_"))
                    );
    private Map<String, Modal.Builder> modals = Map.ofEntries(
            Map.entry("verify.0",
                    Modal.create("verify.1", "Verification").addActionRows(
                            ActionRow.of(TextInput.create("Name", "Name", TextInputStyle.SHORT).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Alter", "Alter", TextInputStyle.SHORT).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Geschlecht", "Geschlecht", TextInputStyle.SHORT).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Pronomen", "Pronomen", TextInputStyle.SHORT).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Sexualität", "Sexualität", TextInputStyle.SHORT).setRequired(true).build())
                            )),
            Map.entry("verify.1",
                    Modal.create("verify.2", "Verification").addActionRows(
                            ActionRow.of(TextInput.create("Geburtsdatum", "Geburtsdatum", TextInputStyle.SHORT).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Hobbies", "Hobbies", TextInputStyle.PARAGRAPH).build()),
                            ActionRow.of(TextInput.create("Extrainfos", "Extrainfos", TextInputStyle.PARAGRAPH).setRequired(false).build()),
                            ActionRow.of(TextInput.create("Warum wir?", "Warum wir?", TextInputStyle.PARAGRAPH).setRequired(true).build()),
                            ActionRow.of(TextInput.create("Woher kennst du uns?", "Woher kennst du uns?", TextInputStyle.PARAGRAPH).setRequired(true).build()))),
            Map.entry("ignore",
                    Modal.create("ignore", "Ablehnen").addActionRows(
                            ActionRow.of(TextInput.create("Ablehnungsgrund", "Grund", TextInputStyle.PARAGRAPH).setRequired(true).build())
                    )),
            Map.entry("ban",
                    Modal.create("ban", "Bannen").addActionRows(
                            ActionRow.of(TextInput.create("Bannungsgrund", "Bannungsgrund", TextInputStyle.PARAGRAPH).setRequired(true).build())
                    )));
    private Map<String, List<ItemComponent>> actionRows = Map.ofEntries(
            Map.entry("control", List.of(
                            Button.success("accept", "Aktzeptieren").asDisabled(),
                            Button.secondary("ignore", "Ablehnen"),
                            Button.danger("ban", "Bannen")
            )),
            Map.entry("enabledControl", List.of(
                    Button.success("accept", "Aktzeptieren"),
                    Button.secondary("ignore", "Ablehnen"),
                    Button.danger("ban", "Bannen")
            ))
    );

    public ServerVerify(Member pMember) {
        super(pMember);
        setEditor(getAuthor().getGuild().getRoleById(v.ADMIN_ROLE_ID));
    }

    @Override
    public void registerAuthorChannel(GuildMessageChannel pTextChannel) {
        super.registerAuthorChannel(pTextChannel);
        //ChannelManager<?, ?> manager = pTextChannel.getManager();
        //manager.setName(getAuthor().getUser().getName() + "_verify").queue();
        pTextChannel.sendMessage(getMessage("authorChannel")).queue();
    }

    @Override
    public ServerVerify build() {
        builder.clear().setActionRow(getActionRow("control"));
        for (Map.Entry e : information.entrySet())
            builder.addContent("\n> " + e.getKey() + " : " + e.getValue());
        // TODO - Bearbeiter, update & Status
        return this;
    }

    public Modal getModal(String modalId) {
        Modal.Builder modal = modals.get(modalId);
        return modal.build();
    }

    public MessageCreateData getMessage(String messageId) {
        MessageCreateBuilder msb = messages.get(messageId);
        msb.setContent(buildString(msb.getContent()));
        return msb.build();
    }

    @Override
    public MessageCreateData getStatusMessage() {
        return new MessageCreateBuilder().setContent("status : x").build();
    }

    public Collection<? extends ItemComponent> getActionRow(String actionRowId) {
        return actionRows.get(actionRowId);
    }
}
