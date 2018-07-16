package com.powerwin.entity;

import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import java.util.Map;


public class Ads extends Common implements Serializable {

	private static final long serialVersionUID = -8438261930942665353L;

	public Ads() {
		super();
	}
	private int planid;
    private int cpid;
    private String name;
    private int type;
    private String url;
    private int ctype;
    private int cstype;
    private int status;
    private int id;
    private String  unitname;
    private float  day_money;
    private float one_money;
    private int start_time;
    private int end_time;
    private int bj_start_time;
    private int bj_end_time;
    private String senddate;
    private int bjhourval;
    private String  sendtype;
    private String sendarea;
    private String senddevmedia ;
    private  String shopname;
    private String  shopdesc;
    private String  onedesc;
    private String  buttondesc;
    private String  showpic;
    private String xxpic;
    public int getCpstatus() {
        return cpstatus;
    }


    public void setCpstatus(int cpstatus) {
        this.cpstatus = cpstatus;
    }

    private int cpstatus;
    public int getUnitstatus() {
        return unitstatus;
    }

    public void setUnitstatus(int unitstatus) {
        this.unitstatus = unitstatus;
    }

    private int unitstatus;
    private String statusres;
    
    public Ads(Map<String, Object> vals) {   
        this.id = (Integer) getValue(vals, "id" , "int");
        this.cpid = (Integer) getValue(vals, "cpid", "int");
        this.planid = (Integer) getValue(vals, "planid", "int");
        this.name = (String) getValue(vals, "name","string");
        this.type = (Integer) getValue(vals, "type", "string");
        this.url = (String) getValue(vals , "url" , "string");    
        this.ctype = (Integer) getValue(vals, "ctype" , "int");
        this.cstype = (Integer) getValue(vals, "cstype" , "int");
        this.status = (Integer) getValue(vals, "status" , "int");      
        this.unitname = (String) getValue(vals,"String" , "string");
        this.day_money = (Float) getValue(vals, "state" , "float");
        this.one_money = (Float) getValue(vals, "one_money","float");
        this.start_time = (Integer) getValue(vals, "start_time" , "int");
        this.end_time = (Integer) getValue(vals, "end_time" , "int");
        this.bj_start_time = (Integer) getValue(vals, "bj_start_time" , "int");
        this.bj_end_time = (Integer) getValue(vals, "bj_end_time" , "int");
        this.senddate = (String) getValue(vals, "senddate","string"); 
        this.bjhourval = (Integer) getValue(vals,  "bjhourval", "int");      //installnum
        this.sendtype = (String) getValue(vals,"sendtype","string");
        this.sendarea = (String) getValue(vals,"sendarea","string");
        this.senddevmedia = (String) getValue(vals,"senddevmedia","string");
        this.shopname = (String) getValue(vals,"shopname","string");
        this.shopdesc = (String) getValue(vals,"shopdesc","string");
        this.onedesc = (String) getValue(vals,"onedesc","string");
        this.showpic = (String) getValue(vals,"showpic","string");
        this.xxpic = (String) getValue(vals,"xxpic","string");
        this.unitstatus = (Integer) getValue(vals,"unitstatus","int");     
        this.buttondesc = (String) getValue(vals,"buttondesc","string");
        this.statusres = (String) getValue(vals,"statusres","string");
        this.cpstatus = (Integer) getValue(vals,"cpstatus","int");     

    }
    
    
    

    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("cpid",this.cpid);
        obj.put("planid",this.planid);
        obj.put("name",this.name);
        obj.put("type",this.type);
        obj.put("url",this.url);
        obj.put("ctype",this.ctype);
        obj.put("cstype",this.cstype);
        obj.put("status",this.status);
        obj.put("unitname",this.unitname);
        obj.put("day_money",this.day_money);
        obj.put("one_money",this.one_money);
        obj.put("start_time",this.start_time);
        obj.put("end_time",this.end_time);
        obj.put("bj_start_time",bj_start_time);
        obj.put("bj_end_time",this.bj_end_time);
        obj.put("senddate",this.senddate);
        obj.put("bjhourval",this.bjhourval);
        obj.put("sendtype",this.sendtype);
        obj.put("sendarea",this.sendarea);
        obj.put("senddevmedia",this.senddevmedia);
        obj.put("shopname",this.shopname);
        obj.put("shopdesc",this.shopdesc);
        obj.put("onedesc",this.onedesc);
        obj.put("showpic",this.showpic);
        obj.put("xxpic",this.xxpic);
        obj.put("unitstatus",this.unitstatus);
        obj.put("buttondesc",this.buttondesc);
        obj.put("statusres",this.statusres);      
        obj.put("cpstatus",this.cpstatus);
      
