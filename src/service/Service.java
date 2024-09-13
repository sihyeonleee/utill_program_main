package service;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import comm.CustomThread;
import gui.obj.ComboBoxObj;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import gui.obj.ImageObj;
import main.Main;

public abstract class Service {
	
	public Integer layout = 1;
	
	public int width = 1000;
	public int height = 1000;
	public int divisionX = 50;
	public int divisionY = 50;
	
	public boolean isAlwasOnTop = false;
	public boolean isShowLog = true;
	
	public Map<String, CustomThread> threads = new HashMap<>();
	public List<CompObj> componentObjs = new ArrayList<>();
	public List<JTextField> inputs = new ArrayList<>();
	public List<JLabel> outputs = new ArrayList<>();
	public List<JButton> buttons = new ArrayList<>();
	public List<JCheckBox> checkBoxes = new ArrayList<>();
	public List<JTextArea> textAreas = new ArrayList<>();
	public List<JScrollPane> scrollPanes = new ArrayList<>();
	@SuppressWarnings("rawtypes")
	public List<JComboBox> comboBoxes = new ArrayList<>();
	public List<Container> panels = new ArrayList<>();
	public FrameObj frame;
	
	
	
	
	
	
	
	// Setting
	public void init( final String name ) throws Exception{

		frame = new FrameObj(name, width, height, divisionX, divisionY, layout, isShowLog);
		frame.setAlwaysOnTop(isAlwasOnTop);
		if( componentObjs.size() > 0 ){
			for(int i=0; i<componentObjs.size(); i++){
				CompObj obj = componentObjs.get(i);
				obj.setIndex(i);
				appendObj(obj);
			}
		}
		
		frame.setIconImage(ImageObj.getMainIcon());
	}
	
