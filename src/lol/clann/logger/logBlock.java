/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.api.BlockApi;
import lol.clann.api.ItemApi;
import lol.clann.api.LogPlayerPack;
import lol.clann.api.LoggerListener;
import lol.clann.api.Operation;
import lol.clann.api.ReflectApi;
import lol.clann.api.ServerInfo;
import lol.clann.api.SpecialItem;
import lol.clann.data.dataBooleanKey;
import lol.clann.data.dataMaterialKey;
import lol.clann.data.dataOperationKey;
import lol.clann.data.dataPlayerKey;
import lol.clann.data.dataWorldKey;
import lol.clann.object.nbt.NBTTagCompound;
import lol.clann.pluginbase.api.AutoRegister;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author zyp
 */
@AutoRegister
public class logBlock extends LoggerListener {

    private int 无尽镐;
    private int 无尽铲;
    private int 无尽斧;

    public static logBlock register() throws SQLException {
        return new logBlock();
    }

    public logBlock() throws SQLException {
        super();
        Material m;
        m = Material.getMaterial(SpecialItem.无尽镐);
        无尽镐 = m != null ? m.getId() : -1;
        m = Material.getMaterial(SpecialItem.无尽铲);
        无尽铲 = m != null ? m.getId() : -1;
        m = Material.getMaterial(SpecialItem.无尽斧);
        无尽斧 = m != null ? m.getId() : -1;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(PlayerInteractEvent e) {
        if (!BlockApi.isEmpty(e.getClickedBlock())) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                logBlock(e.getPlayer(), e.getPlayer().getItemInHand(), Operation.LEFTCLICK_BLOCK.getValue(), e.getClickedBlock(), null);
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                logBlock(e.getPlayer(), e.getPlayer().getItemInHand(), Operation.RIGHTCLICK_BLOCK.getValue(), e.getClickedBlock(), null);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockPlaceEvent e) {
        if (!BlockApi.isEmpty(e.getBlock())) {
            logBlock(e.getPlayer(), e.getPlayer().getItemInHand(), Operation.PLACE_BLOCK.getValue(), e.getBlock(), null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(BlockBreakEvent e) {
        if (!BlockApi.isEmpty(e.getBlock())) {
            org.bukkit.block.Block b = e.getBlock();
            Object tile = ReflectApi.CraftWorld_getTileEntityAt.of(b.getWorld()).call(b.getX(), b.getY(), b.getZ());
            byte[] nbt = null;
            if (tile != null) { //记录NBT数据
                NBTTagCompound tag = new NBTTagCompound();
                ReflectApi.TileEntity_writeToNBT.of(tile).call(tag.getHandle());
                nbt = tag.toBytesGZip();
            }
            logBlock(e.getPlayer(), e.getPlayer().getItemInHand(), Operation.BREAK_BLOCK.getValue(), b, nbt);
        }
    }

    /**
     * 用于过滤无尽工具造成的不必要记录的信息
     *
     * @param world
     * @param handId
     * @param targetId
     * @return
     */
    private boolean shouldLog(String world, int handId, int targetId) {
        if (handId == 无尽镐 || handId == 无尽铲 || handId == 无尽斧) {
            if (targetId == 7 || targetId == 121 || targetId == 1 || targetId == 87 || targetId == 3 || targetId == 12) {//石头、泥土、沙子、地狱岩、末地岩、基岩
                if (world.equalsIgnoreCase(ServerInfo.基岩世界) || world.equalsIgnoreCase(ServerInfo.地狱) || world.equalsIgnoreCase(ServerInfo.末地) || world.equalsIgnoreCase(ServerInfo.资源世界)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void logBlock(Player p, ItemStack hand, byte act, org.bukkit.block.Block b, byte[] nbt) {
        int id = b.getTypeId();
        String world = b.getWorld().getName();
        if (act == Operation.BREAK_BLOCK.getValue() && !ItemApi.isEmpty(hand)) {
            if (!shouldLog(world, hand.getTypeId(), id)) { //过滤垃圾信息,减少信息量(主要是过滤无尽工具产生的不必要的记录)
                return;
            }
        }
        if (ItemApi.isEmpty(hand)) {
            Log.plugin.addQueue(new pack(p.getName(), null, null, act, id, b.getData(), nbt, world, b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(), false));
        } else {
            Log.plugin.addQueue(new pack(p.getName(), hand.getTypeId(), hand.getDurability(), act, id, b.getData(), nbt, world, b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ(), false));
        }
    }

    /**
     * 创建表的sql语句
     *
     * @return
     */
    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,player int,useItemId int,useItemData smallint,action tinyint,blockId int,blockData smallint,nbt image,world int,X int,Y int,Z int,[rollback] bit";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create index bIdx_coordinate  on " + name + " (X, Y,Z)");//坐标联合索引
        Log.plugin.sql.getStatement().execute("create nonclustered index bIdx_rollback on " + name + " ([rollback])");//非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index bIdx_world on " + name + " (world)");//世界非聚集索引
        Log.plugin.sql.getStatement().execute("create clustered index bIdx_time on " + name + " (time)");//时间聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index bIdx_player on " + name + " (player)");//玩家非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index bIdx_action on " + name + " (action)");//操作非聚集索引
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as "
                + "SELECT  "
                + "dbo.dateTrans(time) as 时间,tick as 时钟,pk.player as 玩家,mk.name as 物品,useItemData as 物品data,ok.name as 操作,mk1.name as 方块,blockData as 方块data,nbt,wk.world as 世界,X,Y,Z,bk.name as 回档 "
                + "FROM " + name + " as b left join " + dataMaterialKey.class.getSimpleName() + " as mk on mk.id=b.useItemId left join " + dataMaterialKey.class.getSimpleName() + " mk1 on mk1.id=b.blockId left join " + dataPlayerKey.class.getSimpleName() + " as pk on pk.[index]=b.player left join " + dataWorldKey.class.getSimpleName() + " as wk on wk.[index]=b.world left join " + dataOperationKey.class.getSimpleName() + " as ok on ok.id = b.action left join " + dataBooleanKey.class.getSimpleName() + " as bk on bk.id = b.[rollback] ");//创建视图
    }

    long lastTimeStap = 0;//存储上次插入的时间戳，防止统一毫秒记录两条数据，给回档造成障碍

    class pack extends LogPlayerPack {

        Object p4;
        Object p5;
        byte p6;
        int p7;
        byte p8;
        byte[] p9;
        String p10;
        int p11;
        int p12;
        int p13;
        boolean p14;

        pack(String t3, Object t4, Object t5, byte t6, int t7, byte t8, byte[] t9, String t10, int t11, int t12, int t13, boolean t14) {
            p3 = t3;
            p4 = t4;
            p5 = t5;
            p6 = t6;
            p7 = t7;
            p8 = t8;
            p9 = t9;
            p10 = t10;
            p11 = t11;
            p12 = t12;
            p13 = t13;
            p14 = t14;
            lastTimeStap = unique(lastTimeStap);
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setObject(4, p4);
            ps.setObject(5, p5);
            ps.setByte(6, p6);
            ps.setInt(7, p7);
            ps.setShort(8, p8);
            ps.setBytes(9, p9);
            ps.setInt(10, Log.plugin.worldKey.getWorldKeyByName(p10));
            ps.setInt(11, p11);
            ps.setInt(12, p12);
            ps.setInt(13, p13);
            ps.setBoolean(14, p14);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).append(separator).append(p5).append(separator).append(p6).append(separator).append(p7).append(separator).append(p8).append(separator).append(p9).append(separator).append(p11).append(separator).append(p12).append(separator).append(p13).append(separator).append(p14).toString();
        }
    }

}
