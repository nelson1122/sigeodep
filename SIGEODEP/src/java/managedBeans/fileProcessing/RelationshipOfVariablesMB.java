/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

//import SessionBeans.AreaFacade;
//import entities.Area;
import beans.connection.ConnectionJDBC;
import beans.lists.Field;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.preload.FormsAndFieldsDataMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfVariablesMB")
@SessionScoped
public class RelationshipOfVariablesMB {

    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private boolean btnValidateRelationVarDisabled = true;
    private boolean btnAssociateRelationVarDisabled = true;
    private boolean btnRemoveRelationVarDisabled = true;
    private boolean btnSaveConfigurationDisabled = true;
    private boolean btnLoadConfigurationDisabled = false;
    private boolean btnJoinColumnsDisabled;
    private boolean btnDivideColumnsDisabled;
    private boolean selectDateFormatDisabled = true;
    private boolean compareForCode = false;
    private boolean compareForCodeDisabled = true;
    private List<String> variablesExpected;
    private String currentVarFound = "";
    private List<String> varsFound;
    private List<String> valuesExpected;
    private List<String> valuesDiscarded;
    private List<String> valuesFound;
    private String currentRelatedVars = "";//actual relacion de variables
    private List<String> relatedVars;
    private List<String> relationGroups;
    private String currentRelationGroupName = "";
    private String currentDateFormat = "yyyy/mm/dd";//tipo de formato de fecha actual
    private String currentVarExpected = "";//variable esperda para relacionar variables
    private Field typeVarExepted;
    private String variableDescription = "";
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationsGroup currentRelationsGroup;
    private UploadFileMB uploadFileMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public RelationshipOfVariablesMB() {
        /*
         * Constructor de la clase
         */
    }

    public void refresh(){
        
    }
    
    public void reset() {//@PostConstruct ejecutar despues de el constructor
        this.relatedVars = new ArrayList<String>();
        this.valuesExpected = new ArrayList<String>();
        this.varsFound = new ArrayList<String>();
        this.valuesFound = new ArrayList<String>();
        this.currentVarFound = "";
        this.btnValidateRelationVarDisabled = true;
        this.btnAssociateRelationVarDisabled = true;
        this.btnRemoveRelationVarDisabled = true;
    }

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

    private boolean isCategorical(String str, String category) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        ArrayList<String> categoryList;
        if (compareForCode == true) {
            categoryList = formsAndFieldsDataMB.categoricalCodeList(currentVarExpected, 20);
        } else {
            categoryList = formsAndFieldsDataMB.categoricalNameList(currentVarExpected, 20);
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
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void loadVarsExpectedAndFound() {
        /*
         * cargar las listas de variables encontradas y esperadas
         */
        String[] splitVarRelated;
        List<String> varsExpected = uploadFileMB.getVariablesExpected();//variables esperadas
        for (int i = 0; i < relatedVars.size(); i++) {//quito las variables esperadas que ya esten relacionadas        
            splitVarRelated = relatedVars.get(i).split("->");
            for (int j = 0; j < varsExpected.size(); j++) {
                if (varsExpected.get(j).compareTo(splitVarRelated[0]) == 0) {
                    varsExpected.remove(j);
                    break;
                }
            }
        }
        variablesExpected = varsExpected;

        //recargo la lista de variables encontradas
        varsFound = new ArrayList<String>();
        List<String> varsFound2 = uploadFileMB.getVariablesFound();
        //quito las variables esperadas que ya esten relacionadas
        for (int i = 0; i < relatedVars.size(); i++) {
            splitVarRelated = relatedVars.get(i).split("->");
            for (int j = 0; j < varsFound2.size(); j++) {
                if (varsFound2.get(j).compareTo(splitVarRelated[1]) == 0) {
                    varsFound2.remove(j);
                    break;
                }
            }
        }
        varsFound = varsFound2;
        //recargo la lista de variables relacionadas pero para la seccion de relacionar variables        
        relationshipOfValuesMB.loadCategoricalRelatedVariables(currentRelationsGroup);
    }

    private void loadValuesExpected() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        if (currentVarExpected.length() != 0) {
            typeVarExepted = formsAndFieldsDataMB.searchField(currentVarExpected);
            //fieldsFacade.findByFormField(currentForm, currentVarExpected).getFieldType();            
            valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
            if (typeVarExepted.getFieldType().compareTo("integer") == 0) {//se espera un numero
                valuesExpected.add("Cualquier entero");
            } else if (typeVarExepted.getFieldType().compareTo("date") == 0) { //se espera una fecha
                selectDateFormatDisabled = false;
                valuesExpected.add("Cualquier Fecha ");
            } else if (typeVarExepted.getFieldType().compareTo("text") == 0) {//se espera texto 
                valuesExpected.add("Cualquier texto");
            } else {//se espera un valor categorico compareForCodeDisabled = false;
                if (compareForCode == true) {
                    valuesExpected = formsAndFieldsDataMB.categoricalCodeList(currentVarExpected, 20);
                } else {
                    valuesExpected = formsAndFieldsDataMB.categoricalNameList(currentVarExpected, 20);
                }
            }
        }
    }

