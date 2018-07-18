package com.powerwin.processor;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.powerwin.boot.config.Define;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.entity.*;
import com.powerwin.parser.CallbackParser;
import com.powerwin.store.*;
import com.powerwin.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * 激活业务处理器
 */
public class ActiveProcessor extends CallbackProcessor {

    public static Logger LOG = LogManager.getLogger(ActiveProcessor.class);


    public List<Object>[] process(List<Object> vals) {

        int action = 0, type = 0, adid = 0, active_num = 0, source = 0, isEnable = 0;
        String mac, udid, openudid;
        long disk = 0;

        action = ListUtil.getInt(vals, CallbackParser.Index.ACTION);
        adid = ListUtil.getInt(vals, CallbackParser.Index.ADID);
        mac = ListUtil.getString(vals, CallbackParser.Index.MAC);
        udid = ListUtil.getString(vals, CallbackParser.Index.UDID);
        openudid = ListUtil.getString(vals, CallbackParser.Index.OPENUDID);
        source = ListUtil.getInt(vals, Index.FROM);
        type = ListUtil.getInt(vals, CallbackParser.Index.TYPE);
        try {
            // 是否绕过防作弊，补偿逻辑使用，如果大于0则是补偿激活
            isEnable = ListUtil.getInt(vals, CallbackParser.Index.ISENABLE);
            active_num = ListUtil.getInt(vals, CallbackParser.Index.ACTIVE_NUM);
            disk = ListUtil.getLong(vals, CallbackParser.Index.DISK);
        } catch (Exception e) {
        }

        if (action == 0 || adid == 0) {
            LOG.trace(String.format("data item has error adid=%d action=%d type=%d", adid, action, type));
            return null;
        }
        //if (active_num != 0) {
        if (active_num != 0 && type !=6 ) {
            LOG.trace(String.format("active active_num error adid=%d action=%d type=%d", adid, action, type));
            return null;
        }
        if (mac == null && udid == null && openudid == null) {
            LOG.debug(String.format("data item has error mac=%s udid=%s openudid=%s", mac, udid, openudid));
            return null;
        }
        // by jackcao add host scress start
        if (type == 6) {// 广告类型是热门推荐，单独处理
            new HotscreenActiveProcessor().process(vals);
            return null;
        }
    // by jackcao add host scress end

        int year = ListUtil.getInt(vals, Index.YEAR);
        int mon = ListUtil.getInt(vals, Index.MON);
        int day = ListUtil.getInt(vals, Index.DAY);
        int hour = ListUtil.getInt(vals, Index.HOUR);

        Ads ad = AdsCache.getInstance().get(adid);
        if (ad == null) {
            LOG.debug("ad " + adid + " not found.");
            return null;
        }
//        type = ad.getDataType();
        type = 0;
        // 条件之一 ： ad.getIsHsFlag() == 3
        if (isEnable == 0) {
            // 快速精准控量 补偿逻辑不再精准控量
            RemainActiveUtil activeUtil = RemainActiveUtil.getInstance();
            String tidfa = udid.toLowerCase().replaceAll("-", "");
            if (!activeUtil.isIdfaExists(adid, tidfa)) {
                LOG.info("active wait idfa is not exist adid={"+adid+"} udid={"+udid+"} idfa={"+tidfa+"}");
                return null;
            } else {
                // 从idfa激活等待列表中移除对应的idfa
                activeUtil.idfaWaitActive("-", adid, tidfa);
            }
        }
        int remain = RemainActiveUtil.getRemain(adid);
        LOG.info("active : adid={"+adid+"} udid={"+udid+"} | remain num : {"+remain+"}");
        CallbackItem item = this.getHistory(year, mon, day, type, adid, mac, udid);
        // 不存在点击,已经激活
        if (item == null) {
            LOG.warn(String.format("Pre action not found for : action=%d adid=%d mac=%s udid=%s openudid=%s", action,
                    adid, mac, udid, openudid));
            return null;
        }

        // 点击和跳转都不存在
        if (item.action != Define.ACTION_CLICK && item.action != Define.ACTION_JUMP
                && item.action != Define.ACTION_ACTIVE) {
            LOG.warn(String.format(
                    "actived for : item.date=%d item.action=%d item.mac=%s item.udid=%s action=%d adid=%d mac=%s udid=%s openudid=%s",
                    item.date, item.action, item.mac, item.udid, action, adid, mac, udid, openudid));
            return null;
        }
        int created = this.datetime(vals).timestamp();

        // 广告下线后，我们只接收广告主三天内的激活，给媒体下发只发1天内的激活
        // 广告停止3天后不返数据，ad.getState() != 5 && ad.getState() != 8 &&
        if (ad.getStatus() != 4 && ad.getStatus() != 7 && (created - item.open_time) > 60 * 60 * 24 * 3) {
            LOG.warn(String.format(
                    "click timeout : item.date=%d item.action=%d item.mac=%s item.udid=%s action=%d adid=%d mac=%s udid=%s openudid=%s",
                    item.date, item.action, item.mac, item.udid, action, adid, mac, udid, openudid));
            return null;
        }

//        item.cid = ad.getCid();
        item.cid = 0;

        int appid = item.appid;
        Media media = MediaCache.getInstance().get(appid);
        if (media == null) {
            LOG.debug("media " + item.appid + " not found.");
            return null;
        }

        int data_from = media.getType() == 1 ? 1 : 3;
//        int ad_from = ad.getDataFrom();
        int ad_from = 0;

        float income = 0;
        float cost = 0;

        // 多次激活对应价格读取
        int len = 1;
        try {
//            String json = ad.getJobs();
            String json = "";
            JSONObject jsonObj = JSONObject.parseObject(json);
            JSONArray jobs = jsonObj.getJSONArray("jobs");
            len = jobs.size();
            // 兼容老版本sdk jobs.len =0的情况
            if (active_num < 0 || active_num > len) {
                LOG.debug("job active_num error,num：" + active_num + " jobs:" + len);
                return null;
            }
            JSONObject job = (JSONObject) jobs.get(active_num);
//            String billing = ad.getBilling();
            String billing = "";
            if (billing.indexOf('2') >= 0) {
                String sIncome = "0";
                if (jsonObj.get("income").toString()==null || jsonObj.get("income").toString().length() == 0 ||jsonObj.get("income").toString()=="") {
                    LOG.debug("active income error :  adid : " + adid);
                } else {
                    sIncome = jsonObj.get("income").toString();
                }
                income = Float.parseFloat(sIncome);
                if (income == 0) {
//                    income = ad.getPriceCallbackIncome();
                    income = 0;
                }

                cost = this.getPrice(appid, adid);
                if (cost == 0) {
                    cost = Float.parseFloat(job.get("const").toString());
                    if (cost == 0) {
//                        cost = ad.getPriceCallbackCost();
                        cost = 0;
                    }
                }
            }
        } catch (Exception e) {
//            income = ad.getPriceCallbackIncome();
//            cost = ad.getPriceCallbackCost();
            income = 0;
            cost = 0;
            LOG.warn(String.format("active adid=%d,exception=%s", adid, e.getMessage()));
        }
        // 查找当前激活使用的关键词
        item.keywords = "";
//        item.keywords = ad.getKeywords();

        // 广告已经激活
        if (item.action == Define.ACTION_ACTIVE) {
            // 广告由进程激活后广告主回调激活   ad.getProcess_name() != null && !ad.getProcess_name().isEmpty()
            if ((false) && item.process == Define.FROM_PROCESS
                    && source != Define.FROM_PROCESS) {
                // 上报数据但不计入广告主数据 ad.getIsHsReport() == 2 || ad.getIsHsReport() == 0
                if (false) {


                    LOG.warn(String.format("advertisers cover process ：action=%d, item.income=%.2f, income=%.2f",
                            action, item.income, income));
                    int invalid = item.invalid > 0 ? 1 : 0;

                    // 广告主记费   原代码
                    DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, type, item.data_from, item.ad_from,
                            item.appid, item.uid, item.adid, item.cid,0,0);
                    CountValues cv = CountValues.create(action, 1, invalid, 0, 0, income, 0);
                    Counter.getInstance().add(ck, cv);

                    // 更新激活来源
                    item.process = Define.FROM_SDK;
                    DBHistoryStore.getInstance(FileStore.STORE_STORE, item.date, type, Define.ACTION_CLICK)
                            .update(item);
                }
            }
            return null;
        } else {
            // 找到的点击，判断历史是否已经激活
            boolean isHistoryActive = RedisStore.getInstance().isActive(item.mac + item.udid, adid);
            if (isHistoryActive) {
                LOG.warn(String.format("history active exists : action=%d adid=%d mac=%s udid=%s openudid=%s", action,
                        adid, mac, udid, openudid));
                return null;
            }
        }

