package managedBeans.geo2;

import beans.connection.ConnectionJdbcMB;
import beans.util.Variable;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfGeometry;
import org.primefaces.event.RowEditEvent;

@ManagedBean(name = "geoDBConnectionMB")
@SessionScoped
public class GeoDBConnection implements Serializable {

    private ConnectionJdbcMB connectionJdbcMB;
    private String msj;
    private String url = "";
    String bd;
    String login;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    private String geo_column;
    private int user_id;
    private int indicator_id;
    private String vars;
    private ArrayList<Variable> order;
    private RangeFactory rf;
    private Integer bins;
    private ArrayList<Range> ranges;
    private ArrayList<Double> numbers;
    private int gap;
    private int splitMethod;
    private Ramp selectedRamp;
    private ArrayList<Ramp> ramps;
    private Color startColor;
    private Color middleColor;
    private Color endColor;

    public GeoDBConnection() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        conn = connectionJdbcMB.getConn();
        bins = 3;
        splitMethod = -1;
        this.selectedRamp = RampConverter.rampDB.get(0);
        this.startColor = Color.GREEN;
        this.middleColor = Color.YELLOW;
        this.endColor = Color.RED;
        ramps = RampConverter.rampDB;
    }

    public GeoDBConnection(String user, String pass, String host, String name) {
        url = "jdbc:postgresql://" + host + "/" + name;
        try {
            Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.out.println("Error1: " + e.toString() + " --- Clase: " + this.getClass().getName());
        }
        try {
            // Realizar la conexion
            conn = DriverManager.getConnection(url, user, pass);// Realizar la conexion
        } catch (SQLException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        bins = 3;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void crearURL() {
        try {
            ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
            String path = ((ServletContext) ext.getContext()).getContextPath();
            String cross = "";
            for (Variable var : order) {
                cross += var.getName() + ",";
            }
            ext.redirect(path + "/web/geo2/index.html?indicator_id=" + indicator_id + "&user_id=" + user_id + "&vars=" + cross + "&rf=" + rf.exportRanges());
        } catch (IOException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void refreshIndicatorData(int user_id, int indicator_id, ArrayList<Variable> order) {
        this.setUser_id(user_id);
        this.setIndicator_id(indicator_id);
        this.order = order;
        String column_order = "column_";

        int order2 = 1;
        for (Variable column : order) {
            if (column.getName().equalsIgnoreCase("barrio")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getTestNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("comuna")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getCommunesNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("cuadrante")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getQuadrantsNumbers();
                break;
            }
            if (column.getName().equalsIgnoreCase("corredor")) {
                column_order += order2;
                this.setGeo_column(column_order);
                this.bins = 3;
                this.getCorridorsNumbers();
                break;
            }
            order2++;
        }
        this.calculateGap();
        rf = new RangeFactory();
        rf.setNumbers(this.numbers);
        createRanges();
    }

    public void calculateGap() {
        Set<Double> uniqueNumbers = new HashSet<>(numbers);
        gap = uniqueNumbers.size();
        if (gap > 10) {
            gap = 10;
        }
        if (gap < 1) {
            gap = 1;
        }
    }

    public void createRanges() {
        rf.setBins(this.bins);
        rf.setSplitMethod(splitMethod);
        rf.setSelectedRamp(selectedRamp);
        rf.createRanges();
        this.ranges = rf.getRanges();
    }

    public void onEdit(RowEditEvent event) {
        System.out.println(((Range) event.getObject()).getLabel());
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

    /*
     * New methods for geo!!!
     */
    public List<MfFeature> getPolygons(RangeFactory rf) {
        // indicator_records
        String query = "SELECT "
                + "        record_id, neighborhood_name, count, geom "
                + " FROM "
                + "         neighborhoods "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE neighborhood_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("neighborhood_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<Double> getCommunesNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         communes "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE commune_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MfFeature> getCommunesPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "        record_id, commune_name, count, geom "
                + " FROM "
                + "         communes "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE commune_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("commune_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<Double> getTestNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         neighborhoods "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE neighborhood_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        ResultSet records = this.consult(query);
        System.out.println(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MfFeature> getNeighborhoodPolygons() {
        String query = ""
                + "SELECT "
                + "	osm_id, name, the_geom "
                + "FROM "
                + "	barrios_900913";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("osm_id");
                final String fname = records.getString("name");
                String geom = records.getString("the_geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<MfFeature> getFeaturesPolygons(String f) {
        if(f.compareToIgnoreCase("comunas") == 0){
            f = "commune";
        } else if(f.compareToIgnoreCase("corredores") == 0){
            f = "corridor";
        }else if(f.compareToIgnoreCase("cuadrantes") == 0){
            f = "quadrant";
        }
        String query = ""
                + "SELECT "
                + "	" + f + "_id AS id, " + f + "_name AS name, geom "
                + "FROM "
                + "	" + f + "s";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("id");
                final String fname = records.getString("name");
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<Double> getQuadrantsNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         quadrants "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE quadrant_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MfFeature> getQuadrantsPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "        record_id, quadrant_name, count, geom "
                + " FROM "
                + "         quadrants "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE quadrant_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("quadrant_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ArrayList<Double> getCorridorsNumbers() {
        numbers = new ArrayList<>();
        String query = "SELECT "
                + "        count "
                + " FROM "
                + "         corridors "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE corridor_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count";
        System.out.println(query);
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                numbers.add(new Double(records.getString("count")));
            }
            return numbers;
        } catch (SQLException ex) {
            Logger.getLogger(GeoDBConnection.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<MfFeature> getCorridorsPolygons(RangeFactory rf) {
        String query = "SELECT "
                + "        record_id, corridor_name, count, geom "
                + " FROM "
                + "         corridors "
                + " INNER JOIN "
                + "         (SELECT "
                + "                 min(record_id) AS record_id, " + geo_column + ", sum(count) AS count "
                + "         FROM "
                + "                 indicators_records "
                + "         WHERE "
                + "                 count > 0 "
                + "                 AND user_id = " + user_id + " "
                + "                 AND indicator_id = " + indicator_id + " "
                + "         GROUP BY "
                + "                 " + geo_column + " "
                + "         ORDER BY "
                + "                 2) AS foo "
                + " ON 	"
                + "      (" + geo_column + " LIKE corridor_name)  "
                + " WHERE "
                + "         geom IS NOT NULL "
                + " ORDER BY "
                + "         count DESC";
        WKTReader wktReader = new WKTReader();
        List<MfFeature> polygons = new ArrayList<>();
        ResultSet records = this.consult(query);
        try {
            while (records.next()) {
                final int fid = records.getInt("record_id");
                final String fname = records.getString("corridor_name");
                final Double fvalue = new Double(records.getInt("count"));
                final String fcolour = "#" + rf.getColorByValue(fvalue);
                String geom = records.getString("geom");
                final MfGeometry fgeom = new MfGeometry(wktReader.read(geom));
                MfFeature feature = new MfFeature() {
                    String name = fname;
                    private double value = fvalue;
                    private String colour = fcolour;
                    private Integer id = fid;

                    @Override
                    public String getFeatureId() {
                        return id.toString();
                    }

                    @Override
                    public MfGeometry getMfGeometry() {
                        return fgeom;
                    }

                    @Override
                    public void toJSON(JSONWriter writer) throws JSONException {
                        writer.key("name");
                        writer.value(name);
                        writer.key("value");
                        writer.value(value);
                        writer.key("colour");
                        writer.value(colour);
                        writer.key("id");
                        writer.value(id);
                    }
                };
                polygons.add(feature);
            }
            return polygons;
        } catch (ParseException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getPieData(String WHERE, String geo_column, String column, int user_id, int indicator_id) {
        String query = "SELECT "
                + "	min(record_id) AS id, " + column + " AS label, sum(count) AS count "
                + "FROM "
                + "	indicators_records "
                + "WHERE "
                + "	" + geo_column + " IN " + WHERE + " "
                + "     AND user_id = " + user_id + " "
                + "     AND indicator_id = " + indicator_id + ""
                + "GROUP BY "
                + "	" + column + " "
                + "ORDER BY "
                + "	1";
        ResultSet records = this.consult(query);
        try {
            JSONObject obj = new JSONObject();
            obj.put("title", "a name for this data");
            JSONArray values = new JSONArray();
            JSONArray labels = new JSONArray();
            while (records.next()) {
                String label = records.getString("label");
                int count = records.getInt("count");
                if (count != 0) {
                    labels.put(label + " %% [###]");
                    values.put(count);
                }
            }
            obj.put("labels", labels);
            obj.put("values", values);
            return obj.toString();
        } catch (JSONException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "EPIC FAIL!!!";
        }
    }

    public String getMapName(int indicator_id) {
        try {
            String query = "SELECT indicator_name FROM indicators WHERE indicator_id=" + indicator_id;
            ResultSet records = this.consult(query);
            JSONObject obj = new JSONObject();
            while (records.next()) {
                String map_name = records.getString("indicator_name");
                obj.put("title", map_name);
            }
            return obj.toString();
        } catch (JSONException | SQLException ex) {
            Logger.getLogger(GeoDBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * @param user_id the user_id to set
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * @param indicator_id the indicator_id to set
     */
    public void setIndicator_id(int indicator_id) {
        this.indicator_id = indicator_id;
    }

    /**
     * @param vars the vars to set
     */
    public void setVars(String vars) {
        this.vars = vars;
    }

    public String getGeo_column() {
        return geo_column;
    }

    public void setGeo_column(String geo_column) {
        this.geo_column = geo_column;
    }

    public RangeFactory getRf() {
        return rf;
    }

    public void setRf(RangeFactory rf) {
        this.rf = rf;
    }

    public ArrayList<Range> getRanges() {
        return ranges;
    }

    public void setRanges(ArrayList<Range> ranges) {
        this.ranges = ranges;
    }

    public Integer getBins() {
        return bins;
    }

    public void setBins(Integer bins) {
        this.bins = bins;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public int getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(int splitMethod) {
        this.splitMethod = splitMethod;
        rf.setSplitMethod(splitMethod);
    }

    public Ramp getSelectedRamp() {
        return selectedRamp;
    }

    public void setSelectedRamp(Ramp selectedRamp) {
        this.selectedRamp = selectedRamp;
    }

    public ArrayList<Ramp> getRamps() {
        return ramps;
    }

    public void setRamps(ArrayList<Ramp> ramps) {
        this.ramps = ramps;
    }

    public Color getStartColor() {
        return startColor;
    }

    public void setStartColor(Color startColor) {
        this.startColor = startColor;
    }

    public Color getMiddleColor() {
        return middleColor;
    }

    public void setMiddleColor(Color middleColor) {
        this.middleColor = middleColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public void setEndColor(Color endColor) {
        this.endColor = endColor;
    }
}