/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsMB")
@SessionScoped
public class RecordSetsMB implements Serializable {

    /**
     * OCUPACIONES
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    TagsFacade tagsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    LoadsFacade loadsFacade;
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
    
    private List<Tags> tagsList;
    private List<Loads> loadsList;
    private Tags currentTag;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private RecordSetsLcenfMB recordSetsLcenfMB;
    private String openRecordSets;
    
    private NonFatalInjuries currentNonFatalInjury;
    private NonFatalDomesticViolence currentNonFatalDomesticViolence;
    private FatalInjuryMurder currentFatalInjuryMurder;
    private FatalInjurySuicide currentFatalInjurySuicide;    
    private FatalInjuryTraffic currentFatalInjuryTraffic;
    private FatalInjuryAccident currentFatalInjuryAccident;

    public RecordSetsMB() {
    }

    public String openRecordSets() {
        return openRecordSets;
    }

    public void selectTagClick() {
        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsLcenfMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsViewMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsViewMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsViewMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsLcenfMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsViewMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else {
            openRecordSets = null;
            printMessage(FacesMessage.SEVERITY_ERROR, "Nada", "nada");
        }
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void load() {
        currentTag = null;
        String currentTagName = "";
        boolean equalTagName = true;
        if (selectedRowsDataTable != null) {
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (currentTagName.length() == 0) {
                    currentTagName = selectedRowsDataTable[0].getColumn3();
                } else if (currentTagName.compareTo(selectedRowsDataTable[0].getColumn3()) != 0) {
                    equalTagName = false;
                    break;
                }
            }
            if (equalTagName) {
                currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
                if (currentTag != null) {
                    if (currentTag.getTagName() != null) {
                        name = currentTag.getTagName();
                    } else {
                        name = "";
                    }
                }
                btnEditDisabled = false;
                btnRemoveDisabled = false;

            } else {
                btnEditDisabled = true;
                btnRemoveDisabled = false;
            }
        }
    }

    private void removeVIF(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {
            currentNonFatalDomesticViolence=nonFatalDomesticViolenceFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
            NonFatalInjuries auxNonFatalInjuries = currentNonFatalDomesticViolence.getNonFatalInjuries();
            Victims auxVictims = currentNonFatalDomesticViolence.getNonFatalInjuries().getVictimId();            
            loadsFacade.remove(loadsList.get(j));
            nonFatalDomesticViolenceFacade.remove(currentNonFatalDomesticViolence);
            nonFatalInjuriesFacade.remove(auxNonFatalInjuries);
            victimsFacade.remove(auxVictims);
        }
    }
    
    private void removeMurder(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {
            currentFatalInjuryMurder = fatalInjuryMurderFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
            FatalInjuries auxFatalInjuries = currentFatalInjuryMurder.getFatalInjuries();
            Victims auxVictims = currentFatalInjuryMurder.getFatalInjuries().getVictimId();
            loadsFacade.remove(loadsList.get(j));
            fatalInjuryMurderFacade.remove(currentFatalInjuryMurder);
            fatalInjuriesFacade.remove(auxFatalInjuries);
            victimsFacade.remove(auxVictims);
        }
    }

    private void removeAccident(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {            
            currentFatalInjuryAccident = fatalInjuryAccidentFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
            loadsFacade.remove(loadsList.get(j));
            FatalInjuries auxFatalInjuries = currentFatalInjuryAccident.getFatalInjuries();
            Victims auxVictims = currentFatalInjuryAccident.getFatalInjuries().getVictimId();
            fatalInjuryAccidentFacade.remove(currentFatalInjuryAccident);
            fatalInjuriesFacade.remove(auxFatalInjuries);
            victimsFacade.remove(auxVictims);
        }
    }

    private void removeSuicide(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {
            
            currentFatalInjurySuicide = fatalInjurySuicideFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
            FatalInjuries auxFatalInjuries = currentFatalInjurySuicide.getFatalInjuries();
            Victims auxVictims = currentFatalInjurySuicide.getFatalInjuries().getVictimId();
            loadsFacade.remove(loadsList.get(j));
            fatalInjurySuicideFacade.remove(currentFatalInjurySuicide);
            fatalInjuriesFacade.remove(auxFatalInjuries);
            victimsFacade.remove(auxVictims);
        }
    }

    private void removeTransit(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {
            currentFatalInjuryTraffic = fatalInjuryTrafficFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
            FatalInjuries auxFatalInjuries = currentFatalInjuryTraffic.getFatalInjuries();
            Victims auxVictims = currentFatalInjuryTraffic.getFatalInjuries().getVictimId();
            loadsFacade.remove(loadsList.get(j));
            fatalInjuryTrafficFacade.remove(currentFatalInjuryTraffic);
            fatalInjuriesFacade.remove(auxFatalInjuries);
            victimsFacade.remove(auxVictims);
        }
    }

    private void removeLCENF(Tags currentTagRemove) {
        loadsList = loadsFacade.findByTagId(currentTagRemove.getTagId().toString());
        for (int j = 0; j < loadsList.size(); j++) {
            currentNonFatalInjury = nonFatalInjuriesFacade.find(loadsList.get(j).getLoadsPK().getRecordId());
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
            loadsFacade.remove(loadsList.get(j));
        }
    }

    public void deleteRegistry() {
        currentTag = null;
        String currentTagName = "";
        boolean equalTagName = true;
        if (selectedRowsDataTable != null) {
            //CREO LA LISTA DE TAGS SELECCIONADOS
            tagsList = new ArrayList<Tags>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                //tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
                currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1()));
                if (currentTag != null) {
                    if (currentTag.getFormId().getFormName().compareTo("SCC-F-028") == 0) {
                        removeMurder(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    } else if (currentTag.getFormId().getFormName().compareTo("SCC-F-029") == 0) {
                        removeTransit(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    } else if (currentTag.getFormId().getFormName().compareTo("SCC-F-030") == 0) {
                        removeSuicide(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    } else if (currentTag.getFormId().getFormName().compareTo("SCC-F-031") == 0) {
                        removeAccident(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    } else if (currentTag.getFormId().getFormName().compareTo("SCC-F-032") == 0) {
                        removeLCENF(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    } else if (currentTag.getFormId().getFormName().compareTo("SCC-F-033") == 0) {
                        removeVIF(currentTag);
                        tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                    }
                }
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El Conjunto fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            selectedRowsDataTable = null;
            currentTag = null;
        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentTag != null) {
            if (name.trim().length() != 0) {
                name = name.toUpperCase();
                currentTag.setTagName(name);
                tagsFacade.edit(currentTag);
                name = "";
                currentTag = null;
                selectedRowsDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void saveRegistry() {
        //determinar consecutivo|
        if (newName.trim().length() != 0) {
//            int max = tagsFacade.findMax() + 1;
//            newName=newName.toUpperCase();
//            Tags newRegistry = new Tags((short) max, newName);
//            tagsFacade.create(newRegistry);
//            newName = "";
//            currentTag=null;
//            selectedRowDataTable=null;
//            createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void newRegistry() {
        name = "";
        newName = "";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            tagsList = tagsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (tagsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
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
    }

    @PostConstruct
    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        tagsList = tagsFacade.findAll();
        for (int i = 0; i < tagsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    tagsList.get(i).getTagId().toString(),
                    tagsList.get(i).getTagName(),
                    tagsList.get(i).getFormId().getFormId(),
                    tagsList.get(i).getFormId().getFormName()));
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
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

    public String getOpenRecordSets() {
        return openRecordSets;
    }

    public void setOpenRecordSets(String openRecordSets) {
        this.openRecordSets = openRecordSets;
    }
}
