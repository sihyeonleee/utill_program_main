package service._example;

import java.util.List;
import java.util.Map;

import comm.remote.DBConnection;
import gui.obj.CompObj;
import gui.obj.FrameObj;
import service.Service;

public class DynamicQueryService extends Service{
	
	DBConnection db = null;
	
	public DynamicQueryService() {
		
		// 프레임 설정
		width = 600;
		height = 600;
		layout = FrameObj.LAYOUT_FLOW;
		
		// 컴포넌트설정
		CompObj input1 = new CompObj();
		input1.setType(CompObj.TYPE_INPUT);
		input1.setEventType(CompObj.EVENT_ACTION);
		componentObjs.add(input1);
		
		CompObj btn1 = new CompObj();
		btn1.setType(CompObj.TYPE_BUTTON);
		btn1.setEventType(CompObj.EVENT_ACTION);
		componentObjs.add(btn1);
		
		CompObj area1 = new CompObj();
		area1.setType(CompObj.TYPE_TEXTAREA);
		area1.setScrollAt(true);
		area1.setCharY(30);
		area1.setCharX(50);
		componentObjs.add(area1);
		
//		createDbObject(0);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(String type, CompObj obj, Object...objects) {
		String query = inputs.get(0).getText();
			
		Map<String, Object> result = db.execute(DBConnection.EXECUTE_QUERY, query);
		
		for(Map<String, Object> o : (List<Map<String, Object>>)result.get("data")) {
			appendMessage("textArea", 0, (String) o.get("COLUMN_NAME")+"\n");
		}
		
	}
	
}