        DateTime callback = datetime(vals);
        int interval = (int) (callback.timestamp() - item.open_time);
        long ipcount = 0;

        int date = this.date(vals);
        // 同一IP同一广告IP数不得超过设定值（刘卿）
        //todo
        ipcount = IPCountStore.getInstance().add("IP_" + date, action, adid, item.ip, 1);

        int invalid = 0;

        int ipNum = 0;
        int intervalNum = 0;
//        int ipNum = ad.getIpNum();
//        int intervalNum = ad.getInterval();
        // TODO: check boot time num
        // int boottimeNum = ad.getBootTimeNum();
         int boottimeNum = 0;
        if (isEnable == 0) {
            // ip 假量
            if (media.getType() != 2 && media.getState() != 4) {
                if (ipNum > 0 && ipcount > ipNum) {
                    invalid |= 1;
                }
            }

            // 时间假量
            if (intervalNum > 0 && interval < intervalNum) {
                invalid |= 2;
            }
            // 今日Appuserid已经激活该广告，假量
            if (item.appid == 7823) {
                LOG.trace("process item ischeat");
                boolean isCheat = RedisStore.getInstance().isCheat(item.adid, item.appid, item.appuserid, item.udid);
                if (isCheat) {
                    LOG.warn(String.format("appuserid today actived : action=%d adid=%d mac=%s udid=%s openudid=%s",
                            action, adid, mac, udid, openudid));
                    invalid |= 3;
                }
            }
        }

