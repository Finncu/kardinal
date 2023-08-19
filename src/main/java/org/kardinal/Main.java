package org.kardinal;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String token = "MTE0MTY3OTkxNTI4MDkwODI5OA.GCa_TW.KZml5ehc-PpqNoVg3YClCO8eNKwCyuONAjV9wE";

        JDABuilder b = JDABuilder.createDefault(token).setStatus(OnlineStatus.ONLINE).setActivity(Activity.playing("rarr")).addEventListeners(new Commands());
        b.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
        JDA bot = b.build().awaitReady();
        Guild g = bot.getGuildById("1013463649593081897");
        g.upsertCommand("fmod", "an modal");
        bot.upsertCommand("fmod", "fmod");
        bot.updateCommands();
    }
}