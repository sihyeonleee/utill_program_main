package gui.obj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.swing.JButton;

import comm.fileio.TextFileWriter;
import gui.main.TrayIconHandler;

public class ServiceObj{
	
	public static List<JButton> serviceList = new ArrayList<>();
	
	public ServiceObj(String[] names, String[] events){
		for(int i=0; i<names.length; i++){
			if( names[i].charAt(0) == '#' ){
				multiple(names[i], events[i]);
			}else {
				single(names[i], events[i]);
			}
		}
	}

	public static void single(String nm, String evt){
		
		final String name = nm;
		final String event = evt;
		String e = "";
		
		if(nm.equals("---------------------")){
			TrayIconHandler.addSeparator();
			return;
		}
	
		if(event.contains(".")){
			e = event + "Service";
		}else {
			String className = event.substring(0, 1).toUpperCase() + event.substring(1);
			e = event + "." + className + "Service";
		}
		
		try {
			final Class<?> service = Class.forName("service." + e);
			final Object obj = service.newInstance();
			System.out.println( name + " ### " + service.getName());
			
			try {
				Method init = service.getMethod("init", String.class);
				if(init != null){
					System.out.println( name + " ### " + service.getName() + "." + init.getName());
					init.invoke(obj, name);
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
			
			JButton btn = new JButton(name);
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Method doShow = service.getMethod("doShow", String.class);
						if(doShow != null){
							System.out.println( name + " ### " + service.getName() + "." + doShow.getName());
							doShow.invoke(obj, name);
						}
					} catch (Exception err) {
						err.printStackTrace();
					}
				}
			};
			
			btn.setFocusPainted(false);
			
			if(!event.contains("Exit")){
				TrayIconHandler.addItem(name, listener);
			}
			
			btn.addActionListener(listener);
			serviceList.add(btn);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException err) {
			// 클래스를 찾을 수 없다.
			err.printStackTrace();
			// 기본 타입으로 .java 파일 만들기
			setDefaultServieWrite(e);
		}
	}
	
	public static void multiple(String nm, String evt){
		
		final String event = evt;
		final String name = nm;
		
		if(nm.equals("---------------------")){
			TrayIconHandler.addSeparator();
			return;
		}
			
		JButton btn = new JButton(name);
		
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFrame(event, name);
			}
		};
		
		btn.setFocusPainted(false);
		
		TrayIconHandler.addItem(name, listener);
		btn.addActionListener(listener);
		
		serviceList.add(btn);
	}
	
	public static Map<String, Object> createFrame(String event, String name){
		
		Map<String, Object> map = new HashMap<>();
		
		try {
			
			Class<?> service = null;
			
			if(event.contains(".")){
				service = Class.forName("service." + event + "Service");
			}else {
				String className = event.substring(0, 1).toUpperCase() + event.substring(1);
				service = Class.forName("service." + event + "." + className + "Service");
			}
			
			Object obj = service.newInstance();
			
			map.put("service", service);
			map.put("instance", obj);
			
			System.out.println(name + " ### " + service.getName());
			
			try {
				Method init = service.getMethod("init", String.class);
				if(init != null){
					System.out.println( name + " ### " + service.getName() + "." + init.getName());
					init.invoke(obj, name);
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
			
			try {
				Method doShow = service.getMethod("doShow", String.class);
				if(doShow != null){
					System.out.println( name + " ### " + service.getName() + "." + doShow.getName());
					doShow.invoke(obj, name);
				}
			} catch (Exception err) {
				err.printStackTrace();
			}
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException err) {
			// 클래스를 찾을 수 없다.
			err.printStackTrace();
		}
		
		return map;
		
	}
		
	public static void setDefaultServieWrite(String path){
		
		String[] sp = path.split("\\.");
		String prefix = "service.";
		String packg = sp[0];
		String clsNm = sp[1];
		String suffix = ".java";
		
		String text = "";
		
		text += "package " + prefix + packg + "; \n\n";
		text += "import java.awt.GridBagConstraints; \n\n";
		
		text += "import gui.obj.CompObj; \n";
		text += "import gui.obj.FrameObj; \n";
		text += "import service.Service; \n";
		text += "import comm.CustomThread;  \n\n";
		
		text += "public class " + clsNm + " extends Service{ \n\n";
		
		text += "\tpublic " + clsNm + "(){ \n\n";
		
		text += "\t\t// 프레임 설정\n";
		text += "\t\twidth = 800;\n";
		text += "\t\theight = 800;\n";
		text += "\t\tdivisionX = 103;\n";
		text += "\t\tdivisionY = 107;\n";
		text += "\t\tisAlwasOnTop = true;\n";
		text += "\t\tlayout = FrameObj.LAYOUT_GRIDBAG;\n\n";
		
		text += "\t\t// 컴포넌트 설정\n";
		text += "\t\tCompObj btn1 = new CompObj();\n";
		text += "\t\tbtn1.setName(\"버튼1\");\n";
		text += "\t\tbtn1.setEvtName(\"button1\");\n";
		text += "\t\tbtn1.setType(CompObj.TYPE_BUTTON);\n";
		text += "\t\tbtn1.setEventType(CompObj.EVENT_ACTION);\n";
		text += "\t\tbtn1.setGridPosition(0, 0);\n";
		text += "\t\tbtn1.setGridWeight(1, 1);\n";
		text += "\t\tbtn1.setArrangeType(GridBagConstraints.BOTH);\n";
		text += "\t\tcomponentObjs.add(btn1);\n\n";
		
		text += "\t\tCompObj btn2 = new CompObj();\n";
		text += "\t\tbtn2.setName(\"버튼2\");\n";
		text += "\t\tbtn2.setEvtName(\"button2\");\n";
		text += "\t\tbtn2.setType(CompObj.TYPE_BUTTON);\n";
		text += "\t\tbtn2.setEventType(CompObj.EVENT_ACTION);\n";
		text += "\t\tbtn2.setGridPosition(1, 0);\n";
		text += "\t\tbtn2.setGridWeight(1, 1);\n";
		text += "\t\tbtn2.setArrangeType(GridBagConstraints.BOTH);\n";
		text += "\t\tcomponentObjs.add(btn2);\n\n";
		
		text += "\t\tCompObj area1 = new CompObj();\n";
		text += "\t\tarea1.setEnabled(false);\n";
		text += "\t\tarea1.setType(CompObj.TYPE_TEXTAREA);\n";
		text += "\t\tarea1.setScrollAt(true);\n";
		text += "\t\tarea1.setGridSize(2, 1);\n";
		text += "\t\tarea1.setGridWeight(30, 30);\n";
		text += "\t\tarea1.setGridPosition(0, 2);\n";
		text += "\t\tarea1.setArrangeType(GridBagConstraints.BOTH);\n";
		text += "\t\tcomponentObjs.add(area1);\n\n";
		
		text += "\t}\n\n";
		
		text += "\t@Override\n";
		text += "\tpublic void doShow(String name) {\n\n";
		
		text += "\t\t// Component Object Settings \n";
		text += "\t\tsuper.doShow(name);\n\n";
		
		text += "\t}\n\n";
		
		text += "\t@Override\n";
		text += "\tpublic void onEvent(String type, CompObj obj, Object...objects) throws Exception {\n\n";
		
		text += "\t\tString threadName = \"" + clsNm + "\";\n";
		text += "\t\trunThread(threadName, false);\n\n";
		
		text += "\t\tif(type.equals(\"click\") && \"button1\".equals(obj.getEvtName())){\n\n";
		text += "\t\t}else if(type.equals(\"click\") && \"button2\".equals(obj.getEvtName())){\n\n";
		text += "\t\t}\n\n";
		
		text += "\t}\n\n";
		
		text += "\t@Override\n";
		text += "\tpublic CustomThread createThread(){\n";
		text += "\t\treturn new CustomThread(){\n";
		text += "\t\t\t@Override\n";
		text += "\t\t\tpublic void run() {\n\n";
		
		text += "\t\t\t}\n";
		text += "\t\t};\n";
		text += "\t}\n";
		text += "}\n";
		
		// 경로 설정 후 입력 (쓰기) 실행
		String fullPath = "src" + File.separator + prefix + path;
		fullPath = fullPath.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
		fullPath += suffix;
		
		TextFileWriter write = new TextFileWriter(fullPath, text);
		write.write(false);
	}
	
	
	public List<JButton> getServiceList(){
		return serviceList;
	}
	
}
