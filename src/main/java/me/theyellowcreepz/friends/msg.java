package me.theyellowcreepz.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.scheduler.ScheduledTask;

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
public class msg extends Command{
    public msg() {
        super("msg");
    }

    public void cancelTask(ScheduledTask task){
        task.cancel();
    }

    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {

        if(commandSender instanceof ProxiedPlayer){

            switch (strings.length){

                case 0:

                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a player and message! "));

                    break;

                case 1:

                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a message! "));

                    break;

                default:

                    try {
                        String[] strings1 = strings;

                        if (!Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+strings[0]+"'").next()){

                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                            return;

                        } else {
                            int i = 0;
                            ArrayList<String> msgWords = new ArrayList<>();

                            for(String s : strings1){
                                if(i < 1){
                                    i++;
                                } else {
                                    msgWords.add(s);
                                    continue;
                                }
                            }

                            StringBuilder msg = new StringBuilder();
                            for (String s : msgWords){
                                msg.append(s+" ");
                            }

                            if(BungeeCord.getInstance().getPlayer(strings[0]) != null){

                                ResultSet playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+strings[0]+"'");
                                playerExistence.next();
                                String uuid = playerExistence.getString("uuid");
                                if(uuid.length() <= 2){
                                    return;
                                }

                                if(BungeeCord.getInstance().getPlayer(strings[0]).getServer().getInfo().getName() == ((ProxiedPlayer) commandSender).getServer().getInfo().getName()) {

                                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.messageToPlayer(strings[0], msg.toString()));

                                    BungeeCord.getInstance().getPlayer(strings[0]).sendMessage(me.theyellowcreepz.friends.strings.messageFromPlayer(commandSender.getName(), msg.toString()));

                                } else if(Main.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `secondaryuser` LIKE '"+uuid+"'").next()) {

                                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.messageToPlayer(strings[0], msg.toString()));

                                    BungeeCord.getInstance().getPlayer(strings[0]).sendMessage(me.theyellowcreepz.friends.strings.messageFromPlayer(commandSender.getName(), msg.toString()));

                                } else {
                                    BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(500);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.halfDoneMSG("Searching for that player..."));
                                            try {
                                                Thread.sleep(1500);
                                            } catch(InterruptedException e){
                                                e.printStackTrace();
                                            }
                                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("You're not friends with that player, or they aren't online! "));
                                        }
                                    });
                                    return;
                                }

                            } else {

                                String encoded = iostrings.encodedIOMessage(strings[0], (ProxiedPlayer)commandSender, msg.toString());
                                String[] gettingIDp1 = encoded.split("/");
                                String integerid = gettingIDp1[4];
                                final int msgID = Integer.parseInt(integerid);
                                Main.messageIDsEncoded.put(msgID, encoded);

                                ioutils.sendToOtherBungeeServers(encoded);

                                BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (Main.messageIDsEncoded.containsKey(msgID)) {

                                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.halfDoneMSG("Searching for that player..."));

                                        }
                                        try {
                                            Thread.sleep(1500);
                                        } catch(InterruptedException e){
                                            e.printStackTrace();
                                        }
                                        if (Main.messageIDsEncoded.containsKey(msgID)) {
                                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("You're not friends with that player, or they aren't online! "));
                                            Main.messageIDsEncoded.remove(msgID);
                                        }
                                    }
                                });

                            }
                        }

                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                    break;
            }

            return;
        } else {
            commandSender.sendMessage("[ !!! ] You must be a player to do that! ");
        }

    }
}
