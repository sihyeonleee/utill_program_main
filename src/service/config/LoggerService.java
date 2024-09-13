package service.config;

import java.awt.GridBagConstraints;

import gui.obj.FrameObj;
import main.Main;
import service.Service;

public class LoggerService extends Service{
	
	private boolean initAt = false;
	
	public LoggerService() {
		
		// 프레임 설정
		width = 800;
		height = 800;
		layout = FrameObj.LAYOUT_GRIDBAG;
		
	}
	
	@Override
	public void doShow(String name) {
		if(!initAt){
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.BOTH;
			frame.append(Main.out, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.BOTH;
			frame.append(Main.err, gbc);
			frame.repaint();
			frame.revalidate();
			initAt = true;
		}
		
		super.doShow(name);
		
	}
	
}