    private void loadValuesRelatedList() {
    }

    public ArrayList createListOfValuesFromFile(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo
         */
        ArrayList<String> array = new ArrayList<String>();
        try {
            //determino el nombre de la columna
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult("SELECT " + column + " FROM temp; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
            conx.disconnect();
        } catch (SQLException ex) {
        }
        return array;
    }

    public void loadValuesFound(String column, int amount) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo con valores no repetidos
         */
        ArrayList<String> array = new ArrayList<String>();
        int currentAmount = 0;
        //saco todos los valores distintos de la tabla temp
        //correspondientes a la variable encontrada y los hubico en DistinctVarsExpectedArrayList     
        try {
            //determino el nombre de la columna
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult("SELECT DISTINCT(" + column + ") FROM temp; ");
            while (rs.next()) {
                if (currentAmount < amount) {
                    array.add(rs.getString(1));
                    currentAmount++;
                } else {
                    break;
                }
            }
            conx.disconnect();
        } catch (SQLException ex) {
        }
        valuesFound = array;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeVarExpected() {
        btnValidateRelationVarDisabled = true;
        selectDateFormatDisabled = true;
        compareForCodeDisabled = true;
        btnAssociateRelationVarDisabled = true;
        currentRelatedVars = "";

        if (currentVarExpected != null) {
            if (currentVarExpected.length() != 0) {
                variableDescription = formsAndFieldsDataMB.variableDescription(currentVarExpected);
            }
        }

        if (currentVarFound != null && currentVarExpected != null) {
            if (currentVarExpected.length() != 0 && currentVarFound.length() != 0) {
                compareForCodeDisabled = false;
                btnValidateRelationVarDisabled = false;
                btnAssociateRelationVarDisabled = true;
            }
        }
        loadValuesExpected();
    }

    public void changeVarFound() {
        btnValidateRelationVarDisabled = true;
        btnAssociateRelationVarDisabled = true;
        btnAssociateRelationVarDisabled = true;
        compareForCodeDisabled = true;
        currentRelatedVars = "";
        if (currentVarFound != null && currentVarExpected != null) {
            if (currentVarExpected.length() != 0
                    && currentVarFound.length() != 0) {
                compareForCodeDisabled = false;
                btnValidateRelationVarDisabled = false;
                btnAssociateRelationVarDisabled = true;
            }
        }
        loadValuesFound(currentVarFound, 20);
    }

    public void changeRelatedVars() {
        /*
         * cambia el valor de la lista de variables que esta relacionadas
         * actualmente
         */
        btnRemoveRelationVarDisabled = false;
    }

    public void compareForCodeChange() {
        /*
         * cambia el check box que me dice si coparo por valor o por codigo
         */
        loadValuesExpected();
    }

    public void btnDivideColumnsClick() {
        
    }

    public void btnJoinColumnsClick() {
        
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void btnValidateRelationVarClick() {
        /*
         * click sobre validar si dos variables puedes ser sociadas
         */
        String error = "";
        typeVarExepted = formsAndFieldsDataMB.searchField(currentVarExpected);// fieldsFacade.findByFormField(currentForm, currentVarExpected).getFieldType();
        ArrayList<String> array = createListOfValuesFromFile(currentVarFound);
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).length() != 0) {//validacion de que llegue el valor 
                if (typeVarExepted.getFieldType().compareTo("integer") == 0) {//se espera un numero 
                    if (!isNumeric(array.get(i)))//si el dato no es numerico adiciono el error 
                    {
                        error = "Existen datos dentro de los valores encontrados en el archivo que no son numéricos, y deberán ser corregidos en el proceso de validación si decide asociarlos.";
                    }
                } else if (typeVarExepted.getFieldType().compareTo("date") == 0) { //se espera un valor boleano 
                    if (!isDate(array.get(i), currentDateFormat))//si el dato no es fecha validala adiciono el error 
                    {
                        error = "Existen datos dentro de los valores encontrados en el archivo que no corresponden al formato especificado, y deberán ser corregidos en el proceso de validación si decide asociarlos.";
                    }
                } else if (typeVarExepted.getFieldType().compareTo("text") == 0) { //se espera un texto
                    error = "";//no es necesario validar
                } else {//se espera un valor categorico 
                    if (!isCategorical(array.get(i), typeVarExepted.getFieldType()))//se espera un valorcategorico
                    {//si el dato no pertenece a la categoria adiciono el error 
                        error = "Existen datos dentro de los valores encontrados en el archivo que no corresponden a la categoría especificada, y deberán ser corregidos en el proceso de validación si decide asociarlos.";
                    }
                }
            }
        }
        if (error.length() == 0) {//no existieron errores            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Las dos variables pueden ser asociadas sin problemas."));
        } else {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
        selectDateFormatDisabled = true;
        btnValidateRelationVarDisabled = true;
        btnAssociateRelationVarDisabled = false;
        compareForCodeDisabled = true;
    }

