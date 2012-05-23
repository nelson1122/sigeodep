/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.validation.constraints.Size;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "suicideMB")
@SessionScoped
public class SuicideMB {

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @EJB
    MurderContextsFacade murderContextsFacade;
    private String currentMurderContext;
    private SelectItem[] murderContexts;
    //-------------------- 
    @EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private String currentArea;
    //-------------------- 
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    private String currentWeaponType;
    private SelectItem[] weaponTypes;
    //-------------------- 
    @EJB
    AccidentMechanismsFacade accidentMechanismsFacade;
    private String currentAccidentMechanisms;
    private SelectItem[] accidentMechanisms;
    //-------------------- 
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
    private Short currentUseAlcohol;
    private Short currentUseDrugs;
    private SelectItem[] useAlcohol;
    private SelectItem[] useDrugs;
    //--------------------    
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private String currentIPS;
    private SelectItem[] IPSs;
    //--------------------
    @EJB
    IntentionalitiesFacade intentionalitiesFacade;
    private Short currentIntentionality;
    private SelectItem[] intentionalities;
    //--------------------
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace;
    private SelectItem[] places;
    //--------------------
    @EJB
    ActivitiesFacade activitiesFacade;
    private Short currentActivities;
    private SelectItem[] activities;
    //--------------------
    @EJB
    MechanismsFacade mechanismsFacade;
    private Short currentMechanisms;
    private SelectItem[] mechanisms;
    //--------------------
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    private Short currentEthnicGroup;
    private SelectItem[] ethnicGroups;
    private boolean ethnicGroupsDisabled = true;
    private String otherEthnicGroup;
    //--------------------
    @EJB
    TransportTypesFacade transportTypesFacade;
    private Short currentTransportTypes;
    private SelectItem[] transportTypes;
    //--------------------
    @EJB
    TransportCounterpartsFacade transportCounterpartsFacade;
    private Short currentTransportUser;
    private SelectItem[] transportCounterparts;
    //--------------------
    @EJB
    TransportUsersFacade transportUsersFacade;
    private Short currentTransportCounterpart;
    private SelectItem[] transportUsers;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender;
    private SelectItem[] genders;
    //--------------------
    @EJB
    RelationshipsToVictimFacade relationshipsToVictimFacade;
    private Short currentRelationshipToVictim;
    private SelectItem[] relationshipsToVictim;
    //--------------------
    @EJB
    ContextsFacade contextsFacade;
    private Short currentContext;
    private SelectItem[] contexts;
    //--------------------
    @EJB
    AggressorGendersFacade agreAggressorGendersFacade;
    private Short currentAggressorGenders;
    private SelectItem[] aggressorGenders;
    //--------------------
    @EJB
    PrecipitatingFactorsFacade precipitatingFactorsFacade;
    private Short currentPrecipitatingFactor;
    private SelectItem[] precipitatingFactors;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob;
    private SelectItem[] jobs;
    //--------------------
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    private Short currentDestinationPatient;
    private SelectItem[] destinationsPatient;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome;
    private int currentNeighborhoodHomeCode;
    private String currentNeighborhoodEvent;
    private int currentNeighborhoodEventCode;
    boolean neighborhoodHomeNameDisabled = true;
    //--------------------
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    private String currentHealthProfessionals;
    //--------------------
    @EJB
    DiagnosesFacade diagnosesFacade;
    private String currentDiagnoses;
    private SelectItem[] diagnoses;
    //--------------------
    private SelectItem[] healthInstitutions;
    private Short currentHealthInstitution;
    //------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identifications;
    private Short currentIdentification;
    //------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge;
    private String currentAge;
    private boolean valueAgeDisabled;
    //-------
    private boolean isSubmitted = false;
    private boolean IPSDisabled = true;
    private boolean isMentalHealth = false;
    private boolean isAttemptsSuicide = false;
    //-------
    private boolean otherIntentDisabled = true;
    private boolean otherPlaceDisabled = true;
    private boolean otherActivityDisabled = true;
    private boolean otherMechanismDisabled = true;//otro mecanismo    
    private String otherMechanism;//otro mecanismo       
    private boolean powderWhichDisabled = true;//cual polvora
    private String powderWhich;//cual polvora
    private boolean disasterWhichDisabled = true;//cual desastre
    private String disasterWhich;//cual desastre
    private boolean heightWhichDisabled = true;//cual altura
    private String heightWhich;//cual altura    
    private String forBurned = "none";//para los quemados
    private String displaySecurityElements = "block";
    private String displayInterpersonalViolence = "none";
    private String displayTransport = "block";
    private String displayIntentional = "none";
    private boolean otherTransportTypeDisabled = true;//otro tipo de transporte
    private String otherTransportType;//otro tipo de transporte    
    private boolean otherTransportCounterpartsTypeDisabled = true;//otro tipo de transporte contraparte
    private String otherTransportCounterpartsType;//otro tipo de transporte contraparte   
    private boolean otherTransportUserTypeDisabled = true;//otro tipo de transporte usuario
    private String otherTransportUserType;//otro tipo de transporte usuario   
    private boolean aggressionPast = false;
    private String otherFactor;
    private boolean otherFactorDisabled = true;
    private boolean relationshipToVictimDisabled = true;
    private boolean contextDisabled = true;
    private String otherRelation;
    private boolean otherRelationDisabled = true;
    private boolean aggressorGendersDisabled = true;
    private boolean checkOtherInjury;
    private boolean checkOtherPlace;
    private boolean otherInjuryDisabled = true;
    private boolean otherInjuryPlaceDisabled = true;
    private boolean otherDestinationPatientDisabled = true;
    private String otherDestinationPatient;
    private String txtOtherInjury;
    private String txtOtherPlace;
    private String txtCIE10_1;
    private String txtCIE10_2;
    private String txtCIE10_3;
    private String txtCIE10_4;
    private String idCIE10_1;
    private String idCIE10_2;
    private String idCIE10_3;
    private String idCIE10_4;
    private String currentNarrative;
    private String currentAlcoholLevel;
    private boolean currentAlcoholLevelDisabled = false;
    private boolean isNoDataAlcoholLevel = false;
    private boolean isPendentAlcoholLevel = false;
    private boolean isUnknownAlcoholLevel = false;
    private boolean isNegativeAlcoholLevel = false;
    private boolean isNoDataAlcoholLevelDisabled = false;
    private boolean isPendentAlcoholLevelDisabled = false;
    private boolean isUnknownAlcoholLevelDisabled = false;
    private boolean isNegativeAlcoholLevelDisabled = false;
    
