/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import java.io.*;
import java.text.*;
import java.util.concurrent.*;
import lol.clann.api.*;
import lol.clann.data.*;
import lol.clann.manager.ThreadManager;
import lol.clann.pluginbase.BasePlugin;
import lol.clann.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author zyp
 */
public class Log extends BasePlugin {

    /*
     * 待处理
     *
     */
    public static final String plgName = "Log";
    public static final String databaseName = "LogAll";
    public DateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static Log plugin;
    public SqlServer sql;
    public File dir = null;
    public final LinkedBlockingQueue<iPack> queue = new LinkedBlockingQueue();
    public dataPlayerKey playerKey = null;
    public dataMaterialKey materialKey = null;
    public dataWorldKey worldKey = null;
    public static boolean run = true;

    public Log() {
        plugin = this;
        dir = new File(Clann.parentServerDir, "LogAll");
    }

    public String address;
    public String id;
    public String password;

    @Override
    protected void reloadConfig0() {
        address = getConfig().getString("address");
        id = getConfig().getString("id");
        password = getConfig().getString("password");
    }

    @Override
    public void onDisable0() {
        run = false;
        sql.close();
    }

    @Override
    public void onEnable0() {
        initSql();
        initLogThread();//启动记录线程
    }

    private void initSql() {
        sql = new SqlServer(this, dir, "LogAll", address, id, password);
        try {
            sql.connection();//连接数据库
            log("已连接至数据库");
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

    public void addQueue(iPack p) {
        queue.add(p);
    }

    /**
     * 异步写入数据库
     */
    private void initLogThread() {
        BukkitTask bt = Bukkit.getScheduler().runTaskAsynchronously(Log.plugin, new Runnable() {
            @Override
            public void run() {
                iPack p = null;
                while (run) { //循环
                    if (!Log.plugin.queue.isEmpty()) {
                        try {
                            Log.plugin.sql.startTransaction();  //开始事物
                        } catch (Throwable ex) {
                            API.log(ex, "开启事务失败");
                            try {
                                Thread.sleep(10000);
                                continue;
                            } catch (InterruptedException ex1) {
                                ex1.printStackTrace();
                            }
                        }
                        while (!Log.plugin.queue.isEmpty()) { //事物
                            try {
                                p = Log.plugin.queue.take();
                                p.excute();
                            } catch (Throwable ex) {
                                API.log(ex, p.getClass().getName() + "异常:" + p);
                            }
                        }
                        try {
                            Log.plugin.sql.commitTransaction(); //提交事物
                        } catch (Throwable ex) {
                            API.log(ex, "提交食物失败");
                        }
                    }
                    ThreadManager.sleep(dely);
                }
            }
        });
        add(bt);
    }
    private static long dely = 1000;    //处理队列间隔时间ms
}
