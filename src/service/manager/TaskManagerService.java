 package service.manager; 

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import comm.CustomThread;
import comm.fileio.FileChooser;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;
import service.manager.task.pannel.TaskCndPanel;
import service.manager.task.pannel.TaskDtlPanel;
import service.manager.task.pannel.TaskFilePanel;
import service.manager.task.pannel.TaskHelpPanel;
import service.manager.task.pannel.TaskTagPanel;
import service.manager.task.service.TaskSqlMapper;  

public class TaskManagerService extends Service { 

	// Mapper Service
	TaskSqlMapper mapper = new TaskSqlMapper();
	
	// 경로
	public static final String SERVICEDIR = "task";
	public static final String FILEDIRNAME = "reference";
	
	// 콤보박스 기본값
	private static final String DFLTDOMAIN = "범위"; // 프로젝트
	private static final String DFLTFILT = "필터"; // 검색 필터
	private static final String DFLTCOLOR = "색상"; // 색상
	private static final String DFLTSORT = "정렬"; // 정렬
	
	// 패널 영역
	private TaskCndPanel caldrPanel;
	private TaskDtlPanel contsPanel; 
	private TaskTagPanel tasksPanel;
	private TaskFilePanel filesPanel;
	private TaskHelpPanel helpPanel;
	
