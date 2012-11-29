/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.DataTypeEnum;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import model.dao.FieldsFacade;
import model.pojo.Fields;
import model.pojo.RelationGroup;
import model.pojo.RelationValues;
import model.pojo.RelationVariables;
import model.pojo.RelationsDiscardedValues;

/**
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfVariablesMB")
@SessionScoped
public class RelationshipOfVariablesMB implements Serializable {

    private boolean btnRemoveRelationVarDisabled = true;
    private boolean btnSaveConfigurationDisabled = true;
    private boolean btnLoadConfigurationDisabled = false;
    private boolean btnJoinColumnsDisabled;
    private boolean btnDivideColumnsDisabled;
    private boolean selectDateFormatDisabled = true;
    private boolean compareForCode = false;
    private List<String> variablesExpected;
    private String currentVarFound = "";
    private List<String> varsFound;
    private List<String> valuesExpected;
    private List<String> valuesDiscarded;
    private List<String> valuesFound;
    private String currentRelatedVars = "";//actual relacion de variables
    private List<String> relatedVars;
    //
    private String currentRelationGroupName = "";
    private String currentDateFormat = "dd/MM/yyyy";//tipo de formato de fecha actual
    private String currentVarExpected = "";//variable esperda para relacionar variables
    private String typeVarExepted;
    private String variableDescription = "";
    //private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationGroup currentRelationGroup;
    private UploadFileMB uploadFileMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private LoginMB loginMB;
    //-----------------------------
    private String expectedVariablesFilter = "";
    private String foundVariablesFilter = "";
    private String filterText;
    private String foundText;
    ConnectionJdbcMB connectionJdbcMB;
    private String nameTableTemp = "temp";
    @EJB
    FieldsFacade fieldsFacade;

    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void changeFoundVariablesFilter() {
//        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        loadVarsExpectedAndFound();
//        activeButtons();
    }

    public void changeExpectedVariablesFilter() {
//        currentValueExpected = "";
//        nameOfValueExpected = "";
        loadVarsExpectedAndFound();
//        activeButtons();
    }

    public String getExpectedVariablesFilter() {
        return expectedVariablesFilter;
    }

    public void setExpectedVariablesFilter(String expectedVariablesFilter) {
        this.expectedVariablesFilter = expectedVariablesFilter;
    }

    public String getFoundVariablesFilter() {
        return foundVariablesFilter;
    }

    public void setFoundVariablesFilter(String foundVariablesFilter) {
        this.foundVariablesFilter = foundVariablesFilter;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public RelationshipOfVariablesMB() {
        /*
         * Constructor de la clase
         */
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        nameTableTemp = "temp" + loginMB.getLoginname();
    }

    public void refresh() {
        loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas                       
        changeVarExpected();
        changeVarFound();
    }

    public void reset() {//@PostConstruct ejecutar despues de el constructor
        this.relatedVars = new ArrayList<String>();
        this.valuesExpected = new ArrayList<String>();
        this.valuesFound = new ArrayList<String>();
        this.varsFound = new ArrayList<String>();
        this.variablesExpected = new ArrayList<String>();
        this.currentVarFound = "";
        this.currentVarExpected = "";
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
            categoryList = connectionJdbcMB.categoricalCodeList(typeVarExepted, 20);
        } else {
            categoryList = connectionJdbcMB.categoricalNameList(typeVarExepted, 20);
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

        //filtro los datos
        if (expectedVariablesFilter.trim().length() != 0) {
            filterText = expectedVariablesFilter.toUpperCase();
            for (int j = 0; j < variablesExpected.size(); j++) {
                foundText = variablesExpected.get(j).toUpperCase();
                if (foundText.indexOf(filterText) == -1) {
                    variablesExpected.remove(j);
                    j--;
                }
            }
        }



        //recargo la lista de variables encontradas        
        List<String> varsFound2 = uploadFileMB.getVariablesFound();
        varsFound = new ArrayList<String>();

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

        //filtro los datos
        if (foundVariablesFilter.trim().length() != 0) {
            filterText = foundVariablesFilter.toUpperCase();
            for (int j = 0; j < varsFound.size(); j++) {
                foundText = varsFound.get(j).toUpperCase();
                if (foundText.indexOf(filterText) == -1) {
                    varsFound.remove(j);
                    j--;
                }
            }
        }

        //recargo la lista de variables relacionadas pero para la seccion de relacionar variables        
        if (currentRelationGroup != null) {
            relationshipOfValuesMB.loadCategoricalRelatedVariables(currentRelationGroup);
        }
    }

    public void loadValuesExpected() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        if (currentVarExpected.trim().length() != 0) {
            typeVarExepted = fieldsFacade.findFieldTypeByFieldNameAndFormId(currentVarExpected, currentRelationGroup.getFormId().getFormId()).getFieldType();
            valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
            selectDateFormatDisabled = true;
            switch (DataTypeEnum.convert(typeVarExepted)) {//tipo de relacion
                case integer:
                    valuesExpected.add("Cualquier entero");
                    break;
                case text:
                    valuesExpected.add("Cualquier texto");
                    break;
                case date:
                    valuesExpected.add("Cualquier fecha");
                    selectDateFormatDisabled = false;
                    break;
                case age:
                    valuesExpected.add("Edad representada por un entero o definida en meses y años");
                    break;
                case military:
                    valuesExpected.add("Hora militar");
                    break;
                case minute:
                    valuesExpected.add("Minutos representados por un entero de 1 a 59");
                    break;
                case hour:
                    valuesExpected.add("La hora se representa por un entero de 0 a 24");
                    break;
                case day:
                    valuesExpected.add("El dia se representa por un entero de 1 a 31");
                    break;
                case month:
                    valuesExpected.add("El mes se representa por un entero de 1 a 12");
                    break;
                case year:
                    valuesExpected.add("El año es un valor entero de dos o 4 cifras");
                    break;
                case percentage:
                    valuesExpected.add("El porcentaje es un valor entero de 1 a 100");
                    break;
//                case degree:
//                    valuesExpected.add("El grado mas grave para quemados es un valor entero de 1 a 3 ");
//                    break;
                case NOVALUE://se espera un valor categorico compareForCodeDisabled = false;
                    if (compareForCode == true) {
                        valuesExpected = connectionJdbcMB.categoricalCodeList(typeVarExepted, 20);
                    } else {
                        valuesExpected = connectionJdbcMB.categoricalNameList(typeVarExepted, 20);
                    }
                    break;
            }
        }
    }

    /*
     * crear una lista de valores de una determinada columna proveniente del
     * archivo
     */
    public ArrayList createListOfValuesFromFile(String column) {

        ArrayList<String> array = new ArrayList<String>();
        try {
            //determino el nombre de la columna            
            ResultSet rs = connectionJdbcMB.consult("SELECT " + column + " FROM " + nameTableTemp + "; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_1 > " + ex.toString());
        }
        return array;
    }

    /*
     * crear una lista de valores de una determinada columna proveniente del
     * archivo con valores no repetidos
     */
    public void loadValuesFound(String column, int amount) {

        ArrayList<String> array = new ArrayList<String>();
        int currentAmount = 0;
        try {
            //determino el nombre de la columna            
            ResultSet rs = connectionJdbcMB.consult("SELECT DISTINCT(" + column + ") FROM " + nameTableTemp + "; ");
            while (rs.next()) {
                if (currentAmount < amount) {
                    if (rs.getString(1) != null) {
                        array.add(rs.getString(1));
                        currentAmount++;
                    }
                } else {
                    break;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_2 > " + ex.toString());
        }

        valuesFound = array;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeVarExpected() {
        valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
        if (currentVarExpected != null) {
            if (currentVarExpected.length() != 0) {
                Fields a = fieldsFacade.findFieldTypeByFieldNameAndFormId(currentVarExpected, foundText);
                if (a != null) {
                    variableDescription = a.getFieldDescription();
                } else {
                    variableDescription = "";
                }
                loadValuesExpected();
            }
        }
    }

    public void changeVarFound() {
        valuesFound = new ArrayList<String>();//borro la lista de valores esperados 
        if (currentVarFound != null) {
            if (currentVarFound.trim().length() != 0) {
                currentRelatedVars = "";
                loadValuesFound(currentVarFound, 20);
            }
        }
    }

    public void changeRelatedVars() {
        /*
         * cambia el valor de la lista de variables que esta relacionadas
         * actualmente
         */
        btnRemoveRelationVarDisabled = false;
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
//    private RelationVar findRelationVarByNameFound(String column) {
//        /*
//         * retorna una relacion de variables
//         */
//        return null;
//    }

    /*
     * click sobre asociar dos variables
     */
    public void btnAssociateVarClick() {
        String error = "";
        boolean nextStep = true;
        //--------------------------------------------------------------------------------------------
        //--- se detrmina si hat seleccionada una variable encontrada y una variable esperada
        //--------------------------------------------------------------------------------------------
        if (nextStep) {
            if (currentVarExpected == null) {
                error = "Debe seleccionarse una variable esperada";
                nextStep = false;
            } else {
                if (currentVarExpected.trim().length() == 0) {
                    error = "Debe seleccionarse una variable esperada";
                    nextStep = false;
                }
            }
        }
        if (nextStep) {
            if (currentVarFound == null) {
                error = "Debe seleccionarse una variable encontrada de la lista";
                nextStep = false;
            } else {
                if (currentVarFound.trim().length() == 0) {
                    error = "Debe seleccionarse una variable encontrada de la lista";
                    nextStep = false;
                }
            }
        }

        if (nextStep) {
            selectDateFormatDisabled = true;
            //---------------------------------------------------------------------------
            //como se quita de la lista un item se determina que item quedara seleccionado
            //---------------------------------------------------------------------------
            String nextVarExpectedSelected = "";
            String nextVarFoundSelected = "";
            for (int i = 0; i < varsFound.size(); i++) {
                if (varsFound.get(i).compareTo(currentVarFound) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 < varsFound.size() - 1) {//determino si tiene siguiente
                        nextVarFoundSelected = varsFound.get(i + 1);//asigno el siguiente
                        break;
                    }
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextVarFoundSelected = varsFound.get(i - 1);//asigno el anterior
                        break;
                    }
                }
            }
            for (int i = 0; i < variablesExpected.size(); i++) {
                if (variablesExpected.get(i).compareTo(currentVarExpected) == 0) {//esta es la variable esperada que saldra de la lista
                    if (i + 1 < variablesExpected.size() - 1) {//determino si tiene siguiente
                        nextVarExpectedSelected = variablesExpected.get(i + 1);//asigno el siguiente
                        break;
                    }
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextVarExpectedSelected = variablesExpected.get(i - 1);//asigno el anterior
                        break;
                    }
                }
            }
            //---------------------------------------------------------------------------
            //relaizo la relacion de variables
            //---------------------------------------------------------------------------
            if (currentRelationGroup.getRelationVariablesList() == null) {
                currentRelationGroup.setRelationVariablesList(new ArrayList<RelationVariables>());
            }
            RelationVariables newRelationVariables = new RelationVariables();
            newRelationVariables.setIdRelationVariables(currentRelationGroup.getRelationVariablesList().size());
            newRelationVariables.setNameExpected(currentVarExpected);
            newRelationVariables.setNameFound(currentVarFound);
            newRelationVariables.setFieldType(typeVarExepted);
            newRelationVariables.setComparisonForCode(compareForCode);
            newRelationVariables.setDateFormat(currentDateFormat);
            newRelationVariables.setRelationValuesList(new ArrayList<RelationValues>());
            newRelationVariables.setRelationsDiscardedValuesList(new ArrayList<RelationsDiscardedValues>());


            currentRelationGroup.getRelationVariablesList().add(newRelationVariables);
            //currentRelationGroup.addRelationVar(relVar);//agrego la relacion a el grupo de relaciones actual 
            relatedVars.add(currentVarExpected + "->" + currentVarFound);//agrego la relacion a la lista de relaciones de variables 
            loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas   

            //---------------------------------------------------------------------------
            //selecciono los items de la lista que quedan seleccionados
            //---------------------------------------------------------------------------
            currentVarExpected = nextVarExpectedSelected;
            currentVarFound = nextVarFoundSelected;
            changeVarExpected();
            changeVarFound();


        }
        if (nextStep) {//no se produjeron errores solo alertas
            if (error.length() == 0) {//no existieron errores            
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Asociación de variables realizada."));
            } else {//hay  errores al relacionar la variables 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
            }
        } else {//se produjo un error
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }
    /*
     * cargar las listas de variables encontradas y esperadas
     */

    public void loadRelatedVars() {
        relatedVars = new ArrayList<String>();
        List<RelationVariables> relationVarList = currentRelationGroup.getRelationVariablesList();
        if (relationVarList != null) {
            for (int i = 0; i < relationVarList.size(); i++) {
                relatedVars.add(relationVarList.get(i).getNameExpected() + "->" + relationVarList.get(i).getNameFound());
            }
        }
    }

    public void btnRemoveRelationVarClick() {
        /*
         * click sobre boton remover relacion de variables
         */
        boolean continueProcess = true;

        if (currentRelatedVars == null) {
            continueProcess = false;
        } else {
            if (currentRelatedVars.trim().length() == 0) {
                continueProcess = false;
            }
        }

        if (continueProcess) {
            //como se elimina un item de la lista busco cual es el siguinte item seleccionado
            String nextRelatedVarsSelected = "";
            for (int i = 0; i < relatedVars.size(); i++) {
                if (relatedVars.get(i).compareTo(currentRelatedVars) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= relatedVars.size() - 1) {//determino si tiene siguiente
                        nextRelatedVarsSelected = relatedVars.get(i + 1);//asigno el siguiente
                        break;
                    }
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextRelatedVarsSelected = relatedVars.get(i - 1);//asigno el anterior
                        break;
                    }
                }
            }
            //elimino el item de la lista de variables relacionadas
            if (currentRelatedVars.trim().length() != 0) {
                //String[] splitVarRelated = currentRelatedVars.split("->");
                List<RelationVariables> relationVarList = currentRelationGroup.getRelationVariablesList();
                if (relationVarList != null) {
                    String a;
                    for (int i = 0; i < relationVarList.size(); i++) {
                        a = relationVarList.get(i).getNameExpected() + "->" + relationVarList.get(i).getNameFound();
                        if (currentRelatedVars.compareTo(a) == 0) {
                            currentRelationGroup.getRelationVariablesList().remove(i);
                            break;
                        }
                    }
                }
                //currentRelationGroup.removeRelationVar(splitVarRelated[0], splitVarRelated[1]);//elimino la relacion de el grupo de relaciones actual
                for (int i = 0; i < relatedVars.size(); i++) {//remuevo de la lista de relaciones de variables        
                    if (relatedVars.get(i).compareTo(currentRelatedVars) == 0) {
                        relatedVars.remove(i);
                        break;
                    }
                }

                loadVarsExpectedAndFound();//recargo lista de variables esperadas y encontradas
                valuesExpected = new ArrayList<String>();
                valuesFound = new ArrayList<String>();
                currentRelatedVars = nextRelatedVarsSelected;//asigno el item que queda seleccionado
                if (currentRelatedVars.trim().length() == 0) {
                    btnRemoveRelationVarDisabled = true;
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "La relación ha sido eliminada."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", "Seleccione la relación a eliminar."));
        }
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

//    public boolean isBtnAssociateRelationVarDisabled() {
//        return btnAssociateRelationVarDisabled;
//    }
//
//    public void setBtnAssociateRelationVarDisabled(boolean btnAssociateRelationVarDisabled) {
//        this.btnAssociateRelationVarDisabled = btnAssociateRelationVarDisabled;
//    }
    public boolean isBtnRemoveRelationVarDisabled() {
        return btnRemoveRelationVarDisabled;
    }

    public void setBtnRemoveRelationVarDisabled(boolean btnRemoveRelationVarDisabled) {
        this.btnRemoveRelationVarDisabled = btnRemoveRelationVarDisabled;
    }

//    public boolean isBtnValidateRelationVarDisabled() {
//        return btnValidateRelationVarDisabled;
//    }
//
//    public void setBtnValidateRelationVarDisabled(boolean btnValidateRelationVarDisabled) {
//        this.btnValidateRelationVarDisabled = btnValidateRelationVarDisabled;
//    }
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

//    public boolean isCompareForCodeDisabled() {
//        return compareForCodeDisabled;
//    }
//
//    public void setCompareForCodeDisabled(boolean compareForCodeDisabled) {
//        this.compareForCodeDisabled = compareForCodeDisabled;
//    }
    public String getCurrentRelationGroupName() {
        return currentRelationGroupName;
    }

    public void setCurrentRelationGroupName(String currentRelationGroupName) {
        this.currentRelationGroupName = currentRelationGroupName;
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

    public void setCurrentRelationsGroup(RelationGroup currentRelationsGroup) {
        this.currentRelationGroup = currentRelationsGroup;
    }

    public RelationGroup getCurrentRelationsGroup() {
        return currentRelationGroup;
    }

//    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
//        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
//    }
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
