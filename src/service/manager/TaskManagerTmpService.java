package service.manager; 

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.DefaultComboBoxModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import comm.CustomThread;
import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import gui.obj.ImageObj;
import gui.obj.calendar.CalendarViewer;
import main.Main;
import service.Service;
import service.manager.task.pannel.TaskCndPanel;
import service.manager.task.pannel.TaskDtlPanel;
import service.manager.task.pannel.TaskFilePanel;
import service.manager.task.pannel.TaskTagPanel;  

public class TaskManagerTmpService extends Service { 

	// 경로
	private static final String ROOTDIR = "task";
	private static final String ROOTPATH = comm.Path.ROOTPATH + ROOTDIR + File.separator;
	private static final String FILEDIRNAME = "reference";
	private static final String TASKDIRNAME = "schedule";
	private static final String FILEDBNAME = "INFO";
	private static final String FILEDBEXTN = "lsh";
	private static final String WRAP = "/wrap";
	
	// 콤보박스 기본값
	private static final String comboDefaultDomain = "범위"; // 프로젝트
	private static final String comboDefaultFilt = "필터"; // 검색 필터
	private static final String comboDefaultColor = "색상"; // 색상
	private static final String comboDefaultSort = "정렬"; // 정렬
	
	// 콤보박스 리스트
	private static final List<String> domains = new ArrayList<>();
	private static final String[] filters = { comboDefaultFilt, "이름", "태그", "날짜" };
	private static final List<String> colors = new ArrayList<>(); // 유동적인 색상 변화를 위해
	private static final String[] sorts = { comboDefaultSort, "이름순", "최신순" };
	
	// 패널 영역
	private TaskCndPanel caldrPanel;
	private TaskDtlPanel contsPanel;
	private TaskTagPanel tasksPanel;
	private TaskFilePanel filesPanel;
	
	// 데이터
	private Map<String, Map<String, Map<String, Map<String, String>>>> db = new HashMap<>();
	
