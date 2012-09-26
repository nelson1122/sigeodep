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
import javax.faces.model.SelectItem;
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
    private List<RowDataTable> rowDataTableList;
    private List<Tags> tagsList;
    private Tags currentTag;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private boolean btnViewDisabled = true;
    private RecordSetsLcenfMB recordSetsLcenfMB;
    private RecordSetsAccidentalMB recordSetsAccidentalMB;
    private RecordSetsHomicideMB recordSetsHomicideMB;
    private RecordSetsSuicideMB recordSetsSuicideMB;
    private RecordSetsTransitMB recordSetsTransitMB;
    private RecordSetsVifMB recordSetsVifMB;
    private String openRecordSets;
    @EJB
    FormsFacade formsFacade;
    private SelectItem[] forms;
    private Short currentForm = 0;
    private String formName = "";
    private String groupName = "";
    private String editGroupName = "";
    private String editFormName = "";
    
    private int progress=0;

    public RecordSetsMB() {
    }

    public void onCompleteLoad(){
        progress=0;
    }
    
    public String openRecordSets() {
        return openRecordSets;
    }

    public void selectTagClick() {
        if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-028") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsHomicideMB = (RecordSetsHomicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsHomicideMB}", RecordSetsHomicideMB.class);
            recordSetsHomicideMB.loadValues(selectedRowsDataTable);            
            openRecordSets = "recordSetsHomicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-029") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsTransitMB = (RecordSetsTransitMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsTransitMB}", RecordSetsTransitMB.class);
            recordSetsTransitMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsTransit";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-030") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsSuicideMB = (RecordSetsSuicideMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsSuicideMB}", RecordSetsSuicideMB.class);
            recordSetsSuicideMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsSuicide";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-031") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsAccidentalMB = (RecordSetsAccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsAccidentalMB}", RecordSetsAccidentalMB.class);
            recordSetsAccidentalMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsAccidental";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-032") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsLcenfMB = (RecordSetsLcenfMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsLcenfMB}", RecordSetsLcenfMB.class);
            recordSetsLcenfMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsLCENF";
        } else if (selectedRowsDataTable[0].getColumn3().compareTo("SCC-F-033") == 0) {
            FacesContext context = FacesContext.getCurrentInstance();
            recordSetsVifMB = (RecordSetsVifMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsVifMB}", RecordSetsVifMB.class);
            recordSetsVifMB.loadValues(selectedRowsDataTable);
            openRecordSets = "recordSetsVIF";
        } else {
            openRecordSets = null;
            printMessage(FacesMessage.SEVERITY_ERROR, "Nada", "nada");
        }
        progress=0;
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
            btnRemoveDisabled = false;
            if (selectedRowsDataTable.length > 1) {
                btnEditDisabled = true;
            } else {
                btnEditDisabled = false;
                //cargar los datos de edicion
                currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
                editFormName = selectedRowsDataTable[0].getColumn3();
                editGroupName = selectedRowsDataTable[0].getColumn2();
            }
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (currentTagName.length() == 0) {
                    currentTagName = selectedRowsDataTable[0].getColumn3();
                } else if (currentTagName.compareTo(selectedRowsDataTable[i].getColumn3()) != 0) {
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
                btnViewDisabled = false;
            } else {
                btnViewDisabled = true;
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
        tagsFacade.remove(currentTag);
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
            }
        }
        tagsFacade.remove(currentTag);
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
            }
        }
        tagsFacade.remove(currentTag);
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
            }
        }
        tagsFacade.remove(currentTag);
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
            }
        }
        tagsFacade.remove(currentTag);
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
            }
        }
        tagsFacade.remove(currentTag);
    }

    public void deleteRegistry() {
        currentTag = null;
        System.out.print("ENTRANDO EN ELIMINAR");
        if (selectedRowsDataTable != null) {
            //CREO LA LISTA DE TAGS SELECCIONADOS
            boolean defaultSetSelected = false;
            tagsList = new ArrayList<Tags>();
            for (int i = 0; i < selectedRowsDataTable.length; i++) {
                if (Integer.parseInt(selectedRowsDataTable[i].getColumn1()) < 7) {
                    defaultSetSelected = true;
                }
            }
            if (defaultSetSelected) {//SE SELECCIONO UN CONJUNTO POR DEFECTO DEL SISTEMA
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Se ha seleccionado un conjunto por defecto del sistema "
                        + "por lo cual no se puede realizar la eliminación");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                for (int i = 0; i < selectedRowsDataTable.length; i++) {
                    currentTag = tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1()));
                    if (currentTag != null) {
                        if (currentTag.getFormId().getFormId().compareTo("SCC-F-028") == 0) {
                            removeMurder(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        } else if (currentTag.getFormId().getFormId().compareTo("SCC-F-029") == 0) {
                            removeTransit(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        } else if (currentTag.getFormId().getFormId().compareTo("SCC-F-030") == 0) {
                            removeSuicide(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        } else if (currentTag.getFormId().getFormId().compareTo("SCC-F-031") == 0) {
                            removeAccident(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        } else if (currentTag.getFormId().getFormId().compareTo("SCC-F-032") == 0) {
                            removeLCENF(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        } else if (currentTag.getFormId().getFormId().compareTo("SCC-F-033") == 0) {
                            removeVIF(currentTag);
                            tagsFacade.remove(currentTag);//ELIMINO EL CONJUNTO
                        }
                    }
                }
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Los conjuntos seleccionados fueron eliminados");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                btnEditDisabled = false;
                btnRemoveDisabled = false;
                selectedRowsDataTable = null;
                currentTag = null;
            }

        }
        createDynamicTable();
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
                    btnEditDisabled = true;
                    btnRemoveDisabled = true;
                    btnViewDisabled = true;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
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
                reset();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Se ha registrado el nuevo conjunto");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTE", "El nombre ingresado ya esta registrado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void createDynamicTable() {
        selectedRowsDataTable = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        btnViewDisabled = true;
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
        List<Forms> formsList = formsFacade.findAll();
        forms = new SelectItem[formsList.size()];
        for (int i = 0; i < formsList.size(); i++) {
            forms[i] = new SelectItem(formsList.get(i).getFormId(), formsList.get(i).getFormName());
        }

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
}
