/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.CommunesFacade;
import model.dao.NeighborhoodsFacade;
import model.pojo.Communes;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "communesVariableMB")
@SessionScoped
public class CommunesVariableMB implements Serializable {

    /*
     * COMUNAS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 2;
    private String currentSearchValue = "";
    @EJB
    CommunesFacade communesFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private List<Communes> communesList;
    private Communes currentCommune;
    private String type = "";
    private String communeName = "";//Nombre del cuadrante.
    private String communeId = "";//Código del cuadrante.
    private String communePopuation = "0";
    private String communeType = "";//Tipo de barrio (zona)
    private String newCommuneName = "";//Nombre del cuadrante.
    private String newCommuneId = "";//Código del cuadrante.
    private String newCommunePopuation = "0";
    private String newCommuneType = "";//Tipo de barrio (zona)
    private String newNeighborhoodFilter = "";
    private String neighborhoodFilter = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private UploadedFile file;
    private List<String> availableNeighborhoods = new ArrayList<>();
    private List<String> selectedAvailableNeighborhoods = new ArrayList<>();
    private List<String> availableAddNeighborhoods = new ArrayList<>();
    private List<String> selectedAvailableAddNeighborhoods = new ArrayList<>();
    private List<String> newAvailableNeighborhoods = new ArrayList<>();
    private List<String> newSelectedAvailableNeighborhoods = new ArrayList<>();
    private List<String> newAvailableAddNeighborhoods = new ArrayList<>();
    private List<String> newSelectedAvailableAddNeighborhoods = new ArrayList<>();
    ConnectionJdbcMB connectionJdbcMB;

    public CommunesVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
        createCell(cellStyle, row, 0, "CODIGO");
        createCell(cellStyle, row, 1, "NOMBRE");
        communesList = communesFacade.findAll();
        for (int i = 0; i < communesList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, communesList.get(i).getCommuneId().toString());//CODIGO
            createCell(row, 1, communesList.get(i).getCommuneName());//NOMBRE            

        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        /*
         * cargar el archivo de geometria del varrio
         */
        try {
            //file = event.getFile();
            //File file2 = new File(file.getFileName());
        } catch (Exception ex) {
            //System.out.println("Error: populationsMB_1 > " + ex.toString());
        }
    }

    public void addNeighborhoodInNewQuadrantClick() {
        /*
         * adicionar un cuadrante en un nuevo cuadrante
         */
        if (newSelectedAvailableNeighborhoods != null && !newSelectedAvailableNeighborhoods.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableNeighborhoods.size(); i++) {
                //lo adiciono a la lista de agregados
                newAvailableAddNeighborhoods.add(newSelectedAvailableNeighborhoods.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < newAvailableNeighborhoods.size(); j++) {
                    if (newAvailableNeighborhoods.get(j).compareTo(newSelectedAvailableNeighborhoods.get(i)) == 0) {
                        newAvailableNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    public void addNeighborhoodInExistingQuadrantClick() {
        /*
         * adicionar un cuadrante a la lista de agregados, cuando se esta editando un cuadrante existente
         */
        if (selectedAvailableNeighborhoods != null && !selectedAvailableNeighborhoods.isEmpty()) {
            for (int i = 0; i < selectedAvailableNeighborhoods.size(); i++) {
                //lo adiciono a la lista de agregados
                availableAddNeighborhoods.add(selectedAvailableNeighborhoods.get(i));
                //lo elimino de disponibles
                for (int j = 0; j < availableNeighborhoods.size(); j++) {
                    if (availableNeighborhoods.get(j).compareTo(selectedAvailableNeighborhoods.get(i)) == 0) {
                        availableNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddNeighborhoods = new ArrayList<>();
        }
        //quadrantsFilter = "";
        //changeQuadrantsFilter();
    }

    public void removeNeighborhoodInNewQuadrantClick() {
        /*
         * quitar un cuadrante de la lista de agregados, cuando se esta creando un nuevo cuadrante
         */
        if (newSelectedAvailableAddNeighborhoods != null && !newSelectedAvailableAddNeighborhoods.isEmpty()) {
            for (int i = 0; i < newSelectedAvailableAddNeighborhoods.size(); i++) {
                //lo adiciono a la lista de disonibles
                newAvailableNeighborhoods.add(newSelectedAvailableAddNeighborhoods.get(i));
                //lo elimino de agregados
                for (int j = 0; j < newAvailableAddNeighborhoods.size(); j++) {
                    if (newAvailableAddNeighborhoods.get(j).compareTo(newSelectedAvailableAddNeighborhoods.get(i)) == 0) {
                        newAvailableAddNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        }
        //newQuadrantsFilter = "";
        //changeNewQuadrantsFilter();
    }

    public void removeNeighborhoodInExistingQuadrantClick() {
        /*
         * quitar un cuadrante de la lista de agregados, cuando se esta editando un cuadrante existente
         */
        if (selectedAvailableAddNeighborhoods != null && !selectedAvailableAddNeighborhoods.isEmpty()) {
            for (int i = 0; i < selectedAvailableAddNeighborhoods.size(); i++) {
                //lo adiciono a la lista de disonibles
                availableNeighborhoods.add(selectedAvailableAddNeighborhoods.get(i));
                //lo elimino de agregados
                for (int j = 0; j < availableAddNeighborhoods.size(); j++) {
                    if (availableAddNeighborhoods.get(j).compareTo(selectedAvailableAddNeighborhoods.get(i)) == 0) {
                        availableAddNeighborhoods.remove(j);
                        break;
                    }
                }
            }
            selectedAvailableAddNeighborhoods = new ArrayList<>();
        }
        //quadrantsFilter = "";
        //changeQuadrantsFilter();
    }

    public void loadRegistry() {
        /*
         * carga de los datos de un registro cuando se selecciona una fila de 
         * la tabla que muestra los cuadrantes existentes
         */
        currentCommune = null;
        if (selectedRowDataTable != null) {
            currentCommune = communesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentCommune != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentCommune.getCommuneName() != null) {
                communeName = currentCommune.getCommuneName();
            } else {
                communeName = "";
            }
            if (currentCommune.getCommuneId() != null) {
                communeId = currentCommune.getCommuneId().toString();// integer NOT NULL, -- Código del cuadrante.
            } else {
                communeId = "";
            }
            if (currentCommune.getPopulation() != null) {
                communePopuation = String.valueOf(currentCommune.getPopulation());
            } else {
                communePopuation = "0";
            }
            if (currentCommune.getAreaId() != null) {
                communeType = String.valueOf(currentCommune.getAreaId());// character(1), -- Tipo de barrio.
                if (communeType.compareTo("1") == 0) {//ZONA URBANA
                    
                } else {//ZONA RURAL
                    communeType = "2";
                }
            } else {
                communeType = "1";
            }

            //determino los barrios
            availableNeighborhoods = new ArrayList<>();
            selectedAvailableNeighborhoods = new ArrayList<>();
            availableAddNeighborhoods = new ArrayList<>();
            selectedAvailableAddNeighborhoods = new ArrayList<>();
            neighborhoodFilter = "";
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id NOT IN "
                        + " (SELECT neighborhood_id FROM neighborhoods "
                        + " WHERE neighborhood_suburb = " + currentCommune.getCommuneId().toString() + ") ");
                while (rs.next()) {
                    availableNeighborhoods.add(rs.getString("neighborhood_name"));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id IN "
                        + " (SELECT neighborhood_id FROM neighborhoods "
                        + " WHERE neighborhood_suburb = " + currentCommune.getCommuneId().toString() + ") ");
                while (rs.next()) {
                    availableAddNeighborhoods.add(rs.getString("neighborhood_name"));
                }
            } catch (Exception e) {
            }
        }
    }

    public void deleteRegistry() {
        if (currentCommune != null) {
            //se elimina de la tabla cuadrantes 
            connectionJdbcMB.non_query(""
                    + " DELETE FROM neighborhood_quadrant WHERE neighborhood_id = " + currentCommune.getCommuneId() + "; \n"
                    + " DELETE FROM neighborhoods WHERE neighborhood_id = " + currentCommune.getCommuneId() + "; \n");
            if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El sistema esta haciendo uso de este cuadrante por lo cual no puede ser eliminado"));
            } else {
                currentCommune = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro fue eliminado"));
            }
        }
    }

    public void updateRegistry() {
        if (currentCommune != null) {
            boolean continueProcess = true;
            if (communeName.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
                continueProcess = false;
            }
            if (continueProcess) {
                if (communeId.trim().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                    continueProcess = false;
                }
            }
            if (continueProcess) {
                communeName = communeName.toUpperCase();
                try {
                    //buscar si el codigo o cuadrante ya esta registrado
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants "
                            + " WHERE quadrant_name LIKE '" + communeName + "' AND "
                            + " quadrant_name NOT LIKE '" + currentCommune.getCommuneName() + "'");
                    if (rs.next()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un nombre igual"));
                        continueProcess = false;
                    }
                } catch (SQLException ex) {
                }
            }
            if (continueProcess) {
                communeName = communeName.toUpperCase();
                currentCommune.setCommuneName(communeName);
                //currentQuadrant.setQuadrantId(Integer.parseInt(quadrantId));                
                currentCommune.setPopulation(Integer.parseInt(communePopuation));
                communesFacade.edit(currentCommune);

                String sql = "";
                //elimino los barrios de este cuadrante
                connectionJdbcMB.non_query("DELETE FROM neighborhood_quadrant WHERE quadrant_id = " + currentCommune.getCommuneId());
                //se inserta los diferentes barrios que se haya indicado
                if (availableAddNeighborhoods != null && !availableAddNeighborhoods.isEmpty()) {
                    for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_name LIKE '" + availableAddNeighborhoods.get(i) + "'");
                        try {
                            if (rs.next()) {
                                sql = sql
                                        + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                        + rs.getString(1) + ","//id_cuadrante
                                        + communeId + "); \n";//corredor
                            }
                        } catch (SQLException e) {
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + newCommuneId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);
                //reinicio controles
                communeName = "";
                currentCommune = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void saveRegistry() {
        boolean continueProcess = true;

        if (newCommuneName.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
            continueProcess = false;
        }
        if (continueProcess) {
            newCommuneName = newCommuneName.toUpperCase();
            try {
                //buscar si el nombre de cuadrante ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants WHERE quadrant_name LIKE '" + newCommuneName + "'");
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un nombre igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                //buscar si el codigo o cuadrante ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_id = " + newCommuneId);
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un codigo igual"));
                    continueProcess = false;
                }
            } catch (SQLException ex) {
            }
        }
        if (continueProcess) {
            try {
                String sql = "INSERT INTO quadrants VALUES (";
                sql = sql
                        + newCommuneId + ",'"//codigo
                        + newCommuneName + "',"//nombre
                        + newCommunePopuation + ","//poblacion
                        + "null); \n";//geometria
                //se inserta los diferentes cuadrantes que se haya indicado
                if (newAvailableAddNeighborhoods != null && !newAvailableAddNeighborhoods.isEmpty()) {
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_name LIKE '" + newAvailableAddNeighborhoods.get(i) + "'");
                        if (rs.next()) {
                            sql = sql
                                    + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                    + rs.getString(1) + ","//id_barrio
                                    + newCommuneId + "); \n";//id_cuadrante
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + newCommuneId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);
            } catch (Exception e) {
            }

            currentCommune = null;
            selectedRowDataTable = null;
            createDynamicTable();
            newRegistry();//limpiar formulario
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Nuevo cuadrante almacenado.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void newRegistry() {
        //se limpia el formulario        
        newCommuneId = String.valueOf(communesFacade.findMax() + 1);//id del cuadrante.
        newCommuneName = "";//Nombre del cuadrante.                
        newCommunePopuation = "0";
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        changeNewNeighborhoodFilter();//determinar cuadrantes
    }

    public void changeNewPopulation() {
        try {
            int c = Integer.parseInt(newCommunePopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newCommunePopuation = "0";
            }
        } catch (Exception e) {
            if (newCommunePopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newCommunePopuation = "0";
        }
    }

    public void changePopulation() {
        try {
            int c = Integer.parseInt(communePopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                communePopuation = "0";
            }
        } catch (Exception e) {
            if (communePopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            communePopuation = "0";
        }
    }

//    public void changeCode() {
//        try {
//            int c = Integer.parseInt(quadrantId);
//            if (c < 1) {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El código debe ser un numero positivo");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//                quadrantId = "";
//            }
//
//        } catch (Exception e) {
//            if (quadrantId.trim().length() != 0) {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El código debe ser un valor numerico");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//            quadrantId = "";
//        }
//    }
    public void changeNewNeighborhoodFilter() {
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods ORDER BY neighborhood_name ");
            while (rs.next()) {
                if (newNeighborhoodFilter != null && newNeighborhoodFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("neighborhood_name").toUpperCase().indexOf(newNeighborhoodFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                            if (rs.getString("neighborhood_name").compareTo(newAvailableAddNeighborhoods.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            newAvailableNeighborhoods.add(rs.getString("neighborhood_name"));
                        }
                    }
                } else {//no hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        if (rs.getString("neighborhood_name").compareTo(newAvailableAddNeighborhoods.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        newAvailableNeighborhoods.add(rs.getString("neighborhood_name"));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void changeNeighborhoodFilter() {
        availableNeighborhoods = new ArrayList<>();
        selectedAvailableNeighborhoods = new ArrayList<>();
        boolean foundQuadrant;
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods ORDER BY neighborhood_name ");
            while (rs.next()) {
                if (neighborhoodFilter != null && neighborhoodFilter.trim().length() != 0) {//hay cadena a buscar
                    if (rs.getString("neighborhood_name").toUpperCase().indexOf(neighborhoodFilter.toUpperCase()) != -1) {
                        foundQuadrant = false;
                        for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                            if (rs.getString("neighborhood_name").compareTo(availableAddNeighborhoods.get(i)) == 0) {
                                foundQuadrant = true;
                            }
                        }
                        if (!foundQuadrant) {
                            availableNeighborhoods.add(rs.getString("neighborhood_name"));
                        }
                    }
                } else {//no hay cadena a buscar
                    foundQuadrant = false;
                    for (int i = 0; i < availableAddNeighborhoods.size(); i++) {
                        if (rs.getString("neighborhood_name").compareTo(availableAddNeighborhoods.get(i)) == 0) {
                            foundQuadrant = true;
                        }
                    }
                    if (!foundQuadrant) {
                        availableNeighborhoods.add(rs.getString("neighborhood_name"));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void createDynamicTable() {

        if (currentSearchValue == null || currentSearchValue.trim().length() == 0) {
            currentSearchValue = "";
        }
        currentSearchValue = currentSearchValue.toUpperCase();
        rowDataTableList = new ArrayList<>();
        communesList = communesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
        if (communesList != null && communesList.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        for (int i = 0; i < communesList.size(); i++) {
            if (communesList.get(i).getAreaId() != null) {
                if (communesList.get(i).getAreaId() == 1) {
                    type = "ZONA URBANA";
                } else {
                    type = "ZONA RURAL";
                }
            } else {
                type = "";
            }
            rowDataTableList.add(new RowDataTable(communesList.get(i).getCommuneId().toString(), communesList.get(i).getCommuneName(), type));
        }

    }

    public void reset() {
        rowDataTableList = new ArrayList<>();
        createDynamicTable();
        //quadrantsList = quadrantsFacade.findAll();
        newCommunePopuation = "0";
        //cargo los barrios
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
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

    public String getCommuneName() {
        return communeName;
    }

    public void setCommuneName(String communeName) {
        this.communeName = communeName;
    }

    public String getCommuneId() {
        return communeId;
    }

    public void setCommuneId(String communeId) {
        this.communeId = communeId;
    }

    public String getCommunePopuation() {
        return communePopuation;
    }

    public void setCommunePopuation(String communePopuation) {
        this.communePopuation = communePopuation;
    }

    public String getNewCommuneName() {
        return newCommuneName;
    }

    public void setNewCommuneName(String newCommuneName) {
        this.newCommuneName = newCommuneName;
    }

    public String getNewCommuneId() {
        return newCommuneId;
    }

    public void setNewCommuneId(String newCommuneId) {
        this.newCommuneId = newCommuneId;
    }

    public String getNewCommunePopuation() {
        return newCommunePopuation;
    }

    public void setNewCommunePopuation(String newCommunePopuation) {
        this.newCommunePopuation = newCommunePopuation;
    }

    public String getNewNeighborhoodFilter() {
        return newNeighborhoodFilter;
    }

    public void setNewNeighborhoodFilter(String newNeighborhoodFilter) {
        this.newNeighborhoodFilter = newNeighborhoodFilter;
    }

    public String getNeighborhoodFilter() {
        return neighborhoodFilter;
    }

    public void setNeighborhoodFilter(String neighborhoodFilter) {
        this.neighborhoodFilter = neighborhoodFilter;
    }

    public List<String> getAvailableNeighborhoods() {
        return availableNeighborhoods;
    }

    public void setAvailableNeighborhoods(List<String> availableNeighborhoods) {
        this.availableNeighborhoods = availableNeighborhoods;
    }

    public List<String> getSelectedAvailableNeighborhoods() {
        return selectedAvailableNeighborhoods;
    }

    public void setSelectedAvailableNeighborhoods(List<String> selectedAvailableNeighborhoods) {
        this.selectedAvailableNeighborhoods = selectedAvailableNeighborhoods;
    }

    public List<String> getAvailableAddNeighborhoods() {
        return availableAddNeighborhoods;
    }

    public void setAvailableAddNeighborhoods(List<String> availableAddNeighborhoods) {
        this.availableAddNeighborhoods = availableAddNeighborhoods;
    }

    public List<String> getSelectedAvailableAddNeighborhoods() {
        return selectedAvailableAddNeighborhoods;
    }

    public void setSelectedAvailableAddNeighborhoods(List<String> selectedAvailableAddNeighborhoods) {
        this.selectedAvailableAddNeighborhoods = selectedAvailableAddNeighborhoods;
    }

    public List<String> getNewAvailableNeighborhoods() {
        return newAvailableNeighborhoods;
    }

    public void setNewAvailableNeighborhoods(List<String> newAvailableNeighborhoods) {
        this.newAvailableNeighborhoods = newAvailableNeighborhoods;
    }

    public List<String> getNewSelectedAvailableNeighborhoods() {
        return newSelectedAvailableNeighborhoods;
    }

    public void setNewSelectedAvailableNeighborhoods(List<String> newSelectedAvailableNeighborhoods) {
        this.newSelectedAvailableNeighborhoods = newSelectedAvailableNeighborhoods;
    }

    public List<String> getNewAvailableAddNeighborhoods() {
        return newAvailableAddNeighborhoods;
    }

    public void setNewAvailableAddNeighborhoods(List<String> newAvailableAddNeighborhoods) {
        this.newAvailableAddNeighborhoods = newAvailableAddNeighborhoods;
    }

    public List<String> getNewSelectedAvailableAddNeighborhoods() {
        return newSelectedAvailableAddNeighborhoods;
    }

    public void setNewSelectedAvailableAddNeighborhoods(List<String> newSelectedAvailableAddNeighborhoods) {
        this.newSelectedAvailableAddNeighborhoods = newSelectedAvailableAddNeighborhoods;
    }

    public String getCommuneType() {
        return communeType;
    }

    public void setCommuneType(String communeType) {
        this.communeType = communeType;
    }

    public String getNewCommuneType() {
        return newCommuneType;
    }

    public void setNewCommuneType(String newCommuneType) {
        this.newCommuneType = newCommuneType;
    }
}
