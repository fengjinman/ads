package com.powerwin.entity;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Map;


public class Media extends Common  implements Serializable {

	public static Logger LOG = LogManager.getLogger(Media.class);
	private static final long serialVersionUID = 6490713456309313519L;
	protected int devid;
    protected String cpname;
    protected String linkname;
    protected String moblie;
    protected String qqnumber;
    protected String skype;
    protected String cname;
    protected String caddress;
    protected String ywcard;
    protected String ywcardpic;
    private int status;
    protected int type;
    protected float money;
    protected float finish_money;
    protected String idcard;
    protected String wxnumber;
    protected int locks;
    protected int state;
    protected int update_time;
    protected int create_time;
    protected int id;
    protected String name;
    protected int medtype;
    protected String pgname;
    protected String downloadurl;
    protected int addtype;
    protected int moneytype;
    protected String desc;
    protected int ctype;
    protected int cstype;
    protected String sendarea;
    protected String noaddtype;
    protected int buckle;

    protected int medstatus;
    protected String appkey;
    protected int medupdate_time;
    protected int medcreate_time;
    
    public Media(Map<String, Object> map) {
        this.devid = (Integer) getValue(map, "devid","int");
        this.cpname = (String) getValue(map, "cpname","String");
        this.linkname = (String) getValue(map, "linkname","String");
        this.moblie = (String) getValue(map,"moblie","string");
        this.qqnumber = (String) getValue(map, "qqnumber","String");
        this.skype = (String) getValue(map, "skype","String");
        this.cname = (String) getValue(map, "cname","String");
        
        this.caddress = (String) getValue(map, "caddress","String");
        this.ywcard = (String) getValue(map, "ywcard","String");
        this.ywcardpic = (String) getValue(map, "ywcardpic","string");
        this.status = (Integer) getValue(map, "status","int");
        this.type = (Integer) getValue(map, "type","int");
        this.money = (Float) getValue(map, "money","float");
        this.finish_money = (Float) getValue(map, "finish_money","float");
        this.idcard = (String) getValue(map, "idcard","String");
        this.wxnumber = (String) getValue(map, "wxnumber","String");
        this.locks = (Integer) getValue(map, "locks","int");
        this.state = (Integer) getValue(map, "state","int");
        this.update_time = (Integer) getValue(map, "update_time","int");
        this.create_time = (Integer) getValue(map, "create_time","int");


     
        this.id = (Integer) getValue(map, "id","int");
        this.name = (String) getValue(map, "name","string");
        this.medtype = (Integer) getValue(map, "medtype","int");
        this.pgname = (String) getValue(map, "pgname","string");
        this.downloadurl = (String) getValue(map, "downloadurl","string");
        this.addtype = (Integer) getValue(map, "addtype","int");      
        this.moneytype = (Integer) getValue(map, "moneytype","int");
        this.desc = (String) getValue(map, "desc","string");
        this.ctype = (Integer) getValue(map, "ctype","int");
        this.cstype = (Integer) getValue(map, "cstype","int");
        this.sendarea = (String) getValue(map, "sendarea","string");

        this.noaddtype = (String) getValue(map, "noaddtype","string");
        
        this.buckle = (Integer) getValue(map, "buckle","int");
        this.medstatus = (Integer) getValue(map, "medstatus","int");
        this.medupdate_time = (Integer) getValue(map, "medupdate_time","int");
        this.medcreate_time = (Integer) getValue(map, "medcreate_time","int");
        this.appkey = (String) getValue(map, "appkey","string");
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("devid", this.devid);
        obj.put("cpname", this.cpname);
        obj.put("linkname", this.linkname);
        obj.put("moblie", this.moblie);
        obj.put("qqnumber", this.qqnumber);
        obj.put("skype", this.skype);
        obj.put("cname", this.cname);
        obj.put("caddress", this.caddress);
        obj.put("ywcard", this.ywcard);
        obj.put("ywcardpic", this.ywcardpic);
        obj.put("status", this.status);
        obj.put("type", this.type);
        obj.put("money", this.money);
        obj.put("finish_money", this.finish_money);
        obj.put("idcard", this.idcard);
        obj.put("wxnumber", this.wxnumber);
        obj.put("locks", this.locks);
        obj.put("state", this.state);
        obj.put("update_time", this.update_time);
        obj.put("create_time", this.create_time);
        obj.put("id", this.id);
        obj.put("name", this.name);
        obj.put("medtype", this.medtype);
        obj.put("pgname", this.pgname);
        obj.put("downloadurl", this.downloadurl);
        obj.put("addtype", this.addtype);
        obj.put("moneytype", this.moneytype);
        obj.put("desc", this.desc);
        obj.put("ctype", this.ctype);
        obj.put("cstype", this.cstype);
        obj.put("sendarea", this.sendarea);
        obj.put("noaddtype", this.noaddtype);
        obj.put("buckle", this.buckle);
        obj.put("medstatus", this.medstatus);
        obj.put("appkey", this.appkey);
        obj.put("medupdate_time", this.medupdate_time);
        obj.put("medcreate_time", this.medcreate_time);
        return obj.toString();
    }
	
