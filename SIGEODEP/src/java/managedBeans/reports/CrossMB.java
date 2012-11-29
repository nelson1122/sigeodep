/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.reports;

import beans.util.Variable;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;
import org.primefaces.model.DualListModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author santos
 */
@ManagedBean(name = "crossMB")
@SessionScoped
public class CrossMB {

    private DualListModel<Variable> variables;
    private Date start;
    private Date end;
    private String form_id = "SCC-F-032";
    private int nvariables;
    private Map<Integer, String> row_labels;
    private List<String> row_indexes;
    private List<String> column_names;
    private Map<Integer, String> column_labels;
    private List<String> column_indexes;
    private Object[][] model;
    private String variableName = "";
    private short graphicalType = 1;
    private SelectItem[] valuesList;
    private boolean valuesListDisabled = true;
    private boolean renderedDynamicDataTable = false;
    private boolean renderedLineChart = false;
    private boolean renderedPieChart = false;
    private boolean renderedBarChartVertical = false;
    private boolean renderedBarChartHorizontal = false;
    private boolean renderedBarChartStack = false;
    private String lineModelTitle = "";
    private String verticalBarModelTitle = "";
    private String horizontalBarModelTitle = "";
    private String stackBarModelTitle = "";
    private OutputPanel dynamicDataTableGroup; // Placeholder.
    private PieChartModel pieModel;
    private CartesianChartModel lineModel;
    private CartesianChartModel verticalBarModel;
    private CartesianChartModel horizontalBarModel;
    private CartesianChartModel stackBarModel;
    private int maxLineModelYaxis = 0;
    private int maxStackModelYaxis = 0;
    private ArrayList<SpanColumns> dynamicHeaders1;
    private ArrayList<TableGroup> listaDeGrupos;
    private String[] dynamicHeaders2;
    private String currentValue;

    public CrossMB() {
        reset();
    }

    public final void reset() {
        listaDeGrupos = new ArrayList<TableGroup>();
        ReportConnection connection = new ReportConnection();
        connection.connect();
        List<Variable> variablesSource = connection.getVariablesByFormID(form_id);
        List<Variable> variablesTarget = new ArrayList<Variable>();
        variables = new DualListModel<Variable>(variablesSource, variablesTarget);
        row_labels = new TreeMap<Integer, String>();
        row_indexes = new ArrayList<String>();
        column_names = new ArrayList<String>();
        column_labels = new TreeMap<Integer, String>();
        column_indexes = new ArrayList<String>();

        variableName = "";
        graphicalType = 1;
        valuesList = new SelectItem[0];
        valuesListDisabled = true;
        renderedDynamicDataTable = false;
        renderedLineChart = false;
        renderedPieChart = false;
        renderedBarChartVertical = false;
        renderedBarChartHorizontal = false;
        renderedBarChartStack = false;
        connection.disconnect();
    }

