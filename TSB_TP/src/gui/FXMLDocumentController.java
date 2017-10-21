/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import tplogic.TPLogic;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * FXML Controller class
 *
 * @author estre
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Button btn_readFIle;
    @FXML
    private Label lbl_wordCount;
    @FXML
    private Button btn_search;
    @FXML
    private Label lbl_searchResult;
    @FXML
    private Label lbl_frecuency;

    private TPLogic logic;
    @FXML
    private ListView<String> list_files;
    @FXML
    private TextField txt_wordToSearch;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        logic = new TPLogic();
    }

    @FXML
    private void readFileClick(ActionEvent event) {
        read();
    }

    @FXML
    private void searchClick(ActionEvent event) {
        search();
    }
    
    /**
     * Metodo que maneja la logica de hacer una lectura. 
     */
    private void read() {
        // Intanciar el selector de archivos. 
        
        
        FileChooser fc = new FileChooser();
        
        File file = fc.showOpenDialog(null);
        int palLeidas;
        try {

            if (file != null) {
                palLeidas = logic.readFile(file);
                setTotalText(Integer.toString(logic.checkWordsCount()));
                setFileList(logic.getFilesUsed());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Se agregaron " + palLeidas + " palabras.", ButtonType.OK);
                alert.showAndWait();
            }

        } catch (Exception e) {
            // Falta: mensaje de error. 
        }
    }

    private void search() {
        String word = this.txt_wordToSearch.getText();
        if (!word.isEmpty()) {
            int result = logic.getWordFrecuency(word);

            this.lbl_searchResult.setText(Integer.toString(result));
            this.lbl_searchResult.setVisible(true);
            this.lbl_frecuency.setVisible(true);

        }
    }

    private void setTotalText(String count) {
        this.lbl_wordCount.setText(count);
    }

    private void setFileList(ArrayList<File> files) {
        this.list_files.getItems().clear();
        for (File file : files) {
            this.list_files.getItems().add(file.getName());

        }
    }
}
