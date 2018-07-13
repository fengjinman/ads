package com.powerwin.util;


import com.powerwin.boot.config.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SimpleFileWriter extends BufferedWriter {

    public static String WORKDIR = Configuration.getInstance().getProperty("path.workdir", "/tmp/workdir/store");

    protected String name;

    public SimpleFileWriter(File file, String name) throws IOException {
        super(new FileWriter(file, true));
        this.name = name;
    }

    public static Map<String,SimpleFileWriter> STORES = new ConcurrentHashMap<String,SimpleFileWriter>();

    public static SimpleFileWriter getInstance(String name) {
        SimpleFileWriter store = STORES.get(name);
        if(store == null) {

            File file = new File(WORKDIR + "/" + name);
            File dir = file.getParentFile();
            if(!dir.exists()){

                dir.mkdirs();
            }

            try {
                store = new SimpleFileWriter(file, name);
                STORES.put(name, store);
            } catch (IOException e) {
                store = null;
            }
        }
        return store;
    }

    @Override
    public synchronized void write(String text) throws IOException {
        super.write(text);
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (IOException e) {

        }
        STORES.remove(this.name);
    }

    public static void closeAll() {
        for(SimpleFileWriter store : STORES.values()) {
            store.close();
        }
    }
}
