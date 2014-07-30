/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author fredsilva
 */
public class Scicumulus extends Application {
    
    
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLScicumulus.fxml"));        
        Scene scene = new Scene(root);
        
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();        
        
        stage.setTitle("Scicumulus");
        stage.setScene(scene);
        stage.setY(primaryScreen.getMinY());
        stage.setWidth(primaryScreen.getWidth());
        stage.setHeight(primaryScreen.getHeight());
        stage.setMaximized(true);
        
        stage.show();
//        try {
//            FxmlLoad.load("/fxml/FXMLScicumulus.fxml", null, null);
////            FxmlLoad.load(FxmlPATH.PRINCIPAL,null,null);
//        } catch (IOException ex) {
//            Logger.getLogger(Scicumulus.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
