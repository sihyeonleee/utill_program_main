package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import comm.Network;
import comm.RunnableConfig;
import comm.TextAreaOutputStream;
import gui.main.MainFrame;
import gui.main.TrayIconHandler;

public class Main {
	
	
	/**
	 * #### Runnable Config ####
	 * -DServer.mode=DEV
	 * -Dfile.admin=TRUE
	 * -DServer.network=FALSE
	 * -Dfile.encoding=MS949
	 * 
	 * Command Line Run : java -DServer.mode=DEV -jar .\\utillService_1_1_0.jar
	 * 
	 */
	
	public static JScrollPane out;
	public static JScrollPane err;
	
	public static String encoding = null;
    public static String mode = null;
    public static Boolean isNetwork = null;
    public static Boolean isAdmin = null;
    
	public static void main(String []args){
	
		boolean isPropException = false;
		
		try {
			encoding= System.getProperty("file.encoding");
			mode = System.getProperty("Server.mode");
			isNetwork = ( (String) System.getProperty("Server.network") ).equals("TRUE");
			isAdmin = ( (String) System.getProperty("Server.admin") ).equals("TRUE");
		}catch(Exception err) {
			encoding = "UTF-8";
		    mode = "PROD";
		    isNetwork = false;
		    isAdmin = false;
			isPropException = true;
		}
		
		JTextArea outArea = new JTextArea();
		JTextArea errArea = new JTextArea();
		errArea.setForeground(Color.RED);
		
		TextAreaOutputStream taos1 = new TextAreaOutputStream( outArea );
        PrintStream outStream = new PrintStream( taos1 );
        
        TextAreaOutputStream taos2 = new TextAreaOutputStream( errArea );
        PrintStream errStream = new PrintStream( taos2 );
        
        out = new JScrollPane(outArea);
        err = new JScrollPane(errArea);
        
        out.getVerticalScrollBar().setUnitIncrement(16);
        err.getVerticalScrollBar().setUnitIncrement(16);
        
        if(mode == null || mode.equals("PROD")) {
        	// Log 
        	System.setOut( outStream );
        	System.setErr( errStream );
        }
        
        if(isPropException) System.out.println("Runable Config Not Set");
        System.out.println("Runable Config is ::: [enc : " + encoding + "] [mode : " + mode + "] [network : " + isNetwork + "] [admin : " + isAdmin + "]");
        
		MainFrame mainFrame = null;
		String[] names = null;
		String[] events = null;
		
		
		if(mode != null && mode.equals("DEV")){
			List<ArrayList<String>> list = RunnableConfig.getDevServiceList();
			names = (String[]) list.get(0).toArray(new String[list.get(0).size()]);
			events = list.get(1).toArray(new String[list.get(1).size()]);
			
		}else {
			List<ArrayList<String>> list = RunnableConfig.getOprServiceList();
			names = (String[]) list.get(0).toArray(new String[list.get(0).size()]);
			events = list.get(1).toArray(new String[list.get(1).size()]);
		}
		
		if(!isAdmin && isNetwork) net.clientOn(10000);
		
		mainFrame = new MainFrame(names, events);
		
		try{
			//LookAndFeel Windows 스타일 적용
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		}catch(Exception err){
			TrayIconHandler.displayMessage("ERROR", "Not Working Look And Feel", MessageType.ERROR);
		}
		
		mainFrame.showFrame();
		

		
	}
	
	
	
	
	
	/*
	 * 관리자 이벤트 ( 외부 IP사용자에 의해 현재 미사용 )
	 * */
	public static String[] adminCmds = {"재기동"};
	
	public static Network net = new Network(1008, "192.168.50.210", new Network.Listener(){
		
		@Override
		public void onMessage(String msg) {
			// 재기동
			if(msg.equals(adminCmds[0])){
				reboot();
			}
		}
		
	});
	
	public static void restart(){
		String cmd = "powershell start \'" + comm.Path.COREPATH + "\'";
		try {
			Runtime.getRuntime().exec(cmd);
			System.exit(0);
		}catch(Exception e){
			
		}
	}
	
