package info.admirsabanovic.arenafight.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asabanovic on 5/15/15.
 */
public class Config {
    public static Map getConfig()
    {
        return config;
    }
    private static Map <String,String> config = new HashMap<>();

    public static void setConfig(String key, String value){
        config.put(key, value);
    }

    public static String getConfig(String key){
        setUpConfig();
        return config.get(key);
    }

    public static void setUpConfig(){
        setConfig("host", "http://192.168.0.135/");
    }

    public static Config INSTANCE = new Config();
    public static Config getInstance()
    {
        return INSTANCE;
    }
}
