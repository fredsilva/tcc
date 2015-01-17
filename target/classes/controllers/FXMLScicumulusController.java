/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Agent;
import br.com.uft.scicumulus.graph.EnableResizeAndDrag;
import br.com.uft.scicumulus.graph.Entity;
import br.com.uft.scicumulus.graph.Relation;
import br.com.uft.scicumulus.graph.Shape;
import br.com.uft.scicumulus.tables.Command;
import br.com.uft.scicumulus.utils.SSH;
import br.com.uft.scicumulus.utils.SystemInfo;
import br.com.uft.scicumulus.utils.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javax.swing.JOptionPane;
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
public class FXMLScicumulusController implements Initializable {

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
    private TitledPane acc_properties_activity, acc_properties_relation;
    @FXML
    private ChoiceBox chb_parallel, chb_cloud, chb_act_type, chb_sleeptime;
    @FXML
    private Label lb_number_machines, lb_login_cloud, lb_password_cloud;
    @FXML
    private TextField txt_number_machines, txt_login_cloud, txt_server_directory, txt_programs_direct;
    @FXML
    private TextField txt_act_input_filename, txt_act_output_filename;
    @FXML
    private PasswordField txt_password_cloud;
    @FXML
    private TextField txt_act_name, txt_act_description, txt_act_activation, txt_protocol_s_l;
    @FXML
    private TextArea ta_name_machines, ta_commands;
    @FXML
    private Button btn_salvar_activity, btn_activity;
    @FXML
    private ListView<String> list_programs = new ListView<>();
    @FXML
    TableView<Command> table_commands = new TableView<>();

    final ObservableList<Command> data_commands = FXCollections.observableArrayList(
            new Command("cd /root")
    );

    List commands = Arrays.asList(
            new Command("cd /root"),
            new Command("cd /teste")
    );

    @FXML
    TableColumn<Command, String> col_commands = new TableColumn<Command, String>("Command");

//    ObservableList<Command> dataCommand = FXCollections.observableArrayList(
//            new Command("cd /root")
//    );
    FileChooser fileChosser = new FileChooser();
    DirectoryChooser dirChooser = new DirectoryChooser();
    String directoryDefaultFiles = "src/main/java/br/com/uft/scicumulus/files/";
    String directoryExp, directoryPrograms;

    File dirPrograms;
    File[] programsSelected;

    Activity activity;

    Object selected = null;

    private List<Activity> activities = new ArrayList<Activity>();
    private List<String> listCommands = new ArrayList<String>();
    private List<Agent> agents;
    private List<Node> nodes = new ArrayList<Node>();//Lista de objetos do tipo Node
    private List<TextField> fieldsRequired;
    private List<File> programs = new ArrayList<File>();

    //Tree Workflow
    final TreeItem<String> treeRoot = new TreeItem<String>("Workflow Composition");
    final TreeView treeView = new TreeView();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFullScreen(paneGraph);
        initComponents();
        changedFields();
        txtFocus();
        choiceBoxChanged();
        initializeTreeWork();
        getSelectedTreeItem();
        initializeTableCommands();
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
            createDefaultAgents();
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
        this.directoryExp = this.directoryDefaultFiles + Utils.slashInString(txtExpDirWorkflow.getText().trim());
        File dir = new File(this.directoryExp);
        dir.mkdirs();
        File dirPrograms = new File(this.directoryExp + "/programs");
//            this.directoryPrograms = dirPrograms.getPath();
        dirPrograms.mkdir();

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
        hydraWorkflow.addAttribute("tag", txtTagWorkflow.getText().replace(" ", "").trim());
        hydraWorkflow.addAttribute("description", txtDescriptionWorkflow.getText());
        hydraWorkflow.addAttribute("exectag", txtExecTagWorkflow.getText());
        hydraWorkflow.addAttribute("expdir", txtExpDirWorkflow.getText());

