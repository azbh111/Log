/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.api.LogPlayerPack;
import lol.clann.api.LoggerListener;
import lol.clann.api.Operation;
import lol.clann.data.dataOperationKey;
import lol.clann.data.dataPlayerKey;
import lol.clann.utils.API;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author zyp
 */
@lol.clann.pluginbase.api.AutoRegister
public class logPlayer extends LoggerListener {

    public static logPlayer register() throws SQLException {
        return new logPlayer();
    }

    public logPlayer() throws SQLException {
        super();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void evemt(PlayerKickEvent e) {
        try {
            System.out.println("PlayerKickEvent:" + e.isCancelled());
            logPlayer(e.getPlayer(), Operation.KICK.getValue(), null, e.getReason() + "|" + e.getLeaveMessage());
        } catch (SQLException ex) {
            API.log(ex, "PlayerKickEvent异常");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            logPlayer(event.getPlayer(), Operation.JOIN.getValue(), event.getPlayer().getAddress().getAddress().getHostAddress(), null);
        } catch (SQLException ex) {
            API.log(ex, "PlayerJoinEvent异常");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            logPlayer(event.getPlayer(), Operation.QUIT.getValue(), null, event.getQuitMessage());
        } catch (SQLException ex) {
            API.log(ex, "PlayerQuitEvent异常");
        }
    }

    private void logPlayer(org.bukkit.entity.Player p, byte act, String ip, String message) throws SQLException {
        Log.plugin.addQueue(new pack(p.getName(), act, ip != null ? ip : null, message != null && !message.isEmpty() ? message : null));
    }

    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,player int,action tinyint,playerIp varchar(15),message text";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index pIdx_time on " + name + " (time)");//日期聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index pIdx_player on " + name + " (player)");//玩家非聚集索引
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as \n"
                + "SELECT \n"
                + "       dbo.dateTrans(time) as 时间\n"
                + "      ,tick as 时钟\n"
                + "      ,pk.player as 玩家\n"
                + "      ,ok.name as 操作\n"
                + "      ,playerIp as ip\n"
                + "      ,message as 消息\n"
                + "  FROM " + name + " as o\n"
                + "left join " + dataPlayerKey.class.getSimpleName() + " as pk on pk.[index] = o.player \n"
                + "left join " + dataOperationKey.class.getSimpleName() + " as ok on ok.id = o.action \n"
                + "");//创建视图
    }

    class pack extends LogPlayerPack {

        byte p4;
        String p5;
        String p6;

        pack(String t3, byte t4, String t5, String t6) {
            p3 = t3;
            p4 = t4;
            p5 = t5;
            p6 = t6;
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setByte(4, p4);
            ps.setString(5, p5);
            ps.setString(6, p6);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).append(separator).append(p5).append(separator).append(p6).toString();
        }
    }
}
