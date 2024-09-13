package service.manager.file;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import gui.obj.ImageObj;

@SuppressWarnings("serial")
public class FileObject extends JLabel implements MouseListener, ActionListener{
	
	public static int FILE_TYPE_TEXT = 1;
	public static int FILE_TYPE_IMAGE = 2;
	public static int FILE_TYPE_VIDEO = 3;
	public static int FILE_TYPE_PDF = 4;
	public static int FILE_TYPE_PPT = 5;
	public static int FILE_TYPE_EXCEL = 6;
	public static int FILE_TYPE_FOLDER = 7;
	public static int FILE_TYPE_FILE = 8;
	public static int FILE_TYPE_DOCX = 9;
	public static int FILE_TYPE_HTML = 10;
	public static int FILE_TYPE_EML = 11;
	
	public final static String OPEN_FILE = "파일열기";
	public final static String DELETE_FILE = "파일삭제";
	public final static String UPDATE_TAG = "태그수정";
	public final static String OPEN_FOLDER = "폴더열기";
	public final static String FILE_INFO = "파일정보";
	public final static String FILE_TYPE = "파일구분";
	
	private String projectName;
	private String fileName;
	private String fileExec;
	private String filePath;
	private String fileTag;
	private String insertDt;
	private String updateDt;
	private String taskColor;
	private boolean folder = true;

	private int fileType = FILE_TYPE_TEXT;
	private String[] fileOptions = {OPEN_FILE, DELETE_FILE, OPEN_FOLDER, UPDATE_TAG, FILE_INFO, FILE_TYPE};
	
	private JPopupMenu popupMenu;
	private FileObjEventListener evtListener;
	private Image img;
	
	private int width = 35;
	private int height = 30;
	
	private Color highColor = getBorderColor("WHITE");
	private Color shadColor = Color.GRAY;
	
	private int borderWidth = 2;
	
	private LineBorder defaultBorder = new LineBorder(highColor, borderWidth);
	
	private Font font = new Font("바탕", Font.PLAIN, 11);
	
	public FileObject(){
		
	}
	
	public FileObject(String fileName, String projectName, String filePath, FileObjEventListener evtListener){
		
		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.fileName = fileName;
		this.projectName = projectName;
		this.filePath = filePath;
		this.evtListener = evtListener;
		
		this.fileExec = this.fileName.substring(0, this.fileName.lastIndexOf("."));
		if( this.fileExec.contains(".") ){
			this.fileExec = this.fileExec.substring(this.fileExec.lastIndexOf(".")+1);
			
			if( this.fileExec.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣0-9]+.*") ){
				
			}else {
				this.folder = false;
			}
			
		}
		
		switch(this.fileExec.toLowerCase()){
			case "txt":
				fileType = FILE_TYPE_TEXT;
				img = ImageObj.getTxtIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "png":
			case "jpg":
			case "jpeg":
				fileType = FILE_TYPE_IMAGE;
				img = ImageObj.getImgIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "mp4":
			case "avi":
			case "webm":
				fileType = FILE_TYPE_VIDEO;
				img = ImageObj.getVidoIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "html":
				fileType = FILE_TYPE_HTML;
				img = ImageObj.getHtmlIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "docx":
				fileType = FILE_TYPE_DOCX;
				img = ImageObj.getDocxIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "xls":
			case "xlsx":
				fileType = FILE_TYPE_EXCEL;
				img = ImageObj.getExcelIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "pdf":
				fileType = FILE_TYPE_PDF;
				img = ImageObj.getPdfIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "ppt":
			case "pptx":
				fileType = FILE_TYPE_PPT;
				img = ImageObj.getPptIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			case "eml":
				fileType = FILE_TYPE_EML;
				img = ImageObj.getMailIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
				break;
			default :
				if(this.folder) {
					fileType = FILE_TYPE_FOLDER;
					img = ImageObj.getFolderIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					break;
				}else {
					fileType = FILE_TYPE_FILE;
					img = ImageObj.getFileIcon().getScaledInstance(width, height, Image.SCALE_SMOOTH);
					break;
				}

		}
		
