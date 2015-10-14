package com.busylee.file;

import java.io.File;

/** Created by busylee on 14.08.14.
 * Интерфейс для работы с файловой системой
 */
public interface IFileSystem {

	/**
	 * Установить путь к объекту
	 * @param path
	 */
	void setPath(String path);

	/**
	 * Установить путь к объекту
	 * @param dirPath
	 * @param filename
	 */
	void setPath(String dirPath, String filename);

	/**
	 * Получить путь, на который ссылается данный объект
	 * @return path
	 */
	String getPath();

	String getName();

	String getParentDir();

	/**
	 * Получить размер файла
	 * @return размер файла
	 */
	long getSize();

	/**
	 * Существует ли файл?
	 * @return true, если существует
	 */
	boolean exists();

	/**
	 * Создать путь к файлу
	 * @return true в случае успешного создания
	 */
	boolean createDirectoryRecursive();

	/**
	 * Удалить директорию и все объекты в ней
	 * @return true в случае успешного удаления
	 */
	boolean deleteDirectoryRecursive();

	/**
	 * Удалить текущий файл
	 * @return true в случае успешного удаления
	 */
	boolean deleteFile();

	/**
	 * Удалить файл
	 * @param путь к файлу
	 * @return true в случае успешного удаления
	 */
	boolean deleteFile(String fname);

	/**
	 * Удалить все каталоги, подкаталоги и файлы в них
	 * @return true, если существует
	 */
	boolean deleteDirectoryWithFilesRecursive();

	boolean renameFile(String fname);

	void setFile(File file);

	boolean copyFile(String newFileName);

	void storeByteArray(byte[] buffer);

	String getMimeType();
}
