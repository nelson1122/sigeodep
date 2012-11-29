/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.util.Variable;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsDataMB")
@SessionScoped
public class IndicatorsDataMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
    @EJB
    IndicatorsFacade indicatorsFacade;
    Indicators currentIndicator;

    public IndicatorsDataMB() {
    }
    
    private Variable createVariable(String name, String table, String field, String generic_table){
        Variable newVariable=new Variable(name, table, field, generic_table);
        //cargo la lista de valores posibles
        
        return newVariable;
    }

    /*
     * retorna un listado de las variables que se pueden cruzar en determinado
     * indicador
     */
    public ArrayList<Variable> getVariablesIndicator(int indicatorId, int type) {
        // type=0 :  desagregacion tematica y geografica
        // type=1 :  desagregacion tematica 
        // type=2 :  desagregacion geografica        
        ArrayList<Variable> arrayReturn = new ArrayList<Variable>();
        Variable var;
        switch (indicatorId) {
            case 1:// 'NUMERO DE CASOS POR LESION FATAL                                 '
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 2:// PORCENTAJE DE CASOS POR LESION FATAL                                 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 3:// NUMERO DE CASOS POR LESION NO FATAL                                  
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 4:// PORCENTAJE DE CASOS POR LESION NO FATAL                                              
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 5:// NUMERO DE CASOS DE MUERTES ACCIDENTALES                              
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 6:// VARIACION DE CASOS DE MUERTES ACCIDENTALES                           
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 7:// VARIACION PORCENTUAL DE MUERTES ACCIDENTALES                         
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 8:// PROMEDIO DE CASOS DE MUERTES ACCIDENTALES                            
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 9:// TASA DE MUERTES ACCIDENTALES                                         
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 10:// TASA ESPECIFICA DE MUERTES ACCIDENTALES SEGUN VARIABLE DE CONFUSION 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 11:// PORCENTAJE DE CASOS DE MUERTES ACCIDENTALES SEGUN VARIABLE DE INTERE
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 12:// NUMERO DE CASOS DE HOMICIDIOS                                       
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 13:// VARIACION DE CASOS DE HOMICIDIOS                                    
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 14:// VARIACION PORCENTUAL DE HOMICIDIOS                                  
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 15:// PROMEDIO DE CASOS DE HOMICIDIOS                                     
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 16:// TASA DE HOMICIDIOS                                                  
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 17:// TASA ESPECIFICA DE HOMICIDIOS SEGUN VARIABLE DE CONFUSION           
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 18:// PORCENTAJE DE CASOS DE HOMICIDIOS SEGUN VARIABLE DE INTERES         
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 19:// NUMERO DE CASOS DE SUICIDIOS                                        
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 20:// VARIACION DE CASOS DE SUICIDIOS                                     
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 21:// VARIACION PORCENTUAL DE SUICIDIOS                                   
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 22:// PROMEDIO DE CASOS DE SUICIDIOS                                      
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 23:// TASA DE SUICIDIOS                                                   
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 24:// TASA ESPECIFICA DE SUICIDIOS SEGUN VARIABLE DE CONFUSION            
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 25:// PORCENTAJE DE CASOS DE SUICIDIOS SEGUN VARIABLE DE INTERES          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 26:// NUMERO DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO               
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 27:// VARIACION DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO            
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 28:// VARIACION PORCENTUAL DE MUERTES POR ACCIDENTES DE TRANSITO          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 29:// PROMEDIO DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO             
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 30:// TASA DE MUERTES POR ACCIDENTES DE TRANSITO                          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 31:// TASA ESPECIFICA DE MUERTES POR ACCIDENTES DE TRANSITO SEGUN VARIABLETES DE TRANSITO', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 32:// PORCENTAJE DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO SEGUN VARIENTES DE TRANSITO', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 33:// NUMERO DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA               
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 34:// VARIACION DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA            
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 35:// VARIACION PORCENTUAL DE VIOLENCIA INTERPERSONAL EN FAMILIA          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 36:// PROMEDIO DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA             
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 37:// TASA DE VIOLENCIA INTERPERSONAL EN FAMILIA                          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 38:// TASA ESPECIFICA DE VIOLENCIA INTERPERSONAL EN FAMILIA SEGUN VARIABLEONAL EN FAMILIA', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 39:// PORCENTAJE DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA SEGUN VARIRSONAL EN FAMILIA', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 40:// NUMERO DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES             
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 41:// VARIACION DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES          
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 42:// VARIACION PORCENTUAL DE LESIONES DE CAUSA EXTERNA NO FATALES        
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 43:// PROMEDIO DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES           
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 44:// TASA DE LESIONES DE CAUSA EXTERNA NO FATALES                        
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 45:// TASA ESPECIFICA DE LESIONES DE CAUSA EXTERNA NO FATALES SEGUN VARIAB EXTERNA NO FATALES', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 46:// PORCENTAJE DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES SEGUN VASA EXTERNA NO FATALES', 
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 47:// NUMERO DE CASOS DE VIOLENCIA NO INTENCIONAL                         
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 48:// VARIACION DE CASOS DE VIOLENCIA NO INTENCIONAL                      
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 49:// VARIACION PORCENTUAL DE VIOLENCIA NO INTENCIONAL                    
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 50:// PROMEDIO DE CASOS DE VIOLENCIA NO INTENCIONAL                       
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 51:// TASA DE VIOLENCIA NO INTENCIONAL                                    
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 52:// TASA ESPECIFICA DE VIOLENCIA NO INTENCIONAL SEGUN VARIABLE DE CONFUS
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 53:// PORCENTAJE DE CASOS DE VIOLENCIA NO INTENCIONAL SEGUN VARIABLE DE IN
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 54:// NUMERO DE CASOS DE VIOLENCIA AUTOINFLINGIDA                         
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 55:// VARIACION DE CASOS DE VIOLENCIA AUTOINFLINGIDA                      
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 56:// VARIACION PORCENTUAL DE VIOLENCIA AUTOINFLINGIDA                    
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 57:// PROMEDIO DE CASOS DE VIOLENCIA AUTOINFLINGIDA                       
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 58:// TASA DE VIOLENCIA AUTOINFLINGIDA                                    
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 59:// TASA ESPECIFICA DE VIOLENCIA AUTOINFLINGIDA SEGUN VARIABLE DE CONFUS
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
            case 60:// PORCENTAJE DE CASOS DE VIOLENCIA AUTOINFLINGIDA SEGUN VARIABLE DE INTERES
                if (type == 0 || type == 1) {                    
                    arrayReturn.add(createVariable("genero", "victims", "gender_id", "genders"));
                    arrayReturn.add(createVariable("edad", "victims", "victim_age", "age"));
                    arrayReturn.add(createVariable("hora", "fatal_injuries", "injury_time", "time"));
                    arrayReturn.add(createVariable("dia semana", "fatal_injuries", "injury_day_of_week", "days"));
                    arrayReturn.add(createVariable("mes", "fatal_injuries", "injury_date", "month"));
                    arrayReturn.add(createVariable("año", "fatal_injuries", "injury_date", "year"));
                }                
                if (type == 0 || type == 2) {
                    arrayReturn.add(createVariable("barrio", "fatal_injuries", "injury_neighborhood_id", "neighborhoods"));
                    arrayReturn.add(createVariable("comuna", "fatal_injuries", "'neighborhoods.suburb_id", "neighborhoods"));
                    arrayReturn.add(createVariable("corredor", "fatal_injuries", "neighborhoods.corridor_id", "corridor"));
                    arrayReturn.add(createVariable("zona", "fatal_injuries", "neighborhoods.neighborhood_type", "neighborhood_type"));
                }
                break;
        }
        return arrayReturn;
    }
}