		init();
	}
	
	public void init(){
		
		ImageIcon icon = new ImageIcon(img);
		
		this.setFont(font);
		this.setIcon(icon);
		// this.setToolTipText(fileName);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(true);
		this.setText(fileName.substring(0, 6) + "");
		this.setBorder(defaultBorder);
		this.setSize(this.getWidth() + 10, this.getHeight() + 10);
		this.setIconTextGap(3);
		this.setHorizontalTextPosition(SwingConstants.CENTER);
		this.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.setVerticalAlignment(JLabel.CENTER);
		this.setHorizontalAlignment(JLabel.CENTER);
		
//		this.setVerticalTextPosition(JLabel.BOTTOM);
//		this.setHorizontalTextPosition(JLabel.CENTER);
		
		this.popupMenu = new JPopupMenu();
		
		// 팝업에 파일 제목 셋팅
		JMenuItem name = new JMenuItem(fileName);
		name.setBackground(Color.gray);
		name.setForeground(Color.white);
		name.addActionListener(this);
		this.popupMenu.add(name);
		
		// 파일 기능 팝업 추가
		for(int j=0;j<fileOptions.length; j++) {
			String menu = fileOptions[j];
			final JMenuItem item = new JMenuItem(menu);
			item.setBackground(Color.WHITE);
			item.setForeground(Color.BLACK);
			item.addActionListener(this);
			item.setFont(font);
			this.popupMenu.add(item);
		}
		
		this.setComponentPopupMenu(this.popupMenu);
		this.addMouseListener(this);
		
	}

	
	
	
	

	
	
	private int mouseClickStatus = 0;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			duoClick();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		focus();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		unFocus();
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(mouseClickStatus == 0){
			this.setBorder(new BevelBorder(BevelBorder.LOWERED, highColor, shadColor));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if(e.getX() < 0 || e.getX() > this.getWidth() || e.getY() < 0 || e.getY() > this.getHeight()){
			return;
		}
		select();
		
	}
	
	// 각메뉴 항목 클릭시
	@Override
	public void actionPerformed(ActionEvent e) {
		unFocus();
		evtListener.onMenuEvt(this, e.getActionCommand());
	}
	

	// 외부 클릭 메소드
	public void duoClick(){
		evtListener.onDouClickEvt(this);
		unSelect();
		return;
	}
	
	public void select(){
		if(mouseClickStatus == 0){
			this.setBorder(new BevelBorder(BevelBorder.LOWERED, highColor, shadColor));
			this.setBackground(Color.GRAY);
			mouseClickStatus = 1;
			evtListener.onReleaseEvt(this);
		}else {
			this.setBorder(new BevelBorder(BevelBorder.RAISED, highColor, shadColor));
			this.setBackground(null);
			mouseClickStatus = 0;
		}
	}
	
	public void unSelect(){
		this.setBorder(defaultBorder);
		this.setBackground(null);
		mouseClickStatus = 0;
	}
	
	public void focus(){
		if(mouseClickStatus == 0){
			this.setBorder(new BevelBorder(BevelBorder.RAISED, highColor, shadColor));
		}else {
			if(this.popupMenu.isVisible()){
				
			}else {
				this.setBorder(defaultBorder);
			}
		}
		evtListener.onFocusEvt(this);
	}
	
	public void unFocus(){
		if(mouseClickStatus == 0){
			if(this.popupMenu.isVisible()){
				
			}else {
				this.setBorder(defaultBorder);
			}
		}else {
			this.setBorder(new BevelBorder(BevelBorder.LOWERED, highColor, shadColor));
		}
	}
	
	
	
	
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileTag() {
		return fileTag;
	}

	public void setFileTag(String fileTag) {
		this.fileTag = fileTag;
	}

	public String getInsertDt() {
		return insertDt;
	}

	public void setInsertDt(String insertDt) {
		this.insertDt = insertDt;
	}

	public String getUpdateDt() {
		return updateDt;
	}

	public void setUpdateDt(String updateDt) {
		this.updateDt = updateDt;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public String[] getFileOptions() {
		return fileOptions;
	}

	public void setFileOptions(String[] fileOptions) {
		this.fileOptions = fileOptions;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	public FileObjEventListener getEvtListener() {
		return evtListener;
	}

	public void setEvtListener(FileObjEventListener evtListener) {
		this.evtListener = evtListener;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	public int getMouseClickStatus() {
		return mouseClickStatus;
	}

	public void setMouseClickStatus(int mouseClickStatus) {
		this.mouseClickStatus = mouseClickStatus;
	}
	
	public String getTaskColor() {
		return taskColor;
	}

	public void setTaskColor(String taskColor) {
		this.taskColor = taskColor;
	}

	public void setBorderColor(String key) {
		this.taskColor = key;
		this.highColor = ImageObj.colorPickerList.get(this.taskColor);
		defaultBorder = new LineBorder(highColor, borderWidth);
		this.setBorder(defaultBorder);
	}
	
	public void setBorderColor(int index) {
		if(index >= ImageObj.colorKeyList.size()) index = 0;
		else if(index < 0) index = ImageObj.colorKeyList.size() - 1;
		
		this.taskColor = ImageObj.colorKeyList.get(index);
		this.highColor = ImageObj.colorPickerList.get(this.taskColor);
		defaultBorder = new LineBorder(highColor, borderWidth);
		this.setBorder(defaultBorder);
	}
	
	public Color getBorderColor(String key) {
		if(ImageObj.colorPickerList.size() < 1) ImageObj.getColorPickers();
		return ImageObj.colorPickerList.get(key);
	}
	
}
