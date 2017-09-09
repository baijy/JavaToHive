package com.jianyu.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

/**
 * 查询数据库并写入Excel，支持多线程
 * @author BaiJianyu
 * @date 20170907
 */
public class ThreadToHive extends Thread {
	// 使用JDBC同样可支持Oracle、MYSQL，修改依赖jar包即可
	private final static String drivename = "org.apache.hive.jdbc.HiveDriver";
	private final static String url = "jdbc:hive2://127.0.0.1:10000";
	private final static String username = "username";
	private final static String password = "password";
	private final static String filepath = "C:\\Users\\jianyu.bai\\Desktop\\";

	public String querySQL;
	public String fileType;

	public ThreadToHive(String querySQL, String fileType) {
		this.querySQL = querySQL;
		this.fileType = fileType;
	}

	public void run() {
		try {
			Class.forName(drivename);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = null;
		Statement stmt = null;
		ResultSet res = null;
		System.out.println("-------------查询" + fileType + "开始，请稍等-------------");
		try {
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
			res = stmt.executeQuery(querySQL);
		} catch (Exception e) {
			System.out.println("查询" + fileType + "出错！");
			e.printStackTrace();
		}
		
		try {
			createExcel(res, fileType);
		} catch (Exception e) {
			System.out.println("生成" + fileType + "Excel出错！");
			e.printStackTrace();
		}
		
	}

	// 将ResultSet写入Excel文件
	public static void createExcel(ResultSet rs, String fileType) throws Exception {
		boolean flag = false;
		System.out.println("-------------把"+fileType+"的结果写入Excel文件开始-------------");
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Sheet1");

		// 向Excel中添加数据
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String columnName = null;
		String columnValue = null;
		int rowNum = 0;

		// 添加标题
		Row tileRow = sheet.createRow(rowNum);
		for (int i = 0; i < columnCount; i++) {
			sheet.setColumnWidth(i, 8000);
			columnName = rsmd.getColumnName(i + 1);
			tileRow.createCell(i).setCellValue(columnName);
		}
		rowNum++;

		while (rs.next()) {
			flag = true;
			Row contentRow = sheet.createRow(rowNum);
			for (int i = 0; i < columnCount; i++) {
				int index =i+1; //getObject的序号是从1开始的
				if (rs.getObject(index) != null) {
					columnValue = rs.getObject(index).toString(); // Number列强转可能存在问题
					contentRow.createCell(i).setCellValue(columnValue);
				} else {
					contentRow.createCell(i).setCellValue("");
				}
			}
			rowNum++;
		}
		if (flag == true) {
			// 为防止文件覆盖，取当前时间命名
			String dateStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			String fileName = filepath + fileType + dateStr + ".xls";
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			workBook.write(fos);
			fos.close();
			System.out.println("-------------把"+fileType+"的结果写入Excel文件结束，存放位置：" + fileName + " -------------");
		} else {
			System.out.println("-------------"+fileType+"没有差异结果，不生成文件-------------");
		}

	}
}
