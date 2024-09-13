package service._example;

import java.awt.GridBagConstraints;

import javax.swing.JTextArea;

import comm.fileio.ExcelFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;

public class XcelFileWriterService extends Service{
	public XcelFileWriterService() {
		
		width = 800;
		height = 600;
		layout = FrameObj.LAYOUT_GRIDBAG;
		
		// 컴포넌트설정
		CompObj input1 = new CompObj();
		input1.setHint("ROW 구분자 입력 : 2개이상 구분자는 |( or ) 로 구분");
		input1.setType(CompObj.TYPE_INPUT);
		input1.setGridPosition(0, 0);
		input1.setGridWeight(1, 1);
		input1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input1);
		
		CompObj input2 = new CompObj();
		input2.setHint("COLUMN 구분자 입력 : 2개이상 구분자는 |( or ) 로 구분");
		input2.setType(CompObj.TYPE_INPUT);
		input2.setGridPosition(1, 0);
		input2.setGridWeight(1, 1);
		input2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input2);
		
		CompObj button1 = new CompObj();
		button1.setName("변환");
		button1.setEvtName("convert");
		button1.setType(CompObj.TYPE_BUTTON);
		button1.setEventType(CompObj.EVENT_ACTION);
		button1.setGridPosition(2, 0);
		button1.setGridWeight(1, 1);
		button1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(button1);
		
		CompObj input3 = new CompObj();
		input3.setHint("저장 파일명 입력 ( 확장자 미포함 )");
		input3.setType(CompObj.TYPE_INPUT);
		input3.setGridPosition(0, 1);
		input3.setGridWeight(1, 1);
		input3.setGridSize(3, 1);
		input3.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input3);
		
		CompObj area1 = new CompObj();
		area1.setHint("변환 데이터 입력");
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setGridPosition(0, 2);
		area1.setGridWeight(10, 70);
		area1.setGridSize(3, 1);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);

	}
	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects){
		
		int sheetIndex = 0;
		
		ExcelFileWriter write = new ExcelFileWriter();

		JTextArea area = textAreas.get(0);
		
		String data = area.getText();
		String[] row = data.split(inputs.get(0).getText());
		
		for(int i=0; i<row.length; i++){
			String[] col = row[i].split(inputs.get(1).getText());
			for(int j=0; j<col.length; j++){
				write.addData(sheetIndex, i, j, col[j]);
			}
		}

		write.write(inputs.get(2).getText());
		
	}
}
