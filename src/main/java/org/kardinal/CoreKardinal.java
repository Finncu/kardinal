package org.kardinal;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.kardinal.controller.TicketController;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoreKardinal {
    public static void main(String[] args) throws InterruptedException {
        String token = "rawr";
        TicketController vc = new TicketController();

        JDABuilder b = JDABuilder.createDefault(token).setStatus(OnlineStatus.ONLINE).setActivity(Activity.playing("rarr"))
                .addEventListeners(new CoreKardinalHandler().addController(vc));
        b.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).setMemberCachePolicy(MemberCachePolicy.ALL);
        JDA bot = b.build();
        vc.setBot(bot);
        bot.upsertCommand("verify", "verify").queue();
        bot.upsertCommand("spawn-verify", "spawn-verify").setDefaultPermissions(DefaultMemberPermissions.DISABLED).queue();

	SpringApplication.run(CoreKardinal.class, args);
    }
}

