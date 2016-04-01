package me.andy1ne0.friends;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

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
public class FriendsCommand extends Command {
    public FriendsCommand() {
        super("friends");
    }

    public void sendHelpMessages(ProxiedPlayer p){
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"╔═══════════["+ChatColor.GOLD+"Friends Guide"+ ChatColor.AQUA+""+ChatColor.BOLD+"]═══════════");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.YELLOW+""+ChatColor.BOLD+"/friends help "+ChatColor.DARK_AQUA+"【This Guide】");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.YELLOW+""+ChatColor.BOLD+"/friends add <Username> "+ChatColor.DARK_AQUA+"【Add Friends】");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.YELLOW+""+ChatColor.BOLD+"/friends remove <Username> "+ChatColor.DARK_AQUA+"【Delete Friends】");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.YELLOW+""+ChatColor.BOLD+"/friends list "+ChatColor.DARK_AQUA+"【List Friends】");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.YELLOW+""+ChatColor.BOLD+"/friends goto <Username> "+ChatColor.DARK_AQUA+"【Teleport to your Friends】");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"║ "+ChatColor.DARK_GRAY+""+ChatColor.BOLD+">> "+ChatColor.GRAY+""+ChatColor.ITALIC+"Use /msg to send messages to your friends! ");
        p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"╚══════════════════════════════");
    }

    @Override
    public void execute(final CommandSender commandSender, String[] strings) {


        if(commandSender instanceof ProxiedPlayer){

            if(strings.length >= 1){

                switch (strings[0].toLowerCase()){

                    case "help":
                        //<editor-fold desc="help-internals">

                        sendHelpMessages((ProxiedPlayer) commandSender);

                        //</editor-fold>
                        break;

                    case "list":
                        //<editor-fold desc="internals-list">

                        try {
                            ResultSet getPlayers = Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "'");

                            int friendscount = 0;
                            ArrayList<String> friends = new ArrayList<>();

                            while (getPlayers.next()){
                                friendscount++;
                                ResultSet getPlayerName = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `uuid` LIKE '"+getPlayers.getString("secondaryuser")+"'");
                                getPlayerName.next();
                                friends.add(getPlayerName.getString("username"));
                            }

                            if(friendscount == 0){
                                commandSender.sendMessage(StringStandards.errorMessage("You don't have any friends! "));
                                commandSender.sendMessage(StringStandards.subInfoMSG("Use /friends add to add friends. "));
                                return;
                            }

                            commandSender.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+"╔══════════════════════════════");
                            commandSender.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+"║ "+ StringStandards.informationMessage("You have "+ChatColor.GOLD+friendscount+ChatColor.AQUA+" friends: "));
                            for(String s : friends){
                                commandSender.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+"║ "+ChatColor.GOLD+"➤ "+ChatColor.AQUA+s);
                            }
                            commandSender.sendMessage(ChatColor.DARK_GREEN+""+ChatColor.BOLD+"╚══════════════════════════════");
                            return;

                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                        //</editor-fold>
                        break;

                    case "add":
                        //<editor-fold desc="internals-add">
                        if(strings.length == 2) {

                            try {
                                // if the 1st arg exists
                                ResultSet ifPlayerExists = null;

                                ifPlayerExists = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '" + strings[1] + "'"); // Checking if player is in DB.


                                if (ifPlayerExists.next()) { // If a value was returned.
                                    String requestedUUID = ifPlayerExists.getString("uuid");

                                    ResultSet isBlocked = Friends.sql.getFromDB("SELECT * FROM `blocks` WHERE `blocker` LIKE '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `blockee` LIKE '"+requestedUUID+"'");
                                    ResultSet isBlocked2 = Friends.sql.getFromDB("SELECT * FROM `blocks` WHERE `blocker` LIKE '"+requestedUUID+"' AND `blockee` LIKE '"+((ProxiedPlayer) commandSender).getUUID()+"'");

                                    if(isBlocked.next() || isBlocked2.next()){
                                        commandSender.sendMessage(StringStandards.errorMessage("Either you or "+strings[1]+" have blocked one another! "));
                                        return;
                                    }

                                    ResultSet results = Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `secondaryuser` LIKE '" + requestedUUID + "'");
                                    ResultSet results2 = Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + requestedUUID + "' AND `secondaryuser` LIKE '" + ((ProxiedPlayer) commandSender).getName() + "'");
                                    // Checking if the users have a relationship yet.

                                    if (!results.next() && !results2.next()) {
                                        // if no relations exist

                                        ResultSet hasRequested = Friends.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + requestedUUID + "' AND `requestee` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "'");

                                        ResultSet isAccepting = Friends.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + ((ProxiedPlayer) commandSender).getUUID()+ "' AND `requestee` LIKE '" + requestedUUID + "'");

                                        if (hasRequested.next()) {
                                            // if they've already sent a request

                                            if(isAccepting.next()){
                                                // if the other person has already sent a request, fallback to accepting.
                                                Friends.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('"+((ProxiedPlayer) commandSender).getUUID()+"', '"+requestedUUID+"');");
                                                Friends.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('"+requestedUUID+"', '"+((ProxiedPlayer) commandSender).getUUID()+"');");
                                                Friends.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `pendingrequests`.`requestee` = '"+requestedUUID+"'");
                                                Friends.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '"+requestedUUID+"' AND `pendingrequests`.`requestee` = '"+((ProxiedPlayer) commandSender).getUUID()+"'");
                                                commandSender.sendMessage(StringStandards.successMessage("You are now friends with "+strings[1]+"!"));
                                                if(BungeeCord.getInstance().getPlayer(strings[1]) != null){
                                                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(StringStandards.subInfoMSG(commandSender.getName()+" is now friends with you! "));
                                                } else {
                                                    IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedIOfriendReqAcceptedMessage(commandSender.getName(), strings[1]));
                                                }
                                                return;
                                            } else {
                                                commandSender.sendMessage(StringStandards.errorMessage("You've already sent a friend request to that player! "));
                                                return;
                                            }

                                        } else {
                                            Friends.sql.submitQuery("INSERT IGNORE INTO `pendingrequests` (`requested`, `requestee`) VALUES ('"+requestedUUID+"', '"+ ((ProxiedPlayer) commandSender).getUUID()+"');");

                                            ResultSet hasRequested2 = Friends.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + requestedUUID + "' AND `requestee` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "'");

                                            ResultSet isAccepting2 = Friends.sql.getFromDB("SELECT * FROM `pendingrequests` WHERE `requested` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `requestee` LIKE '" + requestedUUID + "'");

                                            if(hasRequested2.next() && isAccepting2.next()) {
                                                Friends.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('"+((ProxiedPlayer) commandSender).getUUID()+"', '"+requestedUUID+"');");
                                                Friends.sql.submitQuery("INSERT IGNORE INTO `relationships` (`primaryuser`, `secondaryuser`) VALUES ('"+requestedUUID+"', '"+((ProxiedPlayer) commandSender).getUUID()+"');");
                                                Friends.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `pendingrequests`.`requestee` = '"+requestedUUID+"'");
                                                Friends.sql.submitQuery("DELETE FROM `pendingrequests` WHERE `pendingrequests`.`requested` = '"+requestedUUID+"' AND `pendingrequests`.`requestee` = '"+((ProxiedPlayer) commandSender).getUUID()+"'");
                                                commandSender.sendMessage(StringStandards.successMessage("You are now friends with "+strings[1]+"!"));
                                                if(BungeeCord.getInstance().getPlayer(strings[1]) != null){
                                                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(StringStandards.subInfoMSG(commandSender.getName()+" is now friends with you! "));
                                                } else {
                                                    IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedIOfriendReqAcceptedMessage(commandSender.getName(), strings[1]));
                                                }
                                                return;
                                            } else {
                                                commandSender.sendMessage(StringStandards.successMessage("You've sent a friend request to " + strings[1] + "! "));
                                                if(BungeeCord.getInstance().getPlayer(strings[1]) != null){
                                                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(StringStandards.successMessage("You received a friend request from "+commandSender.getName()+"!"));
                                                    BungeeCord.getInstance().getPlayer(strings[1]).sendMessage(StringStandards.subInfoMSG("Type /friends add "+commandSender.getName()+" to accept the request! "));
                                                } else {
                                                    IOUtilsr.sendToOtherBungeeServers(IOStringsr.encodedIOfriendMessage(commandSender.getName(), strings[1]));
                                                }
                                                return;
                                            }
                                        }
                                    } else {
                                        commandSender.sendMessage(StringStandards.errorMessage("You're already friends with that player! "));
                                    }

                                } else {
                                    commandSender.sendMessage(StringStandards.errorMessage("That player hasn't logged in to the server! "));
                                }

                            } catch (SQLException e){
                                e.printStackTrace();
                        }
                    } else {
                            commandSender.sendMessage(StringStandards.errorMessage("Please specify a player! "));
                        }
                        //</editor-fold>
                        break;

                    case "remove":
                        //<editor-fold desc="internals-remove">

                            if(strings.length == 2){
                                ResultSet playerExistence = null;
                                String uuid = null;
                                try {
                                    playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+strings[1]+"'");
                                    if(playerExistence.next()) {
                                        uuid = playerExistence.getString("uuid");
                                        if (uuid.length() <= 2) {
                                            commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                            return;
                                        }
                                    } else {
                                        commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                        return;
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                Friends.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `relationships`.`secondaryuser` = '"+uuid+"'");
                                Friends.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '"+uuid+"' AND `relationships`.`secondaryuser` = '"+((ProxiedPlayer) commandSender).getUUID()+"'");
                                commandSender.sendMessage(StringStandards.successMessage("That player was deleted from your friends list! "));

                            } else {
                                commandSender.sendMessage(StringStandards.errorMessage("Please specify a player! "));
                            }

                        //</editor-fold>
                        break;

                    case "delete":
                        //<editor-fold desc="internals-delete">

                        if(strings.length == 2){
                            ResultSet playerExistence = null;
                            String uuid = null;
                            try {
                                playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+strings[1]+"'");
                                if(playerExistence.next()) {
                                    uuid = playerExistence.getString("uuid");
                                    if (uuid.length() <= 2) {
                                        commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                        return;
                                    }
                                } else {
                                    commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                    return;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            Friends.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '"+((ProxiedPlayer) commandSender).getUUID()+"' AND `relationships`.`secondaryuser` = '"+uuid+"'");
                            Friends.sql.submitQuery("DELETE FROM `relationships` WHERE `relationships`.`primaryuser` = '"+uuid+"' AND `relationships`.`secondaryuser` = '"+((ProxiedPlayer) commandSender).getUUID()+"'");
                            commandSender.sendMessage(StringStandards.successMessage("That player was deleted from your friends list! "));

                        } else {
                            commandSender.sendMessage(StringStandards.errorMessage("Please specify a player! "));
                        }

                        //</editor-fold>
                        break;

                    case "goto":
                        //<editor-fold desc="internals-goto">

                        if(strings.length == 2){
                            try {
                                String uuid = null;
                                ResultSet playerExistence = Friends.sql.getFromDB("SELECT * FROM `players` WHERE `username` LIKE '"+strings[1]+"'");
                                if(playerExistence.next()) {
                                    uuid = playerExistence.getString("uuid");
                                    if (uuid.length() <= 2) {
                                        commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                        return;
                                    }
                                } else {
                                    commandSender.sendMessage(StringStandards.errorMessage("That player doesn't exist! "));
                                    return;
                                }

                                ResultSet isFriend1 = Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "' AND `secondaryuser` LIKE '" + uuid + "'");
                                ResultSet isFriend2 = Friends.sql.getFromDB("SELECT * FROM `relationships` WHERE `primaryuser` LIKE '" + uuid + "' AND `secondaryuser` LIKE '" + ((ProxiedPlayer) commandSender).getUUID() + "'");

                                if (isFriend1.next() || isFriend2.next()) {
                                    commandSender.sendMessage(StringStandards.halfDoneMSG("Searching for " + strings[1] + "... "));

                                    boolean found = false;
                                    Server server = null;

                                    for (ProxiedPlayer pl : BungeeCord.getInstance().getPlayers()) {
                                        if (pl.getName().equalsIgnoreCase(strings[1])) {
                                            server = pl.getServer();
                                            found = true;
                                        }
                                    }

                                    if (found) {

                                        commandSender.sendMessage(StringStandards.successMessage("Sending you to " + server.getInfo().getName() + "... "));
                                        ((ProxiedPlayer) commandSender).connect(server.getInfo());
                                        return;

                                    } else {

                                        String encodedLocMessage = IOStringsr.encodedIOlocatePlayerMessage(strings[1], commandSender.getName());
                                        final int locateID = Integer.parseInt(encodedLocMessage.split("/")[3]);
                                        Friends.locatePlayerEncoded.put(locateID, commandSender.getName());

                                        final String[] stringsFinal = strings;

                                        IOUtilsr.sendToOtherBungeeServers(encodedLocMessage);

                                        BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(2000);
                                                } catch(InterruptedException e){
                                                    e.printStackTrace();
                                                }
                                                if (Friends.locatePlayerEncoded.containsKey(locateID)) {
                                                    commandSender.sendMessage(StringStandards.errorMessage("Could not find " + stringsFinal[1] + "! "));
                                                    Friends.locatePlayerEncoded.remove(locateID);
                                                }
                                            }
                                        });

                                    }

                                } else {
                                    commandSender.sendMessage(StringStandards.errorMessage("You're not friends with that player! "));
                                }

                            } catch (SQLException e){
                                e.printStackTrace();
                            }
                        } else {
                            commandSender.sendMessage(StringStandards.errorMessage("Please specify a player! "));
                        }

                        //</editor-fold>
                        break;

                    default:
                        commandSender.sendMessage(StringStandards.errorMessage("Invalid usage! "));
                        commandSender.sendMessage(StringStandards.subInfoMSG("Use /friends to view help. "));
                    break;
                }

            } else {
                sendHelpMessages((ProxiedPlayer) commandSender);
            }



        } else {
            commandSender.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+">> "+ChatColor.GREEN+"Use "+ChatColor.BOLD+"/friends "+ChatColor.RESET+ChatColor.GREEN+" as a player! ");
        }


    }
}
