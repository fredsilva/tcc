/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;

/**
 *
 * @author fredsilva
 */
public class ClientKryo {
    Client client;
 
    public ClientKryo() {
//        Log.set(Log.LEVEL_DEBUG);
 
        client = new Client();
        CommonsNetwork.registerClientClass(client);
 
        /* Kryonet > 2.12 uses Daemon threads ? */
        new Thread(client).start();
 
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {                            
                Activity act = new Activity("Activity Client");
                client.sendTCP(act);
                
            }
 
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Activity) {
                    Activity resp = (Activity) object;
                    System.out.println("Recebendo Activity no cliente: "+resp+" - "+new Date());
                }
            }
 
            @Override
            public void disconnected(Connection connection) {
            }
        });
 
        try {
            /* Make sure to connect using both tcp and udp port */
            client.connect(5000, "127.0.0.1", CommonsNetwork.TCP_PORT, CommonsNetwork.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
 
    public static void main(String[] args) {
        new ClientKryo();
    }
}
