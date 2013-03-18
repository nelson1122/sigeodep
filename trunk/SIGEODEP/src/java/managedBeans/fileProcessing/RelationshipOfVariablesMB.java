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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import model.dao.FieldsFacade;
import model.dao.RelationGroupFacade;
import model.dao.RelationVariablesFacade;
import model.pojo.RelationGroup;
import model.pojo.RelationVariables;

/**
 *
 * @author santos
 */
@ManagedBean(name = "relationshipOfVariablesMB")
@SessionScoped
public class RelationshipOfVariablesMB implements Serializable {

    //private boolean btnRemoveRelationVarDisabled = true;
    private boolean btnSaveConfigurationDisabled = true;
    private boolean btnLoadConfigurationDisabled = false;
    private boolean btnJoinColumnsDisabled;
    private boolean btnDivideColumnsDisabled;
    private boolean selectDateFormatDisabled = true;
    private boolean compareForCode = false;
    private List<String> variablesExpected;
    //private String currentVarFound = "";
    private List<String> varsFound;
    private List<String> valuesExpected;
    private List<String> valuesDiscarded;
    private List<String> valuesFound;
    //private String currentRelatedVars = "";//actual relacion de variables
    private List<String> relatedVars;
    private String currentRelationGroupName = "";
    private String currentDateFormat = "dd/MM/yyyy";//tipo de formato de fecha actual
    //private String currentVarExpected = "";//variable esperda para relacionar variables
    private List<String> currentVariableExpected = new ArrayList<String>();
    private List<String> currentVariableFound = new ArrayList<String>();
    private List<String> currentRelatedVariables = new ArrayList<String>();
    private String typeVarExepted;
    private String variableDescription = "";
    private RelationGroup currentRelationGroup;
    private ProjectsMB projectsMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private LoginMB loginMB;
    private String filterConsult = "";
    private String expectedVariablesFilter = "";
    private String foundVariablesFilter = "";
    private String relatedVariablesFilter = "";
    private String filterText;
    private String foundText;
    ConnectionJdbcMB connectionJdbcMB;
    String sql = "";
    private String nameTableTemp = "temp";
    @EJB
    FieldsFacade fieldsFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */

    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void changeRelatedVariablesFilter() {
        loadRelatedVariables();
    }

    public void changeFoundVariablesFilter() {
        loadFoundVariables();
    }

    public void changeExpectedVariablesFilter() {
        loadExpectedVariables();
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
        this.currentVariableFound = new ArrayList<String>();
        this.currentVariableExpected = new ArrayList<String>();
        //this.btnRemoveRelationVarDisabled = true;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private void loadExpectedVariables() {
        try {
            filterConsult = "";
            if (expectedVariablesFilter != null && expectedVariablesFilter.trim().length() != 0) {
                filterConsult = "    fields.field_name ILIKE '%" + expectedVariablesFilter + "%' AND \n";
            }
            ResultSet rs;//vaiables esperadas-----------------------------------
            sql = ""
                    + " SELECT \n"
                    + "    fields.field_name \n"
                    + " FROM \n"
                    + "    public.fields \n"
                    + " WHERE \n"
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND \n"
                    + filterConsult
                    + "    fields.field_name NOT IN \n"
                    + "    (SELECT \n"
                    + "        relation_variables.name_expected \n"
                    + "     FROM \n"
                    + "        public.relation_group, \n"
                    + "        public.relation_variables \n"
                    + "     WHERE \n"
                    + "        relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                    + "        relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                    + "     )\n"
                    + " ORDER BY \n"
                    + "    fields.field_order;\n";
            variablesExpected = new ArrayList<String>();
            //System.out.println("A001\n" + sql);
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                variablesExpected.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("******PRIMER INGRESO******");
        }
    }

