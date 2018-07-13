package com.powerwin.util;

import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;


/**平滑控制投放广告*/
public class MinuteAdsPlanUtil {

    public static Logger LOG = LogManager.getLogger(MinuteAdsPlanUtil.class);

    public static class Count {
        public int clickCount = 0;
        public int activeCount = 0;
        public int showCount = 0;
    }

    /**设置平滑投放量的redis的key*/
    public static String DATA_SMOOTH_PREFIX = "DATA_SMOOTH_";
    public static String DATA_SMOOTH_FLAG_PREFIX = "DATA_SMOOTH_FLAG_";

    static {
        Jedis jedis = null;
        try {
            jedis = RedisConnection.getInstance("main");
            String key = DATA_SMOOTH_PREFIX + DateUtils.getBeforeDate(1);
            String key2 = DATA_SMOOTH_FLAG_PREFIX + DateUtils.getBeforeDate(1);
            if (jedis.exists(key)) {
                Long r = jedis.del(key);
                LOG.debug("clean redis key : " + key + ", result : " + r);
            }
            if (jedis.exists(key2)) {
                Long r = jedis.del(key2);
                LOG.debug("clean redis key : " + key2 + ", result : " + r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                RedisConnection.close("main", jedis);
        }
    }

    /**当前小时的流量分配比率*/
    private static float getFlowRate(int hour, int type, Map<Integer, Integer> hourMap) {
        // 缺省每个小时都能消耗完全
        float rate = 0f;
        int sum = 0;
        for (int tmp : hourMap.values()) {
            sum += SmoothControlUtil.MODEL[tmp];
        }
        rate = (float) SmoothControlUtil.MODEL[hour] / (float) sum;
        return rate;
    }

    /**
     * 将小时字符串转为map
     * */
    private static Map<Integer, Integer> parseHoursToMap(String hours) {
        Map<Integer, Integer> hourMap = new HashMap<Integer, Integer>();
        String[] hourArr = hours.split(",");

        int curHour = DateUtils.getHour();
        for (String hour : hourArr) {
            try {
                int h = Integer.parseInt(hour);
                if (h < curHour)
                    continue; // 跳过比当前小的小时

                hourMap.put(h, h);
            } catch (Exception e) {
            }
        }
        return hourMap;
    }

    /***
     * 设置平滑投放的量
     * */
    public static void setSmoothDeliveryNum(int adid, String num) {
        Jedis jedis = null;
        try {
            jedis = RedisConnection.getInstance("main");
            String key = DATA_SMOOTH_PREFIX + DateUtils.getBeforeDate(0);
            Long r = jedis.hset(key, String.valueOf(adid), num);
            LOG.debug(String.format("setSmoothDeliveryNum adid:%s,num:%s,result:%s", adid, num, r));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                RedisConnection.close("main", jedis);
        }
    }

    /***
     * 设置每小时平滑投放的量生效的标示
     * */
    public static void setSmoothDeliveryFlag(int adid, String flags) {
        Jedis jedis = null;
        try {
            jedis = RedisConnection.getInstance("main");
            String key = DATA_SMOOTH_FLAG_PREFIX + DateUtils.getBeforeDate(0);
            Long r = jedis.hset(key, String.valueOf(adid), flags);
            LOG.debug(String.format("setSmoothDeliveryFlag adid:%s,num:%s,result:%s", adid, flags, r));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null)
                RedisConnection.close("main", jedis);
        }
    }

    /***
     * 平滑控量
     * */
//    protected static void pauseAdByHourCount() {
//
//        SQLConnection conn = null;
//        SQLConnection countConn = null;
//        String sql = "SELECT ads.adid as adid, ads.type as adtype, ads.state as state, ad_plan.num active_num, "
//                + "ad_plan.click_num as click_num,ad_plan.hours as hours FROM ads,ad_plan WHERE ads.adid=ad_plan.adid "
//                + "AND ad_plan.deliveryType =1 AND ads.state=4 AND (ad_plan.num >= 0 OR ad_plan.click_num>0) "
//                + "AND UNIX_TIMESTAMP() < ad_plan.end";
//
//        try {
//            // 1.查询启动或留存并以平滑方式投放广告的控量
//            conn = SQLConnection.getInstance("main");
//
//            List<Map<String, Object>> items = conn.queryMap(sql, null);
//            if (items != null && !items.isEmpty()) {
//                for (Map<String, Object> vals : items) {
//                    @SuppressWarnings("unused")
//                    int adid = 0, type = 0, activeNum = 0, clickNum = 0, state = 0;
//                    String hours = "";
//
//                    adid = (Integer) vals.get("adid");
//                    type = (Integer) vals.get("adtype");
//                    activeNum = (Integer) vals.get("active_num");
//                    clickNum = (Integer) vals.get("click_num");
//                    state = (Integer) vals.get("state");
//                    hours = String.valueOf(vals.get("hours"));
//
//                    // 没计算过小时投放量的
//                    if (!SmoothControlUtil.existsSmoothAd(adid)) {
//
//                        // 查询计算已消耗量
//                        int created = DateUtils.getYYYYMMDD(new Date());
////                        DataSource ds = SQLConnection.getDataSource(Define.DATA_SOURCES[type]);
////                        String prefix = ds == null ? "" : ds.getPrefix();
////                        String table = String.format("%s_ad_day_%d", prefix, created / 10000);
////
////                        String querySql = String.format(MinuteAdsPlan.QUERY_STRING, table, adid, created);
////                        countConn = SQLConnection.getInstance(Define.DATA_SOURCES[type]);
////                        Map<String, Object> countVals = countConn.queryOneMap(querySql, null);
////                        if (countVals != null) {
////                            int active_count = Integer.parseInt(countVals.get("active_count").toString());
//////                            int active_count = (int) (float) countVals.get("active_count");
////                            activeNum = (activeNum - active_count) <= 0 ? 0 : activeNum - active_count;
////                        }
//
//                        // 需要投放的小时
//                        Map<Integer, Integer> hourMap = parseHoursToMap(hours);
//
//                        // 初始化每小时投放量
//                        Map<Integer, Integer> hourNum = new HashMap<Integer, Integer>();
//
//                        // 初始化是否生效的标示
//                        Map<Integer, Integer> hourFlag = new HashMap<Integer, Integer>();
//                        for (int i = 0; i <= 23; i++) {
//                            hourNum.put(i, 0);
//                            hourFlag.put(i, 0);
//                        }
//
//                        // 计算每小时的量
//                        for (int h : hourMap.keySet()) {
//                            float rate = getFlowRate(h, type, hourMap);
//                            int num = new Float(activeNum * rate).intValue();
//                            hourNum.put(h, num);
//                        }
//
//                        // 设置每小时投放量
//                        String nums = "";
//                        boolean flag = true;
//                        for (int num : hourNum.values()) {
//                            if (flag) {
//                                nums += String.valueOf(num);
//                            } else {
//                                nums += "," + num;
//                            }
//                            flag = false;
//                        }
//                        setSmoothDeliveryNum(adid, nums);
//
//                        // 设置每小时投放量是否生效的标示
//                        String flags = "";
//                        boolean flag2 = true;
//                        for (int f : hourFlag.values()) {
//                            if (flag2) {
//                                flags += String.valueOf(f);
//                            } else {
//                                flags += "," + f;
//                            }
//                            flag2 = false;
//                        }
//                        setSmoothDeliveryFlag(adid, flags);
//                    }
//
//                    // 当前小时未生效
//                    if (!SmoothControlUtil.checkHourFlag(adid, DateUtils.getHour())) {
//                        // 设置当前小时平滑投放量生效
//                        SmoothControlUtil.setSmoothRemainActive(adid);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (conn != null)
//                conn.close();
//            if (countConn != null)
//                countConn.close();
//        }
//    }

    /**
     * 查询指定广告激活量
     * @param type 广告类型
     * @param adid 广告ID
     * @return int 返回从数据库中查询出来的指定广告ID的激活量
     */
//    public static int queryAdCount(int type, int adid) {
//
//        int active_count = 0;
//        SQLConnection countConn = null;
//        try {
//            int created = DateUtils.getYYYYMMDD(new Date());
////            DataSource ds = SQLConnection.getDataSource(Define.DATA_SOURCES[type]);
////            String prefix = ds == null ? "" : ds.getPrefix();
////            String table = String.format("%s_ad_day_%d", prefix, created / 10000);
////
////            String querySql = String.format(MinuteAdsPlan.QUERY_STRING, table, adid, created);
////            countConn = SQLConnection.getInstance(Define.DATA_SOURCES[type]);
////            Map<String, Object> countVals = countConn.queryOneMap(querySql, null);
////            if (countVals != null) {
////                active_count = Integer.parseInt(countVals.get("active_count").toString());
////            }
//        } catch (Exception e) {
//        } finally {
//            if (countConn != null) {
//                countConn.close();
//            }
//        }
//
//        return active_count;
//    }

    /**
     * 检查激活超时
     */
//    protected static void checkActiveTimeOut() {
//
//        RemainActiveUtil activeUtil = RemainActiveUtil.getInstance();
//        Jedis jedis = null;
//        Jedis jedisMain = null;
//        try {
//            jedis = RedisConnection.getInstance("control");
//            jedisMain = RedisConnection.getInstance("main");
//
//            String adidKey = DateUtils.getBeforeDate(0) + "_IDFA_WAIT_ACTIVE";
//            Set<String> adidSet = jedis.hkeys(adidKey);
////            LOG.debug("check active timeout - adids={}", adidSet);
//            if (adidSet != null && adidSet.size() <= 0){
//                return;
//            }
//            Set<String> keys = new HashSet<String>();
//            for (String adid : adidSet) {
//                String key = DateUtils.getBeforeDate(0) + "_IDFA_WAIT_ACTIVE_" + adid;
//                if (jedis.exists(key)) {
//                    keys.add(key);
//                }
//            }
////            LOG.debug("check active timeout - keys={}", keys);
//            String adids = "";
//            for (String key : keys) {
//                String[] arr = key.split("_");
//                if (arr.length == 5) {
//                    int adid = Integer.parseInt(arr[4]);
//                    adids += adid + ",";
//                }
//            }
//            if (adids.length() > 0) {
//                adids = adids.substring(0, adids.length() - 1);
//            } else {
//                return;
//            }
//            Map<Integer, Ads> AdsMap = new HashMap<Integer, Ads>();
//            Set<Integer> ckeys = new HashSet<Integer>();
//            SQLConnection conn = null;
//            try {
//                conn = SQLConnection.getInstance("main");
//                String sql = "SELECT a.adid, a.options, a.data_type, p.num, a.state from ads a inner join ad_plan p on a.adid = p.adid  where a.adid in ("
//                        + adids + ")";
//                List<Map<String, Object>> items = conn.queryMap(sql, null);
//                if (!items.isEmpty()) {
//                    for (Map<String, Object> vals : items) {
//                        Ads ads = new Ads(vals);
////                        if (ads.getState() != 4) {
////                            ckeys.add(ads.getAdid());
////                            LOG.debug("check time out state : " + ads.getAdid() + " , " + ads.getState());
////                            continue;
////                        }
////
////                        int active_count = MinuteAdsPlanUtil.queryAdCount(ads.getDataType(), ads.getAdid());
////                        if (active_count >= Integer.parseInt(vals.get("num").toString())) {
////                            ckeys.add(ads.getAdid());
////                            LOG.debug("check time out num : " + ads.getAdid() + " , " + ads.getState());
////                            continue;
////                        }
////                        AdsMap.put(ads.getAdid(), ads);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (conn != null) {
//                    conn.close();
//                }
//            }
//
//            // 遍历等待激活的idfa列表
//            for (String key : keys) {
//                String[] arr = key.split("_");
//                if (arr == null || arr.length != 5)
//                    continue;
//
//                int adid = Integer.parseInt(arr[4]);
//                if (ckeys.contains(adid)) {
//                    continue;
//                }
//                float size = 0;
////                try {
////                    Ads ads = AdsMap.get(adid);
////                    JSONObject optJson = JSONObject.parseObject(ads.getOptions());
////                    size = optJson.getFloat("psize");
////                    LOG.debug("check time out adid={} psize={} ", adid, size);
////                } catch (Exception e) {
////                    LOG.warn("check time out get ad size error : adid={} size={} : {}", adid, size, e.getMessage());
////                }
//                Map<String, String> all = jedis.hgetAll(key);
//                for (Iterator<Entry<String, String>> it = all.entrySet().iterator(); it.hasNext();) {
//                    Entry<String, String> next = it.next();
//                    String idfa = next.getKey();
//                    String timeStr = next.getValue();
//                    Long t = Long.parseLong(timeStr);
//                    // 判断是否超时
//                    Long interval = System.currentTimeMillis() / 1000 - t;
//                    int timeout = size > 100 ? RemainActiveUtil.TIME_OUT_MAX : RemainActiveUtil.TIME_OUT;
//                    if (t > 0 && interval > timeout) {
//                        // 将idfa从等待激活的列表中移除
//                        int res = activeUtil.idfaWaitActive(jedis, "-", adid, idfa);
//                        if (res > 0) {
//                            // 增加剩余激活的数量
//                            activeUtil.remainActive(jedisMain, "+", adid, 1);
//                            // 超时IDFA添加到超时队列
//                            activeUtil.timeout(adid, idfa, timeStr, timeout / 60);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            LOG.error("checkActiveTimeOut error : " + e.getMessage());
//        } finally {
//            if (jedis != null)
//                RedisConnection.close("control", jedis);
//            if (jedisMain != null)
//                RedisConnection.close("main", jedisMain);
//        }
//    }

    /**
     * 查询广告appstore关键词排名，计划启动和到量换关键词时使用
     * @param adid
     * @param appstoreid
     * @param keywords
     */
    public static void appstore(String adid, String appstoreid, String keywords) {
        Jedis jedis = RedisConnection.getInstance("appstore");
        String nowdate = DateUtils.formatDate();
        String message = "" + adid + "," + appstoreid + "," + keywords + "," + nowdate + "";
        long result = jedis.rpush("DATA_DEAL_APPSTROE_INFO_VAL", message);
//        LOG.debug("appstore : adid={} : appstore info to redis : message={} result={}", adid, message, result);
        RedisConnection.close("appstore", jedis);
    }

//    public static void main(String[] args) {
//        checkActiveTimeOut();
//    }
}
