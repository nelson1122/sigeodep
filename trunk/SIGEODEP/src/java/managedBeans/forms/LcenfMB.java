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
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.ActivitiesFacade;
import model.dao.AggressorGendersFacade;
import model.dao.ContextsFacade;
import model.dao.DepartamentsFacade;
import model.dao.DestinationsOfPatientFacade;
import model.dao.DiagnosesFacade;
import model.dao.HealthProfessionalsFacade;
import model.dao.IntentionalitiesFacade;
import model.dao.MechanismsFacade;
import model.dao.MunicipalitiesFacade;
import model.dao.NeighborhoodsFacade;
import model.dao.PlacesFacade;
import model.dao.PrecipitatingFactorsFacade;
import model.dao.RelationshipsToVictimFacade;
import model.dao.TransportTypesFacade;
import model.dao.TransportUsersFacade;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "lcenfMB")
@SessionScoped
public class LcenfMB {

    private SelectItem[] healthInstitutions;
    private String currentHealthInstitution;
    private SelectItem[] identifications;
    private String currentIdentification;
    private SelectItem[] measuresOfAge;
    private String currentMeasureOfAge;
    private String currentDepartamentHome;
    private String currentDepartamentHomeCode = "91";
    private SelectItem[] departaments;
    private String currentIntentionality;
    private SelectItem[] intentionalities;
    private String currentPlace;
    private SelectItem[] places;
    private String currentActivities;
    private SelectItem[] activities;
    private String currentMechanisms;
    private SelectItem[] mechanisms;
    private String currentTransportTypesCounterpart;
    private String currentTransportTypes;
    private SelectItem[] transportTypes;
    private String currentTransportUser;
    private SelectItem[] transportUsers;
    private String currentRelationshipToVictim;
    private SelectItem[] relationshipsToVictim;
    private String currentContext;
    private SelectItem[] contexts;
    private String currentAggressorGenders;
    private SelectItem[] aggressorGenders;
    private String currentPrecipitatingFactor;
    private SelectItem[] precipitatingFactors;
    private String currentMunicipalitie;
    private String currentMunicipalitieCode = "19";
    private SelectItem[] municipalities;
    private String currentDestinationPatient;
    private SelectItem[] destinationsPatient;
    private String txtCIE10_1;
    private String txtCIE10_2;
    private String txtCIE10_3;
    private String txtCIE10_4;
    private String idCIE10_1;
    private String idCIE10_2;
    private String idCIE10_3;
    private String idCIE10_4;
    private String currentGender;
    private String currentMedicalHistory;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private String currentNeighborhoodHomeName;
    private String currentNeighborhoodHomeCode;
    private String currentNeighborhoodEventName;
    private String currentNeighborhoodEventCode;
    private String currentHealthProfessionals;
    private String currentDayEvent;
    private String currentMonthEvent;
    private String currentYearEvent;
    private String currentDateEvent;
    private String currentWeekdayEvent;
    private String currentHourEvent;
    private String currentMinuteEvent;
    private String currentAmPmEvent;
    private String currentMilitaryHourEvent;
    private String currentDayConsult;
    private String currentMonthConsult;
    private String currentYearConsult;
    private String currentDateConsult;
    private String currentWeekdayConsult;
    private String currentHourConsult;
    private String currentMinuteConsult;
    private String currentAmPmConsult;
    private String currentMilitaryHourConsult;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DiagnosesFacade diagnosesFacade;
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    @EJB
    IntentionalitiesFacade intentionalitiesFacade;
    @EJB
    PlacesFacade placesFacade;
    @EJB
    ActivitiesFacade activitiesFacade;
    @EJB
    MechanismsFacade mechanismsFacade;
    @EJB
    TransportTypesFacade transportTypesFacade;
    @EJB
    TransportUsersFacade transportUsersFacade;
    @EJB
    RelationshipsToVictimFacade relationshipsToVictimFacade;
    @EJB
    ContextsFacade contextsFacade;
    @EJB
    AggressorGendersFacade agreAggressorGendersFacade;
    @EJB
    PrecipitatingFactorsFacade precipitatingFactorsFacade;

