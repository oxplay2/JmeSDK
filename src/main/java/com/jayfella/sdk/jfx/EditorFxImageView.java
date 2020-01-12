package com.jayfella.sdk.jfx;

import javafx.scene.image.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The resizable image view.
 *
 * @author JavaSaBr
 */
public class EditorFxImageView extends ImageView {

    private final Timer timer = new Timer();
    private TimerTask task = null;
    private final long delay = 200;


    public EditorFxImageView() {
    }

    @Override
    public double minHeight(double width) {
        return 64;
    }

    @Override
    public double maxHeight(double width) {
        return 1000;
    }

    @Override
    public double prefHeight(double width) {
        return minHeight(width);
    }

    @Override
    public double minWidth(double height) {
        return 64;
    }

    @Override
    public double maxWidth(double height) {
        return 10000;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {

        // a setup to stop resizing the JME viewport too much.
        // the task will wait a short period of time for the resizing to stop before it resizes.

        if (task != null) {
            task.cancel();
        }

        task = new TimerTask() {
            @Override
            public void run() {
                resizeAfter(width, height);
            }
        };

        timer.schedule(task, delay);
    }

    private void resizeAfter(double width, double height) {
        super.resize(width, height);
        super.setFitWidth(width);
        super.setFitHeight(height);
    }

}
