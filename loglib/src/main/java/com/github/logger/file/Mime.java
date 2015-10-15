package com.github.logger.file;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class Mime {

	/** Получение mime type из ссылки на файл */
	public static String getMimeType(String url) {
	    String type = null;
	    String extension = MimeTypeMap.getFileExtensionFromUrl(url);

		/* to avoid empty file
		 *	extension string when url contains #
		 */
		if (TextUtils.isEmpty(extension))
			extension = getFileExtension(url);

	    if (extension != null) {
	        MimeTypeMap mime = MimeTypeMap.getSingleton();
	        type = mime.getMimeTypeFromExtension(extension);
	    }
	    
	    if(type == null)
	    	type = getMimeFromLocalMap(extension);
	    
	    return type;
	}

	private static String getFileExtension(String path){
		String ext;
		if(path!= null && path.lastIndexOf(".") != -1){
			int index=path.lastIndexOf(".");
			ext=path.substring(index+1,path.length());

		}else{
			ext="";
		}
		return ext;
	}

	@Deprecated
	private static String getMimeFromLocalMap(String extension) {
		return null;
	}

}
