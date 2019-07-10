package com.github.kr328.rirutools.utils;

import java.io.File;
import java.util.regex.Pattern;

public class PathUtils {
    private static final Pattern SEPARATOR_ALL = Pattern.compile("[/\\\\]+");
    private static final String SEPARATOR = File.separator.replace("\\", "\\\\");

    public static String normalize(String base, String... tree) {
        StringBuilder sb = new StringBuilder();
        sb.append(base);

        for (String s : tree)
            sb.append('/').append(s);

        return SEPARATOR_ALL.matcher(sb.toString()).replaceAll(SEPARATOR);
    }

    public static String zipEntry(String p) {
        String result = p.replaceAll("/+", "/");
        if (result.startsWith("/"))
            return result.substring(1);
        return result;
    }

    public static String executableSuffix(String suffix) {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            return suffix;
        return "";
    }
}

