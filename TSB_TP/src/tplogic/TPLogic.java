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

/**
 *
 * @author a4
 */
public class TPLogic {
    private ArrayList<File> filesUsed;
    private TSBHashtable<String, Integer> hashtable;

    public TPLogic() {
        hashtable = new TSBHashtable();
        filesUsed = new ArrayList();
    }

    public int readFile(String path) throws FileNotFoundException {        
        return readFile(new File(path));
    }

    public int readFile(File file) throws FileNotFoundException {
        FileParser fp = new FileParser(file);
        int value;
        int countPrev = hashtable.size();
        
        for (String pal : fp) {
            if (!pal.equals("")) {
                value = hashtable.getOrDefault(pal, 0);
                hashtable.put(pal, value + 1);
            }
        }
        filesUsed.add(file);
        return hashtable.size()- countPrev;

    }

    public int getWordFrecuency(String word) {
        return hashtable.getOrDefault(word, 0);
    }

    public int checkWordsCount() {
        return hashtable.size();
    }

    public ArrayList<File> getFilesUsed() {
        return filesUsed;
    }
    
    
}
