package service.write; 

import java.awt.GridBagConstraints;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import comm.CustomThread;
import comm.fileio.FileChooser;
import comm.fileio.TextFileConvert;
import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class TextFileConvertService extends Service{ 

	public TextFileConvertService(){ 

		// 프레임 설정
		width = 600;
		height = 300;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj input = new CompObj();
		input.setName("변화 파일 추가");
		input.setHint("변환할 파일 내용의 정규식 표현을 입력하세요.");
		input.setType(CompObj.TYPE_INPUT);
		input.setGridPosition(0, 0);
		input.setGridSize(2, 1);
		input.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input);
		
		CompObj btn1 = new CompObj();
		btn1.setName("변환 파일 추가");
		btn1.setEvtName("add");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 1);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);

		CompObj btn2 = new CompObj();
		btn2.setName("변환");
		btn2.setEvtName("change");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(1, 1);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);
		
		CompObj area1 = new CompObj();
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setEvtName("dragAdd");
		area1.setEventType(CompObj.EVENT_DRAGDROP);
		area1.setScrollAt(true);
		area1.setCharY(30);
		area1.setCharX(50);
		area1.setGridSize(2, 1);
		area1.setGridWeight(50, 50);
		area1.setGridPosition(0, 2);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);
		
	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
//		setMessage("input", 0, "#\\{[a-z A-Z 1-9 -_.]+\\}|%\\{[a-z A-Z 1-9 -_.]+\\}");
//		setMessage("input", 0, "#\\{[\\w]+\\}|%\\{[\\w]+\\}");
		setMessage("input", 0, "#\\{[\\w]+\\}");
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects)  {

		String threadName = "TextFileFormatService";
		
		if("add".equals(obj.getEvtName())) {
			
			String path = comm.Path.ROOTPATH + "convt" + File.separator + "trget";
			
			FileChooser chooser = new FileChooser("파일 선택", path, null, true, JFileChooser.FILES_AND_DIRECTORIES);
			
			File[] selectFiles = chooser.doSelect(true);
			
			if(selectFiles.length > 0) {
				for(File selectFile : selectFiles) {
					appendMessage("textArea", 0, selectFile.getAbsolutePath() + "\n");
				}
			}
			
		}else if("change".equals(obj.getEvtName())){
			
			runThread(threadName, false);
			
		}else if("dragAdd".equals(obj.getEvtName())){
			
			File[] dragFiles = (File[]) objects; 
			
			if(dragFiles.length > 0) {
				for(File selectFile : dragFiles) {
					appendMessage("textArea", 0, selectFile.getAbsolutePath() + "\n");
				}
			}
		}

	}

	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				String[] fileFullPaths = getMessage("textArea", 0).split("\n");
				
				Map<String, TextFileConvert> formatList = new HashMap<>();
				
				Map<String, String> trgtVarList = new HashMap<>();
				Map<String, String> cvtList = null;
				
				List<String> orgFileNameList = new ArrayList<>();
				Map<String, String> newFileNameList = null;
				
				for(String fileFullPath : fileFullPaths) {
					
					String filePath = fileFullPath.substring( 0, fileFullPath.lastIndexOf(File.separator) ); 
					String fileName = fileFullPath.substring( fileFullPath.lastIndexOf(File.separator) + 1, fileFullPath.lastIndexOf(".") );
					String fileExec = fileFullPath.substring( fileFullPath.lastIndexOf(".") + 1 ,fileFullPath.length() ); 
					
					TextFileReader reader = new TextFileReader(filePath, fileName, fileExec);
					
					String orginContents = reader.reade(); 
					
				    TextFileConvert fmt = new TextFileConvert(orginContents, getMessage("input", 0));
				    
				    Object[] result = fmt.findVarPosition().toArray();
				    
				    for(Object obj : result) {
				    	trgtVarList.put((String) obj, "");
				    }
				    
				    formatList.put(fileName, fmt);
				    
				    orgFileNameList.add(fileName);
					
				}
				
				List<String> objList = new ArrayList<>();
				
				for(Map.Entry<String, String> list : trgtVarList.entrySet()) {
					objList.add(list.getKey());
				}
				
				if(objList.size() == 0){
					Main.alertPop("EMPTY", "변환할 내용이 없습니다.\n변환 형식과 파일내용을 다시 확인해주세요.");
					return;
				}
				
				cvtList = Main.confirmPop("변환 값 입력", objList);
				
				if(cvtList.size() == 0) {
					return;
				}
				
				newFileNameList = Main.confirmPop("변환 제목 입력", orgFileNameList);
				
				if(newFileNameList.get("result").equals("confirm")){
					for(Map.Entry<String, String> fileName : newFileNameList.entrySet()) {
						
						if(fileName.getKey().equals("result")) continue;
						
						TextFileConvert format = formatList.get(fileName.getKey());
						
						String afterConvertStr = format.convert(cvtList);
						
						String strePath = comm.Path.ROOTPATH + "convt" + File.separator + "trans" + File.separator;
						String streName = fileName.getValue();
						String streExec = "";
						
						if(streName.contains(".")) {
							streName = streName.substring(0, fileName.getValue().lastIndexOf("."));
							streExec = fileName.getValue().substring(fileName.getValue().lastIndexOf(".")+1);
						}else {
							streExec = "java";
						}
						
						TextFileWriter writer = new TextFileWriter(strePath, streName, streExec, afterConvertStr);
						writer.write(false);
						
						Main.alertPop("확인", "완료 되었습니다.");
						
//						TrayIconHandler.displayMessage("완료", "파일 포맷이 완료 되었습니다.", MessageType.INFO);
						
					}
				}
			}
		};
	}
}
