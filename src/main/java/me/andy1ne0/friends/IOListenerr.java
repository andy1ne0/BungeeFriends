package me.andy1ne0.friends;

import net.md_5.bungee.BungeeCord;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * All code inside this plugin is the
 * property of Andrew Petersen.
 * copying, or claiming any
 * code from this plugin as your own
 * voids any legal rights you have to
 * this plugin, unless express permission
 * is given.
 */
public class IOListenerr implements Runnable {

    Socket client;

    public void run(){
        while(true) {
            try {
                client = Friends.ioSock.accept();
                DataInputStream dis = new DataInputStream(client.getInputStream());
                String data = dis.readUTF();
                if (data.contains(Friends.pluginAuthenticationString)) {
                    IOMessageHandlerr.handleMessage(data);
                    Friends.connectionsInLast5minutes++;
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
