package com.powerwin.processor;


import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.dao.CallbackTableDemoMapper;
import com.powerwin.dao.CpcDayMapper;
import com.powerwin.dao.CpcHourMapper;
import com.powerwin.entity.*;
import com.powerwin.parser.ShowParser;
import com.powerwin.util.DaoUtil;
import com.powerwin.util.ListUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 处理器实现类：显示
 */
public class ShowProcessor extends BaseProcessor {
	public static Logger LOG = LogManager.getLogger(ShowProcessor.class);


	/**
	 * 实现父类BaseProcessor继承的接口方法
	 * 加工数据，存入数据库
	 * @param vals
	 * @return
	 */
	public List<Object>[] process(List<Object> vals) {
        //1
		int action = (Integer) vals.get(ShowParser.Index.ACTION);
		
	    //1
	    int game_id = Integer.parseInt(vals.get(ShowParser.Index.GAME_ID).toString()) ;

	    //7
	    int is_bool_monitor = Integer.parseInt(vals.get(ShowParser.Index.IS_BOOL_MONITOR).toString());

        //5
		int appid = vals.get(ShowParser.Index.APPID) == null ? 0 : (Integer) vals.get(ShowParser.Index.APPID);

		//[6]
		Integer ids[] = (Integer[]) vals.get(ShowParser.Index.IDS);
		
		vals.remove(ShowParser.Index.IDS);
		
		if (appid == 0 || action == 0 || ids == null || ids.length == 0) {
			return null;
		}
        //{"medcreate_time":1529918548,"addtype":1,"desc":"\u6d4b","medupdate_time":1530613778,"noaddtype":"4,5,6,7","appkey":"6b32e01945946ed68","state":1,"medtype":2,"type":1,"id":5,"downloadurl":"www.\u5740.org","pgname":"\u6d4b\u8bd5\u5305\u540d1","name":"\u6d4b\u8bd5\u5a92\u4f531","money":44,"ywcard":"112233445566778899","medstatus":2,"moneytype":1,"qqnumber":"9999999","ywcardpic":"\/ueditor\/php\/upload\/image\/20180625\/1529921471987590.jpg","linkname":"\u77f3\u5bb6\u5e84\u6c49\u666e\u79d1\u6280\u6709\u9650\u516c\u53f8","cpname":"gongsi@Email.com","caddress":"\u6b63\u5b9a\u5de5\u4e1a\u79d1\u6280\u56ed","cstype":20,"status":1,"devid":5,"ctype":1,"buckle":10,"sendarea":"TH,MY,TJ","moblie":"18957456321","update_time":1530613869,"finish_money":5,"locks":1,"create_time":1529546098,"wxnumber":"\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000","cname":"\u77f3\u5bb6\u5e84\u6c49\u666e\u79d1\u6280\u6709\u9650\u516c\u53f8","skype":"001122"}
		Media media = MediaCache.getInstance().get(appid);
		if (media == null) {
			LOG.warn("media "+appid+" not found");
			return null;
		}
		
		boolean isStop = true;
		if(media.getStatus() ==  1 &&  media.getState() == 1 && (  media.getMedstatus() == 1 || media.getMedstatus() ==2 )) {
			isStop = false;
		}

		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);
		int hour = ListUtil.getInt(vals, Index.HOUR);
		
		//1 应用 3 渠道
		int data_from = 1;
		
		//00:00:00:00:00:00
		String mac = ListUtil.getString(vals, Index.MAC);
		//32417e80dd7f31024623e82712386b17
		String udid = ListUtil.getString(vals, Index.UDID);
		
		//5
		//开发者id
		int uid = media.getDevid();
		//20180705
		int date = year * 10000 + mon *100 + day;
		
		//媒体新增请求数，独立请求数
		//int reqUnique = this.checkUnique(date, 0, action, 0, appid, mac, udid);
		//0     todo 是否需要修改
		System.out.println("计算之前----date="+date+" action = "+ action +" mac = "+mac+" udid = "+udid);
	    int reqUnique = this.checkUnique(date, 0, action, 0, appid, mac, udid);
	    
	    System.out.println("!----------独立请求数 = "+reqUnique);

