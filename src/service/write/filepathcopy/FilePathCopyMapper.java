package service.write.filepathcopy;

import java.io.File;
import java.util.List;
import java.util.Map;

import comm.remote.DBConnection;
import service.cmmn.service.CmmnSqlSession;

public class FilePathCopyMapper extends CmmnSqlSession {

	// 경로
	private static String FILEDBNAME = "INFO.db";
	private static String PATH = "pathcopy" + File.separator + FILEDBNAME;

	public FilePathCopyMapper() {
		super("FilePathCopy", "FilePathCopy", PATH, DBConnection.SQLITE, "", "");
	}

	@Override
	public String[] validCheck() {
		return new String[] { "COPY_MANAGE", "COPY_HIST" };
	}

	public List<Map<String, Object>> selectTaskList(Map<String, Object> param) {
		return sql.selectList("task.selectTaskDomainList", param);
	}

	public List<Map<String, Object>> selectTaskTagList(Map<String, Object> param) {
		return sql.selectList("task.selectTaskTagList", param);
	}

	public List<Map<String, Object>> selectTaskSchdList(Map<String, Object> param) {
		return sql.selectList("task.selectTaskSchdList", param);
	}

	public List<Map<String, Object>> selectTaskTagFileList(Map<String, Object> param) {
		return sql.selectList("task.selectTaskTagFileList", param);
	}

	public List<Map<String, Object>> selectTaskSchdFileList(Map<String, Object> param) {
		return sql.selectList("task.selectTaskSchdFileList", param);
	}

	public List<Map<String, Object>> selectSearchKeyword(Map<String, Object> param) {
		return sql.selectList("task.selectSearchKeyword", param);
	}

	public List<Map<String, Object>> selectSearchKeywordFile(Map<String, Object> param) {
		return sql.selectList("task.selectSearchKeywordFile", param);
	}

	public int insertTaskDomain(Map<String, Object> param) {
		return sql.insert("task.insertTaskDomain", param);
	}

}
