package com.jianyu.hive;

import com.jianyu.thread.ThreadToHive;

/**
 * 将HiveSQL结果写入Excel，支持多线程
 * @author BaiJianyu
 */
public class QueryData {
	public static void main(String[] args) {
		String fileType1="系统当前时间";
		String querySQL1 = "select from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')  as `系统当前时间`  ";
		
		String fileType2="商户代码";
		String querySQL2 = "select a.retail_num as `商户代码` from retail_info " ;
		
		// 多线程执行查询，大量节省查询时间
		new ThreadToHive(querySQL1,fileType1).start();
		new ThreadToHive(querySQL2,fileType2).start();
	}
}
