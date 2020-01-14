package com.jayfella.sdk.project.newproject;

import com.google.common.io.Resources;
import com.jayfella.sdk.config.RecentProjects;
import com.jayfella.sdk.config.SdkConfig;
import com.jayfella.sdk.project.DependencyData;
import com.jayfella.sdk.project.newproject.types.ProjectType;
import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NewProjectCreator {

    private static final Logger log = LoggerFactory.getLogger(NewProjectCreator.class);

    private static final String[] defaultResourceDirs = {
            "Interface",
            "MatDefs",  // j3md
            "Materials", // j3m
            "Models",
            "Scenes",
            "Textures",
            "Sounds",
            "Other" // Post processing stacks, etc.
    };

    private final String projectPath;
    private final DependencyData dependencyData;

    public NewProjectCreator(String projectPath, DependencyData dependencyData) {
        this.projectPath = projectPath;
        this.dependencyData = dependencyData;
    }

    public void create(ProjectType projectType) {

        File file = new File(projectPath);

        log.info("Creating project directories.");

        if (!file.mkdirs()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Create Project");
            alert.setContentText("Unable to create project directories.");
            alert.show();

            return;
        }

        /*
            Steps:

                - create project directories
                    - create ./src/main/java
                    - create ./src/main/java/ + groupId + artifactId
                    - create ./src/main/resources
                    - create resource dirs (Interface, Textures, Models, Scenes, etc)

                - copy build.gradle.
                - copy settings.gradle

                - put Main class in appropriate directory.
                - put any shared classes in appropriate directory.
                - put any shared resources in appropriate directory.

         */

        // create sources root
        Path sourcesRoot = Paths.get(projectPath, "src", "main", "java");
        Path resourcesRoot = Paths.get(projectPath, "src", "main", "resources");

        // create sources base (e.g. src/main/java/com/jayfella/mything)
        Path sourcesBase = Paths.get(sourcesRoot.toString(),
                dependencyData.getGroupId().replace(".", File.separator),
                dependencyData.getArtifactId()
        );

        // create a "./dist/" folder
        Path distFolder = Paths.get(projectPath, "dist");

        boolean mkSourcesDir = sourcesRoot.toFile().mkdirs();
        boolean mkResourcesDir = resourcesRoot.toFile().mkdirs();
        boolean mkSourcesBase = sourcesBase.toFile().mkdirs();
        boolean mkDistDir = distFolder.toFile().mkdirs();

        if (!(mkSourcesDir || mkResourcesDir || mkSourcesBase)) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Create Project");
            alert.setContentText("Unable to create project root directories.");
            alert.show();

            return;
        }

        // create resources directories
        for (String resourceDir : defaultResourceDirs) {
            Path resourcePath = Paths.get(projectPath, "src", "main", "resources", resourceDir);

            boolean created = resourcePath.toFile().mkdir();

            if (!created) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Create Project");
                alert.setContentText("Unable to create project root directories.");
                alert.show();

                return;
            }
        }

        // create build.gradle
        log.info("Creating project build.gradle.");
        copyGradleBuildFile();
        log.info("Creating project settings.gradle.");
        copyGradleSettingsFile();

        // create settings.gradle

        // copy source files
        log.info("Creating project sources.");
        for (String str : projectType.getSources()) {
            copyClassFile("Projects/" + projectType.getProjectFolder() + "/", sourcesBase, str);
        }

        // copy shared source files.
        log.info("Creating project shared sources.");
        for (String str : projectType.getSharedSources()) {
            copyClassFile("Projects/Shared/Sources/", sourcesBase, str);
        }

        // copy shared resources.
        log.info("Creating project shared resources.");
        for (String str: projectType.getSharedResources()) {
            copyResourceFile("Projects/Shared/Resources/", resourcesRoot, str);
        }

        // create wrapper
        // String gradlePathAbsolute = Paths.get(SdkConfig.load().getGradleFolder(), "bin").toAbsolutePath().toString();
        String gradlePathAbsolute = new File(SdkConfig.load().getGradleFolder(), "bin/").getAbsolutePath();

        if (SystemUtils.IS_OS_LINUX) {

            log.info("Creating project gradlew wrapper on LINUX");

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.environment().put("PATH", gradlePathAbsolute + File.pathSeparator + System.getenv("PATH"));
            processBuilder.directory(new File(projectPath + "/"));
            processBuilder.command("bash", "-c", "gradle wrapper");

            try {

                Process process = processBuilder.start();

                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println(line);
                    log.info(line);
                }

                int exitCode = process.waitFor();
                // System.out.println("\nExited with error code : " + exitCode);
                log.info("Exited with error code: " + exitCode);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            // System.out.println("Operating System Not Implemented.");
            log.info("Operating System not implemented yet.");
        }

        // System.out.println("Finished Wrapper.");
        log.info("Finished creating Gradle wrapper.");

        // update the "recent projects"
        RecentProjects recentProjects = RecentProjects.load();
        recentProjects.getRecentProjects().add(0, projectPath); // insert it into the first slot.
        recentProjects.save();
        // open the project.

        // execute "gradlew build".
        // this should be a task in itself. don't call it here, instead trigger it when necessary.

    }

    private void copyGradleBuildFile() {

        URL url = Resources.getResource("Projects/build.gradle");
        String fileAsString = null;

        try {
            fileAsString = Resources.toString(url, StandardCharsets.UTF_8);

        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

        fileAsString = fileAsString.replace("${GRADLE.GROUP}", dependencyData.getGroupId());
        fileAsString = fileAsString.replace("${GRADLE.VERSION}", dependencyData.getVersion());

        String mainClassName = dependencyData.getGroupId() + "." + dependencyData.getArtifactId() + ".Main";
        fileAsString = fileAsString.replace("${GRADLE.MAINCLASSNAME}", mainClassName);

        String jmeVersion = "3.3.0-alpha5";
        fileAsString = fileAsString.replace("${GRADLE.JME.VERSION}", jmeVersion);

        File newFile = Paths.get(projectPath, "build.gradle").toFile();

        try {
            FileUtils.writeStringToFile(newFile, fileAsString, StandardCharsets.UTF_8);
        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

    }

    private void copyGradleSettingsFile() {

        URL url = Resources.getResource("Projects/settings.gradle");
        String fileAsString = null;

        try {
            fileAsString = Resources.toString(url, StandardCharsets.UTF_8);

        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

        fileAsString = fileAsString.replace("${GRADLE.PROJECT_NAME}", dependencyData.getArtifactId());


        File newFile = Paths.get(projectPath, "settings.gradle").toFile();

        try {
            FileUtils.writeStringToFile(newFile, fileAsString, StandardCharsets.UTF_8);
        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

    }

    private void copyClassFile(String resourceFolder, Path sourcesRoot, String classFile) {

        URL url = Resources.getResource(resourceFolder + classFile);
        String classAsString = null;

        try {
            classAsString = Resources.toString(url, StandardCharsets.UTF_8);

        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

        String packageName = dependencyData.getGroupId() + "." + dependencyData.getArtifactId();
        classAsString = classAsString.replace("${PACKAGE}", packageName);

        File newFile = Paths.get(sourcesRoot.toString(), classFile).toFile();

        try {
            FileUtils.writeStringToFile(newFile, classAsString, StandardCharsets.UTF_8);
        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

    }

    private void copyResourceFile(String resourceFolder, Path resourcesRoot, String resourceFile) {

        byte[] data = null;

        try {
            data = this.getClass().getClassLoader().getResourceAsStream(resourceFolder + resourceFile).readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();

            showErrorAlert(e);
            return;
        }

        File newFile = Paths.get(resourcesRoot.toString(), resourceFile).toFile();

        try {
            FileUtils.writeByteArrayToFile(newFile, data);
        } catch (IOException e) {

            showErrorAlert(e);
            return;
        }

    }

    private void showErrorAlert(Exception e) {

        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Create Project");
        alert.setContentText(e.getMessage());
        alert.show();

    }

}
