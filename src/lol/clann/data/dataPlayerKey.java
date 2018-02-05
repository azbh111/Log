/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.data;

import java.sql.*;
import java.util.*;
import lol.clann.Log;
import lol.clann.api.Logger;
import lol.clann.api.iPack;
import lol.clann.pluginbase.api.AutoRegister;


/**
 *
 * @author zyp
 */
@AutoRegister
public class dataPlayerKey extends Logger {

    private Map<String, Integer> PlayerKey = new LinkedHashMap();
    private int key = 1;
    private String[] players = null;

    public static dataPlayerKey register() throws SQLException {
        return new dataPlayerKey();
    }

    public dataPlayerKey() throws SQLException {
        super();
        initData();
        Log.plugin.playerKey = this;
    }

    /**
     * 重写预编译指令
     *
     * @throws SQLException
     */
    @Override
    protected void initPreparedStatement() throws SQLException {
        ps = Log.plugin.sql.getPreparedStatement("insert into " + name + "(player) values(?)");
    }

    /**
     * 记录时，会读取玩家key，若不存在，会自动生成
     *
     * @param name
     * @return
     */
    public int getPlayerKeyByName(String name) {
        Integer re = PlayerKey.get(name);
        if (re == null) {
            re = addNewPlayer(name);
        }
        return re;
    }

    public String getPlayerNameByKey(int key) {
        return players[key];
    }

    private int addNewPlayer(String name) {
        if (PlayerKey.containsKey(name)) {
            return -1;
        }
        if (key > players.length - 1) {
            expand();
        }
        int re = key;
        PlayerKey.put(name, key);
        players[key] = name;
        key++;
        Log.plugin.addQueue(new pack(name));//写入数据库
        return re;
    }

    private void expand() {
        String[] a = new String[players.length + 100];
        for (int i = 1; i < key; i++) {
            a[i] = players[i];
        }
        players = a;
    }

    /**
     * 加载所有数据
     *
     * @throws SQLException
     */
    private void initData() throws SQLException {
        ResultSet rs = Log.plugin.sql.getStatement().executeQuery("select * from " + name);
        while (rs.next()) {
            PlayerKey.put(rs.getString(2), rs.getInt(1));
        }
        rs.close();
        key = PlayerKey.size() + 1;
        players = new String[key + 100];
        PlayerKey.entrySet().stream().forEach(en -> {
            players[en.getValue()] = en.getKey();
        });
    }

    @Override
    protected String getTableColumnDefinition() {
        return "[index] int primary key identity(1,1),player varchar(16) unique";
    }

    @Override
    protected void createIndex() throws SQLException {
        //此表有主键，自动创建聚集索引
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
