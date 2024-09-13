package service.manager; 

import java.awt.GridBagConstraints; 

import gui.obj.CompObj; 
import gui.obj.FrameObj; 
import service.Service; 
import comm.CustomThread;  

public class SystemMacroService extends Service{ 

	public SystemMacroService(){ 

		// 프레임 설정
		width = 800;
		height = 800;
		divisionX = 103;
		divisionY = 107;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName("녹화");
		btn1.setEvtName("record");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 0);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);

		CompObj btn2 = new CompObj();
		btn2.setName("실행");
		btn2.setEvtName("run");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(1, 0);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);
		
		CompObj btn3 = new CompObj();
		btn2.setName("실행2");
		btn2.setEvtName("runtime");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(2, 0);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);

	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		if(type.equals("click") && "record".equals(obj.getEvtName())){
			
		}else if(type.equals("click") && "run".equals(obj.getEvtName())){
			
		}else if(type.equals("click") && "runtime".equals(obj.getEvtName())){
			
		}

	}
	
	public void record(){
		
	}
	
	public void run(){
		
	}
	
	public void runtime(){
		
	}
	
}
