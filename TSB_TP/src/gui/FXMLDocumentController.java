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
import java.util.Optional;
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
    @FXML
    private Button cleanButton;

    /*
    ################################# EVENTOS DE GUI.
     */
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Se crea una instancia de TPLogic.
        logic = new TPLogic();

        // Se actualiza label de total.
        setTotalText(Integer.toString(logic.checkWordsCount()));

        // Se actualiza la lista de archivos leidos. 
        setFileList(logic.getFilesUsed());
    }

    /**
     * Evento de click de boton leer archivo.
     *
     * @param event
     */
    @FXML
    private void readFileClick(ActionEvent event) {
        read();
    }

    /**
     * Evento de click del boton buscar.
     *
     * @param event
     */
    @FXML
    private void searchClick(ActionEvent event) {
        search();
    }

    /**
     * Evento de click del boton limpiar.
     *
     * @param event
     */
    @FXML
    private void cleanButtonClick(ActionEvent event) {
        clear();
    }

    /*
    ################################# METODOS SOPORTE DE EVENTOS.
     */
    /**
     * Metodo que maneja la logica de hacer una lectura.
     *
     * Se separa del evento por si se cambia el tipo de control
     * o algo por el estilo.
     */
    private void read() {

        // Intanciar el selector de archivos.                
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fc.getExtensionFilters().add(extFilter);

        // Mostrar el selector y crear el archivo seleccionado. 
        File file = fc.showOpenDialog(null);

        if (logic.wasReaded(file)) {
            boolean readAgain = alertExistingFile(file.getName());
            if (readAgain) {
                read();
            }
            return;

        }

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
                alertAddedWords(palLeidas);
            }

        } catch (Exception e) {
            alertError("Hubo un error al leer el archivo.");
            System.out.println(e);
        }
        clearGUI();
    }

    /**
     * Metodo que maneja la logica de la busqueda.
     */
    private void search() {

        // Se toma el texto del TextField.
        String word = this.txt_wordToSearch.getText();

        // Se chequea que no este vacio. 
        if (!word.trim().isEmpty()) {

            // Se obtiene el resultado. 
            int result = logic.getWordFrecuency(word);

            // Se muestran el resultado de la busqueda. 
            showResultOfSearch(result);

        } else {
            alertError("Debe ingresar una palabra para buscar.");
            clearGUI();
        }
    }

    /**
     * Metodo que maneja la logica de la limpieza.
     */
    private void clear() {
        // Se limpian las listas.
        logic.clear();

        // Se actualiza label de total.
        setTotalText(Integer.toString(logic.checkWordsCount()));

        // Se actualiza la lista de archivos leidos. 
        setFileList(logic.getFilesUsed());

        // Se limpia la GUI.
        clearGUI();

    }

    /*
    ################################# METODOS QUE ACTUAN SOBRE CONTROLES.
     */
    /**
     * Metodo para mostrar el resultado de la busqueda.
     *
     * @param result - frecuencia de la palabra buscada.
     */
    private void showResultOfSearch(int result) {
        // Se actualiza el texto del label de busqueda. 
        this.lbl_searchResult.setText(Integer.toString(result));
        setResultLabels(true);
    }

    /**
     * Setea visible o no los labels relacionados con la busqueda.
     *
     * @param state - estado de los labels.
     */
    private void setResultLabels(Boolean state) {
        // Se pone visible el label de busqueda. 
        this.lbl_searchResult.setVisible(state);

        // Se pone visible un label que dice frecuencia. 
        this.lbl_frecuency.setVisible(state);

    }

    /**
     * Metodo que setea un numero al label de total.
     *
     * @param count - el numero a actualizar.
     */
    private void setTotalText(String count) {
        this.lbl_wordCount.setText(count);
    }

    /**
     * Metodo que setea el listado de archivos.
     *
     * @param files - el listado de archivos a agregar.
     */
    private void setFileList(ArrayList<File> files) {
        this.list_files.getItems().clear();
        for (File file : files) {
            this.list_files.getItems().add(file.getName());

        }
    }

    //################################# ALERTS.
    /**
     * Indica que una archivo ya fue leido.
     *
     * @param fileName - el nombre del archivo.
     * @return - true si eligio intentar de nuevo.
     */
    private boolean alertExistingFile(String fileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                "¿Desea leer otro archivo?",
                ButtonType.YES,
                ButtonType.NO);

        alert.setHeaderText("El archivo '" + fileName + "' ya fue leido.");

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;

    }

    /**
     * Avisa la cantidad de palabras agregadas.
     *
     * @param cant - cantidad de palabras.
     */
    private void alertAddedWords(int cant) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Se agregaron " + cant + " palabras.", ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Muestra un error.
     *
     * @param men - mensaje de error.
     */
    private void alertError(String men) {
        Alert alert = new Alert(Alert.AlertType.ERROR,
                men,
                ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Limpia todos los elementos de la pantalla.
     */
    private void clearGUI() {
        // Se invisibilizan los labels de busqueda. 
        setResultLabels(false);

        // Se limpia la casilla de busqueda. 
        txt_wordToSearch.clear();
    }

}
