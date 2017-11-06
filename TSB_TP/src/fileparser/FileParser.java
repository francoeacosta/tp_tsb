/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileparser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author a4
 */
/**
 * La idea de esta clase es aislar el comportamiento del scanner en un archivo.
 *
 */
public class FileParser implements Iterable<String>, Closeable {

    Scanner sc;

    /**
     * Contructor con path tipo string.
     *
     * @param path - path del archivo a leer.
     * @throws FileNotFoundException - si no encuentra el archivo.
     * @throws NullPointerException - si path es null.
     */
    public FileParser(String path) throws FileNotFoundException, NullPointerException {
        File file = new File(path);
        sc = new Scanner(file, "ISO-8859-1");
        // ([a-z]+[0-9]+|[0-9]+[a-z]+)[a-z0-9]*\b|[^A-Za-z]+
        //sc.skip("[^A-Za-z]+");
    }

    /**
     * Constructor con archivo tipo File.
     *
     * @param file - el archivo.
     * @throws FileNotFoundException - si no encuentra el archivo.
     */
    public FileParser(File file) throws FileNotFoundException {
        sc = new Scanner(file, "ISO-8859-1");

    }

    /**
     * "Iterador" del archivo.
     *
     * @return - una instancia del iterador.
     */
    @Override
    public Iterator<String> iterator() {
        return new FileIterator();
    }

    /**
     * Cierra el scanner para liberar el archivo.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        sc.close();
    }

    /**
     * Clase iteradora del archivo.
     */
    private class FileIterator implements Iterator<String> {

        @Override
        public boolean hasNext() {

            return sc.hasNext();
        }

        @Override
        public String next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException("No quedan mas palabras.");
            }

            return FileParser.this.cleanString(sc.next()).toLowerCase();

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("No se puede alterar el contenido del archivo."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    /**
     * Elimina los acentos de un string.
     * á -> a
     *
     * @param s - string a limpiar.
     * @return - el string sin acentos.
     */
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    /**
     * Elimina todo aquello que no sea palabras de
     * solo letras.
     *
     * @param s - string a limpiar.
     * @return - string solamente de letras.
     */
    public static String stripNonAlphabetic(String s) {
        return s.replaceAll("([a-z]+[0-9]+|[0-9]+[a-z]+)[a-z0-9]*\b|[^A-Za-z]+", "");
    }

    /**
     * Elimina acentos, palabras alfanumericas y puntuacion.
     *
     * @param s - string a limpiar.
     * @return - string limpio.
     */
    public static String cleanString(String s) {
        return stripNonAlphabetic(stripAccents(s));
    }

}
