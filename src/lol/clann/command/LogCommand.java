/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.command;

import java.sql.*;
import java.text.DateFormat;
import java.util.*;
import lol.clann.*;
import lol.clann.Utils.TimeUtils;
import lol.clann.api.AutoRegister;
import lol.clann.api.ItemApi;
import lol.clann.api.MaterialApi;
import lol.clann.api.Operation;
import lol.clann.api.PlayerApi;
import lol.clann.api.ReflectApi;
import lol.clann.api.iPack;
import lol.clann.logger.logBlock;
import lol.clann.object.command.*;
import lol.clann.object.nbt.NBTContainerBlock;
import lol.clann.object.nbt.NBTTagCompound;
import lol.clann.utils.API;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;

/**
 *
 * @author zyp
 */
@AutoRegister.Register(plugin = Log.plgName, type = "command")
public class LogCommand extends CustomCommand {

    PreparedStatement ps_query;
    PreparedStatement ps_rollback;
    inspect isp = new inspect();

    public LogCommand(String cmd) throws SQLException {
        super(Log.plugin, cmd);

        ps_query = Log.plugin.sql.getPreparedStatement(""
                + "SELECT TOP 20 time,player,action,blockId,blockData FROM " + logBlock.class.getSimpleName() + " "
                + "where action>" + Operation.getDivision() + " and world=? and X=? and Y=? and Z=? and [rollback]=0 order by time asc");
        ps_rollback = Log.plugin.sql.getPreparedStatement(""
                + "select time,X,Y,Z,blockId,blockData,nbt,action,[rollback] from " + logBlock.class.getSimpleName() + " \n"
                + "where time>? and world=? and player=? and X>=? and Y>=? and Z>=? and X<=? and Y<=? and Z<=? and action>" + Operation.getDivision() + " and [rollback]=0 order by time desc", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);//设置结果集可更新
        Bukkit.getPluginManager().registerEvents(isp, Log.plugin);
    }

    @SubCommandAnnotation(mustPlayer = true, args = "(on/off)", des = "打开/关闭查询模式")
    public void inspect(CommandSender sender, String[] args) {
        if ("on".equals(args[0])) {
            isp.add(sender.getName());
            sender.sendMessage("开启查询模式");
        } else {
            isp.remove(sender.getName());
            sender.sendMessage("关闭查询模式");
        }
    }

