package com.github.kr328.rirutools;

import com.github.kr328.rirutools.extensions.NdkExtension;
import com.github.kr328.rirutools.tasks.NdkTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Optional;

@NonNullApi
public class NdkPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        NdkTask task = project.getTasks().create("ndkBuild", NdkTask.class);
        NdkExtension ndkExtension = project.getExtensions().create("ndk", NdkExtension.class);

        Optional.ofNullable(project.getTasks().findByName("classes")).ifPresent(t -> t.dependsOn(task));
    }
}
