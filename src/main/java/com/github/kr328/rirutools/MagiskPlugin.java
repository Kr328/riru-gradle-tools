package com.github.kr328.rirutools;

import com.github.kr328.rirutools.extensions.MagiskExtension;
import com.github.kr328.rirutools.tasks.MagiskTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Optional;

@NonNullApi
public class MagiskPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        MagiskExtension ndkExtension = target.getExtensions().create("magisk", MagiskExtension.class);
        MagiskTask task = target.getTasks().create("magiskModule", MagiskTask.class);

        Optional.ofNullable(target.getTasks().findByPath("assemble")).ifPresent(t -> t.dependsOn(task));
        Optional.ofNullable(target.getTasks().findByPath("transformDex")).ifPresent(task::mustRunAfter);
        Optional.ofNullable(target.getTasks().findByPath("ndkBuild")).ifPresent(task::mustRunAfter);
    }
}
