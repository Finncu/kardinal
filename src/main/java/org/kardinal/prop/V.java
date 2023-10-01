package org.kardinal.prop;

import net.dv8tion.jda.api.entities.Guild;

public abstract class V {

    public static VAR g(Guild guild) {
        switch (guild.getId()) {
            case "xxx":
                return new GAY_UNIVERSE();
            case "1149081027386548244":
                return new Aincrad();
        }
        return null;
    }
}
