package service.write;

import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.JTextArea;

import comm.CustomThread;
import comm.fileio.ExcelFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;

public class TextToExcelService extends Service{
	
	public TextToExcelService() {
		
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
		button1.setEvtName("change");
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
		
		CompObj output1 = new CompObj();
		output1.setType(CompObj.TYPE_OUTPUT);
		output1.setGridPosition(0, 2);
		output1.setGridWeight(1, 1);
		output1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(output1);
		
		CompObj area1 = new CompObj();
		area1.setHint("변환 데이터 입력");
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setGridPosition(0, 3);
		area1.setGridWeight(10, 70);
		area1.setGridSize(3, 1);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);
		
	}
	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects){
		
		String threadName = "TextToExcelService";
		runThread(threadName, false);
		
	}
	
	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				int sheetIndex = 0;
				
				String fileName = inputs.get(2).getText();
				
				ExcelFileWriter write = new ExcelFileWriter("", fileName, ExcelFileWriter.EXEC_XLSX);

				JTextArea area = textAreas.get(0);
				
				String data = area.getText();
				String[] row = data.split(inputs.get(0).getText());
				
				this.setLabel(outputs.get(0));
				
				this.setTotalProc(row.length-1);
				
				for(int i=0; i<row.length; i++){
					String[] col = row[i].split(inputs.get(1).getText());
					for(int j=0; j<col.length; j++){
						write.addData(sheetIndex, i, j, col[j]);
					}
					this.setNowProc(i);
				}

				write.write(comm.Path.ROOTPATH + "convt" + File.separator + "trans" + File.separator, fileName);
				
				Main.alertPop("확인", "완료 되었습니다.");
				
//				TrayIconHandler.displayMessage("완료", "파일 포맷이 완료 되었습니다.", MessageType.INFO);
				
			}
		};
	}
}
