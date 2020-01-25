package com.jayfella.sdk.dialog;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * A Lazy loading directory chooser.
 * Only loads children of each parent when the treeItem is expanded.
 *
 * It is also presented in a treeview as opposed to the native FileChooser, which helps the user choose a folder quicker.
 *
 * @author jayfella
 */
public class CustomDirectoryChooser implements Initializable {

    private Stage stage;
    private Path selectedPath = null;

    @FXML private TreeView<Path> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Path show() {
        return show(Paths.get(System.getProperty("user.home")));
    }

    public Path show(Path path) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/JavaFx/Dialog/CustomDirectoryChooser.fxml"));
        fxmlLoader.setController(this);

        Parent root = null;

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root);
        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Choose Directory...");
        stage.setScene(scene);

        TreeItem<Path> treeRoot = new TreeItem<Path>(null);

        treeView.setShowRoot(false);
        treeView.setRoot(treeRoot);
        // create tree structure
        treeView.setCellFactory(p -> new PathTreeCell());

        for (Path fsRoot : FileSystems.getDefault().getRootDirectories()) {
            createTree(fsRoot, treeRoot);
        }

        selectFolder(path);

        stage.showAndWait();

        return selectedPath;
    }

    private void createTree(Path root_file, TreeItem<Path> parent) {

        if (root_file.toFile().isDirectory()) {

            TreeItem<Path> node = new TreeItem<>(root_file);

            parent.getChildren().add(node);

            File[] directories = root_file.toFile().listFiles(file -> file.isDirectory() && !file.isHidden() && file.canRead());

            if (directories != null) {
                for (File f : directories) {

                    TreeItem<Path> placeholder = new TreeItem<>(); // Add TreeItem to make parent expandable even it has no child yet.
                    node.getChildren().add(placeholder);

                    // When parent is expanded continue the recursive
                    node.addEventHandler(TreeItem.branchExpandedEvent(), new EventHandler() {
                        @Override
                        public void handle(Event event) {
                            createTree(f.toPath(), node); // Continue the recursive as usual
                            node.getChildren().remove(placeholder); // Remove placeholder
                            node.removeEventHandler(TreeItem.branchExpandedEvent(), this); // Remove event
                        }
                    });

                }
            }

        } else {
            parent.getChildren().add(new TreeItem<Path>(root_file));
        }
    }

    private int getParentCount(Path path) {

        int parentCount = 0;

        Path parent = path;

        while (parent.getParent() != null) {
            parent = parent.getParent();
            parentCount++;
        }

        return parentCount;
    }

    private Path getNthParent(Path path, int n) {

        Path result = path;

        for (int i = 0; i < n; i++) {
            result = result.getParent();
        }

        return result;
    }

    private void selectFolder(Path path) {

        // start at the beginning and work our way up.
        int parentCount = getParentCount(path);
        // Path pathRoot = getNthParent(path, parentCount);

        // now work our way up.
        TreeItem<Path> treeItem = treeView.getRoot();
        treeItem.setExpanded(true);

        int index = parentCount;

        for (int i = 0; i <= parentCount; i++) {

            Path p = getNthParent(path, index);

            TreeItem<Path> newItem = treeItem.getChildren()
                    .stream()
                    .filter(item -> item.getValue() != null && item.getValue().equals(p))
                    .findFirst()
                    .orElse(null);


            if (newItem == null) {
                String a = "b";
                break;
            }

            newItem.setExpanded(true);

            treeItem = newItem;
            index--;

            treeView.getSelectionModel().select(newItem);
        }

    }

    private static String pathToString(Path p) {
        if (p == null) {
            return "null";
        } else if (p.getFileName() == null) {
            return p.toString();
        }
        return p.getFileName().toString();
    }

    @FXML
    private void onOkButtonPressed(ActionEvent event) {

        Path returnPath = null;

        if (treeView.getSelectionModel().getSelectedItem() != null) {
            returnPath = treeView.getSelectionModel().getSelectedItem().getValue();
        }

        selectedPath = returnPath;

        stage.close();
    }

    @FXML
    private void onCancelButtonPressed(ActionEvent event) {
        selectedPath = null;
        stage.close();
    }

    private static class PathTreeCell extends TreeCell<Path> {

        @Override
        public void updateItem(Path item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(pathToString(item));
                setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER));
            }
        }
    }

}