    private void loadFoundVariables() {
        try {
            ResultSet rs;
            varsFound = new ArrayList<String>();//vaiables encontradas----------            
            filterConsult = "";
            if (foundVariablesFilter != null && foundVariablesFilter.trim().length() != 0) {
                filterConsult = "    project_columns.column_name ILIKE '%" + foundVariablesFilter + "%' AND \n";
            }
            sql = ""
                    + " SELECT     "
                    + "	   project_columns.column_name"
                    + " FROM     "
                    + "	   public.project_records,     "
                    + "	   public.project_columns "
                    + " WHERE     "
                    + "	   project_columns.column_id = project_records.column_id AND    "
                    + filterConsult
                    + "	   project_records.project_id = " + projectsMB.getCurrentProjectId() + " AND "
                    + "	   project_columns.column_name NOT IN "
                    + "	   (SELECT"
                    + "		relation_variables.name_found"
                    + "	   FROM     "
                    + "		public.relation_group,     "
                    + "		public.relation_variables "
                    + "	   WHERE     "
                    + "		relation_variables.id_relation_group = relation_group.id_relation_group AND    "
                    + "		relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "'"
                    + "	   )"
                    + " GROUP BY"
                    + "	   project_columns.column_name,"
                    + "	   project_columns.column_id"
                    + " ORDER BY "
                    + "	   project_columns.column_id";

            rs = connectionJdbcMB.consult(sql);
            //System.out.println("A002\n" + sql);
            while (rs.next()) {
                varsFound.add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("******PRIMER INGRESO******");
        }
    }

    private void loadRelatedVariables() {
        try {
            ResultSet rs;
            relatedVars = new ArrayList<String>();//variables relacionadas------
            filterConsult = "";
            if (relatedVariablesFilter != null && relatedVariablesFilter.trim().length() != 0) {
                filterConsult = " AND \n    (relation_variables.name_expected ILIKE '%" + relatedVariablesFilter + "%' OR \n";
                filterConsult = filterConsult + "    relation_variables.name_found ILIKE '%" + relatedVariablesFilter + "%' )";
            }
            sql = ""
                    + " SELECT "
                    + "    relation_variables.name_expected, \n"
                    + "    relation_variables.name_found \n"
                    + " FROM \n"
                    + "    public.relation_group, \n"
                    + "    public.relation_variables \n"
                    + " WHERE \n"
                    + "    relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                    + "    relation_group.name_relation_group LIKE '" + projectsMB.getCurrentRelationsGroupName() + "' \n"
                    + filterConsult;
            //System.out.println("A003\n" + sql);
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                relatedVars.add(rs.getString(1) + "->" + rs.getString(2));
            }
            relationshipOfValuesMB.setCategoricalRelationsFilter("");
            relationshipOfValuesMB.loadCategoricalRelatedVariables();
//            System.out.println("sssssssssssssssssss");
//            System.out.println("currentValueExpected: " + relationshipOfValuesMB.getCurrentValueExpected().toString());
//            System.out.println("expectedValuesFilter: " + relationshipOfValuesMB.getExpectedValuesFilter().toString());
//            System.out.println("valuesExpected: " + relationshipOfValuesMB.getValuesExpected().toString());
//            System.out.println("sssssssssssssssssss");
        } catch (Exception e) {
            System.out.println("******PRIMER INGRESO******");
        }
    }

    public void loadVarsExpectedAndFound() {
        /*
         * cargar las listas de variables encontradas y esperadas
         */
        loadExpectedVariables();
        loadFoundVariables();
        loadRelatedVariables();
        valuesExpected = new ArrayList<String>();
        valuesFound = new ArrayList<String>();
    }