    /**
     * Creates a new instance of LcenfMB
     */
    public LcenfMB() {
    }

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

    public void reset() {
        try {
            ArrayList<String> category;
            //cargo las instituciones de salud
            category = formsAndFieldsDataMB.categoricalNameList("instisal", 0);
            healthInstitutions = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                healthInstitutions[i] = new SelectItem(category.get(i).toString());
            }
            //cargo los tipos de identificacion
            category = formsAndFieldsDataMB.categoricalNameList("tid", 0);
            identifications = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                identifications[i] = new SelectItem(category.get(i).toString());
            }
            //cargo las medidas de edad
            category = formsAndFieldsDataMB.categoricalNameList("medad", 0);
            measuresOfAge = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                measuresOfAge[i] = new SelectItem(category.get(i).toString());
            }
            //cargo los destinos del paciente
            List<DestinationsOfPatient> destinationsList = destinationsOfPatientFacade.findAll();
            destinationsPatient = new SelectItem[destinationsList.size()];
            for (int i = 0; i < destinationsList.size(); i++) {
                destinationsPatient[i] = new SelectItem(destinationsList.get(i).getDestinationPatientName());
            }
            //cargo los departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            departaments = new SelectItem[departamentsList.size()];
            for (int i = 0; i < departamentsList.size(); i++) {
                departaments[i] = new SelectItem(departamentsList.get(i).getDepartamentName());
            }
            //cargo los municipios
            List<Municipalities> municipalitiesList = municipalitiesFacade.findAll();
            municipalities = new SelectItem[municipalitiesList.size()];
            for (int i = 0; i < municipalitiesList.size(); i++) {
                municipalities[i] = new SelectItem(municipalitiesList.get(i).getMunicipalityName());
            }

            //cargo las intencionalidades
            List<Intentionalities> intentionalitiesList = intentionalitiesFacade.findAll();
            intentionalities = new SelectItem[intentionalitiesList.size()];
            for (int i = 0; i < intentionalitiesList.size(); i++) {
                intentionalities[i] = new SelectItem(intentionalitiesList.get(i).getIntentionalityName());
            }

            //cargo los lugares donde ocurrieron los hechos
            List<Places> placesList = placesFacade.findAll();
            places = new SelectItem[placesList.size()];
            for (int i = 0; i < placesList.size(); i++) {
                places[i] = new SelectItem(placesList.get(i).getPlaceName());
            }

            //cargo las Actividades realizadas cuando ocurrio la lesión
            List<Activities> activitiesList = activitiesFacade.findAll();
            activities = new SelectItem[activitiesList.size()];
            for (int i = 0; i < activitiesList.size(); i++) {
                activities[i] = new SelectItem(activitiesList.get(i).getActivityName());
            }

            //cargo los mecanismos de lesión
            List<Mechanisms> mechanismsList = mechanismsFacade.findAll();
            mechanisms = new SelectItem[mechanismsList.size()];
            for (int i = 0; i < mechanismsList.size(); i++) {
                mechanisms[i] = new SelectItem(mechanismsList.get(i).getMechanismName());
            }

            //cargo los tipos de transporte en lesiones de tránsito
            List<TransportTypes> transportTypesList = transportTypesFacade.findAll();
            transportTypes = new SelectItem[transportTypesList.size()];
            for (int i = 0; i < transportTypesList.size(); i++) {
                transportTypes[i] = new SelectItem(transportTypesList.get(i).getTransportTypeName());
            }

            //cargo los usuarios en una lesion de tránsito y trasporte
            List<TransportUsers> transportUsersList = transportUsersFacade.findAll();
            transportUsers = new SelectItem[transportUsersList.size()];
            for (int i = 0; i < transportUsersList.size(); i++) {
                transportUsers[i] = new SelectItem(transportUsersList.get(i).getTransportUserName());
            }

            //cargo las relaciones entre agresos y victima
            List<RelationshipsToVictim> relationshipsToVictimList = relationshipsToVictimFacade.findAll();
            relationshipsToVictim = new SelectItem[relationshipsToVictimList.size()];
            for (int i = 0; i < relationshipsToVictimList.size(); i++) {
                relationshipsToVictim[i] = new SelectItem(relationshipsToVictimList.get(i).getRelationshipVictimName());
            }

            //cargo los contextos en que ocurrió una lesión
            List<Contexts> contextsList = contextsFacade.findAll();
            contexts = new SelectItem[contextsList.size()];
            for (int i = 0; i < contextsList.size(); i++) {
                contexts[i] = new SelectItem(contextsList.get(i).getContextName());
            }

            //cargo el genero de el/los agresor/es
            List<AggressorGenders> aggressorGendersList = agreAggressorGendersFacade.findAll();
            aggressorGenders = new SelectItem[aggressorGendersList.size()];
            for (int i = 0; i < aggressorGendersList.size(); i++) {
                aggressorGenders[i] = new SelectItem(aggressorGendersList.get(i).getGenderName());
            }

            //cargo los Factores precipitantes en lesiones autoinflingidas.
            List<PrecipitatingFactors> precipitatingFactorsList = precipitatingFactorsFacade.findAll();
            precipitatingFactors = new SelectItem[precipitatingFactorsList.size()];
            for (int i = 0; i < precipitatingFactorsList.size(); i++) {
                precipitatingFactors[i] = new SelectItem(precipitatingFactorsList.get(i).getPrecipitatingFactorName());
            }


        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
        }
    }

    public void changeNeighborhoodHomeName() {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHomeName) == 0) {
                currentNeighborhoodHomeCode = neighborhoodsList.get(i).getNeighborhoodId().toString();
                break;
            }
        }
    }

    public void changeNeighborhoodEventName() {
        Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEventName);
        if (n != null) {
            currentNeighborhoodEventCode = n.getNeighborhoodId().toString();
        } else {
            currentNeighborhoodEventCode = "";
        }
