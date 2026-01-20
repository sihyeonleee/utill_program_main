package service.manager; 

import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JFileChooser;

import comm.CustomThread;
import comm.Path;
import comm.fileio.FileChooser;
import comm.fileio.TextFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;  

public class AiLearningService_old extends Service{ 

	public AiLearningService_old(){ 

		// 프레임 설정
		width = 200;
		height = 200;
		divisionX = 100;
		divisionY = 107;
		isAlwasOnTop = true;
		layout = FrameObj.LAYOUT_GRIDBAG;

		// 컴포넌트 설정
		CompObj btn1 = new CompObj();
		btn1.setName("버튼1");
		btn1.setEvtName("button1");
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		btn1.setGridPosition(0, 0);
		btn1.setGridWeight(1, 1);
		btn1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn1);
		
		CompObj result = new CompObj();
		result.setMsg("파일선택");
		result.setEnabled(false);
		result.setType(CompObj.TYPE_OUTPUT);
		result.setGridSize(1, 1);
		result.setGridWeight(30, 30);
		result.setGridPosition(0, 1);
		componentObjs.add(result);

	}

	@Override
	public void doShow(String name) {

		// Component Object Settings 
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		String threadName = "AiLearningService";
		runThread(threadName, false);
		
		String packageName = "package";
		String packageContentsName = "package_contents";

		if(type.equals("click") && "button1".equals(obj.getEvtName())){
			FileChooser ch = new FileChooser(JFileChooser.DIRECTORIES_ONLY);
			File[] files = ch.doSelect();
			
			if (files != null && files.length > 0) {
			    // 1. 트리 구조 저장
			    StringBuilder treeBuilder = new StringBuilder();
			    for (int i = 0; i < files.length; i++) {
			        File file = files[i];
			        boolean isLast = (i == files.length - 1);
			        if (file.isDirectory()) {
			            printDirectoryTree(file, "", treeBuilder, isLast);
			        } else {
			            treeBuilder.append(file.getName()).append("\n");
			        }
			    }
			    TextFileWriter treeWriter = new TextFileWriter(Path.DESKTOPPATH, packageName, "txt");
			    treeWriter.write(treeBuilder.toString(), false);

			    // 2. 파일 내용 저장
			    StringBuilder contentBuilder = new StringBuilder();
			    for (File file : files) {
			        if (file.isDirectory()) {
			            collectFileContents(file, file, contentBuilder);  // baseDir == file
			        } else {
			            appendFileWithRelativePathHeader(file.getParentFile(), file, contentBuilder);
			        }
			    }
			    TextFileWriter contentWriter = new TextFileWriter(Path.DESKTOPPATH, packageContentsName, "txt");
			    contentWriter.write(contentBuilder.toString(), false);
			    
			    String text = Path.DESKTOPPATH + "/" + packageName + ".txt \n" + Path.DESKTOPPATH + "/" + packageContentsName + ".txt";
			    
			    outputs.get(0).setText("바탕화면 확인");
			}
		}

	}
	
	private void printDirectoryTree(File dir, String prefix, StringBuilder builder, boolean isLast) {
	    builder.append(prefix)
	           .append(isLast ? "└── " : "├── ")
	           .append(dir.getName())
	           .append("\n");

	    File[] children = dir.listFiles();
	    if (children == null || children.length == 0) return;

	    for (int i = 0; i < children.length; i++) {
	        File child = children[i];
	        boolean last = (i == children.length - 1);
	        if (child.isDirectory()) {
	            printDirectoryTree(child, prefix + (isLast ? "    " : "│   "), builder, last);
	        } else {
	            builder.append(prefix)
	                   .append(isLast ? "    " : "│   ")
	                   .append(last ? "└── " : "├── ")
	                   .append(child.getName())
	                   .append("\n");
	        }
	    }
	}
	
	private static void collectFileContents(File baseDir, File current, StringBuilder builder) {
	    File[] children = current.listFiles();
	    if (children == null) return;

	    for (File child : children) {
	        if (child.isDirectory()) {
	            collectFileContents(baseDir, child, builder);
	        } else {
	            appendFileWithRelativePathHeader(baseDir, child, builder);
	        }
	    }
	}
	
	private static void appendFileWithRelativePathHeader(File baseDir, File file, StringBuilder builder) {
	    try {
	        // 상대 경로 생성
	        String relativePath = baseDir.toPath().relativize(file.toPath()).toString().replace("\\", "/");

	        builder.append("// ============== FileName ").append(relativePath).append(" =============\n");
	        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
	        for (String line : lines) {
	            builder.append(line).append("\n");
	        }
	        builder.append("// =============== FileEnd ").append(relativePath).append(" =============\n\n");
	    } catch (IOException e) {
	        builder.append("// [ERROR reading file: ").append(file.getAbsolutePath()).append("]\n\n");
	    }
	}



	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {

			}
		};
	}
}