        // 是否启用防作弊 liuhuiya
        int isPay = 0;
        // 获取广告防作弊设置 ad by chenjun     参数：ad.getOptions()
        Map<String, Integer> ADS_OPTIONS = Cheat.parseAdOptions("");
        int adRate = ADS_OPTIONS.get(Cheat.KEY_AD_RATE);
        isPay = ADS_OPTIONS.get(Cheat.KEY_ISPAY);
        // 条件之一：media.getIsEnable() == 0
        if (isEnable == 0) { // 启用
            // 获取广告防作弊设置 ad by chenjun
            boolean isAdCheck = RandUtil.isRand(adRate);
            if (adRate == 0) {
                isAdCheck = true;
            }

            // 获取媒体防作弊设置      参数：media.getOptions()
            Map<String, Integer> OPTIONS = Cheat.parseMediaOptions("");
            int mRate = OPTIONS.get(Cheat.KEY_RATE);
            boolean isCheck = RandUtil.isRand(mRate);
            if (mRate == 0) {
                isCheck = true;
            }

            /**
             * 激活DID防作弊判断    add by chenjun
             */
            if (invalid == 0 && ADS_OPTIONS.get(Cheat.KEY_SESSION) == 1) {

                int didResult = RedisStore.getInstance().checkDidCheat(ad, udid);
                if (didResult > 0 && isAdCheck) {
                    invalid = didResult;
                }
            }
            if (invalid == 0 && OPTIONS.get(Cheat.KEY_SESSION) == 1) {
                int didResult = RedisStore.getInstance().checkDidCheat(ad, udid);
                if (didResult > 0 && isCheck) {
                    invalid = didResult;
                }
            }

            /**
             * 防作弊：SSID 激活判断   add by chenjun
             */
            if (invalid == 0 && ADS_OPTIONS.get(Cheat.KEY_SSID) == 1) {

                int ssidResult = RedisStore.getInstance().checkSsidCheat(ad, udid);
                if (ssidResult > 0 && isAdCheck) {
                    invalid = ssidResult;
                }
            }
            if (invalid == 0 && OPTIONS.get(Cheat.KEY_SSID) == 1) {
                int ssidResult = RedisStore.getInstance().checkSsidCheat(ad, udid);
                if (ssidResult > 0 && isCheck) {
                    invalid = ssidResult;
                }
            }

            /**
             * 防作弊：OPENUDID 激活判断   add by chenjun
             */
            if (invalid == 0 && ADS_OPTIONS.get(Cheat.KEY_OPENUDID) == 1) {
                int ssidResult = RedisStore.getInstance().checkOpenudidCheat(ad, udid);
                if (ssidResult > 0 && isAdCheck) {
                    invalid = ssidResult;
                }
            }
            if (invalid == 0 && OPTIONS.get(Cheat.KEY_OPENUDID) == 1) {
                int ssidResult = RedisStore.getInstance().checkOpenudidCheat(ad, udid);
                if (ssidResult > 0 && isCheck) {
                    invalid = ssidResult;
                }
            }

            /**
             * 防作弊软件激活的判断   add by chenjun
             */
            if (invalid == 0 && ADS_OPTIONS.get(Cheat.KEY_IG) == 1) {

                boolean isCheat = RedisStore.getInstance().checkCheatSoft(item.mac, item.udid);
                if (isCheat) {
                    invalid = 14;
                }
            }
            if (invalid == 0 && OPTIONS.get(Cheat.KEY_IG) == 1) {
                boolean isCheat = RedisStore.getInstance().checkCheatSoft(item.mac, item.udid);
                if (isCheat) {
                    invalid = 14;
                }
            }

            /**
             * 磁盘空间变化  add by chenjun
             */
            if (invalid == 0 && disk > 0 && ADS_OPTIONS.get(Cheat.KEY_DISK) == 1) {


                boolean flag = RedisStore.getInstance().checkActiveDisk(mac, udid, ad, disk);
                if (flag) {
                    invalid = 15;
                }
            }
            if (invalid == 0 && disk > 0 && OPTIONS.get(Cheat.KEY_DISK) == 1) {
                boolean flag = RedisStore.getInstance().checkActiveDisk(mac, udid, ad, disk);
                if (flag) {
                    invalid = 15;
                }
            }

            /**
             * 进程异常
             */
            if (invalid == 0 && ADS_OPTIONS.get(Cheat.KEY_PROCESS) == 1) {
                boolean isNormal = RedisStore.getInstance().checkNormalSoft(item.mac, item.udid);
                if (!isNormal && isAdCheck) {
                    invalid = 18;
                }
            }
            if (invalid == 0 && OPTIONS.get(Cheat.KEY_PROCESS) == 1) {
                boolean isNormal = RedisStore.getInstance().checkNormalSoft(item.mac, item.udid);
                if (!isNormal && isCheck) {
                    invalid = 18;
                }
            }
        }
        if (isEnable == 0) {
            // 超过24小时不返回数据，ad.getState() != 5 && ad.getState() != 8 &&
            if (ad.getStatus() != 4 && ad.getStatus() != 7 && (created - item.open_time) > 60 * 60 * 24) {
                invalid = 8;
            }
        }

