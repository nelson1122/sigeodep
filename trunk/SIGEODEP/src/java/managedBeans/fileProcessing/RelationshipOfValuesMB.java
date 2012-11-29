/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.DataTypeEnum;
import beans.util.DamerauLevenshtein;
import beans.util.JaroWinkler;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import model.dao.RelationGroupFacade;
import model.dao.RelationVariablesFacade;
import model.pojo.RelationGroup;
import model.pojo.RelationValues;
import model.pojo.RelationVariables;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfValuesMB")
@SessionScoped
public class RelationshipOfValuesMB implements Serializable {

    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    //private ConnectionJDBC conx = null;//conexion sin persistencia a postgres     
    private List<String> categoricalRelatedVariables;
    private boolean newValueDisabled = true;
    private boolean btnAssociateRelationValueDisabled = true;
    private boolean btnAutomaticRelationValueDisabled = true;
    private boolean btnRemoveRelationValueDisabled = true;
    private boolean btnRemoveDiscardedValuesDisabled = true;
    private boolean btnCopyFromDisabled = true;//ABRE EL DIALOGO PARA COPIAR DE OTRAS RELACIONES
    private boolean btnCopyFrom2Disabled = true;// EJECUTA LA COPIA DE RELACIONES
    private boolean btnDiscardValueDisabled = true;
    private boolean btnViewValueDisabled = true;
    private List<String> valuesDiscarded;
    private List<String> valuesExpected;
    private List<String> valuesFoundSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesRelatedSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesDiscardedSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesFound;
    private List<String> valuesRelated;
    private DamerauLevenshtein damerauLevenshtein = new DamerauLevenshtein();
    private JaroWinkler jaroWinkler = new JaroWinkler();
    //private int Similarity;
    private String[] splitFilterText;
    private String[] splitFoundText;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    //private String typeVarExepted;
    //private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationGroup currentRelationsGroup;
    private LoginMB loginMB;
    private String currentVariableExpected = "";//variable Esperada
    private String currentVariableFound = "";//valor Encontrado
    private String currentValueExpected = null;//valor esperado    
    private String currentCategoricalRelatedVariables = "";//valor esperado
    private String coincidentNewValue = "";
    private String expectedValuesFilter = "";
    private String foundValuesFilter = "";
    private String filterText;
    private String foundText;
    private String nameOfValueExpected = "";
    private DinamicTable dinamicTable = new DinamicTable();
    ConnectionJdbcMB connectionJdbcMB;
    private ArrayList<String> selectedRowDataTable = new ArrayList<String>();
    private String nameTableTemp = "temp";
    private List<String> relationGroups;
    private String currentRelationGroup;
    private List<String> relationsVariables;
    private String currentRelationVariables;


    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void changeFoundValuesFilter() {
        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        loadFoundValues();
        loadExpectedValues();
        activeButtons();
    }

    public void changeExpectedValuesFilter() {
        currentValueExpected = null;
        nameOfValueExpected = "";
        loadFoundValues();
        loadExpectedValues();
        activeButtons();
    }

    public void loadCoincidentsRgisters() {

        if (selectedRowDataTable != null) {
            newValueDisabled = false;
            coincidentNewValue = valuesFoundSelectedInRelationValues.get(0);
        }
    }

    public void changeCoincidentValue() {




        String sqlUpdate = currentVariableFound + "='" + coincidentNewValue + "'";
        String sqlId = "id=" + selectedRowDataTable.get(0);
        connectionJdbcMB.update(nameTableTemp, sqlUpdate, sqlId);
        //actualizar tabla
        for (int i = 0; i < dinamicTable.getListOfRecords().size(); i++) {
            if (dinamicTable.getListOfRecords().get(i).get(0).compareTo(selectedRowDataTable.get(0)) == 0) {
                //inicialmente actualizo el campo indicado en la fila seleccionada
                for (int j = 0; j < dinamicTable.getTitles().size(); j++) {
                    if (dinamicTable.getTitles().get(j).compareTo(currentVariableFound) == 0) {
                        dinamicTable.getListOfRecords().get(i).set(j, coincidentNewValue);
                        break;//quiebro ciclo de busqueda en titulos
                    }
                }
                break;//quiebro ciclo de busqueda en filas
            }
        }
        selectedRowDataTable = null;
        coincidentNewValue = "";
        newValueDisabled = true;
    }

    public String getExpectedValuesFilter() {
        return expectedValuesFilter;
    }

    public void setExpectedValuesFilter(String expectedValuesFilter) {
        this.expectedValuesFilter = expectedValuesFilter;
    }

    public String getFoundValuesFilter() {
        return foundValuesFilter;
    }

    public void setFoundValuesFilter(String foundValuesFilter) {
        this.foundValuesFilter = foundValuesFilter;
    }

    public void changeCategoricalRelatedVariables() {
        //asignar el valor de las variables (para poder remove)
        expectedValuesFilter = "";
        foundValuesFilter = "";
        nameOfValueExpected = "";
        String[] splitValuesRelated;
        currentValueExpected = null;
        valuesFound = new ArrayList<String>();
        valuesExpected = new ArrayList<String>();
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();

        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled = true;
        btnCopyFromDisabled = false;

        if (currentCategoricalRelatedVariables.trim().length() != 0) {
            splitValuesRelated = currentCategoricalRelatedVariables.split("->");
            currentVariableExpected = splitValuesRelated[0];
            currentVariableFound = splitValuesRelated[1];
            //currentValueFound = "";
            valuesFoundSelectedInRelationValues = new ArrayList<String>();
            valuesRelatedSelectedInRelationValues = new ArrayList<String>();
            valuesDiscardedSelectedInRelationValues = new ArrayList<String>();
            loadFoundValues();
            loadExpectedValues();
            loadRelatedAndDiscardedValues();
        }
        activeButtons();
    }

