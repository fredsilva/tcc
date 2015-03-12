/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author fredsilva
 */
public class ServerKryo{

    WorkflowKryo workflowKryo;
    List<WorkflowKryo> listWorkflows;
    List<ActivityKryo> activitiesKryo = new ArrayList<>();

    public ServerKryo() {
//        Log.set(Log.LEVEL_DEBUG);

        listWorkflows = new ArrayList<>();

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

                if(object instanceof String){
                    String key = (String) object;
                    if(key.equals("getKey")){                        
                        server.sendToTCP(connection.getID(), workflowKryo.getKeyWorkflow());
                    }
                }
                if (object instanceof WorkflowKryo) {
                    WorkflowKryo workflow = (WorkflowKryo) object;
                    Boolean find = false;                    
                    for (WorkflowKryo work : listWorkflows) {
                        if (workflow.getKeyWorkflow().equals(work.getKeyWorkflow())) {
                            find = true;
                            server.sendToTCP(connection.getID(), work);
                            break;
                        }
                    }
                    if (!find) {
                        listWorkflows.add(workflow);
                        System.out.println("Key in Server: " + workflow.getKeyWorkflow());                        
//                        System.out.println("List Workflows in Server: " + listWorkflows.size());
                    } else {
                        server.sendToTCP(connection.getID(), find);
                    }
                }

                if (object instanceof ActivityKryo) {
                    ActivityKryo act = (ActivityKryo) object;                    
//                    connection.sendTCP(act);                
                    if(!activityInList(act)){
                        activitiesKryo.add(act);
                        System.out.println("Recebendo Activity no servidor: " + act.getIdObject());
                        server.sendToAllExceptTCP(connection.getID(), act);                        
                    }else{
                        System.out.println("Activity já existe na lista");
                    }                    
                }

                if (object instanceof RelationKryo) {
                    RelationKryo relation = (RelationKryo) object;
                    System.out.println("Recebendo Relation no servidor: " + relation.getName());
//                    connection.sendTCP(relation);
                    server.sendToAllExceptTCP(connection.getID(), relation);
                }
                
                if(object instanceof Integer){//Envia todas as activities para a tela principal quando esta é iniciada
                    System.out.println("Recebendo um inteiro no servidor...");
                    for(ActivityKryo actKryo: activitiesKryo){
                        server.sendToTCP(connection.getID(), actKryo);
                        System.out.println("Enviando Activity da Lista para o cliente");
                    }
                    
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
    
    public Boolean activityInList(ActivityKryo activityKryo){
        for(ActivityKryo actKryo: this.activitiesKryo){
            if(actKryo.getIdObject().equals(activityKryo.getIdObject())){
                return true;
            }
        }
        return false;
    }
       

    public static void main(String[] args) {
        System.out.println("Startando servidor...");
        ServerKryo testServer = new ServerKryo();
    }
}
