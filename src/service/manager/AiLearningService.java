package service.manager; 

import java.awt.GridBagConstraints;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import comm.CustomThread;
import comm.Path;
import comm.fileio.FileChooser;
import comm.fileio.TextFileWriter;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;  

public class AiLearningService extends Service {

	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy.MM.dd");

    // prod/learn 디렉터리 (실제 구조에 맞게 필요하면 수정)
    private static final String LEARN_DIR =
            Path.WORKPATH + File.separator + "prop" + File.separator + "learn";

    // evtName -> 대상 경로(여러 개) 매핑
    private Map<String, List<String>> learnTargetMap = new HashMap<>();

    // 버튼이 몇 번째 row까지 사용했는지
    private int nextRow = 1;

    public AiLearningService() {

        // 프레임 설정
        width = 200;
        height = 200;
        divisionX = 100;
        divisionY = 107;
        isAlwasOnTop = true;
        layout = FrameObj.LAYOUT_GRIDBAG;

        // 2) 학습 추가 버튼
        CompObj addBtn = new CompObj();
        addBtn.setName("추가");
        addBtn.setEvtName("add_learning");
        addBtn.setType(CompObj.TYPE_BUTTON);
        addBtn.setEventType(CompObj.EVENT_ACTION);
        addBtn.setGridPosition(0, 0);
        addBtn.setGridWeight(1, 1);
        addBtn.setArrangeType(GridBagConstraints.BOTH);
        componentObjs.add(addBtn);

        // 1) .learn 파일에서 버튼 로딩
        loadLearnButtonsFromFile();
    }
    
    private void loadLearnButtonsFromFile() {
        File dir = new File(LEARN_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".learn"));
        if (files == null) return;

