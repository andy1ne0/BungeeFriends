package me.theyellowcreepz.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * Decompiling, copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class iomessagehandler {

    public static void handleMessage(String s){

        final String[] splitMessage = s.split("/");
        switch(splitMessage[1]){

            case "msg":
                //<editor-fold desc="msg-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[2]) != null) {
                    if (BungeeCord.getInstance().getPlayer(splitMessage[2]).getServer().getInfo().getName().equals(splitMessage[6])) {

                        BungeeCord.getInstance().getPlayer(splitMessage[2]).sendMessage(strings.messageFromPlayer(splitMessage[3], splitMessage[5]));

                        ioutils.sendToOtherBungeeServers(iostrings.encodedReceivedMessage(Integer.parseInt(splitMessage[4])));
                    } else {
                        try {

                            ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                            playerExistence.next();
                            String uuid = playerExistence.getString("uuid");
                            if(uuid.length() <= 2){
                                return;
                            }

                            ResultSet playerExistence2 = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[3]+"'");
                            playerExistence2.next();
                            String uuid2 = playerExistence2.getString("uuid");
                            if(uuid2.length() <= 2){
                                return;
                            }

                            if (Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+uuid2+"' AND `secondaryuser` LIKE '"+uuid+"'").next()) {

                                BungeeCord.getInstance().getPlayer(splitMessage[2]).sendMessage(strings.messageFromPlayer(splitMessage[3], splitMessage[5]));

                                ioutils.sendToOtherBungeeServers(iostrings.encodedReceivedMessage(Integer.parseInt(splitMessage[4])));
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //</editor-fold>
                break;

            case "receivedmsg":
                //<editor-fold desc="receivedmsg-internals">
                if(Main.messageIDsEncoded.containsKey(Integer.parseInt(splitMessage[2]))){
                    String[] splitOrigMSG = Main.messageIDsEncoded.get(Integer.parseInt(splitMessage[2])).split("/");
                    if(BungeeCord.getInstance().getPlayer(splitOrigMSG[3]) != null){

                        BungeeCord.getInstance().getPlayer(splitOrigMSG[3]).sendMessage(strings.messageToPlayer(splitOrigMSG[2], splitOrigMSG[5]));

                        Main.messageIDsEncoded.remove(Integer.parseInt(splitMessage[2]));
                    }
                }
                //</editor-fold>
                break;

            case "friendrequest":
                //<editor-fold desc="friendreq-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[3]) != null){
                    BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(strings.successMessage("You received a friend request from "+splitMessage[2]+"!"));
                    BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(strings.subInfoMSG("Type /friends add "+splitMessage[2]+" to accept the request! "));
                }
                //</editor-fold>
                break;

            case "playerjoin":
                //<editor-fold desc="playerjoin-internals">
                for(ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()){
                    try {

                        ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                        playerExistence.next();
                        String uuid = playerExistence.getString("uuid");
                        if(uuid.length() <= 2){
                            return;
                        }

                        if(Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+pl.getUUID()+"' AND `secondaryuser` LIKE '"+uuid+"'").next()){

                            boolean isMsgSentAlready = false;

                            for(int i : Main.friendJoinTemp.keySet()){

                                if(Main.friendJoinTemp.get(i).playerWhoJoined.equals(splitMessage[2]) && Main.friendJoinTemp.get(i).playerNotified.equals(pl.getName())){
                                    isMsgSentAlready = true;
                                }

                            }

                            if(!isMsgSentAlready){
                                pl.sendMessage(strings.friendMessage("Your friend "+splitMessage[2]+" joined! "));
                                friendJoinObj tempFriend = new friendJoinObj(splitMessage[2], pl.getName());
                                Main.friendJoinTemp.put(tempFriend.getID(), tempFriend);
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Integer> keysToRemove = new ArrayList<>();

                        for(int i : Main.friendJoinTemp.keySet()){
                            if(Main.friendJoinTemp.get(i).playerWhoJoined.equals(splitMessage[2])){
                                keysToRemove.add(i);
                            }
                        }

                        for(int i : keysToRemove){
                            Main.friendJoinTemp.remove(i);
                        }

                    }
                });
                //</editor-fold>
                break;

            case "playerleave":
                //<editor-fold desc="playerleave-internals">
                for(ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()){
                    try {

                        ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                        playerExistence.next();
                        String uuid = playerExistence.getString("uuid");
                        if(uuid.length() <= 2){
                            return;
                        }

                        if(Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+pl.getUUID()+"' AND `secondaryuser` LIKE '"+uuid+"'").next()){

                            boolean isMsgSentAlready = false;

                            for(int i : Main.friendLeaveTemp.keySet()){

                                if(Main.friendLeaveTemp.get(i).playerWhoDCd.equals(splitMessage[2]) && Main.friendLeaveTemp.get(i).playerNotified.equals(pl.getName())){
                                    isMsgSentAlready = true;
                                }

                            }

                            if(!isMsgSentAlready){
                                pl.sendMessage(me.theyellowcreepz.friends.strings.friendMessage("Your friend "+splitMessage[2]+" disconnected! "));
                                friendLeaveObj tempFriend = new friendLeaveObj(splitMessage[2], pl.getName());
                                Main.friendLeaveTemp.put(tempFriend.getID(), tempFriend);
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Integer> keysToRemove = new ArrayList<>();

                        for(int i : Main.friendLeaveTemp.keySet()){
                            if(Main.friendLeaveTemp.get(i).playerWhoDCd.equals(splitMessage[2])){
                                keysToRemove.add(i);
                            }
                        }

                        for(int i : keysToRemove){
                            Main.friendLeaveTemp.remove(i);
                        }

                    }
                });
                //</editor-fold>
                break;

            case "locate":
                //<editor-fold desc="locate-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[2]) != null) {
                    ioutils.sendToOtherBungeeServers(iostrings.encodedIOplayerFoundMessage(BungeeCord.getInstance().getPlayer(splitMessage[2]), Integer.parseInt(splitMessage[3]), splitMessage[4]));
                }
                //</editor-fold>
                break;

            case "foundplayer":
                //<editor-fold desc="foundplayer-internals">
                    if(Main.locatePlayerEncoded.containsKey(Integer.parseInt(splitMessage[3]))){
                        ProxiedPlayer plToTeleport = BungeeCord.getInstance().getPlayer(splitMessage[4]);

                        if(plToTeleport != null){
                            for(String server : BungeeCord.getInstance().getServers().keySet()){
                                if(splitMessage[5].equals(server)){
                                    plToTeleport.sendMessage(me.theyellowcreepz.friends.strings.successMessage("Sending you to " + server + "... "));
                                    plToTeleport.connect(BungeeCord.getInstance().getServerInfo(server));
                                    Main.locatePlayerEncoded.remove(Integer.parseInt(splitMessage[3]));
                                    return;
                                }
                            }

                        }

                    }
                //</editor-fold>
                break;

            case "requestaccept":
                //<editor-fold desc="requestaccept-internals">
                    if(BungeeCord.getInstance().getPlayer(splitMessage[3]) != null){
                        BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(me.theyellowcreepz.friends.strings.subInfoMSG(splitMessage[2]+" is now friends with you! "));
                    }
                //</editor-fold>
                break;

        }

    }

}
