package managedBeans.filters;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.*;

public class FilterConnection implements Serializable {

    String bd;
    String login;
    String password;
    String url;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    String msj;

    public void conexion() {
        bd = "";
        login = "";
        password = "";
        url = "jdbc:postgresql://localhost/";
        conn = null;
        st = null;
        rs = null;
    }

    public void connect() {
        msj = "";
        bd = "od";
        login = "and";
        password = "nancy";
        url = "jdbc:postgresql://" + "localhost" + "/" + bd;// Anadir a la url la bd user y contrasena
        try {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + url + " ... OK");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void conectar(String BD, String Usuario, String Contrasena, String servidor) {
        msj = "";
        bd = BD;
        login = Usuario;
        password = Contrasena;
        url = "jdbc:postgresql://" + servidor + "/" + bd;
        try {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + url + " ... OK");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void disconnect() {
        msj = "";
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("Se cerro conexion a: " + url + " ... OK");
                    msj = "Close conection " + url + " ... OK";
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
        }
    }

    public ResultSet consult(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
            return null;
        }
    }

    public ResultSet consult1(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = st.executeQuery(query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
            return null;
        }
    }

    public int non_query(String query) {
        msj = "";
        PreparedStatement stmt;
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
        PreparedStatement stmt;
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
        PreparedStatement stmt;
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

    void saveNewValuesForField(String field, List<ValueNewValue> model) {
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
}
