package me.theyellowcreepz.friends;

import net.md_5.bungee.api.ChatColor;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class strings {

    public static String errorMessage(String msg){
        return (ChatColor.DARK_RED+""+ChatColor.BOLD+"["+ChatColor.RED+"!!!"+ChatColor.DARK_RED+""+ChatColor.BOLD+"] "+ChatColor.RED+msg);
    }

    public static String messageToPlayer(String personSendingTo, String msg){
        return (ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ">> " + ChatColor.DARK_GRAY + "Message to " + ChatColor.GRAY + personSendingTo +
                ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + msg);
    }

    public static String messageFromPlayer(String senderName, String msg){
        return (ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ">> " + ChatColor.DARK_GRAY + "Message from " + ChatColor.GRAY + senderName +
                ChatColor.DARK_GRAY + ": " + ChatColor.GRAY + msg);
    }

    public static String successMessage(String msg){
        return (ChatColor.DARK_GREEN+""+ChatColor.BOLD+"["+ChatColor.GREEN+">>"+ChatColor.DARK_GREEN+""+ChatColor.BOLD+"] "+ChatColor.GREEN+msg);
    }

    public static String informationMessage(String msg){
        return (ChatColor.DARK_BLUE+""+ChatColor.BOLD+"["+ChatColor.BLUE+">>"+ChatColor.DARK_BLUE+ChatColor.BOLD+"] "+ChatColor.AQUA+msg);
    }
    public static String subInfoMSG(String msg){
        return (ChatColor.DARK_GRAY+""+ChatColor.BOLD+"["+ChatColor.GRAY+">>"+ChatColor.DARK_GRAY+""+ChatColor.BOLD+"] "+ChatColor.GRAY+msg);
    }
    public static String halfDoneMSG(String msg){
        return (ChatColor.GOLD+""+ChatColor.BOLD+"["+ChatColor.YELLOW+">>"+ChatColor.GOLD+ChatColor.BOLD+"] "+ChatColor.GOLD+msg);
    }
    public static String friendMessage(String msg){
        return (ChatColor.GOLD+""+ChatColor.BOLD+"["+ChatColor.YELLOW+">>"+ChatColor.GOLD+ChatColor.BOLD+"] "+ChatColor.GOLD+msg);
    }

    // 【 】➤╔╚║▂▃▅▆▇═

}
