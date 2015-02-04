/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.interfaces;

import java.util.Map;
import javafx.scene.Node;

/**
 *
 * @author fredsilva
 */
public interface FormsInterface {    
    public Node getNode(Map<String, String> map);  
    public Object getController();
    public void onFinish();
}
