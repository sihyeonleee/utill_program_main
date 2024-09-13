package service.manager.task.pannel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import gui.obj.calendar.CalendarEventListener;
import gui.obj.calendar.CalendarViewer;
import gui.obj.calendar.DateObj;

public class TaskCndPanel  extends JPanel implements CalendarEventListener{

	/**
	 * @Date   : 2020. 1. 10.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private CalendarViewer calendar = new CalendarViewer(this);
	private JPanel labelPanel = new JPanel(null);
	
	public TaskCndPanel(){
		this.setLayout(new GridBagLayout());
		labelPanel.setBackground(new Color(0,0,0,0));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.gridwidth = 1; gbc.gridheight = 1;
		
		this.add(labelPanel, gbc);
		this.add(calendar, gbc);
	}
	
	public void init(){
		
		
	}
	
	public void loadComponent(List<Map<String, Object>> list){
		
	}

	@Override
	public void calDatePick(DateObj dateObj) {
		
	}

	@Override
	public void focusDateChange(DateObj dateObj) {
		
	}
	
}