		for (int i = 0; i < ids.length; i++) {
			if(i > 0 && reqUnique == 1){
				reqUnique = 0;
			}
			
			//6
			int adid = ids[i];
			//{"day_money":0,"unitname":"","shopdesc":"\u5546\u54c1\u8be6\u60c5","cpid":5,"senddevmedia":"2,3,4,5,6,7,8","shopname":"\u5546\u54c1\u540d\u79f0 ","type":1,"sendtype":"1,2,3","id":6,"bj_end_time":1530943500,"cpstatus":1,"bj_start_time":1530162048,"bjhourval":-1,"name":"hui1q23e\u7684\u8ba1\u5212","onedesc":"\u5546\u54c1\u8be6\u60c5","showpic":"\/ueditor\/php\/upload\/image\/20180621\/1529554450343346.jpg","unitstatus":1,"buttondesc":"\u6309\u94ae\u6587\u672c1","senddate":"0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23","cstype":66,"statusres":"","status":0,"ctype":3,"sendarea":"TH","url":"http:\/\/hui1q2w3e.org","planid":7,"xxpic":"\/ueditor\/php\/upload\/image\/20180625\/1529921854911399.jpg","end_time":1530947100,"one_money":11,"start_time":1530165600}
			Ads ad = AdsCache.getInstance().get(adid);
			if (ad == null) {
				LOG.warn("ad "+adid+" object not found");
				continue;
			}
			
			if(ad.getUnitstatus() != 1 ) {
				isStop = true;
			}
			
			int ad_from = 1;
			//5
			int cid = ad.getCpid();

			int type = 1;
	        boolean save = false;

			/*DataSaveRole role = DataSave.getRole(media,ad);
			
			if(type == 1 || type == 2){//cpa 扣量，cpc不扣
				save = DataSave.getSave(role);
			}
			*/
			
	        //0
	        System.out.println("计算之前----date="+date+" action = "+ action +" mac = "+mac+" udid = "+udid+" type = "+type +" adid = "+adid +" appid = "+appid);
			int unique = this.checkUnique(date, type, action, adid, appid, mac, udid);
			System.out.println("第二次计算的unique = "+unique);
			
			
			//0 
			int saved = 0;
            //int saved = unique == 0 || save || isStop ? 0 : 1;
			if(is_bool_monitor != 1){
				saved = 1;
			}

			// todo 数据插入
			CpcHourMapper cpcHourMapper = (CpcHourMapper)DaoUtil.getDao(CpcHourMapper.class);
			CpcDayMapper cpcDayMapper = (CpcDayMapper)DaoUtil.getDao(CpcDayMapper.class);
			CallbackTableDemoMapper callbackTableMapper = (CallbackTableDemoMapper)DaoUtil.getDao(CallbackTableDemoMapper.class);

			CpcHour cpc_hour = getCpcHour(year,mon,day,hour,type,data_from,ad_from,game_id,1,reqUnique,unique,saved);
			cpcHourMapper.insert(cpc_hour);

			CpcDay cpc_day = getCpcDay(year,mon,day,type,data_from,ad_from,game_id,1,reqUnique,unique,saved);
			cpcDayMapper.insert(cpc_day);

			StringBuffer tablename = getCallbackTablename();
			CallbackTableDemo callback = getCallBackInstance(data_from,ad_from,game_id,saved,action,adid,appid,udid,uid,cid,is_bool_monitor,ad.getPlanid());
			callbackTableMapper.insert(tablename,callback);
			//去掉is_bool_monitor  增加adplanid
//			DetailHourKeys ck = DetailHourKeys.create(year,mon,day,hour,type,data_from,ad_from,appid,uid,adid,cid,game_id,ad.getPlanid());
//
//			CountValues cv = CountValues.create(action, 1, reqUnique, unique, saved, 0, 0);
//
//			Counter.getInstance().add(ck, cv);
		}
		return null;
	}

	/**
	 * 填充CallbackTableDemo对象
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param saved
	 * @param action
	 * @param adid
	 * @param appid
	 * @param udid
	 * @param uid
	 * @param cid
	 * @param is_bool_monitor
	 * @param planid
	 * @return
	 */
	public CallbackTableDemo getCallBackInstance(int data_from,int ad_from,int game_id,int saved,int action,int adid,int appid,String udid,int uid,int cid,int is_bool_monitor,int planid){
		CallbackTableDemo c = new CallbackTableDemo();
		c.setDataFrom(data_from);
		c.setAdFrom(ad_from);
		c.setGameId(game_id);
		c.setSaved((short) saved);
		c.setCid(cid);
		c.setUid(uid);
		c.setAdid(adid);
		c.setAction((short) action);
		c.setIsBoolMonitor((short)is_bool_monitor);
		c.setAppid(appid);
		c.setAdplanid(planid);
		c.setUdid(udid);
		return c;
	}

	/**
	 * 生成回调表名
	 * @return
	 */
	public StringBuffer getCallbackTablename(){
		StringBuffer base = new StringBuffer("cpc_callback_");
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(d);
		base.append(time);
		return base;
	}

	/**
	 * 填充CpcHour对象
	 * @param year
	 * @param mon
	 * @param day
	 * @param hour
	 * @param type
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param show_count
	 * @param show_invalid
	 * @param show_unique
	 * @param show_saved
	 * @return
	 */
	public CpcHour getCpcHour(int year,int mon,int day,int hour,int type,int data_from,int ad_from,int game_id,int show_count,int show_invalid,int show_unique,int show_saved){
		CpcHour c = new CpcHour();
        c.setYear((short)year);
        c.setMon((short)mon);
        c.setDay((short)day);
        c.setHour((short)hour);
        c.setType((short)type);
        c.setDataFrom((short)data_from);
        c.setAdFrom((short)ad_from);
        c.setGameId((short)game_id);
        c.setShowCount(show_count);
        c.setShowInvalid(show_invalid);
        c.setShowUnique(show_unique);
        c.setShowSaved(show_saved);
		return c;
	}

	/**
	 * 填充CpcDay对象
	 * @param year
	 * @param mon
	 * @param day
	 * @param type
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param show_count
	 * @param show_invalid
	 * @param show_unique
	 * @param show_saved
	 * @return
	 */
	public CpcDay getCpcDay(int year,int mon,int day,int type,int data_from,int ad_from,int game_id,int show_count,int show_invalid,int show_unique,int show_saved){
		CpcDay c = new CpcDay();
        c.setYear((short)year);
        c.setMon((short)mon);
        c.setDay((short)day);
        c.setType((short)type);
        c.setDataFrom((short)data_from);
        c.setAdFrom((short)ad_from);
        c.setGameId((short)game_id);
		c.setShowCount(show_count);
		c.setShowInvalid(show_invalid);
		c.setShowUnique(show_unique);
		c.setShowSaved(show_saved);
		return c;
	}
}