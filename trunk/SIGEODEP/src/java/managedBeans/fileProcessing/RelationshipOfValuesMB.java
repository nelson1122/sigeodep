/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.enumerators.DataTypeEnum;
import beans.lists.Field;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
@ManagedBean(name = "relationshipOfValuesMB")
@SessionScoped
public class RelationshipOfValuesMB implements Serializable {

    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres     
    private String currentCategoricalRelatedVariables = "";//valor esperado
    private List<String> categoricalRelatedVariables;
    private boolean btnAssociateRelationValueDisabled = true;
    private boolean btnAutomaticRelationValueDisabled = true;
    private boolean btnRemoveRelationValueDisabled = true;
    private boolean btnRemoveDiscardedValuesDisabled = true;
    private boolean btnDiscardValueDisabled = true;
    private String currentVariableExpected = "";//ariable Esperado
    private String currentVariableFound = "";//valor Encontrado
    private String currentValueExpected = "";//valor esperado
    //private String currentValueDiscarded  = "";//valor descartado
    private List<String> valuesDiscarded;
    private List<String> valuesExpected;
    //private String currentValueFound = "";//valor encontrado
    private List<String> valuesFoundSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesRelatedSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesDiscardedSelectedInRelationValues = new ArrayList<String>();
    private List<String> valuesFound;
    private List<String> valuesRelated;
    //private String currentValuesRelated = "";//valor encontrado
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private Field typeVarExepted;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private RelationsGroup currentRelationsGroup;

    public void changeCategoricalRelatedVariables() {
        //asignar el valor de las variables (para poder remove)
        String[] splitValuesRelated;
        splitValuesRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitValuesRelated[0];
        currentVariableFound = splitValuesRelated[1];
        //currentValueFound = "";
        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        valuesRelatedSelectedInRelationValues = new ArrayList<String>();
        valuesDiscardedSelectedInRelationValues = new ArrayList<String>();

        currentValueExpected = "";
        valuesFound = new ArrayList<String>();
        valuesExpected = new ArrayList<String>();
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();

        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled = true;

        loadExpectedAndFoundValues();
        loadRelatedAndDiscardedValues();
    }

    public void loadCategoricalRelatedVariables(RelationsGroup relationsGroup) {


        if (relationsGroup != null) {
            currentRelationsGroup = relationsGroup;
        }

        String type;
        ArrayList<String> variablesRelated = new ArrayList<String>();
        for (int i = 0; i < currentRelationsGroup.getRelationVarList().size(); i++) {
            type = currentRelationsGroup.getRelationVarList().get(i).getFieldType();
            switch (DataTypeEnum.convert(type)) {
                case NOVALUE://idica que NO es: integer,date,minute,hour,day,month,year,age,military
                    type = currentRelationsGroup.getRelationVarList().get(i).getNameExpected();
                    type = type + "->";
                    type = type + currentRelationsGroup.getRelationVarList().get(i).getNameFound();
                    variablesRelated.add(type);
                    break;
            }
//            if (type.compareTo("integer") != 0 && type.compareTo("age") != 0 && type.compareTo("military") != 0 && type.compareTo("date") != 0 && type.compareTo("text") != 0) {
//                type = currentRelationsGroup.getRelationVarList().get(i).getNameExpected();
//                type = type + "->";
//                type = type + currentRelationsGroup.getRelationVarList().get(i).getNameFound();
//                variablesRelated.add(type);
//            }
        }
        setCategoricalRelatedVariables(variablesRelated);

        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled = true;
        btnRemoveRelationValueDisabled = true;
        valuesFound = new ArrayList<String>();
        valuesExpected = new ArrayList<String>();
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();
        currentCategoricalRelatedVariables = "";
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
    }

