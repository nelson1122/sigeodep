/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.duplicateSets;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.recordSets.RecordSetsMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "duplicateSetsLcenfMB")
@SessionScoped
public class DuplicateSetsLcenfMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    //private NonFatalInjuries currentNonFatalInjury;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    private List<RowDataTable> rowDataTableList;
    private List<RowDataTable> rowDuplicatedTableList;
    private RowDataTable selectedRowDataTable;
    private RowDataTable selectedRowDuplicatedTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnViewDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private String openForm = "";
    private RecordSetsMB recordSetsMB;
    ConnectionJdbcMB connectionJdbcMB;
    private int tuplesNumber = 0;
    private int tuplesProcessed = 0;
    private String initialDateStr = "";
    private String endDateStr = "";
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */

    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public DuplicateSetsLcenfMB() {
    }

    public String openForm() {
        return openForm;
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void loadDuplicatedRecords() {
        /*
         * saca la lista con todos lo s campos de los registros que pueden ser
         * duplicados de un
         */
        if (selectedRowDuplicatedTable != null) {
            try {
                rowDataTableList = new ArrayList<>();
                RowDataTable newRow = connectionJdbcMB.loadNonFatalInjuryRecord(selectedRowDuplicatedTable.getColumn1());
                if (newRow == null) {
                    printMessage(FacesMessage.SEVERITY_WARN, "Registro eliminado", "Se ha eliminado el registro con el cual se estaba comparando");
                } else {
                    rowDataTableList.add(newRow);
                    String sql = "";
                    sql = sql + "SELECT ";
                    sql = sql + "t1.victim_id ";
                    sql = sql + "FROM ";
                    sql = sql + "duplicate t1, duplicate t2 ";
                    sql = sql + "WHERE ";
                    sql = sql + "t2.victim_id = " + selectedRowDuplicatedTable.getColumn1() + " ";
                    sql = sql + "AND t1.victim_id != t2.victim_id ";
                    sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 4 ";
                    sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 4 ";
                    ResultSet resultSetCount = connectionJdbcMB.consult(sql);
                    int cont = 0;

                    while (resultSetCount.next()) {
//                    resultSet2 = connectionJdbcMB.consult(""
//                            + "SELECT "
//                            + "   non_fatal_injuries.non_fatal_injury_id "
//                            + "FROM "
//                            + "   non_fatal_injuries "
//                            + "WHERE"
//                            + "   non_fatal_injuries.victim_id = " + resultSetCount.getString("victim_id") + "");
//                    resultSet2.next();
                        cont++;
                        rowDataTableList.add(connectionJdbcMB.loadNonFatalInjuryRecord(resultSetCount.getString("victim_id")));
//                    idNFI = Integer.parseInt(resultSet2.getString(1));
//                    rowDataTableList.add(loadValues("", nonFatalInjuriesFacade.find(idNFI)));
                    }
                    if (cont == 0) {
                        printMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "Este registro ya no tiene posibles duplicados");
                    } else {
                        printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se encontraron " + String.valueOf(cont) + " posibles duplicados");
                    }
                    selectedRowDataTable = null;
                }
            } catch (SQLException ex) {
                //Logger.getLogger(DuplicateRecordsMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



    }

    public void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        /*
         * se llama a esta funcion desde record sets cuando se presiona el boton
         * "registros duplicados"
         */
        FacesContext context = FacesContext.getCurrentInstance();
        recordSetsMB = (RecordSetsMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsMB}", RecordSetsMB.class);
        recordSetsMB.setProgress(0);
        tuplesNumber = 0;
        tuplesProcessed = 0;

        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<Tags>();
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            data = data + selectedRowsDataTableTags[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
            //tuplesNumber = tuplesNumber + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
        }
        selectedRowDuplicatedTable = null;
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        rowDuplicatedTableList = new ArrayList<RowDataTable>();
        btnViewDisabled = true;
        btnRemoveDisabled = true;

        /*
         * saca una lista con el nombre, identificacion y numero registros que
         * posiblemente son duplicados
         */
        try {
//            sql = "create view duplicate as "
//                    + "SELECT "
//                    + "   * "
//                    + "FROM "
//                    + "   victims "
//                    + "WHERE ";
//            for (int i = 0; i < tagsList.size(); i++) {
//                if (i == 0) {
//                    sql = sql + " tag_id = " + tagsList.get(i).getTagId().toString() + " ";
//                } else {
//                    sql = sql + " OR tag_id = " + tagsList.get(i).getTagId().toString() + " ";
//                }
//            }
            connectionJdbcMB.non_query("DROP TABLE IF EXISTS duplicate");
            String sql = ""
                    + "create TABLE duplicate as \n"
                    + "   SELECT \n"
                    + "      victims.victim_id, \n"
                    + "      victims.victim_nid, \n"
                    + "      victims.victim_name \n"
                    + "   FROM \n"
                    + "      public.victims, \n"
                    + "      public.non_fatal_injuries \n"
                    + "   WHERE  \n"
                    + "      non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == 0) {
                    sql = sql + "     victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                } else {
                    sql = sql + "     OR victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                }
            }
            //limitar rango de fecha
            sql = sql + "    ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + "    non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            //System.out.println("SQL001: \n" + sql);
            connectionJdbcMB.non_query(sql);
            //CUENTO EL NUMERO DE REGISTROS EN LA CONSULTA---------------------------
            tuplesNumber = 0;
            sql = ""
                    + "   SELECT \n"
                    + "      count(*) \n"
                    + "   FROM \n"
                    + "      public.victims, \n"
                    + "      public.non_fatal_injuries \n"
                    + "   WHERE  \n"
                    + "      non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == 0) {
                    sql = sql + "     victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                } else {
                    sql = sql + "     OR victims.tag_id = " + tagsList.get(i).getTagId().toString() + " \n";
                }
            }
            sql = sql + "    ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + "    non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            ResultSet rs = connectionJdbcMB.consult(sql);
            if (rs.next()) {
                tuplesNumber = rs.getInt(1);
            }
            //------------


            rowDuplicatedTableList = new ArrayList<RowDataTable>();

            ResultSet resultSetFileData = connectionJdbcMB.consult("Select * from duplicate");
            ArrayList<String> addedRecords = new ArrayList<String>();
            boolean first;
            boolean found;
            int countRegisters;
            while (resultSetFileData.next()) {
                //contamos el numero de registros que pueden ser posibles repeticiones
                //si supera la validacion se agregamos a la lista
                found = false;
                for (int i = 0; i < addedRecords.size(); i++) {//saber si ya fue evaluado
                    if (resultSetFileData.getString("victim_id").compareTo(addedRecords.get(i)) == 0) {
                        found = true;
                    }
                }
                if (!found) {//el elemento no ha sido evaluado ni adicionado
                    sql = "";
                    sql = sql + "SELECT ";
                    sql = sql + "t1.victim_id ";
                    sql = sql + "FROM ";
                    sql = sql + "duplicate t1, duplicate t2 ";
                    sql = sql + "WHERE ";
                    sql = sql + "t2.victim_id = " + resultSetFileData.getString("victim_id") + " ";
                    sql = sql + "AND t1.victim_id != t2.victim_id ";
                    sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 4 ";
                    sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 4 ";
                    ResultSet resultSetCount = connectionJdbcMB.consult(sql);
                    first = true;
                    countRegisters = 0;
                    while (resultSetCount.next()) {
                        countRegisters++;
                        if (first) {
                            addedRecords.add(resultSetFileData.getString("victim_id"));
                            first = false;
                        }
                        addedRecords.add(resultSetCount.getString("victim_id"));
                    }
                    if (countRegisters != 0) {//adiciono el registro a la tabla
                        rowDuplicatedTableList.add(new RowDataTable(
                                resultSetFileData.getString("victim_id"),
                                resultSetFileData.getString("victim_nid"),
                                resultSetFileData.getString("victim_name"),
                                String.valueOf(countRegisters)));
                    }
                }
                tuplesProcessed++;
                recordSetsMB.setProgress((int) (tuplesProcessed * 100) / tuplesNumber);
                System.out.println(recordSetsMB.getProgress());
            }
            recordSetsMB.setProgress(100);
            selectedRowDataTable = null;
        } catch (SQLException ex) {
            recordSetsMB.setProgress(100);
            System.out.println("Error: " + ex.toString());
        }
    }

    public void rowDuplicatedTableListSelect() {
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<>();
        btnViewDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowDuplicatedTable != null) {
            loadDuplicatedRecords();
        }
    }

    public void rowDataTableListSelect() {
        //currentNonFatalInjury = null;
        btnRemoveDisabled = true;
        if (selectedRowDataTable != null) {
            if (selectedRowDataTable.getColumn1().compareTo("COMPARADO") != 0) {
                btnRemoveDisabled = false;
            }
            //currentNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
    }

    public void deleteRegistry() {
        if (selectedRowDataTable != null) {
            NonFatalInjuries nonFatalInjuriesSelect = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (nonFatalInjuriesSelect != null) {
                if (nonFatalInjuriesSelect.getNonFatalDomesticViolence() != null) {
                    nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesSelect.getNonFatalDomesticViolence());
                }
                if (nonFatalInjuriesSelect.getNonFatalInterpersonal() != null) {
                    nonFatalInterpersonalFacade.remove(nonFatalInjuriesSelect.getNonFatalInterpersonal());
                }
                if (nonFatalInjuriesSelect.getNonFatalSelfInflicted() != null) {
                    nonFatalSelfInflictedFacade.remove(nonFatalInjuriesSelect.getNonFatalSelfInflicted());
                }
                if (nonFatalInjuriesSelect.getNonFatalTransport() != null) {
                    nonFatalTransportFacade.remove(nonFatalInjuriesSelect.getNonFatalTransport());
                }
                nonFatalInjuriesFacade.remove(nonFatalInjuriesSelect);
                victimsFacade.remove(nonFatalInjuriesSelect.getVictimId());
                //----------------------------------------------------------

                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
            } else {
                printMessage(FacesMessage.SEVERITY_WARN, "Alerta", "El registro seleccionado es quien se esta comparando, por tanto no se puede eliminar");
            }
            //quito los elementos seleccionados de rowsDataTableList seleccion de 
            for (int i = 0; i < rowDataTableList.size(); i++) {
                if (selectedRowDataTable.getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
                    rowDataTableList.remove(i);
                    break;
                }
            }
            //deselecciono los controles
            selectedRowDataTable = null;
            btnRemoveDisabled = true;
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public List<RowDataTable> getRowDuplicatedTableList() {
        return rowDuplicatedTableList;
    }

    public void setRowDuplicatedTableList(List<RowDataTable> rowDuplicatedTableList) {
        this.rowDuplicatedTableList = rowDuplicatedTableList;
    }

    public RowDataTable getSelectedRowDuplicatedTable() {
        return selectedRowDuplicatedTable;
    }

    public void setSelectedRowDuplicatedTable(RowDataTable selectedRowDuplicatedTable) {
        this.selectedRowDuplicatedTable = selectedRowDuplicatedTable;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public boolean isBtnViewDisabled() {
        return btnViewDisabled;
    }

    public void setBtnViewDisabled(boolean btnViewDisabled) {
        this.btnViewDisabled = btnViewDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }
}
