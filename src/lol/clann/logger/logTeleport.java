/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.api.AutoRegister;
import lol.clann.api.LogPlayerPack;
import lol.clann.api.LoggerListener;
import lol.clann.data.dataPlayerKey;
import lol.clann.data.dataWorldKey;
import lol.clann.utils.API;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author zyp
 */
@AutoRegister.Register(plugin = Log.plgName, type = "logger")
public class logTeleport extends LoggerListener {

    public static logTeleport register() throws SQLException {
        return new logTeleport();
    }

    public logTeleport() throws SQLException {
        super();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerTeleportEvent e) {
        try {
            logTeleport(e.getPlayer(), e.getFrom(), e.getTo());
        } catch (SQLException ex) {
            API.log(ex, "TeleportLogger异常");
        }
    }

    /**
     * 记录玩家传送
     */
    private void logTeleport(Player p, Location f, Location t) throws SQLException {
        Log.plugin.addQueue(new pack(p.getName(), f.getWorld().getName(), f.getX(), f.getY(), f.getZ(), t.getWorld().getName(), t.getX(), t.getY(), t.getZ()));
    }

    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,player int,fromWorld int,fromX int,fromY int,fromZ int,toWorld int,toX int,toY int,toZ int";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index tIdx_time on " + name + " (time)");//日期聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index tIdx_player on " + name + " (player)");//玩家非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index tIdx_fromWorld on " + name + " (fromWorld)");//fomWorld非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index tIdx_toWorld on " + name + " (toWorld)");//toWorld非聚集索引
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as \n"
                + "SELECT \n"
                + "       dbo.dateTrans(time) as 时间\n"
                + "      ,tick as 时钟\n"
                + "      ,pk.player as 玩家\n"
                + "      ,wk.world as 从世界\n"
                + "      ,fromX as 从X\n"
                + "      ,fromY as 从Y\n"
                + "      ,fromZ as 从Z\n"
                + "      ,wk1.world as 到世界\n"
                + "      ,toX as 到X\n"
                + "      ,toY as 到Y\n"
                + "      ,toZ as 到Z\n"
                + "  FROM " + name + " as o\n"
                + "left join " + dataPlayerKey.class.getSimpleName() + " as pk on pk.[index] = o.player \n"
                + "left join " + dataWorldKey.class.getSimpleName() + " as wk on wk.[index] = o.fromWorld \n"
                + "left join " + dataWorldKey.class.getSimpleName() + " as wk1 on wk1.[index] = o.toWorld \n"
                + "");//创建视图
    }

    class pack extends LogPlayerPack {

        String p4;
        double p5;
        double p6;
        double p7;
        String p8;
        double p9;
        double p10;
        double p11;

        pack(String t3, String t4, double t5, double t6, double t7, String t8, double t9, double t10, double t11) {
            p3 = t3;
            p4 = t4;
            p5 = t5;
            p6 = t6;
            p7 = t7;
            p8 = t8;
            p9 = t9;
            p10 = t10;
            p11 = t11;
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setInt(4, Log.plugin.worldKey.getWorldKeyByName(p4));
            ps.setDouble(5, p5);
            ps.setDouble(6, p6);
            ps.setDouble(7, p7);
            ps.setInt(8, Log.plugin.worldKey.getWorldKeyByName(p8));
            ps.setDouble(9, p9);
            ps.setDouble(10, p10);
            ps.setDouble(11, p11);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).append(separator).append(p5).append(separator).append(p6).append(separator).append(p7).append(separator).append(p8).append(separator).append(p9).append(separator).append(p10).append(separator).append(p11).toString();
        }
    }
}
