package com.powerwin.parser;


import com.powerwin.boot.config.Define;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseParser是定义好的公共的解析策略
 *
 * ShowParser继承BaseParser后重写自己的特性  使用了内部接口，定义了规范
 */
public class ShowParser extends BaseParser implements Define.SourceIndex {

	public interface Index extends Define.SourceIndex {
		int IDS = 24;
	}
	
	public final static List<String> FIELD_LIST = new ArrayList<String>();
	
	static {
	    
		for(String field: Define.FIELDS) {

			FIELD_LIST.add(field);
		}
		for(String field: Define.SOURCE_FIELDS){

			FIELD_LIST.add(field);
		}

		FIELD_LIST.add(Define.IDS);
	}

	/**
	 * 抽象方法的具体实现：
	 * 返回的集合内容:
	 * TYPE,ACTION,FROM,YEAR,MON,DAY,HOUR,MIN,SEC,IP
	 * @return
	 */
	@Override
	protected List<String> fields() {
		return FIELD_LIST;
	}
	
	public static void main(String[] args) {
		
		
		//String line = "2016-01-24 00:00:19 223.102.2.0 sdk show offer_wall uid=10465&appid=8079&os=ios&osver=9.0.2&device=iPhone&mac=02:00:00:00:00:00&udid=67DA2E97-7706-4143-9610-6A67BB0D96B2&openudid=b90a0a2cac04b64ffd52b2f819e82ed19845c6d0&root=false&appuserid=719397&ids=111&session=&version=1.3&model=iPhone8,2\"";
        String line = "2018-07-04 15:24:23 203.151.232.104 sdk show offer_wall \"uid=5&appid=1&os=MacOSX&osver=67.0.3396.99&device=Chrome&mac=&udid=9cf679f3f3a8c544f695c91bb6d3d52d&openudid=&root=&appuserid=11&ids=3&session=&version=1.0&model=0.1\"";
		ShowParser p = new ShowParser();
		List<Object> vals = p.parse(line);
		System.out.println(vals);
		
		int action = (Integer) vals.get(Index.ACTION);
		System.out.println(action);
	}
}
