package com.powerwin.entity;


public class AdsHourKeys implements CountKeys {
	public int created;
	
	public int year;
	public int mon;
	public int day;
	public int hour;
	
	public int type;
	public int data_from;
	public int ad_from;

	public int adid;
	public int cid;
	public int game_id;
	public int adplanid;
	
	
	public static String SQL_FIELDS[] = {
		"created","year","mon","day","hour",
		"type","data_from","ad_from",
		"adid","cid","game_id","adplanid"
	};
	
	public String getTable() {
//		DataSource ds = SQLConnection.getDataSource(Define.DATA_SOURCES[type]);
//		String prefix = ds == null ? "" : ds.getPrefix();
//		return String.format("%s_ad_hour_%d", prefix, created/100);
		return "";
	}
	
	public String[] getFileds() {
		return SQL_FIELDS;
	}
	
	public Object[] getValues() {
		return new Object[] {
			created,year,mon,day,hour,
			type,data_from,ad_from,
			adid,cid,game_id,adplanid
		};
	}
	
	protected AdsHourKeys() {
		this.created = 0;
		
		this.year = 0;
		this.mon = 0;
		this.day = 0;
		this.hour = 0;
		
		this.type = 0;
		this.data_from = 0;
		this.ad_from = 0;

		this.adid = 0;
		this.cid = 0;
		this.game_id = 0;
		this.adplanid = 0;
	}
	
	public static AdsHourKeys create(DetailHourKeys from) {
		
		AdsHourKeys item = new AdsHourKeys();

		item.year = from.year;
		item.mon = from.mon;
		item.day = from.day;
		item.hour = from.hour;
		
		item.type = from.type;
		item.ad_from = from.ad_from;
		item.data_from = from.data_from;

		item.adid = from.adid;
		item.cid = from.cid;
		item.game_id = from.game_id;
		item.created = item.year*10000 + item.mon *100 + item.day;
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
		AdsHourKeys that = (AdsHourKeys)obj;

		if(this.created != that.created) return false;
		if(this.hour != that.hour) return false;
		if(this.type != that.type) return false;
		if(this.data_from != that.data_from) return false;
		if(this.ad_from != that.ad_from) return false;
		if(this.adid != that.adid) return false;
		if(this.cid != that.cid) return false;
		if(this.game_id != that.game_id) return false;
		if(this.adplanid != that.adplanid) return false;
		
		return true;
	}
}
