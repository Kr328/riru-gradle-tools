package com.github.kr328.rirutools.properties;

import com.github.kr328.rirutools.utils.PropertiesUtils;
import org.gradle.api.GradleScriptException;
import org.gradle.api.Project;

import java.io.FileNotFoundException;
import java.util.Properties;

public class DexProperties {
    private String androidSdkPath;

    public static DexProperties readFromProject(Project project) {
        Properties properties = PropertiesUtils.readFromFiles(project.getRootProject().file("local.properties"),
                project.file("local.properties"));
        DexProperties result = new DexProperties();

        result.androidSdkPath = properties.getProperty("sdk.dir");

        if (result.androidSdkPath == null)
            throw new GradleScriptException("sdk.dir must be set in local.properties"
                    , new FileNotFoundException());

        return result;
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }
}
