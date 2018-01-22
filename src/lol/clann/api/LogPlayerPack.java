/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lol.clann.Log;

/**
 * 与玩家有关的记录
 *
 * @author zyp
 */
public abstract class LogPlayerPack extends LogPack {

    protected String p3;

    protected void preExecute(PreparedStatement ps) throws SQLException {
        super.preExecute(ps);
        ps.setInt(3, Log.plugin.playerKey.getPlayerKeyByName(p3));
    }
}
