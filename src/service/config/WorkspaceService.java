package service.config; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import comm.fileio.FileChooser;
import comm.fileio.TextFileReader;
import comm.fileio.TextFileWriter;
import main.Main;
import service.Service;  

public class WorkspaceService extends Service{ 

	public WorkspaceService(){ 
		
	}

	@Override
	public void doShow(String name) {
		
		String path = "";
		
		TextFileReader reader = new TextFileReader("tokens", "path", "lsh");
		String txt = reader.reade(true);
		Map<String, String> result = reader.getKeyValueTypeDataToMap(txt);
		
		path = result.get("path");
		
		if("".equals(path)){
			path = ".\\";
		}
		
		JLabel label = new JLabel("path");
		JTextField field = new JTextField(path, 15);
		JButton button = new JButton("파일선택");
		JPanel panel = new JPanel();
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser ch = new FileChooser(JFileChooser.DIRECTORIES_ONLY);
				File[] files = ch.doSelect();
				if(files != null) field.setText(files[0].getPath()+"\\");
			}
		});
		
		panel.add(label);
		panel.add(field);
		panel.add(button);
		
		int popResult = Main.confirmPop("경로변경", panel);
		
		if(popResult == 1){
			path = field.getText();
			
			if("".equals(path)) path = ".";
			
			char ch = path.charAt(path.length()-1);
			if(ch != '\\') path += "\\";
		
			File file = new File(path);
			
			if(file.exists()){
				TextFileWriter writer = new TextFileWriter("tokens", "path", "lsh");
				writer.write("path=" + path, false);
				Main.alertPop("경로가 변경 되었습니다.\n시스템을 다시 시작합니다.");
				Main.restart();
			}else {
				Main.alertPop("잘못된 경로입니다.");
			}
				
		}else {
			
		}
		
	}

}
