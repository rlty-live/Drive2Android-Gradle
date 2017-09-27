package com.geronimostudios.drive2android.tasks;


import com.geronimostudios.drive2android.Drive2AndroidExtension;
import com.geronimostudios.drive2android.core.ParseHelper;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

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
        ParseHelper parser = new ParseHelper();

        d2a = (Drive2AndroidExtension)getProject()
                        .getExtensions()
                        .findByName("drive2android");
        outputStream = new ByteArrayOutputStream();
        try {
            d2a.getDrive().files().export(d2a.getFileId(), "text/csv")
                    .executeMediaAndDownloadTo(outputStream);
            fileList = parser.getStringFiles(d2a);
            input = parser.CSVtoMap(outputStream);
            output = parser.XMLtoMAP(fileList);
            output.putAll(input);
            parser.upload(output, d2a);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
