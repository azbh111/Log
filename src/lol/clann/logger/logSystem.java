/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.SQLException;
import lol.clann.Clann;
import lol.clann.Log;
import lol.clann.api.AutoRegister;
import lol.clann.api.LogPack;
import lol.clann.api.Logger;
import lol.clann.manager.ThreadManager;
import lol.clann.utils.API;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author zyp
 */
@AutoRegister.Register(plugin = Log.plgName, type = "logger")
public class logSystem extends Logger {

    public static logSystem register() throws SQLException {
        return new logSystem();
    }
    private int interval = 10;//记录间隔时间

    public logSystem() throws SQLException {
        super();
        run();//开始线程
    }

    private void run() {
        BukkitTask bt = Log.plugin.getServer().getScheduler().runTaskLaterAsynchronously(Log.plugin, new Runnable() {
            @Override
            public void run() {
                while (Log.run) {
                    try {
                        float tps = Clann.plugin.data.getTps(interval);
                        short totalMemory = (short) (Runtime.getRuntime().totalMemory() / 1024 / 1024);
                        short freeMemory = (short) (Runtime.getRuntime().freeMemory() / 1024 / 1024);
                        short playernum = (short) Bukkit.getServer().getOnlinePlayers().length;
                        short loadedchunk = 0;
                        for (World w : Bukkit.getServer().getWorlds()) {
                            loadedchunk += w.getLoadedChunks().length;
                        }
                        logSystem(totalMemory, freeMemory, Clann.plugin.data.getThreadCount(), playernum, loadedchunk, Clann.plugin.data.getLivingEntityCount(), tps);
                    } catch (Exception e) {
                        API.log(e, "logSystem异常");
                    }
                    ThreadManager.sleep(interval * 1000);
                }
            }
        }, 100);
        ThreadManager.addTask(Log.plugin, bt);
    }

    private void logSystem(short t4, short t5, int t6, short t7, short t8, int t9, float t10) throws SQLException {
        Log.plugin.addQueue(new pack(t4, t5, t6, t7, t8, t9, t10));
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index sIdx_time on " + name + " (time)");//日期聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index sIdx_player on " + name + " (player)");//玩家数非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index sIdx_chunk on " + name + " (chunk)");//区块数非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index sIdx_entity on " + name + " (entity)");//实体数非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index sIdx_tps on " + name + " (tps)");//tps非聚集索引
    }

    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,totalMemory smallint,freeMemory smallint,thread int,player smallint,chunk smallint,entity int,tps real";
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as \n"
                + "SELECT \n"
                + "	   dbo.dateTrans(time) as 时间\n"
                + "      ,tick as 时钟\n"
                + "      ,totalMemory as 分配内存\n"
                + "      ,freeMemory as 空闲内存\n"
                + "      ,thread as 线程数\n"
                + "      ,player as 玩家数\n"
                + "      ,chunk as 区块数\n"
                + "      ,entity as 生物数\n"
                + "      ,tps\n"
                + "  FROM " + name + " ");//创建视图
    }

    class pack extends LogPack {

        short p3;
        short p4;
        int p5;
        short p6;
        short p7;
        int p8;
        float p9;

        pack(short t3, short t4, int t5, short t6, short t7, int t8, float t9) {
            p3 = t3;
            p4 = t4;
            p5 = t5;
            p6 = t6;
            p7 = t7;
            p8 = t8;
            p9 = t9;
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setShort(3, p3);
            ps.setShort(4, p4);
            ps.setInt(5, p5);
            ps.setShort(6, p6);
            ps.setShort(7, p7);
            ps.setInt(8, p8);
            ps.setFloat(9, p9);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).append(separator).append(p5).append(separator).append(p6).append(separator).append(p7).append(separator).append(p8).append(separator).append(p9).toString();
        }
    }
}