    public void changeGraphicalType() {
        seeGraph();
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow fila;
        HSSFCell celda;
        HSSFRichTextString texto;
        if (variables.getTarget().size() == 2) {//son dos variables             
            //creo cabeceras
            fila = sheet.createRow(0);// Se crea una fila dentro de la hoja
            for (int i = 0; i < dynamicHeaders2.length; i++) {
                celda = fila.createCell((short) i);// Se crea una celda dentro de la fila                
                texto = new HSSFRichTextString(dynamicHeaders2[i]);// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            for (int i = 0; i < model.length; i++) {
                fila = sheet.createRow(i + 1);// Se crea una fila dentro de la hoja
                for (int j = 0; j < model[i].length; j++) {
                    celda = fila.createCell((short) j);// Se crea una celda dentro de la fila                
                    texto = new HSSFRichTextString(model[i][j].toString());// Se crea el contenido de la celda y se mete en ella.
                    celda.setCellValue(texto);
                }
            }
        }

        if (variables.getTarget().size() == 3) {//son tres variables            
            //esquina inicial
            fila = sheet.createRow(0);// Se crea una fila dentro de la hoja            
            celda = fila.createCell((short) 0);// Se crea una celda dentro de la fila                
            texto = new HSSFRichTextString(dynamicHeaders2[0]);// Se crea el contenido de la celda y se mete en ella.
            celda.setCellValue(texto);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
            //cabecera1
            int pos = 1;
            for (int j = 0; j < dynamicHeaders1.size(); j++) {
                for (int i = 0; i < dynamicHeaders1.get(j).getColumns(); i++) {
                    celda = fila.createCell((short) pos);// Se crea una celda dentro de la fila                
                    texto = new HSSFRichTextString(dynamicHeaders1.get(j).getLabel());// Se crea el contenido de la celda y se mete en ella.
                    celda.setCellValue(texto);
                    pos++;
                }
            }
            int posF = 0;
            int posI = 1;
            for (int j = 0; j < dynamicHeaders1.size(); j++) {
                posI = posF;
                posI++;
                for (int i = 0; i < dynamicHeaders1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
            }
            //cabecera2
            fila = sheet.createRow(1);// Se crea una fila dentro de la hoja            
            for (int j = 1; j < dynamicHeaders2.length; j++) {
                celda = fila.createCell((short) j);// Se crea una celda dentro de la fila                
                texto = new HSSFRichTextString(dynamicHeaders2[j]);// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            //datos
            for (int i = 0; i < model.length; i++) {
                fila = sheet.createRow(i + 2);// Se crea una fila dentro de la hoja
                for (int j = 0; j < model[i].length; j++) {
                    celda = fila.createCell((short) j);// Se crea una celda dentro de la fila                
                    texto = new HSSFRichTextString(model[i][j].toString());// Se crea el contenido de la celda y se mete en ella.
                    celda.setCellValue(texto);
                }
            }
        }
    }

    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {

        Document pdf = (Document) document;
        //pdf=new Document();
        pdf.open();

        PdfPTable tabla = new PdfPTable(dynamicHeaders2.length);
        PdfPCell celda = null;


        /////////////////////////
        if (variables.getTarget().size() == 2) {//son dos variables             
            //creo cabeceras            
            for (int i = 0; i < dynamicHeaders2.length; i++) {
                celda = new PdfPCell(new Paragraph(dynamicHeaders2[i], FontFactory.getFont("Times", 7, Font.BOLD, Color.RED)));
                tabla.addCell(celda);
            }
            for (int i = 0; i < model.length; i++) {
                for (int j = 0; j < model[i].length; j++) {
                    celda = new PdfPCell(new Paragraph(model[i][j].toString(), FontFactory.getFont("Times", 7, Font.BOLD, Color.BLACK)));
                    tabla.addCell(celda);
                }
            }
        }

        if (variables.getTarget().size() == 3) {//son tres variables            
            //esquina inicial
            celda = new PdfPCell(new Paragraph(dynamicHeaders2[0], FontFactory.getFont("Times", 7, Font.BOLD, Color.RED)));
            celda.setRowspan(2);
            tabla.addCell(celda);

            //cabecera1
            for (int j = 0; j < dynamicHeaders1.size(); j++) {
                celda = new PdfPCell(new Paragraph(dynamicHeaders1.get(j).getLabel(), FontFactory.getFont("Times", 7, Font.BOLD, Color.RED)));
                celda.setColspan(dynamicHeaders1.get(j).getColumns());
                tabla.addCell(celda);

            }
            //cabecera2
            for (int j = 1; j < dynamicHeaders2.length; j++) {
                celda = new PdfPCell(new Paragraph(dynamicHeaders2[j], FontFactory.getFont("Times", 7, Font.BOLD, Color.BLUE)));
                tabla.addCell(celda);
            }
            //datos
            for (int i = 0; i < model.length; i++) {
                for (int j = 0; j < model[i].length; j++) {
                    celda = new PdfPCell(new Paragraph(model[i][j].toString(), FontFactory.getFont("Times", 7, Font.BOLD, Color.BLACK)));
                    tabla.addCell(celda);
                }
            }
        }

        pdf.add(tabla);
        ////////////////////////

        pdf.newPage();
    }

    private void createLineModel() {
        //--------------------------------------------
        //datos para el modelo del gafico de lineas
        //--------------------------------------------
        lineModel = new CartesianChartModel();
        ChartSeries serie = new ChartSeries();

        if (variables.getTarget().size() == 2) {//son dos variables 
            lineModelTitle = variables.getTarget().get(0).getName() + " - " + variables.getTarget().get(1).getName();
            maxLineModelYaxis = 1;
            maxStackModelYaxis = 0;
            int aux;
            for (int i = 0; i < model.length; i++) {
                aux = 0;
                for (int j = 0; j < model[i].length; j++) {
                    if (j == 0) {
                        serie = new ChartSeries();
                        serie.setLabel(String.valueOf(model[i][j]));
                    } else {
                        serie.set(dynamicHeaders2[j], (Number) model[i][j]);
                        aux = aux + Integer.parseInt(model[i][j].toString());
                        if (maxLineModelYaxis < Integer.parseInt(model[i][j].toString())) {
                            maxLineModelYaxis = Integer.parseInt(model[i][j].toString());
                        }
                    }
                }
                if (maxStackModelYaxis < aux) {
                    maxStackModelYaxis = aux;
                }
                lineModel.addSeries(serie);
            }
            maxLineModelYaxis = (int) (maxLineModelYaxis + (maxLineModelYaxis * 0.1));
        }

        if (variables.getTarget().size() == 3) {//son tres variables            
            //busco el grupo a graficar
            TableGroup tableGroupSeleccionado = null;
            for (int m = 0; m < listaDeGrupos.size(); m++) {
                if (currentValue.compareTo(listaDeGrupos.get(m).getNombreGrupo()) == 0) {
                    tableGroupSeleccionado = listaDeGrupos.get(m);
                    break;
                }
            }

            lineModelTitle = variables.getTarget().get(0).getName() + " - " + variables.getTarget().get(1).getName() + " - " + variables.getTarget().get(2).getName();
            maxLineModelYaxis = 1;
            maxStackModelYaxis = 1;
            int aux;
            for (int i = 0; i < tableGroupSeleccionado.getDatos().length; i++) {
                aux = 0;
                serie = new ChartSeries();
                serie.setLabel(tableGroupSeleccionado.getNombresFilas().get(i));
                for (int j = 0; j < tableGroupSeleccionado.getDatos()[i].length; j++) {

                    serie.set(tableGroupSeleccionado.getNombresColumnas().get(j), (Number) Integer.parseInt(tableGroupSeleccionado.getDatos()[i][j].toString()));
                    aux = aux + Integer.parseInt(tableGroupSeleccionado.getDatos()[i][j].toString());
                    if (maxLineModelYaxis < aux) {
                        maxLineModelYaxis = aux;
                    }
                }
                if (maxStackModelYaxis < aux) {
                    maxStackModelYaxis = aux;
                }
                lineModel.addSeries(serie);
            }
            maxLineModelYaxis = (int) (maxLineModelYaxis + 10);
            //maxLineModelYaxis = 100;
        }
    }

    private void createPieModel() {
        //--------------------------------------------
        //datos para el modelo del gafico de pastel
        //--------------------------------------------
        pieModel = new PieChartModel();
        pieModel.set("Brand 1", 540);
        pieModel.set("Brand 2", 325);
        pieModel.set("Brand 3", 702);
        pieModel.set("Brand 4", 421);
    }

    private void createVerticalBarModel() {
        //--------------------------------------------
        //datos para el modelo del gafico de barras vertical
        //--------------------------------------------
        createLineModel();
    }

    private void createHorizontalBarModel() {
        //--------------------------------------------
        //datos para el modelo del gafico de barras horizontal
        //--------------------------------------------
        createLineModel();
    }

    private void createStackBarModel() {
        //--------------------------------------------
        //datos para el modelo del gafico de barras apilado
        //--------------------------------------------
        createLineModel();
    }

    private void createDynamicDataTable() {
        PanelGrid panelGrid = new PanelGrid();
        Row row1;
        Column column;
        HtmlOutputText text;
        DataTable dynamicDataTable = new DataTable();

        dynamicDataTable.setValueExpression("value", createValueExpression("#{crossMB.dynamicList}", List.class));
        dynamicDataTable.setVar("dynamicItem");

        dynamicHeaders1 = new ArrayList<SpanColumns>();
        dynamicHeaders2 = new String[column_names.size()];

        if (nvariables == 2) {//solo existe una cabecera            
            for (int i = 0; i < column_names.size(); i++) {
                dynamicHeaders2[i] = column_names.get(i);
            }
        } else {//existen dos cabeceras            
            listaDeGrupos = new ArrayList<TableGroup>();
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < column_names.size(); i++) {
                if (i == 0) {//la primer columna no se divide
                    dynamicHeaders2[i] = column_names.get(i);
                    //nuevoGrupo.setNombreVariable1(column_names.get(i));
                } else {//si no es la primer columna debe ser dividida
                    splitVars = column_names.get(i).split("\\|");//separo las dos variables
                    if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "dynamicHeaders1"
                        int num = dynamicHeaders1.get(dynamicHeaders1.size() - 1).getColumns();
                        dynamicHeaders1.get(dynamicHeaders1.size() - 1).setColumns(num + 1);
                    } else {//no existe la columna la debo crea y adicionar a la lista                    
                        currentVar = splitVars[0];
                        SpanColumns newSpanColumn = new SpanColumns();
                        newSpanColumn.setLabel(splitVars[0]);
                        newSpanColumn.setColumns(1);
                        dynamicHeaders1.add(newSpanColumn);
                    }
                    dynamicHeaders2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
                    //nuevoGrupo.ingresarNombreColumna(splitVars[1]);                    
                }
            }

            int posicion = 1;
            int posicion2 = 1;
            for (int j = 0; j < dynamicHeaders1.size(); j++) {
                TableGroup nuevoGrupo = new TableGroup();
                nuevoGrupo.setNombreGrupo(dynamicHeaders1.get(j).getLabel());
                for (int k = 0; k < dynamicHeaders1.get(j).getColumns(); k++) {
                    nuevoGrupo.ingresarNombreColumna(dynamicHeaders2[posicion]);
                    posicion++;
                }
                String[][] datos = new String[model.length][dynamicHeaders1.get(j).getColumns()];

                for (int m = 0; m < model.length; m++) {
                    nuevoGrupo.ingresarNombreFila(model[m][0].toString());
                }

                for (int m = 0; m < model.length; m++) {
                    for (int k = 0; k < dynamicHeaders1.get(j).getColumns(); k++) {
                        datos[m][k] = model[m][k + posicion2].toString();
                    }
                }
                posicion2 = posicion2 + dynamicHeaders1.get(j).getColumns();
                nuevoGrupo.setDatos(datos);
                listaDeGrupos.add(nuevoGrupo);
            }
        }
        //creo la cabecera 1
        valuesListDisabled = true;
        if (!dynamicHeaders1.isEmpty()) {
            row1 = new Row();
            column = new Column();
            column.setStyleClass("ui-widget-header ui-state-focus");
            text = new HtmlOutputText();
            text.setValue(dynamicHeaders2[0]);
            column.getChildren().add(text);
            column.setRowspan(2);
            row1.getChildren().add(column);
            //---------------
            valuesList = new SelectItem[dynamicHeaders1.size()];

            valuesListDisabled = false;
            //---------------
            for (int j = 0; j < dynamicHeaders1.size(); j++) {
                valuesList[j] = new SelectItem(dynamicHeaders1.get(j).getLabel());
                column = new Column();
                column.setColspan(dynamicHeaders1.get(j).getColumns());
                column.setStyleClass("ui-widget-header");
                text = new HtmlOutputText();
                text.setValue(dynamicHeaders1.get(j).getLabel());
                column.getChildren().add(text);
                row1.getChildren().add(column);
            }
            try {
                currentValue = valuesList[0].getLabel();
            } catch (Exception e) {
            }
            panelGrid.getChildren().add(row1);
        }
        //creo la cabecera 2
        row1 = new Row();
        int pos = 0;
        if (nvariables == 2) {
            pos = 0;
        }
        if (nvariables == 3) {
            pos = 1;
        }
        for (int j = pos; j < dynamicHeaders2.length; j++) {
            column = new Column();
            column.setStyleClass("ui-widget-header ui-state-focus");
            text = new HtmlOutputText();
            text.setValue(dynamicHeaders2[j]);
            column.getChildren().add(text);
            row1.getChildren().add(column);
        }
        panelGrid.getChildren().add(row1);
        //ingreso los datos
        for (int i = 0; i < model.length; i++) {
            row1 = new Row();
            for (int j = 0; j < model[i].length; j++) {
                column = new Column();
                text = new HtmlOutputText();
                text.setValue(model[i][j]);
                column.getChildren().add(text);
                row1.getChildren().add(column);
            }
            panelGrid.getChildren().add(row1);
        }
        dynamicDataTableGroup = new OutputPanel();
        dynamicDataTableGroup.getChildren().add(panelGrid);
        seeGraph();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruce de variables realizado"));
    }

