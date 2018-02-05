/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import lol.clann.api.*;
import lol.clann.data.*;
import lol.clann.pluginbase.BasePlugin;
import lol.clann.utils.*;
import org.bukkit.plugin.java.*;
import org.bukkit.scheduler.*;

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
    }

    private void initSql() {
        sql = new SqlServer(this, dir, "LogAll", address, id, password);
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

    public void addQueue(iPack p) {
        queue.add(p);
    }

}
