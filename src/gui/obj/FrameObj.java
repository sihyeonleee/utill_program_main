package gui.obj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import comm.TextAreaOutputStream;
import comm.TextAreaOutputStream.EventListener;

public class FrameObj extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	public static final int LAYOUT_GRID = 1;
	public static final int LAYOUT_FLOW = 2;
	public static final int LAYOUT_BORDER = 3;
	public static final int LAYOUT_GRIDBAG = 4;
	
	public JPanel panel;
	public JPanel container;
	public JTextArea area;
	
	public boolean isOff = true;
	
	public FrameObj(String name, int width, int height, int divisionX, int divisionY, int layout, boolean showLog){
		
		this.setTitle(name);
		
		this.setSize( width, height );
		
		this.panel = new JPanel(new GridBagLayout());
		
		Dimension frameSize = this.getSize();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		switch(layout){
			case LAYOUT_GRID :
				GridLayout gl = new GridLayout(2,3);
				container = new JPanel(gl);
				break;
			case LAYOUT_FLOW :
				FlowLayout fl = new FlowLayout();
				fl.setAlignment(FlowLayout.CENTER);
				container = new JPanel(fl);
				break;
			case LAYOUT_BORDER :
				BorderLayout bl = new BorderLayout();
				container = new JPanel(bl);
				break;
			case LAYOUT_GRIDBAG :
				GridBagLayout gbl = new GridBagLayout();
				container = new JPanel(gbl);
				break;
			default : 
				break;
		}
		
		container.setBackground(new Color(24,45,65,255));
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		area = new JTextArea();
		JScrollPane scroll = new JScrollPane(area);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		
		TextAreaOutputStream.setListener(new EventListener(){
			@Override
			public void log(String msg) {
				area.append(msg.toString());
				area.setCaretPosition(area.getDocument().getLength());
			}
		});
		
		JButton btn = new JButton("Show Log");
		btn.setBackground(new Color(80,165,165,255));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isOff) {
					gbc.gridx = 0;
					gbc.gridy = 1;
					gbc.weightx = 1;
					gbc.weighty = 50;
					panel.add(scroll, gbc); 
				} else panel.remove(scroll);
				
				isOff = !isOff;
				
				panel.revalidate();
				panel.repaint();
			}
		});
		
		btn.setBorder(new EmptyBorder(1,1,1,1));
		container.setBorder(new EmptyBorder(8,8,8,8));
		panel.setBorder(new EmptyBorder(0,0,0,0));
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 98;
		panel.add(container, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 2;
		
		if(!name.equals("Log") && showLog){
			panel.add(btn, gbc);
		}
		
		this.setContentPane(panel);
		
		// this.setResizable(false);
		// this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		
		double x = (double) (screenSize.width - frameSize.width) / 100;
		double y = (double) (screenSize.height - frameSize.height) / 100;
		
		this.setLocation((int) x * divisionX, (int) y * divisionY);
		
	}
	
	public void append(Component component){
		container.add(component);
	}
	
	public void append(Component component, int arrangeType){
		container.add(component, arrangeType);
	}
	
	public void append(Component component, GridBagConstraints gbc){
		container.add(component, gbc);
	}
	
	public void doShow(){
		this.setVisible(true);
	}
	public void doHide(){
		this.setVisible(false);
	}
}
