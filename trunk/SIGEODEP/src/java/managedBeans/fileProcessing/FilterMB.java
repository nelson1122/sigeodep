/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.filters.FieldCount;
import managedBeans.filters.FilterConnection;
import managedBeans.filters.QueryDataModel;
import managedBeans.filters.ValueNewValue;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author santos
 */
@ManagedBean(name = "filterMB")
@SessionScoped
public class FilterMB {

    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private ErrorsControlMB errorsControlMB;
    private RecordDataMB recordDataMB;
    private ProjectsMB projectsMB;
    private ConnectionJdbcMB connectionJdbcMB;
    private String sql;
    //copy
    private String variableNameToCopy;
    private List<String> variablesFoundToCopy;
    private List<String> valuesFoundToCopy;
    private int numberOfCopies = 1;
    private String copyPrefix;
    private String variableNameToCopyFilter;
    //delete
    private DualListModel<String> variablesPickToDelete;
    //split
    private String variableNameToSplit;
    private List<String> variablesFoundToSplit;
    private List<String> valuesFoundToSplit;
    private String splitFieldName1;
    private String splitFieldName2;
    private String splitDelimiter;
    private boolean splitRendered = false;
    private boolean splitInclude;
    //merge
    private DualListModel<String> variablesPickToMerge;
    private String variableNameToMerge;
    private String mergeDelimiter;
    //filter records
    private QueryDataModel filter_queryModel;
    private List<String> filter_headers;
    private List<String> filter_field_names;
    private FieldCount[] filter_selected;
    //private boolean btnFilterDisable;
    private int redoFilter;
    private String filter_field;
    private List<String> variablesFoundToFilterRecords;
    //rename
    private List<ValueNewValue> rename_model;
    private List<String> rename_headers;
    private List<String> rename_field_names;    
    private int redoRename;
    private String the_field;
    private List<String> variablesFoundToRename;
    //replicate
    private List<String> replicate_source;
    private List<String> replicate_target;
    private List<String> replicateFields;
    //private List<String> replicate_columns2;
    //private LazyDataModel<List> replicate_model2;
    

