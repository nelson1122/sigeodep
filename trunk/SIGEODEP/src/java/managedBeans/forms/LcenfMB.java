/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import beans.connection.ConnectionJDBC;
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
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "lcenfMB")
@SessionScoped
public class LcenfMB implements Serializable {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    //------------------------
    @EJB
    InsuranceFacade insuranceFacade;
    private Short currentInsurance = 0;
    private SelectItem[] insurances;
    //-----------------
    @EJB
    OthersFacade othersFacade;
    //-----------------
    @EJB
    DepartamentsFacade departamentsFacade;
    private Short currentDepartamentHome = 52;//nariño
    private SelectItem[] departaments;
    private boolean currentDepartamentHomeDisabled = false;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private boolean currentMunicipalitieDisabled = false;
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
    private Short currentFromWhere = 0;
    private SelectItem[] fromWhereList;
    //------------------
    @EJB
    Boolean3Facade boolean3Facade;
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
    List<Mechanisms> mechanismsList;
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
    private boolean currentContextDisabled = false;
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
    boolean neighborhoodHomeNameDisabled = false;
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
    private boolean valueAgeDisabled = true;
    //------------------
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    //------------------
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    //------------------
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
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    SecurityElementsFacade securityElementsFacade;
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
    //-----------
    private int currentSearchCriteria = 0;
    private SelectItem[] searchCriteriaList;
    private String currentSearchValue = "";
    //-------
    private boolean isSubmitted = false;
    private boolean fromWhereDisabled = true;
    //-------
    private boolean otherAnatomicalPlaceDisabled = true;
    private boolean otherPlaceDisabled = true;
    private boolean otherActivityDisabled = true;
    private boolean otherMechanismDisabled = true;//otro mecanismo    
    private boolean otherAGDisabled = true;
    private boolean otherMADisabled = true;
    private boolean loading = false;
    private String otherAnimal = "";//otro mecanismo       
    private boolean otherAnimalDisabled = true;//cual polvora
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
    private String displayAggressionOrSuspicion = "none";
    private String displayDomesticViolence = "none";
    private String idElements1 = "";
    private String idElements2 = "";
    private boolean otherTransportTypeDisabled = true;//otro tipo de transporte
    private String otherTransportType = "";//otro tipo de transporte    
    private boolean otherTransportCounterpartsTypeDisabled = true;//otro tipo de transporte contraparte
    private String otherTransportCounterpartsType = "";//otro tipo de transporte contraparte   
    private boolean otherTransportUserTypeDisabled = true;//otro tipo de transporte usuario
    private String otherTransportUserType = "";//otro tipo de transporte usuario   
    private boolean transportUserDisabled = false;
    private boolean securityElementsDisabled = false;
    private short aggressionPast = 0;
    private String otherFactor = "";
    private boolean otherFactorDisabled = true;
    //private boolean relationshipToVictimDisabled = true;
    //private boolean contextDisabled = true;
    private String otherRelation = "";
    private boolean otherRelationDisabled = true;
    //private boolean aggressorGendersDisabled = true;
    private boolean checkOtherInjury = false;
    private boolean checkOtherPlace = false;
    private boolean otherInjuryDisabled = true;
    private boolean otherInjuryPlaceDisabled = true;
    private boolean otherDestinationPatientDisabled = true;
    private boolean identificationNumberDisabled = true;
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
    private int currentAggressionOrSuspicion = 1;
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
    //private String currentSurname = "";
    private Short currentLevelBurned = 0;
    private String currentPercentBurned = "";
    private String currentResponsible = "";
    private boolean directionHomeDisabled = false;
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
    //private String stylePosition="color: red; font-weight: 900;";
    private String stylePosition = "color: #1471B1;";
    private Calendar c = Calendar.getInstance();
    private Date date1;
    private Date date2;
    private String currentIdForm = "";

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public LcenfMB() {

        idElements1 = "IdHealthInstitution IdName IdIdentificationType IdIdentificationNumber IdMeasureOfAge "
                + "IdValueAge IdGender IdJob IdDisplaced IdHandicapped IdEthnicGroup IdOtherEthnicGroup "
                + "IdTelephoneHome IdNeighborhoodEvent IdNeighborhoodsEventCode IdDirectionEvent "
                + "IdDayEvent IdMonthEvent IdYearEvent IdDateEvent IdWeekdayEvent IdHourEvent IdMinuteEvent IdAmPmEvent IdTimeEvent "
                + "IdDayConsult IdMonthConsult IdYearConsult IdDateConsult IdWeekdayConsult IdHourConsult IdMinuteConsult IdAmPmConsult IdTimeConsult "
                + "IdDepartamentHome IdDepartamentHomeCode IdMunicipalitieList IdMunicipalitiesHomeCode IdNeighborhoodHomeName IdNeighborhoodsHomeCode "
                + "IdDirectionHome IdSubmitted IdIPS IdIntentional IdIntentionality IdPlace IdOtherPlace IdActivities IdOtherActivities "
                + "IdAnimalWhich IdMechanisms IdHeightWhich IdPowderWhich IdDisasterWhich IdOtherMechanism IdForBurned IdLevelBurned IdPercentBurned IdUseAlcohol "
                + "IdUseDrugs IdTransport IdTransportTypes IdOtherTransportType IdTransportCounterpart IdOtherTransportCounterpartsType IdTransportUser "
                + "IdOtherTransportUserType IdSecurityElementsOption IdSecurityElements IdBeltUse IdHelmetUse IdBicycleHelmetUse IdVestUse IdOtherElementUse "
                + "IdAggressionPast IdRelationshipToVictim IdOtherRelation IdCurrentContext IdAggressorGenders IdPreviousAttempt IdMentalPastDisorder "
                + "IdAggressionOrSuspicion IdAggressionOrSuspicionOption IdInterpersonalViolence IdDomesticViolence "
                + "IdPrecipitatingFactor IdOtherFactor "
                + "IdAG1 IdAG2 IdAG3 IdAG4 IdAG5 IdAG6 IdAG7 IdAG8 IdAG9 IdAG10 IdOtherAG "
                + "IdMA1 IdMA2 IdMA3 IdMA4 IdMA5 IdMA6 IdMA7 IdMA8 IdOtherMA "
                + "IdAnatomicalSite1 IdAnatomicalSite2 IdAnatomicalSite3 IdAnatomicalSite4 IdAnatomicalSite5 IdAnatomicalSite6 IdAnatomicalSite7 IdAnatomicalSite8 IdAnatomicalSite9 IdAnatomicalSite10 IdAnatomicalSite11 IdAnatomicalSite12 IdOtherPlace2 "
                + "IdNatureOfInjurye1 IdNatureOfInjurye2 IdNatureOfInjurye3 IdNatureOfInjurye4 IdNatureOfInjurye5 IdNatureOfInjurye6 IdNatureOfInjurye7 IdNatureOfInjurye8 IdNatureOfInjurye9 IdCheckOtherInjury IdOtherInjury IdUnknownNatureOfInjurye IdDestinationPatient IdOtherDestinationPatient "
                + "IdIdCIE10_1 IdTxtCIE10_1 IdIdCIE10_2 IdTxtCIE10_2 IdIdCIE10_3 IdTxtCIE10_3 IdIdCIE10_4 IdTxtCIE10_4 "
                + "IdHealthProfessionals IdResponsible IdControls message :IdForm2:IdSearchCriteria :IdForm2:IdSearcValue :IdForm2:IdSearchTable "
                + "IdInsurance IdFormId";
        idElements2 = ":IdForm1:IdHealthInstitution :IdForm1:IdName :IdForm1:IdIdentificationType :IdForm1:IdIdentificationNumber :IdForm1:IdMeasureOfAge "
                + ":IdForm1:IdValueAge :IdForm1:IdGender :IdForm1:IdJob :IdForm1:IdDisplaced :IdForm1:IdHandicapped :IdForm1:IdEthnicGroup :IdForm1:IdOtherEthnicGroup :IdForm1:IdTelephoneHome "
                + ":IdForm1:IdNeighborhoodEvent :IdForm1:IdNeighborhoodsEventCode :IdForm1:IdDirectionEvent :IdForm1:IdDayEvent :IdForm1:IdMonthEvent :IdForm1:IdYearEvent :IdForm1:IdDateEvent "
                + ":IdForm1:IdWeekdayEvent :IdForm1:IdHourEvent :IdForm1:IdMinuteEvent :IdForm1:IdAmPmEvent :IdForm1:IdTimeEvent :IdForm1:IdDayConsult :IdForm1:IdMonthConsult :IdForm1:IdYearConsult "
                + ":IdForm1:IdDateConsult :IdForm1:IdWeekdayConsult :IdForm1:IdHourConsult :IdForm1:IdMinuteConsult :IdForm1:IdAmPmConsult :IdForm1:IdTimeConsult :IdForm1:IdDepartamentHome :IdForm1:IdDepartamentHomeCode "
                + ":IdForm1:IdMunicipalitieList :IdForm1:IdMunicipalitiesHomeCode :IdForm1:IdNeighborhoodHomeName :IdForm1:IdNeighborhoodsHomeCode :IdForm1:IdDirectionHome :IdForm1:IdSubmitted "
                + ":IdForm1:IdIPS :IdForm1:IdIntentional :IdForm1:IdIntentionality :IdForm1:IdPlace :IdForm1:IdOtherPlace :IdForm1:IdActivities :IdForm1:IdOtherActivities :IdForm1:IdMechanisms "
                + ":IdForm1:IdAnimalWhich :IdForm1:IdHeightWhich :IdForm1:IdPowderWhich :IdForm1:IdDisasterWhich :IdForm1:IdOtherMechanism :IdForm1:IdForBurned :IdForm1:IdLevelBurned :IdForm1:IdPercentBurned :IdForm1:IdUseAlcohol :IdForm1:IdUseDrugs "
                + ":IdForm1:IdTransportTypes :IdForm1:IdTransport :IdForm1:IdOtherTransportType :IdForm1:IdTransportCounterpart :IdForm1:IdOtherTransportCounterpartsType :IdForm1:IdTransportUser :IdForm1:IdOtherTransportUserType "
                + ":IdForm1:IdSecurityElementsOption :IdForm1:IdSecurityElements :IdForm1:IdBeltUse :IdForm1:IdHelmetUse :IdForm1:IdBicycleHelmetUse :IdForm1:IdVestUse :IdForm1:IdOtherElementUse :IdForm1:IdAggressionPast "
                + ":IdForm1:IdRelationshipToVictim :IdForm1:IdOtherRelation :IdForm1:IdCurrentContext :IdForm1:IdAggressorGenders :IdForm1:IdPreviousAttempt :IdForm1:IdMentalPastDisorder :IdForm1:IdPrecipitatingFactor "
                + ":IdForm1:IdAggressionOrSuspicion :IdForm1:IdAggressionOrSuspicionOption :IdForm1:IdInterpersonalViolence :IdForm1:IdDomesticViolence "
                + ":IdForm1:IdOtherFactor :IdForm1:IdAG1 :IdForm1:IdAG2 :IdForm1:IdAG3 :IdForm1:IdAG4 :IdForm1:IdAG5 :IdForm1:IdAG6 "
                + ":IdForm1:IdAG7 :IdForm1:IdAG8 :IdForm1:IdAG9 :IdForm1:IdAG10 :IdForm1:IdOtherAG :IdForm1:IdMA1 "
                + ":IdForm1:IdMA2 :IdForm1:IdMA3 :IdForm1:IdMA4 :IdForm1:IdMA5 :IdForm1:IdMA6 :IdForm1:IdMA7 :IdForm1:IdMA8 :IdForm1:IdOtherMA "
                + ":IdForm1:IdAnatomicalSite1 :IdForm1:IdAnatomicalSite2 :IdForm1:IdAnatomicalSite3 :IdForm1:IdAnatomicalSite4 "
                + ":IdForm1:IdAnatomicalSite5 :IdForm1:IdAnatomicalSite6 :IdForm1:IdAnatomicalSite7 :IdForm1:IdAnatomicalSite8 "
                + ":IdForm1:IdAnatomicalSite9 :IdForm1:IdAnatomicalSite10 :IdForm1:IdAnatomicalSite11 :IdForm1:IdAnatomicalSite12 :IdForm1:IdOtherPlace2 "
                + ":IdForm1:IdNatureOfInjurye1 :IdForm1:IdNatureOfInjurye2 :IdForm1:IdNatureOfInjurye3 :IdForm1:IdNatureOfInjurye4 "
                + ":IdForm1:IdNatureOfInjurye5 :IdForm1:IdNatureOfInjurye6 :IdForm1:IdNatureOfInjurye7 :IdForm1:IdNatureOfInjurye8 "
                + ":IdForm1:IdNatureOfInjurye9 :IdForm1:IdCheckOtherInjury :IdForm1:IdOtherInjury :IdForm1:IdUnknownNatureOfInjurye "
                + ":IdForm1:IdDestinationPatient :IdForm1:IdOtherDestinationPatient :IdForm1:IdIdCIE10_1 :IdForm1:IdTxtCIE10_1 "
                + ":IdForm1:IdIdCIE10_2 :IdForm1:IdTxtCIE10_2 :IdForm1:IdIdCIE10_3 :IdForm1:IdTxtCIE10_3 :IdForm1:IdIdCIE10_4 :IdForm1:IdTxtCIE10_4 "
                + ":IdForm1:IdHealthProfessionals :IdForm1:IdResponsible :IdForm1:IdControls :IdForm1:message :IdForm2:IdSearchCriteria :IdForm2:IdSearcValue :IdForm2:IdSearchTable "
                + ":IdForm1:IdInsurance :IdForm1:IdFormId";
    }

