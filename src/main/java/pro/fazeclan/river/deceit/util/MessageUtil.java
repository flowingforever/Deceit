package pro.fazeclan.river.deceit.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtil {

    public static Component formatComponent(String message) {
        return MiniMessage.miniMessage().deserialize("<red>\uD83D\uDDE1</red> " + message);
    }

}
