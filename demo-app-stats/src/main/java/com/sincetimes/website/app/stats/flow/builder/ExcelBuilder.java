package com.sincetimes.website.app.stats.flow.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.TimeUtil;
import com.sincetimes.website.core.common.support.Util;

/**
 * excel builder
 * HSSFWorkbook:是操作Excel2003以前（包括2003）的版本，扩展名是.xls
 * XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx
 */
public class ExcelBuilder extends Builder<HSSFWorkbook> {
	private HSSFWorkbook workbook = new HSSFWorkbook();
	private List<List<String>> wordsList = new ArrayList<>();
	private String title;
	private String author;
	private AtomicInteger incrSheetId = new AtomicInteger(0);
	public ExcelBuilder(){
		wordsList.add(new ArrayList<>());
	}
	@Override
	public Builder<HSSFWorkbook> setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public Builder<HSSFWorkbook> writeWords(String... words) {
		if (Util.isEmpty(words)) {
			return this;
		}
		wordsList.get(wordsList.size() - 1).addAll(Arrays.asList(words));
		return this;
	}

	@Override
	public Builder<HSSFWorkbook> writeLines(String... lines) {
		if (Util.isEmpty(lines)) {
			return this;
		}
		Arrays.stream(lines).forEach(this::appendLine);
		return this;
	}

	@Override
	public HSSFWorkbook createContent() {
		 HSSFSheet sheet = Util.isEmpty(title)?workbook.createSheet():workbook.createSheet(title + incrSheetId.getAndIncrement());
		 AtomicInteger rowIndex = new AtomicInteger(0);
		 HSSFCell cell;
		 HSSFRow row;
		 if(!Util.isEmpty(title)){
			 row = sheet.createRow(rowIndex.getAndIncrement());
			 cell = row.createCell(0);  
		     cell.setCellValue(title); 
		 }
		 if(!Util.isEmpty(author)){
			 row = sheet.createRow(rowIndex.getAndIncrement());
			 cell = row.createCell(0);  
			 cell.setCellValue(author); 
		 }
		 row = sheet.createRow(rowIndex.getAndIncrement());
		 cell = row.createCell(0);  
	     cell.setCellValue("create time:" + TimeUtil.getLocalTime()); 
	     //支持空行
	     wordsList.forEach(ss->{
	    	 HSSFRow rowNew = sheet.createRow(rowIndex.getAndIncrement());
	    	 if(!Util.isEmpty(ss)){
	    		 for(int i = 0; i < ss.size(); i++){
	    			 HSSFCell cellNew = rowNew.createCell(i);  
	    			 cellNew.setCellValue(ss.get(i)); 
	    		 }
	    	 }
	     });
		return workbook;
	}

	private ExcelBuilder appendLine(String line) {
		wordsList.get(wordsList.size() - 1).add(line);
		wordsList.add(new ArrayList<>());
		return this;
	}

	private ExcelBuilder appendLine() {
		wordsList.add(new ArrayList<>());
		return this;
	}

	@Override
	public Builder<HSSFWorkbook> writeLine() {
		return appendLine();
	}

	@Override
	public Builder<HSSFWorkbook> setAuthor(String author) {
		this.author = author;
		return this;
	}

	@Override
	public byte[] getByteArray() {
		 try (ByteArrayOutputStream bout = new ByteArrayOutputStream()){
			createContent().write(bout);
			return bout.toByteArray();
		} catch (IOException e) {//never happen
			LogCore.BASE.error("excel make a err!{}", e);
			return null;
		}
	}

	@Override
	public String getSuffixName() {
		return "xls";
	}
}