	public static void reboot(){
		String cmd = "powershell start \'" + comm.Path.UTILPATH + "\'";
		try {
			Runtime.getRuntime().exec(cmd);
			System.exit(0);
		}catch(Exception e){
			
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 어플리케이션 전역 팝업
	 * */
	public static void warningPop(String msg){
	    Toolkit.getDefaultToolkit().beep();
	    JOptionPane optionPane = new JOptionPane( msg, JOptionPane.WARNING_MESSAGE );
	    JDialog dialog = optionPane.createDialog( "Warning!" );
	    dialog.setAlwaysOnTop(true);
	    dialog.setVisible(true);
	}
	
	public static void alertPop(Object... msg){
		JOptionPane.showMessageDialog(null, msg);
	} 
	
	public static void alertPop(String title, Object msg){
		alertPop(null, title, msg);
	}
	
	public static void alertPop(Component c, Object msg){
		alertPop(c, "", msg);
	}
	
	public static void alertPop(Component c, String title, Object msg){
		
		if(msg instanceof JScrollPane){
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) ((JScrollPane) msg).getPreferredSize().getWidth();
		    int height = (int) ((JScrollPane) msg).getPreferredSize().getHeight();
		    if(height > screenSize.getHeight()) {
		    	height = (int) (screenSize.getHeight() / 100) * 80;
		    }
		    if(width > screenSize.getWidth()) {
		    	width = (int) (screenSize.getWidth() / 100) * 80;
		    }
		    ((JScrollPane) msg).setPreferredSize(new Dimension(width, height));
		    ((JScrollPane) msg).getVerticalScrollBar().setUnitIncrement(16);
		}
		
		JOptionPane.showMessageDialog(c, msg, title, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static int confirmPop(Object msg){
		// JOptionPane.YES_OPTION, JOptionPane.NO_OPTION, JOptionPane.CANCEL_OPTION
		int result = JOptionPane.showConfirmDialog(null, msg);
		
		switch(result){
			case JOptionPane.YES_OPTION :
				result = 1;
				break;
			case JOptionPane.NO_OPTION :
			case JOptionPane.CANCEL_OPTION :
			case JOptionPane.CLOSED_OPTION  :
				result = -1;
				break;
		}
		
		return result;
	}

	public static int confirmPop(String title, Object msg){
		
		int result = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		/*String[] options = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);*/
		
		switch(result){
			case JOptionPane.YES_OPTION :
				result = 1;
				break;
			case JOptionPane.NO_OPTION :
			case JOptionPane.CANCEL_OPTION :
			case JOptionPane.CLOSED_OPTION  :
				result = -1;
				break;
		}
		
		return result;
	}
	
	public static Map<String, String> confirmPop(String title, List<String> inputList){
		
		Object[] inputs = inputList.toArray();
		
		return confirmPop(title, inputs, 0, 0);
		
	}
	
	public static Map<String, String> confirmPop(String title, Object[] inputs){
		
		return confirmPop(title, inputs, 0, 0);
		
	}
	
	public static Map<String, String> confirmPop(String title, List<String> inputList, int aWidth, int aHeight){
		
		Object[] inputs = inputList.toArray();
		
		return confirmPop(title, inputs, aWidth, aHeight);
		
	}
	
	public static Map<String, String> confirmPop(String title, Object[] inputs, int aWidth, int aHeight){
		
		Map<String, String> resultData = new HashMap<>();
		
		Map<String, JTextField> storeObj = new HashMap<>();
		
		Map<String, JTextArea> aStoreObj = new HashMap<>();
		
		GridBagLayout gbl = new GridBagLayout();
		JPanel pane = new JPanel(gbl);
		
		for(int i=0; i<inputs.length; i++){
			
			String name = (String) inputs[i];
			
			JLabel label = new JLabel(name);
			
			JTextField input = null;
			JTextArea aInput = null;
			
			if(aWidth!=0){
				aInput = new JTextArea(aHeight, aWidth);
				aStoreObj.put(name, aInput);
			}else {
				input = new JTextField(15);
				storeObj.put(name, input);
			}
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = i;
			gbc.ipadx = 10;
			gbc.fill = GridBagConstraints.BOTH;
			
			gbc.gridx = 0;
			pane.add(label, gbc);
			
			gbc.weightx = 10;
			gbc.gridwidth = 2;
			gbc.gridx = 1;
			
			if(aWidth!=0){
				JScrollPane scroll = new JScrollPane(aInput);
				scroll.getVerticalScrollBar().setUnitIncrement(16);
				scroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				pane.add(scroll, gbc);
			}else {
				pane.add(input, gbc);
			}
			
		}
		
		JScrollPane scroll = new JScrollPane(pane);
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		int result = JOptionPane.showConfirmDialog(null, scroll, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		switch(result){
		case JOptionPane.YES_OPTION :
			
			for(Object key : inputs){
				
				String text = "";
				
				if(aWidth != 0){
					text = aStoreObj.get(key).getText();
				}else {
					text = storeObj.get(key).getText();
				}
				
				if(!text.trim().equals("")){
					resultData.put( (String) key, text);
				}
				
			}
			
			resultData.put("result", "confirm");
			
			break;
			
		case JOptionPane.NO_OPTION :
		case JOptionPane.CANCEL_OPTION :
		case JOptionPane.CLOSED_OPTION  :
			resultData.put("result", "cancel");
			break;
		}
		
		return resultData;
		
	}
	
	public static Map<String, String> confirmPop(String title, String[] inputs, Map<String, String> datas){
		return confirmPop(title, inputs, datas, 0, 0);
	}
	
	public static Map<String, String> confirmPop(Component c, String title, String[] inputs, Map<String, String> datas){
		return confirmPop(c, title, inputs, datas, 0, 0);
	}
	
	public static Map<String, String> confirmPop(String title, List<String> inputs, Map<String, String> datas){
		return confirmPop(title, inputs.toArray(), datas, 0, 0);
	}

	public static Map<String, String> confirmPop(Component c, String title, List<String> inputs, Map<String, String> datas){
		return confirmPop(c, title, inputs.toArray(), datas, 0, 0);
	}
	
	public static Map<String, String> confirmPop(String title, List<Object> inputs, Map<String, String> datas, int aWidth, int aHeight){
		return confirmPop(title, inputs.toArray(), datas, aWidth, aHeight);
	}
	
	public static Map<String, String> confirmPop(String title, Object[] inputs, Map<String, String> datas, int aWidth, int aHeight){
		return confirmPop(null, title, inputs, datas, aWidth, aHeight);
	}
	
	public static Map<String, String> confirmPop(Component c, String title, Object[] inputs, Map<String, String> datas, int aWidth, int aHeight){
		
		Map<String, String> resultData = new HashMap<>();
		Map<String, JTextField> storeObj = new HashMap<>();
		Map<String, JTextArea> aStoreObj = new HashMap<>();
		
		GridBagLayout gbl = new GridBagLayout();
		JPanel pane = new JPanel(gbl);
		
		for(int i=0; i<inputs.length; i++){
			
			String name = (String) inputs[i];
			
			JLabel label = new JLabel(name);
			
			JTextField input = null;
			JTextArea aInput = null;
			
			if(aWidth!=0){
				aInput = new JTextArea(datas.get(name), aHeight, aWidth);
				aStoreObj.put(name, aInput);
			}else {
				input = new JTextField(datas.get(name), 15);
				storeObj.put(name, input);
			}
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = i;
			gbc.ipadx = 10;
			gbc.fill = GridBagConstraints.BOTH;
			
			gbc.gridx = 0;
			pane.add(label, gbc);
			
			gbc.weightx = 10;
			gbc.gridwidth = 2;
			gbc.gridx = 1;

			if(aWidth!=0){
				JScrollPane scroll = new JScrollPane(aInput);
				scroll.getVerticalScrollBar().setUnitIncrement(16);
				scroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				pane.add(scroll, gbc);
			}else {
				pane.add(input, gbc);
			}
			
		}
		
		JScrollPane scroll = new JScrollPane(pane);
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);

		scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		int result = JOptionPane.showConfirmDialog(c, scroll, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		switch(result){
		case JOptionPane.YES_OPTION :
			
			resultData.put("result", "confirm");
			
			for(Object key : inputs){
				
				String text = "";
				
				if(aWidth != 0){
					text = aStoreObj.get(key).getText();
				}else {
					text = storeObj.get(key).getText();
				}
				
				if(!text.trim().equals("")){
					resultData.put( (String) key, text);
				}
			}
			break;
		case JOptionPane.NO_OPTION :
		case JOptionPane.CANCEL_OPTION :
		case JOptionPane.CLOSED_OPTION  :
			resultData.put("result", "cancel");
			break;
		}
		return resultData;
	}
	
	public static String inputPop(String msg){
		return JOptionPane.showInputDialog(null, msg);
	}
	
	public static String inputPop(String title, String msg){
		return JOptionPane.showInputDialog(null, msg, title, JOptionPane.PLAIN_MESSAGE);
	}
	
	public static Object selectPop(String msg, String select, String...selections){
		
		try{
		
			return  JOptionPane.showInputDialog(null, msg, "선택창", JOptionPane.PLAIN_MESSAGE, null, selections, select);
		
		}catch(ArrayIndexOutOfBoundsException err){
			err.printStackTrace();
			System.err.println("옵션을 확인해주세요 : " + selections.length);
			
			warningPop("옵션을 확인해주세요 : " + selections.length);
			return null;
		}
		
	}
	
	public static Object selectPop(String title, String msg, String select, String...selections){
		
		try{
			
			return JOptionPane.showInputDialog(null, msg, "선택창", JOptionPane.PLAIN_MESSAGE, null, selections, select);
		
		}catch(ArrayIndexOutOfBoundsException err){
			err.printStackTrace();
			System.err.println("옵션을 확인해주세요 : " + selections.length);
			
			warningPop("옵션을 확인해주세요 : " + selections.length);
			return null;
		}
		
	}
	
}