	// 검색 리스트
	private int nowFocusIndex = 0;
	private Map<String, Map<String, String>> allDataList = new HashMap<>();
	private Map<String, Map<String, String>> filtDataList = new HashMap<>();
	private List<Map<String, String>> searchTask = new ArrayList<>();
	private List<Map<String, String>> searchFile = new ArrayList<>();
	
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : Main UI Setting
	 * @Dcpt   : Initial Method
	 */
	public TaskManagerTmpService(){ 

		// 프레임 설정
		width = 780;
		height = 600;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj search1 = new CompObj();
		search1.setType(CompObj.TYPE_INPUT);
		search1.setEvtName("search");
		search1.setArrangeType(GridBagConstraints.BOTH);
		search1.setEventType(CompObj.EVENT_TYPING, CompObj.EVENT_KEYTYPE);
		search1.setGridPosition(0, 0);
		search1.setGridSize(10, 1);
		componentObjs.add(search1);
		
		CompObj line1 = new CompObj();
		line1.setType(CompObj.TYPE_OUTPUT);
		line1.setArrangeType(GridBagConstraints.BOTH);
		line1.setGridPosition(0, 1);
		line1.setGridSize(10, 1);
		componentObjs.add(line1);
		
		CompObj addFolder = new CompObj();
		addFolder.setName("추가");
		addFolder.setEvtName("add");
		addFolder.setType(CompObj.TYPE_BUTTON);
		addFolder.setEventType(CompObj.EVENT_ACTION);
		addFolder.setArrangeType(GridBagConstraints.BOTH);
		addFolder.setGridPosition(0, 2);
		addFolder.setGridSize(2, 1);
		componentObjs.add(addFolder);
		
		CompObj delFolder = new CompObj();
		delFolder.setName("삭제");
		delFolder.setEvtName("delete");
		delFolder.setType(CompObj.TYPE_BUTTON);
		delFolder.setEventType(CompObj.EVENT_ACTION);
		delFolder.setArrangeType(GridBagConstraints.BOTH);
		delFolder.setGridPosition(2, 2);
		delFolder.setGridSize(2, 1);
		componentObjs.add(delFolder);
		
		domains.add(comboDefaultDomain);
		CompObj select1 = new CompObj();
		select1.setEvtName("domain");
		select1.setType(CompObj.TYPE_COMBOBOX);
		select1.setSelectItems(domains);
		select1.setEventType(CompObj.EVENT_ACTION);
		select1.setArrangeType(GridBagConstraints.BOTH);
		select1.setGridPosition(5, 2);
		select1.setGridSize(1, 1);
		componentObjs.add(select1);
		
		CompObj select2 = new CompObj();
		select2.setEvtName("filter");
		select2.setType(CompObj.TYPE_COMBOBOX);
		select2.setSelectItems(filters);
		select2.setEventType(CompObj.EVENT_ACTION);
		select2.setArrangeType(GridBagConstraints.BOTH);
		select2.setGridPosition(6, 2);
		select2.setGridSize(1, 1);
		componentObjs.add(select2);
		
		colors.add(comboDefaultColor);
		Map<String, Color> colorMap = ImageObj.getColorPickers();
		for(Map.Entry<String, Color> map : colorMap.entrySet()) colors.add(map.getKey());
		CompObj select3 = new CompObj();
		select3.setEvtName("color");
		select3.setType(CompObj.TYPE_COMBOBOX);
		select3.setSelectItems(colors);
		select3.setEventType(CompObj.EVENT_ACTION);
		select3.setArrangeType(GridBagConstraints.BOTH);
		select3.setGridPosition(7, 2);
		select3.setGridSize(1, 1);
		componentObjs.add(select3);
		
		CompObj select4 = new CompObj();
		select4.setEvtName("sort");
		select4.setType(CompObj.TYPE_COMBOBOX);
		select4.setSelectItems(sorts);
		select4.setEventType(CompObj.EVENT_ACTION);
		select4.setArrangeType(GridBagConstraints.BOTH);
		select4.setGridPosition(8, 2);
		select4.setGridSize(1, 1);
		componentObjs.add(select4);
		
		CompObj line2 = new CompObj();
		line2.setType(CompObj.TYPE_OUTPUT);
		line2.setArrangeType(GridBagConstraints.BOTH);
		line2.setGridPosition(0, 3);
		line2.setGridSize(10, 1);
		componentObjs.add(line2);
		
		CompObj calendar = new CompObj();
		calendar.setType(CompObj.TYPE_PANEL);
		calendar.setLayout(CompObj.LAYOUT_BORDER);
		calendar.setScrollAt(false);
		calendar.setGridSize(5, 1);
		calendar.setGridWeight(1, 50);
		calendar.setGridPosition(0, 4);
		calendar.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(calendar);
		
		CompObj task = new CompObj();
		task.setType(CompObj.TYPE_PANEL);
		task.setLayout(CompObj.LAYOUT_BORDER);
		task.setScrollAt(false);
		task.setGridSize(5, 1);
		task.setGridWeight(1, 120);
		task.setGridPosition(0, 5);
		task.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(task);
		
		CompObj file = new CompObj();
		file.setType(CompObj.TYPE_PANEL);
		file.setLayout(CompObj.LAYOUT_BORDER);
		file.setScrollAt(false);
		file.setGridSize(5, 1);
		file.setGridWeight(1, 120);
		file.setGridPosition(0, 6);
		file.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(file);
		
		CompObj contents = new CompObj();
		contents.setType(CompObj.TYPE_PANEL);
		contents.setLayout(CompObj.LAYOUT_BORDER);
		contents.setScrollAt(false);
		contents.setGridSize(5, 3);
		contents.setGridWeight(1, 120);
		contents.setGridPosition(5, 4);
		contents.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(contents);
		
		this.caldrPanel = new TaskCndPanel();
		this.contsPanel = new TaskDtlPanel();
		this.tasksPanel = new TaskTagPanel();
		this.filesPanel = new TaskFilePanel();
		
	}

	@Override
	public void doShow(String name) {
		if(panels.get(0).getComponentCount() == 0) init();
		beforeDoShow();
		super.doShow(name);
		afterDoShow();
	}
	
