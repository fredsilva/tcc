/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.uft.scicumulus;

import java.util.Date;

/**
 *
 * @author fredsilva
 */
public class ConfigProject {

    private String nameProject;
    private Date dateCreateProject;
    private Date dateLastAlterProject;    
    private String fileXML;

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public Date getDateCreateProject() {
        return dateCreateProject;
    }

    public void setDateCreateProject(Date dateCreateProject) {
        this.dateCreateProject = dateCreateProject;
    }

    public Date getDateLastAlterProject() {
        return dateLastAlterProject;
    }

    public void setDateLastAlterProject(Date dateLastAlterProject) {
        this.dateLastAlterProject = dateLastAlterProject;
    }

    public String getFileXML() {
        return fileXML;
    }

    public void setFileXML(String fileXML) {
        this.fileXML = fileXML;
    }

    @Override
    public String toString() {
        return "[nameProject=" + getNameProject() + ", dateCreateProject=" + getDateCreateProject() + ", dateLastAlterProject="
                + getDateLastAlterProject() + ", fileXML=" + getFileXML() + "]";
    }

}
