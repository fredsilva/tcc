/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import br.com.uft.scicumulus.ConfigProject;
import br.com.uft.scicumulus.enums.FieldType;
import br.com.uft.scicumulus.enums.Operation;
import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Agent;
import br.com.uft.scicumulus.graph.EnableResizeAndDrag;
import br.com.uft.scicumulus.graph.Entity;
import br.com.uft.scicumulus.graph.Field;
import br.com.uft.scicumulus.graph.Relation;
import br.com.uft.scicumulus.graph.Shape;
import br.com.uft.scicumulus.kryonet.ActivityKryo;
import br.com.uft.scicumulus.kryonet.ClientKryo;
import br.com.uft.scicumulus.kryonet.CommonsNetwork;
import br.com.uft.scicumulus.kryonet.RelationKryo;
import br.com.uft.scicumulus.tables.Command;
import br.com.uft.scicumulus.utils.SSH;
import br.com.uft.scicumulus.utils.SystemInfo;
import br.com.uft.scicumulus.utils.Utils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * FXML Controller class
 *
 * @author Frederico da Silva Santos
 */
public class FXMLScicumulusController extends Listener implements Initializable, Serializable {

    @FXML
    private Pane paneGraph;
    @FXML
    private AnchorPane APane_workflow, APane_programs;
    @FXML
    private TitledPane TP_Workflow_name;
    @FXML
    private TextField txtTagWorkflow, txtDescriptionWorkflow, txtExecTagWorkflow, txtExpDirWorkflow;
    @FXML
    private TextField txtNameDatabase, txtServerDatabase, txtPortDatabase, txtUsernameDatabase;
    @FXML
    private PasswordField txtPasswordDatabase;
    @FXML
    private TitledPane acc_properties_activity, acc_properties_relation, acc_configuration, acc_programs;
    @FXML
    private ChoiceBox chb_parallel, chb_cloud, chb_act_type, chb_sleeptime;
    @FXML
    private Label lb_number_machines, lb_login_cloud, lb_password_cloud;
    @FXML
    private TextField txt_number_machines, txt_login_cloud, txt_server_directory;
    @FXML
    private TextField txt_act_input_filename, txt_act_output_filename;
    @FXML
    private PasswordField txt_password_cloud;
    @FXML
    private TextField txt_act_name, txt_act_description, txt_act_activation, txt_protocol_s_l;
    @FXML
    private TextArea ta_name_machines, ta_commands;
    @FXML
    private Button btn_salvar_activity, btn_activity, btn_entity_file, btn_entity_comp, btn_entity_param;
    @FXML
    private Button btn_entity_note, btn_entity_vm, btn_agent_user, btn_agent_software, btn_agent_hardware, btn_agent_org;
    @FXML
    private Button btn_field_add;
    @FXML
    private ListView<String> list_programs = new ListView<>();

    @FXML
    private AnchorPane acpane_fields;

    FileChooser fileChosser = new FileChooser();
    DirectoryChooser dirChooser = new DirectoryChooser();
    DirectoryChooser dirExpChooser = new DirectoryChooser();

//    String directoryDefaultFiles = "src/main/java/br/com/uft/scicumulus/files/";
    String directoryDefaultFiles = null;
    String directoryExp, directoryPrograms;

    File dirPrograms;
    File[] programsSelected;

    Activity activity;

    Object selected = null;
    String nameWorkflow;

    private List<Activity> activities = new ArrayList<Activity>();
    List<Field> fields = new ArrayList<Field>();
    private List<String> listCommands = new ArrayList<String>();
    private List<Agent> agents;
    private List<Node> nodes = new ArrayList<Node>();//Lista de objetos do tipo Node
    private List<TextField> fieldsRequired;
    private List<File> programs = new ArrayList<File>();

    //Tree Workflow
    final TreeItem<String> treeRoot = new TreeItem<String>("Workflow Composition");
    final TreeView treeView = new TreeView();
    private double mouseX;
    private double mouseY;
    private File dirProject = null;

