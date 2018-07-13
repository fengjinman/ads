package com.powerwin.util;



import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.entity.Ads;
import com.powerwin.entity.Media;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Random;

public class DataSave {
	
	public static Logger LOG = LogManager.getLogger(DataSave.class);
	public final static Random RANDOM = new Random();
	
	public static class DataSaveRole {
		
		private int save;
		private int rate;
		
		public DataSaveRole(int save, int rate) {
			this.save = save;
			this.rate = rate;
		}

		public int getSave() {
			return save;
		}

		public int getRate() {
			return rate;
		}
	}
	
	public static DataSaveRole getRole(Media media, Ads ad) {
//		if(media == null) return null;
//
//		Map<Integer, MediaFilter> rates = media.getRates();
//		MediaFilter mf = rates.get(ad.getDataType());
//
//		int save = mf.getSave();
//		int rate = mf.getRate();
//
//		if(rate == 0) rate = ad.getRate();
//		if(save == 0) save = 10;
//
//		//APP Media
//		if(media instanceof MediaApp) {
//			MediaApp app = (MediaApp)media;
//			if("2.0".equals(app.getSdkVersion())) {
//				save = 100;
//				rate = 0;
//			}
//		}
		int save =0;
		int rate = 0;
 
		return new DataSaveRole(save, rate);
	}

	public static boolean getSave(DataSaveRole role) {
		if(role == null) return false;

		int n = role.save;
		if(RANDOM.nextInt(100) < n) {
			return true;
		}
		return false;
	}
	
	public static float getRate(DataSaveRole role, float price) {
		if(role == null) return price;

		float n = role.rate;
		return price*n/100.0f;
	}
	
	public static void main(String[] args) {
		Media m = MediaCache.getInstance().get(7454);
		Ads a = AdsCache.getInstance().get(10446);
		DataSaveRole dsr = getRole(m,a);
		System.out.println(dsr);
	}
}
