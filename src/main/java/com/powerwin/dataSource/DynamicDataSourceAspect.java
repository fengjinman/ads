package com.powerwin.dataSource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * 切换数据源
 * Created by fengjinman Administrator on 2018/7/11.
 */
@Component
@Aspect
public class DynamicDataSourceAspect {

    @Pointcut("execution( * com.powerwin.dao.*.*(..))")
    public void pointCut(){

    }
    @Before("pointCut()")
    public void before(JoinPoint jp){
        String methodName = jp.getSignature().getName();
        //dao方法查询走从库
        if(methodName.startsWith("query") || methodName.startsWith("get") || methodName.startsWith("count") || methodName.startsWith("list")){
            SqlSessionContentHolder.setContextType(SqlSessionContentHolder.SESSION_FACTORY_SLAVE);
        }else{
            SqlSessionContentHolder.setContextType(SqlSessionContentHolder.SESSION_FACTORY_MASTER);
        }
    }

}