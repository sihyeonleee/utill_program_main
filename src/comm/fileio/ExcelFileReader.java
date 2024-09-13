package comm.fileio;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import comm.Progress;

public class ExcelFileReader {
	
	public static final String EXEC_XLS = "xls";
	public static final String EXEC_XLSX = "xlsx";
	
	private Progress progress;
	
	private String path;
	private String name;
	private String exec;
	private int sheetIndex;
	
	private boolean readAsName = false;
	
	public ExcelFileReader(){
		init();
	}
	
	public ExcelFileReader(String path){
		this.path = path;
		init();
	}
	
	public ExcelFileReader(String path, int sheetIndex){
		this.path = path;
		this.sheetIndex = sheetIndex;
		init();
	}
	
	public void init(){
		ZipSecureFile.setMinInflateRatio(0);
		exec = path.substring(path.lastIndexOf(".")+1);
		name = path.substring(0, path.lastIndexOf("."));
	}
	
	public Map<String, Object> doRead(int... sheetIndex){
		
		Map<String, Object> resultData = new HashMap<>(); 
		Map<Object, List<List<Object>>> data = new HashMap<>(); 
		
		if(!EXEC_XLS.equals(exec) && !EXEC_XLSX.equals(exec)){
			resultData.put("result", "INVALIDEXEC");
			return resultData;
		}
		
		System.out.println("FILE INFO ## " + path + " // " + exec);
		
		FileInputStream fis = null;
		
		try {
			
            fis = new FileInputStream(path);
            
			Workbook workbook = WorkbookFactory.create(fis);
			
			if(sheetIndex == null || sheetIndex.length == 0){
				
				if(progress != null){
					progress.setTotProc(getSizeFromSheets(workbook));
				}
				
				data = readFromSheet(workbook);
				
			}else {
				
				for(int i=0; i<sheetIndex.length; i++){
					
					int index = sheetIndex[i];
					
					Sheet sheet = workbook.getSheetAt(index);
					
					if(progress != null){
						progress.setTotProc(getSizeFromRows(sheet));
					}
					
					String name = sheet.getSheetName();
					List<List<Object>> rowDataList = readFromRows(sheet);
					
					if(readAsName){
						data.put( name, rowDataList );
					}else {
						data.put( i, rowDataList );
					}
				}
				
			}

	    } catch (IOException err) {
	    	resultData.put("result", "FAIL");
			err.printStackTrace();
	    } finally {
	    	try {
				fis.close();
			} catch (IOException err) {
				err.printStackTrace();
			}
		}
		
		resultData.put("result", "SUCCESS");
		
		resultData.put("data", data);
		
		return resultData;
	}
	
	
	
	
	// Sheets Data Read
	public Map<Object, List<List<Object>>> readFromSheet(Workbook workbook){
		
		Map<Object, List<List<Object>>> sheetDataList = new HashMap<>();
		
		int i = 0;
		
		for (Iterator<Sheet> it = workbook.iterator(); it.hasNext(); i++){
			
			Sheet sheet = it.next();
			
			String sheetName = sheet.getSheetName();
			List<List<Object>> rowDataList = readFromRows( sheet );
			
			if(readAsName){
				sheetDataList.put( sheetName, rowDataList );
			}else {
				sheetDataList.put( i, rowDataList );
			}
		}
		
		return sheetDataList;
		
	}
	
	// Rows Data Read	
	public List<List<Object>> readFromRows( Sheet sheet ){
		
		List<List<Object>> rowDataList = new ArrayList<>();
		
		Iterator<Row> rowIt = sheet.iterator();
		
		while(rowIt.hasNext()){

			Row row = rowIt.next(); // 각 행 읽기
			
			rowDataList.add(readFromCells(row));
			
		}
        
        return rowDataList;
        
	}
	
	// Cells Data Read	
	public List<Object> readFromCells(Row row){
		
		List<Object> cellDataList = new ArrayList<>();
		
		Iterator<Cell> cellIt = row.iterator();
		
		while(cellIt.hasNext()){
			
			Cell cell = cellIt.next(); // 각 셀 읽기
			
			Object value = null;
            
            if(cell != null){
            	
                switch (cell.getCellType()) { // 각 셀에 담겨있는 데이터의 타입을 체크하고 해당 타입에 맞게 가져온다.
                    case NUMERIC:
                        value = cell.getNumericCellValue(); break;
                    case STRING:
                        value = cell.getStringCellValue(); break;
                    case BLANK:
                        value = cell.getBooleanCellValue(); break;
                    case ERROR:
                        value = cell.getErrorCellValue(); break;
					default: break;
                }
                
                cellDataList.add(value); // ROW 데이터 축적
                
            }
            
            if(progress != null){
            	progress.setNowProc(progress.getNowProc() + 1);
            }
			
		}
                
		return cellDataList;
		 
	}
	
	
	
	
	// Sheets Size
	public int getSizeFromSheets(Workbook workbook){
		int sizeFromSheets = 0;
		Iterator<Sheet> it = workbook.iterator();
		while(it.hasNext()) sizeFromSheets += getSizeFromRows(it.next());
		return sizeFromSheets;
	}
	
	// Rows Size
	public int getSizeFromRows(Sheet sheet){
		int sizeFromRows = 0;
		Iterator<Row> it = sheet.iterator();
		while(it.hasNext()) sizeFromRows += getSizeFromCells(it.next());
		return sizeFromRows;
	}
	
	// Cells Size
	public int getSizeFromCells(Row row){
		return row.getPhysicalNumberOfCells();
	}
	
	
	
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public String getExec() {
		return exec;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public boolean isReadAsName() {
		return readAsName;
	}

	public void setReadAsName(boolean readAsName) {
		this.readAsName = readAsName;
	}
	
}
