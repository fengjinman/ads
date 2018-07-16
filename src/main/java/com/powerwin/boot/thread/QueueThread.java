package com.powerwin.boot.thread;

import com.powerwin.util.RedisQueue;
import com.powerwin.util.SimpleFileWriter;
import com.powerwin.boot.timetask.TimeCache;
import com.powerwin.boot.config.Configuration;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class QueueThread extends Thread {
	
	private boolean loop = true;
	

	public static SimpleFileWriter getLogFile() {
		String filePath = String.format("data/%d/%02d.log", TimeCache.date, TimeCache.hour);
		SimpleFileWriter logFile = SimpleFileWriter.getInstance(filePath);
		return logFile;
	}
	
	private String queueName;
	
	public QueueThread(String queueName) {
		this.queueName = queueName;
	}
	
	public void quit() {
		loop = false;
	}

	@Override
	public void run() {
		
		RedisQueue queue = RedisQueue.getInstance();

		String line;
		int date = TimeCache.date*100+TimeCache.hour;
		
		SimpleFileWriter logFile = getLogFile();
		
		int nThreads = Configuration.getInstance().getInt("server.threads", 1);
		
		ExecutorService executorService = null;
		if(nThreads > 1) {
			//创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
			executorService = Executors.newFixedThreadPool(nThreads);
		} else if(nThreads == 0) {
			//创建一个可缓存线程池，如果线程池长度超过需要的线程数量，可灵活回收空闲线程，若无可回收，则新建线程
			executorService = Executors.newCachedThreadPool();
		}
		while (loop) {
			line = queue.pop(queueName);
			if (line == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ie) {
				}
				continue;
			}
			if(TimeCache.date != date) {
				logFile.close();
				date = TimeCache.date*100+TimeCache.hour;
				logFile = getLogFile();
			}
			try {
				logFile.write(line + '\n');
			} catch (IOException e1) {
			}
			// 2014-10-14 00:00:01 581.831.143.310 sdk active offer_wall
			if(executorService == null) {
				try {
					TaskRunnable.exec(line);
				} catch (Exception e) {
				}
			} else {
				executorService.execute(new TaskRunnable(line));
			}
		}
		if(executorService != null) {
			executorService.shutdown();
			try {
				executorService.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}
	}
}
