package me.theyellowcreepz.friends;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Random;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class iostrings {

    public static String encodedIOMessage(String playerName, ProxiedPlayer sender, String msg){
        Random rand = new Random();
        String convMSG = msg.replace("/", "");
        return (Main.pluginAuthenticationString+"/msg/"+playerName+"/"+sender.getName()+"/"+rand.nextInt(99999999)+"/"+convMSG+"/"+sender.getServer().getInfo().getName());
    }

    public static String encodedIOfriendMessage(String requester, String requestee){
        return (Main.pluginAuthenticationString+"/friendrequest/"+requester+"/"+requestee);
    }

    public static String encodedIOplayerJoinMessage(String joined){
        return (Main.pluginAuthenticationString+"/playerjoin/"+joined);
    }

    public static String encodedIOplayerLeaveMessage(String disconnected){
        return (Main.pluginAuthenticationString+"/playerleave/"+disconnected);
    }

    public static String encodedIOlocatePlayerMessage(String playerToFind, String CommandSenderUsername){
        return (Main.pluginAuthenticationString+"/locate/"+playerToFind+"/"+new Random().nextInt(9999999)+"/"+CommandSenderUsername);
    }

    public static String encodedIOplayerFoundMessage(ProxiedPlayer playerWhoWasFound, int messageID, String playerWhoWasSearching){
        return (Main.pluginAuthenticationString+"/foundplayer/"+playerWhoWasFound.getName()+"/"+messageID+"/"+playerWhoWasSearching+"/"+playerWhoWasFound.getServer().getInfo().getName());
    }

    public static String encodedReceivedMessage(int i){
        return (Main.pluginAuthenticationString+"/receivedmsg/"+i);
    }

    public static String encodedIOfriendReqAcceptedMessage(String requestSender, String requestReceiver){
        return (Main.pluginAuthenticationString+"/requestaccept/"+requestSender+"/"+requestReceiver);
    }

}
