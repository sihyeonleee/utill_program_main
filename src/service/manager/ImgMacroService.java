package service.manager;

import java.awt.GridBagConstraints;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import comm.CustomThread;
import comm.ImageSearch;
import comm.fileio.FileChooser;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;

public class ImgMacroService extends Service {

	public ImgMacroService() {

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
		
		// 컴포넌트 설정
		CompObj btn2 = new CompObj();
		btn2.setName("버튼2");
		btn2.setEvtName("button2");
		btn2.setType(CompObj.TYPE_BUTTON);
		btn2.setEventType(CompObj.EVENT_ACTION);
		btn2.setGridPosition(0, 1);
		btn2.setGridWeight(1, 1);
		btn2.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(btn2);

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
	
	File file = null;

	@Override
	public void onEvent(String type, CompObj obj, Object... objects) throws Exception {

		String threadName = "ImgMacroService";
		runThread(threadName, false);

		if (type.equals("click") && "button1".equals(obj.getEvtName())) {
			FileChooser ch = new FileChooser();
			File[] files = ch.doSelect();
			
			if (files != null && files.length > 0) {
				file = files[0];
			}
		}else if (type.equals("click") && "button2".equals(obj.getEvtName())) {
			ImageSearch search = new ImageSearch();
			
			CompletableFuture
		    .supplyAsync(() -> search.findMatchesBlocking(file, 0.6f))
		    .thenApplyAsync(matches1 -> {
		        matches1.forEach(m -> {
		            try {
		                new Screen().mouseMove(m);
		                Thread.sleep(300);
		            } catch (Exception ignored) {}
		        });
		        return null;
		    })
		    .thenApplyAsync(none -> {
		        List<Match> matches2 = search.findMatchesBlocking(file, 0.6f);
		        matches2.forEach(m -> {
		            try {
		                new Screen().click(m);
		                Thread.sleep(300);
		            } catch (Exception ignored) {}
		        });
		        return null;
		    })
		    .thenRunAsync(() -> {
		        List<Match> matches3 = search.findMatchesBlocking(file, 0.6f);
		        matches3.forEach(m -> {
		            try {
		                m.highlight(1.0);
		                Thread.sleep(300);
		            } catch (Exception ignored) {}
		        });
		    });
		}

	}

	@Override
	public CustomThread createThread() {
		return new CustomThread() {
			@Override
			public void run() {

			}
		};
	}
}
