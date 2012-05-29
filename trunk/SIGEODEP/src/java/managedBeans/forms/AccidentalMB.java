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
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "accidentalMB")
@SessionScoped
public class AccidentalMB {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    //@EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private String currentArea;
    //-------------------- 
    //@EJB
    //WeaponTypesFacade weaponTypesFacade;
    //private String currentWeaponType;
    //private SelectItem[] weaponTypes;
    //-------------------- 
    @EJB
    AccidentMechanismsFacade accidentMechanismsFacade;
    private String currentAccidentMechanisms;
    private SelectItem[] accidentMechanisms;
    //-------------------- 
    //@EJB
    //DepartamentsFacade departamentsFacade;
    //private Short currentDepartamentHome = 52;//nariño
    //private SelectItem[] departaments;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    //--------------------    
    //@EJB
    //UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    //private Short currentUseAlcohol;
    //private Short currentUseDrugs;
    //private SelectItem[] useAlcohol;
    //private SelectItem[] useDrugs;
    //--------------------    
    //@EJB
    //NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    //private String currentIPS;
    //private SelectItem[] IPSs;
    //--------------------
    //@EJB
    //IntentionalitiesFacade intentionalitiesFacade;
    //private Short currentIntentionality;
    //private SelectItem[] intentionalities;
    //--------------------
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace;
    private SelectItem[] places;
    //--------------------
    //@EJB
    //ActivitiesFacade activitiesFacade;
    //private Short currentActivities;
    //private SelectItem[] activities;
    //--------------------
    //@EJB
    //MechanismsFacade mechanismsFacade;
    //private Short currentMechanisms;
    //private SelectItem[] mechanisms;
    //--------------------
    //@EJB
    //EthnicGroupsFacade ethnicGroupsFacade;
    //private Short currentEthnicGroup;
    //private SelectItem[] ethnicGroups;
    //private boolean ethnicGroupsDisabled = true;
    //private String otherEthnicGroup;
    //--------------------
    //@EJB
    //TransportTypesFacade transportTypesFacade;
    //private Short currentTransportTypes;
    //private SelectItem[] transportTypes;
    //--------------------
    //@EJB
    //TransportCounterpartsFacade transportCounterpartsFacade;
    //private Short currentTransportUser;
    //private SelectItem[] transportCounterparts;
    //--------------------
    //@EJB
    //TransportUsersFacade transportUsersFacade;
    //private Short currentTransportCounterpart;
    //private SelectItem[] transportUsers;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender;
    private SelectItem[] genders;
    //--------------------
    //@EJB
    //RelationshipsToVictimFacade relationshipsToVictimFacade;
    //private Short currentRelationshipToVictim;
    //private SelectItem[] relationshipsToVictim;
    //--------------------
    //@EJB
    //ContextsFacade contextsFacade;
    //private Short currentContext;
    //private SelectItem[] contexts;
    //--------------------
    //@EJB
    //AggressorGendersFacade agreAggressorGendersFacade;
    //private Short currentAggressorGenders;
    //private SelectItem[] aggressorGenders;
    //--------------------
    //@EJB
    //PrecipitatingFactorsFacade precipitatingFactorsFacade;
    //private Short currentPrecipitatingFactor;
    //private SelectItem[] precipitatingFactors;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob;
    private SelectItem[] jobs;
    //--------------------
    //@EJB
    //DestinationsOfPatientFacade destinationsOfPatientFacade;
    //private Short currentDestinationPatient;
    //private SelectItem[] destinationsPatient;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome;
    private int currentNeighborhoodHomeCode;
    private String currentNeighborhoodEvent;
    private int currentNeighborhoodEventCode;
    boolean neighborhoodHomeNameDisabled = true;
    //--------------------
    //@EJB
    //HealthProfessionalsFacade healthProfessionalsFacade;
    //private String currentHealthProfessionals;
    //--------------------
    //@EJB
    //DiagnosesFacade diagnosesFacade;
    //private String currentDiagnoses;
    //private SelectItem[] diagnoses;
    //--------------------
    //private SelectItem[] healthInstitutions;
    //private Short currentHealthInstitution;
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
    //private boolean isSubmitted = false;
    //private boolean IPSDisabled = true;
    //-------
    //private boolean otherIntentDisabled = true;
    //private boolean otherPlaceDisabled = true;
    //private boolean otherActivityDisabled = true;
    //private boolean otherMechanismDisabled = true;//otro mecanismo    
    //private String otherMechanism;//otro mecanismo       
    
