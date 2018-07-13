/**
 * Created by fengjinman Administrator on 2018/7/10.
 */

import com.powerwin.dao.CallbackTableDemoMapper;
import com.powerwin.dao.CpcDayMapper;
import com.powerwin.dao.CpcHourMapper;
import com.powerwin.entity.CallbackTableDemo;
import com.powerwin.entity.CpcDay;
import com.powerwin.entity.CpcHour;
import com.powerwin.util.DaoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-mybatis.xml"})
public class UserDaoTest {

//    @Autowired
//    private UserDao userDao;
//
//    @Test
//    public void testFindUserById() {
//        int id = 1;
//        User user = userDao.queryUser(id);
//        System.out.println(user);
//    }
//
//    @Test
//    public void test2(){
//        User user = new User(5,"改变就是好事","88888",2);
//        int insert = userDao.insert(user);
//        System.out.println(insert);
//    }

    @Test
    public void test3(){
//        StringBuffer base = new StringBuffer("cpc_callback_");
//        Date d = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String time = sdf.format(d);
//        base.append(time);
    }
    @Test
    public void test13(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(d);
    }

    @Test
    public void test5(){
//        CpcDayMapper dao = (CpcDayMapper)DaoUtil.getDao(CpcDayMapper.class);
//        CpcDay c = new CpcDay();
//        c.setId(5);
//        c.setActiveCost((float)1);
//        c.setActiveCount(1);
//        c.setActiveIncome((float)1);
//        c.setActiveInvalid(1);
//        c.setActiveSaved(1);
//        c.setActiveUnique(1);
//        c.setAdFrom((short)1);
//        c.setClickCost((float)1);
//        c.setClickCount(1);
//        c.setClickIncome((float) 1);
//        c.setClickInvalid(1);
//        c.setClickSaved(1);
//        c.setClickUnique(1);
//        c.setJobCost((float)1);
//        c.setJobCount(1);
//        c.setJobCount1(1);
//        c.setJobCount2(1);
//        c.setJobCount3(1);
//        c.setJobCount4(1);
//        c.setJobCount5(1);
//        c.setJobCount6(1);
//        c.setJobCount7(1);
//        c.setJobCount8(1);
//        c.setJobCount9(1);
//        c.setJobIncome((float)1);
//        c.setJobInvalid(1);
//        c.setJobSaved(1);
//        c.setJobUnique(1);
//        c.setJumpCount(1);
//        c.setJumpInvalid(1);
//        c.setJumpSaved(1);
//        c.setJumpUnique(1);
//        c.setCreated(1);
//        c.setGameId((short)1);
//        c.setGameId((short)1);
//        c.setDataFrom((short)1);
//        c.setDay((short)1);
//        c.setMon((short)1);
//        c.setYear((short)1);
//        c.setType((short)1);
//        c.setShowCount(1);
//        c.setShowInvalid(1);
//        c.setShowSaved(1);
//        c.setShowUnique(1);
//        int i = dao.insert(c);
//        if(i==1){
//            System.out.println("插入成功！");
//        }else{
//            System.out.println("插入失败！");
//        }
    }


    @Test
    public void test6(){
//        CpcHourMapper dao = (CpcHourMapper)DaoUtil.getDao(CpcHourMapper.class);
//        CpcHour c = new CpcHour();
//        c.setId(5);
//        c.setHour((short)1);
//        c.setActiveCost((float)1);
//        c.setActiveCount(1);
//        c.setActiveIncome((float)1);
//        c.setActiveInvalid(1);
//        c.setActiveSaved(1);
//        c.setActiveUnique(1);
//        c.setAdFrom((short)1);
//        c.setClickCost((float)1);
//        c.setClickCount(1);
//        c.setClickIncome((float) 1);
//        c.setClickInvalid(1);
//        c.setClickSaved(1);
//        c.setClickUnique(1);
//        c.setJobCost((float)1);
//        c.setJobCount(1);
//        c.setJobCount1(1);
//        c.setJobCount2(1);
//        c.setJobCount3(1);
//        c.setJobCount4(1);
//        c.setJobCount5(1);
//        c.setJobCount6(1);
//        c.setJobCount7(1);
//        c.setJobCount8(1);
//        c.setJobCount9(1);
//        c.setJobIncome((float)1);
//        c.setJobInvalid(1);
//        c.setJobSaved(1);
//        c.setJobUnique(1);
//        c.setJumpCount(1);
//        c.setJumpInvalid(1);
//        c.setJumpSaved(1);
//        c.setJumpUnique(1);
//        c.setCreated(1);
//        c.setGameId((short)1);
//        c.setGameId((short)1);
//        c.setDataFrom((short)1);
//        c.setDay((short)1);
//        c.setMon((short)1);
//        c.setYear((short)1);
//        c.setType((short)1);
//        c.setShowCount(1);
//        c.setShowInvalid(1);
//        c.setShowSaved(1);
//        c.setShowUnique(1);
//        int i = dao.insert(c);
//        if(i==1){
//            System.out.println("插入成功！");
//        }else{
//            System.out.println("插入失败！");
//        }
    }

    @Test
    public void test4(){
//        CallbackTableDemoMapper dao = (CallbackTableDemoMapper)DaoUtil.getDao(CallbackTableDemoMapper.class);
//        StringBuffer base = new StringBuffer("cpc_callback_");
//        Date d = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        String time = sdf.format(d);
//        base.append(time);
//        CallbackTableDemo c = new CallbackTableDemo();
//        c.setUdid("1");
//        c.setAdplanid(1);
//        c.setAppid(1);
//        c.setIsBoolMonitor((short)1);
//        c.setAction((short)1);
//        c.setAdid(1);
//        c.setUid(1);
//        c.setUcid(1);
//        c.setCid(1);
//        c.setSaved((short)1);
//        c.setGameId(1);
//        c.setAdFrom(1);
//        c.setDataFrom(1);
//        c.setAppuserid(1);
//        c.setAreaTime(1);
//        c.setCreateTime(1);
//        c.setIncome((float) 1);
//        c.setIp("1");
//        c.setSys("1");
//        c.setUserAgent("1");
//        c.setVersion("1");
//        int i = dao.insert(base, c);
//        if(i==1){
//            System.out.println("插入成功！");
//        }else{
//            System.out.println("插入失败！");
//        }
    }
}