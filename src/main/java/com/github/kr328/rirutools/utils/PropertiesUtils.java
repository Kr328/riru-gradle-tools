package com.github.kr328.rirutools.utils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Properties;
import java.util.stream.Stream;

public class PropertiesUtils {
    public static Properties readFromFiles(File... files) {
        Properties result = new Properties();

        Stream.of(files)
                .filter(File::exists)
                .map(PropertiesUtils::openFileOrNull)
                .peek(fileInputStream -> propertiesLoadOrNot(result, fileInputStream))
                .forEach(PropertiesUtils::closeClient);

        return result;
    }

    private static FileInputStream openFileOrNull(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static void propertiesLoadOrNot(Properties properties, InputStream inputStream) {
        try {
            properties.load(inputStream);
        } catch (IOException ignored) {
        }
    }

    private static void closeClient(@Nullable InputStream inputStream) {
        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
    }
}
