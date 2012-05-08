/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.lists.Field;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import managedBeans.preload.FormsAndFieldsDataMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfValuesMB")
@SessionScoped
public class RelationshipOfValuesMB {

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
    private String currentValueDiscarded  = "";//valor descartado
    private List<String> valuesDiscarded;
    private List<String> valuesExpected;
    private String currentValueFound = "";//valor encontrado
    private List<String> valuesFound;
    private List<String> valuesRelated;
    private String currentValuesRelated = "";//valor encontrado
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
        currentValueFound = "";
        currentValueExpected = "";
        valuesFound = new ArrayList<String>();
        valuesExpected = new ArrayList<String>();
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();
        
        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled=true;
        
        loadExpectedAndFoundValues();
        loadRelatedAndDiscardedValues();

//        //selecciono cual es la relacion de variables actual
//        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
//        if (relationVarSelected != null) {
//            //cargo todos los valores esperados y encontrados(en encontrados se aplica DISCTINCT)
//            loadExpectedValues();
//            valuesFound = createListOfDistinctValuesFromFile(currentVariableFound);
//
//            //saco la lista de valores realcionados
//            ArrayList<RelationValue> relationValueList = relationVarSelected.getRelationValueList();
//            //elimino los capos que ya esten relacionados de las listas de 
//            //valores encontrados no se elimina de la lista de valore esperados
//            //por que la relacion entre esperados y encontrados es de uno a muchos
//            for (int i = 0; i < relationValueList.size(); i++) {
//                for (int j = 0; j < valuesFound.size(); j++) {
//                    if (relationValueList.get(i).getNameFound().compareTo(valuesFound.get(j)) == 0) {
//                        valuesFound.remove(j);
//                        break;
//                    }
//                }
//            }
//            if (!valuesFound.isEmpty()) {//si quedan valores encontrados se activa la opcion de relacion automatica
//                btnAutomaticRelationValueDisabled = false;
//            }
//        }
    }

    public void loadCategoricalRelatedVariables(RelationsGroup relationsGroup) {
        
        
        if (relationsGroup != null) {
            currentRelationsGroup = relationsGroup;
        }

        String type;
        ArrayList<String> variablesRelated = new ArrayList<String>();
        for (int i = 0; i < currentRelationsGroup.getRelationVarList().size(); i++) {
            type = currentRelationsGroup.getRelationVarList().get(i).getFieldType();
            if (type.compareTo("integer") != 0 && type.compareTo("date") != 0 && type.compareTo("text") != 0) {
                type = currentRelationsGroup.getRelationVarList().get(i).getNameExpected();
                type = type + "->";
                type = type + currentRelationsGroup.getRelationVarList().get(i).getNameFound();
                variablesRelated.add(type);
            }
        }
        setCategoricalRelatedVariables(variablesRelated);

        btnAssociateRelationValueDisabled=true;
        btnAutomaticRelationValueDisabled=true;
        btnDiscardValueDisabled=true;
        btnRemoveDiscardedValuesDisabled=true;
        btnRemoveRelationValueDisabled=true;
        valuesFound = new ArrayList<String>();
        valuesExpected = new ArrayList<String>();
        valuesRelated = new ArrayList<String>();
        valuesDiscarded = new ArrayList<String>();
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
        this.currentValueFound = "";
        this.currentValuesRelated = "";
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
        valuesDiscarded= new ArrayList<String>();
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

        btnRemoveDiscardedValuesDisabled = false;//habilitar remover valor descartado

    }

    public void changeValuesRelated() {
        String[] splitValuesRelated;
        splitValuesRelated = currentValuesRelated.split("->");
        currentValueExpected = splitValuesRelated[0];
        currentValueFound = splitValuesRelated[1];
        currentValueFound = "";
        currentValueExpected = "";
        btnAssociateRelationValueDisabled = true;//deshabilitar asociar valores
        btnAutomaticRelationValueDisabled = true;//deshabilitar asociacion automatica de valores        
        btnRemoveRelationValueDisabled = false;//activar boton de eliminacion
    }

    public void changeValuesExpected() {
        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        if (currentValueFound != null) {
            if (currentValueExpected != null) {
                if (currentValueExpected.length() != 0
                        && currentValueFound.length() != 0) {
                    btnAssociateRelationValueDisabled = false;
                }
            }
        }
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }

    public void changeValuesFound() {
        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnDiscardValueDisabled = false;
        if (currentValueFound != null && currentValueExpected != null) {
            if (currentValueExpected.length() != 0 && currentValueFound.length() != 0) {
                btnAssociateRelationValueDisabled = false;
            }
        }
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public void btnRemoveDiscardedValuesClick() {
        //busco cual es la relacion de variables actual        
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            relationVarSelected.removeDiscartedValue(currentValueDiscarded);
            loadExpectedAndFoundValues();
            loadRelatedAndDiscardedValues();
        }
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled=true;
        //btnRemoveRelationValueDisabled = true;
        //btnAutomaticRelationValueDisabled = true;
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }

    public void btnDiscardValueClick() {        
        //busco cual es la relacion de variables actual        
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            relationVarSelected.addDiscartedValue(currentValueFound);
            loadExpectedAndFoundValues();
            loadRelatedAndDiscardedValues();
        }
        btnDiscardValueDisabled = true;
        btnRemoveDiscardedValuesDisabled=true;
        //btnRemoveRelationValueDisabled = true;
        //btnAutomaticRelationValueDisabled = true;
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }

    public void btnAssociateRelationValueClick() {
        //busco cual es la relacion de variables actual        
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            relationVarSelected.addRelationValue(currentValueExpected, currentValueFound);
            loadExpectedAndFoundValues();
            loadRelatedAndDiscardedValues();
        }
        btnAssociateRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnDiscardValueDisabled = true;
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }

    private void loadExpectedAndFoundValues() {
        /*loadValuesExpectedAndFound
         * cargar las listas de valores esperados y encontrados
         */
        btnAssociateRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnRemoveDiscardedValuesDisabled = true;
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
            valuesDiscarded=relationVarSelected.getDiscardedValues();
            //elimino los campos que ya esten dentro de la lista de valores descartados
            for (int i = 0; i < valuesDiscarded.size(); i++) {
                for (int j = 0; j < valuesFound.size(); j++) {
                    if (valuesDiscarded.get(i).compareTo(valuesFound.get(j)) == 0) {
                        valuesFound.remove(j);
                        break;
                    }
                }
            }
            
            if (!valuesFound.isEmpty()) {//si quedan valores encontrados se activa la opcion de relacion automatica
                btnAutomaticRelationValueDisabled = false;
            }
        }
    }

    public void btnAutomaticRelationClick() {
        //recorro la lista de variables esncontradas y re busco su equivalente
        //enla lista de variables esperadas y realizo las asociaciones
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        if (relationVarSelected != null) {
            for (int i = 0; i < valuesFound.size(); i++) {
                for (int j = 0; j < valuesExpected.size(); j++) {
                    if (valuesFound.get(i).compareTo(valuesExpected.get(j)) == 0
                            || valuesFound.get(i).compareTo(valuesExpected.get(j).toUpperCase()) == 0) {
                        relationVarSelected.addRelationValue(valuesExpected.get(j), valuesFound.get(i));
                    }
                }
            }
            loadExpectedAndFoundValues();
            loadRelatedAndDiscardedValues();
        }
        btnAssociateRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
    }

    public void btnRemoveRelationValueClick() {
        //encuentro la relacion de variables seleccionada
        String[] splitVarRelated = currentCategoricalRelatedVariables.split("->");
        currentVariableExpected = splitVarRelated[0];
        currentVariableFound = splitVarRelated[1];
        RelationVar relationVarSelected = currentRelationsGroup.findRelationVar(currentVariableExpected, currentVariableFound);
        //teniendo la relacion de variables encuantro la relacionde valores
        String[] splitValuedRelated = currentValuesRelated.split("->");
        currentValueExpected = splitValuedRelated[0];
        currentValueFound = splitValuedRelated[1];
        //remuevo la relacion de valores 
        relationVarSelected.removeRelationValue(currentValueExpected, currentValueFound);
        currentValueExpected = "";
        currentValueFound = "";
        currentValuesRelated = "";
        loadExpectedAndFoundValues();
        loadRelatedAndDiscardedValues();
        btnAssociateRelationValueDisabled = true;
        btnRemoveRelationValueDisabled = true;
        btnAutomaticRelationValueDisabled = true;
        if (valuesFound.size() > 0) {
            btnAutomaticRelationValueDisabled = false;
        }
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

    public String getCurrentValueFound() {
        return currentValueFound;
    }

    public void setCurrentValueFound(String currentValueFound) {
        this.currentValueFound = currentValueFound;
    }

    public String getCurrentValuesRelated() {
        return currentValuesRelated;
    }

    public void setCurrentValuesRelated(String currentValuesRelated) {
        this.currentValuesRelated = currentValuesRelated;
    }

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

    public String getCurrentValueDiscarded() {
        return currentValueDiscarded;
    }

    public void setCurrentValueDiscarded(String currentValueDiscarded) {
        this.currentValueDiscarded = currentValueDiscarded;
    }

    public List<String> getValuesDiscarded() {
        return valuesDiscarded;
    }

    public void setValuesDiscarded(List<String> valuesDiscarded) {
        this.valuesDiscarded = valuesDiscarded;
    }
    
    
}
