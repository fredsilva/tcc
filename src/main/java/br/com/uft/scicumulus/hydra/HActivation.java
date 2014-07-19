package br.com.uft.scicumulus.hydra;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import br.com.uft.scicumulus.hydra.HActivity.StatusType;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Classe HActivation apresenta os dados de uma tarefa
 *
 * @author Jonas, Eduardo, Vítor
 * @since 2010-12-25
 *
 * 
 */
public class HActivation implements Serializable {

    protected int HProvenance; /* Esta classe nao pode chamar HProvenance */

    public static HActivation WAIT_TASK = new HActivation(null);
    //databse attributes
    protected HActivity act;
    protected HActivation pipelinedFrom = null;
    protected Integer taskID = null;
    protected Integer processor = null;
    protected Integer exitStatus = null;
    protected String commandLine = null;
    protected String workspace = null;
    protected String stdErr = null;
    protected String stdOut = null;
    protected Date startTime = null;
    protected Date endTime = null;
    protected StatusType status = StatusType.READY;
    protected List<HValue> iValues = new ArrayList<HValue>();
    protected List<HValue> oValues = new ArrayList<HValue>();
    protected List<HFile> files = new ArrayList<HFile>();
    protected List<String> reduceLoop = null;

    /**
     * Construtor da classe HActivation. Passa como parâmetro a atividade que a
     * tarefa pertence
     *
     * @param act
     */
    public HActivation(HActivity act) {
        this.act = act;
    }

    /**
     * Método que ativa uma tarefa
     */
    @SuppressWarnings("CallToThreadDumpStack")
    private void activate() {
        try {
            stdErr = "";
            stdOut = "";
            HydraUtils.createDirectory(workspace);
            if (!act.templateDir.equals("")) {
                HydraUtils.copyTemplateFiles(act.templateDir, workspace);
            }
            if (this.act.atype == HActivity.ActType.REDUCE) {
                this.reduceActivation();
            } else {
                this.manipulateFile();
                this.instrumentFiles();
                commandLine = this.processTags(commandLine);
            }
            HydraUtils.deleteFile("H_Relation.txt", workspace);
        } catch (Exception ex) {
            stdErr += ex.getStackTrace();
            ex.printStackTrace();
        }
    }

    private void manipulateFile() throws IOException, InterruptedException {
        for (int i = 0; i < iValues.size(); i++) {
            HValue value = iValues.get(i);
            if (value.field.ftype != HField.FieldType.FILE) {
                continue;
            }
            String destination = value.getValueAsString();
            value.fileDir = workspace;
            if (value.field.operation != null) {
                if (value.field.operation.equals(HField.FileOper.MOVE) || value.field.operation.equals(HField.FileOper.MOVE_DELETE)) {
                    HydraUtils.moveFile(destination, workspace);
                } else if (value.field.operation.equals(HField.FileOper.COPY) || value.field.operation.equals(HField.FileOper.COPY_DELETE)) {
                    HydraUtils.copyFile(destination, workspace);
                }
            } else {
                HydraUtils.moveFile(destination, workspace);
            }
        }
    }

    /**
     * Método que realiza a instrumentação dos arquivos
     *
     * @param templateFiles
     */
    private void instrumentFiles() throws IOException {
        for (int i = 0; i < act.files.size(); i++) {
            HFile templateFile = act.files.get(i);

            String fileName = workspace + templateFile.fileName;

            if (templateFile.instrumented) {
                String textFile = HydraUtils.ReadFile(fileName);
                textFile = processTags(textFile);
                HydraUtils.WriteFile(fileName, textFile);
            }
        }
    }

    protected String processTags(String command, List<HValue> values) {
        String result = command;
        for (int i = 0; i < values.size(); i++) {
            String key = values.get(i).field.name;
            String value = values.get(i).getValueAsString();
            String tag = "%=" + key + "%";
            result = result.replaceAll(tag, value);
        }

        String patternDir = "%=DIREXP%";
        String directoryConverted = workspace;
        if (HydraUtils.isWindows()) {
            directoryConverted = workspace.replaceAll("\\\\", "@/@");
        }
        result = result.replaceAll(patternDir, directoryConverted);

        if (HydraUtils.isWindows()) {
            result = result.replaceAll("/", "\\\\");
        }

        return result;
    }

    /**
     * Método que realiza a troca das tags por valores
     *
     * @param string
     * @return
     */
    protected String processTags(String string) {
        /* este metodo vai ter que ser melhorado recebendo a relação de origem para trocar de tags */
        return this.processTags(string, iValues);
    }

    private void preparePipeline() {
        if (pipelinedFrom != null) {
            for (int i = 0; i < pipelinedFrom.oValues.size(); i++) {
                HValue pipelinedFromValue = pipelinedFrom.oValues.get(i);
                HField field = this.act.getFieldByName(pipelinedFromValue.field.name);
                if ((field != null) && (field.relation_input != null)) {
                    HValue value = new HValue(field, this.act, this, HValue.ValueType.INPUT);
                    value.copyValues(pipelinedFromValue);
                }
            }
        }
    }

