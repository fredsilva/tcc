/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.graph;

import java.util.Random;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Border {   
    private ReadOnlyDoubleWrapper borderX = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper borderY = new ReadOnlyDoubleWrapper();    
    private double [] bordersX;
    private double [] bordersY;

    public Border(Activity activity) {
        calcBorder(activity.getBoundsInParent());
        activity.boundsInParentProperty().addListener((ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds bounds) -> {
            calcBorder(bounds);
        });        
    }

    private void calcBorder(Bounds bounds) {        
        bordersX = new double[]{
            (bounds.getMinX() + bounds.getWidth() / 2) - bounds.getWidth()/2,
            (bounds.getMinX() + bounds.getWidth() / 2) + bounds.getWidth()/2,
            bounds.getMinX() + bounds.getWidth() / 2,
            bounds.getMinX() + bounds.getWidth() / 2
        };
        bordersY = new double[]{
            (bounds.getMinY() + bounds.getHeight() / 2),
            (bounds.getMinY() + bounds.getHeight() / 2),
            (bounds.getMinY() + bounds.getHeight() / 2) - bounds.getHeight()/2,
            (bounds.getMinY() + bounds.getHeight() / 2) + bounds.getHeight()/2
        };
        
        int indexBorder = generateBorder();
        borderX.set(bordersX[indexBorder]);
        borderY.set(bordersY[indexBorder]);        
    }

    ReadOnlyDoubleProperty borderXProperty() {
        return borderX.getReadOnlyProperty();
    }

    ReadOnlyDoubleProperty borderYProperty() {
        return borderY.getReadOnlyProperty();
    }
    
    int generateBorder(){
        Random random = new Random();
        int index = random.nextInt(3);
        return index;
    }
}