    private RelationVar findRelationVar(String column) {
        /*
         * retorna una relacion de variables
         */
        return null;
    }

    public void btnAssociateVarClick() {
        /*
         * click sobre asociar dos variables
         */
        RelationVar relVar = new RelationVar(currentVarExpected, currentVarFound, typeVarExepted.getFieldType(), compareForCode, currentDateFormat);
        currentRelationsGroup.addRelationVar(relVar);//agrego la relacion a el grupo de relaciones actual 
        relatedVars.add(currentVarExpected + "->" + currentVarFound);//agrego la relacion a la lista de relaciones de variables 
        loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas           
        currentVarExpected = "";
        currentVarFound = "";
        variableDescription = "";
        valuesExpected = new ArrayList<String>();
        valuesFound = new ArrayList<String>();
        btnValidateRelationVarDisabled = true;
        btnAssociateRelationVarDisabled = true;
        selectDateFormatDisabled = true;
        compareForCodeDisabled = false;
        btnSaveConfigurationDisabled = false;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Las variables fueron asociadas correctamente."));
    }

    /*
     * cargar las listas de variables encontradas y esperadas
     */
    public void loadRelatedVars() {
        relatedVars = new ArrayList<String>();
        List<RelationVar> relationVarList = currentRelationsGroup.getRelationVarList();
        for (int i = 0; i < relationVarList.size(); i++) {
            relatedVars.add(relationVarList.get(i).getNameExpected() + "->" + relationVarList.get(i).getNameFound());
        }
    }