    /**
     * Método que executa a tarefa
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public void executeTask() {
        try {
            startTime = new Date();
            preparePipeline();
            switch (act.atype) {
                case MAP:
                    activate();
                    executeMap();
                    executeExtractor(act);
                    createOutput();
                    status = HActivity.StatusType.FINISHED;
                    break;
                case SPLIT_MAP:
                    activate();
                    executeMap();
                    createSplitMapOutput();
                    status = HActivity.StatusType.FINISHED;
                    break;
                case REDUCE:
                    activate();
                    reduce();
                    createReduceOutput();
                    status = HActivity.StatusType.FINISHED;
                    break;
            }
            endTime = new Date();
        } catch (Exception ex) {
            ex.printStackTrace();
            stdErr += ex.getMessage();
        }
    }

    private void executeCommand(String cmd) throws IOException, InterruptedException {
        String command = cmd + " > " + workspace + "H_Result.txt" + " 2> " + workspace + "H_Err.txt";
        exitStatus = HydraUtils.runCommand(command, workspace);
        stdErr = HydraUtils.ReadFile(workspace + "H_Err.txt");
        stdOut = HydraUtils.ReadFile(workspace + "H_Result.txt");

        File dir = new File(workspace);
        FileFilter fileFilter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };
        File[] workfiles = dir.listFiles(fileFilter);
        if (workfiles != null) {
            for (int i = 0; i < workfiles.length; i++) {
                HFile taskFile = new HFile(act, this);
                taskFile.fileDir = workspace;
                taskFile.fileName = workfiles[i].getName();
            }
        }
        dir = null;
    }

    /**
     * Método que executa uma atividade do tipo MAP
     *
     * @param run
     * @throws IOException
     * @throws SQLException
     * @throws InterruptedException
     */
    private void executeMap() throws IOException, SQLException, InterruptedException {
        this.executeCommand(commandLine);
    }

    /**
     * Método que cria a saída de cada tarefa de SplitMap
     *
     *  @return void
     */
    private void createSplitMapOutput() throws SQLException, FileNotFoundException, Exception {
        int abs = 0;
        String originalData = "";
        for (int i = 0; i < iValues.size(); i++) {
            HValue value = iValues.get(i);
            if (value.field.relation_output != null) {
                oValues.add(value);
                originalData += value.getValueAsString() + ";";
            }
        }
        String file = HydraUtils.ReadFile(workspace + "H_Relation.txt");
        String[] lines = file.split(System.getProperty("line.separator"));
        String fieldLine = null;
        String[] fields = null;
        int counter = 1;
        for (int i = 0; i < lines.length; i++) {
            if (counter == 1) {
                fieldLine = lines[i];
                fields = fieldLine.split(";");
                counter++;
            } else {
                String valuesLine = lines[i];
                String[] contents = valuesLine.split(";");
                int rowNumber = abs + act.base;
                for (int j = 0; j < contents.length; j++) {
                    String fieldName = fields[j];
                    HField field = act.getFieldByName(fieldName);
                    String content = contents[j];
                    if (field.ftype == HField.FieldType.FILE) {
                        String name = HydraUtils.getFileName(content);
                        content = workspace + name;
                    }
                    HValue value = new HValue(field, act, this, HValue.ValueType.OUTPUT);
                    value.rowNumber = rowNumber;
                    value.setValueFromString(content);

                }
                abs++;
            }
        }
        act.base += abs;
        HydraUtils.deleteFile("H_Relation.txt", workspace);

        String text = "";
        String fieldNames = "";
        for (int i = 0; i < act.fields.size(); i++) {
            HField field = act.fields.get(i);
            if (field.relation_output != null) {
                fieldNames += field.name + ";";
            }
        }
        text += fieldNames;
        text = text.substring(0, text.length() - 1) + System.getProperty("line.separator");

        String data = originalData;

        for (int j = 0; j < oValues.size(); j++) {
            HValue value = oValues.get(j);
            if (value.field.relation_input == null) {
                data += value.getValueAsString() + ";";
            }
            if (data.split(";").length == fieldNames.split(";").length) {
                data = data.substring(0, data.length() - 1) + System.getProperty("line.separator");
                text += data;
                data = originalData;
            }
            HField field = value.field;
            if ((field.operation != null) && (field.operation.equals(HField.FileOper.MOVE_DELETE) || field.operation.equals(HField.FileOper.COPY_DELETE))) {
                HydraUtils.deleteFile(value.fileName, workspace);
            }
        }
        text = text.substring(0, text.length() - 1) + System.getProperty("line.separator");
        HydraUtils.WriteFile(workspace + "H_Relation.txt", text);
    }