    //private boolean powderWhichDisabled = true;//cual polvora
    //private String powderWhich;//cual polvora
    //private boolean disasterWhichDisabled = true;//cual desastre
    //private String disasterWhich;//cual desastre
    //private boolean heightWhichDisabled = true;//cual altura
    //private String heightWhich;//cual altura    
    //private String forBurned = "none";//para los quemados
    //private String displaySecurityElements = "block";
    //private String displayInterpersonalViolence = "none";
    //private String displayTransport = "block";
    //private String displayIntentional = "none";
    //private boolean otherTransportTypeDisabled = true;//otro tipo de transporte
    //private String otherTransportType;//otro tipo de transporte    
    //private boolean otherTransportCounterpartsTypeDisabled = true;//otro tipo de transporte contraparte
    //private String otherTransportCounterpartsType;//otro tipo de transporte contraparte   
    //private boolean otherTransportUserTypeDisabled = true;//otro tipo de transporte usuario
    //private String otherTransportUserType;//otro tipo de transporte usuario   
    //private boolean aggressionPast = false;
    //private String otherFactor;
    //private boolean otherFactorDisabled = true;
    //private boolean relationshipToVictimDisabled = true;
    //private boolean contextDisabled = true;
    //private String otherRelation;
    //private boolean otherRelationDisabled = true;
    //private boolean aggressorGendersDisabled = true;
    //private boolean checkOtherInjury;
    //private boolean checkOtherPlace;
    //private boolean otherInjuryDisabled = true;
    //private boolean otherInjuryPlaceDisabled = true;
    //private boolean otherDestinationPatientDisabled = true;
    //private String otherDestinationPatient;
    //private String txtOtherInjury;
    //private String txtOtherPlace;
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
    //private String currentSecurityElements;
    //private String currentMedicalHistory;//@Size(min = 6, max = 8) 
    private String currentDayEvent;
    private String currentMonthEvent;
    private String currentYearEvent;
    private String currentDateEvent;
    private String currentWeekdayEvent;
    private String currentHourEvent;
    private String currentMinuteEvent;
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent;
    //private String currentDayConsult;
    //private String currentMonthConsult;
    //private String currentYearConsult;
    //private String currentDateConsult;
    //private String currentWeekdayConsult;
    //private String currentHourConsult;
    //private String currentMinuteConsult;
    //private String currentAmPmConsult = "AM";
    //private String currentMilitaryHourConsult;
    private String currentName;
    private String currentSurame;
    private String currentIdentificationNumber;
    //private String currentInsurance;
    //private String currentDirectionHome;
//    private String currentTelephoneHome;
    private String currentDirectionEvent;
//    private String currentOtherIntentionality;
//    private String currentOtherPlace;
//    private String currentOtherActivitie;
    private String currentSurname;
//    private Short currentLevelBurned;
//    private Short currentPercentBurned;
    private String currentNumberVictims;
    private String currentVictimSource;
//    private boolean isDisplaced;
//    private boolean isHandicapped;
//    private boolean isBeltUse;
//    private boolean isHelmetUse;
//    private boolean isBicycleHelmetUse;
//    private boolean isVestUse;
//    private boolean isPreviousAttempt;
//    private boolean isMentalDisorder;
//    private boolean isUnknownNatureOfInjurye;
    
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public AccidentalMB() {
    }

