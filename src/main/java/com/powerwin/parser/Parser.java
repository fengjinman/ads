package com.powerwin.parser;

import com.powerwin.boot.config.Define;

import java.util.List;


/**
 * 解析字符串的接口
 */
public interface Parser extends Define.Index {
	public List<Object> parse(String line);
}
