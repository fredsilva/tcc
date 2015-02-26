/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

import br.com.uft.scicumulus.enums.Operation;
import br.com.uft.scicumulus.graph.Activity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fredsilva
 */
public class ActivityKryo extends ShapeKryo{    
    
    private String idObject;
    private String name, login, password;
    private String tag, description, type, templatedir, activation, input_filename, output_filename;
    private List<FieldKryo> fields = new ArrayList<FieldKryo>();
    private boolean paralell;
    private Integer num_machines;
    private boolean cloud;
    private List<RelationKryo> relations = new ArrayList<>();
    private Integer timeCommand;
    private List<String> commands;
    private Operation operation;
    
    public ActivityKryo() {                
    }

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTemplatedir() {
        return templatedir;
    }

    public void setTemplatedir(String templatedir) {
        this.templatedir = templatedir;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getInput_filename() {
        return input_filename;
    }

    public void setInput_filename(String input_filename) {
        this.input_filename = input_filename;
    }

    public String getOutput_filename() {
        return output_filename;
    }

    public void setOutput_filename(String output_filename) {
        this.output_filename = output_filename;
    }

    public List<FieldKryo> getFields() {
        return fields;
    }

    public void setFields(List<FieldKryo> fields) {
        this.fields = fields;
    }

    public boolean isParalell() {
        return paralell;
    }

    public void setParalell(boolean paralell) {
        this.paralell = paralell;
    }

    public Integer getNum_machines() {
        return num_machines;
    }

    public void setNum_machines(Integer num_machines) {
        this.num_machines = num_machines;
    }

    public boolean isCloud() {
        return cloud;
    }

    public void setCloud(boolean cloud) {
        this.cloud = cloud;
    }

    public List<RelationKryo> getRelations() {
        return relations;
    }

    public void setRelations(List<RelationKryo> relations) {
        this.relations = relations;
    }

    public Integer getTimeCommand() {
        return timeCommand;
    }

    public void setTimeCommand(Integer timeCommand) {
        this.timeCommand = timeCommand;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
    
    public ActivityKryo convert(Activity activity){
        //Converte uma Activity em activityKryo
        ActivityKryo activityKryo = new ActivityKryo();
        activityKryo.setIdObject(activity.getIdObject());
        activityKryo.setName(activity.getName());
        activityKryo.setActivation(activity.getActivation());
        activityKryo.setCloud(activity.isCloud());
        activityKryo.setCommands(activity.getCommands());
        activityKryo.setDescription(activity.getDescription());
//        activityKryo.setFields(activity.getFields()); Fields      
//        activityKryo.setIdObject(activity.get); IdObject      
        activityKryo.setInput_filename(activity.getInput_filename());
        activityKryo.setOutput_filename(activity.getOutput_filename());
        activityKryo.setLogin(activity.getLogin());
        activityKryo.setNum_machines(activity.getNum_machines());
        activityKryo.setParalell(activity.isParalell());
        activityKryo.setPassword(activity.getPassword());
//        activityKryo.setRelations(activity.getRelations());       Relations
        activityKryo.setTag(activity.getTag());
        activityKryo.setTemplatedir(activity.getTemplatedir());
        activityKryo.setTimeCommand(activity.getTimeCommand());
        activityKryo.setType(activity.getType());
        activityKryo.setOperation(Operation.INSERT);
        return activityKryo;
    }
}
