package gui.obj.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class CalendarViewer extends JPanel implements KeyListener{

	/**
	 * @Date   : 2019. 12. 16.
	 * @Role   : 달력 뷰
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private String []weeks = {"일","월","화","수","목","금","토"};
	
	private Calendar toDay;
	private Calendar nowPickCal;
	private DateObj nowFocusObj;
	private Map<Object, DateObj> pickDates;
	private List<DateObj> pickOrderDates;
	
	private CalendarEventListener calendarEventListener;
	
	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat fmtTit = new SimpleDateFormat("yyyy.MM");
	
	public static final int SELECT_MODE_SINGLE = 0;
	public static final int SELECT_MODE_DUOBLE = 1;
	public static final int SELECT_MODE_MULTI = 2;
	private int multiSelectMode = SELECT_MODE_SINGLE;
	
	private JPanel head = new JPanel();
	private JPanel body = new JPanel();
	private JPanel top = new JPanel(new GridBagLayout());
	private JPanel contents = new JPanel(new GridBagLayout());
	
	private JLabel pickLabel;
	
	public CalendarViewer(){
		init();
	}
	
	public CalendarViewer(CalendarEventListener calendarEventListener){
		this.calendarEventListener = calendarEventListener;
		init();
	}
	
	public CalendarViewer(int multiSelectMode){
		this.multiSelectMode = multiSelectMode;
		init();
	}
	
	public void init(){
		
		this.setBackground(Color.WHITE);
		this.setBorder(new EmptyBorder(5,5,5,5));
		
		toDay = Calendar.getInstance();
		nowPickCal = Calendar.getInstance();
		pickDates = new HashMap<>();
		pickOrderDates = new ArrayList<>();
		JPanel headLeft = new JPanel();
		JPanel headRight = new JPanel();
		
		JButton toDayBtn = new JButton("오늘"); // 오늘
		
//		JButton prevY = new JButton("<<"); // 전년
		JButton prevM = new JButton("<"); // 전달
		this.pickLabel = new JLabel(fmtTit.format(nowPickCal.getTime())); // 선택 일 정보
		JButton nextM = new JButton(">"); // 다음달
//		JButton nextY = new JButton(">>"); // 내년
		
		toDayBtn.setOpaque(false);
//		prevY.setOpaque(false);
		prevM.setOpaque(false);
		nextM.setOpaque(false);
//		nextY.setOpaque(false);
		toDayBtn.setBackground(new Color(255, 255, 255, 255));
//		prevY.setBackground(new Color(255, 255, 255, 255));
		prevM.setBackground(new Color(255, 255, 255, 255));
		nextM.setBackground(new Color(255, 255, 255, 255));
//		nextY.setBackground(new Color(255, 255, 255, 255));
		
		toDayBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDate(toDay.get(Calendar.YEAR), toDay.get(Calendar.MONTH));
				pickLabel.setText(fmtTit.format(toDay.getTime()));
			}
		});
		toDayBtn.setFocusable(false);
//		prevY.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				loadDate(nowPickCal.get(Calendar.YEAR)-1, nowPickCal.get(Calendar.MONTH));
//				pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
//			}
//		});
//		prevY.setFocusable(false);
		prevM.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH)-1);
				pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
			}
		});
		prevM.setFocusable(false);
		nextM.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH)+1);
				pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
			}
		});
		nextM.setFocusable(false);
//		nextY.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				loadDate(nowPickCal.get(Calendar.YEAR)+1, nowPickCal.get(Calendar.MONTH));
//				pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
//			}
//		});
//		nextY.setFocusable(false);
		
		headLeft.add(toDayBtn, BorderLayout.WEST);
//		headRight.add(prevY);
		headRight.add(prevM);
		headRight.add(pickLabel);
		headRight.add(nextM);
//		headRight.add(nextY);
		
		head.setLayout(new BorderLayout());
		head.add(headLeft, BorderLayout.WEST);
		head.add(headRight, BorderLayout.EAST);
		
		for(int i=0; i<weeks.length; i++){
			JLabel d = new JLabel(weeks[i]);
			d.setFont(new Font("바탕", Font.PLAIN, 12));
			d.setHorizontalAlignment(JLabel.CENTER);
			d.setForeground(Color.WHITE);	
			d.setBorder(new EtchedBorder());
			d.setOpaque(true);
			
			if(i==0){
				d.setBackground(new Color(180, 0, 0, 255));
			}else if(i==6){
				d.setBackground(new Color(0	, 0, 180, 255));
			}else {
				d.setBackground(Color.GRAY);
			}
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = i;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			top.add(d, gbc);
		}
		
		body.setLayout(new BorderLayout());
		body.add(top, BorderLayout.NORTH);
		body.add(contents, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(head, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
		
//		try{
//			//LookAndFeel Windows 스타일 적용
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//			SwingUtilities.updateComponentTreeUI(this);
//		}catch(Exception err){
//			TrayIconHandler.displayMessage("ERROR", "Not Working Look And Feel", MessageType.ERROR);
//		}
		
		loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH));
		
		this.setFocusable(true);
		this.addKeyListener(this);
		
		final JPanel calPanel = this;
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent c){
				calPanel.requestFocusInWindow();
				calPanel.requestFocus();
			}
		});
	}
	
	public void loadDate(int year, int month){
		
		contents.removeAll();
		nowPickCal.set(year, month, nowPickCal.get(Calendar.DATE));
		
		// 해당월 1일
		Calendar firstDt = Calendar.getInstance();
		firstDt.set(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH), 1);
		
		// 해당월 마지막일
		Calendar lastDt = Calendar.getInstance();
		lastDt.set(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH), nowPickCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		// 시작 요일 : 일(1) ~ 토(7)
		int dayOfFrstWeek = firstDt.get(Calendar.DAY_OF_WEEK)-1;
		
		// 끝 요일 : 일(1) ~ 토(7)
		int dayOfLastWeek = lastDt.get(Calendar.DAY_OF_WEEK)-1;
		
		// 이전달
		final Calendar prevDt = Calendar.getInstance();
		prevDt.set(firstDt.get(Calendar.YEAR), firstDt.get(Calendar.MONTH), firstDt.get(Calendar.DATE)-dayOfFrstWeek);
		int dayOfWeek = prevDt.get(Calendar.DAY_OF_WEEK)-1;
		
		// 다음달
		Calendar nextDt = Calendar.getInstance();
		nextDt.set(lastDt.get(Calendar.YEAR), lastDt.get(Calendar.MONTH), lastDt.get(Calendar.DATE));
		nextDt.add(Calendar.DATE, 6-dayOfLastWeek);
		
		// 이전, 다음달 일자 수 비교
		long diffSec = (nextDt.getTimeInMillis() - prevDt.getTimeInMillis())/1000;
		long diffDay = diffSec / (24*60*60);
		
		for(int i=0; i<=diffDay; i++){
			
			Calendar cal = Calendar.getInstance();
			cal.set(prevDt.get(Calendar.YEAR), prevDt.get(Calendar.MONTH), prevDt.get(Calendar.DATE));
			
			DateObj d = null;
			
			if(pickDates.containsKey(fmt.format(cal.getTime()))){
				d = pickDates.get(fmt.format(cal.getTime()));
			}else {
				d = new DateObj(cal, new DateObj.EventListener() {
					
					@Override
					public void click(DateObj obj) {
						if(!obj.isClickState()){
							pickDates.remove(obj.getYmd());
							pickOrderDates.remove(obj);
						}else if(obj.isMultiSelectMode() && obj.isClickState()){
							if(multiSelectMode == 1 && pickOrderDates.size() > 1){
								pickOrderDates.get(0).setUnSelectMode();
								pickDates.remove(pickOrderDates.get(0).getYmd());
								pickOrderDates.remove(0);
							}
							if(!pickDates.containsKey(obj.getYmd())){
								pickDates.put(obj.getYmd(), obj);
								pickOrderDates.add(obj);
							}
							nowPickCal.set(Calendar.DATE, obj.getDay());
						}else if(!obj.isMultiSelectMode()){
							if(!pickDates.containsKey(obj.getYmd())){
								if(pickDates.size() > 0) 
									for(Map.Entry<Object, DateObj> o : pickDates.entrySet()) 
										o.getValue().setUnFocusMode();
								pickDates.clear();
								pickDates.put(obj.getYmd(), obj);
							}
							nowPickCal.set(Calendar.DATE, obj.getDay());
						}
						
						if(calendarEventListener != null){
							calendarEventListener.calDatePick(obj);
						}
					}

				}, multiSelectMode == 0 ? false : true);
			}
			
			// 오늘, 이전, 다음달
			if(fmt.format(toDay.getTime()).compareTo(fmt.format(cal.getTime())) == 0){
				d.setToDayMode();
			}else if(fmt.format(firstDt.getTime()).compareTo(fmt.format(prevDt.getTime())) > 0 || fmt.format(lastDt.getTime()).compareTo(fmt.format(prevDt.getTime())) < 0){
				d.setOverDateMode();
			}else {
				d.setDefaultMode();
			}
			
			// 클릭상태 
			if(d.isClickState()){
				if(d.isMultiSelectMode()){
					d.setSelectMode();
				}else {
					d.setAccentMode();
				}
			}
			
			if(fmt.format(nowPickCal.getTime()).compareTo(fmt.format(d.getCal().getTime())) == 0){
				d.setFocusMode();
				nowFocusObj = d;
			}
			
			// 위치
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = dayOfWeek % 7;
			gbc.gridy = dayOfWeek / 7;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			contents.add(d, gbc);
			
			prevDt.add(Calendar.DATE, 1);
			dayOfWeek++;
			
		}
		
		this.repaint();
		this.revalidate();
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == 27){
			disposeParents(this);
			return;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			space();
			return;
		}
		
		if((e.getModifiers() & 2) != 0){
			if((e.getModifiers() & 2) != 0 && e.getKeyCode() == 37){
				left(2);
			}else if((e.getModifiers() & 2) != 0 && e.getKeyCode() == 38){
				up(2);
			}else if((e.getModifiers() & 2) != 0 && e.getKeyCode() == 39){
				right(2);
			}else if((e.getModifiers() & 2) != 0 && e.getKeyCode() == 40){
				down(2);
			}
		}else {
			if(e.getKeyCode() == 37){
				left(1);
			}else if(e.getKeyCode() == 38){
				up(1);
			}else if(e.getKeyCode() == 39){
				right(1);
			}else if(e.getKeyCode() == 40){
				down(1);
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void space(){
		if(nowFocusObj != null) nowFocusObj.click();
	}
	
	public void up(int type){
		if(type == 1){
			this.nowPickCal.add(Calendar.DATE, -7);
		}else if(type == 2){
			this.nowPickCal.add(Calendar.YEAR, -1);
		}
		
		this.loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH));
		this.pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
		if(calendarEventListener != null) calendarEventListener.focusDateChange(nowFocusObj);
	}
	public void down(int type){
		if(type == 1){
			this.nowPickCal.add(Calendar.DATE, 7);
		}else if(type == 2){
			this.nowPickCal.add(Calendar.YEAR, 1);
		}

		this.loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH));
		this.pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
		if(calendarEventListener != null) calendarEventListener.focusDateChange(nowFocusObj);
	}
	public void left(int type){
		if(type == 1){
			this.nowPickCal.add(Calendar.DATE, -1);
		}else if(type == 2){
			this.nowPickCal.add(Calendar.MONTH, -1);
		}
		
		this.loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH));
		this.pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
		if(calendarEventListener != null) calendarEventListener.focusDateChange(nowFocusObj);
	}
	public void right(int type){
		if(type == 1){
			this.nowPickCal.add(Calendar.DATE, 1);
		}else if(type == 2){
			this.nowPickCal.add(Calendar.MONTH, 1);
		}
		
		this.loadDate(nowPickCal.get(Calendar.YEAR), nowPickCal.get(Calendar.MONTH));
		this.pickLabel.setText(fmtTit.format(nowPickCal.getTime()));
		if(calendarEventListener != null) calendarEventListener.focusDateChange(nowFocusObj);
	}
	
	
	
	
	
	
	
	public void disposeParents(Component J){
		try{
			if(J.getParent() instanceof JFrame)
				((JFrame) J.getParent()).dispose();
			else disposeParents(J.getParent());
		}catch(NullPointerException e){
			
		}
	}
	
	
	
	
	
	
	
	public Calendar getToDay() {
		return toDay;
	}

	public void setToDay(Calendar toDay) {
		this.toDay = toDay;
	}

	public Calendar getNowPickDate() {
		return nowPickCal;
	}

	public void setNowPickDate(Calendar nowPickDate) {
		this.nowPickCal = nowPickDate;
	}

	public Map<Object, DateObj> getPickDates() {
		return pickDates;
	}

	public void setPickDates(Map<Object, DateObj> pickDates) {
		this.pickDates = pickDates;
	}

	public int isMultiSelectMode() {
		return multiSelectMode;
	}

	public void setMultiSelectMode(int multiSelectMode) {
		this.multiSelectMode = multiSelectMode;
	}

	@SuppressWarnings("static-access")
	public List<DateObj> getPickOrderDates() {
		List<DateObj> objs = new ArrayList<>();
		if(this.multiSelectMode == this.SELECT_MODE_DUOBLE && pickOrderDates.size() > 1){
			if(fmt.format(pickOrderDates.get(0).getCal().getTime()).compareTo(fmt.format(pickOrderDates.get(1).getCal().getTime())) > 0){
				objs.add(pickOrderDates.get(1));
				objs.add(pickOrderDates.get(0));
			}else {
				objs.add(pickOrderDates.get(0));
				objs.add(pickOrderDates.get(1));
			}
		}else {
			objs.addAll(pickOrderDates);
		}
		return objs;
	}

	public void setPickOrderDates(List<DateObj> pickOrderDates) {
		this.pickOrderDates = pickOrderDates;
	}

	public CalendarEventListener getCalendarEventListener() {
		return calendarEventListener;
	}

	public void setCalendarEventListener(CalendarEventListener calendarEventListener) {
		this.calendarEventListener = calendarEventListener;
	}
	
	// 오늘 일시 받아오기
	public static String getNowDate(String fmt){
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat(fmt);
		return format.format(now);
	}
	
}
