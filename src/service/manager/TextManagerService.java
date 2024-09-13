package service.manager; 

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import comm.fileio.TextFileReader;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;  

public class TextManagerService extends Service{ 
	
	List<JScrollPane> list = new ArrayList<>();
	List<String> nmList = new ArrayList<>();
	
	int index = 0;

	boolean doNotRecord = false;

	public TextManagerService(){ 

		// 프레임 설정
		width = 400;
		height = 400;
		divisionX = 101;
		divisionY = 107;
		layout = FrameObj.LAYOUT_GRIDBAG;

		CompObj label1 = new CompObj();
		label1.setType(CompObj.TYPE_OUTPUT);
		label1.setEnabled(false);
		label1.setName("name");
		label1.setGridPosition(0, 0);
		label1.setGridWeight(1, 1);
		label1.setGridSize(1, 1);
		label1.setEvtName("area");
		label1.setEventType(CompObj.EVENT_DRAGDROP);
		label1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(label1);
		
		// 컴포넌트 설정
		CompObj area1 = new CompObj();
		area1.setEvtName("area");
		area1.setEnabled(true);
		area1.setType(CompObj.TYPE_PANEL);
		area1.setScrollAt(false);
		area1.setGridSize(2, 5);
		area1.setGridWeight(100, 100);
		area1.setGridPosition(0, 1);
		area1.setEventType(CompObj.EVENT_DRAGDROP, CompObj.EVENT_KEYTYPE);
		area1.setLayout(CompObj.LAYOUT_BORDER);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);
		
		CompObj label2 = new CompObj();
		label2.setType(CompObj.TYPE_BUTTON);
		label2.setEventType(CompObj.EVENT_ACTION);
		label2.setEnabled(true);
		label2.setMsg("CLEAR");
		label2.setName("clear");
		label2.setEvtName("clear");
		label2.setGridPosition(0, 7);
		label2.setGridWeight(1, 1);
		label2.setGridSize(1, 1);
		label2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(label2);
	}

	@Override
	public void doShow(String name) {
		super.doShow(name);
		outputs.get(0).setFont(new Font("함초롱돋움", Font.PLAIN, 11));
		outputs.get(0).setHorizontalAlignment(JLabel.CENTER);
		buttons.get(0).setOpaque(true);
		buttons.get(0).setForeground(new Color(150, 150, 150, 255));
		buttons.get(0).setBackground(new Color(230, 230, 230, 255));
		panels.get(0).requestFocus();
	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {
		
		if(type.equals("change") && "area".equals(obj.getEvtName())){
			File[] dragFiles = (File[]) objects; 
			
			if(dragFiles.length > 0) {
				for(File selectFile : dragFiles) {
					
					String fileFullPath = selectFile.getAbsolutePath();
					
					String filePath = fileFullPath.substring( 0, fileFullPath.lastIndexOf(File.separator) ); 
					
					String fileName = fileFullPath.substring( fileFullPath.lastIndexOf(File.separator) + 1, fileFullPath.lastIndexOf(".") );
					
					String fileExec = fileFullPath.substring( fileFullPath.lastIndexOf(".") + 1 ,fileFullPath.length() ); 
					
					TextFileReader reader = new TextFileReader(filePath, fileName, fileExec);
					
					String text = reader.reade();
					
					JTextArea area = new JTextArea();
					
					area.setEnabled(false);
					
					area.setLineWrap(true);
					
					area.setDisabledTextColor(Color.GRAY);
					
					area.setText(text);
					
					area.setFont(new Font("나눔고딕", Font.PLAIN, 11));
					
					JScrollPane pane = new JScrollPane(area);
					
					list.add(pane);
					
					nmList.add(fileName);
					
				}
				
				index = list.size()-1;
				
				setPane(index);
				
			}
		}else if(type.equals("keyPress") && "area".equals(obj.getEvtName())){
			shortcutEvent((KeyEvent) objects[0]);
		}else if(type.equals("click") && "clear".equals(obj.getEvtName())){
			
			nmList.clear();
			list.clear();

			panels.get(0).removeAll();
			panels.get(0).revalidate();
			panels.get(0).repaint();
			outputs.get(0).setText("");
			
			panels.get(0).requestFocus();
			
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
				index = ++index % list.size();
				setPane(index);
			}else {
				index--;
				if(index < 0) index = list.size()-1;
				setPane(index);
			}
		}
		
		// CTRL
		if((e.getModifiers() & 2) != 0){
			
			int numMask = 0;
			int number = 0;
			
			if(e.getKeyCode() >= 96 && e.getKeyCode() <= 105) {
				numMask = 97;
			}else if (e.getKeyCode() >= 48 && e.getKeyCode() <= 57) {
				numMask = 49;
			}
			
			number = e.getKeyCode() - numMask;
			
			if(number < list.size()){
				index = number;
				setPane(number);
			}
				
		}
		
		// ALT
		if(e.isAltDown()){
			if(e.getKeyCode() == KeyEvent.VK_D){
				
			}
		}
		
		JScrollBar bar = list.get(index).getVerticalScrollBar();
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			bar.setValue(bar.getValue() + bar.getVisibleAmount());
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			bar.setValue(bar.getValue() - bar.getVisibleAmount());
		}else if(e.getKeyCode() == KeyEvent.VK_UP){
			bar.setValue(bar.getValue() - bar.getBlockIncrement());
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
			bar.setValue(bar.getValue() + bar.getBlockIncrement());
		}
		
	}
	
	public void setPane(int index){
		panels.get(0).removeAll();
		panels.get(0).add(list.get(index));
		panels.get(0).revalidate();
		panels.get(0).repaint();
		outputs.get(0).setText(nmList.get(index));
	}

	
}
