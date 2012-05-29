/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author and
 */
@ManagedBean(name = "splitMB")
@SessionScoped
public class SplitMB {

    private FilterConnection connection;
    private List<List<String>> newfields;
    private List<String> newheaders;
    private List<String> fields;
    private List<String> values;
    private String the_field;
    private String the_value;
    private String field_name1;
    private String field_name2;
    private String delimiter;
    private int limit;

    public SplitMB() {
        delimiter = "-";
        limit = 2;
        connection = new FilterConnection();
        connection.connect();
        fields = connection.getTempFields();
        newfields = new ArrayList<List<String>>();
        newheaders = new ArrayList<String>();
    }

    public String getField_name1() {
        return field_name1;
    }

    public void setField_name1(String field_name1) {
        this.field_name1 = field_name1;
    }

    public String getField_name2() {
        return field_name2;
    }

    public void setField_name2(String field_name2) {
        this.field_name2 = field_name2;
    }

    public List<String> getNewheaders() {
        return newheaders;
    }

    public void setNewheaders(List<String> newheaders) {
        this.newheaders = newheaders;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<List<String>> getNewfields() {
        return newfields;
    }

    public void setNewfields(List<List<String>> newfields) {
        this.newfields = newfields;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getThe_value() {
        return the_value;
    }

    public void setThe_value(String the_value) {
        this.the_value = the_value;
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

    /**
     * Creates a new instance of SplitMB
     */
    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public void changeField() {
        getFieldValues();
        newfields = new ArrayList<List<String>>();
        newheaders = new ArrayList<String>();
    }

    public void getFieldValues() {
        values = connection.getFieldValues(the_field);
    }

    public void divideAField() {
        List<String> records = connection.getFieldRecords(the_field);
        int newfields_length = 0;
        for (String record : records) {
            String[] split = record.split(delimiter, limit);
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(record);
            temp.addAll(java.util.Arrays.asList(split));
            newfields.add(temp);
            if (temp.size() > newfields_length) {
                newfields_length = temp.size();
            }
        }
        for (List<String> split : newfields) {
            int n = split.size();
            if (n < newfields_length) {
                for (int i = 0; i < newfields_length - n; i++) {
                    split.add("");
                }
            }
        }
        newheaders.add(the_field);
        newheaders.add(field_name1);
        newheaders.add(field_name2);
    }
    
    public void saveNewFields(){
        connection.saveNewFields(newheaders, newfields, the_field);
    }
}
