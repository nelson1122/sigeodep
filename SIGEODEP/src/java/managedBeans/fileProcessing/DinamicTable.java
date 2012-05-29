/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import java.util.ArrayList;

/**
 *
 * @author santos
 */
public class DinamicTable {
   
    private ArrayList<ArrayList<String>> listOfRecords=new ArrayList<ArrayList<String>>();
    private ArrayList<String> titles=new ArrayList<String>();
    
    public DinamicTable() {        
    }

    public DinamicTable(ArrayList<ArrayList<String>> listOfRecords, ArrayList<String> titles) {
        this.listOfRecords = listOfRecords;
        this.titles = titles;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public void setTitles(ArrayList<String> titles) {
        this.titles = titles;
    }    

    public ArrayList<ArrayList<String>> getListOfRecords() {
        return listOfRecords;
    }

    public void setListOfRecords(ArrayList<ArrayList<String>> listOfRecords) {
        this.listOfRecords = listOfRecords;
    }

    
}