    @SubCommandAnnotation(mustPlayer = true, args = "(player)  (x1) (y1) (z1) (x2) (y2) (z2) (second)", des = "回档当前世界指定时间内(秒)指定玩家指定范围内对方块的更改(参数为半径或者两个坐标)")
    public void rollback1(CommandSender sender, String[] args) {
        int x1 = Integer.parseInt(args[1]);
        int y1 = Integer.parseInt(args[2]);
        int z1 = Integer.parseInt(args[3]);
        int x2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);
        int z2 = Integer.parseInt(args[6]);
        if (x1 > x2 || y1 > y2 || z1 > z2) {
            sender.sendMessage("坐标1对应值必须小于等于坐标2");
            return;
        }
        Log.plugin.addQueue(new rollbackPack((Player) sender, ((Player) sender).getWorld(), x1, y1, z1, x2, y2, z2, args[0], Integer.parseInt(args[7])));
    }

    @SubCommandAnnotation(mustPlayer = true, args = "(player)  (xRadius) (yRadius) (zRadius) (second)", des = "回档当前世界指定时间内(秒)指定玩家指定范围内对方块的更改(参数为半径或者两个坐标)")
    public void rollback2(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int x1 = p.getLocation().getBlockX() - Integer.parseInt(args[1]);
        int x2 = p.getLocation().getBlockX() + Integer.parseInt(args[1]);
        int y1 = p.getLocation().getBlockY() - Integer.parseInt(args[2]);
        int y2 = p.getLocation().getBlockY() + Integer.parseInt(args[2]);
        int z1 = p.getLocation().getBlockZ() - Integer.parseInt(args[3]);
        int z2 = p.getLocation().getBlockZ() + Integer.parseInt(args[3]);
        Log.plugin.addQueue(new rollbackPack(p, p.getWorld(), x1, y1, z1, x2, y2, z2, args[0], Integer.parseInt(args[4])));
    }

    @SubCommandAnnotation(mustPlayer = true, args = "(player)  (radius) (second)", des = "回档当前世界指定时间内(秒)指定玩家指定范围内对方块的更改(参数为半径或者两个坐标)")
    public void rollback3(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        int x1 = p.getLocation().getBlockX() - Integer.parseInt(args[1]);
        int x2 = p.getLocation().getBlockX() + Integer.parseInt(args[1]);
        int y1 = p.getLocation().getBlockY() - Integer.parseInt(args[1]);
        int y2 = p.getLocation().getBlockY() + Integer.parseInt(args[1]);
        int z1 = p.getLocation().getBlockZ() - Integer.parseInt(args[1]);
        int z2 = p.getLocation().getBlockZ() + Integer.parseInt(args[1]);
        Log.plugin.addQueue(new rollbackPack(p, p.getWorld(), x1, y1, z1, x2, y2, z2, args[0], Integer.parseInt(args[2])));
    }

    /**
     * 用于查询指定位置方块信息
     */
    class inspect implements Listener {

        List<String> players = new ArrayList();

        private void add(String name) {
            players.add(name);
        }

        private void remove(String name) {
            players.remove(name);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onPlayerQuit(PlayerQuitEvent event) {
            remove(event.getPlayer().getName());
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void event(BlockPlaceEvent e) {
            if (players.contains(e.getPlayer().getName())) {
                e.setCancelled(true);
                query(e.getPlayer(), e.getPlayer().getWorld(), e.getBlockPlaced().getX(), e.getBlockPlaced().getY(), e.getBlockPlaced().getZ());
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void event(PlayerInteractEvent e) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && players.contains(e.getPlayer().getName()) && ItemApi.isEmpty(e.getPlayer().getItemInHand())) {
                e.setCancelled(true);
                query(e.getPlayer(), e.getPlayer().getWorld(), e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ());
            }
        }

        private void query(Player p, World w, int x, int y, int z) {
            Log.plugin.addQueue(new queryPack(p, w, x, y, z));//只查看前20行
        }
    }

    DateFormat formant = TimeUtils.getDateFormat("yyyy-MM-dd HH:mm:ss_SSS");

    class queryPack extends iPack {

        Player p;
        World p1;
        int p2;
        int p3;
        int p4;

        queryPack(Player p, World t1, int t2, int t3, int t4) {
            this.p = p;
            p1 = t1;
            p2 = t2;
            p3 = t3;
            p4 = t4;
        }

        @Override
        public void excute() throws SQLException {
            long t = System.currentTimeMillis();
            ps_query.setInt(1, Log.plugin.worldKey.getWorldKeyByName(p1.getName()));
            ps_query.setInt(2, p2);
            ps_query.setInt(3, p3);
            ps_query.setInt(4, p4);
            ResultSet rs = ps_query.executeQuery();
            t = System.currentTimeMillis() - t;
            if (p.isOnline()) {
                while (rs.next()) {

                    p.sendMessage(formant.format(new java.util.Date(rs.getLong(1))) + " " + Log.plugin.playerKey.getPlayerNameByKey(rs.getInt(2)) + " "
                            + (rs.getByte(3) == Operation.BREAK_BLOCK.getValue() ? "破坏" : "放置") + " " + MaterialApi.byId[rs.getInt(4)].name() + ":" + rs.getShort(5)
                    );
                }
                p.sendMessage("-----------------查询耗时" + t + "ms---------------------");
            }
            rs.close();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p.getPlayer().getName()).append(separator).append(p1.getName()).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).toString();
        }

    }

    class rollbackPack extends iPack {

        Player p;
        World w;
        int x1;
        int y1;
        int z1;
        int x2;
        int y2;
        int z2;
        String target;
        int during;

        /**
         * 回档指定世界指定范围指定时间内指定玩家破坏/放置的方块
         *
         * @param p
         * @param w
         * @param x1
         * @param y1
         * @param z1
         * @param x2
         * @param y2
         * @param z2
         * @param t 秒
         */
        rollbackPack(Player p, World t1, int t2, int t3, int t4, int t5, int t6, int t7, String t8, int t9) {
            this.p = p;
            w = t1;
            x1 = t2;
            y1 = t3;
            z1 = t4;
            x2 = t5;
            y2 = t6;
            z2 = t7;
            target = t8;
            during = t9;
        }

        @Override
        public void excute() throws SQLException {
            long queryTime = System.currentTimeMillis();//查询计时
            long timestap = queryTime - during * 1000L;//从改该时间到现在的时间段
            ps_rollback.setLong(1, timestap);
            ps_rollback.setInt(2, Log.plugin.worldKey.getWorldKeyByName(w.getName()));
            ps_rollback.setInt(3, Log.plugin.playerKey.getPlayerKeyByName(target));
            ps_rollback.setInt(4, x1);
            ps_rollback.setInt(5, y1);
            ps_rollback.setInt(6, z1);
            ps_rollback.setInt(7, x2);
            ps_rollback.setInt(8, y2);
            ps_rollback.setInt(9, z2);
            ResultSet rs = ps_rollback.executeQuery();//查询结果是按时间降序
            long rollbacktime = System.currentTimeMillis();
            queryTime = rollbacktime - queryTime;//查询耗时
            int place = 0;
            int break0 = 0;
            while (rs.next()) {
                try {
                    //此处尝试使用原表的列序号
                    rs.updateBoolean(9, true);//更新数据库
                    rs.updateRow();
                    byte action = rs.getByte(8);
                    if (action == 11) {
                        place(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getByte(6), rs.getBytes(7));
                        place++;
                    } else if (action == 12) {
                        break0(rs.getInt(2), rs.getInt(3), rs.getInt(4));
                        break0++;
                    } else {
                        throw new UnknownError();
                    }
                } catch (Throwable e) {
                    API.log(e, "回档" + w.getName() + "(" + rs.getInt(2) + "," + rs.getInt(3) + "," + rs.getInt(4) + ")" + "时发生异常time=" + rs.getLong(1) + ",action=" + rs.getByte(8));
                }
            }
            rs.close();
            rollbacktime = System.currentTimeMillis() - rollbacktime;
            if (p.isOnline()) {
                p.sendMessage("回档完成，共放置" + place + "个、破坏" + break0 + "个方块，查询耗时" + queryTime + "ms,回档耗时" + rollbacktime + "ms");
            }
        }

        private void place(int x, int y, int z, int id, byte data, byte[] nbt) {
            Block block = w.getBlockAt(x, y, z);
            block.setTypeId(id);
            block.setData(data);
            if (nbt != null && nbt.length > 0) {
                NBTContainerBlock ncb = new NBTContainerBlock(block);
                ncb.writeTag(NBTTagCompound.fromBytesGzip(nbt));
            }
        }

        private void break0(int x, int y, int z) {
            Block block = w.getBlockAt(x, y, z);
            Object tile = ReflectApi.CraftWorld_getTileEntityAt.of(w).call(x, y, z);
            if (tile != null && ReflectApi.IInventory.isInstance(tile)) {
                ReflectApi.clearIInventory(tile);//破坏方块前先清除方块内的物品
            }
            block.setType(Material.AIR);
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p.getName()).append(separator).append(w.getName()).append(separator).append(x1).append(separator).append(y1).append(separator).append(z1).append(separator).append(x2).append(separator).append(y2).append(separator).append(z2).append(separator).append(target).append(separator).append(during).toString();
        }

    }
}
