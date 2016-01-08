package me.theyellowcreepz.friends;

import java.util.Random;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * Decompiling, copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class friendLeaveObj {

    public String playerWhoDCd = null;
    public String playerNotified = null;
    public int ID;

    public friendLeaveObj(String playerDisconnected, String playerNotifd){
        this.playerNotified = playerNotifd;
        this.playerWhoDCd = playerDisconnected;
        Random rand = new Random();
        this.ID = rand.nextInt(999999);
    }

    public int getID(){
        return this.ID;
    }

}
