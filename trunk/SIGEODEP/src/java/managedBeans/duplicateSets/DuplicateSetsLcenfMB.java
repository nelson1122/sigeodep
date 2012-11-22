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
                rowDataTableList = new ArrayList<RowDataTable>();
                int idNFI;
                //cargo el registro con el que estoy comparando
                ResultSet resultSet2 = connectionJdbcMB.consult(""
                        + "SELECT "
                        + "   non_fatal_injuries.non_fatal_injury_id "
                        + "FROM "
                        + "   non_fatal_injuries "
                        + "WHERE"
                        + "   non_fatal_injuries.victim_id = " + selectedRowDuplicatedTable.getColumn1() + "");
                resultSet2.next();
                idNFI = Integer.parseInt(resultSet2.getString(1));
                rowDataTableList.add(loadValues("", nonFatalInjuriesFacade.find(idNFI)));

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

                idNFI = -1;
                int cont = 0;
                //cargo los posibles duplicados 

                while (resultSetCount.next()) {
                    resultSet2 = connectionJdbcMB.consult(""
                            + "SELECT "
                            + "   non_fatal_injuries.non_fatal_injury_id "
                            + "FROM "
                            + "   non_fatal_injuries "
                            + "WHERE"
                            + "   non_fatal_injuries.victim_id = " + resultSetCount.getString("victim_id") + "");
                    resultSet2.next();
                    cont++;
                    idNFI = Integer.parseInt(resultSet2.getString(1));
                    rowDataTableList.add(loadValues("", nonFatalInjuriesFacade.find(idNFI)));
                }
                if (idNFI == -1) {
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
            tuplesNumber = tuplesNumber + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
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

    private RowDataTable loadValues(String c, NonFatalInjuries currentNonFatalI) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA

        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        //if (c.length() == 0) {
            newRowDataTable.setColumn1(currentNonFatalI.getNonFatalInjuryId().toString());
        //} else {
        //    newRowDataTable.setColumn1("COMPARADO");
        //}

        //******type_id
        try {
            if (currentNonFatalI.getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalI.getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalI.getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalI.getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalI.getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalI.getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalI.getVictimId().getStranger() != null) {
                if (currentNonFatalI.getVictimId().getStranger()) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }

            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalI.getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalI.getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalI.getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalI.getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalI.getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalI.getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalI.getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalI.getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalI.getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalI.getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalI.getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalI.getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null && (currentNonFatalI.getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalI.getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalI.getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalI.getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalI.getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalI.getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalI.getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalI.getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalI.getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalI.getVictimId().getVulnerableGroupsList().isEmpty()) {
                for (int i = 0; i < currentNonFatalI.getVictimId().getVulnerableGroupsList().size(); i++) {
                    if (1 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        newRowDataTable.setColumn16("SI");//isDisplaced = true;
                    }
                    if (2 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        //isHandicapped = true;
                        newRowDataTable.setColumn17("SI");
                    }
                }
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalI.getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalI.getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalI.getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalI.getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Cual otro grupo etnico
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Cual otro de lugar del hecho
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Cual otra actividad
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual altura
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual polvora
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Cual desastre natural
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Cual otro mecanismo de objeto
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Cual otro animal
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Cual otro factor precipitante(Autoinflingida intencional)
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Cual otro tipo de agresor(intrafamiliar)
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Cual otro tipo de maltrato(intrafamiliar)
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Cual otra relación (violencia interpersonal)
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                    case 13://13.	Cual otro tipo de transporte(transporte)
                        newRowDataTable.setColumn31(othersList.get(i).getValueText());
                        break;
                    case 14://14.	Cual otro tipo de transporte de contraparte(transporte)
                        newRowDataTable.setColumn32(othersList.get(i).getValueText());
                        break;
                    case 15://15.	Cual otro tipo de transporte de usuario(transporte)
                        newRowDataTable.setColumn33(othersList.get(i).getValueText());
                        break;
                    case 16://16.	Cual otro sitio anatomico
                        newRowDataTable.setColumn34(othersList.get(i).getValueText());
                        break;
                    case 17://17.	Cual otra naturaleza de la lesión
                        newRowDataTable.setColumn35(othersList.get(i).getValueText());
                        break;
                    case 18://18.	Cual otro destino del paciente
                        newRowDataTable.setColumn36(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalI.getCheckupDate() != null) {
                newRowDataTable.setColumn37(sdf.format(currentNonFatalI.getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalI.getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalI.getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn38(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalI.getInjuryDate() != null) {
                newRowDataTable.setColumn39(sdf.format(currentNonFatalI.getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalI.getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalI.getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn40(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalI.getInjuryAddress() != null) {
            newRowDataTable.setColumn41(currentNonFatalI.getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalI.getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn42(currentNonFatalI.getInjuryNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalI.getInjuryPlaceId() != null) {
                newRowDataTable.setColumn43(currentNonFatalI.getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalI.getActivityId() != null) {
                newRowDataTable.setColumn44(currentNonFatalI.getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id
        try {
            if (currentNonFatalI.getIntentionalityId() != null) {
                newRowDataTable.setColumn45(currentNonFatalI.getIntentionalityId().getIntentionalityName());
            }
        } catch (Exception e) {
        }
        //******use_alcohol_id
        try {
            if (currentNonFatalI.getUseAlcoholId() != null) {
                newRowDataTable.setColumn46(currentNonFatalI.getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalI.getUseDrugsId() != null) {
                newRowDataTable.setColumn47(currentNonFatalI.getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalI.getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn48(currentNonFatalI.getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalI.getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn49(currentNonFatalI.getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalI.getSubmittedPatient() != null) {
                if (currentNonFatalI.getSubmittedPatient()) {
                    newRowDataTable.setColumn50("SI");
                } else {
                    newRowDataTable.setColumn50("NO");
                }

            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalI.getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn51(currentNonFatalI.getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id
        try {
            if (currentNonFatalI.getDestinationPatientId() != null) {
                newRowDataTable.setColumn52(currentNonFatalI.getDestinationPatientId().getDestinationPatientName());
            }
        } catch (Exception e) {
        }
        //******input_timestamp
        try {
            if (currentNonFatalI.getInputTimestamp() != null) {
                newRowDataTable.setColumn53(sdf2.format(currentNonFatalI.getInputTimestamp()));
            }
        } catch (Exception e) {
        }
        //******health_professional_id
        try {
            if (currentNonFatalI.getHealthProfessionalId() != null) {
                newRowDataTable.setColumn54(currentNonFatalI.getHealthProfessionalId().getHealthProfessionalName());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalI.getMechanismId() != null) {
                newRowDataTable.setColumn55(currentNonFatalI.getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalI.getUserId() != null) {
                newRowDataTable.setColumn56(currentNonFatalI.getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalI.getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn57(currentNonFatalI.getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        try {
            if (currentNonFatalI.getNonFatalDataSourceId() != null) {
                newRowDataTable.setColumn58(currentNonFatalI.getNonFatalDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******injury_id
        try {
            if (currentNonFatalI.getInjuryId() != null) {
                //if (injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()) != null) {
                newRowDataTable.setColumn59(injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()).getInjuryName());
                //}
            }
        } catch (Exception e) {
            System.out.println("Error por" + e.toString());
        }
        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent() != null) {
                    newRowDataTable.setColumn60(currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn61(currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId().getRelationshipVictimName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getContextId() != null) {
                    newRowDataTable.setColumn62(currentNonFatalI.getNonFatalInterpersonal().getContextId().getContextName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn63(currentNonFatalI.getNonFatalInterpersonal().getAggressorGenderId().getGenderName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn66("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn67("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn68("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn69("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn70("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn71("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn72("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn73("SI");
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
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn74("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn75("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn76("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn77("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn78("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn79("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn80("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn81("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {

            if (currentNonFatalI.getAnatomicalLocationsList() != null) {
                List<AnatomicalLocations> anatomicalLocationsList = currentNonFatalI.getAnatomicalLocationsList();
                for (int i = 0; i < anatomicalLocationsList.size(); i++) {
                    int caso = (int) anatomicalLocationsList.get(i).getAnatomicalLocationId();
                    switch (caso) {
                        case 1://isAnatomicalSite1
                            newRowDataTable.setColumn82("SI");
                            break;
                        case 2://isAnatomicalSite1
                            newRowDataTable.setColumn83("SI");
                            break;
                        case 3://isAnatomicalSite1
                            newRowDataTable.setColumn84("SI");
                            break;
                        case 4://isAnatomicalSite1
                            newRowDataTable.setColumn85("SI");
                            break;
                        case 5://isAnatomicalSite1
                            newRowDataTable.setColumn86("SI");
                            break;
                        case 6://isAnatomicalSite1
                            newRowDataTable.setColumn87("SI");
                            break;
                        case 7://isAnatomicalSite1
                            newRowDataTable.setColumn88("SI");
                            break;
                        case 8://isAnatomicalSite1
                            newRowDataTable.setColumn89("SI");
                            break;
                        case 9://isAnatomicalSite1
                            newRowDataTable.setColumn90("SI");
                            break;
                        case 10://isAnatomicalSite1
                            newRowDataTable.setColumn91("SI");
                            break;
                        case 11://isAnatomicalSite1    
                            newRowDataTable.setColumn92("SI");
                            break;
                        case 98://checkOtherPlace  otherAnatomicalPlaceDisabled 
                            newRowDataTable.setColumn93("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo sitios anatomicos"+e.toString());
        }
        //cargo la naturaleza de la lesion
        try {

            if (currentNonFatalI.getKindsOfInjuryList() != null) {
                List<KindsOfInjury> kindsOfInjuryList = currentNonFatalI.getKindsOfInjuryList();
                for (int i = 0; i < kindsOfInjuryList.size(); i++) {
                    int caso = (int) kindsOfInjuryList.get(i).getKindInjuryId();
                    switch (caso) {
                        case 1://isNatureOfInjurye1
                            newRowDataTable.setColumn94("SI");
                            break;
                        case 2://isNatureOfInjurye1
                            newRowDataTable.setColumn95("SI");
                            break;
                        case 3://isNatureOfInjurye1
                            newRowDataTable.setColumn96("SI");
                            break;
                        case 4://isNatureOfInjurye1
                            newRowDataTable.setColumn97("SI");
                            break;
                        case 5://isNatureOfInjurye1
                            newRowDataTable.setColumn98("SI");
                            break;
                        case 6://isNatureOfInjurye1
                            newRowDataTable.setColumn99("SI");
                            break;
                        case 7://isNatureOfInjurye1
                            newRowDataTable.setColumn100("SI");
                            break;
                        case 8://isNatureOfInjurye1
                            newRowDataTable.setColumn101("SI");
                            break;
                        case 9://isNatureOfInjurye1
                            newRowDataTable.setColumn102("SI");
                            break;
                        case 98://checkOtherInjury
                            newRowDataTable.setColumn103("SI");
                            break;
                        case 99:// isUnknownNatureOfInjurye
                            newRowDataTable.setColumn104("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }
        //cargo los diagnosticos
        try {

            if (currentNonFatalI.getDiagnosesList() != null) {
                List<Diagnoses> diagnosesList = currentNonFatalI.getDiagnosesList();
                for (int i = 0; i < diagnosesList.size(); i++) {
                    switch (i) {
                        case 0:
                            newRowDataTable.setColumn105(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 1:
                            newRowDataTable.setColumn106(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 2:
                            newRowDataTable.setColumn107(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 3:
                            newRowDataTable.setColumn108(diagnosesList.get(i).getDiagnosisName());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo codigo CIE"+e.toString());
        }

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------

        try {

            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt() != null) {
                    newRowDataTable.setColumn109(currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent() != null) {
                    newRowDataTable.setColumn110(currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId() != null) {
                    newRowDataTable.setColumn111(currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId().getPrecipitatingFactorName());
                }
            }
        } catch (Exception e) {
        }

        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportTypeId() != null) {
                    newRowDataTable.setColumn112(currentNonFatalI.getNonFatalTransport().getTransportTypeId().getTransportTypeName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportCounterpartId() != null) {
                    newRowDataTable.setColumn113(currentNonFatalI.getNonFatalTransport().getTransportCounterpartId().getTransportCounterpartName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportUserId() != null) {
                    newRowDataTable.setColumn114(currentNonFatalI.getNonFatalTransport().getTransportUserId().getTransportUserName());
                }
            }
        } catch (Exception e) {
        }


        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getSecurityElementsList() != null) {
                    List<SecurityElements> securityElementsList = currentNonFatalI.getNonFatalTransport().getSecurityElementsList();
                    for (int i = 0; i < securityElementsList.size(); i++) {
                        switch (securityElementsList.get(i).getSecurityElementId()) {
                            case 1://isBeltUse
                                newRowDataTable.setColumn115("SI");
                                break;
                            case 2://isHelmetUse
                                newRowDataTable.setColumn116("SI");
                                break;
                            case 3://isBicycleHelmetUse
                                newRowDataTable.setColumn117("SI");
                                break;
                            case 4://isVestUse
                                newRowDataTable.setColumn118("SI");
                                break;
                            case 5://isOtherElementUse                        
                                break;
                            case 6://currentSecurityElements  "NO";
                                break;
                            case 7://currentSecurityElements = "NO SE SABE";
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo elementos de seguridad"+e.toString());
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
