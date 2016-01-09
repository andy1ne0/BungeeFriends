package me.theyellowcreepz.friends;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
public class blocksmanager extends Command {

    public blocksmanager(){
        super("blocks");
    }

    public void sendBlockHelp(ProxiedPlayer p){
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"----------["+ChatColor.GOLD+"Blocks Guide"+ ChatColor.AQUA+""+ChatColor.BOLD+"]----------");
        p.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"/blocks help "+ChatColor.DARK_AQUA+"(This Guide)");
        p.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"/blocks add <Username> "+ChatColor.DARK_AQUA+"(Add Blocks)");
        p.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"/blocks remove <Username> "+ChatColor.DARK_AQUA+"(Delete Blocks)");
        p.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"/blocks list "+ChatColor.DARK_AQUA+"(List Blocks)");
        p.sendMessage(ChatColor.DARK_GRAY+""+ChatColor.BOLD+">> "+ChatColor.GRAY+""+ChatColor.ITALIC+"Blocked players cannot /msg you, and cannot send friend requests to you. ");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

        if(!(commandSender instanceof ProxiedPlayer)){
            commandSender.sendMessage("[!!!] Please run this as a player! ");
            return;
        }

        if(strings.length < 1){
            sendBlockHelp((ProxiedPlayer)commandSender);
            return;
        }

        switch (strings[0].toLowerCase()){

            case "help":
                //<editor-fold desc="help-internals">
                sendBlockHelp((ProxiedPlayer)commandSender);
                //</editor-fold>
                break;

            case "list":
                //<editor-fold desc="list-internals">
                try {
                    ResultSet getPlayers = Main.sql.getFromDB("SELECT * FROM `blocks` WHERE `blocker` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "'");

                    int blockcount = 0;
                    ArrayList<String> blocks = new ArrayList<>();

                    while (getPlayers.next()){
                        blockcount++;
                        ResultSet getPlayerName = Main.sql.getFromDB("SELECT * FROM `players` WHERE `uuid` LIKE '"+getPlayers.getString("blockee")+"'");
                        getPlayerName.next();
                        blocks.add(getPlayerName.getString("username"));
                    }

                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.informationMessage("You have "+ChatColor.GOLD+blockcount+ChatColor.AQUA+" blocks: "));
                    for(String s : blocks){
                        commandSender.sendMessage(ChatColor.GOLD+">> "+ChatColor.AQUA+s);
                    }
                    return;

                } catch (SQLException e){
                    e.printStackTrace();
                }
                //</editor-fold>
                break;

            case "add":
                //<editor-fold desc="add-internals">

                if(strings.length == 1){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a player to block! "));
                    return;
                }
                if(strings.length >= 3){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Too many arguments! Use /blocks help. "));
                    return;
                } else {

                    //<editor-fold desc="Deleting relationships.">
                    ResultSet playerExistence = null;
                    String uuid = null;
                    try {
                        playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
                        if (playerExistence.next()) {
                            uuid = playerExistence.getString("uuid");
                            if (uuid.length() <= 2) {
                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                                return;
                            }
                        } else {
                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `relationships`.`secondaryuser` = '" + uuid + "'");
                    Main.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '" + uuid + "' AND `relationships`.`secondaryuser` = '" + ((ProxiedPlayer) commandSender).getUUID() + "'");
                    //</editor-fold>

                    Main.sql.submitQuery("INSERT INTO `blocks` (`blocker`, `blockee`) VALUES ('" + ((ProxiedPlayer) commandSender).getUUID() + "', '" + uuid + "');");

                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.successMessage("You have successfully blocked " + strings[1] + "!"));
                }
                //</editor-fold>
                break;

            //<editor-fold desc="Deletion of blocks. ">
            case "delete":
                //<editor-fold desc="delete-internals">
                if(strings.length == 1){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a player to unblock! "));
                    return;
                }
                if(strings.length >= 3){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Too many arguments! Use /blocks help. "));
                    return;
                } else {
                    //<editor-fold desc="Getting UUIDs. ">
                    ResultSet playerExistence = null;
                    String uuid = null;
                    try {
                        playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
                        if (playerExistence.next()) {
                            uuid = playerExistence.getString("uuid");
                            if (uuid.length() <= 2) {
                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                                return;
                            }
                        } else {
                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //</editor-fold>

                    Main.sql.submitQuery("DELETE FROM `blocks` WHERE `blocker` = '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `blockee` = '" + uuid + "'");
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.successMessage("You have successfully unblocked that player! "));
                }
                //</editor-fold>
                break;

            case "remove":
                //<editor-fold desc="remove-internals">
                if(strings.length == 1){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a player to unblock! "));
                    return;
                }
                if(strings.length >= 3){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Too many arguments! Use /blocks help. "));
                    return;
                } else {
                    //<editor-fold desc="Getting UUIDs. ">
                    ResultSet playerExistence = null;
                    String uuid = null;
                    try {
                        playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
                        if (playerExistence.next()) {
                            uuid = playerExistence.getString("uuid");
                            if (uuid.length() <= 2) {
                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                                return;
                            }
                        } else {
                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //</editor-fold>

                    Main.sql.submitQuery("DELETE FROM `blocks` WHERE `blocker` = '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `blockee` = '" + uuid + "'");
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.successMessage("You have successfully unblocked that player! "));
                }
                //</editor-fold>
                break;

            case "unblock":
                //<editor-fold desc="unblock-internals">
                if(strings.length == 1){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Please specify a player to unblock! "));
                    return;
                }
                if(strings.length >= 3){
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("Too many arguments! Use /blocks help. "));
                    return;
                } else {
                    //<editor-fold desc="Getting UUIDs. ">
                    ResultSet playerExistence = null;
                    String uuid = null;
                    try {
                        playerExistence = Main.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'");
                        if (playerExistence.next()) {
                            uuid = playerExistence.getString("uuid");
                            if (uuid.length() <= 2) {
                                commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                                return;
                            }
                        } else {
                            commandSender.sendMessage(me.theyellowcreepz.friends.strings.errorMessage("That player doesn't exist! "));
                            return;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //</editor-fold>

                    Main.sql.submitQuery("DELETE FROM `blocks` WHERE `blocker` = '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `blockee` = '" + uuid + "'");
                    commandSender.sendMessage(me.theyellowcreepz.friends.strings.successMessage("You have successfully unblocked that player! "));
                }
                //</editor-fold>
                break;
            //</editor-fold>

        }

    }
}
