/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.LineInTwoNodes;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * FXML Controller class
 *
 * @author fredsilva
 */
public class FXMLScicumulusController implements Initializable {
    @FXML
    private Pane paneGraph;
    @FXML
    private TextField txtTagWorkflow, txtDescriptionWorkflow, txtExecTagWorkflow, txtExpDirWorkflow;    
    @FXML
    private TextField txtNameDatabase, txtServerDatabase, txtPortDatabase, txtUsernameDatabase;    
    @FXML
    private PasswordField txtPasswordDatabase;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFullScreen(paneGraph);
    }  
    
    public void setFullScreen(Pane pane){
        //Coloca objeto do tamanho da tela
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();             
        pane.setPrefWidth(primaryScreen.getWidth());        
        pane.setPrefHeight(primaryScreen.getHeight());
    }
    
    public void createScicumulusXML() throws IOException{
        //Monta o arquivo Scicumulus.xml        
        
        Document doc = DocumentFactory.getInstance().createDocument();
        Element root = doc.addElement("Hydra");                
        
        Element database = root.addElement("database");
        database.addAttribute("name", txtNameDatabase.getText());
        database.addAttribute("server", txtServerDatabase.getText());
        database.addAttribute("port", txtPortDatabase.getText());
        database.addAttribute("username", txtUsernameDatabase.getText());
        database.addAttribute("password", txtPasswordDatabase.getText());
       
        Element hydraWorkflow = root.addElement("HydraWorkflow");
        hydraWorkflow.addAttribute("tag", txtTagWorkflow.getText());
        hydraWorkflow.addAttribute("description", txtDescriptionWorkflow.getText());
        hydraWorkflow.addAttribute("exectag", txtExecTagWorkflow.getText());
        hydraWorkflow.addAttribute("expdir", txtExpDirWorkflow.getText());
        
        //Element hydraActivity = hydraWorkflow.addElement("HydraActivity");
                
        
        
        //Gravando arquivo
        FileOutputStream fos = new FileOutputStream("src/main/java/br/com/uft/scicumulus/files/SciCumulus.xml");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(doc);
        writer.flush();        
    }
    
    public void insertActivity(){
        Activity activity = new Activity(100, 60);
        paneGraph.getChildren().add(activity);        
        
        enableDrag(activity);
        enableCreateLine(activity);              
    }
    
    
    private double initX;
    private double initY;
    private Point2D dragAnchor;
    private LineInTwoNodes line = null;
    private boolean arastou = false;  
    
    private void enableDrag(Node node) {       
        node.setOnMouseDragged((MouseEvent me) -> {
            arastou = true;
            node.setCursor(Cursor.MOVE);
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();
            double newXPosition = initX + dragX;
            double newYPosition = initY + dragY;
            node.setLayoutX(newXPosition);
            node.setLayoutY(newYPosition);
        });
        node.setOnMouseEntered((MouseEvent me) -> {
            node.toFront();
            node.setCursor(Cursor.HAND);
        });
        node.setOnMousePressed((MouseEvent me) -> {
            initX = node.getLayoutX();
            initY = node.getLayoutY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
        });
        node.setOnMouseReleased((MouseEvent me) -> {
            node.setCursor(Cursor.HAND);
        });

    }
    
    private void enableCreateLine(Node node) {
        node.setOnMouseClicked((me) -> {
            if (!arastou) {
                if (line == null) {
                    line = new LineInTwoNodes(node.getScene(), (Line l) -> {
                        paneGraph.getChildren().remove(l);
                        line = null;
                    });
                    line.setNodeStart(node);
                    paneGraph.getChildren().add(line);
                } else {
                    line.setNodeEnd(node);
                    line = null;
                }
            } else {
                arastou = false;
            }
        });
    }
}
