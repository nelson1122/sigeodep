/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.errorsControl.ErrorControl;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlColumn;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.model.SelectItem;
import managedBeans.preload.FormsAndFieldsDataMB;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;

/**
 *
 * @author santos
 */
@ManagedBean(name = "errorsControlMB")
@SessionScoped
public class ErrorsControlMB {

    ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private String currentError = "";//error actual
    private SelectItem[] errors;
    private boolean btnSolveDisabled = true;
    private String currentAceptedValue = "";//proveedor actual    
    private SelectItem[] aceptedValues;
    private SelectItem[] correctionList;
    private String currentCorrection;
    private boolean btnUndoDisabled = true;
    private ArrayList<ErrorControl> errorControlArrayList;
    private ArrayList<ErrorControl> errorCorrectionArrayList;
    private int sizeErrorsList = 0;
    private String solution = " ";
    private String currentDateFormat;
    private boolean selectDateFormatDisabled;
    //private boolean btnSeeRecordDisabled = true;
    private boolean btnSeeRecordDisabled = true;
    //#{car[column.property]}  
    private String currentDateFormatAcepted;
    private String currentNewValue;
    private String description = "";
    private String valueFound = "";
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationVar relationVar;
    private RelationsGroup currentRelationsGroup;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private ResultSet rsTemp;
    private static List<List<String>> dynamicList; // matriz de los datos
    private static String[] dynamicHeaders; // cabecera
    private HtmlPanelGroup dynamicDataTableGroup; // posicion donde se imprimira.

    public ErrorsControlMB() {
        correctionList = new SelectItem[0];
        errorControlArrayList = new ArrayList<ErrorControl>();
        errorCorrectionArrayList = new ArrayList<ErrorControl>();
    }
    /////////////////////////////////////////////////44444444444444444444444444444444444444444444444

