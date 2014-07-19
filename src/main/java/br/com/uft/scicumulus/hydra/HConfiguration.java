package br.com.uft.scicumulus.hydra;

import java.io.IOException;
import java.sql.SQLException;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import br.com.uft.scicumulus.vs.database.M_DB;

/**
 * Classe responsável pela leitura do arquivo de configuração no formato XML
 *
 * @author Eduardo, Vítor
 * @since 2011-01-13
 */
public class HConfiguration {

    private String configurationFileName;

    /**
     * Construtor da classe HConfiguration
     *
     * @param configurationFile
     */
    public HConfiguration(String configurationFile) {
        this.configurationFileName = configurationFile;
    }

    /**
     * Método que realiza a leitura do arquivo de configuração no formato XML
     *
     * @param hydra
     * @return
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     * @throws SQLException
     */
    public HWorkflow readXMLConfiguration(Hydra hydra) throws ParsingException, ValidityException, IOException, SQLException {
        HWorkflow workflow = new HWorkflow();
        Builder parser = new Builder();
        Document xml = parser.build("file:" + configurationFileName);
        Element elementHydra = xml.getRootElement();

        //Obtain database information from XML
        Element elementDatabase = elementHydra.getChildElements("database").get(0);
        String nameOfDatabase = elementDatabase.getAttributeValue("name");
        String server = elementDatabase.getAttributeValue("server");
        String port = elementDatabase.getAttributeValue("port");
        String username = elementDatabase.getAttributeValue("username");
        String password = elementDatabase.getAttributeValue("password");

        //Database connection configuration
        String connection = "jdbc:postgresql://" + server + ":" + port + "/" + nameOfDatabase + "?chartset=UTF8";
        if (Hydra.mainNode) {
            HProvenance.db = new M_DB(M_DB.DRIVER_POSTGRESQL, connection, username, password, true);
        }

        //Obtain HWorkflow information from XML
        Element elementWorkflow = elementHydra.getChildElements("HydraWorkflow").get(0);
        {
            String tag = elementWorkflow.getAttributeValue("tag");
            String description = elementWorkflow.getAttributeValue("description");
            String expdir = HydraUtils.checkDir(elementWorkflow.getAttributeValue("expdir"));
            String exeTag = elementWorkflow.getAttributeValue("exectag");
            String execmodel = elementWorkflow.getAttributeValue("execmodel");
            String verbose = elementWorkflow.getAttributeValue("verbose");
            String pmonitor = elementWorkflow.getAttributeValue("pmonitor");

            workflow.tag = tag;
            workflow.description = description;
            workflow.expDir = expdir;
            workflow.exeTag = exeTag;
            workflow.pmonitor = pmonitor;
            if (execmodel != null) {
                workflow.model = HWorkflow.ExecModel.valueOf(execmodel);
            }
            if (verbose != null) {
                workflow.verbose = Boolean.parseBoolean(verbose);
                HydraUtils.verbose = workflow.verbose;
            }
        }

        //Get information about the Activities
        Elements elementActivities = elementWorkflow.getChildElements("HydraActivity");
        for (int i = 0; i < elementActivities.size(); i++) {
            Element elementActivity = elementActivities.get(i);
            String tag = elementActivity.getAttributeValue("tag");
            String description = elementActivity.getAttributeValue("description");
            String type = elementActivity.getAttributeValue("type").toUpperCase().trim();
            String activation = elementActivity.getAttributeValue("activation");
            String extractor = elementActivity.getAttributeValue("extractor");
            String templateDir = elementActivity.getAttributeValue("templatedir");
            if (templateDir != null && !templateDir.equals("")) {
                HydraUtils.checkDir(templateDir);
            } else {
                templateDir = "";
            }
            String constrained = elementActivity.getAttributeValue("constrained");

            HActivity act = new HActivity(workflow);
            act.tag = tag;
            act.description = description;
            act.atype = HActivity.ActType.valueOf(type);
            act.templateDir = templateDir;
            act.activation = activation;
            act.extractor = extractor;
            if (constrained != null && constrained.equalsIgnoreCase("true")) {
                act.setConstrainedTrue();
            }

            // get relations information
            Elements elementsRelations = elementActivity.getChildElements("Relation");
            for (int j = 0; j < elementsRelations.size(); j++) {
                Element element = elementsRelations.get(j);
                String relType = element.getAttributeValue("reltype").toUpperCase().trim();
                String relName = element.getAttributeValue("name");
                String relFile = element.getAttributeValue("filename");
                String relDependency = element.getAttributeValue("dependency");

                HRelation relation = new HRelation(act);
                relation.relType = HRelation.RelType.valueOf(relType);
                relation.relName = relName;
                relation.relFile = relFile;
                relation.relDependency = relDependency;
                if (relDependency != null) {
                    act.status = HActivity.StatusType.BLOCKED;
                }
            }

            // get templates information
            Elements elementFiles = elementActivity.getChildElements("File");
            for (int j = 0; j < elementFiles.size(); j++) {
                Element element = elementFiles.get(j);
                String filename = element.getAttributeValue("filename");
                Attribute instrumentedAtt = element.getAttribute("instrumented");
                boolean instrumented = (instrumentedAtt != null) && (instrumentedAtt.getValue().equals("true"));
                boolean template = true;

                HFile file = new HFile(act, null);
                file.fileDir = templateDir;
                file.fileName = filename;
                file.instrumented = instrumented;
                file.template = template;
            }

            // get fields information
            Elements elementFields = elementActivity.getChildElements("Field");
            for (int k = 0; k < elementFields.size(); k++) {
                Element element = elementFields.get(k);
                String name = element.getAttributeValue("name");
                String inputType = element.getAttributeValue("type").toUpperCase().trim();
                String relation_input = element.getAttributeValue("input");
                String relation_output = element.getAttributeValue("output");
                String relation_operation = element.getAttributeValue("operation");
                String field_decimalplaces = element.getAttributeValue("decimalplaces");

                HField field = new HField(act);
                field.name = name;
                field.relation_input = relation_input;
                field.relation_output = relation_output;
                field.ftype = HField.FieldType.valueOf(inputType);
                field.operation = HField.FileOper.MOVE;
                if (relation_operation != null) {
                    field.operation = HField.FileOper.valueOf(relation_operation);
                }
                if (field_decimalplaces != null) {
                    field.decimalplaces = Integer.parseInt(field_decimalplaces);
                }
            }
        }
        workflow.evaluateDependencies();
        workflow.checkPipeline();
        return workflow;
    }
}
