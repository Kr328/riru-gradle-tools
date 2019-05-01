package com.github.kr328.rirutools.tasks;

import com.github.kr328.rirutools.extensions.NdkExtension;
import com.github.kr328.rirutools.properties.NdkProperties;
import com.github.kr328.rirutools.utils.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class NdkTask extends DefaultTask {
    @TaskAction
    void onAction() {
        NdkProperties ndkProperties = NdkProperties.readFromProject(getProject());
        NdkExtension extension = getProject().getExtensions().getByType(NdkExtension.class);

        if (extension.getOutputDir() == null) {
            setDidWork(false);
            return;
        }

        for (String abi : extension.getAbiFilters()) {
            try {
                cmakeConfigure(ndkProperties, extension, abi);
                cmakeBuild(ndkProperties, extension, abi);
            } catch (IOException | InterruptedException e) {
                throw new GradleScriptException("cmake build failure", e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void cmakeConfigure(NdkProperties properties, NdkExtension extension, String abi) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        StringBuilder outputs = new StringBuilder();

        builder.command(PathUtils.normalize(properties.getCmakeDirectory(), "/bin/cmake" + PathUtils.executableSuffix(".exe")),
                "-G", "Ninja",
                "-DCMAKE_MAKE_PROGRAM=" + PathUtils.normalize(properties.getCmakeDirectory(), "/bin/ninja" + PathUtils.executableSuffix(".exe")),
                "-DCMAKE_TOOLCHAIN_FILE=" + PathUtils.normalize(properties.getNdkDirectory(), "/build/cmake/android.toolchain.cmake"),
                "-DANDROID_PLATFORM=" + extension.getPlatform(),
                "-DANDROID_STL=" + extension.getStl(),
                "-DANDROID_ABI=" + abi,
                "-DCMAKE_ARCHIVE_OUTPUT_DIRECTORY=" + PathUtils.normalize(getProject().getBuildDir().getAbsolutePath() + "/" + extension.getOutputDir() + "/" + abi + "/static"),
                "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=" + PathUtils.normalize(getProject().getBuildDir().getAbsolutePath() + "/" + extension.getOutputDir() + "/" + abi + "/shared"),
                "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=" + PathUtils.normalize(getProject().getBuildDir().getAbsolutePath() + "/" + extension.getOutputDir() + "/" + abi + "/executable"),
                getProject().file(PathUtils.normalize(extension.getSourceDir())).getAbsolutePath());

        File cmakeConfigDirectory = getProject().file(PathUtils.normalize(getProject().getBuildDir().getAbsolutePath(), "/intermediate/cmake/", abi)).getAbsoluteFile();
        cmakeConfigDirectory.mkdirs();

        builder.directory(cmakeConfigDirectory);

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
            throw new IOException("CMake config failure: \n" + outputs.toString());

        process.destroy();
    }

    private void cmakeBuild(NdkProperties properties, NdkExtension extension, String abi) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        StringBuilder outputs = new StringBuilder();

        builder.command(PathUtils.normalize(properties.getCmakeDirectory(), "/bin/cmake" + PathUtils.executableSuffix(".exe")), "--build", ".");

        File cmakeConfigDirectory = getProject().file(PathUtils.normalize(getProject().getBuildDir().getAbsolutePath() + "/intermediate/cmake/" + abi)).getAbsoluteFile();

        builder.directory(cmakeConfigDirectory);

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
            throw new IOException("CMake build failure: \n" + outputs.toString());

        process.destroy();
    }
}
