package me.andy1ne0.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class Friends extends Plugin implements Listener {

    public static HashMap<Integer, String> messageIDsEncoded = new HashMap<>();
    public static HashMap<Integer, String> locatePlayerEncoded = new HashMap<>();
    public static HashMap<Integer, FriendJoinObject> friendJoinTemp = new HashMap<>();
    public static HashMap<Integer, FriendLeaveObject> friendLeaveTemp = new HashMap<>();
    File file;
    private static Configuration config;
    private static Plugin Main;
    public static ServerSocket ioSock;
    public static ArrayList<InetSocketAddress> otherServers = new ArrayList<>();
    public static String pluginAuthenticationString = "27591Thsefinaa6786785fsefguysgfhkshfbelruh";
    public static SqlManager sql = null;
    public static int connectionsInLast5minutes = 0;
    public static int outgoingconnectionsInLast5minutes = 0;

    @Override
    public void onEnable(){
        try {
            if(!getDataFolder().exists()) {
                if(!getDataFolder().mkdir()){
                    throw new RuntimeException("Could not create configuration folder!");
                }
            }
            file = new File(getDataFolder(), "config.yml");

            if(!file.exists()){
                if(!file.createNewFile()){
                    throw new RuntimeException("Could not create configuration file!");
                }
                try {
                    config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getResourceAsStream("config.yml"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
                saveConfig();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        initConfig();
        if(file.exists()) {
            saveConfig();
        }

        if(!getConfig().getBoolean("enabled")){
            BungeeCord.getInstance().getLogger().info("[ Friends ] Please configure your database/config information before using this plugin! ");
            BungeeCord.getInstance().getLogger().info("[ Friends ] The plugin has been disabled. ");
            return;
        }

        getProxy().getPluginManager().registerCommand(getInstance(), new FriendsCommand());
        getProxy().getPluginManager().registerCommand(getInstance(), new MsgCommand());
        getProxy().getPluginManager().registerCommand(getInstance(), new BlockManager());
        BungeeCord.getInstance().getPluginManager().registerListener(this, this);
        sql = new SqlManager();

        if (sql.openConnection(getConfig())){
            BungeeCord.getInstance().getLogger().info("[ Friends ] Connection to database established! ");
            sql.submitQuery("CREATE TABLE IF NOT EXISTS `blocks` (`blocker` varchar(100) NOT NULL, `blockee` varchar(100) NOT NULL, PRIMARY KEY (`blocker`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            sql.submitQuery("CREATE TABLE IF NOT EXISTS `players` (`username` varchar(30) NOT NULL,`uuid` varchar(100) NOT NULL, PRIMARY KEY (`uuid`)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            sql.submitQuery("CREATE TABLE IF NOT EXISTS `pendingrequests` (`requested` varchar(100) NOT NULL, `requestee` varchar(100) NOT NULL, PRIMARY KEY (`requested`,`requestee`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
            sql.submitQuery("CREATE TABLE IF NOT EXISTS `relationships` ( `primaryuser` varchar(60) NOT NULL, `secondaryuser` varchar(60) NOT NULL, PRIMARY KEY (`primaryuser`,`secondaryuser`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
        }

        if(getConfig().getBoolean("usingMultipleBungees")) {
            if(getConfig().getString("authenticationkey").length() < 3){
                BungeeCord.getInstance().getLogger().info("[ Friends ] Using the default authentication key for cross-bungee communication. This is not recommended! ");
                BungeeCord.getInstance().getLogger().info("[ Friends ] Change this value to a string of more than 3 characters in your config to increase security. ");
                BungeeCord.getInstance().getLogger().info("[ Friends ] All your other instances must use the same authentication key. ");
            } else {
                pluginAuthenticationString = getConfig().getString("authenticationkey").replace("/", "");
                BungeeCord.getInstance().getLogger().info("[ Friends ] Using custom authentication key. ");
            }
            try {
                ioSock = new ServerSocket(getConfig().getInt("communication_port"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String s : getConfig().getStringList("bungeeinstances")) {

                try {

                    String[] uri = s.split(":");
                    InetSocketAddress sock = new InetSocketAddress(uri[0], Integer.parseInt(uri[1]));
                    InetSocketAddress self = new InetSocketAddress(((ListenerInfo) getProxy().getConfig().getListeners().toArray()[0]).getHost().getAddress(), getConfig().getInt("communication_port"));
                    if (self != sock) {
                        otherServers.add(sock);
                    } else {
                        getProxy().getLogger().info("Note: Self Comm IP/Port is " + sock.getHostName() + ":" + sock.getPort());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    getProxy().getLogger().warning("WARNING: Server " + s + " is an invalid IP/Port! ");
                    return;
                } catch (NumberFormatException e) {
                    getProxy().getLogger().warning("WARNING: Server " + s + " has an invalid port! ");
                    return;
                }
            }

            BungeeCord.getInstance().getScheduler().runAsync(this, new IOListener());
            BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
                @Override
                public void run() {
                    BungeeCord.getInstance().getLogger().info("[ Friends ] Incoming Cross-Bungee connections in the last 5 minutes: "+connectionsInLast5minutes);
                    connectionsInLast5minutes = 0;
                    BungeeCord.getInstance().getLogger().info("[ Friends ] Outgoing Cross-Bungee connections in the last 5 minutes: "+outgoingconnectionsInLast5minutes);
                    connectionsInLast5minutes = 0;
                }
            }, 5, 5, TimeUnit.MINUTES);
        } else {
            BungeeCord.getInstance().getLogger().info("[ Friends ] Not using multiple Bungee instance communication. If you want to enable this, adjust the config accordingly. ");
        }

    }

    @Override
    public void onDisable(){
        sql.terminateConnection();
        BungeeCord.getInstance().getLogger().info("[ Friends ] Disabled! ");
    }

    public static Plugin getInstance(){
        return BungeeCord.getInstance().getPluginManager().getPlugin("friends");
    }

    @EventHandler
    public void onJoin(PostLoginEvent evt){
        sql.submitQuery("INSERT IGNORE INTO `players`(`username`, `uuid`) VALUES ('"+evt.getPlayer().getName()+"','"+evt.getPlayer().getUUID()+"')");
        sql.submitQuery("UPDATE `players` SET `username` = '"+evt.getPlayer().getName()+"' WHERE `uuid` = '"+evt.getPlayer().getUUID()+"';");
        for(ProxiedPlayer p : BungeeCord.getInstance().getPlayers()){
            try {
                if(sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+p.getUUID()+"' AND `secondaryuser` LIKE '"+evt.getPlayer().getUUID()+"'").next()){
                    p.sendMessage(StringStandards.friendMessage("Your friend "+evt.getPlayer().getName()+" joined! "));
                    FriendJoinObject frand = new FriendJoinObject(evt.getPlayer().getName(), p.getName());
                    friendJoinTemp.put(frand.getID(), frand);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        IOUtils.sendToOtherBungeeServers(IOStrings.encodedIOplayerJoinMessage(evt.getPlayer().getName()));

        final String plName = evt.getPlayer().getName();

        BungeeCord.getInstance().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Integer> keysToRemove = new ArrayList<>();
                for(int i : friendJoinTemp.keySet()){
                    if(friendJoinTemp.get(i).playerWhoJoined.equals(plName)){
                        keysToRemove.add(i);
                    }
                }
                for(int i : keysToRemove){
                    friendJoinTemp.remove(i);
                }

            }
        });

    }

    @EventHandler
    public void onLeave(PlayerDisconnectEvent evt){
        for(ProxiedPlayer p : BungeeCord.getInstance().getPlayers()){
            try {
                if(sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+p.getUUID()+"' AND `secondaryuser` LIKE '"+evt.getPlayer().getUUID()+"'").next()){
                    p.sendMessage(StringStandards.friendMessage("Your friend "+evt.getPlayer().getName()+" disconnected! "));
                    FriendLeaveObject frand = new FriendLeaveObject(evt.getPlayer().getName(), p.getName());
                    friendLeaveTemp.put(frand.getID(), frand);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        IOUtils.sendToOtherBungeeServers(IOStrings.encodedIOplayerLeaveMessage(evt.getPlayer().getName()));
        final String plName = evt.getPlayer().getName();
        BungeeCord.getInstance().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ArrayList<Integer> keysToRemove = new ArrayList<>();
                for(int i : friendJoinTemp.keySet()){
                    if(friendLeaveTemp.get(i).playerWhoDCd.equals(plName)){
                        keysToRemove.add(i);
                    }
                }
                for(int i : keysToRemove){
                    friendLeaveTemp.remove(i);
                }
            }
        });
    }

    public void saveConfig(){
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initConfig(){
        try {
            file = new File(getDataFolder(), "config.yml");
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getConfig() {
        return config;
    }

}
