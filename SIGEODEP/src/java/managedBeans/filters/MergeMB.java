/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "mergeMB")
@SessionScoped
public class MergeMB {

    private FilterConnection connection;
    private List<List<String>> newfields;
    private List<String> newheaders;
    private DualListModel<String> fields;
    private String fieldname;
    private String delimiter;

    public MergeMB() {
        newfields = new ArrayList<List<String>>();
        newheaders = new ArrayList<String>();
        delimiter = " ";
        connection = new FilterConnection();
        connection.connect();
        List<String> fieldsSource = connection.getTempFields();
        List<String> fieldsTarget = new ArrayList<String>();
        fields = new DualListModel<String>(fieldsSource, fieldsTarget);
    }

    public void mergeFields() {
        newheaders = new ArrayList<String>();
        newheaders.add("#");
        newheaders.addAll(fields.getTarget());
        newfields = connection.mergeColumns(fields.getTarget(), delimiter, fieldname);
        newheaders.add(fieldname);
    }

    public DualListModel<String> getFields() {
        return fields;
    }

    public void setFields(DualListModel<String> fields) {
        this.fields = fields;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public List<List<String>> getNewfields() {
        return newfields;
    }

    public void setNewfields(List<List<String>> newfields) {
        this.newfields = newfields;
    }

    public List<String> getNewheaders() {
        return newheaders;
    }

    public void setNewheaders(List<String> newheaders) {
        this.newheaders = newheaders;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
}
