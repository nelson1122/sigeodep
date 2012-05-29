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
@ManagedBean(name = "filterMB")
@SessionScoped
public class FilterMB implements Serializable {

    FilterConnection connection;
    private QueryDataModel queryModel;
    private List<String> headers;
    private List<String> field_names;
    private String the_field;
    private FieldCount[] selected;
    private List<String> fields;
    private boolean btnDisable;
    private int redo;

    public FilterMB() {
        btnDisable = true;
        redo = 0;
        connection = new FilterConnection();
        connection.connect();
        fields = connection.getTempFields();
        queryModel = new QueryDataModel(connection.getFieldCounts(the_field));
        headers = new ArrayList<String>();
        headers.add("field");
        headers.add("count");
        field_names = new ArrayList<String>();
        field_names.add(the_field);
        field_names.add("# de Registros");
    }

    public void deleteRecords() {
        System.out.println("Deleting " + selected.length);
        for (FieldCount record : selected) {
            System.out.println("Deleted " + record.getField());
        }
        connection.removeRecordsByFieldAndValue(the_field, selected);
        queryModel = new QueryDataModel(connection.getFieldCounts(the_field));
        redo++;
        btnDisable = false;
    }

    public void redo() {
        connection.undo("Filter");
        queryModel = new QueryDataModel(connection.getFieldCounts(the_field));
        redo--;
        if (redo == 0) {
            btnDisable = true;
        }
    }

    public void changeField() {
        queryModel = new QueryDataModel(connection.getFieldCounts(the_field));
        field_names = new ArrayList<String>();
        field_names.add(the_field);
        field_names.add("# de Registros");
    }

    public QueryDataModel getQueryModel() {
        return queryModel;
    }

    public void setQueryModel(QueryDataModel queryModel) {
        this.queryModel = queryModel;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public FieldCount[] getSelected() {
        return selected;
    }

    public void setSelected(FieldCount[] selected) {
        this.selected = selected;
    }

    public List<String> getField_names() {
        return field_names;
    }

    public void setField_names(List<String> field_names) {
        this.field_names = field_names;
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
