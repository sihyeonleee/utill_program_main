package service._example; 

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import comm.CustomThread;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;  

public class ScreenCaptureService extends Service{ 

	public ScreenCaptureService(){ 

		// 프레임 설정
		width = 800;
		height = 800;
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

		String threadName = "ScreenCaptureService";
		runThread(threadName, false);

	}

	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				try {
//					Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//					BufferedImage capture = new Robot().createScreenCapture(screenRect);
//					ImageIO.write(capture, "bmp", new File("test"));
					
					
					
					
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
					GraphicsDevice[] screens = ge.getScreenDevices();       
					Rectangle allScreenBounds = new Rectangle();  
					for (GraphicsDevice screen : screens) {  
					       Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();        
					       allScreenBounds.width += screenBounds.width;  
					       allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
					       allScreenBounds.x=Math.min(allScreenBounds.x, screenBounds.x);
					       allScreenBounds.y=Math.min(allScreenBounds.y, screenBounds.y);
					      } 
					Robot robot = new Robot();
					BufferedImage bufferedImage = robot.createScreenCapture(allScreenBounds);
					File file = new File(".\\scr.png");
					if(!file.exists())
					    file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					ImageIO.write( bufferedImage, "png", fos );
					
					
					
					
				} catch (IOException | AWTException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