    public void loadCategoricalRelatedVariables(RelationGroup relationsGroup) {

        String type;
        ArrayList<String> variablesRelated = new ArrayList<String>();


        if (relationsGroup != null) {
            currentRelationsGroup = relationsGroup;
        }

        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnCopyFromDisabled = true;
        //System.out.println("Aqui desabilito");
        valuesFound = new ArrayList<String>();
        valuesFoundSelectedInRelationValues = null;
        foundValuesFilter = "";

        valuesExpected = new ArrayList<String>();
        expectedValuesFilter = "";
        currentValueExpected = null;//valor esperado        
        nameOfValueExpected = "";

        valuesRelated = new ArrayList<String>();
        valuesRelatedSelectedInRelationValues = null;

        valuesDiscarded = new ArrayList<String>();
        valuesDiscardedSelectedInRelationValues = null;

        currentCategoricalRelatedVariables = "";


        if (currentRelationsGroup.getRelationVariablesList() != null) {
            for (int i = 0; i < currentRelationsGroup.getRelationVariablesList().size(); i++) {
                type = currentRelationsGroup.getRelationVariablesList().get(i).getFieldType();
                switch (DataTypeEnum.convert(type)) {
                    case NOVALUE://idica que NO es: integer,date,minute,hour,day,month,year,age,military,degree,percentage
                        type = currentRelationsGroup.getRelationVariablesList().get(i).getNameExpected();
                        type = type + "->";
                        type = type + currentRelationsGroup.getRelationVariablesList().get(i).getNameFound();
                        variablesRelated.add(type);
                        break;
                }
            }
        }
        setCategoricalRelatedVariables(variablesRelated);


    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public RelationshipOfValuesMB() {
        /*
         * Constructor de la clase
         */
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        nameTableTemp = "temp" + loginMB.getLoginname();
    }

    public void reset() {//@PostConstruct ejecutar despues de el constructor        

        currentVariableExpected = "";
        currentVariableFound = "";
        nameOfValueExpected = "";
        expectedValuesFilter = "";
        foundValuesFilter = "";

        categoricalRelatedVariables = new ArrayList<String>();
        currentCategoricalRelatedVariables = null;

        valuesExpected = new ArrayList<String>();
        currentValueExpected = null;

        valuesRelated = new ArrayList<String>();
        valuesRelatedSelectedInRelationValues = new ArrayList<String>();

        valuesFound = new ArrayList<String>();
        valuesFoundSelectedInRelationValues = new ArrayList<String>();

        valuesDiscarded = new ArrayList<String>();
        valuesDiscardedSelectedInRelationValues = new ArrayList<String>();

        btnAssociateRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnViewValueDisabled = true;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private void loadExpectedValues() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        currentValueExpected = null;
        if (currentVariableExpected.length() != 0) {
            //typeVarExepted = formsAndFieldsDataMB.searchField(currentVariableExpected);
            //fieldsFacade.findByFormField(currentForm, currentVariableExpected).getFieldType();            
            valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
            RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
            if (currentRelationVar != null) {
                if (currentRelationVar.getComparisonForCode()) {
                    valuesExpected = connectionJdbcMB.categoricalCodeList(currentRelationVar.getFieldType(), 0);
                } else {
                    valuesExpected = connectionJdbcMB.categoricalNameList(currentRelationVar.getFieldType(), 0);
                }
            }
            //filtro los datos
            if (expectedValuesFilter.trim().length() != 0) {
                filterText = expectedValuesFilter.toUpperCase();
                for (int j = 0; j < valuesExpected.size(); j++) {
                    foundText = valuesExpected.get(j).toUpperCase();
                    if (foundText.indexOf(filterText) == -1) {
                        valuesExpected.remove(j);
                        j--;
                    }
                }
            }
        }
    }

    private void loadRelatedAndDiscardedValues() {
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();
        //busco cual es la relacion de variables actual        
        String relation;
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        //RelationVar currentRelationVar = currentRelationsGroup.findRelationVarByNameFound(currentVariableExpected, currentVariableFound);
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            //teniendo la relacion de variables actual determino la lista de relaciones de variables para esta
            if (currentRelationVar.getRelationValuesList() != null) {
                for (int i = 0; i < currentRelationVar.getRelationValuesList().size(); i++) {
                    relation = currentRelationVar.getRelationValuesList().get(i).getNameExpected();
                    relation = relation + "->";
                    relation = relation + currentRelationVar.getRelationValuesList().get(i).getNameFound();
                    valuesRelated.add(relation);
                }
            }
            //recargo la lista de valores descartados
            if (currentRelationVar.getRelationsDiscardedValuesList() != null) {
                for (int i = 0; i < currentRelationVar.getRelationsDiscardedValuesList().size(); i++) {
                    valuesDiscarded.add(currentRelationVar.getRelationsDiscardedValuesList().get(i).getDiscardedValueName());
                }
            }
        }
    }

