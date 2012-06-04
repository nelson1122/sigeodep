/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author and
 */
public class LazyQueryDataModel extends LazyDataModel<List> {

    private List<List> datasource;
    private FilterConnection connection;

    public LazyQueryDataModel() {
        connection = new FilterConnection();
        connection.connect();
        //datasource = connection.getListFromQuery(0, 10);
    }

    @Override
    public List getRowData(String rowKey) {
        for (List list : datasource) {
            if (list.get(0).equals(rowKey)) {
                return list;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(List list) {
        return list.get(0);
    }

    @Override
    public List<List> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        datasource = connection.getListFromQuery(first, pageSize);
        //rowCount
        int dataSize = connection.getTempRowCount();
        this.setRowCount(dataSize);

        return datasource;
    }
}
