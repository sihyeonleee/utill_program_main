package service.manager.task.pannel;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import comm.fileio.TextFileReader;
import gui.obj.ImageObj;
import gui.obj.calendar.CalendarViewer;

/**
 * @Date   : 2020. 1. 13.
 * @Role   : 
 * @Dcpt   : 
 */
public class TaskDtlPanel extends JPanel implements MouseListener{

	/**
	 * @Date   : 2020. 1. 8.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private final BevelBorder focusBd = new BevelBorder(BevelBorder.RAISED);
	private final BevelBorder pressBd = new BevelBorder(BevelBorder.LOWERED);
	private final EmptyBorder defaultBd = new EmptyBorder(2, 2, 2, 2);
	
	private JLabel dateViewer;
	private JTextField titleViewer;
	private JTextArea contentsViewer;
	private JPanel checksViewer;
	private JPanel filesViewer;
	
	private Map<String, Object> object;
	
	public TaskDtlPanel(){
		Font bold = new Font("함초롱", Font.BOLD, 13);
		Font small = new Font("함초롱", Font.ITALIC, 10);
		
		GridBagLayout layout = new GridBagLayout();
		
		this.setLayout(layout);
		this.setBorder(new EmptyBorder(5,5,5,5));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		titleViewer = new JTextField("");
		titleViewer.setFont(bold);
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		this.add(titleViewer, gbc);
		
		contentsViewer = new JTextArea("", 10, 0);
		JScrollPane contentSc = new JScrollPane(contentsViewer);
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		this.add(contentSc, gbc);
		
		checksViewer = new JPanel();
		JMenuItem checkItem = new JMenuItem("업무추가", new ImageIcon(ImageObj.getBusinessManIcon().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		JPopupMenu checkMenu= new JPopupMenu();
		checkMenu.add(checkItem);
		checksViewer.setComponentPopupMenu(checkMenu);
		checksViewer.setLayout(new GridLayout(0, 1));
		checksViewer.setBorder(new BevelBorder(BevelBorder.RAISED));
		checksViewer.setBackground(Color.WHITE);
		JScrollPane tasksSc = new JScrollPane(checksViewer);
		tasksSc.getVerticalScrollBar().setUnitIncrement(16);
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.weightx = 1; gbc.weighty = 15;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		this.add(tasksSc, gbc);
		
		filesViewer = new JPanel();
		JMenuItem fileItem = new JMenuItem("파일추가", new ImageIcon(ImageObj.getBusinessManIcon().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
		JPopupMenu fileMenu= new JPopupMenu();
		fileMenu.add(fileItem);
		filesViewer.setComponentPopupMenu(fileMenu);
		filesViewer.setLayout(new GridLayout(0, 1));
		filesViewer.setBorder(new BevelBorder(BevelBorder.RAISED));
		filesViewer.setBackground(Color.WHITE);
		JScrollPane filesSc = new JScrollPane(filesViewer);
		filesSc.getVerticalScrollBar().setUnitIncrement(16);
		gbc.gridx = 0; gbc.gridy = 3;
		gbc.weightx = 1; gbc.weighty = 15;
		gbc.gridwidth = 5; gbc.gridheight = 1;
		this.add(filesSc, gbc);
		
		JLabel addBtn = new JLabel("추가", JLabel.LEFT);
		addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addBtn.addMouseListener(this);
		addBtn.setBorder(defaultBd);
		addBtn.setForeground(Color.RED);
		addBtn.setFont(small);
		gbc.gridx = 0; gbc.gridy = 4;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		this.add(addBtn, gbc);
		
		JLabel updtBtn = new JLabel("수정", JLabel.LEFT);
		updtBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		updtBtn.addMouseListener(this);
		updtBtn.setBorder(defaultBd);
		updtBtn.setForeground(Color.BLUE);
		updtBtn.setFont(small);
		gbc.gridx = 1; gbc.gridy = 4;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		this.add(updtBtn, gbc);

		JLabel delBtn = new JLabel("삭제", JLabel.LEFT);
		delBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		delBtn.addMouseListener(this);
		delBtn.setBorder(defaultBd);
		delBtn.setForeground(Color.BLACK);
		delBtn.setFont(small);
		gbc.gridx = 2; gbc.gridy = 4;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		this.add(delBtn, gbc);
		
		JLabel empty = new JLabel();
		gbc.gridx = 3; gbc.gridy = 4;
		gbc.weightx = 20; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		this.add(empty, gbc);
		
		dateViewer = new JLabel(CalendarViewer.getNowDate("yyyy/MM/dd"), JLabel.RIGHT);
		dateViewer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		dateViewer.addMouseListener(this);
		dateViewer.setBorder(defaultBd);
		dateViewer.setForeground(Color.BLUE);
		dateViewer.setFont(small);
		gbc.gridx = 4; gbc.gridy = 4;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		this.add(dateViewer, gbc);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel label = ((JLabel) e.getComponent());
		label.setBorder(focusBd);
		
		String btnName = label.getText();
		
		switch(btnName){
			case "추가" :
				break;
			case "수정" :
				break;
			case "삭제" :
				break;
			default :
				break;
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		((JComponent) e.getComponent()).setBorder(focusBd);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		((JComponent) e.getComponent()).setBorder(defaultBd);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		((JComponent) e.getComponent()).setBorder(pressBd);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
	
	
	
	
	public void loadComponent(Map<String, Object> object){
//		
//		this.object = object;
//		
//		TaskObject obj = new TaskObject(this.object);
//		
//		this.titleViewer.setText(obj.getName());
//		this.contentsViewer.setText(obj.getTag());
//		
//		// 업무 안의 업무
//		for(String str : obj.getTasks()){
//			try {
//				Map<String, String> map = TextFileReader.getKeyValueTypeDataToMap(str, "/sep");
//				TaskObject o = new TaskObject(map);
//				checksViewer.add(o);
//			}catch (Exception e) {}
//		}
//		
//		// 업무 안의 파일
//		for(String str : obj.getFiles()){
//			try {
//				Map<String, String> map = TextFileReader.getKeyValueTypeDataToMap(str, "/sep");
//				for(Map.Entry<String, String> m : map.entrySet()){
////					TaskObject o = inject.getFileObj(m.getValue());
////					filesViewer.add(o);
//				}
//			}catch (Exception e) {}
//		}
//		
//		dateViewer.setText(object.get("start") + " ~ " + object.get("end"));
		
	}
	
}
