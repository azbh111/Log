use LogAll
GO
//拆分表,节省空间



//创建时间转换函数
GO
create function dateTrans(@mt bigint) returns varchar(23)
as begin
return convert(char(19),DATEADD(second,@mt/1000,'1970-1-1 08:00:00.000'),120)+'.'+CONVERT(char(3),@mt%1000)
end
GO
--logBlock视图
SELECT 
	dbo.dateTrans(time) as 时间,
	tick as tick时钟,
	pk.player as 玩家,
	mk.name as 物品,
	useItemData as 物品data,
	ok.name as 操作,
	mk1.name as 方块,
	blockData as 方块data,
	nbt,
	wk.world as 世界,
	X,Y,Z,bk.name as 回档 
FROM logBlock as b 
left join dataMaterialKey as mk on mk.id = b.useItemId 
left join dataMaterialKey mk1  on mk1.id = b.blockId 
left join dataPlayerKey as pk on pk.[index] = b.player 
left join dataWorldKey as wk on wk.[index] = b.world 
left join dataOperationKey as ok on ok.id = b.action 
left join dataBooleanKey as bk on bk.id = b.[rollback] 


GO
//logCommand视图
--logBlock视图
create view view_logCommand as 
SELECT 
	dbo.dateTrans(time) as 时间,
	tick as tick时钟,
	pk.player as 玩家,
	command as 消息
FROM logCommand as c
left join dataPlayerKey as pk on pk.[index] = c.player

GO
create view view_logItem as 
SELECT 
		dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,pk.player as 玩家
      ,wk.world as 世界
      ,X
      ,Y
      ,Z
      ,ok.name as 操作
      ,count as 数量
      ,mk.name as 物品
      ,itemData as 物品data
      ,target as 目标容器
      ,extra as 附魔
  FROM logItem as o
left join dataMaterialKey as mk on mk.id = o.itemId 
left join dataPlayerKey as pk on pk.[index] = o.player 
left join dataWorldKey as wk on wk.[index] = o.world 
left join dataOperationKey as ok on ok.id = o.action 

GO
create view view_logPlayer as 
SELECT 
       dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,pk.player as 玩家
      ,ok.name as 操作
      ,playerIp as ip
      ,message as 消息
  FROM logPlayer as o
left join dataPlayerKey as pk on pk.[index] = o.player 
left join dataOperationKey as ok on ok.id = o.action 

GO
create view view_logPlayerChat as 
SELECT 
       dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,pk.player as 玩家
      ,message as 消息
  FROM logPlayerChat as o
left join dataPlayerKey as pk on pk.[index] = o.player 


GO
create view view_logPlayerDeath as 
SELECT 
       dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,pk.player as 玩家
      ,deathMessage as 消息
  FROM logPlayerDeath as o
left join dataPlayerKey as pk on pk.[index] = o.player 

GO
create view view_logSystem as 
SELECT
	   dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,totalMemory as 分配内存
      ,freeMemory as 空闲内存
      ,thread as 线程数
      ,player as 玩家数
      ,chunk as 区块数
      ,entity as 生物数
      ,tps
  FROM logSystem

 GO
create view view_logSystem as 
SELECT 
       dbo.dateTrans(time) as 时间
      ,tick as 时钟
      ,pk.player as 玩家
      ,wk.world as 从世界
      ,fromX as 从X
      ,fromY as 从Y
      ,fromZ as 从Z
      ,wk1.world as 到世界
      ,toX as 到X
      ,toY as 到Y
      ,toZ as 到Z
  FROM logTeleport as o
left join dataPlayerKey as pk on pk.[index] = o.player 
left join dataWorldKey as wk on wk.[index] = o.fromWorld 
left join dataWorldKey as wk1 on wk1.[index] = o.toWorld 
