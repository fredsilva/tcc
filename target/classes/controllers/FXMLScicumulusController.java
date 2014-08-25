/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Agent;
import br.com.uft.scicumulus.graph.EnableResizeAndDrag;
import br.com.uft.scicumulus.graph.Entity;
import br.com.uft.scicumulus.graph.Relation;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
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
    private AnchorPane APane_workflow;
    @FXML
    private TextField txtTagWorkflow, txtDescriptionWorkflow, txtExecTagWorkflow, txtExpDirWorkflow;
    @FXML
    private TextField txtNameDatabase, txtServerDatabase, txtPortDatabase, txtUsernameDatabase;
    @FXML
    private PasswordField txtPasswordDatabase;
    @FXML
    private TitledPane acc_properties_activity, acc_properties_relation;
    @FXML
    private ChoiceBox chb_parallel, chb_cloud, chb_act_type;
    @FXML
    private Label lb_number_machines, lb_login_cloud, lb_password_cloud;
    @FXML
    private TextField txt_number_machines, txt_login_cloud;
    @FXML
    private TextField txt_act_input_filename, txt_act_output_filename;
    @FXML
    private PasswordField txt_password_cloud;
    @FXML
    private TextField txt_act_name, txt_act_description, txt_act_templatedir, txt_act_activation;
    @FXML
    private Button btn_salvar_activity, btn_activity;    

