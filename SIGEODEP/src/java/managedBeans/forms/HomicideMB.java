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
@ManagedBean(name = "homicideMB")
@SessionScoped
public class HomicideMB {
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // DECLARACION DE VARIABLES --------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    @EJB
    AreasFacade areasFacade;
    private SelectItem[] areas;
    private Short currentArea;
    //-------------------- 
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    private Short currentWeaponType;
    private SelectItem[] weaponTypes;
    //-------------------- 
    @EJB
    DepartamentsFacade departamentsFacade;
    //--------------------    
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private Short currentMunicipalitie = 1;//pasto
    private SelectItem[] municipalities;
    //--------------------
    @EJB
    PlacesFacade placesFacade;
    private Short currentPlace;
    private SelectItem[] places;
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
    private String currentNeighborhoodHome = "";
    private String currentNeighborhoodHomeCode = "";
    private String currentNeighborhoodEvent = "";
    private String currentNeighborhoodEventCode = "";
    boolean neighborhoodHomeNameDisabled = true;
    //------------------
    @EJB
    IdTypesFacade idTypesFacade;
    private SelectItem[] identificationsTypes;
    private Short currentIdentificationType;
    //------------------
    @EJB
    AgeTypesFacade ageTypesFacade;
    private SelectItem[] measuresOfAge;
    private Short currentMeasureOfAge;
    private String currentAge = "";
    private boolean valueAgeDisabled;
    //------------------
    @EJB
    MurderContextsFacade murderContextsFacade;
    private Short currentMurderContext;
    private SelectItem[] murderContexts;
    //------------------
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    //------------------
    private boolean currentAlcoholLevelDisabled = false;
    private boolean isNoDataAlcoholLevel = false;
    private boolean isPendentAlcoholLevel = false;
    private boolean isUnknownAlcoholLevel = false;
    private boolean isNegativeAlcoholLevel = false;
    private boolean isNoDataAlcoholLevelDisabled = false;
    private boolean isPendentAlcoholLevelDisabled = false;
    private boolean isUnknownAlcoholLevelDisabled = false;
    private boolean isNegativeAlcoholLevelDisabled = false;
    private boolean save = true;//variable que me dice si el registro esta guadado o no    
    private String currentDayEvent = "";
    private String currentMonthEvent = "";
    private String currentYearEvent = "";
    private String currentDateEvent = "";
    private String currentWeekdayEvent = "";
    private String currentNarrative = "";
    private String currentAlcoholLevel = "";
    private String currentAmPmEvent = "AM";
    private String currentMilitaryHourEvent = "";
    private String currentName = "";
    private String currentIdentificationNumber = "";
    private String currentCode = "";
    private String currentDirectionEvent = "";
    private String currentSurname = "";
    private String currentNumberVictims;
    private String currentVictimSource = "";
    private String currentPosition = "";
    private int totalRegisters = 0;//cantidad total de registros en homicidios
    private int currentFatalInjuriId = -1;//registro actual 
    private String currentHourEvent;
    private String currentMinuteEvent;
    private FatalInjuryMurder currentFatalInjuryMurder;
    private FatalInjuryMurder auxFatalInjuryMurder;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private Date fechaI;
    private String openDialogFirst = "";
    private String openDialogNext = "";
    private String openDialogLast = "";
    private String openDialogPrevious = "";
    private String openDialogNew = "";
    private String openDialogDelete = "";
    //soundex levestein methaphone
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // FUNCIONES VARIAS ----------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public HomicideMB() {
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

