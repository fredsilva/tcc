/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Screen;

/**
 *
 * @author Frederico da Silva Santos
 */

public abstract class Utils {
    
    public static void setFullScreen(Region region){
        //Coloca objeto do tamanho da tela        
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();             
        region.setPrefWidth(primaryScreen.getWidth());        
        region.setPrefHeight(primaryScreen.getHeight());
    }
    
}
