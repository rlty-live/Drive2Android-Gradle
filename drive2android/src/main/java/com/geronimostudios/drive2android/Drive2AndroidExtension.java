package com.geronimostudios.drive2android;


import com.google.api.services.drive.Drive;

import org.gradle.api.Project;
import com.geronimostudios.drive2android.core.DriveBase;

import java.io.IOException;

public class Drive2AndroidExtension {

    private String fileId;
    private Drive mDrive;
    private String resPath;
    private String stringFileName;
    private Project mProject;

    public Drive2AndroidExtension (Project project){
        mProject = project;
    }

    public Drive getDrive() {
        return mDrive;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setClientSecret(String clientSecret) {
        try {
            this.mDrive = DriveBase.getDriveService(mProject.getName() + clientSecret);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String getResPath() {
        return resPath;
    }

    public void setResPath(String resPath) {
        this.resPath = mProject.getName() + resPath;
    }

    public String getStringFileName() {
        return stringFileName;
    }

    public void setStringFileName(String stringFileName) {
        this.stringFileName = stringFileName;
    }
}
