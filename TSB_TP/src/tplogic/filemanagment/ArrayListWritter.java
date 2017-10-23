/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tplogic.filemanagment;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Gonzalo
 */
public class ArrayListWritter {

    // nombre del archivo serializado...
    private String arch = "arraylist.dat";

    /**
     * Crea un objeto ArrayListWritter. Supone que el nombre del archivo a
     * grabar sera "arraylist.dat".
     */
    public ArrayListWritter() {
    }

    /**
     * Crea un objeto ArrayListWritter. Fija el nombre del archivo que se graba
     * con el nombre tomado como parametro.
     *
     * @param nom - el nombre del archivo a grabar.
     */
    public ArrayListWritter(String nom) {
        arch = nom;
    }

    /**
     * Guarda un array en un archivo.
     *
     * @param al - el array a guardar.
     * @return - true si puede grabar.
     */
    public boolean write(ArrayList al) {

        try (FileOutputStream ostream = new FileOutputStream(arch)) {
            ObjectOutputStream p = new ObjectOutputStream(ostream);

            p.writeObject(al);

            p.flush();
        } catch (Exception e) {
            System.out.println(e); //***************************** CHECK
            return false;
        }
        return true;
    }
}
