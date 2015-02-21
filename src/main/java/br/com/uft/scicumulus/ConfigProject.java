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
    private String fileActivities;
    private String fileRelations;

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

    public String getFileActivities() {
        return fileActivities;
    }

    public void setFileActivities(String fileActivities) {
        this.fileActivities = fileActivities;
    }

    public String getFileRelations() {
        return fileRelations;
    }

    public void setFileRelations(String fileRelations) {
        this.fileRelations = fileRelations;
    }

    @Override
    public String toString() {
        return "[nameProject=" + getNameProject() + ", dateCreateProject=" + getDateCreateProject() + ", dateLastAlterProject="
                + getDateLastAlterProject() + ", fileActivities=" + getFileActivities() + ", fileRelations=" + getFileRelations() + "]";
    }

}
