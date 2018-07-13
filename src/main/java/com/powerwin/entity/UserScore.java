package com.powerwin.entity;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class UserScore {

    public static Logger LOG = LogManager.getLogger(UserScore.class);

    public static long hash(String value) {
        char chs[] = value.toCharArray();
        long hash = 0;
        int len = chs.length;
        for (int i = 0; i < len; i++) {
            hash = ((hash << 5) + hash - 6) + ((long) chs[i]);
        }
        return hash;
    }

    public static String getVal(String val) {
        String value = val;
        int index = value.indexOf(".");
        if (value.contains(".")) {
            value = value.substring(index + 1);
        }
        boolean allZero = true;
        for (int i = 0; i < value.length(); i++) {
            if ('0' != value.charAt(i)) {
                allZero = false;
                break;
            }
        }
        if (allZero) {
            return val.substring(0, index);
        } else {
            return val;
        }
    }

    public static void sendScore(MediaApp media, Ads ad, String appuserkey, String mac, String udid, String openudid,
            float score, String process, int active_num, int isPay) {

//        String offerwall = null;
//        switch (ad.getDataType()) {
//        case 1:
//            offerwall = media.getOfferwall();
//            break;
//        case 2:
//            offerwall = media.getFreewall();
//            break;
//        case 3:
//            offerwall = media.getInsertscreen();
//            break;
//        case 4:
//            offerwall = media.getFullscreen();
//            break;
//        case 5:
//            offerwall = media.getBanner();
//            break;
//        case 6:
//            offerwall = media.getHotscreen();
//            break;
//        case 7:
//            offerwall = media.getVideoscreen();
//            break;
//        default:
//            break;
//        }
//        if (offerwall == null) {
//            LOG.error("send score to user error : " + media.getMid());
//            return;
//        }
//
//        long hashId = 0;
//        if (appuserkey == null || appuserkey.isEmpty()) {
//            hashId = hash(media.getUid() + openudid);
//        } else {
//            hashId = hash(media.getUid() + appuserkey);
//        }
//
//        String hashKey = String.valueOf(hashId);
//
//        Jedis redis = RedisConnection.getInstance("main");
//        String old = redis.hget("DATA_USER_SCORE", hashKey);
//        Long result;
//        if (old != null) {
//            float oldn = Float.parseFloat(old);
//            result = redis.hset("DATA_USER_SCORE", hashKey, String.valueOf(score + oldn));
//        } else {
//            result = redis.hset("DATA_USER_SCORE", hashKey, String.valueOf(score));
//        }
//        LOG.info("score : adid={} udid={} | send user score to redis. result:{} score:{}", ad.getAdid(), udid, result,
//                score);
//        RedisConnection.close("main", redis);
//
//        JSONObject opts;
//        try {
//            opts = new JSONObject(offerwall);
//            int callback = opts.getInt("callback");
//            if (callback == 0) {
//                LOG.error("send score to callback error : " + media.getMid());
//                return;
//            }
//        } catch (Exception e1) {
//            LOG.error("send score to callback error : " + e1.getMessage());
//            return;
//        }
//
//        String callbackUrl = media.getCallbackUrl();
//        if (callbackUrl != null && callbackUrl!="") {
//            callbackUrl = callbackUrl.replace("amp;", "");
//
//            String appHost = callbackUrl;
//            String parames = "";
//            if (callbackUrl.indexOf("?") > 0) {
//                appHost = callbackUrl.substring(0, callbackUrl.indexOf("?"));
//                parames = callbackUrl.substring(callbackUrl.indexOf("?"), callbackUrl.length());
//                parames += "&";
//            } else {
//                parames += "?";
//            }
//            // 密钥
//            String appsecret = media.getCallbackKey();
//            String userId = appuserkey;
//            String adName = ad.getAdname();
//            String deviceid = MACUtil.parse(mac) == null ? udid : mac;
//            String appkey = DrkeyUtil.encode(media.getUid(), media.getMid());
//            String adskey = DrkeyUtil.encode(ad.getCid(), ad.getAdid());
//
//            String drkey = appkey + adskey + deviceid + active_num;
//            String time = String.valueOf(System.currentTimeMillis() / 1000);
//            String httpUrl = "";
//            String backJson = "";
//            try {
//                // 保留小数点，整数去掉小数点
//                // String tmpScore = getVal(String.valueOf(score));
//                int tmpScore = (int) score;
//                long hash = hash(drkey);
//                String key = parames + String.format(
//                        "hashid=%s&appid=%d&adid=%d&adname=%s&userid=%s&deviceid=%s&source=dianru&point=%s&time=%s&is_pay=%d&active_num=%d&appsecret=%s",
//                        String.valueOf(hash), media.getMid(), ad.getAdid(), adName, userId, deviceid, tmpScore, time,
//                        isPay, active_num, appsecret);
//
//                parames += String.format(
//                        "hashid=%s&appid=%d&adid=%d&adname=%s&userid=%s&deviceid=%s&source=dianru&point=%s&time=%s&is_pay=%d&active_num=%d&checksum=%s",
//                        String.valueOf(hash), media.getMid(), ad.getAdid(),
//                        adName == null ? "" : URLEncoder.encode(adName, "UTF-8"),
//                        userId == null ? "" : URLEncoder.encode(userId, "UTF-8"), deviceid, tmpScore, time, isPay,
//                        active_num, Md5.crypt(key));
//                httpUrl = appHost + parames;
//                LOG.info("score : adid={} udid={} | send user score to http. userid:{} point:{} is_pay:{}",
//                        ad.getAdid(), udid, userId, tmpScore, isPay);
//                JSONObject obj = new JSONObject();
//                obj.put("protocol", "http");
//                obj.put("method", "get");
//                obj.put("url", httpUrl);
//                Jedis jedis = RedisConnection.getInstance("values");
//                jedis.rpush("ACTION_HTTP_REQUEST", obj.toString());
//                RedisConnection.close("values", jedis);
//                LOG.debug("score : adid={} udid={} | send user score to http. url:{} key:{}", ad.getAdid(), udid,
//                        httpUrl, key);
//            } catch (Exception e) {
//                LOG.error(httpUrl + "  " + backJson + " " + e.getMessage());
//            }
//        }
    }
}
