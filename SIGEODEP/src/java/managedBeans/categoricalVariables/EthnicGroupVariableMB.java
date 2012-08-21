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
import model.dao.EthnicGroupsFacade;
import model.pojo.EthnicGroups;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "ethnicGroupVariableMB")
@SessionScoped
public class EthnicGroupVariableMB implements Serializable {

    /**
     * GRUPOS ETNICOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    private List<EthnicGroups> ethnicGroupsList;
    private EthnicGroups currentEthnicGroup;
    private String name = "";
    private String newName = "";
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

    public EthnicGroupVariableMB() {
    }

    public void load() {
        currentEthnicGroup = null;
        if (selectedRowDataTable != null) {
            currentEthnicGroup = ethnicGroupsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentEthnicGroup != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentEthnicGroup.getEthnicGroupName() != null) {
                name = currentEthnicGroup.getEthnicGroupName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentEthnicGroup != null) {
            ethnicGroupsFacade.remove(currentEthnicGroup);
            currentEthnicGroup=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentEthnicGroup != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentEthnicGroup.setEthnicGroupName(name);
                ethnicGroupsFacade.edit(currentEthnicGroup);
                name = "";
                currentEthnicGroup=null;                
                selectedRowDataTable=null;
                createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {                
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        
    }

    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0) {
            int max = ethnicGroupsFacade.findMax() + 1;
            newName=newName.toUpperCase();
            EthnicGroups newRegistry = new EthnicGroups((short) max, newName);
            ethnicGroupsFacade.create(newRegistry);
            newName = "";
            currentEthnicGroup=null;
            selectedRowDataTable=null;
            createDynamicTable(); reload(); btnEditDisabled=true; btnRemoveDisabled=true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }        
    }

    public void newRegistry() {
        name = "";
        newName="";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            ethnicGroupsList = ethnicGroupsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (ethnicGroupsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < ethnicGroupsList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        ethnicGroupsList.get(i).getEthnicGroupId().toString(),
                        ethnicGroupsList.get(i).getEthnicGroupName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        ethnicGroupsList = ethnicGroupsFacade.findCriteria(currentSearchCriteria, currentSearchValue);        
        for (int i = 0; i < ethnicGroupsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    ethnicGroupsList.get(i).getEthnicGroupId().toString(),
                    ethnicGroupsList.get(i).getEthnicGroupName()));
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
    
}