	// 검색 리스트
	private int nowFocusIndex = 0;
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : Main UI Setting
	 * @Dcpt   : Initial Method
	 */
	public TaskManagerService(){ 

		// 프레임 설정
		width = 620;
		height = 520;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj search1 = new CompObj();
		search1.setType(CompObj.TYPE_INPUT);
		search1.setEvtName("search");
		search1.setArrangeType(GridBagConstraints.BOTH);
		search1.setEventType(CompObj.EVENT_TYPING, CompObj.EVENT_KEYTYPE);
		search1.setGridPosition(0, 0);
		search1.setGridSize(5, 1);
		componentObjs.add(search1);
		
		CompObj line1 = new CompObj();
		line1.setType(CompObj.TYPE_OUTPUT);
		line1.setArrangeType(GridBagConstraints.BOTH);
		line1.setGridPosition(0, 1);
		line1.setGridSize(5, 1);
		componentObjs.add(line1);
		
		CompObj addFolder = new CompObj();
		addFolder.setName("추가");
		addFolder.setEvtName("add");
		addFolder.setType(CompObj.TYPE_BUTTON);
		addFolder.setEventType(CompObj.EVENT_ACTION);
		addFolder.setArrangeType(GridBagConstraints.BOTH);
		addFolder.setGridPosition(0, 2);
		addFolder.setGridSize(1, 1);
		componentObjs.add(addFolder);
		
		CompObj modiFolder = new CompObj();
		modiFolder.setName("수정");
		modiFolder.setEvtName("modify");
		modiFolder.setType(CompObj.TYPE_BUTTON);
		modiFolder.setEventType(CompObj.EVENT_ACTION);
		modiFolder.setArrangeType(GridBagConstraints.BOTH);
		modiFolder.setGridPosition(1, 2);
		modiFolder.setGridSize(1, 1);
		componentObjs.add(modiFolder);
		
		CompObj delFolder = new CompObj();
		delFolder.setName("삭제");
		delFolder.setEvtName("delete");
		delFolder.setType(CompObj.TYPE_BUTTON);
		delFolder.setEventType(CompObj.EVENT_ACTION);
		delFolder.setArrangeType(GridBagConstraints.BOTH);
		delFolder.setGridPosition(2, 2);
		delFolder.setGridSize(1, 1);
		componentObjs.add(delFolder);

		CompObj calendar = new CompObj();
		calendar.setName("달력");
		calendar.setEvtName("calendar");
		calendar.setType(CompObj.TYPE_BUTTON);
		calendar.setEventType(CompObj.EVENT_ACTION);
		calendar.setArrangeType(GridBagConstraints.BOTH);
		calendar.setGridPosition(3, 2);
		calendar.setGridSize(1, 1);
		componentObjs.add(calendar);
		
		CompObj select1 = new CompObj();
		select1.setEvtName("domain");
		select1.setType(CompObj.TYPE_COMBOBOX);
		select1.setSelectItems(new String[]{DFLTDOMAIN});
		select1.setEventType(CompObj.EVENT_ACTION);
		select1.setArrangeType(GridBagConstraints.BOTH);
		select1.setGridPosition(0, 3);
		select1.setGridSize(1, 1);
		componentObjs.add(select1);
		
		CompObj select2 = new CompObj();
		select2.setEvtName("filter");
		select2.setType(CompObj.TYPE_COMBOBOX);
		select2.setSelectItems(new String[]{DFLTFILT});
		select2.setEventType(CompObj.EVENT_ACTION);
		select2.setArrangeType(GridBagConstraints.BOTH);
		select2.setGridPosition(1, 3);
		select2.setGridSize(1, 1);
		componentObjs.add(select2);
		
		CompObj select3 = new CompObj();
		select3.setEvtName("color");
		select3.setType(CompObj.TYPE_COMBOBOX);
		select3.setSelectItems(new String[]{DFLTCOLOR});
		select3.setEventType(CompObj.EVENT_ACTION);
		select3.setArrangeType(GridBagConstraints.BOTH);
		select3.setGridPosition(2, 3);
		select3.setGridSize(1, 1);
		componentObjs.add(select3);
		
		CompObj select4 = new CompObj();
		select4.setEvtName("sort");
		select4.setType(CompObj.TYPE_COMBOBOX);
		select4.setSelectItems(new String[]{DFLTSORT});
		select4.setEventType(CompObj.EVENT_ACTION);
		select4.setArrangeType(GridBagConstraints.BOTH);
		select4.setGridPosition(3, 3);
		select4.setGridSize(1, 1);
		componentObjs.add(select4);
		
		CompObj line2 = new CompObj();
		line2.setType(CompObj.TYPE_OUTPUT);
		line2.setArrangeType(GridBagConstraints.BOTH);
		line2.setGridPosition(0, 4);
		line2.setGridSize(5, 1);
		componentObjs.add(line2);
		
		CompObj mainPanel = new CompObj();
		mainPanel.setType(CompObj.TYPE_TABPANEL);
		mainPanel.setScrollAt(false);
		mainPanel.setGridSize(5, 1);
		mainPanel.setGridWeight(30, 100);
		mainPanel.setGridPosition(0, 5);
		mainPanel.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(mainPanel);
		
		CompObj line3 = new CompObj();
		line3.setType(CompObj.TYPE_OUTPUT);
		line3.setArrangeType(GridBagConstraints.BOTH);
		line3.setGridPosition(6, 0);
		line3.setGridSize(1, 6);
		componentObjs.add(line3);
		
		CompObj contentPanel = new CompObj();
		contentPanel.setType(CompObj.TYPE_PANEL);
		contentPanel.setLayout(CompObj.LAYOUT_BORDER);
		contentPanel.setScrollAt(false);
		contentPanel.setGridSize(1, 10);
		contentPanel.setGridWeight(100, 1);
		contentPanel.setGridPosition(7, 0);
		contentPanel.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(contentPanel);
		
	}

	@Override
	public void doShow(String name) {
		if(((JTabbedPane) this.panels.get(0)).getTabCount() < 1) init();
		
		beforeDoShow();
		super.doShow(name);
		afterDoShow();
		
	}
	
