/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.SQLException;
import lol.clann.Log;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 *
 * @author zyp
 */
public abstract class LoggerListener extends Logger implements Listener {

    public LoggerListener() throws SQLException {
        super();
        Bukkit.getPluginManager().registerEvents(this, Log.plugin);
    }

}