	public int getDevid() {
        return devid;
    }


    public void setDevid(int devid) {
        this.devid = devid;
    }





    public String getCpname() {
        return cpname;
    }





    public void setCpname(String cpname) {
        this.cpname = cpname;
    }





    public String getLinkname() {
        return linkname;
    }





    public void setLinkname(String linkname) {
        this.linkname = linkname;
    }





    public String getMoblie() {
        return moblie;
    }





    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }





    public String getQqnumber() {
        return qqnumber;
    }





    public void setQqnumber(String qqnumber) {
        this.qqnumber = qqnumber;
    }





    public String getSkype() {
        return skype;
    }





    public void setSkype(String skype) {
        this.skype = skype;
    }





    public String getCname() {
        return cname;
    }





    public void setCname(String cname) {
        this.cname = cname;
    }





    public String getCaddress() {
        return caddress;
    }





    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }





    public String getYwcard() {
        return ywcard;
    }





    public void setYwcard(String ywcard) {
        this.ywcard = ywcard;
    }





    public String getYwcardpic() {
        return ywcardpic;
    }





    public void setYwcardpic(String ywcardpic) {
        this.ywcardpic = ywcardpic;
    }





    public int getStatus() {
        return status;
    }





    public void setStatus(int status) {
        this.status = status;
    }





    public int getType() {
        return type;
    }





    public void setType(int type) {
        this.type = type;
    }





    public float getMoney() {
        return money;
    }





    public void setMoney(float money) {
        this.money = money;
    }





    public float getFinish_money() {
        return finish_money;
    }





    public void setFinish_money(float finish_money) {
        this.finish_money = finish_money;
    }





    public String getIdcard() {
        return idcard;
    }





    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }





    public String getWxnumber() {
        return wxnumber;
    }





    public void setWxnumber(String wxnumber) {
        this.wxnumber = wxnumber;
    }





    public int getLocks() {
        return locks;
    }





    public void setLocks(int locks) {
        this.locks = locks;
    }





    public int getState() {
        return state;
    }





    public void setState(int state) {
        this.state = state;
    }





    public int getUpdate_time() {
        return update_time;
    }





    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }





    public int getCreate_time() {
        return create_time;
    }





    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }





    public int getId() {
        return id;
    }





    public void setId(int id) {
        this.id = id;
    }





    public String getName() {
        return name;
    }





    public void setName(String name) {
        this.name = name;
    }





    public int getMedtype() {
        return medtype;
    }





    public void setMedtype(int medtype) {
        this.medtype = medtype;
    }





    public String getPgname() {
        return pgname;
    }





    public void setPgname(String pgname) {
        this.pgname = pgname;
    }





    public String getDownloadurl() {
        return downloadurl;
    }





    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }





    public int getAddtype() {
        return addtype;
    }





    public void setAddtype(int addtype) {
        this.addtype = addtype;
    }





    public int getMoneytype() {
        return moneytype;
    }





    public void setMoneytype(int moneytype) {
        this.moneytype = moneytype;
    }





    public String getDesc() {
        return desc;
    }





    public void setDesc(String desc) {
        this.desc = desc;
    }





    public int getCtype() {
        return ctype;
    }





    public void setCtype(int ctype) {
        this.ctype = ctype;
    }





    public int getCstype() {
        return cstype;
    }





    public void setCstype(int cstype) {
        this.cstype = cstype;
    }





    public String getSendarea() {
        return sendarea;
    }





    public void setSendarea(String sendarea) {
        this.sendarea = sendarea;
    }





    public String getNoaddtype() {
        return noaddtype;
    }





    public void setNoaddtype(String noaddtype) {
        this.noaddtype = noaddtype;
    }





    public int getBuckle() {
        return buckle;
    }





    public void setBuckle(int buckle) {
        this.buckle = buckle;
    }





    public int getMedstatus() {
        return medstatus;
    }





    public void setMedstatus(int medstatus) {
        this.medstatus = medstatus;
    }





    public String getAppkey() {
        return appkey;
    }





    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }





    public int getMedupdate_time() {
        return medupdate_time;
    }





    public void setMedupdate_time(int medupdate_time) {
        this.medupdate_time = medupdate_time;
    }





    public int getMedcreate_time() {
        return medcreate_time;
    }





    public void setMedcreate_time(int medcreate_time) {
        this.medcreate_time = medcreate_time;
    }





   

	
	
	
}
