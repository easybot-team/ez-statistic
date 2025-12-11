# EzStatistic

专为 [EasyBot](https://docs.inectar.cn) 打造的通用 Minecraft [统计信息](https://zh.minecraft.wiki/w/%E7%BB%9F%E8%AE%A1%E4%BF%A1%E6%81%AF) 解析器。

## 为什么要用它？

众所周知，Minecraft 不同版本的统计数据结构简直是“天壤之别”。当你观察过1.12.2和1.13+的统计信息时,你一定会发现下面的问题：

**1.12.2 的数据（简单粗暴）：**
```json lines
{
  "stat.jump": 3607
}
```

**1.13+ 的数据（层层嵌套）：**
```json lines
{
  "stats": {
    "minecraft:custom": {
      "minecraft:jump": 4 // 现在变成了复杂的嵌套结构
    }
  }
}
```

**EzStatistic 就是为了终结这个头疼的问题而生的。**
你不需要关心版本差异，也不用自己做字段映射。我们在内部实现了一套简单的匹配机制：优先尝试匹配`新版 JsonPath`，如果失败，自动回退并转换格式（例如获取损坏物品次数时把 `$.stats['minecraft:broken']['minecraft:wooden_pickaxe']` 自动转译为旧版的 `$['stat.breakItem.minecraft.wooden_pickaxe']`）去匹配旧数据。

简而言之：**你只管查，兼容的问题交给我们。**

## 快速上手

### 1. 准备工作：关于 UUID 的那些事
Minecraft 的统计文件是按 `UUID` 命名的。但我们在写代码时，往往手里只有玩家的 `名字`。
为了避免每次查询都使用 Mojang API（或者处理一个根本不存在的玩家），我们在库里内置了一个轻量的 `Name -> UUID` 本地缓存数据库。

在使用前，请先初始化它：

```java
// 在项目启动时调用
// 数据库基于目录和文件系统,这直接决定了缓存的文件放在哪个目录
StatisticManager.getInstance().initDb("./caches"); 
```

### 2. 缓存数据
虽然我们在玩家离线无法获取UUID时实现了从MojangApi获取信息和对离线玩家的兼容,但是为了性能我们仍然建议在你的实现端手动添加玩家数据。

```java
// 监听玩家加入事件 (PlayerJoinEvent)
StatisticManager.getInstance().getStatDb().putUuidCache(player.getName(), player.getUniqueId());
```

### 3. 开始查询

一切准备就绪，现在获取统计信息变得非常简单：

```java
// 只要缓存里有记录，直接传玩家名字就行，当然传 UUID 也没问题
var stat = StatisticManager.getInstance().getPlayerStat("玩家名 或 UUID");
var rabbitKilled = stat.getEntityKilled("minecraft:rabbit").orElse("0");
```

### 修改查询路径?

默认情况下，直接从当前运行路径下的`stats`目录下读取统计文件。如果你需要修改路径，请调用：
```java
StatisticManager.getInstance().setSavePath(Path.of("xxxx"));
```

### 查询自定义名称的.json?

事实上您传入的 玩家名 或 UUID 加上 `.json` 存在于指定的目录中时，会直接读取该文件。
