/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.ConnectionPoint;
import br.com.uft.scicumulus.graph.EnableResizeAndDrag;
import br.com.uft.scicumulus.graph.LineInTwoNodes;
import br.com.uft.scicumulus.hydra.HWorkflow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * FXML Controller class
 *
 * @author Frederico da Silva Santos
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
    @FXML
    private TitledPane acc_properties;
    @FXML
    private ChoiceBox chb_parallel, chb_cloud;
    @FXML
    private Label lb_number_machines, lb_login_cloud, lb_password_cloud;
    @FXML
    private TextField txt_number_machines, txt_login_cloud;
    @FXML
    private PasswordField txt_password_cloud;
    
    HWorkflow hWorkflow;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFullScreen(paneGraph);
        initComponents();
        choiceBoxChanged();
    }

    public void setFullScreen(Pane pane) {
        //Coloca objeto do tamanho da tela
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();
        pane.setPrefWidth(primaryScreen.getWidth());
        pane.setPrefHeight(primaryScreen.getHeight());
    }

    public void createScicumulusXML() throws IOException {
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

    public void insertActivity() {
        ConnectionPoint connectionPoint = new ConnectionPoint();
//        paneGraph.getChildren().add(connectionPoint);        
//        EnableResizeAndDrag.make(connectionPoint);
        Activity activity = new Activity();
        activity.addPoint("rigth", connectionPoint);
        enableCreateLine(connectionPoint);
        paneGraph.getChildren().add(activity);
        EnableResizeAndDrag.make(activity);                
        
        activateAccProperties();
//        enableDrag(activity);
//        enableCreateLine(connectionPoint);        
    }

    public void configActivity(ConnectionPoint activity) {
        //TODO
    }

    private double initX;
    private double initY;
    private Point2D dragAnchor;
    private LineInTwoNodes line = null;
    private boolean arrastou = false;

//    private void enableDrag(Node node) {
//
//        node.setOnMouseDragged((MouseEvent me) -> {
//            arrastou = true;
//            node.setCursor(Cursor.MOVE);
//            double dragX = me.getSceneX() - dragAnchor.getX();
//            double dragY = me.getSceneY() - dragAnchor.getY();
//            double newXPosition = initX + dragX;
//            double newYPosition = initY + dragY;
//            node.setLayoutX(newXPosition);
//            node.setLayoutY(newYPosition);
//        });
//        node.setOnMouseEntered((MouseEvent me) -> {
//            node.toFront();
//            node.setCursor(Cursor.HAND);
//        });
//        node.setOnMousePressed((MouseEvent me) -> {
//            initX = node.getLayoutX();
//            initY = node.getLayoutY();
//            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
//        });
//        node.setOnMouseReleased((MouseEvent me) -> {
//            node.setCursor(Cursor.HAND);
//        });
//
//    }
//
    private void enableCreateLine(Node node) {

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (me) -> {
            arrastou = true;
        });
        node.setOnMouseClicked((me) -> {
            if (!arrastou) {
                if (line == null) {
                    line = new LineInTwoNodes(node.getScene(), paneGraph, (LineInTwoNodes l) -> {
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
                arrastou = false;
            }
        });
    }

    public void initComponents() {
        int sizeFont = 8;
        chb_parallel.getItems().addAll("Yes", "No");
        chb_parallel.getSelectionModel().selectFirst();
        chb_cloud.getItems().addAll("No", "Yes");
        chb_cloud.getSelectionModel().selectFirst();
        
        lb_login_cloud.disableProperty().setValue(true);
        txt_login_cloud.disableProperty().setValue(true);
        lb_password_cloud.disableProperty().setValue(true);
        txt_password_cloud.disableProperty().setValue(true);
                
    }

    public void choiceBoxChanged() {
        chb_parallel.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number option1, Number option2) {
                if (chb_parallel.getItems().get((Integer) option2).equals("Yes")) {
                    lb_number_machines.disableProperty().setValue(false);
                    txt_number_machines.disableProperty().setValue(false);
                } else {
                    lb_number_machines.disableProperty().setValue(true);
                    txt_number_machines.disableProperty().setValue(true);
                }
            }
        });

        chb_cloud.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number option1, Number option2) {
                if (chb_cloud.getItems().get((Integer) option2).equals("Yes")) {
                    lb_login_cloud.disableProperty().setValue(false);
                    txt_login_cloud.disableProperty().setValue(false);
                    lb_password_cloud.disableProperty().setValue(false);
                    txt_password_cloud.disableProperty().setValue(false);
                } else {
                    lb_password_cloud.disableProperty().setValue(true);
                    txt_password_cloud.disableProperty().setValue(true);
                    lb_login_cloud.disableProperty().setValue(true);
                    txt_login_cloud.disableProperty().setValue(true);
                }
            }
        });

    }
    
    public void activateAccProperties(){
        //Ativa o accordion properties
        acc_properties.disableProperty().setValue(false);
        acc_properties.expandedProperty().setValue(true);
    }
}
