package cn.dcs.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Hashtable;

public class GroupServerHashtable extends Hashtable<String, String> {

    private static final long serialVersionUID = -2012019288272903356L;

    private static GroupServerHashtable property;

    private String file;

    private GroupServerHashtable() {

    }

    static {
        if (property == null) {
            try {
                property = new GroupServerHashtable();
                property.file = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("")
                        .getPath()
                        + "GroupServer.properties", "UTF-8");
                File f = new File(property.file);
                if (!f.exists()) {
                    f.createNewFile();
                }
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String data;
                String[] cache;
                while ((data = reader.readLine()) != null) {
                    if (!data.equals("") && data.indexOf("=") != -1) {
                        cache = data.split("=");
                        property.put(cache[0], cache[1]);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static GroupServerHashtable instence() {
        return property;
    }

    public String read(String key) {
        return property.get(key);
    }

    public void write(String key, String val) {
        try {
            property.put(key, val);
            property.store(new FileOutputStream(property.file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void store(OutputStream out) throws IOException {
        store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")));
    }

    private void store0(BufferedWriter bw) throws IOException {
        synchronized (this) {
            for (Enumeration<String> e = keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String val = (String) get(key);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
        bw.close();
    }
}
