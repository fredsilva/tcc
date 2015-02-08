/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.interfaces;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Field;
import java.util.Map;
import javafx.scene.Node;

/**
 *
 * @author fredsilva
 */
public interface FormsInterface {    
    public Node getNode(Map<String, String> map1, Map<String, String> map2);  
    public Object getController();
    public Map<String, String> getOperation();
    public Map<String, String> getType();
    public String getName();    
    public String getDecimalPlaces();
    public Field addField();    
    public void clearFields();
}
