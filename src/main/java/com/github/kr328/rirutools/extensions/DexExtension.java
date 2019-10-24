package com.github.kr328.rirutools.extensions;

import java.util.ArrayList;

public class DexExtension {
    private String buildTools;
    private String platform;
    private String outputDir = "/outputs/dex/";
    private String output = "classes.jar";

    public String getBuildTools() {
        return buildTools;
    }

    public void setBuildTools(String buildTools) {
        this.buildTools = buildTools;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
