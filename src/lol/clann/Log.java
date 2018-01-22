/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import lol.clann.api.*;
import lol.clann.data.*;
import lol.clann.utils.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;

/**
 *
 * @author zyp
 */
public class Log extends JavaPlugin {

    /* 待处理

     */
    public static final String plgName = "Log";
    public static final String databaseName = "LogAll";
    public DateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public List<BukkitTask> tasks = new ArrayList<>();
    public static Log plugin;
    public SqlServer sql;
    public File dir = null;
    public final LinkedBlockingQueue<iPack> queue = new LinkedBlockingQueue();
    public dataPlayerKey playerKey = null;
    public dataMaterialKey materialKey = null;
    public dataWorldKey worldKey = null;
    public static boolean run = true;

    @Override
    public void onDisable() {
        sql.close();
        run = false;
    }

    @Override
    public void onEnable() {
        plugin = this;
        dir = new File(Clann.parentServerDir, "LogAll");
        initConfig();
        initSql();
        registerEvents();
        registerCommand();
    }

    private void initSql() {
        sql = new SqlServer(this, dir, "LogAll", getConfig().getString("address"), getConfig().getString("id"), getConfig().getString("password"));
        try {
            sql.connection();//连接数据库
            //创建自定义函数
            if (!sql.hasResult("select * from sysobjects where name = 'dateTrans'")) {
                sql.getStatement().execute(""
                        + "create function dateTrans(@mt bigint) returns varchar(23)\n"
                        + "as begin\n"
                        + "return convert(char(19),DATEADD(second,@mt/1000,'1970-1-1 08:00:00.000'),120)+'.'+CONVERT(char(3),@mt%1000)\n"
                        + "end");
            }
        } catch (Exception ex) {
            API.shutdown(ex, "数据库连接失败,关闭服务器");
        }
    }

    /**
     * 注册监听器
     */
    private void registerEvents() {
        try {
            /*
            使用包扫描的方式自动实例化
             */
            AutoRegister.register(this, "logger");
        } catch (Throwable e) {
            API.shutdown(e, "监听器注册失败");
        }
    }

    private void registerCommand() {
        try {
            /*
            使用包扫描的方式自动实例化
             */
            AutoRegister.register(this, "command", "log");
        } catch (Throwable ex) {
            API.shutdown(ex, "指令加载失败");
        }
    }

    private void initConfig() {
        saveDefaultConfig();
    }

    public void addQueue(iPack p) {
        queue.add(p);
    }
}
