/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.testes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author fredsilva
 */
public class ServerKryo {

    public ServerKryo() {
//            Log.set(Log.LEVEL_DEBUG);

        Server server = new Server();
        CommonsNetwork.registerServerClasses(server);

//            server.getKryo().setInstantiatorStrategy(new StdInstantiatorStrategy());
        ((Kryo.DefaultInstantiatorStrategy) server.getKryo().getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

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
                if (object instanceof ActivityKryo) {
                    ActivityKryo act = (ActivityKryo) object;
                    System.out.println("Recebendo Activity no servidor: " + act.getIdObject());
                        connection.sendTCP(act);                        
//                    server.sendToAllExceptTCP(connection.getID(), act);
                }

                if (object instanceof RelationKryo) {
                    RelationKryo relation = (RelationKryo) object;
                    System.out.println("Recebendo Relation no servidor: " + relation.getName());
                    connection.sendTCP(relation);
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
