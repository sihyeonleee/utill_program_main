package service.write;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

import comm.CustomThread;
import comm.Path;
import comm.fileio.FileChooser;
import gui.main.TrayIconHandler;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;

public class FilePathCopyService extends Service {

	private final String defaultCopyHistName = "이력";
	private final String defaultStreDirNm = "Deploy";
	private final String dirKeyLabel = "저장 폴더명";
	private final String btnName = "복사";

	private String strDirNm = defaultStreDirNm;
	private String currentTime = "";
	private int succCopyCnt = 0;
	private int failCopyCnt = 0;

	Color mainColor;
	private Font small;

	public boolean isInit = false;

	public Map<String, ArrayList<Component>> copyHists;

	public List<String> copyHistNames;

	public FilePathCopyService() {

		// 프레임 설정
		width = 370;
		height = 400;
		divisionX = 103;
		divisionY = 107;
		layout = FrameObj.LAYOUT_GRIDBAG;
		isAlwasOnTop = true;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName(btnName);
		btn1.setEvtName("convert");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 0);
		btn1.setGridSize(1, 1);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);

		CompObj btn2 = new CompObj();
		btn2.setName("초기화");
		btn2.setEvtName("delete");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(1, 0);
		btn2.setGridSize(1, 1);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);

		CompObj area = new CompObj();
		area.setName(defaultCopyHistName);
		area.setEvtName("copyHist");
		area.setSelectItems(new String[] { defaultCopyHistName });
		area.setType(CompObj.TYPE_COMBOBOX);
		area.setEventType(CompObj.EVENT_ACTION);
		area.setGridPosition(2, 0);
		area.setGridWeight(1, 1);
		area.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area);

		CompObj area1 = new CompObj();
		area1.setEnabled(true);
		area1.setScrollAt(true);
		area1.setType(CompObj.TYPE_PANEL);
		area1.setEventType(CompObj.EVENT_DRAGDROP, CompObj.EVENT_KEYTYPE);
		area1.setLayout(CompObj.LAYOUT_BOX, BoxLayout.Y_AXIS);
		area1.setEvtName("area");
		area1.setGridSize(3, 1);
		area1.setGridWeight(30, 100);
		area1.setGridPosition(0, 1);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);

	}

	public void init() {

		small = new Font("함초롱돋움", Font.PLAIN, 11);
		mainColor = new Color(200, 200, 200, 255);
		copyHistNames = new ArrayList<>();
		copyHists = new HashMap<>();

//		copyHistNames.add(defaultCopyHistName);
//		copyHists.put(defaultCopyHistName, new ArrayList<>());

		buttons.get(0).setOpaque(true);
		buttons.get(0).setForeground(new Color(150, 150, 150, 255));
		buttons.get(0).setBackground(new Color(230, 230, 230, 255));
		buttons.get(1).setOpaque(true);
		buttons.get(1).setForeground(new Color(150, 150, 150, 255));
		buttons.get(1).setBackground(new Color(230, 230, 230, 255));
		panels.get(0).setBackground(mainColor);
		isInit = true;
	}

	public void doShowBefore() {

		if (!isInit)
			init();

	}

	@Override
	public void doShow(String name) {
		// Component Object Settings
		doShowBefore();
		super.doShow(name);
		doShowAfter();
	}

	public void doShowAfter() {
		panels.get(0).requestFocus();
	}

	@Override
	public void onEvent(String type, CompObj obj, Object... objects) throws Exception {
		if (type.equals("change") && "area".equals(obj.getEvtName())) {
			addFiles(objects);
		} else if (type.equals("click") && "delete".equals(obj.getEvtName())) {
			deleteCopyList();
		} else if (type.equals("click") && "convert".equals(obj.getEvtName())) {
			if (copyBaseDir()) {
				String threadName = "FilePathCopyService";
				runThread(threadName, false);
			}
		} else if (type.equals("keyPress") && "area".equals(obj.getEvtName())) {
			shortcutEvent((KeyEvent) objects[0]);
		} else if (type.equals("change") && "copyHist".equals(obj.getEvtName())) {
			changeCopyHist();
		}

		panels.get(0).requestFocus();

	}

	public void addFiles(Object... objects) {

		File[] files = (File[]) objects;

		for (File file : files) {
			FileObj obj = new FileObj(file.getName(), file.getParent(), file.isDirectory());
			panels.get(0).add(obj);
		}

		repaintPanel();

	}

	public void deleteCopyList() {

		ButtonGroup group = new ButtonGroup();

		JRadioButton radio1 = new JRadioButton("초기화");
		JRadioButton radio2 = new JRadioButton("초기화&삭제");

		group.add(radio1);
		group.add(radio2);

		JPanel panel = new JPanel();
		panel.add(radio1);
		panel.add(radio2);

		radio1.setSelected(true);

		if (Main.confirmPop(panel) > 0) {
			if (radio1.isSelected()) {
				// 초기화
				panels.get(0).removeAll();
				repaintPanel();
			} else {
				// 초기화&삭제
				if (copyHists.containsKey(strDirNm)) {
					copyHistNames.remove(strDirNm);
					copyHists.remove(strDirNm);
					resetCopyHistName();
					changeCopyHist();
				} else {
					panels.get(0).removeAll();
					repaintPanel();
				}
			}
		}

	}

	public boolean copyBaseDir() {

		if (panels.get(0).getComponentCount() < 1)
			return false;

		List<String> inputValueName = new ArrayList<>();
		inputValueName.add(dirKeyLabel);

		Map<String, String> inputValue = new HashMap<String, String>();
		inputValue.put(dirKeyLabel, this.strDirNm);

		Map<String, String> resultMap = Main.confirmPop(frame, "저장 파일 이름", inputValueName.toArray(), inputValue, 0, 0);

		String result = resultMap.get("result");

		if (result.equals("confirm")) {

			this.strDirNm = resultMap.get(dirKeyLabel);

			if (copyHists.containsKey(strDirNm)) {
				copyHistNames.remove(strDirNm);
				copyHists.remove(strDirNm);
			}

			SimpleDateFormat fmt = new SimpleDateFormat("MMdd_HHmm");
			Date dt = new Date();
			this.currentTime = "_" + fmt.format(dt);

			return true;
		} else {
			return false;
		}

	}

	@Override
	public CustomThread createThread() {
		return new CustomThread() {
			@Override
			public void run() {

				Component[] objs = (Component[]) panels.get(0).getComponents();

				copyHistNames.add(strDirNm);
				copyHists.put(strDirNm, new ArrayList<Component>(Arrays.asList(objs)));

				StringBuffer msg = new StringBuffer();

				succCopyCnt = 0;
				failCopyCnt = 0;

				for (Component obj : objs) {
					FileObj fObj = (FileObj) obj;
					copyFile(fObj, msg);
				}

				JTextPane label = new JTextPane();
				label.setContentType("text/html");
				label.setText("<html>" + msg.toString() + "</html>");

				JScrollPane scrollPane = new JScrollPane(label);
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				Main.alertPop(frame, "작업결과 전체 " + (succCopyCnt + failCopyCnt) + "개 중 " + succCopyCnt + "개 성공",
						scrollPane);

				resetCopyHistName();

			}
		};
	}

	public void copyDir(File dir, StringBuffer msg) {

		File[] files = dir.listFiles();

		for (File file : files) {
			FileObj fObj = new FileObj(file.getName(), file.getParent(), file.isDirectory());
			copyFile(fObj, msg);
		}

	}

	public void copyFile(FileObj fObj, StringBuffer msg) {

		fObj.init();

		File dir = new File(fObj.getdPath());
		if (!dir.exists())
			dir.mkdirs();

		File dest = new File(fObj.getdPath() + File.separator + fObj.getName());
		File source = new File(fObj.getsPath() + File.separator + fObj.getName());

		int result = 0;

		if (fObj.isDirectory) {
			String dirPath = fObj.getPath() + File.separator + fObj.getName();
			copyDir(new File(dirPath), msg);
			result = 2;
		} else {
			result = FileChooser.copyFile(source, dest);
			if (result > 0)
				succCopyCnt++;
			else
				failCopyCnt++;
		}

		msg.append(fObj.getName() + (result == 1
				// 성공
				? " >> <span style='color:green'>" + fObj.getdPath().substring(fObj.getdPath().indexOf(strDirNm))
						+ File.separator + fObj.getName() + "</span> <br/>"
				// 폴더
				: result == 2 ? " >> <span style='color:orange'>Directory</span> <br/>"
						// 실패
						: " :: <span style='color:red'> FAIL </span> <br/>"));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void resetCopyHistName() {
		if (copyHistNames.size() > 0) {
			String[] models = copyHistNames.toArray(new String[copyHistNames.size()]);
			comboBoxes.get(0).setModel(new DefaultComboBoxModel(models));
			comboBoxes.get(0).setSelectedItem(copyHistNames.contains(strDirNm) ? strDirNm : copyHistNames.size() - 1);
		} else {
			String[] models = { defaultCopyHistName };
			comboBoxes.get(0).setModel(new DefaultComboBoxModel(models));
			comboBoxes.get(0).setSelectedItem(defaultCopyHistName);
		}
	}

	public void changeCopyHist() {

		String copyHistName = (String) comboBoxes.get(0).getSelectedItem();
		panels.get(0).removeAll();
		this.strDirNm = copyHistName.equals(defaultCopyHistName) ? defaultStreDirNm : copyHistName;

		if (copyHistNames.contains(copyHistName)) {
			for (Component c : copyHists.get(copyHistName)) {
				panels.get(0).add(c);
			}
		}

		repaintPanel();

	}

	public void repaintPanel() {
		panels.get(0).revalidate();
		int cnt = panels.get(0).getComponentCount();
		buttons.get(0).setText(cnt + "개 " + btnName);
		panels.get(0).repaint();
	}

	public void shortcutEvent(KeyEvent e) {

		// ESC
		if (e.getKeyCode() == 27) {
			this.frame.dispose();
			return;
		}

		// ENTER
		if (e.getKeyCode() == 10) {
			// Shift
			if (!e.isShiftDown()) {

			} else {

			}
		}

	}

	/*
	 * File Object
	 */

	public class FileObj extends JPanel {

		private String name;
		private String path;
		private boolean isDirectory;

		private String sPath;
		private String dPath;

		private String rootDirNm;

		public FileObj(String name, String path, boolean isDirectory) {

			FileObj thisObj = this;

			this.name = name;
			this.path = path;
			this.isDirectory = isDirectory;
			this.setBackground(mainColor);

			JTextField nmLabel = new JTextField(name, 15);
			nmLabel.setDisabledTextColor(Color.black);
			nmLabel.setEnabled(false);
			nmLabel.setToolTipText(name);

			JButton delLabel = new JButton("삭제");
			delLabel.setFont(small);
			delLabel.setPreferredSize(new Dimension(60, 20));
			delLabel.setBackground(new Color(150, 210, 210, 255));

			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 40;

			this.add(nmLabel, gbc);

			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 0;

			this.add(delLabel, gbc);

			delLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panels.get(0).remove(thisObj);
					repaintPanel();
				}
			});

		}

		public void init() {

			try {

				String rootDirNm = strDirNm + currentTime;
				String dest = Path.DESKTOPPATH + File.separator + rootDirNm;

				sPath = path;

				if (path.indexOf("src\\main\\java") > 0) {
					String orgin = "src\\\\main\\\\java";
					String target = "target" + File.separator + "classes";
					String[] paths = path.split(orgin);
					sPath = path = paths[0] + target + (paths.length > 1 ? paths[1] : "");
					dPath = dest + File.separator + "classes" + (paths.length > 1 ? paths[1] : "");
					if (!this.isDirectory)
						this.name = name.substring(0, name.lastIndexOf(".")) + ".class";
				} else if (path.indexOf("src\\main\\resources") > 0) {
					// .xml, .properties
					String orgin = "src\\\\main\\\\resources";
					String[] paths = path.split(orgin);
					dPath = dest + File.separator + "classes" + (paths.length > 1 ? paths[1] : "");
				} else if (path.indexOf("target\\classes") > 0) {
					// .xml, .properties, .class
					String orgin = "target\\\\classes";
					String[] paths = path.split(orgin);
					dPath = dest + File.separator + "classes" + (paths.length > 1 ? paths[1] : "");
				} else if (path.indexOf("src\\main") > 0) {
					// jsp, js, images..... static file
					String orgin = "src\\\\main";
					String[] paths = path.split(orgin);
					dPath = dest + (paths.length > 1 ? paths[1] : "");
				} else if (dPath != null && !dPath.equals("") && this.rootDirNm != null && !this.rootDirNm.equals("")) {
					// .java recopy
					dPath = dPath.replaceAll(this.rootDirNm, rootDirNm);
				} else {
					// just projct name copy

					throw new Exception(" /src/main/ 하위 파일만 복사 가능합니다. ");
				}

				this.rootDirNm = rootDirNm;

			} catch (Exception err) {
				TrayIconHandler.displayMessage("ERROR", name + " ::: FAIL " + err.getMessage(), MessageType.ERROR);
				err.printStackTrace();
				return;
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getsPath() {
			return sPath;
		}

		public void setsPath(String sPath) {
			this.sPath = sPath;
		}

		public String getdPath() {
			return dPath;
		}

		public void setdPath(String dPath) {
			this.dPath = dPath;
		}

	}

}
