package service.write; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import comm.CustomThread;
import comm.fileio.ExcelFileReader;
import comm.fileio.ExcelFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class XcelFilesMergeService2 extends Service{ 

	public XcelFilesMergeService2(){ 

		// 프레임 설정
		width = 300;
		height = 300;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj input1 = new CompObj();
		input1.setHint("저장 파일 제목");
		input1.setType(CompObj.TYPE_INPUT);
		input1.setGridPosition(0, 0);
		input1.setGridWeight(1, 1);
		input1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input1);

		CompObj input2 = new CompObj();
		input2.setHint("찾을 cell 내용 (정규표현식)");
		input2.setMsg("^?+(?i)(화면ID|프로그램 ID|프로그램명|설명)+$");
		input2.setType(CompObj.TYPE_INPUT);
		input2.setGridPosition(0, 1);
		input2.setGridWeight(1, 1);
		input2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input2);
		
		CompObj input3 = new CompObj();
		input3.setHint("찾을 Column수 ( 개행 위치 지정 )");
		input3.setMsg("4");
		input3.setType(CompObj.TYPE_INPUT);
		input3.setGridPosition(0, 2);
		input3.setGridWeight(1, 1);
		input3.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(input3);

		CompObj panel = new CompObj();
		panel.setEvtName("add");
		panel.setType(CompObj.TYPE_PANEL);
		panel.setLayout(CompObj.LAYOUT_BORDER);
		panel.setEventType(CompObj.EVENT_DRAGDROP);
		panel.setGridSize(1, 1);
		panel.setGridWeight(10, 10);
		panel.setGridPosition(0, 3);
		panel.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(panel);
	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
		JLabel label = new JLabel("파일을 드래그 하세요", SwingConstants.CENTER);
		label.setOpaque(true);
		label.setBackground(Color.GRAY);
		panels.get(0).add(label, BorderLayout.CENTER);
		super.doShow(name);

	}
	
	File[] files;

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		if(type.equals("change")){
			
			String fileName = inputs.get(0).getText();
			String regex = inputs.get(1).getText();
			String colCnt = inputs.get(2).getText();
			
			if(fileName.trim().equals("") || componentObjs.get(0).getHint().equals(fileName)){
				Main.alertPop("파일명을 확인해주세요.");
				return;
			}
			if(regex.trim().equals("") || componentObjs.get(1).getHint().equals(regex)){
				Main.alertPop("정규표현식을 입력 해주세요.");
				return;
			}
			if(colCnt.trim().equals("") || componentObjs.get(2).getHint().equals(colCnt)){
				Main.alertPop("개행 위치(찾을 컬럼 수)를 지정해주세요.");
				return;
			}
			
			try{
				"".replaceAll(regex, "");
				"".matches(regex);
			}catch(Exception err){
				Main.alertPop("정규표현식의 구문을 해석 할 수 없습니다.");
				err.printStackTrace();
				return;
			}

			files = (File[]) objects;
			
			String threadName = "ExcelFilesMergeService";
			runThread(threadName, false);
			
		}
		
	}

	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				
				final CustomThread t = this;

				ExcelFileWriter write = new ExcelFileWriter();
				String fileName = inputs.get(0).getText();
				String regex = inputs.get(1).getText();
				int colCnt = Integer.parseInt(inputs.get(2).getText());
				int rowIndex = 0;
				int colIndex = 0;
				
				for(File f : files){
					ExcelFileReader reader = new ExcelFileReader(f.getPath());
					
					Map<String, Object> result = reader.doRead();
					
					if("INVALIDEXEC".equals(result.get("result"))){
						Main.alertPop("잘못된 파일입니다.");
					}
					
					@SuppressWarnings("unchecked")
					Map<Object, List<List<Object>>> data =  (Map<Object, List<List<Object>>>) result.get("data");
					
					for(Map.Entry<Object, List<List<Object>>> sheet : data.entrySet()){
						
						List<List<Object>> s = sheet.getValue();
						
						for(int i=0;i<s.size(); i++){
							
							List<Object> row = s.get(i);
							boolean findData = false;
							
							for(int j=0; j<row.size(); j++){
								String col = (String) row.get(j);
								if(findData){
									write.addData(0, rowIndex, colIndex++, col);
									if(colIndex > (colCnt-1)) {
										colIndex = 0;
										rowIndex++;
									}
									findData = false;
									// "^?+(?i)(화면ID|프로그램 ID|프로그램명|설명)+$"
								}else if(col.matches(regex)){
									findData = true;
								}
							}
							
						}
						
					}
					
				}
				
				if(!fileName.equals("")){
					write.write(comm.Path.ROOTPATH + "convt" + File.separator + "trans" + File.separator, fileName);
				}
				
			}
		};
	}
}
