package com.jianyu.hive;

import com.jianyu.thread.ThreadToHive;

/**
 * ��HiveSQL���д��Excel��֧�ֶ��߳�
 * @author BaiJianyu
 */
public class QueryData {
	public static void main(String[] args) {
		String fileType1="ϵͳ��ǰʱ��";
		String querySQL1 = "select from_unixtime(unix_timestamp(),'yyyy-MM-dd HH:mm:ss')  as `ϵͳ��ǰʱ��`  ";
		
		String fileType2="�̻�����";
		String querySQL2 = "select a.retail_num as `�̻�����` from retail_info " ;
		
		// ���߳�ִ�в�ѯ��������ʡ��ѯʱ��
		new ThreadToHive(querySQL1,fileType1).start();
		new ThreadToHive(querySQL2,fileType2).start();
	}
}
