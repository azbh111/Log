/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.data;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lol.clann.Log;
import lol.clann.api.AutoRegister;
import lol.clann.api.Logger;
import lol.clann.api.Operation;
import lol.clann.api.iPack;

@AutoRegister.Register(plugin = Log.plgName, priority = Integer.MAX_VALUE, type = "logger")
public class dataOperationKey extends Logger {

    public static dataOperationKey register() throws SQLException {
        return new dataOperationKey();
    }

    public dataOperationKey() throws SQLException {
        super();
        if (Log.plugin.sql.getRowCountOfTable(name) < Operation.values().length) {
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
//        Operation[] values = Operation.values();
        List<Operation> values = Arrays.asList(Operation.values());
        Collections.sort(values, new Comparator<Operation>() {
            int v1, v2;

            @Override
            public int compare(Operation o1, Operation o2) { //升序:o1>o2返回正值
                v1 = o1.getValue();
                v2 = o2.getValue();
                return v1 == v2 ? 0 : v1 > v2 ? 1 : -1;
            }
        });
        //排序后按ID升序插入
        for (Operation value : values) {
            Log.plugin.queue.add(new pack(value.getValue(), value.getName()));
        }

    }

    @Override
    protected String getTableColumnDefinition() {
        return "id tinyint,name varchar(" + Operation.getMaxLength() + ")";
    }

    @Override
    protected void createIndex() throws SQLException {
        Log.plugin.sql.getStatement().execute("create clustered index okIdx_id on " + name + " (id)");//id聚集索引
    }

    class pack extends iPack {

        byte p1;
        String p2;

        pack(byte t1, String t2) {
            p1 = t1;
            p2 = t2;
        }

        @Override
        public void excute() throws SQLException {
            ps.setByte(1, p1);
            ps.setString(2, p2);
            ps.execute();
        }

        @Override
        public String toString() {
            return new StringBuilder().append(p1).append(separator).append(p2).toString();
        }
    }
}
