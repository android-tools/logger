package com.github.logger;

/**
 * Created by busylee on 14.10.15.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "ExceptionHandler";

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    ExceptionHandler(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        mDefaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
    }

    static void init() {
        Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if(defaultUncaughtExceptionHandler instanceof ExceptionHandler) {
           return;
        }

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(defaultUncaughtExceptionHandler));
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.error(TAG, ":uncaughtException()", ex);
        if(mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
        }
    }
}
