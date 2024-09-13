package comm.fileio;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import gui.main.TrayIconHandler;

public class ExcelFileWriter {
	
	// 확장자
	public static final String EXEC_XLS = "xls";
	public static final String EXEC_XLSX = "xlsx";
	
	// POI 라이브러리
	private Workbook workBook = null;
	private Map<Object, Sheet> sheets = new HashMap<>();
	private Map<Object, Map<Object, Row>> rows = new HashMap<>();
	private Map<Object, Map<Object, Map<Object, Cell>>> cells = new HashMap<>();
	
	// 바탕화면 경로
	private String path = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
	private String fileName = "temp";
	private String exec = EXEC_XLS;
	
	
	public ExcelFileWriter(){
		
	}
	
	public ExcelFileWriter( String path, String fileName, String exec ){
		
		this.path = path;
		this.fileName = fileName;
		this.exec = exec;
		
		if( this.exec.equals(EXEC_XLS) ){
			workBook = new HSSFWorkbook();
		}else if( this.exec.equals(EXEC_XLSX) ){
			workBook = new XSSFWorkbook();
		}
		
	}
	
	public synchronized void addData( Object sheetIndex, int rowIndex, int colIndex, Object data, boolean isAdd ){
		
		createSheet(sheetIndex);
		
		Sheet sheet = sheets.get(sheetIndex);
		
		createRow(sheetIndex, rowIndex, sheet);
		
		Row row = rows.get(sheetIndex).get(rowIndex);
		
		createCel(sheetIndex, rowIndex, colIndex, row);
		
		Cell cell = null;
		
		try {
			cell = cells.get(sheetIndex).get(rowIndex).get(colIndex);
		}catch (Exception e ){
			
		}
		
		if(data instanceof Integer){
			if(isAdd){
				Double value = cell.getNumericCellValue();
				cell.setCellValue( (Integer) data + value );
			}else {
				cell.setCellValue( (Integer) data );
			}
		} else if( data instanceof Long ){
			if(isAdd){
				Long value = (long) cell.getNumericCellValue();
				cell.setCellValue( (Long) data + value );				
			}else {
				cell.setCellValue( (Long) data );
			}
		} else if( data instanceof Double ){
			if(isAdd){
				Double value = cell.getNumericCellValue();
				cell.setCellValue( (Double) data + value );
			}else {
				cell.setCellValue( (Double) data );
			}
		} else if( data instanceof String ){
			
			String[] datas = ((String) data).split("\n");
			cell.setCellValue( (String) data );
			
			/*for(String d : datas){
				cell.setCellValue( (String) data );
				CellStyle cs = workBook.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			}*/
		} else if( data instanceof Date ){
			cell.setCellValue( (Date) data );
		} else if( data instanceof Boolean ){
			cell.setCellValue( (Boolean) data );
		} else if( data instanceof Byte ){
			cell.setCellValue( (Byte) data );
		}
		
		
		sheet.autoSizeColumn(colIndex, true);
//		sheet.setColumnWidth(colIndex, (sheet.getColumnWidth(colIndex)) + 30 );
		
	}
	
	public void addData( Object sheetIndex, int rowIndex, int colIndex, Object data ){
		
		createSheet(sheetIndex);
		
		Sheet sheet = sheets.get(sheetIndex);
		
		createRow(sheetIndex, rowIndex, sheet);
		
		Row row = rows.get(sheetIndex).get(rowIndex);
		
		createCel(sheetIndex, rowIndex, colIndex, row);
		
		Cell cell = null;
		
		try {
			cell = cells.get(sheetIndex).get(rowIndex).get(colIndex);
		}catch (Exception e ){
			
		}
		
		if(data instanceof Integer){
			cell.setCellValue( (Integer) data );
		} else if( data instanceof Long ){
			cell.setCellValue( (Long) data );
		} else if( data instanceof Double ){
			cell.setCellValue( (Double) data );
		} else if( data instanceof String ){
			
			String[] datas = ((String) data).split("\n");
			cell.setCellValue( (String) data );
			
			/*for(String d : datas){
				cell.setCellValue( (String) data );
				CellStyle cs = workBook.createCellStyle();
			    cs.setWrapText(true);
			    cell.setCellStyle(cs);
			}*/
		} else if( data instanceof Date ){
			cell.setCellValue( (Date) data );
		} else if( data instanceof Boolean ){
			cell.setCellValue( (Boolean) data );
		} else if( data instanceof Byte ){
			cell.setCellValue( (Byte) data );
		}
		
		
		sheet.autoSizeColumn(colIndex);
		sheet.setColumnWidth(colIndex, Math.min(255 * 256, (int) ((sheet.getColumnWidth(colIndex)) * 1.5)) );
		
	}
	
