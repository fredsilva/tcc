/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Frederico da Silva Santos, Thaylon Guede Santos
 */
public class ConnectionPoint extends Rectangle{
    /**
     * Representa os pontos de conexão entre activitys
     */
    
    private List<LineInTwoNodes> relations = new ArrayList();
    
    public ConnectionPoint() {        
        setWidth(14);
        setHeight(14);
        setArcWidth(14);
        setArcHeight(14);
        setFill(Color.YELLOW.deriveColor(0, 1.2, 1, 0.6));
        setStroke(Color.YELLOW);
        setStrokeWidth(2);                        
    }            
    
    public void addRelations(LineInTwoNodes line){
        line.addOnRemoveEvent(relations::remove);
        relations.add(line);
    }
}
