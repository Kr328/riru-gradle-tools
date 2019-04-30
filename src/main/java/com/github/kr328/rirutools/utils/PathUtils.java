package com.github.kr328.rirutools.utils;

import java.io.File;

public class PathUtils {
    public static String trim(String p) {
        return p.replaceAll("/+" , File.separator).replaceAll("\\+" ,File.separator);
    }

    public static String zipEntry(String p) {
        String result = p.replaceAll("/+" ,"/");
        if ( result.startsWith("/") )
            return result.substring(1);
        return result;
    }

    public static String executableSuffix(String suffix) {
        if ( System.getProperty("os.name").toLowerCase().contains("win") )
            return suffix;
        return "";
    }
}
