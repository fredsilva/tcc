package br.com.uft.scicumulus.hydra;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import br.com.uft.scicumulus.vs.database.M_DB;
import br.com.uft.scicumulus.vs.database.M_Query;

/**
 * Classe responsável pelas operações com a base de dados. Pode ser resumida em 
 * dois principais tipos de métodos: load e store
 *
 * @author Vítor, Eduardo
 * @since 2010-12-25
 */
public class HProvenance {

    //Database attributes
    protected static M_DB db = null;

    /**
     * Método que armazena os dados de um Workflow
     *
     * @param workflow
     * @throws SQLException
     */
    static void storeWorkflow(HWorkflow workflow) throws SQLException {
        if (db == null) {
            return;
        }
        String SQL = "select f_workflow(?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, workflow.wkfId);
        q.setParString(2, workflow.tag);
        q.setParString(3, workflow.description);
        q.setParString(4, workflow.exeTag);
        q.setParString(5, workflow.expDir);
        ResultSet rs = q.openQuery();
        if (rs.next()) {
            workflow.wkfId = rs.getInt(1);
        }
        rs.close();
    }

    /**
     * Método que armazena os dados de uma atividade. Ao mesmo tempo, armazena
     * os dados das relações, dos campos e dos arquivos dessa mesma atividade
     *
     * @param act
     * @throws SQLException
     */
    public static void storeActivity(HActivity act) throws SQLException {
        if (db == null) {
            return;
        }
        String SQL = "select f_activity(?,?,?,?,?,?,?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, act.actID);
        q.setParInt(2, act.workflow.wkfId);
        q.setParString(3, act.tag);
        q.setParString(4, act.atype.toString());
        q.setParString(5, act.description);
        q.setParString(6, act.templateDir);
        q.setParString(7, act.status.toString());
        q.setParString(8, act.activation);
        q.setParDate(9, act.startTime);
        q.setParDate(10, act.endTime);
        q.setParString(11, act.extractor);
        ResultSet rs = q.openQuery();
        if (rs.next()) {
            act.actID = rs.getInt(1);
        }

        for (int i = 0; i < act.relations.size(); i++) {
            storeRelation(act.relations.get(i));
        }

        for (int i = 0; i < act.fields.size(); i++) {
            storeField(act.fields.get(i));
        }

        for (int i = 0; i < act.files.size(); i++) {
            storeFile(act.files.get(i));
        }
    }

    /**
     * Método que armazena os dados de uma relação
     *
     * @param relation
     * @throws SQLException
     */
    private static void storeRelation(HRelation relation) throws SQLException {
        if (!Hydra.mainNode) {
            return;
        }
        String SQL = "select f_relation(?,?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, relation.relID);
        q.setParInt(2, relation.act.actID);
        q.setParString(3, relation.relType.toString());
        q.setParString(4, relation.relName);
        q.setParString(5, relation.relFile);
        q.setParString(6, relation.relDependency);

        ResultSet rs = q.openQuery();
        if (rs.next()) {
            relation.relID = (rs.getInt(1));
        }
    }

    //Alterado por Pedro, para armazenar o atributo decimal places
    /**
     * Método que armazena os dados dos campos
     *
     * @param field
     * @throws SQLException
     */
    private static void storeField(HField field) throws SQLException {
        if (!Hydra.mainNode) {
            return;
        }
        String SQL = "select f_field(?,?,?,?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, field.fieldID);
        q.setParInt(2, field.act.actID);
        q.setParString(3, field.relation_input);
        q.setParString(4, field.relation_output);
        q.setParString(5, field.name);
        q.setParString(6, field.ftype.toString());
        q.setParString(7, field.operation.toString());
        q.setParInt(8, field.decimalplaces);

        ResultSet rs = q.openQuery();
        if (rs.next()) {
            field.fieldID = (rs.getInt(1));
        }
    }

