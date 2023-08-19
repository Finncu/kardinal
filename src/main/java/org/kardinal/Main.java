package org.kardinal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String token = "MTE0MjkwNDY1NjI4NTE1MTM4NA.GuSrUi.tLRzIz006TtpsktAb46CotSLEOhCh6nmGvQWcU";

        JDABuilder b = JDABuilder.createDefault(token).setStatus(OnlineStatus.ONLINE).setActivity(Activity.playing("rarr")).addEventListeners(new Commands());
        b.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        JDA bot = b.build();
        bot.upsertCommand("test", "test").queue();
        bot.upsertCommand("verify", "verify").queue();
    }
}