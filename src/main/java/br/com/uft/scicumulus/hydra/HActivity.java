package br.com.uft.scicumulus.hydra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe HActivity
 *
 * @author Eduardo, Vítor, Pedro
 * @since 2011-01-04
 */
public class HActivity implements Serializable {

    protected HWorkflow workflow;
    protected Integer actID = null;

    public enum ActType {
        MAP, SPLIT_MAP, REDUCE, FILTER, SR_QUERY, JOIN_QUERY
    }
    protected ActType atype;
    protected String tag;
    protected String description;
    protected String templateDir;
    protected int numActivations = 0;

    public enum StatusType {

        BLOCKED, READY, RUNNING, PIPELINED, FINISHED
    }
    protected StatusType status = StatusType.READY;
    protected String activation;
    protected String extractor;
    protected java.util.Date startTime = null;
    protected java.util.Date endTime = null;
    protected ArrayList<HFile> files = new ArrayList<HFile>();
    protected ArrayList<HRelation> relations = new ArrayList<HRelation>();
    protected ArrayList<HField> fields = new ArrayList<HField>();
    protected String originalWorkspace = "";
    protected HActivity pipeline = null;
    protected int base = 1;
    protected boolean constrained = false;

    /**
     * Construtor da classe HActivity
     *
     * @param workflow
     */
    public HActivity(HWorkflow workflow) {
        this.workflow = workflow;
        if (workflow != null) {
            workflow.activities.add(this);
        }
    }

    /**
     * Obter o id do TemplateFile pelo nome do arquivo informado
     *
     * @param filename
     * @return long
     */
    public long getTemplateFileId(String filename) {
        for (HFile file : files) {
            if (file.fileName.equals(filename)) {
                return file.fileID;
            }
        }
        return 0;
    }

