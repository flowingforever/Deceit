package pro.fazeclan.river.deceit.util;

import java.util.UUID;

public class MiscUtil {

    public static UUID fromStringOrNull(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException _) {
            return null;
        }
    }

}
