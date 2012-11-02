/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.duplicateSets;

import beans.connection.ConnectionJdbcMB;
import managedBeans.recordSets.*;
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
import managedBeans.forms.TransitMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "duplicateSetsTransitMB")
@SessionScoped
public class DuplicateSetsTransitMB implements Serializable {

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
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    CountriesFacade countriesFacade;
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
    
    public DuplicateSetsTransitMB() {
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
                        + "   fatal_injuries.fatal_injury_id "
                        + "FROM "
                        + "   fatal_injuries "
                        + "WHERE"
                        + "   fatal_injuries.victim_id = " + selectedRowDuplicatedTable.getColumn1() + "");
                resultSet2.next();
                id = Integer.parseInt(resultSet2.getString(1));
                rowDataTableList.add(loadValues("", fatalInjuryTrafficFacade.find(id)));


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
                            + "   fatal_injuries.fatal_injury_id "
                            + "FROM "
                            + "   fatal_injuries "
                            + "WHERE"
                            + "   fatal_injuries.victim_id = " + resultSetCount.getString("victim_id") + "");
                    resultSet2.next();
                    cont++;
                    id = Integer.parseInt(resultSet2.getString(1));
                    rowDataTableList.add(loadValues("", fatalInjuryTrafficFacade.find(id)));
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
            totalRegisters = totalRegisters + fatalInjuryTrafficFacade.countFromTag(tagsList.get(i).getTagId());
        }
        loadDuplicatedList();
    }

    private RowDataTable loadValues(String c, FatalInjuryTraffic currentFatalInjuryT) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA

        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryT.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger() != null) {
                newRowDataTable.setColumn5(currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger().toString());
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }


        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address


        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       

        //******residence_municipality
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryT.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryT.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryT.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryT.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryT.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryT.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryT.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryT.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryT.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
        //------------------------------------------------------------
        //cargar vehiculos de la contraparte
        List<CounterpartInvolvedVehicle> involvedVehiclesList = currentFatalInjuryT.getFatalInjuries().getCounterpartInvolvedVehicleList();
        if (involvedVehiclesList != null) {
            for (int i = 0; i < involvedVehiclesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn28(involvedVehiclesList.get(0).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn29(involvedVehiclesList.get(1).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn30(involvedVehiclesList.get(2).getInvolvedVehicleId().getInvolvedVehicleName());
                }
            }
        }

        //cargar tipo de servcio de la contraparte
        List<CounterpartServiceType> serviceTypesList = currentFatalInjuryT.getFatalInjuries().getCounterpartServiceTypeList();

        if (involvedVehiclesList != null) {
            for (int i = 0; i < serviceTypesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn31(serviceTypesList.get(0).getServiceTypeId().getServiceTypeName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn32(serviceTypesList.get(1).getServiceTypeId().getServiceTypeName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn33(serviceTypesList.get(2).getServiceTypeId().getServiceTypeName());
                }
            }
        }


        //******number_non_fatal_victims
        if (currentFatalInjuryT.getNumberNonFatalVictims() != null) {
            newRowDataTable.setColumn34(currentFatalInjuryT.getNumberNonFatalVictims().toString());
        }
        //******victim_characteristic_id
        if (currentFatalInjuryT.getVictimCharacteristicId() != null) {
            newRowDataTable.setColumn35(currentFatalInjuryT.getVictimCharacteristicId().getCharacteristicName());
        }
        //******protection_measure_id
        if (currentFatalInjuryT.getProtectionMeasureId() != null) {
            newRowDataTable.setColumn36(currentFatalInjuryT.getProtectionMeasureId().getProtectiveMeasuresName());
        }
        //******road_type_id
        if (currentFatalInjuryT.getRoadTypeId() != null) {
            newRowDataTable.setColumn37(currentFatalInjuryT.getRoadTypeId().getRoadTypeName());
        }
        //******accident_class_id
        if (currentFatalInjuryT.getAccidentClassId() != null) {
            newRowDataTable.setColumn38(currentFatalInjuryT.getAccidentClassId().getAccidentClassName());
        }
        //******involved_vehicle_id
        if (currentFatalInjuryT.getInvolvedVehicleId() != null) {
            newRowDataTable.setColumn39(currentFatalInjuryT.getInvolvedVehicleId().getInvolvedVehicleName());
        }
        //******service_type_id
        if (currentFatalInjuryT.getServiceTypeId() != null) {
            newRowDataTable.setColumn40(currentFatalInjuryT.getServiceTypeId().getServiceTypeName());
        }

        //******alcohol_level_counterpart
        try {
            if (currentFatalInjuryT.getAlcoholLevelCounterpart() != null) {
                newRowDataTable.setColumn41(currentFatalInjuryT.getAlcoholLevelCounterpart().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_counterpart_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn42(currentFatalInjuryT.getAlcoholLevelCounterpartId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id

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
//        if (selectedRowDataTable != null) {
//            List<NonFatalInjuries> nonFatalInjuriesList = new ArrayList<NonFatalInjuries>();
//            nonFatalInjuriesList.add(nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1())));
//            if (nonFatalInjuriesList != null) {
//                for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
//                    if (nonFatalInjuriesList.get(j).getNonFatalDomesticViolence() != null) {
//                        nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesList.get(j).getNonFatalDomesticViolence());
//                    }
//                    if (nonFatalInjuriesList.get(j).getNonFatalInterpersonal() != null) {
//                        nonFatalInterpersonalFacade.remove(nonFatalInjuriesList.get(j).getNonFatalInterpersonal());
//                    }
//                    if (nonFatalInjuriesList.get(j).getNonFatalSelfInflicted() != null) {
//                        nonFatalSelfInflictedFacade.remove(nonFatalInjuriesList.get(j).getNonFatalSelfInflicted());
//                    }
//                    if (nonFatalInjuriesList.get(j).getNonFatalTransport() != null) {
//                        nonFatalTransportFacade.remove(nonFatalInjuriesList.get(j).getNonFatalTransport());
//                    }
//                    nonFatalInjuriesFacade.remove(nonFatalInjuriesList.get(j));
//                    victimsFacade.remove(nonFatalInjuriesList.get(j).getVictimId());
//                    //----------------------------------------------------------
//                }
//                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
//            } else {
//                printMessage(FacesMessage.SEVERITY_WARN, "Alerta", "El registro seleccionado es quien se esta comparando, por tanto no se puede eliminar");
//            }
//            //quito los elementos seleccionados de rowsDataTableList seleccion de 
//            for (int i = 0; i < rowDataTableList.size(); i++) {
//                if (selectedRowDataTable.getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
//                    rowDataTableList.remove(i);
//                    break;
//                }
//            }
//            //deselecciono los controles
//            selectedRowDataTable = null;
//            btnRemoveDisabled = true;
//
//        }
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