    /**
     * Método que armazena os dados dos arquivos
     *
     * @param file
     * @throws SQLException
     */
    private static void storeFile(HFile file) throws SQLException {
        if (!Hydra.mainNode) {
            return;
        }
        String SQL = "select f_file(?,?,?,?,?,?,?,?,?);";
        Integer actID = null, taskID = null;
        if (file.act != null) {
            actID = new Integer((int) file.act.actID);
        }
        if (file.task != null) {
            taskID = new Integer((int) file.task.taskID);
        }

        File f = new File(file.fileDir + file.fileName);
        if (f.exists()) {
            file.fileSize = (int) f.length();
            file.fileData = new Date(f.lastModified());
        }
        f = null;

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, file.fileID);
        q.setParInt(2, actID);
        q.setParInt(3, taskID);
        q.setParString(4, file.instrumented ? "T" : "F");
        q.setParString(5, file.template ? "T" : "F");
        q.setParString(6, file.fileDir);
        q.setParString(7, file.fileName);
        q.setParInt(8, file.fileSize);
        q.setParDate(9, file.fileData);

        ResultSet rs = q.openQuery();
        if (rs.next()) {
            file.fileID = (rs.getInt(1));
        }
    }

    /**
     * Método que armazena os dados de uma tarefa. Além disso, armazena os
     * dados dos seus arquivos e dos seus valores de um campo
     *
     * @param task
     * @throws SQLException
     */
    public static void storeActivation(HActivation task) throws SQLException {
        if (db == null) {
            return;
        }
        String SQL = "select f_activation(?,?,?,?,?,?,?,?,?,?,?);";
        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, task.taskID);
        q.setParInt(2, task.act.actID);
        q.setParInt(3, task.processor);
        q.setParInt(4, task.exitStatus);
        q.setParString(5, task.commandLine);
        q.setParString(6, task.workspace);
        q.setParString(7, task.stdErr);
        q.setParString(8, task.stdOut);
        q.setParDate(9, task.startTime);
        q.setParDate(10, task.endTime);
        q.setParString(11, task.status.toString());
        ResultSet rs = q.openQuery();
        if (rs.next()) {
            task.taskID = (rs.getInt(1));
        }
        rs.close();

        for (int j = 0; j < task.files.size(); j++) {
            HFile file = task.files.get(j);
            storeFile(file);
        }

        for (int j = 0; j < task.iValues.size(); j++) {
            HValue value = task.iValues.get(j);
            storeIValue(value);
        }

        for (int j = 0; j < task.oValues.size(); j++) {
            HValue value = task.oValues.get(j);
            storeOValue(value);
        }
    }

    public static void storeTasks(HActivation[] tasks) throws SQLException {
        if (db == null) {
            return;
        }
        for (int i = 0; i < tasks.length; i++) {
            storeActivation(tasks[i]);
        }
    }

    /**
     * Método que armazena os valores de um campo
     *
     * @param value
     * @throws SQLException
     */
    public static void storeIValue(HValue value) throws SQLException {
        if (!Hydra.mainNode) {
            return;
        }

        String SQL = "select f_value(?,?,?,?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, value.act.actID);
        q.setParInt(2, value.rowNumber);
        q.setParInt(3, value.field.fieldID);
        Integer taskId = null;
        if (value.task != null) {
            taskId = new Integer((int) value.task.taskID);
        }
        q.setParInt(4, taskId);
        q.setParDouble(5, value.floatValue);
        q.setParString(6, value.stringValue);
        q.setParString(7, value.fileDir);
        q.setParString(8, value.fileName);
        ResultSet rs = q.openQuery();
        rs.close();
    }

    public static void storeOValue(HValue value) throws SQLException {
        if (!Hydra.mainNode) {
            return;
        }

        String SQL = "select f_ovalue(?,?,?,?,?,?,?,?);";

        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, value.act.actID);
        q.setParInt(2, value.rowNumber);
        q.setParInt(3, value.field.fieldID);
        Integer taskId = null;
        if (value.task != null) {
            taskId = new Integer((int) value.task.taskID);
        }
        q.setParInt(4, taskId);
        q.setParDouble(5, value.floatValue);
        q.setParString(6, value.stringValue);
        q.setParString(7, value.fileDir);
        q.setParString(8, value.fileName);
        ResultSet rs = q.openQuery();
        rs.close();
    }


    /**
     * Método que carrega os dados de uma tarefa que esteja pronta para executar
     * numa atividade específica, informada pelo parâmetro act. Ao mesmo tempo,
     * carrega os dados dos seus campos e dos seus valores
     *
     * @param act
     * @return
     * @throws SQLException
     */
    static protected HActivation loadReadyTask(HActivity act) throws SQLException {
        if (db == null) {
            return null;
        }
        HActivation task = null;
        String sql = "select taskid from hactivation where status = ? and actid = ? order by taskid limit 1";
        M_Query q = db.prepQuery(sql);
        q.setParString(1, HActivity.StatusType.READY.toString());
        q.setParInt(2, (int) act.actID);
        ResultSet rs = q.openQuery();

        if (rs.next()) {
            int taskid = rs.getInt("taskid");
            task = loadTask(act, taskid);
            String sqlUpd = "update hactivation set status = ? where taskid = ? ";
            M_Query qryUpd = db.prepQuery(sqlUpd);
            qryUpd.setParString(1, HActivity.StatusType.RUNNING.toString());
            qryUpd.setParInt(2, (int) task.taskID);
            qryUpd.executeUpdate();
        }
        return task;
    }
    
    static private HActivation loadTask(HActivity act, int taskid) throws SQLException {
        HActivation task = null;
        String sql = "select taskid, commandline, workspace from hactivation where taskid = ?";
        M_Query q = db.prepQuery(sql);
        q.setParInt(1, taskid);
        ResultSet rs = q.openQuery();
        if (rs.next()) {
            task = new HActivation(act);
            task.taskID = rs.getInt("taskid");
            task.commandLine = rs.getString("commandline");
            task.workspace = rs.getString("workspace");

            sql = "select rownumber, fieldid, taskid, numericvalue, textvalue, fdir, fname from hvalue where taskid = ? order by 1, 2";
            q = db.prepQuery(sql);
            q.setParInt(1, (int) task.taskID);
            rs = q.openQuery();
            while (rs.next()) {
                HField field = act.getFieldByID(rs.getInt("fieldid"));
                HValue value = new HValue(field, act, task, HValue.ValueType.INPUT);
                value.rowNumber = rs.getInt("rownumber");
                value.floatValue = (rs.getDouble("numericvalue"));
                value.stringValue = (rs.getString("textvalue"));
                value.fileDir = (rs.getString("fdir"));
                value.fileName = (rs.getString("fname"));
            }
            rs.close();
        }
        return task;
    }

    static protected ArrayList<HActivation> loadTask(HActivity act) throws SQLException {
        ArrayList<HActivation> result = new ArrayList<HActivation>();

        if (db == null) {
            return null;
        }
        String sql = "select taskid from hactivation where actid = ? order by taskid";
        M_Query q = db.prepQuery(sql);
        q.setParInt(1, (int) act.actID);
        ResultSet rs = q.openQuery();

        while (rs.next()) {
            int taskid = rs.getInt("taskid");
            HActivation task = loadTask(act, taskid);
            result.add(task);
        }
        rs.close();
        return result;
    }

    /**
     * Método que retorna true caso todas as tarefas da atividade tenha sido
     * executada. Caso contrário, retorna false
     *
     * @param act
     * @return
     * @throws SQLException
     */
    public static boolean checkIfAllTasksFinished(HActivity act) throws SQLException {
        boolean result = false;
        String sql = "SELECT count(*) FROM hactivation WHERE status <> ? AND actid = ?";
        M_Query q = db.prepQuery(sql);
        q.setParString(1, HActivity.StatusType.FINISHED.toString());
        q.setParInt(2, (int) act.actID);
        ResultSet rs = q.openQuery();
        if (rs.next()) {
            int counter = rs.getInt(1);
            if (counter == 0) {
                result = true;
            }
            rs.close();
        }
        return result;
    }


    /**
     * Método que verifica se existe um workflow com tag e tagexec na base de dados
     * e retorna seu wkfId. Se o workflow não existir, retorna -1
     * @param tag
     * @param tagexec
     * @return
     * @throws SQLException
     */
    public static int matchWorkflow(String tag, String tagexec) throws SQLException {
        String SQL = "SELECT wkfId FROM hworkflow WHERE tag=? and tagexec=?";
        M_Query q = db.prepQuery(SQL);
        q.setParString(1, tag);
        q.setParString(2, tagexec);
        ResultSet rs = q.openQuery();
        int wkfId = -1;
        if (rs.next()) {
            wkfId = rs.getInt(1);
        }
        return wkfId;
    }

    public static void matchActivities(HWorkflow workflow) throws SQLException {
        //Load the workflow's activities:
        String SQL = "select actid, tag, status, starttime, endtime, (select count(*) from hactivation t where hactivity.actid = t.actid) as activations from hactivity where wkfId = ? ";
        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, workflow.wkfId);
        ResultSet actRs = q.openQuery();

        while (actRs.next()) {
            int actID = actRs.getInt("actid");
            String tag = actRs.getString("tag");
            String status = actRs.getString("status");
            Date start = actRs.getDate("starttime");
            Date end = actRs.getDate("endtime");
            int numActivations = actRs.getInt("activations");
            HActivity act = workflow.getActivity(tag);
            if (act != null) {
                act.actID = actID;
                act.status = HActivity.StatusType.valueOf(status);
                act.startTime = start;
                act.endTime = end;
                act.numActivations = numActivations;
                act.relations.clear();
                act.fields.clear();
                act.files.clear();
                loadRelations(act);
                loadFields(act);
                loadHFiles(act);
            }
        }
        actRs.close();
    }

    private static void loadRelations(HActivity act) throws SQLException {
        //Load every activity's HRelation
        String SQL = "SELECT relid, rtype, rname, filename, dependency FROM hrelation WHERE actid= ? ";
        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, act.actID);
        ResultSet relRs = q.openQuery();

        while (relRs.next()) {

            HRelation relation = new HRelation(act);
            relation.relID = relRs.getInt(1);
            relation.relType = HRelation.RelType.valueOf(relRs.getString(2));
            relation.relName = relRs.getString(3);
            relation.relFile = relRs.getString(4);
            relation.relDependency = relRs.getString(5);

        }

        relRs.close();
    }

    private static void loadFields(HActivity act) throws SQLException {

        //Load activity's HFields:
        String SQL = "SELECT fieldid, relidinput, relidoutput, fname, ftype, operation, decimalplaces FROM hfield WHERE actid= ? ";
        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, act.actID);
        ResultSet fieldRs = q.openQuery();

        while (fieldRs.next()) {
            HField field = new HField(act);
            field.fieldID = fieldRs.getInt(1);
            int relidinput = fieldRs.getInt(2);
            for (int i = 0; i < act.relations.size(); i++) {
                if (relidinput == act.relations.get(i).relID) {
                    field.relation_input = act.relations.get(i).relName;
                    break;
                }
            }
            int relidoutput = fieldRs.getInt(3);

            for (int j = 0; j < act.relations.size(); j++) {
                if (relidoutput == act.relations.get(j).relID) {
                    field.relation_output = act.relations.get(j).relName;
                    break;
                }
            }
            field.name = fieldRs.getString(4);
            field.ftype = HField.FieldType.valueOf(fieldRs.getString(5));
            field.operation = HField.FileOper.valueOf(fieldRs.getString(6));
            field.decimalplaces = fieldRs.getInt(7);
        }

        fieldRs.close();
    }

    private static void loadHFiles(HActivity act) throws SQLException {

        String SQL = "SELECT fileid, ftemplate, fdir, fname, fsize, fdata, finstrumented FROM hfile WHERE actid= ? ";
        M_Query q = db.prepQuery(SQL);
        q.setParInt(1, act.actID);
        ResultSet fileRs = q.openQuery();

        while (fileRs.next()) {

            HFile file = new HFile(act, null);
            file.fileID = fileRs.getInt(1);
            file.template = (fileRs.getString(2).compareTo("T") == 0) ? true : false;
            file.fileDir = fileRs.getString(3);
            file.fileName = fileRs.getString(4);
            file.fileSize = fileRs.getInt(5);
            file.fileData = fileRs.getDate(6);
            file.instrumented = (fileRs.getString(7).compareTo("T") == 0) ? true : false;
        }
    }

    /**
     * Método que carrega todas as tarefas da atividade act, informada como
     * parâmetro.
     *
     * @param act
     * @return
     * @throws SQLException
     */



}