    /**
     * Método responsável pela execução do Extrator, caso ele exista
     *
     * @param run
     * @param act
     * @throws InterruptedException
     * @throws IOException
     */
    private void executeExtractor(HActivity act) throws InterruptedException, IOException {
        if (act.extractor != null) {
            String command = act.extractor;
            command = this.processTags(command);
            HydraUtils.runCommand(command, workspace);
        }
    }

    /**
     * Método que criar a relação de saída de uma atividade
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException
     * @throws InterruptedException
     */
    private void createOutput() throws FileNotFoundException, IOException, SQLException, InterruptedException {
        int rowNumber = 0;
        if (act.atype == HActivity.ActType.MAP) {
            for (int i = 0; i < iValues.size(); i++) {
                HValue value = iValues.get(i);
                rowNumber = value.rowNumber;
                if (value.field.relation_output != null) {
                    oValues.add(value);
                }
            }
        }

        if (HydraUtils.isFile(workspace + "H_Relation.txt")) {
            String file = HydraUtils.ReadFile(workspace + "H_Relation.txt");
            String[] lines = file.split(System.getProperty("line.separator"));
            String fieldLine = null;
            String[] fields = null;
            int counter = 1;
            for (int i = 0; i < lines.length; i++) {
                if (counter == 1) {
                    fieldLine = lines[i];
                    fields = fieldLine.split(";");
                    counter++;
                } else {
                    String valuesLine = lines[i];
                    String[] contents = valuesLine.split(";");
                    for (int j = 0; j < contents.length; j++) {
                        String fieldName = fields[j];
                        HField field = act.getFieldByName(fieldName);

                        String content = contents[j];
                        if (field.ftype == HField.FieldType.FILE) {
                            String name = HydraUtils.getFileName(content);
                            content = workspace + name;
                        }
                        HValue value = new HValue(field, act, this, HValue.ValueType.OUTPUT);
                        value.rowNumber = rowNumber;
                        value.setValueFromString(content);
                    }
                }
            }
            HydraUtils.deleteFile("H_Relation.txt", workspace);
        }

        String text = "";
        for (int i = 0; i < act.fields.size(); i++) {
            HField field = act.fields.get(i);
            if (field.relation_output != null) {
                text += field.name + ";";
            }
        }
        text = text.substring(0, text.length() - 1) + System.getProperty("line.separator");

        for (int j = 0; j < oValues.size(); j++) {
            HValue value = oValues.get(j);
            if (value.field.relation_output != null) {
                text += value.getValueAsString() + ";";
            }
            HField field = value.field;
            if ((field.operation != null) && (field.operation.equals(HField.FileOper.MOVE_DELETE) || field.operation.equals(HField.FileOper.COPY_DELETE))) {
                HydraUtils.deleteFile(value.fileName, workspace);
            }
        }
        text = text.substring(0, text.length() - 1) + System.getProperty("line.separator");
        HydraUtils.WriteFile(workspace + "H_Relation.txt", text);
    }

    /**
     * Este método realiza a ativação específica para o Reduce. Como neste
     * caso há mais HValues do que HFields, pois há varias linhas de entrada
     * o Reduce gera diversas linhas de comando que serão executadas em loop
     * durante a execução. Para isso, ele varre a lista de valores e realiza
     * as ativações para cada grupo de valores (linha)
     */
    private void reduceActivation() {
        this.reduceLoop = new ArrayList<String>();
        Iterator<HValue> i = iValues.iterator();
        while (i.hasNext()) {
            List<HValue> values = new ArrayList<HValue>();
            for (int j = 0; j < this.act.fields.size(); j++) {
                //adiciona uma "linha" de valores na lista
                if (i.hasNext()) {
                    values.add(i.next());
                } else {
                    break;
                }
            }
            //elabora o comando substituindo os valores na linha de comando
            String reduceCommand = this.processTags(commandLine, values);
            //armazena o comando para ser executado mais tarde
            this.reduceLoop.add(reduceCommand);
        }
    }

    private void reduce() throws IOException, InterruptedException {
        for (String command : this.reduceLoop) {
            this.executeCommand(command);
        }
    }

    /**
     * Este método precisa ser mudado para generalizar. Essa solução é
     * provisória.
     */
    private void createReduceOutput() {
        String agregationField = this.act.activation.split("%")[1].substring(1);
        String output = agregationField + ";REDUCE" + System.getProperty("line.separator");
        for (HValue value : iValues) {
            if (value.field.name.equals(agregationField)) {
                String agregationValue = value.getValueAsString();
                output += agregationValue + ";" + workspace + agregationValue + ".zip" + System.getProperty("line.separator");
                break;
            }
        }
        HydraUtils.WriteFile(workspace + "H_Relation.txt", output);
        
    }
}
