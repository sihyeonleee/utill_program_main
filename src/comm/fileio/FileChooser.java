package comm.fileio;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import gui.main.TrayIconHandler;

public class FileChooser extends FileService{
	
	private String title = "파일 탐색기";
	private boolean allFile = true;
	private Map<String, String[]> filters;
	private int mode;
	
	private String[] selecteFileFullPath;
	private String[] selecteFilePath;
	private String[] selecteFileExec;
	private String[] selecteFileName;
	
	private File[] files;
	
	
	
	
	public FileChooser(){
		this.path = comm.Path.DESKTOPPATH;
	}
	
	public FileChooser(int mode){
		this.path = comm.Path.ROOTPATH;
		this.mode = mode;
	}
	
	public FileChooser( String title, String path, Map<String, String[]> filters, boolean allFile, int mode){
		this.path = path;
	    this.title = title;
	    this.allFile = allFile;
	    this.filters = filters;
	    this.mode = mode;
	}
	
	
	
	// Do Select
	public File[] doSelect( boolean mkdir ) {
		if( mkdir ){
			File folder = new File(this.path + File.separator);
			if(!folder.exists()){
				folder.mkdirs();
			}
		}
		
		return doSelectProc();
	}
	
	public File[] doSelect( ){
		return doSelectProc();
	}
	
	public File[] doSelectProc() {

		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory()); // 디렉토리 설정
		chooser.setCurrentDirectory(new File(path)); // 현재 사용 디렉토리를 지정
		chooser.setAcceptAllFileFilterUsed(allFile);   // Fileter 모든 파일 적용 
		chooser.setDialogTitle(title); // 창의 제목
		chooser.setMultiSelectionEnabled(true); // 멀티파일 선택
		// JFileChooser.FILES_AND_DIRECTORIES
		chooser.setFileSelectionMode(mode); // 파일 선택 모드
		
		if(filters != null){
			for( Map.Entry<String, String[]> filter : filters.entrySet() ){
				FileNameExtensionFilter f = new FileNameExtensionFilter(filter.getKey(), filter.getValue()); // filter 확장자 추가
				chooser.setFileFilter(f); // 파일 필터를 추가
			}
		}
		
		int returnVal = chooser.showOpenDialog(null); // 열기용 창 오픈
		
		if(returnVal == JFileChooser.APPROVE_OPTION) { // 열기를 클릭 
			
			files = chooser.getSelectedFiles();
			
			selecteFileFullPath = new String[files.length];
			selecteFilePath = new String[files.length];
			selecteFileName = new String[files.length];
			selecteFileExec = new String[files.length];
							
			for(int i=0; i<files.length; i++) {
				
				File file = files[i];
				
				selecteFileFullPath[i] = file.getPath();
				selecteFilePath[i] = file.getPath().substring( 0, file.getPath().lastIndexOf(File.separator) );
				try{
					selecteFileName[i] = file.getName().substring( 0, file.getName().lastIndexOf(".") );
					selecteFileExec[i] = selecteFileName[i].substring( selecteFileName[i].lastIndexOf(".") + 1 ,selecteFileName[i].length() );
				}catch(Exception e){ }
			}
		    
		}else if(returnVal == JFileChooser.CANCEL_OPTION){ // 취소를 클릭
			files = null;
		}
		
