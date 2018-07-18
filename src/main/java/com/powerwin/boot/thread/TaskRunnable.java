package com.powerwin.boot.thread;
import com.powerwin.parser.Parser;
import com.powerwin.parser.Parsers;
import com.powerwin.processor.Processor;
import com.powerwin.processor.Processors;
import java.util.List;
/**
 * 线程类2
 *
 * 执行数据解析、处理
 */
public class TaskRunnable implements Runnable {


	public static void exec(String line){
		
		String name = "";
		try {
			//从缓存数据中获得名字  确定是那个模块的数据
			name = Parsers.parseType(line);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//根据解析出来的名字获取指定的解析对象
		Parser parser = Parsers.get(name);

		//根据解析出来的名字获取指定的处理器对象
		Processor processor = Processors.get(name);
		if (parser == null || processor == null) {
			return;
		}

		//解析成数据集合
		List<Object> vals = parser.parse(line);
		if (vals != null) {
			//处理器开始处理
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