    /**
     * Creates a new instance of FilterMB
     */
    public FilterMB() {
        FacesContext context = FacesContext.getCurrentInstance();
        relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
        recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void reset() {
        //---- copy ------
        variableNameToCopy = "";
        variableNameToCopyFilter = "";
        variablesFoundToCopy = loadFoundVariables(variableNameToCopyFilter);
        valuesFoundToCopy = new ArrayList<String>();
        numberOfCopies = 1;
        copyPrefix = "";
        //--- delete ----
        List<String> fieldsSource = loadFoundVariablesRemovingRelated();
        List<String> fieldsTarget = new ArrayList<String>();
        variablesPickToDelete = new DualListModel<String>(fieldsSource, fieldsTarget);
        //--- split ----
        variableNameToSplit = "";
        variablesFoundToSplit = loadFoundVariables(null);
        valuesFoundToSplit = new ArrayList<String>();
        //--- merge ----
        variableNameToMerge = "";
        List<String> fieldsSource2 = loadFoundVariables(null);
        List<String> fieldsTarget2 = new ArrayList<String>();
        variablesPickToMerge = new DualListModel<String>(fieldsSource2, fieldsTarget2);
        mergeDelimiter = "-";
        //---- filter records -----
        variablesFoundToFilterRecords = loadFoundVariables(null);
        filter_field = "";
        filter_queryModel = new QueryDataModel(null);
        filter_headers = new ArrayList<String>();
        filter_headers.add("field");
        filter_headers.add("count");
        filter_field_names = new ArrayList<String>();
        filter_field_names.add(filter_field);
        filter_field_names.add("# de Registros");
        //-----remame--------------
        the_field = "";
        variablesFoundToRename = loadFoundVariables(null);
        //btnRenameDisable = true;
        redoRename = 0;
        rename_model = null;
        rename_headers = new ArrayList<String>();
        rename_headers.add("oldvalue");
        rename_headers.add("newvalue");
        rename_field_names = new ArrayList<String>();
        rename_field_names.add("-");
        rename_field_names.add("# de Registros");
        //-----replicate-----
        replicateFields = loadFoundVariables(null);
        replicate_source= new ArrayList<String>();
        replicate_target= new ArrayList<String>();


    }

    //--------------------------------------------------------------------------
    //-------------------------- FUNCIONES GENERALES ---------------------------
    //--------------------------------------------------------------------------
    private boolean haveSpaces(String text) {
        boolean returnBoolean = false;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == 32) {
                return true;
            }
        }
        return returnBoolean;
    }

    private boolean searchColumn(String text) {
        boolean returnBoolean = false;
        if (projectsMB != null) {
            try {
                ResultSet rs;
                sql = ""
                        + " SELECT \n"
                        + "	   column_name \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n"
                        + "	   project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "        column_name LIKE '" + text + "'";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                if (rs.next()) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return returnBoolean;
    }

    private String determineColumnName(String name) {
        String strReturn = name;
        int number = 0;
        boolean continueProcess = true;
        try {
            while (continueProcess) {
                number++;
                strReturn = name + "_" + String.valueOf(number);
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    * \n"
                        + " FROM \n"
                        + "    project_columns \n"
                        + " WHERE \n"
                        + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "    project_columns.column_name LIKE '" + strReturn + "' \n");
                if (!rs.next()) {
                    continueProcess = false;
                }
            }

        } catch (Exception e) {
        }
        return strReturn;
    }

    private ArrayList<String> loadFoundValues(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo con valores no repetidos
         */
        ArrayList<String> arrayReturn = new ArrayList<String>();
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT \n"
                    + " 	DISTINCT(project_records.data_value) \n"
                    + " FROM \n"
                    + " 	project_records \n"
                    + " WHERE \n"
                    + " 	project_id=" + projectsMB.getCurrentProjectId() + " AND \n"
                    + " 	column_id IN \n"
                    + " 		(SELECT \n"
                    + " 			column_id \n"
                    + " 		FROM \n"
                    + " 			project_columns \n"
                    + " 		WHERE \n"
                    + " 			project_columns.column_name LIKE '" + column + "' \n"
                    + " 		) \n"
                    + " LIMIT 50 \n");

            while (rs.next()) {
                arrayReturn.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return arrayReturn;
    }

    private ArrayList<String> loadFoundVariables(String filter) {
        /*
         * se saca el listado de variables encontradas, sin importar
         * que se encuentren relacionadas o no
         */
        ArrayList<String> arrayReturn = new ArrayList<String>();
        if (projectsMB != null) {
            try {
                ResultSet rs;
                String filterConsult = "";
                if (filter != null && filter.trim().length() != 0) {
                    filterConsult = "column_name ILIKE '%" + filter + "%' AND \n";
                }
                sql = ""
                        + " SELECT \n"
                        + "	   column_name,column_id \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n" + filterConsult
                        + "	   project_id = " + projectsMB.getCurrentProjectId() + " \n"
                        + " ORDER BY \n"
                        + "	   column_id \n";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                while (rs.next()) {
                    arrayReturn.add(rs.getString(1));
                }
            } catch (Exception e) {
                System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return arrayReturn;
    }

    private ArrayList<String> loadFoundVariablesRemovingRelated() {
        /*
         * se saca el listado de variables encontradas, se quitan las 
         * que se encuentren ya relacionadas (en relacion de variables)
         */
        ArrayList<String> arrayReturn = new ArrayList<String>();
        if (projectsMB != null) {
            try {
                ResultSet rs;
                sql = ""
                        + " SELECT \n"
                        + "	   project_columns.column_name \n"
                        + " FROM \n"
                        + "	   project_columns \n"
                        + " WHERE \n"
                        + "	   project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                        + "	   project_columns.column_name NOT IN \n"
                        + "	   (SELECT \n"
                        + "		relation_variables.name_found \n"
                        + "	   FROM \n"
                        + "		public.relation_group, \n"
                        + "		public.relation_variables \n"
                        + "	   WHERE \n"
                        + "		relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                        + "		relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                        + "	   ) \n"
                        + " ORDER BY \n"
                        + "	   project_columns.column_id \n";
                rs = connectionJdbcMB.consult(sql);            //System.out.println("A002\n" + sql);
                while (rs.next()) {
                    arrayReturn.add(rs.getString(1));
                }
            } catch (Exception e) {
                System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
        return arrayReturn;
    }

    //--------------------------------------------------------------------------
    //-------------------------- COPY ------------------------------------------
    //--------------------------------------------------------------------------
    public void changeVariableFoundToCopy() {
        valuesFoundToCopy = loadFoundValues(variableNameToCopy);
        copyPrefix = variableNameToCopy;
    }

    public void copyColumnsClick() {
        boolean continueProcess = true;
        if (continueProcess) {
            if (haveSpaces(splitFieldName1) || haveSpaces(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variableNameToCopy == null || variableNameToCopy.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe selecciona una variable de la lista"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (copyPrefix == null || copyPrefix.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un prefijo para nombrar las nuevas columnas"));
                continueProcess = false;
            }
        }

        if (continueProcess) {
            copyPrefix = copyPrefix.toLowerCase();
            try {
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);
                for (int i = 0; i < numberOfCopies; i++) {
                    maxColumnId++;
                    //inserto una nueva columna en project_columns                       
                    sql = ""
                            + " INSERT INTO project_columns VALUES ("
                            + String.valueOf(maxColumnId) + ",'"
                            + String.valueOf(determineColumnName(copyPrefix)) + "',"
                            + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                    connectionJdbcMB.non_query(sql);

                    //realizo la copia de la columna
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    project_records \n"
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "         project_columns.column_name LIKE '" + variableNameToCopy + "' "
                            + "    )");
                    while (rs.next()) {
                        sql = ""
                                + " INSERT INTO project_records VALUES ("
                                + rs.getString("project_id") + ","
                                + rs.getString("record_id") + ","
                                + String.valueOf(maxColumnId) + ",'"
                                + rs.getString("data_value") + "')";
                        connectionJdbcMB.non_query(sql);
                    }
                }
                reset();//limpiar 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la copia de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }

    public void changeVariableNameToCopyFilter() {
        loadFoundVariables(variableNameToCopyFilter);
        copyPrefix = "";
        valuesFoundToCopy = new ArrayList<String>();
        variableNameToCopy = "";
    }

    //--------------------------------------------------------------------------
    //------------------------- DELETE -----------------------------------------
    //--------------------------------------------------------------------------
    public void deleteVariables() {
        if (variablesPickToDelete.getTarget().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Agrege a la segunda lista las variables a eliminar"));
        } else {
            try {
                ResultSet rs;
                for (int i = 0; i < variablesPickToDelete.getTarget().size(); i++) {
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    project_records \n"
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "         project_columns.column_name LIKE '" + variablesPickToDelete.getTarget().get(i) + "' "
                            + "    )");
                    while (rs.next()) {
                        sql = ""
                                + " DELETE FROM \n"
                                + "    project_records \n"
                                + " WHERE \n"
                                + "    column_id = " + rs.getString("column_id") + " AND \n"
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " \n";
                        connectionJdbcMB.non_query(sql);
                    }
                    sql = ""
                            + " DELETE FROM \n"
                            + "    project_columns \n"
                            + " WHERE \n"
                            + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                            + "    project_columns.column_name LIKE '" + variablesPickToDelete.getTarget().get(i) + "' ";
                    connectionJdbcMB.non_query(sql);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La eliminacion de las columnas se ha realizado correctamente"));
                reset();
                relationshipOfVariablesMB.refresh();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString()));
            }
        }
    }

    //--------------------------------------------------------------------------
    //------------------------- SPLIT -----------------------------------------
    //--------------------------------------------------------------------------
    public void changeVariableFoundToSplit() {
        valuesFoundToSplit = loadFoundValues(variableNameToSplit);
    }

    public void splitColumnsClick() {
        boolean continueProcess = true;
        if (continueProcess) {
            if (variableNameToSplit == null || variableNameToSplit.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar la variable a dividir"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitFieldName1 == null || splitFieldName1.trim().length() == 0 || splitFieldName2 == null || splitFieldName2.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben digitar los dos nuevos nombres de columna"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitDelimiter == null || splitDelimiter.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un separador"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (splitFieldName1.compareToIgnoreCase(splitFieldName2) == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden ser iguales"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (haveSpaces(splitFieldName1) || haveSpaces(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los nuevos nombres de columnas no pueden contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            splitFieldName1 = splitFieldName1.toLowerCase();
            splitFieldName2 = splitFieldName2.toLowerCase();
            //determinar si ya existen estos nombres 
            if (searchColumn(splitFieldName1)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + splitFieldName1 + "), debe ser cambiado"));
                continueProcess = false;
            }
            if (searchColumn(splitFieldName2)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + splitFieldName2 + "), debe ser cambiado"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);

                maxColumnId++;
                //inserto una nueva columna en project_columns
                sql = ""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId) + ",'"
                        + String.valueOf(splitFieldName1) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                connectionJdbcMB.non_query(sql);
                sql = ""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId + 1) + ",'"
                        + String.valueOf(splitFieldName2) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                connectionJdbcMB.non_query(sql);

                //realizo la copia de la columna
                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    * \n"
                        + " FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    column_id IN "
                        + "    ( "
                        + "      SELECT \n"
                        + "         column_id \n"
                        + "      FROM \n"
                        + "         project_columns \n"
                        + "      WHERE \n"
                        + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                        + "         project_columns.column_name LIKE '" + variableNameToSplit + "' "
                        + "    )");
                String[] splitValue;
                while (rs.next()) {
                    splitValue = splitByDigit(rs.getString("data_value"));
                    if (splitValue != null && splitValue.length > 0) {
                        sql = ""
                                + " INSERT INTO project_records VALUES ("
                                + rs.getString("project_id") + ","
                                + rs.getString("record_id") + ","
                                + String.valueOf(maxColumnId) + ",'"
                                + splitValue[0] + "')";
                        connectionJdbcMB.non_query(sql);
                    }
                    if (splitValue != null && splitValue.length > 1) {
                        sql = ""
                                + " INSERT INTO project_records VALUES ("
                                + rs.getString("project_id") + ","
                                + rs.getString("record_id") + ","
                                + String.valueOf(maxColumnId + 1) + ",'"
                                + splitValue[1] + "')";
                        connectionJdbcMB.non_query(sql);
                    }
                }
                reset();
                relationshipOfVariablesMB.refresh();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la división de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + ex.toString());
            }
        }
    }

    public String[] splitByDigit(String text) {
        String[] split = null;
        char chr;
        boolean foundDigit = false;
        String string1 = "";
        String string2 = "";

        if (text == null || text.length() == 0) {
            return null;
        }
        split = new String[2];
        split[0] = "";
        split[1] = "";
        if (splitDelimiter.compareTo("#") == 0) {//REALIZAR DIVISION POR NUMEROS            
            for (int i = 0; i < text.length(); i++) {
                chr = text.charAt(i);
                if (isDigit(chr)) {//es digito
                    foundDigit = true;//digito enconrado
                    if (splitInclude) {//incluir digitos                    
                        string2 = string2 + chr;
                    }
                } else {//no es digito
                    if (foundDigit) {//se encontro digito
                        string2 = string2 + chr;
                    } else {
                        string1 = string1 + chr;
                    }
                }
            }
            split[0] = string1;
            split[1] = string2;
        } else {//REALIZAR SPLIT NORMAL
            if (text.indexOf(splitDelimiter) == -1) {
                split[0] = text;
            } else {
                split[0] = text.substring(0, text.indexOf(splitDelimiter));
                split[1] = text.substring(text.indexOf(splitDelimiter) + splitDelimiter.length(), text.length());
            }
        }
        return split;
    }

    private boolean isDigit(char chr) {
        if (chr >= 48 && chr <= 57) {
            return true;
        } else {
            return false;
        }
    }

    public void setRenders() {
        if ("#".equals(splitDelimiter.trim())) {
            splitRendered = true;
        } else {
            splitRendered = false;
        }
    }

    //--------------------------------------------------------------------------
    //---------------------- MERGE -------------------------------
    //--------------------------------------------------------------------------
    public void mergeFieldsClick() {
        boolean continueProcess = true;
        if (mergeDelimiter == null) {
            mergeDelimiter = "";
        }
        if (continueProcess) {
            if (variablesPickToMerge.getTarget().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Agrege a la segunda lista las variables a unir"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variablesPickToMerge.getTarget().size() < 2) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben agregar mínimo dos variables para realizar la unión"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (variableNameToMerge == null || variableNameToMerge.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar el nombre de la nueva columna"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (haveSpaces(variableNameToMerge)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nuevos nombre de columna no puede contener espacios"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            variableNameToMerge = variableNameToMerge.toLowerCase();
            //determinar si ya existen estos nombres 
            if (searchColumn(variableNameToMerge)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una columna de nombre (" + variableNameToMerge + "), debe ser cambiado"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                //determino el maximo id column_id (tabla project_columns)    
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    MAX(column_id) \n"
                        + " FROM \n"
                        + "    project_columns \n");
                rs.next();
                int maxColumnId = rs.getInt(1);

                maxColumnId++;
                //inserto una nueva columna en project_columns
                sql = ""
                        + " INSERT INTO project_columns VALUES ("
                        + String.valueOf(maxColumnId) + ",'"
                        + String.valueOf(variableNameToMerge) + "',"
                        + String.valueOf(projectsMB.getCurrentProjectId()) + ")";
                connectionJdbcMB.non_query(sql);
                ArrayList<String> dataRecords = new ArrayList<String>();

                //determinamos las variables(column_id)
                ArrayList<String> columns_id = new ArrayList<String>();
                for (int i = 0; i < variablesPickToMerge.getTarget().size(); i++) {
                    rs = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    column_id \n"
                            + " FROM \n"
                            + "    project_columns \n"
                            + " WHERE \n"
                            + "    project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "    project_columns.column_name LIKE '" + variablesPickToMerge.getTarget().get(i) + "' ");
                    if (rs.next()) {
                        columns_id.add(rs.getString(1));
                    }
                }
                //determinamos los identificadores de los registros
                ArrayList<String> records_id = new ArrayList<String>();
                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    DISTINCT(record_id) \n"
                        + " FROM \n"
                        + "    project_records \n"
                        + " WHERE \n"
                        + "    project_id = " + projectsMB.getCurrentProjectId() + " \n"
                        + " ORDER BY "
                        + "    record_id ");
                while (rs.next()) {
                    records_id.add(rs.getString(1));
                }
                //consulta que retorna los valores a agrupar por cada fila
                for (int j = 0; j < records_id.size(); j++) {
                    sql = " SELECT ";
                    for (int i = 0; i < columns_id.size(); i++) {
                        sql = sql
                                + " ( SELECT \n"
                                + "      data_value \n"
                                + "   FROM \n"
                                + "      project_records \n"
                                + "   WHERE \n"
                                + "      project_id = " + projectsMB.getCurrentProjectId() + " AND \n"
                                + "      record_id = " + records_id.get(j) + " AND \n"
                                + "      column_id = " + columns_id.get(i) + "  \n"
                                + " ) AS var" + String.valueOf(i) + "";
                        if (i != columns_id.size() - 1) {
                            sql = sql + ", \n";
                        }
                    }
                    rs = connectionJdbcMB.consult(sql);
                    if (rs.next()) {
                        String salida = "";
                        for (int i = 0; i < columns_id.size(); i++) {
                            if (rs.getString(i + 1) != null && rs.getString(i + 1).length() != 0) {
                                if (i != columns_id.size() - 1) {
                                    salida = salida + rs.getString(i + 1) + mergeDelimiter;
                                } else {
                                    salida = salida + rs.getString(i + 1);
                                }
                            }
                        }
                        sql = ""
                                + " INSERT INTO project_records VALUES ("
                                + projectsMB.getCurrentProjectId() + ","
                                + String.valueOf(j) + ","
                                + String.valueOf(maxColumnId) + ",'"
                                + salida + "')";
                        connectionJdbcMB.non_query(sql);
                    }
                }
                reset();
                relationshipOfVariablesMB.refresh();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la división de columnas"));
            } catch (SQLException ex) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + ex.toString());
            }

        }

    }

    //--------------------------------------------------------------------------
    //---------------------- FILTER RECORDS -------------------------------
    //--------------------------------------------------------------------------
    public void changeVariablesFoundToFilterRecords() {
        filter_queryModel = new QueryDataModel(getFieldCounts(filter_field));
        filter_field_names = new ArrayList<String>();
        filter_field_names.add(filter_field);
        filter_field_names.add("# de Registros");
    }

    public List<FieldCount> getFieldCounts(String field) {
        try {
            List<FieldCount> data = new ArrayList<FieldCount>();
            String query = ""
                    + " SELECT "
                    + "   data_value, count(*) "
                    + " FROM "
                    + "   project_records "
                    + " WHERE \n"
                    + "    column_id IN "
                    + "    ( "
                    + "      SELECT \n"
                    + "         column_id \n"
                    + "      FROM \n"
                    + "         project_columns \n"
                    + "      WHERE \n"
                    + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "         project_columns.column_name LIKE '" + field + "' "
                    + "    ) AND "
                    + "    project_id = " + projectsMB.getCurrentProjectId() + ""
                    + " GROUP BY 1 ORDER BY 2 DESC"
                    + "";
            ResultSet records = connectionJdbcMB.consult(query);
            while (records.next()) {
                FieldCount fc = new FieldCount(records.getString(1), records.getInt(2));
                data.add(fc);
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void filterRecordsClick() {
        boolean continueProcess = true;

        if (continueProcess) {
            if (filter_field == null || filter_field.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la variable a filtrar"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            if (filter_selected == null || filter_selected.length == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado ningun valor a filtrar"));
                continueProcess = false;
            }
        }

        if (continueProcess) {
            try {
                for (FieldCount record : filter_selected) {
                    System.out.println("Deleted " + record.getField());

                    sql = ""
                            + " DELETE "
                            + " FROM "
                            + "   project_records "
                            + " WHERE \n"
                            + "    column_id IN "
                            + "    ( "
                            + "      SELECT \n"
                            + "         column_id \n"
                            + "      FROM \n"
                            + "         project_columns \n"
                            + "      WHERE \n"
                            + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "         project_columns.column_name LIKE '" + filter_field + "' "
                            + "    ) AND "
                            + "    project_id = " + projectsMB.getCurrentProjectId() + " AND "
                            + "    data_value LIKE '" + record.getField() + "'";
                    connectionJdbcMB.non_query(sql);
                }
                reset();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Se han filtrado los valores seleccionados"));
            } catch (Exception e) {
            }
        }
    }

    //--------------------------------------------------------------------------
    //---------------------- RENAME -------------------------------
    //--------------------------------------------------------------------------
    public List<ValueNewValue> getValuesOrderedByFrecuency(String field) {
        /*
         * Retorna los valores de un campo ordenados por su frecuencia
         */
        try {
            List<ValueNewValue> values = new ArrayList<ValueNewValue>();
            String query = ""
                    + " SELECT "
                    + "   data_value, count(*) "
                    + " FROM "
                    + "   project_records "
                    + " WHERE \n"
                    + "    column_id IN "
                    + "    ( "
                    + "      SELECT \n"
                    + "         column_id \n"
                    + "      FROM \n"
                    + "         project_columns \n"
                    + "      WHERE \n"
                    + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "         project_columns.column_name LIKE '" + field + "' "
                    + "    ) AND "
                    + "    project_id = " + projectsMB.getCurrentProjectId() + ""
                    + " GROUP BY 1 ORDER BY 2 DESC"
                    + "";
            ResultSet rows = connectionJdbcMB.consult(query);
            while (rows.next()) {
                values.add(new ValueNewValue(rows.getString(1), ""));
            }
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void changeFieldRename() {
        rename_model = getValuesOrderedByFrecuency(the_field);
        rename_field_names = new ArrayList<String>();
        rename_field_names.add(the_field);
        rename_field_names.add("# de Registros");
    }

    public void renameRecordsClick() {
        boolean continueProcess = true;
        if (continueProcess) {
            if (the_field == null || the_field.length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una variable"));
                continueProcess = false;
            }
        }
        if (continueProcess) {
            try {
                for (ValueNewValue values : rename_model) {
                    if (values.getNewValue() != null && values.getNewValue().length() != 0) {
                        sql = ""
                                + " UPDATE "
                                + "   project_records "
                                + " SET "
                                + " data_value = '"+values.getNewValue()+"'"                                
                                + " WHERE \n"
                                + "    column_id IN "
                                + "    ( "
                                + "      SELECT \n"
                                + "         column_id \n"
                                + "      FROM \n"
                                + "         project_columns \n"
                                + "      WHERE \n"
                                + "         project_columns.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "         project_columns.column_name LIKE '" + the_field + "' "
                                + "    ) AND "
                                + "    project_id = " + projectsMB.getCurrentProjectId() + " AND "
                                + "    data_value LIKE '" + values.getOldValue() + "'";
                        connectionJdbcMB.non_query(sql);
                    }                    
                }
                reset();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error", "Se han renombrado los valores indicados"));
            } catch (Exception e) {
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //---------------------- REPLICATE -------------------------------
    //--------------------------------------------------------------------------
    public  void replicateClick(){
        if (replicate_source.size() % replicate_target.size() != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error",
                    "El número de variables correspondientes debería ser factor del "
                    + "número de variables a replicar."));
            return;
        }
        //executeReplication(replicateFields, replicate_target, replicate_source);
        reset();
        //fields = connection.getTempFields();
        
        replicate_source = new ArrayList<String>();
        replicate_target = new ArrayList<String>();
        //this.refreshReplicate();
        //btnReplicateDisable = false;
        //undoReplicate++;
    }

    //--------------------------------------------------------------------------
    //---------------------- GET Y SET VARIABLES -------------------------------
    //--------------------------------------------------------------------------
    public ProjectsMB getProjectsMB() {
        return projectsMB;
    }

    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
    }

    public String getVariableNameToCopy() {
        return variableNameToCopy;
    }

    public void setVariableNameToCopy(String variableNameToCopy) {
        this.variableNameToCopy = variableNameToCopy;
    }

    public List<String> getVariablesFoundToCopy() {
        return variablesFoundToCopy;
    }

    public void setVariablesFoundToCopy(List<String> variablesFoundToCopy) {
        this.variablesFoundToCopy = variablesFoundToCopy;
    }

    public List<String> getValuesFoundToCopy() {
        return valuesFoundToCopy;
    }

    public void setValuesFoundToCopy(List<String> valuesFoundToCopy) {
        this.valuesFoundToCopy = valuesFoundToCopy;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public String getCopyPrefix() {
        return copyPrefix;
    }

    public void setCopyPrefix(String copyPrefix) {
        this.copyPrefix = copyPrefix;
    }

    public String getVariableNameToCopyFilter() {
        return variableNameToCopyFilter;
    }

    public void setVariableNameToCopyFilter(String variableNameToCopyFilter) {
        this.variableNameToCopyFilter = variableNameToCopyFilter;
    }

    public DualListModel<String> getVariablesPickToDelete() {
        return variablesPickToDelete;
    }

    public void setVariablesPickToDelete(DualListModel<String> variablesPickToDelete) {
        this.variablesPickToDelete = variablesPickToDelete;
    }

    public String getVariableNameToSplit() {
        return variableNameToSplit;
    }

    public void setVariableNameToSplit(String variableNameToSplit) {
        this.variableNameToSplit = variableNameToSplit;
    }

    public List<String> getVariablesFoundToSplit() {
        return variablesFoundToSplit;
    }

    public void setVariablesFoundToSplit(List<String> variablesFoundToSplit) {
        this.variablesFoundToSplit = variablesFoundToSplit;
    }

    public List<String> getValuesFoundToSplit() {
        return valuesFoundToSplit;
    }

    public void setValuesFoundToSplit(List<String> valuesFoundToSplit) {
        this.valuesFoundToSplit = valuesFoundToSplit;
    }

    public String getSplitFieldName1() {
        return splitFieldName1;
    }

    public void setSplitFieldName1(String splitFieldName1) {
        this.splitFieldName1 = splitFieldName1;
    }

    public String getSplitFieldName2() {
        return splitFieldName2;
    }

    public void setSplitFieldName2(String splitFieldName2) {
        this.splitFieldName2 = splitFieldName2;
    }

    public String getSplitDelimiter() {
        return splitDelimiter;
    }

    public void setSplitDelimiter(String splitDelimiter) {
        this.splitDelimiter = splitDelimiter;
    }

    public boolean isSplitRendered() {
        return splitRendered;
    }

    public void setSplitRendered(boolean splitRendered) {
        this.splitRendered = splitRendered;
    }

    public boolean isSplitInclude() {
        return splitInclude;
    }

    public void setSplitInclude(boolean splitInclude) {
        this.splitInclude = splitInclude;
    }

    public DualListModel<String> getVariablesPickToMerge() {
        return variablesPickToMerge;
    }

    public void setVariablesPickToMerge(DualListModel<String> variablesPickToMerge) {
        this.variablesPickToMerge = variablesPickToMerge;
    }

    public String getVariableNameToMerge() {
        return variableNameToMerge;
    }

    public void setVariableNameToMerge(String variableNameToMerge) {
        this.variableNameToMerge = variableNameToMerge;
    }

    public String getMergeDelimiter() {
        return mergeDelimiter;
    }

    public void setMergeDelimiter(String mergeDelimiter) {
        this.mergeDelimiter = mergeDelimiter;
    }

    public QueryDataModel getFilter_queryModel() {
        return filter_queryModel;
    }

    public void setFilter_queryModel(QueryDataModel filter_queryModel) {
        this.filter_queryModel = filter_queryModel;
    }

    public List<String> getFilter_headers() {
        return filter_headers;
    }

    public void setFilter_headers(List<String> filter_headers) {
        this.filter_headers = filter_headers;
    }

    public List<String> getFilter_field_names() {
        return filter_field_names;
    }

    public void setFilter_field_names(List<String> filter_field_names) {
        this.filter_field_names = filter_field_names;
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

    public String getFilter_field() {
        return filter_field;
    }

    public void setFilter_field(String filter_field) {
        this.filter_field = filter_field;
    }

    public List<String> getVariablesFoundToFilterRecords() {
        return variablesFoundToFilterRecords;
    }

    public void setVariablesFoundToFilterRecords(List<String> variablesFoundToFilterRecords) {
        this.variablesFoundToFilterRecords = variablesFoundToFilterRecords;
    }

    public List<ValueNewValue> getRename_model() {
        return rename_model;
    }

    public void setRename_model(List<ValueNewValue> rename_model) {
        this.rename_model = rename_model;
    }

    public List<String> getRename_headers() {
        return rename_headers;
    }

    public void setRename_headers(List<String> rename_headers) {
        this.rename_headers = rename_headers;
    }

    public List<String> getRename_field_names() {
        return rename_field_names;
    }

    public void setRename_field_names(List<String> rename_field_names) {
        this.rename_field_names = rename_field_names;
    }

    public int getRedoRename() {
        return redoRename;
    }

    public void setRedoRename(int redoRename) {
        this.redoRename = redoRename;
    }

    public String getThe_field() {
        return the_field;
    }

    public void setThe_field(String the_field) {
        this.the_field = the_field;
    }

    public List<String> getVariablesFoundToRename() {
        return variablesFoundToRename;
    }

    public void setVariablesFoundToRename(List<String> variablesFoundToRename) {
        this.variablesFoundToRename = variablesFoundToRename;
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

//    public List<String> getReplicate_columns2() {
//        return replicate_columns2;
//    }
//
//    public void setReplicate_columns2(List<String> replicate_columns2) {
//        this.replicate_columns2 = replicate_columns2;
//    }
//
//    public LazyDataModel<List> getReplicate_model2() {
//        return replicate_model2;
//    }
//
//    public void setReplicate_model2(LazyDataModel<List> replicate_model2) {
//        this.replicate_model2 = replicate_model2;
//    }

    public List<String> getReplicateFields() {
        return replicateFields;
    }

    public void setReplicateFields(List<String> replicateFields) {
        this.replicateFields = replicateFields;
    }
    
    
}
