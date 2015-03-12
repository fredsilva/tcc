/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus.graph;

import br.com.uft.scicumulus.enums.Operation;
import br.com.uft.scicumulus.kryonet.ActivityKryo;
import br.com.uft.scicumulus.kryonet.FieldKryo;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author Frederico da Silva Santos
 */
public class Activity extends Shape implements Serializable{

    /**
     * Representa uma Activity
     */
    private String idObject;
    private String name, login, password;
    private String tag, description, type, templatedir, activation, input_filename, output_filename;
    private List<Field> fields = new ArrayList<Field>();
    private boolean paralell;
    private Integer num_machines;
    private boolean cloud;
    private List<Relation> relations = new ArrayList<>();
    private Integer timeCommand;
    private List<String> commands;

    private Entity used;
    private List<Agent> wasAssociatedWith;

    public Activity() throws NoSuchAlgorithmException {
        setDataActivity();
    }
   
    public Activity(String name) throws NoSuchAlgorithmException {
        this.name = name;
        super.title.setText(name);
        this.paralell = true;
        this.num_machines = 1;
        this.cloud = false;
        setDataActivity();
    }

    public Activity(String name, List<Agent> agents, Entity entity) throws NoSuchAlgorithmException {
        this.name = name;
        super.title.setText(name);
        this.paralell = true;
        this.num_machines = 1;
        this.cloud = false;
        this.wasAssociatedWith = agents;
        this.used = entity;
        setDataActivity();
    }

//    public void addField(Field field){
//        this.fields.add(field);
//    }
    public void delField(Field field) {
        this.fields.remove(field);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        super.title.setText(name);
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

    public boolean isParalell() {
        return paralell;
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
    
    public List<Relation> getRelations() {
        return relations;
    }

    public void addRelations(Relation relation) {
        this.relations.add(relation);
    }

    public void addPoint(String position, Node node) {
        if (position.equals("rigth")) {
//            rightVb.getChildren().add(node);
        }
    }

    public Entity getUsed() {
        return used;
    }

    public void setUsed(Entity used) {
        this.used = used;
    }

    public List<Agent> getWasAssociatedWith() {
        return wasAssociatedWith;
    }

    public void setWasAssociatedWith(List<Agent> wasAssociatedWith) {
        this.wasAssociatedWith = wasAssociatedWith;
    }

    public void setDataActivity() {
        super.object.setWidth(100);
        super.object.setHeight(50);
        super.object.setArcWidth(8);
        super.object.setArcHeight(8);
        super.object.setFill(Color.CORNFLOWERBLUE.deriveColor(0, 1.2, 1, 0.6));
        super.object.setStroke(Color.CORNFLOWERBLUE);
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

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Activity convert(ActivityKryo activityKryo) throws NoSuchAlgorithmException{
        Activity act = new Activity();        
        act.setIdObject(activityKryo.getIdObject());
        act.setName(activityKryo.getName());
        act.setActivation(activityKryo.getActivation());
        act.setCloud(activityKryo.isCloud());
        act.setCommands(activityKryo.getCommands());
        act.setDescription(activityKryo.getDescription());
        act.setFields(new Field().convert(activityKryo.getFields()));            
        act.setInput_filename(activityKryo.getInput_filename());
        act.setOutput_filename(activityKryo.getOutput_filename());
        act.setLogin(activityKryo.getLogin());
        act.setNum_machines(activityKryo.getNum_machines());
        act.setParalell(activityKryo.isParalell());
        act.setPassword(activityKryo.getPassword());
        act.setTag(activityKryo.getTag());
        act.setTemplatedir(activityKryo.getTemplatedir());
        act.setTimeCommand(activityKryo.getTimeCommand());
        act.setType(activityKryo.getType());        
        act.setPositionX(activityKryo.getPositionX());
        act.setPositionY(activityKryo.getPositionY());
        return act;
    }
    
    @Override
    public String toString() {
        return "DataObject [name=" + getName() + ", description=" + getDescription() + ", tag="
                + getTag() + ", templatedir="+ getTemplatedir() +", fields="+ getFields() +"]";
    }
}