	public void appendObj(CompObj obj) {
		
		Component c = componentInjection(obj);
		eventInjection(obj, c);
		
		if(obj.isScrollAt()){
			c = this.scrollPanes.get(scrollPanes.size()-1);
		}
		
		Integer type = obj.getArrangeType();
		
		if(layout == FrameObj.LAYOUT_GRIDBAG){
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = obj.getGridX();
			gbc.gridy = obj.getGridY();
			gbc.weightx = obj.getWeightX();
			gbc.weighty = obj.getWeightY();
			gbc.gridwidth = obj.getGridWidth();
			gbc.gridheight = obj.getGridHeight();
			gbc.ipadx = obj.getIpadX();
			gbc.ipady = obj.getIpadY();
			if(type != null) gbc.fill = type;
			frame.append(c, gbc);
		} else if(type != null){
			frame.append(c, type);
		} else {
			frame.append(c);
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Component componentInjection(CompObj obj) {
		
		final CompObj object = obj;
		Component result = null;
		
		switch(obj.getType()){
			case CompObj.TYPE_INPUT: 
				
				final JTextField input = new JTextField(object.getMsg(), object.getCharX());
				
				input.setEditable(object.isEnabled());
				
				if(!object.getHint().equals("") ) {
					
					input.setToolTipText(object.getHint());
					
					if(object.getMsg().trim().equals("")){
						input.setText(object.getHint());
						input.setForeground(Color.GRAY);  
					}
					
					input.addFocusListener(new FocusAdapter() {
						  
						@Override  
						public void focusGained(FocusEvent e) {  
							if (input.getText().equals(object.getHint())) {  
								input.setText("");  
							}else {
				        		input.setForeground(Color.BLACK);  
							}
						}  
						@Override  
				      	public void focusLost(FocusEvent e) {  
							if (input.getText().equals(object.getHint())|| input.getText().length()==0) {  
				        		input.setText(object.getHint());  
				        		input.setForeground(Color.GRAY);  
				        	} else {  
				        		input.setForeground(Color.BLACK);  
				        	}  
						}  
					});  
				}
				
				result = input;
				inputs.add( (JTextField) input );
				
				break;
			case CompObj.TYPE_OUTPUT: 
				
				result = new JLabel(object.getMsg());
				result.setEnabled(object.isEnabled());
				outputs.add( (JLabel) result );
				
				break;
			case CompObj.TYPE_BUTTON: 
				
				String title = object.getName();
				
				if(title.equals("")){
					title = "btn_" + obj.getIndex();
				}
				
				result = new JButton(title);
				result.setEnabled(object.isEnabled());
				buttons.add( (JButton) result );
				
				break;
			case CompObj.TYPE_CHECKBOX: 
				
				result = new JCheckBox(object.getMsg());
				result.setEnabled(object.isEnabled());
				checkBoxes.add( (JCheckBox) result );
				
				break;
			case CompObj.TYPE_TEXTAREA: 
				
				final JTextArea textArea = new JTextArea(object.getMsg(), object.getCharY(), object.getCharX());
				textArea.setEditable(object.isEnabled());
				
				if(!object.getHint().equals("")) {
					
					textArea.setText(object.getHint());  
					textArea.setForeground(Color.GRAY);  
					
					textArea.addFocusListener(new FocusAdapter() {
						  
						@Override  
						public void focusGained(FocusEvent e) {  
							if (textArea.getText().equals(object.getHint())) {  
								textArea.setText("");  
							}else {
								textArea.setForeground(Color.BLACK);  
							}
						}  
						@Override  
				      	public void focusLost(FocusEvent e) {  
							if (textArea.getText().equals(object.getHint())|| textArea.getText().length()==0) {  
								textArea.setText(object.getHint());  
								textArea.setForeground(Color.GRAY);  
				        	} else {  
				        		textArea.setForeground(Color.BLACK);  
				        	}  
						}  
					});  
				}
				
				textAreas.add( textArea );
				
				if(object.isScrollAt()){
					JScrollPane scroll = new JScrollPane( textArea );
					scrollPanes.add( scroll );
				}
				
				result = textArea;
				
				break;
				
			case CompObj.TYPE_COMBOBOX: 
				
				result = new ComboBoxObj<String>(object.getSelectItems());
				result.setEnabled(object.isEnabled());
				comboBoxes.add( (JComboBox) result );
				
				break;
				
			case CompObj.TYPE_PANEL: 
				
				if(object.getLayout() instanceof BoxLayout){
					
					BoxLayout layout = (BoxLayout) object.getLayout();
					
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, layout.getAxis()));
					
					result = panel;
					
				}else {
					result = new JPanel(object.getLayout());
				}
				result.setEnabled(object.isEnabled());
				panels.add( (Container) result );
				if(object.isScrollAt()){
					JScrollPane scroll = new JScrollPane( result );
					// scroll.getVerticalScrollBar().setUI(new ScrollBarUI());
					scroll.getVerticalScrollBar().setUnitIncrement(16);
					scrollPanes.add( scroll );
				} else {
					((JPanel) result).setBorder(new LineBorder(new Color(100,100,100,40)));
				}
				break;
			case CompObj.TYPE_TABPANEL: 
				
				JTabbedPane tPane = new JTabbedPane();
				if(object.getNames() != null){
					for(int j=0;j<object.getNames().length; j++){
						String name = object.getNames()[j];
						JPanel panel = new JPanel(new BorderLayout());
						tPane.add(name, panel);
					}
				}
				panels.add( (Container) tPane );
				result = tPane;
				break;
			default : 
				break;
		}
		
		if(object.getPopupItems().length > 0) {
			
			final PopupMenu popupMenu = new PopupMenu();
			for(int j=0;j<object.getPopupItems().length; j++) {
				String menu = object.getPopupItems()[j];
				final MenuItem item = new MenuItem(menu);
				item.addActionListener(new ActionListener() {
				   @Override
				   public void actionPerformed(ActionEvent e) {
					   try {
						onEvent("menu", obj, item);
					} catch (Exception err) {
						err.printStackTrace();
					}
				   }
				});
				popupMenu.add(item);
			}
			result.add(popupMenu);
			
			final Component comp = result;
			
			result.addMouseListener(new MouseAdapter() {  
	            public void mouseClicked(MouseEvent e) {   
	            	// 더블 클릭
	            	if(e.getClickCount() == 2) {
	            		try {
							onEvent("double", obj);
						} catch (Exception err) {
							err.printStackTrace();
						}
	            	}
	            	
	            	// 우클릭
	            	if(e.isMetaDown()) {
	            		popupMenu.show(comp, e.getX(), e.getY());  
	            	}
	            }                 
	         });  
		}
		
		if(object.getWidth() != null && object.getHeight() != null){
			if(object.getPositionX() != null && object.getPositionY() != null){
				result.setBounds(object.getPositionX(), object.getPositionY(), object.getWidth(), object.getHeight());
			}else {
				result.setSize(object.getWidth(), object.getHeight());
			}
		}
		
//		c.setFocusable(obj.isFocus());
		
		return result;
	}
	
