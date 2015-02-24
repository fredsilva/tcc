/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import br.com.uft.scicumulus.graph.Activity;
import com.esotericsoftware.kryo.Kryo;

/**
 *
 * @author fredsilva
 */
public class CommonsNetwork {
    public static void register(Kryo kryo){
        kryo.register(Activity.class);       
    }
}
