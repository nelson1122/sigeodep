/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.enumerators.DataTypeEnum;
import beans.errorsControl.ErrorControl;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.preload.FormsAndFieldsDataMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "errorsControlMB")
@SessionScoped
public class ErrorsControlMB implements Serializable {

    ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private String currentError = "";//error actual
    private SelectItem[] errors;
    private boolean btnSolveDisabled = true;
    private boolean btnDiscardDisabled = true;
    //private boolean btnDeleteRecordDisabled = true;
    private boolean btnUndoDisabled = true;
    private boolean selectDateFormatDisabled;
    private boolean btnSeeRecordDisabled = true;
    private String currentAceptedValue = "";//proveedor actual    
    private SelectItem[] aceptedValues;
    private SelectItem[] correctionList;
    private String currentCorrection;
    private ArrayList<ErrorControl> errorControlArrayList;
    private ArrayList<ErrorControl> errorCorrectionArrayList;
    private int sizeErrorsList = 0;
    private String solution = " ";
    private String currentDateFormat="dd/MM/yyyy";
    private String currentDateFormatAcepted;
    private String currentNewValue;
    private String description = "";
    private String valueFound = "";
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationVar relationVar;
    private RelationsGroup currentRelationsGroup;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    DinamicTable dinamicTable = new DinamicTable();

    public ErrorsControlMB() {
        correctionList = new SelectItem[0];
        errorControlArrayList = new ArrayList<ErrorControl>();
        errorCorrectionArrayList = new ArrayList<ErrorControl>();
    }

    public final void createDynamicTable() {
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<ArrayList<String>> listOfRecords = new ArrayList<ArrayList<String>>();
        try {
            //determino el error que esta seleccionado en la lista
            ErrorControl currentE = null;
            for (int i = 0; i < errorControlArrayList.size(); i++) {
                if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                    currentE = errorControlArrayList.get(i);
                    break;
                }
            }
            if (currentE != null) {
                conx = new ConnectionJDBC();
                conx.connect();
                ResultSet rs = conx.consult("SELECT * FROM temp WHERE id='" + currentE.getRowId() + "'");
                // determino las cabeceras
                for (int j = 1; j < rs.getMetaData().getColumnCount(); j++) {
                    titles.add(rs.getMetaData().getColumnName(j));
                }
                // determino los datos                
                ArrayList<String> newRow = new ArrayList<String>();
                rs.next();

                for (int k = 1; k < rs.getMetaData().getColumnCount(); k++) {
                    newRow.add(rs.getString(k));
                }
                listOfRecords.add(newRow);
                dinamicTable = new DinamicTable(listOfRecords, titles);
            }
            conx.disconnect();
        } catch (SQLException ex) {
            System.out.println("Error en la creacion de columnas dinamicas: " + ex.toString());
        }
    }

    public void reset() {
        correctionList = new SelectItem[0];
        errorControlArrayList = new ArrayList<ErrorControl>();
        errorCorrectionArrayList = new ArrayList<ErrorControl>();
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
                    + ") columna (" + errorCorrectionArrayList.get(j).getRelationVar().getNameFound() + ")");
        }
    }

