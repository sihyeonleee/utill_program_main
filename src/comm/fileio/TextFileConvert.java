package comm.fileio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileConvert {
	
	private String originalCntnts;	// 변경전 내용
	private String convertCntnts;	// 변경후 내용
	private String trgetPattr;	// 찾을 패턴
	
	// 변경될 위치 저장
	private Map<String, Integer> trgetValue = new HashMap<>();
	
	// 변경할 값 리스트
	private Map<String, String> convertValue;
	
	
	public TextFileConvert() {
		
	}
	
	public TextFileConvert(String originalCntnts, String trgetPattr) {
		this.originalCntnts = originalCntnts;
		this.trgetPattr = trgetPattr;
	}
	
	public String convert() {
		return convertProc();
	}
	
	public String convert(Map<String, String> converts) {
		this.convertValue = converts;
		return convertProc();
	}
	
	public String convertProc() {
		
		if(trgetValue == null) {
			findVarPosition();
		}
		
		if(convertValue != null) {
			
			convertCntnts = originalCntnts;
			
			for(Map.Entry<String, String> convert : convertValue.entrySet()) {
				
				if(!convert.getValue().trim().equals("")) {
					convertCntnts = convertCntnts.replace(convert.getKey(), convert.getValue());
				}
			}
			
		}
		
		return convertCntnts;
		
	}
	
	public List<String> findVarPosition() {
		
		Pattern pattern = Pattern.compile(trgetPattr);
		Matcher matcher = pattern.matcher(originalCntnts);
		
		while(matcher.find()) {
			
			// 변수명
			String varName = matcher.group();
			
			if(this.trgetValue.get(varName) == null) {
				this.trgetValue.put(varName, 0);
			}
			
			this.trgetValue.put(varName, this.trgetValue.get(varName) + 1);
			
		}
		
		return getTrgetVarNameList();
		
	}
	
	public List<String> getTrgetVarNameList() {
		List<String> result = new ArrayList<>();
		
		for(Map.Entry<String, Integer> map : this.trgetValue.entrySet()) {
			result.add(map.getKey());
		}
		
		return result;
	}

}
