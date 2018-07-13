package com.powerwin.entity;

import java.text.DecimalFormat;
import java.util.Map;


public class Common  {
	
	//实体数据类型转化
	public Object getValue(Map<String, Object> vals , String name , String type){
		Object val = vals.get(name);
		try {
			if(val != null){
				if(type == "float" ){
					DecimalFormat decimalFormat = new DecimalFormat("#.##");
					String tmp = decimalFormat.format(val);
					return Float.parseFloat(tmp);
				}else {
					return val;
				}
			}else{
				if(type == "int"){
					val = 0;
				}else if(type == "float" ){
					val = Float.valueOf(0);
				}else if(type == "string"){
					val = "";
				}
			}
			return val;
		} catch (Exception e) {
			return val;
		}
	}
	
//	public static void main(String[] args) {
//		SQLConnection conn = SQLConnection.getInstance("main");
//		//Jedis jedis = RedisConnection.getInstance("main");
//
//
//
//	   // String sql = "SELECT a.id as planid, b.cpid as cpid,a.name as name,type,url,ctype,cstype,b.id as id,b.name as unitname, day_money,one_money,start_time,end_time,bj_start_time,bj_end_time,senddate,bjhourval,sendtype,sendarea,senddevmedia,shopname,shopdesc,onedesc,buttondesc,showpic,xxpic,b.status as unitstatus,statusres FROM adsplan a,adsunit b WHERE a.id=b.apid AND ( a.update_time>? or update_time>?)";
//        String sql = "SELECT a.id planid, b.cpid as cpid,a.name as name,type,url,ctype,cstype,b.id as id,b.name as unitname, day_money,one_money,start_time,end_time,bj_start_time,bj_end_time,senddate,bjhourval,sendtype,sendarea,senddevmedia,shopname,shopdesc,onedesc,buttondesc,showpic,xxpic,b.status as unitstatus,statusres FROM adsplan a,adsunit b WHERE a.id=b.apid";
//
//		List<Map<String,Object>> items = conn.queryMap(sql, new Object[] {  });
//		if (!items.isEmpty()) {
//			for (Map<String,Object> vals : items) {
//				Ads ads = new Ads(vals);
//
//				System.out.println(ads.getId()+ ", " + ads.getPlanid());
//			}
//		}
//		conn.close();
//	}
}