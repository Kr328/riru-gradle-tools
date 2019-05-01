package com.github.kr328.rirutools.properties;

import com.github.kr328.rirutools.utils.PropertiesUtils;
import org.gradle.api.GradleScriptException;
import org.gradle.api.Project;

import java.io.FileNotFoundException;
import java.util.Properties;

public class NdkProperties {
    private String ndkDirectory;
    private String cmakeDirectory;

    private NdkProperties() {
    }

    public static NdkProperties readFromProject(Project project) {
        Properties properties = PropertiesUtils.readFromFiles(project.getRootProject().file("local.properties"),
                project.file("local.properties"));
        NdkProperties result = new NdkProperties();

        result.ndkDirectory = properties.getProperty("ndk.dir");
        result.cmakeDirectory = properties.getProperty("cmake.dir");

        if (result.getNdkDirectory() == null || result.getCmakeDirectory() == null)
            throw new GradleScriptException("ndk.dir and cmake.dir must be set in local.properties"
                    , new FileNotFoundException());

        return result;
    }

    public String getNdkDirectory() {
        return ndkDirectory;
    }

    public String getCmakeDirectory() {
        return cmakeDirectory;
    }
}
