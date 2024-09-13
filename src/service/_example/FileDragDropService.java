package service._example; 

import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import comm.CustomThread;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import main.Main;
import service.Service;  

public class FileDragDropService extends Service{ 

	public FileDragDropService(){ 

		// 프레임 설정
		width = 800;
		height = 800;
		layout = FrameObj.LAYOUT_GRIDBAG;

		CompObj area1 = new CompObj();
		area1.setEnabled(false);
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setArrangeType(GridBagConstraints.BOTH);
		componentObjs.add(area1);

	}

	@SuppressWarnings("serial")
	@Override
	public void doShow(String name) {

		
		textAreas.get(0).setDropTarget(new DropTarget() {
		    @SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
		        try {
		            evt.acceptDrop(DnDConstants.ACTION_COPY);
		            List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		            for (File file : droppedFiles) {
		                Main.alertPop(file.getPath());
		            }
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});	
		super.doShow(name);

	}

	@Override
	public void onEvent(String type, CompObj obj, Object...objects) throws Exception {

		String threadName = "FileDragDropService";
		runThread(threadName, false);

	}

	@Override
	public CustomThread createThread(){
		return new CustomThread(){
			@Override
			public void run() {
				textAreas.get(0).setDragEnabled(true);
			}
		};
	}
}
