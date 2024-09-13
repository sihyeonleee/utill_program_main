package service.cmmn.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import comm.Path;
import comm.remote.DBConnection;

public abstract class CmmnSqlSession {
	
	protected DBConnection db;
	
	protected SqlSession sql;
	
	public CmmnSqlSession(String name, String tag, String url, String driver, String userNm, String userPw){
		
		String pullPath = comm.Path.ROOTPATH + url;
		
		// URL 매핑
		if(driver.equals(DBConnection.SQLITE)){
			pullPath = "jdbc:sqlite:/" + pullPath;
		}else if(driver.equals(DBConnection.MYSQL)){
			pullPath = "jdbc:mysql://" + pullPath;
		}
		
		// DB 연결
		db = new DBConnection(name, tag, pullPath, driver, userNm, userPw);
		
		try{
			// GET SQL Session 
			this.sql = db.getSqlSession(false);
			
			// SQLite 일경우 테이블 생성
			if(driver.equals(DBConnection.SQLITE)){
				
				// 테이블 목록 가져오기
				List<Map<String, Object>> list = sql.selectList("common.selectTables");
				
				// 테이블 목록 체크 :: 하위 클래스에서 테이블 목록 전달
				String[] createTableNms = validCheck();
				
				// 존재하는 테이블 이름 목록
				Map<String, String> nameList = new HashMap<>();
				if(list.size() > 0){
					for(Map<String, Object> map : list){
						String tableNm = (String) map.get("name");
						if(tableNm != null) nameList.put(tableNm, "");
					}
				}else {
					/*
					 * 테이블이 하나도 존재하지 않을경우 파일이 없음을 의미하므로 
					 * 외래키를 활성화 ( 최초 한번 설정 )해준다.
					 * */
					configCommonTable(name);
				}
				
				/*
				 * 필수테이블과 존재하는 테이블 목록을 비교 후 
				 * 필수값을 기준으로 테이블 생성
				 * */
				if(createTableNms != null)
					for(String tableNm : createTableNms)
						if(list == null || !nameList.containsKey(tableNm))
							sql.insert( "create." + tableNm );
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void configCommonTable(String name){
		sql.update( "create.updateEnableFrgnks" );
		sql.insert( "create.COMMONCODE" );
		sql.insert( "create.COMMONCODE_DATA_" + name.toUpperCase() );
	}
	
	public List<Map<String, Object>> selectCommonCode(Map<String, Object> param){
		return sql.selectList( "common.selectCmmnCode", param );
	}
	
	public Map<String, Object> selectCommonCodeInfo(String nm, String cn){
		
		Map<String, Object> param = new HashMap<>();
		
		param.put("codeNm", nm);
		
		param.put("codeCn", cn);
		
		return sql.selectOne( "common.selectCommonCodeInfo", param );
		
	}
	
	public abstract String[] validCheck();
	
	
	
}
