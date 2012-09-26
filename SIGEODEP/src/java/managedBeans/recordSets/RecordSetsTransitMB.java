/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.TransitMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsTransitMB")
@SessionScoped
public class RecordSetsTransitMB implements Serializable {

    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    CountriesFacade countriesFacade;
    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjuryTraffic currentFatalInjuryTraffic;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
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
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    private List<RowDataTable> rowDataTableList;
    //private RowDataTable selectedRowDataTable;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private TransitMB transitMB;
    private String openForm = "";
    private RecordSetsMB recordSetsMB;

    public RecordSetsTransitMB() {
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String openForm() {
        return openForm;
    }

    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        transitMB = (TransitMB) context.getApplication().evaluateExpressionGet(context, "#{transitMB}", TransitMB.class);
        transitMB.loadValues(tagsList, currentFatalInjuryTraffic);
        openForm = "transit";
    }

    void loadValues(RowDataTable[] selectedRowsDataTableTags) {

        FacesContext context = FacesContext.getCurrentInstance();
        recordSetsMB = (RecordSetsMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsMB}", RecordSetsMB.class);
        recordSetsMB.setProgress(0);
        int totalRegisters = 0;
        int totalProcess = 0;

        selectedRowsDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<Tags>();
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            data = data + selectedRowsDataTableTags[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
        }
        //DETERMINO EL NUMERO DE REGISTROS 
        for (int i = 0; i < tagsList.size(); i++) {
            totalRegisters = totalRegisters + fatalInjuryTrafficFacade.countFromTag(tagsList.get(i).getTagId());
        }
        
        //RECORRO CADA TAG Y CARGO UN LISTADO DE SUS REGISTROS
        List<FatalInjuryTraffic> fatalInjuryTrafficList;
        for (int i = 0; i < tagsList.size(); i++) {
            fatalInjuryTrafficList = fatalInjuryTrafficFacade.findFromTag(tagsList.get(i).getTagId());
            if (fatalInjuryTrafficList != null) {
                for (int j = 0; j < fatalInjuryTrafficList.size(); j++) {
                    rowDataTableList.add(loadValues(fatalInjuryTrafficList.get(j)));
                    totalProcess++;
                    if (totalRegisters != 0) {
                        recordSetsMB.setProgress((int) (totalProcess * 100) / totalRegisters);
                    }
                }
            }
        }
    }

    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);
        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        

        createCell(cellStyle, row, 0, "CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "CODIGO");//"100">#{rowX.column23}</p:column>
        createCell(cellStyle, row, 2, "FECHA HECHO");//"100">#{rowX.column13}</p:column>
        createCell(cellStyle, row, 3, "DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
        createCell(cellStyle, row, 4, "HORA HECHO");//"100">#{rowX.column14}</p:column>
        createCell(cellStyle, row, 5, "DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
        createCell(cellStyle, row, 6, "BARRIO HECHO");//"250">#{rowX.column16}</p:column>
        createCell(cellStyle, row, 7, "AREA HECHO");//"100">#{rowX.column24}</p:column>
        createCell(cellStyle, row, 8, "TIPO DE VIA");//"100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 9, "CLASE DE ACCIDENTE");//"250">#{rowX.column38}</p:column>
        createCell(cellStyle, row, 10, "NUMERO VICTIMAS");//"100">#{rowX.column18}</p:column>
        createCell(cellStyle, row, 11, "NUMERO LESIONADOS");//"100">#{rowX.column34}</p:column>
        createCell(cellStyle, row, 12, "NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
        createCell(cellStyle, row, 13, "SEXO");//"100">#{rowX.column8}</p:column>
        createCell(cellStyle, row, 14, "TIPO EDAD");//"100">#{rowX.column6}</p:column>
        createCell(cellStyle, row, 15, "EDAD");//"100">#{rowX.column7}</p:column>
        createCell(cellStyle, row, 16, "OCUPACION");//"100">#{rowX.column9}</p:column>
        createCell(cellStyle, row, 17, "TIPO IDENTIFICACION");//"100">#{rowX.column2}</p:column>                                 
        createCell(cellStyle, row, 18, "IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
        createCell(cellStyle, row, 19, "EXTRANJERO");//"100">#{rowX.column5}</p:column>
        createCell(cellStyle, row, 20, "DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
        createCell(cellStyle, row, 21, "MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
        createCell(cellStyle, row, 22, "BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
        createCell(cellStyle, row, 23, "PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
        createCell(cellStyle, row, 24, "DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
        createCell(cellStyle, row, 25, "MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
        createCell(cellStyle, row, 26, "CARACTERISTICAS DE LA VICTIMA");//"200">#{rowX.column35}</p:column>
        createCell(cellStyle, row, 27, "MEDIDAS DE PROTECCION");//"250">#{rowX.column36}</p:column>
        createCell(cellStyle, row, 28, "TIPO VEHICULO VICTIMA");//"100">#{rowX.column39}</p:column>
        createCell(cellStyle, row, 29, "TIPO VEHICULO CONTRAPARTE 1");//"100">#{rowX.column28}</p:column>
        createCell(cellStyle, row, 30, "TIPO VEHICULO CONTRAPARTE 2");//"100">#{rowX.column29}</p:column>
        createCell(cellStyle, row, 31, "TIPO VEHICULO CONTRAPARTE 3");//"100">#{rowX.column30}</p:column>
        createCell(cellStyle, row, 32, "TIPO DE SERVICIO VICTIMA");//"100">#{rowX.column40}</p:column>        
        createCell(cellStyle, row, 33, "TIPO SERVICIO CONTRAPARTE 1");//"100">#{rowX.column31}</p:column>
        createCell(cellStyle, row, 34, "TIPO SERVICIO CONTRAPARTE 2");//"100">#{rowX.column32}</p:column>
        createCell(cellStyle, row, 35, "TIPO SERVICIO CONTRAPARTE 3");//"100">#{rowX.column33}</p:column>
        createCell(cellStyle, row, 36, "NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
        createCell(cellStyle, row, 37, "NIVEL DE ALCOHOL VICTIMA");//"100">#{rowX.column21}</p:column>
        createCell(cellStyle, row, 38, "TIPO NIVEL ALCOHOL VICTIMA");//"100">#{rowX.column22}</p:column>
        createCell(cellStyle, row, 39, "NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column41}</p:column>
        createCell(cellStyle, row, 40, "TIPO NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column42}</p:column>

        for (int i = 0; i < rowDataTableList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, rowDataTableList.get(i).getColumn1());//"CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
            createCell(row, 1, rowDataTableList.get(i).getColumn23());//"CODIGO");//"100">#{rowX.column23}</p:column>
            createCell(row, 2, rowDataTableList.get(i).getColumn13());//"FECHA HECHO");//"100">#{rowX.column13}</p:column>
            createCell(row, 3, rowDataTableList.get(i).getColumn20());//"DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
            createCell(row, 4, rowDataTableList.get(i).getColumn14());//"HORA HECHO");//"100">#{rowX.column14}</p:column>
            createCell(row, 5, rowDataTableList.get(i).getColumn15());//"DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
            createCell(row, 6, rowDataTableList.get(i).getColumn16());//"BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(row, 7, rowDataTableList.get(i).getColumn24());//"AREA HECHO");//"100">#{rowX.column24}</p:column>
            createCell(row, 8, rowDataTableList.get(i).getColumn37());//"TIPO DE VIA");//"100">#{rowX.column37}</p:column>
            createCell(row, 9, rowDataTableList.get(i).getColumn38());//"CLASE DE ACCIDENTE");//"250">#{rowX.column38}</p:column>
            createCell(row, 10, rowDataTableList.get(i).getColumn18());//"NUMERO VICTIMAS");//"100">#{rowX.column18}</p:column>
            createCell(row, 11, rowDataTableList.get(i).getColumn34());//"NUMERO LESIONADOS");//"100">#{rowX.column34}</p:column>
            createCell(row, 12, rowDataTableList.get(i).getColumn4());//"NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
            createCell(row, 13, rowDataTableList.get(i).getColumn8());//"SEXO");//"100">#{rowX.column8}</p:column>
            createCell(row, 14, rowDataTableList.get(i).getColumn6());//"TIPO EDAD");//"100">#{rowX.column6}</p:column>
            createCell(row, 15, rowDataTableList.get(i).getColumn7());//"EDAD");//"100">#{rowX.column7}</p:column>
            createCell(row, 16, rowDataTableList.get(i).getColumn9());//"OCUPACION");//"100">#{rowX.column9}</p:column>
            createCell(row, 17, rowDataTableList.get(i).getColumn2());//"TIPO IDENTIFICACION");//"100">#{rowX.column2}</p:column>                                 
            createCell(row, 18, rowDataTableList.get(i).getColumn3());//"IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
            createCell(row, 19, rowDataTableList.get(i).getColumn5());//"EXTRANJERO");//"100">#{rowX.column5}</p:column>
            createCell(row, 20, rowDataTableList.get(i).getColumn12());//"DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
            createCell(row, 21, rowDataTableList.get(i).getColumn11());//"MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
            createCell(row, 22, rowDataTableList.get(i).getColumn10());//"BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(row, 23, rowDataTableList.get(i).getColumn25());//"PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
            createCell(row, 24, rowDataTableList.get(i).getColumn26());//"DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
            createCell(row, 25, rowDataTableList.get(i).getColumn27());//"MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
            createCell(row, 26, rowDataTableList.get(i).getColumn35());//"CARACTERISTICAS DE LA VICTIMA");//"200">#{rowX.column35}</p:column>
            createCell(row, 27, rowDataTableList.get(i).getColumn36());//"MEDIDAS DE PROTECCION");//"250">#{rowX.column36}</p:column>
            createCell(row, 28, rowDataTableList.get(i).getColumn39());//"TIPO VEHICULO VICTIMA");//"100">#{rowX.column39}</p:column>
            createCell(row, 29, rowDataTableList.get(i).getColumn28());//"TIPO VEHICULO CONTRAPARTE 1");//"100">#{rowX.column28}</p:column>
            createCell(row, 30, rowDataTableList.get(i).getColumn29());//"TIPO VEHICULO CONTRAPARTE 2");//"100">#{rowX.column29}</p:column>
            createCell(row, 31, rowDataTableList.get(i).getColumn30());//"TIPO VEHICULO CONTRAPARTE 3");//"100">#{rowX.column30}</p:column>
            createCell(row, 32, rowDataTableList.get(i).getColumn40());//"TIPO DE SERVICIO VICTIMA");//"100">#{rowX.column40}</p:column>        
            createCell(row, 33, rowDataTableList.get(i).getColumn31());//"TIPO SERVICIO CONTRAPARTE 1");//"100">#{rowX.column31}</p:column>
            createCell(row, 34, rowDataTableList.get(i).getColumn32());//"TIPO SERVICIO CONTRAPARTE 2");//"100">#{rowX.column32}</p:column>
            createCell(row, 35, rowDataTableList.get(i).getColumn33());//"TIPO SERVICIO CONTRAPARTE 3");//"100">#{rowX.column33}</p:column>
            createCell(row, 36, rowDataTableList.get(i).getColumn19());//"NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
            createCell(row, 37, rowDataTableList.get(i).getColumn21());//"NIVEL DE ALCOHOL VICTIMA");//"100">#{rowX.column21}</p:column>
            createCell(row, 38, rowDataTableList.get(i).getColumn22());//"TIPO NIVEL ALCOHOL VICTIMA");//"100">#{rowX.column22}</p:column>
            createCell(row, 39, rowDataTableList.get(i).getColumn41());//"NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column41}</p:column>
            createCell(row, 40, rowDataTableList.get(i).getColumn42());//"TIPO NIVEL DE ALCOHOL CONTRAPARTE");//"100">#{rowX.column42}</p:column>

        }

    }

    private RowDataTable loadValues(FatalInjuryTraffic currentFatalInjuryT) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryT.getFatalInjuries().getFatalInjuryId().toString());

        //******type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger() != null) {
                newRowDataTable.setColumn5(currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger().toString());
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }


        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address


        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       

        //******residence_municipality
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
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
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryT.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getMinutes());
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
        if (currentFatalInjuryT.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryT.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryT.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryT.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryT.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryT.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryT.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryT.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin();
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
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
        //------------------------------------------------------------
        //cargar vehiculos de la contraparte
        List<CounterpartInvolvedVehicle> involvedVehiclesList = currentFatalInjuryT.getFatalInjuries().getCounterpartInvolvedVehicleList();
        if (involvedVehiclesList != null) {
            for (int i = 0; i < involvedVehiclesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn28(involvedVehiclesList.get(0).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn29(involvedVehiclesList.get(1).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn30(involvedVehiclesList.get(2).getInvolvedVehicleId().getInvolvedVehicleName());
                }
            }
        }

        //cargar tipo de servcio de la contraparte
        List<CounterpartServiceType> serviceTypesList = currentFatalInjuryT.getFatalInjuries().getCounterpartServiceTypeList();

        if (involvedVehiclesList != null) {
            for (int i = 0; i < serviceTypesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn31(serviceTypesList.get(0).getServiceTypeId().getServiceTypeName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn32(serviceTypesList.get(1).getServiceTypeId().getServiceTypeName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn33(serviceTypesList.get(2).getServiceTypeId().getServiceTypeName());
                }
            }
        }


        //******number_non_fatal_victims
        if (currentFatalInjuryT.getNumberNonFatalVictims() != null) {
            newRowDataTable.setColumn34(currentFatalInjuryT.getNumberNonFatalVictims().toString());
        }
        //******victim_characteristic_id
        if (currentFatalInjuryT.getVictimCharacteristicId() != null) {
            newRowDataTable.setColumn35(currentFatalInjuryT.getVictimCharacteristicId().getCharacteristicName());
        }
        //******protection_measure_id
        if (currentFatalInjuryT.getProtectionMeasureId() != null) {
            newRowDataTable.setColumn36(currentFatalInjuryT.getProtectionMeasureId().getProtectiveMeasuresName());
        }
        //******road_type_id
        if (currentFatalInjuryT.getRoadTypeId() != null) {
            newRowDataTable.setColumn37(currentFatalInjuryT.getRoadTypeId().getRoadTypeName());
        }
        //******accident_class_id
        if (currentFatalInjuryT.getAccidentClassId() != null) {
            newRowDataTable.setColumn38(currentFatalInjuryT.getAccidentClassId().getAccidentClassName());
        }
        //******involved_vehicle_id
        if (currentFatalInjuryT.getInvolvedVehicleId() != null) {
            newRowDataTable.setColumn39(currentFatalInjuryT.getInvolvedVehicleId().getInvolvedVehicleName());
        }
        //******service_type_id
        if (currentFatalInjuryT.getServiceTypeId() != null) {
            newRowDataTable.setColumn40(currentFatalInjuryT.getServiceTypeId().getServiceTypeName());
        }

        //******alcohol_level_counterpart
        try {
            if (currentFatalInjuryT.getAlcoholLevelCounterpart() != null) {
                newRowDataTable.setColumn41(currentFatalInjuryT.getAlcoholLevelCounterpart().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_counterpart_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn42(currentFatalInjuryT.getAlcoholLevelCounterpartId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******fatal_injury_id

        return newRowDataTable;
    }

    public void load() {
        currentFatalInjuryTraffic = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjuryTraffic = fatalInjuryTrafficFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
            }
            if (selectedRowsDataTable.length > 1) {
                
                btnEditDisabled = true;
                btnRemoveDisabled = false;
            } else {
                btnEditDisabled = false;
                btnRemoveDisabled = false;
            }
        }
    }

    public void deleteRegistry() {
        if (selectedRowsDataTable != null) {
            List<FatalInjuryTraffic> fatalInjuryTrafficList = new ArrayList<FatalInjuryTraffic>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryTrafficList.add(fatalInjuryTrafficFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (fatalInjuryTrafficList != null) {
                for (int j = 0; j < fatalInjuryTrafficList.size(); j++) {
                    FatalInjuries auxFatalInjuries = fatalInjuryTrafficList.get(j).getFatalInjuries();
                    Victims auxVictims = fatalInjuryTrafficList.get(j).getFatalInjuries().getVictimId();
                    fatalInjuryTrafficFacade.remove(fatalInjuryTrafficList.get(j));
                    fatalInjuriesFacade.remove(auxFatalInjuries);
                    victimsFacade.remove(auxVictims);
                }
            }
            //quito los elementos seleccionados de rowsDataTableList seleccion de 
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                for (int i = 0; i < rowDataTableList.size(); i++) {
                    if (selectedRowsDataTable[j].getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
                        rowDataTableList.remove(i);
                        break;
                    }
                }
            }
            //deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
