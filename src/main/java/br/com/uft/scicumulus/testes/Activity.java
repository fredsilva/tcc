/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fredsilva
 */
public class Activity {    
    private String name;
    private List<String> fields = new ArrayList<String>();        

    public Activity() {
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
         
//    public Activity(String name) {
//        this.name = name;        
//    }     

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
    
    public void add(){
        this.fields.add("field");
        this.fields.add("field2");
        this.fields.add("field3");
    }
}