        for (File f : files) {
            try {
                List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                if (lines.size() < 2) continue; // 제목 + 경로 최소 1개

                String title = lines.get(0).trim();
                if (title.isEmpty()) continue;

                // 2줄째부터 끝까지: 경로들
                List<String> paths = new ArrayList<>();
                for (int i = 1; i < lines.size(); i++) {
                    String p = lines.get(i).trim();
                    if (!p.isEmpty()) {
                        paths.add(p);
                    }
                }
                if (paths.isEmpty()) continue;

                String evtName = "learn_" + f.getName(); // 파일명 기반으로 유니크하게

                CompObj btn = new CompObj();
                btn.setName(title);
                btn.setEvtName(evtName);
                btn.setType(CompObj.TYPE_BUTTON);
                btn.setEventType(CompObj.EVENT_ACTION);
                btn.setGridPosition(0, nextRow);
                btn.setGridWeight(1, 1);
                btn.setArrangeType(GridBagConstraints.BOTH);

                componentObjs.add(btn);
                learnTargetMap.put(evtName, paths);

                nextRow++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doShow(String name) {

        // Component Object Settings 
        super.doShow(name);

    }

    @Override
    public void onEvent(String type, CompObj obj, Object... objects) throws Exception {

        if (!"click".equals(type)) {
            return;
        }

        // 1) 학습 추가
        if ("add_learning".equals(obj.getEvtName())) {
            addLearningEntry();
            return;
        }

        // 2) 기존에 로드된 학습 버튼 클릭
        String evtName = obj.getEvtName();
        if (learnTargetMap.containsKey(evtName)) {
            List<String> targetPaths = learnTargetMap.get(evtName);
            String title = obj.getName();
            if (targetPaths != null && !targetPaths.isEmpty()) {
                runLearning(targetPaths, title);
            }
        }
    }
    
    private void addLearningEntry() {
        // 1) 디렉터리/파일 선택 (복수 선택 가능하게 설계 가정)
        FileChooser ch = new FileChooser(JFileChooser.FILES_AND_DIRECTORIES);
        File[] files = ch.doSelect();

        if (files == null || files.length == 0) {
            return;
        }

        List<File> selectedFiles = new ArrayList<>();
        for (File f : files) {
            if (f != null) {
                selectedFiles.add(f);
            }
        }
        if (selectedFiles.isEmpty()) {
            return;
        }

        File first = selectedFiles.get(0);

        // 2) 제목 입력 (기본값: 첫 번째 선택 파일/디렉터리 이름)
        String title = javax.swing.JOptionPane.showInputDialog(
                null, "버튼 제목을 입력하세요.", first.getName());
        if (title == null || title.trim().isEmpty()) {
            return;
        }
        title = title.trim();

        try {
            // 3) .learn 파일 생성
            File learnDir = new File(LEARN_DIR);
            if (!learnDir.exists()) learnDir.mkdirs();

            String fileName = System.currentTimeMillis() + ".learn";
            File learnFile = new File(learnDir, fileName);

            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            // 선택된 모든 경로를 줄마다 기록
            for (File f : selectedFiles) {
                sb.append(f.getAbsolutePath()).append("\n");
            }

            Files.write(learnFile.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));

            // 4) 메모리에도 버튼/경로 추가 (재실행 없이 바로 보이도록)
            String evtName = "learn_" + fileName;

            CompObj btn = new CompObj();
            btn.setName(title);
            btn.setEvtName(evtName);
            btn.setType(CompObj.TYPE_BUTTON);
            btn.setEventType(CompObj.EVENT_ACTION);
            btn.setGridPosition(0, nextRow);
            btn.setGridWeight(1, 1);
            btn.setArrangeType(GridBagConstraints.BOTH);
            btn.setIndex(componentObjs.size());

            componentObjs.add(nextRow, btn);
            appendObj(btn);

            List<String> pathList = new ArrayList<>();
            for (File f : selectedFiles) {
                pathList.add(f.getAbsolutePath());
            }
            learnTargetMap.put(evtName, pathList);
            nextRow++;

            // 프레임 다시 그리기
            frame.revalidate();
            frame.repaint();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 여러 basePath 를 처리하도록 변경
    private void runLearning(List<String> basePathList, String title) {

        // Windows 파일명에 못 들어가는 문자 정리
        String safeTitle = (title == null ? "" : title)
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .trim();

        if (safeTitle.isEmpty()) {
            safeTitle = "default";
        }
        
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));

        // 버튼 제목을 이용한 파일명
        String packageName          = now + "_" + safeTitle + "_package";
        String packageContentsName  = now + "_" + safeTitle + "_package_contents";

        // 유효한 파일/디렉터리만 수집
        List<File> fileList = new ArrayList<>();
        for (String p : basePathList) {
            if (p == null || p.trim().isEmpty()) continue;
            File f = new File(p.trim());
            if (f.exists()) {
                fileList.add(f);
            }
        }
        if (fileList.isEmpty()) {
            return;
        }

        File[] files = fileList.toArray(new File[0]);

        try {
            // 트리 구조(디렉터리+파일 목록)
            StringBuilder treeBuilder = new StringBuilder();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                boolean isLast = (i == files.length - 1);
                printDirectoryTree(file, "", treeBuilder, isLast);
            }
            TextFileWriter treeWriter =
                    new TextFileWriter(Path.DESKTOPPATH, packageName, "txt");
            treeWriter.write(treeBuilder.toString(), false);

            // 실제 내용 모으기
            StringBuilder contentBuilder = new StringBuilder();
            for (File file : files) {
                if (file.isDirectory()) {
                    collectFileContents(file, file, contentBuilder);
                } else {
                    appendFileWithRelativePathHeader(file.getParentFile(), file, contentBuilder);
                }
            }
            TextFileWriter contentWriter =
                    new TextFileWriter(Path.DESKTOPPATH, packageContentsName, "txt");
            contentWriter.write(contentBuilder.toString(), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void printDirectoryTree(File dir, String prefix, StringBuilder builder, boolean isLast) {
	    appendWithDate(prefix, isLast, dir, builder);
	
	    File[] children = dir.listFiles();
	    if (children == null || children.length == 0) return;
	
	    for (int i = 0; i < children.length; i++) {
	        File child = children[i];
	        boolean last = (i == children.length - 1);
	        if (child.isDirectory()) {
	            printDirectoryTree(child, prefix + (isLast ? "    " : "│   "), builder, last);
	        } else {
	            appendWithDate(prefix + (isLast ? "    " : "│   "), last, child, builder);
	        }
	    }
	}
    
    private void appendWithDate(String prefix, boolean isLast, File file, StringBuilder builder) {
        String lastModifiedStr = DATE_FMT.format(new Date(file.lastModified()));

        builder.append(prefix)
               .append(isLast ? "└── " : "├── ")
               .append(file.getName())
               .append("(")
               .append(lastModifiedStr)
               .append(")")
               .append("\n");
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
