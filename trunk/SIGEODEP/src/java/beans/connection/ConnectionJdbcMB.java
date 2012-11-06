/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.connection;

import beans.util.RowDataTable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.*;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.sql.DataSource;
import managedBeans.filters.FieldCount;
import managedBeans.filters.FilterConnection;
import managedBeans.filters.SqlTable;
import managedBeans.filters.ValueNewValue;
import managedBeans.login.LoginMB;
import model.dao.*;
import model.pojo.FatalInjuryMurder;
import model.pojo.MunicipalitiesPK;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "connectionJdbcMB")
@SessionScoped
public class ConnectionJdbcMB implements Serializable{
    @Resource(name = "jdbc/od")
    private DataSource ds;
    @EJB
    TagsFacade tagsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    CountriesFacade countriesFacade;
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
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;    
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");    
    public Connection conn;
    private String tableName="temp";//nombre de la tabla temp para carga masiva
    Statement st;
    ResultSet rs;
    String msj;
    /**
     * Creates a new instance of ConnectionJdbcMB
     */
    public ConnectionJdbcMB() {
    }

    public void connectToDb() {
        if (ds == null) {
            System.out.println("ERROR: No se obtubo data source");
        } else {
            try {                
                conn = ds.getConnection();
                if (conn == null) {
                    System.out.println("Error: No se obtubo conexion");
                } else {
                    System.out.println("Conexion por JDBC a base de datos SIGEODEP ... OK");
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginMB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection con) {
        this.conn = con;
    }
    
    public ResultSet consult(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                System.out.println("---- CONSULTA: "+query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }            
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage() + "---- CONSULTA:"+query;
            return null;
        }
    }

   

    public int non_query(String query) {
        msj = "";
        PreparedStatement stmt = null;
        int reg;
        reg = 0;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement(query);
                reg = stmt.executeUpdate();
                stmt.close();
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
        return reg;
    }

    public String insert(String Tabla, String elementos, String registro) {
        msj = "";
        int reg = 1;
        String success;
        try {
            if (conn != null) {
                st = conn.createStatement();
                st.execute("INSERT INTO " + Tabla + " (" + elementos + ") VALUES (" + registro + ")");
                if (reg > 0) {
                    success = "true";
                } else {
                    success = "false";
                }
                st.close();
            } else {
                success = "false";
                msj = "ERROR: There don't exist connection...";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            System.out.println("numero: " + e.getErrorCode());
            success = e.getMessage();
            msj = "ERROR: " + e.getMessage();
        }
        return success;
    }

    public void remove(String Tabla, String condicion) {
        msj = "";
        PreparedStatement stmt = null;
        int reg;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement("DELETE FROM " + Tabla + " WHERE " + condicion);
                reg = stmt.executeUpdate();
                if (reg > 0) {
                } else {
                }
                stmt.close();
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void update(String Tabla, String campos, String donde) {
        msj = "";
        PreparedStatement stmt = null;
        int reg;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement("UPDATE " + Tabla + " SET " + campos + " WHERE " + donde);
                reg = stmt.executeUpdate();
                if (reg > 0) {
                } else {
                }
                stmt.close();
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String mens) {
        msj = mens;
    }
    
    
    public RowDataTable loadFatalInjuryMurderRecord(String idVIctim) {
        //CARGO LOS DATOS DE UN REGISTRO DE LESION NO FATAL EN UNA FILA PARA LA TABLA
        FatalInjuryMurder currentFatalInjuryMurder =fatalInjuryMurderFacade.findByIdVictim(idVIctim);
        
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryMurder.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger() != null) {
                if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger() == true) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }


        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address


        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
                newRowDataTable.setColumn31(String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getSuburbId()));
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       

        //******residence_municipality
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryMurder.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryMurder.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryMurder.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
                newRowDataTable.setColumn30(String.valueOf(neighborhoodsFacade.find(currentFatalInjuryMurder.getFatalInjuries().getInjuryNeighborhoodId()).getSuburbId()));
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryMurder.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryMurder.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryMurder.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryMurder.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryMurder.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryMurder.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryMurder.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryMurder.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK = new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR HOMICIDIOS
        //------------------------------------------------------------

        //******murder_context_id
        try {
            if (currentFatalInjuryMurder.getMurderContextId() != null) {
                newRowDataTable.setColumn28(currentFatalInjuryMurder.getMurderContextId().getMurderContextName());
            }
        } catch (Exception e) {
        }
        //******weapon_type_id
        try {
            if (currentFatalInjuryMurder.getWeaponTypeId() != null) {
                newRowDataTable.setColumn29(currentFatalInjuryMurder.getWeaponTypeId().getWeaponTypeName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id
        return newRowDataTable;
    }

    
    
    /* METODOS DE FILTER CONNECTION */
    
    public int getTempRowCount() {
        try {
            String query = "SELECT count(*) FROM temp";
            ResultSet count = this.consult(query);
            count.next();
            return count.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public int getCurrentId() {
        try {
            String query = "SELECT id FROM temp ORDER BY id DESC";
            ResultSet count = this.consult(query);
            count.next();
            return count.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public List<String> getTempFields() {
        try {
            ArrayList<String> fields = new ArrayList<String>();
            String query = "SELECT column_name FROM information_schema.columns "
                    + " WHERE table_name = 'temp' AND column_name NOT LIKE 'id'"
                    + " ORDER BY ordinal_position";
            ResultSet field_names = consult(query);
            while (field_names.next()) {
                fields.add(field_names.getString(1));
            }
            return fields;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<String> getTempFieldsWithId() {
        try {
            ArrayList<String> fields = new ArrayList<String>();
            String query = "SELECT column_name FROM information_schema.columns "
                    + " WHERE table_name = 'temp'"
                    + " ORDER BY ordinal_position";
            ResultSet field_names = consult(query);
            while (field_names.next()) {
                fields.add(field_names.getString(1));
            }
            return fields;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<String> getFieldValues(String field) {
        try {
            ArrayList<String> values = new ArrayList<String>();
            String query = "SELECT " + field + " FROM temp GROUP BY 1 ORDER BY 1";
            ResultSet value_names = consult(query);
            while (value_names.next()) {
                values.add(value_names.getString(1));
            }
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<String> getFieldRecords(String field) {
        try {
            ArrayList<String> records = new ArrayList<String>();
            String query = "SELECT " + field + " FROM temp ORDER BY 1";
            ResultSet results = consult(query);
            while (results.next()) {
                records.add(results.getString(1));
            }
            return records;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void saveNewFields(List<String> headers, List<List<String>> records, String field) {
        try {
            Statement statement = conn.createStatement();
            // Crear las dos nuevas columnas
            for (int i = 1; i < headers.size(); i++) {
                String query = "ALTER TABLE temp ADD COLUMN " + headers.get(i) + " text;";
                statement.executeUpdate(query);
            }
            StringBuilder backup = new StringBuilder();
            // Almacenar un backup para revertir los cambiox
            for (int i = 1; i < headers.size(); i++) {
                String query = "ALTER TABLE temp DROP COLUMN " + headers.get(i) + ";";
                backup.append(query);
            }
            conn.setAutoCommit(false);
            // Actualizar las nuevas columnas con los nuevos valores que vienen en 'records'
            for (List<String> record : records) {
                String query = "UPDATE temp SET ";
                for (int i = 1; i < 3; i++) {
                    query += headers.get(i) + " = '" + record.get(i) + "',";
                }
                query = query.substring(0, query.length() - 1) + " ";
                query += "WHERE " + field + " = '" + record.get(0) + "';";
                // guardar el update en un batch
                statement.addBatch(query);
            }
            // Ejecutamos el batch
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            // Guardamos el backup en la base de datos
            saveBackup("Split", backup);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Guardamos un backup de los cambios realizados.
    private void saveBackup(String desc, StringBuilder backup) {
        String query = "INSERT INTO filter_backups (time_backup, description_backup, text_backup) ";
        query += "VALUES (LOCALTIMESTAMP, '" + desc + "', '" + backup.toString() + "')";
        non_query(query);
    }

    /*
     * Ejecuta una entrada del backup de acuerdo a la descripcion.
     */
    private void restoreBackup(String desc) {
        try {
            String query = "SELECT text_backup FROM filter_backups WHERE "
                    + "description_backup ILIKE '" + desc + "'";
            ResultSet restore = consult(query);
            while (restore.next()) {
                query = restore.getString(1);
                non_query(query);
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Ejecuta la ultima entrada en la tabla de backups
     */
    public void undo(String desc) {
        try {
            String undo = "SELECT id_backup, text_backup "
                    + "FROM filter_backups "
                    + "WHERE description_backup LIKE '" + desc + "' "
                    + "ORDER BY id_backup DESC;";
            ResultSet result = this.consult(undo);
            result.next();
            String query = result.getString("text_backup");
            this.non_query(query);
            int id = result.getInt("id_backup");
            String delete = "DELETE FROM filter_backups WHERE id_backup = " + id + ";";
            this.non_query(delete);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /*
     * Retorna una lista de registros de acuerdo a las columnas pasadas como
     * parametro.
     */
    public List<List<String>> mergeColumns(List<String> fields, String delimiter, String fieldname) {
        try {
            String alter = "ALTER TABLE temp ADD COLUMN " + fieldname + " text;";
            this.non_query(alter);
            StringBuilder undo = new StringBuilder();
            undo.append("ALTER TABLE temp DROP COLUMN ").append(fieldname).append(";");
            List<List<String>> data = new ArrayList<List<String>>();
            String columns = "";
            for (String field : fields) {
                columns += field + ",";
            }
            columns = columns.substring(0, columns.length() - 1);
            String query = "SELECT id, " + columns + " FROM temp ORDER BY id";
            ResultSet records = this.consult(query);
            int ncol = records.getMetaData().getColumnCount();
            ArrayList<String> record;
            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            while (records.next()) {
                StringBuilder update = new StringBuilder();
                record = new ArrayList<String>();
                String merge_str = "";
                for (int i = 1; i <= ncol; i++) {
                    String value = records.getString(i);
                    record.add(value);
                    if (i != 1) {
                        merge_str += value + delimiter;
                    }
                }
                merge_str = merge_str.substring(0, merge_str.length() - delimiter.length());
                update.append("UPDATE temp SET ").append(fieldname).append("='");
                update.append(merge_str).append("' WHERE id = ");
                update.append(records.getInt("id")).append(";");
                statement.addBatch(update.toString());
                record.add(merge_str);
                data.add(record);
            }
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            this.saveBackup("Merge", undo);
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Retorna el listado de valores y su conteo para un campo determinado.
     */
    public List<List<String>> getFieldValuesWithCount(String field) {
        try {
            List<List<String>> data = new ArrayList<List<String>>();
            ArrayList<String> values;
            String query = "SELECT " + field + ", count(*) FROM temp GROUP BY 1 ORDER BY 2 DESC";
            ResultSet records = consult(query);
            int ncols = records.getMetaData().getColumnCount();
            while (records.next()) {
                values = new ArrayList<String>();
                for (int i = 1; i <= ncols; i++) {
                    values.add(records.getString(i));
                }
                data.add(values);
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Retorna una lista de tipo valor = frecuencia desde la tabla temp para un
     * campo determinado.
     */
    public List<FieldCount> getFieldCounts(String field) {
        try {
            List<FieldCount> data = new ArrayList<FieldCount>();
            String query = "SELECT " + field + ", count(*) FROM temp GROUP BY 1 ORDER BY 2 DESC";
            ResultSet records = consult(query);
            while (records.next()) {
                FieldCount fc = new FieldCount(records.getString(1), records.getInt(2));
                data.add(fc);
            }

            return data;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Borra los registros seleccionados por Filtrar
     */
    public void removeRecordsByFieldAndValue(String field, FieldCount[] values) {
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ");
        for (FieldCount value : values) {
            where.append(field).append(" LIKE '").append(value.getField()).append("'");
            where.append(" OR ");
        }
        where = new StringBuilder(where.substring(0, where.length() - 4));
        String temptable = "T" + System.currentTimeMillis();
        StringBuilder create = new StringBuilder();
        create.append("CREATE TABLE ").append(temptable).append(" AS ");
        create.append("SELECT * FROM temp ");
        create.append(where).append(";");
        this.non_query(create.toString());

        StringBuilder delete = new StringBuilder();
        delete.append("DELETE FROM temp ");
        delete.append(where).append(";");
        this.non_query(delete.toString());

        StringBuilder undo = new StringBuilder();
        undo.append("INSERT INTO temp ");
        undo.append("SELECT * FROM ").append(temptable).append(";\n");
        undo.append("DROP TABLE ").append(temptable).append(";");

        this.saveBackup("Filter", undo);

        System.out.println(create);
        System.out.println(delete);
        System.out.println(undo);
    }

    /*
     * Retorna los valores de un campo ordenados por su frecuencia
     */
    public List<ValueNewValue> getValuesOrderedByFrecuency(String field) {
        try {
            List<ValueNewValue> values = new ArrayList<ValueNewValue>();
            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(field).append(", count(*) FROM temp ");
            query.append("GROUP BY 1 ORDER BY 2;");
            ResultSet rows = this.consult(query.toString());
            while (rows.next()) {
                values.add(new ValueNewValue(rows.getString(1), ""));
            }
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void saveNewValuesForField(String field, List<ValueNewValue> model) {
        try {
            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            StringBuilder create = new StringBuilder();
            String temptable = "T" + System.currentTimeMillis();
            create.append("CREATE TABLE ").append(temptable).append(" AS SELECT id,").append(field).append(" FROM temp;\n");
            statement.addBatch(create.toString());
            StringBuilder insert = new StringBuilder();
            insert.append("INSERT INTO filter_tables VALUES('").append(temptable).append("');");
            statement.addBatch(insert.toString());
            statement.executeBatch();
            conn.commit();
            StringBuilder undo = new StringBuilder();
            undo.append("UPDATE temp t1 SET ").append(field).append(" = t2.");
            undo.append(field).append(" FROM (SELECT * FROM ").append(temptable);
            undo.append(") t2 WHERE t1.id = t2.id;\n");
            undo.append("DROP TABLE ").append(temptable).append(";");
            this.saveBackup("Rename", undo);
            System.out.println(undo.toString());
            for (ValueNewValue values : model) {
                StringBuilder update = new StringBuilder();
                update.append("UPDATE temp SET ");
                update.append(field).append("='").append(values.getNewValue()).append("'");
                update.append(" WHERE ");
                update.append(field).append("='").append(values.getOldValue()).append("'");
                update.append(";\n");
                statement.addBatch(update.toString());
                System.out.println(update.toString());
            }
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Copia el contenido de la colunma field n veces usando prefix_n como nuevo
     * nombre
     */
    public void addCopies(String field, int n, String prefix) {
        try {
            String alter;
            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            for (int i = 1; i <= n; i++) {
                alter = "ALTER TABLE temp ADD COLUMN " + prefix + i + " text;";
                statement.addBatch(alter);
            }
            StringBuilder backup = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                String query = "ALTER TABLE temp DROP COLUMN " + prefix + i + ";\n";
                backup.append(query);
            }
            statement.executeBatch();
            conn.commit();
            String update;
            for (int i = 1; i <= n; i++) {
                update = "UPDATE temp SET " + prefix + i + " = " + field + ";";
                statement.addBatch(update);
            }
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            this.saveBackup("Copy", backup);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Elimina el conjunto de columnas representadas por la lista fields
     */
    public void deleteFields(List<String> fields) {
        try {
            StringBuilder create = new StringBuilder();
            String temptable = "T" + System.currentTimeMillis();
            create.append("CREATE TABLE ").append(temptable).append(" AS ");
            String names = "id,";
            for (String field : fields) {
                names += field + ",";
            }
            names = names.substring(0, names.length() - 1);
            create.append("SELECT ").append(names).append(" FROM temp;");
            this.non_query(create.toString());
            StringBuilder undo = new StringBuilder();
            for (String field : fields) {
                undo.append("ALTER TABLE temp ADD COLUMN ").append(field).append(" text;\n");
                undo.append("UPDATE temp t1 SET ").append(field).append(" = t2.");
                undo.append(field).append(" FROM (SELECT * FROM ").append(temptable);
                undo.append(") t2 WHERE t1.id = t2.id;\n");
                undo.append("DROP TABLE ").append(temptable).append(";\n");
            }

            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            for (String field : fields) {
                String delete = "ALTER TABLE temp DROP COLUMN " + field + ";";
                statement.addBatch(delete);
            }
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            this.saveBackup("Delete", undo);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cleanFilterAndBackupTables() {
        try {
            String query = "SELECT * FROM filter_tables;";
            ResultSet tables = this.consult(query);
            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            while (tables.next()) {
                query = "DROP TABLE IF EXISTS " + tables.getString(1);
                statement.addBatch(query);
            }
            statement.addBatch("TRUNCATE filter_tables;");
            statement.addBatch("TRUNCATE filter_backups;");
            statement.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Retorna una lista de los ids de aquellos registros que necesitan ser
     * replicados
     */
    String getIdsToReplicate(List<String> source) {
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT id,");
            for (String field : source) {
                query.append(field).append(",");
            }
            query = new StringBuilder(query.substring(0, query.length() - 1));
            query.append(" FROM temp WHERE ");
            for (String field : source) {
                query.append("trim(").append(field).append(") <> '' OR ");
            }
            query = new StringBuilder(query.substring(0, query.length() - 4));
            System.out.println(query);
            ResultSet records = consult(query.toString());
            String ids = "";
            while (records.next()) {
                ids += records.getString(1) + ",";
            }
            if (ids.length() != 0) {
                return ids.substring(0, ids.length() - 1);
            } else {
                return "";
            }
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void executeReplication(List<String> fields, List<String> target, List<String> source) {
        int id1 = this.getCurrentId();
        fields.removeAll(source);
        fields.removeAll(target);
        StringBuilder variables = new StringBuilder();
        for (String field : fields) {
            variables.append(field).append(",");
        }
        for (String field : target) {
            variables.append(field).append(",");
        }
        variables = new StringBuilder(variables.substring(0, variables.length() - 1));

        int nsource = source.size();
        int ntarget = target.size();
        int steps = nsource / ntarget;
        int x = 0;
        for (int i = 1; i <= steps; i++) {
            List<String> subsource = source.subList(x, x + ntarget);
            x += ntarget;
            StringBuilder values = new StringBuilder();
            for (String field : fields) {
                values.append(field).append(",");
            }
            for (String field : subsource) {
                values.append(field).append(",");
            }
            values = new StringBuilder(values.substring(0, values.length() - 1));

            String ids = this.getIdsToReplicate(subsource);
            if (ids.length() != 0) {
                StringBuilder insert = new StringBuilder();
                insert.append("INSERT INTO temp\n\t(id,").append(variables.toString());
                insert.append(")\nSELECT\n\t").append("CAST(").append(getCurrentId());
                insert.append(" + row_number() OVER (ORDER BY id) AS INTEGER) AS id,");
                insert.append("\n\t").append(values.toString()).append("\nFROM ");
                insert.append("\n\ttemp\nWHERE\n\tid IN (").append(ids).append(");");
                System.out.println(insert.toString());
                this.non_query(insert.toString());
            }
        }
        int id2 = this.getCurrentId();
        if (id1 < id2) {
            StringBuilder undo = new StringBuilder();
            String newids = "";
            for (int i = (id1 + 1); i <= id2; i++) {
                newids += i + ",";
            }
            newids = newids.substring(0, newids.length() - 1);
            undo.append("DELETE FROM temp WHERE id IN (").append(newids).append(");");
            System.out.println(undo.toString());
            this.saveBackup("Replicate", undo);
        }
    }

    public List<SqlTable> getListFromTempTable() {
        try {
            CtClass sqltable;
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(this.getClass()));
            CtClass parent = pool.get("managedBeans.filters.SqlTable");
            String classname = "managedBeans.filters.C" + System.currentTimeMillis();
            sqltable = pool.makeClass(classname);
            sqltable.setSuperclass(parent);
            int nfields = this.getTempFieldsWithId().size();
            for (int i = 1; i <= nfields; i++) {
                String field_name = "private String a" + i + ";";
                CtField field = CtField.make(field_name, sqltable);
                sqltable.addField(field);
                String set = "setA" + i;
                CtMethod setter = CtNewMethod.setter(set, field);
                sqltable.addMethod(setter);
                String get = "getA" + i;
                CtMethod getter = CtNewMethod.getter(get, field);
                sqltable.addMethod(getter);
            }
            sqltable.writeFile();
            Class clazz = sqltable.toClass();
            String query = "SELECT * FROM temp ORDER BY id";
            ResultSet records = this.consult(query);
            int ncols = records.getMetaData().getColumnCount();
            List<SqlTable> rows = new ArrayList<SqlTable>();
            while (records.next()) {
                SqlTable row = (SqlTable) clazz.newInstance();
                for (int i = 1; i <= ncols; i++) {
                    Method method = clazz.getMethod("setA" + i, new Class[]{String.class});
                    method.invoke(row, new Object[]{records.getString(i)});
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (InstantiationException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (InvocationTargetException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SecurityException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(SqlTable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (CannotCompileException ex) {
            Logger.getLogger(SqlTable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NotFoundException ex) {
            Logger.getLogger(SqlTable.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Devuelve una lista con el resultado del query.
     */
    public List<List> getListFromQuery(int first, int pageSize) {
        try {
            List<List> data = new ArrayList<List>();
            String query = "SELECT * FROM temp ORDER BY id "
                    + "LIMIT " + pageSize + " OFFSET " + first;
            ResultSet records = this.consult(query);
            int ncols = records.getMetaData().getColumnCount();
            while (records.next()) {
                List record = new ArrayList();
                for (int i = 1; i <= ncols; i++) {
                    record.add(records.getString(i));
                }
                data.add(record);
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(FilterConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    
    
    
}
