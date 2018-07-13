package com.powerwin.boot.thread;


import com.powerwin.parser.Parser;
import com.powerwin.parser.Parsers;
import com.powerwin.processor.Processor;
import com.powerwin.processor.Processors;

import java.util.List;

/**
 * 线程类，执行数据解析、处理
 */
public class TaskRunnable implements Runnable {


	public static void exec(String line){
		
		String name = "";
		try {
			name = Parsers.parseType(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Parser parser = Parsers.get(name);

		Processor processor = Processors.get(name);
		if (parser == null || processor == null) {
			return;
		}
		
		List<Object> vals = parser.parse(line);
		if (vals != null) {
			processor.process(vals);
		} else {
		}
	}
	
	private String line;
	
	public TaskRunnable(String line) {
		this.line = line;
	}

	public void run() {
		exec(this.line);
	}
}
