/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import lol.clann.Log;
import lol.clann.api.LogPlayerPack;
import lol.clann.api.LoggerListener;
import lol.clann.api.Operation;
import lol.clann.data.dataMaterialKey;
import lol.clann.data.dataOperationKey;
import lol.clann.data.dataPlayerKey;
import lol.clann.data.dataWorldKey;
import lol.clann.object.ID;
import lol.clann.pluginbase.api.AutoRegister;
import lol.clann.utils.API;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author zyp
 */
@AutoRegister
public class logItem extends LoggerListener {

    public boolean debug = false;
    public Map<String, String> chestName = new HashMap<>();
//    public Map<String, Integer> invItems = new HashMap<>();     // 存储玩家背包数据key:  物品ID  Integer 物品数量
    public Map<Player, Map<ID, Integer>> playerInvs = new HashMap<>();  //存储玩家背包数据
    public Map<Player, Boolean> isOpenInv = new HashMap<>();
//    Logger logger;

    public static logItem register() throws SQLException {
        return new logItem();
    }

    public logItem() throws SQLException {
        super();
        initChestName();
    }

    private void initChestName() {
        for (int i = 1; i < 11; i++) {
            chestName.put("商店" + i, "玩家商店");
        }
        chestName.put("COPPER", "铜箱子");
        chestName.put("IRON", "铁箱子");
        chestName.put("SILVER", "银箱子");
        chestName.put("GOLD", "金箱子");
        chestName.put("DIAMOND", "钻石箱子");
        chestName.put("CRYSTAL", "水晶箱子");
        chestName.put("OBSIDIAN", "黑曜石箱子");
        chestName.put("container.chest", "MC箱子");
        chestName.put("container.chestDouble", "MC大箱子");
        chestName.put("TileChest", "ME箱子");
        chestName.put("TileDrive", "ME驱动器");
        chestName.put("AEMultiPart", "ME网络");
        chestName.put("container.enderchest", "末影箱");
        chestName.put("container.minecart", "运输矿车");
        chestName.put("container.hopper", "漏斗");
        chestName.put("Mass Fabricator", "UU物质生成机");
        chestName.put("Replicator", "复制机");
        chestName.put("Personal Safe", "IC保险箱");
        chestName.put("Molecular Transformer", "分子重塑仪");
        chestName.put("Hungry Chest", "饕餮箱子");
        chestName.put("container.decontable", "解构工作台");
        chestName.put("container.arcaneworkbench", "奥术工作台");
        chestName.put("container.thaumcraft", "核心镶饰台");
        chestName.put("enchanter", "渗透附魔台");
        chestName.put("Enchant", "附魔台");
        chestName.put("warpGate", "仙域之门");
        chestName.put("TileCondenser", "物质聚合器");
        chestName.put("TileIOPort", "ME-IO端口");
        chestName.put("TileInterface", "ME接口");
        chestName.put("container.neutron", "种子态素收集器");
        chestName.put("container.neutronium_compressor", "中子态素压缩机");
        chestName.put("container.dire", "终极工作台");
        chestName.put("Hopper", "BC料斗");
        chestName.put("tile.for.factory2.2.name", "林业工作台");
        chestName.put("tile.for.apicultureChest.0.name", "养蜂人箱子");
        chestName.put("Chest", "其他容器"); //一些MOD的容器和手持容器
        chestName.put("ElectricHeatGenerator", "电力加热器");
        chestName.put("Generator", "火力发电机");
        chestName.put("Geoth. Generator", "地热发电机");
        chestName.put("Water Mill", "水力发电机");
        chestName.put("Solar Panel", "太阳能发电机");
        chestName.put("StirlingKineticGenerator", "Stirling Kinetic Generator");
        chestName.put("SolidHeatGenerator", "固体加热器");
        chestName.put("FluidHeatGenerator", "流体加热器");
        chestName.put("RTHeatGenerator", "放射性同位素温差加热器");
        chestName.put("Water Kinetic Generator", "水力动能发生器");
        chestName.put("Electric Kinetic Generator", "电力动能发生器");
        chestName.put("Steam Kinetic Generator", "蒸汽动能发生器");
        chestName.put("Wind Kinetic Generator", "风力动能发生器");
        chestName.put("DIRTCHEST9000", "泥箱子");
        chestName.put("container.dispenser", "发射器");
        chestName.put("container.crafting", "工作台");
        chestName.put("container.furnace", "熔炉");
        chestName.put("Repair", "铁砧");
        chestName.put("Wind Mill", "风力发电机");
        chestName.put("Nuclear Reactor", "核反应堆");
        chestName.put("RTGenerator", "放射性同位素温差发电机");
        chestName.put("SemifluidGenerator", "半流质发电机");
        chestName.put("StirlingGenerator", "斯特林发电机、动能发电机");
        chestName.put("Reactor Fluid Port", "反应堆流体接口");
        chestName.put("TransformerLV", "低压变压器");
        chestName.put("TransformerMV", "中压变压器");
        chestName.put("TransformerHV", "高压变压器");
        chestName.put("TransformerUV", "超高压变压器");
        chestName.put("BatBox", "储电盒");
        chestName.put("CESU", "CESU");
        chestName.put("MFE", "MFE");
        chestName.put("MFSU", "MFSU");
        chestName.put("Chargepad BatBox", "充电座");
        chestName.put("Chargepad CESU", "CESU充电座");
        chestName.put("Chargepad MFE", "MFE充电座");
        chestName.put("Chargepad MFSU", "MFSU充电座");
        chestName.put("Iron Furnace", "铁炉");
        chestName.put("Electric Furnace", "电炉");
        chestName.put("Macerator", "打粉机");
        chestName.put("Extractor", "提取机");
        chestName.put("Compressor", "压缩机");
        chestName.put("Canning Machine", "流体固体装罐机");
        chestName.put("Miner", "采矿机");
        chestName.put("Pump", "泵");
        chestName.put("Magnetizer", "磁化机");
        chestName.put("Electrolyzer", "电解机");
        chestName.put("Recycler", "回收机");
        chestName.put("InductionFurnace", "感应炉");
        chestName.put("Crop-Matron", "作物监管机");
        chestName.put("ThermalCentrifuge", "热能离心机");
        chestName.put("MetalFormer", "金属成型机");
        chestName.put("OreWashing", "洗矿机");
        chestName.put("PatternStorage", "模式存储机");
        chestName.put("scanner", "模式扫描机");
        chestName.put("Solid Canning Machine", "固体装罐机");
        chestName.put("Bottling Plant", "流体装管机");
        chestName.put("AdvMiner", "高级采矿机");
        chestName.put("HeatSourceFluid", "流体热交换机");
        chestName.put("Fermenter", "发酵机");
        chestName.put("WaterHeater", "流体流量调节机");
        chestName.put("Condenser", "冷凝机");
        chestName.put("Steam Generator", "蒸气机");
        chestName.put("Blast Furnace", "IC高炉");
        chestName.put("BlockCutter", "方块切割机");
        chestName.put("Solar Destiller", "太阳能蒸馏机");
        chestName.put("Fluid Distributor", "流体分配机");
        chestName.put("SortingMachine", "电动分拣机");
        chestName.put("ItemBuffer", "物品缓冲机");
        chestName.put("CropHavester", "作物收割机");
        chestName.put("Lathe", "车床");
        chestName.put("Trade-O-Mat", "贸易箱");
        chestName.put("Energy-O-Mat", "电力贸易箱");
        chestName.put("Nuke", "核弹");
        chestName.put("CE Solar 1", "可见光发电机1EU");
        chestName.put("CE Solar 8", "可见光发电机8EU");
        chestName.put("CE Solar 64", "可见光发电机64EU");
        chestName.put("CE Solar 512", "可见光发电机512EU");
        chestName.put("AFSU", "AFSU");
        chestName.put("Advanced Solar Panel", "IC拓展-高级太阳能的发电机");
        chestName.put("Arcane Bore", "奥数钻探机");
        chestName.put("container.alchemyfurnace", "炼金炉");
        chestName.put("tile.for.factory.5.name", "林业榨汁机");
        chestName.put("tile.for.factory.6.name", "林业蒸馏器");
        chestName.put("block.StatusDisplay", "(高级)工业信息屏");
        chestName.put("block.RemoteThermo", "远程温度监控器");
        chestName.put("block.RemoteThermo", "(平均)电力计数器");
        chestName.put("block.RangeTrigger", "范围触发器");
        chestName.put("tile.for.apiculture.0.name", "蜂箱");
        chestName.put("tile.for.apiculture.2.name", "简易蜂房");
        chestName.put("tile.for.lepidopterology.0.name", "鳞翅目昆虫学家箱子");
        chestName.put("tile.for.core.0.name", "分析仪");
        chestName.put("tile.for.core.1.name", "写字台");
        chestName.put("tile.for.engine.0.name", "电动引擎");
        chestName.put("tile.for.engine.1.name", "泥潭引擎");
        chestName.put("tile.for.engine.2.name", "生物燃油引擎");
        chestName.put("tile.for.engine.3.name", "生物质引擎");
        chestName.put("tile.for.factory.0.name", "装瓶机");
        chestName.put("tile.for.factory.1.name", "木工机");
        chestName.put("tile.for.factory.2.name", "林业离心机");
        chestName.put("tile.for.factory.3.name", "林业发酵机");
        chestName.put("tile.for.factory.4.name", "林业加湿器");
        chestName.put("tile.for.factory2.0.name", "林业热电子加工台");
        chestName.put("tile.for.factory2.1.name", "林业集雨蓄水槽");
        chestName.put("tile.for.mail.0.name", "林业信箱");
        chestName.put("tile.for.mail.1.name", "林业贸易站");
        chestName.put("tile.for.mail.2.name", "集邮箱");
        chestName.put("TileSkyChest", "陨石(块)箱子");
        chestName.put("TileGrinder", "石英磨具");
        chestName.put("TileInscriber", "压印器");
        chestName.put("TileWireless", "ME无线访问点");
        chestName.put("TileQuantumBridge", "ME量子连接舱");
        chestName.put("TileSpatialIOPort", "空间IO端口");
        chestName.put("TileVibrationChamber", "AE谐振仓");
        chestName.put("blockTransceiver", "EIO空间传送器");
        chestName.put("blockEnchanter", "EIO附魔器");
        chestName.put("tank", "EIO(高压)流体箱");
        chestName.put("Items", "林业蜂箱组");
        chestName.put("CanInv", "林业蜂箱组保湿器");
        chestName.put("SwarmInv", "林业蜂箱组克隆盒");
        chestName.put("tile.npcBarrel.name", "NPC桶");
        chestName.put("tile.npcCrate.name", "NPC板条箱");
        chestName.put("blockCombustionGenerator", "EIO燃烧发电机");
        chestName.put("blockKillerJoe", "EIO杀手乔");
        chestName.put("blockSagMill", "SAG磨粉机");
        chestName.put("blockSoulBinder", "EIO灵魂绑定器");
        chestName.put("blockCrafter", "EIO合成器");
        chestName.put("blockZombieGenerator", "EIO僵尸发电机");
        chestName.put("Farming Station", "EIO种植站");
        chestName.put("Alloy Smelter", "EIO合金炉");
        chestName.put("blockVacuumChest", "虚空箱子");
        chestName.put("Auto Painter", "EIO喷涂机");
        chestName.put("blockAttractor", "EIO生物诱引装置");
        chestName.put("blockVat", "EIO酸液桶");
        chestName.put("blockExperienceObelisk", "EIO经验交换装置");
        chestName.put("blockSliceAndSplice", "EIO头颅装配机");
        chestName.put("blockSpawnGuard", "EIO生物排除装置");
        chestName.put("blockPoweredSpawner", "EIO电动刷怪笼");
        chestName.put("Stirling Generator", "EIO斯特林发电机");
        chestName.put("tile.blockPowerMonitor", "EIO能量监控器");
        chestName.put("tile.blockBuffer.item", "EIO物品缓存器");
        chestName.put("tile.blockBuffer.power", "EIO能量缓存器");
        chestName.put("tile.blockBuffer.omni", "EIO多功能缓存器");
        chestName.put("tile.blockBuffer.creative", "EIO创造缓存器");
        chestName.put("tile.blockCapacitorBank.name", "EIO(基础、谐振)电容库");
        chestName.put("animationTablet", "活化工具台");
        chestName.put("bamboo.container.campfire", "竹 营火");
        chestName.put("MillStone", "石磨");
        chestName.put("TileMolecularAssembler", "分子装配室");
        chestName.put("IECokeOven", "IE焦炉");
        chestName.put("IEWoodenCrate", "IE木质存储箱");
        chestName.put("Research Table", "神秘研究台");
        chestName.put("container.brewing", "MC酿造台");
        chestName.put("IEBlastFurnaceAdvanced", "IE精制高炉");
        chestName.put("IEWorkbench", "IE工程师装配台");
        chestName.put("IE:squeezer", "IE工业挤压机");
        chestName.put("IERefinery", "IE炼油厂");
        chestName.put("IEArcFurnace", "IE电弧炉");
        chestName.put("IEFermenter", "IE发酵机");
        chestName.put("IEBlastFurnace", "IE高炉");
        chestName.put("thaumcraft.spa", "神秘奥数浴场");
        chestName.put("IEAssembler", "IE装配机");
        chestName.put("tile.for.arboriculture.0.name", "林业员箱子");
        chestName.put("IERouter", "IE物品路由器");
        chestName.put("container.beacon", "信标");
        chestName.put("armourLibrary", "时装工坊-资源库");
        chestName.put("armourerBrain", "时装工坊");
        chestName.put("colourMixer", "时装工坊-调色器");
        chestName.put("dyeTable", "时装工坊-配色工作台");
        chestName.put("mannequin", "时装工坊-模特");
    }

