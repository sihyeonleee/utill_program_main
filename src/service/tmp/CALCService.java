package service.tmp; 

import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import comm.CustomThread;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class CALCService extends Service{ 

	public CALCService(){ 

		// 프레임 설정
		width = 800;
		height = 800;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName("+");
		btn1.setEvtName("button1");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 0);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);

		CompObj btn2 = new CompObj();
		btn2.setName("버튼2");
		btn2.setEvtName("button2");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(1, 0);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);

		CompObj area1 = new CompObj();
		area1.setEnabled(true);
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setGridSize(2, 1);
		area1.setGridWeight(30, 30);
		area1.setGridPosition(0, 2);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);
		
		CompObj area2 = new CompObj();
		area2.setEnabled(true);
		area2.setType(CompObj.TYPE_TEXTAREA);
		area2.setScrollAt(true);
		area2.setGridSize(2, 1);
		area2.setGridWeight(30, 30);
		area2.setGridPosition(0, 3);
		area2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area2);

	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		String threadName = "CALCService";
		/*runThread(threadName, false);*/
		
		if(obj.getEvtName().equals("button1")){
			add(textAreas.get(0).getText());
		}else if (obj.getEvtName().equals("button2")){
			eval(textAreas.get(0).getText());
		}

	}
	
	public void add(String text){
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		text.trim();
		
		String history = "";
		
		String[] nums = text.split(" |\t|\n|,");
		
		long result = 0;
		
		for(String s : nums){
			
			s.trim();
			
			if("".equals(s)) continue;
			
			try{
				result += Integer.parseInt(s);
				history += " + " + s;
			}catch(Exception e){
				Main.alertPop("잘못된 형식입니다. : " + s);
				return;
			}
			
		}
		
		textAreas.get(1).append("\n결과 ::: " + result + " ::: (" + history + ")");
		
		StringSelection contents = new StringSelection(result+"");
		
		// 클립보드에 복사
		clipboard.setContents(contents, null);
		
		// 클립보드에서 붙여넣기
		/*Transferable contents = clipboard.getContents(clipboard);

		if(contents != null){
		     try {
		          String pasteString = (String)(contents.getTransferData( DataFlavor.stringFlavor ));
		     } catch (Exception e) {}
		}*/
		
	}
	
	public void eval(String test){
		
	}

	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				
			}
		};
	}
}