	public void init(){
		inputs.get(0).setBorder(new BevelBorder(BevelBorder.LOWERED));
		outputs.get(0).setBorder(new EmptyBorder(5, 0, 5, 0)); // Line
		buttons.get(0).setOpaque(false);
		buttons.get(0).setForeground(Color.RED);
		buttons.get(0).setBackground(new Color(0,0,0,0));
		buttons.get(1).setOpaque(false);
		buttons.get(1).setBackground(new Color(0,0,0,0));
		buttons.get(1).setForeground(Color.BLUE);
		outputs.get(1).setBorder(new EmptyBorder(5, 0, 5, 0)); // Line
		
		panels.get(0).add(this.caldrPanel);
		panels.get(1).add(this.tasksPanel);
		panels.get(2).add(this.filesPanel);
		panels.get(3).add(this.contsPanel);
		
		caldrPanel.setBackground(Color.WHITE);
		contsPanel.setBackground(Color.WHITE);
		tasksPanel.setBackground(Color.WHITE);
		filesPanel.setBackground(Color.WHITE);
	}
	
	public void beforeDoShow(){
		loadData();
		setDomainBoxItems();
	}
	
	public void afterDoShow(){
		inputs.get(0).requestFocus();
	}
	
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 상시 호출 메인 메소드
	 * @Dcpt   : 
	 */
	@SuppressWarnings("static-access")
	public void loadData(){
		
		// 초기화
		db.clear();
		
		// ROOT 파일 생성
		File root = new File(this.ROOTPATH);
		if(!root.exists()) root.mkdirs();
		
		// 도메인 목록
		File[] domains = root.listFiles();
		if(domains == null || domains.length < 1) return;
		
		// 도메인 별 데이터 파일 파싱
		for(File domain : domains){
			// 파일 관리 목록, 업무 관리 목록
			File schedule  = new File(this.ROOTPATH + domain.getName() + File.separator +  TASKDIRNAME + File.separator);
			File reference = new File(this.ROOTPATH + domain.getName() + File.separator +  FILEDIRNAME + File.separator);
			if(!schedule.exists()) schedule.mkdir(); if(!reference.exists()) reference.mkdir();
			
			File[] schedules  = schedule.listFiles();
			File[] references = reference.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return !name.toLowerCase().endsWith( "." + FILEDBEXTN );
			    }
			});
			
			db.put(domain.getName(), new HashMap<>());
			db.get(domain.getName()).put("schedule", new HashMap<>());
			db.get(domain.getName()).put("reference", new HashMap<>());
			
			// 업무 목록( 파일 ) 데이터 셋팅
			if(schedules != null){
				for(File sch : schedules){
					String fNm = sch.getName().substring(0, sch.getName().lastIndexOf("."));
					TextFileReader taskFileReader = new TextFileReader(schedule.getPath(), fNm, this.FILEDBEXTN);
					Map<String, String> taskFileReadeResult = 
							taskFileReader.getKeyValueTypeDataToMap(taskFileReader.reade(true), WRAP);
					db.get(domain.getName()).get("schedule").put(taskFileReadeResult.get("uuid"), taskFileReadeResult);
				}
			}
			
			// 파일 목록( 파일, db ) 데이터 셋팅
			if(references != null){
				// DB 파일 Reade
				TextFileReader fileDbReader = new TextFileReader(reference.getPath(), this.FILEDBNAME, this.FILEDBEXTN);
				Map<String, Map<String, String>> dbFileReadeResult = 
						fileDbReader.getKeyValueTypeDataToMap(fileDbReader.reade(true), "\\[File\\]", "name", WRAP);
				// 파일 목록 데이터 셋팅
				// ### 파일 존재 유무를 기준으로 데이터 파싱 ###
				for(File strdFile : references){
					Map<String, String> fileKeyValueInfo = null;
					if(dbFileReadeResult.containsKey(strdFile.getName())) fileKeyValueInfo = dbFileReadeResult.get(strdFile.getName());
					else fileKeyValueInfo = getFileInfoBase(strdFile.getName(), false); // DB 정보에 데이터가 없을 경우 임시 데이터 셋팅
					db.get(domain.getName()).get("reference").put(fileKeyValueInfo.get("uuid"), fileKeyValueInfo);
				}
			}
		}
		
		searchKeyWord();
		
	}
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public void searchKeyWord(){
		
		// 초기화
		allDataList.clear();
		filtDataList.clear();
		searchTask.clear();
		searchFile.clear();
		
		// 콤보박스의 상태값
		String domain = (String) comboBoxes.get(0).getSelectedItem();
		String[] searchKeywords = inputs.get(0).getText().split(" ");
		String filter = (String) comboBoxes.get(1).getSelectedItem();
		String color = (String) comboBoxes.get(2).getSelectedItem();
		String sort = (String) comboBoxes.get(3).getSelectedItem();
		
		/*
		 * 1차 필터링 : 도메인 ( allDataList )
		 * */
		if(domain.equals(comboDefaultDomain)){
			for(Map.Entry<String, ?> domains : db.entrySet())
				for(Map.Entry<String, ?> directorys : ((Map<String, ?>)domains.getValue()).entrySet())
					for(Map.Entry<String, ?> file : ((Map<String, ?>)directorys.getValue()).entrySet())
						allDataList.put(file.getKey(), (Map<String, String>) file.getValue());
		}else {
			for(Map.Entry<String, ?> directorys : ((Map<String, ?>)db.get(domain)).entrySet())
				for(Map.Entry<String, ?> file : ((Map<String, ?>)directorys.getValue()).entrySet())
					allDataList.put(file.getKey(), (Map<String, String>) file.getValue());
		}
		
		/*
		 * 
		 * 2차 필터링 : 태그, 색상 ( filtDataList )
		 * */
		for(Entry<String, Map<String, String>> obj : allDataList.entrySet()){
			int matchCnt = 0;
			for(String searchKeyword : searchKeywords){
				if(filter.equals(filters[0])){
					if(navigation(obj.getValue().get("name"), searchKeyword) 
							|| navigation(obj.getValue().get("tag"), searchKeyword) 
							|| rangeDate(obj.getValue().get("start")+"~"+obj.getValue().get("end"), searchKeyword)) matchCnt++;
				}else if(filter.equals(filters[1])){
					if(navigation(obj.getValue().get("name"), searchKeyword)) matchCnt++;
				}else if(filter.equals(filters[2])){
					if(navigation(obj.getValue().get("tag"), searchKeyword)) matchCnt++;
				}else if(filter.equals(filters[3])){
					if(rangeDate(obj.getValue().get("start")+"~"+obj.getValue().get("end"), searchKeyword)) matchCnt++;
				}
			}
			
			if(matchCnt == searchKeywords.length){
				if(color.equals(this.comboDefaultColor)){
					filtDataList.put(obj.getKey(), obj.getValue());
				}else if(color.equals(obj.getValue().get("color"))){
					filtDataList.put(obj.getKey(), obj.getValue());
				}
			}
		}
		
		/*
		 * 3차 필터링 : 정렬 & 분배 ( searchTask, searchFile )
		 * */
		for(Map.Entry<String, Map<String, String>> obj : filtDataList.entrySet()){
			if("task".equals(obj.getValue().get("type"))){
				this.searchTask.add(obj.getValue());
			}else {
				this.searchFile.add(obj.getValue());
			}
		}
		
		loadComponent();
	}
	
	public void loadComponent(){
		
		// 초기화
//		caldrPanel.init();
//		tasksPanel.init();
//		filesPanel.init();
//		contsPanel.init();
		
		// load Component List
//		caldrPanel.loadComponent(this.searchTask);
//		tasksPanel.loadComponent(this.searchTask);
//		filesPanel.loadComponent(this.searchFile);
//		if(searchTask.size() > 0) {
//			TaskContentsPanel.ObjectFileFromUUID finder = new TaskContentsPanel.ObjectFileFromUUID() {
//				@Override
//				public TaskObject getFileObj(String uuid) {
//					return new TaskObject(allDataList.get(uuid));
//				}
//			};
//			contsPanel.loadComponent(searchTask.get(nowFocusIndex), finder);
//		}
		
	}
	
	public void writeData(String domain, String directory, String uuid, boolean append){
		
		Map<String, String> data = db.get(domain).get(directory).get(uuid);
		String path = ROOTPATH + domain + File.separator + directory + File.separator;
		String fileExec = FILEDBEXTN;
		String fileName = "";
		String writeText = "";
		
		if("task".equals(data.get("type"))){
			fileName = data.get("name").substring(0, data.get("name").lastIndexOf("."));
			for(Map.Entry<String, String> d : data.entrySet()){
				writeText += d.getKey();
				writeText += d.getValue();
				writeText += WRAP + "\n";
			}
			
			// 업무 db는 항상 insert
			append = false;
		}else {
			fileName = "INFO";
			if(append){
				writeText = "\n[File]\n";
				for(Map.Entry<String, String> d : data.entrySet()){
					writeText += d.getKey();
					writeText += d.getValue();
					writeText += WRAP + "\n";
				}
			}else {
				for(Map.Entry<String, Map<String, String>> f : db.get(domain).get(directory).entrySet()){
					writeText = "\n[File]\n";
					for(Map.Entry<String, String> d : f.getValue().entrySet()){
						writeText += d.getKey();
						writeText += d.getValue();
						writeText += WRAP + "\n";
					}
				}
			}
		}
		
		TextFileWriter writer = new TextFileWriter(path, fileName, fileExec, writeText);
		int result = writer.write(append);
		
		if( result < 0 ){
			String popStr = "";
			
			if(append){
				popStr = "내용 추가에 실패 했습니다.";
			}else {
				popStr = "파일 추가에 실패 했습니다.";
			}
			
			Main.alertPop(popStr);
			
		}
		
	}
	
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 이벤트
	 * @Dcpt   : 키 입력 , 달력 선택
	 */	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		if(type.equals("keyPress") && "search".equals(obj.getEvtName())){
			keyPressEvt((KeyEvent) objects[0]);
		}
		
