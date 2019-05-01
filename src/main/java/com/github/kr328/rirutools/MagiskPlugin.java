package com.github.kr328.rirutools;

import com.github.kr328.rirutools.tasks.MagiskTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Optional;

@NonNullApi
public class MagiskPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        MagiskTask task = target.getTasks().create("magiskModule", MagiskTask.class);

        Optional.ofNullable(target.getTasks().findByPath("assemble")).ifPresent(t -> t.dependsOn(task));
    }
}
