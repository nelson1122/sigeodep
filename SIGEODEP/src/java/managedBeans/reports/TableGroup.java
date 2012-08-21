/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.reports;

import java.util.ArrayList;

/**
 *
 * @author SANTOS
 */
public class TableGroup {
    
    
    private String nombreGrupo="";
    private ArrayList<String> nombresColumnas;
    private ArrayList<String> nombresFilas;
    
    private String[][] datos;

    public TableGroup() {
        reiniciar();
    }
    
    public void ingresarNombreColumna(String n){
        nombresColumnas.add(n);
    }
    public void ingresarNombreFila(String n){
        nombresFilas.add(n);
    }
    
    public final void reiniciar()
    {
        nombresColumnas=new ArrayList<String>();
        nombresFilas=new ArrayList<String>();
        nombreGrupo="";
    }

    public String[][] getDatos() {
        return datos;
    }

    public void setDatos(String datos[][]) {
        this.datos = datos;
    }

    public ArrayList<String> getNombresColumnas() {
        return nombresColumnas;
    }

    public void setNombresColumnas(ArrayList<String> nombresColumnas) {
        this.nombresColumnas = nombresColumnas;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public ArrayList<String> getNombresFilas() {
        return nombresFilas;
    }

    public void setNombresFilas(ArrayList<String> nombresFilas) {
        this.nombresFilas = nombresFilas;
    }
    
    
}
