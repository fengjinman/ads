package com.powerwin.boot.timetask;


import com.powerwin.util.Counter;
import com.powerwin.entity.CountStoreMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务2
 *
 * 更新数据库
 *
 * 一分钟延迟，一分钟执行一次
 */
public class ReportTimer extends TimerTask{

	public static Logger LOG = LogManager.getLogger(ReportTimer.class);
	
    public void run(){
    	LOG.debug("begin report to database...");
    	CountStoreMap store = Counter.getInstance().switchStore();
    	if(store == null) {
    		LOG.debug("count map is empty");
    		return;
    	}
    	//这里开始插入数据库
    	store.save();
    	
    	//每分钟Dump数据后，重新核算剩余量和激活量
		//MinuteAdsPlan.pauseAdByCount(0);
		LOG.debug("report to database done");
    }
    
    private static Timer timer = null;
    private static ReportTimer storeTask = new ReportTimer();
    
    public static void start() {
    	if(timer != null) return;
    	timer = new Timer();
    	//一分钟延迟，一分钟执行一次
        timer.schedule(storeTask, 60000, 60000);
    }
    
    public static void stop() {
    	if(timer != null) {
    		timer.cancel();
    		timer = null;
    	}
    	storeTask.run();
    }
}
