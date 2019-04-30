package com.github.kr328.rirutools.tasks;

import com.github.kr328.rirutools.extensions.DexExtension;
import com.github.kr328.rirutools.properties.DexProperties;
import com.github.kr328.rirutools.utils.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.*;
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
        JavaCompile task = (JavaCompile) getProject().getTasks().getByName("compileJava");
        File source = task.getDestinationDir();

        DexProperties properties = DexProperties.readFromProject(getProject());
        DexExtension extension = getProject().getExtensions().getByType(DexExtension.class);

        if (task.getState().getNoSource() || task.getState().getUpToDate()) {
            setDidWork(false);
            return;
        }

        try {
            ProcessBuilder builder = new ProcessBuilder();
            StringBuilder  outputs = new StringBuilder();

            String executable = PathUtils.trim(properties.getAndroidSdkPath() +
                    "/build-tools/" + extension.getBuildTools() +
                    "/d8" + PathUtils.executableSuffix(".bat"));
            String outputPath = PathUtils.trim(getProject().getBuildDir().getAbsolutePath()+ "/" + extension.getOutputDir() + "/" + extension.getOutput());
            String libraryPath = PathUtils.trim(properties.getAndroidSdkPath() + "/platforms/" + extension.getPlatform() + "/android.jar");

            new File(outputPath).getParentFile().mkdirs();

            ArrayList<String> command = new ArrayList<>();

            command.add(executable);
            command.add("--output");
            command.add(outputPath);
            command.add("--lib");
            command.add(libraryPath);

            Path sourceDirectory = Paths.get(source.getAbsolutePath());
            List<Path> excludePaths = extension.getExcludePackages().stream()
                    .map(s -> s.replaceAll("\\.+" ,File.separator))
                    .map(Paths::get)
                    .collect(Collectors.toList());

            Files.walk(sourceDirectory)
                    .map(sourceDirectory::relativize)
                    .filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> excludePaths.stream().noneMatch(p::startsWith))
                    .map(sourceDirectory::resolve)
                    .map(Path::toString)
                    .peek(System.out::println)
                    .forEach(command::add);

            System.out.println(command);

            builder.command(command.toArray(new String[0]));

            Process process = builder.start();
            BufferedReader reader;
            String line;

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (( line = reader.readLine()) != null )
                outputs.append(line).append('\n');
            reader.close();

            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while (( line = reader.readLine()) != null )
                outputs.append(line).append('\n');
            reader.close();

            int result = process.waitFor();

            if ( result != 0 )
                throw new IOException("Exec d8: " + command + "\n" + outputs.toString());

            if ( !new File(outputPath).exists() )
                throw new IOException("Exec d8: " + command + "\n" + outputs.toString());

            process.destroy();
        }
        catch (IOException | InterruptedException e) {
            throw new GradleScriptException("Exec d8: " + e.toString(),e);
        }
    }

    private static boolean filterExclude(DexExtension extension ,String path) {
        for ( String excludePackage : extension.getExcludePackages() ) {
            if ( path.startsWith(excludePackage) )
                return false;
        }

        return true;
    }
}