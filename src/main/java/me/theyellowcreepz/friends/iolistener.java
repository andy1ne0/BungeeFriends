package me.theyellowcreepz.friends;

import net.md_5.bungee.BungeeCord;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * Decompiling, copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class iolistener implements Runnable {

    Socket client;

    public void run(){
        while(true) {
            try {
                //init the client
                client = Main.ioSock.accept();
                //Read the data
                DataInputStream dis = new DataInputStream(client.getInputStream());
                String data = dis.readUTF();

                if (data.contains(Main.pluginAuthenticationString)) {

                    iomessagehandler.handleMessage(data);
                    Main.connectionsInLast5minutes++;

                } else {
                    BungeeCord.getInstance().getLogger().warning("IP address " + client.getRemoteSocketAddress() + " attempted a counterfeit plugin message: ");
                    BungeeCord.getInstance().getLogger().warning(data);
                }

                dis.close();
            } catch (IOException e) {

            } catch (NullPointerException e){

            }
        }
    }

}
