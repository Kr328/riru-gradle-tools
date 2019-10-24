package com.github.kr328.rirutools.tasks;

import com.github.kr328.rirutools.extensions.DexExtension;
import com.github.kr328.rirutools.properties.DexProperties;
import com.github.kr328.rirutools.utils.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.GradleScriptException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DexTask extends DefaultTask {
    @TaskAction
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onAction() {
        Jar task = (Jar) getProject().getTasks().getByName("jar");
        File source = task.getArchivePath();

        DexProperties properties = DexProperties.readFromProject(getProject());
        DexExtension extension = getProject().getExtensions().getByType(DexExtension.class);

        String outputPath = PathUtils.normalize(getProject().getBuildDir().getAbsolutePath(),
                extension.getOutputDir(), extension.getOutput());
        File output = new File(outputPath);

        if ( source.lastModified() < output.lastModified() ) {
            setDidWork(false);
            return;
        }

        try {
            ProcessBuilder builder = new ProcessBuilder();
            StringBuilder outputs = new StringBuilder();

            String executable = PathUtils.normalize(properties.getAndroidSdkPath(),
                    "build-tools", extension.getBuildTools(), "/d8" + PathUtils.executableSuffix(".bat"));
            String libraryPath = PathUtils.normalize(properties.getAndroidSdkPath(),
                    "platforms", extension.getPlatform(), "android.jar");

            new File(outputPath).getParentFile().mkdirs();

            ArrayList<String> command = new ArrayList<>();

            command.add(executable);
            command.add("--output");
            command.add(outputPath);
            command.add("--lib");
            command.add(libraryPath);
            command.add(source.getAbsolutePath());

            builder.command(command.toArray(new String[0]));

            Process process = builder.start();
            BufferedReader reader;
            String line;

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null)
                outputs.append(line).append('\n');
            reader.close();

            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = reader.readLine()) != null)
                outputs.append(line).append('\n');
            reader.close();

            int result = process.waitFor();

            if (result != 0)
                throw new IOException("d8: " + command + "\n" + outputs.toString());

            if (!new File(outputPath).exists())
                throw new IOException("d8: " + command + "\n" + outputs.toString());

            process.destroy();
        } catch (IOException | InterruptedException e) {
            throw new GradleScriptException("d8: " + e.toString(), e);
        }
    }
}