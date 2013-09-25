/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import model.dao.NeighborhoodsFacade;
import model.dao.QuadrantsFacade;
import model.pojo.Quadrants;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "quadrantsVariableMB")
@SessionScoped
public class QuadrantsVariableMB implements Serializable {

    /*
     * CUADRANTES
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 2;
    private String currentSearchValue = "";
    @EJB
    QuadrantsFacade quadrantsFacade;    
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    private List<Quadrants> quadrantsList;
    //private SelectItem[] stationsList;
    private SelectItem[] newStationsList;
    private Quadrants currentQuadrant;
    private String type = "";
    private String quadrantName = "";//Nombre del cuadrante.
    private String quadrantId = "";//Código del cuadrante.
    private String quadrantPopuation = "0";
    private String newQuadrantName = "";//Nombre del cuadrante.
    private String newQuadrantId = "";//Código del cuadrante.
    private String newQuadrantPopuation = "0";
    private String newNeighborhoodFilter = "";
    private String neighborhoodFilter = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    
    private String newPoligonText = "";//poligono para el nuevo barrio
    private boolean disabledShowGeomFile = true;//activar/desactivar boton de ver archivo KML
    private String newNameGeomFile = "Archivo no cargado";//nombre del archivo de geometria para nuevo barrio
    private String newGeomText = "Geometría no cargada";//nombre del archivo de geometria para nuevo barrio
    private String nameGeomFile = "";//nombre del archivo de geometria para barrio existente
    
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
    private String realPath = "";

    public QuadrantsVariableMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/"); 
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
        quadrantsList = quadrantsFacade.findAll();
        for (int i = 0; i < quadrantsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, quadrantsList.get(i).getQuadrantId().toString());//CODIGO
            createCell(row, 1, quadrantsList.get(i).getQuadrantName());//NOMBRE            

        }
    }

    private void copyFile(String fileName, InputStream in) {


        disabledShowGeomFile = true;
        newNameGeomFile = "Archivo no cargado";
        try {
            java.io.File folder = new java.io.File(realPath + "web/configurations/maps");
            if (folder.exists()) {//verificar que el directorio exista
                StringBuilder nameAndPathFile = new StringBuilder();
                nameAndPathFile.append(realPath);
                nameAndPathFile.append("web/configurations/maps/");
                nameAndPathFile.append(fileName);
                newNameGeomFile = fileName;//ruta que se usa en java script
                disabledShowGeomFile = false;
                java.io.File ficherofile = new java.io.File(nameAndPathFile.toString());
                if (ficherofile.exists()) {//Probamos a ver si existe ese ultimo dato                    
                    ficherofile.delete();//Lo Borramos
                }
                try (OutputStream out = new FileOutputStream(new File(nameAndPathFile.toString()))) {
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = in.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    in.close();
                    out.flush();
                    System.out.println("El fichero de geometria copiado con exito: " + nameAndPathFile.toString());
                } catch (IOException e) {
                    System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
                }
            } else {
                System.out.println("No se encuentra la carpeta");
            }
        } catch (Exception e) {
            System.out.println("No se pudo procesar el archivo");
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        /*
         * cargar el archivo de geometria del varrio
         */
        newNameGeomFile = "";//nombre del archivo de geometria para nuevo barrio
        try {
            //realizo la copia de este archivo a la carpeta correspondiente a geometrias
            file = event.getFile();
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            //newNameGeomFile = event.getFile().getFileName();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la carga de este archivo"));
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
        currentQuadrant = null;
        if (selectedRowDataTable != null) {
            currentQuadrant = quadrantsFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentQuadrant != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentQuadrant.getQuadrantName() != null) {
                quadrantName = currentQuadrant.getQuadrantName();
            } else {
                quadrantName = "";
            }
            if (currentQuadrant.getQuadrantId() != null) {
                quadrantId = currentQuadrant.getQuadrantId().toString();// integer NOT NULL, -- Código del cuadrante.
            } else {
                quadrantId = "";
            }
            if (currentQuadrant.getPopulation() != null) {
                quadrantPopuation = String.valueOf(currentQuadrant.getPopulation());
            } else {
                quadrantPopuation = "0";
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
                        + " (SELECT neighborhood_id FROM neighborhood_quadrant "
                        + " WHERE quadrant_id = " + currentQuadrant.getQuadrantId().toString() + ") ");
                while (rs.next()) {
                    availableNeighborhoods.add(rs.getString("neighborhood_name"));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT * FROM neighborhoods WHERE neighborhood_id IN "
                        + " (SELECT neighborhood_id FROM neighborhood_quadrant "
                        + " WHERE quadrant_id = " + currentQuadrant.getQuadrantId().toString() + ") ");
                while (rs.next()) {
                    availableAddNeighborhoods.add(rs.getString("neighborhood_name"));
                }
            } catch (Exception e) {
            }
        }
    }
    
    public void showGeomFileClick() {
        newPoligonText = "";
    }

    public void loadGeometrySelected() {
        if (newPoligonText != null && newPoligonText.trim().length() != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "la geometria ha sido cargada"));
            newGeomText = "Geometria cargada";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se ha seleccionado ninguna geometria"));
        }
    }

    public void deleteRegistry() {
        if (currentQuadrant != null) {
            //se elimina de la tabla cuadrantes 
            connectionJdbcMB.non_query(""
                    + " DELETE FROM neighborhood_quadrant WHERE neighborhood_id = " + currentQuadrant.getQuadrantId() + "; \n"
                    + " DELETE FROM neighborhoods WHERE neighborhood_id = " + currentQuadrant.getQuadrantId() + "; \n");
            if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El sistema esta haciendo uso de este cuadrante por lo cual no puede ser eliminado"));
            } else {
                currentQuadrant = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El registro fue eliminado"));
            }
        }
    }

    public void updateRegistry() {
        if (currentQuadrant != null) {
            boolean continueProcess = true;
            if (quadrantName.trim().length() == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
                continueProcess = false;
            }
            if (continueProcess) {
                if (quadrantId.trim().length() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un codigo"));
                    continueProcess = false;
                }
            }
            if (continueProcess) {
                quadrantName = quadrantName.toUpperCase();
                try {
                    //buscar si el codigo o cuadrante ya esta registrado
                    ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants "
                            + " WHERE quadrant_name LIKE '" + quadrantName + "' AND "
                            + " quadrant_name NOT LIKE '" + currentQuadrant.getQuadrantName() + "'");
                    if (rs.next()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe un cuadrante con un nombre igual"));
                        continueProcess = false;
                    }
                } catch (SQLException ex) {
                }
            }
            if (continueProcess) {
                quadrantName = quadrantName.toUpperCase();
                currentQuadrant.setQuadrantName(quadrantName);
                //currentQuadrant.setQuadrantId(Integer.parseInt(quadrantId));                
                currentQuadrant.setPopulation(Integer.parseInt(quadrantPopuation));
                quadrantsFacade.edit(currentQuadrant);

                String sql = "";
                //elimino los barrios de este cuadrante
                connectionJdbcMB.non_query("DELETE FROM neighborhood_quadrant WHERE quadrant_id = " + currentQuadrant.getQuadrantId());
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
                                        + quadrantId + "); \n";//corredor
                            }
                        } catch (SQLException e) {
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + newQuadrantId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);
                //reinicio controles
                quadrantName = "";
                currentQuadrant = null;
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

        if (newQuadrantName.trim().length() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre"));
            continueProcess = false;
        }
        if (continueProcess) {
            newQuadrantName = newQuadrantName.toUpperCase();
            try {
                //buscar si el nombre de cuadrante ya esta registrado
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM quadrants WHERE quadrant_name LIKE '" + newQuadrantName + "'");
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
                ResultSet rs = connectionJdbcMB.consult("SELECT * FROM neighborhoods WHERE neighborhood_id = " + newQuadrantId);
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
                String geom="null";
                if(newPoligonText!=null&&newPoligonText.trim().length()!=0){
                    geom="'"+newPoligonText+"'";
                }
                sql = sql
                        + newQuadrantId + ",'"//codigo
                        + newQuadrantName + "',"//nombre
                        + newQuadrantPopuation + ","//poblacion
                        + geom + "); \n";//geometria
                //se inserta los diferentes cuadrantes que se haya indicado
                if (newAvailableAddNeighborhoods != null && !newAvailableAddNeighborhoods.isEmpty()) {
                    for (int i = 0; i < newAvailableAddNeighborhoods.size(); i++) {
                        ResultSet rs = connectionJdbcMB.consult(""
                                + "SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_name LIKE '" + newAvailableAddNeighborhoods.get(i) + "'");
                        if (rs.next()) {
                            sql = sql
                                    + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                                    + rs.getString(1) + ","//id_barrio
                                    + newQuadrantId + "); \n";//id_cuadrante
                        }
                    }
                } else {//se agrega sin dato si no se ha seleccionado algun barrio
                    sql = sql
                            + "INSERT INTO neighborhood_quadrant VALUES ("//codigo
                            + "52001,"//id_barrio
                            + newQuadrantId + "); \n";//id_cuadrante
                }
                connectionJdbcMB.non_query(sql);
            } catch (Exception e) {
            }

            currentQuadrant = null;
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
        newQuadrantId = String.valueOf(quadrantsFacade.findMax() + 1);//id del cuadrante.
        newQuadrantName = "";//Nombre del cuadrante.                
        newQuadrantPopuation = "0";
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        changeNewNeighborhoodFilter();//determinar cuadrantes
    }

    public void changeNewPopulation() {
        try {
            int c = Integer.parseInt(newQuadrantPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "VALOR NO ACEPTADO", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                newQuadrantPopuation = "0";
            }
        } catch (Exception e) {
            if (newQuadrantPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            newQuadrantPopuation = "0";
        }
    }

    public void changePopulation() {
        try {
            int c = Integer.parseInt(quadrantPopuation);
            if (c < 1) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un número mayor o igual a cero");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                quadrantPopuation = "0";
            }
        } catch (Exception e) {
            if (quadrantPopuation.trim().length() != 0) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La población debe ser un valor numérico");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            quadrantPopuation = "0";
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
        quadrantsList = quadrantsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
        if (quadrantsList.isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        for (int i = 0; i < quadrantsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(quadrantsList.get(i).getQuadrantId().toString(), quadrantsList.get(i).getQuadrantName(), type));
        }

    }

    public void reset() {
        rowDataTableList = new ArrayList<>();
        createDynamicTable();
        //quadrantsList = quadrantsFacade.findAll();
        newQuadrantPopuation = "0";

//        for (int i = 0; i < quadrantsList.size(); i++) {
//            rowDataTableList.add(
//                    new RowDataTable(
//                    quadrantsList.get(i).getQuadrantId().toString(),
//                    quadrantsList.get(i).getQuadrantName(),
//                    type));
//        }
        //cargo los barrios
        newAvailableNeighborhoods = new ArrayList<>();
        newSelectedAvailableNeighborhoods = new ArrayList<>();
        newAvailableAddNeighborhoods = new ArrayList<>();
        newSelectedAvailableAddNeighborhoods = new ArrayList<>();
        
        newPoligonText = "";
        disabledShowGeomFile = true;
        newNameGeomFile = "Archivo no cargado";//nombre del archivo de geometria para nuevo barrio
        newGeomText = "Geometría no cargada";//nombre del archivo de geometria para nuevo barrio
        nameGeomFile = "";//nombre del archivo de geometria para barrio existente
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
//
//    public String getQuadrantId() {
//        return neighborhoodId;
//    }
//
//    public void setNeighborhoodId(String neighborhoodId) {
//        this.neighborhoodId = neighborhoodId;
//    }
//
//    public String getNeighborhoodLevel() {
//        return neighborhoodLevel;
//    }
//
//    public void setNeighborhoodLevel(String neighborhoodLevel) {
//        this.neighborhoodLevel = neighborhoodLevel;
//    }
//
//    public String getQuadrantName() {
//        return neighborhoodName;
//    }
//
//    public void setNeighborhoodName(String neighborhoodName) {
//        this.neighborhoodName = neighborhoodName;
//    }
//
//    public String getNeighborhoodSuburbId() {
//        return neighborhoodSuburbId;
//    }
//
//    public void setNeighborhoodSuburbId(String neighborhoodSuburbId) {
//        this.neighborhoodSuburbId = neighborhoodSuburbId;
//    }
//
//    public String getNewNeighborhoodId() {
//        return newNeighborhoodId;
//    }
//
//    public void setNewNeighborhoodId(String newNeighborhoodId) {
//        this.newNeighborhoodId = newNeighborhoodId;
//    }
//
//    public String getNewNeighborhoodLevel() {
//        return newNeighborhoodLevel;
//    }
//
//    public void setNewNeighborhoodLevel(String newNeighborhoodLevel) {
//        this.newNeighborhoodLevel = newNeighborhoodLevel;
//    }
//
//    public String getNewNeighborhoodName() {
//        return newNeighborhoodName;
//    }
//
//    public void setNewNeighborhoodName(String newNeighborhoodName) {
//        this.newNeighborhoodName = newNeighborhoodName;
//    }
//
//    public String getNewNeighborhoodSuburbId() {
//        return newNeighborhoodSuburbId;
//    }
//
//    public void setNewNeighborhoodSuburbId(String newNeighborhoodSuburbId) {
//        this.newNeighborhoodSuburbId = newNeighborhoodSuburbId;
//    }
//
//    public String getNewNeighborhoodType() {
//        return newNeighborhoodType;
//    }
//
//    public void setNewNeighborhoodType(String newNeighborhoodType) {
//        this.newNeighborhoodType = newNeighborhoodType;
//    }
//
//    public String getNeighborhoodType() {
//        return neighborhoodType;
//    }
//
//    public void setNeighborhoodType(String neighborhoodType) {
//        this.neighborhoodType = neighborhoodType;
//    }
//

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

    //
    //    public SelectItem[] getCommunesList() {
    //        return communesList;
    //    }
    //
    //    public void setCommunesList(SelectItem[] communesList) {
    //        this.communesList = communesList;
    //    }
    //
    //    public SelectItem[] getNewCommunesList() {
    //        return newCommunesList;
    //    }
    //
    //    public void setNewCommunesList(SelectItem[] newCommunesList) {
    //        this.newCommunesList = newCommunesList;
    //    }
    //
    //    public SelectItem[] getCorridorsList() {
    //        return corridorsList;
    //    }
    //
    //    public void setCorridorsList(SelectItem[] corridorsList) {
    //        this.corridorsList = corridorsList;
    //    }
    //
    //    public List<String> getAvailableQuadrants() {
    //        return availableQuadrants;
    //    }
    //
    //    public void setAvailableQuadrants(List<String> availableQuadrants) {
    //        this.availableQuadrants = availableQuadrants;
    //    }
    //
    //    public List<String> getSelectedAvailableQuadrants() {
    //        return selectedAvailableQuadrants;
    //    }
    //
    //    public void setSelectedAvailableQuadrants(List<String> selectedAvailableQuadrants) {
    //        this.selectedAvailableQuadrants = selectedAvailableQuadrants;
    //    }
    //
    //    public List<String> getAvailableAddQuadrants() {
    //        return availableAddQuadrants;
    //    }
    //
    //    public void setAvailableAddQuadrants(List<String> availableAddQuadrants) {
    //        this.availableAddQuadrants = availableAddQuadrants;
    //    }
    //
    //    public List<String> getSelectedAvailableAddQuadrants() {
    //        return selectedAvailableAddQuadrants;
    //    }
    //
    //    public void setSelectedAvailableAddQuadrants(List<String> selectedAvailableAddQuadrants) {
    //        this.selectedAvailableAddQuadrants = selectedAvailableAddQuadrants;
    //    }
    //
    //    public List<String> getNewAvailableQuadrants() {
    //        return newAvailableQuadrants;
    //    }
    //
    //    public void setNewAvailableQuadrants(List<String> newAvailableQuadrants) {
    //        this.newAvailableQuadrants = newAvailableQuadrants;
    //    }
    //
    //    public List<String> getNewSelectedAvailableQuadrants() {
    //        return newSelectedAvailableQuadrants;
    //    }
    //
    //    public void setNewSelectedAvailableQuadrants(List<String> newSelectedAvailableQuadrants) {
    //        this.newSelectedAvailableQuadrants = newSelectedAvailableQuadrants;
    //    }
    //
    //    public List<String> getNewAvailableAddQuadrants() {
    //        return newAvailableAddQuadrants;
    //    }
    //
    //    public void setNewAvailableAddQuadrants(List<String> newAvailableAddQuadrants) {
    //        this.newAvailableAddQuadrants = newAvailableAddQuadrants;
    //    }
    //
    //    public List<String> getNewSelectedAvailableAddQuadrants() {
    //        return newSelectedAvailableAddQuadrants;
    //    }
    //
    //    public void setNewSelectedAvailableAddQuadrants(List<String> newSelectedAvailableAddQuadrants) {
    //        this.newSelectedAvailableAddQuadrants = newSelectedAvailableAddQuadrants;
    //    }
    //
    //    public String getNewPopuation() {
    //        return newPopuation;
    //    }
    //
    //    public void setNewPopuation(String newPopuation) {
    //        this.newPopuation = newPopuation;
    //    }
    //
    //    public String getNewNeighborhoodCorridor() {
    //        return newNeighborhoodCorridor;
    //    }
    //
    //    public void setNewNeighborhoodCorridor(String newNeighborhoodCorridor) {
    //        this.newNeighborhoodCorridor = newNeighborhoodCorridor;
    //    }
    //
    //    public String getNeighborhoodCorridor() {
    //        return neighborhoodCorridor;
    //    }
    //
    //    public void setNeighborhoodCorridor(String neighborhoodCorridor) {
    //        this.neighborhoodCorridor = neighborhoodCorridor;
    //    }
    //
    //    public String getPopuation() {
    //        return popuation;
    //    }
    //
    //    public void setPopuation(String popuation) {
    //        this.popuation = popuation;
    //    }
    //
    //    public String getNewQuadrantsFilter() {
    //        return newQuadrantsFilter;
    //    }
    //
    //    public void setNewQuadrantsFilter(String newQuadrantsFilter) {
    //        this.newQuadrantsFilter = newQuadrantsFilter;
    //    }
    //
    //    public String getQuadrantsFilter() {
    //        return quadrantsFilter;
    //    }
    //
    //    public void setQuadrantsFilter(String quadrantsFilter) {
    //    }
    //    }
    public QuadrantsFacade getQuadrantsFacade() {
        return quadrantsFacade;
    }

    public void setQuadrantsFacade(QuadrantsFacade quadrantsFacade) {
        this.quadrantsFacade = quadrantsFacade;
    }

    public List<Quadrants> getQuadrantsList() {
        return quadrantsList;
    }

    public void setQuadrantsList(List<Quadrants> quadrantsList) {
        this.quadrantsList = quadrantsList;
    }

    public SelectItem[] getNewStationsList() {
        return newStationsList;
    }

    public void setNewStationsList(SelectItem[] newStationsList) {
        this.newStationsList = newStationsList;
    }

    public String getQuadrantName() {
        return quadrantName;
    }

    public void setQuadrantName(String quadrantName) {
        this.quadrantName = quadrantName;
    }

    public String getQuadrantId() {
        return quadrantId;
    }

    public void setQuadrantId(String quadrantId) {
        this.quadrantId = quadrantId;
    }

    public String getQuadrantPopuation() {
        return quadrantPopuation;
    }

    public void setQuadrantPopuation(String quadrantPopuation) {
        this.quadrantPopuation = quadrantPopuation;
    }

    public String getNewQuadrantName() {
        return newQuadrantName;
    }

    public void setNewQuadrantName(String newQuadrantName) {
        this.newQuadrantName = newQuadrantName;
    }

    public String getNewQuadrantId() {
        return newQuadrantId;
    }

    public void setNewQuadrantId(String newQuadrantId) {
        this.newQuadrantId = newQuadrantId;
    }

    public String getNewQuadrantPopuation() {
        return newQuadrantPopuation;
    }

    public void setNewQuadrantPopuation(String newQuadrantPopuation) {
        this.newQuadrantPopuation = newQuadrantPopuation;
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

    public List<String> getNewSelectedAvailableNeighborhoods() {
        return newSelectedAvailableNeighborhoods;
    }

    public void setNewSelectedAvailableNeighborhoods(List<String> newSelectedAvailableNeighborhoods) {
        this.newSelectedAvailableNeighborhoods = newSelectedAvailableNeighborhoods;
    }

    public List<String> getNewSelectedAvailableAddNeighborhoods() {
        return newSelectedAvailableAddNeighborhoods;
    }

    public void setNewSelectedAvailableAddNeighborhoods(List<String> newSelectedAvailableAddNeighborhoods) {
        this.newSelectedAvailableAddNeighborhoods = newSelectedAvailableAddNeighborhoods;
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

    public List<String> getNewAvailableNeighborhoods() {
        return newAvailableNeighborhoods;
    }

    public void setNewAvailableNeighborhoods(List<String> newAvailableNeighborhoods) {
        this.newAvailableNeighborhoods = newAvailableNeighborhoods;
    }

    public List<String> getNewAvailableAddNeighborhoods() {
        return newAvailableAddNeighborhoods;
    }

    public void setNewAvailableAddNeighborhoods(List<String> newAvailableAddNeighborhoods) {
        this.newAvailableAddNeighborhoods = newAvailableAddNeighborhoods;
    }
    
    public String getNewNameGeomFile() {
        return newNameGeomFile;
    }

    public void setNewNameGeomFile(String newNameGeomFile) {
        this.newNameGeomFile = newNameGeomFile;
    }

    public String getNameGeomFile() {
        return nameGeomFile;
    }

    public void setNameGeomFile(String nameGeomFile) {
        this.nameGeomFile = nameGeomFile;
    }

    public boolean isDisabledShowGeomFile() {
        return disabledShowGeomFile;
    }

    public void setDisabledShowGeomFile(boolean disabledShowGeomFile) {
        this.disabledShowGeomFile = disabledShowGeomFile;
    }

    public String getNewGeomText() {
        return newGeomText;
    }

    public void setNewGeomText(String newGeomText) {
        this.newGeomText = newGeomText;
    }

    public String getNewPoligonText() {
        return newPoligonText;
    }

    public void setNewPoligonText(String newPoligonText) {
        this.newPoligonText = newPoligonText;
    }
}