        return obj.toString();
    }
    
    
    

	public int getPlanid() {
        return planid;
    }

    public void setPlanid(int planid) {
        this.planid = planid;
    }

    public int getCpid() {
        return cpid;
    }

    public void setCpid(int cpid) {
        this.cpid = cpid;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }




    public void setType(int type) {
        this.type = type;
    }




    public String getUrl() {
        return url;
    }




    public void setUrl(String url) {
        this.url = url;
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




    public int getStatus() {
        return status;
    }




    public void setStatus(int status) {
        this.status = status;
    }




    public int getId() {
        return id;
    }




    public void setId(int id) {
        this.id = id;
    }




    public String getUnitname() {
        return unitname;
    }




    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }




    public float getDay_money() {
        return day_money;
    }




    public void setDay_money(float day_money) {
        this.day_money = day_money;
    }




    public float getOne_money() {
        return one_money;
    }




    public void setOne_money(float one_money) {
        this.one_money = one_money;
    }




    public int getStart_time() {
        return start_time;
    }




    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }




    public int getEnd_time() {
        return end_time;
    }




    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }




    public int getBj_start_time() {
        return bj_start_time;
    }




    public void setBj_start_time(int bj_start_time) {
        this.bj_start_time = bj_start_time;
    }




    public int getBj_end_time() {
        return bj_end_time;
    }




    public void setBj_end_time(int bj_end_time) {
        this.bj_end_time = bj_end_time;
    }




    public String getSenddate() {
        return senddate;
    }




    public void setSenddate(String senddate) {
        this.senddate = senddate;
    }




    public int getBjhourval() {
        return bjhourval;
    }




    public void setBjhourval(int bjhourval) {
        this.bjhourval = bjhourval;
    }




    public String getSendtype() {
        return sendtype;
    }




    public void setSendtype(String sendtype) {
        this.sendtype = sendtype;
    }




    public String getSendarea() {
        return sendarea;
    }




    public void setSendarea(String sendarea) {
        this.sendarea = sendarea;
    }




    public String getSenddevmedia() {
        return senddevmedia;
    }




    public void setSenddevmedia(String senddevmedia) {
        this.senddevmedia = senddevmedia;
    }




    public String getShopname() {
        return shopname;
    }




    public void setShopname(String shopname) {
        this.shopname = shopname;
    }




    public String getShopdesc() {
        return shopdesc;
    }




    public void setShopdesc(String shopdesc) {
        this.shopdesc = shopdesc;
    }




    public String getOnedesc() {
        return onedesc;
    }




    public void setOnedesc(String onedesc) {
        this.onedesc = onedesc;
    }




    public String getButtondesc() {
        return buttondesc;
    }




    public void setButtondesc(String buttondesc) {
        this.buttondesc = buttondesc;
    }




    public String getShowpic() {
        return showpic;
    }




    public void setShowpic(String showpic) {
        this.showpic = showpic;
    }




    public String getXxpic() {
        return xxpic;
    }




    public void setXxpic(String xxpic) {
        this.xxpic = xxpic;
    }




   








    public String getStatusres() {
        return statusres;
    }




    public void setStatusres(String statusres) {
        this.statusres = statusres;
    }




}
