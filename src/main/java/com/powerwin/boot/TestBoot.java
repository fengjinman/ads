package com.powerwin.boot;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by fengjinman Administrator on 2018/7/12.
 */
public class TestBoot {

    public static void main(String[] args) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
        System.out.println(time+"============>"+"任务执行！");
    }
}
