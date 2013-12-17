/*
 * To change this template, choose Tools | Templates
 * and node_en the template in the editor.
 */
package managedBeans.configurations;

import beans.util.Document;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "helpMB")
@SessionScoped
public class HelpMB implements Serializable {

    //private ConnectionJdbcMB connectionJdbcMB;
    private TreeNode root;
    private String helpTitle = " ";

    public void onNodeSelect(NodeSelectEvent event) {
        /*
         * llama a la funcion js: handleMyNodeClick que se encuentra en cliente
         * y el contenido de la ayuda segun el item seleccionado en el arbol
         */
        Document documento = (Document) event.getTreeNode().getData();
        int pageId = 0;
        try {
            pageId = Integer.parseInt(documento.getId());
        } catch (Exception e) {
        }
        switch (pageId) {
            case 1:
                helpTitle = "1. Registro de datos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1.html')");
                break;
            case 11:
                helpTitle = "1.1 Conjuntos desde archivo";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/11.html')");
                break;
            case 111:
                helpTitle = "1.1.1 Proyectos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/111.html')");
                break;
            case 1111:
                helpTitle = "1.1.1.1 Proyecto actual";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1111.html')");
                break;
            case 1112:
                helpTitle = "1.1.1.2 Nuevo proyecto";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1112.html')");
                break;
            case 1113:
                helpTitle = "1.1.1.3 Proyectos Almacenados";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1113.html')");
                break;
            case 112:
                helpTitle = "1.1.2 Filtros";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/112.html')");
                break;
            case 1121:
                helpTitle = "1.1.2.1 Copiar columnas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1121.html')");
                break;
            case 1122:
                helpTitle = "1.1.2.2 Eliminar columnas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1122.html')");
                break;
            case 1123:
                helpTitle = "1.1.2.3 Filtrar registros";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1123.html')");
                break;
            case 1124:
                helpTitle = "1.1.1.4 Dividir columna";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1124.html')");
                break;
            case 1125:
                helpTitle = "1.1.2.5 Unir columnas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1125.html')");
                break;
            case 1126:
                helpTitle = "1.1.2.6 Renombrar valores";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1126.html')");
                break;
            case 1127:
                helpTitle = "1.1.2.7 Replicar registros";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1127.html')");
                break;
            case 1128:
                helpTitle = "1.1.2.8 Datos actuales";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1128.html')");
                break;
            case 1129:
                helpTitle = "1.1.2.9 Historial de filtros aplicados";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1129.html')");
                break;
            case 113:
                helpTitle = "1.1.3 Relaciones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/113.html')");
                break;
            case 1131:
                helpTitle = "1.1.3.1 Relación de variables";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1131.html')");
                break;
            case 1132:
                helpTitle = "1.1.2.2 Relación de valores";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1132.html')");
                break;
            case 114:
                helpTitle = "1.1.4 Procesamiento";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/114.html')");
                break;
            case 1141:
                helpTitle = "1.1.4.1 Validación de datos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1141.html')");
                break;
            case 1142:
                helpTitle = "1.1.4.2 Errores";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1142.html')");
                break;
            case 1143:
                helpTitle = "1.1.4.3 Historial de Correcciones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1143.html')");
                break;
            case 1144:
                helpTitle = "1.1.4.4 Registro de la información";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1144.html')");
                break;
            case 12:
                helpTitle = "1.2 Registro desde formularios";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/12.html')");
                break;
            case 121:
                helpTitle = "1.2.1 Homicidios";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/121.html')");
                break;
            case 122:
                helpTitle = "1.2.2 Muertes por accidentes de tránsito";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/122.html')");
                break;
            case 123:
                helpTitle = "1.2.3 Suicidios";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/123.html')");
                break;
            case 124:
                helpTitle = "1.2.4 Muertes Accidentales";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/124.html')");
                break;
            case 125:
                helpTitle = "1.2.5 Lesiones de causa externa no Fatales";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/125.html')");
                break;
            case 126:
                helpTitle = "1.2.6 Violencia Intrafamiliar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/126.html')");
                break;
            case 13:
                helpTitle = "1.3 Gestión de conjuntos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/13.html')");
                break;
            case 131:
                helpTitle = "1.3.1 Conjuntos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/131.html')");
                break;
            case 1311:
                helpTitle = "1.3.1.1 Nuevo";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1311.html')");
                break;
            case 1312:
                helpTitle = "1.3.1.2 Renombrar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1312.html')");
                break;
            case 1313:
                helpTitle = "1.3.1.3 Eliminar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1313.html')");
                break;
            case 1314:
                helpTitle = "1.3.1.4 Mostrar datos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1314.html')");
                break;
            case 1315:
                helpTitle = "1.3.1.5 Detectar duplicados";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1315.html')");
                break;
            case 1316:
                helpTitle = "1.3.4.6 Agrupar conjuntos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/1316.html')");
                break;
            case 132:
                helpTitle = "1.3.2 Agrupaciones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/132.html')");
                break;
            case 2:
                helpTitle = "2. Indicadores";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/2.html')");
                break;
            case 21:
                helpTitle = "2.1 Rango de fechas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/21.html')");
                break;
            case 22:
                helpTitle = "2.2 Variables a cruzar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/22.html')");
                break;
            case 23:
                helpTitle = "2.3 Tabla de resultados";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/23.html')");
                break;
            case 24:
                helpTitle = "2.4 Gráficos";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/24.html')");
                break;
            case 25:
                helpTitle = "2.5 Mapas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/25.html')");
                break;
            case 3:
                helpTitle = "3. Configuraciones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3.html')");
                break;
            case 31:
                helpTitle = "3.1 Variables Categóricas";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/31.html')");
                break;
            case 311:
                helpTitle = "3.1.1 Variables simples";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/311.html')");
                break;
            case 3111:
                helpTitle = "3.1.1.1 Nuevo";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3111.html')");
                break;
            case 3112:
                helpTitle = "3.1.1.2 Editar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3112.html')");
                break;
            case 3113:
                helpTitle = "3.1.1.3 Eliminar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3113.html')");
                break;
            case 312:
                helpTitle = "3.1.2 Variables Espaciales";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/312.html')");
                break;
            case 3121:
                helpTitle = "3.1.2.1 Nuevo";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3121.html')");
                break;
            case 3122:
                helpTitle = "3.1.2.2 Editar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3122.html')");
                break;
            case 3123:
                helpTitle = "3.1.2.3 Eliminar";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3123.html')");
                break;
            case 313:
                helpTitle = "3.1.3 Otras Variables";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/313.html')");
                break;
            case 3131:
                helpTitle = "3.1.3.1 Fuentes por Ficha";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3131.html')");
                break;
            case 3132:
                helpTitle = "3.1.3.2 Instituciones salud/receptoras";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/3132.html')");
                break;
            case 32:
                helpTitle = "3.2 Configurar Poblaciones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/32.html')");
                break;
            case 33:
                helpTitle = "3.3 Gestión de Usuarios";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/33.html')");
                break;
            case 34:
                helpTitle = "3.4 Copias de seguridad";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/34.html')");
                break;
            case 35:
                helpTitle = "3.5 Cierres";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/35.html')");
                break;
            case 4:
                helpTitle = "4. General";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/4.html')");
                break;
            case 41:
                helpTitle = "4.1 Interfaz general";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/41.html')");
                break;
            case 42:
                helpTitle = "4.2 Sesiones";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/42.html')");
                break;
            case 43:
                helpTitle = "4.3 Conexión inicial";
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/43.html')");
                break;
            default:
                RequestContext.getCurrentInstance().execute("handleMyNodeClick('help/default.html')");
                break;
        }

    }

    public HelpMB() {
//        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        root = new DefaultTreeNode("root", null);

        //----------------------------------------------------------------------
        TreeNode node_1 = new DefaultTreeNode(new Document("1. Registro de datos", "1", "Folder"), root);
        TreeNode node_1_1 = new DefaultTreeNode(new Document("1.1 Conjuntos desde archivo", "11", "Folder"), node_1);
        TreeNode node_1_1_1 = new DefaultTreeNode(new Document("1.1.1 Proyectos", "111", "Folder"), node_1_1);
        TreeNode node_1_1_1_1 = new DefaultTreeNode("document", new Document("1.1.1.1 Proyecto actual", "1111", "Word Document"), node_1_1_1);
        TreeNode node_1_1_1_2 = new DefaultTreeNode("document", new Document("1.1.1.2 Nuevo proyecto", "1112", "Word Document"), node_1_1_1);
        TreeNode node_1_1_1_3 = new DefaultTreeNode("document", new Document("1.1.1.3 Proyectos Almacenados", "1113", "Word Document"), node_1_1_1);
        TreeNode node_1_1_2 = new DefaultTreeNode(new Document("1.1.2 Filtros", "112", "Folder"), node_1_1);
        TreeNode node_1_1_2_1 = new DefaultTreeNode("document", new Document("1.1.2.1 Copiar columnas", "1121", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_2 = new DefaultTreeNode("document", new Document("1.1.2.2 Eliminar columnas", "1122", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_3 = new DefaultTreeNode("document", new Document("1.1.2.3 Filtrar registros", "1123", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_4 = new DefaultTreeNode("document", new Document("1.1.1.4 Dividir columna", "1124", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_5 = new DefaultTreeNode("document", new Document("1.1.2.5 Unir columnas", "1125", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_6 = new DefaultTreeNode("document", new Document("1.1.2.6 Renombrar valores", "1126", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_7 = new DefaultTreeNode("document", new Document("1.1.2.7 Replicar registros", "1127", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_8 = new DefaultTreeNode("document", new Document("1.1.2.8 Datos actuales", "1128", "Word Document"), node_1_1_2);
        TreeNode node_1_1_2_9 = new DefaultTreeNode("document", new Document("1.1.2.9 Historial de filtros aplicados", "1129", "Word Document"), node_1_1_2);
        TreeNode node_1_1_3 = new DefaultTreeNode(new Document("1.1.3 Relaciones", "113", "Folder"), node_1_1);
        TreeNode node_1_1_3_1 = new DefaultTreeNode("document", new Document("1.1.3.1 Relación de variables", "1131", "Word Document"), node_1_1_3);
        TreeNode node_1_1_3_2 = new DefaultTreeNode("document", new Document("1.1.2.2 Relación de valores", "1132", "Word Document"), node_1_1_3);

        TreeNode node_1_1_4 = new DefaultTreeNode(new Document("1.1.4 Procesamiento", "114", "Folder"), node_1_1);
        TreeNode node_1_1_4_1 = new DefaultTreeNode("document", new Document("1.1.4.1 Validación de datos", "1141", "Word Document"), node_1_1_4);
        TreeNode node_1_1_4_2 = new DefaultTreeNode("document", new Document("1.1.4.2 Errores", "1142", "Word Document"), node_1_1_4);
        TreeNode node_1_1_4_3 = new DefaultTreeNode("document", new Document("1.1.4.3 Historial de Correcciones", "1143", "Word Document"), node_1_1_4);
        TreeNode node_1_1_4_4 = new DefaultTreeNode("document", new Document("1.1.4.4 Registro de la información", "1144", "Word Document"), node_1_1_4);
        //1.2-------------------------------------------
        TreeNode node_1_2 = new DefaultTreeNode(new Document("1.2 Registro desde formularios", "12", "Folder"), node_1);
        TreeNode node_1_2_1 = new DefaultTreeNode("document", new Document("1.2.1 Homicidios", "121", "Word Document"), node_1_2);
        TreeNode node_1_2_2 = new DefaultTreeNode("document", new Document("1.2.2 Muertes por accidentes de tránsito", "122", "Word Document"), node_1_2);
        TreeNode node_1_2_3 = new DefaultTreeNode("document", new Document("1.2.3 Suicidios", "123", "Word Document"), node_1_2);
        TreeNode node_1_2_4 = new DefaultTreeNode("document", new Document("1.2.4 Muertes Accidentales", "124", "Word Document"), node_1_2);
        TreeNode node_1_2_5 = new DefaultTreeNode("document", new Document("1.2.5 Lesiones de causa externa no Fatales", "125", "Word Document"), node_1_2);
        TreeNode node_1_2_6 = new DefaultTreeNode("document", new Document("1.2.6 Violencia Intrafamiliar", "126", "Word Document"), node_1_2);

        TreeNode node_1_3 = new DefaultTreeNode(new Document("1.3 Gestión de conjuntos", "13", "Folder"), node_1);
        TreeNode node_1_3_1 = new DefaultTreeNode(new Document("1.3.1 Conjuntos", "131", "Folder"), node_1_3);
        TreeNode node_1_3_1_1 = new DefaultTreeNode("document", new Document("1.1.3.1 Nuevo", "1311", "Word Document"), node_1_3_1);
        TreeNode node_1_3_1_2 = new DefaultTreeNode("document", new Document("1.1.3.2 Renombrar", "1312", "Word Document"), node_1_3_1);
        TreeNode node_1_3_1_3 = new DefaultTreeNode("document", new Document("1.1.3.3 Eliminar", "1313", "Word Document"), node_1_3_1);
        TreeNode node_1_3_1_4 = new DefaultTreeNode("document", new Document("1.1.3.4 Mostrar datos", "1314", "Word Document"), node_1_3_1);
        TreeNode node_1_3_1_5 = new DefaultTreeNode("document", new Document("1.1.3.5 Detectar duplicados", "1315", "Word Document"), node_1_3_1);
        TreeNode node_1_3_1_6 = new DefaultTreeNode("document", new Document("1.1.3.6 Agrupar conjuntos", "1316", "Word Document"), node_1_3_1);

        TreeNode node_1_3_2 = new DefaultTreeNode("document", new Document("1.3.2 Agrupaciones", "132", "Word Document"), node_1_3);

        //----------------------------------------------------------------------
        TreeNode node_2 = new DefaultTreeNode(new Document("2. Indicadores", "2", "Folder"), root);
        TreeNode node_2_1 = new DefaultTreeNode("document", new Document("2.1 Rango de fechas", "21", "Word Document"), node_2);
        TreeNode node_2_2 = new DefaultTreeNode("document", new Document("2.2 Variables a cruzar", "22", "Word Document"), node_2);
        TreeNode node_2_3 = new DefaultTreeNode("document", new Document("2.3 Tabla de resultados", "23", "Word Document"), node_2);
        TreeNode node_2_4 = new DefaultTreeNode("document", new Document("2.4 Gráficos", "24", "Word Document"), node_2);
        TreeNode node_2_5 = new DefaultTreeNode("document", new Document("2.5 Mapas", "25", "Word Document"), node_2);

        //----------------------------------------------------------------------
        TreeNode node_3 = new DefaultTreeNode(new Document("3. Configuraciones", "3", "Folder"), root);
        TreeNode node_3_1 = new DefaultTreeNode(new Document("3.1 Variables Categóricas", "31", "Folder"), node_3);
        TreeNode node_3_1_1 = new DefaultTreeNode(new Document("3.1.1 Variables simples", "311", "Folder"), node_3_1);
        TreeNode node_3_1_1_1 = new DefaultTreeNode("document", new Document("3.1.1.1 Nuevo", "3111", "Folder"), node_3_1_1);
        TreeNode node_3_1_1_2 = new DefaultTreeNode("document", new Document("3.1.1.2 Editar", "3112", "Folder"), node_3_1_1);
        TreeNode node_3_1_1_3 = new DefaultTreeNode("document", new Document("3.1.1.3 Eliminar", "3113", "Folder"), node_3_1_1);
        TreeNode node_3_1_2 = new DefaultTreeNode(new Document("3.1.2 Variables Espaciales", "312", "Folder"), node_3_1);
        TreeNode node_3_1_2_1 = new DefaultTreeNode("document", new Document("3.1.2.1 Nuevo", "3121", "Folder"), node_3_1_2);
        TreeNode node_3_1_2_2 = new DefaultTreeNode("document", new Document("3.1.2.2 Editar", "3122", "Folder"), node_3_1_2);
        TreeNode node_3_1_2_3 = new DefaultTreeNode("document", new Document("3.1.2.3 Eliminar", "3123", "Folder"), node_3_1_2);
        TreeNode node_3_1_3 = new DefaultTreeNode(new Document("3.1.3 Otras Variables", "313", "Folder"), node_3_1);
        TreeNode node_3_1_3_1 = new DefaultTreeNode("document", new Document("3.1.3.1 Fuentes por Ficha", "3131", "Folder"), node_3_1_3);
        TreeNode node_3_1_3_2 = new DefaultTreeNode("document", new Document("3.1.3.2 Instituciones salud/receptoras", "3132", "Folder"), node_3_1_3);
        TreeNode node_3_2 = new DefaultTreeNode("document", new Document("3.2 Configurar Poblaciones", "32", "Folder"), node_3);
        TreeNode node_3_3 = new DefaultTreeNode("document", new Document("3.3 Gestión de Usuarios", "33", "Folder"), node_3);
        TreeNode node_3_4 = new DefaultTreeNode("document", new Document("3.4 Copias de seguridad", "34", "Folder"), node_3);
        TreeNode node_3_5 = new DefaultTreeNode("document", new Document("3.5 Cierres", "35", "Folder"), node_3);
        TreeNode node_3_6 = new DefaultTreeNode("document", new Document("3.6 Control de registros", "36", "Folder"), node_3);

        //----------------------------------------------------------------------
        TreeNode node_4 = new DefaultTreeNode(new Document("4. General", "4", "Folder"), root);
        TreeNode node_4_1 = new DefaultTreeNode("document", new Document("4.1 Interfaz general", "41", "Folder"), node_4);
        TreeNode node_4_2 = new DefaultTreeNode("document", new Document("4.2 Sesiones", "42", "Folder"), node_4);
        TreeNode node_4_3 = new DefaultTreeNode("document", new Document("4.3 Conexión inicial", "43", "Folder"), node_4);



    }

    public void reset() {
    }

    public TreeNode getRoot() {
        return root;
    }

    public String getHelpTitle() {
        return helpTitle;
    }

    public void setHelpTitle(String helpTitle) {
        this.helpTitle = helpTitle;
    }
}
