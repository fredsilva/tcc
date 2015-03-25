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
import br.com.uft.scicumulus.kryonet.RelationKryo;
import br.com.uft.scicumulus.kryonet.WorkflowKryo;
import br.com.uft.scicumulus.utils.SSH;
import br.com.uft.scicumulus.utils.SystemInfo;
import br.com.uft.scicumulus.utils.Utils;
import com.esotericsoftware.kryonet.Client;
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
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
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
    public TextField txtTagWorkflow, txtDescriptionWorkflow, txtExecTagWorkflow, txtExpDirWorkflow, txt_key;
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
    private Button btn_salvar_activity, btn_new, btn_save, btn_saveas, btn_run, btn_get_key_workflow;
//    @FXML
//    private Button btn_entity_note, btn_entity_vm, btn_agent_user, btn_agent_software, btn_agent_hardware, btn_agent_org;
    @FXML
    private Button btn_field_add, btn_select_programs;
    @FXML
    private ListView<String> list_programs = new ListView<>();

    @FXML
    private AnchorPane acpane_fields;
    @FXML
    private MenuItem mi_save, mi_new, mi_saveas, menuItem_import_workflow, mi_export;
    @FXML
    Menu menu_workflow;

    FileChooser fileChosser = new FileChooser();
    DirectoryChooser dirChooser = new DirectoryChooser();
    DirectoryChooser dirExpChooser = new DirectoryChooser();
    FileChooser inputFileChooser = new FileChooser();

//    String directoryDefaultFiles = "src/main/java/br/com/uft/scicumulus/files/";
    String directoryDefaultFiles = null;
    String directoryExp, directoryPrograms;

    File dirPrograms;
    File[] programsSelected;
    File inputFile;

    Activity activity;

    Object selected = null;
    String nameWorkflowTeste;

    public List<Activity> activities = new ArrayList<Activity>();
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
        initClient();

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
//        relationInput.addAttribute("name", "IListFits");
        relationInput.addAttribute("name", "input_workflow");
        relationInput.addAttribute("filename", this.inputFile.getName());

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
//            createParameterTxt(ta_parameters.getText());
        } catch (IOException ex) {
            Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Copiando arquivos para o diretório programs        
        Utils.copyFiles(this.dirPrograms, directoryExp + "/programs/");
        Utils.copyFiles(this.inputFile, directoryExp + "/" + this.inputFile.getName());

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

        activity.setPositionX((float) mouseX);
        activity.layoutXProperty().set(mouseX);
        activity.setPositionY((float) mouseY);
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
    public List<Relation> relations = new ArrayList<>();

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
                            Activity actStart = (Activity) nodeStart;
                            Activity actEnd = (Activity) nodeEnd;
                            System.out.println("ActStart: " + actStart.getIdObject());
                            System.out.println("ActEnd: " + actEnd.getIdObject());
                            sendRelation(line, Operation.INSERT);

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
                    FieldType.FILE.getController().addField();
                    setFieldsInActivity();
                    sendActivity(sel, Operation.UPDATE);
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
                    sendActivity(sel, Operation.UPDATE);
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro ao tentar remover um field");
                    System.out.println(e.getMessage());
                }
            }
        });
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
                    setDataSelectObj();
