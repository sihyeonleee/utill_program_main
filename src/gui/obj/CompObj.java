package gui.obj;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.List;

import javax.swing.BoxLayout;

public class CompObj {
	
	
	public static final int TYPE_INPUT = 1; 
	public static final int TYPE_OUTPUT = 2; 
	public static final int TYPE_BUTTON = 3; 
	public static final int TYPE_COMBOBOX = 4; 
	public static final int TYPE_TEXTAREA = 5; 
	public static final int TYPE_CHECKBOX = 6; 
	public static final int TYPE_PANEL = 7;
	public static final int TYPE_TABPANEL = 8;
	
	public static final int LAYOUT_GRID = 1;
	public static final int LAYOUT_FLOW = 2;
	public static final int LAYOUT_BORDER = 3;
	public static final int LAYOUT_GRIDBAG = 4;
	public static final int LAYOUT_BOX = 5;
	public static final int LAYOUT_CARD = 6;
	
	public static final int EVENT_ACTION = 1;
	public static final int EVENT_TYPING = 2;
	public static final int EVENT_KEYTYPE = 3;
	public static final int EVENT_DRAGDROP = 4;
	
	private LayoutManager layout;
	
	private String evtName = "";
	private Integer index;
	private String name = "";
	private String names[];
	private String msg = "";
	private String hint = "";
	private String selectItems[] = {};
	private String popupItems[] = {};
	private Integer type = 1;
	private Integer[] eventType = {};
	private boolean enabled = true;
	private boolean focus = true;
	private boolean selected = true;
	
	private Integer width;
	private Integer height;
	private Integer positionX;
	private Integer positionY;
	private Integer charX = 15;
	private Integer charY = 1;
	
	private Integer gridX = 1; 		// 그리드를 배치할 x좌표
	private Integer gridY = 1;		// 그리드를 배치할 y좌표
	private Integer gridWidth = 1;  // 그리드의 가로 크기
	private Integer gridHeight = 1; // 그리드의 세로 크기
	private Integer weightX = 1;	// 그리드의 크기 x좌표 비율
	private Integer weightY = 1;	// 그리드의 크기 y좌표 비율
	private Integer ipadX = 1;	// 그리드의 크기 x좌표 비율
	private Integer ipadY = 1;	// 그리드의 크기 y좌표 비율
	
	private boolean scrollAt;
	
	/* GridBagConstraints.? */
	/* BorderLayout.? */
	/* FlowLayout.? */
	private Integer arrangeType;
	
	
	public void setGridPosition(int x, int y){
		this.gridX = x;
		this.gridY = y;
	}
	
	public void setGridWeight(int x, int y){
		this.weightX = x;
		this.weightY = y;
	}
	
	public void setGridSize(int w, int h){
		this.gridWidth = w;
		this.gridHeight = h;
	}
	
	public void setGridPad(int x, int y){
		this.ipadX = x;
		this.ipadY = y;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public String[] getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(List<String> selectItems) {
		this.selectItems = selectItems.toArray(new String[0]);
	}
	
	public void setSelectItems(String[] selectItems) {
		this.selectItems = selectItems;
	}
	
	public String[] getPopupItems() {
		return popupItems;
	}

	public void setPopupItems(String[] popupItems) {
		this.popupItems = popupItems;
	}

	public LayoutManager getLayout() {
		return layout;
	}

	public void setLayout(int type, Object...param) {
		
			switch(type){
			case LAYOUT_GRID :
				try{
					layout = new GridLayout((Integer)param[0], (Integer)param[1], (Integer)param[2], (Integer)param[3]);
				}catch(Exception e){
					layout = new GridLayout((Integer)param[0], (Integer)param[1]);
				}
				break;
			case LAYOUT_FLOW :
				layout = new FlowLayout();
				((FlowLayout) layout).setAlignment(FlowLayout.CENTER);
				break;
			case LAYOUT_BORDER :
				layout = new BorderLayout();
				break;
			case LAYOUT_GRIDBAG :
				layout = new GridBagLayout();
				break;
			case LAYOUT_BOX :
				layout = new BoxLayout(null, (Integer)param[0]);
				break;
			case LAYOUT_CARD :
				layout = new CardLayout();
				break;
			default : 
				layout = new FlowLayout();
				((FlowLayout) layout).setAlignment(FlowLayout.CENTER);
				break;
		}
	}

	public Integer[] getEventType() {
		return eventType;
	}

	public void setEventType(Integer...eventType) {
		this.eventType = eventType;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	public Integer getCharX() {
		return charX;
	}

	public void setCharX(Integer charX) {
		this.charX = charX;
	}

	public Integer getCharY() {
		return charY;
	}

	public void setCharY(Integer charY) {
		this.charY = charY;
	}

	public Integer getGridX() {
		return gridX;
	}

	public void setGridX(Integer gridX) {
		this.gridX = gridX;
	}

	public Integer getGridY() {
		return gridY;
	}

	public void setGridY(Integer gridY) {
		this.gridY = gridY;
	}
	
	public Integer getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(Integer gridWidth) {
		this.gridWidth = gridWidth;
	}

	public Integer getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(Integer gridHeight) {
		this.gridHeight = gridHeight;
	}

	public Integer getWeightX() {
		return weightX;
	}

	public void setWeightX(Integer weightX) {
		this.weightX = weightX;
	}

	public Integer getWeightY() {
		return weightY;
	}

	public void setWeightY(Integer weightY) {
		this.weightY = weightY;
	}
	
	public Integer getIpadX() {
		return ipadX;
	}

	public void setIpadX(Integer ipadX) {
		this.ipadX = ipadX;
	}

	public Integer getIpadY() {
		return ipadY;
	}

	public void setIpadY(Integer ipadY) {
		this.ipadY = ipadY;
	}

	public boolean isScrollAt() {
		return scrollAt;
	}

	public void setScrollAt(boolean scrollAt) {
		this.scrollAt = scrollAt;
	}

	public Integer getArrangeType() {
		return arrangeType;
	}

	public void setArrangeType(Integer arrangeType) {
		this.arrangeType = arrangeType;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public String getEvtName() {
		return evtName;
	}

	public void setEvtName(String evtName) {
		this.evtName = evtName;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	
	
	
	
}
