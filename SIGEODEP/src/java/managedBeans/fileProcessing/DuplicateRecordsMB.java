/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.errorsControl.ErrorControl;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author santos
 */
@ManagedBean(name = "duplicateRecordsMB")
@SessionScoped
public class DuplicateRecordsMB implements Serializable {

    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    DinamicTable dinamicTable1 = new DinamicTable();
    DinamicTable dinamicTable2 = new DinamicTable();

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public DuplicateRecordsMB() {
        /*
         * Constructor de la clase
         */
        //FacesContext context = FacesContext.getCurrentInstance();
        //relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        //formsAndFieldsDataMB=(FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void loadDuplicateRecords() {
        ArrayList<String> titles = new ArrayList<String>();
        ArrayList<String> titles2 = new ArrayList<String>();
        ArrayList<ArrayList<String>> listOfRecords;

        try {
            conx = new ConnectionJDBC();
            conx.connect();
            String sql = "";
            rowDataTableList = new ArrayList<RowDataTable>();
            //ResultSet resultSetFileData = conx.consult("SELECT * FROM temp; ");
            ResultSet rs = conx.consult("SELECT * FROM temp WHERE id='" + selectedRowDataTable.getColumn4() + "'");
            // determino las cabeceras
            rs.next();
            for (int j = 0; j < rs.getMetaData().getColumnCount() - 1; j++) {
                String cadena="_";
                for (int i = 0; i < rs.getString(j + 1).length(); i++) {
                    cadena=cadena+"_";
                }
                titles.add(rs.getMetaData().getColumnName(j + 1)+cadena);
//                if(rs.getString(j + 1)!=null){
//                    titles.add(""+rs.getMetaData().getColumnName(j + 1)+":  "+rs.getString(j + 1)+" ");
//                }
//                else{
//                    titles.add(rs.getMetaData().getColumnName(j + 1)+": ");
//                }
            }
            //lleno tabla de registro compado
            listOfRecords = new ArrayList<ArrayList<String>>();
            ArrayList<String> newRow = new ArrayList<String>();

            for (int k = 0; k < rs.getMetaData().getColumnCount() - 1; k++) {
               newRow.add(rs.getString(k + 1));
            }
            listOfRecords.add(newRow);
            dinamicTable1 = new DinamicTable(listOfRecords, titles);

            //lleno tabla con registros que pueden ser posibles repeticiones de seleccionado
            sql = "";
            sql = sql + "SELECT ";
            sql = sql + "t2.id ";
            sql = sql + "FROM ";
            sql = sql + "temp t1, temp t2 ";
            sql = sql + "WHERE ";
            sql = sql + "t1.id = " + selectedRowDataTable.getColumn4() + " ";
            sql = sql + "AND t1.id != t2.id ";
            sql = sql + "AND levenshtein(t1.nroid, t2.nroid) < " + String.valueOf(6) + " ";
            sql = sql + "AND levenshtein(t1.nombres, t2.nombres) < " + String.valueOf(4) + " ";
            sql = sql + "AND levenshtein(t1.apellidos, t2.apellidos) < " + String.valueOf(4) + " ";
            ResultSet resultSetCount = conx.consult(sql);
            listOfRecords = new ArrayList<ArrayList<String>>();
            while (resultSetCount.next()) {

                ResultSet resultSetF = conx.consult("select * from temp where id = " + resultSetCount.getString(1));
                resultSetF.next();
                newRow = new ArrayList<String>();
                for (int k = 0; k < resultSetF.getMetaData().getColumnCount() - 1; k++) {
                    if (resultSetF.getString(k + 1) != null) {
                        newRow.add(resultSetF.getString(k + 1));
                    } else {
                        newRow.add(" ");
                    }
                }
                listOfRecords.add(newRow);
            }
            dinamicTable2 = new DinamicTable(listOfRecords, titles);
            conx.disconnect();
        } catch (SQLException ex) {
            System.out.println("Error en la creacion de columnas dinamicas: " + ex.toString());
        }

    }

    public void seeDuplicateRecords() {
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public void btnDeleteClick() {
        //se lo elimina
        //btnDetectionClick();
    }

    public void btnDetectionClick() {
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            String sql = "";
            rowDataTableList = new ArrayList<RowDataTable>();
            ResultSet resultSetFileData = conx.consult("SELECT * FROM temp; ");

            while (resultSetFileData.next()) {

                //contamos el numero de registros que pueden ser posibles repeticiones
                //si supera la validacion loa agregamos a la lista
                sql = "";
                sql = sql + "SELECT ";
                sql = sql + "count(*) ";
//	t1.id as id,	
//	t1.nroid as identificacion1, 	
//	t1.nombres as nombre_1,	
//	t1.apellidos as apellido_1,	
//	t2.nroid as identificacion_2, 	
//	t2.nombres as nombre_2,
//	t2.apellidos as apellido_2,
//	levenshtein(t1.nombres, t2.nombres) as levestein_con_nombre,
//	levenshtein(t1.apellidos, t2.apellidos) as levestein_con_apellido,
//	levenshtein(t1.nroid, t2.nroid) as levestein_con_cedula
                sql = sql + "FROM ";
                sql = sql + "temp t1, temp t2 ";
                sql = sql + "WHERE ";
                sql = sql + "t1.id = " + resultSetFileData.getString("id") + " ";
                sql = sql + "AND t1.id != t2.id ";
                sql = sql + "AND levenshtein(t1.nroid, t2.nroid) < 6 ";
                sql = sql + "AND levenshtein(t1.nombres, t2.nombres) < 4 ";
                sql = sql + "AND levenshtein(t1.apellidos, t2.apellidos) < 4 ";
                ResultSet resultSetCount = conx.consult(sql);
                if (resultSetCount.next()) {
                    if (resultSetCount.getInt(1) != 0) {
                        rowDataTableList.add(new RowDataTable(
                                resultSetFileData.getString("nroid"),
                                resultSetFileData.getString("nombres") + " " + resultSetFileData.getString("apellidos"),
                                String.valueOf(resultSetCount.getInt(1)),
                                resultSetFileData.getString("id")));
                    }
                }

            }
            selectedRowDataTable = null;
            conx.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(DuplicateRecordsMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void btnResetDetectionClick() {
        rowDataTableList = new ArrayList<RowDataTable>();
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

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
//        try{
//        System.out.println("seteando tabla " + selectedRowDataTable.getColumn4());
//        }
//        catch (Exception e){
//            System.out.println("fallo seteando tabla " + e.toString());
//        }
    }

    public DinamicTable getDinamicTable1() {
        return dinamicTable1;
    }

    public void setDinamicTable1(DinamicTable dinamicTable1) {
        this.dinamicTable1 = dinamicTable1;
    }

    public DinamicTable getDinamicTable2() {
        return dinamicTable2;
    }

    public void setDinamicTable2(DinamicTable dinamicTable2) {
        this.dinamicTable2 = dinamicTable2;
    }
}
