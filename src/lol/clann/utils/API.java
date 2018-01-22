/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.utils;

import lol.clann.Log;
import org.bukkit.Bukkit;

/**
 *
 * @author zyp
 */
public class API {
    
    private static final String prefix = "[" + Log.plgName + "]";
    
    public static void log(String s) {
        System.out.println(prefix + s);
    }
    
    public static void log(Throwable e, String s) {
        e.printStackTrace();
        log(s);
    }
    
    public static void shutdown(String s) {
        log(s);
        Bukkit.shutdown();
    }
    
    public static void shutdown(Throwable e, String s) {
        log(e, s);
        Bukkit.shutdown();
    }
}
