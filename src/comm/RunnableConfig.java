package comm;

import java.util.ArrayList;
import java.util.List;

public class RunnableConfig {
	
	/**
	 * name 앞에 # 붙힐경우 multiple 작동
	 * =====================================
	 * argument > Server.mode = DEV, PROD
	 * -DServer.mode=DEV
	 * Command Line Run : java -DServer.mode=DEV -jar .\\utillService_1_1_0.jar
	 */
	
	/*
	 * 개발 서비스
	 * */ 
	@SuppressWarnings("rawtypes")
	public static List<ArrayList<String>> getDevServiceList(){
		List<ArrayList<String>> list = new ArrayList<>();
		
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> events = new ArrayList<>();
		
		names.add("파일관리"				 ); events.add("manager.FileManager"  );
//		names.add("파일복사"				 ); events.add("write.FilePathCopy"   );
		names.add("---------------------"); events.add("---------------------");
		names.add("계산"					 ); events.add("tmp.CALC"             );
		names.add("Log"					 ); events.add("config.Logger"        );
		names.add("HotKey"				 ); events.add("config.HotKey"		  );
		names.add("Workspace"			 ); events.add("config.Workspace"     );
		names.add("종료"					 ); events.add("config.Exit"          );
		
		list.add(names);
		list.add(events);
		
		return list;
	}
	
	/*
	 * 운영 서비스
	 * */ 
	@SuppressWarnings("rawtypes")
	public static List<ArrayList<String>> getOprServiceList(){
		List<ArrayList<String>> list = new ArrayList<>();
		
		ArrayList<String> names = new ArrayList<>();
		ArrayList<String> events = new ArrayList<>();
		
		names.add("파일관리"				 ); events.add("manager.FileManager"  );
//		names.add("파일복사"				 ); events.add("write.FilePathCopy"   );
		names.add("---------------------"); events.add("---------------------");
		names.add("계산"					 ); events.add("tmp.CALC"             );
		names.add("Log"					 ); events.add("config.Logger"        );
		names.add("HotKey"				 ); events.add("config.HotKey"		  );
		names.add("Workspace"			 ); events.add("config.Workspace"     );
		names.add("종료"					 ); events.add("config.Exit"          );
		
		list.add(names);
		list.add(events);
		
		return list;
	}
	
	
}