    /**
     * Obter o índice do HField pelo nome do HField
     *
     * @param name
     * @return int
     */
    public int getHFieldIndex(String name) {
        int result = -1;
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).name.compareTo(name) == 0) {
                return i;
            }
        }
        return result;
    }

    /**
     * Obter o HField através do nome informado do HField
     *
     * @param name
     * @return HField
     */
    public HField getFieldByName(String name) {
        HField result = null;
        int i = getHFieldIndex(name);
        if (i >= 0) {
            result = fields.get(i);
        }
        return result;
    }

    /**
     * Obter o HField pelo id do HField do argumento
     *
     * @param id
     * @return HField
     */
    public HField getFieldByID(int id) {
        for (int i = 0; i < fields.size(); i++) {
            HField field = fields.get(i);
            if (field.fieldID == id) {
                return field;
            }
        }
        return null;
    }

    /**
     * Gera as tarefas de Map
     *
     * @return void
     */
    public void generateMapTasks() {
        String inputFile = this.getInputFileName();

        try {
            Scanner scanner = null;
            //each line stores a task on the database with its respective input values
            FileInputStream fin = new FileInputStream(/*workflow.expDir +*/ inputFile);
            scanner = new Scanner(fin);

            int[] fieldIndex = this.getFieldsIndex(scanner);

            // process data
            numActivations = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.length() == 0) {
                    break;
                }
                numActivations++;
                //set the workspace of the task
                String folderActivity = workflow.expDir + this.tag + HydraUtils.SEPARATOR;
                String folderTask = folderActivity + numActivations + HydraUtils.SEPARATOR;
                HydraUtils.createDirectory(folderActivity);
                String[] inputLine = line.split(";");
                for (int i = 0; i < inputLine.length; i++) {
                    inputLine[i] = inputLine[i].trim();
                }
                //create the new task
                HActivation newTask = new HActivation(this);
                newTask.commandLine = this.activation;
                newTask.workspace = folderTask;
                for (int j = 0; j < inputLine.length; j++) {
                    int k = fieldIndex[j];
                    if (k < 0) {
                        continue;
                    }
                    HField field = fields.get(k);
                    if (field != null) {
                        String value = inputLine[j];
                        HValue inputValue = new HValue(field, this, newTask, HValue.ValueType.INPUT);
                        inputValue.rowNumber = numActivations;
                        inputValue.setValueFromString(value);
                    }
                }
                HProvenance.storeActivation(newTask);
            }
            this.status = StatusType.RUNNING;
            HProvenance.storeActivity(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Realiza a manipulação dos arquivos, respeitando as dependências e as relações estipuladas entre as atividades.
     *
     * @param value
     * @return void
     * @throws IOException
     * @throws InterruptedException
     */
    /**
     * Obtem o HRelation a partir do nome da relação passado como argumento
     *
     * @param relName
     * @return HRelation
     */
    protected HRelation getRelation(String relName) {
        for (int i = 0; i < relations.size(); i++) {
            HRelation relation = relations.get(i);
            if (relation.relName.equals(relName)) {
                return relation;
            }
        }
        return null;
    }

    /**
     * Obtêm o HRelation pelo tipo de relação informado como argumento
     *
     * @param relType
     * @return HRelation
     */
    protected HRelation getRelation(HRelation.RelType relType) {
        for (int i = 0; i < relations.size(); i++) {
            HRelation relation = relations.get(i);
            if (relation.relType == relType) {
                return relation;
            }
        }
        return null;
    }

    /**
     * Obtêm a relação de entrada da atividade que possui como dependência a
     * atividade que foi executada
     *
     * @param relType
     * @return HRelation
     */
    private ArrayList<HRelation> getDependencyRelation() {
        ArrayList<HRelation> list = new ArrayList<HRelation>();
        for (int i = 0; i < workflow.activities.size(); i++) {
            HActivity act = workflow.activities.get(i);
            for (int j = 0; j < act.relations.size(); j++) {
                HRelation relation = act.relations.get(j);
                if (relation.relDependency != null && relation.relDependency.equals(this.tag)) {
                    list.add(relation);
                }
            }
        }
        return list;
    }

    /**
     * Obtêm o arquivo de relação de saída para a atividade
     *
     * @return void
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createOutputRelation() throws SQLException, FileNotFoundException, IOException {
        ArrayList<HActivation> tasks = HProvenance.loadTask(this);

        String text = "";
        for (int i = 0; i < tasks.size(); i++) {
            HActivation task = tasks.get(i);

            //String file = task.workspace + getRelation(HRelation.RelType.OUTPUT).relFile;
            String file = task.workspace + "H_Relation.txt";
            String reader = HydraUtils.ReadFile(file);
            String[] lines = reader.split(System.getProperty("line.separator"));

            if (i == 0) {
                text += lines[0] + System.getProperty("line.separator");
            }
            for (int j = 1; j < lines.length; j++) {
                String line = lines[j];
                text += line + System.getProperty("line.separator");
            }
        }
        HydraUtils.WriteFile(workflow.expDir + getRelation(HRelation.RelType.OUTPUT).relFile, text);
        ArrayList<HRelation> relList = getDependencyRelation();
        for (int i = 0; i < relList.size(); i++) {
            HRelation relDependency = relList.get(i);
            if (relDependency != null) {
                HydraUtils.WriteFile(workflow.expDir + relDependency.relFile, text);
            }
        }
    }

    public void openQuery() {
        status = StatusType.RUNNING;
        String tableName = "";
        String inputFileName = "";
        String outputFileName = "";

        //Define wich are the variables of the method:
        for (int i = 0; i < relations.size(); i++) {
            if (relations.get(i).relType == HRelation.RelType.INPUT) {
                tableName = relations.get(i).relName;
                inputFileName = workflow.expDir + relations.get(i).relFile;
            } else {
                outputFileName = workflow.expDir + relations.get(i).relFile;
            }
        }
        String text = "";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");
            createTable(c, inputFileName, tableName, fields);

            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(activation);
            text = writeQueryResult(rs, outputFileName, fields);
            status = HActivity.StatusType.FINISHED;

        } catch (Exception e) {
            e.printStackTrace();
        }
        //adicionei para criar a relação de dependencia para a próxima atividade
        ArrayList<HRelation> relList = getDependencyRelation();
        for (int i = 0; i < relList.size(); i++) {
            HRelation relDependency = relList.get(i);
            if (relDependency != null) {
                HydraUtils.WriteFile(workflow.expDir + relDependency.relFile, text);
            }
        }
    }

    public void openJoinQuery() throws Exception {
        status = StatusType.RUNNING;
        ArrayList<HRelation> inputRelations = new ArrayList<HRelation>();
        HRelation outRelation = null;

        for (int i = 0; i < relations.size(); i++) {
            if (relations.get(i).relType == HRelation.RelType.OUTPUT) {
                outRelation = relations.get(i);
            } else {
                inputRelations.add(relations.get(i));
            }
        }

        Class.forName("org.hsqldb.jdbcDriver").newInstance();
        Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:.", "SA", "");

        for (int i = 0; i < inputRelations.size(); i++) {
            HRelation tempRelations = inputRelations.get(i);
            ArrayList<HField> tempFields = new ArrayList<HField>();
            for (int j = 0; j < fields.size(); j++) {
                if (fields.get(j).relation_input.compareTo(tempRelations.relName) == 0) {
                    tempFields.add(fields.get(j));
                }
            }
            
             // Daniel
            
            String inputRelationFileName = /*workflow.expDir +*/ tempRelations.relFile;
            createTable(c, inputRelationFileName, tempRelations.relName, tempFields);
        }

        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(activation);
        if (outRelation != null) {
            String outputRelationFileName = workflow.expDir + outRelation.relFile;
            String text = writeQueryResult(rs, outputRelationFileName, fields);
            status = HActivity.StatusType.FINISHED;

            //adicionei para criar a relação de dependencia para a próxima atividade
            //seria bom uniformisar depois
            ArrayList<HRelation> relList = getDependencyRelation();
            for (int i = 0; i < relList.size(); i++) {
                HRelation relDependency = relList.get(i);
                if (relDependency != null) {
                    HydraUtils.WriteFile(workflow.expDir + relDependency.relFile, text);
                }
            }
        } else {
            throw new Exception("The output relation has not been set");
        }
    }

    private static void createTable(Connection c, String fileName, String tableName, ArrayList<HField> fields) throws Exception {
        //This method reads the fileName file and creates a table with name tableName
        //on the c connection, that must be already connected
        Statement st = c.createStatement();
        int colsNumber = 0;

        String createTable = "";
        createTable += "CREATE TABLE " + tableName + " (";
        for (int i = 0; i < fields.size(); i++) {
            switch (fields.get(i).ftype) {
                case FLOAT:
                    createTable += fields.get(i).name + " FLOAT";
                    colsNumber++;
                    break;
                case STRING:
                    createTable += fields.get(i).name + " VARCHAR(100)";
                    colsNumber++;
                    break;
                case FILE:
                    createTable += fields.get(i).name + " VARCHAR(200)";
                    colsNumber++;
                    break;
                default:
                    Exception e = new Exception("Unrecognized HField type");
                    throw e;
            }
            if (i + 1 < fields.size()) {
                createTable += ", ";
            }
        }
        createTable += ")";
        st.execute(createTable);

        BufferedReader inBuffer = new BufferedReader(new FileReader(fileName));
        String inputHeader = inBuffer.readLine();
        String[] inputFieldNames = inputHeader.split(";");

        //This array stores the index of the columns that refers to a field on the input file
        // where fields[i] refers to column[colsIndex[i]]
        int[] colsIndex = new int[fields.size()];
        String fieldsNames = "";

        //Relate the columns to the fields        
        for (int i = 0; i < fields.size(); i++) {
            colsIndex[i] = getIndexFromFieldName(fields.get(i).name, inputFieldNames);
            fieldsNames += fields.get(i).name;
            if (i + 1 < fields.size()) {
                fieldsNames += ", ";
            }
        }

        String[] inputValues;
        String values = "";
        while (inBuffer.ready()) {
            inputValues = inBuffer.readLine().split(";");
            for (int i = 0; i < colsIndex.length; i++) {
                switch (fields.get(i).ftype) {
                    case FLOAT:
                        values += inputValues[colsIndex[i]];
                        break;
                    case STRING:
                    case FILE:
                        values += "'" + inputValues[colsIndex[i]] + "'";
                        break;
                    default:
                        Exception e = new Exception("Unrecognized HField type. ");
                        throw e;
                }
                if (i + 1 < colsIndex.length) {
                    values += ", ";
                }
            }

            String SQL = "INSERT INTO " + tableName + " (" + fieldsNames + ") VALUES (" + values + ");";
            st.execute(SQL);
            SQL = "";
            values = "";
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private static String writeQueryResult(ResultSet rs, String fileName, ArrayList<HField> fields) {
        try {
            File outputFile = new File(fileName);
            outputFile.createNewFile();
            BufferedWriter outputBuffer = new BufferedWriter(new FileWriter(outputFile));

            String outputLine = "";
            String output = "";
            boolean isFirst = true;
            while (rs.next()) {
                ResultSetMetaData rsMeta = rs.getMetaData();
                int resultsNumber = rsMeta.getColumnCount();
                if (isFirst) {
                    String outputHeader = "";
                    for (int i = 1; i <= resultsNumber; i++) {
                        outputHeader += rsMeta.getColumnName(i);
                        if (i < resultsNumber) {
                            outputHeader += ";";
                        } else {
                            outputHeader += System.getProperty("line.separator");
                        }
                    }
                    outputBuffer.write(outputHeader);
                    output += outputHeader;
                    outputBuffer.flush();
                    isFirst = false;
                }

                for (int i = 1; i <= resultsNumber; i++) {
                    if (rsMeta.getColumnType(i) == java.sql.Types.DOUBLE || rsMeta.getColumnType(i) == java.sql.Types.FLOAT) {
                        String aux = "";
                        aux = rsMeta.getColumnName(i);
                        for (int j = 0; j <= fields.size(); j++) {
                            if (fields.get(j).ftype == HField.FieldType.FLOAT) {
                                if (aux.compareTo(fields.get(j).name) == 0) {
                                    outputLine += HydraUtils.formatFloat(rs.getDouble(i), fields.get(j).decimalplaces);
                                    break;
                                }
                            }
                        }
                    } else {
                        outputLine += rs.getString(i);
                    }
                    if (i < resultsNumber) {
                        outputLine += ";";
                    } else {
                        outputLine += System.getProperty("line.separator");
                    }
                }
                outputBuffer.write(outputLine);
                output += outputLine;
                outputBuffer.flush();
                outputLine = "";
            }
            outputBuffer.close();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static int getIndexFromFieldName(String fieldName, String[] fields) {
        for (int j = 0; j < fields.length; j++) {
            if (fieldName.compareTo(fields[j]) == 0) {
                return j;
            }
        }
        return -1;
    }

    public boolean isConstrained() {
        return this.constrained;
    }

    public void setConstrainedTrue() {
        this.constrained = true;
    }

    public String getInputFileName() {
        String inputFile = null;
        for (HRelation relation : relations) {
            if (relation.relType == HRelation.RelType.INPUT) {
                inputFile = relation.relFile;
            }
        }
        return inputFile;
    }

    public int[] getFieldsIndex(Scanner s) {
        String line = s.nextLine();
        String[] fieldNames = null;
        if (line != null) {
            fieldNames = line.split(";");
        }
        int[] fieldIndex = new int[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = fieldNames[i].trim();
            fieldIndex[i] = getHFieldIndex(fieldNames[i]);
        }
        return fieldIndex;
    }

    void generateReduceTasks() throws FileNotFoundException, SQLException {
        String inputFile = this.getInputFileName();

        //each line stores a task on the database with its respective input values
        FileInputStream fin = new FileInputStream(workflow.expDir + inputFile);
        Scanner scanner = new Scanner(fin);
        int[] fieldIndex = this.getFieldsIndex(scanner);

        numActivations = 0;
        String aggregationField = this.activation.split("%")[1].substring(1);
        String aggregationFieldValue = "";
        HActivation newActivation = null;

        String folderActivity = workflow.expDir + this.tag + HydraUtils.SEPARATOR;
        HydraUtils.createDirectory(folderActivity);
        int rowNumber = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            rowNumber++;
            if (line.length() == 0) {
                break;
            }
            
            String[] inputLine = line.split(";");

            //create the new activation
            for (int j = 0; j < inputLine.length; j++) {
                inputLine[j] = inputLine[j].trim();
                int k = fieldIndex[j];
                if (k < 0) {
                    continue;
                }
                HField field = fields.get(k);
                if (field != null) {
                    String value = inputLine[j];
                    if (field.name.equals(aggregationField)) {
                        if (!value.equals(aggregationFieldValue)) {
                            //set the workspace of the task
                            String folderTask = folderActivity + numActivations + HydraUtils.SEPARATOR;
                            numActivations++;
                            aggregationFieldValue = value;
                            if (newActivation != null) {
                                HProvenance.storeActivation(newActivation);
                            }
                            newActivation = new HActivation(this);
                            //get what is inside the parenthesis
                            Pattern p = Pattern.compile("\\((.*?)\\)", Pattern.DOTALL);
                            Matcher m = p.matcher(this.activation);
                            m.find();
                            newActivation.commandLine = m.group(1);
                            newActivation.workspace = folderTask;
                        }
                    }
                    HValue inputValue = new HValue(field, this, newActivation, HValue.ValueType.INPUT);
                    inputValue.rowNumber = rowNumber;
                    inputValue.setValueFromString(value);
                }
            }
        }
        HProvenance.storeActivation(newActivation);
        this.status = StatusType.RUNNING;
        HProvenance.storeActivity(this);



    }
}
