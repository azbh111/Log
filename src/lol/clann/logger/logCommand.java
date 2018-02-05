/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.*;
import lol.clann.*;
import lol.clann.api.LogPlayerPack;
import lol.clann.api.LoggerListener;
import lol.clann.data.dataPlayerKey;
import lol.clann.pluginbase.api.AutoRegister;
import lol.clann.utils.API;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author zyp
 */
@AutoRegister
public class logCommand extends LoggerListener {

    public static logCommand register() throws SQLException {
        return new logCommand();
    }

    public logCommand() throws SQLException {
        super();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        try {
            logPlayerDeath(event.getPlayer(), event.getMessage());
        } catch (SQLException ex) {
            API.log(ex, "PlayerCommandPreprocessEvent异常");
        }
    }

    private void logPlayerDeath(Player p, String command) throws SQLException {
        Log.plugin.addQueue(new pack(p.getName(), command != null && !command.isEmpty() ? command : null));
    }

    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,player int,command text";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index cIdx_time on " + name + " (time)");//日期聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index cIdx_player on " + name + " (player)");//玩家非聚集索引
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as \n"
                + "SELECT \n"
                + "	dbo.dateTrans(time) as 时间,\n"
                + "	tick as 时钟,\n"
                + "	pk.player as 玩家,\n"
                + "	command as 消息\n"
                + "FROM " + name + " as c\n"
                + "left join " + dataPlayerKey.class.getSimpleName() + " as pk on pk.[index] = c.player\n"
                + "");//创建视图
    }

    class pack extends LogPlayerPack {

        String p4;

        pack(String t3, String t4) {
            p3 = t3;
            p4 = t4;
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setString(4, p4);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).toString();
        }
    }

}
