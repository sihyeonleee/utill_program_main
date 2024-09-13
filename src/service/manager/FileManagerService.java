package service.manager; 

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import comm.CustomThread;
import comm.fileio.FileChooser;
import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import gui.obj.ImageObj;
import gui.obj.calendar.CalendarViewer;
import gui.obj.calendar.DateObj;
import main.Main;
import service.Service;
import service.manager.file.FileObjEventListener;
import service.manager.file.FileObject;  

public class FileManagerService extends Service implements FileObjEventListener{ 
	
	private static final String ROOTDIR = "lnk";
	private static final String ROOTPATH = comm.Path.ROOTPATH + ROOTDIR + File.separator;
	
	private static final String FILESUFFIX = "lnk";
	private static final String FILEDBNAME = "db";
	private static final String FILEDBEXTN = "lsh";
	
	private final String comboDefaultPart = "범위 선택";
	private final String comboDefaultFilt = "필터 선택";
	private final String comboDefaultType = "색상 선택";
	
	private String[] filters = { comboDefaultFilt, "이름", "태그", "날짜" };
	private List<String> colors = new ArrayList<>(); // Dynamic Color List
	
	// File DataBase
	private Map<String, Map<String, Map<String, String>>> db = new HashMap<>();
	private List<String> domainNames = new ArrayList<>();
	
	// Searched File List
	private Map<String, Map<String, Map<String, String>>> searchList = new HashMap<>();
	
	// File Navigation Field
	private String searchKeyword = "";
	private String selectedFilter = comboDefaultFilt;
	private String selectedType = comboDefaultType;
	
	// ComboBox Infinite Repetition Denied 
	private boolean comboboxChangeSync = false;
	
	// Keyboard Direction Object Index
	private List<FileObject> keyObjects = new ArrayList<>();
	private Integer keyObjectIndex;
	
	private JLabel projectNameLabel = new JLabel("---");
	private JLabel fileNameLabel = new JLabel("---");
	
