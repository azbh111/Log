/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.manager.ThreadManager;
import lol.clann.utils.API;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author zyp
 */
public abstract class Logger {

    protected PreparedStatement ps;
    protected final String name;
    private static long dely = 1000;    //处理队列间隔时间ms
    
    public Logger() throws SQLException {
        initLogThread();//启动记录线程
        this.name = this.getClass().getSimpleName();
        initTable();
        initPreparedStatement();
    }

    /**
     * 初始化预编译指令
     *
     * @throws SQLException
     */
    protected void initPreparedStatement() throws SQLException {
        ps = Log.plugin.sql.getPreparedStatement(getInsertSql(name, Log.plugin.sql.getColumnCountOfTable(name)));
    }

    /**
     * 异步写入数据库
     */
    private void initLogThread() {
        BukkitTask bt =  Bukkit.getScheduler().runTaskAsynchronously(Log.plugin, new Runnable() {
            @Override
            public void run() {
                iPack p = null;
                while (Log.plugin.run) { //循环
                    if (!Log.plugin.queue.isEmpty()) {
                        try {
                            Log.plugin.sql.startTransaction();  //开始事物
                        } catch (Throwable ex) {
                            API.log(ex, "事物启动失败");
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
                            API.log(ex, "事物启动失败");
                        }
                    }
                    ThreadManager.sleep(dely);
                }
            }
        });
        Log.plugin.add(bt);
    }

    private void initTable() throws SQLException {
        if (!tableExist()) {
            Log.plugin.sql.startTransaction();
            createTable();//创建表
            createIndex();//创建索引
            createView();//创建视图
            Log.plugin.sql.commitTransaction();
            API.log("创建表:" + name);
        }
    }

    /**
     * 创建索引
     */
    protected abstract void createIndex() throws SQLException;

    protected void createView() throws SQLException {

    }

    protected boolean tableExist() throws SQLException {
        return Log.plugin.sql.tableExist(name);
    }

    protected static String getInsertSql(String name, int parmCount) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(name).append(" values(?");
        for (int i = 1; i < parmCount; i++) {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }

    protected abstract String getTableColumnDefinition();

    /**
     * 创建表
     *
     * @throws SQLException
     */
    private void createTable() throws SQLException {
        Log.plugin.sql.getStatement().execute("create table " + name + "(" + getTableColumnDefinition() + ")");
    }

}
