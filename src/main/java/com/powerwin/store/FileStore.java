package com.powerwin.store;

import com.powerwin.util.BufferUtil;
import com.powerwin.boot.config.Configuration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStore {

	public final static String STORE_STORE = "store";
	public final static String STORE_TEMP = "temp";

	public static Logger LOG = LogManager.getLogger(FileStore.class);
	
	public static String workdir = Configuration.getInstance().getProperty(
			"path.workdir", "/tmp/workdir");

	protected RandomAccessFile stream;
	protected String name;
	private FileChannel in;
	private FileChannel out;

	public FileStore(String name) {
		this.name = name;

		File file = new File(workdir + "/" + name);
		File dir = file.getParentFile();

		if (!dir.exists()){
			dir.mkdirs();
		}


		try {
			stream = new RandomAccessFile(file, "rw");
			 in = stream.getChannel();
			 out = stream.getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public long write(String text) {
		byte[] bytes = text.getBytes();
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return write(buf);
	}

	public long write(byte[] bytes) {
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return write(buf);
	}
	
	public long write(Object... objs) {
		ByteBuffer buf = ByteBuffer.allocateDirect(4096);
		for (int i = 0; i < objs.length; i++) {
			Object obj = objs[i];
			BufferUtil.put(buf, obj);
		}
		buf.flip();
		return write(buf);
	}

	public synchronized long write(ByteBuffer buf) {
		try {
			out.position(stream.length());
			long position = out.position();
			//channel.lock(position, buf.limit(), false);
			out.write(buf);
			
			return position;
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("write file " + this.name + " error" + e);
			return -1;
		}
	}

	public synchronized int read(ByteBuffer buf) {
		try {
			int size = in.read(buf);
			return size;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<Object> read() {

		try {
			long size = stream.length();
			ByteBuffer buf = ByteBuffer.allocateDirect((int) size);
			int len = in.read(buf);
			if(len == 0) {
				return null;
			}
			
			buf.flip();

			List<Object> objs = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				if (buf.position() >= buf.limit())
				{break;}

				try {
					Object obj = BufferUtil.get(buf);
					objs.add(obj);
				} catch(Exception e) {
					System.out.println("Read file error : " + name);
					break;
				}
			}
			return objs;
			
		} catch (IOException e) {
		}

		return null;
	}

	public void close() {
		try {
			stream.close();
			in.close();
			out.close();
		} catch (IOException e) {
		}
		STORES.remove(name);
	}

	public static Map<String, FileStore> STORES = new ConcurrentHashMap<String, FileStore>();

	public static FileStore getInstance(String name) {
		FileStore store = STORES.get(name);
		if (store == null) {
			store = new FileStore(name);
			STORES.put(name, store);
		}
		return store;
	}

	public static void closeAll() {
		for (FileStore store : STORES.values()) {
			store.close();
		}
	}
}
