package com.powerwin.processor;

import java.util.List;


/**
 * 处理器接口
 */
public interface Processor {
	public List<Object>[] process(List<Object> map);
}
