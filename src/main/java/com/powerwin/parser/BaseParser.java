package com.powerwin.parser;


import com.powerwin.boot.config.Define;
import com.powerwin.entity.DateTime;
import com.powerwin.entity.KeyValue;
import com.powerwin.util.ParseUtil;
import com.powerwin.util.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 基础抽象类
 * 解析字符串的基本方法
 */
public abstract class BaseParser implements Parser {


	/**
	 * 返回一个角标，对应的值有5种
	 * "unknow","sdk","api","channel", "processor"
	 * @param value
	 * @return
	 */
	public int parseFrom(String value) {
		for(int i = 1; i< Define.FROMS.length; i++) {
			String v = Define.FROMS[i];
			if(v.equals(value)){
				return i;
			}
		}
		return 0;
	}


	/**
	 * 返回一个角标，对应值有两种
	 * "unknow","offer_wall"
	 * @param value
	 * @return
	 */
	public int parseType(String value) {
		for(int i=1;i<Define.TYPES.length;i++) {
			String v = Define.TYPES[i];
			if(v.equals(value)){
				return i;
			}
		}
		return 0;
	}


	/**
	 * 返回一个角标，对应值有6种
	 * "unknow","show","click","jump","active","job"
	 * @param value
	 * @return
	 */
	public int parseAction(String value) {
		for(int i=1;i<Define.ACTIONS.length;i++) {
			String v = Define.ACTIONS[i];
			if(v.equals(value)) {
				return i;
			}
		}
		return 0;
	}
	
	protected boolean isDefinedISP(String val) {
		return Define.ISP_LIST.contains(val);
	}
	
	protected boolean isDefinedLocation(String val) {
		return Define.LOCATION_LIST.contains(val);
	}
	
	protected float parseFloat(String val) {
		Token<Float> token = ParseUtil.parseFloat(val.toCharArray(), 0, val.length());
		return token == null ? 0.0f : token.data;
	}

	protected int parseOS(String val) {
		val = val.toLowerCase();
		
		for(int i=0;i<Define.OSS.length;i++) {
			if(Define.OSS[i].equals(val)){
				return i;
			}
		}
		return -1;
	}
	