        item.invalid = item.invalid == 0 ? invalid : item.invalid;
        item.saved = item.saved == 0 ? (item.invalid > 0 ? 1 : 0) : item.saved;

        // 乐抢评分浮动金额
        float ruleCost = 0f;
        try {
            // ad.getOptions()
            JSONObject optJson = JSONObject.parseObject("");
            if (optJson != null && optJson.get("float_money") != null) {
                float float_moeny = Float.parseFloat(optJson.get("float_money").toString());
                if (cost > 0 && (source == Define.FROM_SDK || source == Define.FROM_PROCESS)) {
                    if (float_moeny > 0) {
                        Jedis jedis = null;
                        try {
                            jedis = RedisConnection.getInstance("rule");
                            String idfa = udid.replace("-", "");
                            idfa = idfa.toLowerCase();

                            String redisText = jedis.hget("RULE_UDID_SCORE", idfa);
                            if (redisText == null || redisText.length() == 0) {
                                redisText = "0,0,0";
                            }
                            String[] tmp = redisText.split(",");
                            float ruleScore = Float.parseFloat(tmp[0]);

                            float t = float_moeny * ruleScore;
                            float cost2 = cost + t;
                            LOG.debug(String.format(
                                    "active rule score udid:%s adid:%d float_moeny:%.2f ruleScore: %.2f cost:%.2f cost2:%.2f",
                                    udid, adid, float_moeny, ruleScore, cost, cost2));

                            cost = cost2;
                            ruleCost = cost2;
                        } catch (Exception e) {
                            ruleCost = -1f;
                            LOG.debug(String.format("active rule score udid exception:%s adid:%d float_moeny:%.2f %s",
                                    udid, adid, float_moeny, e.getMessage()));
                        } finally {
                            if (jedis != null) {
                                RedisConnection.close("rule", jedis);
                            }
                        }
                    }
                } else if (cost > 0 && (source == Define.FROM_API)) { // 快速任务API激活，不走用户评分规则
                    cost = cost + float_moeny;
                }
            }
        } catch (Exception e) {
            LOG.error("float money error : " + e.getMessage());
        }
        DataSave.DataSaveRole role = DataSave.getRole(media, ad);
        cost = (item.saved == 1 || invalid > 0) ? 0 : DataSave.getRate(role, cost);

