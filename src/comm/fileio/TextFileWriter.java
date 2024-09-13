package comm.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

public class TextFileWriter extends FileService{
	
	
	
	public TextFileWriter(){
		
	}
	
	public TextFileWriter(String text){
		this.text = text;
	}
	
	public TextFileWriter(String path, String text){
		
		String[] sp1 = path.split(Matcher.quoteReplacement(File.separator));
		
		String realPath = "";
		String realName = "";
		String realExec = "";
		
		for(int i=0; i<sp1.length; i++){
			if(i == sp1.length - 1){
				//파일 이름
				String[] sp2 = sp1[i].split("\\.");
				realName = sp2[0];
				realExec = sp2[1];
			}else {
				//경로
				realPath += sp1[i] + File.separator;
			}
		}
		
		realPath = realPath.substring(0, realPath.length()-1);
		
		this.path = realPath;
		this.fileName = realName;
		this.fileExec = realExec;
		this.text = text;
	}
	
	public TextFileWriter( String path, String fileName, String fileExec ){
		this.path = path;
		this.fileName = fileName;
		this.fileExec = fileExec;
	}
	
	public TextFileWriter( String path, String fileName, String fileExec, String text ){
		this.path = path;
		this.fileName = fileName;
		this.fileExec = fileExec;
		this.text = text;
	}
	
	
	
	// Write
	public int write(boolean append){
		return writeProc(append);
	}
	
	public int write(String text, boolean append){
		this.text = text;
		return writeProc(append);
	}
	
	private int writeProc(boolean append){
		int result = 0;
		
		this.fullPath = this.path + File.separator + this.fileName + "." + this.fileExec;
		
		File folder = new File(this.path);
				
		if(!folder.exists()){
			if(folder.mkdirs()){
				System.out.println("폴더 생성 : " + this.path);
			}else {
				System.err.println("폴더 생성 실패 : " + this.path);
			}
		}
		
		File file = new File(this.fullPath);
		
		try {
			if(!file.exists()){
				if(file.createNewFile()){
					System.out.println("파일 생성 : " + this.fullPath);
				}else {
					System.err.println("파일 생성 실패 : " + this.fullPath);
				}
			}
		} catch (IOException err) {
			err.printStackTrace();
		}
		
		byte[] buffer = null;
		
		try {
			
			buffer = this.text.getBytes(this.encoding);
			
		} catch (UnsupportedEncodingException err) {
			err.printStackTrace();
		}
		
		
		OutputStream out = null;
		// 파일이 없으면 파일을 만들면서, 오픈시킴
		try {

			out = new FileOutputStream(this.fullPath, append);
			out.write(buffer);
			result = 1;
			
			System.out.println("파일 내용 입력 완료 : " + this.fullPath);
			
		} catch (FileNotFoundException err) {
			err.printStackTrace();
			System.err.println("저장 경로를 찾을 수 없습니다." + this.fullPath);
		} catch (IOException err) {
			err.printStackTrace();
			System.err.println("저장에 실패 하였습니다." + this.fullPath);
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println("알수 없는 에러가 발생했습니다." + this.fullPath);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException err) {
					err.printStackTrace();
					System.err.println("파일 스트림을 닫는데 실패 하였습니다." + this.fullPath);
				}
			}
		}
		
		return result;
	}
	
}
