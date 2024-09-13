package service.config; 

import java.awt.GridBagConstraints; 

import gui.obj.CompObj; 
import gui.obj.FrameObj; 
import service.Service; 
import comm.CustomThread;  

public class PathService extends Service{ 

	public PathService(){ 

		// 프레임 설정
		width = 800;
		height = 800;
		divisionX = 103;
		divisionY = 107;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName("버튼1");
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
		area1.setEnabled(false);
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setGridSize(2, 1);
		area1.setGridWeight(30, 30);
		area1.setGridPosition(0, 2);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);

	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		String threadName = "PathService";
		runThread(threadName, false);

		if(type.equals("click") && "button1".equals(obj.getEvtName())){

		}else if(type.equals("click") && "button2".equals(obj.getEvtName())){

		}

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