        float score = 0;
        if (cost > 0 && media.getType() == 1) {
            MediaApp app = (MediaApp) media;
//            float ratio = app.getRatio();
            float ratio = 0;
            if (ratio > 0) {
                score = cost * ratio;
            }
        }

        int unique = 1;
        int count = 1;
        int saved = item.saved > 0 || invalid > 0 ? 0 : 1;

        int uid = 0;
        int cid = 0;
//        int uid = media.getUid();
//        int cid = ad.getCid();

        // 进程激活  条件之一：ad.getProcess_name() != null && !ad.getProcess_name().isEmpty()
        if (source == Define.FROM_PROCESS) {
            // 上报数据但不计入广告主数据   条件：ad.getIsHsReport() == 2 || ad.getIsHsReport() == 0
            if (false) {
                count = 0;
                income = 0;
            }
            unique = 1;
        }

        // 激活统计
        //todo
        DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, type, data_from, ad_from, appid, uid, adid, cid,0,0);
        CountValues cv = CountValues.create(action, count, invalid > 0 ? 1 : 0, unique, saved, income, cost);
        Counter.getInstance().add(ck, cv);


        // 数据激活
        item.create_time = this.datetime(vals).timestamp();
        item.action = action;

        item.income = income;
        item.cost = cost;
        item.score = score;
        item.process = source;

        LOG.info(String.format(
                "active for : action=%d adid=%d mac=%s udid=%s openudid=%s invalid=%d saved=%d cost=%.2f ruleCost=%.2f",
                action, adid, mac, udid, openudid, invalid, saved, cost, ruleCost));
        if (date == item.date) {
            int result = DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK)
                    .update(item);
            if (result <= 0) {
                LOG.error(String.format("update active if : action=%d adid=%d mac=%s udid=%s openudid=%s", action, adid,
                        mac, udid, openudid));
                return null;
            }
        } else {
            long result = DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).put(adid,
                    mac, udid, item);
            if (result <= 0) {
                LOG.error(String.format("update active else : action=%d adid=%d mac=%s udid=%s openudid=%s", action,
                        adid, mac, udid, openudid));
                return null;
            }
        }

        RedisStore.getInstance().addActive(item.mac + item.udid, adid);

        updateProcessInfo(action, adid, active_num, mac, udid, media, len);
        // 媒体所有广告前10不扣量，渠道针对单一广告前50不扣量，玩否暂时没有使用
        appCounter(ad, media);
        // 下发积分给用户
        sendScoreToUser(active_num, ad, media, item, isPay);
        // 快速任务实时上报IDFA给广告主
        reportToCp(ad, item);
        return null;
    }



    /**
     * 更新用户做过广告信息库（redis）
     * @param action 动作类型
     * @param adid 广告ID
     * @param num 激活次数
     * @param mac mac地址
     * @param udid uuid
     * @param media 媒体
     * @param len 
     */
    private void updateProcessInfo(int action, int adid, int num, String mac, String udid, Media media, int len) {
        // 先将自媒体appid 排除，后续再修改
//        media.getMid() != 7329 && media.getMid() != 7789 && media.getMid() != 8092 && media.getMid() != 8079 && media.getMid() != 7970 && media.getMid() != 7715
        if (false) {
            // 同步激活时间，用于控制深度任务的显示
            // 1.获取DATA_DEVICE_ADID信息
            Jedis jedisProcess = null;
            String macNew = mac;
            try {
                jedisProcess = RedisConnection.getInstance("process");
                String keyProcess = "DATA_DEVICE_ADID";
                String field = String.format("%s%s%d", macNew, udid, adid);
                String info = jedisProcess.hget(keyProcess, field);
                // 是否激活(0未 1已)
                int isAllFinish = (len == num + 1) ? 1 : 0;
                int atime = (int) (System.currentTimeMillis() / 1000);
                if (info == null) {
                    // 标记状态为1:是否激活 (0未 1已), 上次点击时间，已激活次数，上次激活时间，第一次点击时间
                    String v = String.format("%d,%d,%d,%d,%d", isAllFinish, atime, num + 1, atime, atime);
                    jedisProcess.hset(keyProcess, field, v);
                    LOG.info("active : adid={"+adid+"} udid={"+udid+"} | insert process info to redis[DATA_DEVICE_ADID], key={"+field+"},value={"+v+"}");
                } else {
                    String[] infoArr = info.split(",");
                    if (infoArr[0].equals("0")) {
                        int ctime = infoArr[1] == null ? atime : Integer.parseInt(infoArr[1]);
                        int fctime = 0;
                        try {
                            fctime = infoArr[4] == null ? 0 : Integer.parseInt(infoArr[4]);
                        } catch (Exception e) {
                            fctime = 0;
                        }
                        String v = String.format("%d,%d,%d,%d,%d", isAllFinish, ctime, num + 1, atime, fctime);
                        jedisProcess.hset(keyProcess, field, v);
                        LOG.info("active : adid={"+adid+"} udid={"+udid+"} | update process info to redis[DATA_DEVICE_ADID], key={"+field+"},value={"+v+"}");
                    }
                }
            } catch (Exception e) {
                LOG.warn("active :  ={"+adid+"} udid={"+udid+"} | update process info to redis[DATA_DEVICE_ADID] error : {"+e.getMessage()+"}");
            } finally {
                if (jedisProcess != null){
                    RedisConnection.close("process", jedisProcess);
                }
            }
        }
    }

    /**
     * 媒体所有广告前10不扣量，渠道针对单一广告前50不扣量
     * @param ad 广告
     * @param media 媒体
     */
    private void appCounter(Ads ad, Media media) {
        CachedValue appcounter = null;
        String appKey = "";
        if (media.getType() == Define.MEDIA_TYPE_CHANNEL) {
            appcounter = CachedValue.getInstance("CHANNEL_ACTIVED_NUM");
            appKey = String.format("%d%d", 0, 0);
//            appKey = String.format("%d%d", media.getMid(), ad.getAdid());
        } else {
            appcounter = CachedValue.getInstance("APP_ACTIVED_NUM");
            appKey = String.valueOf(0);
//            appKey = String.valueOf(media.getMid());
        }
        String value = appcounter.get(appKey);
        if (value != null) {
            int v = Integer.parseInt(value);
            if (v < Define.ACTIVE_SAVE_MAX) {
                appcounter.put(appKey, String.valueOf(v + 1));
            }
        } else {
            appcounter.put(appKey, "1");
        }
    }

    /**
     * 下发积分给用户
     * @param active_num 激活次数
     * @param ad 广告
     * @param media 媒体
     * @param item 回调报表数据
     * @param isPay 是否付费任务
     */
    private void sendScoreToUser(int active_num, Ads ad, Media media, CallbackItem item, int isPay) {
        if (item.saved == 1 || item.invalid > 0) {
            LOG.info("media active save : " + item.uid + " " + item.appid + " " + item.appuserid + " " + item.mac + " "
                    + item.udid + " " + item.openudid + " " + item.score);
        } else {
            if (media.getType() == 1) {
                MediaApp app = (MediaApp) media;
                // 空串位置是：ad.getProcess_name()
                UserScore.sendScore(app, ad, item.appuserid, item.mac, item.udid, item.openudid, item.score,
                        "", active_num, isPay);
                LOG.info("active : adid={"+"ad.getAdid()"+"} udid={"+item.udid+"} | user send score " + item.uid + " " + item.appid + " "
                        + item.appuserid + " " + item.mac + " " + item.udid + " " + item.openudid + " " + item.score);
            } else {
                // 条件之一
                if (false && item.appuserid != null) {
                    String url;
                    try {
                        url = URLDecoder.decode(item.appuserid, "UTF-8");
                        if (url.length() > 4) {
                            String proto = url.substring(0, 4).toLowerCase();
                            if (proto.equals("http")) {
                                url = url.replaceAll("&amp;", "&");
                                JSONObject obj = new JSONObject();
                                obj.put("protocol", "http");
                                obj.put("method", "get");
                                obj.put("url", url);
                                LOG.debug("channel callback : " + url);
                                Jedis jedis = RedisConnection.getInstance("values");
                                jedis.rpush("ACTION_HTTP_REQUEST", obj.toString());
                                RedisConnection.close("values", jedis);
                            } else {
                                LOG.error("error proto channel " + item.appid + " callback : " + url);
                            }
                        } else {
                            LOG.error("error proto channel " + item.appid + " callback : " + url);
                        }
                    } catch (UnsupportedEncodingException e) {
                        LOG.error("error on decode channel " + item.appid + " callback : " + item.appuserid);
                    }
                } else {
                    LOG.debug("channel has no callback" + item.appid + item.appuserid);
                }
            }
        }
    }

    /**
     * 快速任务实时上报IDFA给广告主
     * @param ad 广告
     * @param item 回调报表数据
     */
    private void reportToCp(Ads ad, CallbackItem item) {
        try {
            //ad.getOptions()
            JSONObject optJson = JSONObject.parseObject("");
            if (optJson != null) {
                String reportIdfaUrl = String.valueOf(optJson.get("report_idfa_url"));
                String reportIdfaKey = String.valueOf(optJson.get("report_idfa_key"));
                if (reportIdfaUrl != null && reportIdfaUrl.length() > 0) {
                    if (reportIdfaKey == null || reportIdfaKey.length() == 0) {
                        reportIdfaKey = "";
                    }
                    // 部分广告主要求上报带有激活真实IP
                    if (reportIdfaUrl.contains("#IP#")) {
                        reportIdfaUrl = reportIdfaUrl.replace("#IP#", IP2Location.long2ip(item.ip));
                    }
                    // 组织上报
                    String httpUrl = "";
                    String params = "";
                    if (reportIdfaUrl.indexOf("?") > 0) {
                        params = String.format("idfa=%s&key=%s", item.udid.toUpperCase(), reportIdfaKey);
                        httpUrl = String.format("%s&idfa=%s&sign=%s", reportIdfaUrl, item.udid.toUpperCase(),
                                Md5.crypt(params));
                    } else {
                        params = String.format("idfa=%s&key=%s", item.udid.toUpperCase(), reportIdfaKey);
                        httpUrl = String.format("%s?idfa=%s&sign=%s", reportIdfaUrl, item.udid.toUpperCase(),
                                Md5.crypt(params));
                    }

                    JSONObject obj = new JSONObject();
                    obj.put("protocol", "http");
                    obj.put("method", "get");
                    obj.put("type", "sdk");
                    obj.put("url", httpUrl);
                    LOG.debug("active : adid={"+"item.udid"+"} udid={"+item.udid+"} | report idfa to cp : {"+httpUrl+"}");
                    Jedis jedis = RedisConnection.getInstance("report");
                    jedis.rpush("REPORT_IDFA_TOCP", obj.toString());
                    RedisConnection.close("report", jedis);
                }
            }
        } catch (Exception e) {
            LOG.error("report idfa[{"+item.udid+"}] to cp[{"+"ad.getAdid()"+"}] error : {"+e.getMessage()+"}");
        }
    }

    public float[] getCost(Ads ad, CallbackItem item, int active_num) {
        float income = 0;
        float cost = 0;
        int len = 1;
        try {
//            String json = ad.getJobs();
            String json = "";
            JSONObject jsonObj = JSONObject.parseObject(json);
            JSONArray jobs = jsonObj.getJSONArray("jobs");
            len = jobs.size();
            // 兼容老版本sdk jobs.len =0的情况
            if (active_num < 0 || active_num > len) {
                LOG.debug("job active_num error,num：" + active_num + " jobs:" + len);
                return null;
            }
            JSONObject job = (JSONObject) jobs.get(active_num);
//            String billing = ad.getBilling();
            String billing = "";
            if (billing.indexOf('2') >= 0) {
                String sIncome = "0";
                if (jsonObj.get("income").toString()==null || jsonObj.get("income").toString().length() == 0 || jsonObj.get("income").toString()=="") {
                    //ad.getAdid()
                    LOG.debug("active income error :  adid : " + 0);
                } else {
                    sIncome = jsonObj.get("income").toString();
                }
                income = Float.parseFloat(sIncome);
                if (income == 0) {
//                    income = ad.getPriceCallbackIncome();
                    income = 0;
                }

                //ad.getAdid()
                cost = this.getPrice(item.appid, 0);
                if (cost == 0) {
                    cost = Float.parseFloat(job.get("const").toString());
                    if (cost == 0) {
//                        cost = ad.getPriceCallbackCost();
                        cost = 0;
                    }
                }
            }
        } catch (Exception e) {
            income = 0;
            cost = 0;
//            income = ad.getPriceCallbackIncome();
//            cost = ad.getPriceCallbackCost();
            //ad.getAdid()
            LOG.warn(String.format("active adid=%d,exception=%s", 0, e.getMessage()));
        }
        return new float[] { income, cost };
    }
}
