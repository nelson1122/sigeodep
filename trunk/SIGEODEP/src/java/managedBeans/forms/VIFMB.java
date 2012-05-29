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
@ManagedBean(name = "vifMB")
@SessionScoped
public class VIFMB {

    //---------------------
    @EJB
    DomesticViolenceDataSourcesFacade domesticViolenceDataSourcesFacade;
    private Short currentDomesticViolenceDataSource;
    private SelectItem[] violenceDataSources;
    //------------------------
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    private Short currentVulnerableGroup;
    private SelectItem[] vulnerableGroups;
    private boolean otherVulnerableGroupDisabled = true;
    private String otherVulnerableGroup;
    //--------------------------
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
//    @EJB
//    IntentionalitiesFacade intentionalitiesFacade;
//    private Short currentIntentionality;
//    private SelectItem[] intentionalities;
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
    private String otherEthnicGroup;
    private boolean otherEthnicGroupDisabled = true;
    //--------------------
    @EJB
    GendersFacade gendersFacade;
    private Short currentGender;
    private SelectItem[] genders;
    //--------------------    
    @EJB
    JobsFacade jobsFacade;
    private Short currentJob;
    private SelectItem[] jobs;
    //--------------------
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private String currentNeighborhoodHome;
    private int currentNeighborhoodHomeCode;
    private String currentNeighborhoodEvent;
    private int currentNeighborhoodEventCode;
    boolean neighborhoodHomeNameDisabled = true;
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
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "entro"+String.valueOf(currentEthnicGroup), "entro");
    //FacesContext.getCurrentInstance().addMessage(null, msg);
//    private boolean otherIntentDisabled = true;
    private boolean otherPlaceDisabled = true;
    private boolean otherActivityDisabled = true;
    private boolean otherMechanismDisabled = true;//otro mecanismo  
    private boolean otherAGDisabled = true;
    private String otherAG;
    private boolean otherMADisabled = true;
    private String otherMA;
    private boolean otherActionDisabled = true;
    private String otherAction;
    private String otherMechanism;//otro mecanismo       
    private boolean powderWhichDisabled = true;//cual polvora
    private String powderWhich;//cual polvora
    private boolean disasterWhichDisabled = true;//cual desastre
    private String disasterWhich;//cual desastre
    private boolean heightWhichDisabled = true;//cual altura
    private String heightWhich;//cual altura    
    private String forBurned = "none";//para los quemados
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
    private boolean isAG1;
    private boolean isAG2;
    private boolean isAG3;
    private boolean isAG4;
    private boolean isAG5;
    private boolean isAG6;
    private boolean isAG7;
    private boolean isAG8;
    private boolean isUnknownAG;
    private boolean isAG10;
    private boolean isMA1;
    private boolean isMA2;
    private boolean isMA3;
    private boolean isMA4;
    private boolean isMA5;
    private boolean isMA6;
    private boolean isUnknownMA;
    private boolean isMA8;
    private boolean isAction1;
    private boolean isAction2;
    private boolean isAction3;
    private boolean isAction4;
    private boolean isAction5;
    private boolean isAction6;
    private boolean isAction7;
    private boolean isAction8;
    private boolean isAction9;
    private boolean isAction10;
    private boolean isAction11;
    private boolean isAction12;
    private boolean isUnknownAction;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public VIFMB() {
    }

    public void reset() {
        try {
            //cargo las instituciones receptoras
            List<DomesticViolenceDataSources> violenceDataSourcesList = domesticViolenceDataSourcesFacade.findAll();
            violenceDataSources = new SelectItem[violenceDataSourcesList.size()];
            for (int i = 0; i < violenceDataSourcesList.size(); i++) {
                violenceDataSources[i] = new SelectItem(violenceDataSourcesList.get(i).getDomesticViolenceDataSourcesId(), violenceDataSourcesList.get(i).getDomesticViolenceDataSourcesName());
            }

            //cargo los grupos vulnerables
            List<VulnerableGroups> vulnerableGroupsList = vulnerableGroupsFacade.findAll();
            vulnerableGroups = new SelectItem[vulnerableGroupsList.size()];
            for (int i = 0; i < vulnerableGroupsList.size(); i++) {
                vulnerableGroups[i] = new SelectItem(vulnerableGroupsList.get(i).getVulnerableGroupId(), vulnerableGroupsList.get(i).getVulnerableGroupName());
            }
            //-------------------------------
            //-------------------------------
            //-------------------------------

            //cargo las instituciones de salud e IPS
            List<NonFatalDataSources> sourcesList = nonFatalDataSourcesFacade.findAll();
            IPSs = new SelectItem[sourcesList.size()];
            for (int i = 0; i < sourcesList.size(); i++) {
                IPSs[i] = new SelectItem(sourcesList.get(i).getNonFatalDataSourceName());
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
//            List<Intentionalities> intentionalitiesList = intentionalitiesFacade.findAll();
//            intentionalities = new SelectItem[intentionalitiesList.size()];
//            for (int i = 0; i < intentionalitiesList.size(); i++) {
//                intentionalities[i] = new SelectItem(intentionalitiesList.get(i).getIntentionalityId(), intentionalitiesList.get(i).getIntentionalityName());
//            }

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

    public void changeNeighborhoodHomeName() {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().compareTo(currentNeighborhoodHome) == 0) {
                currentNeighborhoodHomeCode = neighborhoodsList.get(i).getNeighborhoodId();
                break;
            }
        }
    }

//    public void changeIntentionality() {
//        if (currentIntentionality == 8) {//otro 8
//            otherIntentDisabled = false;
//        } else {
//            otherIntentDisabled = true;
//        }
//    }

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

        if (currentEthnicGroup == 3) {//3. otro
            otherEthnicGroupDisabled = false;

        } else {
            otherEthnicGroupDisabled = true;
            otherEthnicGroup = "";
        }
    }

    public void changeVulnerableGroup() {
        if (currentVulnerableGroup == 98) {//98. otro
            otherVulnerableGroupDisabled = false;

        } else {
            otherVulnerableGroupDisabled = true;
            otherVulnerableGroup = "";
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

    public void changeOtherAG() {
        if (isAG8) {
            otherAGDisabled = false;

        } else {
            otherAGDisabled = true;
            otherAG = "";
        }
    }

    public void changeOtherMA() {
        if (isMA8) {
            otherMADisabled = false;

        } else {
            otherMADisabled = true;
            otherMA = "";
        }
    }

    public void changeOtherAction() {
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
                        if (hourInt == 12){//no existe hora 12
                        
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
                        if (hourInt == 12){//no existe hora 12
                        
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

//    public SelectItem[] getIntentionalities() {
//        return intentionalities;
//    }
//
//    public void setIntentionalities(SelectItem[] intentionalities) {
//        this.intentionalities = intentionalities;
//    }

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

//    public boolean isOtherIntentDisabled() {
//        return otherIntentDisabled;
//    }
//
//    public void setOtherIntentDisabled(boolean otherIntentDisabled) {
//        this.otherIntentDisabled = otherIntentDisabled;
//    }

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

//    public Short getCurrentIntentionality() {
//        return currentIntentionality;
//    }
//
//    public void setCurrentIntentionality(Short currentIntentionality) {
//        this.currentIntentionality = currentIntentionality;
//    }

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
}
