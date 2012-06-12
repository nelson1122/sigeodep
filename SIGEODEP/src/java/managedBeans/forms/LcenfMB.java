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
@ManagedBean(name = "lcenfMB")
@SessionScoped
public class LcenfMB {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @EJB
    DepartamentsFacade departamentsFacade;
    private Short currentDepartamentHome = 52;//nariño
    private SelectItem[] departaments;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    //--------------------    
    @EJB
    UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    private Short currentUseAlcohol = 0;
    private Short currentUseDrugs = 0;
    private SelectItem[] useAlcohol;
    private SelectItem[] useDrugs;
    //--------------------    
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private Short currentIPS = 0;
    private SelectItem[] IPSs;
    //------------------
    @EJB
    BooleanPojoFacade booleanPojoFacade;
    private SelectItem[] booleans;
    private Short previousAttempt = 0;
    private Short mentalPastDisorder = 0;
    //--------------------
    @EJB
    IntentionalitiesFacade intentionalitiesFacade;
    private Short currentIntentionality = 0;
    private SelectItem[] intentionalities;
    //--------------------
    @EJB
    NonFatalPlacesFacade nonFatalPlacesFacade;
    private Short currentPlace = 0;
    private SelectItem[] places;
    //--------------------
    @EJB
    ActivitiesFacade activitiesFacade;
    private Short currentActivities = 0;
    private SelectItem[] activities;
    //--------------------
    @EJB
    MechanismsFacade mechanismsFacade;
    private Short currentMechanisms = 0;
    private SelectItem[] mechanisms;
    //--------------------
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    private Short currentEthnicGroup = 0;
    private SelectItem[] ethnicGroups;
    private boolean ethnicGroupsDisabled = true;
    private String otherEthnicGroup = "";
    //--------------------
    @EJB
    TransportTypesFacade transportTypesFacade;
    private Short currentTransportTypes = 0;
    private SelectItem[] transportTypes;
    //--------------------
    @EJB
    TransportCounterpartsFacade transportCounterpartsFacade;
    private Short currentTransportUser = 0;
    private SelectItem[] transportCounterparts;
    //--------------------
    @EJB
    TransportUsersFacade transportUsersFacade;
    private Short currentTransportCounterpart = 0;
    private SelectItem[] transportUsers;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender = 0;
    private SelectItem[] genders;
    //--------------------
    @EJB
    RelationshipsToVictimFacade relationshipsToVictimFacade;
    private Short currentRelationshipToVictim = 0;
    private SelectItem[] relationshipsToVictim;
    //--------------------
    @EJB
    ContextsFacade contextsFacade;
    private Short currentContext = 0;
    private SelectItem[] contexts;
    //--------------------
    @EJB
    AggressorGendersFacade agreAggressorGendersFacade;
    private Short currentAggressorGenders = 0;
    private SelectItem[] aggressorGenders;
    //--------------------
    @EJB
    PrecipitatingFactorsFacade precipitatingFactorsFacade;
    private Short currentPrecipitatingFactor = 0;
    private SelectItem[] precipitatingFactors;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob = 0;
    private SelectItem[] jobs;
    //--------------------
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    private Short currentDestinationPatient = 0;
    private SelectItem[] destinationsPatient;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome = "";
    private String currentNeighborhoodHomeCode = "";
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    boolean neighborhoodHomeNameDisabled = true;
    //--------------------
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    private String currentHealthProfessionals = "";
    //--------------------
    @EJB
    DiagnosesFacade diagnosesFacade;
    private String currentDiagnoses = "";
    private SelectItem[] diagnoses;
    private SelectItem[] diagnosesAux;
    //--------------------
    private SelectItem[] healthInstitutions;
    private Short currentHealthInstitution = 0;
    //------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identifications;
    private Short currentIdentification = 0;
    //------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge = 0;
    private String currentAge = "";
    private boolean valueAgeDisabled = false;
    //------------------
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    //-------
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    AggressorTypesFacade aggressorTypesFacade;
    @EJB
    AnatomicalLocationsFacade anatomicalLocationsFacade;
    @EJB
    KindsOfInjuryFacade kindsOfInjuryFacade;
    @EJB
    AbuseTypesFacade abuseTypesFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    //-------
    private boolean isSubmitted = false;
    private boolean IPSDisabled = true;
    //-------
    private boolean otherIntentDisabled = true;
    private boolean otherPlaceDisabled = true;
    private boolean otherActivityDisabled = true;
    private boolean otherMechanismDisabled = true;//otro mecanismo    
    private boolean otherAGDisabled = true;
    private boolean otherMADisabled = true;
    private String otherMA = "";
    private String otherMechanism = "";//otro mecanismo       
    private boolean powderWhichDisabled = true;//cual polvora
    private String powderWhich = "";//cual polvora
    private boolean disasterWhichDisabled = true;//cual desastre
    private String disasterWhich = "";//cual desastre
    private boolean heightWhichDisabled = true;//cual altura
    private String heightWhich = "";//cual altura    
    private String forBurned = "none";//para los quemados
    private String displaySecurityElements = "none";
    private String displayInterpersonalViolence = "none";
    private String displayTransport = "none";
    private String displayIntentional = "none";
    private String displayDomesticViolence = "none";
    private boolean otherTransportTypeDisabled = true;//otro tipo de transporte
    private String otherTransportType = "";//otro tipo de transporte    
    private boolean otherTransportCounterpartsTypeDisabled = true;//otro tipo de transporte contraparte
    private String otherTransportCounterpartsType = "";//otro tipo de transporte contraparte   
    private boolean otherTransportUserTypeDisabled = true;//otro tipo de transporte usuario
    private String otherTransportUserType = "";//otro tipo de transporte usuario   
    private boolean aggressionPast = false;
    private String otherFactor = "";
    private boolean otherFactorDisabled = true;
    private boolean relationshipToVictimDisabled = true;
    private boolean contextDisabled = true;
    private String otherRelation = "";
    private boolean otherRelationDisabled = true;
    private boolean aggressorGendersDisabled = true;
    private boolean checkOtherInjury = false;
    private boolean checkOtherPlace = false;
    private boolean otherInjuryDisabled = true;
    private boolean otherInjuryPlaceDisabled = true;
    private boolean otherDestinationPatientDisabled = true;
    private String otherDestinationPatient = "";
    private String txtOtherInjury = "";
    private String txtOtherPlace = "";
    private String txtCIE10_1 = "";
    private String txtCIE10_2 = "";
    private String txtCIE10_3 = "";
    private String txtCIE10_4 = "";
    private String idCIE10_1 = "";
    private String idCIE10_2 = "";
    private String idCIE10_3 = "";
    private String idCIE10_4 = "";
    private String currentSecurityElements = "";
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
    private String currentSurame = "";
    private String currentIdentificationNumber = "";
    private String currentDirectionHome = "";
    private String currentTelephoneHome = "";
    private String currentDirectionEvent = "";
    private String currentOtherIntentionality = "";
    private String currentOtherPlace = "";
    private String currentOtherActivitie = "";
    private String currentSurname = "";
    private Short currentLevelBurned = 0;
    private String currentPercentBurned = "";
    private boolean isDisplaced = false;
    private boolean isHandicapped = false;
    private boolean isBeltUse = false;
    private boolean isHelmetUse = false;
    private boolean isBicycleHelmetUse = false;
    private boolean isVestUse = false;
    private boolean isOtherElementUse = false;
    private boolean isUnknownNatureOfInjurye = false;
    private boolean isNatureOfInjurye1 = false;
    private boolean isNatureOfInjurye2 = false;
    private boolean isNatureOfInjurye3 = false;
    private boolean isNatureOfInjurye4 = false;
    private boolean isNatureOfInjurye5 = false;
    private boolean isNatureOfInjurye6 = false;
    private boolean isNatureOfInjurye7 = false;
    private boolean isNatureOfInjurye8 = false;
    private boolean isNatureOfInjurye9 = false;
    private boolean isAnatomicalSite1 = false;
    private boolean isAnatomicalSite2 = false;
    private boolean isAnatomicalSite3 = false;
    private boolean isAnatomicalSite4 = false;
    private boolean isAnatomicalSite5 = false;
    private boolean isAnatomicalSite6 = false;
    private boolean isAnatomicalSite7 = false;
    private boolean isAnatomicalSite8 = false;
    private boolean isAnatomicalSite9 = false;
    private boolean isAnatomicalSite10 = false;
    private boolean isAnatomicalSite11 = false;
    private boolean isAG1 = false;
    private boolean isAG2 = false;
    private boolean isAG3 = false;
    private boolean isAG4 = false;
    private boolean isAG5 = false;
    private boolean isAG6 = false;
    private boolean isAG7 = false;
    private boolean isAG8 = false;
    private boolean isAG10 = false;
    private boolean isUnknownAG = false;
    private String otherAG = "";
    private boolean isMA1 = false;
    private boolean isMA2 = false;
    private boolean isMA3 = false;
    private boolean isMA4 = false;
    private boolean isMA5 = false;
    private boolean isMA6 = false;
    private boolean isUnknownMA = false;
    private boolean isMA8 = false;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private int CIE_selected = 1;
    private boolean save = true;//variable que me dice si el registro esta guadado o no    
    private int currentNonFatalInjuriId = -1;//registro actual 
    private NonFatalInjuries currentNonFatalInjury;
    private NonFatalInjuries auxNonFatalInjury;
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
    public LcenfMB() {
    }

