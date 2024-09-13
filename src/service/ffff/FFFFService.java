package service.ffff;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import comm.CustomThread;
import comm.Network;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;

public class FFFFService extends Service{ 

	Network net = new Network(8888, "192.168.50.210", new Network.Listener() {
		@Override
		public void onMessage(String msg) {
			
		}
	});
	
	List<JPanel> panelList = new ArrayList<JPanel>();
	
	int panelIndex = 0;
	
	public FFFFService(){ 

		// 프레임 설정
		width = 300;
		height = 300;
		divisionX = 101;
		divisionY = 106;
		isAlwasOnTop = true;
		isShowLog = false;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj panel = new CompObj();
		panel.setEnabled(true);
		panel.setName("panel");
		panel.setEvtName("panel");
		panel.setType(CompObj.TYPE_PANEL);
		panel.setLayout(CompObj.LAYOUT_BORDER);
		panel.setEventType(CompObj.EVENT_KEYTYPE, CompObj.EVENT_DRAGDROP);
		panel.setGridPosition(0, 0);
		panel.setGridWeight(1, 1);
		panel.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(panel);
		
		net.udpOn();
		
	}
	
	
	
	
	
	
	
	
	
	boolean isInit = true;
	
	public void repaintPanel(){
		panels.get(0).revalidate();
		panels.get(0).repaint();
		
		// 모든 스테이지 초기화
		for(JPanel s : panelList) ((stage) s).init();
	}
	
	public void init(){
		
		stage1 s1 = new stage1();
		stage1 s2 = new stage1();
		
		panelList.add(s1);
		panelList.add(s2);
		frame.container.setBackground(Color.BLACK);
	    frame.setUndecorated(true);
	    panels.get(0).setBackground(new Color(0,0,0,0));
	    isInit = false;
	}
	
	public void beforeDoShow(){
		if(isInit) init();
		panelIndex = 0;
		panels.get(0).removeAll();
		panels.get(0).add(panelList.get(panelIndex++));
		repaintPanel();
	}

	@Override
	public void doShow(String name) {
		beforeDoShow();
		super.doShow(name);
		afterDoShow();
	}
	
	public void afterDoShow(){
		panels.get(0).requestFocus();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	int x, y;
	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {
		
//		String threadName = "FFFFService";
//		runThread(threadName, false);
		
		if(type.equals("change") && "area".equals(obj.getEvtName())){
			addFiles(objects);
		}else if(type.equals("keyPress") && "panel".equals(obj.getEvtName())){
			shortcutEvent((KeyEvent) objects[0]);
		}

	}
	
	public void shortcutEvent (KeyEvent e){
		
		// ESC
		if(e.getKeyCode() == 27){
			this.frame.dispose();
			return;
		}
		
		// ENTER
		if(e.getKeyCode() == 10){
			// Shift
			if(!e.isShiftDown()){
				
			}else {
				
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			x = x-1;
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			x = x+1;
		}
		if(e.getKeyCode() == KeyEvent.VK_UP){
			y = y-1;
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			y = y+1;
		}
		
	}
	
	public void addFiles(Object...objects){
		File[] files = (File[]) objects;
	}
	
	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	public interface stage {
		public void init();
	}
	
	
	
	public class stage1 extends JPanel implements stage{
		private Image img;
		private Graphics img_g;
		
		int w = 20, h = 20;
		
		public stage1(){
			init();
		}
		
		@Override
		public void init(){
			x = (int) (Math.random() * width) - w;
			y = (int) (Math.random() * height) - h;
		}
		
		public void draw(Graphics g){
			g.drawRect(x, y, w, h);
		}
		
		@Override
		public void paint(Graphics g) {
			// 메모리에서 Draw
			img = createImage(width, height);
			img_g = img.getGraphics();
			paintComponents(img_g);
			draw(img_g);
			
			// 화면에 Draw
			g.drawImage(img, 0, 0, null);
			repaint(); 
		}
	}
	
	
	
	public class stage2 extends JPanel implements stage{
		private Image img;
		private Graphics img_g;
		
		public stage2(){
			init();
		}
		
		@Override
		public void init(){
			
		}
		
		public void draw(Graphics g){
			
		}
		
		@Override
		public void paint(Graphics g) {
			// 메모리에서 Draw
			img = createImage(width, height);
			img_g = img.getGraphics();
			paintComponents(img_g);
			draw(img_g);
			
			// 화면에 Draw
			g.drawImage(img, 0, 0, null);
			repaint(); 
		}
	}
}
