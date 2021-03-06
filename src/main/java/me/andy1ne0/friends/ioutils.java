package me.andy1ne0.friends;

import net.md_5.bungee.BungeeCord;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class IOUtils {

    public static void sendToOtherBungeeServers(final String data){

        if(!Friends.getConfig().getBoolean("usingMultipleBungees")) return;
        BungeeCord.getInstance().getScheduler().runAsync(Friends.getInstance(), new Runnable(){

            @Override
            public void run() {
                Socket client;
                for(InetSocketAddress s : Friends.otherServers) {
                    try {
                        client = new Socket(s.getAddress(), s.getPort());
                        DataOutputStream ds = new DataOutputStream(client.getOutputStream());
                        ds.writeUTF(data);
                        ds.close();
                        client.close();
                        Friends.outgoingconnectionsInLast5minutes++;
                    } catch (SocketException e){
                        BungeeCord.getInstance().getLogger().warning("[ Friends ] Could not connect to Bungee Instance "+s.getAddress()+":"+s.getPort()+". It is likely mis-configured, or not running. ");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

    }

}
