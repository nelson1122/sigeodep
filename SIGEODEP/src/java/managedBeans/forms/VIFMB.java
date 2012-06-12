/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "vifMB")
@SessionScoped
public class VIFMB {

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
    //------------------------
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    //------------------------
    @EJB
    UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    private Short currentUseAlcohol = 0;
    private Short currentUseDrugs = 0;
    private SelectItem[] useAlcohol;
    private SelectItem[] useDrugs;
    //------------------------
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private Short currentIPS = 0;
    private SelectItem[] IPSs;
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
    boolean neighborhoodHomeNameDisabled = true;
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
    private boolean valueAgeDisabled = false;
    //------------------------
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    //------------------
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    @EJB
    AggressorTypesFacade aggressorTypesFacade;
    @EJB
    AbuseTypesFacade abuseTypesFacade;
    @EJB
    ActionsToTakeFacade actionsToTakeFacade;
    //------------------
    private boolean isSubmitted = false;
    private boolean IPSDisabled = true;
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------    
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
    private boolean disasterWhichDisabled = true;//cual desastre
    private String disasterWhich = "";//cual desastre
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
    private String currentTelephoneHome = "";
    private String currentDirectionEvent = "";
    private String currentOtherPlace = "";
    private String currentOtherActivitie = "";
    private String currentSurname = "";
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public VIFMB() {
    }

