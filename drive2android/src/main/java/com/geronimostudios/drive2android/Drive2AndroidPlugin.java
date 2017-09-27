package com.geronimostudios.drive2android;

import com.geronimostudios.drive2android.tasks.ExportTask;
import com.geronimostudios.drive2android.tasks.ImportTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class Drive2AndroidPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("drive2android", Drive2AndroidExtension.class, project);
        project.getTasks().create("exportStrings", ExportTask.class);
        project.getTasks().create("importStrings", ImportTask.class);
    }
}
