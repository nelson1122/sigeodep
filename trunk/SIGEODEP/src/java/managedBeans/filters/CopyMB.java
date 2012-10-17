/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.filters;

//import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.fileProcessing.RelationshipOfVariablesMB;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author and
 */
@ManagedBean(name = "copyMB")
@SessionScoped
public class CopyMB{

    RelationshipOfVariablesMB relationshipOfVariablesMB;
    private final FilterConnection connection;
    //copy
    private String copy_field;
    private List<String> fields;
    private List<String> values;
    private int copy_ncopies;
    private String copy_prefix;
    private int undoCopy;
    private boolean btnCopyDisable;
    //delete
    private DualListModel<String> delete_pickfields;
    private int undoDelete;
    private boolean btnDeleteDisable;
    //filter
    private QueryDataModel filter_queryModel;
    private List<String> filter_headers;
    private List<String> filter_field_names;
    private FieldCount[] filter_selected;
    private boolean btnFilterDisable;
    private int redoFilter;
    private String filter_field;
    //split
    private String split_field;
    private boolean split_include;
    private List<List<String>> split_newfields;
    private List<String> split_newheaders;
    private String split_field_name1;
    private String split_field_name2;
    private String split_delimiter;
    private int split_limit;
    private int undoSplit;
    private boolean btnSplitDisable;
    private boolean split_rendered;
    //merge
    private DualListModel<String> merge_pickfields;
    private List<List<String>> merge_newfields;
    private List<String> merge_newheaders;
    private String merge_fieldname;
    private String merge_delimiter;
    private int undoMerge;
    private boolean btnMergeDisable;
    //rename
    private List<ValueNewValue> rename_model;
    private List<String> rename_headers;
    private List<String> rename_field_names;
    private boolean btnRenameDisable;
    private int redoRename;
    private String the_field;
    //replicate
    private List<String> replicate_source;
    private List<String> replicate_target;
    private List<String> replicate_columns2;
    private LazyDataModel<List> replicate_model2;
    private boolean btnReplicateDisable;
    private int undoReplicate;

    /**
     * Creates a new instance of CopyMB
     */
    public CopyMB() {

        FacesContext context = FacesContext.getCurrentInstance();
        relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        connection = new FilterConnection();
        connection.connect();
        // copy
        try {
            fields = connection.getTempFields();
            copy_ncopies = 1;
            btnCopyDisable = true;
            undoCopy = 0;
            // delete
            List<String> fieldsSource = connection.getTempFields();
            List<String> fieldsTarget = new ArrayList<String>();
            delete_pickfields = new DualListModel<String>(fieldsSource, fieldsTarget);
            btnDeleteDisable = true;
            undoDelete = 0;
            // filter
            btnFilterDisable = true;
            redoFilter = 0;
            filter_queryModel = new QueryDataModel(connection.getFieldCounts(filter_field));
            filter_headers = new ArrayList<String>();
            filter_headers.add("field");
            filter_headers.add("count");
            filter_field_names = new ArrayList<String>();
            filter_field_names.add(filter_field);
            filter_field_names.add("# de Registros");
            // split
            split_delimiter = "-";
            split_include = false;
            split_limit = 2;
            split_rendered = false;
            split_newfields = new ArrayList<List<String>>();
            split_newheaders = new ArrayList<String>();
            undoSplit = 0;
            btnSplitDisable = true;
            // merge
            List<String> mergeSource = connection.getTempFields();
            List<String> mergeTarget = new ArrayList<String>();
            merge_pickfields = new DualListModel<String>(mergeSource, mergeTarget);
            merge_newfields = new ArrayList<List<String>>();
            merge_newheaders = new ArrayList<String>();
            merge_delimiter = " ";
            btnMergeDisable = true;
            undoMerge = 0;
            // rename
            btnRenameDisable = true;
            redoRename = 0;
            rename_model = connection.getValuesOrderedByFrecuency(the_field);
            rename_headers = new ArrayList<String>();
            rename_headers.add("oldvalue");
            rename_headers.add("newvalue");
            rename_field_names = new ArrayList<String>();
            rename_field_names.add(the_field);
            rename_field_names.add("# de Registros");
            // replicate
            btnReplicateDisable = true;
            undoReplicate = 0;
            replicate_columns2 = connection.getTempFieldsWithId();

            replicate_model2 = new LazyQueryDataModel();
        } catch (Exception e) {
        }
    }

