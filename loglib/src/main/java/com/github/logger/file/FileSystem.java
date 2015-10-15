package com.github.logger.file;

import com.github.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by busylee on 14.08.14.
 * Класс для работы с файловой системой, реализация интерфейса {@link IFileSystem}
 */

public class FileSystem implements IFileSystem {

	private static final String TAG = "FileSystem";
	public static final String DISALLOWED_FILENAME_CHARS = ":/\\?\"/|*<>";
	public static final int MAX_FILENAME_LENGTH = 100;

	/** Файл */
	protected File mFile;

    public FileSystem() {

    }

    public FileSystem(String path) {
        mFile = new File(path);
    }

	@Override
	public void setPath(String path) {
		mFile = new File(path);
	}

	@Override
	public void setPath(String dirPath, String filename) {
		mFile = new File(dirPath, filename);
	}

	@Override
	public String getPath() {
		if(mFile != null)
			return mFile.getPath();
		return null;
	}

	@Override
	public String getName() {
		return mFile.getName();
	}

	@Override
	public String getParentDir() {
		return mFile.getParent();
	}

	@Override
	public boolean exists() {
		return (mFile != null && mFile.exists());
	}

	@Override
	public boolean createDirectoryRecursive() {
		return (mFile != null  && mFile.mkdirs());
	}

	/**
	 * Удалить папку
	 * @param {@link File} папка
	 * @return
	 */
	public static boolean deleteDirectory(File fil) {
		boolean res = true;

		if (fil.isDirectory())
			for (File child : fil.listFiles()) {
				//Log.d("!!!", "Deleting... "+child.getPath()+"/"+child.getPhone());
				res = res && deleteDirectory(child);
			}


		return res && fil.delete();
	}

	@Override
	public boolean deleteDirectoryRecursive() {
		return (mFile != null  && deleteDirectory(mFile));
	}


	public boolean deleteDirectoryWithFilesRecursive() {
		return (mFile != null  && deleteDirectoryWithFiles(mFile));
	}

	private boolean deleteDirectoryWithFiles(File fil) {
		boolean res = true;

		if (fil.isDirectory()) {
			for (File child : fil.listFiles()) {
				res = res && deleteDirectoryWithFiles(child);
			}
			res = res && deleteDirectory(fil);
		} else if(fil.isFile()) {
			res = res && fil.delete();
		}

		return res;
	}


	@Override
	public boolean deleteFile() {
		return (mFile != null  && mFile.delete());
	}

	@Override
	public long getSize() {
		return mFile.length();
	}

	@Override
	public boolean deleteFile(String fname) {
		setPath(fname);
		return deleteFile();
	}

	@Override
	public boolean renameFile(String fname) {
		File newFile = new File(fname);
		return (mFile != null  && mFile.renameTo(newFile));
	}

	@Override
	public boolean copyFile(String newFileName) {
		try {
			File newFile = new File(newFileName);
			InputStream in = new FileInputStream(mFile);
			try {
				OutputStream out = new FileOutputStream(newFile);
				try {
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0){
						out.write(buf, 0, len);
					}
				} finally {
					try {
						out.close();
					} catch (IOException ignore) {}
				}
			} finally {
				try {
					in.close();
				} catch (IOException ignore) {}
			}
		} catch (IOException e) {
			Logger.warning(TAG, "Exception in copyFile: "+e.toString());
			return false;
		}
		return true;
	}

	@Override
	public void storeByteArray(byte[] buffer) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(mFile);
			out.write(buffer, 0, buffer.length);
		}catch (Exception e) {
			Logger.warning(TAG, ":storeByteArray() error", e);
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (IOException ignore) {}
		}
	}

	@Override
	public void setFile(File file) {
		mFile = file;
	}

	@Override
	public String getMimeType() {
		return Mime.getMimeType(mFile.getName());
	}

	@Override
	public void clearFile() {
        if(exists()) {
            deleteFile();
        } else {
            setPath(getParentDir());
            if(!exists())
                createDirectoryRecursive();
        }
	}

    @Override
    public boolean setPublic() {
        if(exists()) {
            return mFile.setReadable(true, false);
        } else {
            Logger.warning(TAG, ":setPublic() file does not exists");
        }

        return false;
    }

    @Override
    public boolean setPrivate() {
        if(exists()) {
            return mFile.setReadable(false, false);
        } else {
            Logger.warning(TAG, ":setPublic() file does not exists");
        }

        return false;
    }
}