    // Helpers -----------------------------------------------------------------------------------
    private ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createValueExpression(
                facesContext.getELContext(), valueExpression, valueType);
    }

    private void createDynamicDataTable() {

        // Create <h:dataTable value="#{myBean.dynamicList}" var="dynamicItem">.
        HtmlDataTable dynamicDataTable = new HtmlDataTable();
        dynamicDataTable.setValueExpression("value",
                createValueExpression("#{errorsControlMB.dynamicList}", List.class));
        dynamicDataTable.setVar("dynamicItem");

        // Iterate over columns.
        for (int i = 0; i < dynamicList.get(0).size(); i++) {

            // Create <h:column>.
            HtmlColumn column = new HtmlColumn();
            dynamicDataTable.getChildren().add(column);

            // Create <h:outputText value="dynamicHeaders[i]"> for <f:facet name="header"> of column.
            HtmlOutputText header = new HtmlOutputText();
            header.setValue(dynamicHeaders[i]);
            column.setHeader(header);

            // Create <h:outputText value="#{dynamicItem[" + i + "]}"> for the body of column.
            HtmlOutputText output = new HtmlOutputText();
            output.setValueExpression("value",
                    createValueExpression("#{dynamicItem[" + i + "]}", String.class));
            column.getChildren().add(output);
        }

        // Add the datatable to <h:panelGroup binding="#{myBean.dynamicDataTableGroup}">.
        dynamicDataTableGroup = new HtmlPanelGroup();
        dynamicDataTableGroup.getChildren().add(dynamicDataTable);
    }

    public final void createDynamicColumns() {

        ArrayList<String> columnas = new ArrayList<String>();
        ArrayList<String> datos = new ArrayList<String>();
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            //determino el error que esta seleccionado en la lista
            ErrorControl currentE = null;
            for (int i = 0; i < errorControlArrayList.size(); i++) {
                if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                    currentE = errorControlArrayList.get(i);
                    break;
                }
            }

            if (currentE != null) {
                ResultSet rs = conx.consult("SELECT * FROM temp WHERE id='" + currentE.getRowId() + "'");

                // determino las cabeceras
                for (int j = 1; j < rs.getMetaData().getColumnCount(); j++) {
                    columnas.add(rs.getMetaData().getColumnName(j));
                }
                dynamicHeaders = new String[columnas.size()];
                for (int i = 0; i < columnas.size(); i++) {
                    dynamicHeaders[i] = columnas.get(i);
                }

                // determino los datos                
                rs.next();
                for (int k = 1; k < rs.getMetaData().getColumnCount(); k++) {
                    datos.add(rs.getString(k));
                }
                String[] auxArray = new String[datos.size()];
                for (int i = 0; i < datos.size(); i++) {
                    auxArray[i] = datos.get(i);
                }
                dynamicList = new ArrayList<List<String>>();
                //dynamicList.add(Arrays.asList(new String[]{"ID1", "Name1", "Value1"}));
                dynamicList.add(Arrays.asList(auxArray));
                createDynamicDataTable(); // Creacion del codigo que representa la tabla.
            }
            conx.disconnect();
        } catch (SQLException ex) {
            System.out.println("Error en la creacion de columnas dinamicas: " + ex.toString());
        }
    }
    /////////////////////////////////////////////////44444444444444444444444444444444444444444444444

    public void reset() {
    }

    private void updateErrors() {
        if (errorControlArrayList.isEmpty()) {
            errors = new SelectItem[0];
            btnSolveDisabled = true;
            btnSeeRecordDisabled = true;
        } else {
            errors = new SelectItem[errorControlArrayList.size()];
            for (int i = 0; i < errorControlArrayList.size(); i++) {
                errors[i] = new SelectItem(errorControlArrayList.get(i).getErrorDescription());
            }
        }
    }

    public void addError(ErrorControl error) {
        errorControlArrayList.add(error);
        updateErrors();
    }

    private void updateCorrectionArrayList() {
        correctionList = new SelectItem[errorCorrectionArrayList.size()];
        for (int j = 0; j < errorCorrectionArrayList.size(); j++) {
            correctionList[j] = new SelectItem(
                    String.valueOf(j + 1) + ". Se cambio el valor (" + errorCorrectionArrayList.get(j).getValue()
                    + ") por (" + errorCorrectionArrayList.get(j).getNewValue() + ") en la fila ("
                    + errorCorrectionArrayList.get(j).getRowId()
                    + ") columna (" + errorCorrectionArrayList.get(j).getVarFoundName() + ")");
        }
    }

    public int solveError() {
        //verifico que el nuevo dato sea un valor esperado
        for (int i = 0; i < errorControlArrayList.size(); i++) {
            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();
                relationVar = currentRelationsGroup.findRelationVar(errorControlArrayList.get(i).getVarFoundName());
                //valores aceptados
                if (errorControlArrayList.get(i).getTypeExepted().compareTo("text") == 0) {//valor de tipo texto no se valida
                } else if (errorControlArrayList.get(i).getTypeExepted().compareTo("integer") == 0) {//si el dato no es numerico adiciono el error 
                    if (!isNumeric(currentNewValue)) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El valor ingresado no es entero");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        //se realiza la actualizacion de la tabla
                        conx = new ConnectionJDBC();
                        conx.connect();
                        conx.update("temp", errorControlArrayList.get(i).getVarFoundName() + "='" + currentNewValue + "'", "id=" + errorControlArrayList.get(i).getRowId());
                        //quitamos el error de la lista
                        errorControlArrayList.get(i).setNewValue(currentNewValue);
                        errorCorrectionArrayList.add(errorControlArrayList.get(i));
                        errorControlArrayList.remove(i);

                        sizeErrorsList--;
                        updateErrorsArrayList();
                        btnSolveDisabled = true;

                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        conx.disconnect();
                        return 0;
                    }
                } else if (errorControlArrayList.get(i).getTypeExepted().compareTo("date") == 0) {//si el dato no es fecha validala adiciono el error 
                    if (!isDate(currentNewValue, currentDateFormat)) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La fecha especificada no corresponde al formato indicado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        //se realiza la actualizacion de la tabla
                        conx = new ConnectionJDBC();
                        conx.connect();
                        conx.update("temp", errorControlArrayList.get(i).getVarFoundName() + "='" + currentNewValue + "'", "id=" + errorControlArrayList.get(i).getRowId());
                        //quitamos el error de la lista
                        errorControlArrayList.get(i).setNewValue(currentNewValue);
                        errorCorrectionArrayList.add(errorControlArrayList.get(i));
                        errorControlArrayList.remove(i);
                        sizeErrorsList--;
                        updateErrorsArrayList();
                        btnSolveDisabled = true;
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        conx.disconnect();
                        return 0;

                    }
                } else {//se espera un valorcategorico
                    if (!isCategorical(
                            currentNewValue, errorControlArrayList.get(i).getVarExeptedName(),
                            errorControlArrayList.get(i).isTypeComparisonForCode(), relationVar.getRelationValueList())) {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El valor ingresado no se encuentra dentro de la categoria");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        //se realiza la actualizacion de la tabla
                        conx = new ConnectionJDBC();
                        conx.connect();
                        conx.update("temp", errorControlArrayList.get(i).getVarFoundName() + "='" + currentNewValue + "'", "id=" + errorControlArrayList.get(i).getRowId());
                        //quitamos el error de la lista
                        errorControlArrayList.get(i).setNewValue(currentNewValue);
                        errorCorrectionArrayList.add(errorControlArrayList.get(i));
                        errorControlArrayList.remove(i);
                        sizeErrorsList--;
                        
                        btnSolveDisabled = true;
                        updateErrorsArrayList();
                        updateCorrectionArrayList();

                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        conx.disconnect();
                        return 0;
                    }
                }



                break;
            }
        }
        return 0;
    }