    public void refresh() {
        // copy
        fields = connection.getTempFields();
        values = new ArrayList<String>();
        copy_ncopies = 1;
        // delete
        List<String> deleteSource = connection.getTempFields();
        List<String> deleteTarget = new ArrayList<String>();
        delete_pickfields = new DualListModel<String>(deleteSource, deleteTarget);
        // split

        // merge
        List<String> mergeSource = connection.getTempFields();
        List<String> mergeTarget = new ArrayList<String>();
        merge_pickfields = new DualListModel<String>(mergeSource, mergeTarget);
        // rename

        // replicate
        replicate_columns2 = connection.getTempFieldsWithId();
        try {
            replicate_model2 = new LazyQueryDataModel();
        } catch (Exception e) {
        }
        relationshipOfVariablesMB.refresh();
    }

    public void cleanBackupTables() {
        connection.cleanFilterAndBackupTables();
    }

    // Copy's methods
    public void changeFieldCopy() {
        values = connection.getFieldValues(copy_field);
        this.setCopy_prefix(copy_field);
    }

    public void saveCopies() {
        connection.addCopies(copy_field, copy_ncopies, copy_prefix);
        this.refresh();
        undoCopy++;
        btnCopyDisable = false;
    }

    public void undoCopy() {
        connection.undo("Copy");
        this.refresh();
        undoCopy--;
        if (undoCopy == 0) {
            btnCopyDisable = true;
        }
    }

    // Delete's methods
    public void deleteFields() {
        connection.deleteFields(delete_pickfields.getTarget());
        this.refresh();
        undoDelete++;
        btnDeleteDisable = false;
        relationshipOfVariablesMB.refresh();
    }

    public void undoDelete() {
        connection.undo("Delete");
        this.refresh();
        undoDelete--;
        if (undoDelete == 0) {
            btnDeleteDisable = true;
        }
    }

    // Filter's methods
    public void deleteRecords() {
        System.out.println("Deleting " + filter_selected.length);
        for (FieldCount record : filter_selected) {
            System.out.println("Deleted " + record.getField());
        }
        connection.removeRecordsByFieldAndValue(filter_field, filter_selected);
        this.refreshReplicate();
        filter_queryModel = new QueryDataModel(connection.getFieldCounts(filter_field));
        redoFilter++;
        btnFilterDisable = false;
    }

    public void redoFilter() {
        connection.undo("Filter");
        filter_queryModel = new QueryDataModel(connection.getFieldCounts(filter_field));
        this.refreshReplicate();
        redoFilter--;
        if (redoFilter == 0) {
            btnFilterDisable = true;
        }
    }

    public void changeFieldFilter() {
        filter_queryModel = new QueryDataModel(connection.getFieldCounts(filter_field));
        filter_field_names = new ArrayList<String>();
        filter_field_names.add(filter_field);
        filter_field_names.add("# de Registros");
    }

    // Split's methods
    public void setRenders() {
        if ("#".equals(split_delimiter.trim())) {
            split_rendered = true;
        } else {
            split_rendered = false;
        }
    }

    private boolean isDigit(char chr) {
        if (chr >= 48 && chr <= 57) {
            return true;
        } else {
            return false;
        }
    }

    public String[] splitByDigit(String text) {
        String[] split = new String[2];
        int i;
        int length = text.length();
        for (i = 0; i < length; i++) {
            char chr = text.charAt(i);
            if (isDigit(chr)) {
                break;
            }
        }
        if (split_include && i < length) {
            while (isDigit(text.charAt(i))) {
                i++;
                if (i == length) {
                    break;
                }
            }
        }
        split[0] = text.substring(0, i);
        split[1] = text.substring(i, length);
        return split;
    }

    public void changeFieldSplit() {
        System.out.println(split_field);
        values = connection.getFieldValues(split_field);
        split_newfields = new ArrayList<List<String>>();
        split_newheaders = new ArrayList<String>();
    }

