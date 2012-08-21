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
import model.pojo.Neighborhoods;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "neighborhoodsVariableMB")
@SessionScoped
public class NeighborhoodsVariableMB implements Serializable {

    /**
     * BARRIOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private List<Neighborhoods> neighborhoodsList;
    private Neighborhoods currentNeighborhood;
    private String type = "";
    private String neighborhoodName = "";//Nombre del barrio.
    private String neighborhoodId = "";//Código del barrio.
    private String neighborhoodSuburbId = "";//Código de la comuna.
    private String neighborhoodLevel = "";//Estrato socioeconómico del barrio.
    private String neighborhoodType = "";//Tipo de barrio.
    private String newNeighborhoodName = "";//Nombre del barrio.
    private String newNeighborhoodId = "";//Código del barrio.
    private String newNeighborhoodSuburbId = "";//Código de la comuna.
    private String newNeighborhoodLevel = "";//Estrato socioeconómico del barrio.
    private String newNeighborhoodType = "";//Tipo de barrio.
    private boolean newNeighborhoodSuburbIdDisabled = false;
    private boolean neighborhoodSuburbIdDisabled = false;
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    
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

    public NeighborhoodsVariableMB() {
    }

    public void load() {
        currentNeighborhood = null;
        if (selectedRowDataTable != null) {
            currentNeighborhood = neighborhoodsFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentNeighborhood != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentNeighborhood.getNeighborhoodName() != null) {
                neighborhoodName = currentNeighborhood.getNeighborhoodName();
            } else {
                neighborhoodName = "";
            }
            if (currentNeighborhood.getNeighborhoodId() != null) {
                neighborhoodId = currentNeighborhood.getNeighborhoodId().toString();// integer NOT NULL, -- Código del barrio.
            } else {
                neighborhoodId = "";
            }
            if (currentNeighborhood.getNeighborhoodType() != null) {
                neighborhoodType = String.valueOf(currentNeighborhood.getNeighborhoodType());// character(1), -- Tipo de barrio.
                if (neighborhoodType.compareTo("1") == 0) {//ZONA URBANA
                    neighborhoodSuburbIdDisabled = false;
                } else {//ZONA RURAL
                    neighborhoodSuburbIdDisabled = true;
                    neighborhoodSuburbId = "0";
                }
            } else {
                neighborhoodType = "0";
                neighborhoodSuburbIdDisabled = false;
            }
            if (currentNeighborhood.getNeighborhoodLevel() != null) {
                neighborhoodLevel = String.valueOf(currentNeighborhood.getNeighborhoodLevel());// character(1), -- Estrato socioeconómico del barrio.
            } else {
                neighborhoodLevel = "";
            }
            if (currentNeighborhood.getSuburbId() != -1) {
                neighborhoodSuburbId = String.valueOf(currentNeighborhood.getSuburbId());// character(1), -- Tipo de barrio.
            } else {
                neighborhoodSuburbId = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentNeighborhood != null) {
            neighborhoodsFacade.remove(currentNeighborhood);
            currentNeighborhood = null;
            selectedRowDataTable = null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        reload();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        if (currentNeighborhood != null) {
            if (neighborhoodName.trim().length() == 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                neighborhoodName = neighborhoodName.toUpperCase();
                currentNeighborhood.setNeighborhoodName(neighborhoodName);
                //currentNeighborhood.setNeighborhoodId(Integer.parseInt(neighborhoodId));
                currentNeighborhood.setNeighborhoodType(neighborhoodType.charAt(0));
                currentNeighborhood.setSuburbId(Integer.parseInt(neighborhoodSuburbId));
                currentNeighborhood.setNeighborhoodLevel(neighborhoodLevel.charAt(0));

                neighborhoodsFacade.edit(currentNeighborhood);
                neighborhoodName = "";
                currentNeighborhood = null;
                selectedRowDataTable = null;
                createDynamicTable();
                reload();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void saveRegistry() {
        //determinar consecutivo
        if (newNeighborhoodName.trim().length() == 0 || newNeighborhoodId.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE O CODIGO", "Se debe digitar un nombre y un codigo");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            //buscar si el codigo ya esta registrado
            Neighborhoods auxNeighborhood = neighborhoodsFacade.find(Integer.parseInt(newNeighborhoodId));
            if (auxNeighborhood == null) {
                newNeighborhoodName = newNeighborhoodName.toUpperCase();
                Neighborhoods newRegistry = new Neighborhoods();
                newRegistry.setNeighborhoodId(Integer.parseInt(newNeighborhoodId));
                newRegistry.setNeighborhoodName(newNeighborhoodName);
                newRegistry.setNeighborhoodType(newNeighborhoodType.charAt(0));
                newRegistry.setSuburbId(Integer.parseInt(newNeighborhoodSuburbId));
                newRegistry.setNeighborhoodLevel(newNeighborhoodLevel.charAt(0));
                neighborhoodsFacade.create(newRegistry);
                currentNeighborhood = null;
                selectedRowDataTable = null;
                createDynamicTable();
                reload();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El codigo ingresado ya esta siendo usado por otro barrio");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void newRegistry() {
        //se limpia el formulario
        newNeighborhoodType = "1";//Tipo de barrio.
        newNeighborhoodName = "";//Nombre del barrio.
        newNeighborhoodSuburbId = "1";//Código de la comuna.
        newNeighborhoodLevel = "";//Estrato socioeconómico del barrio.   
        newNeighborhoodSuburbId = "0";
        newNeighborhoodLevel = "0";
        newNeighborhoodSuburbIdDisabled = false;
        calculateCode();
        //se determina el codigo
    }

    public void changeType() {//cambia tipo de barrio
        if (neighborhoodType.compareTo("1") == 0) {//ZONA URBANA
            neighborhoodSuburbIdDisabled = false;
        } else {//ZONA RURAL
            neighborhoodSuburbIdDisabled = true;
            neighborhoodSuburbId = "0";
        }
    }

    public void changeNewType() {//cambia nuevo tipo de barrio
        if (newNeighborhoodType.compareTo("1") == 0) {//ZONA URBANA
            newNeighborhoodSuburbIdDisabled = false;
            newNeighborhoodSuburbId = "1";
        } else {//ZONA RURAL
            newNeighborhoodSuburbIdDisabled = true;
            newNeighborhoodSuburbId = "0";
        }
        calculateCode();
    }

    public void calculateCode() {
        int max = neighborhoodsFacade.findMax(newNeighborhoodType, newNeighborhoodSuburbId) + 1;
        newNeighborhoodId = String.valueOf(max);
    }

    public void changeNewCode() {
        try {
            int c = Integer.parseInt(newNeighborhoodId);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "El código debe ser un numero positivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newNeighborhoodId = "";
            }
        } catch (Exception e) {
            if (newNeighborhoodId.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "El código debe ser un valor numerico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newNeighborhoodId = "";
        }
    }

    public void changeCode() {
        try {
            int c = Integer.parseInt(neighborhoodId);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "El código debe ser un numero positivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                neighborhoodId = "";
            }

        } catch (Exception e) {
            if (neighborhoodId.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "El código debe ser un valor numerico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            neighborhoodId = "";
        }
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            neighborhoodsList = neighborhoodsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (neighborhoodsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < neighborhoodsList.size(); i++) {
                if (neighborhoodsList.get(i).getNeighborhoodType() != null) {
                    if (neighborhoodsList.get(i).getNeighborhoodType() == '1') {
                        type = "ZONA URBANA";
                    } else {
                        type = "ZONA RURAL";
                    }
                } else {
                    type = "";
                }
                rowDataTableList.add(new RowDataTable(neighborhoodsList.get(i).getNeighborhoodId().toString(), neighborhoodsList.get(i).getNeighborhoodName(), type));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        neighborhoodsList = neighborhoodsFacade.findAll();
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodType() == '1') {
                type = "ZONA URBANA";
            } else {
                type = "ZONA RURAL";
            }
            rowDataTableList.add(
                    new RowDataTable(
                    neighborhoodsList.get(i).getNeighborhoodId().toString(),
                    neighborhoodsList.get(i).getNeighborhoodName(),
                    type));
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

    public Neighborhoods getCurrentNeighborhood() {
        return currentNeighborhood;
    }

    public void setCurrentNeighborhood(Neighborhoods currentNeighborhood) {
        this.currentNeighborhood = currentNeighborhood;
    }

    public String getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(String neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public String getNeighborhoodLevel() {
        return neighborhoodLevel;
    }

    public void setNeighborhoodLevel(String neighborhoodLevel) {
        this.neighborhoodLevel = neighborhoodLevel;
    }

    public String getNeighborhoodName() {
        return neighborhoodName;
    }

    public void setNeighborhoodName(String neighborhoodName) {
        this.neighborhoodName = neighborhoodName;
    }

    public String getNeighborhoodSuburbId() {
        return neighborhoodSuburbId;
    }

    public void setNeighborhoodSuburbId(String neighborhoodSuburbId) {
        this.neighborhoodSuburbId = neighborhoodSuburbId;
    }

    public String getNeighborhoodType() {
        return neighborhoodType;
    }

    public void setNeighborhoodType(String neighborhoodType) {
        this.neighborhoodType = neighborhoodType;
    }

    public String getNewNeighborhoodId() {
        return newNeighborhoodId;
    }

    public void setNewNeighborhoodId(String newNeighborhoodId) {
        this.newNeighborhoodId = newNeighborhoodId;
    }

    public String getNewNeighborhoodLevel() {
        return newNeighborhoodLevel;
    }

    public void setNewNeighborhoodLevel(String newNeighborhoodLevel) {
        this.newNeighborhoodLevel = newNeighborhoodLevel;
    }

    public String getNewNeighborhoodName() {
        return newNeighborhoodName;
    }

    public void setNewNeighborhoodName(String newNeighborhoodName) {
        this.newNeighborhoodName = newNeighborhoodName;
    }

    public String getNewNeighborhoodSuburbId() {
        return newNeighborhoodSuburbId;
    }

    public void setNewNeighborhoodSuburbId(String newNeighborhoodSuburbId) {
        this.newNeighborhoodSuburbId = newNeighborhoodSuburbId;
    }

    public String getNewNeighborhoodType() {
        return newNeighborhoodType;
    }

    public void setNewNeighborhoodType(String newNeighborhoodType) {
        this.newNeighborhoodType = newNeighborhoodType;
    }

    public boolean isNeighborhoodSuburbIdDisabled() {
        return neighborhoodSuburbIdDisabled;
    }

    public void setNeighborhoodSuburbIdDisabled(boolean neighborhoodSuburbIdDisabled) {
        this.neighborhoodSuburbIdDisabled = neighborhoodSuburbIdDisabled;
    }

    public boolean isNewNeighborhoodSuburbIdDisabled() {
        return newNeighborhoodSuburbIdDisabled;
    }

    public void setNewNeighborhoodSuburbIdDisabled(boolean newNeighborhoodSuburbIdDisabled) {
        this.newNeighborhoodSuburbIdDisabled = newNeighborhoodSuburbIdDisabled;
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