//		String threadName = "TaskManagerService";
//		runThread(threadName, false);

	}

	public void keyPressEvt(KeyEvent e){

	}

	
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 셋팅 함수
	 * @Dcpt   : 도메인 콤보박스, 기본 파일정보
	 */
	// 프로젝트 콤보박스 셋팅
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setDomainBoxItems(){
		domains.clear();
		domains.add(this.comboDefaultDomain);
		for(Entry<String, ?> key : db.entrySet()) domains.add(key.getKey());
		comboBoxes.get(0).setModel(new DefaultComboBoxModel(domains.toArray()));
	}
	
	// 파일 기본 정보 셋팅
	public Map<String, String> getFileInfoBase(String name, boolean isTaskType){
		Map<String, String> vo = new HashMap<>();
		
		vo.put("name", name);
		vo.put("color", "WHITE");
		vo.put("tag", "{empty}");
		vo.put("date", CalendarViewer.getNowDate("yyyy/MM/dd HH:mm:ss"));
		vo.put("create", CalendarViewer.getNowDate("yyyy/MM/dd HH:mm:ss"));
		vo.put("comple", "N");
		vo.put("uuid", UUID.randomUUID()+"");
		
		if(isTaskType){
			vo.put("tasks", "{}");
			vo.put("files", "{}");
		}
		
		return vo;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 선택 함수
	 * @Dcpt   : 스레드
	 */
	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {

			}
		};
	}

	public boolean navigation(String target, String keyword){
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
	
	public boolean rangeDate(String target, String keyword){
		boolean find = false;

		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy/MM/dd");
		String[] betweenTarger = target.split("\\~");
		
		Date fStart = null;
		Date fEnd = null;
		
		try {
			fStart = transFormat.parse(betweenTarger[0]);
			fEnd = transFormat.parse(betweenTarger[1]);
		} catch (ParseException e1) {
		}
		
		if(keyword.contains("~")){
			String[] betweenKeywords = keyword.split("\\~");
			try {
				Date start = transFormat.parse(betweenKeywords[0]);
				Date end = transFormat.parse(betweenKeywords[1]);
				if(fStart.compareTo(end)>=0 && fEnd.compareTo(start)<=0){
					find = true;
				}
			} catch (Exception e) {}
		}else {
			try{
				Date start = transFormat.parse(keyword);
				if(fStart.compareTo(start)<=0 && fEnd.compareTo(start)>=0){
					find = true;
				}
			}catch(Exception e){}
		}
		
		return find;
	}


}








