/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

/**
 *
 * @author fredsilva
 */
public class RelationKryo {    
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
    
    
}