    private String getTypeVariableExpected() {
        String strReturn = "";
        try {
            //determino el ty            
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    fields.field_type"
                    + " FROM "
                    + "    public.fields, "
                    + "    public.relation_group"
                    + " WHERE "
                    + "    relation_group.form_id = fields.form_id AND"
                    + "    relation_group.id_relation_group = " + projectsMB.getCurrentRelationsGroupId() + " AND "
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND "
                    + "    fields.field_name LIKE '" + currentVariableExpected.get(0) + "';");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_1 > " + ex.toString());
        }
        return strReturn;
    }

    private String getDescriptionVariableExpected() {
        String strReturn = "";
        try {
            //determino el ty            
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    fields.field_description"
                    + " FROM "
                    + "    public.fields, "
                    + "    public.relation_group"
                    + " WHERE "
                    + "    relation_group.form_id = fields.form_id AND"
                    + "    relation_group.id_relation_group = " + projectsMB.getCurrentRelationsGroupId() + " AND "
                    + "    fields.form_id LIKE '" + projectsMB.getCurrentFormId() + "' AND "
                    + "    fields.field_name LIKE '" + currentVariableExpected.get(0) + "';");
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_1 > " + ex.toString());
        }
        return strReturn;
    }

    public void loadValuesExpected() {
        /*
         * cargar los valores esperados dependiendo la variable esperada
         */
        //if (currentVarExpected.trim().length() != 0) {
        if (currentVariableExpected != null && !currentVariableExpected.isEmpty()) {
            typeVarExepted = getTypeVariableExpected();
            //fieldsFacade.findFieldTypeByFieldNameAndFormId(currentVarExpected, currentRelationGroup.getFormId().getFormId()).getFieldType();
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
//    public ArrayList createListOfValuesFromFile(String column) {
//
//        ArrayList<String> array = new ArrayList<String>();
//        try {
//            //determino el nombre de la columna            
//            ResultSet rs = connectionJdbcMB.consult("SELECT " + column + " FROM " + nameTableTemp + "; ");
//            while (rs.next()) {
//                array.add(rs.getString(1));
//            }
//        } catch (SQLException ex) {
//            System.out.println("Error: rovaMB_1 > " + ex.toString());
//        }
//        return array;
//    }

    /*
     * crear una lista de valores de una determinada columna proveniente del
     * archivo con valores no repetidos
     */
    public void loadValuesFound(String column) {
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + " 	DISTINCT(project_records.data_value) "
                    + " FROM "
                    + " 	project_records"
                    + " WHERE   "
                    + " 	project_id=" + projectsMB.getCurrentProjectId() + " AND"
                    + " 	column_id IN "
                    + " 		(SELECT "
                    + " 			column_id "
                    + " 		FROM "
                    + " 			project_columns"
                    + " 		WHERE"
                    + " 			project_columns.column_name LIKE '" + column + "'"
                    + " 		)"
                    + " LIMIT 50;");

            valuesFound = new ArrayList<String>();
            while (rs.next()) {
                valuesFound.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_2 > " + ex.toString());
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR -----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeRelatedVariables() {
        //la funcion se encargda de limpiar la seccion "RELACION DE VALORES"
        //System.out.println("SE LIMPIA SECCION DE RELACION DE VALORES");
        relationshipOfValuesMB.setCategoricalRelationsFilter("");
        relationshipOfValuesMB.loadCategoricalRelatedVariables();
    }

    public void changeVarExpected() {
        valuesExpected = new ArrayList<String>();//borro la lista de valores esperados 
        if (currentVariableExpected != null && !currentVariableExpected.isEmpty()) {
            //if (currentVarExpected != null && currentVarExpected.length() != 0) {
            //if (currentVarExpected.length() != 0) {
            variableDescription = getDescriptionVariableExpected();
            loadValuesExpected();
            //}
        }
    }

    public void changeVarFound() {
        valuesFound = new ArrayList<String>();//borro la lista de valores esperados 
        if (currentVariableFound != null && !currentVariableFound.isEmpty()) {
            //if (currentVarFound.trim().length() != 0) {
            currentRelatedVariables = new ArrayList<String>();
            loadValuesFound(currentVariableFound.get(0));
            //}
        }
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

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
            //if (currentVarExpected == null || currentVarExpected.trim().length() == 0) {
            if (currentVariableExpected == null || currentVariableExpected.isEmpty()) {
                error = "Debe seleccionarse una variable esperada";
                nextStep = false;
            }
        }
        if (nextStep) {
            if (currentVariableFound == null || currentVariableFound.isEmpty()) {
                error = "Debe seleccionarse una variable encontrada";
                nextStep = false;
            }
        }
        if (nextStep) {
            selectDateFormatDisabled = true;
            RelationVariables newRelationVariables = new RelationVariables();
            newRelationVariables.setIdRelationVariables(relationVariablesFacade.findMaxId() + 1);
            newRelationVariables.setNameExpected(currentVariableExpected.get(0));
            newRelationVariables.setNameFound(currentVariableFound.get(0));
            newRelationVariables.setFieldType(typeVarExepted);
            newRelationVariables.setComparisonForCode(compareForCode);
            newRelationVariables.setDateFormat(currentDateFormat);
            newRelationVariables.setIdRelationGroup(relationGroupFacade.find(projectsMB.getCurrentRelationsGroupId()));
            relationVariablesFacade.create(newRelationVariables);
            loadVarsExpectedAndFound();//recargo listas de variables esperadas y encontradas   
        }
        if (nextStep) {//no se produjeron errores solo alertas
            if (error.length() == 0) {//no existieron errores            
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Asociación de variables realizada."));
            } else {//hay  errores al relacionar la variables 
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", error));
            }
        } else {//se produjo un error
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", error));
        }
    }

    private int getRelationVariablesId(String name_expected, String name_found) {
        int intReturn = -1;
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + " 	id_relation_variables "
                    + " FROM "
                    + " 	relation_variables"
                    + " WHERE   "
                    + " 	id_relation_group=" + projectsMB.getCurrentRelationsGroupId() + " AND"
                    + " 	name_expected LIKE '" + name_expected + "' AND "
                    + " 	name_found LIKE '" + name_found + "'");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error: rovaMB_2 > " + ex.toString());
        }
        return intReturn;
    }

    public void btnRemoveRelationVarClick() {
        /*
         * click sobre boton remover relacion de variables
         */
        if (currentRelatedVariables == null || currentRelatedVariables.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar la relación a eliminar."));
        } else {
            //elimino los item de la lista de variables relacionadas
            for (int i = 0; i < currentRelatedVariables.size(); i++) {
                String[] splitVarRelated = currentRelatedVariables.get(i).split("->");
                //FALTA ELIMINAR RELACIONES DE VALORES SI ES UNA RELACION CATEGORICA
                try {
                    int relationVariablesId = getRelationVariablesId(splitVarRelated[0], splitVarRelated[1]);
                    connectionJdbcMB.remove("relations_discarded_values", "id_relation_variables = " + relationVariablesId);
                    connectionJdbcMB.remove("relation_values", "id_relation_variables = " + relationVariablesId);
                    connectionJdbcMB.remove("relation_variables", "id_relation_variables = " + relationVariablesId);
                } catch (Exception e) {
                    System.out.println("Excepcion eliminando relacion de valores: " + e.toString());
                }
                //relationVariablesFacade.remove(relationVariablesFacade.find(getRelationVariablesId(splitVarRelated[0], splitVarRelated[1])));
            }
            currentRelatedVariables = new ArrayList<String>();
            loadVarsExpectedAndFound();//recargo lista de variables esperadas y encontradas
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Las relaciónes seleccionadas han sido eliminadas."));
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
//    public String getCurrentVarExpected() {
//        return currentVarExpected;
//    }
//
//    public void setCurrentVarExpected(String currentVarExpected) {
//        this.currentVarExpected = currentVarExpected;
//    }
//    public String getCurrentVarFound() {
//        return currentVarFound;
//    }
//
//    public void setCurrentVarFound(String currentVarFound) {
//        this.currentVarFound = currentVarFound;
//    }
    public List<String> getCurrentVariableFound() {
        return currentVariableFound;
    }

    public void setCurrentVariableFound(List<String> currentVariableFound) {
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
//    public boolean isBtnRemoveRelationVarDisabled() {
//        return btnRemoveRelationVarDisabled;
//    }
//
//    public void setBtnRemoveRelationVarDisabled(boolean btnRemoveRelationVarDisabled) {
//        this.btnRemoveRelationVarDisabled = btnRemoveRelationVarDisabled;
//    }
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

//    public String getCurrentRelatedVars() {
//        return currentRelatedVars;
//    }
//
//    public void setCurrentRelatedVars(String currentRelatedtVars) {
//        this.currentRelatedVars = currentRelatedtVars;
//    }
    public List<String> getCurrentRelatedVariables() {
        return currentRelatedVariables;
    }

    public void setCurrentRelatedVariables(List<String> currentRelatedVariables) {
        this.currentRelatedVariables = currentRelatedVariables;
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
    public void setProjectsMB(ProjectsMB projectsMB) {
        this.projectsMB = projectsMB;
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

    public String getRelatedVariablesFilter() {
        return relatedVariablesFilter;
    }

    public void setRelatedVariablesFilter(String relatedVariablesFilter) {
        this.relatedVariablesFilter = relatedVariablesFilter;
    }

    public List<String> getCurrentVariableExpected() {
        return currentVariableExpected;
    }

    public void setCurrentVariableExpected(List<String> currentVariableExpected) {
        this.currentVariableExpected = currentVariableExpected;
    }
}
