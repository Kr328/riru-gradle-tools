package com.github.kr328.rirutools.extensions;

public class NdkExtension {
    private String sourceDir;
    private String outputDir = "/outputs/ndk";
    private String[] abiFilters = new String[]{"armeabi-v7a", "arm64-v8a"};
    private String platform = "android-21";
    private String stl = "c++_static";

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String[] getAbiFilters() {
        return abiFilters;
    }

    public void setAbiFilters(String[] abiFilters) {
        this.abiFilters = abiFilters;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStl() {
        return stl;
    }

    public void setStl(String stl) {
        this.stl = stl;
    }
}
