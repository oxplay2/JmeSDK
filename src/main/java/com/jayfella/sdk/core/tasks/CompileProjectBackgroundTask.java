package com.jayfella.sdk.core.tasks;

import com.google.common.io.Files;
import com.jayfella.sdk.core.background.BackgroundTask;
import com.jayfella.sdk.project.Project;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class CompileProjectBackgroundTask extends BackgroundTask {

    private static final Logger log = LoggerFactory.getLogger(CompileProjectBackgroundTask.class);

    public CompileProjectBackgroundTask() {
        super("Compile Project");
    }

    @Override
    public void execute() {

        String projectPath = Project.getOpenProject().getProjectPath();

        String[] command = null;

        if (SystemUtils.IS_OS_LINUX) {
            command = new String[] {"bash", "-c", "./gradlew shadowJar"};
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            command = new String[] {"cmd", "/c", "./gradlew.bat shadowJar"};
        }
        else {
            log.info("Operating System not implemented yet.");
        }

        if (command != null) {

            log.info("Compiling project.");
            setStatus("Compiling project...");

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(projectPath + "//"));
            processBuilder.command(command);

            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            try {

                Process process = processBuilder.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    setStatus(line);
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    log.info("Compile completed successfully.");
                    setStatus("Compile completed successfully.");
                }
                else {
                    log.error("Exited with error code: " + exitCode);
                    setStatus("Build Failed.");
                    succeededProperty().setValue(false);
                    return;
                }

            } catch (IOException | InterruptedException e) {

                // e.printStackTrace();

                log.error("Build failed.", e);

                setStatus("Build Failed.");
                succeededProperty().setValue(false);
                return;

            }
        }


        // copy the compiled lib to "./dist" as "dist.jar

        File distFolder = new File(projectPath, "./dist");
        File[] existingFiles = distFolder.listFiles();

        if (existingFiles != null) {
            for (File file : existingFiles) {

                log.info("Deleting old jar.");
                setStatus("Deleting old Jar...");

                boolean deleted = file.delete();

                if (!deleted) {

                    setStatus("Unable to delete old dist.jar.");
                    log.info("Unable to delete old dist jar.");

                    succeededProperty().setValue(false);
                    return;
                }

            }
        }

        // ./build/libs/*-all.jar

        File buildFolder = new File(projectPath, "/build/libs/");
        File[] buildFiles = buildFolder.listFiles();

        if (buildFiles != null) {
            File allJar = Arrays.stream(buildFiles)
                    .filter(file -> file.getName().endsWith("-all.jar"))
                    .findFirst()
                    .orElse(null);

            if (allJar != null && allJar.exists()) {

                log.info("Copying dist jar.");
                setStatus("Copying dist jar...");

                try {
                    Files.copy(allJar, new File(projectPath, "/dist/dist.jar"));
                } catch (IOException e) {

                    // e.printStackTrace();

                    log.error("Build failed.", e);

                    setStatus(e.getMessage());
                    succeededProperty().setValue(false);

                    return;
                }
            }
        }
        else {

            log.info("Unable to locate built jar in ./build/libs/");
            setStatus("Unable to locate built jar in ./build/libs/");

            succeededProperty().setValue(false);
            return;
        }

        log.info("Compile completed.");
        setStatus("Compile completed.");

    }

}