    public void reset() {//@PostConstruct ejecutar despues de el constructor
        this.categoricalRelatedVariables = new ArrayList<String>();
        this.valuesExpected = new ArrayList<String>();
        this.valuesRelated = new ArrayList<String>();
        this.valuesFound = new ArrayList<String>();
        this.valuesDiscarded = new ArrayList<String>();
        this.currentVariableFound = "";
        this.currentVariableExpected = "";
        this.currentValueExpected = "";
        //this.currentValueFound = "";
        //this.currentValuesRelated = "";
        valuesFoundSelectedInRelationValues = new ArrayList<String>();
        valuesRelatedSelectedInRelationValues = new ArrayList<String>();
        valuesDiscardedSelectedInRelationValues = new ArrayList<String>();
        this.btnAssociateRelationValueDisabled = true;
        this.btnRemoveRelationValueDisabled = true;
        this.btnAutomaticRelationValueDisabled = true;
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
        if (currentVariableExpected.length() != 0) {
            typeVarExepted = formsAndFieldsDataMB.searchField(currentVariableExpected);
            //fieldsFacade.findByFormField(currentForm, currentVariableExpected).getFieldType();            
            valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
            if (currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound).getTypeComparisonForCode()) {
                valuesExpected = formsAndFieldsDataMB.categoricalCodeList(currentVariableExpected, 0);
            } else {
                valuesExpected = formsAndFieldsDataMB.categoricalNameList(currentVariableExpected, 0);
            }
        }
    }

    private void loadRelatedAndDiscardedValues() {
        valuesRelated = new ArrayList<String>();
        //busco cual es la relacion de variables actual        
        String relation;
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar currentRelationVar = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        //teniendo la relacion de variables actual determino la lista de relaciones de variables para esta
        ArrayList<RelationValue> currentRelationValuesList = currentRelationVar.getRelationValueList();
        for (int i = 0; i < currentRelationValuesList.size(); i++) {
            relation = currentRelationValuesList.get(i).getNameExpected();
            relation = relation + "->";
            relation = relation + currentRelationValuesList.get(i).getNameFound();
            valuesRelated.add(relation);
        }
        //recargo la lista de valores descartados
        //ArrayList<RelationValue> currentRelationValuesList = currentRelationVar.getRelationValueList();
        valuesDiscarded = new ArrayList<String>();
        for (int i = 0; i < currentRelationVar.getDiscardedValues().size(); i++) {
            valuesDiscarded.add(currentRelationVar.getDiscardedValues().get(i));
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
            //determino el nombre de la columna
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet rs = conx.consult("SELECT DISTINCT(" + column + ") FROM temp; ");
            while (rs.next()) {
                array.add(rs.getString(1));
            }
            conx.disconnect();
        } catch (SQLException ex) {
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeValuesDiscarded() {
        activeButtons();
        //btnRemoveDiscardedValuesDisabled = false;//habilitar remover valor descartado

    }

    public void changeValuesRelated() {
        //String[] splitValuesRelated;
        //splitValuesRelated = currentValuesRelated.split("->");
        //currentValueExpected = splitValuesRelated[0];
        //currentValueFound = splitValuesRelated[1];
        //currentValueFound = "";
        //currentValueExpected = "";
//        btnAssociateRelationValueDisabled = true;//deshabilitar asociar valores
//        btnAutomaticRelationValueDisabled = true;//deshabilitar asociacion automatica de valores        
//        btnRemoveRelationValueDisabled = false;//activar boton de eliminacion
        activeButtons();
    }

    public void changeValuesExpected() {
        activeButtons();
//        btnAssociateRelationValueDisabled = true;
//        btnAutomaticRelationValueDisabled = true;
//        btnRemoveRelationValueDisabled = true;
//        if (!valuesFoundSelectedInRelationValues.isEmpty()) {
//            if (currentValueExpected != null) {
//                if (currentValueExpected.length() != 0) {
//                    btnAssociateRelationValueDisabled = false;
//                }
//            }
//        }
//        btnAutomaticRelationValueDisabled = true;
//        if (valuesFound.size() > 0) {
//            btnAutomaticRelationValueDisabled = false;
//        }
    }

    public void changeValuesFound() {
        activeButtons();
//        if (!valuesFoundSelectedInRelationValues.isEmpty() && currentValueExpected != null) {
//            if (currentValueExpected.length() != 0) {
//                btnAssociateRelationValueDisabled = false;
//            }
//        }
//        if (valuesFound.size() > 0) {
//            btnAutomaticRelationValueDisabled = false;
//        }
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
        String firstValuesDiscardedSelected = "";//primer relacion de valores a eliminar
        String lastValuesDiscardedSelected = "";//ultima relacion de valores a eliminar
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
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            for (int i = 0; i < valuesDiscardedSelectedInRelationValues.size(); i++) {
                relationVarSelected.removeDiscartedValue(valuesDiscardedSelectedInRelationValues.get(i));
            }
            loadExpectedAndFoundValues();
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
        String firstValuesFoundSelected = "";//primer relacion de valores a eliminar
        String lastValuesFoundSelected = "";//ultima relacion de valores a eliminar
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
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                relationVarSelected.addDiscartedValue(valuesFoundSelectedInRelationValues.get(i));
            }
            loadExpectedAndFoundValues();
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
        String firstValuesFoundSelected = "";//primer relacion de valores a eliminar
        String lastValuesFoundSelected = "";//ultima relacion de valores a eliminar
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
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            for (int i = 0; i < valuesFoundSelectedInRelationValues.size(); i++) {
                relationVarSelected.addRelationValue(currentValueExpected, valuesFoundSelectedInRelationValues.get(i));
            }
            loadExpectedAndFoundValues();
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
        if (!valuesRelatedSelectedInRelationValues.isEmpty()) {
            btnRemoveRelationValueDisabled = false;
        }
        btnRemoveDiscardedValuesDisabled = true;
        if (!valuesDiscardedSelectedInRelationValues.isEmpty()) {
            btnRemoveDiscardedValuesDisabled = false;
        }
        btnDiscardValueDisabled = true;
        if (!valuesFoundSelectedInRelationValues.isEmpty()) {
            btnDiscardValueDisabled = false;
        }
        btnAutomaticRelationValueDisabled = true;
        if (!valuesFound.isEmpty() && !valuesExpected.isEmpty()) {
            btnAutomaticRelationValueDisabled = false;
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

    private void loadExpectedAndFoundValues() {
        /*
         * loadValuesExpectedAndFound cargar las listas de valores esperados y
         * encontrados
         */
        //btnAssociateRelationValueDisabled = true;
        //btnAutomaticRelationValueDisabled = true;
        //btnRemoveRelationValueDisabled = true;
        //btnRemoveDiscardedValuesDisabled = true;
        //selecciono cual es la relacion de variables actual
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            if (relationVarSelected.getTypeComparisonForCode())//cuando se creo la relacion se comparo po codigo
            {
                //compareForCode = true;
            }
            //cargo todos los valores esperados y encontrados(en encontrados se aplica DISCTINCT)            
            loadExpectedValues();
            valuesFound = createListOfDistinctValuesFromFile(currentVariableFound);
            //saco la lista de valores realcionados
            ArrayList<RelationValue> relationValueList = relationVarSelected.getRelationValueList();
            //elimino los capos que ya esten relacionados de las listas de 
            //valores encontrados no se elimina de la lista de valore esperados
            //por que la relacion entre esperados y encontrados es de uno a muchos
            for (int i = 0; i < relationValueList.size(); i++) {
                for (int j = 0; j < valuesFound.size(); j++) {
                    if (relationValueList.get(i).getNameFound().compareTo(valuesFound.get(j)) == 0) {
                        valuesFound.remove(j);
                        break;
                    }
                }
            }
            //saco la lista de valores descartados
            valuesDiscarded = relationVarSelected.getDiscardedValues();
            //elimino los campos que ya esten dentro de la lista de valores descartados
            for (int i = 0; i < valuesDiscarded.size(); i++) {
                for (int j = 0; j < valuesFound.size(); j++) {
                    if (valuesDiscarded.get(i).compareTo(valuesFound.get(j)) == 0) {
                        valuesFound.remove(j);
                        break;
                    }
                }
            }

            //if (!valuesFound.isEmpty()) {//si quedan valores encontrados se activa la opcion de relacion automatica
            //    btnAutomaticRelationValueDisabled = false;
            //}
        }
        activeButtons();
    }

    public void btnAutomaticRelationClick() {
        //recorro la lista de variables esncontradas y re busco su equivalente
        //enla lista de variables esperadas y realizo las asociaciones
        int numberOfCreate = 0;//numero de relaciones creadas
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];

        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            for (int i = 0; i < valuesFound.size(); i++) {
                for (int j = 0; j < valuesExpected.size(); j++) {
                    String valueFoundNoAccent = valuesFound.get(i).toUpperCase();
                    String valueExpectedNoAccent = valuesExpected.get(j).toUpperCase();
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
                        relationVarSelected.addRelationValue(valuesExpected.get(j), valuesFound.get(i));
                        numberOfCreate++;
                    }
                }
            }
            loadExpectedAndFoundValues();
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

    public void btnRemoveRelationValueClick() {
        //---------------------------------------------------------------------------
        //como se quita de la lista un item se determina que item quedara seleccionado
        //---------------------------------------------------------------------------        
        String nextValuesRelatedSelected = "";
        String firstValuesRelatedSelected = "";//primer relacion de valores a eliminar
        String lastValuesRelatedSelected = "";//ultima relacion de valores a eliminar
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
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        //teniendo la relacion de variables encuentro la relacion de valores
        for (int i = 0; i < valuesRelatedSelectedInRelationValues.size(); i++) {
            String[] splitValuedRelated = valuesRelatedSelectedInRelationValues.get(i).split("->");
            currentValueExpected = splitValuedRelated[0];
            String currentValueFound = splitValuedRelated[1];
            //remuevo la relacion de valores 
            relationVarSelected.removeRelationValue(currentValueExpected, currentValueFound);
        }
        loadExpectedAndFoundValues();
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

    public void setCurrentRelationsGroup(RelationsGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public RelationsGroup getCurrentRelationsGroup() {
        return currentRelationsGroup;
    }

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

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
    public Field getTypeVarExepted() {
        return typeVarExepted;
    }

    public void setTypeVarExepted(Field typeVarExepted) {
        this.typeVarExepted = typeVarExepted;
    }

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

//    public String getCurrentValueDiscarded() {
//        return currentValueDiscarded;
//    }
//
//    public void setCurrentValueDiscarded(String currentValueDiscarded) {
//        this.currentValueDiscarded = currentValueDiscarded;
//    }
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
}
