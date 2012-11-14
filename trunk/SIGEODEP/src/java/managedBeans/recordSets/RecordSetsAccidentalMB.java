/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.FormsEnum;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.AccidentalMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsAccidentalMB")
@SessionScoped
public class RecordSetsAccidentalMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjuryAccident currentFatalInjuryAccident;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    CountriesFacade countriesFacade;
    private LazyDataModel<RowDataTable> table_model;
    //private List<RowDataTable> rowDataTableList;
    //private RowDataTable selectedRowDataTable;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private AccidentalMB accidentalMB;
    private String openForm = "";
    private RecordSetsMB recordSetsMB;
    private ConnectionJdbcMB connection;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String sqlTags = "";

    public RecordSetsAccidentalMB() {
        tagsList = new ArrayList<Tags>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_031);
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String openForm() {
        return openForm;
    }

    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        accidentalMB = (AccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{accidentalMB}", AccidentalMB.class);
        accidentalMB.loadValues(tagsList, currentFatalInjuryAccident);
        openForm = "accidental";
    }

    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        //CREO LA LISTA DE TAGS SELECCIONADOS        
        tagsList = new ArrayList<Tags>();
        data = "";
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            if (i == 0) {
                data = data + " " + selectedRowsDataTableTags[i].getColumn2() + "  ";
            } else {
                data = data + " || " + selectedRowsDataTableTags[i].getColumn2();
            }
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
        }
        //DETERMINO TOTAL DE REGISTROS
        int rowCountAux = 0;
        sqlTags = "";
        for (int i = 0; i < tagsList.size(); i++) {
            rowCountAux = rowCountAux + fatalInjuryAccidentFacade.countAccidental(tagsList.get(i).getTagId());
            if (i != tagsList.size() - 1) {
                sqlTags = sqlTags + " tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " AND ";
            } else {
                sqlTags = sqlTags + " tag_id = " + String.valueOf(tagsList.get(i).getTagId());
            }
        }
        System.out.println("Total de registros = " + String.valueOf(rowCountAux));
        table_model = new LazyRecordSetsDataModel(rowCountAux, sqlTags, FormsEnum.SCC_F_031);
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
        try {
            int rowPosition = 0;
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);

            row = sheet.createRow(rowPosition);// Se crea una fila dentro de la hoja        

            createCell(cellStyle, row, 0, "CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
            createCell(cellStyle, row, 1, "CODIGO");//"100">#{rowX.column23}</p:column>                                
            createCell(cellStyle, row, 2, "DIA HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 3, "MES HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 4, "AÑO HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 5, "FECHA HECHO");//"100">#{rowX.column13}</p:column>
            createCell(cellStyle, row, 6, "DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
            createCell(cellStyle, row, 7, "HORA HECHO");//"100">#{rowX.column14}</p:column>
            createCell(cellStyle, row, 8, "DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
            createCell(cellStyle, row, 9, "BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(cellStyle, row, 10, "COMUNA BARRIO HECHO");//"250">#{rowX.column16}</p:column>

            createCell(cellStyle, row, 11, "AREA HECHO");//"100">#{rowX.column24}</p:column>
            createCell(cellStyle, row, 12, "CLASE DE LUGAR");//"250">#{rowX.column17}</p:column>
            createCell(cellStyle, row, 13, "NUMERO VICTIMAS EN HECHO");//"100">#{rowX.column18}</p:column>
            createCell(cellStyle, row, 14, "NUMERO LESIONADOS EN ECHO");//"250">#{rowX.column28}</p:column>        
            createCell(cellStyle, row, 15, "NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
            createCell(cellStyle, row, 16, "SEXO");//"100">#{rowX.column8}</p:column> 
            createCell(cellStyle, row, 17, "TIPO EDAD");//"100">#{rowX.column6}</p:column>
            createCell(cellStyle, row, 18, "EDAD");//"100">#{rowX.column7}</p:column> 
            createCell(cellStyle, row, 19, "OCUPACION");//"100">#{rowX.column9}</p:column> 
            createCell(cellStyle, row, 20, "TIPO IDENTIFICACION");//"200">#{rowX.column2}</p:column>
            createCell(cellStyle, row, 21, "IDENTIFICACION");//"100">#{rowX.column3}</p:column>
            createCell(cellStyle, row, 22, "EXTRANJERO");//"100">#{rowX.column5}</p:column>
            createCell(cellStyle, row, 23, "DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
            createCell(cellStyle, row, 24, "MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
            createCell(cellStyle, row, 25, "BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>                                
            createCell(cellStyle, row, 26, "COMUNA BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>                                

            createCell(cellStyle, row, 27, "PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
            createCell(cellStyle, row, 28, "DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
            createCell(cellStyle, row, 29, "MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
            createCell(cellStyle, row, 30, "ARMA O CAUSA DE MUERTE");//"200">#{rowX.column29}</p:column>                                    
            createCell(cellStyle, row, 31, "NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>                                
            createCell(cellStyle, row, 32, "NIVEL DE ALCOHOL");//"100">#{rowX.column21}</p:column>
            createCell(cellStyle, row, 33, "TIPO NIVEL DE ALCOHOL");//"100">#{rowX.column22}</p:column>

            ResultSet resultSet = connection.consult(""
                    + " SELECT "
                    + "    victim_id"
                    + " FROM "
                    + "    victims "
                    + " WHERE "
                    + sqlTags);
            String[] splitDate;

            while (resultSet.next()) {

                RowDataTable rowDataTableList = connection.loadFatalInjuryAccidentRecord(resultSet.getString(1));
                rowPosition++;
                row = sheet.createRow(rowPosition);
                createCell(row, 0, rowDataTableList.getColumn1());//CODIGO INTERNO"
                createCell(row, 1, rowDataTableList.getColumn23());//CODIGO"
                if (rowDataTableList.getColumn13() != null) {
                    splitDate = rowDataTableList.getColumn13().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 2, splitDate[0]);//"DIA HECHO");
                        createCell(row, 3, splitDate[1]);//"MES HECHO");
                        createCell(row, 4, splitDate[2]);//"AÑO HECHO");
                    }
                }
                createCell(row, 5, rowDataTableList.getColumn13());//FECHA HECHO"
                createCell(row, 6, rowDataTableList.getColumn20());//DIA EN SEMANA"
                createCell(row, 7, rowDataTableList.getColumn14());//HORA HECHO"
                createCell(row, 8, rowDataTableList.getColumn15());//DIRECCION HECHO"
                createCell(row, 9, rowDataTableList.getColumn16());//BARRIO HECHO"
                createCell(row, 10, rowDataTableList.getColumn30());//COMUNA BARRIO HECHO"

                createCell(row, 11, rowDataTableList.getColumn24());//AREA HECHO"
                createCell(row, 12, rowDataTableList.getColumn17());//CLASE DE LUGAR"
                createCell(row, 13, rowDataTableList.getColumn18());//NUMERO VICTIMAS EN HECHO"
                createCell(row, 14, rowDataTableList.getColumn28());//NUMERO LESIONADOS EN ECHO"
                createCell(row, 15, rowDataTableList.getColumn4());//NOMBRES Y APELLIDOS"
                createCell(row, 16, rowDataTableList.getColumn8());//SEXO"
                createCell(row, 17, rowDataTableList.getColumn6());//TIPO EDAD"
                createCell(row, 18, rowDataTableList.getColumn7());//EDAD"
                createCell(row, 19, rowDataTableList.getColumn9());//OCUPACION"
                createCell(row, 20, rowDataTableList.getColumn2());//TIPO IDENTIFICACION"
                createCell(row, 21, rowDataTableList.getColumn3());//IDENTIFICACION"
                createCell(row, 22, rowDataTableList.getColumn5());//EXTRANJERO"
                createCell(row, 23, rowDataTableList.getColumn12());//DEPARTAMENTO RESIDENCIA"
                createCell(row, 24, rowDataTableList.getColumn11());//MUNICIPIO RESIDENCIA"
                createCell(row, 25, rowDataTableList.getColumn10());//BARRIO RESIDENCIA"
                createCell(row, 26, rowDataTableList.getColumn31());//COMUNA BARRIO RESIDENCIA"

                createCell(row, 27, rowDataTableList.getColumn25());//PAIS PROCEDENCIA"
                createCell(row, 28, rowDataTableList.getColumn26());//DEPARTAMENTO PROCEDENCIA"
                createCell(row, 29, rowDataTableList.getColumn27());//MUNICIPIO PROCEDENCIA"
                createCell(row, 30, rowDataTableList.getColumn29());//ARMA O CAUSA DE MUERTE"
                createCell(row, 31, rowDataTableList.getColumn19());//NARRACION DEL HECHO"
                createCell(row, 32, rowDataTableList.getColumn21());//NIVEL DE ALCOHOL"
                createCell(row, 33, rowDataTableList.getColumn22());//TIPO NIVEL DE ALCOHOL"
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        currentFatalInjuryAccident = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length > 1) {
                currentFatalInjuryAccident = fatalInjuryAccidentFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
            }
            if (selectedRowsDataTable.length > 1) {
                btnEditDisabled = true;
                btnRemoveDisabled = false;
            } else {
                btnEditDisabled = false;
                btnRemoveDisabled = false;
            }
        }
    }

    public void deleteRegistry() {
        if (selectedRowsDataTable != null) {
            List<FatalInjuryAccident> fatalInjuryAccidentList = new ArrayList<FatalInjuryAccident>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryAccidentList.add(fatalInjuryAccidentFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (fatalInjuryAccidentList != null) {
                for (int j = 0; j < fatalInjuryAccidentList.size(); j++) {
                    FatalInjuries auxFatalInjuries = fatalInjuryAccidentList.get(j).getFatalInjuries();
                    Victims auxVictims = fatalInjuryAccidentList.get(j).getFatalInjuries().getVictimId();
                    fatalInjuryAccidentFacade.remove(fatalInjuryAccidentList.get(j));
                    fatalInjuriesFacade.remove(auxFatalInjuries);
                    victimsFacade.remove(auxVictims);
                }
            }
            //quito los elementos seleccionados de rowsDataTableList seleccion de 
//            for (int j = 0; j < selectedRowsDataTable.length; j++) {
//                for (int i = 0; i < rowDataTableList.size(); i++) {
//                    if (selectedRowsDataTable[j].getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
//                        rowDataTableList.remove(i);
//                        break;
//                    }
//                }
//            }
            //deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        }
    }

//    public List<RowDataTable> getRowDataTableList() {
//        return rowDataTableList;
//    }
//
//    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
//        this.rowDataTableList = rowDataTableList;
//    }
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LazyDataModel<RowDataTable> getTable_model() {
        return table_model;
    }

    public void setTable_model(LazyDataModel<RowDataTable> table_model) {
        this.table_model = table_model;
    }
}
