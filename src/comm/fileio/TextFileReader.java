package comm.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.universalchardet.UniversalDetector;

public class TextFileReader extends FileService{
	
	
	
	public TextFileReader(){
		
	}
	
	public TextFileReader( String fileName, String fileExec ){
		this.fileName = fileName;
		this.fileExec = fileExec;
	}
	
	public TextFileReader( String path, String fileName, String fileExec ){
		this.path = path;
		this.fileName = fileName;
		this.fileExec = fileExec;
	}
	
	
	
	
	// Do Read
	public String reade(){
		return readeProc(false);
	}
	
	public String reade(boolean makeDir, String base){
		String readStr = readeProc(true);
		if(readStr.trim().equals("")) {
			TextFileWriter write = new TextFileWriter(this.path, this.fileName, this.fileExec, base);
			write.write(false);
			readStr = base;
		}
		return readeProc(true);
	}
	
	public String reade(boolean makeDir){
		return readeProc(makeDir);
	}
	
	public String reade(String fileName, String fileExec){
		this.fileName = fileName;
		this.fileExec = fileExec;
		return readeProc(false);
	}
	
	public String reade(String fileName, String fileExec, boolean makeDir){
		this.fileName = fileName;
		this.fileExec = fileExec;
		return readeProc(makeDir);
	}
	
	
	private String readeProc(boolean makeDir){
		
		this.fullPath = this.path + File.separator + this.fileName + "." + this.fileExec; 
		
		// 읽은 내용이 담겨질 스트림 (byte[])
		byte[] buffer = null;
		
		// 파일 읽기 객체
		InputStream in = null;
		
		try {

			if( makeDir ){
				File folder = new File(this.path + File.separator);
				if(!folder.exists()){
					folder.mkdirs();
				}
				
				File file = new File(this.fullPath);
				if(!file.exists()){
					file.createNewFile();
				}
			}
			
			in = new FileInputStream(this.fullPath);
			buffer = new byte[in.available()];
			in.read(buffer);

			System.out.println("파일 내용 읽기 완료 : " + this.fullPath);
			
		} catch (FileNotFoundException err) {
			err.printStackTrace();
			System.err.println("저장 경로를 찾을 수 없습니다. : " + this.fullPath);
		} catch (IOException err) {
			err.printStackTrace();
			System.err.println("파일 읽기에 실패 하였습니다. : " + this.fullPath);
		} catch (Exception err) {
			err.printStackTrace();
			System.err.println("알수 없는 에러가 발생했습니다. : " + this.fullPath);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException err) {
					err.printStackTrace();
					System.err.println("파일 스트림을 닫는데 실패 하였습니다. : " + this.fullPath);
				}
			}
		}
		
		// data 배열에 내용이 있다면, 문자열로 변환하여 출력
		if(buffer != null) {
			// 문자열로 변환시에는 저장된 인코딩으로 지정해 준다.
			try {
				
				this.text = new String(buffer, detectCharset(buffer));
				
			} catch (UnsupportedEncodingException err) {
				err.printStackTrace();
				System.err.println("ENCODING 지정 에러 : " + this.fullPath);
			}
		}
		
		return this.text;
		
	}

	
	private String detectCharset(byte[] bytes) {
		
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String detectedCharset = detector.getDetectedCharset();
		
		if (detectedCharset != null /* && detector.isDone() && Charset.isSupported(detectedCharset) */) {
           return detectedCharset;
		}else {
			return "MS949";
		}
	}
	
	// Data To Key Values
	public Map<String, String> getKeyValueTypeDataToMap(String data){
		
		Map<String, String> result = new HashMap<>();
		
		String[] sp1 = data.split("\n");
		
		for(String info : sp1){
			
			String[] value = info.split("=");
			
			if(value.length > 1){
				result.put(value[0].trim(), value[1].trim());
			}
			
		}
		
		return result;
		
	}
	
	public static Map<String, String> getKeyValueTypeDataToMap(String data, String wrap){
		
		Map<String, String> result = new HashMap<>();
		
		String[] sp1 = data.split(wrap);
		
		for(String info : sp1){
			String k = info.substring(0, info.indexOf("="));
			String v = info.substring(info.indexOf("=")+1);
			result.put(k.trim(), v.trim());
		}
		
		return result;
		
	}
	
	public static Map<String, Map<String, String>> getKeyValueTypeDataToMap(String data, String seperatorHead, String key){
		
		Map<String, Map<String, String>> result = new HashMap<>();
		
		String[] sp1 = data.split(seperatorHead);
		
		for(int i=0; i<sp1.length - 1; i++){
			
			Map<String, String> keyValueMap = new HashMap<String, String>();
			
			String[] sp2 = sp1[ i + 1].split("\n");
			
			for(String info : sp2){
				
				String[] value = info.split("=");
				
				if(value.length > 1){
					keyValueMap.put(value[0], value[1].trim());
				}
			}
			
			if(key == null || key.trim().equals("")){
				result.put(i+"", keyValueMap);
			}else {
				if(keyValueMap.containsKey(key)){
					result.put(keyValueMap.get(key), keyValueMap);
				}else {
					result.put(i+"", keyValueMap);
				}
			}
		}
		
		return result;
		
	}
	
	public static Map<String, Map<String, String>> getKeyValueTypeDataToMap(String data, String seperatorHead, String key, String wrap){
		
		Map<String, Map<String, String>> result = new HashMap<>();
		
		String[] sp1 = data.split(seperatorHead);
		
		for(int i=0; i<sp1.length - 1; i++){
			
			Map<String, String> keyValueMap = new HashMap<String, String>();
			
			String[] sp2 = sp1[ i + 1].split(wrap);
			
			for(String info : sp2){
				try{
					String k = info.substring(0, info.indexOf("="));
					String v = info.substring(info.indexOf("=")+1);
					keyValueMap.put(k.trim(), v.trim());
				}catch(Exception e){}
				
			}
			
			if(key == null || key.trim().equals("")){
				result.put(i+"", keyValueMap);
			}else {
				if(keyValueMap.containsKey(key)){
					result.put(keyValueMap.get(key), keyValueMap);
				}else {
					result.put(i+"", keyValueMap);
				}
			}
		}
		
		return result;
		
	}
	
	
	
}
