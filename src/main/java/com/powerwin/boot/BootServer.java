package com.powerwin.boot;
import com.powerwin.boot.config.Configuration;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.boot.thread.QueueThread;
import com.powerwin.boot.timetask.TimeCache;
import com.powerwin.boot.timetask.ObjectCache;
import com.powerwin.store.FileStore;
import com.powerwin.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.File;
/**
 * Created by fengjinman Administrator on 2018/7/6.
 */
public class BootServer {

    public static boolean SERVER_LOOP = true;

    public static Logger LOG = LogManager.getLogger(BootServer.class);


    public static void main(String[] args) {


        String filePath = Configuration.getInstance().getProperty("path.app.pid", "");
        File fileForPid = new File(filePath);
        if(fileForPid.exists()) {
            LOG.error("server start error pid file : "+fileForPid+" exists");
        }

        int pid = ProcessUtil.saveProcessId(fileForPid);

        LOG.info("server start with pid : " + pid);

        //定时任务，不断更新当前时间 启动时延迟1秒 之后一秒钟执行一次
        TimeCache.start();
        //定时任务，更新数据库和redis 启动时延迟一分钟 之后一分钟执行一次
        ObjectCache.start();

        QueueThread showQueueThread = new QueueThread("ACTION_SHOW_REPORT");
//        QueueThread clickQueueThread = new QueueThread("");
//        QueueThread activeQueueThread = new QueueThread("");
//        QueueThread jumpQueueThread = new QueueThread("");
//        QueueThread jobQueueThread = new QueueThread("");

        showQueueThread.start();
//        clickQueueThread.start();
//        activeQueueThread.start();
//        jumpQueueThread.start();
//        jobQueueThread.start();

        while(SERVER_LOOP) {
            try {
                //100毫秒
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        showQueueThread.quit();
//        clickQueueThread.quit();
//        activeQueueThread.quit();
//        jumpQueueThread.quit();
//        jobQueueThread.quit();

        try {
            showQueueThread.join();
//            clickQueueThread.join();
//            activeQueueThread.join();
//            jumpQueueThread.join();
//            jobQueueThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimeCache.stop();
        ObjectCache.stop();

        SimpleFileWriter.closeAll();
        FileStore.closeAll();

        RedisConnection.shutdown();

        if(fileForPid.exists()){
            fileForPid.delete();
        }
    }
}