//        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
//        for (int i = 0; i < neighborhoodsList.size(); i++) {
//            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodEventName) == 0) {
//                currentNeighborhoodEventCode = neighborhoodsList.get(i).getNeighborhoodId().toString();
//                break;
//            }
//        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public String getCurrentHealthInstitution() {
        return currentHealthInstitution;
    }

    public void setCurrentHealthInstitution(String currentHealthInstitution) {
        this.currentHealthInstitution = currentHealthInstitution;
    }

    public SelectItem[] getHealthInstitutions() {
        return healthInstitutions;
    }

    public void setHealthInstitutions(SelectItem[] healthInstitutions) {
        this.healthInstitutions = healthInstitutions;
    }

    public FormsAndFieldsDataMB getFormsAndFieldsDataMB() {
        return formsAndFieldsDataMB;
    }

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

    public String getCurrentMedicalHistory() {
        return currentMedicalHistory;
    }

    public void setCurrentMedicalHistory(String currentMedicalHistory) {
        this.currentMedicalHistory = currentMedicalHistory;
    }

    public String getCurrentIdentification() {
        return currentIdentification;
    }

    public void setCurrentIdentification(String currentIdentification) {
        this.currentIdentification = currentIdentification;
    }

    public SelectItem[] getIdentifications() {
        return identifications;
    }

    public void setIdentifications(SelectItem[] identifications) {
        this.identifications = identifications;
    }

    public String getCurrentMeasureOfAge() {
        return currentMeasureOfAge;
    }

    public void setCurrentMeasureOfAge(String currentMeasureOfAge) {
        this.currentMeasureOfAge = currentMeasureOfAge;
    }

    public SelectItem[] getMeasuresOfAge() {
        return measuresOfAge;
    }

    public void setMeasuresOfAge(SelectItem[] measuresOfAge) {
        this.measuresOfAge = measuresOfAge;
    }

    public String getCurrentGender() {
        return currentGender;
    }

    public void setCurrentGender(String currentGender) {
        this.currentGender = currentGender;
    }