    public void reset() {

        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        try {
            //cargo las instituciones de salud e IPS
            List<NonFatalDataSources> sourcesList = nonFatalDataSourcesFacade.findAll();
            IPSs = new SelectItem[sourcesList.size() + 1];
            IPSs[0] = new SelectItem(0, "");
            healthInstitutions = new SelectItem[sourcesList.size() + 1];
            healthInstitutions[0] = new SelectItem(0, "");
            for (int i = 0; i < sourcesList.size(); i++) {
                IPSs[i + 1] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceId(), sourcesList.get(i).getNonFatalDataSourceName());
                healthInstitutions[i + 1] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceId(), sourcesList.get(i).getNonFatalDataSourceName());
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

            //cargo los destinos del paciente
            List<DestinationsOfPatient> destinationsList = destinationsOfPatientFacade.findAll();
            destinationsPatient = new SelectItem[destinationsList.size() + 1];
            destinationsPatient[0] = new SelectItem(0, "");
            for (int i = 0; i < destinationsList.size(); i++) {
                destinationsPatient[i + 1] = new SelectItem(destinationsList.get(i).getDestinationPatientId(), destinationsList.get(i).getDestinationPatientName());
            }

            //cargo los departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            departaments = new SelectItem[departamentsList.size()];
            for (int i = 0; i < departamentsList.size(); i++) {
                departaments[i] = new SelectItem(departamentsList.get(i).getDepartamentId(), departamentsList.get(i).getDepartamentName());
            }

            //cargo los municipios
            findMunicipalities();


            //cargo las intencionalidades
            List<Intentionalities> intentionalitiesList = intentionalitiesFacade.findAll();
            intentionalities = new SelectItem[intentionalitiesList.size() + 1];
            intentionalities[0] = new SelectItem(0, "");
            for (int i = 0; i < intentionalitiesList.size(); i++) {
                intentionalities[i + 1] = new SelectItem(intentionalitiesList.get(i).getIntentionalityId(), intentionalitiesList.get(i).getIntentionalityName());
            }

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

            //cargo los tipos de transporte en lesiones de tránsito
            List<TransportTypes> transportTypesList = transportTypesFacade.findAll();
            transportTypes = new SelectItem[transportTypesList.size() + 1];
            transportTypes[0] = new SelectItem(0, "");
            for (int i = 0; i < transportTypesList.size(); i++) {
                transportTypes[i + 1] = new SelectItem(transportTypesList.get(i).getTransportTypeId(), transportTypesList.get(i).getTransportTypeName());
            }

            //cargo los Tipos de transporte de la contraparte en lesiones de tránsito y transporte.
            List<TransportCounterparts> transportCounterpartsList = transportCounterpartsFacade.findAll();
            transportCounterparts = new SelectItem[transportCounterpartsList.size() + 1];
            transportCounterparts[0] = new SelectItem(0, "");
            for (int i = 0; i < transportCounterpartsList.size(); i++) {
                transportCounterparts[i + 1] = new SelectItem(transportCounterpartsList.get(i).getTransportCounterpartId(), transportCounterpartsList.get(i).getTransportCounterpartName());
            }

            //cargo los usuarios en una lesion de tránsito y trasporte
            List<TransportUsers> transportUsersList = transportUsersFacade.findAll();
            transportUsers = new SelectItem[transportUsersList.size() + 1];
            transportUsers[0] = new SelectItem(0, "");
            for (int i = 0; i < transportUsersList.size(); i++) {
                transportUsers[i + 1] = new SelectItem(transportUsersList.get(i).getTransportUserId(), transportUsersList.get(i).getTransportUserName());
            }

            //cargo las relaciones entre agresos y victima
            List<RelationshipsToVictim> relationshipsToVictimList = relationshipsToVictimFacade.findAll();
            relationshipsToVictim = new SelectItem[relationshipsToVictimList.size() + 1];
            relationshipsToVictim[0] = new SelectItem(0, "");
            for (int i = 0; i < relationshipsToVictimList.size(); i++) {
                relationshipsToVictim[i + 1] = new SelectItem(relationshipsToVictimList.get(i).getRelationshipVictimId(), relationshipsToVictimList.get(i).getRelationshipVictimName());
            }

            //cargo los contextos en que ocurrió una lesión
            List<Contexts> contextsList = contextsFacade.findAll();
            contexts = new SelectItem[contextsList.size() + 1];
            contexts[0] = new SelectItem(0, "");
            for (int i = 0; i < contextsList.size(); i++) {
                contexts[i + 1] = new SelectItem(contextsList.get(i).getContextId(), contextsList.get(i).getContextName());
            }

            //cargo el genero de el/los agresor/es
            List<AggressorGenders> aggressorGendersList = agreAggressorGendersFacade.findAll();
            aggressorGenders = new SelectItem[aggressorGendersList.size() + 1];
            aggressorGenders[0] = new SelectItem(0, "");
            for (int i = 0; i < aggressorGendersList.size(); i++) {
                aggressorGenders[i + 1] = new SelectItem(aggressorGendersList.get(i).getGenderId(), aggressorGendersList.get(i).getGenderName());
            }

            //cargo los Factores precipitantes en lesiones autoinflingidas.
            List<PrecipitatingFactors> precipitatingFactorsList = precipitatingFactorsFacade.findAll();
            precipitatingFactors = new SelectItem[precipitatingFactorsList.size() + 1];
            precipitatingFactors[0] = new SelectItem(0, "");
            for (int i = 0; i < precipitatingFactorsList.size(); i++) {
                precipitatingFactors[i + 1] = new SelectItem(precipitatingFactorsList.get(i).getPrecipitatingFactorId(), precipitatingFactorsList.get(i).getPrecipitatingFactorName());
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

            //listado de diagnosticos
            List<Diagnoses> diagnosesList = diagnosesFacade.findAll();
            diagnoses = new SelectItem[diagnosesList.size()];
            for (int i = 0; i < diagnosesList.size(); i++) {
                diagnoses[i] = new SelectItem(diagnosesList.get(i).getDiagnosisId() + " - " + diagnosesList.get(i).getDiagnosisName());
            }

            //categoria boolean
            List<BooleanPojo> booleanList = booleanPojoFacade.findAll();
            booleans = new SelectItem[booleanList.size() + 1];
            booleans[0] = new SelectItem(0, "");
            for (int i = 0; i < booleanList.size(); i++) {
                booleans[i + 1] = new SelectItem(booleanList.get(i).getBooleanId(), booleanList.get(i).getBooleanName());
            }

            currentDiagnoses = "S000";
            save = true;
            determinePosition();
        } catch (Exception e) {
            System.out.println("*******************************************ERROR_L1: " + e.toString());
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
            currentIdentification = currentNonFatalInjury.getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentification = 0;
        }
        //******victim_nid
        currentIdentificationNumber = currentNonFatalInjury.getVictimId().getVictimNid();
        if (currentIdentificationNumber == null) {
            currentIdentificationNumber = "";
        }
        //******victim_firstname
        currentName = currentNonFatalInjury.getVictimId().getVictimFirstname();
        if (currentName == null) {
            currentName = "";
        }
        //******victim_lastname
        currentSurname = currentNonFatalInjury.getVictimId().getVictimLastname();
        if (currentSurname == null) {
            currentSurname = "";
        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentNonFatalInjury.getVictimId().getAgeTypeId();
        } catch (Exception e) {
            currentMeasureOfAge = 0;
        }
        //******victim_age
        try {
            currentAge = currentNonFatalInjury.getVictimId().getVictimAge().toString();
        } catch (Exception e) {
            currentAge = "";
        }
        //******gender_id
        try {
            currentGender = currentNonFatalInjury.getVictimId().getGenderId().getGenderId();
        } catch (Exception e) {
            currentGender = 0;
        }
        //******job_id
        try {
            currentJob = currentNonFatalInjury.getVictimId().getJobId().getJobId();
        } catch (Exception e) {
            currentJob = 0;
        }
        //******vulnerable_group_id	
        //******ethnic_group_id
        //******victim_telephone
        //******victim_address
        if (currentNonFatalInjury.getVictimId().getVictimAddress() != null) {
            currentDirectionHome = currentNonFatalInjury.getVictimId().getVictimAddress();
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalInjury.getVictimId().getVictimNeighborhoodId().getNeighborhoodId() != null) {
                currentNeighborhoodHomeCode = String.valueOf(currentNonFatalInjury.getVictimId().getVictimNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodHome = neighborhoodsFacade.find(currentNonFatalInjury.getVictimId().getVictimNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
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
            currentMunicipalitie = currentNonFatalInjury.getVictimId().getResidenceMunicipality();
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
        //******injury_date
        try {
            currentDateEvent = currentNonFatalInjury.getInjuryDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentNonFatalInjury.getInjuryDate());
            currentDayEvent = String.valueOf(cal.get(Calendar.DATE));
            currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate1();
        } catch (Exception e) {
            currentDateEvent = "";
        }
        //******injury_time
        try {
            currentHourEvent = String.valueOf(currentNonFatalInjury.getInjuryTime().getHours());
            currentMinuteEvent = String.valueOf(currentNonFatalInjury.getInjuryTime().getMinutes());
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
        currentDirectionEvent = currentNonFatalInjury.getInjuryAddress();
        if (currentDirectionEvent == null) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalInjury.getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentNonFatalInjury.getInjuryNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentNonFatalInjury.getInjuryNeighborhoodId()).getNeighborhoodName();
            }
        } catch (Exception e) {
            currentNeighborhoodEventCode = "";
            currentNeighborhoodEvent = "";
        }
        //******injury_place_id
        try {
            currentPlace = currentNonFatalInjury.getInjuryPlaceId().getNonFatalPlaceId();
        } catch (Exception e) {
            currentPlace = 0;
        }
        //******victim_number	
        //******injury_description
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        currentWeekdayEvent = currentNonFatalInjury.getInjuryDayOfWeek();
        if (currentWeekdayEvent == null) {
            currentWeekdayEvent = "";
        }
        //******victim_id
        //******fatal_injury_id
        currentHealthInstitution=currentNonFatalInjury.getNonFatalDataSourceId();
        if (currentHealthInstitution == null) {
            currentHealthInstitution=0;
        }
        //******alcohol_level_victim_id, alcohol_level_victim
        //******code
        //******area_id
        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------

        //cargo la lista de agresores-----------------------------------
        List<AggressorTypes> aggressorTypesList = currentNonFatalInjury.getNonFatalDomesticViolence().getAggressorTypesList();
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

        List<AbuseTypes> abuseTypesList = currentNonFatalInjury.getNonFatalDomesticViolence().getAbuseTypesList();

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

        //cargo sitio anatomico
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------

        List<AnatomicalLocations> anatomicalLocationsList = currentNonFatalInjury.getAnatomicalLocationsList();
        isAnatomicalSite1 = false;
        isAnatomicalSite2 = false;
        isAnatomicalSite3 = false;
        isAnatomicalSite4 = false;
        isAnatomicalSite5 = false;
        isAnatomicalSite6 = false;
        isAnatomicalSite7 = false;
        isAnatomicalSite8 = false;
        isAnatomicalSite9 = false;
        isAnatomicalSite10 = false;
        isAnatomicalSite11 = false;
        checkOtherPlace = false;
        for (int i = 0; i < anatomicalLocationsList.size(); i++) {
            int caso = (int) anatomicalLocationsList.get(i).getAnatomicalLocationId();
            switch (caso) {
                case 1:
                    isAnatomicalSite1 = true;
                    break;
                case 2:
                    isAnatomicalSite2 = true;
                    break;
                case 3:
                    isAnatomicalSite3 = true;
                    break;
                case 4:
                    isAnatomicalSite4 = true;
                    break;
                case 5:
                    isAnatomicalSite5 = true;
                    break;
                case 6:
                    isAnatomicalSite6 = true;
                    break;
                case 7:
                    isAnatomicalSite7 = true;
                    break;
                case 8:
                    isAnatomicalSite8 = true;
                    break;
                case 9:
                    isAnatomicalSite9 = true;
                    break;
                case 10:
                    isAnatomicalSite10 = true;
                    break;
                case 11:
                    isAnatomicalSite11 = true;
                    break;
                case 12:
                    checkOtherPlace = true;
                    break;
            }
        }

        //cargo la naturaleza de la lesion
        List<KindsOfInjury> kindsOfInjuryList = currentNonFatalInjury.getKindsOfInjuryList();
        isNatureOfInjurye1 = false;
        isNatureOfInjurye2 = false;
        isNatureOfInjurye3 = false;
        isNatureOfInjurye4 = false;
        isNatureOfInjurye5 = false;
        isNatureOfInjurye6 = false;
        isNatureOfInjurye7 = false;
        isNatureOfInjurye8 = false;
        isNatureOfInjurye9 = false;
        checkOtherInjury = false;
        isUnknownNatureOfInjurye = false;
        for (int i = 0; i < kindsOfInjuryList.size(); i++) {
            int caso = (int) kindsOfInjuryList.get(i).getKindInjuryId();
            switch (caso) {
                case 1:
                    isNatureOfInjurye1 = true;
                    break;
                case 2:
                    isNatureOfInjurye2 = true;
                    break;
                case 3:
                    isNatureOfInjurye3 = true;
                    break;
                case 4:
                    isNatureOfInjurye4 = true;
                    break;
                case 5:
                    isNatureOfInjurye5 = true;
                    break;
                case 6:
                    isNatureOfInjurye6 = true;
                    break;
                case 7:
                    isNatureOfInjurye7 = true;
                    break;
                case 8:
                    isNatureOfInjurye8 = true;
                    break;
                case 9:
                    isNatureOfInjurye9 = true;
                    break;
                case 10:
                    checkOtherInjury = true;
                    break;
                case 11:
                    isUnknownNatureOfInjurye = true;
                    break;
            }
        }
        //cargo los diagnosticos
        List<Diagnoses> diagnosesList = currentNonFatalInjury.getDiagnosesList();
        for (int i = 0; i < diagnosesList.size(); i++) {
            switch (i) {
                case 0:
                    idCIE10_1 = diagnosesList.get(i).getDiagnosisId();
                    txtCIE10_1 = diagnosesList.get(i).getDiagnosisName();
                    break;
                case 1:
                    idCIE10_2 = diagnosesList.get(i).getDiagnosisId();
                    txtCIE10_2 = diagnosesList.get(i).getDiagnosisName();
                    break;
                case 2:
                    idCIE10_3 = diagnosesList.get(i).getDiagnosisId();
                    txtCIE10_4 = diagnosesList.get(i).getDiagnosisName();
                    break;
            }
        }
        //------------------------------------------------------------
        //SE CREA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //------------------------------------------------------------
        if (currentNonFatalInjury.getNonFatalInterpersonal().getPreviousAntecedent() != null) {
            aggressionPast = currentNonFatalInjury.getNonFatalInterpersonal().getPreviousAntecedent();
        }
        if (currentNonFatalInjury.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
            currentRelationshipToVictim = currentNonFatalInjury.getNonFatalInterpersonal().getRelationshipVictimId().getRelationshipVictimId();
        }
        if (currentNonFatalInjury.getNonFatalInterpersonal().getContextId() != null) {
            currentContext = currentNonFatalInjury.getNonFatalInterpersonal().getContextId().getContextId();
        }
        if (currentNonFatalInjury.getNonFatalInterpersonal().getAggressorGenderId() != null) {
            currentAggressorGenders = currentNonFatalInjury.getNonFatalInterpersonal().getAggressorGenderId().getGenderId();
        }

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------
        if (currentNonFatalInjury.getNonFatalSelfInflicted().getPreviousAttempt() != null) {
            previousAttempt = currentNonFatalInjury.getNonFatalSelfInflicted().getPreviousAttempt().getBooleanId();
        }
        if (currentNonFatalInjury.getNonFatalSelfInflicted().getMentalAntecedent() != null) {
            mentalPastDisorder = currentNonFatalInjury.getNonFatalSelfInflicted().getMentalAntecedent().getBooleanId();
        }
        if (currentNonFatalInjury.getNonFatalSelfInflicted().getPrecipitatingFactorId() != null) {
            currentPrecipitatingFactor = currentNonFatalInjury.getNonFatalSelfInflicted().getPrecipitatingFactorId().getPrecipitatingFactorId();
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
            //if (currentVulnerableGroup != 0) {
            //	newVictim.setVulnerableGroupId(vulnerableGroupsFacade.find(currentVulnerableGroup));
            //}

            //falta la definicion si es otro grupo vulnerable
            if (otherEthnicGroup.trim().length() != 0) {
            }
            if (currentEthnicGroup != 0) {
                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(currentEthnicGroup));
            }
            //falta la definicion si es otro grupo etnico
            //if (otherVulnerableGroup.trim().length() != 0) {
            //}

            if (currentTelephoneHome.trim().length() != 0) {
                newVictim.setVictimTelephone(currentTelephoneHome);
            }
            if (currentDirectionHome.trim().length() != 0) {
                newVictim.setVictimAddress(currentDirectionHome);
            }
            if (currentNeighborhoodHomeCode.trim().length() != 0) {
                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(currentNeighborhoodHomeCode)));
            }
            newVictim.setVictimDateOfBirth(new Date());
            //newVictim.setEpsId(null);
            if (currentNeighborhoodHomeCode.trim().length() != 0) {
            }
            //newVictim.setVictimClass(null);            
            newVictim.setResidenceMunicipality(currentMunicipalitie);

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------
            NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();


            newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 54));//es 54 por ser no fatal

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
            if (currentIntentionality != 0) {
                newNonFatalInjuries.setIntentionalityId(intentionalitiesFacade.find((short) currentIntentionality));
            }

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
                newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort(currentPercentBurned));
            }
            if (isSubmitted) {
                newNonFatalInjuries.setSubmittedPatient(isSubmitted);
                newNonFatalInjuries.setEpsId(currentIPS);
            }
            //newNonFatalInjuries.setDestinationPatientId(null); 

            newNonFatalInjuries.setInputTimestamp(new Date());
            if (currentPercentBurned.trim().length() != 0) {
                newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort(currentPercentBurned));
            }

            //newNonFatalInjuries.setHealthProfessionalId(null);

            if (currentHealthInstitution != 0) {
                newNonFatalInjuries.setNonFatalDataSourceId(currentHealthInstitution);
            }
            if (currentMechanisms != 0) {
                newNonFatalInjuries.setMechanismId(mechanismsFacade.find(currentMechanisms));
            }

            newNonFatalInjuries.setUserId(usersFacade.find(1));

            if (currentWeekdayEvent.trim().length() != 0) {
                newNonFatalInjuries.setInjuryDayOfWeek(currentWeekdayEvent);
            }

            newNonFatalInjuries.setVictimId(newVictim);

            //---LISTA DE SITIOS ANATOMICOS---------------------------------------
            List<AnatomicalLocations> anatomicalLocationList = new ArrayList<AnatomicalLocations>();

            if (isAnatomicalSite1) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 1));
            }
            if (isAnatomicalSite2) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 2));
            }
            if (isAnatomicalSite3) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 3));
            }
            if (isAnatomicalSite4) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 4));
            }
            if (isAnatomicalSite5) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 5));
            }
            if (isAnatomicalSite6) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 6));
            }
            if (isAnatomicalSite7) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 7));
            }
            if (isAnatomicalSite8) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 8));
            }
            if (isAnatomicalSite9) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 9));
            }
            if (isAnatomicalSite10) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 10));
            }
            if (isAnatomicalSite11) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 11));
            }
            if (checkOtherPlace) {
                anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 99));
            }
            newNonFatalInjuries.setAnatomicalLocationsList(anatomicalLocationList);

            //---NATURALEZA DE LA LESION--------------------------------------------
            List<KindsOfInjury> kindsOfInjuryList = new ArrayList<KindsOfInjury>();

            if (isNatureOfInjurye1) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 1));
            }
            if (isNatureOfInjurye2) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 2));
            }
            if (isNatureOfInjurye3) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 3));
            }
            if (isNatureOfInjurye4) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 4));
            }
            if (isNatureOfInjurye5) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 5));
            }
            if (isNatureOfInjurye6) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 6));
            }
            if (isNatureOfInjurye7) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 7));
            }
            if (isNatureOfInjurye8) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 8));
            }
            if (isNatureOfInjurye9) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 9));
            }
            if (checkOtherInjury) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 98));
            }
            if (isUnknownNatureOfInjurye) {
                kindsOfInjuryList.add(kindsOfInjuryFacade.find((short) 99));
            }

            newNonFatalInjuries.setKindsOfInjuryList(kindsOfInjuryList);

            //---CODIGO CIE10---------------------------------
            List<Diagnoses> diagnosesesList = new ArrayList<Diagnoses>();

            if (idCIE10_1.trim().length() != 0) {
                diagnosesesList.add(diagnosesFacade.find(idCIE10_1));
            }
            if (idCIE10_2.trim().length() != 0) {
                diagnosesesList.add(diagnosesFacade.find(idCIE10_2));
            }
            if (idCIE10_3.trim().length() != 0) {
                diagnosesesList.add(diagnosesFacade.find(idCIE10_3));
            }
            newNonFatalInjuries.setDiagnosesList(diagnosesesList);

            if (currentHealthProfessionals != null) {
                if (currentHealthProfessionals.trim().length() != 0) {
                    newNonFatalInjuries.setHealthProfessionalId(healthProfessionalsFacade.findByName(currentHealthProfessionals));
                }
            }


            //------------------------------------------------------------
            //SE CREA VARIABLE PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------

            NonFatalDomesticViolence newNonFatalDomesticViolence = new NonFatalDomesticViolence();
            newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
            //newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(domesticViolenceDataSourcesFacade.find(currentDomesticViolenceDataSource));
            newNonFatalDomesticViolence.setNonFatalInjuries(newNonFatalInjuries);
            //---LISTA DE AGRESORES-----------------------------------
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

            //----LISTA DE TIPOS DE MALTRATO-----------------------------------
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

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA VIOLENCIA INTERPERSONAL
            //------------------------------------------------------------
            NonFatalInterpersonal newNonFatalInterpersonal = new NonFatalInterpersonal();
            newNonFatalInterpersonal.setPreviousAntecedent(aggressionPast);
            if (currentRelationshipToVictim != 0) {
                newNonFatalInterpersonal.setRelationshipVictimId(relationshipsToVictimFacade.find(currentRelationshipToVictim));
            }
            if (currentContext != 0) {
                newNonFatalInterpersonal.setContextId(contextsFacade.find(currentContext));
            }
            if (currentAggressorGenders != 0) {
                newNonFatalInterpersonal.setAggressorGenderId(agreAggressorGendersFacade.find(currentAggressorGenders));
            }
            newNonFatalInterpersonal.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
            //------------------------------------------------------------
            //AUTOINFLINGIDA INTENCIONAL
            //------------------------------------------------------------
            NonFatalSelfInflicted newNonFatalSelfInflicted = new NonFatalSelfInflicted();
            if (previousAttempt != 0) {
                newNonFatalSelfInflicted.setPreviousAttempt(booleanPojoFacade.find(previousAttempt));
            }
            if (mentalPastDisorder != 0) {
                newNonFatalSelfInflicted.setMentalAntecedent(booleanPojoFacade.find(mentalPastDisorder));
            }
            if (currentPrecipitatingFactor != 0) {
                newNonFatalSelfInflicted.setPrecipitatingFactorId(precipitatingFactorsFacade.find(currentPrecipitatingFactor));
            }
            newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());


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
                    nonFatalInterpersonalFacade.create(newNonFatalInterpersonal);
                    nonFatalSelfInflictedFacade.create(newNonFatalSelfInflicted);

                    save = true;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR
                    //System.out.println("actualizando registro existente");
                    //updateRegistry(newVictim, newNonFatalInjuries, newNonFatalDomesticViolence);
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
            System.out.println("*******************************************ERROR_L2: " + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }
    }

    private void updateRegistry(Victims victim, FatalInjuries fatalInjurie, FatalInjuryMurder fatalInjuryMurder) {
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
        if (save) {
            System.out.println("cargando siguiente registro");
            if (currentNonFatalInjuriId == -1) {//esta en registro nuevo                
            } else {
                auxNonFatalInjury = nonFatalInjuriesFacade.findNext(currentNonFatalInjuriId);
                if (auxNonFatalInjury != null) {
                    clearForm();
                    currentNonFatalInjury = auxNonFatalInjury;
                    currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            System.out.println("No esta guardadado (para poder cargar siguiente registro)");
        }
    }

    public void previous() {
        if (save) {
            System.out.println("cargando anterior registro");
            if (currentNonFatalInjuriId == -1) {//esta en registro nuevo                
            } else {
                auxNonFatalInjury = nonFatalInjuriesFacade.findPrevious(currentNonFatalInjuriId);
                if (auxNonFatalInjury != null) {
                    clearForm();
                    currentNonFatalInjury = auxNonFatalInjury;
                    currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
                    determinePosition();
                    loadValues();
                }
            }
        } else {
            System.out.println("No esta guardadado (para poder cargar anterior registro)");
        }
    }

    public void first() {
        if (save) {
            System.out.println("cargando primer registro");
            auxNonFatalInjury = nonFatalInjuriesFacade.findFirst();
            if (auxNonFatalInjury != null) {
                clearForm();
                currentNonFatalInjury = auxNonFatalInjury;
                currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            System.out.println("No esta guardadado (para poder cargar primer registro)");
        }
    }

    public void last() {
        if (save) {
            System.out.println("cargando ultimo registro");
            auxNonFatalInjury = nonFatalInjuriesFacade.findLast();
            if (auxNonFatalInjury != null) {
                clearForm();
                currentNonFatalInjury = auxNonFatalInjury;
                currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            System.out.println("No esta guardadado (para poder cargar ultimo registro)");
        }
    }

    public void clearForm() {

        System.out.println("Limpiando formulario");
        currentHealthInstitution = 0;
        isDisplaced = false;
        isHandicapped = false;
        currentEthnicGroup = 0;
        otherEthnicGroup = "";

        currentDepartamentHome = 52;
        currentMunicipalitie = 1;
        currentDirectionHome = "";
        currentTelephoneHome = "";

        isSubmitted = false;
        IPSDisabled = true;
        currentIPS = 0;

        currentIntentionality = 0;
        currentOtherIntentionality = "";
        otherIntentDisabled = true;

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

        currentTransportTypes = 0;
        otherTransportType = "";
        otherTransportTypeDisabled = true;

        currentTransportCounterpart = 0;
        otherTransportCounterpartsType = "";
        otherTransportCounterpartsTypeDisabled = true;

        currentTransportUser = 0;
        otherTransportUserType = "";
        otherTransportUserTypeDisabled = true;

        currentSecurityElements = "NO";
        displaySecurityElements = "none";
        isBeltUse = false;
        isHelmetUse = false;
        isBicycleHelmetUse = false;
        isVestUse = false;
        isOtherElementUse = false;

        relationshipToVictimDisabled = true;
        aggressionPast = false;
        currentRelationshipToVictim = 0;
        otherRelation = "";
        otherRelationDisabled = true;
        contextDisabled = true;
        currentContext = 0;
        aggressorGendersDisabled = true;
        currentAggressorGenders = 0;

        currentDestinationPatient = 0;
        otherDestinationPatientDisabled = true;
        otherDestinationPatient = "";

        idCIE10_1 = "";
        idCIE10_2 = "";
        idCIE10_3 = "";
        idCIE10_4 = "";

        txtCIE10_1 = "";
        txtCIE10_2 = "";
        txtCIE10_3 = "";
        txtCIE10_4 = "";

        currentHealthProfessionals = "";

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

        isMA1 = false;
        isMA2 = false;
        isMA3 = false;
        isMA4 = false;
        isMA5 = false;
        isMA6 = false;
        isUnknownMA = false;

        isAnatomicalSite1 = false;
        isAnatomicalSite2 = false;
        isAnatomicalSite3 = false;
        isAnatomicalSite4 = false;
        isAnatomicalSite5 = false;
        isAnatomicalSite6 = false;
        isAnatomicalSite7 = false;
        isAnatomicalSite8 = false;
        isAnatomicalSite9 = false;
        isAnatomicalSite10 = false;
        isAnatomicalSite11 = false;
        checkOtherPlace = false;
        txtOtherPlace = "";
        otherPlaceDisabled = false;

        isNatureOfInjurye1 = false;
        isNatureOfInjurye2 = false;
        isNatureOfInjurye3 = false;
        isNatureOfInjurye4 = false;
        isNatureOfInjurye5 = false;
        isNatureOfInjurye6 = false;
        isNatureOfInjurye7 = false;
        isNatureOfInjurye8 = false;
        isNatureOfInjurye9 = false;
        checkOtherInjury = false;
        txtOtherInjury = "";
        otherInjuryDisabled = false;
        isUnknownNatureOfInjurye = false;
        findMunicipalities();
    }

    public void newForm() {
        //currentFatalInjuryMurder = null;
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
//            fatalInjuryMurderFacade.remove(currentFatalInjuryMurder);
//            System.out.println("registro eliminado");
//        } else {
//            System.out.println("Se esta actualmente en un nuevo registro, no se puede eliminar");
//        }
//        //System.out.println("eliminando registro: '" + openDialogDelete + "'");
    }

    public void putDiagnose() {
        //llenas las casillas CIE_CASMPO_1 y TXT_CIE_10_1 seleccionadas del dialog que lista los diagnosticos
        String[] splitDiagnose;
        splitDiagnose = currentDiagnoses.split(" - ");
        switch (CIE_selected) {
            case 1:
                idCIE10_1 = splitDiagnose[0];
                txtCIE10_1 = splitDiagnose[1];
                break;
            case 2:
                idCIE10_2 = splitDiagnose[0];
                txtCIE10_2 = splitDiagnose[1];
                break;
            case 3:
                idCIE10_3 = splitDiagnose[0];
                txtCIE10_3 = splitDiagnose[1];
                break;
            case 4:
                idCIE10_4 = splitDiagnose[0];
                txtCIE10_4 = splitDiagnose[1];
                break;
        }
        //clearDiagnoses();
        changeForm();

    }

    public void setCIE_1() {
        //funcion para saber que se dio clik sobre la casilla txt de diagnostico 1
        CIE_selected = 1;
        //loadDiagnoses();
    }

    public void setCIE_2() {
        //funcion para saber que se dio clik sobre la casilla txt de diagnostico 2
        CIE_selected = 2;
        //loadDiagnoses();
    }

    public void setCIE_3() {
        //funcion para saber que se dio clik sobre la casilla txt de diagnostico 3
        CIE_selected = 3;
        //loadDiagnoses();
    }

    public void setCIE_4() {
        //funcion para saber que se dio clik sobre la casilla txt de diagnostico 4
        CIE_selected = 4;
        //loadDiagnoses();
    }

    public void determinePosition() {
        totalRegisters = nonFatalInjuriesFacade.count();
        if (currentNonFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = nonFatalInjuriesFacade.findPosition(currentNonFatalInjury.getNonFatalInjuryId());
            currentPosition = position + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "dialogDelete.show();";
        }
        if (!save) {
            currentPosition = currentPosition + " *";
        }
        //System.out.println("POSICION DETERMINADA: " + currentPosition);
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

    public List<String> suggestCIE10(String entered) {
        List<Diagnoses> diagnosesesList = diagnosesFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < diagnosesesList.size(); i++) {
            if (diagnosesesList.get(i).getDiagnosisId().startsWith(entered)) {
                list.add(diagnosesesList.get(i).getDiagnosisId());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    public List<String> suggestHealthProfessionals(String entered) {
        List<HealthProfessionals> professionalsList = healthProfessionalsFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < professionalsList.size(); i++) {
            if (professionalsList.get(i).getHealthProfessionalName().startsWith(entered)) {
                list.add(professionalsList.get(i).getHealthProfessionalName());
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
    // FUNCIONES CUANDO LISTAS Y CAMPOS CAMBIAN DE VALOR -------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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

    public void changeUnknownNatureOfInjurye() {
        changeForm();
        if (isUnknownNatureOfInjurye) {
            isNatureOfInjurye1 = false;
            isNatureOfInjurye2 = false;
            isNatureOfInjurye3 = false;
            isNatureOfInjurye4 = false;
            isNatureOfInjurye5 = false;
            isNatureOfInjurye6 = false;
            isNatureOfInjurye7 = false;
            isNatureOfInjurye8 = false;
            isNatureOfInjurye9 = false;
            checkOtherInjury = false;
            txtOtherInjury = "";
            otherInjuryDisabled = false;
        }

    }

    public void changeUnknownAG() {
        changeForm();
        if (isUnknownAG) {
            isAG1 = false;
            isAG2 = false;
            isAG3 = false;
            isAG4 = false;
            isAG5 = false;
            isAG6 = false;
            isAG7 = false;
            isAG8 = false;
        }
    }

    public void changeUnknownMA() {
        changeForm();
        if (isUnknownMA) {
            isMA1 = false;
            isMA2 = false;
            isMA3 = false;
            isMA4 = false;
            isMA5 = false;
            isMA6 = false;
        }
    }

    public void changePercentBurned() {
        changeForm();
        try {
            int percentInt = Integer.parseInt(currentPercentBurned);
            if (percentInt < 0 || percentInt > 100) {
                currentPercentBurned = "";
            }

        } catch (Exception e) {
            currentPercentBurned = "";
        }
    }

    public void changeValueAge() {
        changeForm();
        try {
            int ageInt = Integer.parseInt(currentAge);
            if (ageInt < 0) {
                currentAge = "";
            }

        } catch (Exception e) {
            currentAge = "";
        }
    }

    public void findMunicipalities() {
        if (!save) {
            changeForm();
        }
        Departaments d = departamentsFacade.findById(currentDepartamentHome);
        //currentDepartamentHomeCode = d.getDepartamentId().toString();
        //currentMunicipalitie = d.getMunicipalitiesList().get(0).getMunicipalityName();
        //currentMunicipalitieCode = d.getMunicipalitiesList().get(0).getMunicipalitiesPK().getMunicipalityId();
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

    public void changeMunicipalitie() {
        changeForm();
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, (short) 52);
        if (currentMunicipalitie == 1) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
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

    public void changeOtherFactor() {
        changeForm();
        if (currentPrecipitatingFactor == 98) {//98. otro
            otherFactorDisabled = false;
        } else {
            otherFactorDisabled = true;
            otherFactor = "";
        }
    }

    public void changeRelationshipToVictim() {
        changeForm();
        if (currentRelationshipToVictim == 3) {//3. otro
            otherRelationDisabled = false;
        } else {
            otherRelationDisabled = true;
            otherRelation = "";
        }
    }

    public void changeAggressionPast() {
        changeForm();
        if (aggressionPast) {
            relationshipToVictimDisabled = false;
            contextDisabled = false;
            aggressorGendersDisabled = false;

        } else {
            relationshipToVictimDisabled = true;
            currentRelationshipToVictim = 0;
            otherRelation = "";
            otherRelationDisabled = true;
            contextDisabled = true;
            currentContext = 0;
            aggressorGendersDisabled = true;
            currentAggressorGenders = 0;
        }
        try {
            if (currentRelationshipToVictim == 3) {//3. otro
                otherRelationDisabled = false;
            } else {
                otherRelationDisabled = true;
                otherRelation = "";
            }
        } catch (Exception e) {
        }
    }

    public void changeOtherInjury() {
        changeForm();
        if (checkOtherInjury) {
            otherInjuryDisabled = false;

        } else {
            otherInjuryDisabled = true;
            txtOtherInjury = "";
        }
    }

    public void changeDestinationPatient() {
        changeForm();
        if (currentDestinationPatient == 10) {//10. otro
            otherDestinationPatientDisabled = false;
            otherDestinationPatient = "";
        } else {
            otherDestinationPatientDisabled = true;
            otherDestinationPatient = "";
        }
    }

    public void changeOtherPlace() {
        changeForm();
        if (checkOtherPlace) {
            otherPlaceDisabled = false;
        } else {
            otherPlaceDisabled = true;
            txtOtherPlace = "";
        }
    }

    public void changeSecurityElements() {
        changeForm();
        if (currentSecurityElements.compareTo("SI") == 0) {
            displaySecurityElements = "block";
        } else {
            displaySecurityElements = "none";
            isBeltUse = false;
            isHelmetUse = false;
            isBicycleHelmetUse = false;
            isVestUse = false;
            isOtherElementUse = false;
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

    public void changeNeighborhoodHomeName() {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = String.valueOf(neighborhoodsList.get(i).getNeighborhoodId());
                break;
            }
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

    public void changeSubmitted() {
        changeForm();
        if (isSubmitted) {
            IPSDisabled = false;

        } else {
            IPSDisabled = true;
            currentIPS = 0;
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
        if (currentMechanisms == 1) {//lesion de transporte"        
            displayTransport = "block";
        } else {
            displayTransport = "none";
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

    public void changeTransportCounterpart() {
        changeForm();
        if (currentTransportCounterpart == 12) {//12. otro
            otherTransportCounterpartsTypeDisabled = false;
        } else {
            otherTransportCounterpartsTypeDisabled = true;
            otherTransportCounterpartsType = "";
        }
    }

    public void changeTransportType() {
        changeForm();
        if (currentTransportTypes == 10) {//10. otro
            otherTransportTypeDisabled = false;
        } else {
            otherTransportTypeDisabled = true;
            otherTransportType = "";
        }
    }

    public void changeTransportUser() {
        changeForm();
        if (currentTransportUser == 8) {//8. otro
            otherTransportUserTypeDisabled = false;
        } else {
            otherTransportUserTypeDisabled = true;
            otherTransportUserType = "";
        }
    }

    public void changeEthnicGroups() {
        changeForm();
        if (currentEthnicGroup == 3) {//3. otro
            ethnicGroupsDisabled = false;

        } else {
            ethnicGroupsDisabled = true;
            otherEthnicGroup = "";
        }
    }

    public void changeMeasuresOfAge() {
        if (currentMeasureOfAge == 4) {//4. otro
            valueAgeDisabled = true;

        } else {
            valueAgeDisabled = false;
            currentAge = "";
        }
        //System.out.println("----" + currentEthnicGroup + "----" + ethnicGroupsDisabled);

    }

    public void changeActivities() {
        changeForm();
        if (currentActivities == 98) {//98. otra cual?
            otherActivityDisabled = false;
        } else {
            otherActivityDisabled = true;
            currentOtherActivitie = "";
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

    public void changeIntentionality() {
        changeForm();
        if (currentIntentionality == 8) {//otro 8
            otherIntentDisabled = false;
            displayInterpersonalViolence = "none";
            displayDomesticViolence = "none";
            displayIntentional = "none";
        } else {
            otherIntentDisabled = true;
            currentOtherIntentionality = "";
        }
        if (currentIntentionality == 1 || currentIntentionality == 9 || currentIntentionality == 0) {
            //no intencional 1 //no se sabe 9 // vacio
            displayInterpersonalViolence = "none";
            displayDomesticViolence = "none";
            displayIntentional = "none";
        }
        if (currentIntentionality == 2) {//Autoinflingida 2
            displayInterpersonalViolence = "none";
            displayDomesticViolence = "none";
            displayIntentional = "block";
        }
        if (currentIntentionality == 3) {//Violencia / agresión o sospecha 3
            displayInterpersonalViolence = "block";
            displayDomesticViolence = "block";
            displayIntentional = "none";
        }
    }

    public void changeIdCIE10_1() {
        changeForm();
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_1.toUpperCase());
        if (selectDiagnoses != null) {
            txtCIE10_1 = selectDiagnoses.getDiagnosisName();
            idCIE10_1 = idCIE10_1.toUpperCase();
        } else {
            txtCIE10_1 = "";
            idCIE10_1 = "";
        }
    }

    public void changeIdCIE10_2() {
        changeForm();
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_2.toUpperCase());
        if (selectDiagnoses != null) {
            txtCIE10_2 = selectDiagnoses.getDiagnosisName();
            idCIE10_2 = idCIE10_2.toUpperCase();
        } else {
            txtCIE10_2 = "";
            idCIE10_2 = "";
        }
    }

    public void changeIdCIE10_3() {
        changeForm();
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_3.toUpperCase());
        if (selectDiagnoses != null) {
            txtCIE10_3 = selectDiagnoses.getDiagnosisName();
            idCIE10_3 = idCIE10_3.toUpperCase();
        } else {
            txtCIE10_3 = "";
            idCIE10_3 = "";
        }
    }

    public void changeIdCIE10_4() {
        changeForm();
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_4.toUpperCase());
        if (selectDiagnoses != null) {
            txtCIE10_4 = selectDiagnoses.getDiagnosisName();
            idCIE10_4 = idCIE10_4.toUpperCase();
        } else {
            txtCIE10_4 = "";
            idCIE10_4 = "";
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
    public SelectItem[] getHealthInstitutions() {
        return healthInstitutions;
    }

    public void setHealthInstitutions(SelectItem[] healthInstitutions) {
        this.healthInstitutions = healthInstitutions;
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

    public SelectItem[] getDestinationsPatient() {
        return destinationsPatient;
    }

    public void setDestinationsPatient(SelectItem[] destinationsPatient) {
        this.destinationsPatient = destinationsPatient;
    }

    public String getTxtCIE10_1() {
        return txtCIE10_1;
    }

    public void setTxtCIE10_1(String txtCIE10_1) {
        this.txtCIE10_1 = txtCIE10_1;
    }

    public String getTxtCIE10_2() {
        return txtCIE10_2;
    }

    public void setTxtCIE10_2(String txtCIE10_2) {
        this.txtCIE10_2 = txtCIE10_2;
    }

    public String getIdCIE10_1() {
        return idCIE10_1;
    }

    public void setIdCIE10_1(String idCIE10_1) {
        this.idCIE10_1 = idCIE10_1;
    }

    public String getIdCIE10_2() {
        return idCIE10_2;
    }

    public void setIdCIE10_2(String idCIE10_2) {
        this.idCIE10_2 = idCIE10_2;
    }

    public String getIdCIE10_3() {
        return idCIE10_3;
    }

    public void setIdCIE10_3(String idCIE10_3) {
        this.idCIE10_3 = idCIE10_3;
    }

    public String getIdCIE10_4() {
        return idCIE10_4;
    }

    public void setIdCIE10_4(String idCIE10_4) {
        this.idCIE10_4 = idCIE10_4;
    }

    public String getTxtCIE10_3() {
        return txtCIE10_3;
    }

    public void setTxtCIE10_3(String txtCIE10_4) {
        this.txtCIE10_4 = txtCIE10_4;
    }

    public String getTxtCIE10_4() {
        return txtCIE10_4;
    }

    public void setTxtCIE10_4(String txtCIE10_4) {
        this.txtCIE10_4 = txtCIE10_4;
    }

    public SelectItem[] getActivities() {
        return activities;
    }

    public void setActivities(SelectItem[] activities) {
        this.activities = activities;
    }

    public SelectItem[] getAggressorGenders() {
        return aggressorGenders;
    }

    public void setAggressorGenders(SelectItem[] aggressorGenders) {
        this.aggressorGenders = aggressorGenders;
    }

    public SelectItem[] getContexts() {
        return contexts;
    }

    public void setContexts(SelectItem[] contexts) {
        this.contexts = contexts;
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

    public SelectItem[] getIntentionalities() {
        return intentionalities;
    }

    public void setIntentionalities(SelectItem[] intentionalities) {
        this.intentionalities = intentionalities;
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

    public SelectItem[] getPrecipitatingFactors() {
        return precipitatingFactors;
    }

    public void setPrecipitatingFactors(SelectItem[] precipitatingFactors) {
        this.precipitatingFactors = precipitatingFactors;
    }

    public SelectItem[] getTransportTypes() {
        return transportTypes;
    }

    public void setTransportTypes(SelectItem[] transportTypes) {
        this.transportTypes = transportTypes;
    }

    public SelectItem[] getTransportUsers() {
        return transportUsers;
    }

    public void setTransportUsers(SelectItem[] transportUsers) {
        this.transportUsers = transportUsers;
    }

    public SelectItem[] getRelationshipsToVictim() {
        return relationshipsToVictim;
    }

    public void setRelationshipsToVictim(SelectItem[] relationshipsToVictim) {
        this.relationshipsToVictim = relationshipsToVictim;
    }

    public SelectItem[] getTransportCounterparts() {
        return transportCounterparts;
    }

    public void setTransportCounterparts(SelectItem[] transportCounterparts) {
        this.transportCounterparts = transportCounterparts;
    }

    public SelectItem[] getEthnicGroups() {
        return ethnicGroups;
    }

    public void setEthnicGroups(SelectItem[] ethnicGroups) {
        this.ethnicGroups = ethnicGroups;
    }

    public boolean isEthnicGroupsDisabled() {
        return ethnicGroupsDisabled;
    }

    public void setEthnicGroupsDisabled(boolean ethnicGroupsDisabled) {
        this.ethnicGroupsDisabled = ethnicGroupsDisabled;
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

    public boolean isOtherIntentDisabled() {
        return otherIntentDisabled;
    }

    public void setOtherIntentDisabled(boolean otherIntentDisabled) {
        this.otherIntentDisabled = otherIntentDisabled;
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

    public String getOtherTransportCounterpartsType() {
        return otherTransportCounterpartsType;
    }

    public void setOtherTransportCounterpartsType(String otherTransportCounterpartsType) {
        this.otherTransportCounterpartsType = otherTransportCounterpartsType;
    }

    public String getOtherTransportType() {
        return otherTransportType;
    }

    public void setOtherTransportType(String otherTransportType) {
        this.otherTransportType = otherTransportType;
    }

    public boolean isOtherTransportCounterpartsTypeDisabled() {
        return otherTransportCounterpartsTypeDisabled;
    }

    public void setOtherTransportCounterpartsTypeDisabled(boolean otherTransportCounterpartsTypeDisabled) {
        this.otherTransportCounterpartsTypeDisabled = otherTransportCounterpartsTypeDisabled;
    }

    public boolean isOtherTransportTypeDisabled() {
        return otherTransportTypeDisabled;
    }

    public void setOtherTransportTypeDisabled(boolean otherTransportTypeDisabled) {
        this.otherTransportTypeDisabled = otherTransportTypeDisabled;
    }

    public String getOtherTransportUserType() {
        return otherTransportUserType;
    }

    public void setOtherTransportUserType(String otherTransportUserType) {
        this.otherTransportUserType = otherTransportUserType;
    }

    public boolean isOtherTransportUserTypeDisabled() {
        return otherTransportUserTypeDisabled;
    }

    public void setOtherTransportUserTypeDisabled(boolean otherTransportUserTypeDisabled) {
        this.otherTransportUserTypeDisabled = otherTransportUserTypeDisabled;
    }

    public String getDisplaySecurityElements() {
        return displaySecurityElements;
    }

    public void setDisplaySecurityElements(String displaySecurityElements) {
        this.displaySecurityElements = displaySecurityElements;
    }

    public String getCurrentSecurityElements() {
        return currentSecurityElements;
    }

    public void setCurrentSecurityElements(String currentSecurityElements) {
        this.currentSecurityElements = currentSecurityElements;
    }

    public boolean isAggressionPast() {
        return aggressionPast;
    }

    public void setAggressionPast(boolean aggressionPast) {
        this.aggressionPast = aggressionPast;
    }

    public boolean isAggressorGendersDisabled() {
        return aggressorGendersDisabled;
    }

    public void setAggressorGendersDisabled(boolean aggressorGendersDisabled) {
        this.aggressorGendersDisabled = aggressorGendersDisabled;
    }

    public boolean isContextDisabled() {
        return contextDisabled;
    }

    public void setContextDisabled(boolean contextDisabled) {
        this.contextDisabled = contextDisabled;
    }

    public boolean isOtherRelationDisabled() {
        return otherRelationDisabled;
    }

    public void setOtherRelationDisabled(boolean otherRelationDisabled) {
        this.otherRelationDisabled = otherRelationDisabled;
    }

    public boolean isRelationshipToVictimDisabled() {
        return relationshipToVictimDisabled;
    }

    public void setRelationshipToVictimDisabled(boolean relationshipToVictimDisabled) {
        this.relationshipToVictimDisabled = relationshipToVictimDisabled;
    }

    public String getOtherRelation() {
        return otherRelation;
    }

    public void setOtherRelation(String otherRelation) {
        this.otherRelation = otherRelation;
    }

    public String getOtherFactor() {
        return otherFactor;
    }

    public void setOtherFactor(String otherFactor) {
        this.otherFactor = otherFactor;
    }

    public boolean isOtherFactorDisabled() {
        return otherFactorDisabled;
    }

    public void setOtherFactorDisabled(boolean otherFactorDisabled) {
        this.otherFactorDisabled = otherFactorDisabled;
    }

    public boolean isCheckOtherInjury() {
        return checkOtherInjury;
    }

    public void setCheckOtherInjury(boolean checkOtherInjury) {
        this.checkOtherInjury = checkOtherInjury;
    }

    public boolean isCheckOtherPlace() {
        return checkOtherPlace;
    }

    public void setCheckOtherPlace(boolean checkOtherPlace) {
        this.checkOtherPlace = checkOtherPlace;
    }

    public boolean isOtherInjuryDisabled() {
        return otherInjuryDisabled;
    }

    public void setOtherInjuryDisabled(boolean otherInjuryDisabled) {
        this.otherInjuryDisabled = otherInjuryDisabled;
    }

    public boolean isOtherInjuryPlaceDisabled() {
        return otherInjuryPlaceDisabled;
    }

    public void setOtherInjuryPlaceDisabled(boolean otherInjuryPlaceDisabled) {
        this.otherInjuryPlaceDisabled = otherInjuryPlaceDisabled;
    }

    public String getTxtOtherInjury() {
        return txtOtherInjury;
    }

    public void setTxtOtherInjury(String txtOtherInjury) {
        this.txtOtherInjury = txtOtherInjury;
    }

    public String getTxtOtherPlace() {
        return txtOtherPlace;
    }

    public void setTxtOtherPlace(String txtOtherPlace) {
        this.txtOtherPlace = txtOtherPlace;
    }

    public String getOtherDestinationPatient() {
        return otherDestinationPatient;
    }

    public void setOtherDestinationPatient(String otherDestinationPatient) {
        this.otherDestinationPatient = otherDestinationPatient;
    }

    public boolean isOtherDestinationPatientDisabled() {
        return otherDestinationPatientDisabled;
    }

    public void setOtherDestinationPatientDisabled(boolean otherDestinationPatientDisabled) {
        this.otherDestinationPatientDisabled = otherDestinationPatientDisabled;
    }

    public SelectItem[] getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(SelectItem[] diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getDisplayIntentional() {
        return displayIntentional;
    }

    public void setDisplayIntentional(String displayIntentional) {
        this.displayIntentional = displayIntentional;
    }

    public String getDisplayInterpersonalViolence() {
        return displayInterpersonalViolence;
    }

    public void setDisplayInterpersonalViolence(String displayInterpersonalViolence) {
        this.displayInterpersonalViolence = displayInterpersonalViolence;
    }

    public String getDisplayTransport() {
        return displayTransport;
    }

    public void setDisplayTransport(String displayTransport) {
        this.displayTransport = displayTransport;
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

    public Short getCurrentAggressorGenders() {
        return currentAggressorGenders;
    }

    public void setCurrentAggressorGenders(Short currentAggressorGenders) {
        this.currentAggressorGenders = currentAggressorGenders;
    }

    public Short getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Short currentContext) {
        this.currentContext = currentContext;
    }

    public Short getCurrentDestinationPatient() {
        return currentDestinationPatient;
    }

    public void setCurrentDestinationPatient(Short currentDestinationPatient) {
        this.currentDestinationPatient = currentDestinationPatient;
    }

    public String getCurrentDiagnoses() {
        return currentDiagnoses;
    }

    public void setCurrentDiagnoses(String currentDiagnoses) {
        this.currentDiagnoses = currentDiagnoses;
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

    public Short getCurrentHealthInstitution() {
        return currentHealthInstitution;
    }

    public void setCurrentHealthInstitution(Short currentHealthInstitution) {
        this.currentHealthInstitution = currentHealthInstitution;
    }

    public String getCurrentHealthProfessionals() {
        return currentHealthProfessionals;
    }

    public void setCurrentHealthProfessionals(String currentHealthProfessionals) {
        this.currentHealthProfessionals = currentHealthProfessionals;
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

    public Short getCurrentIntentionality() {
        return currentIntentionality;
    }

    public void setCurrentIntentionality(Short currentIntentionality) {
        this.currentIntentionality = currentIntentionality;
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

    public Short getCurrentPrecipitatingFactor() {
        return currentPrecipitatingFactor;
    }

    public void setCurrentPrecipitatingFactor(Short currentPrecipitatingFactor) {
        this.currentPrecipitatingFactor = currentPrecipitatingFactor;
    }

    public Short getCurrentRelationshipToVictim() {
        return currentRelationshipToVictim;
    }

    public void setCurrentRelationshipToVictim(Short currentRelationshipToVictim) {
        this.currentRelationshipToVictim = currentRelationshipToVictim;
    }

    public Short getCurrentTransportCounterpart() {
        return currentTransportCounterpart;
    }

    public void setCurrentTransportCounterpart(Short currentTransportCounterpart) {
        this.currentTransportCounterpart = currentTransportCounterpart;
    }

    public Short getCurrentTransportTypes() {
        return currentTransportTypes;
    }

    public void setCurrentTransportTypes(Short currentTransportTypes) {
        this.currentTransportTypes = currentTransportTypes;
    }

    public Short getCurrentTransportUser() {
        return currentTransportUser;
    }

    public void setCurrentTransportUser(Short currentTransportUser) {
        this.currentTransportUser = currentTransportUser;
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

    public String getCurrentOtherIntentionality() {
        return currentOtherIntentionality;
    }

    public void setCurrentOtherIntentionality(String currentOtherIntentionality) {
        this.currentOtherIntentionality = currentOtherIntentionality;
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

    public String getCurrentSurame() {
        return currentSurame;
    }

    public void setCurrentSurame(String currentSurame) {
        this.currentSurame = currentSurame;
    }

    public String getCurrentTelephoneHome() {
        return currentTelephoneHome;
    }

    public void setCurrentTelephoneHome(String currentTelephoneHome) {
        this.currentTelephoneHome = currentTelephoneHome;
    }

    public boolean isIsBeltUse() {
        return isBeltUse;
    }

    public void setIsBeltUse(boolean isBeltUse) {
        this.isBeltUse = isBeltUse;
    }

    public boolean isIsBicycleHelmetUse() {
        return isBicycleHelmetUse;
    }

    public void setIsBicycleHelmetUse(boolean isBicycleHelmetUse) {
        this.isBicycleHelmetUse = isBicycleHelmetUse;
    }

    public boolean isIsDisplaced() {
        return isDisplaced;
    }

    public void setIsDisplaced(boolean isDisplaced) {
        this.isDisplaced = isDisplaced;
    }

    public boolean isIsHandicapped() {
        return isHandicapped;
    }

    public void setIsHandicapped(boolean isHandicapped) {
        this.isHandicapped = isHandicapped;
    }

    public boolean isIsHelmetUse() {
        return isHelmetUse;
    }

    public void setIsHelmetUse(boolean isHelmetUse) {
        this.isHelmetUse = isHelmetUse;
    }

    public Short getMentalPastDisorder() {
        return mentalPastDisorder;
    }

    public void setMentalPastDisorder(Short mentalPastDisorder) {
        this.mentalPastDisorder = mentalPastDisorder;
    }

    public Short getPreviousAttempt() {
        return previousAttempt;
    }

    public void setPreviousAttempt(Short previousAttempt) {
        this.previousAttempt = previousAttempt;
    }

    public SelectItem[] getBooleans() {
        return booleans;
    }

    public void setBooleans(SelectItem[] booleans) {
        this.booleans = booleans;
    }

    public boolean isIsVestUse() {
        return isVestUse;
    }

    public void setIsVestUse(boolean isVestUse) {
        this.isVestUse = isVestUse;
    }

    public boolean isIsOtherElementUse() {
        return isOtherElementUse;
    }

    public void setIsOtherElementUse(boolean isOtherElementUse) {
        this.isOtherElementUse = isOtherElementUse;
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

    public boolean isIsAnatomicalSite1() {
        return isAnatomicalSite1;
    }

    public void setIsAnatomicalSite1(boolean isAnatomicalSite1) {
        this.isAnatomicalSite1 = isAnatomicalSite1;
    }

    public boolean isIsAnatomicalSite10() {
        return isAnatomicalSite10;
    }

    public void setIsAnatomicalSite10(boolean isAnatomicalSite10) {
        this.isAnatomicalSite10 = isAnatomicalSite10;
    }

    public boolean isIsAnatomicalSite11() {
        return isAnatomicalSite11;
    }

    public void setIsAnatomicalSite11(boolean isAnatomicalSite11) {
        this.isAnatomicalSite11 = isAnatomicalSite11;
    }

    public boolean isIsAnatomicalSite2() {
        return isAnatomicalSite2;
    }

    public void setIsAnatomicalSite2(boolean isAnatomicalSite2) {
        this.isAnatomicalSite2 = isAnatomicalSite2;
    }

    public boolean isIsAnatomicalSite3() {
        return isAnatomicalSite3;
    }

    public void setIsAnatomicalSite3(boolean isAnatomicalSite3) {
        this.isAnatomicalSite3 = isAnatomicalSite3;
    }

    public boolean isIsAnatomicalSite4() {
        return isAnatomicalSite4;
    }

    public void setIsAnatomicalSite4(boolean isAnatomicalSite4) {
        this.isAnatomicalSite4 = isAnatomicalSite4;
    }

    public boolean isIsAnatomicalSite5() {
        return isAnatomicalSite5;
    }

    public void setIsAnatomicalSite5(boolean isAnatomicalSite5) {
        this.isAnatomicalSite5 = isAnatomicalSite5;
    }

    public boolean isIsAnatomicalSite6() {
        return isAnatomicalSite6;
    }

    public void setIsAnatomicalSite6(boolean isAnatomicalSite6) {
        this.isAnatomicalSite6 = isAnatomicalSite6;
    }

    public boolean isIsAnatomicalSite7() {
        return isAnatomicalSite7;
    }

    public void setIsAnatomicalSite7(boolean isAnatomicalSite7) {
        this.isAnatomicalSite7 = isAnatomicalSite7;
    }

    public boolean isIsAnatomicalSite8() {
        return isAnatomicalSite8;
    }

    public void setIsAnatomicalSite8(boolean isAnatomicalSite8) {
        this.isAnatomicalSite8 = isAnatomicalSite8;
    }

    public boolean isIsAnatomicalSite9() {
        return isAnatomicalSite9;
    }

    public void setIsAnatomicalSite9(boolean isAnatomicalSite9) {
        this.isAnatomicalSite9 = isAnatomicalSite9;
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

    public boolean isIsNatureOfInjurye1() {
        return isNatureOfInjurye1;
    }

    public void setIsNatureOfInjurye1(boolean isNatureOfInjurye1) {
        this.isNatureOfInjurye1 = isNatureOfInjurye1;
    }

    public boolean isIsNatureOfInjurye2() {
        return isNatureOfInjurye2;
    }

    public void setIsNatureOfInjurye2(boolean isNatureOfInjurye2) {
        this.isNatureOfInjurye2 = isNatureOfInjurye2;
    }

    public boolean isIsNatureOfInjurye3() {
        return isNatureOfInjurye3;
    }

    public void setIsNatureOfInjurye3(boolean isNatureOfInjurye3) {
        this.isNatureOfInjurye3 = isNatureOfInjurye3;
    }

    public boolean isIsNatureOfInjurye4() {
        return isNatureOfInjurye4;
    }

    public void setIsNatureOfInjurye4(boolean isNatureOfInjurye4) {
        this.isNatureOfInjurye4 = isNatureOfInjurye4;
    }

    public boolean isIsNatureOfInjurye5() {
        return isNatureOfInjurye5;
    }

    public void setIsNatureOfInjurye5(boolean isNatureOfInjurye5) {
        this.isNatureOfInjurye5 = isNatureOfInjurye5;
    }

    public boolean isIsNatureOfInjurye6() {
        return isNatureOfInjurye6;
    }

    public void setIsNatureOfInjurye6(boolean isNatureOfInjurye6) {
        this.isNatureOfInjurye6 = isNatureOfInjurye6;
    }

    public boolean isIsNatureOfInjurye7() {
        return isNatureOfInjurye7;
    }

    public void setIsNatureOfInjurye7(boolean isNatureOfInjurye7) {
        this.isNatureOfInjurye7 = isNatureOfInjurye7;
    }

    public boolean isIsNatureOfInjurye8() {
        return isNatureOfInjurye8;
    }

    public void setIsNatureOfInjurye8(boolean isNatureOfInjurye8) {
        this.isNatureOfInjurye8 = isNatureOfInjurye8;
    }

    public boolean isIsNatureOfInjurye9() {
        return isNatureOfInjurye9;
    }

    public void setIsNatureOfInjurye9(boolean isNatureOfInjurye9) {
        this.isNatureOfInjurye9 = isNatureOfInjurye9;
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

    public boolean isIsUnknownNatureOfInjurye() {
        return isUnknownNatureOfInjurye;
    }

    public void setIsUnknownNatureOfInjurye(boolean isUnknownNatureOfInjurye) {
        this.isUnknownNatureOfInjurye = isUnknownNatureOfInjurye;
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

    public String getDisplayDomesticViolence() {
        return displayDomesticViolence;
    }

    public void setDisplayDomesticViolence(String displayDomesticViolence) {
        this.displayDomesticViolence = displayDomesticViolence;
    }

    public boolean isOtherAGDisabled() {
        return otherAGDisabled;
    }

    public void setOtherAGDisabled(boolean otherAGDisabled) {
        this.otherAGDisabled = otherAGDisabled;
    }

    public String getOtherAG() {
        return otherAG;
    }

    public void setOtherAG(String otherAG) {
        this.otherAG = otherAG;
    }

    public boolean isIsAG10() {
        return isAG10;
    }

    public void setIsAG10(boolean isAG10) {
        this.isAG10 = isAG10;
    }

    public boolean isIsMA8() {
        return isMA8;
    }

    public void setIsMA8(boolean isMA8) {
        this.isMA8 = isMA8;
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
}
