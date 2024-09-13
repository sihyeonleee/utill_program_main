package service.manager.task.pannel;

import java.awt.GridLayout;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import service.manager.task.obj.FileObj;

public class TaskFilePanel extends JPanel{

	/**
	 * @Date   : 2020. 1. 8.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> list;
	
	public TaskFilePanel(){
		this.setLayout(new GridLayout(0, 1));
	}
	
	public void init(){
		
		list.clear();
		
	}
	
	public void loadComponent(List<Map<String, Object>> list){
		this.list = list;
		for(Map<String, Object> map : list) this.add(new FileObj(map));
	}
	
}
