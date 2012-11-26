/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

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
    /*
     * retorna un listado de las variables que se pueden cruzar en determinado indicador
     */
    public ArrayList<String> getVariablesIndicator(int indicatorId,int type) {
        // type=0 :  desagregacion tematica y geografica
        // type=1 :  desagregacion tematica 
        // type=2 :  desagregacion geografica        
        switch (indicatorId) {
            case 1:// 'NUMERO DE CASOS POR LESION FATAL                                 '
                break;
            case 2:// PORCENTAJE DE CASOS POR LESION FATAL                                 
                break;
            case 3:// NUMERO DE CASOS POR LESION NO FATAL                                  
                break;
            case 4:// PORCENTAJE DE CASOS POR LESION NO FATAL                                              
                break;
            case 5:// NUMERO DE CASOS DE MUERTES ACCIDENTALES                              
                break;
            case 6:// VARIACION DE CASOS DE MUERTES ACCIDENTALES                           
                break;
            case 7:// VARIACION PORCENTUAL DE MUERTES ACCIDENTALES                         
                break;
            case 8:// PROMEDIO DE CASOS DE MUERTES ACCIDENTALES                            
                break;
            case 9:// TASA DE MUERTES ACCIDENTALES                                         
                break;
            case 10:// TASA ESPECIFICA DE MUERTES ACCIDENTALES SEGUN VARIABLE DE CONFUSION 
                break;
            case 11:// PORCENTAJE DE CASOS DE MUERTES ACCIDENTALES SEGUN VARIABLE DE INTERE
                break;
            case 12:// NUMERO DE CASOS DE HOMICIDIOS                                       
                break;
            case 13:// VARIACION DE CASOS DE HOMICIDIOS                                    
                break;
            case 14:// VARIACION PORCENTUAL DE HOMICIDIOS                                  
                break;
            case 15:// PROMEDIO DE CASOS DE HOMICIDIOS                                     
                break;
            case 16:// TASA DE HOMICIDIOS                                                  
                break;
            case 17:// TASA ESPECIFICA DE HOMICIDIOS SEGUN VARIABLE DE CONFUSION           
                break;
            case 18:// PORCENTAJE DE CASOS DE HOMICIDIOS SEGUN VARIABLE DE INTERES         
                break;
            case 19:// NUMERO DE CASOS DE SUICIDIOS                                        
                break;
            case 20:// VARIACION DE CASOS DE SUICIDIOS                                     
                break;
            case 21:// VARIACION PORCENTUAL DE SUICIDIOS                                   
                break;
            case 22:// PROMEDIO DE CASOS DE SUICIDIOS                                      
                break;
            case 23:// TASA DE SUICIDIOS                                                   
                break;
            case 24:// TASA ESPECIFICA DE SUICIDIOS SEGUN VARIABLE DE CONFUSION            
                break;
            case 25:// PORCENTAJE DE CASOS DE SUICIDIOS SEGUN VARIABLE DE INTERES          
                break;
            case 26:// NUMERO DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO               
                break;
            case 27:// VARIACION DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO            
                break;
            case 28:// VARIACION PORCENTUAL DE MUERTES POR ACCIDENTES DE TRANSITO          
                break;
            case 29:// PROMEDIO DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO             
                break;
            case 30:// TASA DE MUERTES POR ACCIDENTES DE TRANSITO                          
                break;
            case 31:// TASA ESPECIFICA DE MUERTES POR ACCIDENTES DE TRANSITO SEGUN VARIABLETES DE TRANSITO', 
                break;
            case 32:// PORCENTAJE DE CASOS DE MUERTES POR ACCIDENTES DE TRANSITO SEGUN VARIENTES DE TRANSITO', 
                break;
            case 33:// NUMERO DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA               
                break;
            case 34:// VARIACION DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA            
                break;
            case 35:// VARIACION PORCENTUAL DE VIOLENCIA INTERPERSONAL EN FAMILIA          
                break;
            case 36:// PROMEDIO DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA             
                break;
            case 37:// TASA DE VIOLENCIA INTERPERSONAL EN FAMILIA                          
                break;
            case 38:// TASA ESPECIFICA DE VIOLENCIA INTERPERSONAL EN FAMILIA SEGUN VARIABLEONAL EN FAMILIA', 
                break;
            case 39:// PORCENTAJE DE CASOS DE VIOLENCIA INTERPERSONAL EN FAMILIA SEGUN VARIRSONAL EN FAMILIA', 
                break;
            case 40:// NUMERO DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES             
                break;
            case 41:// VARIACION DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES          
                break;
            case 42:// VARIACION PORCENTUAL DE LESIONES DE CAUSA EXTERNA NO FATALES        
                break;
            case 43:// PROMEDIO DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES           
                break;
            case 44:// TASA DE LESIONES DE CAUSA EXTERNA NO FATALES                        
                break;
            case 45:// TASA ESPECIFICA DE LESIONES DE CAUSA EXTERNA NO FATALES SEGUN VARIAB EXTERNA NO FATALES', 
                break;
            case 46:// PORCENTAJE DE CASOS DE LESIONES DE CAUSA EXTERNA NO FATALES SEGUN VASA EXTERNA NO FATALES', 
                break;
            case 47:// NUMERO DE CASOS DE VIOLENCIA NO INTENCIONAL                         
                break;
            case 48:// VARIACION DE CASOS DE VIOLENCIA NO INTENCIONAL                      
                break;
            case 49:// VARIACION PORCENTUAL DE VIOLENCIA NO INTENCIONAL                    
                break;
            case 50:// PROMEDIO DE CASOS DE VIOLENCIA NO INTENCIONAL                       
                break;
            case 51:// TASA DE VIOLENCIA NO INTENCIONAL                                    
                break;
            case 52:// TASA ESPECIFICA DE VIOLENCIA NO INTENCIONAL SEGUN VARIABLE DE CONFUS
                break;
            case 53:// PORCENTAJE DE CASOS DE VIOLENCIA NO INTENCIONAL SEGUN VARIABLE DE IN
                break;
            case 54:// NUMERO DE CASOS DE VIOLENCIA AUTOINFLINGIDA                         
                break;
            case 55:// VARIACION DE CASOS DE VIOLENCIA AUTOINFLINGIDA                      
                break;
            case 56:// VARIACION PORCENTUAL DE VIOLENCIA AUTOINFLINGIDA                    
                break;
            case 57:// PROMEDIO DE CASOS DE VIOLENCIA AUTOINFLINGIDA                       
                break;
            case 58:// TASA DE VIOLENCIA AUTOINFLINGIDA                                    
                break;
            case 59:// TASA ESPECIFICA DE VIOLENCIA AUTOINFLINGIDA SEGUN VARIABLE DE CONFUS
                break;
            case 60:// PORCENTAJE DE CASOS DE VIOLENCIA AUTOINFLINGIDA SEGUN VARIABLE DE INTERES
                break;
        }
        return null;
    }
}
