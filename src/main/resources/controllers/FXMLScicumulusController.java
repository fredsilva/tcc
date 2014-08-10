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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private ChoiceBox chb_parallel, chb_cloud, chb_activity_type;
    @FXML
    private Label lb_number_machines, lb_login_cloud, lb_password_cloud;
    @FXML
    private TextField txt_number_machines, txt_login_cloud;
    @FXML
    private PasswordField txt_password_cloud;
    @FXML
    private TextField txt_name_activity, txt_activity_tag, txt_activity_description, txt_activity_templatedir, txt_activity_activation;
    @FXML
    private Button btn_salvar_activity;

    Activity activity;

    private List<Activity> activitys = new ArrayList<Activity>();

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

        setDataActivity(this.activity);//Utilizado para gravar a última activity

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

        Element hydraActivity;
        for (Activity act : this.activitys) {
            hydraActivity = hydraWorkflow.addElement("HydraActivity");
            hydraActivity.addAttribute("tag", act.getTag());
            hydraActivity.addAttribute("description", act.getDescription());
            hydraActivity.addAttribute("type", act.getType());
            hydraActivity.addAttribute("templatedir", act.getTemplatedir());
            hydraActivity.addAttribute("activation", act.getActivation());
            //Informar se vai executar paralelo ou na nuvem

            String input = new String();
            String output = new String();

            for (LineInTwoNodes rel : this.relations) {
                if (act.equals(rel.nodeStart)) {
                    Element relation = hydraActivity.addElement("Relation");
                    relation.addAttribute("reltype", "Input");
                    relation.addAttribute("name", rel.getName() + "_" + "input");
                    relation.addAttribute("filename", null);//Colocar o nome do arquivo
                    relation.addAttribute("dependency", null);//Colocar o nome da dependência se existir                                        

                    input = rel.getName();
                }
                if (act.equals(rel.nodeEnd)) {
                    Element relation = hydraActivity.addElement("Relation");
                    relation.addAttribute("reltype", "Output");
                    relation.addAttribute("name", rel.getName() + "_" + "output");
                    relation.addAttribute("filename", null);//Colocar o nome do arquivo                    

                    output = rel.getName();
                }
            }
            Element field = hydraActivity.addElement("Field");
            field.addAttribute("name", "FASTA_FILE");
            field.addAttribute("type", "string");
            field.addAttribute("input", input);
            field.addAttribute("output", output);
            
            Element file = hydraActivity.addElement("File");
            file.addAttribute("filename", "experiment.cmd");
            file.addAttribute("instrumented", "true");
        }
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
        if (this.activity != null) {
            setDataActivity(this.activity);//Grava os dados na activity
        }
        activity = new Activity("Act_" + Integer.toString(activitys.size() + 1));
//        activity.addPoint("rigth", connectionPoint);
        enableCreateLine(activity);
        paneGraph.getChildren().add(activity);
        EnableResizeAndDrag.make(activity);

        activateAccProperties();

        txt_name_activity.setText(activity.getName());

        clearFieldsActivity();//Limpa os campos necessários