//                    Activity activity = (Activity) selected;
//                    updateActivity(activity);

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

        if(activities.contains(this.activity)){
            sendActivity(this.activity, Operation.UPDATE);
        }
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
        sendActivity(this.activity, Operation.INSERT);
    }

    public void updateActivity(Activity activity) {
        for (Activity act : this.activities) {
            if (act.getIdObject().equals(activity.getIdObject())) {
                act.setName(activity.getName());
                act.setActivation(activity.getActivation());
                act.setCommands(activity.getCommands());
                act.setDescription(activity.getDescription());
                act.setFields(activity.getFields());
                act.setInput_filename(activity.getInput_filename());
                act.setOutput_filename(activity.getOutput_filename());
                act.setLogin(activity.getLogin());
                act.setNum_machines(activity.getNum_machines());
                act.setPassword(activity.getPassword());
                act.setTag(activity.getTag());
                act.setTemplatedir(activity.getTemplatedir());
                act.setTimeCommand(activity.getTimeCommand());
                act.setType(activity.getType());
                
//                Activity sel = act;
//                FieldType.FILE.getController().addField();
//                setFieldsInActivity(act);
                break;
            }
        }
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
        Activity sel = (Activity) selected;
        for (Field field : sel.getFields()) {
            System.out.println("Field Name: " + field.getName());
            System.out.println("Field Type: " + field.getType());
            System.out.println("Field Decimal Places: " + field.getDecimalPlaces());
        }
    }

    public void saveAs() throws FileNotFoundException, IOException {
        try {
            FileChooser project = new FileChooser();
            project.setInitialDirectory(new File(System.getProperty("user.home")));
            project.setTitle("Save As");
            File fileProject = project.showSaveDialog(this.paneGraph.getScene().getWindow());

            dirProject = new File(fileProject.getAbsolutePath());
            dirProject.mkdir();

//            Utils.saveFile(dirProject.getAbsolutePath() + "/activities.sci", this.nodes);
//            Utils.saveFile(dirProject.getAbsolutePath() + "/relations.sci", this.relations);
            ConfigProject config = new ConfigProject();
            config.setNameProject(txt_name_workflow.getText().trim());
            config.setDateCreateProject(new Date());
            config.setDateLastAlterProject(new Date());
            config.setFileActivities("activities.sci");
            config.setFileRelations("relations.sci");
            Utils.saveFileJson(dirProject.getAbsolutePath() + "/project.json", config);

            this.directoryDefaultFiles = dirProject.getAbsolutePath() + "/files";
//            clientKryo.send(setDataWorkflowKryo());    
//            setDataInCollatorator();
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
    }
    
    public void setFieldsInActivity(Activity activity) {
        //Setando a lista na activity para resolver o problema do client
        List<Field> fields = FieldType.FILE.getController().getFields();        
        activity.setFields(fields);
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

//        txt_act_name.focusedProperty().addListener(new ChangeListener<Boolean>(){
//
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if(!newValue){
//                    
//                }
//            }
//            
//        });
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
                removeElements(node);

                if (node instanceof Activity) {
                    Activity activity = (Activity) node;
                    sendActivity(activity, Operation.REMOVE);
                }
                if (node instanceof Relation) {
                    Relation relation = (Relation) node;
                    sendRelation(relation, Operation.REMOVE);
                    System.out.println("Enviando remoção de Relation para o servidor");
                }
//                if (node instanceof Activity) {
                //                    //Todo - Remover os elementos da Treeview
                //                    List<Activity> indexRemove = new ArrayList<>();
                //                    for (Activity act : this.activities) {
                //                        if (node.equals(act)) {
                //                            indexRemove.add(act);
                //                        }
                //                    }
                //                    for (int i = 0; i < indexRemove.size(); i++) {
                //                        this.activities.remove(indexRemove.get(i));
                //                    }
                //                }
                //
                //                List<Relation> indexRemove = new ArrayList<>();
                //                for (Relation rel : this.relations) {
                //                    if (rel.nodeStart.equals(node) || rel.nodeEnd.equals(node)) {
                //                        paneGraph.getChildren().remove(rel);
                //                        indexRemove.add(rel);
                //                        this.selected = null;
                //                    }
                //                }
                //                for (int i = 0; i < indexRemove.size(); i++) {
                //                    this.relations.remove(indexRemove.get(i));
                //                }
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
            txt_input_file.setText(txtExpDirWorkflow.getText());
        } catch (Exception ex) {
        }
    }

    @FXML
    protected void selectedInputFile() throws IOException {
        //Seleciona o arquivo de entrada do workflow
        try {
            inputFileChooser.setTitle("Select Input File");
            inputFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            this.inputFile = inputFileChooser.showOpenDialog(this.paneGraph.getScene().getWindow());
            txt_input_file.setText(inputFile.getAbsolutePath());
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
    CheckBox ckb_iscolaboration = new CheckBox("Is Colaboration?");
    TextField txt_name_workflow = new TextField();
    TextField txt_input_file = new TextField();
    TextField txt_key_workflow = new TextField();
//    TextArea ta_parameters = new TextArea();
    Button btn_select_input_file = new Button("Select Input File");
    Button btn_create = new Button("Create");

    @FXML
    protected void newWorkflow(ActionEvent event) {
        txt_key_workflow.setDisable(true);
        txt_input_file.setDisable(true);
        Stage dialogAPPLICATION_MODAL = new Stage();
        dialogAPPLICATION_MODAL.initModality(Modality.APPLICATION_MODAL);

        try {
            Scene sceneAPPLICATION_MODAL = new Scene(VBoxBuilder.create()
                    .children(
                            ckb_iscolaboration,
                            new Text("Key Workflow"),
                            txt_key_workflow,
                            new Text("Workflow Name"),
                            txt_name_workflow,
                            //                            new Text("Parameters"),
                            //                            ta_parameters,
                            //                            new Text("Select Expansion Directory"),
                            new Text(""),
                            btn_select_input_file,
                            txt_input_file,
                            new Text(""),
                            btn_create)
                    .alignment(Pos.TOP_LEFT)
                    .padding(new Insets(10))
                    .build(), 500, 250);
            dialogAPPLICATION_MODAL.setResizable(true);
            dialogAPPLICATION_MODAL.setTitle("New Workflow");
            dialogAPPLICATION_MODAL.setScene(sceneAPPLICATION_MODAL);
            dialogAPPLICATION_MODAL.show();

            btn_select_input_file.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me) -> {
                try {
                    selectedInputFile();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            ckb_iscolaboration.addEventFilter(MouseEvent.MOUSE_CLICKED, (me) -> {
                if (ckb_iscolaboration.isSelected()) {
                    txt_key_workflow.setDisable(false);
                    txt_name_workflow.setDisable(true);
                    btn_select_input_file.setDisable(true);
                    txt_name_workflow.requestFocus();
                } else {
                    txt_key_workflow.setDisable(true);
                    txt_name_workflow.setDisable(false);
                    btn_select_input_file.setDisable(false);
                    txt_key_workflow.requestFocus();
                }
            });

            btn_create.addEventHandler(MouseEvent.MOUSE_CLICKED, (me) -> {
                if (ckb_iscolaboration.isSelected()) {
                    if (!txt_key_workflow.getText().trim().equals(null)) {
                        WorkflowKryo workflow = new WorkflowKryo();
                        workflow.setKeyWorkflow(txt_key_workflow.getText().trim());

//                        client.sendTCP(workflow);                        
                        clientKryo.send(workflow);

                        setDataInCollatorator();//Preencher os dados do workflow com os dados originais

                        if (this.workflowExist) {
                            dialogAPPLICATION_MODAL.close();
                            activeComponentsWiw();

                            saveAs = false;

                            setDataInitialWorkflow(this.workflowKryo);
                            clientKryo.send(1);
                        } else {
                            Dialogs.create()
                                    .owner(null)
                                    .title("Error")
                                    .masthead(null)
                                    .message("Workflow not exist!")
                                    .showInformation();
                        }
                    } else {
                        Dialogs.create()
                                .owner(null)
                                .title("Information")
                                .masthead(null)
                                .message("Key Workflow is required!")
                                .showInformation();
                    }
                } else {
                    if (!(txt_name_workflow.getText().trim().equals("") || txt_input_file.getText().trim().equals(""))) {
                        setDataInitialWorkflow(null);

                        try {

                            dialogAPPLICATION_MODAL.close();
                            activeComponentsWiw();
                            saveAs = false;

                            clientKryo.send(setDataWorkflowKryo());//Envia o workflow para o servidor
                        } catch (NoSuchAlgorithmException ex) {
                            Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
//                            ta_parameters.requestFocus();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDataInitialWorkflow(Object object) {
        if (object instanceof WorkflowKryo) {
            WorkflowKryo workflow = (WorkflowKryo) object;
            TP_Workflow_name.setText("Workflow: " + workflow.getNameWorkflow());
            txtTagWorkflow.setText(workflow.getTag());
            txtDescriptionWorkflow.setText(workflow.getDescription());
            txtExecTagWorkflow.setText(workflow.getTagExecution());
            txtExpDirWorkflow.setText(workflow.getExpDirectory());
            txt_server_directory.setText(workflow.getServerDirectory());
            txtNameDatabase.setText(workflow.getDatabaseName());
            txtServerDatabase.setText(workflow.getDatabaseServer());
            txtPortDatabase.setText(workflow.getDatabasePort().toString());
            txtUsernameDatabase.setText(workflow.getDatabaseUsername());
            txtPasswordDatabase.setText(workflow.getDatabasePassword());
            txt_number_machines.setText(workflow.getExecutionNumMachines().toString());
            txt_protocol_s_l.setText(workflow.getExecutionProtocolo());
            ta_name_machines.setText(workflow.getExecutionNameMachines());
            txt_key.setText(workflow.getKeyWorkflow());
//            listCommands.set(0, workflow.getPrograms());
            List<Object> fields = Arrays.asList(
                    TP_Workflow_name, txtDescriptionWorkflow, txtExecTagWorkflow, txtTagWorkflow,
                    txtExpDirWorkflow, txt_server_directory, txtNameDatabase, txtServerDatabase,
                    txtPortDatabase, txtPortDatabase, txtUsernameDatabase, txtPasswordDatabase,
                    txt_number_machines, txt_protocol_s_l, ta_name_machines, btn_select_programs
            );
            disableFields(fields);
        } else {
            TP_Workflow_name.setText("Workflow: " + txt_name_workflow.getText());
            txtTagWorkflow.setText(txt_name_workflow.getText());
            txtExecTagWorkflow.setText(txt_name_workflow.getText());
            txtExpDirWorkflow.setText("expdir");
            txt_server_directory.setText("/scicumulus/experiments");
        }
    }

    public void activeComponentsWiw() {
        //Ativa os componentes da janela ao criar um workflow        
        btn_save.disableProperty().setValue(false);
        btn_saveas.disableProperty().setValue(false);
//        btn_entity_note.disableProperty().setValue(false);
        btn_run.disableProperty().setValue(false);
//        btn_entity_vm.disableProperty().setValue(false);
//        btn_agent_hardware.disableProperty().setValue(false);
//        btn_agent_org.disableProperty().setValue(false);
//        btn_agent_software.disableProperty().setValue(false);
//        btn_agent_user.disableProperty().setValue(false);
        acc_configuration.disableProperty().setValue(false);
        acc_programs.disableProperty().setValue(false);
        TP_Workflow_name.disableProperty().setValue(false);
    }

    private void createParameterTxt(String content) throws IOException {
        Utils.createFile(this.directoryExp + "/parameter.txt", content);
    }

    public void enableObject(Node node) {
        //Habilita os objetos para serem arrastados e para os enventos do mouse
        if (node instanceof Activity) {
            EnableResizeAndDrag.make((Activity) node);
            enableCreateLine((Activity) node);
            mouseEvents((Activity) node);
            keyPressed((Activity) node);
        }
    }

    public void disableFields(List<Object> fields) {
        for (Object field : fields) {
            if (field instanceof TextInputControl) {
                TextInputControl f = (TextInputControl) field;
                f.disableProperty().setValue(true);
            }
            if (field instanceof ButtonBase) {
                ButtonBase button = (ButtonBase) field;
                button.disableProperty().setValue(true);
            }
        }
    }
    /*
     * Kryonet    
     */

    public void sendActivity(Activity activity, Operation operation) {
        for(Field field: activity.getFields()){
            System.out.println("Field in Master: "+field.getName());
        }
        send(new ActivityKryo().convert(activity), operation);
    }

    public void sendRelation(Relation relation, Operation operation) {
        System.out.println("Relation Removed: " + relation.getIdObject());
        send(new RelationKryo().convert(relation), operation);
    }

    Client client;
    WorkflowKryo workflowKryo = new WorkflowKryo();
    ActivityKryo activityKryo;
    RelationKryo relationKryo;
    List<ActivityKryo> activitiesKryo = new ArrayList<>();
    List<RelationKryo> relationsKryo = new ArrayList<>();

    public void send(Object object, Operation operation) {
        if (object instanceof ActivityKryo) {
            ActivityKryo act = (ActivityKryo) object;
            act.setOperation(operation);
            System.out.println("Enviando " + act + "...");
//            client.sendTCP(act);
            clientKryo.send(act);
        }

        if (object instanceof RelationKryo) {
            RelationKryo relation = (RelationKryo) object;
            relation.setOperation(operation);
            System.out.println("Enviando " + relation.getName() + "...");
//            client.sendTCP(relation);
            clientKryo.send(relation);
        }
    }

    public void sendDelete(Object object) {
        System.out.println("Entrou do delete: " + object);
        if (object instanceof ActivityKryo) {
            ActivityKryo act = (ActivityKryo) object;
            act.setOperation(Operation.REMOVE);
            System.out.println("Enviando " + act + " para deletar");
//            client.sendTCP(act);
            clientKryo.send(act);
        }

        if (object instanceof RelationKryo) {
            RelationKryo relation = (RelationKryo) object;
            System.out.println("Enviando " + relation.getName() + "...");
//            client.sendTCP(relation);
            clientKryo.send(relation);
        }
    }

    public void setDataInCollatorator() {
        //Preenche os dados vindos do Workflow Mestre
        txtTagWorkflow.setText(this.workflowKryo.getTag());
        txtDescriptionWorkflow.setText(this.workflowKryo.getDescription());
        txtExecTagWorkflow.setText(this.workflowKryo.getTagExecution());
        txtExpDirWorkflow.setText(this.workflowKryo.getExpDirectory());
        txt_server_directory.setText(this.workflowKryo.getServerDirectory());
        acc_configuration.disableProperty().setValue(true);
        mi_save.setDisable(true);
        mi_saveas.setDisable(true);
        menuItem_import_workflow.setDisable(true);
        mi_export.setDisable(true);
        menu_workflow.setDisable(true);

    }

    private void initClient() {
        this.clientKryo = new ClientKryo(this);
    }

    public Boolean workflowExist;

    public void isWorkflowExist(Boolean bool) {
        this.workflowExist = bool;
    }

    public String nameWorkflow;

    public void getDataWorkflow(String name) {
        this.nameWorkflowTeste = name;
    }

    public void getWorkflowKryo(WorkflowKryo workflowKryo) {
        this.workflowKryo = workflowKryo;
//        System.out.println("Fred - Recebendo o Workflow na tela: "+workflowKryo.getNameWorkflow());
    }

    public Pane getPaneGraph() {
        return this.paneGraph;
    }

    private WorkflowKryo setDataWorkflowKryo() throws NoSuchAlgorithmException {
        WorkflowKryo workflow = new WorkflowKryo(txt_name_workflow.getText().trim());
        workflow.setTag(txtTagWorkflow.getText());
        workflow.setDescription(txtDescriptionWorkflow.getText());
        workflow.setExpDirectory(txtExpDirWorkflow.getText());
        workflow.setServerDirectory(txt_server_directory.getText());
        workflow.setDatabaseName(txtNameDatabase.getText());
        workflow.setDatabaseServer(txtServerDatabase.getText());
        workflow.setDatabasePort(Integer.parseInt(txtPortDatabase.getText()));
        workflow.setDatabaseUsername(txtUsernameDatabase.getText());
        workflow.setDatabasePassword(txtPasswordDatabase.getText());
        workflow.setExecutionNumMachines(Integer.parseInt(txt_number_machines.getText()));
        workflow.setExecutionProtocolo(txt_protocol_s_l.getText());
        workflow.setExecutionNameMachines(ta_name_machines.getText());
        workflow.setPrograms(list_programs.getSelectionModel().getSelectedItem());
        return workflow;
    }

    public void getKeyWorkflowServer() {
        clientKryo.send(new String("getKey"));
    }

    public Boolean activityInList(Activity activity) {
        for (Activity act : this.activities) {
            if (act.getIdObject().equals(activity.getIdObject())) {
                return true;
            }
        }
        return false;
    }

    public Boolean relationInList(Relation relation) {
        for (Relation rel : this.relations) {
            if (rel.getIdObject().equals(relation.getIdObject())) {
                return true;
            }
        }
        return false;
    }

    public void removeElements(Node node) {
        //Remove as activities e relations da tela        
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

    public List<Relation> getRelationsRemove(Node node) {
        List<Relation> relationsRemove = new ArrayList<Relation>();
        if (node instanceof Activity) {
            Activity activity = (Activity) node;
            for (Relation relation : this.relations) {
                Activity nodeStart = (Activity) relation.getNodeStart();
                Activity nodeEnd = (Activity) relation.getNodeEnd();
                if (nodeStart.getIdObject().equals(activity.getIdObject()) || nodeEnd.getIdObject().equals(activity.getIdObject())) {
                    relationsRemove.add(relation);
                }
            }
        }
        return relationsRemove;
    }

    public Activity getActivity(String id) {
        for (Activity act : this.activities) {
            if (act.getIdObject().equals(id)) {
                return act;
            }
        }
        return null;
    }

    public Relation getRelation(String id) {
        for (Relation rel : this.relations) {
            if (rel.getIdObject().equals(id)) {
                return rel;
            }
        }
        return null;
    }
}