	public void init(){
		
		inputs.get(0).setBorder(new BevelBorder(BevelBorder.LOWERED));
		outputs.get(0).setBorder(new EmptyBorder(1, 0, 1, 0)); // Line
		buttons.get(0).setOpaque(true);
		buttons.get(0).setForeground(new Color(55, 188, 125, 255));
		buttons.get(0).setBackground(new Color(255, 255, 255, 255));
		buttons.get(1).setOpaque(true);
		buttons.get(1).setForeground(new Color(125, 188, 55, 255));
		buttons.get(1).setBackground(new Color(255, 255, 255, 255));
		buttons.get(2).setOpaque(true);
		buttons.get(2).setForeground(new Color(188, 55, 125, 255));
		buttons.get(2).setBackground(new Color(255, 255, 255, 255));
		buttons.get(3).setOpaque(true);
		buttons.get(3).setForeground(new Color(188, 125, 55, 255));
		buttons.get(3).setBackground(new Color(255, 255, 255, 255));
		outputs.get(1).setBorder(new EmptyBorder(1, 0, 1, 0)); // Line
		outputs.get(2).setBorder(new EmptyBorder(0, 3, 0, 3)); // Line
		
		this.tasksPanel = new TaskTagPanel();
		this.caldrPanel = new TaskCndPanel();
		this.contsPanel = new TaskDtlPanel();
		this.filesPanel = new TaskFilePanel();
		this.helpPanel = new TaskHelpPanel();
		
		tasksPanel.setBackground(Color.WHITE);
		caldrPanel.setBackground(Color.WHITE);
		filesPanel.setBackground(Color.WHITE);
		contsPanel.setBackground(Color.WHITE);
		helpPanel.setBackground(Color.WHITE);
		
		JTabbedPane tabPanel = (JTabbedPane) panels.get(0);
		tabPanel.addTab("목록", this.tasksPanel);
		tabPanel.addTab("달력", this.caldrPanel);
		tabPanel.addTab("파일", this.filesPanel);
		tabPanel.addTab("도움말", this.helpPanel);
		
		panels.get(1).add(this.contsPanel);
		
	}
	
	public void beforeDoShow(){
		
		setCommonCodeBoxItems(DFLTFILT, 1);
		setCommonCodeBoxItems(DFLTCOLOR, 2);
		setCommonCodeBoxItems(DFLTSORT, 3);
		setDomainBoxItems();
		
	}
	
	public void afterDoShow(){
		inputs.get(0).requestFocus();
	}
	
	
	
	
	
	
	public void searchKeyword(){
		
		nowFocusIndex = 0;
		
		String keyword = inputs.get(0).getText();
		String domain = ((String) comboBoxes.get(0).getSelectedItem()).equals(DFLTDOMAIN) ? null : (String) comboBoxes.get(0).getSelectedItem();
		String filter = ((String) comboBoxes.get(1).getSelectedItem()).equals(DFLTFILT) ? null : (String) comboBoxes.get(1).getSelectedItem();
		String color = ((String) comboBoxes.get(2).getSelectedItem()).equals(DFLTCOLOR) ? null : (String) comboBoxes.get(2).getSelectedItem();
		String sort = ((String) comboBoxes.get(3).getSelectedItem()).equals(DFLTSORT) ? null : (String) comboBoxes.get(3).getSelectedItem();
		
		Map<String, Object> param = new HashMap<>();
		
		param.put("keyword", keyword);
		param.put("domain", domain);
		if(filter != null) param.put("filter", mapper.selectCommonCodeInfo(DFLTFILT, filter).get("codeNo"));
		if(color != null) param.put("color", mapper.selectCommonCodeInfo(DFLTCOLOR, color).get("codeCn"));
		if(sort != null) param.put("sort", mapper.selectCommonCodeInfo(DFLTSORT, sort).get("codeNo"));
		
		// 태그목록
		List<Map<String, Object>> searchList = mapper.selectSearchKeyword(param);
		
		// 파일목록 ( 태그없는 파일은 존재하지 않는다 )
		List<Map<String, Object>> searchFileList = mapper.selectSearchKeywordFile(param);
		
		loadCmptTagList(searchList);
		
		loadCmptFileList(searchFileList);
		
		if(searchList.size() > nowFocusIndex){
			
			Map<String, Object> map = searchList.get(nowFocusIndex);
			
			List<Map<String, Object>> cntnList = mapper.selectTaskSchdList(map);
			
			List<Map<String, Object>> fileList = mapper.selectTaskTagFileList(map);
			
			map.put("cntnList", cntnList);
			
			map.put("fileList", fileList);
			
			loadCmptCntnts(map);
			
		}
		
	}
	
