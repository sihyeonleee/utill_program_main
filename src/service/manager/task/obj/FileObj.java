package service.manager.task.obj;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;

public class FileObj extends JLabel implements MouseListener, ActionListener{
	
	/**
	 * @Date   : 2020. 1. 8.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Font font = new Font("궁서", Font.PLAIN, 12);
	private final int MAXSTRLENGTH = 22;

	public final static String OPEN_FILE = "파일열기";
	public final static String DELETE_FILE = "파일삭제";
	public final static String UPDATE_TAG = "태그수정";
	public final static String OPEN_FOLDER = "폴더열기";
	public final static String FILE_INFO = "파일정보";
	public final static String FILE_TYPE = "파일구분";
	
	private String[] rightClickOptions = {OPEN_FILE, DELETE_FILE, OPEN_FOLDER, UPDATE_TAG, FILE_INFO, FILE_TYPE};
	private JPopupMenu popupMenu;
	
	private Map<String, Object> data = new HashMap<>();
	
	public FileObj(Map<String, Object> data){
		this.data = data;
		init();
	}
	
	public void init(){
		
		this.setOpaque(false);
		
		String tagNm = (String)data.get("tagSj");
		
		this.setText(tagNm.length() > MAXSTRLENGTH ? tagNm.substring(0, MAXSTRLENGTH) : tagNm);
		
		this.setToolTipText(tagNm);
		
		this.setHorizontalAlignment(JLabel.CENTER);
		
		this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		
		this.setFont(font);
		
		this.popupMenu = new JPopupMenu();
		
		// 팝업에 파일 제목 셋팅
		JMenuItem name = new JMenuItem(tagNm);
		name.setBackground(Color.gray);
		name.setForeground(Color.white);
		name.addActionListener(this);
		this.popupMenu.add(name);
		
		// 파일 기능 팝업 추가
		for(int j=0;j<rightClickOptions.length; j++) {
			String menu = rightClickOptions[j];
			final JMenuItem item = new JMenuItem(menu);
			item.setBackground(Color.WHITE);
			item.setForeground(Color.BLACK);
			item.setFont(font);
			item.addActionListener(this);
			this.popupMenu.add(item);
		}
		
		this.setComponentPopupMenu(this.popupMenu);
		
	}
	
	
	


	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
}
