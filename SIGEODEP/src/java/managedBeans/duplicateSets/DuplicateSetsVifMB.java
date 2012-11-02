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
import managedBeans.forms.VIFMB;
import managedBeans.recordSets.RecordSetsMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "duplicateSetsVifMB")
@SessionScoped
public class DuplicateSetsVifMB implements Serializable {

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
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */

    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public DuplicateSetsVifMB() {
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
                rowDataTableList = new ArrayList<RowDataTable>();
                int id;
                //cargo el registro con el que estoy comparando
                ResultSet resultSet2 = connectionJdbcMB.consult(""
                        + "SELECT "
                        + "   non_fatal_injuries.non_fatal_injury_id "
                        + "FROM "
                        + "   non_fatal_injuries "
                        + "WHERE"
                        + "   non_fatal_injuries.victim_id = " + selectedRowDuplicatedTable.getColumn1() + "");
                resultSet2.next();
                id = Integer.parseInt(resultSet2.getString(1));
                rowDataTableList.add(loadValues("", nonFatalDomesticViolenceFacade.find(id)));


                String sql = "";
                sql = sql + "SELECT ";
                sql = sql + "t1.victim_id ";
                sql = sql + "FROM ";
                sql = sql + "duplicate t1, duplicate t2 ";
                sql = sql + "WHERE ";
                sql = sql + "t2.victim_id = " + selectedRowDuplicatedTable.getColumn1() + " ";
                sql = sql + "AND t1.victim_id != t2.victim_id ";
                sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 6 ";
                sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 6 ";
                ResultSet resultSetCount = connectionJdbcMB.consult(sql);


                id = -1;
                int cont = 0;
                //cargo los posibles duplicados 

                while (resultSetCount.next()) {
                    resultSet2 = connectionJdbcMB.consult(""
                            + "SELECT "
                            + "   non_fatal_injuries.fatal_injury_id "
                            + "FROM "
                            + "   non_fatal_injuries "
                            + "WHERE"
                            + "   non_fatal_injuries.victim_id = " + resultSetCount.getString("victim_id") + "");
                    resultSet2.next();
                    cont++;
                    id = Integer.parseInt(resultSet2.getString(1));
                    rowDataTableList.add(loadValues("", nonFatalDomesticViolenceFacade.find(id)));
                }
                if (id == -1) {
                    printMessage(FacesMessage.SEVERITY_WARN, "Sin datos", "La búsqueda no produjo resultados");
                } else {
                    printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se encontraron " + String.valueOf(cont) + " posibles duplicados");
                }
                selectedRowDataTable = null;
            } catch (SQLException ex) {
                //Logger.getLogger(DuplicateRecordsMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



    }