	private Font big = new Font("함초롱바탕", Font.BOLD, 13);
	private Font small = new Font("함초롱돋움", Font.PLAIN, 11);
	
	
	
	
	public FileManagerService(){ 

		// 프레임 설정
		width = 480;
		height = 450;
		divisionX = 103;
		divisionY = 99;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj label1 = new CompObj();
		label1.setType(CompObj.TYPE_BUTTON);
		label1.setEnabled(false);
		label1.setName("PART");
		label1.setGridPosition(0, 0);
		label1.setGridWeight(1, 1);
		label1.setGridSize(1, 1);
		label1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(label1);
		
		CompObj addFolder = new CompObj();
		addFolder.setName("추가");
		addFolder.setEvtName("add");
		addFolder.setType(CompObj.TYPE_BUTTON);
		addFolder.setEventType(CompObj.EVENT_ACTION);
		addFolder.setArrangeType(GridBagConstraints.BOTH);
		addFolder.setGridPosition(1, 0);
		addFolder.setGridSize(1, 1);
		componentObjs.add(addFolder);
		
		CompObj modFolder = new CompObj();
		modFolder.setName("수정");
		modFolder.setEvtName("modify");
		modFolder.setType(CompObj.TYPE_BUTTON);
		modFolder.setEventType(CompObj.EVENT_ACTION);
		modFolder.setArrangeType(GridBagConstraints.BOTH);
		modFolder.setGridPosition(2, 0);
		modFolder.setGridSize(1, 1);
		componentObjs.add(modFolder);
		
		CompObj delFolder = new CompObj();
		delFolder.setName("삭제");
		delFolder.setEvtName("delete");
		delFolder.setType(CompObj.TYPE_BUTTON);
		delFolder.setEventType(CompObj.EVENT_ACTION);
		delFolder.setArrangeType(GridBagConstraints.BOTH);
		delFolder.setGridPosition(3, 0);
		delFolder.setGridSize(1, 1);
		componentObjs.add(delFolder);
		
		String[] domainList = { comboDefaultPart };
		CompObj domainSelect = new CompObj();
		domainSelect.setEvtName("domain");
		domainSelect.setType(CompObj.TYPE_COMBOBOX);
		domainSelect.setSelectItems(domainList);
		domainSelect.setEventType(CompObj.EVENT_ACTION);
		domainSelect.setArrangeType(GridBagConstraints.BOTH);
		domainSelect.setGridPosition(4, 0);
		domainSelect.setGridWeight(1, 1);
		domainSelect.setGridSize(2, 1);
		componentObjs.add(domainSelect);
		
		CompObj label2 = new CompObj();
		label2.setType(CompObj.TYPE_BUTTON);
		label2.setEnabled(false);
		label2.setName("FIND");
		label2.setGridPosition(0, 1);
		label2.setGridSize(1, 1);
		label2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(label2);
		
		CompObj search = new CompObj();
		search.setEvtName("search");
		search.setType(CompObj.TYPE_INPUT);
		search.setEventType(CompObj.EVENT_TYPING, CompObj.EVENT_KEYTYPE);
		search.setGridPosition(1, 1);
		search.setGridWeight(10, 1);
		search.setGridSize(3, 1);
		search.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(search);
		
		CompObj filterSelect = new CompObj();
		filterSelect.setEvtName("filter");
		filterSelect.setType(CompObj.TYPE_COMBOBOX);
		filterSelect.setSelectItems(filters);
		filterSelect.setEventType(CompObj.EVENT_ACTION);
		filterSelect.setArrangeType(GridBagConstraints.BOTH);
		filterSelect.setGridPosition(4, 1);
		filterSelect.setGridSize(1, 1);
		componentObjs.add(filterSelect);
		
		colors.add(comboDefaultType);
		Map<String, Color> colorMap = ImageObj.getColorPickers();
		for(Map.Entry<String, Color> map : colorMap.entrySet()) colors.add(map.getKey());
		CompObj colorSelect = new CompObj();
		colorSelect.setEvtName("color");
		colorSelect.setType(CompObj.TYPE_COMBOBOX);
		colorSelect.setSelectItems(colors);
		colorSelect.setEventType(CompObj.EVENT_ACTION);
		colorSelect.setArrangeType(GridBagConstraints.BOTH);
		colorSelect.setGridPosition(5, 1);
		colorSelect.setGridSize(1, 1);
		componentObjs.add(colorSelect);
		
		CompObj panel1 = new CompObj();
		panel1.setEvtName("panel");
		panel1.setType(CompObj.TYPE_PANEL);
		panel1.setEventType(CompObj.EVENT_DRAGDROP);
		panel1.setLayout(CompObj.LAYOUT_GRID, 0, 5, 2, 2);
		panel1.setScrollAt(true);
		panel1.setGridSize(6, 1);
		panel1.setGridWeight(1, 50);
		panel1.setGridPosition(0, 2);
		panel1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(panel1);
		
		CompObj projectNameSpace = new CompObj();
		projectNameSpace.setType(CompObj.TYPE_PANEL);
		projectNameSpace.setLayout(CompObj.LAYOUT_FLOW);
		projectNameSpace.setScrollAt(false);
		projectNameSpace.setGridSize(6, 1);
		projectNameSpace.setGridWeight(1, 3);
		projectNameSpace.setArrangeType(GridBagConstraints.BOTH);
		projectNameSpace.setGridPosition(0, 3);
		componentObjs.add(projectNameSpace);
		
		CompObj fileNameSpace = new CompObj();
		fileNameSpace.setType(CompObj.TYPE_PANEL);
		fileNameSpace.setLayout(CompObj.LAYOUT_FLOW);
		fileNameSpace.setScrollAt(false);
		fileNameSpace.setGridSize(6, 1);
		fileNameSpace.setGridWeight(1, 3);
		fileNameSpace.setArrangeType(GridBagConstraints.BOTH);
		fileNameSpace.setGridPosition(0, 4);
		componentObjs.add(fileNameSpace);
		
	}
	
	
	
	
	@Override
	public void doShow(String name) {
		if(panels.get(1).getComponentCount() == 0) init();
		if(db.size() == 0) beforeDoShow();
		super.doShow(name);
		afterDoShow();
	}
	
	public void init(){
		
		((JComponent) panels.get(1)).setBorder(null);
		panels.get(0).setBackground(Color.white);
		panels.get(1).add(projectNameLabel);
		panels.get(2).add(fileNameLabel);
		buttons.get(0).setFont(small);
		buttons.get(1).setFont(small);
		buttons.get(2).setFont(small);
		buttons.get(3).setFont(small);
		buttons.get(0).setBackground(new Color(235,235,235,255));
		buttons.get(1).setBackground(new Color(235,235,235,255));
		buttons.get(2).setBackground(new Color(235,235,235,255));
		buttons.get(3).setBackground(new Color(235,235,235,255));
		projectNameLabel.setFont(small);
		fileNameLabel.setFont(big);
		
		comboBoxes.get(0).setFont(small);
		comboBoxes.get(1).setFont(small);
		comboBoxes.get(2).setFont(small);
		
	}
	
	public void beforeDoShow(){
		loadData();
	}
	
	public void afterDoShow(){
		inputs.get(0).requestFocusInWindow();
	}
	
	
	
