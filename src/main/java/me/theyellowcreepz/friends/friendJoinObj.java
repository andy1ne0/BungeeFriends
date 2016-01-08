package me.theyellowcreepz.friends;

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
public class friendJoinObj {

    public String playerWhoJoined = null;
    public String playerNotified = null;
    public int ID;

    public friendJoinObj(String playerJoined, String playerNotifd){
        this.playerNotified = playerNotifd;
        this.playerWhoJoined = playerJoined;
        Random rand = new Random();
        this.ID = rand.nextInt(999999);
    }

    public int getID(){
        return this.ID;
    }

}
