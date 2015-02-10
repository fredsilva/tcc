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
    public Button btn_field_add;
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

        cb_field_type.getSelectionModel().select("string");
        cb_field_operation.setDisable(true);
        txt_field_decimal_places.setDisable(true);
        changeCombo();
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
        this.txt_field_decimal_places.setDisable(true);
        this.cb_field_operation.setDisable(true);
        this.txt_field_name.setText("");
        this.txt_field_decimal_places.setText("");
        this.cb_field_operation.getSelectionModel().select("");
        this.cb_field_type.getSelectionModel().select("string");
    }

    @Override
    public Field addField() {
        this.field.setName(getName());
        this.field.setOperation(getOperation().get("operation"));
        this.field.setType(getType().get("type"));
        this.field.setDecimalPlaces(getDecimalPlaces());
        clearFields();
        return field;
    }

    @Override
    public Button getButtonAddField() {
        return this.btn_field_add;
    }

    public void changeCombo() {
        cb_field_type.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                mapType.put("type", t1);

                if (t1.equals("file")) {
                    txt_field_decimal_places.setDisable(true);
                    cb_field_operation.setDisable(false);
                } else {
                    cb_field_operation.getSelectionModel().select("");
                    if (t1.equals("string")) {
                        txt_field_decimal_places.setDisable(true);
                        cb_field_operation.setDisable(true);
                    }

                    if (t1.equals("float")) {
                        txt_field_decimal_places.setDisable(false);
                        cb_field_operation.setDisable(true);
                    }
                }
            }
        });

        cb_field_operation.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                mapOperation.put("operation", t1);
            }
        });
    }
}
