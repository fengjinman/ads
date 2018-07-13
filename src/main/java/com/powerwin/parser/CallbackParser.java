package com.powerwin.parser;


import com.powerwin.boot.config.Define;
import com.powerwin.util.ListUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * BaseParser是定义好的公共的解析策略
 *
 * CallbackParser的继承重写自己的特性  使用了内部接口，定义了规范
 */
public class CallbackParser extends BaseParser {

	public interface Index extends Define.Index {
		int CID = 10;
		int ADID = 11;
		int MAC = 12;
		int UDID = 13;
		int OPENUDID = 14;
		int ACTIVE_NUM = 15;
		int DISK = 16;
		int ISENABLE = 17;
		// by jackcao start add host screen
		int JOB_NUM = 18;
	}
	
	public final static List<String> FIELD_LIST = new ArrayList<String>();
	static {
		for(String field: Define.FIELDS) {
			FIELD_LIST.add(field);
		}
		
		FIELD_LIST.add(Define.CID);
		FIELD_LIST.add(Define.ADID);
		FIELD_LIST.add(Define.MAC);
		FIELD_LIST.add(Define.UDID);
		FIELD_LIST.add(Define.OPENUDID);
		FIELD_LIST.add(Define.ACTIVE_NUM);
		FIELD_LIST.add(Define.DISK);
		FIELD_LIST.add(Define.ISENABLE);
		// by jackcao start add host screen
		FIELD_LIST.add(Define.JOB_NUM);
	}

	/**
	 * 返回一些固定的枚举属性
	 * @return
	 */
	@Override
	protected List<String> fields() {
		return FIELD_LIST;
	}
	
	public static void main(String[] args) {
		
		String line = "2015-07-08 18:03:21 127.0.0.1 processor active offer_wall \"adid=123&mac=02:00:00:00:00:00&udid=afdafd-afAACA4A2FF5E&openudid=0eb087e9b65d2e30ca1c8fc7f6cf389f5350d225&active_num=12123&disk=7270387712\"";
		
		CallbackParser p = new CallbackParser();
		List<Object> vals = p.parse(line);
		System.out.println(vals);
		
		String udid = ListUtil.getString(vals, Index.UDID);
		System.out.println("udid : " + udid);
	}	
}