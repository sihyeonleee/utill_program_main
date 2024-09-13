package gui.obj;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class ComboBoxObj<E> extends JComboBox<E> {
	
	 /**
	 * @Date   : 2020. 1. 31.
	 * @Role   : 
	 * @Dcpt   : 
	 */
	private static final long serialVersionUID = 1L;
	
	public ComboBoxObj(E[] list){
		super(list);
	}
	
	@Override 
	public void updateUI() {
		super.updateUI();
		
		UIManager.put("ComboBox.squareButton", Boolean.FALSE);
		
		setUI(new BasicComboBoxUI() {
			@Override 
			protected JButton createArrowButton() {
				JButton b = new JButton();
				b.setBorder(BorderFactory.createEmptyBorder());
				b.setVisible(false);
				return b;
			}
		});
		
		setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, false));
	}
}