	public void loadCmptTagList(List<Map<String, Object>> searchList){
		tasksPanel.loadComponent(searchList);
		caldrPanel.loadComponent(searchList);
	}
	
	public void loadCmptFileList(List<Map<String, Object>> searchList){
		filesPanel.loadComponent(searchList);
	}
	
	public void loadCmptCntnts(Map<String, Object> map){
		contsPanel.loadComponent(map);
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
		}else if(type.equals("click") && "add".equals(obj.getEvtName())){
			addDomain();
		}else if(type.equals("click") && "delete".equals(obj.getEvtName())){
			
		}else if(type.equals("change") && "domain".equals(obj.getEvtName())){
			searchKeyword();
		}else if(type.equals("change") && "filter".equals(obj.getEvtName())){
			
		}else if(type.equals("change") && "color".equals(obj.getEvtName())){
			
		}else if(type.equals("change") && "sort".equals(obj.getEvtName())){
			
		}
		
//		String threadName = "TaskManagerService";
//		runThread(threadName, false);

	}

	public void keyPressEvt(KeyEvent e){
		searchKeyword();
	}
	
	public void addDomain(){
		// 프로젝트 구분 추가시
		String domainName = Main.inputPop("프로젝트 명을 입력해주세요.");
		
		if( domainName == null ){
			return;
		} else if(domainName.trim().equals("")){
			Main.alertPop("올바른 파일명을 입력해주세요.");
			return;
		}
		
		int result = domainMkdir(domainName);
		
		
		if(result > 0){
			Main.alertPop("프로젝트 구분이 추가 되었습니다.");
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("domainNm", domainName);
			mapper.insertTaskDomain(param);
			
			setDomainBoxItems();
			
			return;
		}else if(result == -1){
			Main.alertPop("올바른 파일명을 입력해주세요.");
			return;
		}else if(result == -2){
			Main.alertPop("같은 이름의 프로젝트가 존재합니다.");
			return;
		}
	}
	
	
	
	
	
	
	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 셋팅 함수
	 * @Dcpt   : 도메인 콤보박스, 기본 파일정보
	 */
	// 프로젝트 콤보박스 셋팅
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setDomainBoxItems(){
		
		Map<String, Object> param = new HashMap<>();
		
		List<Map<String, Object>> domainList = mapper.selectTaskList(param);
		
		List<String> domains = new ArrayList<>();
		domains.add(this.DFLTDOMAIN);
		for(Map<String, Object> map : domainList){
			String domainNm = (String) map.get("domainNm");
			domainMkdir(domainNm);
			domains.add(domainNm);
		}
		
		comboBoxes.get(0).setModel(new DefaultComboBoxModel(domains.toArray()));
		
		searchKeyword();
		
	}
	
	public void setCommonCodeBoxItems(String codeNm, int index){
		
		Map<String, Object> param = new HashMap<>();
		
		param.put("codeNm", codeNm);
		
		List<Map<String, Object>> comList = mapper.selectCommonCode(param);

		List<String> list = new ArrayList<>();
		
		// 리스트의 처음은 기본 이름
		list.add(codeNm);
		
		for(Map<String, Object> map : comList){
			String codeCn = (String) map.get("codeCn");
			list.add(codeCn);
		}
		
		comboBoxes.get(index).setModel(new DefaultComboBoxModel(list.toArray()));
		
	}
	
	// 도메인 폴더 생성
	public int domainMkdir(String fileName){
		
		return FileChooser.mkdirs(comm.Path.ROOTPATH + SERVICEDIR + File.separator + FILEDIRNAME + File.separator + fileName);
		
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








