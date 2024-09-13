package service._example;

import java.awt.GridBagConstraints;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;

import comm.fileio.ExcelFileReader;
import comm.fileio.FileChooser;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;

public class FileReaderService extends Service{
	public FileReaderService() {
		
		width = 600;
		height = 600;
		layout = FrameObj.LAYOUT_GRIDBAG;
		
		// 컴포넌트설정
		CompObj button1 = new CompObj();
		button1.setEvtName("button1");
		button1.setType(CompObj.TYPE_BUTTON);
		button1.setEventType(CompObj.EVENT_ACTION);
		button1.setGridPosition(0, 1);
		button1.setGridWeight(1, 1);
		componentObjs.add(button1);
		
		CompObj area1 = new CompObj();
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setCharY(30);
		area1.setCharX(50);
		area1.setGridPosition(0, 0);
		area1.setGridWeight(10, 10);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);

	}
	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects){
		
		FileChooser chooser = new FileChooser(JFileChooser.FILES_AND_DIRECTORIES);
		
		chooser.doSelect();
		
		ExcelFileReader reader = new ExcelFileReader(chooser.getSelecteFilePath()[0]);
		
		Map<String, Object> results = reader.doRead();
		
		if("INVALIDEXEC".equals(results.get("result"))){
			Main.alertPop("잘못된 파일입니다.");
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<Object, List<List<Object>>> data =  (Map<Object, List<List<Object>>>) results.get("data");
		
		for( Entry<Object, List<List<Object>>> result : data.entrySet() ){
			appendMessage( "textArea", 0, result.getValue().toString() );
		}
		
		
	}
}
