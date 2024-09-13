package service.manager.task.pannel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import service.manager.task.obj.FileObj;

public class TaskTagPanel extends JPanel{

	/**
	 * @Date   : 2020. 1. 8.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Map<String, Object>> list;
	
	private JPanel panel;
	
	public TaskTagPanel(){
		
		this.setLayout(new BorderLayout());
		
		this.panel = new JPanel(new GridLayout(0, 1, 0, 0));
		
		this.panel.setBackground(Color.WHITE);
		
		JScrollPane scroll = new JScrollPane( this.panel );
		
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		this.add(scroll);
		
	}
	
	public void loadComponent(List<Map<String, Object>> list){
		this.panel.removeAll();
		this.list = list;
		for(Map<String, Object> map : list) 
			this.panel.add(new FileObj(map));
	}

}
