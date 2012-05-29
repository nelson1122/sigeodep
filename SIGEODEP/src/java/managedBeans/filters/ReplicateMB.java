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
@ManagedBean(name = "replicateMB")
@SessionScoped
public class ReplicateMB implements Serializable {

    private List<String> fields;
    private List<String> source;
    private List<String> target;
    private final FilterConnection connection;
    private List<SqlTable> model;
    private List<String> headers;
    private List<ColumnModel> columns;
    private boolean btnUndoDisable;
    private int undo;
    private int page;
    private int index;
    private int pageSize;
    private int limit;

    /**
     * Creates a new instance of CopyMB
     */
    public ReplicateMB() {
        btnUndoDisable = true;
        undo = 0;
        connection = new FilterConnection();
        connection.connect();
        fields = connection.getTempFields();
        model = connection.getListFromTempTable();
        headers = connection.getTempFieldsWithId();
        columns = new ArrayList<ColumnModel>();
        page = 0;
        index = page + 2;
        pageSize = 10;
        limit = connection.getTempRowCount();
        int i = 1;
        for (String header : headers) {
            columns.add(new ColumnModel(header, "a" + i));
            i++;
        }
    }

    public void replicate() {
        connection.executeReplication(fields, target, source);
        fields = connection.getTempFields();
        source = new ArrayList<String>();
        target = new ArrayList<String>();
        this.refresh();
        btnUndoDisable = false;
        undo++;
    }

    public void undo() {
        connection.undo("Replicate");
        fields = connection.getTempFields();
        source = new ArrayList<String>();
        target = new ArrayList<String>();
        this.refresh();
        undo--;
        if (undo == 0) {
            btnUndoDisable = true;
        }
    }

    public void refresh() {
        model = connection.getListFromTempTable();
    }

    public void setPage(int page) {
        if (page >= 0) {
            this.page = page;
            this.index = this.page + 2;
            System.out.println("setPage -> Page=" + this.page + "  Index=" + this.index);
        } else {
            this.page = 0;
            this.index = 2;
        }
    }

    public void setIndex(int index) {
        if (index >= 2) {
            this.index = index;
            this.page = this.index - 2;
            System.out.println("SetIndex -> Page=" + this.page + "  Index=" + this.index);
        } else {
            this.index = 2;
            this.page = 0;
        }
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> getSource() {
        return source;
    }

    public void setSource(List<String> source) {
        this.source = source;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getIndex() {
        return index;

    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public List<SqlTable> getModel() {
        return model;
    }

    public void setModel(List<SqlTable> model) {
        this.model = model;
    }

    public List<ColumnModel> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnModel> columns) {
        this.columns = columns;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isBtnUndoDisable() {
        return btnUndoDisable;
    }

    public void setBtnUndoDisable(boolean btnUndoDisable) {
        this.btnUndoDisable = btnUndoDisable;
    }
}