    private String currentSecurityElements;
    private String currentMedicalHistory;//@Size(min = 6, max = 8) 
    private String currentDayEvent;
    private String currentMonthEvent;
    private String currentYearEvent;
    private String currentDateEvent;
    private String currentWeekdayEvent;
    private String currentHourEvent;
    private String currentMinuteEvent;
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent;
    private String currentDayConsult;
    private String currentMonthConsult;
    private String currentYearConsult;
    private String currentDateConsult;
    private String currentWeekdayConsult;
    private String currentHourConsult;
    private String currentMinuteConsult;
    private String currentAmPmConsult = "AM";
    private String currentMilitaryHourConsult;
    private String currentName;
    private String currentSurame;
    private String currentIdentificationNumber;
    private String currentInsurance;
    private String currentDirectionHome;
    private String currentTelephoneHome;
    private String currentDirectionEvent;
    private String currentOtherIntentionality;
    private String currentOtherPlace;
    private String currentOtherActivitie;
    private String currentSurname;
    private Short currentLevelBurned;
    private Short currentPercentBurned;
    private String currentNumberVictims;
    private String currentVictimSource;
    private boolean isDisplaced;
    private boolean isHandicapped;
    private boolean isBeltUse;
    private boolean isHelmetUse;
    private boolean isBicycleHelmetUse;
    private boolean isVestUse;
    private boolean isPreviousAttempt;
    private boolean isMentalDisorder;
    private boolean isUnknownNatureOfInjurye;
    private boolean isNatureOfInjurye1;
    private boolean isNatureOfInjurye2;
    private boolean isNatureOfInjurye3;
    private boolean isNatureOfInjurye4;
    private boolean isNatureOfInjurye5;
    private boolean isNatureOfInjurye6;
    private boolean isNatureOfInjurye7;
    private boolean isNatureOfInjurye8;
    private boolean isNatureOfInjurye9;
    private boolean isAnatomicalSite1;
    private boolean isAnatomicalSite2;
    private boolean isAnatomicalSite3;
    private boolean isAnatomicalSite4;
    private boolean isAnatomicalSite5;
    private boolean isAnatomicalSite6;
    private boolean isAnatomicalSite7;
    private boolean isAnatomicalSite8;
    private boolean isAnatomicalSite9;
    private boolean isAnatomicalSite10;
    private boolean isAnatomicalSite11;
    private boolean isAG1;
    private boolean isAG2;
    private boolean isAG3;
    private boolean isAG4;
    private boolean isAG5;
    private boolean isAG6;
    private boolean isAG7;
    private boolean isAG8;
    private boolean isUnknownAG;
    private boolean isMA1;
    private boolean isMA2;
    private boolean isMA3;
    private boolean isMA4;
    private boolean isMA5;
    private boolean isMA6;
    private boolean isUnknownMA;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private int CIE_selected = 1;
    //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "entro", "entro");
    //FacesContext.getCurrentInstance().addMessage(null, msg);

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public SuicideMB() {
    }
    
    
    public void reset() {
        try {
            //cargo las instituciones de salud e IPS
            List<NonFatalDataSources> sourcesList = nonFatalDataSourcesFacade.findAll();
            IPSs = new SelectItem[sourcesList.size()];
            healthInstitutions = new SelectItem[sourcesList.size()];
            for (int i = 0; i < sourcesList.size(); i++) {
                IPSs[i] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceName());
                healthInstitutions[i] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceId(), sourcesList.get(i).getNonFatalDataSourceName());
            }
            //cargo los tipos de identificacion
            List<IdTypes> idTypesList = idTypesFacade.findAll();
            identifications = new SelectItem[idTypesList.size()];
            for (int i = 0; i < idTypesList.size(); i++) {
                identifications[i] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
            }


