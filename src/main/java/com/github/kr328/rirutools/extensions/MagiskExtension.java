package com.github.kr328.rirutools.extensions;

import java.io.File;
import java.util.LinkedHashMap;

public class MagiskExtension {
    private ZipRecord zip = new ZipRecord();
    private String output = "$buildDir/outputs/magisk/magisk-module.zip";

    public ZipRecord getZip() {
        return zip;
    }

    public void setZip(ZipRecord zip) {
        this.zip = zip;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public static class ZipRecord {
        private LinkedHashMap<File, String> zipMap = new LinkedHashMap<>();

        public void map(File source, String target) {
            zipMap.put(source, target);
        }

        public LinkedHashMap<File, String> getZipMap() {
            return zipMap;
        }

        public void setZipMap(LinkedHashMap<File, String> zipMap) {
            this.zipMap = zipMap;
        }
    }
}
