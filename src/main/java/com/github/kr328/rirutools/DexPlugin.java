package com.github.kr328.rirutools;

import com.github.kr328.rirutools.extensions.DexExtension;
import com.github.kr328.rirutools.properties.DexProperties;
import com.github.kr328.rirutools.tasks.DexTask;
import org.gradle.api.*;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@NonNullApi
public class DexPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        if (!target.getPlugins().hasPlugin(JavaPlugin.class))
            target.getPlugins().apply(JavaPlugin.class);

        JavaCompile compile = (JavaCompile) target.getTasks().findByName("compileJava");
        if (compile == null)
            throw new GradleException("Failure to apply java plugin");

        target.getExtensions().create("dex", DexExtension.class);
        DexTask dexTask = target.getTasks().create("transformDex", DexTask.class);

        dexTask.dependsOn(target.getTasks().getByName("jar"));
        Optional.ofNullable(target.getTasks().findByName("assemble")).ifPresent(t -> t.dependsOn(dexTask));

        compile.doFirst(task -> {
            DexProperties properties = DexProperties.readFromProject(task.getProject());
            JavaCompile compiler = (JavaCompile) task;
            DexExtension extension = task.getProject().getExtensions().findByType(DexExtension.class);

            if (extension == null || extension.getPlatform() == null)
                throw new GradleScriptException("platform is null", new NullPointerException());

            File androidJar = Paths.get(properties.getAndroidSdkPath(), "platforms", extension.getPlatform(), "android.jar").toFile();

            if ( !androidJar.exists() )
                throw new GradleException("android.jar not found. " + androidJar.getAbsolutePath());

            compiler.setClasspath(target.files(androidJar.getAbsolutePath()).plus(compiler.getClasspath()));
        });
    }
}