    /*
     * crear una lista de valores de una determinada columna proveniente del
     * archivo con valores no repetidos
     */
    public ArrayList createListOfDistinctValuesFromFile(String column) {
        //saco todos los valores distintos de la tabla temp
        //correspondientes a la variable encontrada y los hubico en DistinctVarsExpectedArrayList        
        ArrayList<String> array = new ArrayList<String>();
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT DISTINCT(" + column + ") FROM " + nameTableTemp + " WHERE " + column + " NOT LIKE ''; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovMB_1 > " + ex.toString());
        }
        return array;
    }

    public ArrayList createListOfValuesFromFile(String column) {
        /*
         * crear una lista de valores de una determinada columna proveniente del
         * archivo
         */
        ArrayList<String> array = new ArrayList<String>();
        try {
            //determino el nombre de la columna
            ResultSet rs = connectionJdbcMB.consult("SELECT " + column + " FROM " + nameTableTemp + "; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovMB_2 > " + ex.toString());
        }
        return array;
    }

    public void changeIn() {
        FacesMessage msg = new FacesMessage("Car Edited", "EDITO!!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        System.out.println("aQUI ENTRO");
    }
    /*
     * edicion de registro que se muestra desde la relacion de valores
     */

    public void onEdit(RowEditEvent event) {
//        ArrayList<ValueCell> recibido=(ArrayList<ValueCell>) event.getObject();
//        String salio="";
//        for (int i = 0; i < 5; i++) {
//            salio=salio+"     (["+recibido.get(i).getOldValue()+"-"+recibido.get(i).getNewValue()+"])    ";
//        }

        FacesMessage msg = new FacesMessage("Car Edited", ((ArrayList<String>) event.getObject()).toString());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        //FacesMessage msg = new FacesMessage("Car Cancelled", ((ArrayList<ArrayList<String>>) event.getObject()).toString());    
        //FacesContext.getCurrentInstance().addMessage(null, msg);  
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeValuesDiscarded() {
        activeButtons();
    }

    public void changeValuesRelated() {
        activeButtons();
    }

    public void changeValuesExpected() {
        nameOfValueExpected = "";
        //busco el nombre o codigo del valor esperado
        if (currentValueExpected != null) {
            if (currentValueExpected.trim().length() != 0) {
                //---------------------------------------
                String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
                currentVariableExpected = splitVarRelated[0];
                currentVariableFound = splitVarRelated[1];
                RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
                if (currentRelationVar != null) {
                    if (currentVariableExpected.length() != 0) {
                        //typeVarExepted = formsAndFieldsDataMB.searchField(currentVariableExpected);
                        ArrayList<String> valuesExpectedAux;
                        ArrayList<String> valuesExpectedAux2;
                        valuesExpectedAux = connectionJdbcMB.categoricalCodeList(currentRelationVar.getFieldType(), 0);
                        valuesExpectedAux2 = connectionJdbcMB.categoricalNameList(currentRelationVar.getFieldType(), 0);
                        for (int i = 0; i < valuesExpectedAux.size(); i++) {
                            if (valuesExpectedAux.get(i).compareTo(currentValueExpected) == 0) {
                                nameOfValueExpected = valuesExpectedAux2.get(i);
                                break;
                            }
                            if (valuesExpectedAux2.get(i).compareTo(currentValueExpected) == 0) {
                                nameOfValueExpected = valuesExpectedAux.get(i);
                                break;
                            }
                        }
                    }
                }
                //---------------------------------------
            }
        }


        //;
        activeButtons();
    }

    public void changeValuesFound() {
        coincidentNewValue = "";
        newValueDisabled = true;
        activeButtons();
        if (valuesFoundSelectedInRelationValues.size() == 1) {
            btnViewValueDisabled = false;
            //createDynamicTable();
        } else {
            btnViewValueDisabled = true;
        }
    }

    public final void createDynamicTable() {
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<ArrayList<String>> listOfRecords = new ArrayList<ArrayList<String>>();
        try {
            //determino el error que esta seleccionado en la lista
            //busco cual es la relacion de variables actual        
            String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
            currentVariableExpected = splitVarRelated[0];
            currentVariableFound = splitVarRelated[1];
            RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
            if (currentRelationVar != null) {
                if (valuesFoundSelectedInRelationValues.size() == 1) {
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " WHERE " + currentVariableFound + "='" + valuesFoundSelectedInRelationValues.get(0) + "'");
                    // determino las cabeceras
                    for (int j = 1; j < rs.getMetaData().getColumnCount(); j++) {
                        titles.add(rs.getMetaData().getColumnName(j));
                    }
                    // determino los datos                
                    while (rs.next()) {
                        ArrayList<String> newRow = new ArrayList<String>();
                        for (int k = 1; k < rs.getMetaData().getColumnCount(); k++) {
                            newRow.add(rs.getString(k));
                        }
                        listOfRecords.add(newRow);
                    }
                    dinamicTable = new DinamicTable(listOfRecords, titles);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovMB_3 > " + ex.toString());
            //System.out.println("Error en la creacion de columnas dinamicas: " + ex.toString());
        }
    }

    public void changeRelationGroup() {
        //SE CARGAN LAS RELACIONES DE VARIABLES PERTENECIENTES A ESTE CONJUNTO
        relationsVariables = new ArrayList<String>();
        currentRelationVariables = null;
        btnCopyFrom2Disabled = true;
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();//buscar si ya existe el nombre
        for (int i = 0; i < relationGroupList.size(); i++) {
            if (relationGroupList.get(i).getNameRelationGroup().compareTo(currentRelationGroup) == 0) {
                List<RelationVariables> relationVariablesList = relationGroupList.get(i).getRelationVariablesList();
                for (int j = 0; j < relationVariablesList.size(); j++) {
                    relationsVariables.add(relationVariablesList.get(j).getNameExpected() + "->" + relationVariablesList.get(j).getNameFound());
                }
                break;
            }
        }
    }

    public void changeRelationVariables() {

        if (currentRelationVariables == null) {
            btnCopyFrom2Disabled = true;
        } else {
            if (currentRelationVariables.trim().length() == 0) {
                btnCopyFrom2Disabled = true;
            } else {
                btnCopyFrom2Disabled = false;
            }
        }
    }

    public void loadRelationsGroups() {

        //CARGAR GRUPO DE RELACIONES EXISTENTES
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
        relationGroups = new ArrayList<String>();
        for (int i = 0; i < relationGroupList.size(); i++) {
            relationGroups.add(relationGroupList.get(i).getNameRelationGroup());
        }
        relationsVariables = new ArrayList<String>();
        currentRelationVariables = null;
        currentRelationGroup = null;
        btnCopyFrom2Disabled = true;
    }

    public void btnCopyFromClick() {
        int numberCopy = 0;
        if (currentRelationVariables != null && currentRelationGroup != null) {
            if (currentRelationVariables.trim().length() != 0 && currentRelationGroup.trim().length() != 0) {
                //busco el grupo de relaciones seleccionado                

                RelationGroup relationGroupSelected = null;
                RelationVariables relationVariablesSelected = null;
                List<RelationValues> relationValuesListToCopy = null;

                //busco el conjunto de relaciones almacenado que esta seleccionado
                List<RelationGroup> relationGroupList = relationGroupFacade.findAll();//buscar si ya existe el nombre
                for (int i = 0; i < relationGroupList.size(); i++) {
                    if (relationGroupList.get(i).getNameRelationGroup().compareTo(currentRelationGroup) == 0) {
                        relationGroupSelected = relationGroupList.get(i);
                        break;
                    }
                }
                //dentro del grupo busco el grupo de relaciones de variables
                if (relationGroupSelected != null) {
                    List<RelationVariables> relationVariablesList = relationGroupSelected.getRelationVariablesList();
                    for (int i = 0; i < relationVariablesList.size(); i++) {
                        String relationVars = relationVariablesList.get(i).getNameExpected() + "->" + relationVariablesList.get(i).getNameFound();
                        if (relationVars.compareTo(currentRelationVariables) == 0) {
                            relationValuesListToCopy = relationVariablesList.get(i).getRelationValuesList();
                        }
                    }
                }
                //si existen valores realizo la copia
                if (relationValuesListToCopy != null) {
                    //busco cual es la relacion de variables actual        
                    String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
                    currentVariableExpected = splitVarRelated[0];
                    currentVariableFound = splitVarRelated[1];
                    RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
                    if (currentRelationVar != null) {
                        //le transfiero la relacion de valores del uno al otro                        
                        for (int i = 0; i < relationValuesListToCopy.size(); i++) {
                            //si no esta en a lista lo agrego
                            if (!currentRelationVar.findRelationValues(relationValuesListToCopy.get(i).getNameExpected(), relationValuesListToCopy.get(i).getNameFound())) {
                                //se determina si el valor encontrado encontrado de las que se
                                //van a copiar esta dentro de los valores encontrados del archivo
                                List<String> valuesFound2 = createListOfDistinctValuesFromFile(currentVariableFound);
                                for (int j = 0; j < valuesFound2.size(); j++) {
                                    if (relationValuesListToCopy.get(i).getNameFound().compareTo(valuesFound2.get(j)) == 0) {
                                        //se puede agragar la relacion por que el valor encontrado 
                                        //en la relacion de valores a copiar si esta en el archivo
                                        currentRelationVar.addRelationValue(relationValuesListToCopy.get(i).getNameExpected(), relationValuesListToCopy.get(i).getNameFound());
                                        numberCopy++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                changeCategoricalRelatedVariables();
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Finalizado", "Se copiaron: (" + String.valueOf(numberCopy) + ") relaciones de valores "));
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void btnRemoveDiscardedValuesClick() {
        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------        
        String nextValuesDiscardedSelected = "";
        String firstValuesDiscardedSelected;//primer relacion de valores a eliminar
        String lastValuesDiscardedSelected;//ultima relacion de valores a eliminar
        if (!valuesDiscardedSelectedInRelationValues.isEmpty()) {
            firstValuesDiscardedSelected = valuesDiscardedSelectedInRelationValues.get(0);
            lastValuesDiscardedSelected = valuesDiscardedSelectedInRelationValues.get(valuesDiscardedSelectedInRelationValues.size() - 1);
            for (int i = 0; i < valuesDiscarded.size(); i++) {
                if (valuesDiscarded.get(i).compareTo(lastValuesDiscardedSelected) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= valuesDiscarded.size() - 1) {//determino si tiene siguiente
                        nextValuesDiscardedSelected = valuesDiscarded.get(i + 1);//asigno el siguiente
                        break;
                    }
                }
                if (valuesDiscarded.get(i).compareTo(firstValuesDiscardedSelected) == 0) {//esta es la variable encontrada que saldra de la lista                    
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextValuesDiscardedSelected = valuesDiscarded.get(i - 1);//asigno el anterior
                        break;
                    }
                }

            }
        }

        //busco cual es la relacion de variables actual 

        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            for (int i = 0; i < valuesDiscardedSelectedInRelationValues.size(); i++) {
                currentRelationVar.removeDiscartedValue(valuesDiscardedSelectedInRelationValues.get(i));
            }
            loadFoundValues();
            //loadExpectedValues();
            loadRelatedAndDiscardedValues();
        }

        valuesDiscardedSelectedInRelationValues = new ArrayList<String>();
        if (nextValuesDiscardedSelected.trim().length() != 0) {
            valuesDiscardedSelectedInRelationValues.add(nextValuesDiscardedSelected);
        }
        activeButtons();
    }

    public void btnDiscardValueClick() {

        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------        
        String nextValuesFoundSelected = "";
        String firstValuesFoundSelected;//primer relacion de valores a eliminar
        String lastValuesFoundSelected;//ultima relacion de valores a eliminar
        if (!valuesFoundSelectedInRelationValues.isEmpty()) {
            firstValuesFoundSelected = valuesFoundSelectedInRelationValues.get(0);
            lastValuesFoundSelected = valuesFoundSelectedInRelationValues.get(valuesFoundSelectedInRelationValues.size() - 1);
            for (int i = 0; i < valuesFound.size(); i++) {
                if (valuesFound.get(i).compareTo(lastValuesFoundSelected) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= valuesFound.size() - 1) {//determino si tiene siguiente
                        nextValuesFoundSelected = valuesFound.get(i + 1);//asigno el siguiente
                        break;
                    }
                }
                if (valuesFound.get(i).compareTo(firstValuesFoundSelected) == 0) {//esta es la variable encontrada que saldra de la lista                    
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextValuesFoundSelected = valuesFound.get(i - 1);//asigno el anterior
                        break;
                    }
                }

            }
        }
        //busco cual es la relacion de variables actual        
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                currentRelationVar.addDiscartedValue(valuesFoundSelectedInRelationValues.get(i));
            }
            loadFoundValues();
            loadExpectedValues();
            loadRelatedAndDiscardedValues();
        }
        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        if (nextValuesFoundSelected.trim().length() != 0) {
            valuesFoundSelectedInRelationValues.add(nextValuesFoundSelected);
        }
        activeButtons();
    }

    public void btnAssociateRelationValueClick() {
        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------            
        String nextValuesFoundSelected = "";
        String firstValuesFoundSelected;//primer relacion de valores a eliminar
        String lastValuesFoundSelected;//ultima relacion de valores a eliminar
        if (!valuesFoundSelectedInRelationValues.isEmpty()) {
            firstValuesFoundSelected = valuesFoundSelectedInRelationValues.get(0);
            lastValuesFoundSelected = valuesFoundSelectedInRelationValues.get(valuesFoundSelectedInRelationValues.size() - 1);
            for (int i = 0; i < valuesFound.size(); i++) {
                if (valuesFound.get(i).compareTo(lastValuesFoundSelected) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= valuesFound.size() - 1) {//determino si tiene siguiente
                        nextValuesFoundSelected = valuesFound.get(i + 1);//asigno el siguiente
                        break;
                    }
                }
                if (valuesFound.get(i).compareTo(firstValuesFoundSelected) == 0) {//esta es la variable encontrada que saldra de la lista                    
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextValuesFoundSelected = valuesFound.get(i - 1);//asigno el anterior
                        break;
                    }
                }

            }
        }
        //busco cual es la relacion de variables actual        
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                currentRelationVar.addRelationValue(currentValueExpected, valuesFoundSelectedInRelationValues.get(i));
            }
            loadFoundValues();
            loadExpectedValues();
            loadRelatedAndDiscardedValues();
        }
        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        if (nextValuesFoundSelected.trim().length() != 0) {
            valuesFoundSelectedInRelationValues.add(nextValuesFoundSelected);
        }
        activeButtons();
    }

    private void activeButtons() {
        btnRemoveRelationValueDisabled = true;
        if (valuesRelatedSelectedInRelationValues != null) {
            if (!valuesRelatedSelectedInRelationValues.isEmpty()) {
                btnRemoveRelationValueDisabled = false;
            }
        }
        btnRemoveDiscardedValuesDisabled = true;
        if (valuesDiscardedSelectedInRelationValues != null) {
            if (!valuesDiscardedSelectedInRelationValues.isEmpty()) {
                btnRemoveDiscardedValuesDisabled = false;
            }
        }
        btnDiscardValueDisabled = true;
        btnViewValueDisabled = true;
        if (valuesFoundSelectedInRelationValues != null) {
            if (!valuesFoundSelectedInRelationValues.isEmpty()) {
                btnDiscardValueDisabled = false;
                btnViewValueDisabled = false;
            }
        }

        btnAutomaticRelationValueDisabled = true;
        if (valuesFound != null && valuesExpected != null) {
            if (!valuesFound.isEmpty() && !valuesExpected.isEmpty()) {
                btnAutomaticRelationValueDisabled = false;
            }
        }
        btnAssociateRelationValueDisabled = true;
        if (currentValueExpected != null) {
            if (valuesFoundSelectedInRelationValues != null) {
                if (currentValueExpected.trim().length() != 0 && !valuesFoundSelectedInRelationValues.isEmpty()) {
                    btnAssociateRelationValueDisabled = false;
                }
            }
        }
    }

    private void loadFoundValues() {
        /*
         * loadValuesExpectedAndFound cargar las listas de valores esperados y
         * encontrados
         */

        //selecciono cual es la relacion de variables actual
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            //cargo todos los valores esperados y encontrados(en encontrados se aplica DISCTINCT)            
            //loadExpectedValues();
            valuesFound = createListOfDistinctValuesFromFile(currentVariableFound);
            //saco la lista de valores realcionados
            if (currentRelationVar.getRelationValuesList() != null) {
                //elimino los valores que ya esten relacionados de las listas de valores encontrados                 
                for (int i = 0; i < currentRelationVar.getRelationValuesList().size(); i++) {
                    for (int j = 0; j < valuesFound.size(); j++) {
                        if (currentRelationVar.getRelationValuesList().get(i).getNameFound().compareTo(valuesFound.get(j)) == 0) {
                            valuesFound.remove(j);
                            break;
                        }
                    }
                }
            }
            //saco la lista de valores descartados            
            if (currentRelationVar.getRelationsDiscardedValuesList() != null) {
                valuesDiscarded = new ArrayList<String>();
                for (int i = 0; i < currentRelationVar.getRelationsDiscardedValuesList().size(); i++) {
                    valuesDiscarded.add(currentRelationVar.getRelationsDiscardedValuesList().get(i).getDiscardedValueName());
                }
                //elimino los campos que ya esten dentro de la lista de valores descartados
                for (int i = 0; i < valuesDiscarded.size(); i++) {
                    for (int j = 0; j < valuesFound.size(); j++) {
                        if (valuesDiscarded.get(i).compareTo(valuesFound.get(j)) == 0) {
                            valuesFound.remove(j);
                            break;
                        }
                    }
                }

            }
            //filtro los datos
            if (foundValuesFilter.trim().length() > 0) {
                filterText = foundValuesFilter.toUpperCase();
                for (int j = 0; j < valuesFound.size(); j++) {
                    foundText = valuesFound.get(j).toUpperCase();
//                    float x=jaroWinkler.getSimilarity(filterText, foundText);
//                    if (x < 0.7) {
//                        System.out.println("se quita: " +foundText+"    -   "+ String.valueOf(x));
//                        valuesFound.remove(j);
//                        j--;
//                    }
//                    else{
//                        System.out.println("acepta:  "+foundText+"    -   "+ String.valueOf(x));
//                    }


                    if (foundText.indexOf(filterText) == -1) {
                        if (!calculateLevenstein(filterText, foundText)) {
                            valuesFound.remove(j);
                            j--;
                        }
                    }
                }
            }
            if (!valuesFound.isEmpty()) {
                btnAutomaticRelationValueDisabled = false;
            }
        }
        activeButtons();
    }

    private boolean calculateLevenstein(String filterText, String foundText) {
        //damerauLevenshtein = new DamerauLevenshtein();
        //Similarity = 0;
        //creo un arreglo de cadenas con cada palabra
        splitFilterText = filterText.split(" ");
        splitFoundText = foundText.split(" ");
        //elimino las cadenas de cada arreglo que tengan menos de 4s caracteres
        for (int i = 0; i < splitFilterText.length; i++) {
            if (splitFilterText[i].length() <= 3) {
                splitFilterText[i] = "";
            }
        }
        for (int i = 0; i < splitFoundText.length; i++) {
            if (splitFoundText[i].length() <= 3) {
                splitFoundText[i] = "";
            }
        }
        //realizo el calculo de levenstein de todoas las palabras
        for (int i = 0; i < splitFilterText.length; i++) {
            for (int j = 0; j < splitFoundText.length; j++) {
                if (splitFilterText[i].length() != 0 && splitFoundText[j].length() != 0) {
                    if (damerauLevenshtein.getSimilarity(splitFilterText[i], splitFoundText[j]) < 2) {
                        return true;
                    }

//                    System.out.println(
//                            "COMPARACION: " + String.valueOf(jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]))
//                            + " CADENA1: " + splitFilterText[i]
//                            + " CADENA2: " + splitFoundText[j]);//jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]);
//
//                    if (jaroWinkler.getSimilarity(splitFilterText[i], splitFoundText[j]) > 0.8) {
//                        return true;
//                    }
                }
            }
        }


        return false;
    }

    public void btnAutomaticRelationClick() {
        //recorro la lista de variables esncontradas y re busco su equivalente
        //enla lista de variables esperadas y realizo las asociaciones
        int numberOfCreate = 0;//numero de relaciones creadas
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];

        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (currentRelationVar != null) {
            for (int i = 0; i < valuesFound.size(); i++) {
                for (int j = 0; j < valuesExpected.size(); j++) {
                    String valueFoundNoAccent = valuesFound.get(i).trim().toUpperCase().replace(".", "").replace(";", "");
                    String valueExpectedNoAccent = valuesExpected.get(j).trim().toUpperCase().replace(".", "").replace(";", "");

                    valueFoundNoAccent = valueFoundNoAccent.replace("Á", "A");
                    valueFoundNoAccent = valueFoundNoAccent.replace("É", "E");
                    valueFoundNoAccent = valueFoundNoAccent.replace("Í", "I");
                    valueFoundNoAccent = valueFoundNoAccent.replace("Ó", "O");
                    valueFoundNoAccent = valueFoundNoAccent.replace("Ú", "U");

                    valueExpectedNoAccent = valueExpectedNoAccent.replace("Á", "A");
                    valueExpectedNoAccent = valueExpectedNoAccent.replace("É", "E");
                    valueExpectedNoAccent = valueExpectedNoAccent.replace("Í", "I");
                    valueExpectedNoAccent = valueExpectedNoAccent.replace("Ó", "O");
                    valueExpectedNoAccent = valueExpectedNoAccent.replace("Ú", "U");

                    if (valueFoundNoAccent.compareTo(valueExpectedNoAccent) == 0) {
                        currentRelationVar.addRelationValue(valuesExpected.get(j), valuesFound.get(i));
                        numberOfCreate++;
                    }
                }
            }
            loadFoundValues();
            loadExpectedValues();
            loadRelatedAndDiscardedValues();
        }
        //btnAssociateRelationValueDisabled = true;
        //btnRemoveRelationValueDisabled = true;
        //btnAutomaticRelationValueDisabled = true;
        //if (valuesFound.size() > 0) {
        //    btnAutomaticRelationValueDisabled = false;
        //}
        activeButtons();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Finalizado", "El proceso automático realizó: (" + String.valueOf(numberOfCreate) + ") relaciones de valores"));
    }

    public void btnRemoveUnnecessaryClick() {
        //quitar de la lista las relaciones repetidas(no debe haber) y las que no se nececitan por que 
        //en los valores encontrados no existen los que estan en las relaciones
    }

    public void btnRemoveRelationValueClick() {
        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------        
        String nextValuesRelatedSelected = "";
        String firstValuesRelatedSelected;//primer relacion de valores a eliminar
        String lastValuesRelatedSelected;//ultima relacion de valores a eliminar
        if (!valuesRelatedSelectedInRelationValues.isEmpty()) {
            firstValuesRelatedSelected = valuesRelatedSelectedInRelationValues.get(0);
            lastValuesRelatedSelected = valuesRelatedSelectedInRelationValues.get(valuesRelatedSelectedInRelationValues.size() - 1);
            for (int i = 0; i < valuesRelated.size(); i++) {
                if (valuesRelated.get(i).compareTo(firstValuesRelatedSelected) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= valuesRelated.size() - 1) {//determino si tiene siguiente
                        nextValuesRelatedSelected = valuesRelated.get(i + 1);//asigno el siguiente
                        break;
                    }
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextValuesRelatedSelected = valuesRelated.get(i - 1);//asigno el anterior
                        break;
                    }
                }
                if (valuesRelated.get(i).compareTo(lastValuesRelatedSelected) == 0) {//esta es la variable encontrada que saldra de la lista
                    if (i + 1 <= valuesRelated.size() - 1) {//determino si tiene siguiente
                        nextValuesRelatedSelected = valuesRelated.get(i + 1);//asigno el siguiente
                        break;
                    }
                    if (i - 1 >= 0) {//determino si tiene anterior
                        nextValuesRelatedSelected = valuesRelated.get(i - 1);//asigno el anterior
                        break;
                    }
                }
            }
        }

        //encuentro la relacion de variables seleccionada
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVariables currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        //teniendo la relacion de variables encuentro la relacion de valores
        for (int i = 0; i < valuesRelatedSelectedInRelationValues.size(); i++) {
            String[] splitValuedRelated = valuesRelatedSelectedInRelationValues.get(i).split("->");
            currentValueExpected = splitValuedRelated[0];
            String currentValueFound = splitValuedRelated[1];
            //remuevo la relacion de valores 
            currentRelationVar.removeRelationValue(currentValueExpected, currentValueFound);
        }
        loadFoundValues();
        loadExpectedValues();
        loadRelatedAndDiscardedValues();
        valuesRelatedSelectedInRelationValues = new ArrayList<String>();
        //btnRemoveRelationValueDisabled = true;
        if (nextValuesRelatedSelected.trim().length() != 0) {
            valuesRelatedSelectedInRelationValues.add(nextValuesRelatedSelected);
            //btnRemoveRelationValueDisabled = false;
        }
        //btnAutomaticRelationValueDisabled = true;
        //if (valuesFound.size() > 0) {
        //    btnAutomaticRelationValueDisabled = false;
        //}
        activeButtons();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public String getcurrentVariableExpected() {
        return currentVariableExpected;
    }

    public void setcurrentVariableExpected(String currentVariableExpected) {
        this.currentVariableExpected = currentVariableExpected;
    }

    public String getcurrentVariableFound() {
        return currentVariableFound;
    }

    public void setcurrentVariableFound(String currentVariableFound) {
        this.currentVariableFound = currentVariableFound;
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

    public RelationGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setCurrentRelationsGroup(RelationGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

//    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
//        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
//    }
    public boolean isBtnAssociateRelationValueDisabled() {
        return btnAssociateRelationValueDisabled;
    }

    public void setBtnAssociateRelationValueDisabled(boolean btnAssociateRelationValueDisabled) {
        this.btnAssociateRelationValueDisabled = btnAssociateRelationValueDisabled;
    }

    public boolean isBtnAutomaticRelationValueDisabled() {
        return btnAutomaticRelationValueDisabled;
    }

    public void setBtnAutomaticRelationValueDisabled(boolean btnAutomaticRelationValueDisabled) {
        this.btnAutomaticRelationValueDisabled = btnAutomaticRelationValueDisabled;
    }

    public boolean isBtnRemoveRelationValueDisabled() {
        return btnRemoveRelationValueDisabled;
    }

    public void setBtnRemoveRelationValueDisabled(boolean btnRemoveRelationValueDisabled) {
        this.btnRemoveRelationValueDisabled = btnRemoveRelationValueDisabled;
    }

    public String getCurrentValueExpected() {
        return currentValueExpected;
    }

    public void setCurrentValueExpected(String currentValueExpected) {
        this.currentValueExpected = currentValueExpected;
    }

//    public String getCurrentValueFound() {
//        return currentValueFound;
//    }
//
//    public void setCurrentValueFound(String currentValueFound) {
//        this.currentValueFound = currentValueFound;
//    }
//
//    public String getCurrentValuesRelated() {
//        return currentValuesRelated;
//    }
//
//    public void setCurrentValuesRelated(String currentValuesRelated) {
//        this.currentValuesRelated = currentValuesRelated;
//    }
//    public Field getTypeVarExepted() {
//        return typeVarExepted;
//    }
//
//    public void setTypeVarExepted(Field typeVarExepted) {
//        this.typeVarExepted = typeVarExepted;
//    }
    public List<String> getValuesRelated() {
        return valuesRelated;
    }

    public void setValuesRelated(List<String> valuesRelated) {
        this.valuesRelated = valuesRelated;
    }

    public List<String> getCategoricalRelatedVariables() {
        return categoricalRelatedVariables;
    }

    public void setCategoricalRelatedVariables(List<String> categoricalRelatedVariables) {
        this.categoricalRelatedVariables = categoricalRelatedVariables;
    }

    public String getCurrentCategoricalRelatedVariables() {
        return currentCategoricalRelatedVariables;
    }

    public void setCurrentCategoricalRelatedVariables(String currentCategoricalRelatedVariables) {
        this.currentCategoricalRelatedVariables = currentCategoricalRelatedVariables;
    }

    public RelationshipOfVariablesMB getRelationshipOfVariablesMB() {
        return relationshipOfVariablesMB;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public boolean isBtnRemoveDiscardedValuesDisabled() {
        return btnRemoveDiscardedValuesDisabled;
    }

    public void setBtnRemoveDiscardedValuesDisabled(boolean btnRemoveDiscardedValuesDisabled) {
        this.btnRemoveDiscardedValuesDisabled = btnRemoveDiscardedValuesDisabled;
    }

    public boolean isBtnDiscardValueDisabled() {
        return btnDiscardValueDisabled;
    }

    public void setBtnDiscardValueDisabled(boolean btnDiscardValueDisabled) {
        this.btnDiscardValueDisabled = btnDiscardValueDisabled;
    }

    public boolean isBtnViewValueDisabled() {
        return btnViewValueDisabled;
    }

    public void setBtnViewValueDisabled(boolean btnViewValueDisabled) {
        this.btnViewValueDisabled = btnViewValueDisabled;
    }

    public List<String> getValuesDiscarded() {
        return valuesDiscarded;
    }

    public void setValuesDiscarded(List<String> valuesDiscarded) {
        this.valuesDiscarded = valuesDiscarded;
    }

    public List<String> getValuesDiscardedSelectedInRelationValues() {
        return valuesDiscardedSelectedInRelationValues;
    }

    public void setValuesDiscardedSelectedInRelationValues(List<String> valuesDiscardedSelectedInRelationValues) {
        this.valuesDiscardedSelectedInRelationValues = valuesDiscardedSelectedInRelationValues;
    }

    public List<String> getValuesFoundSelectedInRelationValues() {
        return valuesFoundSelectedInRelationValues;
    }

    public void setValuesFoundSelectedInRelationValues(List<String> valuesFoundSelectedInRelationValues) {
        this.valuesFoundSelectedInRelationValues = valuesFoundSelectedInRelationValues;
    }

    public List<String> getValuesRelatedSelectedInRelationValues() {
        return valuesRelatedSelectedInRelationValues;
    }

    public void setValuesRelatedSelectedInRelationValues(List<String> valuesRelatedSelectedInRelationValues) {
        this.valuesRelatedSelectedInRelationValues = valuesRelatedSelectedInRelationValues;
    }

    public String getNameOfValueExpected() {
        return nameOfValueExpected;
    }

    public void setNameOfValueExpected(String nameOfValueExpected) {
        this.nameOfValueExpected = nameOfValueExpected;
    }

    public DinamicTable getDinamicTable() {
        return dinamicTable;
    }

    public void setDinamicTable(DinamicTable dinamicTable) {
        this.dinamicTable = dinamicTable;
    }

    public ArrayList<String> getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(ArrayList<String> selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public String getCoincidentNewValue() {
        return coincidentNewValue;
    }

    public void setCoincidentNewValue(String coincidentNewValue) {
        this.coincidentNewValue = coincidentNewValue;
    }

    public String getCurrentVariableFound() {
        return currentVariableFound;
    }

    public void setCurrentVariableFound(String currentVariableFound) {
        this.currentVariableFound = currentVariableFound;
    }

    public boolean isNewValueDisabled() {
        return newValueDisabled;
    }

    public void setNewValueDisabled(boolean newValueDisabled) {
        this.newValueDisabled = newValueDisabled;
    }

    public boolean isBtnCopyFromDisabled() {
        return btnCopyFromDisabled;
    }

    public void setBtnCopyFromDisabled(boolean btnCopyFromDisabled) {
        this.btnCopyFromDisabled = btnCopyFromDisabled;
    }

    public List<String> getRelationGroups() {
        return relationGroups;
    }

    public void setRelationGroups(List<String> relationGroups) {
        this.relationGroups = relationGroups;
    }

    public String getCurrentRelationGroup() {
        return currentRelationGroup;
    }

    public void setCurrentRelationGroup(String currentRelationGroup) {
        this.currentRelationGroup = currentRelationGroup;
    }

    public String getCurrentRelationVariables() {
        return currentRelationVariables;
    }

    public void setCurrentRelationVariables(String currentRelationVariables) {
        this.currentRelationVariables = currentRelationVariables;
    }

    public List<String> getRelationsVariables() {
        return relationsVariables;
    }

    public void setRelationsVariables(List<String> relationsVariables) {
        this.relationsVariables = relationsVariables;
    }

    public boolean isBtnCopyFrom2Disabled() {
        return btnCopyFrom2Disabled;
    }

    public void setBtnCopyFrom2Disabled(boolean btnCopyFrom2Disabled) {
        this.btnCopyFrom2Disabled = btnCopyFrom2Disabled;
    }
}
