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
@ManagedBean(name = "transitMB")
@SessionScoped
public class TransitMB {

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    @EJB
    DepartamentsFacade departamentsFacade;
    //-------------------- 
    @EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private Short currentArea = 0;
    //-------------------- 
    @EJB
    InvolvedVehiclesFacade involvedVehiclesFacade;
    private SelectItem[] involvedVehicles;
    private Short currentVictimVehicle = 0;
    private Short currentCounterpartVehicle1 = 0;
    private Short currentCounterpartVehicle2 = 0;
    private Short currentCounterpartVehicle3 = 0;
    //-------------------- 
    @EJB
    ServiceTypesFacade serviceTypesFacade;
    private SelectItem[] serviceTypes;
    private String currentServiceTypes = "";
    private Short currentVictimServiceType = 0;
    private Short currentCounterpartServiceType1 = 0;
    private Short currentCounterpartServiceType2 = 0;
    private Short currentCounterpartServiceType3 = 0;
    //--------------------
    @EJB
    ProtectiveMeasuresFacade protectiveMeasuresFacade;
    private Short currentProtectiveMeasures = 0;
    private SelectItem[] protectiveMeasures;
    //--------------------
    @EJB
    VictimCharacteristicsFacade victimCharacteristicsFacade;
    private Short currentVictimCharacteristics = 0;
    private SelectItem[] victimCharacteristics;
    //-------------------- 
    @EJB
    AccidentClassesFacade accidentClassesFacade;
    private SelectItem[] accidentClasses;
    private Short currentAccidentClasses = 0;
    //-------------------- 
    @EJB
    RoadTypesFacade roadTypesFacade;
    private SelectItem[] roadTypes;
    private Short currentRoadType = 0;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    //-------------------- 
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace = 0;
    private SelectItem[] places;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender = 0;
    private SelectItem[] genders;
    //--------------------
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob = 0;
    private SelectItem[] jobs;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    //--------------------
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    //------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identificationsTypes;
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
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    //------------------
    private String currentNumberInjured = "";
    private String currentAlcoholLevelC = "";
    private boolean currentAlcoholLevelDisabledC = false;
    private boolean isNoDataAlcoholLevelC = false;
    private boolean isPendentAlcoholLevelC = false;
    private boolean isUnknownAlcoholLevelC = false;
    private boolean isNegativeAlcoholLevelC = false;
    private boolean isNoDataAlcoholLevelDisabledC = false;
    private boolean isPendentAlcoholLevelDisabledC = false;
    private boolean isUnknownAlcoholLevelDisabledC = false;
    private boolean isNegativeAlcoholLevelDisabledC = false;
    private String currentPlaceOfResidence = "";
    private String currentNarrative = "";
    private String currentAlcoholLevel = "";
    private boolean currentAlcoholLevelDisabled = false;
    private boolean isNoDataAlcoholLevel = false;
    private boolean isPendentAlcoholLevel = false;
    private boolean isUnknownAlcoholLevel = false;
    private boolean isNegativeAlcoholLevel = false;
    private boolean isNoDataAlcoholLevelDisabled = false;
    private boolean isPendentAlcoholLevelDisabled = false;
    private boolean isUnknownAlcoholLevelDisabled = false;
    private boolean isNegativeAlcoholLevelDisabled = false;
    private String currentDayEvent = "";
    private String currentMonthEvent = "";
    private String currentYearEvent = "";
    private String currentDateEvent = "";
    private String currentWeekdayEvent = "";
    private String currentHourEvent = "";
    private String currentMinuteEvent;
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent = "";
    private String currentName = "";
    private String currentIdentificationNumber;
    private String currentDirectionEvent = "";
    private String currentSurname = "";
    private String currentNumberVictims = "";
    private String currentVictimSource = "";
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private String currentCode = "";
    private boolean save = true;//variable que me dice si el registro esta guadado o no    
    private int currentFatalInjuriId = -1;//registro actual 
    private FatalInjuryTraffic currentFatalInjuryTraffic;
    private FatalInjuryTraffic auxFatalInjuryTraffic;
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
    //----------------------
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public TransitMB() {
    }

