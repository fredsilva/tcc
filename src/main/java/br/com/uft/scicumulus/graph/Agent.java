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
public class Agent extends Rectangle{
    private String name;
    private TYPE type;       
        
    public Agent(String name, TYPE type) {
        this.name = name;
        this.type = type;
        setDataAgent();
    }
    
    public void setDataAgent(){            
        setWidth(60);
        setHeight(60);
        setArcWidth(60);
        setArcHeight(60);                
        
        if (this.type.equals(Agent.TYPE.HARDWARE)){
            setFill(Color.BLUEVIOLET.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.BLUEVIOLET);
        }
        if (this.type.equals(Agent.TYPE.ORGANIZATION)){
            setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.CORNFLOWERBLUE);
        }
        if (this.type.equals(Agent.TYPE.SOFTWARE)){
            setFill(Color.DIMGREY.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.DIMGREY);
        }
        if (this.type.equals(Agent.TYPE.USER)){
            setFill(Color.INDIANRED.deriveColor(0, 1.2, 1, 0.6));
            setStroke(Color.INDIANRED);
        }               
        setStrokeWidth(2);
    }
    
    public enum TYPE{
        ORGANIZATION, USER, SOFTWARE, HARDWARE;
    }
}
