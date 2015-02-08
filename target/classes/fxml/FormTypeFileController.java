/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxml;

import br.com.uft.scicumulus.graph.Field;
import br.com.uft.scicumulus.graph.Relation;
import br.com.uft.scicumulus.interfaces.FormsInterface;
import br.com.uft.scicumulus.utils.Utils;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Frederico da Silva Santos
 */
public class FormTypeFileController implements Initializable, FormsInterface, Cloneable {

    @FXML
    private ComboBox<String> cb_field_operation, cb_field_type;
    @FXML
    private Pane pane;
    Map<String, String> mapOperation;
    Map<String, String> mapType;
    @FXML
    private TextField txt_field_name, txt_field_decimal_places;
    @FXML
    private Button btn_field_add;
    Field field = new Field();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cb_field_operation.getItems().add("COPY");
        cb_field_operation.getItems().add("MOVE");
        cb_field_type.getItems().add("file");
        cb_field_type.getItems().add("float");
        cb_field_type.getItems().add("string");

        cb_field_operation.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                mapOperation.put("operation", t1);
            }
        });

        cb_field_type.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                mapType.put("type", t1);
            }
        });

    }

    @Override
    public Node getNode(Map<String, String> mapOperation, Map<String, String> mapType) {
        this.mapOperation = mapOperation;
        this.mapType = mapType;
        return pane;
    }

    @Override
    public Map<String, String> getOperation() {
        return mapOperation;
    }

    @Override
    public Map<String, String> getType() {
        return mapType;
    }

    @Override
    public String getName() {
        return this.txt_field_name.getText().trim();
    }    

    @Override
    public String getDecimalPlaces() {
        return this.txt_field_decimal_places.getText().trim();
    }

    @Override
    public Object getController() {
        return Utils.loadFXML("/fxml/formTypeFile.fxml");
    }

    @Override
    public void clearFields() {
        this.txt_field_name.setText("");
        this.txt_field_decimal_places.setText("");
        this.cb_field_operation.getSelectionModel().select("");
        this.cb_field_type.getSelectionModel().select("");
    }

    @Override
    public Field addField() {
        this.field.setName(getName());
        this.field.setOperation(getOperation().get("operation"));
        this.field.setType(getType().get("type"));
//        this.field.setInput(relation.getName() + "_" + "input");
//        this.field.setOutput(relation.getName() + "_" + "output");
        this.field.setDecimalPlaces(getDecimalPlaces());
        clearFields();
        return field;
    }

    public Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (Exception e) {
            System.out.println("Cloning not allowed.");
            return this;
        }
    }
    /*
     * Gest and Sets
     */
//    public ComboBox<String> getCb_field_operation() {
//        return cb_field_operation;
//    }
//
//    public void setCb_field_operation(ComboBox<String> cb_field_operation) {
//        this.cb_field_operation = cb_field_operation;
//    }
//
//    public ComboBox<String> getCb_field_type() {
//        return cb_field_type;
//    }
//
//    public void setCb_field_type(ComboBox<String> cb_field_type) {
//        this.cb_field_type = cb_field_type;
//    }
//
//    public Pane getPane() {
//        return pane;
//    }
//
//    public void setPane(Pane pane) {
//        this.pane = pane;
//    }
//
//    public TextField getTxt_field_name() {
//        return txt_field_name;
//    }
//
//    public void setTxt_field_name(TextField txt_field_name) {
//        this.txt_field_name = txt_field_name;
//    }
//
//    public TextField getTxt_field_input() {
//        return txt_field_input;
//    }
//
//    public void setTxt_field_input(TextField txt_field_input) {
//        this.txt_field_input = txt_field_input;
//    }
//
//    public TextField getTxt_field_output() {
//        return txt_field_output;
//    }
//
//    public void setTxt_field_output(TextField txt_field_output) {
//        this.txt_field_output = txt_field_output;
//    }
//
//    public TextField getTxt_field_decimal_places() {
//        return txt_field_decimal_places;
//    }
//
//    public void setTxt_field_decimal_places(TextField txt_field_decimal_places) {
//        this.txt_field_decimal_places = txt_field_decimal_places;
//    }       
}
