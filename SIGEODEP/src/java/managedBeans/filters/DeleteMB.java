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

/**
 *
 * @author and
 */
@ManagedBean(name = "deleteMB")
@SessionScoped
public class DeleteMB {

    private FilterConnection connection;
    private DualListModel<String> fields;

    /**
     * Creates a new instance of DeleteMB
     */
    public DeleteMB() {
        connection = new FilterConnection();
        connection.connect();
        List<String> fieldsSource = connection.getTempFields();
        List<String> fieldsTarget = new ArrayList<String>();
        fields = new DualListModel<String>(fieldsSource, fieldsTarget);
    }

    public void deleteFields() {
        connection.deleteFields(fields.getTarget());
        List<String> fieldsSource = connection.getTempFields();
        List<String> fieldsTarget = new ArrayList<String>();
        fields = new DualListModel<String>(fieldsSource, fieldsTarget);
    }

    public DualListModel<String> getFields() {
        return fields;
    }

    public void setFields(DualListModel<String> fields) {
        this.fields = fields;
    }
}
