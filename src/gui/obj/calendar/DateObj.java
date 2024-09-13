package gui.obj.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class DateObj extends JLabel{

	/**
	 * @Date   : 2019. 12. 16.
	 * @Role   : 달력 DAY 오브젝트
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private EventListener eventListener;
	
	private boolean clickState = false;

	private boolean multiSelectMode = false;
	
	private Calendar cal;
	
	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
	
	private String ymd;
	private int year;
	private int month;
	private int day;
	
	private String msg;
	
	private Color backClickColor = new Color(110, 110, 150, 255);
	private Color backColor = new Color(255, 255, 255, 255);
	private Color foreColor = new Color(0, 0, 0, 255);
	
	private BevelBorder focusBd = new BevelBorder(BevelBorder.RAISED);
	private EtchedBorder unFocusBd = new EtchedBorder();
	private BevelBorder selectBd = new BevelBorder(BevelBorder.LOWERED, Color.WHITE, Color.GRAY);
	private EtchedBorder accentBd = new EtchedBorder(Color.CYAN, Color.WHITE);
	
	public DateObj(String msg){
		
		this.msg = msg;
		init();
		
	}
	
	public DateObj(Calendar cal){
		
		this.cal = cal;
		init();
		
	}
	
	public DateObj(Calendar cal, EventListener eventListener){
		
		this.eventListener = eventListener;
		this.cal = cal;
		init();
		
	}
	
	public DateObj(Calendar cal, EventListener eventListener, boolean multiSelectMode){
		
		this.multiSelectMode = multiSelectMode;
		this.eventListener = eventListener;
		this.cal = cal;
		init();
		
	}
	
	public void init(){
		
		final DateObj obj = this;
		
		if(this.cal != null){
			this.year = this.cal.get(Calendar.YEAR);
			this.month = this.cal.get(Calendar.MONTH);
			this.day = this.cal.get(Calendar.DATE);
			this.ymd = fmt.format(cal.getTime());
			this.setText(this.cal.get(Calendar.DATE)+"");
		}else {
			this.setText(this.msg);
		}
		
		this.setFont(new Font("바탕", Font.PLAIN, 12));
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setBorder(unFocusBd);
		this.setBackground(backColor);
		this.setForeground(foreColor);
		
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				obj.click();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(!multiSelectMode){
					obj.setBorder(selectBd);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				obj.unFocusMode();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				obj.setFocusMode();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {}
			
		});
	}
	
	public void setDefaultMode(){
		this.backColor = new Color(255, 255, 255, 255);
		this.foreColor = new Color(0, 0, 0, 255);
		this.setBackground(backColor);
		this.setForeground(foreColor);
	}
	
	public void setOverDateMode(){
		this.backColor = new Color(200, 200, 200, 255);
		this.foreColor = Color.WHITE;
		this.setBackground(backColor);
		this.setForeground(foreColor);
	}
	
	public void setToDayMode(){
		this.backColor = new Color(50, 200, 200, 255);
		this.setBackground(backColor);
	}
	
	public void setFocusMode(){
		if(!clickState) this.setBorder(focusBd);
	}
	
	public void setUnFocusMode(){
		this.setBorder(unFocusBd);
		this.clickState = false;
	}
	
	public void unFocusMode(){
		if(!clickState) this.setBorder(unFocusBd);
	}
	
	public void setSelectToggleMode(){
		if(!clickState){
			this.setBorder(selectBd);
			this.setBackground(backClickColor);
		}else {
			this.setBorder(unFocusBd);
			this.setBackground(backColor);
		}
	}
	
	public void setSelectMode(){
		this.setBorder(selectBd);
		this.setBackground(backClickColor);
		this.clickState = true;
	}
	
	public void setUnSelectMode(){
		this.setBorder(unFocusBd);
		this.setBackground(backColor);
		this.clickState = false;
	}
	
	public void setAccentToggleMode(){
		if(!clickState){
			this.setBorder(accentBd);
		}else {
			this.setBorder(unFocusBd);
		}
	}	
	
	public void setAccentMode(){
		this.setBorder(accentBd);
		this.clickState = true;
	}
	
	public void setUnAccentMode(){
		this.setBorder(unFocusBd);
		this.clickState = false;
	}
	
	public void click(){
		
		if(multiSelectMode){
			setSelectToggleMode();
			this.clickState = !this.clickState;
		}else {
			this.setAccentToggleMode();
			this.clickState = !this.clickState;
		}
		
		
		if( eventListener != null ){
			eventListener.click(this);
		}
		
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(EventListener eventListener) {
		this.eventListener = eventListener;
	}

	public boolean isClickState() {
		return clickState;
	}

	public void setClickState(boolean clickState) {
		this.clickState = clickState;
	}

	public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

	public int getYear() {
		this.year = this.cal.get(Calendar.YEAR);
		return year;
	}

	public void setYear(int year) {
		this.year = year;
		this.cal.set(Calendar.YEAR, this.year);
	}

	public int getMonth() {
		this.month = this.cal.get(Calendar.MONTH);
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
		this.cal.set(Calendar.MONTH, this.month);
	}

	public int getDay() {
		this.day = this.cal.get(Calendar.DATE);
		return day;
	}

	public void setDay(int day) {
		this.day = day;
		this.cal.set(Calendar.DATE, this.day);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isMultiSelectMode() {
		return multiSelectMode;
	}

	public void setMultiSelectMode(boolean multiSelectMode) {
		this.multiSelectMode = multiSelectMode;
	}

	public String getYmd() {
		return ymd;
	}

	public void setYmd(String ymd) {
		this.ymd = ymd;
	}
	
	public interface EventListener {
		public void click(DateObj obj);
	}
	
}
