/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.data;

import java.sql.SQLException;
import lol.clann.Log;
import lol.clann.api.AutoRegister;
import lol.clann.api.Logger;
import lol.clann.api.iPack;

/**
 *
 * @author zyp
 */
@AutoRegister.Register(plugin = Log.plgName, priority = Integer.MAX_VALUE, type = "logger")
public class dataBooleanKey extends Logger {

    public static dataBooleanKey register() throws SQLException {
        return new dataBooleanKey();
    }

    public dataBooleanKey() throws SQLException {
        super();
        if (Log.plugin.sql.getRowCountOfTable(name) < 2) {
            generate();
        }
    }

    /**
     * 加载所有数据
     *
     * @throws SQLException
     */
    private void generate() throws SQLException {
        Log.plugin.sql.getStatement().execute("truncate table " + name);//删除本表所有数据
        Log.plugin.queue.add(new pack(false, "否"));
        Log.plugin.queue.add(new pack(true, "是"));
    }

    @Override
    protected String getTableColumnDefinition() {
        return "[id] bit,name nchar(1)";
    }

    @Override
    protected void createIndex() throws SQLException {
        //只有两条数据，不建立索引
    }

    class pack extends iPack {

        boolean p1;
        String p2;

        pack(boolean t1, String t2) {
            p1 = t1;
            p2 = t2;
        }

        @Override
        public void excute() throws SQLException {
            ps.setBoolean(1, p1);
            ps.setString(2, p2);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).toString();
        }
    }
}
