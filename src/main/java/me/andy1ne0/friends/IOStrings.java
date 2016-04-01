package me.andy1ne0.friends;

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
public class IOStrings {

    public static String encodedIOMessage(String playerName, ProxiedPlayer sender, String msg){
        Random rand = new Random();
        String convMSG = msg.replace("/", "");
        return (Friends.pluginAuthenticationString+"/msg/"+playerName+"/"+sender.getName()+"/"+rand.nextInt(99999999)+"/"+convMSG+"/"+sender.getServer().getInfo().getName());
    }

    public static String encodedIOfriendMessage(String requester, String requestee){
        return (Friends.pluginAuthenticationString+"/friendrequest/"+requester+"/"+requestee);
    }

    public static String encodedIOplayerJoinMessage(String joined){
        return (Friends.pluginAuthenticationString+"/playerjoin/"+joined);
    }

    public static String encodedIOplayerLeaveMessage(String disconnected){
        return (Friends.pluginAuthenticationString+"/playerleave/"+disconnected);
    }

    public static String encodedIOlocatePlayerMessage(String playerToFind, String CommandSenderUsername){
        return (Friends.pluginAuthenticationString+"/locate/"+playerToFind+"/"+new Random().nextInt(9999999)+"/"+CommandSenderUsername);
    }

    public static String encodedIOplayerFoundMessage(ProxiedPlayer playerWhoWasFound, int messageID, String playerWhoWasSearching){
        return (Friends.pluginAuthenticationString+"/foundplayer/"+playerWhoWasFound.getName()+"/"+messageID+"/"+playerWhoWasSearching+"/"+playerWhoWasFound.getServer().getInfo().getName());
    }

    public static String encodedReceivedMessage(int i){
        return (Friends.pluginAuthenticationString+"/receivedmsg/"+i);
    }

    public static String encodedIOfriendReqAcceptedMessage(String requestSender, String requestReceiver){
        return (Friends.pluginAuthenticationString+"/requestaccept/"+requestSender+"/"+requestReceiver);
    }

}
