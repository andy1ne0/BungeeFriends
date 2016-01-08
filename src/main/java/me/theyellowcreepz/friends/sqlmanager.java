package me.theyellowcreepz.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.config.Configuration;

import java.sql.*;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class sqlmanager {

    Connection conn = null;

    public boolean openConnection(Configuration config){

        String user = config.getString("sql.username");
        String pass = config.getString("sql.password");
        String url = config.getString("sql.url");
        String dbName = config.getString("sql.dbname");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:"+url+"/"+dbName, user, pass);

            return true;

        } catch (Exception e){
            BungeeCord.getInstance().getLogger().severe("[ Friends ] Could not connect to the database! ");
            e.printStackTrace();
            return false;
        }

    }

    public void submitQuery(String s){
        if(conn != null){
            try {
                PreparedStatement statement = conn.prepareStatement(s);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public void terminateConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet getFromDB(String s) throws SQLException {

        if(conn != null){

            PreparedStatement statement = conn.prepareStatement(s);
            ResultSet results = statement.executeQuery();
            return results;

        } else {
            throw new SQLException("Could not submit query! The connection is not valid. ");
        }

    }

}
