/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
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
import managedBeans.duplicateSets.*;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsMB")
@SessionScoped
public class RecordSetsMB implements Serializable {

    @EJB
    TagsFacade tagsFacade;
    @EJB
    UngroupedTagsFacade ungroupedTagsFacade;
    @EJB
    SivigilaEventFacade sivigilaEventFacade;
    @EJB
    SivigilaVictimFacade sivigilaVictimFacade;
    @EJB
    SivigilaAggresorFacade sivigilaAggresorFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    FatalInjurySuicideFacade fatalInjurySuicideFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    VictimsFacade victimsFacade;
    private RowDataTable[] selectedRowsDataTable;
    private RowDataTable selectedRowsDataTable2;
    private List<RowDataTable> rowDataTableList;
    private List<RowDataTable> rowDataTableList2;
    private List<Tags> tagsList;
    private Tags currentTag;
    private UngroupedTags newUngroupedTags;
    private int currentSearchCriteria = 0;
    private int currentSearchCriteria2 = 0;
    private String currentSearchValue = "";
    private String currentSearchValue2 = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private boolean btnViewDisabled = true;
    private boolean btnDuplicateDisabled = true;
    private boolean btnJoinDisabled = true;
    private RecordSetsLcenfMB recordSetsLcenfMB;
    private RecordSetsAccidentalMB recordSetsAccidentalMB;
    private RecordSetsHomicideMB recordSetsHomicideMB;
    private RecordSetsSuicideMB recordSetsSuicideMB;
    private RecordSetsTransitMB recordSetsTransitMB;
    private RecordSetsVifMB recordSetsVifMB;
    private RecordSetsSivigilaVifMB recordSetsSivigilaVifMB;
    private DuplicateSetsLcenfMB duplicateSetsLcenfMB;
    private DuplicateSetsAccidentalMB duplicateSetsAccidentalMB;
    private DuplicateSetsHomicideMB duplicateSetsHomicideMB;
    private DuplicateSetsSuicideMB duplicateSetsSuicideMB;
    private DuplicateSetsTransitMB duplicateSetsTransitMB;
    private DuplicateSetsVifMB duplicateSetsVifMB;
    private DuplicateSetsSIvigilaVifMB duplicateSetsSivigilaVifMB;
    private String openRecordSets;
    private String openDuplicateSets;
    @EJB
    FormsFacade formsFacade;
    private SelectItem[] forms;
    private Short currentForm = 0;
    private String formName = "";
    private String groupName = "";
    private String editGroupName = "";
    private String editFormName = "";
    private int progress = 0;//PROGRESO AL ABRIR CONJUNTOS
    private int progressDelete = 0;//PROGRESO AL ELIMINAR CONJUNTOS
    private int progressSplit = 0;//PROGRESO AL ELIMINAR CONJUNTOS
    private int totalRegisters = 0;
    private int totalProcess = 0;
    private Date initialDateView = new Date();
    private Date endDateView = new Date();
    private Date initialDateDuplicate = new Date();
    private Date endDateDuplicate = new Date();
    ConnectionJdbcMB connectionJdbcMB;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    FacesMessage msg;

