/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tplogic.filemanagment;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 *
 * @author Gonzalo
 */
public class ArrayListReader {

    private String arch = "arraylist.dat";

    /**
     * Crea un objeto ArraylistReader. Asume que el nombre del archivo desde el
     * cual se recupera es "lista.dat".
     */
    public ArrayListReader() {
    }

    /**
     * Crea un objeto SimpleListReader. Fija el nombre del archivo desde el cual
     * se recupera con el nombre tomado como parametro.
     *
     * @param nom - el nombre del archivo a abrir para iniciar la recuperacion.
     */
    public ArrayListReader(String nom) {
        arch = nom;
    }

    /**
     * Recupera un objeto arraylist. 
     * 
     * @return - el objeto si lo encuentra, null si hay un error. 
     */
    public ArrayList read() {
        ArrayList al;

        try (FileInputStream istream = new FileInputStream(arch)) {
            ObjectInputStream p = new ObjectInputStream(istream);

            al = (ArrayList) p.readObject();

            p.close();
        } catch (Exception e) {
            al = null;
            System.out.println(e); //***************************** CHECK
        }

        return al;
    }
}