    /**
     * 记录玩家丢弃物品
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        String extra = "";
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            extra = "(" + item.getItemMeta().getDisplayName() + ")";
        }
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            extra = extra + item.getEnchantments();
        }
        try {
            logItem(p, p.getLocation(), Operation.DROP_ITEM.getValue(), (short) item.getAmount(), item.getTypeId(), item.getDurability(), null, extra);
        } catch (SQLException ex) {
            API.log(ex, "PlayerDropItemEvent异常");
        }
    }

    /**
     * 更正玩家打开背包时丢弃物品可能导致的计算错误
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        ItemStack is = event.getItemDrop().getItemStack();
        ID id = new ID(is.getTypeId(), is.getDurability());
        Player p = event.getPlayer();
        if (!playerInvs.containsKey(p)) {
            return;
        }
        if (!playerInvs.get(p).containsKey(id)) {
            return;
        }
        Map<ID, Integer> items = playerInvs.get(p);
        items.put(id, items.get(id) - is.getAmount());
    }

    /**
     * 更正玩家打开背包时捡起物品可能导致的计算错误
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickupItem1(PlayerPickupItemEvent event) {
        ItemStack is = event.getItem().getItemStack();
        ID id = new ID(is.getTypeId(), is.getDurability());
        Player p = event.getPlayer();
        if (!playerInvs.containsKey(p)) {
            return;
        }
        if (!playerInvs.get(p).containsKey(id)) {
            return;
        }
        Map<ID, Integer> items = playerInvs.get(p);
        items.put(id, items.get(id) + is.getAmount());
    }

    /**
     * 玩家打开背包时记录背包内容
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        if (!chestName.containsKey(event.getInventory().getTitle())) {
            if (debug) {
                String s = "不支持这种容器:  " + event.getPlayer().getLocation()
                        + "\n" + "Name   " + event.getInventory().getName()
                        + "\n" + "Title  " + event.getInventory().getTitle() + "\n\n";
                API.log(s);
                if (event.getPlayer().isOp()) {
                    System.out.println(s);
                }
            }
        }
        Player p = (Player) event.getPlayer();
        if (!isOpenInv.containsKey(p)) {
            isOpenInv.put(p, false);
        }
        if (!isOpenInv.get(p)) {
            isOpenInv.put(p, true);
            playerInvs.put(p, readInv(p));
        }
    }

    /**
     * 玩家关闭背包时，分析背包内容变化
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Map<ID, Integer> newInvItems = readInv(p);// 存储玩家背包数据key:  物品ID  Integer 物品数量
        if (!playerInvs.containsKey(p)) {
            return;
        }
        compareInv(p, event.getInventory().getTitle(), playerInvs.get(p), newInvItems);
        playerInvs.remove(p);
        isOpenInv.put(p, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        String extra = "";
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            extra = "(" + item.getItemMeta().getDisplayName() + ")";
        }
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            extra = extra + item.getEnchantments();
        }
        try {
            logItem(p, p.getLocation(), Operation.PICKUP_ITEM.getValue(), (short) item.getAmount(), item.getTypeId(), item.getDurability(), null, extra);
        } catch (SQLException ex) {
            API.log(ex, "PlayerPickupItemEvent异常");
        }
    }

    /**
     * 记录玩家背包物品
     *
     * @param p
     * @return
     */
    private Map<ID, Integer> readInv(Player p) {
        Inventory inv = p.getInventory();
        Map<ID, Integer> invItems = new HashMap<>();
        ItemStack[] items = inv.getContents();
        ID id;
        Integer num;
        for (ItemStack it : items) {
            if (it == null || it.getType().equals(Material.AIR)) {
                continue;
            }
            id = new ID(it.getTypeId(), it.getDurability());
            num = it.getAmount();
            if (invItems.containsKey(id)) {
                invItems.put(id, num + invItems.get(id));
            } else {
                invItems.put(id, num);
            }
        }
        return invItems;
    }