    public RecordSetsMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        Calendar c = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        initialDateView.setDate(1);
        initialDateView.setMonth(0);
        initialDateView.setYear(2002 - 1900);
        endDateView.setDate(c.get(Calendar.DATE));
        endDateView.setMonth(c.get(Calendar.MONTH));
        endDateView.setYear(c.get(Calendar.YEAR) - 1900);
        initialDateDuplicate.setDate(1);
        initialDateDuplicate.setMonth(0);
        initialDateDuplicate.setYear(2002 - 1900);
        endDateDuplicate.setDate(c.get(Calendar.DATE));
        endDateDuplicate.setMonth(c.get(Calendar.MONTH));
        endDateDuplicate.setYear(c.get(Calendar.YEAR) - 1900);


    }

    public void onCompleteLoad() {
        progress = 0;
    }

    public void onCompleteDelete() {
        progressDelete = 0;
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCompleteSplit() {
        progressSplit = 0;
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String openRecordSets() {
        return openRecordSets;
    }

    public void detectDuplicateClick() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
            duplicateSetsHomicideMB = (DuplicateSetsHomicideMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsHomicideMB}", DuplicateSetsHomicideMB.class);
            duplicateSetsHomicideMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsHomicideMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsHomicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsHomicide";
            openDuplicateSets = "duplicateSetsHomicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
            duplicateSetsTransitMB = (DuplicateSetsTransitMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsTransitMB}", DuplicateSetsTransitMB.class);
            duplicateSetsTransitMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsTransitMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsTransitMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsTransit";
            openDuplicateSets = "duplicateSetsTransit";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
            duplicateSetsSuicideMB = (DuplicateSetsSuicideMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsSuicideMB}", DuplicateSetsSuicideMB.class);
            duplicateSetsSuicideMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsSuicideMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsSuicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsSuicide";
            openDuplicateSets = "duplicateSetsSuicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
            duplicateSetsAccidentalMB = (DuplicateSetsAccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsAccidentalMB}", DuplicateSetsAccidentalMB.class);
            duplicateSetsAccidentalMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsAccidentalMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsAccidentalMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsAccidental";
            openDuplicateSets = "duplicateSetsAccidental";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
            duplicateSetsLcenfMB = (DuplicateSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsLcenfMB}", DuplicateSetsLcenfMB.class);
            duplicateSetsLcenfMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsLcenfMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
            openDuplicateSets = "duplicateSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
            duplicateSetsVifMB = (DuplicateSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsVifMB}", DuplicateSetsVifMB.class);
            duplicateSetsVifMB.setInitialDateStr(formato.format(initialDateDuplicate));
            duplicateSetsVifMB.setEndDateStr(formato.format(endDateDuplicate));
            duplicateSetsVifMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsVIF";
            openDuplicateSets = "duplicateSetsVIF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SIVIGILA-VIF") == 0) {
            boolean enableControls = true;
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[i].getColumn3().compareTo("SCC-F-033") == 0) {//es tipo vif
                    duplicateSetsVifMB = (DuplicateSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsVifMB}", DuplicateSetsVifMB.class);
                    duplicateSetsVifMB.setInitialDateStr(formato.format(initialDateDuplicate));
                    duplicateSetsVifMB.setEndDateStr(formato.format(endDateDuplicate));
                    duplicateSetsVifMB.loadValues(selectedRowsDataTable);
                    openRecordSets = "recordSetsVIF";
                    openDuplicateSets = "duplicateSetsVIF";
                    enableControls = false;
                    break;
                }
            }
            if (enableControls) {//si solo son sivigila se abre duplicados de sivigila
                duplicateSetsSivigilaVifMB = (DuplicateSetsSIvigilaVifMB) context.getApplication().evaluateExpressionGet(context, "#{duplicateSetsSivigilaVifMB}", DuplicateSetsSIvigilaVifMB.class);
                duplicateSetsSivigilaVifMB.setInitialDateStr(formato.format(initialDateDuplicate));
                duplicateSetsSivigilaVifMB.setEndDateStr(formato.format(endDateDuplicate));
                duplicateSetsSivigilaVifMB.loadValues(selectedRowsDataTable);
                openRecordSets = "recordSetsSivigilaVif";
                openDuplicateSets = "duplicateSetsSivigilaVif";
            }
        } else {
            openRecordSets = null;
            openDuplicateSets = null;
            printMessage(FacesMessage.SEVERITY_ERROR, "Nada", "nada");
        }
        progress = 0;
    }

    public String openDuplicateSets() {
        return openDuplicateSets;
    }

    public void resetDates() {
//        Calendar c = Calendar.getInstance();
//        Calendar c2 = Calendar.getInstance();
//        
//        initialDate.setDate(1);
//        initialDate.setMonth(0);
//        initialDate.setYear(2003-1900);
//        endDate.setDate(c.get(Calendar.DATE));
//        endDate.setMonth(c.get(Calendar.MONTH));
//        endDate.setYear(c.get(Calendar.YEAR)-1900);
    }

    public void selectTagClick() {

        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsHomicideMB = (RecordSetsHomicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsHomicideMB}", RecordSetsHomicideMB.class);
            recordSetsHomicideMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsHomicideMB.setEndDateStr(formato.format(endDateView));
            recordSetsHomicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsHomicide";
            openDuplicateSets = "duplicateSetsHomicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsTransitMB = (RecordSetsTransitMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsTransitMB}", RecordSetsTransitMB.class);
            recordSetsTransitMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsTransitMB.setEndDateStr(formato.format(endDateView));
            recordSetsTransitMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsTransit";
            openDuplicateSets = "duplicateSetsTransit";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsSuicideMB = (RecordSetsSuicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsSuicideMB}", RecordSetsSuicideMB.class);
            recordSetsSuicideMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsSuicideMB.setEndDateStr(formato.format(endDateView));
            recordSetsSuicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsSuicide";
            openDuplicateSets = "duplicateSetsSuicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsAccidentalMB = (RecordSetsAccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsAccidentalMB}", RecordSetsAccidentalMB.class);
            recordSetsAccidentalMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsAccidentalMB.setEndDateStr(formato.format(endDateView));
            recordSetsAccidentalMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsAccidental";
            openDuplicateSets = "duplicateSetsAccidental";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsLcenfMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsLcenfMB.setEndDateStr(formato.format(endDateView));
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
            openDuplicateSets = "duplicateSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
            boolean enableControls = true;
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[i].getColumn3().compareTo("SIVIGILA-VIF") == 0) {//es tipo sivigila
                    enableControls = false;//se encontro uno de tipo vif se carga tipo vif                    
                    break;
                }
            }
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsVifMB = (RecordSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsVifMB}", RecordSetsVifMB.class);
            recordSetsVifMB.setInitialDateStr(formato.format(initialDateView));
            recordSetsVifMB.setEndDateStr(formato.format(endDateView));
            recordSetsVifMB.loadValues(selectedRowsDataTable);
            recordSetsVifMB.setRenderControls(enableControls);//permite o no ver en formulario y eliminar registros
            openRecordSets = "recordSetsVIF";
            openDuplicateSets = "duplicateSetsVIF";

        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SIVIGILA-VIF") == 0) {
            boolean enableControls = true;
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (selectedRowsDataTable[i].getColumn3().compareTo("SCC-F-033") == 0) {//es tipo vif
                    enableControls = false;//se encontro uno de tipo vif se carga tipo vif
                    FacesContext context = FacesContext.getCurrentInstance();
                    recordSetsVifMB = (RecordSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsVifMB}", RecordSetsVifMB.class);
                    recordSetsVifMB.setInitialDateStr(formato.format(initialDateView));
                    recordSetsVifMB.setEndDateStr(formato.format(endDateView));
                    recordSetsVifMB.loadValues(selectedRowsDataTable);
                    recordSetsVifMB.setRenderControls(enableControls);//no permite mostrar en formulario ni eliminar registros
                    openRecordSets = "recordSetsVIF";
                    openDuplicateSets = "duplicateSetsVIF";
                    break;
                }
            }
            if (enableControls) {//sino se encontro uno de vif se carga sivigila
                FacesContext context = FacesContext.getCurrentInstance();
                recordSetsSivigilaVifMB = (RecordSetsSivigilaVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsSivigilaVifMB}", RecordSetsSivigilaVifMB.class);
                recordSetsSivigilaVifMB.setInitialDateStr(formato.format(initialDateView));
                recordSetsSivigilaVifMB.setEndDateStr(formato.format(endDateView));
                recordSetsSivigilaVifMB.loadValues(selectedRowsDataTable);
                openRecordSets = "recordSetsSivigilaVif";
                openDuplicateSets = "duplicateSetsSivigilaVif";
            }
        } else {
            openRecordSets = null;
            openDuplicateSets = null;
            printMessage(FacesMessage.SEVERITY_WARN, "CONSULTA SIN DATOS", "CONSULTA SIN DATOS");
        }
        progress = 0;
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg2 = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg2);
    }

    public void load() {
        currentTag = null;
        String currentTagName = "";
        boolean equalTagName = true;
        boolean sivigilaAndVifTagName = true;
        btnEditDisabled = false;
        btnRemoveDisabled = false;
        btnViewDisabled = true;
        btnJoinDisabled = true;
        btnDuplicateDisabled = true;
        name = "";
        if (selectedRowsDataTable != null) {
            //SI ALGUNO DE LOS SELECCIONADOS ES GENERAL NO REALIZAR ELIMINACION
            for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 7) {
                    btnRemoveDisabled = true;
                    btnEditDisabled = true;
                }
            }
            if (selectedRowsDataTable.length == 1) {//ES SOLO UNA CARGA
                btnDuplicateDisabled = false;//permitir detectar duplicados
                btnViewDisabled = false;//permitir mostrar datos                
                currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
                editFormName = selectedRowsDataTable[0].getColumn3();//nombre cuando se edita
                editGroupName = selectedRowsDataTable[0].getColumn2();//ficha cuando se edita
            } else {//ES MAS DE UNA CARGA                
                //VERIFICAR SI SON DEL MISMO TIPO
                //btnEditDisabled = true;//no permitir edicion de nombre
                for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                    if (currentTagName.length() == 0) {
                        currentTagName = selectedRowsDataTable[0].getColumn3();
                    } else if (currentTagName.compareTo(selectedRowsDataTable[i].getColumn3()) != 0) {
                        equalTagName = false;
                    }
                }
                if (equalTagName) {//SI SON DEL MISMO TIPO                                         
                    btnDuplicateDisabled = false;//permitir detectar duplicados
                    btnViewDisabled = false;//permitir mostrar datos           
                    btnJoinDisabled = false;//permitir unir conjuntos
                    btnViewDisabled = false;
                } else {//SI NO SON DEL MISMO TIPO VERIFICAR SI SON VIF O SIVIGILA LOS SELECCIONADOS
                    for (int i = 0; i < selectedRowsDataTable.length; i++) {//verificar si esta seleccionado uno de las cargas por defecto
                        if (selectedRowsDataTable[i].getColumn4().compareTo("SIVIGILA-VIF") != 0
                                && selectedRowsDataTable[i].getColumn4().compareTo("VIF") != 0) {
                            sivigilaAndVifTagName = false;
                        }
                    }
                    if (sivigilaAndVifTagName) {//los nombres son sivigila o vif
                        btnDuplicateDisabled = false;//permitir detectar duplicados
                        btnViewDisabled = false;//permitir mostrar datos           
                    } else {
                        btnDuplicateDisabled = true;//no permitir detectar duplicados
                        btnViewDisabled = true;//no permitir mostrar datos           
                    }
                }
            }
        }
    }

    private void removeVIF(Tags currentTagRemove) {
        List<NonFatalDomesticViolence> nonFatalDomesticViolenceList = nonFatalDomesticViolenceFacade.findFromTag(currentTagRemove.getTagId());
        if (nonFatalDomesticViolenceList != null) {
            for (int j = 0; j < nonFatalDomesticViolenceList.size(); j++) {
                NonFatalInjuries auxNonFatalInjuries = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries();
                Victims auxVictims = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries().getVictimId();
                nonFatalDomesticViolenceFacade.remove(nonFatalDomesticViolenceList.get(j));
                nonFatalInjuriesFacade.remove(auxNonFatalInjuries);
                victimsFacade.remove(auxVictims);
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeSivigilaVif(Tags currentTagRemove) {
        List<NonFatalInjuries> nonFatalInjuriesList = nonFatalInjuriesFacade.findFromTag(currentTagRemove.getTagId());
        if (nonFatalInjuriesList != null) {
            for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
                NonFatalInjuries auxNonFatalInjury = nonFatalInjuriesList.get(j);
                Victims auxVictim = nonFatalInjuriesList.get(j).getVictimId();
                NonFatalDomesticViolence auxDomesticViolence = nonFatalInjuriesList.get(j).getNonFatalDomesticViolence();
                SivigilaEvent auxSivigilaEvent = auxDomesticViolence.getSivigilaEvent();
                SivigilaVictim auxSivigilaVictim = auxSivigilaEvent.getSivigilaVictimId();
                SivigilaAggresor auxSivigilaAggresor = auxSivigilaEvent.getSivigilaAgresorId();

                sivigilaEventFacade.remove(auxSivigilaEvent);
                nonFatalDomesticViolenceFacade.remove(auxDomesticViolence);
                nonFatalInjuriesFacade.remove(auxNonFatalInjury);
                victimsFacade.remove(auxVictim);

                sivigilaVictimFacade.remove(auxSivigilaVictim);
                sivigilaAggresorFacade.remove(auxSivigilaAggresor);
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeMurder(Tags currentTagRemove) {
        List<FatalInjuryMurder> fatalInjuryMurderList = fatalInjuryMurderFacade.findFromTag(currentTagRemove.getTagId());
        if (fatalInjuryMurderList != null) {
            for (int j = 0; j < fatalInjuryMurderList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjuryMurderList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjuryMurderList.get(j).getFatalInjuries().getVictimId();
                fatalInjuryMurderFacade.remove(fatalInjuryMurderList.get(j));
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                totalProcess++;
                if (totalRegisters != 0) {
                    progressDelete = (int) (totalProcess * 100) / totalRegisters;
                }
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeAccident(Tags currentTagRemove) {
        List<FatalInjuryAccident> fatalInjuryAccidentList = fatalInjuryAccidentFacade.findFromTag(currentTagRemove.getTagId());
        if (fatalInjuryAccidentList != null) {
            for (int j = 0; j < fatalInjuryAccidentList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjuryAccidentList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjuryAccidentList.get(j).getFatalInjuries().getVictimId();
                fatalInjuryAccidentFacade.remove(fatalInjuryAccidentList.get(j));
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                totalProcess++;
                if (totalRegisters != 0) {
                    progressDelete = (int) (totalProcess * 100) / totalRegisters;
                }
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeSuicide(Tags currentTagRemove) {
        List<FatalInjurySuicide> fatalInjurySuicideList = fatalInjurySuicideFacade.findFromTag(currentTagRemove.getTagId());
        if (fatalInjurySuicideList != null) {
            for (int j = 0; j < fatalInjurySuicideList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjurySuicideList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjurySuicideList.get(j).getFatalInjuries().getVictimId();
                fatalInjurySuicideFacade.remove(fatalInjurySuicideList.get(j));
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                totalProcess++;
                if (totalRegisters != 0) {
                    progressDelete = (int) (totalProcess * 100) / totalRegisters;
                }
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeTransit(Tags currentTagRemove) {
        List<FatalInjuryTraffic> fatalInjuryTrafficList = fatalInjuryTrafficFacade.findFromTag(currentTagRemove.getTagId());
        if (fatalInjuryTrafficList != null) {
            for (int j = 0; j < fatalInjuryTrafficList.size(); j++) {
                FatalInjuries auxFatalInjuries = fatalInjuryTrafficList.get(j).getFatalInjuries();
                Victims auxVictims = fatalInjuryTrafficList.get(j).getFatalInjuries().getVictimId();
                fatalInjuryTrafficFacade.remove(fatalInjuryTrafficList.get(j));
                fatalInjuriesFacade.remove(auxFatalInjuries);
                victimsFacade.remove(auxVictims);
                totalProcess++;
                if (totalRegisters != 0) {
                    progressDelete = (int) (totalProcess * 100) / totalRegisters;
                }
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private void removeLCENF(Tags currentTagRemove) {
        List<NonFatalInjuries> nonFatalInjuriesList = nonFatalInjuriesFacade.findFromTag(currentTagRemove.getTagId());
        if (nonFatalInjuriesList != null) {
            for (int j = 0; j < nonFatalInjuriesList.size(); j++) {

                if (nonFatalInjuriesList.get(j).getNonFatalDomesticViolence() != null) {
                    nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesList.get(j).getNonFatalDomesticViolence());
                }
                if (nonFatalInjuriesList.get(j).getNonFatalInterpersonal() != null) {
                    nonFatalInterpersonalFacade.remove(nonFatalInjuriesList.get(j).getNonFatalInterpersonal());
                }
                if (nonFatalInjuriesList.get(j).getNonFatalSelfInflicted() != null) {
                    nonFatalSelfInflictedFacade.remove(nonFatalInjuriesList.get(j).getNonFatalSelfInflicted());
                }
                if (nonFatalInjuriesList.get(j).getNonFatalTransport() != null) {
                    nonFatalTransportFacade.remove(nonFatalInjuriesList.get(j).getNonFatalTransport());
                }
                nonFatalInjuriesFacade.remove(nonFatalInjuriesList.get(j));
                victimsFacade.remove(nonFatalInjuriesList.get(j).getVictimId());
                totalProcess++;
                if (totalRegisters != 0) {
                    progressDelete = (int) (totalProcess * 100) / totalRegisters;
                    System.out.print("PROGRESO ELIMINANDO: " + String.valueOf(progressDelete));
                }
            }
        }
        //tagsFacade.remove(currentTag);
    }

    private String determineTagName(String name) {
        /*
         * determina si el nombre ya existe sino aumentarle 1,2,3...
         * hasta que encuetre uno que no exista
         */
        String nameReturn = "";
        String tagName = name;
        int count = 0;
        boolean sameName;
        List<Tags> tagsList2 = tagsFacade.findAll();
        while (true) {
            sameName = false;
            for (int i = 0; i < tagsList2.size(); i++) {
                if (tagsList2.get(i).getTagName().compareTo(tagName) == 0) {
                    sameName = true;
                    break;
                }
            }
            if (!sameName) {//si el nombre no existe salir
                nameReturn = tagName;
                break;
            } else {
                count++;
                tagName = name + " " + count;
            }
        }
        return nameReturn;
    }

    public void ungroupTags() {
        System.out.print("ENTRANDO EN DESAGRUPACION DE CONJUNTOS");
        //currentTag = null;
        //List<Tags> tagsListAux = new ArrayList<Tags>();
        boolean continueProcess = true;
        //EXISTAN FILAS SELECCIONADAS
        if (selectedRowsDataTable2 == null) {
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben el conjunto a desagrupar de la lista");
        }

        if (continueProcess) {
            try {
                //crear un nuevo registro en tags
                String newTagName = determineTagName(selectedRowsDataTable2.getColumn5());

                connectionJdbcMB.non_query(""
                        + " INSERT INTO tags VALUES("
                        + selectedRowsDataTable2.getColumn4()
                        + ",'" + newTagName + "'"
                        + ",'' "
                        + ",'' "
                        + ",'" + selectedRowsDataTable2.getColumn6() + "')");
                //actualizar las victimas                

                connectionJdbcMB.update(
                        "victims",
                        "tag_id = " + selectedRowsDataTable2.getColumn4(),
                        "first_tag_id = " + selectedRowsDataTable2.getColumn4() + "");

                connectionJdbcMB.update(
                        "ungrouped_tags",
                        "ungrouped_tag_name = '" + newTagName + "'",
                        "ungrouped_tag_id = " + selectedRowsDataTable2.getColumn4() + "");

                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Los conjuntos: \""
                        + selectedRowsDataTable2.getColumn2()
                        + "\" y \"" + newTagName + "\" se han desagrupado");
            } catch (Exception e) {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", e.toString());
            }
            totalProcess = 100;
            selectedRowsDataTable = null;
            selectedRowsDataTable2 = null;
            currentTag = null;
            createDynamicTable();
            createDynamicTable2();
        }
    }

    public void splitTags() {
        System.out.print("ENTRANDO EN UNION DE CONJUNTOS");
        currentTag = null;
        List<Tags> tagsListAux = new ArrayList<Tags>();
        boolean continueProcess = true;
        //EXISTAN FILAS SELECCIONADAS
        if (selectedRowsDataTable == null) {
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar mínimo dos conjuntos a agrupar");
        }
        //DEBE ESCOGER DOS CONJUNTOS
        if (continueProcess && selectedRowsDataTable.length < 2) {
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben seleccionar mínimo dos conjuntos a agrupar");
        }
        if (continueProcess) {

            //CREO LA LISTA DE TAGS SELECCIONADOS
            tagsList = new ArrayList<Tags>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 7) {
                    //si es general lo coloco de primero
                    tagsListAux.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                    for (int j = 0; j < tagsList.size(); j++) {
                        tagsListAux.add(tagsList.get(j));
                    }
                    tagsList = tagsListAux;
                } else {
                    tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                }
            }
            //current tag sera la que permanezca, las otras se unen a current tag
            currentTag = tagsList.get(0);
            tagsList.remove(0);

            for (int i = 0; i < tagsList.size(); i++) {
                //Modifico el tag
                connectionJdbcMB.update(
                        "victims",
                        "tag_id = " + String.valueOf(currentTag.getTagId()) + "",
                        "tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + "");
                //elimino de la tabla tags
                connectionJdbcMB.remove("tags", "tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + "");
            }
            printMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Los conjuntos seleccionados fueron agrupados en uno solo");
            totalProcess = 100;
            selectedRowsDataTable = null;
            selectedRowsDataTable2 = null;
            currentTag = null;
            createDynamicTable();
            createDynamicTable2();
        }
    }

    public void deleteTag() {
        currentTag = null;
        System.out.print("ENTRANDO EN ELIMINAR CONJUNTO");
        if (selectedRowsDataTable != null) {
            //CREO LA LISTA DE TAGS SELECCIONADOS
            boolean defaultSetSelected = false;
            tagsList = new ArrayList<Tags>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 7) {
                    defaultSetSelected = true;
                } else {
                    tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                }
            }
            if (defaultSetSelected) {//SE SELECCIONO UN CONJUNTO POR DEFECTO DEL SISTEMA
                totalProcess = 100;
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Se ha seleccionado un conjunto por defecto del sistema "
                        + "por lo cual no se puede realizar la eliminación");
            } else {
                //DETERMINO EL NUMERO DE REGISTROS 
                totalProcess = 0;
                totalRegisters = 0;
                for (int i = 0; i < tagsList.size(); i++) {
                    if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                        totalRegisters = totalRegisters + fatalInjuryMurderFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-029") == 0) {
                        totalRegisters = totalRegisters + fatalInjuryTrafficFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-030") == 0) {
                        totalRegisters = totalRegisters + fatalInjurySuicideFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                        totalRegisters = totalRegisters + fatalInjuryAccidentFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-032") == 0) {
                        totalRegisters = totalRegisters + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                        totalRegisters = totalRegisters + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SIVIGILA-VIF") == 0) {
                        totalRegisters = totalRegisters + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
                    }
                }

                for (int i = 0; i < tagsList.size(); i++) {
                    //ELIMINACION DE CONJUNTOS AGRUPADOS
                    try {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + " SELECT "
                                + "    tag_id, first_tag_id "
                                + " FROM "
                                + "    victims "
                                + " WHERE "
                                + "	   tag_id != first_tag_id AND "
                                + "	   tag_id = " + tagsList.get(i).getTagId().toString()
                                + " GROUP BY "
                                + "	   tag_id, first_tag_id "
                                + " ORDER BY "
                                + "    tag_id ");
                        while (rs.next()) {
                            connectionJdbcMB.remove("ungrouped_tags", "ungrouped_tag_id = " + rs.getString("first_tag_id"));
                        }
                    } catch (Exception e) {
                    }
                    //ELIMINACION DE REGISTROS
                    if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                        removeMurder(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-029") == 0) {
                        removeTransit(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-030") == 0) {
                        removeSuicide(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                        removeAccident(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-032") == 0) {
                        removeLCENF(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                        removeVIF(tagsList.get(i));
                    } else if (tagsList.get(i).getFormId().getFormId().compareTo("SIVIGILA-VIF") == 0) {
                        removeSivigilaVif(tagsList.get(i));
                    }
                    //ELIMINACION DE CONJUNTOS
//                    try {                        
//                        connectionJdbcMB.remove("tags", "tag_id = " + tagsList.get(i).getTagId().toString());
//                        connectionJdbcMB.remove("ungrouped_tags", "ungrouped_tag_id = " + tagsList.get(i).getTagId().toString());
//                    } catch (Exception e) {
//                    }
                    tagsFacade.remove(tagsList.get(i));
                    ungroupedTagsFacade.remove(ungroupedTagsFacade.find(tagsList.get(i).getTagId()));
                }
                progressDelete = 100;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Los conjuntos seleccionados fueron eliminados");
                totalProcess = 100;
                btnEditDisabled = false;
                btnRemoveDisabled = false;
                selectedRowsDataTable = null;
                currentTag = null;
            }
        }
        createDynamicTable();
        createDynamicTable2();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentTag != null) {
            if (editGroupName.trim().length() != 0) {
                if (tagsFacade.findByName(editGroupName.toUpperCase()) == null) {
                    editGroupName = editGroupName.toUpperCase();
                    currentTag.setTagName(editGroupName);
                    tagsFacade.edit(currentTag);
                    editGroupName = "";
                    currentTag = null;
                    selectedRowsDataTable = null;
                    createDynamicTable();
                    createDynamicTable2();
                    btnEditDisabled = true;
                    btnRemoveDisabled = true;
                    btnViewDisabled = true;
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void saveRegistry() {
        int a = 0;
        a++;
        System.out.print("ENTRANDO EN NUEVO");
        if (groupName.length() != 0) {
            if (tagsFacade.findByName(groupName.toUpperCase()) == null) {
                int maxIdTag = tagsFacade.findMax() + 1;
                Tags newTag = new Tags(maxIdTag);
                newTag.setTagFileInput("-");
                newTag.setTagFileStored("-");
                newTag.setFormId(formsFacade.find(formName));
                newTag.setTagName(groupName);
                tagsFacade.create(newTag);
                createDynamicTable();
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Se ha registrado el nuevo conjunto");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public final void createDynamicTable() {
        selectedRowsDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        btnViewDisabled = true;
        currentSearchValue = currentSearchValue.toUpperCase();
        rowDataTableList = new ArrayList<RowDataTable>();
        if (currentSearchValue.trim().length() == 0) {
            tagsList = tagsFacade.findAll();
        } else {
            tagsList = tagsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
        }
        if (tagsList.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        for (int i = 0; i < tagsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    tagsList.get(i).getTagId().toString(),
                    tagsList.get(i).getTagName(),
                    tagsList.get(i).getFormId().getFormId(),
                    tagsList.get(i).getFormId().getFormName()));
        }
    }

    public final void createDynamicTable2() {
        selectedRowsDataTable2 = null;
        currentSearchValue2 = currentSearchValue2.toUpperCase();
        rowDataTableList2 = new ArrayList<RowDataTable>();
        try {
            ResultSet rs2;
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    tag_id, first_tag_id "
                    + " FROM "
                    + "    victims "
                    + " WHERE "
                    + "	   tag_id != first_tag_id "
                    + " GROUP BY "
                    + "	   tag_id, first_tag_id "
                    + " ORDER BY "
                    + "    tag_id ");
            while (rs.next()) {
                RowDataTable newRowDataTable = new RowDataTable();
                rs2 = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    ungrouped_tag_id, ungrouped_tag_name "
                        + " FROM "
                        + "    ungrouped_tags "
                        + " WHERE "
                        + "    ungrouped_tag_id = " + rs.getString("tag_id"));
                rs2.next();

                newRowDataTable.setColumn1(rs2.getString("ungrouped_tag_id"));
                newRowDataTable.setColumn2(rs2.getString("ungrouped_tag_name"));


                rs2 = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    ungrouped_tag_id, ungrouped_tag_name, ungrouped_tag_date, form_id "
                        + " FROM "
                        + "    ungrouped_tags "
                        + " WHERE "
                        + "    ungrouped_tag_id = " + rs.getString("first_tag_id"));
                rs2.next();
                newRowDataTable.setColumn3(rs2.getString("ungrouped_tag_date"));
                newRowDataTable.setColumn4(rs2.getString("ungrouped_tag_id"));
                newRowDataTable.setColumn5(rs2.getString("ungrouped_tag_name"));
                newRowDataTable.setColumn6(rs2.getString("form_id"));

                if (currentSearchValue2 != null && currentSearchValue2.trim().length() != 0) {
                    if (currentSearchCriteria2 == 2) {
                        if (newRowDataTable.getColumn2().toUpperCase().indexOf(currentSearchValue2.toUpperCase()) != -1) {
                            rowDataTableList2.add(newRowDataTable);
                        }
                    } else {
                        if (newRowDataTable.getColumn5().toUpperCase().indexOf(currentSearchValue2.toUpperCase()) != -1) {
                            rowDataTableList2.add(newRowDataTable);
                        }
                    }
                } else {
                    rowDataTableList2.add(newRowDataTable);
                }
            }
        } catch (Exception e) {
        }
    }

    @PostConstruct
    public void reset() {

        List<Forms> formsList = formsFacade.findAll();
        forms = new SelectItem[formsList.size()];
        for (int i = 0; i < formsList.size(); i++) {
            forms[i] = new SelectItem(formsList.get(i).getFormId(), formsList.get(i).getFormName());
        }
        createDynamicTable();
        createDynamicTable2();
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public List<RowDataTable> getRowDataTableList2() {
        return rowDataTableList2;
    }

    public void setRowDataTableList2(List<RowDataTable> rowDataTableList2) {
        this.rowDataTableList2 = rowDataTableList2;
    }

    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
    }

    public RowDataTable getSelectedRowsDataTable2() {
        return selectedRowsDataTable2;
    }

    public void setSelectedRowsDataTable2(RowDataTable selectedRowsDataTable2) {
        this.selectedRowsDataTable2 = selectedRowsDataTable2;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public boolean isBtnViewDisabled() {
        return btnViewDisabled;
    }

    public void setBtnViewDisabled(boolean btnViewDisabled) {
        this.btnViewDisabled = btnViewDisabled;
    }

    public String getOpenRecordSets() {
        return openRecordSets;
    }

    public void setOpenRecordSets(String openRecordSets) {
        this.openRecordSets = openRecordSets;
    }

    public String getOpenDuplicateSets() {
        return openDuplicateSets;
    }

    public void setOpenDuplicateSets(String openDuplicateSets) {
        this.openDuplicateSets = openDuplicateSets;
    }

    public Short getCurrentForm() {
        return currentForm;
    }

    public SelectItem[] getForms() {
        return forms;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEditFormName() {
        return editFormName;
    }

    public void setEditFormName(String editFormName) {
        this.editFormName = editFormName;
    }

    public String getEditGroupName() {
        return editGroupName;
    }

    public void setEditGroupName(String editGroupName) {
        this.editGroupName = editGroupName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressSplit() {
        return progressSplit;
    }

    public void setProgressSplit(int progressSplit) {
        this.progressSplit = progressSplit;
    }

    public int getProgressDelete() {
        return progressDelete;
    }

    public void setProgressDelete(int progressDelete) {
        this.progressDelete = progressDelete;
    }

    public Date getEndDateView() {
        return endDateView;
    }

    public void setEndDateView(Date endDateView) {
        this.endDateView = endDateView;
    }

    public Date getInitialDateView() {
        return initialDateView;
    }

    public void setInitialDateView(Date initialDateView) {
        this.initialDateView = initialDateView;
    }

    public Date getInitialDateDuplicate() {
        return initialDateDuplicate;
    }

    public void setInitialDateDuplicate(Date initialDateDuplicate) {
        this.initialDateDuplicate = initialDateDuplicate;
    }

    public Date getEndDateDuplicate() {
        return endDateDuplicate;
    }

    public void setEndDateDuplicate(Date endDateDuplicate) {
        this.endDateDuplicate = endDateDuplicate;
    }

    public boolean isBtnDuplicateDisabled() {
        return btnDuplicateDisabled;
    }

    public void setBtnDuplicateDisabled(boolean btnDuplicateDisabled) {
        this.btnDuplicateDisabled = btnDuplicateDisabled;
    }

    public boolean isBtnJoinDisabled() {
        return btnJoinDisabled;
    }

    public void setBtnJoinDisabled(boolean btnJoinDisabled) {
        this.btnJoinDisabled = btnJoinDisabled;
    }

    public int getCurrentSearchCriteria2() {
        return currentSearchCriteria2;
    }

    public void setCurrentSearchCriteria2(int currentSearchCriteria2) {
        this.currentSearchCriteria2 = currentSearchCriteria2;
    }

    public String getCurrentSearchValue2() {
        return currentSearchValue2;
    }

    public void setCurrentSearchValue2(String currentSearchValue2) {
        this.currentSearchValue2 = currentSearchValue2;
    }
}
