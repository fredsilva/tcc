/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.kryonet;

import br.com.uft.scicumulus.enums.Operation;
import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Relation;
import static br.com.uft.scicumulus.kryonet.ClientKryo.workflowKryo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import controllers.FXMLScicumulusController;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 *
 * @author Frederico da Silva Santos
 */
public class ClientKryo extends Listener {

    Client client;
    ActivityKryo activityKryo;
    RelationKryo relationKryo;
    public static WorkflowKryo workflowKryo = new WorkflowKryo();
    List<ActivityKryo> activitiesKryo = new ArrayList<>();
    List<RelationKryo> relationsKryo = new ArrayList<>();
    Boolean workflowExist = false;
    FXMLScicumulusController controller;

    public ClientKryo(Object controller) {
//        Log.set(Log.LEVEL_DEBUG);

        this.controller = (FXMLScicumulusController) controller;

        client = new Client();

        CommonsNetwork.registerClientClass(client);

        ((Kryo.DefaultInstantiatorStrategy) client.getKryo().getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

//        new Thread(client).start();
        start();
        try {
            /* Make sure to connect using both tcp and udp port */
            client.connect(5000, "127.0.0.1", CommonsNetwork.TCP_PORT, CommonsNetwork.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void start() {
        client.start();
        client.addListener(this);
    }

    @Override
    public void connected(Connection connection) {
        System.out.println(connection.getRemoteAddressTCP().getHostString() + " Conectou");
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof Boolean) {
            Boolean find = (Boolean) object;
            this.workflowKryo.setExist(find);
            this.controller.isWorkflowExist(find);
        }

        if (object instanceof WorkflowKryo) {
            WorkflowKryo workflowKryo = (WorkflowKryo) object;
            this.controller.getWorkflowKryo(workflowKryo);
        }

        if (object instanceof ActivityKryo) {
            activityKryo = (ActivityKryo) object;
            activitiesKryo.add(activityKryo);
            System.out.println("Recebendo Activity no cliente: " + activityKryo.getIdObject());
            Activity activity;
            try {
                activity = new Activity().convert(activityKryo);
                if (activityKryo.getOperation().equals(Operation.INSERT)) {
                    //Insere activity                                                        
                    controller.activities.add(activity);
                    //Atualiza a Interface
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            controller.getPaneGraph().getChildren().add(activity);
                            controller.enableObject(activity);
                            controller.activateAccProperties();
                        }
                    });
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (object instanceof RelationKryo) {
            relationKryo = (RelationKryo) object;
            relationsKryo.add(relationKryo);
            Relation relation;
            try {
                relation = new Relation().convert(relationKryo);
                System.out.println("Recebendo Relation no cliente: " + relation.getIdObject());
                //Atualiza a Interface
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.getPaneGraph().getChildren().add(relation);
                        controller.activateAccProperties();
                    }
                });
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Client Disconnected");
    }

    public void send(Object object) {
        this.client.sendTCP(object);
    }

    public Boolean getWorkflowKryo() {
        return this.workflowExist;
    }
}