	protected int parseDevice(String val) {

		val = val.toLowerCase();
		for(int i=0;i<Define.DEVICES.length;i++) {
			if(Define.DEVICES[i].equals(val)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * 如果为空返回00:00:00:00:00:00
	 * 不为空并且长度等于12或17 返回原值
	 * @param val
	 * @return
	 */
	protected Object parseMAC(String val) {
		String d = "00:00:00:00:00:00";
		if(val.length() != 17 && val.length() != 12){
			return d;
		}
		return val.length() == 0?d:val;
	}


	/**
	 * 处理了空值
	 * @param val
	 * @return
	 */
	private Object parseUDID(String val) {
		
		if(val == null || val.length() == 0){
			return "";
		}
	    return val;

		//return val.toUpperCase();
	}
	
	private Object parseOpenUDID(String val) {
		return val;
	}
	/**
	 * 参数是带小数点的小数，返回值为去掉小数后的int类型整数
	 * 如果出现解析异常，返回值为-1
	 * @param val
	 * @return
	 */
	public int parseInt(String val) {
		try {
			if(val.contains(".")){
				int i = val.indexOf(".");
				val = val.substring(0,i);
			}
			return Integer.parseInt(val);
		} catch(Exception e) {
			return -1;
		}
	}

	/**
	 * 参数是带小数点的小数，返回值为去掉小数后的long类型整数
	 * 如果出现解析异常，返回值为-1
	 * @param val
	 * @return
	 */
	public long parseLong(String val) {
		try {
			if(val.contains(".")){
				int i = val.indexOf(".");
				val = val.substring(0,i);
			}
			return Long.parseLong(val);
		} catch(Exception e) {
			return -1;
		}
	}
	
	public int parseMediaType(int from) {
		if(from == 1 || from == 2) {
			return 1;
		}else if(from == 3){
			return 2;
		}else {
			return 0;
		}
	}

	/**
	 * 解析字符串，返回Integer数组
	 * @param val
	 * @return
	 */
	public Integer[] parseInts(String val) {
		if(val == null){
			return null;
		}
		char[] chs = val.toCharArray();
		int end = chs.length;
		int i=0;
		
		List<Integer> ints = new ArrayList<Integer>();
		while(i<end) {
			Token<Integer> token = ParseUtil.parseInt(chs, i, end, ',');
			ints.add(token.data);
			i = token.end+1;
		}
		return ints.toArray(new Integer[ints.size()]);
	}

	/**
	 * 1.处理了空值，返回空串
	 * 2.非空直接返回
	 * @param val
	 * @return
	 */
	public Object parseString(String val)  {
		return val == null || val.length() == 0 ? "" : val;
	}


	/**
	 * 根据属性名称，解析对应数据的类型，返回符合类型的值
	 * @param field
	 * @param val
	 * @return
	 */
	public Object parseField(String field, String val) {

		if(Define.OSVER.equals(field) || Define.VERSION.equals(field)) {
			return parseString(val);
		}
		else if(Define.OS.equals(field)) {
		    return parseString(val);
			//return parseOS(val);
		}
		else if(Define.DEVICE.equals(field)) {
		    return parseString(val);
			//return parseDevice(val);
		}
		else if(Define.MAC.equals(field)) {
			return parseMAC(val);
		}
		else if(Define.UDID.equals(field)) {
			return parseUDID(val);
		}
		else if(Define.OPENUDID.equals(field)) {
			return parseOpenUDID(val);
		}
		else if(Define.APP_USER_KEY.equals(field)) {
			return val == null ? "" : val;
		}
		else if(Define.UID.equals(field)) {
		    System.out.println(parseInt(val));
			return parseInt(val);
		}
		else if(Define.APPID.equals(field)) {
			return parseInt(val);
		}
		else if(Define.CID.equals(field)) {
			return parseInt(val);
		}
		else if(Define.ADID.equals(field)) {
			return parseInt(val);
		}
		else if(Define.IDS.equals(field)) {
			return parseInts(val);
		}
		else if(Define.DISK.equals(field)) {
			return parseLong(val);
		}
		else if(Define.IS_BOOL_MONITOR.equals(field)) {
            return parseInt(val);
        }
		else if(Define.GAME_ID.equals(field)) {
            return parseInt(val);
        }
		return val;
	}


	/**
	 * 拆分传入的字符串，将属性填充到Map中返回
	 * @param line
	 * @return
	 */
	public Map<String,Object> parseMap(String line) {
		
		if(line.charAt(0) == '#') {
			return null;
		}
		
		char[] chs = (line+" ").toCharArray();
		int end = chs.length-1;
		int i=0;
		int len = chs.length;
		Token<DateTime> datetime = ParseUtil.parseDateTime(chs,i, len);
		if(datetime == null || datetime.end == end) {
			return null;
		}
		i = ParseUtil.skipSplit(chs, datetime.end, len);

		Token<Long> ip = ParseUtil.parseIP(chs, i, len);
		if(ip == null || ip.end == end){
			return null;
		}
		i = ParseUtil.skipSplit(chs, ip.end, len);
		
		Token<String> ft = ParseUtil.parseValue(chs, i, len);
		if(ft == null || ft.end == end) {
			return null;
		}
		i = ParseUtil.skipSplit(chs, ft.end, len);
		int from = this.parseFrom(ft.data);

		Token<String> at = ParseUtil.parseValue(chs, i, len);
		if(at == null || at.end == end){
			return null;
		}
		i = ParseUtil.skipSplit(chs, at.end, len);
		int action = this.parseAction(at.data);

		Token<String> tt = ParseUtil.parseValue(chs, i, len);
		if(tt == null || tt.end == end) {
			return null;
		}
		i = ParseUtil.skipSplit(chs, tt.end, len);
		int type = this.parseType(tt.data);
		
		Token<String> qs = ParseUtil.parseRange(chs, i, len);
		if(qs==null) {
			return null;
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		for(i=qs.begin+1;i < end;i++) {
			Token<KeyValue> token = ParseUtil.parseKeyValue(chs, i, qs.end-1);
			if(token == null) {
				break;
			}

			Object val = parseField(token.data.key, (String)token.data.value);
			map.put(token.data.key, val);
			
			i = token.end;
		}

		map.put("year", datetime.data.year);
		map.put("mon", datetime.data.mon);
		map.put("day", datetime.data.day);
		map.put("hour", datetime.data.hour);
		map.put("min", datetime.data.min);
		map.put("sec", datetime.data.sec);
		map.put("ip", ip.data);
		
		map.put("action", action);
		map.put("type", type);
		map.put("from", from);

		return map;
	}

	/**
	 * 抽象方法，只能在子类中使用
	 * @return
	 */
	protected abstract List<String> fields();


	/**
	 * 解析字符串，返回一个元素为Object类型的list
	 * @param line
	 * @return
	 */
	public List<Object> parse(String line) {
		Map<String,Object> map = parseMap(line);
		if(map == null || map.isEmpty()){
			return null;
		}
		List<Object> vals = new ArrayList<Object>();
		for(String key : fields()) {
			Object val = map.get(key);
			vals.add(val);
		}

		return vals;
	}
	
	public static void main(String[] args) {
		String a = "2.00";
		int i = a.indexOf(".");
		a = a.substring(0, i);
		int parseInt = Integer.parseInt(a);
		System.out.println(parseInt);
	}
}