    public void loadDuplicatedList() {
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
            String sql = "DROP VIEW IF EXISTS duplicate";
            connectionJdbcMB.non_query(sql);
            sql = "create view duplicate as "
                    + "SELECT "
                    + "   * "
                    + "FROM "
                    + "   victims "
                    + "WHERE ";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == 0) {
                    sql = sql + " tag_id = " + tagsList.get(i).getTagId().toString() + " ";
                } else {
                    sql = sql + " OR tag_id = " + tagsList.get(i).getTagId().toString() + " ";
                }
            }
            connectionJdbcMB.non_query(sql);
            rowDuplicatedTableList = new ArrayList<RowDataTable>();
            sql = "Select * from duplicate";
            ResultSet resultSetFileData = connectionJdbcMB.consult(sql);
            ArrayList<String> addedRecords = new ArrayList<String>();;
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
                    sql = sql + "AND levenshtein(t1.victim_nid, t2.victim_nid) < 6 ";
                    sql = sql + "AND levenshtein(t1.victim_name, t2.victim_name) < 6 ";
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
            }
            selectedRowDataTable = null;
        } catch (SQLException ex) {
            //Logger.getLogger(DuplicateRecordsMB.class.getName()).log(Level.SEVERE, null, ex);
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
        int totalRegisters = 0;
        int totalProcess = 0;

        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<Tags>();
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            data = data + selectedRowsDataTableTags[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
        }
        //DETERMINO EL NUMERO DE REGISTROS 
        for (int i = 0; i < tagsList.size(); i++) {
            totalRegisters = totalRegisters + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
        }
        loadDuplicatedList();
    }

    private RowDataTable loadValues(String c, NonFatalDomesticViolence currentNonFatalDomesticV) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA

        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalInjuryId().toString());
        //******type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger() != null) {
                newRowDataTable.setColumn5(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger().toString());
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().isEmpty()) {
                newRowDataTable.setColumn16(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().get(0).getVulnerableGroupName());
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Otro lugar del hecho
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Otra actividad
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Altura
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual polvora			
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual desastre natural
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Otro mecanismo objeto
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Animal cual
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Otro grupo étnico
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Otro grupo vulnerable
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Otro tipo de agresor
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Otro tipo de maltrato
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Otro tipo de agresion
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate() != null) {
                newRowDataTable.setColumn31(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn32(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn33(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn34(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn35(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn36(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn37(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getActivityId() != null) {
                newRowDataTable.setColumn38(currentNonFatalDomesticV.getNonFatalInjuries().getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id        
        //******use_alcohol_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId() != null) {
                newRowDataTable.setColumn39(currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId() != null) {
                newRowDataTable.setColumn40(currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn41(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn42(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient() != null) {
                newRowDataTable.setColumn43(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient().toString());
            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn44(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id        
        //******input_timestamp
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp() != null) {
                newRowDataTable.setColumn45(sdf2.format(currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp()));
            }
        } catch (Exception e) {
        }
        //******health_professional_id        
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId() != null) {
                newRowDataTable.setColumn46(currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUserId() != null) {
                newRowDataTable.setColumn47(currentNonFatalDomesticV.getNonFatalInjuries().getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn48(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id        
        //******injury_id        

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalDomesticV.getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn49("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn50("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn51("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn52("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn53("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn54("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn55("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn56("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn57("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn58("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo violencia intrafamiliar"+e.toString());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalDomesticV.getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn59("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn60("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn61("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn62("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn63("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn66("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de acciones a realizar
        try {
            if (currentNonFatalDomesticV.getActionsToTakeList() != null) {
                List<ActionsToTake> actionsToTakesList = currentNonFatalDomesticV.getActionsToTakeList();
                for (int i = 0; i < actionsToTakesList.size(); i++) {
                    int caso = (int) actionsToTakesList.get(i).getActionId();
                    switch (caso) {
                        case 1://AR1 Conciliación
                            newRowDataTable.setColumn67("SI");
                            break;
                        case 2://AR2 Caución
                            newRowDataTable.setColumn68("SI");
                            break;
                        case 3://AR3 Dictamén Medicina Legal
                            newRowDataTable.setColumn69("SI");
                            break;
                        case 4://AR4 Remisión a Fiscalía
                            newRowDataTable.setColumn70("SI");
                            break;
                        case 5://AR5 Remisión a Medicina Legal
                            newRowDataTable.setColumn71("SI");
                            break;
                        case 6://AR6 Remisión a Comisaría de Familia
                            newRowDataTable.setColumn72("SI");
                            break;
                        case 7://AR7 Remisión a ICBF
                            newRowDataTable.setColumn73("SI");
                            break;
                        case 8://AR8 Medidas de protección
                            newRowDataTable.setColumn74("SI");
                            break;
                        case 9://AR9 Remisión a salud
                            newRowDataTable.setColumn75("SI");
                            break;
                        case 10://AR10 Atención psicosocial
                            newRowDataTable.setColumn76("SI");
                            break;
                        case 11://AR11 Restablecimiento de derechos
                            newRowDataTable.setColumn77("SI");
                            break;
                        case 12://AR12 Otro, cual?
                            newRowDataTable.setColumn78("SI");
                            break;
                        case 13://AR13 Sin dato
                            newRowDataTable.setColumn79("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }

        if (currentNonFatalDomesticV.getDomesticViolenceDataSourceId() != null) {
            newRowDataTable.setColumn80(currentNonFatalDomesticV.getDomesticViolenceDataSourceId().getDomesticViolenceDataSourcesName());
        }

        return newRowDataTable;
    }

    public void rowDuplicatedTableListSelect() {
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
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
            List<NonFatalInjuries> nonFatalInjuriesList = new ArrayList<NonFatalInjuries>();
            nonFatalInjuriesList.add(nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1())));
            if (nonFatalInjuriesList != null) {
                for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
                    if (nonFatalInjuriesList.get(j).getNonFatalDomesticViolence() != null) {
                        nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesList.get(j).getNonFatalDomesticViolence());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalInterpersonal() != null) {
                        nonFatalInterpersonalFacade.remove(nonFatalInjuriesList.get(j).getNonFatalInterpersonal());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalSelfInflicted() != null) {
                        nonFatalSelfInflictedFacade.remove(nonFatalInjuriesList.get(j).getNonFatalSelfInflicted());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalTransport() != null) {
                        nonFatalTransportFacade.remove(nonFatalInjuriesList.get(j).getNonFatalTransport());
                    }
                    nonFatalInjuriesFacade.remove(nonFatalInjuriesList.get(j));
                    victimsFacade.remove(nonFatalInjuriesList.get(j).getVictimId());
                    //----------------------------------------------------------
                }
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
}
