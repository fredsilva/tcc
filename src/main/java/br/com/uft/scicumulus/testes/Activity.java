/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.testes;

/**
 *
 * @author fredsilva
 */
public class Activity {
    private int number = 10;
    private String name;
 
    public Activity() {        
    } 
 
    public Activity(String name) {
        this.name = name;
    }
 
    public int getNumber() {
        return number;
    }
 
    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
