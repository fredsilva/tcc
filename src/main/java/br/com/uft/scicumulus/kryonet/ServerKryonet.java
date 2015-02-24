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
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Frederico da Silva Santos
 */
public class ServerKryonet extends Listener{

    public static int port = 54555;
    
    public void start() throws IOException{        
        Server server = new Server();
        server.addListener(this);
        server.bind(port);
        CommonsNetwork.register(server.getKryo());
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
