package com.powerwin.boot.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;


public class Configuration extends Properties {

	private static final long serialVersionUID = 5044046358027224342L;
	protected static Logger LOG = LogManager.getLogger(Configuration.class);
	
	private static Configuration instance = null;
	public static synchronized Configuration getInstance(){ 
		if(instance == null){ 
			instance = new Configuration(); 
		} 
		return instance; 
	}
	
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return (val == null || val=="") ? defaultValue : val;
	}

	public String getString(String name, String defaultValue) {
		return this.getProperty(name, defaultValue);
	}

	public int getInt(String name, int defaultValue) {
		String val = this.getProperty(name);
		return (val == null || val=="") ? defaultValue : Integer.parseInt(val);
	}

	public long getLong(String name, long defaultValue) {
		String val = this.getProperty(name);
		return (val == null || val=="") ? defaultValue : Integer.parseInt(val);
	}

	public float getFloat(String name, float defaultValue) {
		String val = this.getProperty(name);
		return (val == null || val=="") ? defaultValue : Float.parseFloat(val);
	}

	public double getDouble(String name, double defaultValue) {
		String val = this.getProperty(name);
		return (val == null || val=="") ? defaultValue : Double.parseDouble(val);
	}

	public byte getByte(String name, byte defaultValue) {
		String val = this.getProperty(name);
		return (val == null ||val=="") ? defaultValue : Byte.parseByte(val);
	}

	public Configuration() {
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties");
		try {
			this.load(in);
			in.close();
		} catch (InvalidPropertiesFormatException e) {
			LOG.error(e);
		} catch (IOException e) {
			LOG.error(e);
		} catch (Exception e){
			LOG.error(e);
		}
	}
}
