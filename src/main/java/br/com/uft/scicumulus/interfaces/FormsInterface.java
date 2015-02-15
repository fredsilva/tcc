/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.interfaces;

import br.com.uft.scicumulus.graph.Field;
import java.util.List;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.Button;

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
    public Field delField();    
    public List<Field> getFields();
    public void clearList();
    public void clearFields();
    public Button getButtonAddField(); 
    public Button getButtonDelField(); 
    public Button getButtonFinishField(); 
}
