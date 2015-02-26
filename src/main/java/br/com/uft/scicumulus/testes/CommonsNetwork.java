/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.testes;

//import br.com.uft.scicumulus.graph.Entity;
//import br.com.uft.scicumulus.graph.Activity;
//import br.com.uft.scicumulus.graph.Field;
//import br.com.uft.scicumulus.graph.Relation;
//import br.com.uft.scicumulus.graph.Shape;
import br.com.uft.scicumulus.enums.Operation;
import com.esotericsoftware.kryo.Kryo;
//import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import java.util.ArrayList;
import java.util.List;
//import javafx.scene.layout.StackPane;

/**
 *
 * @author fredsilva
 */
public class CommonsNetwork {

    public static final int TCP_PORT = 55223;
    public static final int UDP_PORT = 55224;

    public static void registerServerClasses(Server server) {
        register(server.getKryo());
    }

    public static void registerClientClass(Client client) {
        register(client.getKryo());
    }

    private static void register(Kryo kryo) {
        kryo.register(ShapeKryo.class);
        kryo.register(ActivityKryo.class);
        kryo.register(RelationKryo.class);
        kryo.register(FieldKryo.class);
        kryo.register(Operation.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.Arrays.class);
        kryo.register(List.class);
        kryo.register(int.class);
        kryo.register(String.class);
        kryo.register(Object.class);                
    }
}