    public void btnRemoveRelationVarClick() {
        /*
         * click sobre boton remover relacion de variables
         */
        String[] splitVarRelated = currentRelatedVars.split("->");
        currentRelationsGroup.removeRelationVar(splitVarRelated[0], splitVarRelated[1]);//elimino la relacion de el grupo de relaciones actual
        for (int i = 0; i < relatedVars.size(); i++) {//remuevo de la lista de relaciones de variables        
            if (relatedVars.get(i).compareTo(currentRelatedVars) == 0) {
                relatedVars.remove(i);
                break;
            }
        }
        loadVarsExpectedAndFound();//recargo lista de variables esperadas y encontradas
        valuesExpected = new ArrayList<String>();
        valuesFound = new ArrayList<String>();
        compareForCodeDisabled = false;
        currentVarFound = "";
        currentVarExpected = "";
        variableDescription = "";
        btnValidateRelationVarDisabled = true;
        btnAssociateRelationVarDisabled = true;
        btnRemoveRelationVarDisabled = true;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "La relación ha sido eliminada."));

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public String getCurrentVarExpected() {
        return currentVarExpected;
    }

    public void setCurrentVarExpected(String currentVarExpected) {
        this.currentVarExpected = currentVarExpected;
    }

    public String getCurrentVarFound() {
        return currentVarFound;
    }

    public void setCurrentVarFound(String currentVarFound) {
        this.currentVarFound = currentVarFound;
    }

    public List<String> getValuesExpected() {
        return valuesExpected;
    }

    public void setValuesExpected(List<String> valuesExpected) {
        this.valuesExpected = valuesExpected;
    }

    public List<String> getValuesFound() {
        return valuesFound;
    }

    public void setValuesFound(List<String> valuesFound) {
        this.valuesFound = valuesFound;
    }

    public List<String> getVarsFound() {
        return varsFound;
    }

    public void setVarsFound(List<String> varsFound) {
        this.varsFound = varsFound;
    }

    public boolean isBtnAssociateRelationVarDisabled() {
        return btnAssociateRelationVarDisabled;
    }

    public void setBtnAssociateRelationVarDisabled(boolean btnAssociateRelationVarDisabled) {
        this.btnAssociateRelationVarDisabled = btnAssociateRelationVarDisabled;
    }

    public boolean isBtnRemoveRelationVarDisabled() {
        return btnRemoveRelationVarDisabled;
    }

    public void setBtnRemoveRelationVarDisabled(boolean btnRemoveRelationVarDisabled) {
        this.btnRemoveRelationVarDisabled = btnRemoveRelationVarDisabled;
    }

    public boolean isBtnValidateRelationVarDisabled() {
        return btnValidateRelationVarDisabled;
    }

    public void setBtnValidateRelationVarDisabled(boolean btnValidateRelationVarDisabled) {
        this.btnValidateRelationVarDisabled = btnValidateRelationVarDisabled;
    }

    public List<String> getVariablesExpected() {
        return variablesExpected;
    }

    public void setVariablesExpected(List<String> variablesExpected) {
        this.variablesExpected = variablesExpected;
    }

    public String getCurrentRelatedVars() {
        return currentRelatedVars;
    }

    public void setCurrentRelatedVars(String currentRelatedtVars) {
        this.currentRelatedVars = currentRelatedtVars;
    }

    public List<String> getRelatedVars() {
        return relatedVars;
    }

    public void setRelatedVars(List<String> relatedVars) {
        this.relatedVars = relatedVars;
    }

    public boolean isCompareForCode() {
        return compareForCode;
    }

    public void setCompareForCode(boolean compareForCode) {
        this.compareForCode = compareForCode;
    }

    public boolean isCompareForCodeDisabled() {
        return compareForCodeDisabled;
    }

    public void setCompareForCodeDisabled(boolean compareForCodeDisabled) {
        this.compareForCodeDisabled = compareForCodeDisabled;
    }

    public String getCurrentRelationGroupName() {
        return currentRelationGroupName;
    }

    public void setCurrentRelationGroupName(String currentRelationGroupName) {
        this.currentRelationGroupName = currentRelationGroupName;
    }

    public List<String> getRelationGroups() {
        return relationGroups;
    }

    public void setRelationGroups(List<String> relationGroups) {
        this.relationGroups = relationGroups;
    }

    public boolean isBtnLoadConfigurationDisabled() {
        return btnLoadConfigurationDisabled;
    }

    public void setBtnLoadConfigurationDisabled(boolean btnLoadConfigurationDisabled) {
        this.btnLoadConfigurationDisabled = btnLoadConfigurationDisabled;
    }

    public boolean isBtnSaveConfigurationDisabled() {
        return btnSaveConfigurationDisabled;
    }

    public void setBtnSaveConfigurationDisabled(boolean btnSaveConfigurationDisabled) {
        this.btnSaveConfigurationDisabled = btnSaveConfigurationDisabled;
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

    public String getVariableDescription() {
        return variableDescription;
    }

    public void setVariableDescription(String variableDescription) {
        this.variableDescription = variableDescription;
    }

    public void setCurrentRelationsGroup(RelationsGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public RelationsGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

    public void setUploadFileMB(UploadFileMB uploadFileMB) {
        this.uploadFileMB = uploadFileMB;
    }

    public RelationshipOfValuesMB getRelationshipOfValuesMB() {
        return relationshipOfValuesMB;
    }

    public void setRelationshipOfValuesMB(RelationshipOfValuesMB relationshipOfValuesMB) {
        this.relationshipOfValuesMB = relationshipOfValuesMB;
    }

    public boolean isBtnDivideColumnsDisabled() {
        return btnDivideColumnsDisabled;
    }

    public void setBtnDivideColumnsDisabled(boolean btnDivideColumnsDisabled) {
        this.btnDivideColumnsDisabled = btnDivideColumnsDisabled;
    }

    public boolean isBtnJoinColumnsDisabled() {
        return btnJoinColumnsDisabled;
    }

    public void setBtnJoinColumnsDisabled(boolean btnJoinColumnsDisabled) {
        this.btnJoinColumnsDisabled = btnJoinColumnsDisabled;
    }

    public List<String> getValuesDiscarded() {
        return valuesDiscarded;
    }

    public void setValuesDiscarded(List<String> valuesDiscarded) {
        this.valuesDiscarded = valuesDiscarded;
    }
    
    
}