//    Activity activity;
    Activity activity;

    private List<Activity> activities = new ArrayList<Activity>();

    //Tree Workflow
    final TreeItem<String> treeRoot = new TreeItem<String>("Workflow Data");
    final TreeView treeView = new TreeView();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFullScreen(paneGraph);
        initComponents();
        choiceBoxChanged();
        initializeTreeWork();
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
        for (Activity act : this.activities) {
            hydraActivity = hydraWorkflow.addElement("HydraActivity");
            hydraActivity.addAttribute("tag", act.getTag());
            hydraActivity.addAttribute("description", act.getDescription());
            hydraActivity.addAttribute("type", act.getType());
            hydraActivity.addAttribute("templatedir", act.getTemplatedir());
            hydraActivity.addAttribute("activation", act.getActivation());

            String input = new String();
            String output = new String();

            int cont = 0;
            for (Relation rel : this.relations) {
                if (act.equals(rel.nodeStart)) {
                    if (cont == 0) {
                        //Primeira entrada
                        Element relation1 = hydraActivity.addElement("Relation");
                        relation1.addAttribute("reltype", "Input");
                        relation1.addAttribute("name", rel.getName() + "_" + "input");
                        relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
                    }
                    Element relation2 = hydraActivity.addElement("Relation");
                    relation2.addAttribute("reltype", "Output");
                    relation2.addAttribute("name", rel.getName() + "_" + "output");
                    relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                    

                    input = rel.getName();
                }
                if (act.equals(rel.nodeEnd)) {
                    Activity dependency = (Activity) rel.nodeStart;
                    Element relation1 = hydraActivity.addElement("Relation");
                    relation1.addAttribute("reltype", "Input");
                    relation1.addAttribute("name", rel.getName() + "_" + "input");
                    relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
                    relation1.addAttribute("dependency", dependency.getTag());//Colocar o nome da dependência se existir                                        

                    if (cont == this.relations.size() - 1) {
                        //Última saída
                        Element relation2 = hydraActivity.addElement("Relation");
                        relation2.addAttribute("reltype", "Output");
                        relation2.addAttribute("name", rel.getName() + "_" + "output");
                        relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                                            
                    }
                    output = rel.getName();
                }
                cont++;
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

    public void importWorkflow() throws DocumentException {
        //Impota o workflow e cria o grafo
        System.out.println("Importando...");
        SAXReader reader = new SAXReader();
        Document docXml = reader.read("src/main/java/br/com/uft/scicumulus/files/SciCumulus.xml");
        Element root = docXml.getRootElement();
        for (Iterator i = root.elementIterator(); i.hasNext();) {
            Element element = (Element) i.next();
//            tempList.addElement(element.attributeValue("id"));

            for (Iterator j = element.elementIterator(); j.hasNext();) {
                Element innerElement = (Element) j.next();
                System.out.println(innerElement.getData().toString());
            }
        }
    }

//    public void insertActivity() {
//        Agent agent = new Agent("Fred", Agent.TYPE.USER);
//
//        if (this.activity != null) {
//            setDataActivity(this.activity2);//Grava os dados na activity
//        }
//        activity = new Activity("Act_" + Integer.toString(activities.size() + 1), agent);
//
//        //TODO Definir o nome da Entity
//        Entity entity = new Entity("Teste", Entity.TYPE.COMPUTER, this.activity, this.activity, null, null);
//        enableCreateLine(activity);
//        paneGraph.getChildren().add(activity);
//        EnableResizeAndDrag.make(activity);
//
//        //Adiciona a Activity à Tree
//        treeRoot.getChildren().get(0).getChildren().addAll(Arrays.asList(
//                new TreeItem<String>(activity.getName())));
//
////        paneGraph.getChildren().add(entity);
////        EnableResizeAndDrag.make(entity);
//        activateAccProperties();
//
//        txt_act_name.setText(activity.getName());
//
//        clearFieldsActivity();//Limpa os campos necessários
//
////        addActivityList(activity);
////        enableActivityClick(activity);
////        enableDrag(activity);
////        enableCreateLine(connectionPoint);                
//    }
    
    public void insertActivity() {   
        Agent agent = new Agent("Fred", Agent.TYPE.USER);

        if (this.activity != null) {
            setDataActivity(this.activity);//Grava os dados na activity
        }
        Text title = new Text("Act_" + Integer.toString(activities.size() + 1));        
        activity = new Activity(title.getText(), null);
        
        //TODO Definir a Entity
        
        //Adiciona a Activity à Tree
        treeRoot.getChildren().get(0).getChildren().addAll(Arrays.asList(
                new TreeItem<String>(activity.getName())));                        
                
        paneGraph.getChildren().add(activity);
        
        EnableResizeAndDrag.make(activity);
        
        enableCreateLine(activity);  
        
        activateAccProperties();
        txt_act_name.setText(activity.getName());
        clearFieldsActivity();//Limpa os campos necessários
    }

    private double initX;
    private double initY;
    private Point2D dragAnchor;
    private Relation line = null;
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
    List<Relation> relations = new ArrayList<>();

    private void enableCreateLine(Node node) {

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (me) -> {
            arrastou = true;
        });
        node.setOnMouseClicked((me) -> {
            if (!arrastou) {
                if (line == null) {
                    line = new Relation("Rel_" + Integer.toString(relations.size() + 1), node.getScene(), paneGraph, (Relation l) -> {
                        paneGraph.getChildren().remove(l);
                        line = null;
                    });
                    line.setNodeStart(node);
                    paneGraph.getChildren().add(line);
                } else {
                    line.setNodeEnd(node);
                    relations.add(line);

                    //Acrescenta a Entity na Tree
//                    treeRoot.getChildren().get(1).getChildren().addAll(Arrays.asList(
//                    new TreeItem<String>("Entity1")));        
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

        chb_act_type.getItems().addAll(activity_types);
        chb_act_type.getSelectionModel().selectFirst();

        Image image = new Image(getClass().getResourceAsStream("activity.png"));
        btn_activity.setGraphic(new ImageView(image));                
    }

    public void clearFieldsActivity() {
        txt_act_description.setText("");
        txt_act_templatedir.setText("");

        chb_parallel.getSelectionModel().selectFirst();
        chb_cloud.getSelectionModel().selectFirst();
        chb_act_type.getSelectionModel().selectFirst();
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
        acc_properties_activity.disableProperty().setValue(false);
        acc_properties_activity.expandedProperty().setValue(true);
    }

    public void addActivityList(Activity act) {
        activities.add(act);
    }

    public void setNameActivity() {
        this.activity.setName(txt_act_name.getText());
    }

    public void setNumberMachinesActivity() {
        this.activity.setNum_machines(Integer.parseInt(txt_number_machines.getText()));
    }

    public void setDataActivity() {
        //Seta os dados na parte gráfica                

        this.activity.setName(txt_act_name.getText());
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
        this.activity.setName(txt_act_name.getText());
        this.activity.setTag(txt_act_name.getText());
        this.activity.setDescription(txt_act_description.getText());
        this.activity.setType(chb_act_type.getValue().toString());
        this.activity.setTemplatedir(txt_act_templatedir.getText());
        this.activity.setActivation(txt_act_activation.getText());
        this.activity.setInput_filename(txt_act_input_filename.getText());
        this.activity.setOutput_filename(txt_act_output_filename.getText());
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

        addActivityList(this.activity);
    }

    public void closeWindow() {
        System.exit(0);
    }

    public void initializeTreeWork() {
        treeRoot.getChildren().addAll(Arrays.asList(
                new TreeItem<String>("Activities"),
                new TreeItem<String>("Entities")));

        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);
        APane_workflow.getChildren().add(treeView);
    }

    public void insertEntity() {
        Text title = new Text("Ent1");
        Entity entity = new Entity(title.getText(), Entity.TYPE.FILE, null, null, null, null);        
                
        paneGraph.getChildren().add(entity);
        
        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);

        //Adiciona a Entity à Tree
        treeRoot.getChildren().get(1).getChildren().addAll(Arrays.asList(
                new TreeItem<String>(entity.getName())));
    }
    
    public void insertAgent() {   
        Text title = new Text("Ag_1");
        Agent agent = new Agent(title.getText(), Agent.TYPE.SOFTWARE);        
                
        paneGraph.getChildren().add(agent);
        
        EnableResizeAndDrag.make(agent);
        
        enableCreateLine(agent);        
                     
        //Adiciona a Entity à Tree
//        treeRoot.getChildren().get(1).getChildren().addAll(Arrays.asList(
//                new TreeItem<String>(entity.getName())));
    }
}