    private ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().getExpressionFactory().createValueExpression(
                facesContext.getELContext(), valueExpression, valueType);
    }

    public void seeGraph() {
        switch (graphicalType) {
            case 1://lineal  
                createLineModel();
                renderedLineChart = true;
                renderedPieChart = false;
                renderedBarChartVertical = false;
                renderedBarChartHorizontal = false;
                renderedBarChartStack = false;
                break;
            case 2://pastel
                createPieModel();
                renderedLineChart = false;
                renderedPieChart = true;
                renderedBarChartVertical = false;
                renderedBarChartHorizontal = false;
                renderedBarChartStack = false;
                break;
            case 3://barra horizontal
                createHorizontalBarModel();
                renderedLineChart = false;
                renderedPieChart = false;
                renderedBarChartVertical = false;
                renderedBarChartHorizontal = true;
                renderedBarChartStack = false;
                break;
            case 4://barravertical
                createVerticalBarModel();
                renderedLineChart = false;
                renderedPieChart = false;
                renderedBarChartVertical = true;
                renderedBarChartHorizontal = false;
                renderedBarChartStack = false;
                break;
            case 5://barra apilada
                createStackBarModel();
                renderedLineChart = false;
                renderedPieChart = false;
                renderedBarChartVertical = false;
                renderedBarChartHorizontal = false;
                renderedBarChartStack = true;
                break;
        }
    }

    public void setRowAndColumns() {

        renderedLineChart = false;
        renderedPieChart = false;
        renderedBarChartVertical = false;
        renderedBarChartHorizontal = false;
        renderedBarChartStack = false;

        if (variables.getTarget().size() < 2) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "DEBE CRUZAR DOS VARIABLES COMO MINIMO");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (variables.getTarget().size() > 3) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "SOLO SE PUEDE CRUZAR 3 VARIABLES COMO MAXIMO");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            ReportConnection connection = new ReportConnection();
            connection.connect();
            column_labels.clear();
            column_indexes.clear();
            row_labels.clear();
            row_indexes.clear();
            Variable row = variables.getTarget().get(0);
            String subquery = connection.getPrepivotQuery(variables);
            List<Integer> column_values = connection.getValuesFromVariable(row.getField(), subquery);
            int index = 0;
            for (Integer column_value : column_values) {
                row_labels.put(index, connection.getValuesFromGenericTable(column_value, row.getGeneric_table()));
                row_indexes.add(index, column_value.toString());
                index++;
            }
            if (variables.getTarget().size() == 2) {
                nvariables = 2;
                Variable column1 = variables.getTarget().get(1);
                List<Integer> column1_values = connection.getValuesFromVariable(column1.getField(), subquery);
                index = 0;
                for (Integer column1_value : column1_values) {
                    column_indexes.add(index, column1_value.toString());
                    String column1_label = connection.getValuesFromGenericTable(column1_value, column1.getGeneric_table());
                    column_labels.put(index, column1_label);
                    index++;
                }
            }
            if (variables.getTarget().size() == 3) {
                nvariables = 3;
                Variable column1 = variables.getTarget().get(1);
                Variable column2 = variables.getTarget().get(2);
                variableName = variables.getTarget().get(1).getName();
                column_indexes = connection.getCombinedColumns(column1.getField(), column2.getField());
                index = 0;
                for (String column_id : column_indexes) {
                    String[] columns = column_id.split("\\|");
                    int id1 = Integer.parseInt(columns[0]);
                    int id2 = Integer.parseInt(columns[1]);
                    String name1 = connection.getValuesFromGenericTable(id1, column1.getGeneric_table());
                    String name2 = connection.getValuesFromGenericTable(id2, column2.getGeneric_table());
                    column_labels.put(index++, name1 + "|" + name2);
                }
            }
            System.out.println(row_indexes);
            System.out.println(row_labels);
            System.out.println(column_indexes);
            System.out.println(column_labels);
            connection.disconnect();
            buildModel();
            createDynamicDataTable(); // Populate editable datatable.
            renderedDynamicDataTable = true;
        }

    }

    public void buildModel() {
        try {
            int ncols = row_indexes.size();
            int nrows = column_indexes.size() + 1;
            model = new Object[ncols][nrows];
            for (int x = 0; x < ncols; x++) {
                model[x][0] = row_labels.get(x);
                for (int y = 1; y < nrows; y++) {
                    model[x][y] = 0;
                }
            }
            ReportConnection connection = new ReportConnection();
            connection.connect();
            ResultSet records = connection.consult("SELECT * FROM prepivot;");
            while (records.next()) {
                int x = getColumnIndex(records.getString(1));
                int y;
                if (nvariables == 2) {
                    y = getRowIndex(records.getString(2));
                } else {
                    y = getRowIndex(records.getString(2), records.getString(3));
                }
                model[x][y + 1] = records.getInt("count");
            }
            column_names.clear();
            column_names.add(variables.getTarget().get(0).getName());
            for (Integer key : column_labels.keySet()) {
                column_names.add(column_labels.get(key));
            }
            System.out.println("Done!!!");
            connection.disconnect();


        } catch (SQLException ex) {
            Logger.getLogger(CrossMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getColumnIndex(String value) {
        return row_indexes.indexOf(value);
    }

    private int getRowIndex(String value) {
        return column_indexes.indexOf(value);
    }

    private int getRowIndex(String value1, String value2) {
        String value = value1 + "|" + value2;
        return column_indexes.indexOf(value);
    }

    /*
     * Setters and Getters
     */
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public DualListModel<Variable> getVariables() {
        return variables;
    }

    public void setVariables(DualListModel<Variable> variables) {
        this.variables = variables;
    }

    public List<String> getColumn_labels() {
        List<String> columns = new ArrayList<String>();
        for (Integer key : row_labels.keySet()) {
            columns.add(row_labels.get(key));
        }
        return columns;
    }

    public void setColumn_labels(Map<Integer, String> column_labels) {
        this.row_labels = column_labels;
    }

    public Object[][] getModel() {
        return model;
    }

    public void setModel(Object[][] model) {
        this.model = model;
    }

    public List<String> getRow_labels() {
        List<String> rows = new ArrayList<String>();
        rows.add("Columna0");
        for (Integer key : column_labels.keySet()) {
            rows.add(column_labels.get(key));
        }
        return rows;
    }

    public void setRow_labels(Map<Integer, String> row_labels) {
        this.column_labels = row_labels;
    }

    public List<String> getColumn_names() {
        return column_names;
    }

    public void setColumn_names(List<String> column_names) {
        this.column_names = column_names;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public short getGraphicalType() {
        return graphicalType;
    }

    public void setGraphicalType(short graphicalType) {
        this.graphicalType = graphicalType;
    }

    public SelectItem[] getValuesList() {
        return valuesList;
    }

    public void setValuesList(SelectItem[] valuesList) {
        this.valuesList = valuesList;
    }

    public boolean isValuesListDisabled() {
        return valuesListDisabled;
    }

    public void setValuesListDisabled(boolean valuesListDisabled) {
        this.valuesListDisabled = valuesListDisabled;
    }

    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

    public boolean isRenderedBarChartHorizontal() {
        return renderedBarChartHorizontal;
    }

    public void setRenderedBarChartHorizontal(boolean renderedBarChartHorizontal) {
        this.renderedBarChartHorizontal = renderedBarChartHorizontal;
    }

    public boolean isRenderedBarChartStack() {
        return renderedBarChartStack;
    }

    public void setRenderedBarChartStack(boolean renderedBarChartStack) {
        this.renderedBarChartStack = renderedBarChartStack;
    }

    public boolean isRenderedBarChartVertical() {
        return renderedBarChartVertical;
    }

    public void setRenderedBarChartVertical(boolean renderedBarChartVertical) {
        this.renderedBarChartVertical = renderedBarChartVertical;
    }

    public boolean isRenderedLineChart() {
        return renderedLineChart;
    }

    public void setRenderedLineChart(boolean renderedLineChart) {
        this.renderedLineChart = renderedLineChart;
    }

    public boolean isRenderedPieChart() {
        return renderedPieChart;
    }

    public void setRenderedPieChart(boolean renderedPieChart) {
        this.renderedPieChart = renderedPieChart;
    }

    public String getHorizontalBarModelTitle() {
        return horizontalBarModelTitle;
    }

    public void setHorizontalBarModelTitle(String horizontalBarModelTitle) {
        this.horizontalBarModelTitle = horizontalBarModelTitle;
    }

    public String getLineModelTitle() {
        return lineModelTitle;
    }

    public void setLineModelTitle(String lineModelTitle) {
        this.lineModelTitle = lineModelTitle;
    }

    public String getStackBarModelTitle() {
        return stackBarModelTitle;
    }

    public void setStackBarModelTitle(String stackBarModelTitle) {
        this.stackBarModelTitle = stackBarModelTitle;
    }

    public String getVerticalBarModelTitle() {
        return verticalBarModelTitle;
    }

    public void setVerticalBarModelTitle(String verticalBarModelTitle) {
        this.verticalBarModelTitle = verticalBarModelTitle;
    }

    public OutputPanel getDynamicDataTableGroup() {
        return dynamicDataTableGroup;
    }

    public void setDynamicDataTableGroup(OutputPanel dynamicDataTableGroup) {
        this.dynamicDataTableGroup = dynamicDataTableGroup;
    }

    public CartesianChartModel getHorizontalBarModel() {
        return horizontalBarModel;
    }

    public CartesianChartModel getStackBarModel() {
        return stackBarModel;
    }

    public CartesianChartModel getVerticalBarModel() {
        return verticalBarModel;
    }

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }

    public CartesianChartModel getLineModel() {
        return lineModel;
    }

    public int getMaxLineModelYaxis() {
        return maxLineModelYaxis;
    }

    public int getMaxStackModelYaxis() {
        return maxStackModelYaxis;
    }

    public void setMaxStackModelYaxis(int maxStackModelYaxis) {
        this.maxStackModelYaxis = maxStackModelYaxis;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }
}
