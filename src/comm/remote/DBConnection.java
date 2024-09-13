package comm.remote;

import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import gui.main.TrayIconHandler;

public class DBConnection {
	
	public static String MYSQL = "com.mysql.jdbc.Driver";
	public static String POSTGRESQL = "org.postgresql.Driver";
	public static String SQLITE = "org.sqlite.JDBC";
	
	public static String MYBATIS_CONFIG = "mybatis/config/mybatis_config.xml";
	
	public static int EXECUTE_QUERY = 1;
	public static int EXECUTE_UPDATE = 2;
	public static int NONTIME_QUERY = 3;
	public static int NONTIME_UPDATE = 4;
	
	private String name;
	private String tag;
	private String url;
	private String driver;
	private String userNm;
	private String userPw;
	
	private Connection conn = null;
	
	boolean isConnection = false;
	
	boolean isError = false;
	
	public DBConnection(String name, String url, String driver, String userNm, String userPw){
		this.name = name;
		this.url = url;
		this.driver = driver;
		this.userNm = userNm;
		this.userPw = userPw;
		
//		System.out.println( name + " // " + connection() );
//		disConnection();
	}
	
	public DBConnection(String name, String tag, String url, String driver, String userNm, String userPw){
		this.name = name;
		this.tag = tag;
		this.url = url;
		this.driver = driver;
		this.userNm = userNm;
		this.userPw = userPw;
		
//		System.out.println( name + " // " + connection() );
//		disConnection();
	}
	
	public boolean connection(){
		
		try {
			
			Class.forName(driver);
			
			try {
				
				conn = DriverManager.getConnection(url, userNm, userPw);
				isConnection = true;
				
			} catch (SQLException err) {
				err.printStackTrace();
				isConnection = false;
			}
			
		} catch (ClassNotFoundException err) {
			err.printStackTrace();
			isConnection = false;
		} 
		
		return isConnection;
	}
	
	public boolean disConnection(){
		
		try {
			if(conn != null){
				conn.close();
				isConnection = false;
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
		
		return isConnection;
	}
	
	public boolean isConnection(){
		return isConnection;
	}
	
	@SuppressWarnings("resource")
	public synchronized Map<String, Object> execute(int executeType, String query, String... params){
		
		if(!connection()) {
			TrayIconHandler.displayMessage("Database Not Connection", "DB설정에서 데이터베이스 설정을 확인해주세요.", MessageType.ERROR);
			return null;
		}
		
		PreparedStatement pstmt = null;
		
		ResultSet result = null;
		
		Map<String, Object> resultData = new HashMap<>();
		
		try {
			
			// 쿼리 셋팅
			pstmt = conn.prepareStatement( query );
			
			int i = 1;
			for(String param : params){
				pstmt.setString(i++, param);
			}
			
			/*for(int i=0; i<params.length; i++){
				
				if(params[i].equals("")) {
					continue;
				}
				
				pstmt.setString(i+1, params[i]);
				
			}*/
			
			// 쿼리 실행
			if( executeType == EXECUTE_QUERY ) {
				result = pstmt.executeQuery();
				resultData.put("data", getDataProc(result));
			}else if( executeType == EXECUTE_UPDATE ){
				int resultInt = pstmt.executeUpdate();
				resultData.put("data", resultInt);
			}else if (executeType == NONTIME_QUERY){
				setMaxExecutionTime();
				result = pstmt.executeQuery();
				resultData.put("data", getDataProc(result));
			}else if (executeType == NONTIME_UPDATE){
				setMaxExecutionTime();
				int resultInt = pstmt.executeUpdate();
				resultData.put("data", resultInt);
			}
			
			// 실행쿼리 셋팅 ( 쿼리문은 쿼리의 실행전, 후가 다르다 ) 
			String statementText = pstmt.toString();
			String executeQuery = statementText.substring( statementText.indexOf( ": " ) + 2 );
			resultData.put("query", executeQuery);
			
		} catch (SQLException err) {
			
			TrayIconHandler.displayMessage("Database Exception", "시스템 로그를 확인해주세요", MessageType.ERROR);
			
			err.printStackTrace();
			
		} finally {
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException err) {
					err.printStackTrace();
				}
			}
			
			if(result != null) {
				try {
					result.close();
				} catch (SQLException err) {
					err.printStackTrace();
				}
			}
			
			disConnection();
		}
		
		return resultData;
		
	}
	
	public List<Map<String, Object>> getDataProc(ResultSet result) throws SQLException{
		
		List<Map<String, Object>> list = new ArrayList<>();
		
		ResultSetMetaData md = result.getMetaData();
		
		int colCnt = md.getColumnCount();
		
		while(result.next()){
			
			Map<String, Object> info = new HashMap<String, Object>();
			
			for( int i = 0; i < colCnt; i++ ){
				
				int id = i + 1;
				
				Object obj = result.getObject( id );
				
				info.put( md.getColumnName( id ), obj );
				
			}
			
			list.add(info);
			
		}
		
		return list;
		
	}
	
	public synchronized void rollback(){
		try {
			isError = true;
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setMaxExecutionTime() throws SQLException{
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement( "SET session max_execution_time=6000000" );
		pstmt.executeUpdate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public SqlSession getSqlSession(boolean isLongQuery){
		
		Properties properties = new Properties();
		properties.setProperty("driver", driver);
		properties.setProperty("url", url);
		properties.setProperty("username", userNm);
		properties.setProperty("password", userPw);
		
		Reader reader;
		
		try {
			reader = Resources.getResourceAsReader(MYBATIS_CONFIG);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, properties);
			SqlSession sql = sqlSessionFactory.openSession(true);
			
			if(isLongQuery){
				sql.insert("avgservice.setMaxExecutionTime");
			}
			
			return sql;
		} catch (IOException err) {
			err.printStackTrace();
			return null;
		}
	}
	
	
}