    //Kryonet
    ClientKryo clientKryo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFullScreen(paneGraph);
        initComponents();
        changedFields();
        fieldsFocus();
        keypressFields();
        choiceBoxChanged();
        initializeTreeWork();
        getSelectedTreeItem();
        runClient();

//        Polygon pol = new Polygon(new double[]{
//            50, 50, 20,
//            80, 80
//        });        
//        
//        pol.setFill(Color.BLUEVIOLET);
//        pol.setStrokeWidth(2);
//        
//        paneGraph.getChildren().add(pol);
        try {
            try {
                createDefaultAgents();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFullScreen(Pane pane) {
        //Coloca objeto do tamanho da tela
        Rectangle2D primaryScreen = Screen.getPrimary().getVisualBounds();
        pane.setPrefWidth(primaryScreen.getWidth());
        pane.setPrefHeight(primaryScreen.getHeight());
    }

    public void createScicumulusXML() throws IOException, Exception {
        paneGraph.requestFocus();//Altera o foco para o paneGraph
        this.fieldsRequired = Arrays.asList(
                txtTagWorkflow, txtDescriptionWorkflow, txtExecTagWorkflow,
                txtExpDirWorkflow, txtNameDatabase, txtServerDatabase,
                txtPortDatabase, txtUsernameDatabase, txtPasswordDatabase,
                txt_server_directory
        );
        //Monta o arquivo Scicumulus.xml 
//        if (!isFieldEmpty()) {
        //Cria o diretório de expansão                    
        this.directoryExp = dirProject.getAbsolutePath() + "/" + txtExpDirWorkflow.getText().trim();
        File dir = new File(this.directoryExp);
        dir.mkdirs();
        File dirPrograms = new File(this.directoryExp + "/programs");
//            this.directoryPrograms = dirPrograms.getPath();
        dirPrograms.mkdir();

        setDataActivity(this.activity);//Utilizado para gravar a última activity

        Document doc = DocumentFactory.getInstance().createDocument();
        Element root = doc.addElement("SciCumulus");

        Element environment = root.addElement("environment");
        environment.addAttribute("type", "LOCAL");

        Element binary = root.addElement("binary");
        binary.addAttribute("directory", this.directoryExp + "/bin");
        binary.addAttribute("execution_version", "SCCore.jar");

        Element constraint = root.addElement("constraint");
        constraint.addAttribute("workflow_exectag", "montage-1");
        constraint.addAttribute("cores", "Colocar a quantidade de cores...");

        Element workspace = root.addElement("workspace");
        workspace.addAttribute("workflow_dir", this.dirProject.getAbsolutePath());

        Element database = root.addElement("database");
        database.addAttribute("name", txtNameDatabase.getText());
        database.addAttribute("server", txtServerDatabase.getText());
        database.addAttribute("port", txtPortDatabase.getText());
        database.addAttribute("username", txtUsernameDatabase.getText());
        database.addAttribute("password", txtPasswordDatabase.getText());

        Element hydraWorkflow = root.addElement("conceptualWorkflow");
        hydraWorkflow.addAttribute("tag", txtTagWorkflow.getText().replace(" ", "").trim());
        hydraWorkflow.addAttribute("description", txtDescriptionWorkflow.getText());
        hydraWorkflow.addAttribute("exectag", txtExecTagWorkflow.getText());
        hydraWorkflow.addAttribute("expdir", this.directoryExp);

        Element hydraActivity;
        for (Activity act : this.activities) {
            hydraActivity = hydraWorkflow.addElement("activity");
            hydraActivity.addAttribute("tag", act.getTag().replace(" ", "").trim());
            hydraActivity.addAttribute("description", act.getDescription());
            hydraActivity.addAttribute("type", act.getType());
            hydraActivity.addAttribute("template", act.getTemplatedir());
            hydraActivity.addAttribute("activation", act.getActivation());
            hydraActivity.addAttribute("extractor", "./extractor.cmd");

            dir = new File(this.directoryExp + "/template_" + act.getName());
            dir.mkdirs();

            //Criando arquivo experiment.cmd
            File fout = new File(dir.getPath() + "/" + "experiment.cmd");
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (String command : act.getCommands()) {
                bw.write("sleep " + chb_sleeptime.getValue().toString());
                bw.newLine();
                bw.write(command);
                bw.newLine();
            }
            bw.close();

            String input = new String();
            String output = new String();

            int cont = 0;
            for (Relation rel : this.relations) {
                if (act.equals(rel.nodeStart)) {
                    if (cont == 0) {
                        //Primeira entrada
                        Element relation1 = hydraActivity.addElement("relation");
                        relation1.addAttribute("reltype", "Input");
                        relation1.addAttribute("name", "I" + act.getName());
//                        relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
                    }
                    Element relation2 = hydraActivity.addElement("relation");
                    relation2.addAttribute("reltype", "Output");
                    relation2.addAttribute("name", "O" + act.getName());
//                    relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                    

//                    input = "I"+act.getName();
                }
                if (act.equals(rel.nodeEnd)) {
                    Activity dependency = (Activity) rel.nodeStart;
                    Element relation1 = hydraActivity.addElement("relation");
                    relation1.addAttribute("reltype", "Input");
                    relation1.addAttribute("name", "I" + act.getName());
                    relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
                    relation1.addAttribute("dependency", dependency.getTag());//Colocar o nome da dependência se existir                                        

                    if (cont == this.relations.size() - 1) {
                        //Última saída
                        Element relation2 = hydraActivity.addElement("relation");
                        relation2.addAttribute("reltype", "Output");
                        relation2.addAttribute("name", "O" + act.getName());
//                        relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                                            
                    }
//                    output = "O"+act.getName();
                }
                cont++;
            }
            input = "I" + act.getName();
            output = "O" + act.getName();
            for (Field fieldAct : act.getFields()) {
                Element field = hydraActivity.addElement("field");
                field.addAttribute("name", fieldAct.getName());
                field.addAttribute("type", fieldAct.getType());
                field.addAttribute("input", input);
                if (!fieldAct.getType().equals("string")) {
                    field.addAttribute("output", output);
                }
                if (fieldAct.getType().equals("float")) {
                    field.addAttribute("decimalplaces", fieldAct.getDecimalPlaces());
                }
                if (fieldAct.getType().equals("file")) {
                    field.addAttribute("operation", fieldAct.getOperation());
                }
            }
//            String input = new String();
//            String output = new String();
//
//            int cont = 0;
//            for (Relation rel : this.relations) {
//                if (act.equals(rel.nodeStart)) {
//                    if (cont == 0) {
//                        //Primeira entrada
//                        Element relation1 = hydraActivity.addElement("relation");
//                        relation1.addAttribute("reltype", "Input");
//                        relation1.addAttribute("name", rel.getName() + "_" + "input");
////                        relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
//                    }
//                    Element relation2 = hydraActivity.addElement("relation");
//                    relation2.addAttribute("reltype", "Output");
//                    relation2.addAttribute("name", rel.getName() + "_" + "output");
////                    relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                    
//
//                    input = rel.getName();
//                }
//                if (act.equals(rel.nodeEnd)) {
//                    Activity dependency = (Activity) rel.nodeStart;
//                    Element relation1 = hydraActivity.addElement("relation");
//                    relation1.addAttribute("reltype", "Input");
//                    relation1.addAttribute("name", rel.getName() + "_" + "input");
//                    relation1.addAttribute("filename", act.getInput_filename());//Colocar o nome do arquivo                    
//                    relation1.addAttribute("dependency", dependency.getTag());//Colocar o nome da dependência se existir                                        
//
//                    if (cont == this.relations.size() - 1) {
//                        //Última saída
//                        Element relation2 = hydraActivity.addElement("relation");
//                        relation2.addAttribute("reltype", "Output");
//                        relation2.addAttribute("name", rel.getName() + "_" + "output");
////                        relation2.addAttribute("filename", act.getOutput_filename());//Colocar o nome do arquivo                                            
//                    }
//                    output = rel.getName();
//                }
//                cont++;
//            }
//            Element field = hydraActivity.addElement("field");
//            field.addAttribute("name", "FASTA_FILE");
//            field.addAttribute("type", "string");
//            field.addAttribute("input", input);
//            field.addAttribute("output", output);            
            Element file = hydraActivity.addElement("File");
            file.addAttribute("filename", "experiment.cmd");
            file.addAttribute("instrumented", "true");
        }
        Element executionWorkflow = root.addElement("executionWorkflow");
        executionWorkflow.addAttribute("tag", "Coloca nome da tag...");
        executionWorkflow.addAttribute("execmodel", "DYN_FAF");
        executionWorkflow.addAttribute("expdir", this.directoryExp);
        executionWorkflow.addAttribute("max_failure", "1");
        executionWorkflow.addAttribute("user_interaction", "false");
        executionWorkflow.addAttribute("redundancy", "false");
        executionWorkflow.addAttribute("reliability", "0.1");

        Element relationInput = executionWorkflow.addElement("relation");
        relationInput.addAttribute("name", "IListFits");
        relationInput.addAttribute("filename", "input.dataset");

        //Gravando arquivo
        FileOutputStream fos = new FileOutputStream(this.directoryExp + "/SciCumulus.xml");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(doc);
        writer.flush();

        try {
            //Criando o arquivo machines.conf        
            createMachinesConf();
            //Criando o arquivo parameter.txt        
            createParameterTxt(ta_parameters.getText());
        } catch (IOException ex) {
            Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Copiando arquivos para o diretório programs        
        Utils.copyFiles(this.dirPrograms, directoryExp + "/programs/");

//            sendWorkflow(this.directoryExp, "/deploy/experiments");
//            sendWorkflow(this.directoryExp, this.txt_server_directory.getText().trim());            
//        String[] dirComplete = this.directoryExp.split(this.directoryDefaultFiles)[1].split("/");
//        String dirLocal = this.directoryDefaultFiles + dirComplete[0];
//        sendWorkflow(dirLocal, this.txt_server_directory.getText().trim());
        sendWorkflow(this.dirProject.getAbsolutePath(), this.txt_server_directory.getText().trim());

//        }else{
//            JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
//        }
//        if (isActivityEmpty()) {
//            Dialogs.create()
//                    .owner(null)
//                    .title("Activity Duplicate")
//                    .masthead(null)
//                    .message("Existe uma activity que não está conectada!")
//                    .showInformation();
//        }
    }

    public boolean sendWorkflow(String pathLocal, String pathServer) throws Exception {
        SSH ssh = new SSH();
        return ssh.sendFiles(pathLocal, pathServer);
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

    public void insertActivity() throws NoSuchAlgorithmException {
        if (this.activity != null) {
            setDataActivity(this.activity);
        }

        Text title = new Text("Act_" + Integer.toString(activities.size() + 1));
        activity = new Activity(title.getText(), this.agents, null);

        for (Agent ag : this.agents) {
            addAgentTree(ag);
        }

        addActivityTree(activity);

        addNodeList(activity);

        activity.layoutXProperty().set(mouseX);
        activity.layoutYProperty().set(mouseY);
        paneGraph.getChildren().add(activity);

        EnableResizeAndDrag.make(activity);

        //Substituir por enableObject();
        enableCreateLine(activity);
        mouseEvents(activity);
        keyPressed(activity);

        activateAccProperties();
        txt_act_name.setText(activity.getName());
        txt_act_activation.setText("./experiment.cmd");
        txt_act_input_filename.setText("input_" + txt_act_name.getText() + ".txt");
        txt_act_output_filename.setText("output_" + txt_act_name.getText() + ".txt");

        activity.setActivation(txt_act_activation.getText().trim());
        activity.setInput_filename(txt_act_input_filename.getText().trim());
        activity.setOutput_filename(txt_act_output_filename.getText().trim());
        activity.setType(chb_act_type.getSelectionModel().getSelectedItem().toString());
        activity.setTimeCommand((Integer) chb_sleeptime.getSelectionModel().getSelectedItem());

        clearFieldsActivity();//Limpa os campos necessários
//        FieldType.FILE.getController().clearList(); //Limpa o formulário e a lista de fields
//        removeFormFields();
//        newFormFields();
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

    Node nodeStart = null;
    Node nodeEnd = null;

    private void enableCreateLine(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (me) -> {
            arrastou = true;
        });
        node.setOnMouseClicked((me) -> {
            if (!arrastou) {
                if (line == null) {
                    try {
                        line = new Relation("Rel_" + Integer.toString(relations.size() + 1), node.getScene(), paneGraph, (Relation l) -> {
                            paneGraph.getChildren().remove(l);
                            line = null;
                        });
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    line.setNodeStart(node);
                    paneGraph.getChildren().add(line);

                    if (node instanceof Activity) {
                        nodeStart = (Activity) node;
                    }
                    if (node instanceof Entity) {
                        nodeStart = (Entity) node;
                    }
                    if (node instanceof Agent) {
                        nodeStart = (Agent) node;
                    }
                } else {
                    line.setNodeEnd(node);
                    if (!isConnected(nodeStart, nodeEnd)) {
                        relations.add(line);
                        mouseEvents(line);
                        //Conexão entre duas Activities
                        if (node instanceof Activity && nodeStart instanceof Activity) {
                            nodeEnd = (Activity) node;

                            sendRelation(line);

                            Activity newActivity = (Activity) nodeStart;
                            Entity entity = null;
                            try {
                                entity = new Entity("out_" + newActivity.getName(), Entity.TYPE.FILE, newActivity, null, null); //Entity criada entre duas activities
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            newActivity.setUsed(entity);
                            addNodeList(entity);
                            addEntityTree(entity);
                        }
                        //Conexão entre um Agent e uma Activity
                        if (node instanceof Agent && nodeStart instanceof Activity) {
                            nodeEnd = (Agent) node;
                            Activity newActivity = (Activity) nodeStart;
                            Agent agent = null;
                            try {
                                agent = new Agent("Agent", Agent.TYPE.USER, newActivity);
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            addNodeList(agent);
                        }
                        //Conexão entre um Agent e uma Entity
                        if (node instanceof Agent && nodeStart instanceof Entity) {
                            nodeEnd = (Agent) node;
                            Entity newEntity = (Entity) nodeStart;
                            Agent agent = null;
                            try {
                                agent = new Agent("Agent", Agent.TYPE.USER, newEntity);
                            } catch (NoSuchAlgorithmException ex) {
                                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            List<Agent> agents = Arrays.asList(agent);
                            newEntity.setWasAttributedTo(agents);
                            addNodeList(agent);
                        }
                        line = null;
                    } else {
                        paneGraph.getChildren().remove(line);
                        nodeStart = null;//Colocado na conexão entre activities que já estão conectadas
                        nodeEnd = null;//Colocado na conexão entre activities que já estão conectadas
                    }
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

        chb_sleeptime.getItems().addAll(10, 20, 30, 40, 50, 60);
        chb_sleeptime.getSelectionModel().select(2);

        newFormFields();//Novo formulário de fields

        //Permitindo inserir activity com um duplo click
        paneGraph.setOnMouseClicked((me) -> {
            if (me.getClickCount() == 2) {
                try {
                    mouseX = me.getX();
                    mouseY = me.getY();
                    insertActivity();
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void newFormFields() {
        //Formulário Fields
        acpane_fields.getChildren().add(FieldType.FILE.getController().getNode(new HashMap<>(), new HashMap<>()));

        //Adicionando evento no botão do formulário de fields
        FieldType.FILE.getController().getButtonAddField().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Activity sel = (Activity) selected;
//                    sel.addField(new Field(FieldType.FILE.getController().addField()));
                    FieldType.FILE.getController().addField();
                    setFieldsInActivity();
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro ao tentar inserir um field");
                    System.out.println(e.getMessage());
                }
            }
        });

        //Adicionando evento no botão do formulário de fields
        FieldType.FILE.getController().getButtonDelField().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Activity sel = (Activity) selected;
                    sel.delField(FieldType.FILE.getController().delField());
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro ao tentar remover um field");
                    System.out.println(e.getMessage());
                }
            }
        });

        //Adicionando evento no botão do formulário de fields
//        FieldType.FILE.getController().getButtonFinishField().setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    setFieldsInActivity();
//                } catch (Exception e) {
//                    System.out.println("Ocorreu um erro ao tentar finalizar um field");
//                    System.out.println(e.getMessage());
//                }
//            }
//        });
    }

    public void clearFieldsActivity() {
        txt_act_description.setText("");
        ta_commands.setText("");
        chb_parallel.getSelectionModel().selectFirst();
        chb_cloud.getSelectionModel().selectFirst();
        chb_act_type.getSelectionModel().selectFirst();
        chb_sleeptime.getSelectionModel().select(2);
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

        chb_act_type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Apagar se não for usar
            }

        });
    }

    public void fieldsFocus() {

        this.txt_act_name.focusedProperty().addListener(new ChangeListener<Boolean>() {
            //Controla o recebimento e perda de foco do txt_act_name    
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                } else {
                    if (txt_act_name.getText().equals("") || txt_act_name.getText().equals("<<empty>>")) {
                        Dialogs.create()
                                .owner(null)
                                .title("Activity Empty")
                                .masthead(null)
                                .message("Activity empty! Please, inform the name!")
                                .showInformation();
                        txt_act_name.requestFocus();
                        txt_act_name.setText("<<empty>>");
                        setNameActivity();
                        txt_act_name.selectAll();
                    }
//                    if (isActivityExist(txt_act_name.getText())) {
//                        Dialogs.create()
//                                .owner(null)
//                                .title("Activity Duplicate")
//                                .masthead(null)
//                                .message("Activity duplicate! Please, change the name!")
//                                .showInformation();
//                        txt_act_name.requestFocus();
//                        txt_act_name.setText("<<empty>>");
//                        setNameActivity();
//                        txt_act_name.selectAll();
//                    }
                }
            }
        });

        chb_act_type.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                } else {
                    setDataSelectObj();
                }
            }
        });

        chb_sleeptime.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                } else {
                    setDataSelectObj();
                }
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

