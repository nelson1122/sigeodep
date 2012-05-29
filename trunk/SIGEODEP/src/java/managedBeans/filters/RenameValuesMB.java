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
@ManagedBean(name = "renameValuesMB")
@SessionScoped
public class RenameValuesMB implements Serializable {

    FilterConnection connection;
    private List<ValueNewValue> model;
    private List<String> headers;
    private List<String> field_names;
    private String the_field;
    private List<String> fields;
    private boolean btnDisable;
    private int redo;

    public RenameValuesMB() {
        btnDisable = true;
        redo = 0;
        connection = new FilterConnection();
        connection.connect();
        fields = connection.getTempFields();
        model = connection.getValuesOrderedByFrecuency(the_field);
        headers = new ArrayList<String>();
        headers.add("oldvalue");
        headers.add("newvalue");
        field_names = new ArrayList<String>();
        field_names.add(the_field);
        field_names.add("# de Registros");
        connection.dropFilterTables();
    }

    public void renameRecords() {
        connection.saveNewValuesForField(the_field, model);
        model = connection.getValuesOrderedByFrecuency(the_field);
        redo++;
        btnDisable = false;
    }
    
    public void redo(){
        connection.undo("Rename");
        model = connection.getValuesOrderedByFrecuency(the_field);
        redo--;
        if(redo == 0){
            btnDisable = true;
        }
    }

    public List<ValueNewValue> getModel() {
        return model;
    }

    public void setModel(List<ValueNewValue> model) {
        this.model = model;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<String> getField_names() {
        return field_names;
    }

    public void setField_names(List<String> field_names) {
        this.field_names = field_names;
    }

    public void changeField() {
        model = connection.getValuesOrderedByFrecuency(the_field);
        field_names = new ArrayList<String>();
        field_names.add(the_field);
        field_names.add("# de Registros");
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

    public boolean isBtnDisable() {
        return btnDisable;
    }

    public void setBtnDisable(boolean btnDisable) {
        this.btnDisable = btnDisable;
    }
    
}
