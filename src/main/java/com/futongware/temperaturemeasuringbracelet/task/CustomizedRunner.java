package com.futongware.temperaturemeasuringbracelet.task;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Function Tips
 * Use Supplier if it takes nothing, but returns something.
 *
 * Use Consumer if it takes something, but returns nothing.
 *
 * Use Callable if it returns a result and might throw (most akin to Thunk in general CS terms).
 *
 * Use Runnable if it does neither and cannot throw.
 *
 * @param <T>
 * @param <R>
 */
public class CustomizedRunner<T, R> implements Runnable {
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private List<String> pauseLock = new ArrayList<>();

    @Getter @Setter
    private T taskArgs = null;
    @Getter @Setter
    private Function<T, R> task;
    @Getter @Setter
    private Object[] errorHandlerArgs = null;
    @Getter @Setter
    private Consumer<Exception> errorHandler;
    @Getter @Setter
    private Consumer<R> recall;
    @Getter @Setter
    private Runnable finallyHandler;

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) {
                    break;
                }
                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) { // running might have changed since we paused
                        break;
                    }
                }//if(paused)
            }//syncronized
            try {
                R result = task.apply(taskArgs);
                if (recall != null)
                    recall.accept(result);
            } catch (Exception e) {
                System.err.println("Exception"+ e);
                running = false;
                if (errorHandler != null)
                    errorHandler.accept(e);
            }
            finally {
                if (finallyHandler != null)
                    finallyHandler.run();
            }
        }//while
    }//run()

    public void terminate() {
        running = false;
    }
    public void pause() {
        paused = true;
    }
    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }

}
