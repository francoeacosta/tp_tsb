/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tplogic;

import fileparser.FileParser;
import hashtable.TSBHashtable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import tplogic.filemanagment.*;

/**
 *
 * @author a4
 */
public class TPLogic {

    private String filesUsedName = "filesUsed.dat";
    private ArrayList<File> filesUsed;
    private String hashtableName = "hashtable.dat";
    private TSBHashtable<String, Integer> hashtable;

    /**
     * Se crea la instancia y se lee desde los archivos
     * tanto hashtabel como filesUsed. Depende de la logica
     * implementada en load().
     */
    public TPLogic() {
        load();

    }

    /**
     * Lee un archivo cuyo path sea pasado como string, y
     * agrega las palabras a la tabla hash.
     *
     * @param path - la ruta del archivo.
     * @return - la cantidad de palabras nuevas.
     * @throws FileNotFoundException - si el archivo no existe.
     */
    public int readFile(String path) throws FileNotFoundException {
        return readFile(new File(path));
    }

    /**
     * Lee un archivo pasado por parametro y
     * agrega las palabras a la tabla hash
     *
     * @param file - el archivo a leer.
     * @return - la cantidad de palabras agregadas.
     * @throws FileNotFoundException - si el archivo no existe.
     */
    public int readFile(File file) throws FileNotFoundException {

        // Se usa un objeto de FileParser. 
        FileParser fp = new FileParser(file);

        // Variable para guardar la frecuencia 
        // de la alabra a agregar en caso que exista. 
        int value;

        // Guarda el tamaño hashtable 
        // previo a la insersion.
        int countPrev = hashtable.size();

        // Se itera por cada palabra que se encuentre. 
        for (String pal : fp) {

            /**
             * Si la palabra es vacia se la ignora.
             * Esto es asi porque antes de retornar el iterador
             * del archivo hace una limpieza (No me salio de otra
             * manera, Gonza) y puede ser que luego de la limpieza
             * no quede nada. Por ejemplo si el proximo item es "!!!",
             * se elimina todo.
             */
            if (!pal.equals("")) {

                /**
                 * Se hace un getOrDefault, si la encuentra, devuelve
                 * la frecuencia, y sino devuelve 0.
                 */
                value = hashtable.getOrDefault(pal, 0);

                /**
                 * Se hace un put, si la palabra existe, se le
                 * sumara uno a la freucuencia, sino se agrega una entrada
                 * a la tabla con value=1.
                 */
                hashtable.put(pal, value + 1);
            }
        }

        // Se agrega el archivo a la lista de archivos analizados.
        filesUsed.add(file);

        // Se guardan los cambios. 
        save();

        // Se retorna la diferencia de size del hashtable. 
        return hashtable.size() - countPrev;

    }

    /**
     * Devuelve la frecuencia de una palabra.
     *
     * @param word - la palabra a consultar.
     * @return - la frecuencia de la palabra.
     */
    public int getWordFrecuency(String word) {
        /**
         * El metodo get or default devuelve la frecuencia de la
         * palabra si la encuentra, sino retorna un valor default
         * que aca se seteo en 0, que vendira a ser cero apariciones.
         */
        String wordToSearch = FileParser.cleanString(word);
        return hashtable.getOrDefault(wordToSearch, 0);
    }

    /**
     * Retorna la cantidad de elementos que hay.
     *
     * @return - elementos del hashtable.
     */
    public int checkWordsCount() {
        return hashtable.size();
    }

    /**
     * Devuelve el array de archivos.
     * Se usa para llenar el ListView de la GUI.
     *
     * @return - el array.
     */
    public ArrayList<File> getFilesUsed() {
        return filesUsed;
    }

    /**
     * Guarda hashtable en un archivo.
     */
    private void saveHashtable() {
        HashtableWritter htw = new HashtableWritter(hashtableName);
        System.out.println(htw.write(hashtable));

    }

    /**
     * Guarda filesUsed en un archivo.
     */
    private void saveFilesUsed() {
        ArrayListWritter alw = new ArrayListWritter(filesUsedName);
        System.out.println(alw.write(filesUsed));

    }

    /**
     * Carga hashtable desde un archivo.
     */
    private void loadHashtable() {
        HashtableReader htr = new HashtableReader(hashtableName);
        hashtable = htr.read();

    }

    /**
     * Carga filesUsed desde un archivo.
     */
    private void loadFilesUsed() {
        ArrayListReader alr = new ArrayListReader(filesUsedName);
        filesUsed = alr.read();
    }

    /**
     * Maneja la logica de la carga. Si al leer hashtable o
     * filesUsed se tiene un null en alguno de los dos, es
     * decir no se encontro el archivo o se produjo un error,
     * crea desde cero las intancias.
     */
    private void load() {
        loadHashtable();
        loadFilesUsed();
        if (hashtable == null || filesUsed == null) {
            clear();
        }
    }

    /**
     * Realiza el guardado de la hashtable y filesUsed.
     */
    private void save() {
        saveHashtable();
        saveFilesUsed();
    }

    /**
     * Borra toda la informacion de hashtable y filesUsed y
     * sobre escribe a los archivos relacionados.
     */
    public void clear() {
        hashtable = new TSBHashtable();
        saveHashtable();
        filesUsed = new ArrayList();
        saveFilesUsed();
    }

    /**
     * Responde si un archivo fue o no leido.
     *
     * @param file - el archivo a chequear.
     * @return - true si fue leido.
     */
    public boolean wasReaded(File file) {
        return filesUsed.contains(file);
    }

}