	// Main Methods
	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {
		
		String domainName = (String) comboBoxes.get(0).getSelectedItem();
		
		if(type.equals("change") && "domain".equals(obj.getEvtName()) ){
			if(this.comboboxChangeSync) searchKeyword();
		}else if(type.equals("click") && "add".equals(obj.getEvtName())){
			addProject();
		}else if(type.equals("click") && "modify".equals(obj.getEvtName())){
			modProject(domainName);
		}else if(type.equals("click") && "delete".equals(obj.getEvtName())){
			delProject(domainName);
		}else if(type.equals("tyInsert") || type.equals("tyRemove") && "search".equals(obj.getEvtName())){
			this.searchKeyword = (String) objects[0];
			loadComponents();
		}else if(type.equals("keyPress") && "search".equals(obj.getEvtName())){
			shortcutEvent((KeyEvent) objects[0]);
		}else if(type.equals("change") && "filter".equals(obj.getEvtName())){
			this.selectedFilter = (String) comboBoxes.get(1).getSelectedItem();
			if("날짜".equals(selectedFilter)) showCalendarPicker();
			loadComponents();
		}else if(type.equals("change") && "color".equals(obj.getEvtName())){
			this.selectedType = (String) comboBoxes.get(2).getSelectedItem();
			loadComponents();
		}else if(type.equals("change") && "panel".equals(obj.getEvtName())){
			addFiles(domainName, objects);
		}
		
//		String threadName = "FileManageService";
//		runThread(threadName, false);

	}
	
