package com.jayfella.sdk.core.tasks;

import com.jayfella.sdk.core.background.BackgroundTask;

import java.util.Random;

public class FakeBackgroundTask extends BackgroundTask {

    public FakeBackgroundTask() {
        super("Fake Task");
    }

    @Override
    public void execute() {

        Random random = new Random();

        while (true) {
            float progress = (random.nextFloat() * 2) - 1;
            setProgress(progress);
            setStatus("Status = " + progress);

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