    public void addNodeList(Node node) {
        this.nodes.add(node);
    }

    public void setNameActivity() {
//        this.activity.setName(txt_act_name.getText());
        this.activity = (Activity) this.selected;
        this.activity.setName(txt_act_name.getText());

        this.txt_act_input_filename.setText("input_" + txt_act_name.getText() + ".txt");
        this.txt_act_output_filename.setText("output_" + txt_act_name.getText() + ".txt");

        //Altera o nome da activity na Tree
//        treeRoot.getChildren().get(0).getChildren().addAll(Arrays.asList(
//                new TreeItem<String>(activity.getName())));
    }

    public void keypressFields() {
        //Grava as informações ao digitar os dados
        txt_act_name.setOnKeyReleased((me) -> {
            setDataSelectObj();
        });

        txt_act_description.setOnKeyReleased((me) -> {
            setDataSelectObj();
        });

//        ta_commands.setOnKeyReleased((me) ->{
//            setDateSelectObj();
//        });
    }

    public void setDataSelectObj() {
        //Seta os dados do objeto selecionado
        this.activity = (Activity) this.selected;
        this.activity.setName(txt_act_name.getText());
        this.activity.setDescription(txt_act_description.getText());
        this.txt_act_input_filename.setText("input_" + this.activity.getName() + ".txt");
        this.txt_act_output_filename.setText("output_" + this.activity.getName() + ".txt");

        this.activity.setType(chb_act_type.getSelectionModel().getSelectedItem().toString());
        this.activity.setTimeCommand((Integer) chb_sleeptime.getSelectionModel().getSelectedItem());
        //Altera o nome da activity na Tree
//        treeRoot.getChildren().get(0).getChildren().addAll(Arrays.asList(
//                new TreeItem<String>(activity.getName())));
    }

