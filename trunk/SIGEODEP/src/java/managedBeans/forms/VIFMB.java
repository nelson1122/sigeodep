/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.login.LoginMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "vifMB")
@SessionScoped
public class VIFMB implements Serializable {

    //------------------    
    @EJB
    TagsFacade tagsFacade;
    private SelectItem[] tags;
    private int currentTag;
    //------------------------
    @EJB
    InsuranceFacade insuranceFacade;
    private Short currentInsurance = 0;
    private SelectItem[] insurances;
    //------------------------
    @EJB
    DomesticViolenceDataSourcesFacade domesticViolenceDataSourcesFacade;
    private Short currentDomesticViolenceDataSource = 1;
    private SelectItem[] violenceDataSources;
    //------------------------
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    private Short currentVulnerableGroup = 0;
    private SelectItem[] vulnerableGroups;
    private boolean otherVulnerableGroupDisabled = true;
    private String otherVulnerableGroup = "";
    //------------------------
    @EJB
    DepartamentsFacade departamentsFacade;
    private Short currentDepartamentHome = 52;//nariño
    private SelectItem[] departaments;
    private boolean currentDepartamentHomeDisabled = false;
    //------------------------
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    private boolean currentMunicipalitieDisabled = false;
    //------------------------
    @EJB
    UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    private Short currentUseAlcohol = 0;
    private Short currentUseDrugs = 0;
    private SelectItem[] useAlcohol;
    private SelectItem[] useDrugs;
    //------------------------
    @EJB
    NonFatalDataSourcesFromWhereFacade nonFatalDataSourcesFromWhereFacade;
    private Short currentFromWhere = 0;
    private SelectItem[] fromWhereList;
    //------------------------
    @EJB
    NonFatalPlacesFacade nonFatalPlacesFacade;
    private Short currentPlace = 0;
    private SelectItem[] places;
    //------------------------
    @EJB
    ActivitiesFacade activitiesFacade;
    private Short currentActivities = 0;
    private SelectItem[] activities;
    //------------------------
    @EJB
    MechanismsFacade mechanismsFacade;
    private Short currentMechanisms = 0;
    private SelectItem[] mechanisms;
    //------------------------
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    private Short currentEthnicGroup = 0;
    private SelectItem[] ethnicGroups;
    private String otherEthnicGroup = "";
    private boolean otherEthnicGroupDisabled = true;
    //------------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender = 0;
    private SelectItem[] genders;
    //------------------------
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob = 0;
    private SelectItem[] jobs;
    //------------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome = "";
    private String currentNeighborhoodHomeCode = "";
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    boolean neighborhoodHomeNameDisabled = false;
    //------------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identifications;
    private Short currentIdentification = 0;
    //------------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge = 0;
    private String currentAge = "";
    private boolean valueAgeDisabled = true;
    //------------------------
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    //@EJB
    //UsersFacade usersFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    @EJB
    AggressorTypesFacade aggressorTypesFacade;
    @EJB
    AbuseTypesFacade abuseTypesFacade;
    @EJB
    ActionsToTakeFacade actionsToTakeFacade;
    @EJB
    OthersFacade othersFacade;
    //------------------
    //------------------
    //@EJB
    //StateTimeFacade stateTimeFacade;
    //@EJB
    //StateDateFacade stateDateFacade;
    //private SelectItem[] stateDateList;
    //private SelectItem[] stateTimeList;
    //private Short currentStateDateEvent = 1;
    //private Short currentStateTimeEvent = 1;
    private boolean strangerDisabled = true;
    private boolean currentDayEventDisabled = false;
    private boolean currentMonthEventDisabled = false;
    private boolean currentYearEventDisabled = false;
    private boolean currentHourEventDisabled = false;
    private boolean currentMinuteEventDisabled = false;
    private boolean currentAmPmEventDisabled = false;
//    private Short currentStateDateConsult = 1;
//    private Short currentStateTimeConsult = 1;
    private boolean currentDayConsultDisabled = false;
    private boolean currentMonthConsultDisabled = false;
    private boolean currentYearConsultDisabled = false;
    private boolean currentHourConsultDisabled = false;
    private boolean currentMinuteConsultDisabled = false;
    private boolean currentAmPmConsultDisabled = false;
    private boolean stranger = false;
    //-------------------------------------
    private boolean isSubmitted = false;
    private boolean fromWhereDisabled = true;
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------    
    private boolean identificationNumberDisabled = true;
    private boolean otherPlaceDisabled = true;
    private boolean otherActivityDisabled = true;
    private boolean otherMechanismDisabled = true;//otro mecanismo  
    private boolean otherAGDisabled = true;
    private String otherAG = "";
    private boolean otherMADisabled = true;
    private String otherMA = "";
    private boolean otherActionDisabled = true;
    private String otherAction = "";
    private String otherMechanism = "";//otro mecanismo       
    private boolean powderWhichDisabled = true;//cual polvora
    private String powderWhich = "";//cual polvora
    private boolean heightWhichDisabled = true;//cual altura
    private String heightWhich = "";//cual altura    
    private String forBurned = "none";//para los quemados
    private String currentDayEvent = "";
    private String currentMonthEvent = "";
    private String currentYearEvent = "";
    private String currentDateEvent = "";
    private String currentWeekdayEvent = "";
    private String currentHourEvent = "";
    private String currentMinuteEvent = "";
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent = "";
    private String currentDayConsult = "";
    private String currentMonthConsult = "";
    private String currentYearConsult = "";
    private String currentDateConsult = "";
    private String currentWeekdayConsult = "";
    private String currentHourConsult = "";
    private String currentMinuteConsult = "";
    private String currentAmPmConsult = "AM";
    private String currentMilitaryHourConsult = "";
    private String currentName = "";
    private String currentIdentificationNumber = "";
    private String currentDirectionHome = "";
    private boolean directionHomeDisabled = true;
    private String currentTelephoneHome = "";
    private String currentDirectionEvent = "";
    private String currentOtherPlace = "";
    private String currentOtherActivitie = "";
    //private String currentSurname = "";
    private Short currentLevelBurned = 0;
    private String currentPercentBurned = "";
    private String currentResponsible = "";
    private boolean isAG1 = false;
    private boolean isAG2 = false;
    private boolean isAG3 = false;
    private boolean isAG4 = false;
    private boolean isAG5 = false;
    private boolean isAG6 = false;
    private boolean isAG7 = false;
    private boolean isAG8 = false;
    private boolean isUnknownAG = false;
    private boolean isAG10 = false;
    private boolean isMA1 = false;
    private boolean isMA2 = false;
    private boolean isMA3 = false;
    private boolean isMA4 = false;
    private boolean isMA5 = false;
    private boolean isMA6 = false;
    private boolean isUnknownMA = false;
    private boolean isMA8 = false;
    private boolean isAction1 = false;
    private boolean isAction2 = false;
    private boolean isAction3 = false;
    private boolean isAction4 = false;
    private boolean isAction5 = false;
    private boolean isAction6 = false;
    private boolean isAction7 = false;
    private boolean isAction8 = false;
    private boolean isAction9 = false;
    private boolean isAction10 = false;
    private boolean isAction11 = false;
    private boolean isAction12 = false;
    private boolean isUnknownAction = false;
    private boolean loading = false;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private boolean save = true;//variable que me dice si el registro esta guadado o no    
    private int currentNonFatalInjuriId = -1;//registro actual 
    private NonFatalDomesticViolence currentNonFatalDomesticViolence;
    private NonFatalDomesticViolence auxFatalDomesticViolence;
    private ArrayList<String> validationsErrors;
    private String currentPosition = "";
    private int totalRegisters = 0;//cantidad total de registros en transito
    private String openDialogFirst = "";
    private String openDialogNext = "";
    private String openDialogLast = "";
    private String openDialogPrevious = "";
    private String openDialogNew = "";
    private String openDialogDelete = "";
    private Calendar c = Calendar.getInstance();
    private String stylePosition = "color: #1471B1;";
    private String currentIdForm = "";
    private Users currentUser;
    ConnectionJdbcMB connectionJdbcMB;
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */

    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public VIFMB() {
    }

    public void loadValues(List<Tags> tagsList, NonFatalDomesticViolence currentNonDomesticV) {
        for (int i = 0; i < tagsList.size(); i++) {
            try {
                reset();
                clearForm();
                currentTag = tagsList.get(i).getTagId();
                this.currentNonFatalDomesticViolence = currentNonDomesticV;
                currentNonFatalInjuriId = currentNonDomesticV.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            } catch (Exception e) {
                reset();
                noSaveAndGoNew();
            }
        }
    }

