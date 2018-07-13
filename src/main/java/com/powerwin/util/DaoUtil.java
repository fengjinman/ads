package com.powerwin.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by fengjinman Administrator on 2018/7/10.
 */
public class DaoUtil {

    public static Object getDao(Class t){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-mybatis.xml"});
        Object mapper = ctx.getBean(t);
        return mapper;
    }

}
