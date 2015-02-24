/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author fredsilva
 */
    public class ServerKryo {
     
        public ServerKryo() {
//            Log.set(Log.LEVEL_DEBUG);
     
            Server server = new Server();
            CommonsNetwork.registerServerClasses(server);
     
            server.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    System.out.println("Conectado");
                }
     
                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Desconectado");
                }
     
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Activity) {
                        Activity act = (Activity) object;
                        System.out.println("Recebendo Activity no servidor: "+act+" - "+new Date());                        
                        connection.sendTCP(act);
//                        connection.sendUDP(new TestObjectResponse(act));
                    }                    
                }
            });
     
            try {
                server.bind(CommonsNetwork.TCP_PORT, CommonsNetwork.UDP_PORT);
            } catch (IOException ex) {
                System.out.println(ex);
            }
     
            server.start();
        }
     
        public static void main(String[] args) {
            System.out.println("Startando servidor...");
            ServerKryo testServer = new ServerKryo();
        }
    }