	public Component eventInjection(CompObj obj, Component c){
		
		final int index = obj.getIndex();
		
		if(obj.getEventType().length > 0){
			for(int type : obj.getEventType()){
				
				switch(type){
				
					case CompObj.EVENT_ACTION :
						switch(obj.getType()){
							case CompObj.TYPE_INPUT: 
								JTextField input = ( JTextField ) c;
								input.addActionListener(new AbstractAction() {
									private static final long serialVersionUID = 1L;
									@Override
									public void actionPerformed(ActionEvent e) {
										try {
											onEvent( "enter", obj );
										} catch (Exception err) {
											err.printStackTrace();
										}
									}
								});
								break;
							case CompObj.TYPE_OUTPUT: 
								break;
							case CompObj.TYPE_COMBOBOX:
								@SuppressWarnings("rawtypes") final JComboBox combobox = ( JComboBox ) c;
								combobox.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										try {
											onEvent( "change", obj , combobox.getSelectedIndex(), combobox.getSelectedItem());
										} catch (Exception err) {
											err.printStackTrace();
										}
									}
								});
								break;
							case CompObj.TYPE_BUTTON:
								JButton button = ( JButton ) c;
								button.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										try {
											onEvent( "click", obj );
										} catch (Exception err) {
											err.printStackTrace();
										}
									}
								});
								break;
							case CompObj.TYPE_CHECKBOX:
								JCheckBox checkbox = ( JCheckBox ) c;
								checkbox.setSelected(obj.isSelected());
								checkbox.addItemListener(new ItemListener() {
									
									@Override
									public void itemStateChanged(ItemEvent e) {
										try {
											onEvent( "change", obj, checkbox.isSelected());
										} catch (Exception err) {
											err.printStackTrace();
										}
									}
								});
								break;
							case CompObj.TYPE_TEXTAREA: 
								break;
							default : 
								break;
						}
						break;
					case CompObj.EVENT_TYPING :
						final JTextComponent textComp = ( JTextComponent ) c;
						textComp.getDocument().addDocumentListener(new DocumentListener() {
							
							@Override
							public void removeUpdate(DocumentEvent e) {
								try {
									onEvent("tyRemove", obj, textComp.getText());
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
							
							@Override
							public void insertUpdate(DocumentEvent e) {
								try {
									onEvent("tyInsert", obj, textComp.getText());
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
							
							@Override
							public void changedUpdate(DocumentEvent arg0) {
								try {
									onEvent("tyUpdate", obj, textComp.getText());
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
						});
						break;
					case CompObj.EVENT_KEYTYPE :
						final Component comp =  c;
						comp.addKeyListener(new KeyListener() {
							
							KeyEvent e;
							
							@Override
							public void keyTyped(KeyEvent e) {
								try {
									onEvent("keyTyped", obj, this.e);
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
							
							@Override
							public void keyReleased(KeyEvent e) {
								try {
									onEvent("keyRelea", obj, e);
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
							
							@Override
							public void keyPressed(KeyEvent e) {
								try {
									this.e = e;
									onEvent("keyPress", obj, e);
								} catch (Exception err) {
									err.printStackTrace();
								}
							}
						});
						break;
					case CompObj.EVENT_DRAGDROP :
						c.setDropTarget(new DropTarget() {
							private static final long serialVersionUID = 1L;

							@SuppressWarnings("unchecked")
							public synchronized void drop(DropTargetDropEvent evt) {
						        try {
						            evt.acceptDrop(DnDConstants.ACTION_COPY);
						            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						            onEvent( "change", obj, droppedFiles.toArray() );
						        } catch (Exception err) {
						        	err.printStackTrace();
						        }
						    }
						});
						break;
					default : 
						break;
						
				}
				
			}
		}
		return c;
	}
	
	
	
	
	
	
	// Main Method
	public void doShow( String name ) {
		frame.doShow();
	}
	
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception{
		
	}
	
	
	
	
	
	
	
	
	// 메세지 전달
	public void setMessage(String type, int index, String msg) {
		if(type.equals("input")){
			inputs.get(index).setText(msg);
		}else if(type.equals("output")){
			outputs.get(index).setText(msg);
		}else if(type.equals("button")){
			buttons.get(index).setText(msg);
		}else if(type.equals("textArea")){
			textAreas.get(index).setText(msg);
			textAreas.get(index).setCaretPosition(textAreas.get(index).getDocument().getLength());
		}
	}
	
	public void appendMessage(String type, int index, String msg) {
		if(type.equals("input")){
			inputs.get(index).setText(inputs.get(index).getText() + msg);
		}else if(type.equals("output")){
			outputs.get(index).setText(outputs.get(index).getText() + msg);
		}else if(type.equals("button")){
			buttons.get(index).setText(buttons.get(index).getText() + msg);
		}else if(type.equals("textArea")){
			textAreas.get(index).setText(textAreas.get(index).getText() + msg);
			textAreas.get(index).setCaretPosition(textAreas.get(index).getDocument().getLength());
		}
	}
	
	public String getMessage(String type, int index){
		String result = "";
		if(type.equals("input")){
			result =  inputs.get(index).getText();
		}else if(type.equals("output")){
			result =   outputs.get(index).getText();
		}else if(type.equals("button")){
			result =   buttons.get(index).getText();
		}else if(type.equals("textArea")){
			result =   textAreas.get(index).getText();
		}
		return result;
	}
	
	
	
	
	
	
	
	public CustomThread createThread(){
		return new CustomThread();
	}
	
	public CustomThread runThread(String threadName, boolean multiple){
		
		CustomThread thread = threads.get(threadName);
		
		if(thread == null){
			thread = createThread();
			thread.setName(threadName);
			threads.put(threadName, thread);
		}
		
		Thread.State state = thread.getState();
		
		if(state == Thread.State.NEW){
			thread.start();
		}if(state == Thread.State.TERMINATED){
			thread = createThread();
			thread.setName(threadName);
			threads.put(threadName, thread);
			thread.start();
		}else if(state == Thread.State.RUNNABLE){
			if(multiple){
				thread = createThread();
				thread.setName(threadName);
				threads.put(threadName, thread);
				thread.start();
			}else {
				String progress = "이미 처리중입니다. \nTHREAD_NAME ::: " + thread.getName();
				progress += "\n진행률 : " + thread.getNowProc() + " / " + thread.getTotalProc();
				progress += " ( " + thread.getPercent() + "% ) ";
				Main.alertPop(progress);
			}
		}
		
		return thread;
		
	}
	
	public void append(CompObj obj) {
		
		int index = 0;
		
		switch(obj.getType()) {
			case CompObj.TYPE_INPUT :
				index = inputs.size();
				break;
			case CompObj.TYPE_OUTPUT :
				index = outputs.size();
				break;
			case CompObj.TYPE_BUTTON :
				index = buttons.size();
				break;
			case CompObj.TYPE_TEXTAREA :
				index = textAreas.size();
				break;
		}
		
		appendObj(obj);
		
	}
	
	
}
