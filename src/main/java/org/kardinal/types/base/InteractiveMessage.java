package org.kardinal.types.base;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public abstract class InteractiveMessage {
    Message message;
    protected GuildMessageChannel textChannel; // TODO -- Achtung
    protected final MessageCreateBuilder builder = new MessageCreateBuilder().addContent("...");
    protected final EmbedBuilder embed = new EmbedBuilder();

    public InteractiveMessage(GuildMessageChannel pTextChannel) {
        setChannel(pTextChannel);
    }

    public InteractiveMessage() {};

    public InteractiveMessage changeChannel(GuildMessageChannel pTextChannel) {
        setChannel(pTextChannel);
        return this;
    }

    public InteractiveMessage setChannel(GuildMessageChannel pTextChannel) {
        textChannel = pTextChannel;
        return this;
    }
    
    public InteractiveMessage build() {
        // some changes
        return this;
    }

    public RestAction<Message> update() {
        buildEmbed();
        return message.editMessage(MessageEditData.fromCreateData(builder.build())).map(m -> {
            message = m;
            return m;
        });
    }
    
    public RestAction<Message> reSend() {
        return message.delete().flatMap(n -> send());
    }

    public RestAction<Message> send() {
        buildEmbed();
        return textChannel.sendMessage(builder.build()).map(m -> {
            message = m;
            return m;
        });
    }

    public void buildEmbed() {
        if (!embed.isEmpty())
            builder.addEmbeds(embed.build());
    }
}