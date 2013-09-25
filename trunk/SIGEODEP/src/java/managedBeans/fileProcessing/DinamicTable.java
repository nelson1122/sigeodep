/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author santos
 */
public class DinamicTable implements Serializable{
   
    private ArrayList<ArrayList<String>> listOfRecords=new ArrayList<ArrayList<String>>();
    private ArrayList<String> titles=new ArrayList<String>();
    private ArrayList<String> titles2=new ArrayList<String>();
    
    public DinamicTable() {        
    }

    public DinamicTable(ArrayList<ArrayList<String>> listOfRecords, ArrayList<String> titles) {
        this.listOfRecords = listOfRecords;
        this.titles = titles;
    }
    
    public DinamicTable(ArrayList<ArrayList<String>> listOfRecords, ArrayList<String> titles, ArrayList<String> titles2) {
        this.listOfRecords = listOfRecords;
        this.titles = titles;
        this.titles2 = titles2;
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

    public ArrayList<String> getTitles2() {
        return titles2;
    }

    public void setTitles2(ArrayList<String> titles2) {
        this.titles2 = titles2;
    }   
}
