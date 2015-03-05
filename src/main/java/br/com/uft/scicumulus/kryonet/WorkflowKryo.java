/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.uft.scicumulus.kryonet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Frederico da Silva Santos
 */
public class WorkflowKryo {
    String keyWorkflow, nameWorkflow, tag, description, tagExecution, expDirectory, serverDirectory;       
    Boolean exist;
    
    public WorkflowKryo() {
    }   
    
    public WorkflowKryo(String name) throws NoSuchAlgorithmException {
        this.nameWorkflow = name;
        generationKeyWorkflow();
    }

    public String getKeyWorkflow() {
        return keyWorkflow;
    }

    public void setKeyWorkflow(String keyWorkflow) {
        this.keyWorkflow = keyWorkflow;
    }

    public String getNameWorkflow() {
        return nameWorkflow;
    }

    public void setNameWorkflow(String nameWorkflow) {
        this.nameWorkflow = nameWorkflow;
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

    public String getTagExecution() {
        return tagExecution;
    }

    public void setTagExecution(String tagExecution) {
        this.tagExecution = tagExecution;
    }

    public String getExpDirectory() {
        return expDirectory;
    }

    public void setExpDirectory(String expDirectory) {
        this.expDirectory = expDirectory;
    }

    public String getServerDirectory() {
        return serverDirectory;
    }

    public void setServerDirectory(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    public Boolean isExist() {
        return exist;
    }

    public void setExist(Boolean exist) {
        this.exist = exist;
    }        
    
    public void generationKeyWorkflow() throws NoSuchAlgorithmException {
        Random random = new Random();
        String input = Integer.toString(random.nextInt(1000)) + "-" + new Date();
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes();
        md.update(buffer);
        byte[] digest = md.digest();
        String key = "";
        for (int i = 0; i < digest.length; i++) {
            key += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
        }
        this.keyWorkflow = key;
    }
}
