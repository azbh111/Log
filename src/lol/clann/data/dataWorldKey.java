/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import lol.clann.Log;
import lol.clann.api.Logger;
import lol.clann.api.iPack;
import lol.clann.pluginbase.api.AutoRegister;

/**
 *
 * @author zyp
 */
@AutoRegister(priority = 98)
public class dataWorldKey extends Logger {

    private Map<String, Integer> WorldKey = new LinkedHashMap();
    private int key = 1;
    private String[] worlds = null;

    public static dataWorldKey register() throws SQLException {
        return new dataWorldKey();
    }

    public dataWorldKey() throws SQLException {
        super();
        initData();
        Log.plugin.worldKey = this;
    }

    /**
     * 重写预编译指令
     *
     * @throws SQLException
     */
    @Override
    protected void initPreparedStatement() throws SQLException {
        ps = Log.plugin.sql.getPreparedStatement("insert into " + name + "(world) values(?)");
    }

    /**
     * 记录时，会读取玩家key，若不存在，会自动生成
     *
     * @param name
     * @return
     */
    public int getWorldKeyByName(String name) {
        Integer re = WorldKey.get(name);
        if (re == null) {
            re = addNewWorld(name);
        }
        return re;
    }

    public String getWorldNameByKey(int key) {
        return worlds[key];
    }

    private int addNewWorld(String name) {
        if (WorldKey.containsKey(name)) {
            return -1;
        }
        if (key > worlds.length) {
            expand();
        }
        int re = key;
        WorldKey.put(name, key);
        worlds[key] = name;
        key++;
        Log.plugin.addQueue(new pack(name));//写入数据库
        return re;
    }

    private void expand() {
        String[] a = new String[worlds.length + 10];
        for (int i = 1; i < key; i++) {
            a[i] = worlds[i];
        }
        worlds = a;
    }

    /**
     * 加载所有数据
     *
     * @throws SQLException
     */
    private void initData() throws SQLException {
        ResultSet rs = Log.plugin.sql.getStatement().executeQuery("select * from " + name);
        while (rs.next()) {
            WorldKey.put(rs.getString(2), rs.getInt(1));
        }
        rs.close();
        key = WorldKey.size() + 1;
        worlds = new String[key + 10];
        WorldKey.entrySet().stream().forEach(en -> {
            worlds[en.getValue()] = en.getKey();
        });
    }

    @Override
    protected String getTableColumnDefinition() {
        return "[index] int primary key identity(1,1),world varchar(16) unique";
    }

    @Override
    protected void createIndex() throws SQLException {
        //有主键，自动创建聚集索引
    }

    class pack extends iPack {

        String p1;

        pack(String t1) {
            p1 = t1;
        }

        @Override
        public void excute() throws SQLException {
            ps.setString(1, p1);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).toString();
        }
    }
}
