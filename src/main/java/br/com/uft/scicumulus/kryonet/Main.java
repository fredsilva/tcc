/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import java.io.IOException;

/**
 *
 * @author fredsilva
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        ServerKryonet server = new ServerKryonet();
        server.start();
    }
}
