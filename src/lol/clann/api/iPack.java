/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lol.clann.api;

import java.sql.SQLException;

/**
 * 
 * @author zyp
 */
public abstract class iPack {

    /**
     * p1-p3通用，其他参数需继承后定义
     */
    protected static char separator = ' ';

    public iPack() {
        
    }

    public abstract void excute() throws SQLException;

    @Override
    public abstract String toString();
}
