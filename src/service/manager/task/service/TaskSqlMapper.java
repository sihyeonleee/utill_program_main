package service.manager.task.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import comm.remote.DBConnection;
import service.cmmn.service.CmmnSqlSession;
import service.manager.TaskManagerService;

public class TaskSqlMapper extends CmmnSqlSession{
	
	// 경로
	private static String FILEDBNAME = "INFO.db";
	private static String PATH = TaskManagerService.SERVICEDIR + File.separator + FILEDBNAME;
	
	public TaskSqlMapper() {
		super("TASK", "Task Manage", PATH, DBConnection.SQLITE, "", "");
	}
	
	@Override
	public String[] validCheck() {
		return new String[]{"TASK_DOMAIN", "TASK_TAG", "TASK_SCHD", "TASK_FILE"};
	}

	public List<Map<String, Object>> selectTaskList(Map<String, Object> param){
		return sql.selectList("task.selectTaskDomainList", param);
	}
	
	public List<Map<String, Object>> selectTaskTagList(Map<String, Object> param){
		return sql.selectList("task.selectTaskTagList", param);
	}
	
	public List<Map<String, Object>> selectTaskSchdList(Map<String, Object> param){
		return sql.selectList("task.selectTaskSchdList", param);
	}
	
	public List<Map<String, Object>> selectTaskTagFileList(Map<String, Object> param){
		return sql.selectList("task.selectTaskTagFileList", param);
	}
	
	public List<Map<String, Object>> selectTaskSchdFileList(Map<String, Object> param){
		return sql.selectList("task.selectTaskSchdFileList", param);
	}
	
	public List<Map<String, Object>> selectSearchKeyword(Map<String, Object> param){
		return sql.selectList("task.selectSearchKeyword", param);
	}
	
	public List<Map<String, Object>> selectSearchKeywordFile(Map<String, Object> param){
		return sql.selectList("task.selectSearchKeywordFile", param);
	}
	
	public int insertTaskDomain(Map<String, Object> param){
		return sql.insert("task.insertTaskDomain", param);
	} 
	
}
