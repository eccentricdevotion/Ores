package me.eccentric_nz.ores;

public class OresStringUtils {

    public static String capitalise(String s) {
        String[] split = s.split("_");
        StringBuilder builder = new StringBuilder();
        for (String str : split) {
            builder.append(uppercaseFirst(str)).append(" ");
        }
        return builder.toString().trim();
    }

    private static String uppercaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
