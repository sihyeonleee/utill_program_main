package gui.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import gui.obj.ImageObj;
import gui.obj.ServiceObj;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	String[] names = {};
	String[] events = {};
	
	static Image icon = ImageObj.getMainIcon();
	
	public MainFrame() {
		
	}
	
	public MainFrame(String[] names, String[] events) {
		this.names = names;
		this.events = events;
		
		TextFileReader reader = new TextFileReader("tokens", "path", "lsh");
		String text = reader.reade(true);
		Map<String, String> result = reader.getKeyValueTypeDataToMap(text);
		try{
			File file = new File(result.get("path"));
			if(file.exists()){
				comm.Path.ROOTPATH = result.get("path");
			}else {
				// Not Found ROOT Path Error 
				comm.Path.ROOTPATH = ".\\";
			}
		}catch(Exception err){
			String txt = "path=.\\";
			TextFileWriter writer = new TextFileWriter("tokens", "path", "lsh", txt);
			writer.write(false);
			comm.Path.ROOTPATH = ".\\";
		}
		
	}

	public void showFrame() {
		
		this.setTitle("Utill Program");
		this.setSize(400, 200);

		setTrayIconInit(this);
		
		ServiceObj obj = new ServiceObj(names, events);
		List<JButton> btnList = obj.getServiceList();
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,3));
		
		for(int i=0;i<btnList.size();i++){
			JButton btn = btnList.get(i);
			btn.setText(i+1 + ") " + btn.getText());
			panel.add(btn, i);
		}
		
		this.add(panel);
		
		Dimension frameSize = this.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setResizable(false);
		this.setLocation((screenSize.width - frameSize.width), (screenSize.height - frameSize.height) - 60);
		this.setIconImage(icon);
		this.setVisible(true);
				
		final JFrame frame = this;
		frame.requestFocusInWindow();
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) { }
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 27) frame.dispose();
			}
		});
		
		setTrayIconExit(this);
		setGlobalShortKey(btnList);
	}
	
	public void setGlobalShortKey(final List<JButton> btnList) {
		new JnaHook(this, btnList);
	}
	
	public static void setTrayIconInit(JFrame f){
		final JFrame frame = f;
		
		TrayIconHandler.registerTrayIcon(
			icon, "Utills", new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true);
				}
			}
		);
		
	}
	
	public static void setTrayIconExit(JFrame f){
		
		final JFrame frame = f;
		
		TrayIconHandler.addSeparator();
		
		TrayIconHandler.addItem("OPEN", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(true);
			}
		});
		
		TrayIconHandler.addItem("EXIT", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
	}
	
}
