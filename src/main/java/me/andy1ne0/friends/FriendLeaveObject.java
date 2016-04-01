package me.andy1ne0.friends;

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
public class FriendLeaveObject {

    public String playerWhoDCd = null;
    public String playerNotified = null;
    public int ID;

    public FriendLeaveObject(String playerDisconnected, String playerNotifd){
        this.playerNotified = playerNotifd;
        this.playerWhoDCd = playerDisconnected;
        Random rand = new Random();
        this.ID = rand.nextInt(999999);
    }

    public int getID(){
        return this.ID;
    }

}