    public void divideAField() {
        try {
            List<String> records = connection.getFieldRecords(split_field);
            int newfields_length = 0;
            for (String record : records) {
                String[] split;
                if ("#".equals(split_delimiter.trim())) {
                    split = splitByDigit(record);
                } else {
                    split = record.split(split_delimiter, split_limit);
                }
                ArrayList<String> temp = new ArrayList<String>();
                temp.add(record);
                temp.addAll(java.util.Arrays.asList(split));
                split_newfields.add(temp);
                if (temp.size() > newfields_length) {
                    newfields_length = temp.size();
                }
            }
            for (List<String> split : split_newfields) {
                int n = split.size();
                if (n < newfields_length) {
                    for (int i = 0; i < newfields_length - n; i++) {
                        split.add("");
                    }
                }
            }
            split_newheaders.add(split_field);
            split_newheaders.add(split_field_name1);
            split_newheaders.add(split_field_name2);
            connection.saveNewFields(split_newheaders, split_newfields, split_field);
            //relationshipOfVariablesMB.refresh();
            this.refresh();
            undoSplit++;
            btnSplitDisable = false;
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void undoSplit() {
        connection.undo("Split");
        split_newfields = new ArrayList<List<String>>();
        split_newheaders = new ArrayList<String>();
        this.refresh();
        undoSplit--;
        if (undoSplit == 0) {
            btnSplitDisable = true;
        }
    }

    // Merge's methods
    public void mergeFields() {
        merge_newheaders = new ArrayList<String>();
        merge_newheaders.add("#");
        merge_newheaders.addAll(merge_pickfields.getTarget());
        merge_newfields = connection.mergeColumns(merge_pickfields.getTarget(),
                merge_delimiter, merge_fieldname);
        merge_newheaders.add(merge_fieldname);
        this.refresh();
        undoMerge++;
        btnMergeDisable = false;
    }

    public void undoMerge() {
        connection.undo("Merge");
        this.refresh();
        merge_newheaders = new ArrayList<String>();
        merge_newfields = new ArrayList<List<String>>();
        undoMerge--;
        if (undoMerge == 0) {
            btnMergeDisable = true;
        }
    }

    // Rename's methods
    public void renameRecords() {
        connection.saveNewValuesForField(the_field, rename_model);
        rename_model = connection.getValuesOrderedByFrecuency(the_field);
        this.refreshReplicate();
        redoRename++;
        btnRenameDisable = false;
    }

    public void changeFieldRename() {
        rename_model = connection.getValuesOrderedByFrecuency(the_field);
        rename_field_names = new ArrayList<String>();
        rename_field_names.add(the_field);
        rename_field_names.add("# de Registros");
    }

    public void redoRename() {
        connection.undo("Rename");
        rename_model = connection.getValuesOrderedByFrecuency(the_field);
        this.refreshReplicate();
        redoRename--;
        if (redoRename == 0) {
            btnRenameDisable = true;
        }
    }

    // Replicate's methods
    public void replicate() {
        if (replicate_source.size() % replicate_target.size() != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error",
                    "El número de variables correspondientes debería ser factor del "
                    + "número de variables a replicar."));
            return;
        }
        connection.executeReplication(fields, replicate_target, replicate_source);
        fields = connection.getTempFields();
        replicate_source = new ArrayList<String>();
        replicate_target = new ArrayList<String>();
        this.refreshReplicate();
        btnReplicateDisable = false;
        undoReplicate++;
    }

    public void undoReplicate() {
        connection.undo("Replicate");
        fields = connection.getTempFields();
        replicate_source = new ArrayList<String>();
        replicate_target = new ArrayList<String>();
        this.refreshReplicate();
        undoReplicate--;
        if (undoReplicate == 0) {
            btnReplicateDisable = true;
        }
    }

    public void refreshReplicate() {
        try {
            replicate_columns2 = connection.getTempFieldsWithId();
            replicate_model2 = new LazyQueryDataModel();
        } catch (Exception e) {
        }
    }

    // Setters and Getters
    public int getCopy_ncopies() {
        return copy_ncopies;
    }