    public void reset() {

        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        LoginMB loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        currentUser = loginMB.getCurrentUser();
        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        loading = true;
        save = true;
        stylePosition = "color: #1471B1;";
        try {
            //cargo los conjuntos de registros
            List<Tags> tagsList = tagsFacade.findAll();
            int count = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                    count++;
                }
            }
            tags = new SelectItem[count];
            count = 0;
            currentTag = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                    if (currentTag == 0) {
                        currentTag = tagsList.get(i).getTagId();
                    }
                    tags[count] = new SelectItem(tagsList.get(i).getTagId(), tagsList.get(i).getTagName());
                    count++;
                }
            }
            //cargo las aseguradoras
            List<Insurance> insuranceList = insuranceFacade.findAll();
            insurances = new SelectItem[insuranceList.size() + 1];
            insurances[0] = new SelectItem(0, "");
            for (int i = 0; i < insuranceList.size(); i++) {
                insurances[i + 1] = new SelectItem(insuranceList.get(i).getInsuranceId(), insuranceList.get(i).getInsuranceName());
            }
            //cargo las instituciones receptoras
            List<DomesticViolenceDataSources> violenceDataSourcesList = domesticViolenceDataSourcesFacade.findAll();
            violenceDataSources = new SelectItem[violenceDataSourcesList.size()];
            for (int i = 0; i < violenceDataSourcesList.size(); i++) {
                violenceDataSources[i] = new SelectItem(violenceDataSourcesList.get(i).getDomesticViolenceDataSourcesId(), violenceDataSourcesList.get(i).getDomesticViolenceDataSourcesName());
            }
            //cargo los grupos vulnerables
            List<VulnerableGroups> vulnerableGroupsList = vulnerableGroupsFacade.findAll();
            vulnerableGroups = new SelectItem[vulnerableGroupsList.size() + 1];
            vulnerableGroups[0] = new SelectItem(0, "");
            for (int i = 0; i < vulnerableGroupsList.size(); i++) {
                vulnerableGroups[i + 1] = new SelectItem(vulnerableGroupsList.get(i).getVulnerableGroupId(), vulnerableGroupsList.get(i).getVulnerableGroupName());
            }
            //cargo las instituciones de salud e IPS            
            List<NonFatalDataSourcesFromWhere> sourcesList = nonFatalDataSourcesFromWhereFacade.findAll();
            fromWhereList = new SelectItem[sourcesList.size()];
            for (int i = 0; i < sourcesList.size(); i++) {
                fromWhereList[i] = new SelectItem(sourcesList.get(i).getNonFatalDataSourcesFromWhereId(), sourcesList.get(i).getNonFatalDataSourcesFromWhereName());
            }
            //cargo los tipos de identificacion
            List<IdTypes> idTypesList = idTypesFacade.findAll();
            identifications = new SelectItem[idTypesList.size() + 1];
            identifications[0] = new SelectItem(0, "");
            for (int i = 0; i < idTypesList.size(); i++) {
                identifications[i + 1] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
            }
            //cargo las medidas de edad
            List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
            measuresOfAge = new SelectItem[ageTypesList.size() + 1];
            measuresOfAge[0] = new SelectItem(0, "");
            for (int i = 0; i < ageTypesList.size(); i++) {
                measuresOfAge[i + 1] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
            }
            //cargo los departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            departaments = new SelectItem[departamentsList.size() + 1];
            departaments[0] = new SelectItem(0, "");
            for (int i = 0; i < departamentsList.size(); i++) {
                departaments[i + 1] = new SelectItem(departamentsList.get(i).getDepartamentId(), departamentsList.get(i).getDepartamentName());
            }
            currentDepartamentHome = 0;
            // municipios inicia vacio
            municipalities = new SelectItem[1];
            municipalities[0] = new SelectItem(0, "");
            currentMunicipalitie = 0;
            //cargo los lugares donde ocurrieron los hechos
            List<NonFatalPlaces> placesList = nonFatalPlacesFacade.findAll();
            places = new SelectItem[placesList.size() + 1];
            places[0] = new SelectItem(0, "");
            for (int i = 0; i < placesList.size(); i++) {
                places[i + 1] = new SelectItem(placesList.get(i).getNonFatalPlaceId(), placesList.get(i).getNonFatalPlaceName());
            }
            //cargo las Actividades realizadas cuando ocurrio la lesión
            List<Activities> activitiesList = activitiesFacade.findAll();
            activities = new SelectItem[activitiesList.size() + 1];
            activities[0] = new SelectItem(0, "");
            for (int i = 0; i < activitiesList.size(); i++) {
                activities[i + 1] = new SelectItem(activitiesList.get(i).getActivityId(), activitiesList.get(i).getActivityName());
            }
            //cargo los mecanismos de lesión//se quita 
            List<Mechanisms> mechanismsList = mechanismsFacade.findAll();
            mechanisms = new SelectItem[mechanismsList.size() + 1 - 3];//+1(opcion vacio) -3(1.lesion transporte 24.animal cual 26.desastre natural)
            mechanisms[0] = new SelectItem(0, "");
            int pos = 0;
            for (int i = 0; i < mechanismsList.size(); i++) {
                if (mechanismsList.get(i).getMechanismId() != 1
                        && mechanismsList.get(i).getMechanismId() != 24
                        && mechanismsList.get(i).getMechanismId() != 26) {
                    mechanisms[pos + 1] = new SelectItem(mechanismsList.get(i).getMechanismId(), mechanismsList.get(i).getMechanismName());
                    pos++;
                }
            }
            //grupos etnicos
            List<EthnicGroups> ethnicGroupsList = ethnicGroupsFacade.findAll();
            ethnicGroups = new SelectItem[ethnicGroupsList.size() + 1];
            ethnicGroups[0] = new SelectItem(0, "");
            for (int i = 0; i < ethnicGroupsList.size(); i++) {
                ethnicGroups[i + 1] = new SelectItem(ethnicGroupsList.get(i).getEthnicGroupId(), ethnicGroupsList.get(i).getEthnicGroupName());
            }
            //generos
            List<Genders> gendersList = gendersFacade.findAll();
            genders = new SelectItem[gendersList.size() + 1];
            genders[0] = new SelectItem(0, "");
            for (int i = 0; i < gendersList.size(); i++) {
                genders[i + 1] = new SelectItem(gendersList.get(i).getGenderId(), gendersList.get(i).getGenderName());
            }
            //trabajos
            List<Jobs> jobsList = jobsFacade.findAllOrder();
            jobs = new SelectItem[jobsList.size() + 1];
            jobs[0] = new SelectItem(0, "");
            for (int i = 0; i < jobsList.size(); i++) {
                jobs[i + 1] = new SelectItem(jobsList.get(i).getJobId(), jobsList.get(i).getJobName());
            }
            //Uso de drogas y alcohol
            List<UseAlcoholDrugs> useAlcoholDrugsList = useAlcoholDrugsFacade.findAll();
            useAlcohol = new SelectItem[useAlcoholDrugsList.size() + 1];
            useAlcohol[0] = new SelectItem(0, "");
            useDrugs = new SelectItem[useAlcoholDrugsList.size() + 1];
            useDrugs[0] = new SelectItem(0, "");
            for (int i = 0; i < useAlcoholDrugsList.size(); i++) {
                useAlcohol[i + 1] = new SelectItem(useAlcoholDrugsList.get(i).getUseAlcoholDrugsId(), useAlcoholDrugsList.get(i).getUseAlcoholDrugsName());
                useDrugs[i + 1] = new SelectItem(useAlcoholDrugsList.get(i).getUseAlcoholDrugsId(), useAlcoholDrugsList.get(i).getUseAlcoholDrugsName());
            }
            currentResponsible = "ADMIN";

            determinePosition();

            currentDepartamentHome = 52;
            changeDepartamentHome();
            currentMunicipalitie = 1;

            //lista de criterios de busqueda            
            searchCriteriaList = new SelectItem[3];
            searchCriteriaList[0] = new SelectItem(1, "IDENTIFICACION");
            searchCriteriaList[1] = new SelectItem(2, "NOMBRE");
            searchCriteriaList[2] = new SelectItem(3, "CODIGO INTERNO");

            rowDataTableList = new ArrayList<RowDataTable>();

            determinePosition();
            openDialogFirst = "";
            openDialogNext = "";
            openDialogLast = "";
            openDialogPrevious = "";
            openDialogNew = "";
            save = true;
            System.out.println("Save=true");
            stylePosition = "color: #1471B1;";
            neighborhoodHomeNameDisabled = false;
            directionHomeDisabled = false;

        } catch (Exception e) {
            System.out.println("*******************************************ERROR_V3: " + e.toString());
        }
        loading = false;
        System.out.println("//////////////FORMULARIO REINICIADO//////////////////////////");
    }

    @PostConstruct
    private void postConstruct() {
        save = true;
        stylePosition = "color: #1471B1;";
    }

    public void loadValues() {
        save = true;
        stylePosition = "color: #1471B1;";
        loading = true;
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------
        //******stranger
        try {
            stranger = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getStranger();
        } catch (Exception e) {
            stranger = false;
        }
        changeStranger();

        //******type_id
        try {
            currentIdentification = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentification = 0;
        }
        changeIdentificationType();
        //******victim_nid
        try {
            currentIdentificationNumber = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNid();
            if (currentIdentification == 6 || currentIdentification == 7 || currentIdentification == 0) {
                identificationNumberDisabled = true;
                currentIdentificationNumber = "";
            } else {
                identificationNumberDisabled = false;
            }
        } catch (Exception e) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        }
        //******victim_firstname
        try {
            currentName = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimName();
            if (currentName == null) {
                currentName = "";
            }
        } catch (Exception e) {
            currentName = "";
        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getAgeTypeId();
            if (currentMeasureOfAge == null) {
                currentMeasureOfAge = 0;
                valueAgeDisabled = true;
            } else {
                valueAgeDisabled = false;
            }
        } catch (Exception e) {
            currentMeasureOfAge = 0;
            valueAgeDisabled = true;
        }
        //******victim_age
        try {
            currentAge = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimAge().toString();
            if (currentAge == null) {
                currentAge = "";
            }
        } catch (Exception e) {
            currentAge = "";
        }
        //******gender_id
        try {
            currentGender = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getGenderId().getGenderId();
            if (currentGender == null) {
                currentGender = 0;
            }
        } catch (Exception e) {
            currentGender = 0;
        }
        //******job_id
        try {
            currentJob = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getJobId().getJobId();
            if (currentJob == null) {
                currentJob = 0;
            }
        } catch (Exception e) {
            currentJob = 0;
        }
        //******vulnerable_group_id
        try {
            currentVulnerableGroup = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().get(0).getVulnerableGroupId();
            if (currentVulnerableGroup == null) {
                currentVulnerableGroup = 0;
                otherVulnerableGroupDisabled = true;
            } else {
                if (currentVulnerableGroup == 98) {//otro
                    otherVulnerableGroupDisabled = false;
                }
            }
        } catch (Exception e) {
            currentVulnerableGroup = 0;
            otherVulnerableGroupDisabled = true;
        }

        //******ethnic_group_id

        try {
            currentEthnicGroup = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupId();
            if (currentEthnicGroup == null) {
                currentEthnicGroup = 0;
                otherEthnicGroupDisabled = true;
            } else {
                if (currentEthnicGroup == 3) {//otro
                    otherEthnicGroupDisabled = false;
                }
            }
        } catch (Exception e) {
            currentEthnicGroup = 0;
            otherEthnicGroupDisabled = true;
        }

        //******victim_telephone
        try {
            currentTelephoneHome = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimTelephone();
            if (currentTelephoneHome == null) {
                currentTelephoneHome = "";
            }
        } catch (Exception e) {
            currentTelephoneHome = "";
        }


        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id
        //******residence_municipality
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                currentDepartamentHome = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getResidenceDepartment();
                changeDepartamentHome();
                if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getResidenceMunicipality() != null) {
                    currentMunicipalitie = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
                    if (currentDepartamentHome == 52 && currentMunicipalitie == 1) {
                        neighborhoodHomeNameDisabled = false;
                        directionHomeDisabled = false;
                    }

                } else {
                    currentMunicipalitie = 0;
                }
            } else {
                currentDepartamentHome = 0;
                currentMunicipalitie = 0;
            }
        } catch (Exception e) {
            currentDepartamentHome = 0;
            currentMunicipalitie = 0;
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId() != null) {
                currentNeighborhoodHomeCode = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodHome = neighborhoodsFacade.find(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            } else {
                currentNeighborhoodHomeCode = "";
                currentNeighborhoodHome = "";
            }
        } catch (Exception e) {
            currentNeighborhoodHomeCode = "";
            currentNeighborhoodHome = "";
        }
        //******victim_address
        try {
            currentDirectionHome = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimAddress();
            if (currentDirectionHome == null) {
                currentDirectionHome = "";
            }
        } catch (Exception e) {
            currentDirectionHome = "";
        }

        //******insurance_id
        try {
            currentInsurance = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getInsuranceId().getInsuranceId();
            if (currentInsurance == null) {
                currentInsurance = 0;
            }
        } catch (Exception e) {
            currentInsurance = 0;
        }

        //-----CARGAR CAMPOS OTROS----------------
        List<Others> othersList = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getOthersList();
        for (int i = 0; i < othersList.size(); i++) {
            switch (othersList.get(i).getOthersPK().getFieldId()) {

                case 1://1.	Otro lugar del hecho
                    currentOtherPlace = othersList.get(i).getValueText();
                    otherPlaceDisabled = false;
                    break;
                case 2://2.	Otra actividad
                    currentOtherActivitie = othersList.get(i).getValueText();
                    otherActivityDisabled = false;
                    break;
                case 3://3.	Altura
                    heightWhich = othersList.get(i).getValueText();
                    heightWhichDisabled = false;
                    break;
                case 4://4.	Cual polvora			
                    powderWhich = othersList.get(i).getValueText();
                    powderWhichDisabled = false;
                    break;
                case 5://5.	Cual desastre natural

                    break;
                case 6://6.	Otro mecanismo objeto
                    otherMechanism = othersList.get(i).getValueText();
                    otherMechanismDisabled = false;
                    break;
                case 7://7.	Animal cual

                    break;
                case 8://8.	Otro grupo étnico
                    otherEthnicGroup = othersList.get(i).getValueText();
                    otherEthnicGroupDisabled = false;
                    break;
                case 9://9.	Otro grupo vulnerable
                    otherVulnerableGroup = othersList.get(i).getValueText();
                    otherVulnerableGroupDisabled = false;
                    break;
                case 10://10.	Otro tipo de agresor
                    otherAG = othersList.get(i).getValueText();
                    otherAGDisabled = false;
                    break;
                case 11://11.	Otro tipo de maltrato
                    otherMA = othersList.get(i).getValueText();
                    otherMADisabled = false;
                    break;
                case 12://12.	Otro tipo de agresion
                    otherAction = othersList.get(i).getValueText();
                    otherActionDisabled = false;
                    break;
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id

        //******checkup_date
        try {
            currentDateConsult = currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupDate());
            currentDayConsult = String.valueOf(cal.get(Calendar.DATE));
            currentMonthConsult = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearConsult = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate2();
        } catch (Exception e) {
            currentDateConsult = "";
        }
        //******checkup_time
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupTime().getHours() == 0) {
                currentHourConsult = "12";
                currentAmPmConsult = "AM";
            } else {
                currentHourConsult = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupTime().getHours());
                if (Integer.parseInt(currentHourConsult) != 12) {
                    if (Integer.parseInt(currentHourConsult) > 12) {
                        currentHourConsult = String.valueOf(Integer.parseInt(currentHourConsult) - 12);
                        currentAmPmConsult = "PM";
                    } else {
                        currentAmPmConsult = "AM";
                    }
                } else {
                    currentHourEvent = "12";
                    currentAmPmEvent = "PM";
                }
            }
            currentMinuteConsult = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupTime().getMinutes());

            calculateTime2();
        } catch (Exception e) {
            currentHourConsult = "";
            currentMinuteConsult = "";
            currentAmPmConsult = "SIN DATO";
            changeAmPmConsult();

        }
        //******injury_date
        try {
            currentDateEvent = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryDate());
            currentDayEvent = String.valueOf(cal.get(Calendar.DATE));
            currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate1();
        } catch (Exception e) {
            currentDateEvent = "";
        }
        //******injury_time
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryTime().getHours() == 0) {
                currentHourEvent = "12";
                currentAmPmEvent = "AM";
            } else {
                currentHourEvent = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryTime().getHours());
                if (Integer.parseInt(currentHourEvent) != 12) {
                    if (Integer.parseInt(currentHourEvent) > 12) {
                        currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) - 12);
                        currentAmPmEvent = "PM";
                    } else {
                        currentAmPmEvent = "AM";
                    }
                } else {
                    currentHourEvent = "12";
                    currentAmPmEvent = "PM";
                }
            }
            currentMinuteEvent = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryTime().getMinutes());

            calculateTime1();
        } catch (Exception e) {
            currentHourEvent = "";
            currentMinuteEvent = "";
            currentAmPmEvent = "SIN DATO";
            changeAmPmEvent();
        }
        //******injury_address
        try {
            currentDirectionEvent = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryAddress();
            if (currentDirectionEvent == null) {
                currentDirectionEvent = "";
            }
        } catch (Exception e) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            } else {
                currentNeighborhoodEventCode = "";
                currentNeighborhoodEvent = "";
            }
        } catch (Exception e) {
            currentNeighborhoodEventCode = "";
            currentNeighborhoodEvent = "";
        }
        //******injury_place_id
        try {
            currentPlace = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceId();
            if (currentPlace == null) {
                currentPlace = 0;
                otherPlaceDisabled = true;
            } else {
                if (currentPlace == 8) {
                    otherPlaceDisabled = false;
                } else {
                    otherPlaceDisabled = true;
                }
            }
        } catch (Exception e) {
            currentPlace = 0;
            otherPlaceDisabled = true;
        }
        //******activity_id
        try {
            currentActivities = currentNonFatalDomesticViolence.getNonFatalInjuries().getActivityId().getActivityId();
            if (currentActivities == null) {
                currentActivities = 0;
            }
        } catch (Exception e) {
            currentActivities = 0;
        }
        //******intentionality_id
        //******use_alcohol_id
        try {

            currentUseAlcohol = currentNonFatalDomesticViolence.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsId();
            if (currentUseAlcohol == null) {
                currentUseAlcohol = 0;
            }
        } catch (Exception e) {
            currentUseAlcohol = 0;
        }
        //******use_drugs_id
        try {
            currentUseDrugs = currentNonFatalDomesticViolence.getNonFatalInjuries().getUseDrugsId().getUseAlcoholDrugsId();
            if (currentUseDrugs == null) {
                currentUseDrugs = 0;
            }
        } catch (Exception e) {
            currentUseDrugs = 0;
        }
        //******burn_injury_degree
        try {
            currentLevelBurned = currentNonFatalDomesticViolence.getNonFatalInjuries().getBurnInjuryDegree();
            if (currentLevelBurned == null) {
                currentLevelBurned = 0;
            }
        } catch (Exception e) {
            currentLevelBurned = 0;
        }
        //******burn_injury_percentage
        try {
            currentPercentBurned = currentNonFatalDomesticViolence.getNonFatalInjuries().getBurnInjuryPercentage().toString();
            if (currentPercentBurned == null) {
                currentPercentBurned = "";
            }
        } catch (Exception e) {
            currentPercentBurned = "";
        }
        //******submitted_patient eps_id
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getSubmittedPatient() != null) {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getSubmittedPatient()) {
                isSubmitted = true;
                currentFromWhere = currentNonFatalDomesticViolence.getSubmittedFormWhereId().getNonFatalDataSourcesFromWhereId();
                fromWhereDisabled = false;
            } else {
                isSubmitted = false;
                fromWhereDisabled = true;
            }
        } else {
            isSubmitted = false;
            fromWhereDisabled = true;
        }
        //******destination_patient_id
        //******input_timestamp
        //******health_professional_id
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            currentMechanisms = currentNonFatalDomesticViolence.getNonFatalInjuries().getMechanismId().getMechanismId();
            if (currentMechanisms == null) {
                currentMechanisms = 0;
            } else {
                if (currentMechanisms == 5) {//"Otra caida, altura")
                    heightWhichDisabled = false;
                } else if (currentMechanisms == 12) {//Pólvora, cual"
                    powderWhichDisabled = false;
                } else if (currentMechanisms == 27) {//Otro, cual"
                    otherMechanismDisabled = false;
                }
            }
        } catch (Exception e) {
            currentMechanisms = 0;
        }
        //******user_id
        try {
            currentResponsible = currentNonFatalDomesticViolence.getNonFatalInjuries().getUserId().getUserName();
        } catch (Exception e) {
            currentResponsible = "";
        }
        //******injury_day_of_week
        try {
            currentWeekdayEvent = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryDayOfWeek();
            if (currentWeekdayEvent == null) {
                currentWeekdayEvent = "";
            }
        } catch (Exception e) {
            currentWeekdayEvent = "";
        }

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------

        if (currentNonFatalDomesticViolence.getDomesticViolenceDataSourceId() != null) {
            currentDomesticViolenceDataSource = currentNonFatalDomesticViolence.getDomesticViolenceDataSourceId().getDomesticViolenceDataSourcesId();
        }
        //cargo la lista de agresores-----------------------------------
        List<AggressorTypes> aggressorTypesList = currentNonFatalDomesticViolence.getAggressorTypesList();
        isAG1 = false;
        isAG2 = false;
        isAG3 = false;
        isAG4 = false;
        isAG5 = false;
        isAG6 = false;
        isAG7 = false;
        isAG8 = false;
        isAG10 = false;
        isUnknownAG = false;
        for (int i = 0; i < aggressorTypesList.size(); i++) {
            int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
            switch (caso) {
                case 1:
                    isAG1 = true;
                    break;
                case 2:
                    isAG2 = true;
                    break;
                case 3:
                    isAG3 = true;
                    break;
                case 4:
                    isAG4 = true;
                    break;
                case 5:
                    isAG5 = true;
                    break;
                case 6:
                    isAG6 = true;
                    break;
                case 7:
                    isAG7 = true;
                    break;
                case 8:
                    isAG8 = true;
                    otherAGDisabled = false;
                    break;
                case 9:
                    isUnknownAG = true;
                    break;
                case 10:
                    isAG10 = true;
                    break;
            }
        }

        //cargo la lista de abusos(tipos de maltrato)-----------------------------------

        List<AbuseTypes> abuseTypesList = currentNonFatalDomesticViolence.getAbuseTypesList();

        isMA1 = false;
        isMA2 = false;
        isMA3 = false;
        isMA4 = false;
        isMA5 = false;
        isMA6 = false;
        isMA8 = false;
        isUnknownMA = false;
        for (int i = 0; i < abuseTypesList.size(); i++) {
            int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
            switch (caso) {
                case 1:
                    isMA1 = true;
                    break;
                case 2:
                    isMA2 = true;
                    break;
                case 3:
                    isMA3 = true;
                    break;
                case 4:
                    isMA4 = true;
                    break;
                case 5:
                    isMA5 = true;
                    break;
                case 6:
                    isMA6 = true;
                    break;
                case 7:
                    isUnknownMA = true;
                    break;
                case 8:
                    isMA8 = true;
                    otherMADisabled = false;
                    break;
            }
        }

        //cargo la lista de acciones a realizar-----------------------------------
        List<ActionsToTake> actionsToTakeList = currentNonFatalDomesticViolence.getActionsToTakeList();

        isAction1 = false;
        isAction2 = false;
        isAction3 = false;
        isAction4 = false;
        isAction5 = false;
        isAction6 = false;
        isAction7 = false;
        isAction8 = false;
        isAction9 = false;
        isAction10 = false;
        isAction11 = false;
        isAction12 = false;
        isUnknownAction = false;
        for (int i = 0; i < actionsToTakeList.size(); i++) {
            int caso = (int) actionsToTakeList.get(i).getActionId();
            switch (caso) {
                case 1:
                    isAction1 = true;
                    break;
                case 2:
                    isAction2 = true;
                    break;
                case 3:
                    isAction3 = true;
                    break;
                case 4:
                    isAction4 = true;
                    break;
                case 5:
                    isAction5 = true;
                    break;
                case 6:
                    isAction6 = true;
                    break;
                case 7:
                    isAction7 = true;
                    break;
                case 8:
                    isAction8 = true;
                    break;
                case 9:
                    isAction9 = true;
                    break;
                case 10:
                    isAction10 = true;
                    break;
                case 11:
                    isAction11 = true;
                    break;
                case 12:
                    isAction12 = true;
                    otherActionDisabled = false;
                    break;
                case 13:
                    isUnknownAction = true;
                    break;

            }
        }
        loading = false;
    }

    private boolean validateFields() {
        validationsErrors = new ArrayList<String>();
        //---------VALIDAR QUE EXISTA FECHA DE HECHO
        if (currentDateEvent.trim().length() == 0) {
            validationsErrors.add("Es obligatorio ingresar la fecha del evento");
        }
        //---------VALIDAR QUE LA FECHA DEL CONSULTA SEA MAYOR A LA FECHA DEL HECHO 
        if (currentDateConsult.trim().length() != 0 && currentDateEvent.trim().length() != 0) {

            try {
                Calendar eventDate = Calendar.getInstance();
                Calendar consultDate = Calendar.getInstance();
                Date dateConsult = formato.parse(currentDateConsult);
                Date dateEvent = formato.parse(currentDateEvent);

                consultDate.setTime(dateConsult);
                eventDate.setTime(dateEvent);
                if (consultDate.compareTo(eventDate) < 0) {
                    validationsErrors.add("La fecha del evento: (" + currentDateEvent + ") no puede ser mayor que la fecha de la consulta (" + currentDateConsult + ")");
                }
            } catch (ParseException ex) {
                Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //---------VALIDAR QUE LA FECHA DEL SISTEMA SEA MAYOR A LA FECHA DEL HECHO 
        if (currentDateEvent.trim().length() != 0) {
            try {
                Calendar currentDate = Calendar.getInstance();
                Calendar eventDate = Calendar.getInstance();
                Date dateEvent = formato.parse(currentDateEvent);
                eventDate.setTime(dateEvent);
                if (currentDate.compareTo(eventDate) < 0) {
                    validationsErrors.add("La fecha del evento: (" + currentDateEvent + ") es mayor que la fecha del sistema");
                }
            } catch (ParseException ex) {
                Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //---------VALIDAR QUE LA FECHA DEL SISTEMA SEA MAYOR A LA FECHA DE CONSULTA
        if (currentDateConsult.trim().length() != 0) {
            try {
                Calendar currentDate = Calendar.getInstance();
                Calendar consultDate = Calendar.getInstance();
                Date dateConsult = formato.parse(currentDateConsult);
                consultDate.setTime(dateConsult);
                if (currentDate.compareTo(consultDate) < 0) {
                    validationsErrors.add("La fecha de consulta: (" + currentDateConsult + ") es mayor que la fecha del sistema");
                }
            } catch (ParseException ex) {
                Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //---------VALIDAR SI FECHA DE EVENTO IGUAL A FECHA DE CONSULTA LA HORA DE CONSULTA SEA MAYOR A LA DE EVENTO EN UN MISMO DIA
        if (currentDateConsult.trim().length() != 0 && currentDateEvent.trim().length() != 0) {
            try {
                Calendar eventDate = Calendar.getInstance();
                Calendar consultDate = Calendar.getInstance();
                Date dateConsult = formato.parse(currentDateConsult);
                Date dateEvent = formato.parse(currentDateEvent);

                consultDate.setTime(dateConsult);
                eventDate.setTime(dateEvent);
                if (consultDate.compareTo(eventDate) == 0) {
                    String hEvent, hConsult;
                    hEvent = currentMilitaryHourEvent.trim();
                    hConsult = currentMilitaryHourConsult.trim();
                    if (hConsult.compareTo("0000") == 0) {
                        hConsult = "2400";
                    }
                    if (hEvent.length() != 0 && hConsult.length() != 0) {
                        if (Integer.parseInt(hEvent) > Integer.parseInt(hConsult)) {
                            validationsErrors.add("La hora del evento: (" + hEvent + ") no puede ser mayor que la hora de la consulta (" + hConsult + ") en un mismo dia.");
                        }
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //---------MOSTRAR LOS ERRORES SI EXISTEN
        if (validationsErrors.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < validationsErrors.size(); i++) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", validationsErrors.get(i));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            return false;
        }
    }

    private boolean saveRegistry() {
        //realizo validaciones
        if (validateFields()) {
            try {
                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA VICTIMA
                //------------------------------------------------------------
                Victims newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                if (currentIdentification != 0) {
                    newVictim.setTypeId(idTypesFacade.find(currentIdentification));
                }
                //******stranger                
                newVictim.setStranger(stranger);

                if (currentIdentificationNumber.trim().length() != 0) {
                    newVictim.setVictimNid(currentIdentificationNumber);
                }
                if (currentName.trim().length() != 0) {
                    newVictim.setVictimName(currentName);
                }
//                if (currentName.trim().length() != 0) {
//                    newVictim.setVictimFirstname(currentName);
//                }
//                if (currentSurname.trim().length() != 0) {
//                    newVictim.setVictimLastname(currentSurname);
//                }
                if (currentMeasureOfAge != 0) {
                    newVictim.setAgeTypeId(currentMeasureOfAge);
                }
                if (currentAge.trim().length() != 0) {
                    newVictim.setVictimAge(Short.parseShort(currentAge));
                }
                if (currentGender != 0) {
                    newVictim.setGenderId(gendersFacade.find(currentGender));
                }
                if (currentJob != 0) {
                    newVictim.setJobId(jobsFacade.find(currentJob));
                }
                if (currentVulnerableGroup != 0) {
                    VulnerableGroups auxVulnerableGroups = vulnerableGroupsFacade.find(currentVulnerableGroup);
                    List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                    vulnerableGroupsList.add(auxVulnerableGroups);
                    newVictim.setVulnerableGroupsList(vulnerableGroupsList);
                    //newVictim.setVulnerableGroupId(vulnerableGroupsFacade.find(currentVulnerableGroup));
                }

                //falta la definicion si es otro grupo vulnerable
                if (otherEthnicGroup.trim().length() != 0) {
                }
                if (currentEthnicGroup != 0) {
                    newVictim.setEthnicGroupId(ethnicGroupsFacade.find(currentEthnicGroup));
                }
                //falta la definicion si es otro grupo etnico
                if (otherVulnerableGroup.trim().length() != 0) {
                }
                if (currentTelephoneHome.trim().length() != 0) {
                    newVictim.setVictimTelephone(currentTelephoneHome);
                }
                if (currentDirectionHome.trim().length() != 0) {
                    newVictim.setVictimAddress(currentDirectionHome);
                }
                if (currentNeighborhoodHomeCode.trim().length() != 0) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(currentNeighborhoodHomeCode)));
                }
                //newVictim.setVictimDateOfBirth(new Date());
                //newVictim.setEpsId(null);
                //newVictim.setVictimClass(null);            
                //if (currentMunicipalitie != 0) {
                newVictim.setResidenceMunicipality(currentMunicipalitie);
                newVictim.setResidenceDepartment(currentDepartamentHome);
                //}

                if (currentInsurance != 0) {
                    newVictim.setInsuranceId(insuranceFacade.find(currentInsurance));
                }

                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------
                NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();


                newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 53));//es 53 por ser vif

                newNonFatalInjuries.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);

                if (currentDateConsult.trim().length() != 0) {
                    newNonFatalInjuries.setCheckupDate(formato.parse(currentDateConsult));
                }
                if (currentMilitaryHourConsult.trim().length() != 0) {
                    int hourInt = Integer.parseInt(currentMilitaryHourConsult.substring(0, 2));
                    int minuteInt = Integer.parseInt(currentMilitaryHourConsult.substring(2, 4));
                    Date n = new Date();
                    n.setHours(hourInt);
                    n.setMinutes(minuteInt);
                    n.setSeconds(0);
                    newNonFatalInjuries.setCheckupTime(n);
                }

                if (currentDateEvent.trim().length() != 0) {
                    newNonFatalInjuries.setInjuryDate(formato.parse(currentDateEvent));
                }
                if (currentMilitaryHourEvent.trim().length() != 0) {
                    int hourInt = Integer.parseInt(currentMilitaryHourEvent.substring(0, 2));
                    int minuteInt = Integer.parseInt(currentMilitaryHourEvent.substring(2, 4));
                    Date n = new Date();
                    n.setHours(hourInt);
                    n.setMinutes(minuteInt);
                    n.setSeconds(0);
                    newNonFatalInjuries.setInjuryTime(n);
                }
                if (currentDirectionEvent.trim().length() != 0) {
                    newNonFatalInjuries.setInjuryAddress(currentDirectionEvent);
                }
                if (currentNeighborhoodEventCode.trim().length() != 0) {
                    newNonFatalInjuries.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(currentNeighborhoodEventCode)));
                }
                if (currentPlace != 0) {
                    newNonFatalInjuries.setInjuryPlaceId(nonFatalPlacesFacade.find(currentPlace));
                }
                if (currentActivities != 0) {
                    newNonFatalInjuries.setActivityId(activitiesFacade.find(currentActivities));
                }
                //newNonFatalInjuries.setIntentionalityId();
                if (currentUseAlcohol != 0) {
                    newNonFatalInjuries.setUseAlcoholId(useAlcoholDrugsFacade.find(currentUseAlcohol));
                }
                if (currentUseDrugs != 0) {
                    newNonFatalInjuries.setUseDrugsId(useAlcoholDrugsFacade.find(currentUseDrugs));
                }
                if (currentLevelBurned != 0) {
                    newNonFatalInjuries.setBurnInjuryDegree(currentLevelBurned);
                }
                if (currentPercentBurned.trim().length() != 0) {
                    newNonFatalInjuries.setBurnInjuryPercentage(Short.parseShort(currentPercentBurned));
                }

                //newNonFatalInjuries.setDestinationPatientId(null);            
                newNonFatalInjuries.setInputTimestamp(new Date());


                //newNonFatalInjuries.setHealthProfessionalId(null);

                //if (currentDomesticViolenceDataSource != 0) {
                //newNonFatalInjuries.setNonFatalDataSourceId(currentDomesticViolenceDataSource);
                //}
                if (currentMechanisms != 0) {
                    newNonFatalInjuries.setMechanismId(mechanismsFacade.find(currentMechanisms));
                }

                newNonFatalInjuries.setUserId(currentUser);

                if (currentWeekdayEvent.trim().length() != 0) {
                    newNonFatalInjuries.setInjuryDayOfWeek(currentWeekdayEvent);
                }

                newNonFatalInjuries.setVictimId(newVictim);


                //------------------------------------------------------------
                //SE CREA VARIABLE PARA VIOLENCIA INTRAFAMILIAR
                //------------------------------------------------------------

                NonFatalDomesticViolence newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(domesticViolenceDataSourcesFacade.find(currentDomesticViolenceDataSource));
                newNonFatalDomesticViolence.setNonFatalInjuries(newNonFatalInjuries);

                if (currentDepartamentHome != 0) {
                    newNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setResidenceDepartment(currentDepartamentHome);
                }

                if (isSubmitted) {
                    newNonFatalInjuries.setSubmittedPatient(isSubmitted);
                    newNonFatalDomesticViolence.setSubmittedFormWhereId(nonFatalDataSourcesFromWhereFacade.find(currentFromWhere));
                }
                //creo la lista de agresores-----------------------------------
                List<AggressorTypes> aggressorTypesList = new ArrayList<AggressorTypes>();

                if (isAG1) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 1));
                }
                if (isAG2) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 2));
                }
                if (isAG3) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 3));
                }
                if (isAG4) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 4));
                }
                if (isAG5) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 5));
                }
                if (isAG6) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 6));
                }
                if (isAG7) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 7));
                }
                if (isAG8) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 8));
                }
                if (isUnknownAG) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 9));
                }
                if (isAG10) {
                    aggressorTypesList.add(aggressorTypesFacade.find((short) 10));
                }
                newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);

                //creo la lista de abusos(tipos de maltrato)-----------------------------------
                List<AbuseTypes> abuseTypesList = new ArrayList<AbuseTypes>();

                if (isMA1) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 1));
                }
                if (isMA2) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 2));
                }
                if (isMA3) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 3));
                }
                if (isMA4) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 4));
                }
                if (isMA5) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 5));
                }
                if (isMA6) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 6));
                }
                if (isUnknownMA) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 7));
                }
                if (isMA8) {
                    abuseTypesList.add(abuseTypesFacade.find((short) 8));
                }
                newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);

                //creo la lista de acciones a realizar-----------------------------------
                List<ActionsToTake> actionsToTakeList = new ArrayList<ActionsToTake>();

                if (isAction1) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 1));
                }
                if (isAction2) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 2));
                }
                if (isAction3) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 3));
                }
                if (isAction4) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 4));
                }
                if (isAction5) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 5));
                }
                if (isAction6) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 6));
                }
                if (isAction7) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 7));
                }
                if (isAction8) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 8));
                }
                if (isAction9) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 9));
                }
                if (isAction10) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 10));
                }
                if (isAction11) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 11));
                }
                if (isAction12) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 12));
                }
                if (isUnknownAction) {
                    actionsToTakeList.add(actionsToTakeFacade.find((short) 13));
                }

                newNonFatalDomesticViolence.setActionsToTakeList(actionsToTakeList);

                //-----GUARDAR CAMPOS OTROS----------------
                List<Others> othersList = new ArrayList<Others>();
                Others newOther;
                OthersPK newOtherPK;

                if (currentOtherPlace.trim().length() != 0) {//1.	Otro lugar del hecho
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 1);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(currentOtherPlace);
                    othersList.add(newOther);

                }
                if (currentOtherActivitie.trim().length() != 0) {//2.	Otra actividad
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 2);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(currentOtherActivitie);
                    othersList.add(newOther);

                }
                if (heightWhich.trim().length() != 0) {//3.	Altura
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 3);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(heightWhich);
                    othersList.add(newOther);

                }
                if (powderWhich.trim().length() != 0) {//4.	Cual polvora
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 4);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(powderWhich);
                    othersList.add(newOther);
                }
                if (otherMechanism.trim().length() != 0) {//6.	Otro mecanismo objeto
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 6);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherMechanism);
                    othersList.add(newOther);
                }

                if (otherEthnicGroup.trim().length() != 0) {//8.	Otro grupo étnico
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 8);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherEthnicGroup);
                    othersList.add(newOther);
                }
                if (otherVulnerableGroup.trim().length() != 0) {//9.	Otro grupo vulnerable
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 9);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherVulnerableGroup);
                    othersList.add(newOther);
                }
                if (otherAG.trim().length() != 0) {//10.	Otro tipo de agresor
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 10);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherAG);
                    othersList.add(newOther);
                }
                if (otherMA.trim().length() != 0) {//11.	Otro tipo de maltrato
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 11);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherMA);
                    othersList.add(newOther);
                }
                if (otherAction.trim().length() != 0) {//12.	Otro tipo de accion
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 12);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherAction);
                    othersList.add(newOther);
                }

                newVictim.setOthersList(othersList);

                //-------------------------------------------------------------------------------
                //-------------------GUARDAR----------------------------
                //if (validationsErrors.isEmpty()) {
                openDialogFirst = "";
                openDialogNext = "";
                openDialogLast = "";
                openDialogPrevious = "";
                openDialogNew = "";
                openDialogDelete = "";
                if (currentNonFatalInjuriId == -1) {//ES UN NUEVO REGISTRO SE DEBE PERSISTIR  //System.out.println("guardando nuevo registro");

                    newVictim.setTagId(tagsFacade.find(currentTag));
                    victimsFacade.create(newVictim);
                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
                    nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);

                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR             //System.out.println("actualizando registro existente");
                    updateRegistry(newVictim, newNonFatalInjuries, newNonFatalDomesticViolence);
                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ACTUALIZADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                return true;
                //} else {
                //    for (int i = 0; i < validationsErrors.size(); i++) {
                //        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", validationsErrors.get(i));
                //        FacesContext.getCurrentInstance().addMessage(null, msg);
                //    }
                //    return false;
                //}
            } catch (Exception e) {
                System.out.println("*******************************************ERROR_V2: " + e.toString());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        } else {
            return false;
        }
    }

    private void updateRegistry(Victims victim, NonFatalInjuries nonFatalInjurie, NonFatalDomesticViolence nonFatalDomesticViolence) {

        try {
            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA VICTIMA
            //------------------------------------------------------------

            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setTypeId(victim.getTypeId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimNid(victim.getVictimNid());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimName(victim.getVictimName());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setInsuranceId(victim.getInsuranceId());

//            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimFirstname(victim.getVictimFirstname());
//            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimLastname(victim.getVictimLastname());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setAgeTypeId(victim.getAgeTypeId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimAge(victim.getVictimAge());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setGenderId(victim.getGenderId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setJobId(victim.getJobId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVulnerableGroupsList(victim.getVulnerableGroupsList());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setEthnicGroupId(victim.getEthnicGroupId());
            //if (otherEthnicGroup.trim().length() != 0) {	
            //if (otherVulnerableGroup.trim().length() != 0) {	
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimTelephone(victim.getVictimTelephone());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimAddress(victim.getVictimAddress());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimNeighborhoodId(victim.getVictimNeighborhoodId());
            //newVictim.setVictimDateOfBirth(new Date());
            //newVictim.setEpsId(null);
            //newVictim.setVictimClass(null);            
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setResidenceMunicipality(victim.getResidenceMunicipality());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setResidenceDepartment(victim.getResidenceDepartment());

            //-----GUARDAR CAMPOS OTROS----------------

            List<Others> othersList = new ArrayList<Others>();
            Others newOther;
            OthersPK newOtherPK;
            for (int i = 0; i < victim.getOthersList().size(); i++) {
                newOther = new Others();
                newOtherPK = new OthersPK();
                newOtherPK.setVictimId(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimId());
                newOtherPK.setFieldId(victim.getOthersList().get(i).getOthersPK().getFieldId());
                newOther.setOthersPK(newOtherPK);
                newOther.setValueText(victim.getOthersList().get(i).getValueText());
                othersList.add(newOther);
            }
            //elimino la lista anterior
            for (int i = 0; i < currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getOthersList().size(); i++) {
                othersFacade.remove(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getOthersList().get(i));
            }
            //persisto nueva lista
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setOthersList(othersList);


            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------
            //

            currentNonFatalDomesticViolence.getNonFatalInjuries().setCheckupDate(nonFatalInjurie.getCheckupDate());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setCheckupTime(nonFatalInjurie.getCheckupTime());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryDate(nonFatalInjurie.getInjuryDate());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryTime(nonFatalInjurie.getInjuryTime());

            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryAddress(nonFatalInjurie.getInjuryAddress());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryNeighborhoodId(nonFatalInjurie.getInjuryNeighborhoodId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryPlaceId(nonFatalInjurie.getInjuryPlaceId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setActivityId(nonFatalInjurie.getActivityId());
            //newNonFatalInjuries.setIntentionalityId();
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUseAlcoholId(nonFatalInjurie.getUseAlcoholId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUseDrugsId(nonFatalInjurie.getUseDrugsId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setBurnInjuryDegree(nonFatalInjurie.getBurnInjuryDegree());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setBurnInjuryPercentage(nonFatalInjurie.getBurnInjuryPercentage());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setSubmittedPatient(nonFatalInjurie.getSubmittedPatient());

            //newNonFatalInjuries.setDestinationPatientId(null);            
            //newNonFatalInjuries.setHealthProfessionalId(null);
            currentNonFatalDomesticViolence.getNonFatalInjuries().setNonFatalDataSourceId(nonFatalInjurie.getNonFatalDataSourceId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setMechanismId(nonFatalInjurie.getMechanismId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUserId(nonFatalInjurie.getUserId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryDayOfWeek(nonFatalInjurie.getInjuryDayOfWeek());

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------
            //nonFatalDomesticViolence.setNonFatalInjuryId(nonFatalDomesticViolence.getNonFatalInjuryId());
            //currentNonFatalDomesticViolence.setResidenceDepartament(nonFatalDomesticViolence.getResidenceDepartament());
            currentNonFatalDomesticViolence.setSubmittedFormWhereId(nonFatalDomesticViolence.getSubmittedFormWhereId());
            currentNonFatalDomesticViolence.setDomesticViolenceDataSourceId(nonFatalDomesticViolence.getDomesticViolenceDataSourceId());
            currentNonFatalDomesticViolence.setAggressorTypesList(nonFatalDomesticViolence.getAggressorTypesList());
            currentNonFatalDomesticViolence.setAbuseTypesList(nonFatalDomesticViolence.getAbuseTypesList());
            currentNonFatalDomesticViolence.setActionsToTakeList(nonFatalDomesticViolence.getActionsToTakeList());



            victimsFacade.edit(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId());
            nonFatalInjuriesFacade.edit(currentNonFatalDomesticViolence.getNonFatalInjuries());
            nonFatalDomesticViolenceFacade.edit(currentNonFatalDomesticViolence);
            System.out.println("registro actualizado");

        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void saveAndGoNext() {//guarda cambios si se han realizado y se dirije al siguiente
        if (saveRegistry()) {
            next();
        } else {
            //System.out.println("No se guardo");
        }
    }

    public void saveAndGoPrevious() {//guarda cambios si se han realizado y se dirije al anterior
        if (currentNonFatalInjuriId != -1) {
            if (saveRegistry()) {
                previous();
            }
        } else {
            if (saveRegistry()) {
                last();
            }
        }
    }

    public void saveAndGoFirst() {//guarda cambios si se han realizado y se dirije al primero
        if (saveRegistry()) {
            first();
        }
    }

    public void saveAndGoLast() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveRegistry()) {
            last();
        }
    }

    public void saveAndGoNew() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveRegistry()) {
            newForm();
        }
    }

    public void noSaveAndGoNew() {//guarda cambios si se han realizado y se dirije al ultimo
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        newForm();

    }

    public void noSaveAndGoNext() {//va al siguiente sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        next();
    }

    public void noSaveAndGoPrevious() {//va al anterior sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        if (currentNonFatalInjuriId != -1) {
            previous();
        } else {
            last();
        }
    }

    public void noSaveAndGoFirst() {//va al primero sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        first();
    }

    public void noSaveAndGoLast() {//va al ultimo sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
        stylePosition = "color: #1471B1;";
        last();
    }

    public void next() {
        if (save) {//se busca el siguiente se el registro esta guardado (si esta guardado se abrira un dialogo que pregunta si guardar)             
            //System.out.println("cargando siguiente registro");
            if (currentNonFatalInjuriId == -1) {//esta en registro nuevo                
            } else {
                auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findNext(currentNonFatalInjuriId, currentTag);
                if (auxFatalDomesticViolence != null) {
                    clearForm();
                    currentNonFatalDomesticViolence = auxFatalDomesticViolence;
                    currentNonFatalInjuriId = currentNonFatalDomesticViolence.getNonFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar siguiente registro)");
        }
    }

    public void previous() {
        if (save) {//se busca el siguiente se el registro esta guardado (si esta guardado se abrira un dialogo que pregunta si guardar)             
            //System.out.println("cargando siguiente registro");
            if (currentNonFatalInjuriId == -1) {//esta en registro nuevo                
                last();
            } else {
                auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findPrevious(currentNonFatalInjuriId, currentTag);
                if (auxFatalDomesticViolence != null) {
                    clearForm();
                    currentNonFatalDomesticViolence = auxFatalDomesticViolence;
                    currentNonFatalInjuriId = currentNonFatalDomesticViolence.getNonFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar siguiente registro)");
        }
    }

    public void first() {
        if (save) {
            //System.out.println("cargando primer registro");
            auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findFirst(currentTag);
            if (auxFatalDomesticViolence != null) {
                clearForm();
                currentNonFatalDomesticViolence = auxFatalDomesticViolence;
                currentNonFatalInjuriId = currentNonFatalDomesticViolence.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar primer registro)");
        }
    }

    public void last() {
        if (save) {
            //System.out.println("cargando primer registro");
            auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findLast(currentTag);
            if (auxFatalDomesticViolence != null) {
                clearForm();
                currentNonFatalDomesticViolence = auxFatalDomesticViolence;
                currentNonFatalInjuriId = currentNonFatalDomesticViolence.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar primer registro)");
        }
    }

    public void clearForm() {

        currentAmPmEvent = "AM";
        currentAmPmConsult = "AM";
        currentMinuteEventDisabled = false;
        currentHourEventDisabled = false;
        currentAmPmEventDisabled = false;

        currentMinuteConsultDisabled = false;
        currentHourConsultDisabled = false;
        currentAmPmConsultDisabled = false;

        loading = true;
        strangerDisabled = true;
        stranger = false;

        currentInsurance = 0;
        currentDomesticViolenceDataSource = 1;//IMPORTANTE!!!

        currentEthnicGroup = 0;
        otherEthnicGroup = "";
        otherEthnicGroupDisabled = true;
        currentVulnerableGroup = 0;
        otherVulnerableGroup = "";
        otherVulnerableGroupDisabled = true;
        currentResponsible = "";


        currentDepartamentHomeDisabled = false;
        currentDepartamentHome = 52;
        changeDepartamentHome();
        currentMunicipalitie = 1;
        //municipalities = new SelectItem[1];
        //municipalities[0] = new SelectItem(0, "");

        currentDirectionHome = "";
        currentTelephoneHome = "";

        isSubmitted = false;
        fromWhereDisabled = true;
        currentFromWhere = 0;

        currentPlace = 0;
        currentOtherPlace = "";
        otherPlaceDisabled = true;

        currentActivities = 0;
        currentOtherActivitie = "";
        otherActivityDisabled = true;

        currentMechanisms = 0;

        heightWhich = "";
        heightWhichDisabled = true;
        powderWhich = "";
        powderWhichDisabled = true;
        otherMechanism = "";
        otherMechanismDisabled = true;

        currentPercentBurned = "";

        currentUseAlcohol = 0;
        currentUseDrugs = 0;

        currentIdentification = 0;
        currentIdentificationNumber = "";
        currentName = "";
        //currentSurname = "";
        currentMeasureOfAge = 0;
        currentAge = "";
        valueAgeDisabled = true;
        currentGender = 0;
        currentJob = 0;
        currentDirectionEvent = "";

        currentNeighborhoodEventCode = "";
        currentNeighborhoodEvent = "";

        currentDateEvent = "";
        currentDayEvent = "";
        currentMonthEvent = "";
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        currentHourEvent = "";
        currentMinuteEvent = "";
        currentMilitaryHourEvent = "";
        currentDateConsult = "";
        currentDayConsult = "";
        currentMonthConsult = "";
        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
        currentHourConsult = "";
        currentMinuteConsult = "";
        currentMilitaryHourConsult = "";

        currentDirectionEvent = "";
        currentNeighborhoodHomeCode = "";
        currentNeighborhoodHome = "";
        currentMunicipalitie = 1;
        currentMunicipalitieDisabled = false;
        neighborhoodHomeNameDisabled = false;
        currentPlace = 0;

        currentWeekdayEvent = "";
        currentWeekdayConsult = "";

        isAG1 = false;
        isAG2 = false;
        isAG3 = false;
        isAG4 = false;
        isAG5 = false;
        isAG6 = false;
        isAG7 = false;
        isAG8 = false;
        isUnknownAG = false;
        isAG10 = false;
        otherAGDisabled = true;
        otherAG = "";


        isMA1 = false;
        isMA2 = false;
        isMA3 = false;
        isMA4 = false;
        isMA5 = false;
        isMA6 = false;
        //isMA7 = false;
        isMA8 = false;
        isUnknownMA = false;
        otherMADisabled = true;
        otherMA = "";

        isAction1 = false;
        isAction2 = false;
        isAction3 = false;
        isAction4 = false;
        isAction5 = false;
        isAction6 = false;
        isAction7 = false;
        isAction8 = false;
        isAction9 = false;
        isAction10 = false;
        isAction11 = false;
        isAction12 = false;
        otherActionDisabled = true;
        otherAction = "";
        isUnknownAction = false;
        neighborhoodHomeNameDisabled = false;
        directionHomeDisabled = false;
        loading = false;
    }

    public void newForm() {
        //currentNonFatalDomesticViolence = null;
        if (save) {
            clearForm();
            currentNonFatalInjuriId = -1;
            determinePosition();
        } else {
            //System.out.println("No esta guardado (para poder limpiar formulario)");
        }

    }

    public void nada() {
    }

    public void deleteRegistry() {
        //NonFatalDomesticViolence auxNonFatalDomesticViolence=currentNonFatalDomesticViolence;
        if (currentNonFatalInjuriId != -1) {
            NonFatalInjuries auxNonFatalInjuries = currentNonFatalDomesticViolence.getNonFatalInjuries();
            Victims auxVictims = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId();
            nonFatalDomesticViolenceFacade.remove(currentNonFatalDomesticViolence);
            nonFatalInjuriesFacade.remove(auxNonFatalInjuries);
            victimsFacade.remove(auxVictims);
            System.out.println("registro eliminado");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha eliminado el registro");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            noSaveAndGoNew();
        }
    }

    public void determinePosition() {
        totalRegisters = nonFatalDomesticViolenceFacade.countVIF(currentTag);
        if (currentNonFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            currentIdForm = String.valueOf(nonFatalInjuriesFacade.findMax() + 1);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = nonFatalDomesticViolenceFacade.findPosition(currentNonFatalDomesticViolence.getNonFatalInjuryId(), currentTag);
            currentIdForm = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuryId());
            currentPosition = position + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "dialogDelete.show();";
        }
        System.out.println("POSICION DETERMINADA: " + currentPosition);
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES PARA BUSCAR UN REGISTRO -----------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private Date date1;
    private Date date2;
    private int currentSearchCriteria = 0;
    private SelectItem[] searchCriteriaList;
    private String currentSearchValue = "";

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public void openForm() {
        if (selectedRowDataTable != null) {
            //auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findByIdVictim(selectedRowDataTable.getColumn1());
            auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (auxFatalDomesticViolence != null) {
                clearForm();
                currentNonFatalDomesticViolence = auxFatalDomesticViolence;
                currentNonFatalInjuriId = currentNonFatalDomesticViolence.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        }
        clearSearch();
    }

    public void clearSearch() {
        currentSearchValue = "";
        currentSearchCriteria = 1;
        rowDataTableList = new ArrayList<RowDataTable>();

    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un valor a buscar");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            s = false;
        }
        if (s) {
            try {
                rowDataTableList = new ArrayList<RowDataTable>();

                String sql = "";
                sql = sql + "SELECT ";
                sql = sql + "non_fatal_injuries.non_fatal_injury_id, ";
                sql = sql + "victims.victim_nid, ";
                sql = sql + "victims.victim_name ";
                sql = sql + "FROM ";
                sql = sql + "victims, ";
                sql = sql + "non_fatal_injuries, ";
                sql = sql + "injuries ";
                sql = sql + "WHERE ";
                sql = sql + "non_fatal_injuries.victim_id = victims.victim_id AND ";
                sql = sql + "injuries.injury_id = non_fatal_injuries.injury_id AND ";

                switch (currentSearchCriteria) {
                    case 1://Identificación
                        sql = sql + "victims.victim_nid LIKE '" + currentSearchValue + "%' AND ";
                        break;
                    case 2://Nombres
                        sql = sql + "UPPER(victims.victim_name) LIKE UPPER('%" + currentSearchValue + "%') AND ";
                        break;
                    case 3://codigo interno
                        sql = sql + "non_fatal_injuries.non_fatal_injury_id = " + currentSearchValue + " AND ";
                        break;
                }
//                if (date1 != null) {
//                    sql = sql + "non_fatal_injuries.input_timestamp < " + date1.toString() + " AND ";
//                }
//                if (date2 != null) {
//                    sql = sql + "non_fatal_injuries.input_timestamp > " + date2.toString() + " AND ";
//                }
                sql = sql + "injuries.injury_id = 53; ";
                //sql = sql + "injuries.injury_id = 50 OR ";
                //sql = sql + "injuries.injury_id = 51 OR ";
                //sql = sql + "injuries.injury_id = 52 OR ";
                //sql = sql + "injuries.injury_id = 54 OR ";
                //sql = sql + "injuries.injury_id = 55);";
                System.out.println(sql);
                ResultSet rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    rowDataTableList.add(new RowDataTable(rs.getString(1), rs.getString(2), rs.getString(3)));
                    s = false;//aqui se usa para saber si hay registros
                }
                if (s) {//si es true no hay registros
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No hay coincidencias", "No se encontraron registros para esta búsqueda");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } catch (Exception ex) {
            }
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES PARA AUTOCOMPLETAR ----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public List<String> suggestNeighborhoods(String entered) {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().startsWith(entered)) {
                list.add(neighborhoodsList.get(i).getNeighborhoodName());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR ----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeTag() {//cambia el conjunto de registros
        noSaveAndGoNew();
    }

    public void changeStranger() {
        if (loading == false) {
            changeForm();
        }
        if (stranger) {
            currentMunicipalitieDisabled = true;
            neighborhoodHomeNameDisabled = true;
            currentDepartamentHomeDisabled = true;
            directionHomeDisabled = true;
            currentDirectionHome = "";
            currentDepartamentHome = 0;
            municipalities = new SelectItem[1];
            municipalities[0] = new SelectItem(0, "");
            currentMunicipalitie = 0;
            currentNeighborhoodHome = "";
        } else {
            currentDepartamentHomeDisabled = false;
            currentMunicipalitieDisabled = false;
        }
    }

    public void changeUnknownAG() {
        if (loading == false) {
            changeForm();
        }
        if (isUnknownAG) {
            isAG1 = false;
            isAG2 = false;
            isAG3 = false;
            isAG4 = false;
            isAG5 = false;
            isAG6 = false;
            isAG7 = false;
            isAG8 = false;
            otherAG = "";
            otherAGDisabled = true;
            isAG10 = false;
        }
    }

    public void changeUnknownMA() {
        if (loading == false) {
            changeForm();
        }
        if (isUnknownMA) {
            isMA1 = false;
            isMA2 = false;
            isMA3 = false;
            isMA4 = false;
            isMA5 = false;
            isMA6 = false;
            isMA8 = false;
            otherMA = "";
            otherMADisabled = true;
        }
    }

    public void changeUnknownAction() {
        if (loading == false) {
            changeForm();
        }
        if (isUnknownAction) {
            isAction1 = false;
            isAction2 = false;
            isAction3 = false;
            isAction4 = false;
            isAction5 = false;
            isAction6 = false;
            isAction7 = false;
            isAction8 = false;
            isAction9 = false;
            isAction10 = false;
            isAction11 = false;
            isAction12 = false;
            otherAction = "";
            isUnknownAction = true;
            otherActionDisabled = true;

        }
    }

    public void changeIdentificationType() {

        if (loading == false) {
            changeForm();
        }

        if (currentIdentification == 3 || currentIdentification == 2) {//pasaporte
            strangerDisabled = false;
        } else {
            strangerDisabled = true;
        }
        if (currentIdentification == 6 || currentIdentification == 7 || currentIdentification == 0) {
            identificationNumberDisabled = true;
            currentIdentificationNumber = "";
        } else {
            identificationNumberDisabled = false;
            currentIdentificationNumber = "";
        }
    }

    public void changeForm() {//el formulario fue modificado        
        openDialogFirst = "dialogFirst.show();";
        openDialogNext = "dialogNext.show();";
        openDialogLast = "dialogLast.show();";
        openDialogPrevious = "dialogPrevious.show();";
        openDialogNew = "dialogNew.show();";
        openDialogDelete = "dialogDelete.show();";
        save = false;
        stylePosition = "color: red; font-weight: 900;";
    }

    public void changePercentBurned() {
        try {
            int percentInt = Integer.parseInt(currentPercentBurned);
            if (percentInt < 1 || percentInt > 100) {
                currentPercentBurned = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El porcentaje de quemadura es un número del 1 al 100");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentPercentBurned.length() != 0) {
                currentPercentBurned = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El porcentaje de quemadura es un número del 1 al 100");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void changeValueAge() {
        try {
            int ageInt = Integer.parseInt(currentAge);
            if (ageInt < 1) {
                currentAge = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La edad debe ser un número,y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentAge.length() != 0) {
                currentAge = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La edad debe ser un número y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void changeDayEvent() {
        try {
            int dayInt = Integer.parseInt(currentDayEvent);
            if (dayInt < 1 || dayInt > 31) {
                currentDayEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia del evento debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentDayEvent.length() != 0) {
                currentDayEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia del evento debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();

    }

    public void changeMonthEvent() {
        try {
            int monthInt = Integer.parseInt(currentMonthEvent);
            if (monthInt < 1 || monthInt > 12) {
                currentMonthEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes del evento debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentMonthEvent.length() != 0) {
                currentMonthEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes del evento debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();
    }

    public void changeYearEvent() {
        Calendar cal = Calendar.getInstance();
        int yearSystem = cal.get(Calendar.YEAR);
        try {
            int yearInt = Integer.parseInt(currentYearEvent);
            if (yearInt < 2003 || yearInt < yearSystem) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentYearEvent.length() != 0) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate1();
    }

    public void changeHourEvent() {
        try {
            int hourInt = Integer.parseInt(currentHourEvent);
            if (hourInt < 1 || hourInt > 12) {
                currentHourEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora del evento debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentHourEvent.length() != 0) {
                currentHourEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora del evento debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime1();
    }

    public void changeAmPmEvent() {

        if (loading == false) {
            changeForm();
        }

        try {
            if (currentAmPmEvent.compareTo("AM") == 0 || currentAmPmEvent.compareTo("PM") == 0) {
                currentMinuteEventDisabled = false;
                currentHourEventDisabled = false;
            } else {
                currentMinuteEventDisabled = true;
                currentHourEventDisabled = true;
                currentMinuteEvent = "";
                currentHourEvent = "";
                currentMilitaryHourEvent = "";
            }
        } catch (Exception e) {
            currentMinuteEventDisabled = false;
            currentHourEventDisabled = false;
        }
    }

    public void changeAmPmConsult() {

        if (loading == false) {
            changeForm();
        }

        try {
            if (currentAmPmConsult.compareTo("AM") == 0 || currentAmPmConsult.compareTo("PM") == 0) {
                currentMinuteConsultDisabled = false;
                currentHourConsultDisabled = false;
            } else {
                currentMinuteConsultDisabled = true;
                currentHourConsultDisabled = true;
                currentMinuteConsult = "";
                currentHourConsult = "";
                currentMilitaryHourConsult = "";
            }
        } catch (Exception e) {
            currentMinuteConsultDisabled = false;
            currentHourConsultDisabled = false;
        }
    }

    public void changeMinuteEvent() {
        try {
            int minuteInt = Integer.parseInt(currentMinuteEvent);
            if (minuteInt < 0 || minuteInt > 59) {
                currentMinuteEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto del evento debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentMinuteEvent.length() != 0) {
                currentMinuteEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto del evento debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime1();
    }

    public void changeDayConsult() {
        try {
            int dayInt = Integer.parseInt(currentDayConsult);
            if (dayInt < 1 || dayInt > 31) {
                currentDayConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia de la consulta debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentDayConsult.length() != 0) {
                currentDayConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El dia de la consulta debe ser un número del 1 al 31");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate2();
    }

    public void changeMonthConsult() {
        try {
            int monthInt = Integer.parseInt(currentMonthConsult);
            if (monthInt < 1 || monthInt > 12) {
                currentMonthConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes de la consulta debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (currentMonthConsult.length() != 0) {
                currentMonthConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El mes de la consulta debe ser un número del 1 al 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate2();
    }

    public void changeYearConsult() {
        Calendar cal = Calendar.getInstance();
        int yearSystem = cal.get(Calendar.YEAR);
        try {
            int yearInt = Integer.parseInt(currentYearConsult);
            if (yearInt < 2003 || yearInt < yearSystem) {
                currentYearConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año de la consulta debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentYearConsult.length() != 0) {
                currentYearConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año de la consulta debe ser un número del 2003 hasta " + String.valueOf(yearSystem));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateDate2();
    }

    public void changeHourConsult() {
        try {
            int hourInt = Integer.parseInt(currentHourConsult);
            if (hourInt < 1 || hourInt > 12) {
                currentHourConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora de la consulta debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentHourConsult.length() != 0) {
                currentHourConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La hora de la consulta debe ser un número de 1 a 12");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime2();
    }

    public void changeMinuteConsult() {
        try {
            int minuteInt = Integer.parseInt(currentMinuteConsult);
            if (minuteInt < 0 || minuteInt > 59) {
                currentMinuteConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto de la consulta debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentMinuteConsult.length() != 0) {
                currentMinuteConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El minuto de la consulta debe ser un número de 0 a 59");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        calculateTime2();
    }

    public void changeDepartamentHome() {
        if (loading == false) {
            changeForm();
            neighborhoodHomeNameDisabled = true;
            directionHomeDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
            currentDirectionHome = "";
        }
        if (currentDepartamentHome != 0) {
            Departaments d = departamentsFacade.findById(currentDepartamentHome);
            municipalities = new SelectItem[d.getMunicipalitiesList().size() + 1];
            municipalities[0] = new SelectItem(0, "");
            for (int i = 0; i < d.getMunicipalitiesList().size(); i++) {
                municipalities[i + 1] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
            }
            if (currentDepartamentHome == 52) {
                currentMunicipalitie = 1;
            } else {
                currentMunicipalitie = 0;
            }
        } else {
            municipalities = new SelectItem[1];
            municipalities[0] = new SelectItem(0, "");
            currentMunicipalitie = 0;
        }

        changeMunicipalitieHome();
    }

    public void changeMunicipalitieHome() {
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
        if (loading == false) {
            changeForm();
        }
        if (currentMunicipalitie == 1 && currentDepartamentHome == 52) {
            neighborhoodHomeNameDisabled = false;
            directionHomeDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
            directionHomeDisabled = true;
            currentDirectionHome = "";

        }
    }

    public void changeNeighborhoodHomeName() {
        if (loading == false) {
            changeForm();
        }
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = String.valueOf(neighborhoodsList.get(i).getNeighborhoodId());
                break;
            }
        }
    }

    public void changeSubmitted() {
        if (loading == false) {
            changeForm();
        }
        if (isSubmitted) {
            fromWhereDisabled = false;
        } else {
            fromWhereDisabled = true;
            currentFromWhere = 0;
        }
    }

    public void changeNeighborhoodEvent() {
        if (loading == false) {
            changeForm();
        }
        Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
        if (n != null) {
            currentNeighborhoodEventCode = String.valueOf(n.getNeighborhoodId());
        } else {
            currentNeighborhoodEventCode = "";
        }
    }

    public void changeMechanisms() {
        if (loading == false) {
            changeForm();
        }
        heightWhichDisabled = true;
        powderWhichDisabled = true;
        otherMechanismDisabled = true;
        forBurned = "none";
        heightWhich = "";
        powderWhich = "";
        otherMechanism = "";

        if (currentMechanisms == 5) {//"Otra caida, altura")
            heightWhichDisabled = false;
        } else if (currentMechanisms == 12) {//Pólvora, cual"
            powderWhichDisabled = false;
        } else if (currentMechanisms == 27) {//Otro, cual"
            otherMechanismDisabled = false;
        }

        if (currentMechanisms == 10//Fuego / llama
                || currentMechanisms == 11//objeto caliente
                || currentMechanisms == 12//Pólvora
                || currentMechanisms == 21//explotar
                || currentMechanisms == 22//explosivo
                || currentMechanisms == 25) {//electricidad
            forBurned = "block";

        }
    }

    public void changeEthnicGroups() {
        if (loading == false) {
            changeForm();
        }
        if (currentEthnicGroup == 3) {//3. otro
            otherEthnicGroupDisabled = false;

        } else {
            otherEthnicGroupDisabled = true;
            otherEthnicGroup = "";
        }
    }

    public void changeVulnerableGroup() {
        if (loading == false) {
            changeForm();
        }
        if (currentVulnerableGroup == 98) {//98. otro
            otherVulnerableGroupDisabled = false;

        } else {
            otherVulnerableGroupDisabled = true;
            otherVulnerableGroup = "";
        }
    }

    public void changeMeasuresOfAge() {
        if (loading == false) {
            changeForm();
        }
        if (currentMeasureOfAge == 0 || currentMeasureOfAge == 4) {
            valueAgeDisabled = true;

        } else {
            valueAgeDisabled = false;
            currentAge = "";
        }
    }

    public void changeActivities() {
        if (loading == false) {
            changeForm();
        }
        if (currentActivities == 98) {//98. otra cual?
            otherActivityDisabled = false;
            currentOtherActivitie = "";
        } else {
            otherActivityDisabled = true;
            currentOtherActivitie = "";
        }
    }

    public void changePlace() {
        if (loading == false) {
            changeForm();
        }
        if (currentPlace == 8) {//8. otro
            otherPlaceDisabled = false;
        } else {
            otherPlaceDisabled = true;
            currentOtherPlace = "";
        }
    }

    public void changeHeightWhich() {
        try {
            int heightWhichInt = Integer.parseInt(heightWhich);
            if (heightWhichInt < 1) {
                heightWhich = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La altura debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } catch (Exception e) {
            if (heightWhich.length() != 0) {
                heightWhich = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La altura debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void changeOtherAG() {
        if (loading == false) {
            changeForm();
        }
        if (isAG8) {
            otherAGDisabled = false;

        } else {
            otherAGDisabled = true;
            otherAG = "";
        }
    }

    public void changeOtherMA() {
        if (loading == false) {
            changeForm();
        }
        if (isMA8) {
            otherMADisabled = false;

        } else {
            otherMADisabled = true;
            otherMA = "";
        }
    }

    public void changeOtherAction() {
        if (loading == false) {
            changeForm();
        }
        if (isAction12) {
            otherActionDisabled = false;

        } else {
            otherActionDisabled = true;
            otherAction = "";
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES DE CALCULO DE FECHA Y HORA MILITAR ------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private String intToDay(int i) {
        if (i == Calendar.MONDAY) {
            return "Lunes";
        } else if (i == Calendar.TUESDAY) {
            return "Martes";
        } else if (i == Calendar.WEDNESDAY) {
            return "Miércoles";
        } else if (i == Calendar.THURSDAY) {
            return "Jueves";
        } else if (i == Calendar.FRIDAY) {
            return "Viernes";
        } else if (i == Calendar.SATURDAY) {
            return "Sábado";
        } else {//if (i == Calendar.SUNDAY) 
            return "Domingo";
        }
    }

    private void calculateDate1() {
        try {
            fechaI = formato.parse(currentDayEvent + "/" + currentMonthEvent + "/" + currentYearEvent);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);
            currentDateEvent = formato.format(fechaI);
            currentWeekdayEvent = intToDay(cal.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException ex) {

            currentDateEvent = "";
            currentWeekdayEvent = "";
        }
    }

    private boolean calculateTime1() {
        int hourInt = 0;
        int minuteInt = 0;
        int timeInt;
        boolean continuar = true;
        try {
            hourInt = Integer.parseInt(currentHourEvent);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourEvent = "";
        }
        try {
            minuteInt = Integer.parseInt(currentMinuteEvent);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourEvent = "";
        }
        if (continuar) {
            try {
                if (currentAmPmEvent.length() != 0) {
                    String hourStr;
                    String minuteStr;
                    String timeStr;
                    if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                        if (currentAmPmEvent.compareTo("PM") == 0) {//hora PM

                            if (hourInt != 12) {
                                hourInt = hourInt + 12;
                            }
                            if (continuar) {
                                hourStr = String.valueOf(hourInt);
                                minuteStr = String.valueOf(minuteInt);
                                if (hourStr.length() == 1) {
                                    hourStr = "0" + hourStr;
                                }
                                if (minuteStr.length() == 1) {
                                    minuteStr = "0" + minuteStr;
                                }
                                timeStr = hourStr + minuteStr;
                                timeInt = Integer.parseInt(timeStr);
                                if (timeInt > 2400) {
                                    timeStr = "00" + minuteStr;
                                }
                                if (timeStr.compareTo("2400") == 0) {
                                    timeStr = "0000";
                                }
                                currentMilitaryHourEvent = timeStr;
                            }
                        } else {//hora AM
                            if (hourInt == 12) {
                                hourInt = hourInt + 12;
                            }
                        }
                        if (continuar) {
                            hourStr = String.valueOf(hourInt);
                            minuteStr = String.valueOf(minuteInt);
                            if (hourStr.length() == 1) {
                                hourStr = "0" + hourStr;
                            }
                            if (minuteStr.length() == 1) {
                                minuteStr = "0" + minuteStr;
                            }
                            timeStr = hourStr + minuteStr;
                            timeInt = Integer.parseInt(timeStr);
                            if (timeInt > 2400) {
                                timeStr = "00" + minuteStr;
                            }
                            if (timeStr.compareTo("2400") == 0) {
                                timeStr = "0000";
                            }
                            currentMilitaryHourEvent = timeStr;
                        }
                    } else {
                        currentMilitaryHourEvent = "";
                        continuar = false;
                    }
                }
            } catch (Exception ex) {
                currentMilitaryHourEvent = "" + ex.toString();
                continuar = false;
            }
        } else {
            currentMilitaryHourEvent = "";
            return false;
        }
        return continuar;
    }

    private void calculateDate2() {
        try {
            fechaI = formato.parse(currentDayConsult + "/" + currentMonthConsult + "/" + currentYearConsult);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);
            currentDateConsult = formato.format(fechaI);
            currentWeekdayConsult = intToDay(cal.get(Calendar.DAY_OF_WEEK));
        } catch (ParseException ex) {

            currentDateConsult = "";
            currentWeekdayConsult = "";
        }
    }

    private boolean calculateTime2() {
        int hourInt = 0;
        int minuteInt = 0;
        int timeInt;
        boolean continuar = true;
        try {
            hourInt = Integer.parseInt(currentHourConsult);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourConsult = "";
        }

        try {
            minuteInt = Integer.parseInt(currentMinuteConsult);
        } catch (Exception ex) {
            continuar = false;
            currentMilitaryHourConsult = "";
        }

        if (continuar) {
            try {
                if (currentAmPmConsult.length() != 0) {
                    String hourStr;
                    String minuteStr;
                    String timeStr;
                    if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                        if (currentAmPmConsult.compareTo("PM") == 0) {//hora PM
                            if (hourInt != 12) {
                                hourInt = hourInt + 12;
                            }
                            if (continuar) {

                                hourStr = String.valueOf(hourInt);
                                minuteStr = String.valueOf(minuteInt);
                                if (hourStr.length() == 1) {
                                    hourStr = "0" + hourStr;
                                }
                                if (minuteStr.length() == 1) {
                                    minuteStr = "0" + minuteStr;
                                }
                                timeStr = hourStr + minuteStr;
                                timeInt = Integer.parseInt(timeStr);
                                if (timeInt > 2400) {
                                    timeStr = "00" + minuteStr;
                                }
                                if (timeStr.compareTo("2400") == 0) {
                                    timeStr = "0000";
                                }
                                currentMilitaryHourConsult = timeStr;
                            }
                        } else {//hora AM
                            if (hourInt == 12) {
                                hourInt = hourInt + 12;
                            }
                        }
                        if (continuar) {
                            hourStr = String.valueOf(hourInt);
                            minuteStr = String.valueOf(minuteInt);
                            if (hourStr.length() == 1) {
                                hourStr = "0" + hourStr;
                            }
                            if (minuteStr.length() == 1) {
                                minuteStr = "0" + minuteStr;
                            }
                            timeStr = hourStr + minuteStr;
                            timeInt = Integer.parseInt(timeStr);
                            if (timeInt > 2400) {
                                timeStr = "00" + minuteStr;
                            }
                            if (timeStr.compareTo("2400") == 0) {
                                timeStr = "0000";
                            }
                            currentMilitaryHourConsult = timeStr;
                        }
                    } else {
                        //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hora incorrecta", "Corrija los valores de minutos y horas");
                        //FacesContext.getCurrentInstance().addMessage(null, msg);
                        currentMilitaryHourConsult = "";
                        continuar = false;
                    }
                }
            } catch (Exception ex) {
                //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al tratar de convertir la hora a tipo militar");
                //FacesContext.getCurrentInstance().addMessage(null, msg);
                currentMilitaryHourConsult = "" + ex.toString();
                continuar = false;
            }
        }
        return continuar;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // GET Y SET DE VARIABLES ----------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public Short getCurrentDomesticViolenceDataSource() {
        return currentDomesticViolenceDataSource;
    }

    public void setCurrentDomesticViolenceDataSource(Short currentDomesticViolenceDataSource) {
        this.currentDomesticViolenceDataSource = currentDomesticViolenceDataSource;
    }

    public SelectItem[] getViolenceDataSources() {
        return violenceDataSources;
    }

    public void setViolenceDataSources(SelectItem[] violenceDataSources) {
        this.violenceDataSources = violenceDataSources;
    }

    public Short getCurrentVulnerableGroup() {
        return currentVulnerableGroup;
    }

    public void setCurrentVulnerableGroup(Short currentVulnerableGroup) {
        this.currentVulnerableGroup = currentVulnerableGroup;
    }

    public SelectItem[] getVulnerableGroups() {
        return vulnerableGroups;
    }

    public void setVulnerableGroups(SelectItem[] vulnerableGroups) {
        this.vulnerableGroups = vulnerableGroups;
    }

    public boolean isIsAction1() {
        return isAction1;
    }

    public void setIsAction1(boolean isAction1) {
        this.isAction1 = isAction1;
    }

    public boolean isIsAction10() {
        return isAction10;
    }

    public void setIsAction10(boolean isAction10) {
        this.isAction10 = isAction10;
    }

    public boolean isIsAction11() {
        return isAction11;
    }

    public void setIsAction11(boolean isAction11) {
        this.isAction11 = isAction11;
    }

    public boolean isIsAction12() {
        return isAction12;
    }

    public void setIsAction12(boolean isAction12) {
        this.isAction12 = isAction12;
    }

    public boolean isIsAction2() {
        return isAction2;
    }

    public void setIsAction2(boolean isAction2) {
        this.isAction2 = isAction2;
    }

    public boolean isIsAction3() {
        return isAction3;
    }

    public void setIsAction3(boolean isAction3) {
        this.isAction3 = isAction3;
    }

    public boolean isIsAction4() {
        return isAction4;
    }

    public void setIsAction4(boolean isAction4) {
        this.isAction4 = isAction4;
    }

    public boolean isIsAction5() {
        return isAction5;
    }

    public void setIsAction5(boolean isAction5) {
        this.isAction5 = isAction5;
    }

    public boolean isIsAction6() {
        return isAction6;
    }

    public void setIsAction6(boolean isAction6) {
        this.isAction6 = isAction6;
    }

    public boolean isIsAction7() {
        return isAction7;
    }

    public void setIsAction7(boolean isAction7) {
        this.isAction7 = isAction7;
    }

    public boolean isIsAction8() {
        return isAction8;
    }

    public void setIsAction8(boolean isAction8) {
        this.isAction8 = isAction8;
    }

    public boolean isIsAction9() {
        return isAction9;
    }

    public void setIsAction9(boolean isAction9) {
        this.isAction9 = isAction9;
    }

    public boolean isIsUnknownAction() {
        return isUnknownAction;
    }

    public void setIsUnknownAction(boolean isUnknownAction) {
        this.isUnknownAction = isUnknownAction;
    }

    public SelectItem[] getIdentifications() {
        return identifications;
    }

    public void setIdentifications(SelectItem[] identifications) {
        this.identifications = identifications;
    }

    public SelectItem[] getMeasuresOfAge() {
        return measuresOfAge;
    }

    public void setMeasuresOfAge(SelectItem[] measuresOfAge) {
        this.measuresOfAge = measuresOfAge;
    }

    public SelectItem[] getGenders() {
        return genders;
    }

    public void setGenders(SelectItem[] genders) {
        this.genders = genders;
    }

    public SelectItem[] getDepartaments() {
        return departaments;
    }

    public void setDepartaments(SelectItem[] departaments) {
        this.departaments = departaments;

    }

    public SelectItem[] getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(SelectItem[] municipalities) {
        this.municipalities = municipalities;
    }

    public SelectItem[] getActivities() {
        return activities;
    }

    public void setActivities(SelectItem[] activities) {
        this.activities = activities;
    }

    public String getCurrentAmPmConsult() {
        return currentAmPmConsult;
    }

    public void setCurrentAmPmConsult(String currentAmPmConsult) {
        this.currentAmPmConsult = currentAmPmConsult;
        calculateTime2();
    }

    public String getCurrentAmPmEvent() {
        return currentAmPmEvent;
    }

    public void setCurrentAmPmEvent(String currentAmPmEvent) {
        this.currentAmPmEvent = currentAmPmEvent;
        calculateTime1();
    }

    public String getCurrentDateConsult() {
        return currentDateConsult;
    }

    public void setCurrentDateConsult(String currentDateConsult) {
        this.currentDateConsult = currentDateConsult;
    }

    public String getCurrentDateEvent() {
        return currentDateEvent;
    }

    public void setCurrentDateEvent(String currentDateEvent) {
        this.currentDateEvent = currentDateEvent;
    }

    public String getCurrentDayConsult() {
        return currentDayConsult;
    }

    public void setCurrentDayConsult(String currentDayConsult) {
        this.currentDayConsult = currentDayConsult;
        calculateDate2();
    }

    public String getCurrentDayEvent() {
        return currentDayEvent;
    }

    public void setCurrentDayEvent(String currentDayEvent) {
        this.currentDayEvent = currentDayEvent;
        calculateDate1();
    }

    public String getCurrentHourConsult() {
        return currentHourConsult;
    }

    public void setCurrentHourConsult(String currentHourConsult) {
        this.currentHourConsult = currentHourConsult;
        calculateTime2();
    }

    public String getCurrentHourEvent() {
        return currentHourEvent;
    }

    public void setCurrentHourEvent(String currentHourEvent) {
        this.currentHourEvent = currentHourEvent;
        calculateTime1();
    }

    public String getCurrentMilitaryHourConsult() {
        return currentMilitaryHourConsult;
    }

    public void setCurrentMilitaryHourConsult(String currentMilitaryHourConsult) {
        this.currentMilitaryHourConsult = currentMilitaryHourConsult;
    }

    public String getCurrentMilitaryHourEvent() {
        return currentMilitaryHourEvent;
    }

    public void setCurrentMilitaryHourEvent(String currentMilitaryHourEvent) {
        this.currentMilitaryHourEvent = currentMilitaryHourEvent;
    }

    public String getCurrentMinuteConsult() {
        return currentMinuteConsult;
    }

    public void setCurrentMinuteConsult(String currentMinuteConsult) {
        this.currentMinuteConsult = currentMinuteConsult;
        calculateTime2();
    }

    public String getCurrentMinuteEvent() {
        return currentMinuteEvent;
    }

    public void setCurrentMinuteEvent(String currentMinuteEvent) {
        this.currentMinuteEvent = currentMinuteEvent;
        calculateTime1();
    }

    public String getCurrentMonthConsult() {
        return currentMonthConsult;
    }

    public void setCurrentMonthConsult(String currentMonthConsult) {
        this.currentMonthConsult = currentMonthConsult;
        calculateDate2();
    }

    public String getCurrentMonthEvent() {
        return currentMonthEvent;
    }

    public void setCurrentMonthEvent(String currentMonthEvent) {
        this.currentMonthEvent = currentMonthEvent;
        calculateDate1();
    }

    public String getCurrentWeekdayConsult() {
        return currentWeekdayConsult;
    }

    public void setCurrentWeekdayConsult(String currentWeekdayConsult) {
        this.currentWeekdayConsult = currentWeekdayConsult;
    }

    public String getCurrentWeekdayEvent() {
        return currentWeekdayEvent;
    }

    public void setCurrentWeekdayEvent(String currentWeekdayEvent) {
        this.currentWeekdayEvent = currentWeekdayEvent;
    }

    public String getCurrentYearConsult() {
        return currentYearConsult;

    }

    public void setCurrentYearConsult(String currentYearConsult) {
        this.currentYearConsult = currentYearConsult;
        calculateDate2();
    }

    public String getCurrentYearEvent() {
        return currentYearEvent;
    }

    public void setCurrentYearEvent(String currentYearEvent) {
        this.currentYearEvent = currentYearEvent;
        calculateDate1();
    }

    public SelectItem[] getMechanisms() {
        return mechanisms;
    }

    public void setMechanisms(SelectItem[] mechanisms) {
        this.mechanisms = mechanisms;
    }

    public SelectItem[] getPlaces() {
        return places;
    }

    public void setPlaces(SelectItem[] places) {
        this.places = places;
    }

    public SelectItem[] getEthnicGroups() {
        return ethnicGroups;
    }

    public void setEthnicGroups(SelectItem[] ethnicGroups) {
        this.ethnicGroups = ethnicGroups;
    }

    public String getOtherEthnicGroup() {
        return otherEthnicGroup;
    }

    public void setOtherEthnicGroup(String otherEthnicGroup) {
        this.otherEthnicGroup = otherEthnicGroup;
    }

    public boolean isValueAgeDisabled() {
        return valueAgeDisabled;
    }

    public void setValueAgeDisabled(boolean valueAgeDisabled) {
        this.valueAgeDisabled = valueAgeDisabled;
    }

    public SelectItem[] getJobs() {
        return jobs;
    }

    public boolean isNeighborhoodHomeNameDisabled() {
        return neighborhoodHomeNameDisabled;
    }

    public void setNeighborhoodHomeNameDisabled(boolean neighborhoodHomeNameDisabled) {
        this.neighborhoodHomeNameDisabled = neighborhoodHomeNameDisabled;
    }

    public SelectItem[] getFromWhereList() {
        return fromWhereList;
    }

    public void setFromWhereList(SelectItem[] fromWhereList) {
        this.fromWhereList = fromWhereList;
    }

    public boolean isIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public boolean isFromWhereDisabled() {
        return fromWhereDisabled;
    }

    public void setFromWhereDisabled(boolean fromWhereDisabled) {
        this.fromWhereDisabled = fromWhereDisabled;
    }

    public boolean isOtherActivityDisabled() {
        return otherActivityDisabled;
    }

    public void setOtherActivityDisabled(boolean otherActivityDisabled) {
        this.otherActivityDisabled = otherActivityDisabled;
    }

    public boolean isOtherPlaceDisabled() {
        return otherPlaceDisabled;
    }

    public void setOtherPlaceDisabled(boolean otherPlaceDisabled) {
        this.otherPlaceDisabled = otherPlaceDisabled;
    }

    public SelectItem[] getUseAlcohol() {
        return useAlcohol;
    }

    public void setUseAlcohol(SelectItem[] useAlcohol) {
        this.useAlcohol = useAlcohol;
    }

    public SelectItem[] getUseDrugs() {
        return useDrugs;
    }

    public void setUseDrugs(SelectItem[] useDrugs) {
        this.useDrugs = useDrugs;
    }

    public String getHeightWhich() {
        return heightWhich;
    }

    public void setHeightWhich(String heightWhich) {
        this.heightWhich = heightWhich;
    }

    public boolean isHeightWhichDisabled() {
        return heightWhichDisabled;
    }

    public void setHeightWhichDisabled(boolean heightWhichDisabled) {
        this.heightWhichDisabled = heightWhichDisabled;
    }

    public String getOtherMechanism() {
        return otherMechanism;
    }

    public void setOtherMechanism(String otherMechanism) {
        this.otherMechanism = otherMechanism;
    }

    public boolean isOtherMechanismDisabled() {
        return otherMechanismDisabled;
    }

    public void setOtherMechanismDisabled(boolean otherMechanismDisabled) {
        this.otherMechanismDisabled = otherMechanismDisabled;
    }

    public String getPowderWhich() {
        return powderWhich;
    }

    public void setPowderWhich(String powderWhich) {
        this.powderWhich = powderWhich;
    }

    public boolean isPowderWhichDisabled() {
        return powderWhichDisabled;
    }

    public void setPowderWhichDisabled(boolean powderWhichDisabled) {
        this.powderWhichDisabled = powderWhichDisabled;
    }

    public String getForBurned() {
        return forBurned;
    }

    public void setForBurned(String forBurned) {
        this.forBurned = forBurned;
    }

    public Short getCurrentDepartamentHome() {
        return currentDepartamentHome;
    }

    public void setCurrentDepartamentHome(Short currentDepartamentHome) {
        this.currentDepartamentHome = currentDepartamentHome;
    }

    public Short getCurrentActivities() {
        return currentActivities;
    }

    public void setCurrentActivities(Short currentActivities) {
        this.currentActivities = currentActivities;
    }

    public Short getCurrentEthnicGroup() {
        return currentEthnicGroup;
    }

    public void setCurrentEthnicGroup(Short currentEthnicGroup) {
        this.currentEthnicGroup = currentEthnicGroup;
    }

    public Short getCurrentGender() {
        return currentGender;
    }

    public void setCurrentGender(Short currentGender) {
        this.currentGender = currentGender;
    }

    public Short getCurrentFromWhere() {
        return currentFromWhere;
    }

    public void setCurrentFromWhere(Short currentFromWhere) {
        this.currentFromWhere = currentFromWhere;
    }

    public Short getCurrentIdentification() {
        return currentIdentification;
    }

    public void setCurrentIdentification(Short currentIdentification) {
        this.currentIdentification = currentIdentification;
    }

    public Short getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Short currentJob) {
        this.currentJob = currentJob;
    }

    public Short getCurrentMeasureOfAge() {
        return currentMeasureOfAge;
    }

    public void setCurrentMeasureOfAge(Short currentMeasureOfAge) {
        this.currentMeasureOfAge = currentMeasureOfAge;
    }

    public Short getCurrentMechanisms() {
        return currentMechanisms;
    }

    public void setCurrentMechanisms(Short currentMechanisms) {
        this.currentMechanisms = currentMechanisms;
    }

    public Short getCurrentMunicipalitie() {
        return currentMunicipalitie;
    }

    public void setCurrentMunicipalitie(Short currentMunicipalitie) {
        this.currentMunicipalitie = currentMunicipalitie;
    }

    public String getCurrentNeighborhoodEventCode() {
        return currentNeighborhoodEventCode;
    }

    public void setCurrentNeighborhoodEventCode(String currentNeighborhoodEventCode) {
        this.currentNeighborhoodEventCode = currentNeighborhoodEventCode;
    }

    public String getCurrentNeighborhoodHome() {
        return currentNeighborhoodHome;
    }

    public void setCurrentNeighborhoodHome(String currentNeighborhoodHome) {
        this.currentNeighborhoodHome = currentNeighborhoodHome;
    }

    public String getCurrentNeighborhoodHomeCode() {
        return currentNeighborhoodHomeCode;
    }

    public void setCurrentNeighborhoodHomeCode(String currentNeighborhoodHomeCode) {
        this.currentNeighborhoodHomeCode = currentNeighborhoodHomeCode;
    }

    public String getCurrentNeighborhoodEvent() {
        return currentNeighborhoodEvent;
    }

    public void setCurrentNeighborhoodEvent(String currentNeighborhoodEvent) {
        this.currentNeighborhoodEvent = currentNeighborhoodEvent;
    }

    public Short getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Short currentPlace) {
        this.currentPlace = currentPlace;
    }

    public Short getCurrentUseAlcohol() {
        return currentUseAlcohol;
    }

    public void setCurrentUseAlcohol(Short currentUseAlcohol) {
        this.currentUseAlcohol = currentUseAlcohol;
    }

    public Short getCurrentUseDrugs() {
        return currentUseDrugs;
    }

    public void setCurrentUseDrugs(Short currentUseDrugs) {
        this.currentUseDrugs = currentUseDrugs;
    }

    public String getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(String currentAge) {
        this.currentAge = currentAge;
    }

    public String getCurrentDirectionEvent() {
        return currentDirectionEvent;
    }

    public void setCurrentDirectionEvent(String currentDirectionEvent) {
        this.currentDirectionEvent = currentDirectionEvent;
    }

    public String getCurrentDirectionHome() {
        return currentDirectionHome;
    }

    public void setCurrentDirectionHome(String currentDirectionHome) {
        this.currentDirectionHome = currentDirectionHome;
    }

    public String getCurrentIdentificationNumber() {
        return currentIdentificationNumber;
    }

    public void setCurrentIdentificationNumber(String currentIdentificationNumber) {
        this.currentIdentificationNumber = currentIdentificationNumber;
    }

    public Short getCurrentLevelBurned() {
        return currentLevelBurned;
    }

    public void setCurrentLevelBurned(Short currentLevelBurned) {
        this.currentLevelBurned = currentLevelBurned;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public String getCurrentOtherActivitie() {
        return currentOtherActivitie;
    }

    public void setCurrentOtherActivitie(String currentOtherActivitie) {
        this.currentOtherActivitie = currentOtherActivitie;
    }

    public String getCurrentOtherPlace() {
        return currentOtherPlace;
    }

    public void setCurrentOtherPlace(String currentOtherPlace) {
        this.currentOtherPlace = currentOtherPlace;
    }

    public String getCurrentPercentBurned() {
        return currentPercentBurned;
    }

    public void setCurrentPercentBurned(String currentPercentBurned) {
        this.currentPercentBurned = currentPercentBurned;
    }

    public String getCurrentTelephoneHome() {
        return currentTelephoneHome;
    }

    public void setCurrentTelephoneHome(String currentTelephoneHome) {
        this.currentTelephoneHome = currentTelephoneHome;
    }

//    public String getCurrentSurname() {
//        return currentSurname;
//    }
//
//    public void setCurrentSurname(String currentSurname) {
//        this.currentSurname = currentSurname;
//    }
    public boolean isIsAG1() {
        return isAG1;
    }

    public void setIsAG1(boolean isAG1) {
        this.isAG1 = isAG1;
    }

    public boolean isIsAG2() {
        return isAG2;
    }

    public void setIsAG2(boolean isAG2) {
        this.isAG2 = isAG2;
    }

    public boolean isIsAG3() {
        return isAG3;
    }

    public void setIsAG3(boolean isAG3) {
        this.isAG3 = isAG3;
    }

    public boolean isIsAG4() {
        return isAG4;
    }

    public void setIsAG4(boolean isAG4) {
        this.isAG4 = isAG4;
    }

    public boolean isIsAG5() {
        return isAG5;
    }

    public void setIsAG5(boolean isAG5) {
        this.isAG5 = isAG5;
    }

    public boolean isIsAG6() {
        return isAG6;
    }

    public void setIsAG6(boolean isAG6) {
        this.isAG6 = isAG6;
    }

    public boolean isIsAG7() {
        return isAG7;
    }

    public void setIsAG7(boolean isAG7) {
        this.isAG7 = isAG7;
    }

    public boolean isIsAG8() {
        return isAG8;
    }

    public void setIsAG8(boolean isAG8) {
        this.isAG8 = isAG8;
    }

    public boolean isIsAG10() {
        return isAG10;
    }

    public void setIsAG10(boolean isAG10) {
        this.isAG10 = isAG10;
    }

    public boolean isIsMA1() {
        return isMA1;
    }

    public void setIsMA1(boolean isMA1) {
        this.isMA1 = isMA1;
    }

    public boolean isIsMA2() {
        return isMA2;
    }

    public void setIsMA2(boolean isMA2) {
        this.isMA2 = isMA2;
    }

    public boolean isIsMA3() {
        return isMA3;
    }

    public void setIsMA3(boolean isMA3) {
        this.isMA3 = isMA3;
    }

    public boolean isIsMA4() {
        return isMA4;
    }

    public void setIsMA4(boolean isMA4) {
        this.isMA4 = isMA4;
    }

    public boolean isIsMA5() {
        return isMA5;
    }

    public void setIsMA5(boolean isMA5) {
        this.isMA5 = isMA5;
    }

    public boolean isIsMA6() {
        return isMA6;
    }

    public void setIsMA6(boolean isMA6) {
        this.isMA6 = isMA6;
    }

    public boolean isIsMA8() {
        return isMA8;
    }

    public void setIsMA8(boolean isMA8) {
        this.isMA8 = isMA8;
    }

    public boolean isIsUnknownAG() {
        return isUnknownAG;
    }

    public void setIsUnknownAG(boolean isUnknownAG) {
        this.isUnknownAG = isUnknownAG;
    }

    public boolean isIsUnknownMA() {
        return isUnknownMA;
    }

    public void setIsUnknownMA(boolean isUnknownMA) {
        this.isUnknownMA = isUnknownMA;
    }

    public boolean isOtherEthnicGroupDisabled() {
        return otherEthnicGroupDisabled;
    }

    public void setOtherEthnicGroupDisabled(boolean otherEthnicGroupDisabled) {
        this.otherEthnicGroupDisabled = otherEthnicGroupDisabled;
    }

    public String getOtherVulnerableGroup() {
        return otherVulnerableGroup;
    }

    public void setOtherVulnerableGroup(String otherVulnerableGroup) {
        this.otherVulnerableGroup = otherVulnerableGroup;
    }

    public boolean isOtherVulnerableGroupDisabled() {
        return otherVulnerableGroupDisabled;
    }

    public void setOtherVulnerableGroupDisabled(boolean otherVulnerableGroupDisabled) {
        this.otherVulnerableGroupDisabled = otherVulnerableGroupDisabled;
    }

    public String getOtherAG() {
        return otherAG;
    }

    public void setOtherAG(String otherAG) {
        this.otherAG = otherAG;
    }

    public boolean isOtherAGDisabled() {
        return otherAGDisabled;
    }

    public void setOtherAGDisabled(boolean otherAGDisabled) {
        this.otherAGDisabled = otherAGDisabled;
    }

    public String getOtherMA() {
        return otherMA;
    }

    public void setOtherMA(String otherMA) {
        this.otherMA = otherMA;
    }

    public boolean isOtherMADisabled() {
        return otherMADisabled;
    }

    public void setOtherMADisabled(boolean otherMADisabled) {
        this.otherMADisabled = otherMADisabled;
    }

    public String getOtherAction() {
        return otherAction;
    }

    public void setOtherAction(String otherAction) {
        this.otherAction = otherAction;
    }

    public boolean isOtherActionDisabled() {
        return otherActionDisabled;
    }

    public void setOtherActionDisabled(boolean otherActionDisabled) {
        this.otherActionDisabled = otherActionDisabled;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getOpenDialogDelete() {
        return openDialogDelete;
    }

    public void setOpenDialogDelete(String openDialogDelete) {
        this.openDialogDelete = openDialogDelete;
    }

    public String getOpenDialogFirst() {
        return openDialogFirst;
    }

    public void setOpenDialogFirst(String openDialogFirst) {
        this.openDialogFirst = openDialogFirst;
    }

    public String getOpenDialogLast() {
        return openDialogLast;
    }

    public void setOpenDialogLast(String openDialogLast) {
        this.openDialogLast = openDialogLast;
    }

    public String getOpenDialogNew() {
        return openDialogNew;
    }

    public void setOpenDialogNew(String openDialogNew) {
        this.openDialogNew = openDialogNew;
    }

    public String getOpenDialogNext() {
        return openDialogNext;
    }

    public void setOpenDialogNext(String openDialogNext) {
        this.openDialogNext = openDialogNext;
    }

    public String getOpenDialogPrevious() {
        return openDialogPrevious;
    }

    public void setOpenDialogPrevious(String openDialogPrevious) {
        this.openDialogPrevious = openDialogPrevious;
    }

    public int getTotalRegisters() {
        return totalRegisters;
    }

    public void setTotalRegisters(int totalRegisters) {
        this.totalRegisters = totalRegisters;
    }

    public String getCurrentResponsible() {
        return currentResponsible;
    }

    public void setCurrentResponsible(String currentResponsible) {
        this.currentResponsible = currentResponsible;
    }

    public boolean isIdentificationNumberDisabled() {
        return identificationNumberDisabled;
    }

    public void setIdentificationNumberDisabled(boolean identificationNumberDisabled) {
        this.identificationNumberDisabled = identificationNumberDisabled;
    }

    public boolean isDirectionHomeDisabled() {
        return directionHomeDisabled;
    }

    public void setDirectionHomeDisabled(boolean directionHomeDisabled) {
        this.directionHomeDisabled = directionHomeDisabled;
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

    public SelectItem[] getSearchCriteriaList() {
        return searchCriteriaList;
    }

    public void setSearchCriteriaList(SelectItem[] searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    public Date getDate2() {
        return date2;
    }

    public void setDate2(Date date2) {
        this.date2 = date2;
    }

    public String getStylePosition() {
        return stylePosition;
    }

    public void setStylePosition(String stylePosition) {
        this.stylePosition = stylePosition;
    }

    //----------------------
    public boolean isCurrentAmPmConsultDisabled() {
        return currentAmPmConsultDisabled;
    }

    public void setCurrentAmPmConsultDisabled(boolean currentAmPmEConsultDisabled) {
        this.currentAmPmConsultDisabled = currentAmPmEConsultDisabled;
    }

    public boolean isCurrentAmPmEventDisabled() {
        return currentAmPmEventDisabled;
    }

    public void setCurrentAmPmEventDisabled(boolean currentAmPmEventDisabled) {
        this.currentAmPmEventDisabled = currentAmPmEventDisabled;
    }

    public boolean isCurrentDayConsultDisabled() {
        return currentDayConsultDisabled;
    }

    public void setCurrentDayConsultDisabled(boolean currentDayConsultDisabled) {
        this.currentDayConsultDisabled = currentDayConsultDisabled;
    }

    public boolean isCurrentDayEventDisabled() {
        return currentDayEventDisabled;
    }

    public void setCurrentDayEventDisabled(boolean currentDayEventDisabled) {
        this.currentDayEventDisabled = currentDayEventDisabled;
    }

    public boolean isCurrentHourConsultDisabled() {
        return currentHourConsultDisabled;
    }

    public void setCurrentHourConsultDisabled(boolean currentHourConsultDisabled) {
        this.currentHourConsultDisabled = currentHourConsultDisabled;
    }

    public boolean isCurrentHourEventDisabled() {
        return currentHourEventDisabled;
    }

    public void setCurrentHourEventDisabled(boolean currentHourEventDisabled) {
        this.currentHourEventDisabled = currentHourEventDisabled;
    }

    public boolean isCurrentMinuteConsultDisabled() {
        return currentMinuteConsultDisabled;
    }

    public void setCurrentMinuteConsultDisabled(boolean currentMinuteConsultDisabled) {
        this.currentMinuteConsultDisabled = currentMinuteConsultDisabled;
    }

    public boolean isCurrentMinuteEventDisabled() {
        return currentMinuteEventDisabled;
    }

    public void setCurrentMinuteEventDisabled(boolean currentMinuteEventDisabled) {
        this.currentMinuteEventDisabled = currentMinuteEventDisabled;
    }

    public boolean isCurrentMonthConsultDisabled() {
        return currentMonthConsultDisabled;
    }

    public void setCurrentMonthConsultDisabled(boolean currentMonthConsultDisabled) {
        this.currentMonthConsultDisabled = currentMonthConsultDisabled;
    }

    public boolean isCurrentMonthEventDisabled() {
        return currentMonthEventDisabled;
    }

    public void setCurrentMonthEventDisabled(boolean currentMonthEventDisabled) {
        this.currentMonthEventDisabled = currentMonthEventDisabled;
    }

//    public Short getCurrentStateDateConsult() {
//        return currentStateDateConsult;
//    }
//
//    public void setCurrentStateDateConsult(Short currentStateDateConsult) {
//        this.currentStateDateConsult = currentStateDateConsult;
//    }
//
//    public Short getCurrentStateDateEvent() {
//        return currentStateDateEvent;
//    }
//
//    public void setCurrentStateDateEvent(Short currentStateDateEvent) {
//        this.currentStateDateEvent = currentStateDateEvent;
//    }
//
//    public Short getCurrentStateTimeConsult() {
//        return currentStateTimeConsult;
//    }
//
//    public void setCurrentStateTimeConsult(Short currentStateTimeConsult) {
//        this.currentStateTimeConsult = currentStateTimeConsult;
//    }
//
//    public Short getCurrentStateTimeEvent() {
//        return currentStateTimeEvent;
//    }
//
//    public void setCurrentStateTimeEvent(Short currentStateTimeEvent) {
//        this.currentStateTimeEvent = currentStateTimeEvent;
//    }
    public boolean isCurrentYearConsultDisabled() {
        return currentYearConsultDisabled;
    }

    public void setCurrentYearConsultDisabled(boolean currentYearConsultDisabled) {
        this.currentYearConsultDisabled = currentYearConsultDisabled;
    }

    public boolean isCurrentYearEventDisabled() {
        return currentYearEventDisabled;
    }

    public void setCurrentYearEventDisabled(boolean currentYearEventDisabled) {
        this.currentYearEventDisabled = currentYearEventDisabled;
    }

//    public SelectItem[] getStateDateList() {
//        return stateDateList;
//    }
//
//    public void setStateDateList(SelectItem[] stateDateList) {
//        this.stateDateList = stateDateList;
//    }
//
//    public SelectItem[] getStateTimeList() {
//        return stateTimeList;
//    }
//
//    public void setStateTimeList(SelectItem[] stateTimeList) {
//        this.stateTimeList = stateTimeList;
//    }
    public boolean isStranger() {
        return stranger;
    }

    public void setStranger(boolean stranger) {
        this.stranger = stranger;
    }

    public boolean isCurrentMunicipalitieDisabled() {
        return currentMunicipalitieDisabled;
    }

    public void setCurrentMunicipalitieDisabled(boolean currentMunicipalitieDisabled) {
        this.currentMunicipalitieDisabled = currentMunicipalitieDisabled;
    }

    public boolean isCurrentDepartamentHomeDisabled() {
        return currentDepartamentHomeDisabled;
    }

    public void setCurrentDepartamentHomeDisabled(boolean currentDepartamentHomeDisabled) {
        this.currentDepartamentHomeDisabled = currentDepartamentHomeDisabled;
    }

    public boolean isStrangerDisabled() {
        return strangerDisabled;
    }

    public void setStrangerDisabled(boolean strangerDisabled) {
        this.strangerDisabled = strangerDisabled;
    }

    public Short getCurrentInsurance() {
        return currentInsurance;
    }

    public void setCurrentInsurance(Short currentInsurance) {
        this.currentInsurance = currentInsurance;
    }

    public SelectItem[] getInsurances() {
        return insurances;
    }

    public void setInsurances(SelectItem[] insurances) {
        this.insurances = insurances;
    }

    public String getCurrentIdForm() {
        return currentIdForm;
    }

    public void setCurrentIdForm(String currentIdForm) {
        this.currentIdForm = currentIdForm;
    }

    public int getCurrentTag() {
        return currentTag;
    }

    public void setCurrentTag(int currentTag) {
        this.currentTag = currentTag;
    }

    public SelectItem[] getTags() {
        return tags;
    }

    public void setTags(SelectItem[] tags) {
        this.tags = tags;
    }
}
