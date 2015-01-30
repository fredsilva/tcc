/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

import static spark.Spark.*;
/**
 *
 * @author fredsilva
 */
public class Spark {
    public static void main(String [] args){
        get("/hello", (req, res) -> "Hello Fred");
    }
    
}