    public void insertNameWorkflow() {
        TP_Workflow_name.setText("Workflow: " + txtTagWorkflow.getText());
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
        this.activity.setTimeCommand((Integer) chb_sleeptime.getValue());
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
        //Envia activity para o servidor ao finalizar a edição dela
        sendActivity(this.activity);
    }

    public void closeWindow() {
        System.exit(0);
    }

    public void initializeTreeWork() {
        treeRoot.getChildren().addAll(Arrays.asList(
                new TreeItem<String>("Activities"),
                new TreeItem<String>("Entities"),
                new TreeItem<String>("Agents")));

        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);
        APane_workflow.getChildren().add(treeView);
    }

    public void insertEntityFile() throws NoSuchAlgorithmException {
        Text title = new Text("File");
        Entity entity = new Entity(title.getText(), Entity.TYPE.FILE, null, null, null);

        paneGraph.getChildren().add(entity);

        //Substituir por enableObject();
        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
//        System.out.println("ID: "+entity.getIdObject());
    }

    public void insertEntityComputer() throws NoSuchAlgorithmException {
        Text title = new Text("Computer");
        Entity entity = new Entity(title.getText(), Entity.TYPE.COMPUTER, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);

        addEntityTree(entity);
    }

    public void insertEntityParameter() throws NoSuchAlgorithmException {
        Text title = new Text("Parameter");
        Entity entity = new Entity(title.getText(), Entity.TYPE.PARAMETER, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertEntityNote() throws NoSuchAlgorithmException {
        Text title = new Text("Note");
        Entity entity = new Entity(title.getText(), Entity.TYPE.NOTE, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertEntityVMachine() throws NoSuchAlgorithmException {
        Text title = new Text("V. Machine");
        Entity entity = new Entity(title.getText(), Entity.TYPE.VIRTUAL_MACHINE, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertAgentUser() throws NoSuchAlgorithmException {
        Text title = new Text("User");
        Agent agent = new Agent(title.getText(), Agent.TYPE.USER);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentSoftware() throws NoSuchAlgorithmException {
        Text title = new Text("Software");
        Agent agent = new Agent(title.getText(), Agent.TYPE.SOFTWARE);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentHardware() throws NoSuchAlgorithmException {
        Text title = new Text("Hardware");
        Agent agent = new Agent(title.getText(), Agent.TYPE.HARDWARE);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentOrganization() throws NoSuchAlgorithmException {
        Text title = new Text("Organization");
        Agent agent = new Agent(title.getText(), Agent.TYPE.ORGANIZATION);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    //Pegar o item selecionado na TreeView
    public void getSelectedTreeItem() {
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue,
                    Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                System.out.println("Selected Text : " + selectedItem);
            }
        });
    }

    public void createDefaultAgents() throws IOException, NoSuchAlgorithmException {
        SystemInfo si = new SystemInfo();
        Agent organization = new Agent("UFT", Agent.TYPE.ORGANIZATION);
        Agent user = new Agent(System.getProperty("user.name"), Agent.TYPE.USER);
        Agent hardware = new Agent(si.getHardware() + "- CPU(" + Runtime.getRuntime().availableProcessors() + ")", Agent.TYPE.HARDWARE);
        Agent software = new Agent("Scicumulus - " + System.getProperty("os.name") + " - " + System.getProperty("os.arch"), Agent.TYPE.SOFTWARE);
        agents = Arrays.asList(organization, user, hardware, software);
    }

    public void addActivityTree(Activity activity) {
        treeRoot.getChildren().get(0).getChildren().addAll(Arrays.asList(
                new TreeItem<String>(activity.getName())));
    }

    public void addAgentTree(Agent agent) {
        treeRoot.getChildren().get(2).getChildren().addAll(Arrays.asList(
                new TreeItem<String>(agent.getType() + ": " + agent.getName())));
    }

    public void addEntityTree(Entity entity) {
        treeRoot.getChildren().get(1).getChildren().addAll(Arrays.asList(
                new TreeItem<String>(entity.getType() + ": " + entity.getName())));
    }

    public void createLocalRepository() throws IOException {
//        SSH_old ssh = new SSH_old();
//        String path = System.getProperty("user.dir")+"/src/main/java/br/com/uft/scicumulus";        
////        git.init(path);
//        ssh.sendFiles(path, "/deploy");
    }

    public boolean isFieldEmpty() {
        List<TextField> errors = new ArrayList<>();
        for (TextField field : this.fieldsRequired) {
            if (field.getText().isEmpty()) {
                errors.add(field);
            }
        }
        return !errors.isEmpty();
//        if (txtTagWorkflow.getText().isEmpty())
//            return true;
//        return false;
    }

    public void selectDirectoryPrograms() {
        //Selecionando o Diretório -- Verificar se vou deixar
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory with the programs");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(null);
        for (File file : selectedDirectory.listFiles()) {
            System.out.println(file.getName());
        }
        this.directoryPrograms = selectedDirectory.toString();
    }

    public void addProgram() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select the program");
        File file = chooser.showOpenDialog(null);
        this.programs.add(file);
    }

    private void copyPrograms() throws IOException {
        String destination = this.directoryExp;
        for (File file : this.programs) {
            SystemInfo si = new SystemInfo();
            si.copyFile(file.getPath() + "/" + file.getName(), destination + "/" + file.getName());
//            Utils.copyFile(file, destination);
        }
    }

    private void createMachinesConf() throws IOException {
        String text = "# Number of Processes\n"
                + txt_number_machines.getText() + "\n"
                + "# Protocol switch limit\n"
                + txt_protocol_s_l.getText() + "\n"
                + "# Entry in the form of machinename@port@rank\n"
                + ta_name_machines.getText() + "\n";
        Utils.createFile(this.directoryExp + "/machines.conf", text);
    }

    //Método utilizado para diversos testes
    public void teste() throws NoSuchAlgorithmException, FileNotFoundException, IOException, Exception {
//        this.directoryExp = dirProject.getAbsolutePath() +"/"+txtExpDirWorkflow.getText().trim();
//        String[] dirComplete = this.directoryExp.split(this.dirProject.getAbsolutePath())[1].split("/");
//        String dirLocal = this.directoryDefaultFiles + dirComplete[0];
        sendWorkflow("/home/fredsilva/Documentos/fred/workflow_ary", this.txt_server_directory.getText().trim());
        System.out.println("Enviando para o servidor...");
    }

    public void saveAs() throws FileNotFoundException, IOException {
        try {
            FileChooser project = new FileChooser();
            project.setInitialDirectory(new File(System.getProperty("user.home")));
            project.setTitle("Save As");
            File fileProject = project.showSaveDialog(this.paneGraph.getScene().getWindow());

            dirProject = new File(fileProject.getAbsolutePath());
            dirProject.mkdir();

            Utils.saveFile(dirProject.getAbsolutePath() + "/activities.sci", this.nodes);
//            Utils.saveFile(dirProject.getAbsolutePath() + "/relations.sci", this.relations);

            ConfigProject config = new ConfigProject();
            config.setNameProject(txt_name_workflow.getText().trim());
            config.setDateCreateProject(new Date());
            config.setDateLastAlterProject(new Date());
            config.setFileActivities("activities.sci");
            config.setFileRelations("relations.sci");
            Utils.saveFileJson(dirProject.getAbsolutePath() + "/project.json", config);

            this.directoryDefaultFiles = dirProject.getAbsolutePath() + "/files";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() throws FileNotFoundException, IOException {
        try {
            Utils.saveFile(dirProject.getAbsolutePath() + "/activities.sci", this.nodes);
//            Utils.saveFile(dirProject.getAbsolutePath() + "/relations.sci", this.relations);
            //Alterar data da última alteração do arquivo e gravar novamente no json
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restaurando() throws FileNotFoundException, IOException, ClassNotFoundException {
        try {
            FileInputStream fileStream = new FileInputStream(dirProject.getAbsolutePath() + "/activities.sci");
            ObjectInputStream os = new ObjectInputStream(fileStream);
            Object workflow = os.readObject();
            List<Node> nodes = (List<Node>) workflow;
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            FileInputStream fileStream2 = new FileInputStream(dirProject.getAbsolutePath() + "/relations.sci");
//            ObjectInputStream os2 = new ObjectInputStream(fileStream2);
//            Object rel = os2.readObject();
//            List<Relation> relation = (List<Relation>) rel;
//            os2.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void setFieldsInActivity() {
        //Setando a lista na activity
        List<Field> fields = FieldType.FILE.getController().getFields();
        Activity sel = (Activity) this.selected;
        sel.setFields(fields);

//        FieldType.FILE.getController().clearList();
    }

    public void changedFields() {
        ta_commands.focusedProperty().addListener(new ChangeListener<Boolean>() {
            //Pegando comandos para montar o arquivo experiment.cmd
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    listCommands = Arrays.asList(ta_commands.getText().split("\n"));
                }
                activity.setCommands(listCommands);
            }
        });
    }

    public void mouseEvents(Shape node) {
        node.setOnMouseEntered((me) -> {
            node.onMouseClicked();
            this.selected = node;
            keyPressed((Node) this.selected);
            try {
                setDataObjSelected((Node) this.selected);
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        node.setOnMouseExited((me) -> {
            node.onMouseExit();
        });

        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent me) -> {
            //Define posição onde o elemento está
            node.setPositionX(node.layoutXProperty().floatValue());
            node.setPositionY(node.layoutYProperty().floatValue());
//           System.out.println("X: "+node.getPositionX()+" Y: "+node.getPositionY());
        });
    }

    public void mouseEvents(Relation line) {
        line.setOnMouseClicked((me) -> {
            line.onMouseClicked();
            this.selected = line;
            keyPressed((Relation) line);
        });

        line.setOnMouseExited((me) -> {
            line.onMouseExit();
        });
    }

    public void keyPressed(Node node) {
        node.getScene().setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                paneGraph.getChildren().remove(node);
                deleteSelectedNode();
                if (node instanceof Activity) {
                    //Todo - Remover os elementos da Treeview
                    List<Activity> indexRemove = new ArrayList<>();
                    for (Activity act : this.activities) {
                        if (node.equals(act)) {
                            indexRemove.add(act);
                        }
                    }
                    for (int i = 0; i < indexRemove.size(); i++) {
                        this.activities.remove(indexRemove.get(i));
                    }
                }

                List<Relation> indexRemove = new ArrayList<>();
                for (Relation rel : this.relations) {
                    if (rel.nodeStart.equals(node) || rel.nodeEnd.equals(node)) {
                        paneGraph.getChildren().remove(rel);
                        indexRemove.add(rel);
                        this.selected = null;
                    }
                }
                for (int i = 0; i < indexRemove.size(); i++) {
                    this.relations.remove(indexRemove.get(i));
                }
            }

            //Salvar
            if (event.isControlDown() && event.getCode().equals(KeyCode.S)) {
                try {
                    if (saveAs == false) {
                        saveAs();
                        saveAs = true;
                    } else {
                        save();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //Salvar como
            if (event.isControlDown() && event.isShiftDown() && event.getCode().equals(KeyCode.S)) {
                try {
                    if (saveAs == false) {
                        saveAs();
                        saveAs = true;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /*
     Seta dos dados do componente selecionado
     */
    public void setDataObjSelected(Node selected) throws NoSuchFieldException {
        //Preenche os campos com os dados do objeto selecionado ao selecionar o objeto
        if (selected instanceof Activity) {
            Activity activitySelected = (Activity) selected;
            txt_act_name.setText(activitySelected.getName());
            txt_act_description.setText(activitySelected.getDescription());
            txt_act_activation.setText(activitySelected.getActivation());
            txt_act_input_filename.setText(activitySelected.getInput_filename());
            txt_act_output_filename.setText(activitySelected.getOutput_filename());

            chb_act_type.getSelectionModel().select(activitySelected.getType());
            chb_sleeptime.getSelectionModel().select(activitySelected.getTimeCommand());
            
            //Resgatando os fields ao selecionar a Activity
            FieldType.FILE.getController().addListField(activitySelected.getFields());
            
            try {
                String text_ta_commands = "";
                for (String command : activitySelected.getCommands()) {
                    text_ta_commands += command + "\n";
                }
                ta_commands.setText(text_ta_commands);
            } catch (Exception e) {
                ta_commands.setText("");
            }
        }
        //Colocar aqui os outros Entity, Agent...
        //chb_act_type.selectionModelProperty().setValue(activitySelected.getType());
    }

    private void deleteSelectedNode() {

    }

    @FXML
    protected void locateFile(ActionEvent event) throws IOException {
        try {
            dirChooser.setTitle("Select the programs directory");
            dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            this.dirPrograms = dirChooser.showDialog(this.paneGraph.getScene().getWindow());
            ObservableList<String> items = FXCollections.observableArrayList(dirPrograms.getAbsoluteFile().toString());
            this.list_programs.setItems(items);

            this.programsSelected = this.dirPrograms.listFiles();
        } catch (Exception ex) {
        }
    }

    @FXML
    protected void selectedExpDir() throws IOException {
        //Seleciona o diretório de expansão
        try {
            dirExpChooser.setTitle("Select Expansion Directory");
//            dirExpChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            txtExpDirWorkflow.setText(dirChooser.showDialog(this.paneGraph.getScene().getWindow()).toString());
            txt_exp_dir.setText(txtExpDirWorkflow.getText());
        } catch (Exception ex) {
        }
    }

    public Boolean isActivityExist(String name) {
        for (Activity activity : this.activities) {
            if (activity.getName().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public void isActivityEmpty() {
        //Verificar se há alguma activity sem conexão
        for (Activity activity : this.activities) {
            for (Relation line : this.relations) {
                if (activity.equals(line.nodeStart) || activity.equals(line.nodeEnd)) {
//                    return true;
                    System.out.println("Há uma activity sem conexão!");
                }
            }
        }
//        return false;
    }

    public void firstActivity() {
        //Verificar qual é a primeira Activity do workflow
        Activity first = null;
        for (Relation line : this.relations) {
            first = (Activity) line.nodeStart;
            System.out.println("Node Start:" + first.getName());
            first = (Activity) line.nodeEnd;
            System.out.println("Node End:" + first.getName());

//            for (Activity activity: this.activities){
//                if (!line.nodeEnd.equals(activity)) {
//                    first = activity;                                                            
////                    break;
//                }
//            }
        }
        //System.out.println(first.getName());
//        for (Activity activity : this.activities) {
//            for (Relation line : this.relations) {
//                if (!line.nodeEnd.equals(activity)) {
//                    first = activity;                                                            
//                    //break;
//                }
//            }
//        }        
//        System.out.println(first.getName());
    }

    public Boolean isConnected(Node nodeStart, Node nodeEnd) {
        //Verifica se os nodes já estão conectados
        for (Relation rel : this.relations) {
            if (rel.nodeStart.equals(nodeStart) && rel.nodeEnd.equals(nodeEnd)) {
                Dialogs.create()
                        .owner(null)
                        .title("Relation Duplicate")
                        .masthead(null)
                        .message("Relation Duplicate!")
                        .showInformation();
                return true;
            }
        }
        return false;
    }

    boolean saveAs = false;
    TextField txt_name_workflow = new TextField();
    TextField txt_exp_dir = new TextField();
    TextArea ta_parameters = new TextArea();
//    Button btn_select_exp_dir = new Button("Select");
    Button btn_create = new Button("Create");

    @FXML
    protected void newWorkflow(ActionEvent event) {
        Stage dialogAPPLICATION_MODAL = new Stage();
        dialogAPPLICATION_MODAL.initModality(Modality.APPLICATION_MODAL);

        try {
            Scene sceneAPPLICATION_MODAL = new Scene(VBoxBuilder.create()
                    .children(
                            new Text("Workflow Name"),
                            txt_name_workflow,
                            new Text("Parameters"),
                            ta_parameters,
                            //                            new Text("Select Expansion Directory"),
                            //                            btn_select_exp_dir,
                            //                            txt_exp_dir,
                            new Text(""),
                            btn_create)
                    .alignment(Pos.TOP_LEFT)
                    .padding(new Insets(10))
                    .build());

            dialogAPPLICATION_MODAL.setTitle("New Workflow");
            dialogAPPLICATION_MODAL.setScene(sceneAPPLICATION_MODAL);
            dialogAPPLICATION_MODAL.show();

//            btn_select_exp_dir.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
//                try {
//                    selectedExpDir();
//                } catch (IOException ex) {
//                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            });
            btn_create.addEventHandler(MouseEvent.MOUSE_CLICKED, (me) -> {
                if (!(txt_name_workflow.getText().trim().equals("") || ta_parameters.getText().trim().equals(""))) {
                    TP_Workflow_name.setText("Workflow: " + txt_name_workflow.getText().trim());
                    txtTagWorkflow.setText(txt_name_workflow.getText().trim());
                    txtTagWorkflow.setText(txt_name_workflow.getText().trim());
//                    txtExpDirWorkflow.setText(txt_name_workflow.getText().toLowerCase().trim());
                    txtExpDirWorkflow.setText("expdir");

                    dialogAPPLICATION_MODAL.close();
                    activeComponentsWiw();
                    saveAs = false;
//                    initClient();
                } else {
                    Dialogs.create()
                            .owner(null)
                            .title("Information")
                            .masthead(null)
                            .message("Fields required!")
                            .showInformation();
                    if (txt_name_workflow.getText().trim().equals("")) {
                        txt_name_workflow.requestFocus();
                    } else {
                        ta_parameters.requestFocus();
                    }
                }
            });
        } catch (Exception ex) {
        }
    }

    public void activeComponentsWiw() {
        //Ativa os componentes da janela ao criar um workflow
        btn_activity.disableProperty().setValue(false);
        btn_entity_file.disableProperty().setValue(false);
        btn_entity_comp.disableProperty().setValue(false);
        btn_entity_note.disableProperty().setValue(false);
        btn_entity_param.disableProperty().setValue(false);
        btn_entity_vm.disableProperty().setValue(false);
        btn_agent_hardware.disableProperty().setValue(false);
        btn_agent_org.disableProperty().setValue(false);
        btn_agent_software.disableProperty().setValue(false);
        btn_agent_user.disableProperty().setValue(false);
        acc_configuration.disableProperty().setValue(false);
        acc_programs.disableProperty().setValue(false);
        TP_Workflow_name.disableProperty().setValue(false);
    }

    private void createParameterTxt(String content) throws IOException {
        Utils.createFile(this.directoryExp + "/parameter.txt", content);
    }

    private void enableObject(Node node) {
        //Habilita os objetos para serem arrastados e para os enventos do mouse
        if (node instanceof Activity) {
            EnableResizeAndDrag.make((Activity) node);
            enableCreateLine((Activity) node);
            mouseEvents((Activity) node);
            keyPressed((Activity) node);
        }
    }

    /*
     * Kryonet    
     */
    public void initClient() {
        try {
            this.clientKryo = new ClientKryo();
        } catch (Exception e) {
            System.out.println("Server offline");
        }

    }

    public void sendActivity(Activity activity) {
        //Envia Activity para o servidor
//        this.clientKryo.send(new ActivityKryo().convert(activity));
        send(new ActivityKryo().convert(activity));
    }

    public void sendRelation(Relation relation) {
        //Envia Relation para o servidor
        RelationKryo relationKryo = new RelationKryo();
        relationKryo.setName(relation.getName());
        relationKryo.setNodeStart(new ActivityKryo().convert((Activity) relation.getNodeStart()));
        relationKryo.setNodeEnd(new ActivityKryo().convert((Activity) relation.getNodeEnd()));
        send(relationKryo);
    }

    Client client;
    ActivityKryo activityKryo;
    RelationKryo relationKryo;
    List<ActivityKryo> activitiesKryo = new ArrayList<>();
    List<RelationKryo> relationsKryo = new ArrayList<>();

    private void runClient() {
        client = new Client();
        CommonsNetwork.registerClientClass(client);

        ((Kryo.DefaultInstantiatorStrategy) client.getKryo().getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

        new Thread(client).start();

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println(connection.getRemoteAddressTCP().getHostString() + " Conectou");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof ActivityKryo) {
                    activityKryo = (ActivityKryo) object;
                    activitiesKryo.add(activityKryo);
                    System.out.println("Recebendo Activity no cliente: " + activityKryo.getIdObject());
                    Activity activity;
                    try {
                        activity = new Activity().convert(activityKryo);
                        if (activityKryo.getOperation().equals(Operation.INSERT)) {
                            //Insere activity                                                        
                            activities.add(activity);

                            //Atualiza a Interface
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    paneGraph.getChildren().add(activity);
                                    enableObject(activity);                                    
                                }
                            });
                        }
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (object instanceof RelationKryo) {
                    relationKryo = (RelationKryo) object;
                    relationsKryo.add(relationKryo);
                    Relation relation;
                    try {
                        relation = new Relation().convert(relationKryo);
                        System.out.println("Recebendo Relation no cliente: " + relation.getIdObject());
                        //Atualiza a Interface
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                paneGraph.getChildren().add(relation);
                            }
                        });
                    } catch (NoSuchAlgorithmException ex) {
                        Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                }
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("Client Disconnected");
            }
        });

        try {
            /* Make sure to connect using both tcp and udp port */
            client.connect(5000, "127.0.0.1", CommonsNetwork.TCP_PORT, CommonsNetwork.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public void send(Object object) {
        if (object instanceof ActivityKryo) {
            ActivityKryo act = (ActivityKryo) object;
            System.out.println("Enviando " + act + "...");
            client.sendTCP(act);
        }

        if (object instanceof RelationKryo) {
            RelationKryo relation = (RelationKryo) object;
            System.out.println("Enviando " + relation.getName() + "...");
            client.sendTCP(relation);
        }
    }

//    public List<ActivityKryo> getActivityKryo() {
//        List<ActivityKryo> listActKryo = activitiesKryo;
//        activitiesKryo = new ArrayList<>();
//        return listActKryo;
//    }
//
//    public List<RelationKryo> getRelationsKryo() {
//        List<RelationKryo> listRelKryo = relationsKryo;
//        relationsKryo = new ArrayList<>();
//        return listRelKryo;
//    }
//    private void threadMonitoring() {
//        //Monitora o recebimento de objetos do servidor
//        new Thread(() -> {
//            while (0 < 1) {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                List<ActivityKryo> activitiesKryo = clientKryo.getActivityKryo();
//                if (activitiesKryo == null) {
//                    System.out.println("Nenhuma activity nova");
//                } else {
//                    for (ActivityKryo actKryo : activitiesKryo) {
//                        try {
//                            Activity activity = new Activity().convert(actKryo);
//                            if (actKryo.getOperation().equals(Operation.INSERT)) {
//                                //Insere activity
//                                this.activities.add(activity);
//                                System.out.println("Activities: "+this.activities.size());
//                            }
//                            if (actKryo.getOperation().equals(Operation.REMOVE)) {
//                                //Remove activity
//                                Activity actRemoved = null;
//                                for(int i = 0; i < this.activities.size(); i++){
//                                    if(activity.getIdObject().equals(this.activities.get(i).getIdObject())){
//                                        actRemoved = this.activities.get(i);
//                                    }
//                                }
//                                this.activities.remove(actRemoved);
//                            }
//                            Activity act = new Activity();
//                            act.setName(actKryo.getName());
//                            System.out.println("Activity: " + act.getName());
//                        } catch (NoSuchAlgorithmException ex) {
//                            Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
////                    System.out.println("List Size: " + activitiesKryo.size());
//                }
//
//                List<RelationKryo> relationsKryo = clientKryo.getRelationsKryo();
//                if (relationsKryo == null) {
//                    System.out.println("Nenhuma relation nova");
//                } else {
//                    for (RelationKryo relKryo : relationsKryo) {
//                        Relation rel = new Relation(relKryo.getName());
//                        System.out.println("Relation: " + rel.getName());
//                    }
//                    System.out.println("List Size: " + relationsKryo.size());
//                }
//            }
//        }).start();
//    }        
}