    public void reset() {
	try {
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

	    //cargo los lugares donde ocurrieron los hechos
	    List<Places> placesList = placesFacade.findAll();
	    places = new SelectItem[placesList.size()];
	    for (int i = 0; i < placesList.size(); i++) {
		places[i] = new SelectItem(placesList.get(i).getPlaceId(), placesList.get(i).getPlaceName());
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
	    
	    //cargo las areas del hecho
            List<Areas> areasList = areasFacade.findAll();
            areas = new SelectItem[areasList.size()];
            for (int i = 0; i < areasList.size(); i++) {
                areas[i] = new SelectItem(areasList.get(i).getAreaName());
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

    public void findMunicipalitieCode() {
	//Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, currentDepartamentHome);
	if (currentMunicipalitie == 1) {
	    neighborhoodHomeNameDisabled = false;
	} else {
	    neighborhoodHomeNameDisabled = true;
	    currentNeighborhoodHome = "";
	    currentNeighborhoodHomeCode = 0;
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

//    public void changeOtherInjury() {
//	if (checkOtherInjury) {
//	    otherInjuryDisabled = false;
//
//	} else {
//	    otherInjuryDisabled = true;
//	    txtOtherInjury = "";
//	}
//    }
//
//    public void changeOtherPlace() {
//	if (checkOtherPlace) {
//	    otherPlaceDisabled = false;
//	} else {
//	    otherPlaceDisabled = true;
//	    txtOtherPlace = "";
//	}
//    }
//
//    public void changeSecurityElements() {
//	if (currentSecurityElements.compareTo("SI") == 0) {
//	    displaySecurityElements = "block";
//	} else {
//	    displaySecurityElements = "none";
//	}
//    }

    public void changeNeighborhoodHomeName() {
	List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
	for (int i = 0; i < neighborhoodsList.size(); i++) {
	    if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
		currentNeighborhoodHomeCode = neighborhoodsList.get(i).getNeighborhoodId();
		break;
	    }
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

    public void changeMeasuresOfAge() {
	if (currentMeasureOfAge == 4) {//4. otro
	    valueAgeDisabled = true;

	} else {
	    valueAgeDisabled = false;
	    currentAge = "";
	}
	//System.out.println("----" + currentEthnicGroup + "----" + ethnicGroupsDisabled);

    }

//    public void changePlace() {
//	if (currentPlace == 8) {//8. otro
//	    otherPlaceDisabled = false;
//	} else {
//	    otherPlaceDisabled = true;
//
//	}
//    }

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

//    private void calculateDate2() {
//	try {
//	    fechaI = formato.parse(currentDayConsult + "/" + currentMonthConsult + "/" + currentYearConsult);
//	    Calendar cal = Calendar.getInstance();
//	    cal.setTime(fechaI);
//	    currentDateConsult = formato.format(fechaI);
//	    //currentWeekdayConsult = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
//	    if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
//		currentWeekdayConsult = "Lunes";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
//		currentWeekdayConsult = "Martes";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
//		currentWeekdayConsult = "Miércoles";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
//		currentWeekdayConsult = "Jueves";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
//		currentWeekdayConsult = "Viernes";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
//		currentWeekdayConsult = "Sábado";
//	    } else if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
//		currentWeekdayConsult = "Domingo";
//	    }
//
//	} catch (ParseException ex) {
//	    // POR FAVOR CORRIJA LA FECHA DEL PRIMER PAGO
//	    currentDateConsult = "";
//	    currentWeekdayConsult = "";
//	}
//    }

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

//    private void calculateTime2() {
//	int hourInt;
//	int minuteInt;
//	int timeInt;
//	try {
//	    hourInt = Integer.parseInt(currentHourConsult);
//	} catch (Exception ex) {
//	    hourInt = 0;
//	}
//	try {
//	    minuteInt = Integer.parseInt(currentMinuteConsult);
//	} catch (Exception ex) {
//	    minuteInt = 0;
//	}
//	try {
//	    if (currentAmPmConsult.length() != 0) {
//		String hourStr;
//		String minuteStr;
//		String timeStr;
//		boolean continuar = true;
//		if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
//		    if (currentAmPmConsult.compareTo("PM") == 0) {//hora PM
//			if (hourInt == 12) {//no existe hora 12
//
//			    currentMilitaryHourConsult = "Error";
//			    continuar = false;
//			}
//			if (continuar) {
//			    hourStr = String.valueOf(hourInt);
//			    minuteStr = String.valueOf(minuteInt);
//			    if (hourStr.length() == 1) {
//				hourStr = "0" + hourStr;
//			    }
//			    if (minuteStr.length() == 1) {
//				minuteStr = "0" + minuteStr;
//			    }
//			    timeStr = hourStr + minuteStr;
//			    timeInt = Integer.parseInt(timeStr);
//			    if (timeInt > 2400) {
//				timeStr = "00" + minuteStr;
//			    }
//			    currentMilitaryHourConsult = timeStr;
//			}
//		    } else {//hora AM
//			if (hourInt == 12) {
//			    hourInt = hourInt + 12;
//			}
//		    }
//		    if (continuar) {
//			hourStr = String.valueOf(hourInt);
//			minuteStr = String.valueOf(minuteInt);
//			if (hourStr.length() == 1) {
//			    hourStr = "0" + hourStr;
//			}
//			if (minuteStr.length() == 1) {
//			    minuteStr = "0" + minuteStr;
//			}
//			timeStr = hourStr + minuteStr;
//			timeInt = Integer.parseInt(timeStr);
//			if (timeInt > 2400) {
//			    timeStr = "00" + minuteStr;
//			}
//			currentMilitaryHourConsult = timeStr;
//		    }
//
//		} else {
//		    currentMilitaryHourConsult = "Error";
//		}
//	    }
//	} catch (Exception ex) {
//
//	    currentMilitaryHourConsult = "Error  " + ex.toString();
//	}
//    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // GET Y SET DE VARIABLES ----------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
//    public String getCurrentMedicalHistory() {
//	return currentMedicalHistory;
//    }

//    public void setCurrentMedicalHistory(String currentMedicalHistory) {
//	this.currentMedicalHistory = currentMedicalHistory;
//    }

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

    public SelectItem[] getMunicipalities() {
	return municipalities;
    }

    public void setMunicipalities(SelectItem[] municipalities) {
	this.municipalities = municipalities;
    }

//    public String getCurrentAmPmConsult() {
//	return currentAmPmConsult;
//    }
//
//    public void setCurrentAmPmConsult(String currentAmPmConsult) {
//	this.currentAmPmConsult = currentAmPmConsult;
//	calculateTime2();
//    }

    public String getCurrentAmPmEvent() {
	return currentAmPmEvent;
    }

    public void setCurrentAmPmEvent(String currentAmPmEvent) {
	this.currentAmPmEvent = currentAmPmEvent;
	calculateTime1();
    }

//    public String getCurrentDateConsult() {
//	return currentDateConsult;
//    }
//
//    public void setCurrentDateConsult(String currentDateConsult) {
//	this.currentDateConsult = currentDateConsult;
//    }

    public String getCurrentDateEvent() {
	return currentDateEvent;
    }

    public void setCurrentDateEvent(String currentDateEvent) {
	this.currentDateEvent = currentDateEvent;
    }

//    public String getCurrentDayConsult() {
//	return currentDayConsult;
//    }
//
//    public void setCurrentDayConsult(String currentDayConsult) {
//	this.currentDayConsult = currentDayConsult;
//	calculateDate2();
//    }

    public String getCurrentDayEvent() {
	return currentDayEvent;
    }

    public void setCurrentDayEvent(String currentDayEvent) {
	this.currentDayEvent = currentDayEvent;
	calculateDate1();
    }

//    public String getCurrentHourConsult() {
//	return currentHourConsult;
//    }
//
//    public void setCurrentHourConsult(String currentHourConsult) {
//	this.currentHourConsult = currentHourConsult;
//	calculateTime2();
//    }

    public String getCurrentHourEvent() {
	return currentHourEvent;
    }

    public void setCurrentHourEvent(String currentHourEvent) {
	this.currentHourEvent = currentHourEvent;
	calculateTime1();
    }

//    public String getCurrentMilitaryHourConsult() {
//	return currentMilitaryHourConsult;
//    }
//
//    public void setCurrentMilitaryHourConsult(String currentMilitaryHourConsult) {
//	this.currentMilitaryHourConsult = currentMilitaryHourConsult;
//    }

    public String getCurrentMilitaryHourEvent() {
	return currentMilitaryHourEvent;
    }

    public void setCurrentMilitaryHourEvent(String currentMilitaryHourEvent) {
	this.currentMilitaryHourEvent = currentMilitaryHourEvent;
    }

//    public String getCurrentMinuteConsult() {
//	return currentMinuteConsult;
//    }
//
//    public void setCurrentMinuteConsult(String currentMinuteConsult) {
//	this.currentMinuteConsult = currentMinuteConsult;
//	calculateTime2();
//    }

    public String getCurrentMinuteEvent() {
	return currentMinuteEvent;
    }

    public void setCurrentMinuteEvent(String currentMinuteEvent) {
	this.currentMinuteEvent = currentMinuteEvent;
	calculateTime1();
    }

//    public String getCurrentMonthConsult() {
//	return currentMonthConsult;
//    }
//
//    public void setCurrentMonthConsult(String currentMonthConsult) {
//	this.currentMonthConsult = currentMonthConsult;
//	calculateDate2();
//    }

    public String getCurrentMonthEvent() {
	return currentMonthEvent;
    }

    public void setCurrentMonthEvent(String currentMonthEvent) {
	this.currentMonthEvent = currentMonthEvent;
	calculateDate1();
    }

//    public String getCurrentWeekdayConsult() {
//	return currentWeekdayConsult;
//    }
//
//    public void setCurrentWeekdayConsult(String currentWeekdayConsult) {
//	this.currentWeekdayConsult = currentWeekdayConsult;
//    }

    public String getCurrentWeekdayEvent() {
	return currentWeekdayEvent;
    }

    public void setCurrentWeekdayEvent(String currentWeekdayEvent) {
	this.currentWeekdayEvent = currentWeekdayEvent;
    }

//    public String getCurrentYearConsult() {
//	return currentYearConsult;
//
//    }
//
//    public void setCurrentYearConsult(String currentYearConsult) {
//	this.currentYearConsult = currentYearConsult;
//	calculateDate2();
//    }

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

    public SelectItem[] getJobs() {
	return jobs;
    }

    public boolean isNeighborhoodHomeNameDisabled() {
	return neighborhoodHomeNameDisabled;
    }

    public void setNeighborhoodHomeNameDisabled(boolean neighborhoodHomeNameDisabled) {
	this.neighborhoodHomeNameDisabled = neighborhoodHomeNameDisabled;
    }

//    public boolean isOtherActivityDisabled() {
//	return otherActivityDisabled;
//    }
//
//    public void setOtherActivityDisabled(boolean otherActivityDisabled) {
//	this.otherActivityDisabled = otherActivityDisabled;
//    }
//
//    public boolean isOtherIntentDisabled() {
//	return otherIntentDisabled;
//    }
//
//    public void setOtherIntentDisabled(boolean otherIntentDisabled) {
//	this.otherIntentDisabled = otherIntentDisabled;
//    }
//
//    public boolean isOtherPlaceDisabled() {
//	return otherPlaceDisabled;
//    }
//
//    public void setOtherPlaceDisabled(boolean otherPlaceDisabled) {
//	this.otherPlaceDisabled = otherPlaceDisabled;
//    }
//
//    public String getDisasterWhich() {
//	return disasterWhich;
//    }
//
//    public void setDisasterWhich(String disasterWhich) {
//	this.disasterWhich = disasterWhich;
//    }
//
//    public boolean isDisasterWhichDisabled() {
//	return disasterWhichDisabled;
//    }
//
//    public void setDisasterWhichDisabled(boolean disasterWhichDisabled) {
//	this.disasterWhichDisabled = disasterWhichDisabled;
//    }
//
//    public String getHeightWhich() {
//	return heightWhich;
//    }
//
//    public void setHeightWhich(String heightWhich) {
//	this.heightWhich = heightWhich;
//    }
//
//    public boolean isHeightWhichDisabled() {
//	return heightWhichDisabled;
//    }
//
//    public void setHeightWhichDisabled(boolean heightWhichDisabled) {
//	this.heightWhichDisabled = heightWhichDisabled;
//    }
//
//    public String getOtherMechanism() {
//	return otherMechanism;
//    }
//
//    public void setOtherMechanism(String otherMechanism) {
//	this.otherMechanism = otherMechanism;
//    }
//
//    public boolean isOtherMechanismDisabled() {
//	return otherMechanismDisabled;
//    }
//
//    public void setOtherMechanismDisabled(boolean otherMechanismDisabled) {
//	this.otherMechanismDisabled = otherMechanismDisabled;
//    }
//
//    public String getPowderWhich() {
//	return powderWhich;
//    }
//
//    public void setPowderWhich(String powderWhich) {
//	this.powderWhich = powderWhich;
//    }
//
//    public boolean isPowderWhichDisabled() {
//	return powderWhichDisabled;
//    }
//
//    public void setPowderWhichDisabled(boolean powderWhichDisabled) {
//	this.powderWhichDisabled = powderWhichDisabled;
//    }
//
//    public String getForBurned() {
//	return forBurned;
//    }
//
//    public void setForBurned(String forBurned) {
//	this.forBurned = forBurned;
//    }
//
//    public String getOtherTransportCounterpartsType() {
//	return otherTransportCounterpartsType;
//    }
//
//    public void setOtherTransportCounterpartsType(String otherTransportCounterpartsType) {
//	this.otherTransportCounterpartsType = otherTransportCounterpartsType;
//    }
//
//    public String getOtherTransportType() {
//	return otherTransportType;
//    }
//
//    public void setOtherTransportType(String otherTransportType) {
//	this.otherTransportType = otherTransportType;
//    }
//
//    public boolean isOtherTransportCounterpartsTypeDisabled() {
//	return otherTransportCounterpartsTypeDisabled;
//    }
//
//    public void setOtherTransportCounterpartsTypeDisabled(boolean otherTransportCounterpartsTypeDisabled) {
//	this.otherTransportCounterpartsTypeDisabled = otherTransportCounterpartsTypeDisabled;
//    }
//
//    public boolean isOtherTransportTypeDisabled() {
//	return otherTransportTypeDisabled;
//    }
//
//    public void setOtherTransportTypeDisabled(boolean otherTransportTypeDisabled) {
//	this.otherTransportTypeDisabled = otherTransportTypeDisabled;
//    }
//
//    public String getOtherTransportUserType() {
//	return otherTransportUserType;
//    }
//
//    public void setOtherTransportUserType(String otherTransportUserType) {
//	this.otherTransportUserType = otherTransportUserType;
//    }
//
//    public boolean isOtherTransportUserTypeDisabled() {
//	return otherTransportUserTypeDisabled;
//    }
//
//    public void setOtherTransportUserTypeDisabled(boolean otherTransportUserTypeDisabled) {
//	this.otherTransportUserTypeDisabled = otherTransportUserTypeDisabled;
//    }
//
//    public String getDisplaySecurityElements() {
//	return displaySecurityElements;
//    }
//
//    public void setDisplaySecurityElements(String displaySecurityElements) {
//	this.displaySecurityElements = displaySecurityElements;
//    }
//
//    public String getCurrentSecurityElements() {
//	return currentSecurityElements;
//    }
//
//    public void setCurrentSecurityElements(String currentSecurityElements) {
//	this.currentSecurityElements = currentSecurityElements;
//    }
//
//    public boolean isAggressionPast() {
//	return aggressionPast;
//    }
//
//    public void setAggressionPast(boolean aggressionPast) {
//	this.aggressionPast = aggressionPast;
//    }
//
//    public boolean isAggressorGendersDisabled() {
//	return aggressorGendersDisabled;
//    }
//
//    public void setAggressorGendersDisabled(boolean aggressorGendersDisabled) {
//	this.aggressorGendersDisabled = aggressorGendersDisabled;
//    }
//
//    public boolean isContextDisabled() {
//	return contextDisabled;
//    }
//
//    public void setContextDisabled(boolean contextDisabled) {
//	this.contextDisabled = contextDisabled;
//    }
//
//    public boolean isOtherRelationDisabled() {
//	return otherRelationDisabled;
//    }
//
//    public void setOtherRelationDisabled(boolean otherRelationDisabled) {
//	this.otherRelationDisabled = otherRelationDisabled;
//    }
//
//    public boolean isRelationshipToVictimDisabled() {
//	return relationshipToVictimDisabled;
//    }
//
//    public void setRelationshipToVictimDisabled(boolean relationshipToVictimDisabled) {
//	this.relationshipToVictimDisabled = relationshipToVictimDisabled;
//    }
//
//    public String getOtherRelation() {
//	return otherRelation;
//    }
//
//    public void setOtherRelation(String otherRelation) {
//	this.otherRelation = otherRelation;
//    }
//
//    public String getOtherFactor() {
//	return otherFactor;
//    }
//
//    public void setOtherFactor(String otherFactor) {
//	this.otherFactor = otherFactor;
//    }
//
//    public boolean isOtherFactorDisabled() {
//	return otherFactorDisabled;
//    }
//
//    public void setOtherFactorDisabled(boolean otherFactorDisabled) {
//	this.otherFactorDisabled = otherFactorDisabled;
//    }
//
//    public boolean isCheckOtherInjury() {
//	return checkOtherInjury;
//    }
//
//    public void setCheckOtherInjury(boolean checkOtherInjury) {
//	this.checkOtherInjury = checkOtherInjury;
//    }
//
//    public boolean isCheckOtherPlace() {
//	return checkOtherPlace;
//    }
//
//    public void setCheckOtherPlace(boolean checkOtherPlace) {
//	this.checkOtherPlace = checkOtherPlace;
//    }
//
//    public boolean isOtherInjuryDisabled() {
//	return otherInjuryDisabled;
//    }
//
//    public void setOtherInjuryDisabled(boolean otherInjuryDisabled) {
//	this.otherInjuryDisabled = otherInjuryDisabled;
//    }
//
//    public boolean isOtherInjuryPlaceDisabled() {
//	return otherInjuryPlaceDisabled;
//    }
//
//    public void setOtherInjuryPlaceDisabled(boolean otherInjuryPlaceDisabled) {
//	this.otherInjuryPlaceDisabled = otherInjuryPlaceDisabled;
//    }
//
//    public String getTxtOtherInjury() {
//	return txtOtherInjury;
//    }
//
//    public void setTxtOtherInjury(String txtOtherInjury) {
//	this.txtOtherInjury = txtOtherInjury;
//    }
//
//    public String getTxtOtherPlace() {
//	return txtOtherPlace;
//    }
//
//    public void setTxtOtherPlace(String txtOtherPlace) {
//	this.txtOtherPlace = txtOtherPlace;
//    }
//
//    public String getOtherDestinationPatient() {
//	return otherDestinationPatient;
//    }
//
//    public void setOtherDestinationPatient(String otherDestinationPatient) {
//	this.otherDestinationPatient = otherDestinationPatient;
//    }
//
//    public boolean isOtherDestinationPatientDisabled() {
//	return otherDestinationPatientDisabled;
//    }
//
//    public void setOtherDestinationPatientDisabled(boolean otherDestinationPatientDisabled) {
//	this.otherDestinationPatientDisabled = otherDestinationPatientDisabled;
//    }
//
//    public String getDisplayIntentional() {
//	return displayIntentional;
//    }
//
//    public void setDisplayIntentional(String displayIntentional) {
//	this.displayIntentional = displayIntentional;
//    }
//
//    public String getDisplayInterpersonalViolence() {
//	return displayInterpersonalViolence;
//    }
//
//    public void setDisplayInterpersonalViolence(String displayInterpersonalViolence) {
//	this.displayInterpersonalViolence = displayInterpersonalViolence;
//    }
//
//    public String getDisplayTransport() {
//	return displayTransport;
//    }
//
//    public void setDisplayTransport(String displayTransport) {
//	this.displayTransport = displayTransport;
//    }

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

//    public String getCurrentDirectionHome() {
//	return currentDirectionHome;
//    }
//
//    public void setCurrentDirectionHome(String currentDirectionHome) {
//	this.currentDirectionHome = currentDirectionHome;
//    }

    public String getCurrentIdentificationNumber() {
	return currentIdentificationNumber;
    }

    public void setCurrentIdentificationNumber(String currentIdentificationNumber) {
	this.currentIdentificationNumber = currentIdentificationNumber;
    }

//    public String getCurrentInsurance() {
//	return currentInsurance;
//    }
//
//    public void setCurrentInsurance(String currentInsurance) {
//	this.currentInsurance = currentInsurance;
//    }
//
//    public Short getCurrentLevelBurned() {
//	return currentLevelBurned;
//    }
//
//    public void setCurrentLevelBurned(Short currentLevelBurned) {
//	this.currentLevelBurned = currentLevelBurned;
//    }

    public String getCurrentName() {
	return currentName;
    }

    public void setCurrentName(String currentName) {
	this.currentName = currentName;
    }

//    public String getCurrentOtherActivitie() {
//	return currentOtherActivitie;
//    }
//
//    public void setCurrentOtherActivitie(String currentOtherActivitie) {
//	this.currentOtherActivitie = currentOtherActivitie;
//    }
//
//    public String getCurrentOtherIntentionality() {
//	return currentOtherIntentionality;
//    }
//
//    public void setCurrentOtherIntentionality(String currentOtherIntentionality) {
//	this.currentOtherIntentionality = currentOtherIntentionality;
//    }
//
//    public String getCurrentOtherPlace() {
//	return currentOtherPlace;
//    }
//
//    public void setCurrentOtherPlace(String currentOtherPlace) {
//	this.currentOtherPlace = currentOtherPlace;
//    }
//
//    public Short getCurrentPercentBurned() {
//	return currentPercentBurned;
//    }
//
//    public void setCurrentPercentBurned(Short currentPercentBurned) {
//	this.currentPercentBurned = currentPercentBurned;
//    }

    public String getCurrentSurame() {
	return currentSurame;
    }

    public void setCurrentSurame(String currentSurame) {
	this.currentSurame = currentSurame;
    }

//    public String getCurrentTelephoneHome() {
//	return currentTelephoneHome;
//    }
//
//    public void setCurrentTelephoneHome(String currentTelephoneHome) {
//	this.currentTelephoneHome = currentTelephoneHome;
//    }
//
//    public boolean isIsBeltUse() {
//	return isBeltUse;
//    }
//
//    public void setIsBeltUse(boolean isBeltUse) {
//	this.isBeltUse = isBeltUse;
//    }
//
//    public boolean isIsBicycleHelmetUse() {
//	return isBicycleHelmetUse;
//    }
//
//    public void setIsBicycleHelmetUse(boolean isBicycleHelmetUse) {
//	this.isBicycleHelmetUse = isBicycleHelmetUse;
//    }
//
//    public boolean isIsDisplaced() {
//	return isDisplaced;
//    }
//
//    public void setIsDisplaced(boolean isDisplaced) {
//	this.isDisplaced = isDisplaced;
//    }
//
//    public boolean isIsHandicapped() {
//	return isHandicapped;
//    }
//
//    public void setIsHandicapped(boolean isHandicapped) {
//	this.isHandicapped = isHandicapped;
//    }
//
//    public boolean isIsHelmetUse() {
//	return isHelmetUse;
//    }
//
//    public void setIsHelmetUse(boolean isHelmetUse) {
//	this.isHelmetUse = isHelmetUse;
//    }
//
//    public boolean isIsMentalDisorder() {
//	return isMentalDisorder;
//    }
//
//    public void setIsMentalDisorder(boolean isMentalDisorder) {
//	this.isMentalDisorder = isMentalDisorder;
//    }
//
//    public boolean isIsPreviousAttempt() {
//	return isPreviousAttempt;
//    }
//
//    public void setIsPreviousAttempt(boolean isPreviousAttempt) {
//	this.isPreviousAttempt = isPreviousAttempt;
//    }
//
//    public boolean isIsVestUse() {
//	return isVestUse;
//    }
//
//    public void setIsVestUse(boolean isVestUse) {
//	this.isVestUse = isVestUse;
//    }

    public String getCurrentSurname() {
	return currentSurname;
    }

    public void setCurrentSurname(String currentSurname) {
	this.currentSurname = currentSurname;
    }

//    public boolean isIsUnknownNatureOfInjurye() {
//	return isUnknownNatureOfInjurye;
//    }
//
//    public void setIsUnknownNatureOfInjurye(boolean isUnknownNatureOfInjurye) {
//	this.isUnknownNatureOfInjurye = isUnknownNatureOfInjurye;
//    }

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
    
    
}
