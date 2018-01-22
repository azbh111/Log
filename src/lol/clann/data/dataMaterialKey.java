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
import lol.clann.api.MaterialApi;
import lol.clann.api.iPack;

/**
 *
 * @author zyp
 */
@AutoRegister.Register(plugin = Log.plgName, priority = Integer.MAX_VALUE, type = "logger")
public class dataMaterialKey extends Logger {

    public static dataMaterialKey register() throws SQLException {
        return new dataMaterialKey();
    }

    public dataMaterialKey() throws SQLException {
        super();
        if (Log.plugin.sql.getRowCountOfTable(name) <= 0) {
            generate();
        }
        Log.plugin.materialKey = this;
    }

    /**
     * 用于指令调用(当新增MOD或删除MOD时ID发生变化，用于更新数据)
     *
     * @throws SQLException
     */
    public void regenerate() throws SQLException {
        Log.plugin.sql.getStatement().execute("truncate table " + name);//删除本表所有数据
        generate();
    }

    /**
     * 加载所有数据
     *
     * @throws SQLException
     */
    private void generate() throws SQLException {
        for (int i = 0; i < MaterialApi.byId.length; i++) {//按ID顺序插入
            if (MaterialApi.byId[i] != null) {
                Log.plugin.queue.add(new pack(i, MaterialApi.byId[i].name()));
            }
        }
    }

    @Override
    protected String getTableColumnDefinition() {
        return "id int,name text";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index mkIdx_id on " + name + " (id)");//ID聚集索引
    }

    class pack extends iPack {

        int p1;
        String p2;

        pack(int t1, String t2) {
            p1 = t1;
            p2 = t2;
        }

        @Override
        public void excute() throws SQLException {
            ps.setInt(1, p1);
            ps.setString(2, p2);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).toString();
        }
    }

}
