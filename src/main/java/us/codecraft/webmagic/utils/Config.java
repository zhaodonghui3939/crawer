package us.codecraft.webmagic.utils;

/**
 * Config
 *
 * @author 东黎
 * @date 15/11/30
 */


import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private Properties prop = null; //the properties we are using

    private static Config util = null;

    private static Logger log = Logger.getLogger(Config.class);

    public static Config getInstance() {
        if (util == null) {
            util = new Config();
            util.init();
        }
        return util;
    }

    public static void main(String args[]) {
        Config a = Config.getInstance();
        //System.out.println(EncryptionUtil.hashAlgorithm("cityindex", "md5"));
        log.debug(a);
       // System.out.println(a.getStringValue("dt.yicai.username", "yi"));
    }

    public void init() {

        // for get the ext_config_path,and get the internal config file
        this.loadInternalConfig();
    }

    private void loadInternalConfig() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("config.properties");
        prop = new Properties();
        try {
            prop.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
        }
    }

    public String getStringValue(String key, String def) {
        String value = this.prop.getProperty(key);
        if (value == null || value.trim().equals("")) {
            return def;
        }

        return value.trim();
    }

}