//        addActivityList(activity);
//        enableActivityClick(activity);
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
    List<LineInTwoNodes> relations = new ArrayList<>();

    private void enableCreateLine(Node node) {

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (me) -> {
            arrastou = true;
        });
        node.setOnMouseClicked((me) -> {
            if (!arrastou) {
                if (line == null) {
                    line = new LineInTwoNodes("Rel_" + Integer.toString(relations.size() + 1), node.getScene(), paneGraph, (LineInTwoNodes l) -> {
                        paneGraph.getChildren().remove(l);
                        line = null;
                    });
                    line.setNodeStart(node);
                    paneGraph.getChildren().add(line);
                } else {
                    line.setNodeEnd(node);
//                    this.activity.addRelations(line);
                    relations.add(line);
                    line = null;
                }
            } else {
                arrastou = false;
            }
        });
    }

    List<String> activity_types = Arrays.asList("MAP", "SPLIT_MAP", "REDUCE", "FILTER", "SR_QUERY", "JOIN_QUERY");

    public void initComponents() {
        chb_parallel.getItems().addAll("Yes", "No");
        chb_parallel.getSelectionModel().selectFirst();
        chb_cloud.getItems().addAll("No", "Yes");
        chb_cloud.getSelectionModel().selectFirst();

        lb_login_cloud.disableProperty().setValue(true);
        txt_login_cloud.disableProperty().setValue(true);
        lb_password_cloud.disableProperty().setValue(true);
        txt_password_cloud.disableProperty().setValue(true);

        chb_activity_type.getItems().addAll(activity_types);
        chb_activity_type.getSelectionModel().selectFirst();                
    }

    public void clearFieldsActivity() {
        txt_activity_tag.setText("");
        txt_activity_description.setText("");        
        txt_activity_templatedir.setText("");

        chb_parallel.getSelectionModel().selectFirst();
        chb_cloud.getSelectionModel().selectFirst();
        chb_activity_type.getSelectionModel().selectFirst();
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

    public void enableActivityClick(Node node) {
        node.setOnMouseClicked((MouseEvent me) -> {
            node.setStyle("-fx-background-color: #9AFF9A; -fx-border-color: #FFD700; -fx-border-style: solid; -fx-border-width: 2;");
        });

        node.setOnKeyPressed((KeyEvent ke) -> {
            if (ke.getCode() == KeyCode.DELETE) {
            }
        });
    }

    //Activity
    public void activateAccProperties() {
        //Ativa o accordion properties
        acc_properties.disableProperty().setValue(false);
        acc_properties.expandedProperty().setValue(true);
    }

    public void addActivityList(Activity act) {
        activitys.add(act);
    }

    public void setNameActivity() {
        this.activity.setName(txt_name_activity.getText());
    }

    public void setNumberMachinesActivity() {
        this.activity.setNum_machines(Integer.parseInt(txt_number_machines.getText()));
    }

    public void setDataActivity() {
        //Seta os dados na parte gráfica                

        this.activity.setName(txt_name_activity.getText());
        this.activity.setNum_machines(Integer.parseInt(txt_number_machines.getText()));
        this.activity.setLogin(txt_login_cloud.getText());
        this.activity.setPassword(txt_password_cloud.getText());

        if (chb_parallel.getValue().equals("Yes")) {
            this.activity.setParalell(true);
        } else {
            this.activity.setParalell(false);
        }
        if (chb_cloud.getValue().equals("Yes")) {
            this.activity.setCloud(true);
        } else {
            this.activity.setCloud(false);
        }
    }

    public void setDataActivity(Activity activity) {
        //Seta os dados de cada activity antes de ser adicinada à lista
        this.activity.setName(txt_name_activity.getText());
        this.activity.setTag(txt_activity_tag.getText());
        this.activity.setDescription(txt_activity_description.getText());
        this.activity.setType(chb_activity_type.getValue().toString());
        this.activity.setTemplatedir(txt_activity_templatedir.getText());
        this.activity.setActivation(txt_activity_activation.getText());

        this.activity.setNum_machines(Integer.parseInt(txt_number_machines.getText()));
        this.activity.setLogin(txt_login_cloud.getText());
        this.activity.setPassword(txt_password_cloud.getText());

        if (chb_parallel.getValue().equals("Yes")) {
            this.activity.setParalell(true);
        } else {
            this.activity.setParalell(false);
        }
        if (chb_cloud.getValue().equals("Yes")) {
            this.activity.setCloud(true);
        } else {
            this.activity.setCloud(false);
        }

        addActivityList(activity);
    }

    public void testes() {
        setDataActivity(this.activity);
        for (Activity act : activitys) {
            System.out.println("Activity: " + act);
        }
        System.out.println("---------------------------------------");
        for (LineInTwoNodes rel : relations) {
            Activity actStart = (Activity) rel.nodeStart;
            System.out.println("ActivityStart: " + actStart);
            Activity actEnd = (Activity) rel.nodeEnd;
            System.out.println("ActivityEnd: " + actEnd);
        }
    }
}
