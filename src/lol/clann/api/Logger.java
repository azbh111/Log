/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.utils.API;

/**
 *
 * @author zyp
 */
public abstract class Logger {

    protected PreparedStatement ps;
    protected final String name;

    public Logger() throws SQLException {
        this.name = this.getClass().getSimpleName();
        initTable();
        initPreparedStatement();
    }

    /**
     * 初始化预编译指令
     *
     * @throws SQLException
     */
    protected void initPreparedStatement() throws SQLException {
        ps = Log.plugin.sql.getPreparedStatement(getInsertSql(name, Log.plugin.sql.getColumnCountOfTable(name)));
    }

    private void initTable() throws SQLException {
        if (!tableExist()) {
            Log.plugin.sql.startTransaction();
            createTable();//创建表
            createIndex();//创建索引
            createView();//创建视图
            Log.plugin.sql.commitTransaction();
            API.log("创建表:" + name);
        }
    }

    /**
     * 创建索引
     */
    protected abstract void createIndex() throws SQLException;

    protected void createView() throws SQLException {

    }

    protected boolean tableExist() throws SQLException {
        return Log.plugin.sql.tableExist(name);
    }

    protected static String getInsertSql(String name, int parmCount) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(name).append(" values(?");
        for (int i = 1; i < parmCount; i++) {
            sb.append(",?");
        }
        sb.append(")");
        return sb.toString();
    }

    protected abstract String getTableColumnDefinition();

    /**
     * 创建表
     *
     * @throws SQLException
     */
    private void createTable() throws SQLException {
        Log.plugin.sql.getStatement().execute("create table " + name + "(" + getTableColumnDefinition() + ")");
    }

}
