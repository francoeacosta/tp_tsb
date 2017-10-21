/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

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
// Imports agregados. 
import java.io.File;
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
     *
     * Se separa del evento por si se cambia el tipo de control
     * o algo por el estilo.
     */
    private void read() {

        // Intanciar el selector de archivos.                
        FileChooser fc = new FileChooser();

        // Mostrar el selector y crear el archivo seleccionado. 
        File file = fc.showOpenDialog(null);

        try {

            // Chequeo si se selecciono el archivo. 
            if (file != null) {

                // Se lee el archivo y se conoce la cantidad agregada. 
                int palLeidas = logic.readFile(file);

                // Se actualiza label de total.
                setTotalText(Integer.toString(logic.checkWordsCount()));

                // Se actualiza la lista de archivos leidos. 
                setFileList(logic.getFilesUsed());

                // Se muestra un alert con la cantidad de palabras agreadas. 
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Se agregaron " + palLeidas + " palabras.", ButtonType.OK);
                alert.showAndWait();
            }

        } catch (Exception e) {
            // Falta: mensaje de error. 
        }
    }
    
    /**
     * Metodo que maneja la logica de la busqueda. 
     */
    private void search() {

        // Se toma el texto del TextField.
        String word = this.txt_wordToSearch.getText();

        // Se chequea que no este vacio. 
        if (!word.isEmpty()) {

            // Se obtiene el resultado. 
            int result = logic.getWordFrecuency(word);

            // Se actualiza el texto del label de busqueda. 
            this.lbl_searchResult.setText(Integer.toString(result));

            // Se pone visible el label de busqueda. 
            this.lbl_searchResult.setVisible(true);

            // Se pone visible un label que dice frecuencia. 
            this.lbl_frecuency.setVisible(true);

        }
    }
    
    /**
     * Metodo que setea un numero al label de total. 
     * @param count - el numero a actualizar.
     */
    private void setTotalText(String count) {
        this.lbl_wordCount.setText(count);
    }
    
    /**
     * Metodo que setea el listado de archivos. 
     * @param files - el listado de archivos a agregar. 
     */
    private void setFileList(ArrayList<File> files) {
        this.list_files.getItems().clear();
        for (File file : files) {
            this.list_files.getItems().add(file.getName());

        }
    }
}