//----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    private boolean isNumeric(String str) {
        /*
         * validacion de si un string es entero
         */
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isDate(String fecha, String format) {
        /*
         * validacion de si un string es una fecha de un determinado formto
         */
        SimpleDateFormat formato = new SimpleDateFormat(format);
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private boolean isCategorical(String str, String category, boolean compareForCode, ArrayList<RelationValue> relationValueList) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        ArrayList<String> categoryList = new ArrayList<String>();
        //se valida con respecto a las relaciones de variables
        for (int i = 0; i < relationValueList.size(); i++) {//le paso a categori list los valores encontrados en la relacion de valores
            categoryList.add(relationValueList.get(i).getNameFound());
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        //se valida con respecto a los valores esperados

        if (compareForCode == true) {
            categoryList = formsAndFieldsDataMB.categoricalCodeList(category, 0);
        } else {
            categoryList = formsAndFieldsDataMB.categoricalNameList(category, 0);
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONES Y LISTAS --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public void btnUndoErrorClick() {
        for (int i = 0; i < correctionList.length; i++) {
            //String i_str=String.valueOf(i+1)+". ";
            if (correctionList[i].getValue().toString().compareTo(currentCorrection) == 0) {
            
                try {
                    conx = new ConnectionJDBC();
                    conx.connect();
                    //determino el error que esta seleccionado en la lista
                    //ErrorControl currentE = null;
                    //ResultSet rs = conx.consult("SELECT * FROM temp WHERE id='" + currentE.getRowId() + "'");
                    int id_int=Integer.parseInt(errorControlArrayList.get(i).getRowId())+1;
                    
                    conx.update("temp",
                            errorControlArrayList.get(i).getVarFoundName()+"='"+errorControlArrayList.get(i).getValue()+"'",
                            "id="+String.valueOf(id_int));
                    //elimino del historial
                    errorControlArrayList.remove(i);
                    updateErrorsArrayList();
                    updateCorrectionArrayList();
                    conx.disconnect();
                } catch (Exception ex) {
                    System.out.println("Error en la creación de columnas dinamicas: " + ex.toString());
                }

                break;
            }
        }
    }

    public void changeCorrectionList() {
        btnUndoDisabled = false;
    }

    public void changeErrorsList() {
        updateErrorsArrayList();
        for (int i = 0; i < errorControlArrayList.size(); i++) {
            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                //descripcion
                description = errorControlArrayList.get(i).getErrorDescription();
                //valores aceptados
                if (errorControlArrayList.get(i).getTypeExepted().compareTo("text") == 0) {//valor de tipo texto no se valida
                    aceptedValues = new SelectItem[]{new SelectItem("Cualquier texto", "Cualquier texto"),};
                    selectDateFormatDisabled = true;
                } else if (errorControlArrayList.get(i).getTypeExepted().compareTo("integer") == 0) {//si el dato no es numerico adiciono el error 
                    aceptedValues = new SelectItem[]{new SelectItem("Número entero", "Número Entero"),};
                    selectDateFormatDisabled = true;
                } else if (errorControlArrayList.get(i).getTypeExepted().compareTo("date") == 0) {//si el dato no es fecha validala adiciono el error 
                    aceptedValues = new SelectItem[]{new SelectItem("Una fecha válida", "Una fecha válida"),};
                    selectDateFormatDisabled = false;
                } else {//se espera un valorcategorico
                    selectDateFormatDisabled = true;
                    btnSolveDisabled = false;
                    ArrayList<String> categoricalList;
                    if (errorControlArrayList.get(i).isTypeComparisonForCode()) {
                        categoricalList = formsAndFieldsDataMB.categoricalCodeList(
                                errorControlArrayList.get(i).getVarExeptedName(), 0);
                    } else {
                        categoricalList = formsAndFieldsDataMB.categoricalNameList(
                                errorControlArrayList.get(i).getVarExeptedName(), 0);
                    }
                    aceptedValues = new SelectItem[categoricalList.size()];
                    for (int j = 0; j < categoricalList.size(); j++) {
                        aceptedValues[j] = new SelectItem(categoricalList.get(j));
                    }
                }
                btnSolveDisabled = false;
                btnSeeRecordDisabled = false;
                createDynamicColumns();
                //valor actual
                valueFound = errorControlArrayList.get(i).getValue();
                //solucion
                solution = errorControlArrayList.get(i).getErrorSolution();
                break;
            }
        }
    }

    public void updateErrorsArrayList() {
        errors = new SelectItem[errorControlArrayList.size()];
        for (int i = 0; i < errorControlArrayList.size(); i++) {
            errors[i] = new SelectItem(errorControlArrayList.get(i).getErrorDescription());
        }
        setAceptedValues(new SelectItem[0]);
        setBtnSolveDisabled(false);
        setCurrentAceptedValue("");
        //setCurrentError("");
        setCurrentNewValue("");
        setDescription("");
        setSelectDateFormatDisabled(true);
        setSolution("");
        setValueFound("");

    }

    public void changeAcceptedValuesList() {
        currentNewValue = currentAceptedValue;
        btnSolveDisabled = false;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public SelectItem[] getAceptedValues() {
        return aceptedValues;
    }

    public void setAceptedValues(SelectItem[] aceptedValues) {
        this.aceptedValues = aceptedValues;
    }

    public boolean isBtnSolveDisabled() {
        return btnSolveDisabled;
    }

    public void setBtnSolveDisabled(boolean btnSolveDisabled) {
        this.btnSolveDisabled = btnSolveDisabled;
    }

    public String getCurrentAceptedValue() {
        return currentAceptedValue;
    }

    public void setCurrentAceptedValue(String currentAceptedValue) {
        this.currentAceptedValue = currentAceptedValue;
    }

    public String getCurrentError() {
        return currentError;
    }

    public void setCurrentError(String currentError) {
        this.currentError = currentError;
    }

    public SelectItem[] getErrors() {
        return errors;
    }

    public void setErrors(SelectItem[] errors) {
        this.errors = errors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSizeErrorsList() {
        return sizeErrorsList;
    }

    public void setSizeErrorsList(int sizeErrorsList) {
        this.sizeErrorsList = sizeErrorsList;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getValueFound() {
        return valueFound;
    }

    public void setValueFound(String valueFound) {
        this.valueFound = valueFound;
    }

    public FormsAndFieldsDataMB getFormsAndFieldsDataMB() {
        return formsAndFieldsDataMB;
    }

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

    public String getCurrentDateFormat() {
        return currentDateFormat;
    }

    public void setCurrentDateFormat(String currentDateFormat) {
        this.currentDateFormat = currentDateFormat;
    }

    public boolean isSelectDateFormatDisabled() {
        return selectDateFormatDisabled;
    }

    public void setSelectDateFormatDisabled(boolean selectDateFormatDisabled) {
        this.selectDateFormatDisabled = selectDateFormatDisabled;
    }

    public String getCurrentNewValue() {
        return currentNewValue;
    }

    public void setCurrentNewValue(String currentNewValue) {
        this.currentNewValue = currentNewValue;
        if (currentNewValue.trim().length() == 0) {
            btnSolveDisabled = true;
        } else {
            btnSolveDisabled = false;
        }
    }

    public ArrayList<ErrorControl> getErrorControlArrayList() {
        return errorControlArrayList;
    }

    public void setErrorControlArrayList(ArrayList<ErrorControl> errorControlArrayList) {
        this.errorControlArrayList = errorControlArrayList;
    }

    public RelationsGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setCurrentRelationsGroup(RelationsGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public RelationshipOfVariablesMB getRelationshipOfVariablesMB() {
        return relationshipOfVariablesMB;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public String getCurrentDateFormatAcepted() {
        return currentDateFormatAcepted;
    }

    public void setCurrentDateFormatAcepted(String currentDateFormatAcepted) {
        this.currentDateFormatAcepted = currentDateFormatAcepted;
    }

    public boolean isBtnSeeRecordDisabled() {
        return btnSeeRecordDisabled;
    }

    public void setBtnSeeRecordDisabled(boolean btnSeeRecordDisabled) {
        this.btnSeeRecordDisabled = btnSeeRecordDisabled;
    }

    public boolean isBtnUndoDisabled() {
        return btnUndoDisabled;
    }

    public void setBtnUndoDisabled(boolean btnUndoDisabled) {
        this.btnUndoDisabled = btnUndoDisabled;
    }

    public SelectItem[] getCorrectionList() {
        return correctionList;
    }

    public void setCorrectionList(SelectItem[] correctionList) {
        this.correctionList = correctionList;
    }

    public String getCurrentCorrection() {
        return currentCorrection;
    }

    public void setCurrentCorrection(String currentCorrection) {
        this.currentCorrection = currentCorrection;
    }

    public HtmlPanelGroup getDynamicDataTableGroup() {
        return dynamicDataTableGroup;
    }

    public List<List<String>> getDynamicList() {
        return dynamicList;
    }

    public void setDynamicDataTableGroup(HtmlPanelGroup dynamicDataTableGroup) {
        this.dynamicDataTableGroup = dynamicDataTableGroup;
    }
}
