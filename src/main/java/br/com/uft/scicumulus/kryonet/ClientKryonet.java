/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

/**
 *
 * @author fredsilva
 */
public class ClientKryonet extends Listener{
    
    public void start() throws IOException{
        Client client = new Client();
        client.addListener(this);
        CommonsNetwork.register(client.getKryo());
        client.connect(500, "localhost", ServerKryonet.port);       
    }
    
    public static void main(String[] args) throws IOException {
        ClientKryonet client = new ClientKryonet();
        client.start();
    }
}