	@SuppressWarnings("static-access")
	public void loadData() {

		this.comboboxChangeSync = false;
		
		this.db.clear();
		this.domainNames.clear();
		
		File[] domains = FileChooser.getFileList(this.ROOTPATH);
		
		if(domains == null){
			FileChooser.mkdirs(this.ROOTPATH);
			domains = FileChooser.getFileList(this.ROOTPATH);
		}
		
		domainNames.add(comboDefaultPart);
		
		Arrays.sort(domains, Comparator.comparing(File::getName));
		
		for(File domain : domains){
			
			String domainName = domain.getName();
			
			this.domainNames.add(domainName);
			
			db.put(domainName, new HashMap<String, Map<String, String>>());
			
			// DB 파일 읽기
			TextFileReader reader = new TextFileReader(domain.getPath(), FILEDBNAME, FILEDBEXTN);
			Map<String, Map<String, String>> readeResult = reader.getKeyValueTypeDataToMap(reader.reade(true), "\\[File\\]", "fileName");
			
			// 파일 목록 확인 (존재하는)
			File[] files = domain.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return !name.toLowerCase().endsWith("." + FILEDBEXTN);
			    }
			});
			
			for(File file : files){
				Map<String, String> fileKeyValueInfo = null;
				if(readeResult.containsKey(file.getName())) fileKeyValueInfo = readeResult.get(file.getName());
				else fileKeyValueInfo = getFileInfoBase(file.getName()); // DB 정보에 데이터가 없을 경우 임시 데이터 셋팅
				db.get(domainName).put(file.getName(), fileKeyValueInfo);
			}
		}
		
		searchKeyword();
		
	}
	
	public void searchKeyword(){
		
		this.searchList.clear();
		
		String domainName = (String) comboBoxes.get(0).getSelectedItem();
		String[] searchKeywords = this.searchKeyword.split(" ");
		
		Map<String, Map<String, Map<String, String>>> domains = new HashMap<>();
		
		// 1차 필터링 : 도메인
		if(domainName.equals(comboDefaultPart)){
			domains = db;
		}else {
			domains.put(domainName, db.get(domainName));
		}
		
		
		// 2차 필터링 : 태그, 색상
		for(Entry<String, Map<String, Map<String, String>>> domain : domains.entrySet()){
			for(Entry<String, Map<String, String>> file : domain.getValue().entrySet()){
				
				// 매치 카운터
				int matchCnt = 0;
				
				// 태그 필터링
				for(String searchKeyword : searchKeywords){
					if(selectedFilter.equals(filters[0])){
						if(matchKeyword(file.getValue().get("fileName"), searchKeyword) 
								|| matchKeyword(file.getValue().get("fileTag"), searchKeyword) 
								|| matchKeyword(file.getValue().get("insertDt"), searchKeyword)) matchCnt++;
					}else if(selectedFilter.equals(filters[1])){
						if(matchKeyword(file.getValue().get("fileName"), searchKeyword)) matchCnt++;
					}else if(selectedFilter.equals(filters[2])){
						if(matchKeyword(file.getValue().get("fileTag"), searchKeyword)) matchCnt++;
					}else if(selectedFilter.equals(filters[3])){
						if(matchKeyword(file.getValue().get("insertDt"), searchKeyword)) matchCnt++;
					}
				}
				
				// 색상 필터링
				if(matchCnt == searchKeywords.length){
					if(!searchList.containsKey(domain.getKey())) searchList.put(domain.getKey(), new HashMap<>());
					if(selectedType.equals(comboDefaultType) || selectedType.equals(file.getValue().get("taskColor")))
						searchList.get(domain.getKey()).put(file.getKey(), file.getValue());
				}
				
			}
		}
		
		loadComponents();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	public void loadComponents(){
		
		panels.get(0).removeAll();
		
		this.keyObjects.clear();
		
		for(Entry<String, Map<String, Map<String, String>>> domain : searchList.entrySet()) {
			for(Entry<String, Map<String, String>> file : domain.getValue().entrySet()){

				String path = this.ROOTPATH + File.separator + domain.getKey() + File.separator + file.getKey();
				FileObject obj = new FileObject(file.getKey(), domain.getKey(), path, this);
				obj.setFileTag(file.getValue().get("fileTag"));
				obj.setInsertDt(file.getValue().get("insertDt"));
				obj.setUpdateDt(file.getValue().get("updateDt"));
				obj.setBorderColor(file.getValue().get("taskColor"));
				
				this.keyObjects.add(obj);
				panels.get(0).add(obj);
			}
		}
		
		panels.get(0).revalidate();
		panels.get(0).repaint();
		
		String domainName = (String) comboBoxes.get(0).getSelectedItem();
		String[] models = domainNames.toArray(new String[domainNames.size()]);
		
		comboBoxes.get(0).setModel(new DefaultComboBoxModel(models));
		comboBoxes.get(0).setSelectedItem(domainName);
		
		this.comboboxChangeSync = true;
		
	}

	@SuppressWarnings("static-access")
	public void writeFiles(boolean append, String... domainNames) {
		
		Map<String, Map<String, Map<String, String>>> db = new HashMap<>();
		
		if(domainNames == null || domainNames.length < 1) db = this.db; // All Data Write
		else for(String name : domainNames) db.put(name, this.db.get(name)); // Selected Data Write
		
		// 도메인
		for(Entry<String, Map<String, Map<String, String>>> domain : db.entrySet()){
			
			String text = "";
			String path = this.ROOTPATH + File.separator + domain.getKey() + File.separator;
			
			// 프로젝트 생성 시, 프로젝트 폴더가 없을경우
			File dbFile = new File(path);
			if(!dbFile.exists()) dbFile.mkdirs();
			
			// 파일
			for(Entry<String, Map<String, String>> file : domain.getValue().entrySet()){
				
				text += "[File]\n";
				// 키 값
				for(Entry<String, String> keyValues : file.getValue().entrySet()){
					text += keyValues.getKey() + "=" + keyValues.getValue() + "\n";
				}
				
				text += "\n\n";
				
			}
			
			TextFileWriter write = new TextFileWriter(path, FILEDBNAME, FILEDBEXTN, text);
			write.write(append);
			
		}
		
		searchKeyword();
		
	}
	
	
	
	
	// File Object Double Click
	@Override
	public void onDouClickEvt(FileObject label) {
		FileChooser.exec(label.getFilePath(), false);
	}

	// File Object Key index init
	@Override
	public void onReleaseEvt(FileObject label) {
		inputs.get(0).requestFocus();
		this.keyObjectIndex = this.keyObjects.indexOf(label);
	}
	
	// File Object Menu List Click
	@Override
	public void onMenuEvt(FileObject label, String menuType) {
		
		final List<FileObject> objects = new ArrayList<>();

		// Selected Object List
		for(FileObject obj : this.keyObjects){
			if(label.getMouseClickStatus() == 1){
				if(obj.getMouseClickStatus() == 1){
					objects.add(obj);
				}
			}else {
				if(label != obj){
					obj.unSelect();
				}else {
					objects.add(obj);
				}
			}
		}
		
		switch(menuType){
			case FileObject.OPEN_FILE :
				for(FileObject obj : objects){
					FileChooser.exec(obj.getFilePath(), false);
					obj.unSelect();
				}
				break;
			case FileObject.UPDATE_TAG :
				updateFileTag(label);
				break;
			case FileObject.DELETE_FILE :
				delFiles(objects);
				break;
			case FileObject.OPEN_FOLDER :
				for(FileObject obj : objects) {
					Map<String, String> r = FileChooser.openOrginFolder(obj.getFilePath());
					if(!r.get("err").trim().equals("")) {
						Main.alertPop(obj.getFileName() + " 파일의 경로를 찾을수 없습니다.");
					}
				}
				break;
			case FileObject.FILE_INFO :
				Main.alertPop("파일이름 : " + label.getFileName() + "\n생성일자 : " + label.getInsertDt() + "\n마지막수정일자 : " + label.getUpdateDt() + 
						"\n\n### 태그정보 ### \n" + label.getFileTag().replace("\t", "\n"));
				break;
			case FileObject.FILE_TYPE :
				
				final JLabel color = new JLabel();
				int colorPopResult = showColorPicker(color);
				
				if(colorPopResult > 0){
					for(FileObject obj : objects){
			        	db.get(obj.getProjectName()).get(obj.getFileName()).put("taskColor", color.getText());
			        	writeFiles(false, obj.getProjectName());
					}
				}
				break;
		}
		
	}
	
	// File Object Focusing
	@Override
	public void onFocusEvt(FileObject label) {
		fileNameLabel.setText(label.getFileName());
		projectNameLabel.setText(label.getProjectName());
	}
	
	
	
	
	// Event Methods
	@SuppressWarnings("static-access")
	public void shortcutEvent(KeyEvent e) {
		
		// ESC
		if(e.getKeyCode() == 27){
			this.frame.dispose();
			return;
		}
		
		// ENTER
		if(e.getKeyCode() == 10){
					
			if(this.keyObjectIndex == null) {
				this.keyObjectIndex = 0;
			}
			
			if(this.keyObjects.isEmpty()) {
				Main.alertPop("목록이 비어있습니다.");
			}else if(!e.isShiftDown()) {
				FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
				if(Main.confirmPop("파일 실행", "[" + keyObject.getProjectName() + "]\n " + keyObject.getFileName() + " 파일을 실행 하시겠습니까?") > 0){
					FileChooser.exec(keyObject.getFilePath(), false);
				}
			}else if(e.isShiftDown()) {

				if(e.isShiftDown() && e.getKeyCode() == 10){
					if(Main.confirmPop("폴더열기", this.keyObjects.get(this.keyObjectIndex).getFileName() + " 폴더를 띄우시겠습니까?") > 0){
						FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
						Map<String, String> r = FileChooser.openOrginFolder(keyObject.getFilePath());
						if(!r.get("err").trim().equals("")) {
							Main.alertPop(keyObject.getFileName() + " 파일의 경로를 찾을수 없습니다. \n파일의 경로가 변경 되었다면, 파일을 한번 실행후 다시 시도해주세요.");
						}
					}
				}
			}
			
			return;
			
		}
		
		// CTRL
		if((e.getModifiers() & 2) != 0){
			try{
				if(e.getKeyCode() >= 96 && e.getKeyCode() <= 105) {
					// 컨트롤 + 숫자(패드) 색상
					FileObject obj = this.keyObjects.get(this.keyObjectIndex);
					obj.setBorderColor(e.getKeyCode() - 97);
					db.get(obj.getProjectName()).get(obj.getFileName()).put("taskColor", obj.getTaskColor());
		        	writeFiles(false, obj.getProjectName());
	    		}else if (e.getKeyCode() >= 48 && e.getKeyCode() <= 57) {
	    			// 컨트롤 + 숫자 색상
	    			FileObject obj = this.keyObjects.get(this.keyObjectIndex);
	    			obj.setBorderColor(e.getKeyCode() - 49);
	    			db.get(obj.getProjectName()).get(obj.getFileName()).put("taskColor", obj.getTaskColor());
		        	writeFiles(false, obj.getProjectName());
	    		}
			}catch(Exception err){
				
			}
			
			if(e.getKeyCode() == 38 || e.getKeyCode() == 40) {
				// 프로젝트 위
				if(e.getKeyCode() == 38) {
					try{
						if(comboBoxes.get(0).getSelectedIndex() == 0){
							comboBoxes.get(0).setSelectedIndex(comboBoxes.get(0).getItemCount()-1);
						}else {
							comboBoxes.get(0).setSelectedIndex(comboBoxes.get(0).getSelectedIndex()-1);
						}
					}catch(Exception err){
						comboBoxes.get(0).setSelectedIndex(comboBoxes.get(0).getItemCount()-1);
					}
				// 프로젝트 아래
				}else if(e.getKeyCode() == 40) {
					try{
						comboBoxes.get(0).setSelectedIndex(comboBoxes.get(0).getSelectedIndex()+1);
					}catch(Exception err){
						comboBoxes.get(0).setSelectedIndex(0);
					}
				}
				
			}
			
			return;
		}
		
		// ALT
		if(e.isAltDown()){
			
			if(e.getKeyCode() == KeyEvent.VK_D){
				showCalendarPicker();
			}else if(e.getKeyCode() == KeyEvent.VK_Q){
				try{
					FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
					keyObject.setBorderColor(ImageObj.getColorKeyList().indexOf(keyObject.getTaskColor())-1);
					db.get(keyObject.getProjectName()).get(keyObject.getFileName()).put("taskColor", keyObject.getTaskColor());
		        	writeFiles(false, keyObject.getProjectName());
				}catch(Exception err){}
			}else if(e.getKeyCode() == KeyEvent.VK_W){
				try{
					FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
					keyObject.setBorderColor(ImageObj.getColorKeyList().indexOf(keyObject.getTaskColor())+1);
					db.get(keyObject.getProjectName()).get(keyObject.getFileName()).put("taskColor", keyObject.getTaskColor());
		        	writeFiles(false, keyObject.getProjectName());
				}catch(Exception err){}
			}else if(e.getKeyCode() == KeyEvent.VK_A){
				try{
					FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
					Main.alertPop("경로 : " + keyObject.getProjectName() + "\n파일이름 : " + keyObject.getFileName() + "\n생성일자 : " + keyObject.getInsertDt() + "\n마지막수정일자 : " + keyObject.getUpdateDt() + 
							"\n\n### 태그정보 ### \n" + keyObject.getFileTag().replace("\t", "\n"));
				}catch(Exception err){}
			}else if(e.getKeyCode() == KeyEvent.VK_S){
				try{
					FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
					updateFileTag(keyObject);
				}catch(Exception err){}
			}else if(e.getKeyCode() == KeyEvent.VK_E){
				try{
					comboBoxes.get(2).setSelectedIndex(comboBoxes.get(2).getSelectedIndex() != 0 ? comboBoxes.get(2).getSelectedIndex()-1 : comboBoxes.get(2).getItemCount()-1);
				}catch(Exception err){
					comboBoxes.get(2).setSelectedIndex(comboBoxes.get(2).getItemCount()-1);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_R){
					try{
						comboBoxes.get(2).setSelectedIndex(comboBoxes.get(2).getSelectedIndex()+1);
					}catch(Exception err){
						comboBoxes.get(2).setSelectedIndex(0);
					}
			}
//			else if(e.getKeyCode() == 192){
//				// ALT + ~ ( 프로젝트 변경 )
//				try{
//					comboBoxes.get(0).setSelectedIndex(comboBoxes.get(0).getSelectedIndex()+1);
//				}catch(Exception err){
//					comboBoxes.get(0).setSelectedIndex(0);
//				}
//			}
			else if(e.getKeyCode() == 37 || e.getKeyCode() == 38 || e.getKeyCode() == 39 || e.getKeyCode() == 40) {
				// 좌, 전, 우, 후
				if(e.getKeyCode() == 37){
					if(this.keyObjectIndex == null) {
						this.keyObjectIndex = this.keyObjects.size() - 1;
					}else {
						if(this.keyObjectIndex <= 0 || this.keyObjectIndex >= this.keyObjects.size()) {
							FileObject keyObject = this.keyObjects.get(0);
							keyObject.unFocus();
							this.keyObjectIndex = this.keyObjects.size() - 1;
						}else {
							FileObject keyObject = this.keyObjects.get(this.keyObjectIndex--);
							keyObject.unFocus();
						}
					}
				}else if(e.getKeyCode() == 38){
					if(this.keyObjectIndex == null) {
						this.keyObjectIndex = this.keyObjects.size() - 1;
					}else {
						if(this.keyObjectIndex-5 < 0 || this.keyObjectIndex-5 >= this.keyObjects.size()) {
							/*FileObject keyObject = filterList.get(this.keyObjectIndex);
							keyObject.unFocus();
							this.keyObjectIndex = (filterList.size() - 1) - (5 - this.keyObjectIndex);*/
						}else {
							FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
							keyObject.unFocus();
							this.keyObjectIndex = this.keyObjectIndex - 5;
						}
					}
				}else if(e.getKeyCode() == 39) {
					if(this.keyObjectIndex == null) {
						this.keyObjectIndex = 0;
					}else {
						if(this.keyObjectIndex >= this.keyObjects.size()-1 || this.keyObjectIndex < 0) {
							FileObject keyObject = this.keyObjects.get(this.keyObjects.size()-1);
							keyObject.unFocus();
							this.keyObjectIndex = 0;
						}else {
							FileObject keyObject = this.keyObjects.get(this.keyObjectIndex++);
							keyObject.unFocus();
						}
					}
				}else if(e.getKeyCode() == 40) {
					if(this.keyObjectIndex == null) {
						this.keyObjectIndex = 0;
					}else {
						if(this.keyObjectIndex+5 > this.keyObjects.size()-1 || this.keyObjectIndex+5 < 0) {
							/*FileObject keyObject = filterList.get(this.keyObjectIndex);
							keyObject.unFocus();
							this.keyObjectIndex = (5 + this.keyObjectIndex) - (filterList.size() - 1);*/
						}else {
							FileObject keyObject = this.keyObjects.get(this.keyObjectIndex);
							keyObject.unFocus();
							this.keyObjectIndex = this.keyObjectIndex + 5;
						}
					}
				}
				
				FileObject keyObject;
				
				try {
					keyObject = this.keyObjects.get(this.keyObjectIndex);
					keyObject.focus();
					((JPanel) panels.get(0)).scrollRectToVisible(keyObject.getBounds());
				}catch(Exception err) {
					
				}
			}
		}
		
	}
	
	public void addProject(){
		// 프로젝트 구분 추가시
		String folderName = Main.inputPop("프로젝트 명을 입력해주세요.");
		
		if( folderName == null ){
			return;
		} else if(folderName.trim().equals("")){
			Main.alertPop("올바른 파일명을 입력해주세요.");
			return;
		}
		
		int result = FileChooser.mkdirs(this.ROOTPATH + File.separator + folderName);
		
		if(result > 0){
			Main.alertPop("프로젝트 구분이 추가 되었습니다.");
			domainNames.add(folderName);
			db.put(folderName, new HashMap<>());
			comboBoxes.get(0).setSelectedItem(comboBoxes.get(0).getSelectedItem());
			return;
		}else if(result == -1){
			Main.alertPop("올바른 파일명을 입력해주세요.");
			return;
		}else if(result == -2){
			Main.alertPop("같은 이름의 프로젝트가 존재합니다.");
			return;
		}
	}
	
	public void modProject(String domainName){
		
		if(domainName.equals(comboDefaultPart)){
			domainName = showProjectPicker();
			if(domainName == null){
				return;
			}
		}
		
		Map<String, String> map = new HashMap<>();
		map.put("name", domainName);
		String[] name = {"name"};
		
		Map<String, String> result = Main.confirmPop("프로젝트 수정", name, map);
		
		if("confirm".equals(result.get("result"))){
			
			if(result.containsKey("name")){
				String newName = result.get("name");
				
				boolean runResult = FileChooser.nameChange(ROOTPATH + domainName, ROOTPATH + newName);
				
				if(runResult){
					domainNames.remove(domainName);
					domainNames.add(newName);
					db.put(newName, db.remove(domainName));
					comboBoxes.get(0).setSelectedItem(comboBoxes.get(0).getSelectedItem());
				}else {
					Main.alertPop("오류발생 : 다시 시도해주세요");
				}
			}else {
				Main.alertPop("입력을 확인해주세요.");
			}
			
		}
		
	}
	
	public void delProject(String domainName){
		// 프로젝트 구분 삭제시
		if(domainName.equals(comboDefaultPart)){
			domainName = showProjectPicker();
			if(domainName == null){
				return;
			}
		}
		
		if(Main.confirmPop("프로젝트 삭제", "\"" + domainName + "\" 프로젝트를 삭제할 경우 관리 항목(파일) 모두 삭제 됩니다. \n삭제 하시겠습니까?") > 0){
			int result = FileChooser.deleteFile(this.ROOTPATH + File.separator + domainName);
			if(result > 0){
				Main.alertPop("프로젝트가 삭제 되었습니다.");
				db.remove(domainName);
				domainNames.remove(domainName);
				comboBoxes.get(0).setSelectedItem(comboDefaultPart);
				return;
			}else {
				Main.alertPop("파일 삭제에 실패 했습니다. \n다시 시도해주세요.");
				return;
			}
			
		}else {
			return;
		}
	}
	
	public void addFiles(String domainName, Object...objects){
		
		// 추가할 위치 기본으로 묻기
		domainName = showProjectPicker();
		
		if(domainName == null || "".equals(domainName)) return;
		
		File[] files = (File[]) objects;
		List<String> fileNames = new ArrayList<>();
		String lnkPath = ROOTPATH + domainName;
        
		for (File file : files) {
        	fileNames.add(file.getName()+"."+FILESUFFIX);
        	
        	// ###
//        	if(file.getPath().matches("^?+(?i).*(Desktop|desktop|desk|바탕화면).*+$")){
//        		Main.alertPop("바탕화면의 파일은 참조 할수 없습니다.");
//        		return;
//        	}
        	
        }
		
        if(fileNames.size() > 0) {
        	Map<String, String> resultPop = Main.confirmPop( " TAG ( 공백으로 구분 ) ", fileNames.toArray(), 16, 5);
	        
        	if("confirm".equals(resultPop.get("result"))){
    	        for (File file : files) {
    	        	Map<String, Object> result = FileChooser.shortcutFile(file, lnkPath, false);
    	        	
    	        	if(result.get("result").equals("aleady")){
    	        		Main.alertPop("이미 존재하는 파일입니다.\n파일명 : " + file.getName());
    	        		continue;
    	        	}
    	        	
    	        	String tag = resultPop.containsKey(file.getName()+"."+FILESUFFIX) ? resultPop.get(file.getName()+"."+FILESUFFIX) : "empty";
    	        	tag = tag.replace("\n", "\t");
    	        	
    	        	Map<String, String> keyVal = new HashMap<>();
        			keyVal.put("fileName", file.getName()+"."+FILESUFFIX);
        			keyVal.put("insertDt", CalendarViewer.getNowDate("yyyy/MM/dd"));
        			keyVal.put("fileTag", tag);
        			keyVal.put("taskColor", "WHITE");
        			
        			db.get(domainName).put(file.getName()+"."+FILESUFFIX, keyVal);
    	        }

    	        writeFiles(true, domainName);

        	}else {
        		return;
        	}
        }
	}

	public void delFiles(List<FileObject> objects){
		if(Main.confirmPop("파일 삭제", "파일을 삭제 하시겠습니까?") != 1){
			return;
		}
		
		int result = 1;
		for(FileObject obj : objects) {
			result *= FileChooser.deleteFile(obj.getFilePath());
			if(result > 0){
				db.get(obj.getProjectName()).remove(obj.getFileName());
			}
		}
		
		if(result > 0){
			Main.alertPop("파일이 삭제 되었습니다.");
		}else {
			Main.alertPop("파일 삭제에 실패 하였습니다. \n다시 시도해주세요.");
		}
		
		searchKeyword();
		
	}
	
	public void updateFileTag(FileObject obj){
		String[] editStr = {"Tag"};
    	Map<String, String> data = new HashMap<>();
    	data.put("Tag", this.db.get(obj.getProjectName()).get(obj.getFileName()).get("fileTag").replace("\t", "\n"));
    	Map<String, String> resultPop = Main.confirmPop("파일 정보 수정", editStr, data, 16, 5);
    	
    	if(resultPop.get("result").equals("cancel")){
    		return;
    	}else if(resultPop.size()-1 != editStr.length){
    		Main.alertPop("입력값을 확인해주세요.");
    		return;
    	}
    	
    	String tag = resultPop.get("Tag").replace("\n", "\t");
    	
    	db.get(obj.getProjectName()).get(obj.getFileName()).put("fileTag", tag);
    	db.get(obj.getProjectName()).get(obj.getFileName()).put("updateDt", CalendarViewer.getNowDate("yyyy/MM/dd"));
    	
    	writeFiles(false, obj.getProjectName());
    	
	}
	
	public boolean matchKeyword(String target, String keyword){
		boolean find = false;
		
		if(keyword.contains("|")){
			String[] orKeywords = keyword.toLowerCase().split("\\|");
			if(orKeywords != null)
				for(String orKeyword : orKeywords)
					if(target.toLowerCase().contains(orKeyword.toLowerCase())) find = true;
		}else if(keyword.contains("~")){
			String[] betweenKeywords = keyword.toLowerCase().split("\\~");
			try {
				SimpleDateFormat transFormat = new SimpleDateFormat("yyyy/MM/dd");
				Date fileDt = transFormat.parse(target);
				Date start = transFormat.parse(betweenKeywords[0]);
				Date end = transFormat.parse(betweenKeywords[1]);
				if(fileDt.compareTo(start) >= 0 && fileDt.compareTo(end) <= 0) find = true;
			} catch (Exception e) {}
		}else {
			if(target.toLowerCase().contains(keyword.toLowerCase())) find = true;
		}
		
		return find;
	}
	
	
	
	
	// Open Popup Window
	public String showProjectPicker(){
		
		String project = "";
		
		int i = 0;
		
		String[] selects = new String[db.size()];
		
		for(Entry<String, ?> domain : db.entrySet()) selects[i++] = domain.getKey();
		
		Arrays.sort(selects);
		
		if(selects.length > 0){
			project = (String) Main.selectPop("프로젝트 선택", "구분", "", selects);
		}else {
			Main.alertPop("프로젝트가 비어있습니다.");
			return null;
		}
		
		return project;
	}
	
	public void showCalendarPicker(){
		CalendarViewer calendar = new CalendarViewer(CalendarViewer.SELECT_MODE_DUOBLE);
		
		String[] options = {"OK", "Cancel"};
		int result = JOptionPane.showOptionDialog(null, calendar, "Calendar", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
		
		if(result == JOptionPane.YES_OPTION){
			List<DateObj> objs = calendar.getPickOrderDates();
			String date = "";
			for(DateObj o : objs){
				date += o.getYmd() + "~";
			}
			date = date.substring(0, date.length()-1);
			inputs.get(0).setText(date);
		}
		
	}
	
	public int showColorPicker(final JLabel result){
		
		final JPanel panel = new JPanel(new GridLayout(3, 3, 5, 5));
		
		MouseAdapter listener = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					JButton button = (JButton) e.getComponent();
					result.setText(button.getText());
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					panel.repaint();
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					panel.repaint();
				}
				
			};
		for(Map.Entry<String, Color> map : ImageObj.getColorPickers().entrySet()){
			JButton color = new JButton(map.getKey());
			color.setOpaque(true);
			color.setBackground(map.getValue());
			color.addMouseListener(listener);
			panel.add(color);
		}
		
		return Main.confirmPop("Color Picker", panel);
	}
	
	
	
	public Map<String, String> getFileInfoBase(String name){
		Map<String, String> keyValue = new HashMap<>();
		keyValue.put("fileName", name);
		keyValue.put("insertDt", CalendarViewer.getNowDate("yyyy/MM/dd"));
		keyValue.put("fileTag", "empty");
		keyValue.put("taskColor", "WHITE");
		return keyValue;
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
