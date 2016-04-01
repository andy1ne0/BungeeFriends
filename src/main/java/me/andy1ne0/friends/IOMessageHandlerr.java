package me.andy1ne0.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class IOMessageHandlerr {

    public static void handleMessage(String s){

        final String[] splitMessage = s.split("/");
        switch(splitMessage[1]){

            case "msg":
                //<editor-fold desc="msg-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[2]) != null) {
                    if (BungeeCord.getInstance().getPlayer(splitMessage[2]).getServer().getInfo().getName().equals(splitMessage[6])) {

                        BungeeCord.getInstance().getPlayer(splitMessage[2]).sendMessage(StringStandards.messageFromPlayer(splitMessage[3], splitMessage[5]));

                        IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedReceivedMessage(Integer.parseInt(splitMessage[4])));
                    } else {
                        try {

                            ResultSet playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                            playerExistence.next();
                            String uuid = playerExistence.getString("uuid");
                            if(uuid.length() <= 2){
                                return;
                            }

                            ResultSet playerExistence2 = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[3]+"'");
                            playerExistence2.next();
                            String uuid2 = playerExistence2.getString("uuid");
                            if(uuid2.length() <= 2){
                                return;
                            }

                            if (Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+uuid2+"' AND `secondaryuser` LIKE '"+uuid+"'").next()) {

                                BungeeCord.getInstance().getPlayer(splitMessage[2]).sendMessage(StringStandards.messageFromPlayer(splitMessage[3], splitMessage[5]));

                                IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedReceivedMessage(Integer.parseInt(splitMessage[4])));
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
                if(Friends.messageIDsEncoded.containsKey(Integer.parseInt(splitMessage[2]))){
                    String[] splitOrigMSG = Friends.messageIDsEncoded.get(Integer.parseInt(splitMessage[2])).split("/");
                    if(BungeeCord.getInstance().getPlayer(splitOrigMSG[3]) != null){

                        BungeeCord.getInstance().getPlayer(splitOrigMSG[3]).sendMessage(StringStandards.messageToPlayer(splitOrigMSG[2], splitOrigMSG[5]));

                        Friends.messageIDsEncoded.remove(Integer.parseInt(splitMessage[2]));
                    }
                }
                //</editor-fold>
                break;

            case "friendrequest":
                //<editor-fold desc="friendreq-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[3]) != null){
                    BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(StringStandards.successMessage("You received a friend request from "+splitMessage[2]+"!"));
                    BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(StringStandards.subInfoMSG("Type /friends add "+splitMessage[2]+" to accept the request! "));
                }
                //</editor-fold>
                break;

            case "playerjoin":
                //<editor-fold desc="playerjoin-internals">
                for(ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()){
                    try {

                        ResultSet playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                        playerExistence.next();
                        String uuid = playerExistence.getString("uuid");
                        if(uuid.length() <= 2){
                            return;
                        }

                        if(Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+pl.getUUID()+"' AND `secondaryuser` LIKE '"+uuid+"'").next()){

                            boolean isMsgSentAlready = false;

                            for(int i : Friends.friendJoinTemp.keySet()){

                                if(Friends.friendJoinTemp.get(i).playerWhoJoined.equals(splitMessage[2]) && Friends.friendJoinTemp.get(i).playerNotified.equals(pl.getName())){
                                    isMsgSentAlready = true;
                                }

                            }

                            if(!isMsgSentAlready){
                                pl.sendMessage(StringStandards.friendMessage("Your friend "+splitMessage[2]+" joined! "));
                                FriendJoinObjectr tempFriend = new FriendJoinObjectr(splitMessage[2], pl.getName());
                                Friends.friendJoinTemp.put(tempFriend.getID(), tempFriend);
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Integer> keysToRemove = new ArrayList<>();

                        for(int i : Friends.friendJoinTemp.keySet()){
                            if(Friends.friendJoinTemp.get(i).playerWhoJoined.equals(splitMessage[2])){
                                keysToRemove.add(i);
                            }
                        }

                        for(int i : keysToRemove){
                            Friends.friendJoinTemp.remove(i);
                        }

                    }
                });
                //</editor-fold>
                break;

            case "playerleave":
                //<editor-fold desc="playerleave-internals">
                for(ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()){
                    try {

                        ResultSet playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+splitMessage[2]+"'");
                        playerExistence.next();
                        String uuid = playerExistence.getString("uuid");
                        if(uuid.length() <= 2){
                            return;
                        }

                        if(Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+pl.getUUID()+"' AND `secondaryuser` LIKE '"+uuid+"'").next()){

                            boolean isMsgSentAlready = false;

                            for(int i : Friends.friendLeaveTemp.keySet()){

                                if(Friends.friendLeaveTemp.get(i).playerWhoDCd.equals(splitMessage[2]) && Friends.friendLeaveTemp.get(i).playerNotified.equals(pl.getName())){
                                    isMsgSentAlready = true;
                                }

                            }

                            if(!isMsgSentAlready){
                                pl.sendMessage(StringStandards.friendMessage("Your friend "+splitMessage[2]+" disconnected! "));
                                FriendLeaveObjectr tempFriend = new FriendLeaveObjectr(splitMessage[2], pl.getName());
                                Friends.friendLeaveTemp.put(tempFriend.getID(), tempFriend);
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ArrayList<Integer> keysToRemove = new ArrayList<>();

                        for(int i : Friends.friendLeaveTemp.keySet()){
                            if(Friends.friendLeaveTemp.get(i).playerWhoDCd.equals(splitMessage[2])){
                                keysToRemove.add(i);
                            }
                        }

                        for(int i : keysToRemove){
                            Friends.friendLeaveTemp.remove(i);
                        }

                    }
                });
                //</editor-fold>
                break;

            case "locate":
                //<editor-fold desc="locate-internals">
                if(BungeeCord.getInstance().getPlayer(splitMessage[2]) != null) {
                    IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedIOplayerFoundMessage(BungeeCord.getInstance().getPlayer(splitMessage[2]), Integer.parseInt(splitMessage[3]), splitMessage[4]));
                }
                //</editor-fold>
                break;

            case "foundplayer":
                //<editor-fold desc="foundplayer-internals">
                    if(Friends.locatePlayerEncoded.containsKey(Integer.parseInt(splitMessage[3]))){
                        ProxiedPlayer plToTeleport = BungeeCord.getInstance().getPlayer(splitMessage[4]);

                        if(plToTeleport != null){
                            for(String server : BungeeCord.getInstance().getServers().keySet()){
                                if(splitMessage[5].equals(server)){
                                    plToTeleport.sendMessage(StringStandards.successMessage("Sending you to " + server + "... "));
                                    plToTeleport.connect(BungeeCord.getInstance().getServerInfo(server));
                                    Friends.locatePlayerEncoded.remove(Integer.parseInt(splitMessage[3]));
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
                        BungeeCord.getInstance().getPlayer(splitMessage[3]).sendMessage(StringStandards.subInfoMSG(splitMessage[2]+" is now friends with you! "));
                    }
                //</editor-fold>
                break;

        }

    }

}
