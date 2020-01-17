package com.jayfella.sdk.sdk.list.filter;

import com.jayfella.sdk.controller.SceneConfiguration;
import com.jayfella.sdk.core.DnDFormat;
import com.jayfella.sdk.core.SelectablePostProcessor;
import com.jayfella.sdk.ext.core.FilterManager;
import com.jayfella.sdk.ext.core.ServiceManager;
import com.jayfella.sdk.ext.service.JmeEngineService;
import com.jme3.post.Filter;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.util.Map;

public class FilterCellFactory implements Callback<ListView<SelectablePostProcessor>, ListCell<SelectablePostProcessor>> {

    private final SceneConfiguration sceneConfiguration;
    private ListCell<SelectablePostProcessor> draggedItem;

    public FilterCellFactory(SceneConfiguration sceneConfiguration) {
        this.sceneConfiguration = sceneConfiguration;
    }

    @Override
    public ListCell<SelectablePostProcessor> call(ListView<SelectablePostProcessor> listView) {

        ListCell<SelectablePostProcessor> cell = new FilterCell();

        cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, listView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, listView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, listView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());

        cell.setOnDragEntered(event -> {
            cell.setOpacity(0.3);
        });

        cell.setOnDragExited(event -> {
            cell.setOpacity(1);
        });

        return cell;
    }

    private void dragDetected(MouseEvent event, ListCell<SelectablePostProcessor> listCell, ListView<SelectablePostProcessor> listView) {

        draggedItem = listCell;

        Dragboard db = listCell.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();

        content.put(DnDFormat.JME_FILTER, 0);
        db.setContent(content);
        db.setDragView(listCell.snapshot(null, null));
        event.consume();

    }

    private void dragOver(DragEvent event, ListCell<SelectablePostProcessor> listCell, ListView<SelectablePostProcessor> listView) {

        if (listCell == null) {
            return;
        }

        if (event.getDragboard().hasContent(DnDFormat.JME_FILTER)) {

            // if it's not the same
            if (listCell != draggedItem) {

                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();

            }

        }

    }

    private void drop(DragEvent event, ListCell<SelectablePostProcessor> listCell, ListView<SelectablePostProcessor> listView) {

        if (event.getDragboard().hasContent(DnDFormat.JME_FILTER)) {

            // if it's not the same
            if (listCell != draggedItem) {

                // get the index of the item we are hovering over and move it above.
                int hoveredIndex = listView.getItems().indexOf(listCell.getItem());
                // int dragIndex = listView.getItems().indexOf(draggedItem.getItem());
                // FilterPostProcessor fpp = ServiceManager.getService(JmeEngineService.class).getFilterPostProcessor();

                // Registrar<FilterRegistrar> filterRegistration = ServiceManager.getService(RegistrationService.class).getFilterRegistration();
                FilterManager filterManager = ServiceManager.getService(JmeEngineService.class).getFilterManager();

                // FilterRegistrar filterRegistrar = null;
                Class<? extends Filter> filterClass = null;
                Filter filter = null;

                for (Map.Entry<Class<? extends Filter>, Filter> entry : filterManager.getFilters().entrySet()) {

                    if (entry.getKey().isAssignableFrom(draggedItem.getItem().getFilterClass())) {
                        // filterRegistrar = entry.getKey();
                        filterClass = entry.getKey();
                        filter = entry.getValue();
                        break;
                    }
                }


                filterManager.getFilters().remove(filterClass);
                filterManager.getFilters().put(hoveredIndex, filterClass, filter);

                sceneConfiguration.populatePostProcessors();
                filterManager.refreshFilters(); // refresh the filters (re-load in the new order).


                event.setDropCompleted(true);
                event.consume();

            }

        }

    }

    private void clearDropLocation() {

    }

}