    private void compareInv(Player p, String invType, Map<ID, Integer> oldInv, Map<ID, Integer> newInv) {
        ID id;
        Integer num;
        for (Map.Entry<ID, Integer> ent : oldInv.entrySet()) {
            id = ent.getKey();
            num = ent.getValue();
            if (newInv.containsKey(id)) {
                newInv.put(id, newInv.get(id) - num);
            } else {
                newInv.put(id, -num);
            }
        }
        for (Map.Entry<ID, Integer> ent : newInv.entrySet()) {
            analyseVariety(p, invType, ent.getKey(), ent.getValue());
        }
    }

    private void analyseVariety(Player p, String invType, ID id, int num) {
        if (num == 0) {
            return;
        }
        try {
            logItem(p, p.getLocation(), (byte) (num > 0 ? Operation.GET_OUT.getValue() : Operation.PUT_IN.getValue()), (short) (num > 0 ? num : -num), id.id, id.data, invType, null);
        } catch (SQLException ex) {
            API.log(ex, "analyseVariety异常");
        }
    }

    private void logItem(Player p, Location loc, byte act, short count, int id, short data, String target, String enchantment) throws SQLException {
        if (target != null && !target.isEmpty()) {
            String t = chestName.get(target);
            if (t != null) {
                target = t;
            }
        } else {
            target = null;
        }
        enchantment = enchantment != null && !enchantment.isEmpty() ? enchantment : null;
        Log.plugin.addQueue(new pack(p.getName(), loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), act, count, id, data, target, enchantment));
    }

    @Override
    protected String getTableColumnDefinition() {
        return "time bigint,tick int,player int,world int,X int,Y int,Z int,action tinyint,count smallint,itemId int,itemData smallint,target text,extra text";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index iIdx_time on " + name + " (time)");//日期聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index iIdx_player on " + name + " (player)");//玩家非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index iIdx_itemId on " + name + " (itemId)");//物品非聚集索引
        Log.plugin.sql.getStatement().execute("create nonclustered index iIdx_ItemData on " + name + " (ItemData)");//物品非聚集索引
    }

    @Override
    protected void createView() throws SQLException {
        Log.plugin.sql.getStatement().execute(""
                + "create view view_" + name + " as \n"
                + "SELECT \n"
                + "       dbo.dateTrans(time) as 时间\n"
                + "      ,tick as 时钟\n"
                + "      ,pk.player as 玩家\n"
                + "      ,wk.world as 世界\n"
                + "      ,X\n"
                + "      ,Y\n"
                + "      ,Z\n"
                + "      ,ok.name as 操作\n"
                + "      ,count as 数量\n"
                + "      ,mk.name as 物品\n"
                + "      ,itemData as 物品data\n"
                + "      ,target as 目标容器\n"
                + "      ,extra as 附魔\n"
                + "  FROM " + name + " as o\n"
                + "left join " + dataMaterialKey.class.getSimpleName() + " as mk on mk.id = o.itemId \n"
                + "left join " + dataPlayerKey.class.getSimpleName() + " as pk on pk.[index] = o.player \n"
                + "left join " + dataWorldKey.class.getSimpleName() + " as wk on wk.[index] = o.world \n"
                + "left join " + dataOperationKey.class.getSimpleName() + " as ok on ok.id = o.action \n"
                + "");//创建视图
    }

    class pack extends LogPlayerPack {

        String p4;
        int p5;
        int p6;
        int p7;
        byte p8;
        short p9;
        int p10;
        short p11;
        String p12;
        String p13;

        pack(String t3, String t4, int t5, int t6, int t7, byte t8, short t9, int t10, short t11, String t12, String t13) {
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
        }

        @Override
        public void excute() throws SQLException {
            preExecute(ps);
            ps.setInt(4, Log.plugin.worldKey.getWorldKeyByName(p4));
            ps.setInt(5, p5);
            ps.setInt(6, p6);
            ps.setInt(7, p7);
            ps.setByte(8, p8);
            ps.setShort(9, p9);
            ps.setInt(10, p10);
            ps.setShort(11, p11);
            ps.setString(12, p12);
            ps.setString(13, p13);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).append(separator).append(p3).append(separator).append(p4).append(separator).append(p5).append(separator).append(p6).append(separator).append(p7).append(separator).append(p8).append(separator).append(p9).append(separator).append(p10).append(separator).append(p11).append(separator).append(p12).append(separator).append(p13).toString();
        }
    }
}
