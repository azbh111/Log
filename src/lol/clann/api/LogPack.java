/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lol.clann.Clann;

/**
 * 记录
 *
 * @author zyp
 */
public abstract class LogPack extends iPack {

    protected long p1 = System.currentTimeMillis();
    protected int p2 = Clann.serverTick.getTick();

    protected void preExecute(PreparedStatement ps) throws SQLException {
        ps.setLong(1, p1);
        ps.setInt(2, p2);
    }

    protected long unique(long t) {
        if (p1 == t) {
            p1 = ++t;
        }
        return p1;
    }

}
