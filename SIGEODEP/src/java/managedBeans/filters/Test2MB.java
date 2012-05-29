/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author and
 */
@ManagedBean(name = "test2MB")
@SessionScoped
public class Test2MB implements Serializable{

    private FilterConnection connection;
    private List<List<String>> newfields;
    private List<String> newheaders;
    private List<String> fields;
    private List<String> values;
    private String the_field;
    private FieldCount[] selected;

    public Test2MB() {
        connection = new FilterConnection();
        connection.connect();
        fields = connection.getTempFields();
        newfields = new ArrayList<List<String>>();
        newheaders = new ArrayList<String>();
    }

    public List<String> getNewheaders() {
        return newheaders;
    }

    public void setNewheaders(List<String> newheaders) {
        this.newheaders = newheaders;
    }

    public List<List<String>> getNewfields() {
        return newfields;
    }

    public void setNewfields(List<List<String>> newfields) {
        this.newfields = newfields;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getThe_field() {
        return the_field;
    }

    public void setThe_field(String the_field) {
        this.the_field = the_field;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public void changeField() {
        newfields = new ArrayList<List<String>>();
        newheaders = new ArrayList<String>();
        newfields = connection.getFieldValuesWithCount(the_field);
        newheaders.add(the_field);
        newheaders.add("# de registros");
    }

    public void deleteRecords() {
        System.out.println("Deleting " + selected.length);
        for (FieldCount record : selected) {
            System.out.println("Deleted " + record.getField());
        }
    }

    public void getFieldValues() {
        values = connection.getFieldValues(the_field);
    }

    public FieldCount[] getSelected() {
        return selected;
    }

    public void setSelected(FieldCount[] selected) {
        this.selected = selected;
    }

}