    public void reset() {

	currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
	try {
	    determinePosition();
	    //cargo los tipos de identificacion
	    List<IdTypes> idTypesList = idTypesFacade.findAll();
	    identificationsTypes = new SelectItem[idTypesList.size() + 1];
	    identificationsTypes[0] = new SelectItem(0, "");
	    for (int i = 0; i < idTypesList.size(); i++) {
		identificationsTypes[i + 1] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
	    }
	    //medidas de seguridad
	    List<ProtectiveMeasures> protectiveMeasuresList = protectiveMeasuresFacade.findAll();
	    protectiveMeasures = new SelectItem[protectiveMeasuresList.size() + 1];
	    protectiveMeasures[0] = new SelectItem(0, "");
	    for (int i = 0; i < protectiveMeasuresList.size(); i++) {
		protectiveMeasures[i + 1] = new SelectItem(protectiveMeasuresList.get(i).getProtectiveMeasuresId(), protectiveMeasuresList.get(i).getProtectiveMeasuresName());
	    }
	    //cargo las medidas de edad
	    List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
	    measuresOfAge = new SelectItem[ageTypesList.size() + 1];
	    measuresOfAge[0] = new SelectItem(0, "");
	    for (int i = 0; i < ageTypesList.size(); i++) {
		measuresOfAge[i + 1] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
	    }
	    //cargo los lugares donde ocurrieron los hechos
	    List<Places> placesList = placesFacade.findAll();
	    places = new SelectItem[placesList.size() + 1];
	    places[0] = new SelectItem(0, "");
	    for (int i = 0; i < placesList.size(); i++) {
		places[i + 1] = new SelectItem(placesList.get(i).getPlaceId(), placesList.get(i).getPlaceName());
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
	    //cargo las areas del hecho
	    List<Areas> areasList = areasFacade.findAll();
	    areas = new SelectItem[areasList.size() + 1];
	    areas[0] = new SelectItem(0, "");
	    for (int i = 0; i < areasList.size(); i++) {
		areas[i + 1] = new SelectItem(areasList.get(i).getAreaId(), areasList.get(i).getAreaName());
	    }
	    //tipos de vias
	    List<RoadTypes> roadTypesList = roadTypesFacade.findAll();
	    roadTypes = new SelectItem[roadTypesList.size() + 1];
	    roadTypes[0] = new SelectItem(0, "");
	    for (int i = 0; i < roadTypesList.size(); i++) {
		roadTypes[i + 1] = new SelectItem(roadTypesList.get(i).getRoadTypeId(), roadTypesList.get(i).getRoadTypeName());
	    }
	    //Clases de accidentes
	    List<AccidentClasses> accidentClassesList = accidentClassesFacade.findAll();
	    accidentClasses = new SelectItem[accidentClassesList.size() + 1];
	    accidentClasses[0] = new SelectItem(0, "");
	    for (int i = 0; i < accidentClassesList.size(); i++) {
		accidentClasses[i + 1] = new SelectItem(accidentClassesList.get(i).getAccidentClassId(), accidentClassesList.get(i).getAccidentClassName());
	    }
	    //caracteristicas de la victima
	    List<VictimCharacteristics> victimCharacteristicsList = victimCharacteristicsFacade.findAll();
	    victimCharacteristics = new SelectItem[victimCharacteristicsList.size() + 1];
	    victimCharacteristics[0] = new SelectItem(0, "");
	    for (int i = 0; i < victimCharacteristicsList.size(); i++) {
		victimCharacteristics[i + 1] = new SelectItem(victimCharacteristicsList.get(i).getCharacteristicId(), victimCharacteristicsList.get(i).getCharacteristicName());
	    }
	    //Tipos de servicio
	    List<ServiceTypes> serviceTypesList = serviceTypesFacade.findAll();
	    serviceTypes = new SelectItem[serviceTypesList.size() + 1];
	    serviceTypes[0] = new SelectItem(0, "");
	    for (int i = 0; i < serviceTypesList.size(); i++) {
		serviceTypes[i + 1] = new SelectItem(serviceTypesList.get(i).getServiceTypeId(), serviceTypesList.get(i).getServiceTypeName());
	    }
	    //Vehiculos involucrados en el accidente
	    List<InvolvedVehicles> involvedVehiclesList = involvedVehiclesFacade.findAll();
	    involvedVehicles = new SelectItem[involvedVehiclesList.size() + 1];
	    involvedVehicles[0] = new SelectItem(0, "");
	    for (int i = 0; i < involvedVehiclesList.size(); i++) {
		involvedVehicles[i + 1] = new SelectItem(involvedVehiclesList.get(i).getInvolvedVehicleId(), involvedVehiclesList.get(i).getInvolvedVehicleName());
	    }

	    //cargo los municipios
	    findMunicipalities();

	} catch (Exception e) {
	    System.out.println("*******************************************ERROR: " + e.toString());
	}
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
	    currentIdentification = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getTypeId().getTypeId();
	} catch (Exception e) {
	    currentIdentification = 0;
	}
	//******victim_nid
	currentIdentificationNumber = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getVictimNid();
	if (currentIdentificationNumber == null) {
	    currentIdentificationNumber = "";
	}
	//******victim_firstname
	currentName = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getVictimFirstname();
	if (currentName == null) {
	    currentName = "";
	}
	//******victim_lastname
	currentSurname = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getVictimLastname();
	if (currentSurname == null) {
	    currentSurname = "";
	}
	//******age_type_id
	try {
	    currentMeasureOfAge = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getAgeTypeId();
	} catch (Exception e) {
	    currentMeasureOfAge = 0;
	}
	//******victim_age
	try {
	    currentAge = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getVictimAge().toString();
	} catch (Exception e) {
	    currentAge = "";
	}
	//******gender_id
	try {
	    currentGender = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getGenderId().getGenderId();
	} catch (Exception e) {
	    currentGender = 0;
	}
	//******job_id
	try {
	    currentJob = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getJobId().getJobId();
	} catch (Exception e) {
	    currentJob = 0;
	}
	//******vulnerable_group_id
	//******ethnic_group_id
	//******victim_telephone
	//******victim_address
	//******victim_neighborhood_id	
	//******victim_date_of_birth
	//******eps_id
	//******victim_class
	//******victim_id
	//******residence_municipality
	try {
	    currentMunicipalitie = currentFatalInjuryTraffic.getFatalInjuries().getVictimId().getResidenceMunicipality();
	} catch (Exception e) {
	    currentMunicipalitie = 1;
	}
	//------------------------------------------------------------
	//SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
	//------------------------------------------------------------
	//******injury_id
	//******injury_date
	try {
	    currentDateEvent = currentFatalInjuryTraffic.getFatalInjuries().getInjuryDate().toString();
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(currentFatalInjuryTraffic.getFatalInjuries().getInjuryDate());
	    currentDayEvent = String.valueOf(cal.get(Calendar.DATE));
	    currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH) + 1);
	    currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
	    calculateDate1();
	} catch (Exception e) {
	    currentDateEvent = "";
	}
	//******injury_time
	try {
	    currentHourEvent = String.valueOf(currentFatalInjuryTraffic.getFatalInjuries().getInjuryTime().getHours());
	    currentMinuteEvent = String.valueOf(currentFatalInjuryTraffic.getFatalInjuries().getInjuryTime().getMinutes());
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
	currentDirectionEvent = currentFatalInjuryTraffic.getFatalInjuries().getInjuryAddress();
	if (currentDirectionEvent == null) {
	    currentDirectionEvent = "";
	}
	//******injury_neighborhood_id
	try {
	    if (currentFatalInjuryTraffic.getFatalInjuries().getInjuryNeighborhoodId() != null) {
		currentNeighborhoodEventCode = String.valueOf(currentFatalInjuryTraffic.getFatalInjuries().getInjuryNeighborhoodId());
		currentNeighborhoodEvent = neighborhoodsFacade.find(currentFatalInjuryTraffic.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName();
	    }
	} catch (Exception e) {
	    currentNeighborhoodEventCode = "";
	    currentNeighborhoodEvent = "";
	}
	//******injury_place_id
	try {
	    currentPlace = currentFatalInjuryTraffic.getFatalInjuries().getInjuryPlaceId().getPlaceId();
	} catch (Exception e) {
	    currentPlace = 0;
	}
	//******victim_number
	if (currentFatalInjuryTraffic.getFatalInjuries().getVictimNumber() != null) {
	    currentNumberVictims = String.valueOf(currentFatalInjuryTraffic.getFatalInjuries().getVictimNumber());
	} else {
	    currentNumberVictims = "";
	}
	//******injury_description
	currentNarrative = currentFatalInjuryTraffic.getFatalInjuries().getInjuryDescription();
	if (currentNarrative == null) {
	    currentNarrative = "";
	}
	//******user_id	
	//******input_timestamp	
	//******injury_day_of_week
	currentWeekdayEvent = currentFatalInjuryTraffic.getFatalInjuries().getInjuryDayOfWeek();
	if (currentWeekdayEvent == null) {
	    currentWeekdayEvent = "";
	}
	//******victim_id
	//******fatal_injury_id
	//******alcohol_level_victim_id, alcohol_level_victim
	if (currentFatalInjuryTraffic.getFatalInjuries().getAlcoholLevelVictim() != null) {
	    isNoDataAlcoholLevelDisabled = false;
	    isUnknownAlcoholLevelDisabled = false;
	    isPendentAlcoholLevelDisabled = false;
	    isNegativeAlcoholLevelDisabled = false;
	    currentAlcoholLevelDisabled = false;
	    currentAlcoholLevel = String.valueOf(currentFatalInjuryTraffic.getFatalInjuries().getAlcoholLevelVictim());
	    isNoDataAlcoholLevel = false;
	    isUnknownAlcoholLevel = false;
	    isPendentAlcoholLevel = false;
	    isNegativeAlcoholLevel = false;
	} else {
	    try {
		Short level = currentFatalInjuryTraffic.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelId();
		if (level == 2) {//isNoDataAlcoholLevel
		    isNoDataAlcoholLevelDisabled = false;
		    isUnknownAlcoholLevelDisabled = true;
		    isPendentAlcoholLevelDisabled = true;
		    isNegativeAlcoholLevelDisabled = true;
		    currentAlcoholLevelDisabled = true;
		    currentAlcoholLevel = "";
		    isNoDataAlcoholLevel = true;
		    isUnknownAlcoholLevel = false;
		    isPendentAlcoholLevel = false;
		    isNegativeAlcoholLevel = false;
		}
		if (level == 3) {//isUnknownAlcoholLevel
		    isNoDataAlcoholLevelDisabled = true;
		    isUnknownAlcoholLevelDisabled = false;
		    isPendentAlcoholLevelDisabled = true;
		    isNegativeAlcoholLevelDisabled = true;
		    currentAlcoholLevelDisabled = true;
		    currentAlcoholLevel = "";
		    isNoDataAlcoholLevel = false;
		    isUnknownAlcoholLevel = true;
		    isPendentAlcoholLevel = false;
		    isNegativeAlcoholLevel = false;
		}
		if (level == 4) {//isPendentAlcoholLevel                
		    isNoDataAlcoholLevelDisabled = true;
		    isUnknownAlcoholLevelDisabled = true;
		    isPendentAlcoholLevelDisabled = false;
		    isNegativeAlcoholLevelDisabled = true;
		    currentAlcoholLevelDisabled = true;
		    currentAlcoholLevel = "";
		    isNoDataAlcoholLevel = false;
		    isUnknownAlcoholLevel = false;
		    isPendentAlcoholLevel = true;
		    isNegativeAlcoholLevel = false;
		}
		if (level == 5) {//isNegativeAlcoholLevel

		    isNoDataAlcoholLevelDisabled = true;
		    isUnknownAlcoholLevelDisabled = true;
		    isPendentAlcoholLevelDisabled = true;
		    isNegativeAlcoholLevelDisabled = false;
		    currentAlcoholLevelDisabled = true;
		    currentAlcoholLevel = "";
		    isNoDataAlcoholLevel = false;
		    isUnknownAlcoholLevel = false;
		    isPendentAlcoholLevel = false;
		    isNegativeAlcoholLevel = true;
		}
	    } catch (Exception e) {
		isNoDataAlcoholLevelDisabled = false;
		isUnknownAlcoholLevelDisabled = false;
		isPendentAlcoholLevelDisabled = false;
		isNegativeAlcoholLevelDisabled = false;
		currentAlcoholLevelDisabled = false;
		currentAlcoholLevel = "";
		isNoDataAlcoholLevel = false;
		isUnknownAlcoholLevel = false;
		isPendentAlcoholLevel = false;
		isNegativeAlcoholLevel = false;
	    }
	}
	//******code
	currentCode = currentFatalInjuryTraffic.getFatalInjuries().getCode();
	if (currentCode == null) {
	    currentWeekdayEvent = "";
	}
	//******area_id
	try {
	    currentArea = currentFatalInjuryTraffic.getFatalInjuries().getAreaId().getAreaId();
	} catch (Exception e) {
	    currentArea = 0;
	}
	//------------------------------------------------------------
	//SE CARGA DATOS PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
	//------------------------------------------------------------
	//cargar vehiculos de la contraparte
	List<InvolvedVehicles> involvedVehiclesList = currentFatalInjuryTraffic.getFatalInjuries().getInvolvedVehiclesList();
	if (involvedVehiclesList != null) {
	    for (int i = 0; i < involvedVehiclesList.size(); i++) {
		if (i == 0) {
		    currentCounterpartVehicle1 = involvedVehiclesList.get(0).getInvolvedVehicleId();
		}
		if (i == 1) {
		    currentCounterpartVehicle2 = involvedVehiclesList.get(1).getInvolvedVehicleId();
		}
		if (i == 2) {
		    currentCounterpartVehicle3 = involvedVehiclesList.get(2).getInvolvedVehicleId();
		}
	    }
	}

	//cargar tipo de servcio de la contraparte
	List<ServiceTypes> serviceTypesList = currentFatalInjuryTraffic.getFatalInjuries().getServiceTypesList();

	if (involvedVehiclesList != null) {
	    for (int i = 0; i < serviceTypesList.size(); i++) {
		if (i == 0) {
		    currentCounterpartServiceType1 = serviceTypesList.get(0).getServiceTypeId();
		}
		if (i == 1) {
		    currentCounterpartServiceType2 = serviceTypesList.get(1).getServiceTypeId();
		}
		if (i == 2) {
		    currentCounterpartServiceType3 = serviceTypesList.get(2).getServiceTypeId();
		}
	    }
	}


	//******number_non_fatal_victims
	if (currentFatalInjuryTraffic.getNumberNonFatalVictims() != null) {
	    currentNumberInjured = String.valueOf(currentFatalInjuryTraffic.getNumberNonFatalVictims());
	}
	//******victim_characteristic_id
	if (currentFatalInjuryTraffic.getVictimCharacteristicId() != null) {
	    currentVictimCharacteristics = currentFatalInjuryTraffic.getVictimCharacteristicId().getCharacteristicId();
	}
	//******protection_measure_id
	if (currentFatalInjuryTraffic.getProtectionMeasureId() != null) {
	    currentProtectiveMeasures = currentFatalInjuryTraffic.getProtectionMeasureId().getProtectiveMeasuresId();
	}
	//******road_type_id
	if (currentFatalInjuryTraffic.getRoadTypeId() != null) {
	    currentRoadType = currentFatalInjuryTraffic.getRoadTypeId().getRoadTypeId();
	}
	//******accident_class_id
	if (currentFatalInjuryTraffic.getAccidentClassId() != null) {
	    currentAccidentClasses = currentFatalInjuryTraffic.getAccidentClassId().getAccidentClassId();
	}
	//******involved_vehicle_id
	if (currentFatalInjuryTraffic.getInvolvedVehicleId() != null) {
	    currentVictimVehicle = currentFatalInjuryTraffic.getInvolvedVehicleId().getInvolvedVehicleId();
	}
	//******service_type_id
	if (currentFatalInjuryTraffic.getServiceTypeId() != null) {
	    currentVictimServiceType = currentFatalInjuryTraffic.getServiceTypeId().getServiceTypeId();
	}
	//******alcohol_level_counterpart_id, alcohol_level_counterpart
	if (currentFatalInjuryTraffic.getAlcoholLevelCounterpart() != null) {
	    isNoDataAlcoholLevelDisabledC = false;
	    isUnknownAlcoholLevelDisabledC = false;
	    isPendentAlcoholLevelDisabledC = false;
	    isNegativeAlcoholLevelDisabledC = false;
	    currentAlcoholLevelDisabledC = false;
	    currentAlcoholLevelC = String.valueOf(currentFatalInjuryTraffic.getAlcoholLevelCounterpart());
	    isNoDataAlcoholLevelC = false;
	    isUnknownAlcoholLevelC = false;
	    isPendentAlcoholLevelC = false;
	    isNegativeAlcoholLevelC = false;
	} else {
	    try {
		Short level = currentFatalInjuryTraffic.getAlcoholLevelCounterpartId().getAlcoholLevelId();
		if (level == 2) {//isNoDataAlcoholLevel
		    isNoDataAlcoholLevelDisabledC = false;
		    isUnknownAlcoholLevelDisabledC = true;
		    isPendentAlcoholLevelDisabledC = true;
		    isNegativeAlcoholLevelDisabledC = true;
		    currentAlcoholLevelDisabledC = true;
		    currentAlcoholLevelC = "";
		    isNoDataAlcoholLevelC = true;
		    isUnknownAlcoholLevelC = false;
		    isPendentAlcoholLevelC = false;
		    isNegativeAlcoholLevelC = false;
		}
		if (level == 3) {//isUnknownAlcoholLevel
		    isNoDataAlcoholLevelDisabledC = true;
		    isUnknownAlcoholLevelDisabledC = false;
		    isPendentAlcoholLevelDisabledC = true;
		    isNegativeAlcoholLevelDisabledC = true;
		    currentAlcoholLevelDisabledC = true;
		    currentAlcoholLevelC = "";
		    isNoDataAlcoholLevelC = false;
		    isUnknownAlcoholLevelC = true;
		    isPendentAlcoholLevelC = false;
		    isNegativeAlcoholLevelC = false;
		}
		if (level == 4) {//isPendentAlcoholLevel                
		    isNoDataAlcoholLevelDisabledC = true;
		    isUnknownAlcoholLevelDisabledC = true;
		    isPendentAlcoholLevelDisabledC = false;
		    isNegativeAlcoholLevelDisabledC = true;
		    currentAlcoholLevelDisabledC = true;
		    currentAlcoholLevelC = "";
		    isNoDataAlcoholLevelC = false;
		    isUnknownAlcoholLevelC = false;
		    isPendentAlcoholLevelC = true;
		    isNegativeAlcoholLevelC = false;
		}
		if (level == 5) {//isNegativeAlcoholLevel

		    isNoDataAlcoholLevelDisabledC = true;
		    isUnknownAlcoholLevelDisabledC = true;
		    isPendentAlcoholLevelDisabledC = true;
		    isNegativeAlcoholLevelDisabledC = false;
		    currentAlcoholLevelDisabledC = true;
		    currentAlcoholLevelC = "";
		    isNoDataAlcoholLevelC = false;
		    isUnknownAlcoholLevelC = false;
		    isPendentAlcoholLevelC = false;
		    isNegativeAlcoholLevelC = true;
		}
	    } catch (Exception e) {
		isNoDataAlcoholLevelDisabledC = false;
		isUnknownAlcoholLevelDisabledC = false;
		isPendentAlcoholLevelDisabledC = false;
		isNegativeAlcoholLevelDisabledC = false;
		currentAlcoholLevelDisabledC = false;
		currentAlcoholLevelC = "";
		isNoDataAlcoholLevelC = false;
		isUnknownAlcoholLevelC = false;
		isPendentAlcoholLevelC = false;
		isNegativeAlcoholLevelC = false;
	    }
	}
	//******fatal_injury_id
    }

    private boolean saveRegistry() {
	validationsErrors = new ArrayList<String>();
	try {
	    //------------------------------------------------------------
	    //SE CREA VARIABLE PARA LA NUEVA VICTIMA
	    //------------------------------------------------------------


	    Victims newVictim = new Victims();
	    //******type_id
	    if (currentIdentification != 0) {
		newVictim.setTypeId(idTypesFacade.find(currentIdentification));
	    }
	    //******victim_nid
	    if (currentIdentificationNumber.trim().length() != 0) {
		newVictim.setVictimNid(currentIdentificationNumber);
	    }
	    //******victim_firstname
	    if (currentName.trim().length() != 0) {
		newVictim.setVictimFirstname(currentName);
	    }
	    //******victim_lastname
	    if (currentSurname.trim().length() != 0) {
		newVictim.setVictimLastname(currentSurname);
	    }
	    //******age_type_id
	    if (currentMeasureOfAge != 0) {
		newVictim.setAgeTypeId(currentMeasureOfAge);
	    }
	    //******victim_age
	    if (currentAge.trim().length() != 0) {
		try {
		    newVictim.setVictimAge(Short.parseShort(currentAge));
		} catch (Exception e) {
		    validationsErrors.add("Corregir valor de: Edad Cantidad");
		}
	    }
	    //******gender_id
	    if (currentGender != 0) {
		newVictim.setGenderId(gendersFacade.find(currentGender));
	    }

	    //******job_id
	    if (currentJob != 0) {
		newVictim.setJobId(jobsFacade.find(currentJob));
	    }
	    //******vulnerable_group_id
	    //******ethnic_group_id
	    //******victim_telephone
	    //******victim_address
	    //******victim_neighborhood_id	
	    //******victim_date_of_birth
	    //******eps_id
	    //******victim_class
	    //******victim_id
	    newVictim.setVictimId(victimsFacade.findMax() + 1);
	    //******residence_municipality
	    newVictim.setResidenceMunicipality(currentMunicipalitie);

	    //------------------------------------------------------------
	    //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA FATAL
	    //------------------------------------------------------------
	    FatalInjuries newFatalInjurie = new FatalInjuries();
	    //******injury_id
	    newFatalInjurie.setInjuryId(injuriesFacade.find((short) 11));//es 11 por ser accidente de transito
	    //******injury_date
	    if (currentDateEvent.trim().length() != 0) {
		newFatalInjurie.setInjuryDate(formato.parse(currentDateEvent));
	    }
	    //******injury_time
	    if (currentMilitaryHourEvent.trim().length() != 0) {
		try {
		    if (currentAmPmEvent.compareTo("PM") == 0) {
			currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) + 12);
		    }
		    int hourInt = Integer.parseInt(currentHourEvent);
		    int minuteInt = Integer.parseInt(currentMinuteEvent);
		    if (hourInt > 12 && hourInt < 0) {
			validationsErrors.add("Corregir la hora del hecho");
		    } else {
			if (minuteInt > 59 && minuteInt < 0) {
			    validationsErrors.add("Corregir la hora del hecho");
			} else {
			    newFatalInjurie.setInjuryTime(new Time(hourInt, minuteInt, 0));
			}
		    }

		} catch (Exception e) {
		    validationsErrors.add("Corregir la hora del hecho");
		}
	    }
	    //******injury_address
	    if (currentDirectionEvent.trim().length() != 0) {
		newFatalInjurie.setInjuryAddress(currentDirectionEvent);
	    }
	    //******injury_neighborhood_id
	    if (currentNeighborhoodEventCode.trim().length() != 0) {
		newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(currentNeighborhoodEventCode));
	    }
	    //******injury_place_id
	    if (currentPlace != 0) {
		newFatalInjurie.setInjuryPlaceId(placesFacade.find(currentPlace));
	    }
	    //******victim_number
	    if (currentNumberVictims.trim().length() != 0) {
		try {
		    newFatalInjurie.setVictimNumber(Short.parseShort(currentNumberVictims));
		} catch (Exception e) {
		    validationsErrors.add("Corregir el numero de victimas");
		}
	    }
	    //******injury_description
	    if (currentNarrative.trim().length() != 0) {
		newFatalInjurie.setInjuryDescription(currentNarrative);
	    }
	    //******user_id	
	    try {
		newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
	    } catch (Exception e) {
		System.out.println("*******************************************ERROR_A1: " + e.toString());
	    }
	    //******input_timestamp	
	    newFatalInjurie.setInputTimestamp(new Date());//momento en que se capturo el registro
	    //******injury_day_of_week
	    if (currentWeekdayEvent.trim().length() != 0) {
		newFatalInjurie.setInjuryDayOfWeek(currentWeekdayEvent);
	    }
	    //******victim_id
	    newFatalInjurie.setVictimId(newVictim);
	    //******fatal_injury_id
	    newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
	    //******alcohol_level_victim_id, alcohol_level_victim
	    if (currentAlcoholLevel.trim().length() != 0) {
		try {
		    newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(currentAlcoholLevel));
		    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 1));//con dato
		} catch (Exception e) {
		    validationsErrors.add("Corregir el nivel de alcohol de la victima");
		}
	    } else {
		if (isNoDataAlcoholLevel) {
		    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));//sin dato
		}
		if (isUnknownAlcoholLevel) {
		    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));//no suministrado
		}
		if (isPendentAlcoholLevel) {
		    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));//pendiente
		}
		if (isNegativeAlcoholLevel) {
		    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));//negativo
		}
	    }
	    //******code
	    if (currentCode.trim().length() != 0) {
		newFatalInjurie.setCode(currentCode);
	    }
	    //******area_id
	    if (currentArea != 0) {
		newFatalInjurie.setAreaId(areasFacade.find(currentArea));
	    }
	    //------------------------------------------------------------
	    //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
	    //------------------------------------------------------------

	    //almacenamiento de vehiculos de la contraparte
	    List<InvolvedVehicles> involvedVehiclesList = new ArrayList<InvolvedVehicles>();
	    if (currentCounterpartVehicle1 != 0) {
		involvedVehiclesList.add(new InvolvedVehicles(currentCounterpartVehicle1));
	    }
	    if (currentCounterpartVehicle2 != 0) {
		involvedVehiclesList.add(new InvolvedVehicles(currentCounterpartVehicle2));
	    }
	    if (currentCounterpartVehicle3 != 0) {
		involvedVehiclesList.add(new InvolvedVehicles(currentCounterpartVehicle3));
	    }
	    newFatalInjurie.setInvolvedVehiclesList(involvedVehiclesList);
	    //almacenamiento de servcio de la contraparte
	    List<ServiceTypes> serviceTypesList = new ArrayList<ServiceTypes>();
	    if (currentCounterpartServiceType1 != 0) {
		serviceTypesList.add(new ServiceTypes(currentCounterpartServiceType1));
	    }
	    if (currentCounterpartServiceType2 != 0) {
		serviceTypesList.add(new ServiceTypes(currentCounterpartServiceType2));
	    }
	    if (currentCounterpartServiceType3 != 0) {
		serviceTypesList.add(new ServiceTypes(currentCounterpartServiceType3));
	    }
	    newFatalInjurie.setServiceTypesList(serviceTypesList);

	    //------------------------------------------------------------
	    //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
	    //------------------------------------------------------------
	    FatalInjuryTraffic newFatalInjuryTraffic = new FatalInjuryTraffic();
	    //******number_non_fatal_victims
	    if (currentNumberVictims.length() != 0) {
		newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(currentNumberVictims));
	    }
	    //******victim_characteristic_id
	    if (currentVictimCharacteristics != 0) {
		newFatalInjuryTraffic.setVictimCharacteristicId(victimCharacteristicsFacade.find(currentVictimCharacteristics));
	    }
	    //******protection_measure_id
	    if (currentProtectiveMeasures != 0) {
		newFatalInjuryTraffic.setProtectionMeasureId(protectiveMeasuresFacade.find(currentProtectiveMeasures));
	    }
	    //******road_type_id
	    if (currentRoadType != 0) {
		newFatalInjuryTraffic.setRoadTypeId(roadTypesFacade.find(currentRoadType));
	    }
	    //******accident_class_id
	    if (currentAccidentClasses != 0) {
		newFatalInjuryTraffic.setAccidentClassId(accidentClassesFacade.find(currentAccidentClasses));
	    }
	    //******involved_vehicle_id
	    if (currentVictimVehicle != 0) {
		newFatalInjuryTraffic.setInvolvedVehicleId(involvedVehiclesFacade.find(currentVictimVehicle));
	    }
	    //******service_type_id
	    if (currentVictimServiceType != 0) {
		newFatalInjuryTraffic.setServiceTypeId(serviceTypesFacade.find(currentVictimServiceType));
	    }
	    //******alcohol_level_counterpart_id, alcohol_level_counterpart
	    if (currentAlcoholLevelC.trim().length() != 0) {
		try {
		    newFatalInjuryTraffic.setAlcoholLevelCounterpart(Short.parseShort(currentAlcoholLevelC));
		    newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find((short) 1));//con dato
		} catch (Exception e) {
		    validationsErrors.add("Corregir el nivel de alcohol de la contraparte");
		}
	    } else {
		if (isNoDataAlcoholLevelC) {
		    newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find((short) 2));//sin dato
		}
		if (isUnknownAlcoholLevelC) {
		    newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find((short) 3));//no suministrado
		}
		if (isPendentAlcoholLevelC) {
		    newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find((short) 4));//pendiente
		}
		if (isNegativeAlcoholLevelC) {
		    newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find((short) 5));//negativo
		}
	    }
	    //******fatal_injury_id
	    newFatalInjuryTraffic.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());

	    //-------------------------------------------------------------------------------
	    if (validationsErrors.isEmpty()) {
		openDialogFirst = "";
		openDialogNext = "";
		openDialogLast = "";
		openDialogPrevious = "";
		openDialogNew = "";
		openDialogDelete = "";
		if (currentFatalInjuriId == -1) {//ES UN NUEVO REGISTRO SE DEBE PERSISTIR
		    System.out.println("guardando nuevo registro");
		    victimsFacade.create(newVictim);
		    fatalInjuriesFacade.create(newFatalInjurie);
		    fatalInjuryTrafficFacade.create(newFatalInjuryTraffic);
		    save = true;
		    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "NUEVO REGISTRO ALMACENADO");
		    FacesContext.getCurrentInstance().addMessage(null, msg);
		} else {//ES UN REGISTRO EXISTENTE SE DEBE ACTUALIZAR
		    System.out.println("actualizando registro existente");
		    updateRegistry(newVictim, newFatalInjurie, newFatalInjuryTraffic);
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
	    System.out.println("*******************************************ERROR: " + e.toString());
	    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
	    FacesContext.getCurrentInstance().addMessage(null, msg);
	    return false;
	}
    }

    private void updateRegistry(Victims victim, FatalInjuries fatalInjurie, FatalInjuryTraffic fatalInjuryTraffic) {

	try {
	    //------------------------------------------------------------
	    //DATOS VICTIMA
	    //------------------------------------------------------------
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setTypeId(victim.getTypeId());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimNid(victim.getVictimNid());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimFirstname(victim.getVictimFirstname());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimLastname(victim.getVictimLastname());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setAgeTypeId(victim.getAgeTypeId());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimAge(victim.getVictimAge());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setGenderId(victim.getGenderId());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setJobId(victim.getJobId());
	    //newVictim.setVulnerableGroupId(v);
	    //newVictim.setEthnicGroupId(et);
	    //newVictim.setVictimTelephone();
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimAddress(victim.getVictimAddress());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setVictimNeighborhoodId(victim.getVictimNeighborhoodId());
	    currentFatalInjuryTraffic.getFatalInjuries().getVictimId().setResidenceMunicipality(victim.getResidenceMunicipality());
	    //newVictim.setEpsId(null);
	    //newVictim.setVictimClass();//si victima es nn

	    //------------------------------------------------------------
	    //DATOS LESION DE CAUSA EXTERNA FATAL
	    //------------------------------------------------------------
	    //FatalInjuries newFatalInjurie = new FatalInjuries();
	    //newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
	    //newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio

	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryDate(fatalInjurie.getInjuryDate());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryTime(fatalInjurie.getInjuryTime());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryAddress(fatalInjurie.getInjuryAddress());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryNeighborhoodId(fatalInjurie.getInjuryNeighborhoodId());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryPlaceId(fatalInjurie.getInjuryPlaceId());
	    currentFatalInjuryTraffic.getFatalInjuries().setVictimNumber(fatalInjurie.getVictimNumber());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryDescription(fatalInjurie.getInjuryDescription());
	    //currentFatalInjuryTraffic.getFatalInjuries().setUserId(fatalInjurie.getUserId());
	    //currentFatalInjuryTraffic.getFatalInjuries().setInputTimestamp(fatalInjurie.getInputTimestamp());
	    currentFatalInjuryTraffic.getFatalInjuries().setInjuryDayOfWeek(fatalInjurie.getInjuryDayOfWeek());
	    currentFatalInjuryTraffic.getFatalInjuries().setAlcoholLevelVictim(fatalInjurie.getAlcoholLevelVictim());
	    currentFatalInjuryTraffic.getFatalInjuries().setAlcoholLevelVictimId(fatalInjurie.getAlcoholLevelVictimId());
	    currentFatalInjuryTraffic.getFatalInjuries().setCode(fatalInjurie.getCode());
	    currentFatalInjuryTraffic.getFatalInjuries().setInvolvedVehiclesList(fatalInjurie.getInvolvedVehiclesList());
	    currentFatalInjuryTraffic.getFatalInjuries().setServiceTypesList(fatalInjurie.getServiceTypesList());
	    //------------------------------------------------------------
	    //DATOS LESION FATAL POR TRANSITO
	    //------------------------------------------------------------
	    currentFatalInjuryTraffic.setNumberNonFatalVictims(fatalInjuryTraffic.getNumberNonFatalVictims());
	    currentFatalInjuryTraffic.setVictimCharacteristicId(fatalInjuryTraffic.getVictimCharacteristicId());
	    currentFatalInjuryTraffic.setProtectionMeasureId(fatalInjuryTraffic.getProtectionMeasureId());
	    currentFatalInjuryTraffic.setRoadTypeId(fatalInjuryTraffic.getRoadTypeId());
	    currentFatalInjuryTraffic.setAccidentClassId(fatalInjuryTraffic.getAccidentClassId());
	    currentFatalInjuryTraffic.setInvolvedVehicleId(fatalInjuryTraffic.getInvolvedVehicleId());
	    currentFatalInjuryTraffic.setServiceTypeId(fatalInjuryTraffic.getServiceTypeId());
	    currentFatalInjuryTraffic.setAlcoholLevelCounterpart(fatalInjuryTraffic.getAlcoholLevelCounterpart());
	    currentFatalInjuryTraffic.setAlcoholLevelCounterpartId(fatalInjuryTraffic.getAlcoholLevelCounterpartId());//con dato
	    //actualizar
	    victimsFacade.edit(currentFatalInjuryTraffic.getFatalInjuries().getVictimId());
	    fatalInjuriesFacade.edit(currentFatalInjuryTraffic.getFatalInjuries());
	    fatalInjuryTrafficFacade.edit(currentFatalInjuryTraffic);	    
	    System.out.println("registro actualizado");
	} catch (Exception e) {
	    System.out.println("*******************************************ERROR: " + e.toString());
	    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
	    FacesContext.getCurrentInstance().addMessage(null, msg);
	}
    }

    public void determinePosition() {

	totalRegisters = fatalInjuryTrafficFacade.count();
	if (currentFatalInjuriId == -1) {
	    currentPosition = "new" + "/" + String.valueOf(totalRegisters);
	    openDialogDelete = "";//es nuevo no se puede borrar
	} else {
	    int position = fatalInjuryTrafficFacade.findPosition(currentFatalInjuryTraffic.getFatalInjuryId());
	    currentPosition = position + "/" + String.valueOf(totalRegisters);
	    openDialogDelete = "dialogDelete.show();";
	}
	if (!save) {
	    currentPosition = currentPosition + " *";
	}
	System.out.println("POSICION DETERMINADA: " + currentPosition);
	//FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Posicion", String.valueOf(currentFatalInjuriId));
	//FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void saveAndGoNext() {//guarda cambios si se han realizado y se dirije al siguiente
	if (saveRegistry()) {
	    next();
	} else {
	    System.out.println("No se guardo");
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
	    System.out.println("cargando siguiente registro");
	    if (currentFatalInjuriId == -1) {//esta en registro nuevo                
	    } else {
		auxFatalInjuryTraffic = fatalInjuryTrafficFacade.findNext(currentFatalInjuriId);
		if (auxFatalInjuryTraffic != null) {
		    clearForm();
		    currentFatalInjuryTraffic = auxFatalInjuryTraffic;
		    currentFatalInjuriId = currentFatalInjuryTraffic.getFatalInjuryId();
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
	    if (currentFatalInjuriId == -1) {//esta en registro nuevo
		last();
		determinePosition();
	    } else {
		auxFatalInjuryTraffic = fatalInjuryTrafficFacade.findPrevious(currentFatalInjuriId);
		if (auxFatalInjuryTraffic != null) {
		    clearForm();
		    currentFatalInjuryTraffic = auxFatalInjuryTraffic;
		    currentFatalInjuriId = currentFatalInjuryTraffic.getFatalInjuryId();
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
	    auxFatalInjuryTraffic = fatalInjuryTrafficFacade.findFirst();
	    if (auxFatalInjuryTraffic != null) {
		clearForm();
		currentFatalInjuryTraffic = auxFatalInjuryTraffic;
		currentFatalInjuriId = currentFatalInjuryTraffic.getFatalInjuryId();
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
	    auxFatalInjuryTraffic = fatalInjuryTrafficFacade.findLast();
	    if (auxFatalInjuryTraffic != null) {
		clearForm();
		currentFatalInjuryTraffic = auxFatalInjuryTraffic;
		currentFatalInjuriId = currentFatalInjuryTraffic.getFatalInjuryId();
		determinePosition();
		loadValues();
	    }
	} else {
	    System.out.println("No esta guardadado (para poder cargar ultimo registro)");
	}
    }

    public void clearForm() {
	//totalRegisters = fatalInjuryTrafficFacade.count();
	//currentPosition = "new" + "/" + String.valueOf(totalRegisters);
	System.out.println("Limpiando formulario");
	//------------------------------------------------------------
	//REINICIAR VALORES PARA LA NUEVA VICTIMA
	//------------------------------------------------------------	
	currentIdentification = 0;
	currentIdentificationNumber = "";
	currentName = "";
	currentSurname = "";
	currentMeasureOfAge = 0;
	currentAge = "";
	currentGender = 0;
	currentJob = 0;
	currentDirectionEvent = "";
	currentPlaceOfResidence = "";
	currentNeighborhoodEvent = "";
	currentVictimCharacteristics = 0;
	currentProtectiveMeasures = 0;

	currentVictimVehicle = 0;
	currentCounterpartVehicle1 = 0;
	currentCounterpartVehicle2 = 0;
	currentCounterpartVehicle3 = 0;
	currentVictimServiceType = 0;
	currentCounterpartServiceType1 = 0;
	currentCounterpartServiceType2 = 0;
	currentCounterpartServiceType3 = 0;

	currentRoadType = 0;
	currentArea = 0;
	currentAccidentClasses = 0;
	currentNumberInjured = "";
	currentNeighborhoodEventCode = "";
	//------------------------------------------------------------
	//REINICIAR VARIABLES LESION DE CAUSA EXTERNA FATAL
	//------------------------------------------------------------
	currentDateEvent = "";
	currentDayEvent = "";
	currentMonthEvent = "";
	currentYearEvent = Integer.toString(c.get(Calendar.YEAR));
	currentHourEvent = "";
	currentMinuteEvent = "";
	currentMilitaryHourEvent = "";
	currentDirectionEvent = "";
	//currentNeighborhoodHomeCode = "";
	//currentNeighborhoodHome = "";
	currentMunicipalitie = 1;
	//neighborhoodHomeNameDisabled = false;
	currentPlace = 0;
	currentNumberVictims = "";
	currentNarrative = "";
	currentWeekdayEvent = "";
	isNoDataAlcoholLevelDisabled = false;
	isUnknownAlcoholLevelDisabled = false;
	isPendentAlcoholLevelDisabled = false;
	isNegativeAlcoholLevelDisabled = false;
	currentAlcoholLevelDisabled = false;
	currentAlcoholLevel = "";
	isNoDataAlcoholLevel = false;
	isUnknownAlcoholLevel = false;
	isPendentAlcoholLevel = false;
	isNegativeAlcoholLevel = false;
	isNoDataAlcoholLevelDisabledC = false;
	isUnknownAlcoholLevelDisabledC = false;
	isPendentAlcoholLevelDisabledC = false;
	isNegativeAlcoholLevelDisabledC = false;
	currentAlcoholLevelDisabledC = false;
	currentAlcoholLevelC = "";
	isNoDataAlcoholLevelC = false;
	isUnknownAlcoholLevelC = false;
	isPendentAlcoholLevelC = false;
	isNegativeAlcoholLevelC = false;
	currentCode = "";
	//------------------------------------------------------------
	//LIMPIAR PARA LA NUEVA LESION FATAL POR HOMICIDIOS
	//------------------------------------------------------------
	currentVictimSource = "";
	//currentMurderContext = 0;
	//currentWeaponType = 0;
	//currentArea = 0;
    }

    public void newForm() {
	//currentFatalInjuryTraffic = null;
	if (save) {
	    clearForm();
	    currentFatalInjuriId = -1;
	    determinePosition();
	} else {
	    System.out.println("No esta guardado (para poder limpiar formulario)");
	}

    }

    public void deleteRegistry() {
	if (currentFatalInjuriId != -1) {
	    fatalInjuryTrafficFacade.remove(currentFatalInjuryTraffic);
	    System.out.println("registro eliminado");
	} else {
	    System.out.println("Se esta actualmente en un nuevo registro, no se puede eliminar");
	}
	//System.out.println("eliminando registro: '" + openDialogDelete + "'");
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
    // FUNCIONES CUANDO LISTAS Y CAMPOS CAMBIAN DE VALOR ----------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void findMunicipalities() {
	Departaments d = departamentsFacade.findById((short) 52);
	municipalities = new SelectItem[d.getMunicipalitiesList().size()];

	for (int i = 0; i < municipalities.length; i++) {
	    municipalities[i] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
	}
	currentMunicipalitie = d.getMunicipalitiesList().get(0).getMunicipalitiesPK().getMunicipalityId();

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

    public void changeMunicipalitie() {
	changeForm();
	//Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, (short) 52);
	//if (currentMunicipalitie == 1) {
	//    neighborhoodHomeNameDisabled = false;
	//} else {
	//    neighborhoodHomeNameDisabled = true;
	//    currentNeighborhoodHome = "";
	//    currentNeighborhoodHomeCode = "";
	//}
    }

    public void changeAlcoholLevelNumber() {
	try {
	    int alcoholLevel = Integer.parseInt(currentAlcoholLevel);
	    if (alcoholLevel < 0) {
		currentAlcoholLevel = "";
	    }
	} catch (Exception e) {
	    currentAlcoholLevel = "";
	}
    }

    public void changeAlcoholLevelNumberC() {
	try {
	    int alcoholLevel = Integer.parseInt(currentAlcoholLevelC);
	    if (alcoholLevel < 0) {
		currentAlcoholLevelC = "";
	    }
	} catch (Exception e) {
	    currentAlcoholLevelC = "";
	}
    }

    public void changeAlcoholLevel() {
	changeForm();
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

    public void changeAlcoholLevelC() {
	if (!isNoDataAlcoholLevelC && !isPendentAlcoholLevelC
		&& !isUnknownAlcoholLevelC && !isNegativeAlcoholLevelC) {
	    currentAlcoholLevelDisabledC = false;
	    currentAlcoholLevelC = "";
	    isNoDataAlcoholLevelDisabledC = false;
	    isPendentAlcoholLevelDisabledC = false;
	    isUnknownAlcoholLevelDisabledC = false;
	    isNegativeAlcoholLevelDisabledC = false;
	} else {
	    if (isNoDataAlcoholLevelC) {
		currentAlcoholLevelDisabledC = true;
		currentAlcoholLevelC = "";
		isNoDataAlcoholLevelDisabledC = false;
		isPendentAlcoholLevelDisabledC = true;
		isUnknownAlcoholLevelDisabledC = true;
		isNegativeAlcoholLevelDisabledC = true;
	    }
	    if (isPendentAlcoholLevelC) {
		currentAlcoholLevelDisabledC = true;
		currentAlcoholLevelC = "";
		isNoDataAlcoholLevelDisabledC = true;
		isPendentAlcoholLevelDisabledC = false;
		isUnknownAlcoholLevelDisabledC = true;
		isNegativeAlcoholLevelDisabledC = true;
	    }
	    if (isUnknownAlcoholLevelC) {
		currentAlcoholLevelDisabledC = true;
		currentAlcoholLevelC = "";
		isNoDataAlcoholLevelDisabledC = true;
		isPendentAlcoholLevelDisabledC = true;
		isUnknownAlcoholLevelDisabledC = false;
		isNegativeAlcoholLevelDisabledC = true;
	    }
	    if (isNegativeAlcoholLevelC) {
		currentAlcoholLevelDisabledC = true;
		currentAlcoholLevelC = "";
		isNoDataAlcoholLevelDisabledC = true;
		isPendentAlcoholLevelDisabledC = true;
		isUnknownAlcoholLevelDisabledC = true;
		isNegativeAlcoholLevelDisabledC = false;
	    }
	}
    }

    public void changeNeighborhoodEvent() {
	changeForm();
	System.out.println("select");
	Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
	if (n != null) {
	    currentNeighborhoodEventCode = String.valueOf(n.getNeighborhoodId());
	} else {
	    currentNeighborhoodEventCode = "";
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

    public void changeNumberVictims() {
	changeForm();
	try {
	    int numberInt = Integer.parseInt(currentNumberVictims);
	    if (numberInt < 0) {
		currentNumberVictims = "";
	    }

	} catch (Exception e) {
	    currentNumberVictims = "";
	}
    }

    public void changeNumberInjured() {
	changeForm();
	try {
	    int numberInt = Integer.parseInt(currentNumberInjured);
	    if (numberInt < 0) {
		currentNumberInjured = "";
	    }

	} catch (Exception e) {
	    currentNumberInjured = "";
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // GET Y SET DE VARIABLES ----------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public SelectItem[] getIdentificationsTypes() {
	return identificationsTypes;
    }

    public void setIdentificationsTypes(SelectItem[] identificationsTypes) {
	this.identificationsTypes = identificationsTypes;
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

    public SelectItem[] getMunicipalities() {
	return municipalities;
    }

    public void setMunicipalities(SelectItem[] municipalities) {
	this.municipalities = municipalities;
    }

    public String getCurrentAmPmEvent() {
	return currentAmPmEvent;
    }

    public void setCurrentAmPmEvent(String currentAmPmEvent) {
	this.currentAmPmEvent = currentAmPmEvent;
	calculateTime1();
    }

    public String getCurrentDateEvent() {
	return currentDateEvent;
    }

    public void setCurrentDateEvent(String currentDateEvent) {
	this.currentDateEvent = currentDateEvent;
    }

    public String getCurrentDayEvent() {
	return currentDayEvent;
    }

    public void setCurrentDayEvent(String currentDayEvent) {
	this.currentDayEvent = currentDayEvent;
	calculateDate1();
    }

    public String getCurrentHourEvent() {
	return currentHourEvent;
    }

    public void setCurrentHourEvent(String currentHourEvent) {
	this.currentHourEvent = currentHourEvent;
	calculateTime1();
    }

    public String getCurrentMilitaryHourEvent() {
	return currentMilitaryHourEvent;
    }

    public void setCurrentMilitaryHourEvent(String currentMilitaryHourEvent) {
	this.currentMilitaryHourEvent = currentMilitaryHourEvent;
    }

    public String getCurrentMinuteEvent() {
	return currentMinuteEvent;
    }

    public void setCurrentMinuteEvent(String currentMinuteEvent) {
	this.currentMinuteEvent = currentMinuteEvent;
	calculateTime1();
    }

    public String getCurrentMonthEvent() {
	return currentMonthEvent;
    }

    public void setCurrentMonthEvent(String currentMonthEvent) {
	this.currentMonthEvent = currentMonthEvent;
	calculateDate1();
    }

    public String getCurrentWeekdayEvent() {
	return currentWeekdayEvent;
    }

    public void setCurrentWeekdayEvent(String currentWeekdayEvent) {
	this.currentWeekdayEvent = currentWeekdayEvent;
    }

    public String getCurrentYearEvent() {
	return currentYearEvent;
    }

    public void setCurrentYearEvent(String currentYearEvent) {
	this.currentYearEvent = currentYearEvent;
	calculateDate1();
    }

    public SelectItem[] getPlaces() {
	return places;
    }

    public void setPlaces(SelectItem[] places) {
	this.places = places;
    }

    public boolean isValueAgeDisabled() {
	return valueAgeDisabled;
    }

    public void setValueAgeDisabled(boolean valueAgeDisabled) {
	this.valueAgeDisabled = valueAgeDisabled;
    }

    public Short getCurrentGender() {
	return currentGender;
    }

    public void setCurrentGender(Short currentGender) {
	this.currentGender = currentGender;
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

    public SelectItem[] getJobs() {
	return jobs;
    }

    public void setJobs(SelectItem[] jobs) {
	this.jobs = jobs;
    }

    public Short getCurrentMeasureOfAge() {
	return currentMeasureOfAge;
    }

    public void setCurrentMeasureOfAge(Short currentMeasureOfAge) {
	this.currentMeasureOfAge = currentMeasureOfAge;
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

    public String getCurrentIdentificationNumber() {
	return currentIdentificationNumber;
    }

    public void setCurrentIdentificationNumber(String currentIdentificationNumber) {
	this.currentIdentificationNumber = currentIdentificationNumber;
    }

    public String getCurrentName() {
	return currentName;
    }

    public void setCurrentName(String currentName) {
	this.currentName = currentName;
    }

    public String getCurrentSurname() {
	return currentSurname;
    }

    public void setCurrentSurname(String currentSurname) {
	this.currentSurname = currentSurname;
    }

    public SelectItem[] getAreas() {
	return areas;
    }

    public void setAreas(SelectItem[] areas) {
	this.areas = areas;
    }

    public Short getCurrentArea() {
	return currentArea;
    }

    public void setCurrentArea(Short currentArea) {
	this.currentArea = currentArea;
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

    public Short getCurrentRoadType() {
	return currentRoadType;
    }

    public void setCurrentRoadType(Short currentRoadType) {
	this.currentRoadType = currentRoadType;
    }

    public SelectItem[] getRoadTypes() {
	return roadTypes;
    }

    public void setRoadTypes(SelectItem[] roadTypes) {
	this.roadTypes = roadTypes;
    }

    public SelectItem[] getAccidentClasses() {
	return accidentClasses;
    }

    public void setAccidentClasses(SelectItem[] accidentClasses) {
	this.accidentClasses = accidentClasses;
    }

    public Short getCurrentAccidentClasses() {
	return currentAccidentClasses;
    }

    public void setCurrentAccidentClasses(Short currentAccidentClasses) {
	this.currentAccidentClasses = currentAccidentClasses;
    }

    public String getCurrentNumberInjured() {
	return currentNumberInjured;
    }

    public void setCurrentNumberInjured(String currentNumberInjured) {
	this.currentNumberInjured = currentNumberInjured;
    }

    public String getCurrentPlaceOfResidence() {
	return currentPlaceOfResidence;
    }

    public void setCurrentPlaceOfResidence(String currentPlaceOfResidence) {
	this.currentPlaceOfResidence = currentPlaceOfResidence;
    }

    public Short getCurrentVictimCharacteristics() {
	return currentVictimCharacteristics;
    }

    public void setCurrentVictimCharacteristics(Short currentVictimCharacteristics) {
	this.currentVictimCharacteristics = currentVictimCharacteristics;
    }

    public SelectItem[] getVictimCharacteristics() {
	return victimCharacteristics;
    }

    public void setVictimCharacteristics(SelectItem[] victimCharacteristics) {
	this.victimCharacteristics = victimCharacteristics;
    }

    public Short getCurrentProtectiveMeasures() {
	return currentProtectiveMeasures;
    }

    public void setCurrentProtectiveMeasures(Short currentProtectiveMeasures) {
	this.currentProtectiveMeasures = currentProtectiveMeasures;
    }

    public SelectItem[] getProtectiveMeasures() {
	return protectiveMeasures;
    }

    public void setProtectiveMeasures(SelectItem[] protectiveMeasures) {
	this.protectiveMeasures = protectiveMeasures;
    }

    public SelectItem[] getServiceTypes() {
	return serviceTypes;
    }

    public void setServiceTypes(SelectItem[] ServiceTypes) {
	this.serviceTypes = ServiceTypes;
    }

    public String getCurrentServiceTypes() {
	return currentServiceTypes;
    }

    public void setCurrentServiceTypes(String currentServiceTypes) {
	this.currentServiceTypes = currentServiceTypes;
    }

    public Short getCurrentCounterpartServiceType1() {
	return currentCounterpartServiceType1;
    }

    public void setCurrentCounterpartServiceType1(Short currentCounterpartServiceType1) {
	this.currentCounterpartServiceType1 = currentCounterpartServiceType1;
    }

    public Short getCurrentCounterpartServiceType2() {
	return currentCounterpartServiceType2;
    }

    public void setCurrentCounterpartServiceType2(Short currentCounterpartServiceType2) {
	this.currentCounterpartServiceType2 = currentCounterpartServiceType2;
    }

    public Short getCurrentCounterpartServiceType3() {
	return currentCounterpartServiceType3;
    }

    public void setCurrentCounterpartServiceType3(Short currentCounterpartServiceType3) {
	this.currentCounterpartServiceType3 = currentCounterpartServiceType3;
    }

    public Short getCurrentCounterpartVehicle1() {
	return currentCounterpartVehicle1;
    }

    public void setCurrentCounterpartVehicle1(Short currentCounterpartVehicle1) {
	this.currentCounterpartVehicle1 = currentCounterpartVehicle1;
    }

    public Short getCurrentCounterpartVehicle2() {
	return currentCounterpartVehicle2;
    }

    public void setCurrentCounterpartVehicle2(Short currentCounterpartVehicle2) {
	this.currentCounterpartVehicle2 = currentCounterpartVehicle2;
    }

    public Short getCurrentCounterpartVehicle3() {
	return currentCounterpartVehicle3;
    }

    public void setCurrentCounterpartVehicle3(Short currentCounterpartVehicle3) {
	this.currentCounterpartVehicle3 = currentCounterpartVehicle3;
    }

    public Short getCurrentVictimServiceType() {
	return currentVictimServiceType;
    }

    public void setCurrentVictimServiceType(Short currentVictimServiceType) {
	this.currentVictimServiceType = currentVictimServiceType;
    }

    public Short getCurrentVictimVehicle() {
	return currentVictimVehicle;
    }

    public void setCurrentVictimVehicle(Short currentVictimVehicle) {
	this.currentVictimVehicle = currentVictimVehicle;
    }

    public SelectItem[] getInvolvedVehicles() {
	return involvedVehicles;
    }

    public void setInvolvedVehicles(SelectItem[] involvedVehicles) {
	this.involvedVehicles = involvedVehicles;
    }

    public String getCurrentAlcoholLevelC() {
	return currentAlcoholLevelC;
    }

    public void setCurrentAlcoholLevelC(String currentAlcoholLevelC) {
	this.currentAlcoholLevelC = currentAlcoholLevelC;
    }

    public boolean isCurrentAlcoholLevelDisabledC() {
	return currentAlcoholLevelDisabledC;
    }

    public void setCurrentAlcoholLevelDisabledC(boolean currentAlcoholLevelDisabledC) {
	this.currentAlcoholLevelDisabledC = currentAlcoholLevelDisabledC;
    }

    public boolean isIsNegativeAlcoholLevelC() {
	return isNegativeAlcoholLevelC;
    }

    public void setIsNegativeAlcoholLevelC(boolean isNegativeAlcoholLevelC) {
	this.isNegativeAlcoholLevelC = isNegativeAlcoholLevelC;
    }

    public boolean isIsNegativeAlcoholLevelDisabledC() {
	return isNegativeAlcoholLevelDisabledC;
    }

    public void setIsNegativeAlcoholLevelDisabledC(boolean isNegativeAlcoholLevelDisabledC) {
	this.isNegativeAlcoholLevelDisabledC = isNegativeAlcoholLevelDisabledC;
    }

    public boolean isIsNoDataAlcoholLevelC() {
	return isNoDataAlcoholLevelC;
    }

    public void setIsNoDataAlcoholLevelC(boolean isNoDataAlcoholLevelC) {
	this.isNoDataAlcoholLevelC = isNoDataAlcoholLevelC;
    }

    public boolean isIsNoDataAlcoholLevelDisabledC() {
	return isNoDataAlcoholLevelDisabledC;
    }

    public void setIsNoDataAlcoholLevelDisabledC(boolean isNoDataAlcoholLevelDisabledC) {
	this.isNoDataAlcoholLevelDisabledC = isNoDataAlcoholLevelDisabledC;
    }

    public boolean isIsPendentAlcoholLevelC() {
	return isPendentAlcoholLevelC;
    }

    public void setIsPendentAlcoholLevelC(boolean isPendentAlcoholLevelC) {
	this.isPendentAlcoholLevelC = isPendentAlcoholLevelC;
    }

    public boolean isIsPendentAlcoholLevelDisabledC() {
	return isPendentAlcoholLevelDisabledC;
    }

    public void setIsPendentAlcoholLevelDisabledC(boolean isPendentAlcoholLevelDisabledC) {
	this.isPendentAlcoholLevelDisabledC = isPendentAlcoholLevelDisabledC;
    }

    public boolean isIsUnknownAlcoholLevelC() {
	return isUnknownAlcoholLevelC;
    }

    public void setIsUnknownAlcoholLevelC(boolean isUnknownAlcoholLevelC) {
	this.isUnknownAlcoholLevelC = isUnknownAlcoholLevelC;
    }

    public boolean isIsUnknownAlcoholLevelDisabledC() {
	return isUnknownAlcoholLevelDisabledC;
    }

    public void setIsUnknownAlcoholLevelDisabledC(boolean isUnknownAlcoholLevelDisabledC) {
	this.isUnknownAlcoholLevelDisabledC = isUnknownAlcoholLevelDisabledC;
    }

    public String getCurrentCode() {
	return currentCode;
    }

    public void setCurrentCode(String currentCode) {
	this.currentCode = currentCode;
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
}
