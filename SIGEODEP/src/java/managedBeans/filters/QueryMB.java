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
@ManagedBean(name = "queryMB")
@SessionScoped
public class QueryMB {

    private List<SqlTable> model;
    private List<String> headers;
    private FilterConnection connection;
    private List<ColumnModel> columns;
    private int page;
    private int index;
    private int pageSize;
    private int limit;

    /**
     * Creates a new instance of QueryMB
     */
    public QueryMB() {
        connection = new FilterConnection();
        connection.connect();
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
    
    public void refresh(){
        model = connection.getListFromTempTable();
    }

    public int getPage() {
        return page;
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

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getIndex() {
        return index;

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
    
}