            //cargo las medidas de edad
            List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
            measuresOfAge = new SelectItem[ageTypesList.size()];
            for (int i = 0; i < ageTypesList.size(); i++) {
                measuresOfAge[i] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
            }

            //cargo los destinos del paciente
            List<DestinationsOfPatient> destinationsList = destinationsOfPatientFacade.findAll();
            destinationsPatient = new SelectItem[destinationsList.size()];
            for (int i = 0; i < destinationsList.size(); i++) {
                destinationsPatient[i] = new SelectItem(destinationsList.get(i).getDestinationPatientId(), destinationsList.get(i).getDestinationPatientName());
            }

            //cargo los departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            departaments = new SelectItem[departamentsList.size()];
            for (int i = 0; i < departamentsList.size(); i++) {
                departaments[i] = new SelectItem(departamentsList.get(i).getDepartamentId(), departamentsList.get(i).getDepartamentName());
            }
            currentDepartamentHome = 52;
            //cargo los municipios
            findMunicipalities();


            //cargo las intencionalidades
            List<Intentionalities> intentionalitiesList = intentionalitiesFacade.findAll();
            intentionalities = new SelectItem[intentionalitiesList.size()];
            for (int i = 0; i < intentionalitiesList.size(); i++) {
                intentionalities[i] = new SelectItem(intentionalitiesList.get(i).getIntentionalityId(), intentionalitiesList.get(i).getIntentionalityName());
            }

            //cargo los lugares donde ocurrieron los hechos
            List<Places> placesList = placesFacade.findAll();
            places = new SelectItem[placesList.size()];
            for (int i = 0; i < placesList.size(); i++) {
                places[i] = new SelectItem(placesList.get(i).getPlaceId(), placesList.get(i).getPlaceName());
            }

            //cargo las Actividades realizadas cuando ocurrio la lesión
            List<Activities> activitiesList = activitiesFacade.findAll();
            activities = new SelectItem[activitiesList.size()];
            for (int i = 0; i < activitiesList.size(); i++) {
                activities[i] = new SelectItem(activitiesList.get(i).getActivityId(), activitiesList.get(i).getActivityName());
            }

            //cargo los mecanismos de lesión
            List<Mechanisms> mechanismsList = mechanismsFacade.findAll();
            mechanisms = new SelectItem[mechanismsList.size()];
            for (int i = 0; i < mechanismsList.size(); i++) {
                mechanisms[i] = new SelectItem(mechanismsList.get(i).getMechanismId(), mechanismsList.get(i).getMechanismName());
            }

            //cargo los tipos de transporte en lesiones de tránsito
            List<TransportTypes> transportTypesList = transportTypesFacade.findAll();
            transportTypes = new SelectItem[transportTypesList.size()];
            for (int i = 0; i < transportTypesList.size(); i++) {
                transportTypes[i] = new SelectItem(transportTypesList.get(i).getTransportTypeId(), transportTypesList.get(i).getTransportTypeName());
            }

            //cargo los Tipos de transporte de la contraparte en lesiones de tránsito y transporte.
            List<TransportCounterparts> transportCounterpartsList = transportCounterpartsFacade.findAll();
            transportCounterparts = new SelectItem[transportCounterpartsList.size()];
            for (int i = 0; i < transportCounterpartsList.size(); i++) {
                transportCounterparts[i] = new SelectItem(transportCounterpartsList.get(i).getTransportCounterpartId(), transportCounterpartsList.get(i).getTransportCounterpartName());
            }

            //cargo los usuarios en una lesion de tránsito y trasporte
            List<TransportUsers> transportUsersList = transportUsersFacade.findAll();
            transportUsers = new SelectItem[transportUsersList.size()];
            for (int i = 0; i < transportUsersList.size(); i++) {
                transportUsers[i] = new SelectItem(transportUsersList.get(i).getTransportUserId(), transportUsersList.get(i).getTransportUserName());
            }

