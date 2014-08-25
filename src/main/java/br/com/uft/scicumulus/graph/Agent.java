/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Agent extends Shape{
    private String name;
    private TYPE type;       
        
    public Agent(String name, TYPE type) {
        this.name = name;
        super.title.setText(name);
        this.type = type;
        setDataAgent();
    }
    
    public void setDataAgent(){            
        super.object.setWidth(60);
        super.object.setHeight(60);
        super.object.setArcWidth(60);
        super.object.setArcHeight(60);                
        
        if (this.type.equals(Agent.TYPE.HARDWARE)){
            super.object.setFill(Color.BLUEVIOLET.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.BLUEVIOLET);
        }
        if (this.type.equals(Agent.TYPE.ORGANIZATION)){
            super.object.setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.CORNFLOWERBLUE);
        }
        if (this.type.equals(Agent.TYPE.SOFTWARE)){
            super.object.setFill(Color.DIMGREY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DIMGREY);
        }
        if (this.type.equals(Agent.TYPE.USER)){
            super.object.setFill(Color.INDIANRED.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.INDIANRED);
        }                       
    }
    
    public enum TYPE{
        ORGANIZATION, USER, SOFTWARE, HARDWARE;
    }
}