    public void setCopy_ncopies(int copy_ncopies) {
        this.copy_ncopies = copy_ncopies;
    }

    public String getCopy_prefix() {
        return copy_prefix;
    }

    public void setCopy_prefix(String copy_prefix) {
        this.copy_prefix = copy_prefix;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getThe_field() {
        return the_field;
    }

    public void setThe_field(String the_field) {
        this.the_field = the_field;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean isBtnFilterDisable() {
        return btnFilterDisable;
    }

    public void setBtnFilterDisable(boolean btnFilterDisable) {
        this.btnFilterDisable = btnFilterDisable;
    }

    public List<String> getFilter_field_names() {
        return filter_field_names;
    }

    public void setFilter_field_names(List<String> filter_field_names) {
        this.filter_field_names = filter_field_names;
    }

    public List<String> getFilter_headers() {
        return filter_headers;
    }

    public void setFilter_headers(List<String> filter_headers) {
        this.filter_headers = filter_headers;
    }

    public QueryDataModel getFilter_queryModel() {
        return filter_queryModel;
    }

    public void setFilter_queryModel(QueryDataModel filter_queryModel) {
        this.filter_queryModel = filter_queryModel;
    }

    public FieldCount[] getFilter_selected() {
        return filter_selected;
    }

    public void setFilter_selected(FieldCount[] filter_selected) {
        this.filter_selected = filter_selected;
    }

    public int getRedoFilter() {
        return redoFilter;
    }

    public void setRedoFilter(int redoFilter) {
        this.redoFilter = redoFilter;
    }

    public String getSplit_delimiter() {
        return split_delimiter;
    }

    public void setSplit_delimiter(String split_delimiter) {
        this.split_delimiter = split_delimiter;
    }

    public String getSplit_field_name1() {
        return split_field_name1;
    }

    public void setSplit_field_name1(String split_field_name1) {
        this.split_field_name1 = split_field_name1;
    }

    public String getSplit_field_name2() {
        return split_field_name2;
    }

    public void setSplit_field_name2(String split_field_name2) {
        this.split_field_name2 = split_field_name2;
    }

    public int getSplit_limit() {
        return split_limit;
    }

    public void setSplit_limit(int split_limit) {
        this.split_limit = split_limit;
    }

    public List<List<String>> getSplit_newfields() {
        return split_newfields;
    }

    public void setSplit_newfields(List<List<String>> split_newfields) {
        this.split_newfields = split_newfields;
    }

    public List<String> getSplit_newheaders() {
        return split_newheaders;
    }

    public void setSplit_newheaders(List<String> split_newheaders) {
        this.split_newheaders = split_newheaders;
    }

    public String getMerge_delimiter() {
        return merge_delimiter;
    }

    public void setMerge_delimiter(String merge_delimiter) {
        this.merge_delimiter = merge_delimiter;
    }

    public String getMerge_fieldname() {
        return merge_fieldname;
    }

    public void setMerge_fieldname(String merge_fieldname) {
        this.merge_fieldname = merge_fieldname;
    }

    public List<List<String>> getMerge_newfields() {
        return merge_newfields;
    }

    public void setMerge_newfields(List<List<String>> merge_newfields) {
        this.merge_newfields = merge_newfields;
    }

    public List<String> getMerge_newheaders() {
        return merge_newheaders;
    }

    public void setMerge_newheaders(List<String> merge_newheaders) {
        this.merge_newheaders = merge_newheaders;
    }

    public DualListModel<String> getDelete_pickfields() {
        return delete_pickfields;
    }

    public void setDelete_pickfields(DualListModel<String> delete_pickfields) {
        this.delete_pickfields = delete_pickfields;
    }

    public DualListModel<String> getMerge_pickfields() {
        return merge_pickfields;
    }

    public void setMerge_pickfields(DualListModel<String> merge_pickfields) {
        this.merge_pickfields = merge_pickfields;
    }

    public boolean isBtnRenameDisable() {
        return btnRenameDisable;
    }

    public void setBtnRenameDisable(boolean btnRenameDisable) {
        this.btnRenameDisable = btnRenameDisable;
    }

    public int getRedoRename() {
        return redoRename;
    }

    public void setRedoRename(int redoRename) {
        this.redoRename = redoRename;
    }

    public List<String> getRename_field_names() {
        return rename_field_names;
    }

    public void setRename_field_names(List<String> rename_field_names) {
        this.rename_field_names = rename_field_names;
    }

    public List<String> getRename_headers() {
        return rename_headers;
    }

    public void setRename_headers(List<String> rename_headers) {
        this.rename_headers = rename_headers;
    }

    public List<ValueNewValue> getRename_model() {
        return rename_model;
    }

    public void setRename_model(List<ValueNewValue> rename_model) {
        this.rename_model = rename_model;
    }

    public boolean isBtnReplicateDisable() {
        return btnReplicateDisable;
    }

    public void setBtnReplicateDisable(boolean btnReplicateDisable) {
        this.btnReplicateDisable = btnReplicateDisable;
    }

    public List<String> getReplicate_source() {
        return replicate_source;
    }

    public void setReplicate_source(List<String> replicate_source) {
        this.replicate_source = replicate_source;
    }

    public List<String> getReplicate_target() {
        return replicate_target;
    }

    public void setReplicate_target(List<String> replicate_target) {
        this.replicate_target = replicate_target;
    }

    public int getUndoReplicate() {
        return undoReplicate;
    }

    public void setUndoReplicate(int undoReplicate) {
        this.undoReplicate = undoReplicate;
    }

    public String getSplit_field() {
        return split_field;
    }

    public void setSplit_field(String split_field) {
        this.split_field = split_field;
    }

    public boolean isBtnSplitDisable() {
        return btnSplitDisable;
    }

    public void setBtnSplitDisable(boolean btnSplitDisable) {
        this.btnSplitDisable = btnSplitDisable;
    }

    public int getUndoSplit() {
        return undoSplit;
    }

    public void setUndoSplit(int undoSplit) {
        this.undoSplit = undoSplit;
    }

    public boolean isBtnCopyDisable() {
        return btnCopyDisable;
    }

    public void setBtnCopyDisable(boolean btnCopyDisable) {
        this.btnCopyDisable = btnCopyDisable;
    }

    public int getUndoCopy() {
        return undoCopy;
    }

    public void setUndoCopy(int undoCopy) {
        this.undoCopy = undoCopy;
    }

    public boolean isBtnDeleteDisable() {
        return btnDeleteDisable;
    }

    public void setBtnDeleteDisable(boolean btnDeleteDisable) {
        this.btnDeleteDisable = btnDeleteDisable;
    }

    public int getUndoDelete() {
        return undoDelete;
    }

    public void setUndoDelete(int undoDelete) {
        this.undoDelete = undoDelete;
    }

    public String getCopy_field() {
        return copy_field;
    }

    public void setCopy_field(String copy_field) {
        this.copy_field = copy_field;
    }

    public String getFilter_field() {
        return filter_field;
    }

    public void setFilter_field(String filter_field) {
        this.filter_field = filter_field;
    }

    public boolean isBtnMergeDisable() {
        return btnMergeDisable;
    }

    public void setBtnMergeDisable(boolean btnMergeDisable) {
        this.btnMergeDisable = btnMergeDisable;
    }

    public int getUndoMerge() {
        return undoMerge;
    }

    public void setUndoMerge(int undoMerge) {
        this.undoMerge = undoMerge;
    }

    public List<String> getReplicate_columns2() {
        return replicate_columns2;
    }

    public void setReplicate_columns2(List<String> replicate_columns2) {
        this.replicate_columns2 = replicate_columns2;
    }

    public LazyDataModel<List> getReplicate_model2() {
        return replicate_model2;
    }

    public void setReplicate_model2(LazyDataModel<List> replicate_model2) {
        this.replicate_model2 = replicate_model2;
    }

    public boolean isSplit_include() {
        return split_include;
    }

    public void setSplit_include(boolean split_include) {
        this.split_include = split_include;
    }

    public boolean isSplit_rendered() {
        return split_rendered;
    }

    public void setSplit_rendered(boolean split_rendered) {
        this.split_rendered = split_rendered;
    }
}
