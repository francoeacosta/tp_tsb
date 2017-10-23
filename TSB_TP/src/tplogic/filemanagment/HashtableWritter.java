/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tplogic.filemanagment;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import hashtable.TSBHashtable;

/**
 *
 * @author Gonzalo
 */
public class HashtableWritter {

    // nombre del archivo serializado...
    private String arch = "map.dat";

    /**
     * Crea un objeto HashtableWritter. Supone que el nombre del archivo a
     * grabar sera "map.dat".
     */
    public HashtableWritter() {
    }

    /**
     * Crea un objeto HashtableWritter. Fija el nombre del archivo que se graba
     * con el nombre tomado como parametro.
     *
     * @param nom - el nombre del archivo a grabar.
     */
    public HashtableWritter(String nom) {
        arch = nom;
    }

    /**
     * Graba un map en un archivo.
     *
     * @param mp - el mapa a guardar.
     * @return - true si puede grabar.
     */
    public boolean write(TSBHashtable ht) {

        try (FileOutputStream ostream = new FileOutputStream(arch)) {

            ObjectOutputStream p = new ObjectOutputStream(ostream);

            p.writeObject(ht);

            p.flush();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
