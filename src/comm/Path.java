package comm;

import java.io.File;
import java.nio.file.Paths;

import javax.swing.filechooser.FileSystemView;

public class Path {
	
	public static String ROOTPATH = File.separator;
	
	public static String COREPATH = "." + File.separator + "Core.exe";
	
	public static String UTILPATH = "." + File.separator + "LSH_Utill.exe";
	
	public static String DESKTOPPATH = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
	
	public static String WORKPATH = Paths.get("").toAbsolutePath().toString();
	
	public static String PATH_FILE_HOTKEY = "hotkey";
	
}
