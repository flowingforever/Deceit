package pro.fazeclan.river.deceit.util;

public class TimeUtil {

    public static String ticksIntoReadableFormat(long ticks) {
        long seconds = ticks / 20;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

}
