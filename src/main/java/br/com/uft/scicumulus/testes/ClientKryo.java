/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.testes;

import br.com.uft.scicumulus.enums.Operation;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author fredsilva
 */
public class ClientKryo {

    Client client;
    ActivityKryo activityKryo;  
    RelationKryo relationKryo;  
    List<ActivityKryo> activitiesKryo = new ArrayList<>();
    List<RelationKryo> relationsKryo = new ArrayList<>();
    
    public ClientKryo() {
//        Log.set(Log.LEVEL_DEBUG);

        client = new Client();
        CommonsNetwork.registerClientClass(client);

//        client.getKryo().setInstantiatorStrategy(new StdInstantiatorStrategy());
        ((Kryo.DefaultInstantiatorStrategy) client.getKryo().getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

        /* Kryonet > 2.12 uses Daemon threads ? */
        new Thread(client).start();

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println(connection.getRemoteAddressTCP().getHostString() + " Conectou");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActivityKryo) {                    
                    activityKryo = (ActivityKryo) object;                    
                    activitiesKryo.add(activityKryo);
                    System.out.println("Recebendo Activity no cliente: " + activityKryo.getIdObject());
                }
                
                if (object instanceof RelationKryo) {                    
                    relationKryo = (RelationKryo) object;                    
                    relationsKryo.add(relationKryo);
                    System.out.println("Recebendo Relation no cliente: " + relationKryo.getName());
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

    public void send(Object object) {                
        if (object instanceof ActivityKryo) {            
            ActivityKryo act = (ActivityKryo) object;            
            System.out.println("Enviando " + act+"...");
            client.sendTCP(act);
        }
        
        if (object instanceof RelationKryo) {
            RelationKryo relation = (RelationKryo) object;
            System.out.println("Enviando " + relation.getName()+"...");
            client.sendTCP(relation);
        }
    }
    
    public List<ActivityKryo> getActivityKryo(){ 
        List<ActivityKryo> listActKryo = activitiesKryo;
        activitiesKryo = new ArrayList<>();
        return listActKryo;
    }

//    public static void main(String[] args) {
//        new ClientKryo();
//    }
}
