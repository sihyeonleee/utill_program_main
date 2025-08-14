//package service.manager;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.Graphics;
//import java.awt.GridBagConstraints;
//import java.awt.Rectangle;
//import java.io.File;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
//import org.sikuli.script.Match;
//import org.sikuli.script.Screen;
//
//import comm.CustomThread;
//import comm.ImageSearch;
//import comm.fileio.FileChooser;
//import gui.obj.CompObj;
//import gui.obj.FrameObj;
//import service.Service;
//
//public class ImgMacroService extends Service {
//
//	public ImgMacroService() {
//
//		// 프레임 설정
//		width = 200;
//		height = 200;
//		divisionX = 100;
//		divisionY = 107;
//		isAlwasOnTop = true;
//		layout = FrameObj.LAYOUT_GRIDBAG;
//
//		// 컴포넌트 설정
//		CompObj btn1 = new CompObj();
//		btn1.setName("버튼1");
//		btn1.setEvtName("button1");
//		btn1.setType(CompObj.TYPE_BUTTON);
//		btn1.setEventType(CompObj.EVENT_ACTION);
//		btn1.setGridPosition(0, 0);
//		btn1.setGridWeight(1, 1);
//		btn1.setArrangeType(GridBagConstraints.BOTH);
//		componentObjs.add(btn1);
//		
//		// 컴포넌트 설정
//		CompObj btn2 = new CompObj();
//		btn2.setName("버튼2");
//		btn2.setEvtName("button2");
//		btn2.setType(CompObj.TYPE_BUTTON);
//		btn2.setEventType(CompObj.EVENT_ACTION);
//		btn2.setGridPosition(0, 1);
//		btn2.setGridWeight(1, 1);
//		btn2.setArrangeType(GridBagConstraints.BOTH);
//		componentObjs.add(btn2);
//		
//		// 컴포넌트 설정
//		CompObj btn3 = new CompObj();
//		btn3.setName("버튼3");
//		btn3.setEvtName("button3");
//		btn3.setType(CompObj.TYPE_BUTTON);
//		btn3.setEventType(CompObj.EVENT_ACTION);
//		btn3.setGridPosition(0, 2);
//		btn3.setGridWeight(1, 1);
//		btn3.setArrangeType(GridBagConstraints.BOTH);
//		componentObjs.add(btn3);
//
//	}
//
//	@Override
//	public void doShow(String name) {
//
//		// Component Object Settings
//		super.doShow(name);
//
//	}
//	
//	File file = null;
//
//	@Override
//	public void onEvent(String type, CompObj obj, Object... objects) throws Exception {
//
//		String threadName = "ImgMacroService";
//		runThread(threadName, false);
//
//		ImageSearch search = new ImageSearch();
//
//		if (type.equals("click") && "button1".equals(obj.getEvtName())) {
//			FileChooser ch = new FileChooser();
//			File[] files = ch.doSelect();
//			
//			if (files != null && files.length > 0) {
//				file = files[0];
//			}
//		}else if (type.equals("click") && "button2".equals(obj.getEvtName())) {
//			
//			CompletableFuture
//		    .supplyAsync(() -> search.findMatchesSelectBlocking(file, 0.6f))
//		    .thenApplyAsync(matches1 -> {
//		    	
//		    	List<Rectangle> regions = search.groupMatches(matches1);
//		        
//		        JFrame frame = new JFrame("Match Visualizer");
//		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		        frame.setUndecorated(true); // 창 테두리 제거
//		        frame.setAlwaysOnTop(true);
//		        frame.setBackground(new Color(0, 0, 0, 128)); // 반투명 검정 배경
//		        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // 전체 화면
//
//		        JPanel panel = new JPanel() {
//		            protected void paintComponent(Graphics g) {
//		                super.paintComponent(g);
//		                g.setColor(Color.RED);
//		                g.setFont(new Font("Arial", Font.BOLD, 14));
//
//		                for(Rectangle r : regions) {
//		                	g.drawRect(r.x, r.y, r.width, r.height);
//		                    g.drawString("test", r.x + 3, r.y + 15);
//		                }
//		                
////		                for (Match m : matches1) {
////		                    Rectangle rect = m.getRect();
////		                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
////		                    g.drawString("test", rect.x + 3, rect.y + 15);
////		                }
//		            }
//		        };
//
//		        panel.setOpaque(false);
//		        frame.add(panel);
//		        frame.setVisible(true);
//		        return null;
//		    });
//			
//			
//			
////		    .thenApplyAsync(none -> {
////		        List<Match> matches2 = search.findMatchesSelectBlocking(file, 0.6f);
////		        matches1.forEach(m -> {
////		            try {
////		                new Screen().mouseMove(m);
////		                Thread.sleep(300);
////		            } catch (Exception ignored) {}
////		        });
////		        matches2.forEach(m -> {
////		            try {
////		                new Screen().click(m);
////		                Thread.sleep(300);
////		            } catch (Exception ignored) {}
////		        });
////		        return null;
////		    })
////		    .thenRunAsync(() -> {
////		        List<Match> matches3 = search.findMatchesSelectBlocking(file, 0.6f);
////		        matches3.forEach(m -> {
////		            try {
////		                m.highlight(1.0);
////		                Thread.sleep(300);
////		            } catch (Exception ignored) {}
////		        });
////		    });
//			
//			
//			
//		}else if (type.equals("click") && "button3".equals(obj.getEvtName())) {
//			search.tesseract(file);
//		}
//
//	}
//
//	@Override
//	public CustomThread createThread() {
//		return new CustomThread() {
//			@Override
//			public void run() {
//
//			}
//		};
//	}
//}
