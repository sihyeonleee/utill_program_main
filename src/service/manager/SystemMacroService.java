package service.manager;

import java.awt.GridBagConstraints;
import java.awt.Robot;

import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;

public class SystemMacroService extends Service {

	public SystemMacroService() {

		// 프레임 설정
		width = 200;
		height = 200;
		divisionX = 100;
		divisionY = 107;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName("시작");
		btn1.setEvtName("button1");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 0);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);

		// 컴포넌트 설정
		CompObj btn2 = new CompObj();
		btn2.setName("정지");
		btn2.setEvtName("button2");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(0, 1);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);

	}

	@Override
	public void doShow(String name) {

		// Component Object Settings
		super.doShow(name);

	}

	// 클래스 레벨에 선언 (공유 상태)
	private volatile boolean running = false;
	private Thread macroThread;

	@Override
	public void onEvent(String type, CompObj obj, Object... objects) throws Exception {

		if ("click".equals(type) && "button1".equals(obj.getEvtName())) {
			if (running)
				return; // 중복 시작 방지

			running = true;
			macroThread = new Thread(() -> {
				try {
					int cnt = 0;
					Robot robot = new Robot();
					System.out.println("매크로 시작");

					while (running) {
						Thread.sleep(10000); // 10초 대기
						if (cnt < 10)
							robot.mouseWheel(5);
						else
							robot.mouseWheel(-5);
						cnt++;
					}

					System.out.println("매크로 종료");

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			macroThread.start();

		} else if ("click".equals(type) && "button2".equals(obj.getEvtName())) {
			running = false; // 종료 플래그
			System.out.println("정지 요청됨");
		}

	}

	public void record() {

	}

	public void run() {

	}

	public void runtime() {

	}

}
