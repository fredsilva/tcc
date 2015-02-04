/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxml;

import br.com.uft.scicumulus.interfaces.FormsInterface;
import br.com.uft.scicumulus.utils.Utils;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author fredsilva
 */
public class FormTypeFileController implements Initializable, FormsInterface {

    @FXML
    private ComboBox<String> type_operation;
    @FXML
    private Pane pane;
    Map<String, String> map;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        type_operation.getItems().add("COPY");
        type_operation.getItems().add("MOVE");
        
        type_operation.valueProperty().addListener(new ChangeListener<String>() {
        @Override public void changed(ObservableValue ov, String t, String t1) {
            map.put("operation", t1);
            onFinish();
        }    
    });
    }   

    @Override
    public Node getNode(Map<String, String> map) {
        this.map = map;
        return pane;
    }

    @Override
    public Object getController() {
        return Utils.loadFXML("/fxml/formTypeFile.fxml");        
    }

    @Override
    public void onFinish() {
        
    }

}