            //cargo las relaciones entre agresos y victima
            List<RelationshipsToVictim> relationshipsToVictimList = relationshipsToVictimFacade.findAll();
            relationshipsToVictim = new SelectItem[relationshipsToVictimList.size()];
            for (int i = 0; i < relationshipsToVictimList.size(); i++) {
                relationshipsToVictim[i] = new SelectItem(relationshipsToVictimList.get(i).getRelationshipVictimId(), relationshipsToVictimList.get(i).getRelationshipVictimName());
            }

            //cargo los contextos en que ocurrió una lesión
            List<Contexts> contextsList = contextsFacade.findAll();
            contexts = new SelectItem[contextsList.size()];
            for (int i = 0; i < contextsList.size(); i++) {
                contexts[i] = new SelectItem(contextsList.get(i).getContextId(), contextsList.get(i).getContextName());
            }

            //cargo el genero de el/los agresor/es
            List<AggressorGenders> aggressorGendersList = agreAggressorGendersFacade.findAll();
            aggressorGenders = new SelectItem[aggressorGendersList.size()];
            for (int i = 0; i < aggressorGendersList.size(); i++) {
                aggressorGenders[i] = new SelectItem(aggressorGendersList.get(i).getGenderId(), aggressorGendersList.get(i).getGenderName());
            }

            //cargo los Factores precipitantes en lesiones autoinflingidas.
            List<PrecipitatingFactors> precipitatingFactorsList = precipitatingFactorsFacade.findAll();
            precipitatingFactors = new SelectItem[precipitatingFactorsList.size()];
            for (int i = 0; i < precipitatingFactorsList.size(); i++) {
                precipitatingFactors[i] = new SelectItem(precipitatingFactorsList.get(i).getPrecipitatingFactorId(), precipitatingFactorsList.get(i).getPrecipitatingFactorName());
            }

            //grupos etnicos
            List<EthnicGroups> ethnicGroupsList = ethnicGroupsFacade.findAll();
            ethnicGroups = new SelectItem[ethnicGroupsList.size()];
            for (int i = 0; i < ethnicGroupsList.size(); i++) {
                ethnicGroups[i] = new SelectItem(ethnicGroupsList.get(i).getEthnicGroupId(), ethnicGroupsList.get(i).getEthnicGroupName());
            }

            //generos
            List<Genders> gendersList = gendersFacade.findAll();
            genders = new SelectItem[gendersList.size()];
            for (int i = 0; i < gendersList.size(); i++) {
                genders[i] = new SelectItem(gendersList.get(i).getGenderId(), gendersList.get(i).getGenderName());
            }

            //trabajos
            List<Jobs> jobsList = jobsFacade.findAll();
            jobs = new SelectItem[jobsList.size()];
            for (int i = 0; i < jobsList.size(); i++) {
                jobs[i] = new SelectItem(jobsList.get(i).getJobId(), jobsList.get(i).getJobName());
            }

            //Uso de drogas y alcohol
            List<UseAlcoholDrugs> useAlcoholDrugsList = useAlcoholDrugsFacade.findAll();
            useAlcohol = new SelectItem[useAlcoholDrugsList.size()];
            useDrugs = new SelectItem[useAlcoholDrugsList.size()];
            for (int i = 0; i < useAlcoholDrugsList.size(); i++) {
                useAlcohol[i] = new SelectItem(useAlcoholDrugsList.get(i).getUseAlcoholDrugsId(), useAlcoholDrugsList.get(i).getUseAlcoholDrugsName());
                useDrugs[i] = new SelectItem(useAlcoholDrugsList.get(i).getUseAlcoholDrugsId(), useAlcoholDrugsList.get(i).getUseAlcoholDrugsName());
            }

            //listado de diagnosticos
            List<Diagnoses> diagnosesList = diagnosesFacade.findAll();
            diagnoses = new SelectItem[diagnosesList.size()];
            for (int i = 0; i < diagnosesList.size(); i++) {
                diagnoses[i] = new SelectItem(diagnosesList.get(i).getDiagnosisId() + " - " + diagnosesList.get(i).getDiagnosisName());
            }
            currentDiagnoses = "S000";

            //cargo las areas del hecho
            List<Areas> areasList = areasFacade.findAll();
            areas = new SelectItem[areasList.size()];
            for (int i = 0; i < areasList.size(); i++) {
                areas[i] = new SelectItem(areasList.get(i).getAreaName());
            }

            //cargo los tipos de armas
            List<WeaponTypes> weaponTypesList = weaponTypesFacade.findAll();
            weaponTypes = new SelectItem[weaponTypesList.size()];
            for (int i = 0; i < weaponTypesList.size(); i++) {
                weaponTypes[i] = new SelectItem(weaponTypesList.get(i).getWeaponTypeName());
            }
            