    public void reset() {
        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
        try {
//            //estados de fecha
//            List<StateDate> stateDateL = stateDateFacade.findAll();
//            stateDateList = new SelectItem[stateDateL.size()];
//            for (int i = 0; i < stateDateL.size(); i++) {
//                stateDateList[i] = new SelectItem(stateDateL.get(i).getIdStateDate(), stateDateL.get(i).getName());
//            }
//
//            //estados de hora
//            List<StateTime> stateTimeL = stateTimeFacade.findAll();
//            stateTimeList = new SelectItem[stateTimeL.size()];
//            for (int i = 0; i < stateDateL.size(); i++) {
//                stateTimeList[i] = new SelectItem(stateTimeL.get(i).getIdStateTime(), stateTimeL.get(i).getName());
//            }
            //cargo las aseguradoras
            List<Insurance> insuranceList = insuranceFacade.findAll();
            insurances = new SelectItem[insuranceList.size() + 1];
            insurances[0] = new SelectItem(0, "");
            for (int i = 0; i < insuranceList.size(); i++) {
                insurances[i + 1] = new SelectItem(insuranceList.get(i).getInsuranceId(), insuranceList.get(i).getInsuranceName());
            }

            //cargo las instituciones de salud de donde es remitido
            List<NonFatalDataSources> sourcesList = nonFatalDataSourcesFacade.findAll();
            fromWhereList = new SelectItem[sourcesList.size() + 1];
            fromWhereList[0] = new SelectItem(0, "");
            healthInstitutions = new SelectItem[sourcesList.size() + 1];
            healthInstitutions[0] = new SelectItem(0, "");
            for (int i = 0; i < sourcesList.size(); i++) {
                fromWhereList[i + 1] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceId(), sourcesList.get(i).getNonFatalDataSourceName());
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
            mechanismsList = mechanismsFacade.findAll();
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

            //listado de diagnosticos
            List<Diagnoses> diagnosesList = diagnosesFacade.findAll();
            diagnoses = new SelectItem[diagnosesList.size()];
            for (int i = 0; i < diagnosesList.size(); i++) {
                diagnoses[i] = new SelectItem(diagnosesList.get(i).getDiagnosisId() + " - " + diagnosesList.get(i).getDiagnosisName());
            }
            currentDiagnoses = "S000";

            //categoria boolean
            List<Boolean3> booleanList = boolean3Facade.findAll();
            booleans = new SelectItem[booleanList.size() + 1];
            booleans[0] = new SelectItem(0, "");
            for (int i = 0; i < booleanList.size(); i++) {
                booleans[i + 1] = new SelectItem(booleanList.get(i).getBooleanId(), booleanList.get(i).getBooleanName());
            }
            //

            currentDepartamentHome = 52;
            changeDepartamentHome();
            currentMunicipalitie = 1;

            //lista de criterios de busqueda            
            searchCriteriaList = new SelectItem[3];
            searchCriteriaList[0] = new SelectItem(1, "IDENTIFICACION");
            searchCriteriaList[1] = new SelectItem(2, "NOMBRE");
            searchCriteriaList[2] = new SelectItem(3, "CODIGO INTERNO");

            rowDataTableList = new ArrayList<RowDataTable>();
            //createDynamicTable();

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
            System.out.println("*******************************************ERROR_L1: " + e.toString());
        }
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
            stranger = currentNonFatalInjury.getVictimId().getStranger();
        } catch (Exception e) {
            stranger = false;
        }
        changeStranger();
        //******type_id
        try {
            currentIdentification = currentNonFatalInjury.getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentification = 0;
        }
        changeIdentificationType();
        //******victim_nid
        try {
            currentIdentificationNumber = currentNonFatalInjury.getVictimId().getVictimNid();
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
        currentName = currentNonFatalInjury.getVictimId().getVictimName();
        if (currentName == null) {
            currentName = "";
        }
//        //******victim_firstname
//        currentName = currentNonFatalInjury.getVictimId().getVictimFirstname();
//        if (currentName == null) {
//            currentName = "";
//        }
//        //******victim_lastname
//        currentSurname = currentNonFatalInjury.getVictimId().getVictimLastname();
//        if (currentSurname == null) {
//            currentSurname = "";
//        }
        //******age_type_id
        try {
            currentMeasureOfAge = currentNonFatalInjury.getVictimId().getAgeTypeId();
            if (currentMeasureOfAge == 4) {
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

        //******ethnic_group_id
        try {
            currentEthnicGroup = currentNonFatalInjury.getVictimId().getEthnicGroupId().getEthnicGroupId();
            if (currentEthnicGroup == 3) {
                ethnicGroupsDisabled = false;
            } else {
                ethnicGroupsDisabled = true;
            }
        } catch (Exception e) {
            currentEthnicGroup = 0;
            ethnicGroupsDisabled = true;
        }
        //******victim_telephone
        try {
            currentTelephoneHome = currentNonFatalInjury.getVictimId().getVictimTelephone();
        } catch (Exception e) {
            currentTelephoneHome = "";
        }



        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id
        //******residence_municipality

        //******residence_municipality
        try {
            if (currentNonFatalInjury.getVictimId().getResidenceDepartment() != null) {
                currentDepartamentHome = currentNonFatalInjury.getVictimId().getResidenceDepartment();
                changeDepartamentHome();
                if (currentNonFatalInjury.getVictimId().getResidenceMunicipality() != null) {
                    currentMunicipalitie = currentNonFatalInjury.getVictimId().getResidenceMunicipality();
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

        //informacion de grupos vunerables

        if (currentNonFatalInjury.getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalInjury.getVictimId().getVulnerableGroupsList().isEmpty()) {
                isDisplaced = false;
                isHandicapped = false;
                for (int i = 0; i < currentNonFatalInjury.getVictimId().getVulnerableGroupsList().size(); i++) {
                    if (1 == currentNonFatalInjury.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        isDisplaced = true;
                    }
                    if (2 == currentNonFatalInjury.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        isHandicapped = true;
                    }
                }
            }
        }

        //******insurance_id
        try {
            currentInsurance = currentNonFatalInjury.getVictimId().getInsuranceId().getInsuranceId();
            if (currentInsurance == null) {
                currentInsurance = 0;
            }
        } catch (Exception e) {
            currentInsurance = 0;
        }

        //-----CARGAR CAMPOS OTROS----------------
        List<Others> othersList = currentNonFatalInjury.getVictimId().getOthersList();
        for (int i = 0; i < othersList.size(); i++) {
            switch (othersList.get(i).getOthersPK().getFieldId()) {
                case 1://1.	Cual otro grupo etnico
                    otherEthnicGroup = othersList.get(i).getValueText();
                    ethnicGroupsDisabled = false;
                    break;
                case 2://2.	Cual otro de lugar del hecho
                    currentOtherPlace = othersList.get(i).getValueText();
                    otherPlaceDisabled = false;
                    break;
                case 3://3.	Cual otra actividad
                    currentOtherActivitie = othersList.get(i).getValueText();
                    otherActivityDisabled = false;
                    break;
                case 4://4.	Cual altura
                    heightWhich = othersList.get(i).getValueText();
                    heightWhichDisabled = false;
                    break;
                case 5://5.	Cual polvora
                    powderWhich = othersList.get(i).getValueText();
                    powderWhichDisabled = false;
                    break;
                case 6://6.	Cual desastre natural
                    disasterWhich = othersList.get(i).getValueText();
                    disasterWhichDisabled = false;
                    break;
                case 7://7.	Cual otro mecanismo de objeto
                    otherMechanism = othersList.get(i).getValueText();
                    otherMechanismDisabled = false;
                    break;
                case 8://8.	Cual otro animal
                    otherAnimal = othersList.get(i).getValueText();
                    otherAnimalDisabled = false;
                    break;
                case 9://9.	Cual otro factor precipitante(Autoinflingida intencional)
                    otherFactor = othersList.get(i).getValueText();
                    otherFactorDisabled = false;
                    break;
                case 10://10.	Cual otro tipo de agresor(intrafamiliar)
                    otherAG = othersList.get(i).getValueText();
                    otherAGDisabled = false;
                    break;
                case 11://11.	Cual otro tipo de maltrato(intrafamiliar)
                    otherMA = othersList.get(i).getValueText();
                    otherMADisabled = false;
                    break;
                case 12://12.	Cual otra relación (violencia interpersonal)
                    otherRelation = othersList.get(i).getValueText();
                    otherRelationDisabled = false;
                    break;
                case 13://13.	Cual otro tipo de transporte(transporte)
                    otherTransportType = othersList.get(i).getValueText();
                    otherTransportTypeDisabled = false;
                    break;
                case 14://14.	Cual otro tipo de transporte de contraparte(transporte)
                    otherTransportCounterpartsType = othersList.get(i).getValueText();
                    otherTransportCounterpartsTypeDisabled = false;
                    break;
                case 15://15.	Cual otro tipo de transporte de usuario(transporte)
                    otherTransportUserType = othersList.get(i).getValueText();
                    otherTransportUserTypeDisabled = false;
                    break;
                case 16://16.	Cual otro sitio anatomico
                    txtOtherPlace = othersList.get(i).getValueText();
                    otherAnatomicalPlaceDisabled = false;
                    break;
                case 17://17.	Cual otra naturaleza de la lesión
                    txtOtherInjury = othersList.get(i).getValueText();
                    otherInjuryDisabled = false;
                    break;
                case 18://18.	Cual otro destino del paciente
                    otherDestinationPatient = othersList.get(i).getValueText();
                    otherDestinationPatientDisabled = false;
                    break;
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------
        //******injury_id
        //******checkup_date

        try {
            currentDateConsult = currentNonFatalInjury.getCheckupDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentNonFatalInjury.getCheckupDate());
            currentDayConsult = String.valueOf(cal.get(Calendar.DATE));
            currentMonthConsult = String.valueOf(cal.get(Calendar.MONTH) + 1);
            currentYearConsult = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate2();
        } catch (Exception e) {
            currentDateConsult = "";
        }
        //******checkup_time


        try {
            currentHourConsult = String.valueOf(currentNonFatalInjury.getCheckupTime().getHours());
            currentMinuteConsult = String.valueOf(currentNonFatalInjury.getCheckupTime().getMinutes());
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
            currentAmPmConsult = "SIN DATO";
            changeAmPmConsult();

        }
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
            currentAmPmEvent = "SIN DATO";
            changeAmPmEvent();
        }
        //******injury_address
        currentDirectionEvent = currentNonFatalInjury.getInjuryAddress();
        if (currentDirectionEvent
                == null) {
            currentDirectionEvent = "";
        }
        //******injury_neighborhood_id


        try {
            if (currentNonFatalInjury.getInjuryNeighborhoodId() != null) {
                currentNeighborhoodEventCode = String.valueOf(currentNonFatalInjury.getInjuryNeighborhoodId().getNeighborhoodId());
                currentNeighborhoodEvent = neighborhoodsFacade.find(currentNonFatalInjury.getInjuryNeighborhoodId().getNeighborhoodId()).getNeighborhoodName();
            }
        } catch (Exception e) {
            currentNeighborhoodEventCode = "";
            currentNeighborhoodEvent = "";
        }
        //******injury_place_id


        try {
            currentPlace = currentNonFatalInjury.getInjuryPlaceId().getNonFatalPlaceId();
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
            currentActivities = currentNonFatalInjury.getActivityId().getActivityId();
            if (currentActivities == 98) {
                otherActivityDisabled = false;
            } else {
                otherActivityDisabled = true;
            }
        } catch (Exception e) {
            currentActivities = 0;
            otherActivityDisabled = true;
        }

        try {
            currentActivities = currentNonFatalInjury.getActivityId().getActivityId();
        } catch (Exception e) {
            currentActivities = 0;
        }
        //******intentionality_id
        try {
            currentIntentionality = currentNonFatalInjury.getIntentionalityId().getIntentionalityId();
            displayAggressionOrSuspicion = "none";
            displayInterpersonalViolence = "none";
            displayDomesticViolence = "none";
            displayIntentional = "none";
            switch (currentIntentionality) {
                case 2: //02. Autoinflingida intencional (suicidio)                    
                    displayIntentional = "block";
                    break;
                case 3: //03. Violencia / agresión o sospecha
                    displayAggressionOrSuspicion = "block";
                    //aqui ver cual de los dos se escogio(interpersonal o intrafamiliar)
                    if (currentNonFatalInjury.getNonFatalInterpersonal() != null) {
                        currentAggressionOrSuspicion = 2;
                        displayInterpersonalViolence = "block";//nonFatalInterpersonal
                    } else {
                        currentAggressionOrSuspicion = 1;
                        displayDomesticViolence = "block";//nonFatalDomesticViolence
                    }
                case 1: //01. No intencional (accidentes)                                    
                case 0: //0. vacio                                    
                    break;
            }
        } catch (Exception e) {
            currentIntentionality = 0;
        }
        //******use_alcohol_id


        try {
            currentUseAlcohol = currentNonFatalInjury.getUseAlcoholId().getUseAlcoholDrugsId();
        } catch (Exception e) {
            currentUseAlcohol = 0;
        }
        //******use_drugs_id


        try {
            currentUseDrugs = currentNonFatalInjury.getUseDrugsId().getUseAlcoholDrugsId();
        } catch (Exception e) {
            currentUseDrugs = 0;
        }
        //******burn_injury_degree


        try {
            currentLevelBurned = currentNonFatalInjury.getBurnInjuryDegree();

        } catch (Exception e) {
            currentLevelBurned = 0;
        }
        //******burn_injury_percentage


        try {
            currentPercentBurned = currentNonFatalInjury.getBurnInjuryPercentage().toString();
        } catch (Exception e) {
            currentPercentBurned = "";
        }
        //******submitted_patient eps_id

        try {
            if (currentNonFatalInjury.getSubmittedPatient()) {
                isSubmitted = true;
                if (currentNonFatalInjury.getSubmittedDataSourceId() != null) {
                    currentFromWhere = currentNonFatalInjury.getSubmittedDataSourceId().getNonFatalDataSourceId();
                } else {
                    currentFromWhere = 0;
                }
                fromWhereDisabled = false;
            } else {
                isSubmitted = false;
                fromWhereDisabled = true;
            }
        } catch (Exception e) {
            isSubmitted = false;
            fromWhereDisabled = true;
        }
        //******destination_patient_id


        try {
            if (currentNonFatalInjury.getDestinationPatientId() != null) {
                currentDestinationPatient = currentNonFatalInjury.getDestinationPatientId().getDestinationPatientId();
                otherDestinationPatientDisabled = false;
            } else {
                currentDestinationPatient = 0;
                otherDestinationPatientDisabled = true;
            }
        } catch (Exception e) {
            currentDestinationPatient = 0;
            otherDestinationPatientDisabled = true;
        }
        //******input_timestamp
        //******health_professional_id


        try {
            currentHealthProfessionals = currentNonFatalInjury.getHealthProfessionalId().getHealthProfessionalName();
        } catch (Exception e) {
            currentHealthProfessionals = "";
        }
        //******non_fatal_data_source_id
        //******mechanism_id


        try {
            currentMechanisms = currentNonFatalInjury.getMechanismId().getMechanismId();
            switch (currentMechanisms) {
                case 1:// 01. Lesión de transporte
                    displayTransport = "block";
                    break;
                case 5:// 05. Otra caida, altura __ mts
                    heightWhichDisabled = false;
                    break;
                case 10:// 10. Fuego / llama / humo
                case 11:// 11. Líquido / objeto caliente
                case 12:// 12. Pólvora, cual?
                    powderWhichDisabled = false;
                    forBurned = "block";
                    break;
                case 21:// 21. Minas / munición sin explotar
                case 22:// 22. Otro artefacto explosivo
                case 25:// 25. Electricidad
                    forBurned = "block";
                    break;
                case 26:// 26. Desastre natural, cual?                
                    disasterWhichDisabled = false;
                    break;
                case 27:// 27. Otro, cual?
                    otherMechanismDisabled = false;
                    break;
                case 2:// 02. Agresión sexual
                case 3:// 03. Caida propia altura
                case 4:// 04. Caida por escaleras
                case 6:// 06. Golpe / fuerza contundente
                case 7:// 07. Corte / puñalada
                case 8:// 08. Objeto corto-contundente
                case 9:// 09. Arma de fuego
                case 13:// 13. Estrangulado / ahorcado
                case 14:// 14. Inmersión
                case 15:// 15. Asfixia por cuerpo extraño
                case 16:// 16. Lesion por cuerpo extraño
                case 17:// 17. Fármacos
                case 18:// 18. Plaguicidas
                case 19:// 19. Hidrocarburos
                case 20:// 20. Otros tóxicos
                case 23:// 23. Mordedura de persona
                case 24:// 24. Animal, cual?                
                case 28:// 28. No se sabe
                    break;
            }

        } catch (Exception e) {
            currentMechanisms = 0;
        }
        changeMechanisms();
        //******user_id
        currentResponsible = "ADMIN";
        //******injury_day_of_week


        try {
            currentWeekdayEvent = currentNonFatalInjury.getInjuryDayOfWeek();
        } catch (Exception e) {
            currentWeekdayEvent = "";
        }


        try {
            currentHealthInstitution = currentNonFatalInjury.getNonFatalDataSourceId().getNonFatalDataSourceId();
        } catch (Exception e) {
            currentHealthInstitution = 0;
        }

        //determino el tipo de lesion

//        short injury_type = currentNonFatalInjury.getInjuryId().getInjuryId();
//
//        switch (injury_type) {
//
//            case 50://interpersonal
        //                break;
//            case 51://accidente de transito
//                break;
//            case 52://intencional autoinflingida
//                break;
//            case 54://no intencional
//                break;
//            case 55://intrafamiliar
//                break;
//        }

        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------

        try {
            aggressionPast = currentNonFatalInjury.getNonFatalInterpersonal().getPreviousAntecedent().getBooleanId();
        } catch (Exception e) {
            aggressionPast = 0;
        }


        try {
            currentRelationshipToVictim = currentNonFatalInjury.getNonFatalInterpersonal().getRelationshipVictimId().getRelationshipVictimId();
            if (currentRelationshipToVictim == 3) {//3. otro
                otherRelationDisabled = false;
            } else {
                otherRelationDisabled = true;
            }
        } catch (Exception e) {
            currentRelationshipToVictim = 0;
            otherRelationDisabled = true;
        }


        try {
            currentContext = currentNonFatalInjury.getNonFatalInterpersonal().getContextId().getContextId();
        } catch (Exception e) {
            currentContext = 0;
        }


        try {
            currentAggressorGenders = currentNonFatalInjury.getNonFatalInterpersonal().getAggressorGenderId().getGenderId();
        } catch (Exception e) {
            currentAggressorGenders = 0;
        }



        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
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
        try {
            List<AggressorTypes> aggressorTypesList = currentNonFatalInjury.getNonFatalDomesticViolence().getAggressorTypesList();
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
        } catch (Exception e) {
            //System.out.println("no se cargo violencia intrafamiliar"+e.toString());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        isMA1 = false;
        isMA2 = false;
        isMA3 = false;
        isMA4 = false;
        isMA5 = false;
        isMA6 = false;
        isMA8 = false;
        isUnknownMA = false;
        try {
            List<AbuseTypes> abuseTypesList = currentNonFatalInjury.getNonFatalDomesticViolence().getAbuseTypesList();
            for (int i = 0;
                    i < abuseTypesList.size();
                    i++) {
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
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de abusos(tipos de maltrato)-----------------------------------

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
        try {
            List<AnatomicalLocations> anatomicalLocationsList = currentNonFatalInjury.getAnatomicalLocationsList();
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
                    case 98:
                        checkOtherPlace = true;
                        otherAnatomicalPlaceDisabled = false;
                        break;
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo sitios anatomicos"+e.toString());
        }

        //cargo la naturaleza de la lesion

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
        try {
            List<KindsOfInjury> kindsOfInjuryList = currentNonFatalInjury.getKindsOfInjuryList();
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
                    case 98:
                        checkOtherInjury = true;
                        break;
                    case 99:
                        isUnknownNatureOfInjurye = true;
                        break;
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }
        //cargo los diagnosticos
        try {
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
                        txtCIE10_3 = diagnosesList.get(i).getDiagnosisName();
                        break;
                    case 3:
                        idCIE10_4 = diagnosesList.get(i).getDiagnosisId();
                        txtCIE10_4 = diagnosesList.get(i).getDiagnosisName();
                        break;
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo codigo CIE"+e.toString());
        }

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------

        try {
            previousAttempt = currentNonFatalInjury.getNonFatalSelfInflicted().getPreviousAttempt().getBooleanId();
        } catch (Exception e) {
            previousAttempt = 0;
        }


        try {
            mentalPastDisorder = currentNonFatalInjury.getNonFatalSelfInflicted().getMentalAntecedent().getBooleanId();
        } catch (Exception e) {
            mentalPastDisorder = 0;
        }


        try {
            currentPrecipitatingFactor = currentNonFatalInjury.getNonFatalSelfInflicted().getPrecipitatingFactorId().getPrecipitatingFactorId();
        } catch (Exception e) {
            currentPrecipitatingFactor = 0;
        }

        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------

        try {
            currentTransportTypes = currentNonFatalInjury.getNonFatalTransport().getTransportTypeId().getTransportTypeId();
            if (currentTransportTypes == 10) {//otro
                otherTransportTypeDisabled = false;
            } else {
                otherTransportTypeDisabled = true;
            }
        } catch (Exception e) {
            currentTransportTypes = 0;
            otherTransportTypeDisabled = true;
        }

        try {
            currentTransportCounterpart = currentNonFatalInjury.getNonFatalTransport().getTransportCounterpartId().getTransportCounterpartId();
            if (currentTransportCounterpart == 12) {//otro
                otherTransportCounterpartsTypeDisabled = false;
            } else {
                otherTransportCounterpartsTypeDisabled = true;
            }
        } catch (Exception e) {
            currentTransportCounterpart = 0;
            otherTransportCounterpartsTypeDisabled = true;
        }

        try {
            currentTransportUser = currentNonFatalInjury.getNonFatalTransport().getTransportUserId().getTransportUserId();
            changeTransportType();
        } catch (Exception e) {
            otherTransportUserTypeDisabled = true;
            currentTransportUser = 0;
        }

        try {
            List<SecurityElements> securityElementsList = currentNonFatalInjury.getNonFatalTransport().getSecurityElementsList();
            for (int i = 0; i < securityElementsList.size(); i++) {
                switch (securityElementsList.get(i).getSecurityElementId()) {
                    case 1:
                        currentSecurityElements = "SI";
                        displaySecurityElements = "block";
                        isBeltUse = true;
                        break;
                    case 2:
                        displaySecurityElements = "block";
                        currentSecurityElements = "SI";
                        isHelmetUse = true;
                        break;
                    case 3:
                        displaySecurityElements = "block";
                        currentSecurityElements = "SI";
                        isBicycleHelmetUse = true;
                        break;
                    case 4:
                        displaySecurityElements = "block";
                        currentSecurityElements = "SI";
                        isVestUse = true;
                        break;
                    case 5:
                        displaySecurityElements = "block";
                        currentSecurityElements = "SI";
                        isOtherElementUse = true;
                        break;
                    case 6:
                        currentSecurityElements = "NO";
                        displaySecurityElements = "none";
                        break;
                    case 7:
                        currentSecurityElements = "NO SE SABE";
                        displaySecurityElements = "none";
                        break;

                }
            }

        } catch (Exception e) {
            //System.out.println("no se cargo elementos de seguridad"+e.toString());
        }
        save = true;
        stylePosition = "color: #1471B1;";
        loading = false;
    }

    private boolean validateFields() {
        validationsErrors = new ArrayList<String>();




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

        //---------(PARA SECCION TRANSITO) VALIDAR QUE SI ELEMENTOS DE SEGURIDAD ESTA EN SI SELECCIONE ALMENOS UNA OPCION
        if (currentSecurityElements.compareTo("SI") == 0) {
            if (!isBeltUse && !isHelmetUse && !isBicycleHelmetUse && !isVestUse && !isOtherElementUse) {
                validationsErrors.add("Como la opción: elementos de seguridad está en \"SI\", se debe seleccionar al menos un elemento de seguridad de la lista");
            }

        }
        //---------VALIDAR QUE SE HAYA INGRESADO UNA INTENCIONALIDAD
        if (currentIntentionality == 0) {
            validationsErrors.add("Es obligatorio seleccionar una intencionalidad");
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
                if (currentNonFatalInjuriId == -1) {//SI ES NUEVO
                    newVictim.setVictimId(victimsFacade.findMax() + 1);
                } else {//SI SE ESTA MODIFICANDO
                    newVictim.setVictimId(currentNonFatalInjury.getVictimId().getVictimId());
                }
                //******stranger                
                newVictim.setStranger(stranger);

                if (currentIdentification != 0) {
                    newVictim.setTypeId(idTypesFacade.find(currentIdentification));
                }
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
                //if (currentVulnerableGroup != 0) {
                //	newVictim.setVulnerableGroupId(vulnerableGroupsFacade.find(currentVulnerableGroup));
                //}


                if (currentEthnicGroup != 0) {
                    newVictim.setEthnicGroupId(ethnicGroupsFacade.find(currentEthnicGroup));
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
                if (currentNeighborhoodHomeCode.trim().length() != 0) {
                }
                //newVictim.setVictimClass(null);            
                if (currentMunicipalitie != 0) {
                    newVictim.setResidenceMunicipality(currentMunicipalitie);
                }

                //informacion de grupos vunerables
                List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                if (isHandicapped) {
                    VulnerableGroups vg = vulnerableGroupsFacade.find(Short.parseShort("2"));//discapacitado
                    vulnerableGroupsList.add(vg);
                }
                if (isDisplaced) {
                    VulnerableGroups vg = vulnerableGroupsFacade.find(Short.parseShort("1"));//desplazado            
                    vulnerableGroupsList.add(vg);
                }
                if (!vulnerableGroupsList.isEmpty()) {
                    newVictim.setVulnerableGroupsList(vulnerableGroupsList);
                } else {
                    newVictim.setVulnerableGroupsList(null);
                }
                if (currentInsurance != 0) {
                    newVictim.setInsuranceId(insuranceFacade.find(currentInsurance));
                }


                //------------------------------------------------------------
                //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------
                NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();


                //newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 54));//es 54 por ser no fatal

//                if (currentDepartamentHome != 0) {
//                    newNonFatalInjuries.setResidenceDepartament(currentDepartamentHome);
//                }
                if (currentDepartamentHome != 0) {
                    newVictim.setResidenceDepartment(currentDepartamentHome);
                }

                if (currentNonFatalInjuriId == -1) {//SI ES NUEVO
                    newNonFatalInjuries.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                } else {//SI SE ESTA MODIFICANDO
                    newNonFatalInjuries.setNonFatalInjuryId(currentNonFatalInjury.getNonFatalInjuryId());
                }

                if (currentDateConsult.trim().length() != 0) {
                    newNonFatalInjuries.setCheckupDate(formato.parse(currentDateConsult));
                }
                if (currentMilitaryHourConsult.trim().length() != 0) {
                    if (currentAmPmConsult.compareTo("PM") == 0) {
                        if (currentHourConsult.compareTo("12") != 0) {
                            currentHourConsult = String.valueOf(Integer.parseInt(currentHourConsult) + 12);
                        }
                    }
                    int hourInt = Integer.parseInt(currentHourConsult);
                    int minuteInt = Integer.parseInt(currentMinuteConsult);
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
                    if (currentAmPmEvent.compareTo("PM") == 0) {
                        if (currentHourEvent.compareTo("12") != 0) {
                            currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) + 12);
                        }
                    }
                    int hourInt = Integer.parseInt(currentHourEvent);
                    int minuteInt = Integer.parseInt(currentMinuteEvent);
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
                    //newNonFatalInjuries.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(currentFromWhere));
                }
                if (currentFromWhere != 0) {
                    newNonFatalInjuries.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(currentFromWhere));
                }

                if (currentDestinationPatient != 0) {
                    newNonFatalInjuries.setDestinationPatientId(destinationsOfPatientFacade.find(currentDestinationPatient));
                }


                newNonFatalInjuries.setInputTimestamp(new Date());
                if (currentPercentBurned.trim().length() != 0) {
                    newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort(currentPercentBurned));
                }

                //newNonFatalInjuries.setHealthProfessionalId(null);

                if (currentHealthInstitution != 0) {
                    newNonFatalInjuries.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(currentHealthInstitution));
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
                    anatomicalLocationList.add(anatomicalLocationsFacade.find((short) 98));
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
                if (idCIE10_4.trim().length() != 0) {
                    diagnosesesList.add(diagnosesFacade.find(idCIE10_4));
                }
                newNonFatalInjuries.setDiagnosesList(diagnosesesList);

                if (currentHealthProfessionals != null) {
                    if (currentHealthProfessionals.trim().length() != 0) {
                        newNonFatalInjuries.setHealthProfessionalId(healthProfessionalsFacade.findByName(currentHealthProfessionals));
                    }
                }

                NonFatalDomesticViolence newNonFatalDomesticViolence = null;
                NonFatalInterpersonal newNonFatalInterpersonal = null;
                NonFatalSelfInflicted newNonFatalSelfInflicted = null;
                switch (currentIntentionality) {
                    case 2: //02. Autoinflingida intencional (suicidio)                    
                        //------------------------------------------------------------
                        //AUTOINFLINGIDA INTENCIONAL
                        //------------------------------------------------------------
                        newNonFatalSelfInflicted = new NonFatalSelfInflicted();
                        if (previousAttempt != 0) {
                            newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find(previousAttempt));
                        }
                        if (mentalPastDisorder != 0) {
                            newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find(mentalPastDisorder));
                        }
                        if (currentPrecipitatingFactor != 0) {
                            newNonFatalSelfInflicted.setPrecipitatingFactorId(precipitatingFactorsFacade.find(currentPrecipitatingFactor));
                        }
                        newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                        break;
                    case 3: //03. Violencia / agresión o sospecha
                        if (currentAggressionOrSuspicion == 2) {//2.VIOLENCIA INTERPERSONAL
                            //------------------------------------------------------------
                            //SE CREA VARIABLE PARA VIOLENCIA INTERPERSONAL
                            //------------------------------------------------------------
                            newNonFatalInterpersonal = new NonFatalInterpersonal();
                            newNonFatalInterpersonal.setPreviousAntecedent(boolean3Facade.find(aggressionPast));
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
                        } else {//1. VIOLENCIA INTRAFAMILIAR
                            //------------------------------------------------------------
                            //SE CREA VARIABLE PARA VIOLENCIA INTRAFAMILIAR
                            //------------------------------------------------------------
                            newNonFatalDomesticViolence = new NonFatalDomesticViolence();
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
                        }
                        break;
                    case 1: //01. No intencional (accidentes)                                    
                    case 0: //0. vacio                                    
                        break;
                }

                //------------------------------------------------------------
                //SE CREA VARIABLE PARA TRANSITO
                //------------------------------------------------------------
                NonFatalTransport newNonFatalTransport = null;
                if (currentMechanisms == 1) {//el mecanismo objeto de la lesion es transito
                    newNonFatalTransport = new NonFatalTransport();
                    if (currentTransportTypes != 0) {
                        newNonFatalTransport.setTransportTypeId(transportTypesFacade.find(currentTransportTypes));
                    }
                    if (currentTransportCounterpart != 0) {
                        newNonFatalTransport.setTransportCounterpartId(transportCounterpartsFacade.find(currentTransportCounterpart));
                    }
                    if (currentTransportUser != 0) {
                        newNonFatalTransport.setTransportUserId(transportUsersFacade.find(currentTransportUser));
                    }
                    List<SecurityElements> securityElementsList = new ArrayList<SecurityElements>();
                    if (currentSecurityElements.compareTo("SI") == 0) {
                        if (isBeltUse) {
                            securityElementsList.add(securityElementsFacade.find((short) 1));
                        }
                        if (isHelmetUse) {
                            securityElementsList.add(securityElementsFacade.find((short) 2));
                        }
                        if (isBicycleHelmetUse) {
                            securityElementsList.add(securityElementsFacade.find((short) 3));
                        }
                        if (isVestUse) {
                            securityElementsList.add(securityElementsFacade.find((short) 4));
                        }
                        if (isOtherElementUse) {
                            securityElementsList.add(securityElementsFacade.find((short) 5));
                        }
                    } else {
                        if (currentSecurityElements.compareTo("NO") == 0) {
                            securityElementsList.add(securityElementsFacade.find((short) 6));
                        }
                        if (currentSecurityElements.compareTo("NO SE SABE") == 0) {
                            securityElementsList.add(securityElementsFacade.find((short) 7));
                        }
                    }
                    newNonFatalTransport.setSecurityElementsList(securityElementsList);
                    newNonFatalTransport.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                }

                //-----GUARDAR CAMPOS OTROS----------------
                List<Others> othersList = new ArrayList<Others>();
                Others newOther;
                OthersPK newOtherPK;
                if (otherEthnicGroup.trim().length() != 0) {//1.	Cual otro grupo etnico
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 1);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherEthnicGroup);
                    othersList.add(newOther);
                }
                if (currentOtherPlace.trim().length() != 0) {//2.	Cual otro de lugar del hecho
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 2);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(currentOtherPlace);
                    othersList.add(newOther);
                }
                if (currentOtherActivitie.trim().length() != 0) {//3.	Cual otra actividad
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 3);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(currentOtherActivitie);
                    othersList.add(newOther);
                }
                if (heightWhich.trim().length() != 0) {//4.	Cual altura
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 4);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(heightWhich);
                    othersList.add(newOther);
                }
                if (powderWhich.trim().length() != 0) {//5.	Cual polvora
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 5);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(powderWhich);
                    othersList.add(newOther);
                }
                if (disasterWhich.trim().length() != 0) {//6.	Cual desastre natural
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 6);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(disasterWhich);
                    othersList.add(newOther);
                }
                if (otherMechanism.trim().length() != 0) {//7.	Cual otro mecanismo de objeto
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 7);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherMechanism);
                    othersList.add(newOther);
                }
                if (otherAnimal.trim().length() != 0) {//8.	Cual otro animal
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 8);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherAnimal);
                    othersList.add(newOther);
                }
                if (otherFactor.trim().length() != 0) {//9.	Cual otro factor precipitante(Autoinflingida intencional)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 0);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherFactor);
                    othersList.add(newOther);
                }
                if (otherAG.trim().length() != 0) {//10.	Cual otro tipo de agresor(intrafamiliar)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 10);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherAG);
                    othersList.add(newOther);
                }
                if (otherMA.trim().length() != 0) {//11.	Cual otro tipo de maltrato(intrafamiliar)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 11);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherMA);
                    othersList.add(newOther);
                }
                if (otherRelation.trim().length() != 0) {//12.	Cual otra relación (violencia interpersonal)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 12);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherRelation);
                    othersList.add(newOther);
                }
                if (otherTransportType.trim().length() != 0) {//13.	Cual otro tipo de transporte(transporte)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 13);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherTransportType);
                    othersList.add(newOther);
                }
                if (otherTransportCounterpartsType.trim().length() != 0) {//14.	Cual otro tipo de transporte de contraparte(transporte)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 14);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherTransportCounterpartsType);
                    othersList.add(newOther);
                }
                if (otherTransportUserType.trim().length() != 0) {//15.	Cual otro tipo de transporte de usuario(transporte)
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 15);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherTransportUserType);
                    othersList.add(newOther);
                }
                if (txtOtherPlace.trim().length() != 0) {//16.	Cual otro sitio anatomico
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 16);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(txtOtherPlace);
                    othersList.add(newOther);
                }
                if (txtOtherInjury.trim().length() != 0) {//17.	Cual otra naturaleza de la lesión
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 17);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(txtOtherInjury);
                    othersList.add(newOther);
                }
                if (otherDestinationPatient.trim().length() != 0) {//18.	Cual otro destino del paciente
                    newOther = new Others();
                    newOtherPK = new OthersPK();
                    newOtherPK.setVictimId(newVictim.getVictimId());
                    newOtherPK.setFieldId((short) 18);
                    newOther.setOthersPK(newOtherPK);
                    newOther.setValueText(otherDestinationPatient);
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
                if (currentNonFatalInjuriId == -1) {//ES UN NUEVO REGISTRO SE DEBE PERSISTIR
                    //System.out.println("guardando nuevo registro");
                    if (currentIntentionality == 1) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 54));//54. No intencional
                    }
                    if (newNonFatalDomesticViolence != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 55));//55. Violencia intrafamiliar LCENF;
                    }
                    if (newNonFatalInterpersonal != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 50));//50. Violencia interpersonal
                    }
                    if (newNonFatalSelfInflicted != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 52));//52. Intencional autoinflingida
                    }
                    if (newNonFatalTransport != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 51));//51. Lesión en accidente de tránsito
                    }

                    victimsFacade.create(newVictim);
                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
                    if (newNonFatalDomesticViolence != null) {
                        nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    }
                    if (newNonFatalInterpersonal != null) {
                        nonFatalInterpersonalFacade.create(newNonFatalInterpersonal);
                    }
                    if (newNonFatalSelfInflicted != null) {
                        nonFatalSelfInflictedFacade.create(newNonFatalSelfInflicted);
                    }
                    if (newNonFatalTransport != null) {
                        nonFatalTransportFacade.create(newNonFatalTransport);
                    }
                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    System.out.println("nuevo registro almacenado");
                } else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR                    
                    System.out.println("actualizando registro existente");
                    if (currentNonFatalInjury.getNonFatalDomesticViolence() != null) {
                        nonFatalDomesticViolenceFacade.remove(currentNonFatalInjury.getNonFatalDomesticViolence());
                    }
                    if (currentNonFatalInjury.getNonFatalInterpersonal() != null) {
                        nonFatalInterpersonalFacade.remove(currentNonFatalInjury.getNonFatalInterpersonal());
                    }
                    if (currentNonFatalInjury.getNonFatalSelfInflicted() != null) {
                        nonFatalSelfInflictedFacade.remove(currentNonFatalInjury.getNonFatalSelfInflicted());
                    }
                    if (currentNonFatalInjury.getNonFatalTransport() != null) {
                        nonFatalTransportFacade.remove(currentNonFatalInjury.getNonFatalTransport());
                    }
                    nonFatalInjuriesFacade.remove(currentNonFatalInjury);
                    victimsFacade.remove(currentNonFatalInjury.getVictimId());

                    /////////////////
                    if (currentIntentionality == 1) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 54));//54. No intencional
                    }
                    if (newNonFatalDomesticViolence != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 55));//55. Violencia intrafamiliar LCENF;
                    }
                    if (newNonFatalInterpersonal != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 50));//50. Violencia interpersonal
                    }
                    if (newNonFatalSelfInflicted != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 52));//52. Intencional autoinflingida
                    }
                    if (newNonFatalTransport != null) {
                        newNonFatalInjuries.setInjuryId(injuriesFacade.find((short) 51));//51. Lesión en accidente de tránsito
                    }
                    victimsFacade.create(newVictim);
                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
                    if (newNonFatalDomesticViolence != null) {
                        nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    }
                    if (newNonFatalInterpersonal != null) {
                        nonFatalInterpersonalFacade.create(newNonFatalInterpersonal);
                    }
                    if (newNonFatalSelfInflicted != null) {
                        nonFatalSelfInflictedFacade.create(newNonFatalSelfInflicted);
                    }
                    if (newNonFatalTransport != null) {
                        nonFatalTransportFacade.create(newNonFatalTransport);
                    }
                    save = true;
                    stylePosition = "color: #1471B1;";
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ACTUALIZADO");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                return true;
            } catch (Exception e) {
                System.out.println("*******************************************ERROR_L2: " + e.toString());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        } else {
            return false;
        }
    }

    public void saveAndGoNext() {//guarda cambios si se han realizado y se dirije al siguiente
        if (saveRegistry()) {
            next();
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
        openDialogFirst = "-";
        openDialogNext = "-";
        openDialogLast = "-";
        openDialogPrevious = "-";
        openDialogNew = "-";
        openDialogDelete = "-";
        save = true;
        stylePosition = "color: #1471B1;";
        newForm();

    }

    public void noSaveAndGoNext() {//va al siguiente sin guardar cambios si se han realizado
        openDialogFirst = "-";
        openDialogNext = "-";
        openDialogLast = "-";
        openDialogPrevious = "-";
        openDialogNew = "-";
        openDialogDelete = "-";
        save = true;
        stylePosition = "color: #1471B1;";
        next();
    }

    public void noSaveAndGoPrevious() {//va al anterior sin guardar cambios si se han realizado
        openDialogFirst = "-";
        openDialogNext = "-";
        openDialogLast = "-";
        openDialogPrevious = "-";
        openDialogNew = "-";
        openDialogDelete = "-";
        save = true;
        stylePosition = "color: #1471B1;";
        if (currentNonFatalInjuriId != -1) {
            previous();
        } else {
            last();
        }

    }

    public void noSaveAndGoFirst() {//va al primero sin guardar cambios si se han realizado
        openDialogFirst = "-";
        openDialogNext = "-";
        openDialogLast = "-";
        openDialogPrevious = "-";
        openDialogNew = "-";
        openDialogDelete = "-";
        save = true;
        stylePosition = "color: #1471B1;";
        first();
    }

    public void noSaveAndGoLast() {//va al ultimo sin guardar cambios si se han realizado
        openDialogFirst = "-";
        openDialogNext = "-";
        openDialogLast = "-";
        openDialogPrevious = "-";
        openDialogNew = "-";
        openDialogDelete = "-";
        save = true;
        stylePosition = "color: #1471B1;";
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
                last();
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
        //System.out.println("Entra en first(): " + openDialogFirst + " save:" + String.valueOf(save));
        if (save) {
            openDialogFirst = "-";
            openDialogNext = "-";
            openDialogLast = "-";
            openDialogPrevious = "-";
            openDialogNew = "-";
            //System.out.println("cargando primer registro");
            //System.out.println("esta guardado se dirige a primer registro, opendialogFirst: " + openDialogFirst + " save:" + String.valueOf(save));
            auxNonFatalInjury = nonFatalInjuriesFacade.findFirst();
            if (auxNonFatalInjury != null) {
                clearForm();
                currentNonFatalInjury = auxNonFatalInjury;
                currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            //System.out.println("No esta guardadado (para poder cargar primer registro)");
        }
        System.out.println("dialog firts sale en : " + openDialogFirst);
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

        otherTransportTypeDisabled = true;
        otherTransportUserTypeDisabled = true;
        otherTransportType = "";
        transportUserDisabled = false;
        securityElementsDisabled = false;
        currentTransportUser = 0;
        currentSecurityElements = "NO";
        displaySecurityElements = "none";

        displayAggressionOrSuspicion = "none";
        displayInterpersonalViolence = "none";
        displayDomesticViolence = "none";
        displayIntentional = "none";

        currentHealthInstitution = 0;
        isDisplaced = false;
        isHandicapped = false;
        currentEthnicGroup = 0;
        otherEthnicGroup = "";
        ethnicGroupsDisabled = true;


        currentDepartamentHome = 52;
        changeDepartamentHome();
        currentMunicipalitie = 1;

//        currentDepartamentHome = 0;
//        municipalities = new SelectItem[1];
//        municipalities[0] = new SelectItem(0, "");
//        currentMunicipalitie = 0;


        currentDirectionHome = "";

        currentTelephoneHome = "";

        isSubmitted = false;
        fromWhereDisabled = true;
        currentFromWhere = 0;

        currentIntentionality = 0;
        currentOtherIntentionality = "";

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
        otherAnimal = "";
        otherAnimalDisabled = true;

        currentLevelBurned = 0;
        forBurned = "none";
        currentPercentBurned = "";
        currentUseAlcohol = 0;
        currentUseDrugs = 0;

        //LESION POR TRANSPOTE
        displayTransport = "none";
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

        //VIOLENCIA INTERPERSONAL
        displayInterpersonalViolence = "none";
        aggressionPast = 0;
        currentRelationshipToVictim = 0;
        otherRelation = "";
        otherRelationDisabled = true;
        currentContext = 0;
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
        otherAG = "";
        otherAGDisabled = true;

        isMA1 = false;
        isMA2 = false;
        isMA3 = false;
        isMA4 = false;
        isMA5 = false;
        isMA6 = false;
        isUnknownMA = false;
        isMA8 = false;
        otherMA = "";
        otherMADisabled = true;


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
        otherAnatomicalPlaceDisabled = true;

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
        otherInjuryDisabled = true;
        isUnknownNatureOfInjurye = false;
        neighborhoodHomeNameDisabled = false;
        directionHomeDisabled = false;
        save = true;
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

    public void deleteRegistry() {
        if (currentNonFatalInjuriId != -1) {
            if (currentNonFatalInjury.getNonFatalDomesticViolence() != null) {
                nonFatalDomesticViolenceFacade.remove(currentNonFatalInjury.getNonFatalDomesticViolence());
            }
            if (currentNonFatalInjury.getNonFatalInterpersonal() != null) {
                nonFatalInterpersonalFacade.remove(currentNonFatalInjury.getNonFatalInterpersonal());
            }
            if (currentNonFatalInjury.getNonFatalSelfInflicted() != null) {
                nonFatalSelfInflictedFacade.remove(currentNonFatalInjury.getNonFatalSelfInflicted());
            }
            if (currentNonFatalInjury.getNonFatalTransport() != null) {
                nonFatalTransportFacade.remove(currentNonFatalInjury.getNonFatalTransport());
            }
            nonFatalInjuriesFacade.remove(currentNonFatalInjury);
            victimsFacade.remove(currentNonFatalInjury.getVictimId());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha eliminado el registro");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            System.out.println("registro eliminado");
            noSaveAndGoNew();
        }
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
        if (loading == false) {
            changeForm();
        }

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
        totalRegisters = nonFatalInjuriesFacade.countLCENF();
        if (currentNonFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            currentIdForm = String.valueOf(nonFatalInjuriesFacade.findMax() + 1);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            int position = nonFatalInjuriesFacade.findPosition(currentNonFatalInjury.getNonFatalInjuryId());
            currentIdForm = String.valueOf(currentNonFatalInjury.getNonFatalInjuryId());
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
            //auxNonFatalInjury = nonFatalInjuriesFacade.findByIdVictim(selectedRowDataTable.getColumn1());
            auxNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            if (auxNonFatalInjury != null) {
                clearForm();
                currentNonFatalInjury = auxNonFatalInjury;
                currentNonFatalInjuriId = currentNonFatalInjury.getNonFatalInjuryId();
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
                conx = new ConnectionJDBC();
                conx.connect();
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
                if (date1 != null) {
                    sql = sql + "non_fatal_injuries.input_timestamp < " + date1.toString() + " AND ";
                }
                if (date2 != null) {
                    sql = sql + "non_fatal_injuries.input_timestamp > " + date2.toString() + " AND ";
                }
                //sql = sql + "(injuries.injury_id = 53 OR ";
                sql = sql + "(injuries.injury_id = 50 OR ";
                sql = sql + "injuries.injury_id = 51 OR ";
                sql = sql + "injuries.injury_id = 52 OR ";
                sql = sql + "injuries.injury_id = 54 OR ";
                sql = sql + "injuries.injury_id = 55);";
                System.out.println(sql);
                ResultSet rs = conx.consult(sql);
                conx.disconnect();
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

//    public void changeStateDateEvent() {
//       if (loading == false) {             changeForm();         }
//        currentDayEventDisabled = true;
//        currentMonthEventDisabled = true;
//        currentYearEventDisabled = true;
//        currentDayEvent = "";
//        currentMonthEvent = "";
//        currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
//        switch (currentStateDateEvent) {
//            case 1://fecha determinada
//                currentDayEventDisabled = false;
//                currentMonthEventDisabled = false;
//                currentYearEventDisabled = false;
//                break;
//            case 2://sin determinar
//                break;
//            case 3://sin dia                
//                currentMonthEventDisabled = false;
//                currentYearEventDisabled = false;
//                break;
//            case 4://sin mes
//                currentYearEventDisabled = false;
//                break;
//        }
//    }
//
//    public void changeStateDateConsult() {
//       if (loading == false) {             changeForm();         }
//        currentDayConsultDisabled = true;
//        currentMonthConsultDisabled = true;
//        currentYearConsultDisabled = true;
//        currentDayConsult = "";
//        currentMonthConsult = "";
//        currentYearConsult = Integer.toString(c.get(Calendar.YEAR));
//        switch (currentStateDateConsult) {
//            case 1://fecha determinada
//                currentDayConsultDisabled = false;
//                currentMonthConsultDisabled = false;
//                currentYearConsultDisabled = false;
//                break;
//            case 2://sin determinar
//                break;
//            case 3://sin dia                
//                currentMonthConsultDisabled = false;
//                currentYearConsultDisabled = false;
//                break;
//            case 4://sin mes
//                currentYearConsultDisabled = false;
//                break;
//        }
//    }
//
//    public void changeStateTimeEvent() {
//       if (loading == false) {             changeForm();         }
//        currentHourEventDisabled = true;
//        currentMinuteEventDisabled = true;
//        currentAmPmEventDisabled = true;
//        currentHourEvent = "";
//        currentMinuteEvent = "";
//        currentAmPmEvent = "AM";
//        switch (currentStateTimeEvent) {
//            case 1://hora determinada
//                currentHourEventDisabled = false;
//                currentMinuteEventDisabled = false;
//                currentAmPmEventDisabled = false;
//                break;
//            case 2://hora sin determinar
//                break;
//            case 3://sin minutos                
//                currentHourEventDisabled = false;
//                currentAmPmEventDisabled = false;
//                break;
//            case 4://sin horas
//                currentAmPmEventDisabled = false;
//                break;
//        }
//    }
//
//    public void changeStateTimeConsult() {
//       if (loading == false) {             changeForm();         }
//        currentHourConsultDisabled = true;
//        currentMinuteConsultDisabled = true;
//        currentAmPmConsultDisabled = true;
//        currentHourConsult = "";
//        currentMinuteConsult = "";
//        currentAmPmConsult = "AM";
//        switch (currentStateTimeConsult) {
//            case 1://hora determinada
//                currentHourConsultDisabled = false;
//                currentMinuteConsultDisabled = false;
//                currentAmPmConsultDisabled = false;
//                break;
//            case 2://hora sin determinar
//                break;
//            case 3://sin minutos                
//                currentHourConsultDisabled = false;
//                currentAmPmConsultDisabled = false;
//                break;
//            case 4://sin horas
//                currentAmPmConsultDisabled = false;
//                break;
//        }
//    }
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

    public void changeUnknownNatureOfInjurye() {
        if (loading == false) {
            changeForm();
        }
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
            otherInjuryDisabled = true;
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

    public void changeIdentificationType() {

        if (loading == false) {
            changeForm();
        }

        if (currentIdentification == 3 || currentIdentification == 2) {//pasaporte//
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

    public void changeDepartamentHome() {
        if (loading == false) {
            changeForm();
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
        neighborhoodHomeNameDisabled = true;
        directionHomeDisabled = true;
        currentNeighborhoodHome = "";
        currentNeighborhoodHomeCode = "";
        currentDirectionHome = "";
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
        try {
            int yearInt = Integer.parseInt(currentYearEvent);
            if (yearInt < 0) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentYearEvent.length() != 0) {
                currentYearEvent = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número");
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
        try {
            int yearInt = Integer.parseInt(currentYearConsult);
            if (yearInt < 0) {
                currentYearConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año de la consulta debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } catch (Exception e) {
            if (currentYearConsult.length() != 0) {
                currentYearConsult = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El año del evento debe ser un número, y mayor que cero");
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

    public void changeOtherFactor() {
        if (loading == false) {
            changeForm();
        }
        if (currentPrecipitatingFactor == 98) {//98. otro
            otherFactorDisabled = false;
        } else {
            otherFactorDisabled = true;
            otherFactor = "";
        }
    }

    public void changeRelationshipToVictim() {
        if (loading == false) {
            changeForm();
        }
        if (currentRelationshipToVictim == 3) {//3. otro
            otherRelationDisabled = false;
        } else {
            otherRelationDisabled = true;
            otherRelation = "";
        }
    }

    public void changeOtherInjury() {
        if (loading == false) {
            changeForm();
        }
        if (checkOtherInjury) {
            otherInjuryDisabled = false;

        } else {
            otherInjuryDisabled = true;
            txtOtherInjury = "";
        }
    }

    public void changeDestinationPatient() {
        if (loading == false) {
            changeForm();
        }
        if (currentDestinationPatient == 10) {//10. otro
            otherDestinationPatientDisabled = false;
            otherDestinationPatient = "";
        } else {
            otherDestinationPatientDisabled = true;
            otherDestinationPatient = "";
        }
    }

    public void changeOtherPlace() {
        if (loading == false) {
            changeForm();
        }
        if (checkOtherPlace) {
            otherAnatomicalPlaceDisabled = false;
        } else {
            otherAnatomicalPlaceDisabled = true;
            txtOtherPlace = "";
        }
    }

    public void changeAggressionOrSuspicion() {
        if (loading == false) {
            changeForm();
        }
        clearDomesticViolence();
        clearInterpersonalViolence();
        switch (currentAggressionOrSuspicion) {
            case 1://VIOLENCIA INTRAFAMILIAR
                displayInterpersonalViolence = "none";
                displayDomesticViolence = "block";
                break;
            case 2://VIOLENCIA INTERPERSONAL
                if (currentMechanisms == 2) {//VIOLENCIA SEXUAL
                    currentContext = 3;
                    currentContextDisabled = true;
                } else {
                    currentContext = 0;
                    currentContextDisabled = false;
                }
                displayInterpersonalViolence = "block";
                displayDomesticViolence = "none";
                break;
        }
    }

    public void changeSecurityElements() {
        if (loading == false) {
            changeForm();
        }
        isBeltUse = false;
        isHelmetUse = false;
        isBicycleHelmetUse = false;
        isVestUse = false;
        isOtherElementUse = false;
        if (currentSecurityElements.compareTo("SI") == 0) {
            displaySecurityElements = "block";
        } else {
            displaySecurityElements = "none";
        }
    }

    public void changeHeightWhich() {
        if (loading == false) {
            changeForm();
        }
        try {
            int heightWhichInt = Integer.parseInt(heightWhich);
            if (heightWhichInt < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor para la altura debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                heightWhich = "";
            }
        } catch (Exception e) {
            if (heightWhich.length() != 0) {
                heightWhich = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor para la altura debe ser un número, y mayor que cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
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

    public void changeMechanisms() {

        if (loading == false) {
            changeForm();
        }

        heightWhichDisabled = true;
        powderWhichDisabled = true;
        otherMechanismDisabled = true;
        disasterWhichDisabled = true;
        otherAnimalDisabled = true;
        forBurned = "none";
        heightWhich = "";
        powderWhich = "";
        otherMechanism = "";
        otherAnimal = "";
        disasterWhich = "";
        displayTransport = "none";
        currentContext = 0;
        currentContextDisabled = false;

        switch (currentMechanisms) {
            case 5://"Otra caida, altura"
                heightWhichDisabled = false;
                break;
            case 12://Pólvora, cual"
                powderWhichDisabled = false;
                forBurned = "block";
                break;
            case 26://Desastre
                disasterWhichDisabled = false;
                break;
            case 27://Otro, cual
                otherMechanismDisabled = false;
                break;
            case 1://lesion de transporte
                displayTransport = "block";
                break;
            case 2://violencia sexual
                if (currentAggressionOrSuspicion == 2) {//VIOLENCIA INTERPERSONAL
                    currentContext = 3;
                    currentContextDisabled = true;
                }
                break;
            case 24://animal cual
                otherAnimalDisabled = false;
                break;
            case 10://Fuego / llama
            case 11://objeto caliente
            case 21://explotar
            case 22://explosivo
            case 25://electricidad
                forBurned = "block";
                break;
        }
    }

    public void changeTransportCounterpart() {
        if (loading == false) {
            changeForm();
        }
        if (currentTransportCounterpart == 12) {//12. otro
            otherTransportCounterpartsTypeDisabled = false;
        } else {
            otherTransportCounterpartsTypeDisabled = true;
            otherTransportCounterpartsType = "";
        }
    }

    public void changeTransportType() {

        if (loading == false) {
            changeForm();
        }

        otherTransportTypeDisabled = true;
        otherTransportUserTypeDisabled = true;
        otherTransportType = "";
        transportUserDisabled = false;
        securityElementsDisabled = false;
        currentTransportUser = 0;
        currentSecurityElements = "NO";
        displaySecurityElements = "none";
        List<TransportUsers> transportUsersList;
        switch (currentTransportTypes) {
            case 1://peaton
                //otherTransportTypeDisabled = false;
                currentTransportUser = 1;
                transportUserDisabled = true;
                currentSecurityElements = "NO";
                securityElementsDisabled = true;
                //se carga la lista de tipos de transporte con peaton incluido //cargo los usuarios en una lesion de tránsito y trasporte
                transportUsersList = transportUsersFacade.findAll();
                transportUsers = new SelectItem[transportUsersList.size() + 1];
                transportUsers[0] = new SelectItem(0, "");
                for (int i = 0; i < transportUsersList.size(); i++) {
                    transportUsers[i + 1] = new SelectItem(transportUsersList.get(i).getTransportUserId(), transportUsersList.get(i).getTransportUserName());
                }
                break;
            case 10://otro
                otherTransportTypeDisabled = false;
                otherTransportUserTypeDisabled = false;
                currentTransportUser = 8;
                transportUserDisabled = false;
            default://para otro(10) y cualquira de los demas
                //se carga la lista de tipos de transporte sin peaton
                //cargo los usuarios en una lesion de tránsito y trasporte
                transportUsersList = transportUsersFacade.findAll();
                transportUsers = new SelectItem[transportUsersList.size()];
                transportUsers[0] = new SelectItem(0, "");
                int pos = 0;
                for (int i = 0; i < transportUsersList.size(); i++) {
                    if (transportUsersList.get(i).getTransportUserId() != 1) {
                        transportUsers[pos + 1] = new SelectItem(transportUsersList.get(i).getTransportUserId(), transportUsersList.get(i).getTransportUserName());
                        pos++;
                    }
                }
                break;
        }
    }

    public void changeTransportUser() {
        if (loading == false) {
            changeForm();
        }
        if (currentTransportUser == 8) {//8. otro
            otherTransportUserTypeDisabled = false;
        } else {
            otherTransportUserTypeDisabled = true;
            otherTransportUserType = "";
        }
    }

    public void changeEthnicGroups() {
        if (loading == false) {
            changeForm();
        }
        if (currentEthnicGroup == 3) {//3. otro
            ethnicGroupsDisabled = false;

        } else {
            ethnicGroupsDisabled = true;
            otherEthnicGroup = "";
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
        //System.out.println("----" + currentEthnicGroup + "----" + ethnicGroupsDisabled);

    }

    public void changeActivities() {
        if (loading == false) {
            changeForm();
        }
        if (currentActivities == 98) {//98. otra cual?
            otherActivityDisabled = false;
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

    private void clearTransit() {
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
    }

    private void clearSelfInflected() {
        previousAttempt = 0;
        mentalPastDisorder = 0;
        currentPrecipitatingFactor = 0;
        otherFactor = "";
        otherFactorDisabled = true;
    }

    private void clearDomesticViolence() {
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
        isUnknownMA = false;
        isMA8 = false;
        otherMADisabled = true;
        otherMA = "";
    }

    private void clearInterpersonalViolence() {
        aggressionPast = 0;
        currentRelationshipToVictim = 0;
        otherRelation = "";
        otherRelationDisabled = true;
        currentContext = 0;
        currentAggressorGenders = 0;
    }

    public void changeIntentionality() {
        if (loading == false) {
            changeForm();
        }

        currentOtherIntentionality = "";
        currentMechanisms = 0;
        displayInterpersonalViolence = "none";
        displayDomesticViolence = "none";
        displayIntentional = "none";
        displayTransport = "none";
        displayAggressionOrSuspicion = "none";
        displayDomesticViolence = "none";
        displayInterpersonalViolence = "none";

        heightWhichDisabled = true;
        powderWhichDisabled = true;
        otherMechanismDisabled = true;
        disasterWhichDisabled = true;
        forBurned = "none";
        heightWhich = "";
        powderWhich = "";
        otherMechanism = "";
        disasterWhich = "";
        displayTransport = "none";


        clearTransit();
        clearSelfInflected();
        clearDomesticViolence();
        clearInterpersonalViolence();
        //System.out.println("entra.....");
        List<Mechanisms> auxMechanismsList;
        //se recargan todos los mecanismos
        mechanisms = new SelectItem[mechanismsList.size() + 1];
        mechanisms[0] = new SelectItem(0, "");
        for (int i = 0; i < mechanismsList.size(); i++) {
            mechanisms[i + 1] = new SelectItem(mechanismsList.get(i).getMechanismId(), mechanismsList.get(i).getMechanismName());
        }
        //-------------------------------
        switch (currentIntentionality) {
            case 2: //02. Autoinflingida intencional (suicidio)		
                //System.out.println("2");
                displayIntentional = "block";
                //recargo los mecanismos
                auxMechanismsList = new ArrayList<Mechanisms>();
                for (int i = 0; i < mechanismsList.size(); i++) {
                    if (mechanismsList.get(i).getMechanismId() != 2 //no listar Agresion sexual
                            && mechanismsList.get(i).getMechanismId() != 1 //no listar lesion de transito
                            && mechanismsList.get(i).getMechanismId() != 3 //no listar caída propia altura
                            && mechanismsList.get(i).getMechanismId() != 16 //no listar lesión por cuerpo extraño    
                            && mechanismsList.get(i).getMechanismId() != 21 //no listar minas/munición sin explotar
                            && mechanismsList.get(i).getMechanismId() != 22 //no listar otro artefacto explosivo
                            && mechanismsList.get(i).getMechanismId() != 23 //no listar mordedura de persona
                            && mechanismsList.get(i).getMechanismId() != 24 //no listar mordedura de animal
                            && mechanismsList.get(i).getMechanismId() != 26 //no listar desastre natural
                            ) {
                        auxMechanismsList.add(mechanismsList.get(i));
                    }
                }
                mechanisms = new SelectItem[auxMechanismsList.size() + 1];
                mechanisms[0] = new SelectItem(0, "");
                for (int i = 0; i < auxMechanismsList.size(); i++) {
                    mechanisms[i + 1] = new SelectItem(auxMechanismsList.get(i).getMechanismId(), auxMechanismsList.get(i).getMechanismName());
                }

                break;
            case 3: //03. Violencia / agresión o sospecha		
                //System.out.println("3");
                displayAggressionOrSuspicion = "block";
                switch (currentAggressionOrSuspicion) {
                    case 1:
                        displayDomesticViolence = "block";
                        break;
                    case 2:
                        displayInterpersonalViolence = "block";
                        break;
                }
                //recargo los mecanismos
                auxMechanismsList = new ArrayList<Mechanisms>();
                for (int i = 0; i < mechanismsList.size(); i++) {
                    if (mechanismsList.get(i).getMechanismId() != 1 //no listar lesion de transito
                            && mechanismsList.get(i).getMechanismId() != 24 //no listar mordedura de animal
                            && mechanismsList.get(i).getMechanismId() != 26 //no listar desastre natural
                            ) {
                        auxMechanismsList.add(mechanismsList.get(i));
                    }
                }
                mechanisms = new SelectItem[auxMechanismsList.size() + 1];
                mechanisms[0] = new SelectItem(0, "");
                for (int i = 0; i < auxMechanismsList.size(); i++) {
                    mechanisms[i + 1] = new SelectItem(auxMechanismsList.get(i).getMechanismId(), auxMechanismsList.get(i).getMechanismName());
                }

                break;
            case 1: //01. No intencional (accidentes)                                
                auxMechanismsList = new ArrayList<Mechanisms>();
                for (int i = 0; i < mechanismsList.size(); i++) {
                    if (mechanismsList.get(i).getMechanismId() != 2 //no listar Agresion sexual
                            && mechanismsList.get(i).getMechanismId() != 23) //no listar mordedura de persona
                    {
                        auxMechanismsList.add(mechanismsList.get(i));
                    }
                }
                mechanisms = new SelectItem[auxMechanismsList.size() + 1];
                mechanisms[0] = new SelectItem(0, "");
                for (int i = 0; i < auxMechanismsList.size(); i++) {
                    mechanisms[i + 1] = new SelectItem(auxMechanismsList.get(i).getMechanismId(), auxMechanismsList.get(i).getMechanismName());
                }
            case 0: //0. vacio                                                
                break;
        }
    }

    public void changeIdCIE10_1() {
        //if (loading == false) {             changeForm();         }
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
        //if(!loading)if (loading == false) {             changeForm();         }
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
        //if (loading == false) {             changeForm();         }
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
        //if (loading == false) {             changeForm();         }
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

    public short getAggressionPast() {
        return aggressionPast;
    }

    public void setAggressionPast(short aggressionPast) {
        this.aggressionPast = aggressionPast;
    }

//    public boolean isAggressorGendersDisabled() {
//        return aggressorGendersDisabled;
//    }
//    public void setAggressorGendersDisabled(boolean aggressorGendersDisabled) {
//        this.aggressorGendersDisabled = aggressorGendersDisabled;
//    }
//    public boolean isContextDisabled() {
//        return contextDisabled;
//    }
//
//    public void setContextDisabled(boolean contextDisabled) {
//        this.contextDisabled = contextDisabled;
//    }
    public boolean isOtherRelationDisabled() {
        return otherRelationDisabled;
    }

    public void setOtherRelationDisabled(boolean otherRelationDisabled) {
        this.otherRelationDisabled = otherRelationDisabled;
    }

//    public boolean isRelationshipToVictimDisabled() {
//        return relationshipToVictimDisabled;
//    }
//
//    public void setRelationshipToVictimDisabled(boolean relationshipToVictimDisabled) {
//        this.relationshipToVictimDisabled = relationshipToVictimDisabled;
//    }
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

    public boolean isCurrentContextDisabled() {
        return currentContextDisabled;
    }

    public void setCurrentContextDisabled(boolean currentContextDisabled) {
        this.currentContextDisabled = currentContextDisabled;
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

    public String getCurrentResponsible() {
        return currentResponsible;
    }

    public void setCurrentResponsible(String currentResponsible) {
        this.currentResponsible = currentResponsible;
    }

    public String getIdElements1() {
        return idElements1;
    }

    public void setIdElements1(String idElements1) {
        this.idElements1 = idElements1;
    }

    public String getIdElements2() {
        return idElements2;
    }

    public void setIdElements2(String idElements2) {
        this.idElements2 = idElements2;
    }

    public String getDisplayAggressionOrSuspicion() {
        return displayAggressionOrSuspicion;
    }

    public void setDisplayAggressionOrSuspicion(String displayAggressionOrSuspicion) {
        this.displayAggressionOrSuspicion = displayAggressionOrSuspicion;
    }

    public int getCurrentAggressionOrSuspicion() {
        return currentAggressionOrSuspicion;
    }

    public void setCurrentAggressionOrSuspicion(int currentAggressionOrSuspicion) {
        this.currentAggressionOrSuspicion = currentAggressionOrSuspicion;
    }

    public Short getCurrentFromWhere() {
        return currentFromWhere;
    }

    public void setCurrentFromWhere(Short currentFromWhere) {
        this.currentFromWhere = currentFromWhere;
    }

    public boolean isFromWhereDisabled() {
        return fromWhereDisabled;
    }

    public void setFromWhereDisabled(boolean fromWhereDisabled) {
        this.fromWhereDisabled = fromWhereDisabled;
    }

    public String getOtherAnimal() {
        return otherAnimal;
    }

    public void setOtherAnimal(String otherAnimal) {
        this.otherAnimal = otherAnimal;
    }

    public boolean isOtherAnimalDisabled() {
        return otherAnimalDisabled;
    }

    public void setOtherAnimalDisabled(boolean otherAnimalDisabled) {
        this.otherAnimalDisabled = otherAnimalDisabled;
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

    public boolean isOtherAnatomicalPlaceDisabled() {
        return otherAnatomicalPlaceDisabled;
    }

    public void setOtherAnatomicalPlaceDisabled(boolean otherAnatomicalPlaceDisabled) {
        this.otherAnatomicalPlaceDisabled = otherAnatomicalPlaceDisabled;
    }

    public boolean isSecurityElementsDisabled() {
        return securityElementsDisabled;
    }

    public void setSecurityElementsDisabled(boolean securityElementsDisabled) {
        this.securityElementsDisabled = securityElementsDisabled;
    }

    public boolean isTransportUserDisabled() {
        return transportUserDisabled;
    }

    public void setTransportUserDisabled(boolean transportUserDisabled) {
        this.transportUserDisabled = transportUserDisabled;
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

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
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

    public SelectItem[] getInsurances() {
        return insurances;
    }

    public void setInsurances(SelectItem[] insurances) {
        this.insurances = insurances;
    }

    public Short getCurrentInsurance() {
        return currentInsurance;
    }

    public void setCurrentInsurance(Short currentInsurance) {
        this.currentInsurance = currentInsurance;
    }

    public String getCurrentIdForm() {
        return currentIdForm;
    }

    public void setCurrentIdForm(String currentIdForm) {
        this.currentIdForm = currentIdForm;
    }
}
