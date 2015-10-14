/**
 Copyright (C) 2014 Ancort Ltd 96

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.busylee.log;

import android.os.Build;

import com.busylee.file.FileSystem;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by busylee on 18.08.14.
 */
public class Logger {

	private static final long DEFAULT_MAX_LOG_SIZE = 10*1024*1024; //10 mb
	private static final String ANDROID_LOG_STRING_FORMAT = "%02d-%02d %02d:%02d:%02d.%03d %04d %04d %s %s: %s";

	private static String sLogFilePath;
    private static long sMaxFileSize = DEFAULT_MAX_LOG_SIZE;
	private static boolean sNeedPrintToFile = false;
    private static boolean sOneFile = false;
    static boolean sWorldPublicFile = true;

	static final Map<String, String> mPhoneInfoMap = new HashMap<>();

	enum LogLevel {
		EInfo("I"),
		EVerbose("V"),
		EDebug("D"),
		EWarning("W"),
		EError("E");

		final String mLevelString;

		LogLevel(String levelString) {
			mLevelString = levelString;
		}

		public String getLevelString() {
			return mLevelString;
		}
	}

    public static void setOneFileMode() {
        sOneFile = true;
    }

    public static void setLogFilePrivate() {
        sWorldPublicFile = false;
    }

	public static void setFilePath(String path) {
        sLogFilePath = chooseLogPath(path);
        prepareFile(sLogFilePath);
        sNeedPrintToFile = true;
	}

    public static String getFilePath() {
        return sLogFilePath;
    }

    private static void prepareFile(String path) {
        FileSystem fileSystem = new FileSystem(path);
        if(sWorldPublicFile) {
            fileSystem.setPublic();
        } else {
            fileSystem.setPrivate();
        }
    }

	private static String chooseLogPath(String path) {
		FileSystem fs = new FileSystem();
		fs.setPath(path);

		String dirPath = fs.getParentDir();
		if(fs.exists() && fs.getSize()> sMaxFileSize) {

			String filename = fs.getName();
            if(!sOneFile) {
                String fileNameBody = filename.substring(0, filename.lastIndexOf("."));
                String fileNameExtension = filename.substring(filename.lastIndexOf("."));

                int k = 2;
                //todo ugly
                while (true){
                    String newPath = String.format("%s(%d)%s", fileNameBody, k++, fileNameExtension);
                    fs.setPath(dirPath, newPath);
                    if(!fs.exists())
                        return fs.getPath();
                }
			} else {
                fs.clearFile();
            }
		} else {
			fs.setPath(fs.getParentDir());
			if(!fs.exists())
				fs.createDirectoryRecursive();
		}
		return path;
	}

    public static void setMaxFileSize(long maxFileSize) {
        sMaxFileSize = maxFileSize;
    }

	public static void printPhoneInfo() {

		final String TAG_PHONE_INFO = "PhoneInfo";

		info(TAG_PHONE_INFO, "1. Phone model: " + Build.MODEL);
		info(TAG_PHONE_INFO, "2. Software number: " + Build.VERSION.INCREMENTAL);
		info(TAG_PHONE_INFO, "3. Android version: " + Build.VERSION.RELEASE);
		info(TAG_PHONE_INFO, "4. Kernel version: " + System.getProperty("os.version"));
		info(TAG_PHONE_INFO, "5. Fingerprint: " + Build.FINGERPRINT);

		int i = 7;
		for(String name : mPhoneInfoMap.keySet()) {

			String val = mPhoneInfoMap.get(name);
			info(TAG_PHONE_INFO, String.valueOf(i) + ". " + name + ": " + val);

			i++;
		}

	}

	private static void printLogString(LogLevel logLevel, String tag, String message) {
		printLogString(logLevel, tag, message, null);
	}

	protected static boolean needPrintLog() {
		return true;
	}

	private static void printLogString(LogLevel logLevel, String tag, String message, Throwable thr) {
		if(!needPrintLog())
			return;

		if( thr != null )
			message += "\n" + android.util.Log.getStackTraceString(thr);

		switch (logLevel) {
			case EInfo:
				android.util.Log.i(tag, message);
				break;
			case EVerbose:
				android.util.Log.v(tag, message);
				break;
			case EDebug:
				android.util.Log.d(tag, message);
				break;
			case EWarning:
				android.util.Log.w(tag, message);
				break;
			case EError:
				android.util.Log.e(tag, message);
				break;
			default:
				android.util.Log.i(tag, message);
		}

		if(sNeedPrintToFile)
			printToFile(logLevel, tag, message);

	}

	private static void printToFile(LogLevel logLevel, String tag, String message) {
		LogToFile.printToFile(sLogFilePath, getLogString(logLevel, tag, message));
	}

	private static String getLogString(LogLevel logLevel, String tag, String message) {
		Calendar c = Calendar.getInstance();
		return String.format(ANDROID_LOG_STRING_FORMAT,
				c.get(Calendar.MONTH) + 1,  // java такая java
				c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND),
				c.get(Calendar.MILLISECOND),
				android.os.Process.myPid(),
				android.os.Process.myTid(),
				logLevel.getLevelString(),
				tag,
				message);
	}

	public static void updatePhoneInfo(String name, String value) {
		mPhoneInfoMap.put(name, value);
	}

	public static void debug(String tag, String message) {
		printLogString(LogLevel.EDebug, tag, message);
	}

	public static void verbose(String tag, String message) {
		printLogString(LogLevel.EVerbose, tag, message);
	}

	public static void info(String tag, String message) {
		printLogString(LogLevel.EInfo, tag, message);
	}

	public static void warning(String tag, String message) {
		printLogString(LogLevel.EWarning, tag, message);
	}

	public static void error(String tag, String message) {
		printLogString(LogLevel.EError, tag, message);
	}

	public static void debug(String tag, String message, Throwable thr) {
		printLogString(LogLevel.EDebug, tag, message, thr);
	}

	public static void verbose(String tag, String message, Throwable thr) {
		printLogString(LogLevel.EVerbose, tag, message, thr);
	}

	public static void info(String tag, String message, Throwable thr) {
		printLogString(LogLevel.EInfo, tag, message, thr);
	}

	public static void warning(String tag, String message, Throwable thr) {
		printLogString(LogLevel.EWarning, tag, message, thr);
	}

	public static void error(String tag, String message, Throwable thr) {
		printLogString(LogLevel.EError, tag, message, thr);
	}

}
