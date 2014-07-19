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
public class Activity extends Rectangle{
    /**
     * Retângulo que representa uma Activity
     */
       
    
    public Activity(double width, double heigth) {        
        setWidth(width);
        setHeight(heigth);
        setArcWidth(16);
        setArcHeight(16);
        setFill(Color.FORESTGREEN.deriveColor(0, 1.2, 1, 0.6));
        setStroke(Color.FORESTGREEN);
        setStrokeWidth(3);                
    }         
    
}