//    public SelectItem[] getGenders() {
//        return genders;
//    }
//
//    public void setGenders(SelectItem[] genders) {
//        this.genders = genders;
//    }
    public String getCurrentDepartamentHome() {
        return currentDepartamentHome;
    }

    public void setCurrentDepartamentHome(String currentDepartamentHome) {
        this.currentDepartamentHome = currentDepartamentHome;
        //cargo el codigo del departamento departamentos
        this.currentDepartamentHomeCode = departamentsFacade.findByName(currentDepartamentHome).getDepartamentId();
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

    public String getCurrentMunicipalitie() {
        return currentMunicipalitie;
    }

    public void setCurrentMunicipalitie(String currentMunicipalitie) {
        this.currentMunicipalitie = currentMunicipalitie;
        this.currentMunicipalitieCode = municipalitiesFacade.findByName(currentMunicipalitie).getMunicipalityId().toString();
    }

    public String getCurrentNeighborhoodEventCode() {
        return currentNeighborhoodEventCode;
    }

    public void setCurrentNeighborhoodEventCode(String currentNeighborhoodEventCode) {
        this.currentNeighborhoodEventCode = currentNeighborhoodEventCode;
    }

    public String getCurrentNeighborhoodEventName() {
        return currentNeighborhoodEventName;
    }

    public void setCurrentNeighborhoodEventName(String currentNeighborhoodEventName) {
        this.currentNeighborhoodEventName = currentNeighborhoodEventName;
    }

    public String getCurrentNeighborhoodHomeCode() {
        return currentNeighborhoodHomeCode;
    }

    public void setCurrentNeighborhoodHomeCode(String currentNeighborhoodHomeCode) {
        this.currentNeighborhoodHomeCode = currentNeighborhoodHomeCode;
    }

    public String getCurrentNeighborhoodHomeName() {
        return currentNeighborhoodHomeName;
    }

    public void setCurrentNeighborhoodHomeName(String currentNeighborhoodHomeName) {
        this.currentNeighborhoodHomeName = currentNeighborhoodHomeName;
        //this.currentNeighborhoodHomeCode=neighborhoodsFacade.findByName(currentNeighborhoodHomeName).getNeighborhoodId().toString();
    }

    public String getCurrentHealthProfessionals() {
        return currentHealthProfessionals;
    }

    public void setCurrentHealthProfessionals(String currentHealthProfessionals) {
        this.currentHealthProfessionals = currentHealthProfessionals;
    }

    public String getCurrentDestinationPatient() {
        return currentDestinationPatient;
    }

    public void setCurrentDestinationPatient(String currentDestinationPatient) {
        this.currentDestinationPatient = currentDestinationPatient;
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

    public String getTxtCIE10_4() {
        return txtCIE10_4;
    }

    public void setTxtCIE10_4(String txtCIE10_4) {
        this.txtCIE10_4 = txtCIE10_4;
    }

    public String getCurrentDepartamentHomeCode() {
        return currentDepartamentHomeCode;
    }

    public String getCurrentMunicipalitieCode() {
        return currentMunicipalitieCode;
    }

    public void setCurrentMunicipalitieCode(String currentMunicipalitieCode) {
        this.currentMunicipalitieCode = currentMunicipalitieCode;
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

    public String getCurrentActivities() {
        return currentActivities;
    }

    public void setCurrentActivities(String currentActivities) {
        this.currentActivities = currentActivities;
    }

    public String getCurrentAggressorGenders() {
        return currentAggressorGenders;
    }

    public void setCurrentAggressorGenders(String currentAggressorGenders) {
        this.currentAggressorGenders = currentAggressorGenders;
    }

    public String getCurrentAmPmConsult() {
        return currentAmPmConsult;
    }

    public void setCurrentAmPmConsult(String currentAmPmConsult) {
        this.currentAmPmConsult = currentAmPmConsult;
    }

    public String getCurrentAmPmEvent() {
        return currentAmPmEvent;
    }

    public void setCurrentAmPmEvent(String currentAmPmEvent) {
        this.currentAmPmEvent = currentAmPmEvent;
    }

    public String getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(String currentContext) {
        this.currentContext = currentContext;
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
    }

    public String getCurrentDayEvent() {
        return currentDayEvent;
    }

    public void setCurrentDayEvent(String currentDayEvent) {
        this.currentDayEvent = currentDayEvent;

        try {
            fechaI = formato.parse(currentDayEvent + "/" + currentMonthEvent + "/" + currentYearEvent);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaI);

            currentDateEvent = "si";
            currentWeekdayEvent = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));

        } catch (ParseException ex) {
            // POR FAVOR CORRIJA LA FECHA DEL PRIMER PAGO
            currentDateEvent = "no";
            currentWeekdayEvent = "no";
        }

    }

    public String getCurrentHourConsult() {
        return currentHourConsult;
    }

    public void setCurrentHourConsult(String currentHourConsult) {
        this.currentHourConsult = currentHourConsult;
    }

    public String getCurrentHourEvent() {
        return currentHourEvent;
    }

    public void setCurrentHourEvent(String currentHourEvent) {
        this.currentHourEvent = currentHourEvent;
    }

    public String getCurrentIntentionality() {
        return currentIntentionality;
    }

    public void setCurrentIntentionality(String currentIntentionality) {
        this.currentIntentionality = currentIntentionality;
    }

    public String getCurrentMechanisms() {
        return currentMechanisms;
    }

    public void setCurrentMechanisms(String currentMechanisms) {
        this.currentMechanisms = currentMechanisms;
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
    }

    public String getCurrentMinuteEvent() {
        return currentMinuteEvent;
    }

    public void setCurrentMinuteEvent(String currentMinuteEvent) {
        this.currentMinuteEvent = currentMinuteEvent;
    }

    public String getCurrentMonthConsult() {
        return currentMonthConsult;
    }

    public void setCurrentMonthConsult(String currentMonthConsult) {
        this.currentMonthConsult = currentMonthConsult;
    }

    public String getCurrentMonthEvent() {
        return currentMonthEvent;
    }

    public void setCurrentMonthEvent(String currentMonthEvent) {
        this.currentMonthEvent = currentMonthEvent;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String currentPlace) {
        this.currentPlace = currentPlace;
    }

    public String getCurrentPrecipitatingFactor() {
        return currentPrecipitatingFactor;
    }

    public void setCurrentPrecipitatingFactor(String currentPrecipitatingFactor) {
        this.currentPrecipitatingFactor = currentPrecipitatingFactor;
    }

    public String getCurrentRelationshipToVictim() {
        return currentRelationshipToVictim;
    }

    public void setCurrentRelationshipToVictim(String currentRelationshipToVictim) {
        this.currentRelationshipToVictim = currentRelationshipToVictim;
    }

    public String getCurrentTransportTypes() {
        return currentTransportTypes;
    }

    public void setCurrentTransportTypes(String currentTransportTypes) {
        this.currentTransportTypes = currentTransportTypes;
    }

    public String getCurrentTransportUser() {
        return currentTransportUser;
    }

    public void setCurrentTransportUser(String currentTransportUser) {
        this.currentTransportUser = currentTransportUser;
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
    }

    public String getCurrentYearEvent() {
        return currentYearEvent;
    }

    public void setCurrentYearEvent(String currentYearEvent) {
        this.currentYearEvent = currentYearEvent;
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

    public String getCurrentTransportTypesCounterpart() {
        return currentTransportTypesCounterpart;
    }

    public void setCurrentTransportTypesCounterpart(String currentTransportTypesCounterpart) {
        this.currentTransportTypesCounterpart = currentTransportTypesCounterpart;
    }
}
