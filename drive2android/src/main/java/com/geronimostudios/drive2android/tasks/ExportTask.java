package com.geronimostudios.drive2android.tasks;


import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import com.geronimostudios.drive2android.Drive2AndroidExtension;
import com.geronimostudios.drive2android.core.ParseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

public class ExportTask extends DefaultTask {

    @TaskAction
    public void doExport() {
        HashMap<String, String[]> input;
        HashMap<String, String[]> output;
        File[] fileList;
        Drive2AndroidExtension d2a;
        ByteArrayOutputStream outputStream;

        d2a = (Drive2AndroidExtension)getProject()
                        .getExtensions()
                        .findByName("drive2android");
        outputStream = new ByteArrayOutputStream();
        try {
            d2a.getDrive().files().export(d2a.getFileId(), "text/csv")
                    .executeMediaAndDownloadTo(outputStream);
            fileList = ParseHelper.getStringFiles(d2a);
            input = ParseHelper.CSVtoMap(outputStream);
            output = ParseHelper.XMLtoMAP(fileList);
            output.putAll(input);
            ParseHelper.upload(output, d2a);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
