package com.busylee.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by busylee on 19.08.14.
 */
public class LogToFile {

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

	public static void printToFile(final String path, final String str) {
		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				PrintWriter out = null;
				File file = GetFileFromPath(path);
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
					out.println(str);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(out != null)
						out.close();
				}
			}
		});
	}

	private static File GetFileFromPath(String path) {
		boolean ret;
		if (TextUtils.isEmpty(path)) {
			Log.e("Error", "The path of Log file is Null.");
			return null;
		}
		File file = new File(path);
		if (file.exists()) {
			if (! file.canWrite())
				Log.e("Error", "The Log file can not be written.");
		} else {
			//create the log file
			try {
				ret = file.createNewFile();
				if (ret) {
					Log.i("Success", "The Log file was successfully created! -" + file.getAbsolutePath());
				} else {
					Log.i("Success", "The Log file exist! -" + file.getAbsolutePath());
				}
				if (!file.canWrite()) {
					Log.e("Error", "The Log file can not be written.");
				}
			} catch (IOException e) {
				Log.e("Error", "Failed to create The Log file.");
				e.printStackTrace();
			}
		}
		return file;
	}
}
