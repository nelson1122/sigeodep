/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.PopulationsEnum;
import beans.util.RowDataTable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.io.Serializable;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import model.dao.AreasFacade;
import model.dao.GendersFacade;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "populationsMB")
@SessionScoped
public class PopulationsMB implements Serializable {

    /**
     * CLASE DE ACCIDENTE(TRANSITO)
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    //private int currentSearchCriteria = 0;
    //private String currentSearchValue = "";
    @EJB
    GendersFacade gendersFacade;
    @EJB
    AreasFacade areasFacade;
    //private List<AccidentClasses> accidentClassesList;
    //private AccidentClasses accidentClass;
    //private String name = "";//Nombre del barrio.
    //private String newName = "";//Nombre del barrio.
    //private boolean btnEditDisabled = true;
    //private boolean btnRemoveDisabled = true;
    private UploadedFile file;
    ConnectionJdbcMB connectionJdbcMB;

    public PopulationsMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    private String getInt(String str) {
        String strReturn = "";
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                strReturn = strReturn + str.charAt(i);
            }
        }
        return strReturn;
    }

    private String getIntWhitZero(String str) {
        String strReturn = "";
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                strReturn = strReturn + str.charAt(i);
            }
        }
        if (strReturn.length() == 0) {
            strReturn = "0";
        }
        return strReturn;
    }
    ArrayList<String> capitalMunicipality;
    ArrayList<String> dispersedRural;
    ArrayList<String> townCenter;

    public void handleFileUpload(FileUploadEvent event) {
        try {
            file = event.getFile();
            File file2 = new File(file.getFileName());
            int a1, b1, a2, b2;
            capitalMunicipality = new ArrayList<String>();
            dispersedRural = new ArrayList<String>();
            townCenter = new ArrayList<String>();

            //----------------------------------------------------
            //cargo los vectores
            try {
                OPCPackage container;
                container = OPCPackage.open(file2.getAbsolutePath());
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
                XSSFReader xssfReader = new XSSFReader(container);
                StylesTable styles = xssfReader.getStylesTable();
                XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                while (iter.hasNext()) {
                    InputStream stream = iter.next();
                    InputSource sheetSource = new InputSource(stream);
                    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxFactory.newSAXParser();
                        XMLReader sheetParser = saxParser.getXMLReader();
                        ContentHandler handler;
                        handler = new XSSFSheetXMLHandler(styles, strings, new XSSFSheetXMLHandler.SheetContentsHandler() {
                            ArrayList<String> rowFileData;
                            //PopulationsEnum populationsEnum = PopulationsEnum.NOVALUE;
                            boolean first = true;//primera ves que se lee
                            String pos;//posicion en el vector
                            String currentState = "";

                            @Override
                            public void startRow(int rowNum) {
                                //System.out.println(" INICIA " + String.valueOf(rowNum) + "-----------------------");
                                if (first) {
                                    for (int h = 0; h < 150; h++) {
                                        capitalMunicipality.add("");
                                        dispersedRural.add("");
                                        townCenter.add("");
                                    }
                                    first = false;
                                }
                                rowFileData = new ArrayList<String>();
                            }

                            @Override
                            public void endRow() {
                                //System.out.println("Fila leida: " + rowFileData.toString());
                                switch (PopulationsEnum.convert(currentState)) {
                                    case loadCapitalMunicipality:
                                        if (rowFileData.get(0).indexOf("año") != -1) {
                                            pos = getInt(rowFileData.get(0));
                                            if (pos.length() != 0) {
                                                capitalMunicipality.set(Integer.parseInt(pos), getIntWhitZero(rowFileData.get(1)) + "," + getIntWhitZero(rowFileData.get(2)));
                                            }
                                        } else if (rowFileData.get(0).indexOf("Total") != -1) {
                                            currentState = "";
                                        }
                                        break;
                                    case loadDispersedRural:
                                        if (rowFileData.get(0).indexOf("año") != -1) {
                                            pos = getInt(rowFileData.get(0));
                                            if (pos.length() != 0) {
                                                dispersedRural.set(Integer.parseInt(pos), getIntWhitZero(rowFileData.get(1)) + "," + getIntWhitZero(rowFileData.get(2)));
                                            }
                                        } else if (rowFileData.get(0).indexOf("Total") != -1) {
                                            currentState = "";
                                        }
                                        break;
                                    case loadTownCenter:
                                        if (rowFileData.get(0).indexOf("año") != -1) {
                                            pos = getInt(rowFileData.get(0));
                                            if (pos.length() != 0) {
                                                townCenter.set(Integer.parseInt(pos), getIntWhitZero(rowFileData.get(1)) + "," + getIntWhitZero(rowFileData.get(2)));
                                            }
                                        } else if (rowFileData.get(0).indexOf("Total") != -1) {
                                            currentState = "";
                                        }
                                        break;
                                }

                                if (!rowFileData.isEmpty()) {
                                    if (rowFileData.get(0).indexOf("52001199") != -1) {
                                        currentState = "loadCapitalMunicipality";
                                        //populationsEnum = PopulationsEnum.loadCapitalMunicipality;
                                    } else if (rowFileData.get(0).indexOf("52001299") != -1) {
                                        currentState = "loadTownCenter";
                                        //populationsEnum = PopulationsEnum.loadTownCenter;
                                    } else if (rowFileData.get(0).indexOf("52001399") != -1) {
                                        currentState = "loadDispersedRural";
                                        //populationsEnum = PopulationsEnum.loadDispersedRural;
                                    }
                                }
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                                //System.out.println("CELDA:"+cellReference + "   VALOR." + formattedValue);
                                CellReference a = new CellReference(cellReference);
                                int empyColumns = a.getCol() - rowFileData.size();
                                for (int i = 0; i < empyColumns; i++) {
                                    rowFileData.add("");
                                }
                                rowFileData.add(formattedValue);
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        },
                                false//means result instead of formula
                                );
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(sheetSource);
                    } catch (ParserConfigurationException e) {
                        throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
                    }
                    stream.close();
                    break;
                }
            } catch (InvalidFormatException e) {
                System.out.println(e.toString());
            } catch (SAXException e) {
                System.out.println(e.toString());
            } catch (OpenXML4JException e) {
                System.out.println(e.toString());
            }
//            //imprimo vectores
//            for (int i = 0; i < capitalMunicipality.size(); i++) {
//                System.out.println("CABECERA: POSICION" + String.valueOf(i) + " :" + capitalMunicipality.get(i));
//            }
//            for (int i = 0; i < dispersedRural.size(); i++) {
//                System.out.println("RURAL DIS: POSICION" + String.valueOf(i) + " :" + dispersedRural.get(i));
//            }
//            for (int i = 0; i < townCenter.size(); i++) {
//                System.out.println("CENTRO PO: POSICION" + String.valueOf(i) + " :" + townCenter.get(i));
//            }


            //uno los vectores centro poblado y rural disperso en centro poblado
            String[] splitDispersedRural;
            String[] splitTownCenter;
            a1 = 0;
            b1 = 0;
            a2 = 0;
            b2 = 0;
            for (int i = 0; i < dispersedRural.size(); i++) {
                if (dispersedRural.get(i).length() != 0) {//hay datos de rural disperso
                    splitDispersedRural = dispersedRural.get(i).split(",");
                    a1 = Integer.parseInt(splitDispersedRural[0]);
                    b1 = Integer.parseInt(splitDispersedRural[1]);
                    if (townCenter.get(i).length() != 0) {//hay datos de centro poblado
                        splitTownCenter = townCenter.get(i).split(",");
                        a2 = Integer.parseInt(splitTownCenter[0]);
                        b2 = Integer.parseInt(splitTownCenter[1]);
                    }
                    townCenter.set(i, String.valueOf(a1 + a2) + "," + String.valueOf(b1 + b2));
                } else if (townCenter.get(i).length() != 0) {//hay datos de centro poblado
                    splitTownCenter = townCenter.get(i).split(",");
                    a1 = Integer.parseInt(splitTownCenter[0]);
                    b1 = Integer.parseInt(splitTownCenter[1]);
                    if (dispersedRural.get(i).length() != 0) {//hay datos de rural disperso
                        splitDispersedRural = dispersedRural.get(i).split(",");
                        a2 = Integer.parseInt(splitDispersedRural[0]);
                        b2 = Integer.parseInt(splitDispersedRural[1]);
                    }
                    townCenter.set(i, String.valueOf(a1 + a2) + "," + String.valueOf(b1 + b2));
                }
            }
//            for (int i = 0; i < townCenter.size(); i++) {
//                System.out.println("FINAL: POSICION" + String.valueOf(i) + " :" + townCenter.get(i));
//            }
            StringBuilder sb = new StringBuilder();
            CopyManager cpManager;
            boolean insertedData = false;
            boolean populationsTableClear = false;
            try {
                cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
                int batchSize = 200;//numero de insert por copy realizado
                PushbackReader reader = new PushbackReader(new StringReader(""), 10000);
                //REGISTRO DE POBLACIONES ZONA URBANA---------------------------
                for (int i = 0; i < capitalMunicipality.size(); i++) {
                    if (capitalMunicipality.get(i).length() != 0) {
                        //id           genero,area         
                        sb.append(i).append(",1,1,").append(capitalMunicipality.get(i).split(",")[0]).append("\n");
                        sb.append(i).append(",2,1,").append(capitalMunicipality.get(i).split(",")[1]).append("\n");
                        insertedData = true;
                    }
                    if (i % batchSize == 0) {
                        if (!populationsTableClear) {//no se eliminado los datos de populations
                            connectionJdbcMB.non_query("delete from populations");
                            populationsTableClear = true;
                        }
                        reader.unread(sb.toString().toCharArray());
                        cpManager.copyIn("COPY populations FROM STDIN WITH CSV", reader);
                        //System.out.println("####### \n\r " + sb);
                        sb.delete(0, sb.length());
                    }
                }
                if (!populationsTableClear && insertedData) {//no se eliminado los datos de populations y hay que registrar datos
                    connectionJdbcMB.non_query("delete from populations");
                    populationsTableClear = true;
                }
                reader.unread(sb.toString().toCharArray());
                cpManager.copyIn("COPY populations FROM STDIN WITH CSV", reader);
                //System.out.println("####### \n\r " + sb);
                sb.delete(0, sb.length());
                //REGISTRO DE POBLACIONES ZONA RURAL---------------------------
                for (int i = 0; i < townCenter.size(); i++) {
                    if (townCenter.get(i).length() != 0) {
                        //id           genero,area         
                        sb.append(i).append(",1,2,").append(townCenter.get(i).split(",")[0]).append("\n");
                        sb.append(i).append(",2,2,").append(townCenter.get(i).split(",")[1]).append("\n");
                        insertedData = true;
                    }
                    if (i % batchSize == 0) {
                        if (!populationsTableClear) {//no se eliminado los datos de populations
                            connectionJdbcMB.non_query("delete from populations");
                            populationsTableClear = true;
                        }
                        reader.unread(sb.toString().toCharArray());
                        cpManager.copyIn("COPY populations FROM STDIN WITH CSV", reader);
                        //System.out.println("####### \n\r " + sb);
                        sb.delete(0, sb.length());
                    }
                }
                if (!populationsTableClear && insertedData) {//no se eliminado los datos de populations y hay que registrar datos
                    connectionJdbcMB.non_query("delete from populations");
                }
                reader.unread(sb.toString().toCharArray());
                cpManager.copyIn("COPY populations FROM STDIN WITH CSV", reader);
                //System.out.println("####### \n\r " + sb);

            } catch (SQLException ex) {
                System.out.println("Error: populationsMB_2 > " + ex.toString());
            }
            FacesMessage msg;
            if (insertedData) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Archivo procesado", "Las poblaciones han sido cargadas"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Archivo procesado", "No se obtuvo ninguna informacion sobre poblaciones de este archivo, o no tiene el formato necesario"));
            }
        } catch (IOException ex) {
            System.out.println("Error: populationsMB_1 > " + ex.toString());
        }
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
//        HSSFWorkbook book = (HSSFWorkbook) document;
//        HSSFRow row;
//        HSSFCellStyle cellStyle = book.createCellStyle();
//        HSSFFont font = book.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        cellStyle.setFont(font);
//        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
//        createCell(cellStyle, row, 0, "CODIGO");//"100">#{rowX.column1}</p:column>
//        createCell(cellStyle, row, 1, "NOMBRE");//"100">#{rowX.column23}</p:column>                                
//        accidentClassesList=accidentClassesFacade.findAll();
//        for (int i = 0; i < accidentClassesList.size(); i++) {
//            row = sheet.createRow(i + 1);
//            createCell(row, 0, accidentClassesList.get(i).getAccidentClassId().toString());//CODIGO
//            createCell(row, 1, accidentClassesList.get(i).getAccidentClassName());//NOMBRE            
//        }
    }

    public void createDynamicTable() {
        try {
            ResultSet rs = connectionJdbcMB.consult("Select * from populations order by 3,2,1");
            rowDataTableList = new ArrayList<RowDataTable>();
            String gender;
            while (rs.next()) {


                rowDataTableList.add(new RowDataTable(
                        rs.getString(1),
                        gendersFacade.find(Short.parseShort(rs.getString(2))).getGenderName(),
                        areasFacade.find(Short.parseShort(rs.getString(3))).getAreaName(),
                        rs.getString(4)));
            }
        } catch (Exception e) {
            System.out.println("Error populationMB_1: " + e.toString());
        }
    }

    public void reset() {
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
}
