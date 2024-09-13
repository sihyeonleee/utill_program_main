package service.config; 

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;

import comm.CustomThread;
import comm.Path;
import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import gui.main.JnaHook;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class HotKeyService extends Service{ 

	public List<Map<String, String>> keyList  = null;



	String base = ""
			+ "[HOTKEY]\ntitle=1.TILT\nkey=`\nvalue=192\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=2.INS\nkey=ins\nvalue=45\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=3.DEL\nkey=del\nvalue=46\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=4.HOME\nkey=home\nvalue=36\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=5.END\nkey=end\nvalue=35\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=6.PAGEUP\nkey=pageup\nvalue=33\ntrayIcon=\ninstallPath=\n\n"
			+ "[HOTKEY]\ntitle=7.PAGEDOWN\nkey=pagedown\nvalue=34\ntrayIcon=\ninstallPath=\n\n";
	
	public HotKeyService(){ 

		// 프레임 설정
		width = 500;
		height = 300;
		divisionX = 102;
		divisionY = 94;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

	
		CompObj pane = new CompObj();
		pane.setEvtName("panel");
		pane.setType(CompObj.TYPE_PANEL);
		pane.setEventType(CompObj.EVENT_KEYTYPE);
		pane.setLayout(CompObj.LAYOUT_GRID, 0, 4, 2, 2);
		pane.setScrollAt(true);
		pane.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(pane);

	}

	@Override
	public void doShow(String name) {

		read();

		load();
		
		// Component Object Settings 
		super.doShow(name);
		
		panels.get(0).requestFocusInWindow();

	}
	public void read() {
		TextFileReader reader = new TextFileReader(Path.ROOTPATH+"prop", Path.PATH_FILE_HOTKEY, "lsh");
		Map<String, Map<String, String>> readeResult = reader.getKeyValueTypeDataToMap(reader.reade(true, base), "\\[HOTKEY\\]", "title");
		keyList = new ArrayList<>(readeResult.values());
		keyList.sort(Comparator.comparing(k -> k.get("title")));
	}
	
	public void load() {
		panels.get(0).removeAll();
		for(Map<String, String> k : keyList) {
			JButton btn = new JButton(k.get("title"));
			btn.setPreferredSize(new Dimension(30, 50));
			btn.addActionListener(listener(k));
			panels.get(0).add(btn);
		}
		
		panels.get(0).revalidate();
		panels.get(0).repaint();
		
	}
	
	public ActionListener listener(Map<String, String> k) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
	
				Map<String, String> result = Main.confirmPop( "단축키설정", new String[] {"trayIcon", "installPath"}, k);
				
				if("confirm".equals(result.get("result"))){
					
					String trayIcon = result.get("trayIcon");
					String installPath = result.get("installPath");
					trayIcon = trayIcon == null ? "" : trayIcon.trim();
					installPath = installPath == null ? "" : installPath.trim();
					if(!k.containsKey("trayIcon") || !k.containsKey("installPath") || !k.get("trayIcon").equals(trayIcon) || !k.get("installPath").equals(installPath)) {
						k.put("trayIcon", trayIcon);
						k.put("installPath", installPath);
						write();
					}
					
				}
				

				panels.get(0).requestFocusInWindow();
			}
		};
	}
	
	
	public void write() {
		
		StringBuilder text = new StringBuilder();
		
		for(Map<String, String> wk : keyList) {
			String trayIcon = wk.get("trayIcon");
			String installPath = wk.get("installPath");
			text.append("[HOTKEY]\n");
			text.append("title="); text.append(wk.get("title")); text.append("\n");
			text.append("key="); text.append(wk.get("key")); text.append("\n");
			text.append("value="); text.append(wk.get("value")); text.append("\n");
			text.append("trayIcon="); text.append(trayIcon != null && !"".equals(trayIcon) ? trayIcon : ""); text.append("\n");
			text.append("installPath="); text.append(installPath != null && !"".equals(installPath) ? installPath : ""); text.append("\n");
			text.append("\n");
		}
		TextFileWriter write = new TextFileWriter(Path.ROOTPATH+"prop", Path.PATH_FILE_HOTKEY, "lsh", text.toString());
		write.write(false);

		JnaHook.hotKeyList = keyList;
		
	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

//		String threadName = "HotKeyService";
//		runThread(threadName, false);

		if(type.equals("keyPress") && "panel".equals(obj.getEvtName())){
			KeyEvent e = (KeyEvent) objects[0];
			if(e.getKeyCode() == 27){
				this.frame.dispose();
				return;
			}
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
