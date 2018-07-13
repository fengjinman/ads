package com.powerwin.entity;


public class DetailDayKeys implements CountKeys {
	public int created;
	
	public int year;
	public int mon;
	public int day;
	
	public int type;
	public int data_from;
	public int ad_from;
	
	public int appid;
	public int uid;
	public int adid;
	public int cid;
	public int game_id;
	public int adplanid;
	
	public static String SQL_FIELDS[] = {
		"created","year","mon","day",
		"type","data_from","ad_from",
		"appid","uid","adid","cid","game_id","adplanid"
	};
	
	public String getTable() {
//		DataSource ds = SQLConnection.getDataSource(Define.DATA_SOURCES[type]);
//		String prefix = ds == null ? "" : ds.getPrefix();
//		return String.format("%s_day_%d", prefix, created/100/100);
		return "";
	}
	
	public String[] getFileds() {
		return SQL_FIELDS;
	}
	
	public Object[] getValues() {
		return new Object[] {
			created,year,mon,day,
			type,data_from,ad_from,
			appid,uid,adid,cid,game_id,adplanid
		};
	}
	
	protected DetailDayKeys() {
		this.created = 0;
		
		this.year = 0;
		this.mon = 0;
		this.day = 0;
		
		this.type = 0;
		this.data_from = 0;
		this.ad_from = 0;
		
		this.appid = 0;
		this.uid = 0;
		this.adid = 0;
		this.cid = 0;
		this.game_id = 0;
		this.adplanid = 0;
	}
	
	public static DetailDayKeys create(DetailHourKeys from) {
		
		DetailDayKeys item = new DetailDayKeys();

		item.year = from.year;
		item.mon = from.mon;
		item.day = from.day;

		item.type = from.type;
		item.data_from = from.data_from;
		item.ad_from = from.ad_from;

		item.appid = from.appid;
		item.uid = from.uid;
		item.adid = from.adid;
		item.cid = from.cid;
		
		item.created = item.year*10000 + item.mon *100 + item.day;
		item.game_id = from.game_id;
		item.adplanid = from.adplanid;
		
		return item;
	}
	
	private int hash = 0;
	@Override
	public int hashCode() {
		if(hash != 0) return hash;
		
		Object[] vals = this.getValues();
		int h = 0;
		int off = 0;
		int len = vals.length;

		for (int i=1; i<len;i++) {
			h = 31*h + (Integer)vals[off++];
	    }
		hash = h;
		return hash;
	}
	@Override
	public boolean equals(Object obj) {
		DetailDayKeys that = (DetailDayKeys)obj;

		if(this.created != that.created) return false;
		if(this.type != that.type) return false;
		if(this.data_from != that.data_from) return false;
		if(this.ad_from != that.ad_from) return false;
		if(this.appid != that.appid) return false;
		if(this.uid != that.uid) return false;
		if(this.adid != that.adid) return false;
		if(this.cid != that.cid) return false;
		if(this.game_id != that.game_id) return false;
		if(this.adplanid != that.adplanid) return false;
		
		return true;
	}
}
