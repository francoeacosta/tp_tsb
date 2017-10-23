/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tplogic.filemanagment;

import hashtable.TSBHashtable;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 *
 * @author Gonzalo
 */
public class HashtableReader {

    private String arch = "map.dat";

    /**
     * Crea un objeto HashtableReader. Asume que el nombre del archivo desde el
     * cual se recupera es "map.dat".
     */
    public HashtableReader() {
    }

    /**
     * Crea un objeto HashtableReader. Fija el nombre del archivo desde el cual
     * se recupera con el nombre tomado como parametro.
     *
     * @param nom el nombre del archivo a abrir para iniciar la recuperacion.
     */
    public HashtableReader(String nom) {
        arch = nom;
    }

    /**
     * Recupera un objeto tipo map.
     *
     * @return - el objeto si lo encuentra, null si hay un error.
     */
    public TSBHashtable read() {
        TSBHashtable ht;

        try {
            FileInputStream istream = new FileInputStream(arch);
            ObjectInputStream p = new ObjectInputStream(istream);

            ht = (TSBHashtable) p.readObject();

            p.close();
            istream.close();
        } catch (Exception e) {
            ht = null;
            System.out.println(e); //***************************** CHECK
        }

        return ht;
    }
}