		return files;
	}
	
	
	
	// Function
	public static int deleteFile(String fullPath){
		
		int result = 1;
		
		try{
			File folder = new File(fullPath);
			if(folder.exists()){
				if(folder.isDirectory()){
					File[] list = folder.listFiles();
					for(File file : list){
						result *= deleteFile(file.getAbsolutePath());
					}
				}
				if(!folder.delete()){
					result = -1;
				}
			}
		}catch(Exception err){
			err.printStackTrace();
			return -1;
		}
		
		return result;
		
	}
	
	public static Map<String, Object> shortcutFile(File file, String lnkPath, boolean overrideFile){
		
		Map<String, Object> resultMap = new HashMap<>();
		
		String fileExec = "lnk";
		String fileName = file.getName();
//    	fileName = fileName.substring(0, fileName.lastIndexOf("."));
    	
    	resultMap.put("fileName", fileName);
    	resultMap.put("fileExec", fileExec);
    	
    	fileName = File.separator + fileName + "." + fileExec;
    	
		try {
			
			File folder = new File(lnkPath);
			
			if(!folder.exists()){
				try{
					folder.mkdirs();
				}catch(Exception err){
					err.printStackTrace();
				}
			}
			File stredFile = new File(lnkPath + File.separator + file.getName() + "." + fileExec);
			
			if(stredFile.exists() && !overrideFile){
				// 이미 카피한 동일 이름을 가진 파일이 있는경우 ( 확장자 상관없이 이름으로 비교한다. )
				resultMap.put("result", "aleady");
				resultMap.put("file", new File(lnkPath + fileName));
			}else {
				String cmd = "";
		    	
				cmd += "powershell \"";
		        	cmd += "$s=(New-Object -COM WScript.Shell).";
		        	cmd += "CreateShortcut(\'" + lnkPath + fileName + "\');";
		        	cmd += "$s.TargetPath=\'" + file.getAbsolutePath() + "\';";
		        	cmd += "$s.Save()";
		    	cmd += "\"";
		    	
		    	System.out.println(cmd);
		    	
		    	try {
		    		Process proc = Runtime.getRuntime().exec(cmd);
					printStream(proc);
				} catch (InterruptedException err) {
					err.printStackTrace();
				}

		    	File lnkFile = new File(lnkPath + fileName);
		    	
		    	if(lnkFile.exists()){
		    		resultMap.put("result", "success");
		    		resultMap.put("file", lnkFile);
		    	}else {
		    		resultMap.put("result", "fail");
		    		resultMap.put("file", null);
		    	}
		    	
			}
		
		} catch (IOException err) {
			err.printStackTrace();
			
    		resultMap.put("result", "fail");
    		resultMap.put("file", null);
		}
		
		return resultMap;
		
	}
	
	public static void exec(String fullPath, boolean adminstrator){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				
				String cmd = "powershell Start-Process powershell -verb runAs ";
				
				if(adminstrator){
					cmd += fullPath;
				}else {
					cmd = "powershell start \'" + fullPath + "\'";
				}
				
				try {
					Process proc = Runtime.getRuntime().exec(cmd);
					printStream(proc);
				} catch (IOException | InterruptedException err) {
					err.printStackTrace();
				}
				
			}
		});
		
		t.start();
		
	}
	
	public static boolean nameChange(String fullPathOrginName, String fullPathChangeName){
		
		String cmd = "";
		
		cmd = "powershell \"mv \'" + fullPathOrginName + "\' \'" + fullPathChangeName + "\' \"";
		
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			printStream(proc);
			return true;
		} catch (IOException | InterruptedException err) {
			err.printStackTrace();
			return false;
		}
		
	}
	
	public static Map<String, String> openOrginFolder(String fullPath){
		
		Map<String, String> result = null;
		
		fullPath = fullPath.replace(File.separator, File.separator+File.separator);
		String cmd = "wmic path win32_shortcutfile where \"name=\'" + fullPath + "\'\" get target /value";
		
		try{
			
			Process proc = Runtime.getRuntime().exec(cmd);
			
			result = printStream(proc);
			
			String log = result.get("log");
			
			log = log.substring(log.indexOf("=")+1);
			log = log.substring(0, log.lastIndexOf(File.separator));
			Process openProc = Runtime.getRuntime().exec("powershell start \'" + log + "\'");
			result.put("err", printStream(openProc).get("err"));
		} catch (IOException | InterruptedException err) {
			err.printStackTrace();
		}
		
		return result;
	}
	
	public static void openFolder(String path){
		try {
			Process proc = Runtime.getRuntime().exec("powershell start " + path);
			printStream(proc);
		} catch (IOException | InterruptedException err) {
			err.printStackTrace();
		}		
	}
	
	public static int mkdirs(String path){
		
		File file = new File(path);
		
		if(!file.exists()){
			if(file.mkdirs()){
				return 1;
			}else {
				return -1;
			}
		}else {
			return -2;
		}
		
	}
	
	private static Map<String, String> printStream(Process process) throws IOException, InterruptedException {
		
		Map<String, String> result = new HashMap<>();
		
		process.waitFor();
		
		
		try (InputStream psout = process.getInputStream()) {
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(psout));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        
	        String excuteConsoleLog = out.toString();
	        result.put("log", excuteConsoleLog);
	        System.out.println(excuteConsoleLog);
	        reader.close();
			
		}
		
		try (InputStream psout = process.getErrorStream()) {
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(psout));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
	        
	        String errorConsoleLog = out.toString();
	        result.put("err", errorConsoleLog);
	        reader.close();
		}
		
		return result;
		
	}
	
	public static File[] getFileList(String path) {
		File file = new File(path);
		return file.listFiles();
	}
	
	 //파일복사
	public static int copyFile(File source,File dest){
		
		int result = 0;
		
		long startTime = System.currentTimeMillis();
		
		int count = 0;
		long totalSize = 0;
		byte[] b = new byte[128];
		
		FileInputStream in = null;
		FileOutputStream out = null; 
		//성능향상을 위한 버퍼 스트림 사용
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try {
			in = new FileInputStream(source);
			bin = new BufferedInputStream(in);
			
			out = new FileOutputStream(dest);
			bout = new BufferedOutputStream(out);
			while((count = bin.read(b))!= -1){
				bout.write(b,0,count);
				totalSize += count;
			}
			
			result = 1;
		} catch (Exception err) {
			// TODO: handle exception
			result = -1;
			TrayIconHandler.displayMessage("ERROR", source.getName() + " ::: FAIL " + err.getMessage() , MessageType.ERROR);
			err.printStackTrace();
		} finally{// 스트림 close 필수
			try {
			 if(bout!=null){
				 bout.close();
			 }    
			 if (out != null){
				 out.close();
			 }
			 if(bin!=null){
				 bin.close();
			 }
			 if (in != null){
				 in.close();
			 }
			
			} catch (IOException r) {
			 // TODO: handle exception
				System.out.println("close 도중 에러 발생.");
			}
		}
		//복사 시간 체크
		StringBuffer time = new StringBuffer("소요시간 : ");
		time.append(System.currentTimeMillis() - startTime);
		time.append(",FileSize : " + totalSize);
		System.out.println(time);
		return result;
	}
	 
	 //디렉토리 생성 -> 파일복사
	private static void copyDirectory(File source,File dest){
		try{
			long startTime = System.currentTimeMillis();
			
			if(!source.exists()){
				throw new IllegalArgumentException("디렉토리 없음");
			}
			
			if(!dest.exists()) dest.mkdirs();
			
			File[] fileList = source.listFiles();//내부의 파일리스트 가져오기
			
			for(int i=0;i<fileList.length;i++){
				File sourceFile = fileList[i];
			 
				File destFile = new File(dest, sourceFile.getName());
				copyFile(sourceFile, destFile);//copyFile메소드 실행
			}
			
			//복사 시간 체크
			StringBuffer time = new StringBuffer("소요시간 : ");
			time.append(System.currentTimeMillis() - startTime);
			time.append(",File Total List : " +  fileList.length);
			System.out.println(time);
		}catch(Exception err){
			TrayIconHandler.displayMessage("ERROR", source.getName() + " ::: FAIL " + err.getMessage() , MessageType.ERROR);
			err.printStackTrace();
		}
		
	}

	
	
	// Getter, Setter
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isAllFile() {
		return allFile;
	}

	public void setAllFile(boolean allFile) {
		this.allFile = allFile;
	}

	public Map<String, String[]> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String[]> filters) {
		this.filters = filters;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getSelecteFilePath() {
		return selecteFilePath;
	}

	public void setSelecteFilePath(String[] selecteFilePath) {
		this.selecteFilePath = selecteFilePath;
	}

	public String[] getSelecteFileExec() {
		return selecteFileExec;
	}

	public void setSelecteFileExec(String[] selecteFileExec) {
		this.selecteFileExec = selecteFileExec;
	}

	public String[] getSelecteFileName() {
		return selecteFileName;
	}

	public void setSelecteFileName(String[] selecteFileName) {
		this.selecteFileName = selecteFileName;
	}

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}
	
	
	
	
}