//    public void deleteErrorRecord() {
//        for (int i = 0; i < errorControlArrayList.size(); i++) {
//            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
//                currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();
//                relationVar = currentRelationsGroup.findRelationVar(errorControlArrayList.get(i).getRelationVar().getNameFound());
//                //////////////////////////////
//                //creo la instruccion que revertira la eliminacion
//                //String sql="";
//                //se realiza la eliminacion de la tabla                    
//                conx = new ConnectionJDBC();
//                conx.connect();
//                String rowId=errorControlArrayList.get(i).getRowId();
//                conx.remove("temp", "id=" + rowId);
//
//                //quitamos los errores de esta linea de la lista
//                for (int j = errorControlArrayList.size(); j > -1; j--) {
//                    if(errorControlArrayList.get(j).getRowId().compareTo(rowId)==0){
//                        errorControlArrayList.remove(j);
//                        sizeErrorsList--;
//                    }
//                }
//                //adiciono la nueva correccion
//                //errorCorrectionArrayList.add(errorControlArrayList.get(i));               
//                
//                updateErrorsArrayList();
//                updateCorrectionArrayList();
//                btnSolveDisabled = true;
//                btnDiscardDisabled = true;
//                btnDeleteRecordDisabled = true;
//                btnSeeRecordDisabled = true;
//                conx.disconnect();
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Registro eliminado");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
////                    return 0;
////                }
//            }
//        }
////        return 0;
//    }

    public int discardError() {
        boolean correction = false;
        for (int i = 0; i < errorControlArrayList.size(); i++) {
            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();
                relationVar = currentRelationsGroup.findRelationVar(errorControlArrayList.get(i).getRelationVar().getNameFound());
                //////////////////////////////

                //verifico que la columna a descartar no sea la de intencionalidad
                switch (DataTypeEnum.convert(errorControlArrayList.get(i).getRelationVar().getFieldType())) {//tipo de relacion                    
                    case NOVALUE://categorical
                        if (errorControlArrayList.get(i).getRelationVar().getNameExpected().compareTo("intenci") == 0) {
                            correction = false;
                        } else {
                            correction = true;
                        }
                        break;
                }
                ///////////////////////////////                
                if (!correction) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Este campo debe contener un valor obligatoriamente");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    //se realiza la actualizacion de la tabla                    
                    String sqlUpdate = errorControlArrayList.get(i).getRelationVar().getNameFound() + "=''";
                    String sqlId = "id=" + errorControlArrayList.get(i).getRowId();
                    conx = new ConnectionJDBC();
                    conx.connect();
                    conx.update("temp", sqlUpdate, sqlId);
                    //quitamos el error de la lista
                    errorControlArrayList.get(i).setNewValue(currentNewValue);
                    errorCorrectionArrayList.add(errorControlArrayList.get(i));
                    errorControlArrayList.remove(i);
                    sizeErrorsList--;
                    updateErrorsArrayList();
                    updateCorrectionArrayList();
                    btnSolveDisabled = true;
                    btnDiscardDisabled = true;
                    //btnDeleteRecordDisabled = true;
                    btnSeeRecordDisabled = true;
                    conx.disconnect();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return 0;
                }
            }
        }
        return 0;
    }

    public int solveError() {
        boolean correction = false;
        //verifico que el nuevo dato sea un valor esperado
        for (int i = 0; i < errorControlArrayList.size(); i++) {
            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();
                relationVar = currentRelationsGroup.findRelationVar(errorControlArrayList.get(i).getRelationVar().getNameFound());
                //////////////////////////////
                switch (DataTypeEnum.convert(errorControlArrayList.get(i).getRelationVar().getFieldType())) {//tipo de relacion
                    case text:
                        break;
                    case integer:
                        if (isNumeric(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case age:
                        if (isAge(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case date:
                        if (isDate(currentNewValue, currentDateFormat)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case military:
                        if (isMilitary(currentNewValue) == null) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case hour:
                        if (isHour(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case minute:
                        if (isMinute(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case day:
                        if (isDay(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case month:
                        if (isMonth(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case year:
                        if (isYear(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case percentage:
                        if (isPercentage(currentNewValue)) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                    case NOVALUE://categorical
                        if (isCategorical(
                                currentNewValue, errorControlArrayList.get(i).getRelationVar().getNameExpected(),
                                errorControlArrayList.get(i).getRelationVar().getTypeComparisonForCode(), relationVar.getRelationValueList())) {
                            correction = true;
                        } else {
                            correction = false;
                        }
                        break;
                }
                ///////////////////////////////                
                if (!correction) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El valor ingresado no es aceptado como válido");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    //se realiza la actualizacion de la tabla
                    String sqlUpdate = errorControlArrayList.get(i).getRelationVar().getNameFound() + "='" + currentNewValue + "'";
                    String sqlId = "id=" + errorControlArrayList.get(i).getRowId();
                    conx = new ConnectionJDBC();
                    conx.connect();
                    conx.update("temp", sqlUpdate, sqlId);
                    //quitamos el error de la lista
                    errorControlArrayList.get(i).setNewValue(currentNewValue);
                    errorCorrectionArrayList.add(errorControlArrayList.get(i));
                    errorControlArrayList.remove(i);
                    sizeErrorsList--;
                    updateErrorsArrayList();
                    updateCorrectionArrayList();
                    btnSolveDisabled = true;
                    btnDiscardDisabled = true;
                    //btnDeleteRecordDisabled = true;
                    btnSeeRecordDisabled = true;
                    conx.disconnect();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El valor solucionó el error");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return 0;
                }
            }
        }
        return 0;
    }
//----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    private boolean isDay(String str) {
        /*
         * validacion de si un numero de 1 y 31
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 32) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isMonth(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 13) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isYear(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        if (str.trim().length() != 2 && str.trim().length() != 4) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isPercentage(String str) {
        /*
         * validacion de si un numero es porcentaje 1-100
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i < 0 || i > 100) {
                return false;
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isDegree(String str) {
        /*
         * Grado quemadura 1,2,3
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i == 1 || i == 2 || i == 3) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isMinute(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > -1 && i < 60) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isHour(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 25) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isNumeric(String str) {
        /*
         * validacion de si un string es entero
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            str.replaceAll(",", "");
            str.replaceAll(".", "");
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
        if (fecha.trim().length() == 0) {
            return true;
        }
        SimpleDateFormat formato = new SimpleDateFormat(format);
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private String isMilitary(String str) {
        /*
         * validacion de si un string es un hora miitar
         */


        //----------------------------------------------
        //determinar si hay caracteres
        if (str.trim().length() == 0) {
            return "no se acepta cadenas vacias";
        }
        //----------------------------------------------
        //quitar " AM A.M.
        str = str.trim().toUpperCase();
        str = str.replace("AM", "");
        str = str.replace("A.M.", "");
        str = str.replace("\"", "");

        //determinar si es un timestamp
        if (str.trim().length() == 12 || str.trim().length() == 8) {
            String[] splitMilitary = str.split(":");
            if (splitMilitary.length == 3) {
                try {
                    int h = Integer.parseInt(splitMilitary[0]);
                    int m = Integer.parseInt(splitMilitary[1]);
                    if (h > 23 || h < 0) {
                        return "La hora debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return "los minutos deben estar entre 0 y 59";
                    }
                    return null;
                } catch (Exception ex) {
                }
            }
        }


        //----------------------------------------------
        //determinar si tiene como separador un :
        boolean length2 = false;

        String[] splitMilitary;

        splitMilitary = str.split(":");
        if (splitMilitary.length == 2) {
            length2 = true;
        } else {
            splitMilitary = str.split(",");
            if (splitMilitary.length == 2) {
                length2 = true;
            } else {
                splitMilitary = str.split(";");
                if (splitMilitary.length == 2) {
                    length2 = true;
                } else {
                    splitMilitary = str.split("\\+");
                    if (splitMilitary.length == 2) {
                        length2 = true;
                    } else {
                        splitMilitary = str.split(".");
                        if (splitMilitary.length == 2) {
                            length2 = true;
                        }
                    }
                }
            }
        }

        if (length2) {
            try {
                int h = Integer.parseInt(splitMilitary[0]);
                int m = Integer.parseInt(splitMilitary[1]);
                if (h == 24) {
                    if (m == 0) {
                        return null;
                    } else {
                        return "Si la hora es 24 los minutos solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return "La hora debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return "los minutos deben estar entre 0 y 59";
                }
                return null;
            } catch (Exception ex) {
            }
        }

        //----------------------------------------------
        //determinar si tiene caracteres diferentes a    0123456789
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1' && str.charAt(i) != '2'
                    && str.charAt(i) != '3' && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7' && str.charAt(i) != '8'
                    && str.charAt(i) != '9') {
                return "Valor no aceptado como hora militar";
            }
        }

        //----------------------------------------------
        //verificar si tiene menos de 4 cifras 
        if (str.trim().length() < 4) {
            //con tres cifras y mayor de 241            
            //y se evalua los dos ultimos digitos < 60
            //3 40 valido
            //9 99 no valido
            return "Una hora militar con menos de 3 cifras es ambigua";
        }

        //----------------------------------------------
        //verificar si puede ser convertida en numero
        try {
            int n = Integer.parseInt(str);
            if (n <= 2400) {
                return null;
            } else {
                return "Una hora militar tiene como valor maximo 2400";
            }
        } catch (Exception ex) {
        }
        //----------------------------------------------
        //si llego a esta linea es que no supero ningun tipo de validacion
        return "Valor no aceptado como hora militar";
    }

    private boolean isAge(String str) {
        /*
         * validacion de si un string es numero entero o edad definida en meses
         * y años
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            String[] splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isCategorical(String str, String category, boolean compareForCode, ArrayList<RelationValue> relationValueList) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        if (str.trim().length() == 0) {
            return true;
        }
        ArrayList<String> categoryList = new ArrayList<String>();
        //se valida con respecto a las relaciones de valores
        for (int i = 0; i < relationValueList.size(); i++) {//le paso a categoriList los valores encontrados en la relacion de valores
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
                    int id_int = Integer.parseInt(errorCorrectionArrayList.get(i).getRowId());

                    conx.update("temp",
                            errorCorrectionArrayList.get(i).getRelationVar().getNameFound() + "='" + errorCorrectionArrayList.get(i).getValue() + "'",
                            "id=" + String.valueOf(id_int));
                    //elimino del historial
                    errorCorrectionArrayList.remove(i);
                    //updateErrorsArrayList();
                    updateCorrectionArrayList();
                    conx.disconnect();
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El cambio se a revertido");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
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
        btnSolveDisabled = true;
        btnSeeRecordDisabled = true;
        //btnDeleteRecordDisabled = true;
        btnDiscardDisabled = true;

        for (int i = 0; i < errorControlArrayList.size(); i++) {
            if (errorControlArrayList.get(i).getErrorDescription().compareTo(currentError) == 0) {
                selectDateFormatDisabled = true;
                description = errorControlArrayList.get(i).getErrorDescription();
                //valores aceptados
                switch (DataTypeEnum.convert(errorControlArrayList.get(i).getRelationVar().getFieldType())) {//tipo de relacion
                    case text:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Cualquier texto"),};
                        break;
                    case integer:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Número entero"),};
                        break;
                    case age:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Número entero"),};
                        break;
                    case military:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Hora militar"),};
                        break;
                    case date:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Fecha segun el formato"),};
                        selectDateFormatDisabled = false;
                        break;
                    case day:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 31"),};
                        break;
                    case month:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 12"),};
                        break;
                    case year:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Entero de 4 cifras"),};
                        break;
                    case minute:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 0 a 59"),};
                        break;
                    case hour:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 0 a 24"),};
                        break;
//                    case degree:
//                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero 1, 2, 3"),};
//                        break;
                    case percentage:
                        aceptedValues = new SelectItem[]{new SelectItem("1", "Numero de 1 a 100"),};
                        break;
                    case error:
                        aceptedValues = new SelectItem[]{new SelectItem("1", " "),};
                        break;
                    case NOVALUE:
                        selectDateFormatDisabled = true;
                        //btnSolveDisabled = false;
                        ArrayList<String> categoricalList;
                        if (errorControlArrayList.get(i).getRelationVar().getTypeComparisonForCode()) {
                            categoricalList = formsAndFieldsDataMB.categoricalCodeList(
                                    errorControlArrayList.get(i).getRelationVar().getNameExpected(), 0);
                        } else {
                            categoricalList = formsAndFieldsDataMB.categoricalNameList(
                                    errorControlArrayList.get(i).getRelationVar().getNameExpected(), 0);
                        }
                        aceptedValues = new SelectItem[categoricalList.size()];
                        for (int j = 0; j < categoricalList.size(); j++) {
                            aceptedValues[j] = new SelectItem(categoricalList.get(j));
                        }
                        break;
                }

                if (errorControlArrayList.get(i).getRelationVar().getFieldType().compareTo("error") != 0) {
                    createDynamicTable();
                    btnSeeRecordDisabled = false;
                    //btnDeleteRecordDisabled = false;
                    btnDiscardDisabled = false;
                    valueFound = errorControlArrayList.get(i).getValue();//valor actual
                    solution = errorControlArrayList.get(i).getErrorSolution();//solucion
                } else {
                    valueFound = "";//valor actual
                    solution = errorControlArrayList.get(i).getErrorSolution();//solucion
                }

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

//    public boolean isBtnDeleteRecordDisabled() {
//        return btnDeleteRecordDisabled;
//    }
//
//    public void setBtnDeleteRecordDisabled(boolean btnDeleteRecordDisabled) {
//        this.btnDeleteRecordDisabled = btnDeleteRecordDisabled;
//    }

    public boolean isBtnDiscardDisabled() {
        return btnDiscardDisabled;
    }

    public void setBtnDiscardDisabled(boolean btnDiscardDisabled) {
        this.btnDiscardDisabled = btnDiscardDisabled;
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

    public DinamicTable getDinamicTable() {
        return dinamicTable;
    }

    public void setDinamicTable(DinamicTable dinamicTable) {
        this.dinamicTable = dinamicTable;
    }
}
