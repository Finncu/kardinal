package org.kardinal.types.ticket;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.kardinal.prop.V;
import org.kardinal.prop.VAR;
import org.kardinal.types.base.BaseTicket;
import org.kardinal.types.base.InteractiveMessage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class Ticket extends InteractiveMessage {
    public final VAR v;
    private GuildMessageChannel authorChannel;
    private final Member author;
    private int status = 0;
    private ISnowflake editor;
    public final LinkedHashMap<String, String> information = new LinkedHashMap<>();

    public Ticket(Member pAuthor) {
        v = V.g(pAuthor.getGuild());
        author = pAuthor;
        information.put("author", pAuthor.getAsMention());
    }

    public void registerAuthorChannel(GuildMessageChannel pTextChannel){
        BaseTicket tes = new BaseTicket("tes");
        tes.test();
        if (authorChannel == null) {
            authorChannel = pTextChannel;
            information.put("authorChannel", authorChannel.getAsMention());
        }
        else new Exception("Author Channel already registered!").printStackTrace();
    }

    public ISnowflake getEditor() {
        return editor;
    }

    public Member getAuthor() {
        return author;
    }

    public GuildMessageChannel getAuthorChannel() {
        return authorChannel;
    }

    public ThreadChannel getTicketChannel() {
        return (ThreadChannel) textChannel;
    }

    public abstract Modal getModal(String modalId);

    public MessageCreateBuilder getTicketMessage() {
        return builder;
    }

    public abstract MessageCreateData getMessage(String messageId);

    public String buildString(String in) {
        StringBuilder out  = new StringBuilder();
        for (String piece : in.split("}")) {
            String[] pieces = piece.split("\\{");
            out.append(pieces[0]);
            if (pieces.length > 1)
                out.append(getVar(pieces[1]));
        }
        return out.toString();
    }

    private String getVar(String piece) {
        switch (piece) {
            case "channels" -> {
                return "<#" + v.INTRODUCTION_CHANNEL_ID + "> Hier kannst du dich Vorstellen\n\n" +
                    "<#" + v.CHAT_CHANNEL_ID + "> Hier kannst du Chatten\n\n" +
                    "<#" + v.SELFROLE_CHANNEL_ID + "> Hier kannst du deine Selfroles anpassen\n\n" +
                    "<#" + v.SUPPORT_CHANNEL_ID + "> Hier kannst du Support anfragen \n\n";
            }
            case "editorChannel" -> {
                return textChannel.getAsMention();
            }
            default -> {
                return information.get(piece);
            }
        }
    }

    public RestAction<Message> addInfos(List<ModalMapping> values) {
        for (ModalMapping mp : values)
            information.put(mp.getId(), mp.getAsString());
        return build().reSend();
    }

    public Ticket close(List<ModalMapping> values) {
        addInfos(values);
        return close();
    }

    public abstract MessageCreateData getStatusMessage();

    public Ticket close(String reason) {
        information.put("Grund", reason);
        builder.addContent("\n" + getMessage("close").getContent());
        return close();
    }

    public Ticket close() {
        getTicketChannel().delete().queue();
        getAuthorChannel().delete().queue();
        information.remove("authorChannel"); // TODO - in Enum umwandeln, .localization hinzufügen
        build();
        builder.addContent("\n" + getMessage("close").getContent()); // TODO - reimplement getMessage, close
        return this;
    }

    public void nextStatus() {
        status += 1;
    };

    public int getStatus() {
        return status;
    }

    public boolean isStatus(String s) {
        return s.equals(status + "");
    }

    public void setEditor(@NotNull IMentionable editorMember) {
        editor = editorMember;
        information.put("editor", editorMember.getAsMention());
    }
}
