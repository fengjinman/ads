package com.powerwin.entity;


public class SumHourKeys implements CountKeys {
	public int created;
	
	public int year;
	public int mon;
	public int day;
	public int hour;
	
	public int type;
	public int data_from;
	public int ad_from;
	public int game_id;
	
	public static String SQL_FIELDS[] = {
		"created","year","mon","day","hour",
		"type","data_from","ad_from","game_id"
	};
	
	public String getTable() {
//		DataSource ds = SQLConnection.getDataSource(Define.DATA_SOURCES[type]);
//		String prefix = ds == null ? "" : ds.getPrefix();
//		return String.format("%s_hour", prefix);
		return "";
	}
	
	public String[] getFileds() {
		return SQL_FIELDS;
	}
	
	public Object[] getValues() {
		return new Object[] {
			created,year,mon,day,hour,
			type,data_from,ad_from,game_id
		};
	}
	
	protected SumHourKeys() {
		this.created = 0;
		
		this.year = 0;
		this.mon = 0;
		this.day = 0;
		this.hour = 0;
		
		this.type = 0;
		this.data_from = 0;
		this.ad_from = 0;
		this.game_id = 0;
	}
	
	public static SumHourKeys create(DetailHourKeys from) {
		
		SumHourKeys item = new SumHourKeys();

		item.year = from.year;
		item.mon = from.mon;
		item.day = from.day;
		item.hour = from.hour;
		
		item.type = from.type;
		item.data_from = from.data_from;
		item.ad_from = from.ad_from;
		
		item.created = item.year*10000 + item.mon *100 + item.day;
		item.game_id = from.game_id;
		
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
		SumHourKeys that = (SumHourKeys)obj;

		if(this.created != that.created) return false;
		if(this.hour != that.hour) return false;
		if(this.type != that.type) return false;
		if(this.data_from != that.data_from) return false;
		if(this.ad_from != that.ad_from) return false;
		if(this.game_id != that.game_id) return false;
		
		return true;
	}
}
