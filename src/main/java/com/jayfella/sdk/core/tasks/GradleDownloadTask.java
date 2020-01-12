package com.jayfella.sdk.core.tasks;

import com.jayfella.sdk.config.SdkConfig;
import com.jayfella.sdk.core.FolderStructure;
import com.jayfella.sdk.core.background.BackgroundTask;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GradleDownloadTask extends BackgroundTask {

    private static final Logger log = Logger.getLogger(GradleDownloadTask.class);

    public GradleDownloadTask() {
        super("Downloading Gradle");
    }

    private String constructFileName(String version, boolean all) {

        String filename = "gradle-" + version;
        if (all) filename += "-all";
        filename += ".zip";

        return filename;
    }

    @Override
    public void execute() {

        String version = "6.0.1";
        String gradleUrl = "https://services.gradle.org/distributions/";
        String filename = constructFileName(version, true);

        String outputFile = FolderStructure.Temporary.getFolder() + filename;

        log.info("Checking for Gradle version: " + version);

        if (!new File(outputFile).exists()) {

            log.info("Downloading Gradle version: " + version);

            try {

                URL url = new URL(gradleUrl + filename);
                HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
                long completeFileSize = httpConnection.getContentLength();

                java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());

                java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile);
                java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
                byte[] data = new byte[1024];
                long downloadedFileSize = 0;
                int x = 0;
                while ((x = in.read(data, 0, 1024)) >= 0) {
                    downloadedFileSize += x;

                    // calculate progress
                    double currentProgress = (double) downloadedFileSize / (double) completeFileSize;

                    // update progress bar
                    setProgress(currentProgress);

                    bout.write(data, 0, x);
                }
                bout.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        else {
            log.info("Gradle download already exists. Skipping download.");
        }

        // work out if a gradle version has already been unzipped.
        File existingDir = new File(FolderStructure.Gradle.getFolder() + "gradle-" + version);

        if (!existingDir.exists()) {

            // unzip to the folder
            String destination = FolderStructure.Gradle.getFolder();

            log.info("Unzipping Gradle " + version + " to " + destination);


            try {
                ZipFile zipFile = new ZipFile(outputFile);

                List fileHeaderList = zipFile.getFileHeaders();
                for (int i = 0; i < fileHeaderList.size(); i++) {
                    FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);

                    double progress = (i + 1d) / fileHeaderList.size();
                    setProgress(progress);

                    setStatus("Extracting: " + fileHeader.getFileName());

                    zipFile.extractFile(fileHeader, destination);
                }
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
        else {
            log.info("Gradle already exists. Skipping unzip.");
        }

        // delete the zip
        boolean deleteFile = false;
        if (deleteFile) {

            log.info("Deleting downloaded Gradle " + version + " zip file.");

            try {
                Files.delete(Paths.get(outputFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            log.info("Skipping delete Gradle " + version + " zip file.");
        }

        // set the gradle folder in our SDK Settings
        SdkConfig sdkConfig = SdkConfig.load();
        if (sdkConfig.getGradleFolder() == null) {
            log.info("Setting Gradle folder to: " + existingDir.toString());
            sdkConfig.setGradleFolder(existingDir.toString());
            sdkConfig.save();
        }
        else {
            log.info("Not setting Gradle folder. Already exists.");
        }

    }



}