        Element hydraActivity;
        for (Activity act : this.activities) {
            hydraActivity = hydraWorkflow.addElement("HydraActivity");
            hydraActivity.addAttribute("tag", act.getTag().replace(" ", "").trim());
            hydraActivity.addAttribute("description", act.getDescription());
            hydraActivity.addAttribute("type", act.getType());
            hydraActivity.addAttribute("templatedir", act.getTemplatedir());
            hydraActivity.addAttribute("activation", act.getActivation());

            dir = new File(this.directoryExp + "template_" + act.getName());
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
        FileOutputStream fos = new FileOutputStream(this.directoryExp + "SciCumulus.xml");
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(fos, format);
        writer.write(doc);
        writer.flush();

        //Criando o arquivo machines.conf
        createMachinesConf();

        //Copiando arquivos para o diretório programs        
        Utils.copyFiles(this.dirPrograms, directoryExp + "/programs/");

//            sendWorkflow(this.directoryExp, "/deploy/experiments");
//            sendWorkflow(this.directoryExp, this.txt_server_directory.getText().trim());            
        String[] dirComplete = this.directoryExp.split(this.directoryDefaultFiles)[1].split("/");
        String dirLocal = this.directoryDefaultFiles + dirComplete[0];
        sendWorkflow(dirLocal, this.txt_server_directory.getText().trim());
//        }else{
//            JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
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

    public void insertActivity() {
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

        paneGraph.getChildren().add(activity);

        EnableResizeAndDrag.make(activity);

        enableCreateLine(activity);
        mouseEvents(activity);
        keyPressed(activity);

        activateAccProperties();
        txt_act_name.setText(activity.getName());
        txt_act_input_filename.setText("input_" + txt_act_name.getText() + ".txt");
        txt_act_output_filename.setText("output_" + txt_act_name.getText() + ".txt");
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

    Node nodeStart = null;
    Node nodeEnd = null;

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
                    relations.add(line);
                    mouseEvents(line);
                    //Conexão entre duas Activities
                    if (node instanceof Activity && nodeStart instanceof Activity) {
                        nodeEnd = (Activity) node;
                        Activity newActivity = (Activity) nodeStart;
                        Entity entity = new Entity("out_" + newActivity.getName(), Entity.TYPE.FILE, newActivity, null, null);//Entity criada entre duas activities                                                                
                        newActivity.setUsed(entity);
                        addNodeList(entity);
                        addEntityTree(entity);
                    }
                    //Conexão entre um Agent e uma Activity
                    if (node instanceof Agent && nodeStart instanceof Activity) {
                        nodeEnd = (Agent) node;
                        Activity newActivity = (Activity) nodeStart;
                        Agent agent = new Agent("Agent", Agent.TYPE.USER, newActivity);
                        addNodeList(agent);
                    }
                    //Conexão entre um Agent e uma Entity
                    if (node instanceof Agent && nodeStart instanceof Entity) {
                        nodeEnd = (Agent) node;
                        Entity newEntity = (Entity) nodeStart;
                        Agent agent = new Agent("Agent", Agent.TYPE.USER, newEntity);
                        List<Agent> agents = Arrays.asList(agent);
                        newEntity.setWasAttributedTo(agents);
                        addNodeList(agent);
                    }

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

        chb_sleeptime.getItems().addAll(10, 20, 30, 40, 50, 60);
        chb_sleeptime.getSelectionModel().select(2);
//        Image image = new Image(getClass().getResourceAsStream("activity.png"));
//        btn_activity.setGraphic(new ImageView(image));    

    }

    public void clearFieldsActivity() {
        txt_act_description.setText("");
        ta_commands.setText("");
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

    public void txtFocus() {

        this.txt_act_name.focusedProperty().addListener(new ChangeListener<Boolean>() {
            //Controla o recebimento e perda de foco do txt_act_name    
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                } else {
                    if(txt_act_name.getText().equals("") || txt_act_name.getText().equals("<<empty>>")){
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
                    if (isActivityExist(txt_act_name.getText())) {                                                
                        Dialogs.create()
                                .owner(null)
                                .title("Activity Duplicate")
                                .masthead(null)
                                .message("Activity duplicate! Please, change the name!")
                                .showInformation();               
                        txt_act_name.requestFocus();
                        txt_act_name.setText("<<empty>>");
                        setNameActivity();
                        txt_act_name.selectAll();
                    }
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
        this.activity.setName(txt_act_name.getText());
        this.txt_act_input_filename.setText("input_" + txt_act_name.getText() + ".txt");
        this.txt_act_output_filename.setText("output_" + txt_act_name.getText() + ".txt");

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
                new TreeItem<String>("Entities"),
                new TreeItem<String>("Agents")));

        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);
        APane_workflow.getChildren().add(treeView);
    }

    public void insertEntityFile() {
        Text title = new Text("File");
        Entity entity = new Entity(title.getText(), Entity.TYPE.FILE, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertEntityComputer() {
        Text title = new Text("Computer");
        Entity entity = new Entity(title.getText(), Entity.TYPE.COMPUTER, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);

        addEntityTree(entity);
    }

    public void insertEntityParameter() {
        Text title = new Text("Parameter");
        Entity entity = new Entity(title.getText(), Entity.TYPE.PARAMETER, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertEntityNote() {
        Text title = new Text("Note");
        Entity entity = new Entity(title.getText(), Entity.TYPE.NOTE, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertEntityVMachine() {
        Text title = new Text("V. Machine");
        Entity entity = new Entity(title.getText(), Entity.TYPE.VIRTUAL_MACHINE, null, null, null);

        paneGraph.getChildren().add(entity);

        EnableResizeAndDrag.make(entity);
        enableCreateLine(entity);
        mouseEvents(entity);
        keyPressed(entity);
        addEntityTree(entity);
    }

    public void insertAgentUser() {
        Text title = new Text("User");
        Agent agent = new Agent(title.getText(), Agent.TYPE.USER);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentSoftware() {
        Text title = new Text("Software");
        Agent agent = new Agent(title.getText(), Agent.TYPE.SOFTWARE);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentHardware() {
        Text title = new Text("Hardware");
        Agent agent = new Agent(title.getText(), Agent.TYPE.HARDWARE);

        paneGraph.getChildren().add(agent);

        EnableResizeAndDrag.make(agent);

        enableCreateLine(agent);
        mouseEvents(agent);
        keyPressed(agent);
        addAgentTree(agent);
    }

    public void insertAgentOrganization() {
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

    public void createDefaultAgents() throws IOException {
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

    private void initializeTableCommands() {
//        col_commands.setCellValueFactory(new PropertyValueFactory("Comm"));       
        col_commands.setPrefWidth(200);
        table_commands.setItems(FXCollections.observableArrayList(commands));
        table_commands.getColumns().addAll(col_commands);
//        col_commands = new TableColumn();
//        col_commands.setText("Commandd");
//        table_commands = new TableView();
//        table_commands.setItems(commands);
//        table_commands.getColumns().addAll(col_commands);
//        col_commands.setCellValueFactory(new PropertyValueFactory<Command, String>("Command"));
//        table_commands.setItems(dataCommand);
    }

    private void createMachinesConf() throws IOException {
        String text = "# Number of Processes\n"
                + txt_number_machines.getText() + "\n"
                + "# Protocol switch limit\n"
                + txt_protocol_s_l.getText() + "\n"
                + "# Entry in the form of machinename@port@rank\n"
                + ta_name_machines.getText() + "\n";
        Utils.createFile(this.directoryExp + "machines.conf", text);
    }

    //Método utilizado para diversos testes
    public void teste() {
        for (Relation rel : this.relations) {
            System.out.println(rel);
        }

        for (Activity act : this.activities) {
            System.out.println(act);
        }
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
//            try {
//                setDataSelected();
//            } catch (NoSuchFieldException ex) {
//                Logger.getLogger(FXMLScicumulusController.class.getName()).log(Level.SEVERE, null, ex);
//            }
        });

        node.setOnMouseExited((me) -> {
            node.onMouseExit();
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
        });
    }

    /*
     Seta dos dados do componente selecionado
     */
    public void setDataSelected() throws NoSuchFieldException {
        txt_act_name.setText(selected.getClass().getDeclaredField("name").toString());
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

    public Boolean isActivityExist(String name) {
        for (Activity activity : this.activities) {
            if (activity.getName().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }
}