    public void reset() {
        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        try {
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
            List<NonFatalDataSources> sourcesList = nonFatalDataSourcesFacade.findAll();
            IPSs = new SelectItem[sourcesList.size()];
            for (int i = 0; i < sourcesList.size(); i++) {
                IPSs[i] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceId(), sourcesList.get(i).getNonFatalDataSourceName());
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
            currentDepartamentHome = 52;
            //cargo los municipios
            findMunicipalities();
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
            //cargo los mecanismos de lesión
            List<Mechanisms> mechanismsList = mechanismsFacade.findAll();
            mechanisms = new SelectItem[mechanismsList.size() + 1];
            mechanisms[0] = new SelectItem(0, "");
            for (int i = 0; i < mechanismsList.size(); i++) {
                mechanisms[i + 1] = new SelectItem(mechanismsList.get(i).getMechanismId(), mechanismsList.get(i).getMechanismName());
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
            List<Jobs> jobsList = jobsFacade.findAll();
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
            save = true;
            determinePosition();
        } catch (Exception e) {
            System.out.println("*******************************************ERROR_V3: " + e.toString());
        }
    }

    @PostConstruct
    private void postConstruct() {
        save = true;
    }

    public void loadValues() {
        save = true;

        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------
        //******type_id
        try {
            currentIdentification = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentification = 0;
        }
        //******victim_nid
        currentIdentificationNumber = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNid();
        if (currentIdentificationNumber == null) {
            currentIdentificationNumber = "";
        }
        //******victim_firstname
        currentName = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimFirstname();
        if (currentName == null) {
            currentName = "";
        }
        //******victim_lastname
        currentSurname = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimLastname();
        if (currentSurname == null) {
            currentSurname = "";
        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getAgeTypeId();
        } catch (Exception e) {
            currentMeasureOfAge = 0;
        }
        //******victim_age
        try {
            currentAge = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimAge().toString();
        } catch (Exception e) {
            currentAge = "";
        }
        //******gender_id
        try {
            currentGender = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getGenderId().getGenderId();
        } catch (Exception e) {
            currentGender = 0;
        }
        //******job_id
        try {
            currentJob = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getJobId().getJobId();
        } catch (Exception e) {
            currentJob = 0;
        }
        //******vulnerable_group_id
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVulnerableGroupId() != null) {
            currentVulnerableGroup = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVulnerableGroupId().getVulnerableGroupId();
        }
        //******ethnic_group_id
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getEthnicGroupId() != null) {
            currentEthnicGroup = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupId();
        }
        //******victim_telephone
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimTelephone() != null) {
            currentTelephoneHome = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimTelephone();
        }

        //******victim_address
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimAddress() != null) {
            currentDirectionHome = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimAddress();
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId() != null) {
                currentNeighborhoodHomeCode = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodHome = neighborhoodsFacade.find(currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            }
        } catch (Exception e) {
            currentNeighborhoodHomeCode = "";
            currentNeighborhoodHome = "";
        }
        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id
        //******residence_municipality
        try {
            currentMunicipalitie = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
            if (currentMunicipalitie == 1) {
                neighborhoodHomeNameDisabled = false;
            } else {
                neighborhoodHomeNameDisabled = true;
            }
        } catch (Exception e) {
            currentMunicipalitie = 1;
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
            currentHourConsult = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupTime().getHours());
            currentMinuteConsult = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getCheckupTime().getMinutes());
            if (Integer.parseInt(currentHourConsult) > 12) {
                currentHourConsult = String.valueOf(Integer.parseInt(currentHourConsult) - 12);
                currentAmPmConsult = "PM";
            } else {
                currentAmPmConsult = "AM";
            }
            calculateTime2();
        } catch (Exception e) {
            currentHourConsult = "";
            currentMinuteConsult = "";
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
            currentHourEvent = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryTime().getHours());
            currentMinuteEvent = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryTime().getMinutes());
            if (Integer.parseInt(currentHourEvent) > 12) {
                currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) - 12);
                currentAmPmEvent = "PM";
            } else {
                currentAmPmEvent = "AM";
            }
            calculateTime1();
        } catch (Exception e) {
            currentHourEvent = "";
            currentMinuteEvent = "";
        }
        //******injury_address
        currentDirectionEvent = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryAddress();
        if (currentDirectionEvent == null) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            }
        } catch (Exception e) {

            currentNeighborhoodEventCode = "";
            currentNeighborhoodEvent = "";
        }
        //******injury_place_id
        try {
            currentPlace = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceId();
            if (currentPlace == 8) {
                otherPlaceDisabled = false;
            } else {
                otherPlaceDisabled = true;
            }
        } catch (Exception e) {
            currentPlace = 0;
            otherPlaceDisabled = true;
        }
        //******activity_id
        try {
            currentActivities = currentNonFatalDomesticViolence.getNonFatalInjuries().getActivityId().getActivityId();
        } catch (Exception e) {
            currentActivities = 0;
        }
        //******intentionality_id
        //******use_alcohol_id
        try {
            currentUseAlcohol = currentNonFatalDomesticViolence.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsId();
        } catch (Exception e) {
            currentUseAlcohol = 0;
        }
        //******use_drugs_id
        try {
            currentUseDrugs = currentNonFatalDomesticViolence.getNonFatalInjuries().getUseDrugsId().getUseAlcoholDrugsId();
        } catch (Exception e) {
            currentUseDrugs = 0;
        }
        //******burn_injury_degree
        try {
            currentLevelBurned = currentNonFatalDomesticViolence.getNonFatalInjuries().getBurnInjuryDegree();
        } catch (Exception e) {
            currentLevelBurned = 0;
        }
        //******burn_injury_percentage
        try {
            currentPercentBurned = currentNonFatalDomesticViolence.getNonFatalInjuries().getBurnInjuryPercentage().toString();
        } catch (Exception e) {
            currentPercentBurned = "";
        }
        //******submitted_patient
        if (currentNonFatalDomesticViolence.getNonFatalInjuries().getSubmittedPatient() != null) {
            if (currentNonFatalDomesticViolence.getNonFatalInjuries().getSubmittedPatient()) {
                isSubmitted = true;
                currentIPS = currentNonFatalDomesticViolence.getNonFatalInjuries().getEpsId();
                IPSDisabled = false;
            } else {
                isSubmitted = false;
                IPSDisabled = true;
            }
        } else {
            isSubmitted = false;
            IPSDisabled = true;
        }
        //******eps_id
        //******destination_patient_id
        //******input_timestamp
        //******health_professional_id
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            currentMechanisms = currentNonFatalDomesticViolence.getNonFatalInjuries().getMechanismId().getMechanismId();
        } catch (Exception e) {
            currentMechanisms = 0;
        }
        //******user_id
        currentResponsible = "ADMIN";
        //******injury_day_of_week
        currentWeekdayEvent = currentNonFatalDomesticViolence.getNonFatalInjuries().getInjuryDayOfWeek();
        if (currentWeekdayEvent == null) {
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
                    break;
                case 13:
                    isUnknownAction = true;
                    break;

            }
        }
    }

    private boolean saveRegistry() {
        validationsErrors = new ArrayList<String>();
        try {
            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA VICTIMA
            //------------------------------------------------------------
            Victims newVictim = new Victims();
            newVictim.setVictimId(victimsFacade.findMax() + 1);
            if (currentIdentification != 0) {
                newVictim.setTypeId(idTypesFacade.find(currentIdentification));
            }
            if (currentIdentificationNumber.trim().length() != 0) {
                newVictim.setVictimNid(currentIdentificationNumber);
            }
            if (currentName.trim().length() != 0) {
                newVictim.setVictimFirstname(currentName);
            }
            if (currentSurname.trim().length() != 0) {
                newVictim.setVictimLastname(currentSurname);
            }
            if (currentMeasureOfAge != 0) {
                newVictim.setAgeTypeId(currentMeasureOfAge);
            }
            if (currentAge.trim().length() != 0) {
                try {
                    newVictim.setVictimAge(Short.parseShort(currentAge));
                } catch (Exception e) {
                    validationsErrors.add("Corregir valor de: Edad Cantidad");
                }
            }
            if (currentGender != 0) {
                newVictim.setGenderId(gendersFacade.find(currentGender));
            }
            if (currentJob != 0) {
                newVictim.setJobId(jobsFacade.find(currentJob));
            }
            if (currentVulnerableGroup != 0) {
                newVictim.setVulnerableGroupId(vulnerableGroupsFacade.find(currentVulnerableGroup));
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
            newVictim.setResidenceMunicipality(currentMunicipalitie);

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------
            NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();


            newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 53));//es 53 por ser vif

            newNonFatalInjuries.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);

            if (currentDateConsult.trim().length() != 0) {
                newNonFatalInjuries.setCheckupDate(formato.parse(currentDateEvent));
            }
            if (currentMilitaryHourConsult.trim().length() != 0) {
                try {
                    if (currentAmPmConsult.compareTo("PM") == 0) {
                        currentHourConsult = String.valueOf(Integer.parseInt(currentHourConsult) + 12);
                    }
                    int hourInt = Integer.parseInt(currentHourConsult);
                    int minuteInt = Integer.parseInt(currentMinuteConsult);
                    if (hourInt > 12 && hourInt < 0) {
                        validationsErrors.add("Corregir la hora de consulta");
                    } else {
                        if (minuteInt > 59 && minuteInt < 0) {
                            validationsErrors.add("Corregir la hora de consulta");
                        } else {
                            newNonFatalInjuries.setCheckupTime(new Time(hourInt, minuteInt, 0));
                        }
                    }
                } catch (Exception e) {
                    validationsErrors.add("Corregir la hora del consulta");
                }
            }
            if (currentDateEvent.trim().length() != 0) {
                newNonFatalInjuries.setInjuryDate(formato.parse(currentDateEvent));
            }
            if (currentMilitaryHourEvent.trim().length() != 0) {
                try {
                    if (currentAmPmEvent.compareTo("PM") == 0) {
                        currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) + 12);
                    }
                    int hourInt = Integer.parseInt(currentHourEvent);
                    int minuteInt = Integer.parseInt(currentMinuteEvent);
                    if (hourInt > 12 && hourInt < 0) {
                        validationsErrors.add("Corregir la hora de consulta");
                    } else {
                        if (minuteInt > 59 && minuteInt < 0) {
                            validationsErrors.add("Corregir la hora del hecho");
                        } else {
                            newNonFatalInjuries.setInjuryTime(new Time(hourInt, minuteInt, 0));
                        }
                    }
                } catch (Exception e) {
                    validationsErrors.add("Corregir la hora del hecho");
                }
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
            if (isSubmitted) {
                newNonFatalInjuries.setSubmittedPatient(isSubmitted);
                newNonFatalInjuries.setEpsId(currentIPS);
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

            newNonFatalInjuries.setUserId(usersFacade.find(1));

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

            //-------------------------------------------------------------------------------
            if (validationsErrors.isEmpty()) {
                openDialogFirst = "";
                openDialogNext = "";
                openDialogLast = "";
                openDialogPrevious = "";
                openDialogNew = "";
                openDialogDelete = "";
                if (currentNonFatalInjuriId == -1) {//ES UN NUEVO REGISTRO SE DEBE PERSISTIR
                    //System.out.println("guardando nuevo registro");
                    victimsFacade.create(newVictim);
                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
                    nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    save = true;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR
                    //System.out.println("actualizando registro existente");
                    updateRegistry(newVictim, newNonFatalInjuries, newNonFatalDomesticViolence);
                    save = true;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ACTUALIZADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                return true;
            } else {
                for (int i = 0; i < validationsErrors.size(); i++) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", validationsErrors.get(i));
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println("*******************************************ERROR_V2: " + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }
    }

    private void updateRegistry(Victims victim, NonFatalInjuries fatalInjurie, NonFatalDomesticViolence nonFatalDomesticViolence) {
        try {
            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA VICTIMA
            //------------------------------------------------------------
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setTypeId(victim.getTypeId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimNid(victim.getVictimNid());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimFirstname(victim.getVictimFirstname());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimLastname(victim.getVictimLastname());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setAgeTypeId(victim.getAgeTypeId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVictimAge(victim.getVictimAge());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setGenderId(victim.getGenderId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setJobId(victim.getJobId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId().setVulnerableGroupId(victim.getVulnerableGroupId());
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
            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------
            currentNonFatalDomesticViolence.getNonFatalInjuries().setCheckupDate(fatalInjurie.getCheckupDate());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setCheckupTime(fatalInjurie.getCheckupTime());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryDate(fatalInjurie.getInjuryDate());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryTime(fatalInjurie.getInjuryTime());

            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryAddress(fatalInjurie.getInjuryAddress());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryNeighborhoodId(fatalInjurie.getInjuryNeighborhoodId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryPlaceId(fatalInjurie.getInjuryPlaceId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setActivityId(fatalInjurie.getActivityId());
            //newNonFatalInjuries.setIntentionalityId();
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUseAlcoholId(fatalInjurie.getUseAlcoholId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUseDrugsId(fatalInjurie.getUseDrugsId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setBurnInjuryDegree(fatalInjurie.getBurnInjuryDegree());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setBurnInjuryPercentage(fatalInjurie.getBurnInjuryPercentage());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setSubmittedPatient(fatalInjurie.getSubmittedPatient());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setEpsId(fatalInjurie.getEpsId());
            //newNonFatalInjuries.setDestinationPatientId(null);            
            //newNonFatalInjuries.setHealthProfessionalId(null);
            currentNonFatalDomesticViolence.getNonFatalInjuries().setNonFatalDataSourceId(fatalInjurie.getNonFatalDataSourceId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setMechanismId(fatalInjurie.getMechanismId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setUserId(fatalInjurie.getUserId());
            currentNonFatalDomesticViolence.getNonFatalInjuries().setInjuryDayOfWeek(fatalInjurie.getInjuryDayOfWeek());

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------
            //nonFatalDomesticViolence.setNonFatalInjuryId(nonFatalDomesticViolence.getNonFatalInjuryId());
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
        if (saveRegistry()) {
            previous();
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
        previous();
    }

    public void noSaveAndGoFirst() {//va al primero sin guardar cambios si se han realizado
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        save = true;
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
        last();
    }

    public void next() {
        if (save) {//se busca el siguiente se el registro esta guardado (si esta guardado se abrira un dialogo que pregunta si guardar)             
            //System.out.println("cargando siguiente registro");
            if (currentNonFatalInjuriId == -1) {//esta en registro nuevo                
            } else {
                auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findNext(currentNonFatalInjuriId);
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
            } else {
                auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findPrevious(currentNonFatalInjuriId);
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
            auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findFirst();
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
            auxFatalDomesticViolence = nonFatalDomesticViolenceFacade.findLast();
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
        //System.out.println("Limpiando formulario");


        currentDomesticViolenceDataSource = 1;//IMPORTANTE!!!

        currentEthnicGroup = 0;
        otherEthnicGroup = "";
        otherEthnicGroupDisabled = true;
        currentVulnerableGroup = 0;
        otherVulnerableGroup = "";
        otherVulnerableGroupDisabled = true;
        currentResponsible = "";

        currentDepartamentHome = 52;
        currentMunicipalitie = 1;
        currentDirectionHome = "";
        currentTelephoneHome = "";

        isSubmitted = false;
        IPSDisabled = true;
        currentIPS = 0;

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
        disasterWhich = "";
        disasterWhichDisabled = true;
        otherMechanism = "";
        otherMechanismDisabled = true;

        currentPercentBurned = "";

        currentUseAlcohol = 0;
        currentUseDrugs = 0;

        currentIdentification = 0;
        currentIdentificationNumber = "";
        currentName = "";
        currentSurname = "";
        currentMeasureOfAge = 0;
        currentAge = "";
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

        otherPlaceDisabled = false;

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
        findMunicipalities();
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

    public void deleteRegistry() {
//        if (currentFatalInjuriId != -1) {
//            fatalInjuryMurderFacade.remove(currentNonFatalDomesticViolence);
//            System.out.println("registro eliminado");
//        } else {
//            System.out.println("Se esta actualmente en un nuevo registro, no se puede eliminar");
//        }
//        //System.out.println("eliminando registro: '" + openDialogDelete + "'");
    }

    public void determinePosition() {

        totalRegisters = nonFatalDomesticViolenceFacade.count();
        if (currentNonFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = nonFatalDomesticViolenceFacade.findPosition(currentNonFatalDomesticViolence.getNonFatalInjuryId());
            currentPosition = position + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "dialogDelete.show();";
        }
        if (!save) {
            currentPosition = currentPosition + " *";
        }
        //System.out.println("POSICION DETERMINADA: " + currentPosition);
        //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Posicion", String.valueOf(currentFatalInjuriId));
        //FacesContext.getCurrentInstance().addMessage(null, msg);
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
    public void changeForm() {//el formulario fue modificado        
        openDialogFirst = "dialogFirst.show();";
        openDialogNext = "dialogNext.show();";
        openDialogLast = "dialogLast.show();";
        openDialogPrevious = "dialogPrevious.show();";
        openDialogNew = "dialogNew.show();";
        openDialogDelete = "dialogDelete.show();";
        save = false;
        determinePosition();
        //System.out.println("Aqui cambio: " + currentPosition);
    }

    public void changeValueAge() {
        try {
            int ageInt = Integer.parseInt(currentAge);
            if (ageInt < 0) {
                currentAge = "";
            }

        } catch (Exception e) {
            currentAge = "";
        }
    }

    public void changeDayEvent() {
        try {
            int dayInt = Integer.parseInt(currentDayEvent);
            if (dayInt < 1 || dayInt > 31) {
                currentDayEvent = "";
            }
        } catch (Exception e) {
            currentDayEvent = "";
        }
        calculateDate1();
    }

    public void changeMonthEvent() {
        try {
            int monthInt = Integer.parseInt(currentMonthEvent);
            if (monthInt < 1 || monthInt > 12) {
                currentMonthEvent = "";
            }
        } catch (Exception e) {
            currentMonthEvent = "";
        }
        calculateDate1();
    }

    public void changeYearEvent() {
        try {
            int yearInt = Integer.parseInt(currentYearEvent);
            if (yearInt < 0) {
                currentYearEvent = "";
            }

        } catch (Exception e) {
            currentYearEvent = "";
        }
        calculateDate1();
    }

    public void changeHourEvent() {
        try {
            int hourInt = Integer.parseInt(currentHourEvent);
            if (hourInt < 0 || hourInt > 12) {
                currentHourEvent = "";
            }

        } catch (Exception e) {
            currentHourEvent = "";
        }
        calculateTime1();
    }

    public void changeMinuteEvent() {
        try {
            int minuteInt = Integer.parseInt(currentMinuteEvent);
            if (minuteInt < 0 || minuteInt > 59) {
                currentMinuteEvent = "";
            }

        } catch (Exception e) {
            currentMinuteEvent = "";
        }
        calculateTime1();
    }

    public void changeDayConsult() {
        try {
            int dayInt = Integer.parseInt(currentDayConsult);
            if (dayInt < 1 || dayInt > 31) {
                currentDayConsult = "";
            }
        } catch (Exception e) {
            currentDayConsult = "";
        }
        calculateDate2();
    }

    public void changeMonthConsult() {
        try {
            int monthInt = Integer.parseInt(currentMonthConsult);
            if (monthInt < 1 || monthInt > 12) {
                currentMonthConsult = "";
            }
        } catch (Exception e) {
            currentMonthConsult = "";
        }
        calculateDate2();
    }

    public void changeYearConsult() {
        try {
            int yearInt = Integer.parseInt(currentYearConsult);
            if (yearInt < 0) {
                currentYearConsult = "";
            }

        } catch (Exception e) {
            currentYearConsult = "";
        }
        calculateDate2();
    }

    public void changeHourConsult() {
        try {
            int hourInt = Integer.parseInt(currentHourConsult);
            if (hourInt < 0 || hourInt > 12) {
                currentHourConsult = "";
            }

        } catch (Exception e) {
            currentHourConsult = "";
        }
        calculateTime2();
    }

    public void changeMinuteConsult() {
        try {
            int minuteInt = Integer.parseInt(currentMinuteConsult);
            if (minuteInt < 0 || minuteInt > 59) {
                currentMinuteConsult = "";
            }

        } catch (Exception e) {
            currentMinuteConsult = "";
        }
        calculateTime2();
    }

    public void findMunicipalities() {
        if (!save) {
            changeForm();
        }
        Departaments d = departamentsFacade.findById(currentDepartamentHome);
        municipalities = new SelectItem[d.getMunicipalitiesList().size()];
        for (int i = 0; i < municipalities.length; i++) {
            municipalities[i] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
        }
        currentMunicipalitie = d.getMunicipalitiesList().get(0).getMunicipalitiesPK().getMunicipalityId();
        if (currentMunicipalitie == 1 && currentDepartamentHome == 52) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    public void findMunicipalitieCode() {
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
        changeForm();
        if (currentMunicipalitie == 1 && currentDepartamentHome == 52) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    public void changeNeighborhoodHomeName() {
        changeForm();
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = String.valueOf(neighborhoodsList.get(i).getNeighborhoodId());
                break;
            }
        }
    }

    public void changeSubmitted() {
        changeForm();
        if (isSubmitted) {
            IPSDisabled = false;
        } else {
            IPSDisabled = true;
            currentIPS = 0;
        }
    }

    public void changeNeighborhoodEvent() {
        changeForm();
        Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
        if (n != null) {
            currentNeighborhoodEventCode = String.valueOf(n.getNeighborhoodId());
        } else {
            currentNeighborhoodEventCode = "";
        }
    }

    public void changeMechanisms() {
        changeForm();
        heightWhichDisabled = true;
        powderWhichDisabled = true;
        otherMechanismDisabled = true;
        disasterWhichDisabled = true;
        forBurned = "none";
        heightWhich = "";
        powderWhich = "";
        otherMechanism = "";
        disasterWhich = "";

        if (currentMechanisms == 5) {//"Otra caida, altura")
            heightWhichDisabled = false;
        } else if (currentMechanisms == 12) {//Pólvora, cual"
            powderWhichDisabled = false;
        } else if (currentMechanisms == 26) {//Desastre
            disasterWhichDisabled = false;
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
        changeForm();
        if (currentEthnicGroup == 3) {//3. otro
            otherEthnicGroupDisabled = false;

        } else {
            otherEthnicGroupDisabled = true;
            otherEthnicGroup = "";
        }
    }

    public void changeVulnerableGroup() {
        changeForm();
        if (currentVulnerableGroup == 98) {//98. otro
            otherVulnerableGroupDisabled = false;

        } else {
            otherVulnerableGroupDisabled = true;
            otherVulnerableGroup = "";
        }
    }

    public void changeMeasuresOfAge() {
        changeForm();
        if (currentMeasureOfAge == 4) {//4. otro
            valueAgeDisabled = true;

        } else {
            valueAgeDisabled = false;
            currentAge = "";
        }
    }

    public void changeActivities() {
        changeForm();
        if (currentActivities == 98) {//98. otra cual?
            otherActivityDisabled = false;
        } else {
            otherActivityDisabled = true;
        }
    }

    public void changePlace() {
        changeForm();
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
            if (heightWhichInt < 0) {
                heightWhich = "";
            }
        } catch (Exception e) {
            heightWhich = "";
        }
    }

    public void changeOtherAG() {
        changeForm();
        if (isAG8) {
            otherAGDisabled = false;

        } else {
            otherAGDisabled = true;
            otherAG = "";
        }
    }

    public void changeOtherMA() {
        changeForm();
        if (isMA8) {
            otherMADisabled = false;

        } else {
            otherMADisabled = true;
            otherMA = "";
        }
    }

    public void changeOtherAction() {
        changeForm();
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
        int timeInt = 0;
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

    public SelectItem[] getIPSs() {
        return IPSs;
    }

    public void setIPSs(SelectItem[] IPSs) {
        this.IPSs = IPSs;
    }

    public boolean isIsSubmitted() {
        return isSubmitted;
    }

    public void setIsSubmitted(boolean isSubmitted) {
        this.isSubmitted = isSubmitted;
    }

    public boolean isIPSDisabled() {
        return IPSDisabled;
    }

    public void setIPSDisabled(boolean IPSDisabled) {
        this.IPSDisabled = IPSDisabled;
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

    public String getDisasterWhich() {
        return disasterWhich;
    }

    public void setDisasterWhich(String disasterWhich) {
        this.disasterWhich = disasterWhich;
    }

    public boolean isDisasterWhichDisabled() {
        return disasterWhichDisabled;
    }

    public void setDisasterWhichDisabled(boolean disasterWhichDisabled) {
        this.disasterWhichDisabled = disasterWhichDisabled;
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

    public Short getCurrentIPS() {
        return currentIPS;
    }

    public void setCurrentIPS(Short currentIPS) {
        this.currentIPS = currentIPS;
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

    public String getCurrentSurname() {
        return currentSurname;
    }

    public void setCurrentSurname(String currentSurname) {
        this.currentSurname = currentSurname;
    }

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
}
