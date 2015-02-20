/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import javafx.scene.paint.Color;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Agent extends Shape implements Serializable{    
    private String name;
    private TYPE type;      

    public Agent() throws NoSuchAlgorithmException{
    }
        
    public Agent(String name, TYPE type) throws NoSuchAlgorithmException{
        this.name = name;
        super.title.setText(name);
        this.type = type;        
        setDataAgent();
    }        
    
    public Agent(String name, TYPE type, Activity wasAssociatedWith) throws NoSuchAlgorithmException{
        this.name = name;
        super.title.setText(name);
        this.type = type;                
        setDataAgent();
    }        
    
    public Agent(String name, TYPE type, Entity wasAttributedTo) throws NoSuchAlgorithmException{
        this.name = name;
        super.title.setText(name);
        this.type = type;                
        setDataAgent();
    }        
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }        

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }      
    
    public void setDataAgent(){            
        super.object.setWidth(70);
        super.object.setHeight(70);
        super.object.setArcWidth(70);
        super.object.setArcHeight(70);                
        
        if (this.type.equals(Agent.TYPE.HARDWARE)){
            super.object.setFill(Color.GRAY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.GRAY);
        }
        if (this.type.equals(Agent.TYPE.ORGANIZATION)){
            super.object.setFill(Color.DARKCYAN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DARKCYAN);
        }
        if (this.type.equals(Agent.TYPE.SOFTWARE)){
            super.object.setFill(Color.LIMEGREEN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.LIMEGREEN);
        }
        if (this.type.equals(Agent.TYPE.USER)){
            super.object.setFill(Color.MEDIUMPURPLE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.MEDIUMPURPLE);
        }                       
    }
    
    public enum TYPE{
        ORGANIZATION, USER, SOFTWARE, HARDWARE;
    }
    
    public void onMouseExit(){
       if (this.type.equals(Agent.TYPE.HARDWARE)){
            super.object.setFill(Color.GRAY.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.GRAY);
        }
        if (this.type.equals(Agent.TYPE.ORGANIZATION)){
            super.object.setFill(Color.DARKCYAN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.DARKCYAN);
        }
        if (this.type.equals(Agent.TYPE.SOFTWARE)){
            super.object.setFill(Color.LIMEGREEN.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.LIMEGREEN);
        }
        if (this.type.equals(Agent.TYPE.USER)){
            super.object.setFill(Color.MEDIUMPURPLE.deriveColor(0, 1.2, 1, 0.6));
            super.object.setStroke(Color.MEDIUMPURPLE);
        }
    }
}
