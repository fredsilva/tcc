/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import br.com.uft.scicumulus.graph.Activity;
import br.com.uft.scicumulus.graph.Relation;

/**
 *
 * @author fredsilva
 */
public class RelationKryo {    
    String idObject;
    private String name;    
    public ActivityKryo nodeStart;
    public ActivityKryo nodeEnd;

    public RelationKryo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ActivityKryo getNodeStart() {
        return nodeStart;
    }

    public void setNodeStart(ActivityKryo nodeStart) {
        this.nodeStart = nodeStart;
    }

    public ActivityKryo getNodeEnd() {
        return nodeEnd;
    }

    public void setNodeEnd(ActivityKryo nodeEnd) {
        this.nodeEnd = nodeEnd;
    }

    public String getIdObject() {
        return idObject;
    }

    public void setIdObject(String idObject) {
        this.idObject = idObject;
    }        
    
    public RelationKryo convert(Relation relation){
        //Converte uma Activity em activityKryo
        RelationKryo relationKryo = new RelationKryo();
        relationKryo.setIdObject(relation.getIdObject());
        relationKryo.setName(relation.getName());
        relationKryo.setNodeStart(new ActivityKryo().convert((Activity) relation.getNodeStart()));
        relationKryo.setNodeEnd(new ActivityKryo().convert((Activity) relation.getNodeEnd()));        
        return relationKryo;
    }
}
