package comm.fileio;

import java.util.Map;

public class FileService {

	public static String PATH_DIRECTORY_PROP = "prop";
	
	public static String PATH_FILE_DATABASE = "database";
	public static String PATH_FILE_COLTOVARIABLE = "coltovariable";
	
	public static String EXEC_FILE_LSH = "lsh";
	
	String path;
	String fileName;
	String fileExec;
	String fullPath;
	String text;
	String encoding = "utf-8";
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileExec() {
		return fileExec;
	}

	public void setFileExec(String fileExec) {
		this.fileExec = fileExec;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
