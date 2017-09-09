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
 * ��ѯ���ݿⲢд��Excel��֧�ֶ��߳�
 * @author BaiJianyu
 * @date 20170907
 */
public class ThreadToHive extends Thread {
	// ʹ��JDBCͬ����֧��Oracle��MYSQL���޸�����jar������
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
		System.out.println("-------------��ѯ" + fileType + "��ʼ�����Ե�-------------");
		try {
			conn = DriverManager.getConnection(url, username, password);
			stmt = conn.createStatement();
			res = stmt.executeQuery(querySQL);
		} catch (Exception e) {
			System.out.println("��ѯ" + fileType + "����");
			e.printStackTrace();
		}
		
		try {
			createExcel(res, fileType);
		} catch (Exception e) {
			System.out.println("����" + fileType + "Excel����");
			e.printStackTrace();
		}
		
	}

	// ��ResultSetд��Excel�ļ�
	public static void createExcel(ResultSet rs, String fileType) throws Exception {
		boolean flag = false;
		System.out.println("-------------��"+fileType+"�Ľ��д��Excel�ļ���ʼ-------------");
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("Sheet1");

		// ��Excel���������
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String columnName = null;
		String columnValue = null;
		int rowNum = 0;

		// ��ӱ���
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
				int index =i+1; //getObject������Ǵ�1��ʼ��
				if (rs.getObject(index) != null) {
					columnValue = rs.getObject(index).toString(); // Number��ǿת���ܴ�������
					contentRow.createCell(i).setCellValue(columnValue);
				} else {
					contentRow.createCell(i).setCellValue("");
				}
			}
			rowNum++;
		}
		if (flag == true) {
			// Ϊ��ֹ�ļ����ǣ�ȡ��ǰʱ������
			String dateStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			String fileName = filepath + fileType + dateStr + ".xls";
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			workBook.write(fos);
			fos.close();
			System.out.println("-------------��"+fileType+"�Ľ��д��Excel�ļ����������λ�ã�" + fileName + " -------------");
		} else {
			System.out.println("-------------"+fileType+"û�в��������������ļ�-------------");
		}

	}
}