        try {
            currentIdentificationType = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId().getTypeId();
        } catch (Exception e) {
            currentIdentificationType = 0;
        }
        currentIdentificationNumber = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid();
        currentName = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimFirstname();
        currentSurname = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimLastname();
        try {
            currentMeasureOfAge = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId();
        } catch (Exception e) {
            currentMeasureOfAge = 0;
        }
        try {
            currentAge = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge().toString();
        } catch (Exception e) {
            currentAge = "";
        }
        try {
            currentGender = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId().getGenderId();
        } catch (Exception e) {
            currentGender = 0;
        }
        try {
            currentJob = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId().getJobId();
        } catch (Exception e) {
            currentJob = 0;
        }
        currentDirectionEvent = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAddress();
        try {
            currentNeighborhoodHomeCode = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().toString();
            currentNeighborhoodHome = neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId()).getNeighborhoodName();
        } catch (Exception e) {
            currentNeighborhoodHomeCode = "";
            currentNeighborhoodHome = "";
        }
        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        try {
            currentDateEvent = currentFatalInjuryMurder.getFatalInjuries().getInjuryDate().toString();
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentFatalInjuryMurder.getFatalInjuries().getInjuryDate());
            currentDayEvent = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
            currentMonthEvent = String.valueOf(cal.get(Calendar.MONTH));
            currentYearEvent = String.valueOf(cal.get(Calendar.YEAR));
            calculateDate1();
        } catch (Exception e) {
            currentDateEvent = "";
        }
        try {
            currentHourEvent = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getHours());
            currentMinuteEvent = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getMinutes());
            if (Integer.parseInt(currentHourEvent) > 12) {
                currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) - 12);
                currentAmPmEvent = "AM";
            } else {
                currentAmPmEvent = "PM";
            }
            calculateTime1();
        } catch (Exception e) {
            currentHourEvent = "";
            currentMinuteEvent = "";
        }
        currentDirectionEvent = currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress();
        if (currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId() != null) {
            currentNeighborhoodHomeCode = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId());
        } else {
            currentNeighborhoodHomeCode = "";
        }
        try {
            currentPlace = currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId().getPlaceId();
        } catch (Exception e) {
            currentPlace = 0;
        }

        if (currentFatalInjuryMurder.getFatalInjuries().getVictimNumber() != null) {
            currentNumberVictims = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getVictimNumber());
        } else {
            currentNumberVictims = "";
        }
        currentNarrative = currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription();
        currentWeekdayEvent = currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek();
        if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim() != null) {
            isNoDataAlcoholLevelDisabled = false;
            isUnknownAlcoholLevelDisabled = false;
            isPendentAlcoholLevelDisabled = false;
            isNegativeAlcoholLevelDisabled = false;
            currentAlcoholLevelDisabled = false;
            currentAlcoholLevel = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim());
            isNoDataAlcoholLevel = false;
            isUnknownAlcoholLevel = false;
            isPendentAlcoholLevel = false;
            isNegativeAlcoholLevel = false;
        } else {
            try {
                Short level = currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelId();
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
        currentCode = currentFatalInjuryMurder.getFatalInjuries().getCode();
        //------------------------------------------------------------
        //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------
        currentVictimSource = currentFatalInjuryMurder.getVictimPlaceOfOrigin();
        try {
            currentMurderContext = currentFatalInjuryMurder.getMurderContextId().getMurderContextId();
        } catch (Exception e) {
            currentMurderContext = 0;
        }
        try {
            currentWeaponType = currentFatalInjuryMurder.getWeaponTypeId().getWeaponTypeId();
        } catch (Exception e) {
            currentWeaponType = 0;
        }
        try {
            currentArea = currentFatalInjuryMurder.getAreaId().getAreaId();
        } catch (Exception e) {
            currentArea = 0;
        }
    }

    public void determinePosition() {

        totalRegisters = fatalInjuryMurderFacade.count();
        if (currentFatalInjuriId == -1) {
            currentPosition = "new" + "/" + String.valueOf(totalRegisters);
            openDialogDelete = "";//es nuevo no se puede borrar
        } else {
            currentPosition = currentFatalInjuriId + "/" + String.valueOf(totalRegisters);
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
        if (saveForm()) {
            System.out.println("Si se guardo");
            save = true;
            next();
        } else {
            System.out.println("No se guardo");
        }
    }

    public void saveAndGoPrevious() {//guarda cambios si se han realizado y se dirije al anterior
        if (saveForm()) {
            save = true;
            previous();
        }
    }

    public void saveAndGoFirst() {//guarda cambios si se han realizado y se dirije al primero
        if (saveForm()) {
            save = true;
            first();
        }
    }

    public void saveAndGoLast() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveForm()) {
            save = true;
            last();
        }
    }

    public void saveAndGoNew() {//guarda cambios si se han realizado y se dirije al ultimo
        if (saveForm()) {
            save = true;
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
                auxFatalInjuryMurder = fatalInjuryMurderFacade.findNext(currentFatalInjuriId);
                if (auxFatalInjuryMurder != null) {
                    clearForm();
                    currentFatalInjuryMurder = auxFatalInjuryMurder;
                    currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
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
                auxFatalInjuryMurder = fatalInjuryMurderFacade.findPrevious(currentFatalInjuriId);
                if (auxFatalInjuryMurder != null) {
                    clearForm();
                    currentFatalInjuryMurder = auxFatalInjuryMurder;
                    currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
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
            auxFatalInjuryMurder = fatalInjuryMurderFacade.findFirst();
            if (auxFatalInjuryMurder != null) {
                clearForm();
                currentFatalInjuryMurder = auxFatalInjuryMurder;
                currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
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
            auxFatalInjuryMurder = fatalInjuryMurderFacade.findLast();
            if (auxFatalInjuryMurder != null) {
                clearForm();
                currentFatalInjuryMurder = auxFatalInjuryMurder;
                currentFatalInjuriId = currentFatalInjuryMurder.getFatalInjuryId();
                determinePosition();
                loadValues();
            }
        } else {
            System.out.println("No esta guardadado (para poder cargar ultimo registro)");
        }
    }

    public void clearForm() {
        //totalRegisters = fatalInjuryMurderFacade.count();
        //currentPosition = "new" + "/" + String.valueOf(totalRegisters);
        //------------------------------------------------------------
        //REINICIAR VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------	
        currentIdentificationType = 0;
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
        //------------------------------------------------------------
        //REINICIAR VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        currentDateEvent = "";
        currentDayEvent = "";
        currentMonthEvent = "";
        currentHourEvent = "";
        currentMinuteEvent = "";
        currentMilitaryHourEvent = "";
        currentDirectionEvent = "";
        currentNeighborhoodHomeCode = "";
        currentNeighborhoodHome = "";
        currentMunicipalitie=1;
        neighborhoodHomeNameDisabled=false;
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
        currentCode = "";
        //------------------------------------------------------------
        //LIMPIAR PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------
        currentVictimSource = "";
        currentMurderContext = 0;
        currentWeaponType = 0;
        currentArea = 0;
    }

    public void newForm() {
        //currentFatalInjuryMurder = null;
        if (save) {
            System.out.println("Limpiando formulario");            
            clearForm();
            currentFatalInjuriId = -1;
            determinePosition();
        } else {
            System.out.println("No esta guardado (para poder limpiar formulario)");
        }

    }

    public void deleteForm() {
        System.out.println("eliminando registro: '" + openDialogDelete + "'");
    }

    private boolean saveRegistry()
    {
        try {
            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA VICTIMA
            //------------------------------------------------------------
            String validation = "";
            Victims newVictim = new Victims();
            newVictim.setVictimId(victimsFacade.findMax() + 1);
            if (currentIdentificationType != 0) {
                newVictim.setTypeId(idTypesFacade.find(currentIdentificationType));
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
                    validation = validation + "\n * Corregir valor de: Edad Cantidad";
                }
            }
            if (currentGender != 0) {
                newVictim.setGenderId(gendersFacade.find(currentGender));
            }
            if (currentJob != 0) {
                newVictim.setJobId(jobsFacade.find(currentJob));
            }
            //newVictim.setVulnerableGroupId(v);
            //newVictim.setEthnicGroupId(et);
            //newVictim.setVictimTelephone();
            if (currentDirectionEvent.trim().length() != 0) {
                newVictim.setVictimAddress(currentDirectionEvent);
            }
            if (currentNeighborhoodHomeCode.trim().length() != 0) {
                newVictim.setVictimNeighborhoodId(Integer.parseInt(currentNeighborhoodHomeCode));
            }
            //if (currentDateEvent.trim().length() != 0) {
            //    try {
            //newVictim.setVictimDateOfBirth(formato.parse(currentDateEvent));
            //     } catch (Exception e) {
            //        validation = validation + "\n * Corregir valor de: Fecha Evento";
            //    }
            //}

            //newVictim.setEpsId(null);
            //newVictim.setVictimClass();//si victima es nn

            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION DE CAUSA EXTERNA FATAL
            //------------------------------------------------------------
            FatalInjuries newFatalInjurie = new FatalInjuries();
            newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
            newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio


            if (currentDateEvent.trim().length() != 0) {
                try {
                    newFatalInjurie.setInjuryDate(formato.parse(currentDateEvent));
                } catch (Exception e) {
                    validation = validation + "\n Corregir valor de: Fecha Evento";
                }
            }
            if (currentHourEvent.trim().length() != 0 || currentMinuteEvent.trim().length() != 0) {
                try {
                    if (currentAmPmEvent.compareTo("PM") == 0) {
                        currentHourEvent = String.valueOf(Integer.parseInt(currentHourEvent) + 12);
                    }
                    int hourInt = Integer.parseInt(currentHourEvent);
                    int minuteInt = Integer.parseInt(currentMinuteEvent);
                    if (hourInt > 12 && hourInt < 0) {
                        validation = validation + "\n * Corregir la hora del hecho";
                    } else {
                        if (minuteInt > 59 && minuteInt < 0) {
                            validation = validation + "\n * Corregir la hora del hecho";
                        } else {
                            newFatalInjurie.setInjuryTime(new Time(hourInt, minuteInt, 0));
                        }
                    }

                } catch (Exception e) {
                    validation = validation + "\n * Corregir la hora del hecho";
                }
            }
            if (currentDirectionEvent.trim().length() != 0) {
                newFatalInjurie.setInjuryAddress(currentDirectionEvent);
            }
            if (currentNeighborhoodHomeCode.trim().length() != 0) {
                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(currentNeighborhoodHomeCode));
            }
            if (currentPlace != 0) {
                newFatalInjurie.setInjuryPlaceId(placesFacade.find(currentPlace));
            }
            if (currentNumberVictims.trim().length() != 0) {
                try {
                    newFatalInjurie.setVictimNumber(Short.parseShort(currentNumberVictims));
                } catch (Exception e) {
                    validation = validation + "\n * Corregir el numero de victimas";
                }
            }
            if (currentNarrative.trim().length() != 0) {
                newFatalInjurie.setInjuryDescription(currentNarrative);
            }
            try {
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
            } catch (Exception e) {
                System.out.println("*******************************************ERROR_A1: " + e.toString());
            }

            newFatalInjurie.setInputTimestamp(new Date());//momento en que se capturo el registro

            if (currentWeekdayEvent.trim().length() != 0) {
                newFatalInjurie.setInjuryDayOfWeek(currentWeekdayEvent);
            }
            //valores del nivel de alcohol
            if (currentAlcoholLevel.trim().length() != 0) {
                try {
                    newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(currentAlcoholLevel));
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 1));
                } catch (Exception e) {
                    validation = validation + "\n * Corregir el nivel de alcohol de la victima";
                }
            } else {
                if (isNoDataAlcoholLevel) {
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                }
                if (isUnknownAlcoholLevel) {
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                }
                if (isPendentAlcoholLevel) {
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                }
                if (isNegativeAlcoholLevel) {
                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                }
            }
            if (currentCode.trim().length() != 0) {
                newFatalInjurie.setCode(currentCode);
            }

            newFatalInjurie.setVictimId(newVictim);



            //------------------------------------------------------------
            //SE CREA VARIABLE PARA LA NUEVA LESION FATAL POR HOMICIDIOS
            //------------------------------------------------------------
            FatalInjuryMurder newMurder = new FatalInjuryMurder();
            if (currentVictimSource.trim().length() != 0) {
                newMurder.setVictimPlaceOfOrigin(currentVictimSource);
            }
            if (currentMurderContext != 0) {
                newMurder.setMurderContextId(murderContextsFacade.find(currentMurderContext));
            }
            if (currentWeaponType != 0) {
                newMurder.setWeaponTypeId(weaponTypesFacade.find(currentWeaponType));
            }
            if (currentArea != 0) {
                newMurder.setAreaId(areasFacade.find(currentArea));
            }

            newMurder.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());


            if (validation.length() == 0) {
                victimsFacade.create(newVictim);//se persiste
                fatalInjuriesFacade.create(newFatalInjurie);//se persiste
                fatalInjuryMurderFacade.create(newMurder);
                //victimsFacade.
                save = true;
                newForm();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "REGISTRO ALMACENADO");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return true;
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validacion", validation);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }
    }
    
    public boolean updateRegistry()
    {
        return true;
    }
    
    public boolean saveForm() {

        System.out.println("guardando registro");
        openDialogFirst = "";
        openDialogNext = "";
        openDialogLast = "";
        openDialogPrevious = "";
        openDialogNew = "";
        openDialogDelete = "";
        //determino si es nuevo o se esta modificando
        if(currentFatalInjuriId==-1)//es un nuevo registro
        {
            return saveRegistry();
        }
        else//se esta actualizando un registro
        {
            return updateRegistry();
        }
        
    }

    public void reset() {
        try {
            //totalRegisters = fatalInjuryMurderFacade.count();
            //currentFatalInjuriId = -1;
            //currentPosition = "new/" + String.valueOf(totalRegisters);
            determinePosition();
            //cargo los tipos de identificacion
            List<IdTypes> idTypesList = idTypesFacade.findAll();
            identificationsTypes = new SelectItem[idTypesList.size() + 1];
            identificationsTypes[0] = new SelectItem(0, "");
            for (int i = 0; i < idTypesList.size(); i++) {
                identificationsTypes[i + 1] = new SelectItem(idTypesList.get(i).getTypeId(), idTypesList.get(i).getTypeName());
            }

            //cargo las medidas de edad
            List<AgeTypes> ageTypesList = ageTypesFacade.findAll();
            measuresOfAge = new SelectItem[ageTypesList.size() + 1];
            measuresOfAge[0] = new SelectItem(0, "");
            for (int i = 0; i < ageTypesList.size(); i++) {
                measuresOfAge[i + 1] = new SelectItem(ageTypesList.get(i).getAgeTypeId(), ageTypesList.get(i).getAgeTypeName());
            }
            //contexto relacionado con el hecho
            List<MurderContexts> murderContextsList = murderContextsFacade.findAll();
            murderContexts = new SelectItem[murderContextsList.size() + 1];
            murderContexts[0] = new SelectItem(0, "");
            for (int i = 0; i < murderContextsList.size(); i++) {
                murderContexts[i + 1] = new SelectItem(murderContextsList.get(i).getMurderContextId(), murderContextsList.get(i).getMurderContextName());
            }
            //cargo los municipios
            findMunicipalities();

            //clase de lugares donde ocurrieron los hechos
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

            //cargo los tipos de armas
            List<WeaponTypes> weaponTypesList = weaponTypesFacade.findAll();
            weaponTypes = new SelectItem[weaponTypesList.size() + 1];
            weaponTypes[0] = new SelectItem(0, "");
            for (int i = 0; i < weaponTypesList.size(); i++) {
                weaponTypes[i + 1] = new SelectItem(weaponTypesList.get(i).getWeaponTypeId(), weaponTypesList.get(i).getWeaponTypeName());
            }

        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
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
        Departaments d = departamentsFacade.findById((short) 52);
        municipalities = new SelectItem[d.getMunicipalitiesList().size()];

        for (int i = 0; i < municipalities.length; i++) {
            municipalities[i] = new SelectItem(d.getMunicipalitiesList().get(i).getMunicipalitiesPK().getMunicipalityId(), d.getMunicipalitiesList().get(i).getMunicipalityName());
        }
        currentMunicipalitie = d.getMunicipalitiesList().get(0).getMunicipalitiesPK().getMunicipalityId();


        if (currentMunicipalitie == 1) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
        }
    }

    public void findMunicipalitieCode() {
        changeForm();
        Municipalities m = municipalitiesFacade.findById(currentMunicipalitie, (short) 52);
        if (currentMunicipalitie == 1) {
            neighborhoodHomeNameDisabled = false;
        } else {
            neighborhoodHomeNameDisabled = true;
            currentNeighborhoodHome = "";
            currentNeighborhoodHomeCode = "";
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

    public void changeNeighborhoodEvent() {
        changeForm();
        Neighborhoods n = neighborhoodsFacade.findByName(currentNeighborhoodEvent);
        if (n != null) {
            currentNeighborhoodEventCode = String.valueOf(n.getNeighborhoodId());
        } else {
            currentNeighborhoodEventCode = "";
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
        }
        try {
            minuteInt = Integer.parseInt(currentMinuteEvent);
        } catch (Exception ex) {
            continuar = false;
        }
        if (continuar) {
            try {
                if (currentAmPmEvent.length() != 0) {
                    String hourStr;
                    String minuteStr;
                    String timeStr;
                    if (hourInt > 0 && hourInt < 13 && minuteInt > -1 && minuteInt < 60) {
                        if (currentAmPmEvent.compareTo("PM") == 0) {//hora PM
                            if (hourInt == 12) {//no existe hora 12
                                currentMilitaryHourEvent = "";
                                continuar = false;
                            }
                            if (continuar) {
                                hourInt = hourInt + 12;
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
        }
        return continuar;
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    // GET Y SET DE VARIABLES ----------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public Short getCurrentIdentificationType() {
        return currentIdentificationType;
    }

    public void setCurrentIdentificationType(Short currentIdentificationType) {
        this.currentIdentificationType = currentIdentificationType;
    }

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

    public SelectItem[] getJobs() {
        return jobs;
    }

    public boolean isNeighborhoodHomeNameDisabled() {
        return neighborhoodHomeNameDisabled;
    }

    public void setNeighborhoodHomeNameDisabled(boolean neighborhoodHomeNameDisabled) {
        this.neighborhoodHomeNameDisabled = neighborhoodHomeNameDisabled;
    }

    public Short getCurrentGender() {
        return currentGender;
    }

    public void setCurrentGender(Short currentGender) {
        this.currentGender = currentGender;
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

    public Short getCurrentMurderContext() {
        return currentMurderContext;
    }

    public void setCurrentMurderContext(Short currentMurderContext) {
        this.currentMurderContext = currentMurderContext;
    }

    public Short getCurrentWeaponType() {
        return currentWeaponType;
    }

    public void setCurrentWeaponType(Short currentWeaponType) {
        this.currentWeaponType = currentWeaponType;
    }

    public SelectItem[] getMurderContexts() {
        return murderContexts;
    }

    public void setMurderContexts(SelectItem[] murderContexts) {
        this.murderContexts = murderContexts;
    }

    public SelectItem[] getWeaponTypes() {
        return weaponTypes;
    }

    public void setWeaponTypes(SelectItem[] weaponTypes) {
        this.weaponTypes = weaponTypes;
    }

    public int getTotalRegisters() {
        return totalRegisters;
    }

    public void setTotalRegisters(int totalRegisters) {
        this.totalRegisters = totalRegisters;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
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
}
