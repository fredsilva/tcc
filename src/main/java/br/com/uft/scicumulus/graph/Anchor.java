/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.graph;

import java.util.Random;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Anchor extends Circle {
    double[] inputs;
    Color[] colors = new Color[] {Color.TOMATO, Color.BLUE, Color.DARKGREEN};
    
    public Anchor(DoubleProperty x, DoubleProperty y) {        
        this.inputs = new double[] {x.get()-50, x.get()+50, y.get()-25, y.get()+25};
        setCenterX(x.get());
        setCenterY(this.inputs[3]);
        setRadius(7);
//        super(x.get(), y.get(), 6);               
        setFill(generateColor().deriveColor(1, 1, 1, 0.5));
       
        x.bind(centerXProperty());
        y.bind(centerYProperty());
    }
    
    private Color generateColor(){
        Random random = new Random();
        int index = random.nextInt(this.colors.length);
        return this.colors[index];
    }
    
    public double generatePosition(){
        Random random = new Random();
        int index = random.nextInt(this.inputs.length);
        return 0;
    }
}
