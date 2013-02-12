package managedBeans.geo;

import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.Color;
import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;

public class GeoDBConnection implements Serializable {

    String bd;
    String login;
    String password;
    String url;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    String msj;
    private int max;
    private double avg;
    private double sd;

    public GeoDBConnection() {
    }

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
        login = "postgres";
        password = "1234";
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


    /*
     * New methods for geo!!!
     */
    public Map<String, String> getVariables() {
        try {
            Map<String, String> variables = new TreeMap<String, String>();
            String query = "SELECT name, field FROM cross_variables";
            ResultSet records = this.consult(query);
            while (records.next()) {
                variables.put(records.getString(1), records.getString(2));
            }
            return variables;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Map<String, Integer> getValues(String form, String variable) {
        try {
            String generic_table = this.getGenericTable(form, variable);
            Map<String, Integer> values = new LinkedHashMap<String, Integer>();
            String query = "SELECT DISTINCT CAST(" + variable + " AS int) "
                    + " FROM dataset "
                    + " WHERE " + variable + " <> '' "
                    + " ORDER BY 1";
            ResultSet records = this.consult(query);
            while (records.next()) {
                String id = records.getString(1);
                values.put(this.getValueFromGenericTable(generic_table, id),
                        Integer.parseInt(id));
            }
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String getGenericTable(String form, String variable) {
        try {
            String query = "SELECT generic_table "
                    + "FROM cross_variables "
                    + "WHERE form_id = '" + form + "' AND field = '" + variable + "'";
            ResultSet consult = this.consult(query);
            consult.next();
            return consult.getString(1);
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String getValueFromGenericTable(String generic_table, String id) {
        try {
            String query = "SELECT * FROM " + generic_table;
            ResultSet values = this.consult(query);
            while (values.next()) {
                if (values.getInt(1) == Integer.parseInt(id)) {
                    return values.getString(2);
                }
            }
            System.out.println("Valor no encontrado en la tabla generica " + table);
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private Map<String, Object> getMinMaxValues(String subquery) {
        try {
            Map<String, Object> values = new HashMap<String, Object>();
            String query = "SELECT min(value), max(value), avg(value), stddev(value) "
                    + " FROM (" + subquery + ") AS subquery";
            System.out.println(query);
            ResultSet consult = this.consult(query);
            consult.next();
            values.put("min", consult.getInt("min"));
            values.put("max", consult.getInt("max"));
            values.put("avg", consult.getDouble("avg"));
            values.put("sd", consult.getDouble("stddev"));
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<MfFeature> getPolygons(String variable, String value) {
        try {
            List<MfFeature> polygons = new ArrayList<MfFeature>();
            max = 0;
            avg = 0;
            sd = 0;
            String query;
            if (variable == null || value == null) {
                query = "SELECT osm_id, name, codbar, codcom, the_geom FROM barrios_900913";
            } else {
                query = "SELECT "
                        + "     barrios_900913.*, value "
                        + "FROM "
                        + "     (SELECT "
                        + "         " + variable + " AS name_value, codbar, count(*) AS value "
                        + "     FROM "
                        + "         dataset "
                        + "     WHERE "
                        + "         codbar <> '' AND " + variable + " <> '' "
                        + "     GROUP BY "
                        + "         1,2 "
                        + "     ORDER BY "
                        + "         1,2) AS prepivot "
                        + "JOIN "
                        + "     barrios_900913 "
                        + "USING "
                        + "     (codbar) "
                        + "WHERE "
                        + "     name_value = '" + value + "'";
                Map<String, Object> minMaxValues = this.getMinMaxValues(query);
                max = (Integer) minMaxValues.get("max");
                avg = (Double) minMaxValues.get("avg");
                sd = (Double) minMaxValues.get("sd");
            }
            WKTReader wktReader = new WKTReader();
            ColorRamp ramp = new ColorRamp();
            ResultSet records = this.consult(query);
            while (records.next()) {
                final double vvalue;
                final String ccolour;
                if (variable == null || value == null) {
                    vvalue = 0;
                    ccolour = "#ffffff";
                } else {
                    vvalue = records.getInt("value");
                    Color back = new Color(ramp.getRampedValueRGB((double) vvalue / (double) max));
                    String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
                    while (hexString.length() < 6) {
                        hexString = "0" + hexString;
                    }
                    ccolour = hexString;
                }
                final String nname = records.getString("name");
                final int oosm_id = records.getInt("osm_id");
                final int ccodbar = records.getInt("codbar");
                final int ccodcom = records.getInt("codcom");
                String the_geom = records.getString("the_geom");
                final MfGeometry mfGeometry = new MfGeometry(wktReader.read(the_geom));
                MfFeature feature = new MfFeature() {

                    String name = nname;
                    private double value = vvalue;
                    private String colour = ccolour;
                    private Integer id = oosm_id;
                    private Integer code = ccodbar;
                    private Integer suburb = ccodcom;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return mfGeometry;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("code");
                        writer.value(code);
                        writer.key("suburb");
                        writer.value(suburb);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<MfFeature> getCorredorPolygons(String variable, String value) {
        try {
            List<MfFeature> polygons = new ArrayList<MfFeature>();
            max = 0;
            avg = 0;
            sd = 0;
            String query;
            if (variable == null || value == null) {
                query = "SELECT corredores_id, corredores_name, corredores_geom FROM corredores_900913";
            } else {
                query = "SELECT"
                        + "	name_value, corredores_id, corredores_name, corredores_geom, value "
                        + "FROM "
                        + "	(SELECT "
                        + "	  " + variable + " AS name_value, corredor_id, count(*) AS value "
                        + "	FROM "
                        + "	 dataset "
                        + "	JOIN "
                        + "		barrios_corredores "
                        + "	ON "
                        + "		(codbar = code) "
                        + "	WHERE "
                        + "	 codbar <> '' AND  " + variable + "  <> '' "
                        + "	GROUP BY "
                        + "	 1,2"
                        + "	ORDER BY "
                        + "	 1,2) AS prepivot "
                        + "JOIN "
                        + "	corredores_900913 "
                        + "ON "
                        + "	(corredores_id = corredor_id) "
                        + "WHERE "
                        + "     name_value = '" + value + "'";
                Map<String, Object> minMaxValues = this.getMinMaxValues(query);
                max = (Integer) minMaxValues.get("max");
                avg = (Double) minMaxValues.get("avg");
                sd = (Double) minMaxValues.get("sd");
            }
            WKTReader wktReader = new WKTReader();
            ColorRamp ramp = new ColorRamp();
            ResultSet records = this.consult(query);
            while (records.next()) {
                final double vvalue;
                final String ccolour;
                if (variable == null || value == null) {
                    vvalue = 0;
                    ccolour = "#ffffff";
                } else {
                    vvalue = records.getInt("value");
                    Color back = new Color(ramp.getRampedValueRGB((double) vvalue / (double) max));
                    String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
                    while (hexString.length() < 6) {
                        hexString = "0" + hexString;
                    }
                    ccolour = hexString;
                }
                final String nname = records.getString("corredores_name");
                final int corr_id = records.getInt("corredores_id");
                String the_geom = records.getString("corredores_geom");
                final MfGeometry mfGeometry = new MfGeometry(wktReader.read(the_geom));
                MfFeature feature = new MfFeature() {

                    String name = nname;
                    private double value = vvalue;
                    private String colour = ccolour;
                    private Integer id = corr_id;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return mfGeometry;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<MfFeature> getComunaPolygons(String variable, String value) {
        try {
            List<MfFeature> polygons = new ArrayList<MfFeature>();
            max = 0;
            avg = 0;
            sd = 0;
            String query;
            if (variable == null || value == null) {
                query = "SELECT comuna_id, comuna_name, comuna_geom FROM comunas_900913";
            } else {
                query = "SELECT "
                        + "     comunas_900913.*, value "
                        + "FROM "
                        + "     (SELECT "
                        + "	  " + variable + "  AS name_value, neighborhood_suburb, count(*) AS value "
                        + "     FROM "
                        + "	 dataset "
                        + "     JOIN "
                        + "		neighborhoods "
                        + "     ON "
                        + "	(neighborhood_id = CAST(codbar AS int)) "
                        + "     WHERE "
                        + "	 codbar <> '' AND  " + variable + "  <> '' "
                        + "     GROUP BY "
                        + "	 1,2 "
                        + "     ORDER BY "
                        + "	 1,2) AS prepivot "
                        + "JOIN "
                        + "    comunas_900913 "
                        + "ON "
                        + "     (comuna_id = neighborhood_suburb) "
                        + "WHERE "
                        + "     name_value = '" + value + "'";
                Map<String, Object> minMaxValues = this.getMinMaxValues(query);
                max = (Integer) minMaxValues.get("max");
                avg = (Double) minMaxValues.get("avg");
                sd = (Double) minMaxValues.get("sd");
            }
            WKTReader wktReader = new WKTReader();
            ColorRamp ramp = new ColorRamp();
            ResultSet records = this.consult(query);
            while (records.next()) {
                final double vvalue;
                final String ccolour;
                if (variable == null || value == null) {
                    vvalue = 0;
                    ccolour = "#ffffff";
                } else {
                    vvalue = records.getInt("value");
                    Color back = new Color(ramp.getRampedValueRGB((double) vvalue / (double) max));
                    String hexString = Integer.toHexString(back.getRGB() & 0x00FFFFFF);
                    while (hexString.length() < 6) {
                        hexString = "0" + hexString;
                    }
                    ccolour = hexString;
                }
                final String nname = records.getString("comuna_name");
                final int corr_id = records.getInt("comuna_id");
                String the_geom = records.getString("comuna_geom");
                final MfGeometry mfGeometry = new MfGeometry(wktReader.read(the_geom));
                MfFeature feature = new MfFeature() {

                    String name = nname;
                    private double value = vvalue;
                    private String colour = ccolour;
                    private Integer id = corr_id;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return mfGeometry;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getSd() {
        return sd;
    }

    public void setSd(double sd) {
        this.sd = sd;
    }
}
