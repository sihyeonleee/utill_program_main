package service.write; 

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import comm.CustomThread;
import comm.Progress;
import comm.fileio.ExcelFileReader;
import comm.fileio.ExcelFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class XcelFilesMergeService extends Service{ 

	JLabel defaultLabel;
	
	List<File> files = new ArrayList<>();
	List<JLabel> labels = new ArrayList<>();
	
	String[] select = {"단순병합 : 같은양식", "추가병합 : 다른양식", "선택병합 : 새로운양식", "명령병합 : 사용자정의"};
	
	String selectedItem = select[0];
	
	
	
	
	
	public XcelFilesMergeService(){ 

		// 프레임 설정
		width = 300;
		height = 300;
		layout = FrameObj.LAYOUT_GRIDBAG;

		CompObj select = new CompObj();
		select.setSelectItems(this.select);
		select.setEvtName("select");
		select.setName("병합");
		select.setType(CompObj.TYPE_COMBOBOX);
		select.setEventType(CompObj.EVENT_ACTION);
		select.setGridSize(1, 1);
		select.setGridWeight(1, 1);
		select.setGridPosition(0, 0);
		componentObjs.add(select);
		
		CompObj btn = new CompObj();
		btn.setEvtName("btn");
		btn.setName("병합");
		btn.setType(CompObj.TYPE_BUTTON);
		btn.setEventType(CompObj.EVENT_ACTION);
		btn.setGridSize(1, 1);
		btn.setGridWeight(1, 1);
		btn.setGridPosition(1, 0);
		//btn.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn);
		
		CompObj init = new CompObj();
		init.setEvtName("init");
		init.setName("리셋");
		init.setType(CompObj.TYPE_BUTTON);
		init.setEventType(CompObj.EVENT_ACTION);
		init.setGridSize(1, 1);
		init.setGridWeight(1, 1);
		init.setGridPosition(2, 0);
		//btn.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(init);
		
		CompObj panel = new CompObj();
		panel.setEvtName("panel");
		panel.setType(CompObj.TYPE_PANEL);
		panel.setScrollAt(true);
		panel.setLayout(CompObj.LAYOUT_GRID, 0, 1);
		panel.setEventType(CompObj.EVENT_DRAGDROP);
		panel.setGridSize(3, 1);
		panel.setGridWeight(10, 10);
		panel.setGridPosition(0, 1);
		panel.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(panel);
		
	}

	@Override
	public void doShow(String name) {

		if(defaultLabel == null) init();
		
		beforeDoShow();
		
		super.doShow(name);

		afterDoShow();
		
	}
	
	public void init(){
		defaultLabel = new JLabel("파일을 드래그 하세요.", SwingConstants.CENTER);
		defaultLabel.setOpaque(true);
		defaultLabel.setBackground(Color.GRAY);
		panels.get(0).add(defaultLabel);
	}
	
	public void beforeDoShow(){
		
	}
	
	public void afterDoShow(){
		
	}
	
	
	
	
	
	
	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {
		
		if(type.equals("change") && "panel".equals(obj.getEvtName())){
			// 파일 추가 ( 1개이상 )
			addFiles( (File[]) objects );
		}else if(type.equals("click") && "btn".equals(obj.getEvtName())){
			// 변환 시작
			doRun();
		}else if(type.equals("click") && "init".equals(obj.getEvtName())){
			// 초기화
			reset();
		}else if(type.equals("change") && "select".equals(obj.getEvtName()) ){
			// 병합 방식
			changeSelect();
		}
		
	}

	public void changeSelect(){
		selectedItem = (String) comboBoxes.get(0).getSelectedItem();
	}
	
	public void addFiles(File[] files){
		
		defaultLabel.setVisible(false);
		
		this.labels.clear();
		this.panels.get(0).removeAll();
		
		this.files.addAll(new ArrayList<File>(Arrays.asList(files)));
		
		File[] list = this.files.toArray(new File[this.files.size()]);
		
		Arrays.sort(list, Comparator.comparingLong(File::lastModified).reversed());
		this.files = new ArrayList<File>(Arrays.asList(list));
		
		for(File f : this.files) {
			JLabel l = new JLabel(f.getName(), SwingConstants.CENTER);
			panels.get(0).add(l);
			panels.get(0).revalidate();
			this.labels.add(l);
		}
		
	}
	
	public void reset(){
		
		panels.get(0).removeAll();
		labels.clear();
		files.clear();
		
		panels.get(0).add(defaultLabel);
		defaultLabel.setVisible(true);
		
	}
	
	public void doRun(){
		String threadName = "ExcelFilesMergeService";
		runThread(threadName, false);
	}
	
	
	
	
	
	
	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			
			@Override
			public void run() {
				
				doConvert();
				
			}
			
		};
	}
	
	public void doConvert(){
		
		Map<String, Map<Object, List<List<Object>>>> convertData = null;
		
		// 변환 방식에 따라 변환 수행
		if(this.select[0].equals(selectedItem)){
			// 경우 1 : 같은 양식, 단순 병합 >> 충돌 감지
			convertData = merge();
		}else if(this.select[1].equals(selectedItem)){
			// 경우 2 : 다른 양식, 추가 병합 >> 순번 선택
			convertData = append();
		}else if(this.select[2].equals(selectedItem)){
			// 경우 3 : 제한 없음, 선택 병합 >> 
			convertData = chcConvert();
		}else if(this.select[3].equals(selectedItem)){
			// 경우 3 : 제한 없음, 사용자 정의 병합 >>
			convertData = cmdConvert();
		}
		
		if(convertData != null){
			ExcelFileWriter writer = new ExcelFileWriter();
			//////////////////
		}
		
	}
	
	public Map<String, Map<Object, List<List<Object>>>> merge(){
		
		String[] collisionStr = {"마지막 수정일자", "파일 직접 선택", "충돌 데이터 직접 선택"};
		String collision = (String) Main.selectPop("선택", "데이터 충돌 처리방식 우선순위 선택", "", collisionStr);
		if(collision == null) return null;
		
		Map<String, Map<Object, List<List<Object>>>> filtSheet = showSheetChoicePopup(readFileListData());
		
		Map<String, Map<Object, List<List<Object>>>> convertDatas = new HashMap<>();
		
		String fileName = storeFileName();
		
		if(collision.equals(collisionStr[0])){
			// 마지막 수정일자
			for(Entry<String, Map<Object, List<List<Object>>>> map : filtSheet.entrySet()){
				
			}
		}else if(collision.equals(collisionStr[1])){
			// 파일 직접 선택
			
		}else if(collision.equals(collisionStr[2])){
			// 충돌 데이터 직접 선택
			
		}
		
		return convertDatas;
		
	}
	
	public Map<String, Map<Object, List<List<Object>>>> append(){
		
		Map<String, Map<Object, List<List<Object>>>> filtSheet = showSheetChoicePopup(readFileListData());
		
		Map<String, Map<Object, List<List<Object>>>> convertDatas = new HashMap<>();
		
		return convertDatas;
		
	}
	
	public Map<String, Map<Object, List<List<Object>>>> chcConvert(){
	
		Map<String, Map<Object, List<List<Object>>>> filtSheet = showSheetChoicePopup(readFileListData());
		
		Map<String, Map<Object, List<List<Object>>>> convertDatas = new HashMap<>();
		
		return convertDatas;
		
	}
	
	public Map<String, Map<Object, List<List<Object>>>> cmdConvert(){
		
		Map<String, Map<Object, List<List<Object>>>> filtSheet = showSheetChoicePopup(readFileListData());
		
		Map<String, Map<Object, List<List<Object>>>> convertDatas = new HashMap<>();
		
		return convertDatas;
		
	}
	
	
	
	
	
	public Map<String, Map<Object, List<List<Object>>>> showSheetChoicePopup(Map<String, Map<Object, List<List<Object>>>> datas){
		
		Map<String, Map<Object, List<List<Object>>>> filtDatas = new HashMap<>();
		
		JPanel contain = new JPanel(new GridLayout(1, 0, 10, 0));
		
		for(Entry<String, Map<Object, List<List<Object>>>> file : datas.entrySet()){
			String fileName = file.getKey();
			List<String> keys = getKeyList(file.getValue());
			contain.add(getSheetPanelUI(fileName, keys));
		}
		
		int result = Main.confirmPop("병합 시트 선택", contain);
		
		if(result < 0) return null;
		
		Map<String, List<String>> selectedList = getSelectSheetList(contain);
		
		for(Entry<String, List<String>> map : selectedList.entrySet()){
			
			Map<Object, List<List<Object>>> filtSheets = new HashMap<>();
			
			for(String sheet : map.getValue()){
				filtSheets.put(sheet, datas.get(map.getKey()).get(sheet));
			}
			filtDatas.put(map.getKey(), filtSheets);
		}
		
		return filtDatas;
		
	}
	
	public JPanel getSheetPanelUI(String fileName, List<String> keys){
		JPanel pane = new JPanel(new GridLayout(0, 1));
		pane.setBorder(new LineBorder(Color.GRAY));
		pane.add(new JLabel(fileName));
		pane.add(getSheetLabelUI("전체"));
		for(String key : keys) pane.add(getSheetLabelUI(key));
		
		return pane;
	}
	
	public JLabel getSheetLabelUI(String key){
		
		JLabel keyLabel = new JLabel(key);
		keyLabel.setOpaque(true);
		keyLabel.setBackground(Color.WHITE);
		keyLabel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getComponent().getBackground() == Color.GRAY){
					e.getComponent().setBackground(Color.WHITE);
					JPanel contain = (JPanel) e.getComponent().getParent();
					if(!((JLabel)e.getComponent()).getText().equals("전체"))
						contain.getComponent(1).setBackground(Color.WHITE);
				}else {
					e.getComponent().setBackground(Color.GRAY);
					JPanel contain = (JPanel) e.getComponent().getParent();
					if(((JLabel)e.getComponent()).getText().equals("전체"))
						for(int i=2; i<contain.getComponentCount();i++) 
							contain.getComponent(i).setBackground(Color.GRAY);
				}
			}
		});
		
		return keyLabel;
		
	}
	
	public List<String> getKeyList(Map<Object, ?> map){
		List<String> objs = new ArrayList<>();
		for(Entry<Object, ?> m : map.entrySet()) objs.add((String) m.getKey());
		return objs;
	}
	
	public Map<String, List<String>> getSelectSheetList(JPanel contain){
		
		Map<String, List<String>> list = new HashMap<>();
		
		// 선택 된 시트 셋팅
		for(int i=0; i<contain.getComponentCount(); i++){
			JPanel panel = (JPanel) contain.getComponent(i);
			String fileNm = ((JLabel) panel.getComponent(0)).getText();
			
			for(int j=2; j<panel.getComponentCount(); j++){
				JLabel label = (JLabel) panel.getComponent(j);
				if(label.getBackground() == Color.GRAY){
					if(!list.containsKey(fileNm)) list.put(fileNm, new ArrayList<>());
					list.get(fileNm).add(label.getText());
				}
			}
		}
		
		return list;
		
	}
	
	public String storeFileName(){
		
		return Main.inputPop("파일 이름", "저장 파일 이름 지정");
		
	}
	
	
	
	
	public Map<String, Map<Object, List<List<Object>>>> readFileListData(){
		
		Map<String, Map<Object, List<List<Object>>>> datas = new HashMap<>();
		
		for(int i=0; i<files.size(); i++){
			
			File f = files.get(i);

			Map<Object, List<List<Object>>> data = readFileData(i);
			
			datas.put(f.getName(), data);
			
		}
		
		return datas;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Object, List<List<Object>>> readFileData(int index){
		Map<Object, List<List<Object>>> data = new HashMap<>();
		
		File file = files.get(index);
		JLabel label = labels.get(index);
		
		ExcelFileReader reader = new ExcelFileReader(file.getPath());
		reader.setReadAsName(true);
		reader.setProgress(new Progress(){
			@Override
			public void nowProc(int total, int now) {
				
				if(now == total) label.setText(file.getName()); 
				else label.setText(file.getName() + " :: " + now + "/" + total);
			}
		});
		
		Map<String, Object> result = reader.doRead();
		
		if("INVALIDEXEC".equals(result.get("result"))) {
			Main.alertPop("잘못된 파일입니다." + reader.getName());
			reset();
			return null;
		}
		
		return (Map<Object, List<List<Object>>>) result.get("data");
	}
	
	
}