            //contexto de la muerte
            List<MurderContexts> murderContextsList = murderContextsFacade.findAll();
            murderContexts = new SelectItem[murderContextsList.size()];
            for (int i = 0; i < murderContextsList.size(); i++) {
                murderContexts[i] = new SelectItem(murderContextsList.get(i).getMurderContextName());
            }

            //Arma o causante de muerte
            List<AccidentMechanisms> accidentMechanismsList = accidentMechanismsFacade.findAll();
            accidentMechanisms = new SelectItem[accidentMechanismsList.size()];
            for (int i = 0; i < accidentMechanismsList.size(); i++) {
                accidentMechanisms[i] = new SelectItem(accidentMechanismsList.get(i).getAccidentMechanismId(), accidentMechanismsList.get(i).getAccidentMechanismName());
            }

        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
        }
    }

    public void loadDiagnose() {
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

    }

    public void setCIE_1() {
        //fincion para saber que se dio clik sobre la casilla txt de diagnostico 1
        CIE_selected = 1;
    }

    public void setCIE_2() {
        //fincion para saber que se dio clik sobre la casilla txt de diagnostico 2
        CIE_selected = 2;
    }

    public void setCIE_3() {
        //fincion para saber que se dio clik sobre la casilla txt de diagnostico 3
        CIE_selected = 3;
    }

    public void setCIE_4() {
        //fincion para saber que se dio clik sobre la casilla txt de diagnostico 4
        CIE_selected = 4;
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
    // FUNCIONES CUANDO LISTAS CAMBIAN DE VALOR ----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void findMunicipalities() {
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
            currentNeighborhoodHomeCode = 0;
        }
    }

    public void findMunicipalitieCode() {
        //Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
        if (currentMunicipalitie == 1 && currentDepartamentHome == 52) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = 0;
        }
    }

    public void changeOtherFactor() {
        if (currentPrecipitatingFactor == 98) {//98. otro
            otherFactorDisabled = false;
        } else {
            otherFactorDisabled = true;
            otherFactor = "";
        }
    }

    public void changeAlcoholLevel() {
        if (!isNoDataAlcoholLevel && !isPendentAlcoholLevel
                && !isUnknownAlcoholLevel && !isNegativeAlcoholLevel) {
            currentAlcoholLevelDisabled = false;
            currentAlcoholLevel = "";
            isNoDataAlcoholLevelDisabled = false;
            isPendentAlcoholLevelDisabled = false;
            isUnknownAlcoholLevelDisabled = false;
            isNegativeAlcoholLevelDisabled = false;
        } else {
            if (isNoDataAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = false;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isPendentAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = false;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isUnknownAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = false;
                isNegativeAlcoholLevelDisabled = true;
            }
            if (isNegativeAlcoholLevel) {
                currentAlcoholLevelDisabled = true;
                currentAlcoholLevel = "";
                isNoDataAlcoholLevelDisabled = true;
                isPendentAlcoholLevelDisabled = true;
                isUnknownAlcoholLevelDisabled = true;
                isNegativeAlcoholLevelDisabled = false;
            }
        }
    }
    

    public void changeRelationshipToVictim() {
        if (currentRelationshipToVictim == 3) {//3. otro
            otherRelationDisabled = false;
        } else {
            otherRelationDisabled = true;
            otherRelation = "";
        }
    }

    public void changeAggressionPast() {
        if (aggressionPast) {
            relationshipToVictimDisabled = false;
            contextDisabled = false;
            aggressorGendersDisabled = false;
        } else {
            relationshipToVictimDisabled = true;
            contextDisabled = true;
            aggressorGendersDisabled = true;
        }
        if (currentRelationshipToVictim == 3) {//3. otro
            otherRelationDisabled = false;
        } else {
            otherRelationDisabled = true;
            otherRelation = "";
        }
    }

    public void changeOtherInjury() {
        if (checkOtherInjury) {
            otherInjuryDisabled = false;

        } else {
            otherInjuryDisabled = true;
            txtOtherInjury = "";
        }
    }

    public void changeDestinationPatient() {
        if (currentDestinationPatient == 10) {//10. otro
            otherDestinationPatientDisabled = false;
            otherDestinationPatient = "";
        } else {
            otherDestinationPatientDisabled = true;
            otherDestinationPatient = "";
        }
    }

    public void changeOtherPlace() {
        if (checkOtherPlace) {
            otherPlaceDisabled = false;
        } else {
            otherPlaceDisabled = true;
            txtOtherPlace = "";
        }
    }

    public void changeSecurityElements() {
        if (currentSecurityElements.compareTo("SI") == 0) {
            displaySecurityElements = "block";
        } else {
            displaySecurityElements = "none";
        }
    }

    public void changeNeighborhoodHomeName() {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = neighborhoodsList.get(i).getNeighborhoodId();
                break;
            }
        }
    }

    public void changeSubmitted() {
        if (isSubmitted) {
            IPSDisabled = false;
        } else {
            IPSDisabled = true;
        }
    }

    public void changeNeighborhoodEvent() {
        Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
        if (n != null) {
            currentNeighborhoodEventCode = n.getNeighborhoodId();
        } else {
            currentNeighborhoodEventCode = 0;
        }
    }

    public void changeMechanisms() {
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

        if (currentTransportCounterpart == 12) {//12. otro
            otherTransportCounterpartsTypeDisabled = false;
            otherTransportCounterpartsType = "";
        } else {
            otherTransportCounterpartsTypeDisabled = true;
        }
    }

    public void changeTransportType() {
        if (currentTransportTypes == 8) {//10. otro
            otherTransportTypeDisabled = false;
            otherTransportType = "";
        } else {
            otherTransportTypeDisabled = true;
        }
    }

    public void changeTransportUser() {
        if (currentTransportUser == 8) {//8. otro
            otherTransportUserTypeDisabled = false;
            otherTransportUserType = "";
        } else {
            otherTransportUserTypeDisabled = true;
        }
    }

    public void changeEthnicGroups() {
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
        if (currentActivities == 98) {//98. otra cual?
            otherActivityDisabled = false;
        } else {
            otherActivityDisabled = true;
        }
    }

    public void changePlace() {
        if (currentPlace == 8) {//8. otro
            otherPlaceDisabled = false;
        } else {
            otherPlaceDisabled = true;

        }
    }

    public void changeIntentionality() {
        if (currentIntentionality == 8) {//otro 8
            otherIntentDisabled = false;
            displayInterpersonalViolence = "none";
            displayIntentional = "none";
        } else {
            otherIntentDisabled = true;
        }

        if (currentIntentionality == 1 || currentIntentionality == 9) {
            //no intencional 1 //no se sabe 9
            displayInterpersonalViolence = "none";
            displayIntentional = "none";
        }
        if (currentIntentionality == 2) {//Autoinflingida 2
            displayInterpersonalViolence = "none";
            displayIntentional = "block";
        }
        if (currentIntentionality == 3) {//Violencia / agresión o sospecha 3
            displayInterpersonalViolence = "block";
            displayIntentional = "none";
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES DE CALCULO DE FECHA Y HORA MILITAR ------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private void calculateDate1() {
        try {
            fechaI = formato.parse(currentDayEvent + "/" + currentMonthEvent + "/" + currentYearEvent);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);
            currentDateEvent = formato.format(fechaI);

            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                currentWeekdayEvent = "Lunes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                currentWeekdayEvent = "Martes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                currentWeekdayEvent = "Miércoles";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                currentWeekdayEvent = "Jueves";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                currentWeekdayEvent = "Viernes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                currentWeekdayEvent = "Sábado";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                currentWeekdayEvent = "Domingo";
            }



        } catch (ParseException ex) {
            // POR FAVOR CORRIJA LA FECHA DEL PRIMER PAGO
            currentDateEvent = "";
            currentWeekdayEvent = "";
        }
    }

    private void calculateDate2() {
        try {
            fechaI = formato.parse(currentDayConsult + "/" + currentMonthConsult + "/" + currentYearConsult);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);
            currentDateConsult = formato.format(fechaI);
            //currentWeekdayConsult = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                currentWeekdayConsult = "Lunes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
                currentWeekdayConsult = "Martes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                currentWeekdayConsult = "Miércoles";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
                currentWeekdayConsult = "Jueves";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                currentWeekdayConsult = "Viernes";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                currentWeekdayConsult = "Sábado";
            } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                currentWeekdayConsult = "Domingo";
            }

        } catch (ParseException ex) {
            // POR FAVOR CORRIJA LA FECHA DEL PRIMER PAGO
            currentDateConsult = "";
            currentWeekdayConsult = "";
        }
    }

    private void calculateTime1() {
        int hourInt;
        int minuteInt;
        int timeInt;
        try {
            hourInt = Integer.parseInt(currentHourEvent);
        } catch (Exception ex) {
            hourInt = 0;
        }
        try {
            minuteInt = Integer.parseInt(currentMinuteEvent);
        } catch (Exception ex) {
            minuteInt = 0;
        }
        try {
            if (currentAmPmEvent.length() != 0) {
                String hourStr;
                String minuteStr;
                String timeStr;
                boolean continuar = true;
                if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                    if (currentAmPmEvent.compareTo("PM") == 0) {//hora PM
                        if (hourInt == 12) {//no existe hora 12

                            currentMilitaryHourEvent = "Error";
                            continuar = false;
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
                    currentMilitaryHourEvent = "Error";
                }
            }
        } catch (Exception ex) {

            currentMilitaryHourEvent = "Error  " + ex.toString();
        }
    }

    private void calculateTime2() {
        int hourInt;
        int minuteInt;
        int timeInt;
        try {
            hourInt = Integer.parseInt(currentHourConsult);
        } catch (Exception ex) {
            hourInt = 0;
        }
        try {
            minuteInt = Integer.parseInt(currentMinuteConsult);
        } catch (Exception ex) {
            minuteInt = 0;
        }
        try {
            if (currentAmPmConsult.length() != 0) {
                String hourStr;
                String minuteStr;
                String timeStr;
                boolean continuar = true;
                if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                    if (currentAmPmConsult.compareTo("PM") == 0) {//hora PM
                        if (hourInt == 12) {//no existe hora 12

                            currentMilitaryHourConsult = "Error";
                            continuar = false;
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
                    currentMilitaryHourConsult = "Error";
                }
            }
        } catch (Exception ex) {

            currentMilitaryHourConsult = "Error  " + ex.toString();
        }
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

    public String getCurrentMedicalHistory() {
        return currentMedicalHistory;
    }

    public void setCurrentMedicalHistory(String currentMedicalHistory) {
        this.currentMedicalHistory = currentMedicalHistory;
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
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_1);
        if (selectDiagnoses != null) {
            txtCIE10_1 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_1 = "";
        }
    }

    public String getIdCIE10_2() {
        return idCIE10_2;
    }

    public void setIdCIE10_2(String idCIE10_2) {
        this.idCIE10_2 = idCIE10_2;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_2);
        if (selectDiagnoses != null) {
            txtCIE10_2 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_2 = "";
        }
    }

    public String getIdCIE10_3() {
        return idCIE10_3;
    }

    public void setIdCIE10_3(String idCIE10_3) {
        this.idCIE10_3 = idCIE10_3;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_3);
        if (selectDiagnoses != null) {
            txtCIE10_3 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_3 = "";
        }
    }

    public String getIdCIE10_4() {
        return idCIE10_4;
    }

    public void setIdCIE10_4(String idCIE10_4) {
        this.idCIE10_4 = idCIE10_4;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_4);
        if (selectDiagnoses != null) {
            txtCIE10_4 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_4 = "";
        }
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

    public String getCurrentIPS() {
        return currentIPS;
    }

    public void setCurrentIPS(String currentIPS) {
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

    public int getCurrentNeighborhoodEventCode() {
        return currentNeighborhoodEventCode;
    }

    public void setCurrentNeighborhoodEventCode(int currentNeighborhoodEventCode) {
        this.currentNeighborhoodEventCode = currentNeighborhoodEventCode;
    }

    public String getCurrentNeighborhoodHome() {
        return currentNeighborhoodHome;
    }

    public void setCurrentNeighborhoodHome(String currentNeighborhoodHome) {
        this.currentNeighborhoodHome = currentNeighborhoodHome;
    }

    public int getCurrentNeighborhoodHomeCode() {
        return currentNeighborhoodHomeCode;
    }

    public void setCurrentNeighborhoodHomeCode(int currentNeighborhoodHomeCode) {
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

    public String getCurrentInsurance() {
        return currentInsurance;
    }

    public void setCurrentInsurance(String currentInsurance) {
        this.currentInsurance = currentInsurance;
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

    public Short getCurrentPercentBurned() {
        return currentPercentBurned;
    }

    public void setCurrentPercentBurned(Short currentPercentBurned) {
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

    public boolean isIsMentalDisorder() {
        return isMentalDisorder;
    }

    public void setIsMentalDisorder(boolean isMentalDisorder) {
        this.isMentalDisorder = isMentalDisorder;
    }

    public boolean isIsPreviousAttempt() {
        return isPreviousAttempt;
    }

    public void setIsPreviousAttempt(boolean isPreviousAttempt) {
        this.isPreviousAttempt = isPreviousAttempt;
    }

    public boolean isIsVestUse() {
        return isVestUse;
    }

    public void setIsVestUse(boolean isVestUse) {
        this.isVestUse = isVestUse;
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

    public SelectItem[] getAreas() {
        return areas;
    }

    public void setAreas(SelectItem[] areas) {
        this.areas = areas;
    }

    public String getCurrentArea() {
        return currentArea;
    }

    public void setCurrentArea(String currentArea) {
        this.currentArea = currentArea;
    }

    public String getCurrentWeaponType() {
        return currentWeaponType;
    }

    public void setCurrentWeaponType(String currentWeaponType) {
        this.currentWeaponType = currentWeaponType;
    }

    public SelectItem[] getWeaponTypes() {
        return weaponTypes;
    }

    public void setWeaponTypes(SelectItem[] weaponTypes) {
        this.weaponTypes = weaponTypes;
    }

    public String getCurrentNumberVictims() {
        return currentNumberVictims;
    }

    public void setCurrentNumberVictims(String currentNumberVictims) {
        this.currentNumberVictims = currentNumberVictims;
    }

    public String getCurrentVictimSource() {
        return currentVictimSource;
    }

    public void setCurrentVictimSource(String currentVictimSource) {
        this.currentVictimSource = currentVictimSource;
    }

    public SelectItem[] getAccidentMechanisms() {
        return accidentMechanisms;
    }

    public void setAccidentMechanisms(SelectItem[] accidentMechanisms) {
        this.accidentMechanisms = accidentMechanisms;
    }

    public String getCurrentAccidentMechanisms() {
        return currentAccidentMechanisms;
    }

    public void setCurrentAccidentMechanisms(String currentAccidentMechanisms) {
        this.currentAccidentMechanisms = currentAccidentMechanisms;
    }

    public String getCurrentNarrative() {
        return currentNarrative;
    }

    public void setCurrentNarrative(String currentNarrative) {
        this.currentNarrative = currentNarrative;
    }

    public String getCurrentAlcoholLevel() {
        return currentAlcoholLevel;
    }

    public void setCurrentAlcoholLevel(String currentAlcoholLevel) {
        this.currentAlcoholLevel = currentAlcoholLevel;
    }

    public boolean isIsNegativeAlcoholLevel() {
        return isNegativeAlcoholLevel;
    }

    public void setIsNegativeAlcoholLevel(boolean isNegativeAlcoholLevel) {
        this.isNegativeAlcoholLevel = isNegativeAlcoholLevel;
    }

    public boolean isIsNoDataAlcoholLevel() {
        return isNoDataAlcoholLevel;
    }

    public void setIsNoDataAlcoholLevel(boolean isNoDataAlcoholLevel) {
        this.isNoDataAlcoholLevel = isNoDataAlcoholLevel;
    }

    public boolean isIsPendentAlcoholLevel() {
        return isPendentAlcoholLevel;
    }

    public void setIsPendentAlcoholLevel(boolean isPendentAlcoholLevel) {
        this.isPendentAlcoholLevel = isPendentAlcoholLevel;
    }

    public boolean isIsUnknownAlcoholLevel() {
        return isUnknownAlcoholLevel;
    }

    public void setIsUnknownAlcoholLevel(boolean isUnknownAlcoholLevel) {
        this.isUnknownAlcoholLevel = isUnknownAlcoholLevel;
    }

    public boolean isIsNegativeAlcoholLevelDisabled() {
        return isNegativeAlcoholLevelDisabled;
    }

    public void setIsNegativeAlcoholLevelDisabled(boolean isNegativeAlcoholLevelDisabled) {
        this.isNegativeAlcoholLevelDisabled = isNegativeAlcoholLevelDisabled;
    }

    public boolean isIsNoDataAlcoholLevelDisabled() {
        return isNoDataAlcoholLevelDisabled;
    }

    public void setIsNoDataAlcoholLevelDisabled(boolean isNoDataAlcoholLevelDisabled) {
        this.isNoDataAlcoholLevelDisabled = isNoDataAlcoholLevelDisabled;
    }

    public boolean isIsPendentAlcoholLevelDisabled() {
        return isPendentAlcoholLevelDisabled;
    }

    public void setIsPendentAlcoholLevelDisabled(boolean isPendentAlcoholLevelDisabled) {
        this.isPendentAlcoholLevelDisabled = isPendentAlcoholLevelDisabled;
    }

    public boolean isIsUnknownAlcoholLevelDisabled() {
        return isUnknownAlcoholLevelDisabled;
    }

    public void setIsUnknownAlcoholLevelDisabled(boolean isUnknownAlcoholLevelDisabled) {
        this.isUnknownAlcoholLevelDisabled = isUnknownAlcoholLevelDisabled;
    }

    public boolean isCurrentAlcoholLevelDisabled() {
        return currentAlcoholLevelDisabled;
    }

    public void setCurrentAlcoholLevelDisabled(boolean currentAlcoholLevelDisabled) {
        this.currentAlcoholLevelDisabled = currentAlcoholLevelDisabled;
    }
    
     public String getCurrentMurderContext() {
        return currentMurderContext;
    }

    public void setCurrentMurderContext(String currentMurderContext) {
        this.currentMurderContext = currentMurderContext;
    }

    public SelectItem[] getMurderContexts() {
        return murderContexts;
    }

    public void setMurderContexts(SelectItem[] murderContexts) {
        this.murderContexts = murderContexts;
    }

    public boolean isIsAttemptsSuicide() {
        return isAttemptsSuicide;
    }

    public void setIsAttemptsSuicide(boolean isAttemptsSuicide) {
        this.isAttemptsSuicide = isAttemptsSuicide;
    }

    public boolean isIsMentalHealth() {
        return isMentalHealth;
    }

    public void setIsMentalHealth(boolean isMentalHealth) {
        this.isMentalHealth = isMentalHealth;
    }

    
    
    
}
