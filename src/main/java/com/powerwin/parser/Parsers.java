package com.powerwin.parser;



import java.util.HashMap;
import java.util.Map;

/**
 * 将解析类作为多个静态变量封装
 */
public class Parsers {
	private final static Map<String,Parser> PARSER_MAP = new HashMap<String,Parser>();
	
	private final static ShowParser SHOW_PARSER = new ShowParser();
	private final static ActionParser ACTION_PARSER = new ActionParser();
	private final static CallbackParser CALLBACK_PARSER = new CallbackParser();
	
	private static String[] PARSER_NAMES;
	static {

		PARSER_MAP.put("show", SHOW_PARSER);
	
		PARSER_MAP.put("click", ACTION_PARSER);
		PARSER_MAP.put("jump", ACTION_PARSER);
		
		PARSER_MAP.put("active", CALLBACK_PARSER);
		PARSER_MAP.put("job", CALLBACK_PARSER);

		PARSER_NAMES = PARSER_MAP.keySet().toArray(new String[PARSER_MAP.size()]);
	}
	
	public final static  String[] names() {
		return PARSER_NAMES;
	}
	
	public final static Parser get(String name) {
		return PARSER_MAP.get(name);
	}
	
	public static String parseType(String line) throws Exception{
		int c = 0;
		int b = 0, e = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == ' ') {
				c++;

				if (c == 4) {
					b = i;
				} else if (c == 5) {
					e = i;
					break;
				}
			}
		}
		int len = e - b;
		if (len == 0) {
			return null;
		}

		String name = line.substring(b + 1, e);
		return name;
	}
}
