/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.categoricalVariables;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.*;
import model.dao.NeighborhoodsFacade;
import model.dao.NonFatalDataSourcesFacade;
import model.pojo.Neighborhoods;
import model.pojo.NonFatalDataSources;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "healtInstitutionsVariableMB")
@SessionScoped
public class HealtInstitutionsVariableMB implements Serializable {

    /**
     * INSTITUCIONES DE SALUD(LCENF)
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    private List<NonFatalDataSources> nonFatalDataSourcesList;
    private NonFatalDataSources currentNonFatalDataSources;
    private String nameInstitution = "";
    private String addressInstitution = "";
    private String neighborhoodInstitution = "";
    private Short typeInstitution = 0;
    private String newNameInstitution = "";
    private String newAddressInstitution = "";
    private String newNeighborhoodInstitution = "";
    private Short newTypeInstitution = 0;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    LcenfMB lcenfMB;
    AccidentalMB accidentalMB;
    HomicideMB homicideMB;
    SuicideMB suicideMB;
    TransitMB transitMB;
    VIFMB vifMB;
    FacesContext context;

    public void reload() {
        context = FacesContext.getCurrentInstance();
        System.out.println("Reiniciando formularios....");
        lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);
        accidentalMB = (AccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{accidentalMB}", AccidentalMB.class);
        homicideMB = (HomicideMB) context.getApplication().evaluateExpressionGet(context, "#{homicideMB}", HomicideMB.class);
        suicideMB = (SuicideMB) context.getApplication().evaluateExpressionGet(context, "#{suicideMB}", SuicideMB.class);
        transitMB = (TransitMB) context.getApplication().evaluateExpressionGet(context, "#{transitMB}", TransitMB.class);
        vifMB = (VIFMB) context.getApplication().evaluateExpressionGet(context, "#{vifMB}", VIFMB.class);
        lcenfMB.reset();
        accidentalMB.reset();
        homicideMB.reset();
        suicideMB.reset();
        transitMB.reset();
        vifMB.reset();
    }

    public HealtInstitutionsVariableMB() {
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

    public void load() {
        currentNonFatalDataSources = null;
        if (selectedRowDataTable != null) {
            currentNonFatalDataSources = nonFatalDataSourcesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentNonFatalDataSources != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentNonFatalDataSources.getNonFatalDataSourceName() != null) {
                nameInstitution = currentNonFatalDataSources.getNonFatalDataSourceName();
            } else {
                nameInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceAddress() != null) {
                addressInstitution = currentNonFatalDataSources.getNonFatalDataSourceAddress();
            } else {
                addressInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceNeighborhoodId() != null) {
                neighborhoodInstitution = neighborhoodsFacade.find(Integer.parseInt(currentNonFatalDataSources.getNonFatalDataSourceNeighborhoodId())).getNeighborhoodName();
            } else {
                neighborhoodInstitution = "";
            }
            if (currentNonFatalDataSources.getNonFatalDataSourceType() != null) {
                typeInstitution = currentNonFatalDataSources.getNonFatalDataSourceType();
            } else {
                typeInstitution = 0;
            }
        }
    }

    public void deleteRegistry() {
        if (currentNonFatalDataSources != null) {
            nonFatalDataSourcesFacade.remove(currentNonFatalDataSources);
            currentNonFatalDataSources = null;
            selectedRowDataTable = null;
            createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    public void updateRegistry() {
        if (currentNonFatalDataSources != null) {
            if (nameInstitution.trim().length() != 0) {
                nameInstitution = nameInstitution.toUpperCase();
                currentNonFatalDataSources.setNonFatalDataSourceName(nameInstitution);
                currentNonFatalDataSources.setNonFatalDataSourceAddress(addressInstitution);
                currentNonFatalDataSources.setNonFatalDataSourceType(typeInstitution);
                try {
                    String a = neighborhoodsFacade.findByName(neighborhoodInstitution).getNeighborhoodId().toString();
                    currentNonFatalDataSources.setNonFatalDataSourceNeighborhoodId(a);
                } catch (Exception e) {
                    currentNonFatalDataSources.setNonFatalDataSourceNeighborhoodId(null);
                }
                nonFatalDataSourcesFacade.edit(currentNonFatalDataSources);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            currentNonFatalDataSources = null;
            selectedRowDataTable = null;
            createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
        }
    }

    public void saveRegistry() {
        if (newNameInstitution.trim().length() != 0) {
            //determinar consecutivo
            int max = nonFatalDataSourcesFacade.findMax() + 1;
            newNameInstitution = newNameInstitution.toUpperCase();
            NonFatalDataSources newRegistry = new NonFatalDataSources((short) max, newNameInstitution);
            newRegistry.setNonFatalDataSourceAddress(newAddressInstitution);
            newRegistry.setNonFatalDataSourceType(newTypeInstitution);
            try {
                String a = neighborhoodsFacade.findByName(newNeighborhoodInstitution).getNeighborhoodId().toString();
                newRegistry.setNonFatalDataSourceNeighborhoodId(a);
            } catch (Exception e) {
                newRegistry.setNonFatalDataSourceNeighborhoodId(null);
            }
            nonFatalDataSourcesFacade.create(newRegistry);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        currentNonFatalDataSources = null;
        selectedRowDataTable = null;
        createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void newRegistry() {
        nameInstitution = "";
        addressInstitution = "";
        typeInstitution = 0;
        neighborhoodInstitution = "";
        newNameInstitution = "";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            nonFatalDataSourcesList = nonFatalDataSourcesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (nonFatalDataSourcesList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < nonFatalDataSourcesList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        nonFatalDataSourcesList.get(i).getNonFatalDataSourceId().toString(),
                        nonFatalDataSourcesList.get(i).getNonFatalDataSourceName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        nonFatalDataSourcesList = nonFatalDataSourcesFacade.findAll();
        for (int i = 0; i < nonFatalDataSourcesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    nonFatalDataSourcesList.get(i).getNonFatalDataSourceId().toString(),
                    nonFatalDataSourcesList.get(i).getNonFatalDataSourceName()));
        }
    }

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

    public String getAddressInstitution() {
        return addressInstitution;
    }

    public void setAddressInstitution(String addressInstitution) {
        this.addressInstitution = addressInstitution;
    }

    public String getNameInstitution() {
        return nameInstitution;
    }

    public void setNameInstitution(String nameInstitution) {
        this.nameInstitution = nameInstitution;
    }

    public String getNeighborhoodInstitution() {
        return neighborhoodInstitution;
    }

    public void setNeighborhoodInstitution(String neighborhoodInstitution) {
        this.neighborhoodInstitution = neighborhoodInstitution;
    }

    public String getNewAddressInstitution() {
        return newAddressInstitution;
    }

    public void setNewAddressInstitution(String newAddressInstitution) {
        this.newAddressInstitution = newAddressInstitution;
    }

    public String getNewNameInstitution() {
        return newNameInstitution;
    }

    public void setNewNameInstitution(String newNameInstitution) {
        this.newNameInstitution = newNameInstitution;
    }

    public String getNewNeighborhoodInstitution() {
        return newNeighborhoodInstitution;
    }

    public void setNewNeighborhoodInstitution(String newNeighborhoodInstitution) {
        this.newNeighborhoodInstitution = newNeighborhoodInstitution;
    }

    public Short getNewTypeInstitution() {
        return newTypeInstitution;
    }

    public void setNewTypeInstitution(Short newTypeInstitution) {
        this.newTypeInstitution = newTypeInstitution;
    }

    public Short getTypeInstitution() {
        return typeInstitution;
    }

    public void setTypeInstitution(Short typeInstitution) {
        this.typeInstitution = typeInstitution;
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
    
}