	public void setAutoSize(){
		
		for(Map.Entry<Object, Sheet> m : sheets.entrySet()){
			Sheet sheet = m.getValue();
			for(int i=0; i<sheet.getRow(0).getPhysicalNumberOfCells(); i++){
				sheet.autoSizeColumn(i);
				sheet.setColumnWidth(i, (int) ((sheet.getColumnWidth(i)) * 1.5) );
			}
		}
	}

	public synchronized void createSheet(Object sheetIndex){
		if(sheets.get(sheetIndex) == null){
			sheets.put(sheetIndex, workBook.createSheet());
			workBook.setSheetName(workBook.getSheetIndex(sheets.get(sheetIndex)), sheetIndex+"");
		}
	}
	
	public synchronized void createRow(Object sheetIndex, int rowIndex, Sheet sheet){
		if(rows.get(sheetIndex) == null){
			rows.put(sheetIndex, new HashMap<Object, Row>());
		}
		
		if(rows.get(sheetIndex).get(rowIndex) == null){
			rows.get(sheetIndex).put(rowIndex, sheet.createRow(rowIndex));
		}
	}
	
	public void createCel(Object sheetIndex, int rowIndex, int colIndex, Row row){
		if(cells.get(sheetIndex) == null){
			cells.put(sheetIndex, new HashMap<Object, Map<Object, Cell>>());
		}
		
		if(cells.get(sheetIndex).get(rowIndex) == null){
			cells.get(sheetIndex).put(rowIndex, new HashMap<Object, Cell>());
		}
		
		if(cells.get(sheetIndex).get(rowIndex).get(colIndex) == null){
			cells.get(sheetIndex).get(rowIndex).put(colIndex, row.createCell(colIndex));
		}
	}
	
	public void mergeData(Object sheetIndex, int firstRow, int lastRow, int firstCol, int lastCol){
		
		Sheet sheet = sheets.get(sheetIndex);
		
		//열시작, 열종료, 행시작, 행종료 (자바배열과 같이 0부터 시작)
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol)); 
		
	}
	
	public int write(){
		return writeProc();
	}
	
	public int write(String fileName){
		this.fileName = fileName;
		return writeProc();
	}
	
	public int write(String path, String fileName){
		this.path = path;
		this.fileName = fileName;
		return writeProc();
	}
	
	public int writeProc(){
		
		int result = 0;
		
		File file = new File( this.path + File.separator + this.fileName + "." + this.exec );
        FileOutputStream fos = null;
        
        try {
        	
            fos = new FileOutputStream(file);
            workBook.write(fos);
            result = 1;
            
        } catch (FileNotFoundException err) {
        	
        	TrayIconHandler.displayMessage(err.getMessage(), err.getMessage(), MessageType.ERROR);
			err.printStackTrace();
			result = -1;
			
        } catch (IOException err) {

        	TrayIconHandler.displayMessage(err.getMessage(), err.getMessage(), MessageType.ERROR);
			err.printStackTrace();
			result = -1;
			
        } finally {
        	
            try {
            	
                if(workBook!=null) workBook.close();
                if(fos!=null) fos.close();
                
            } catch (IOException err) {
            	
            	TrayIconHandler.displayMessage(err.getMessage(), err.getMessage(), MessageType.ERROR);
    			err.printStackTrace();
    			result = -1;
    			
            }
            
        }
        
        return result;
        
	}
	
	
	
	
	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExec() {
		return exec;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}
	
}
