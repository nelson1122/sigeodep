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
import managedBeans.forms.HomicideMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsHomicideMB")
@SessionScoped
public class RecordSetsHomicideMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjuryMurder currentFatalInjuryMurder;
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
    private HomicideMB homicideMB;
    private String openForm = "";
    private RecordSetsMB recordSetsMB;

    public RecordSetsHomicideMB() {
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
        homicideMB = (HomicideMB) context.getApplication().evaluateExpressionGet(context, "#{homicideMB}", HomicideMB.class);
        homicideMB.loadValues(tagsList, currentFatalInjuryMurder);
        openForm = "homicide";
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
            totalRegisters = totalRegisters + fatalInjuryMurderFacade.countFromTag(tagsList.get(i).getTagId());
        }
        System.out.println("Total de registros = "+String.valueOf(totalRegisters));
        //RECORRO CADA TAG Y CARGO UN LISTADO DE SUS REGISTROS
        List<FatalInjuryMurder> fatalInjuryMurderList;
        for (int i = 0; i < tagsList.size(); i++) {
            fatalInjuryMurderList = fatalInjuryMurderFacade.findFromTag(tagsList.get(i).getTagId());
            if (fatalInjuryMurderList != null) {
                for (int j = 0; j < fatalInjuryMurderList.size(); j++) {
                    rowDataTableList.add(loadValues(fatalInjuryMurderList.get(j)));
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
        createCell(cellStyle, row, 7, "AREA DEL HECHO");//"100">#{rowX.column24}</p:column>
        createCell(cellStyle, row, 8, "CLASE DE LUGAR");//"250">#{rowX.column17}</p:column>
        createCell(cellStyle, row, 9, "NUMERO DE VICTIMAS");//"100">#{rowX.column18}</p:column>                                
        createCell(cellStyle, row, 10, "NOMBRES APELLIDOS");//"400">#{rowX.column4}</p:column>
        createCell(cellStyle, row, 11, "SEXO");//"100">#{rowX.column8}</p:column>
        createCell(cellStyle, row, 12, "TIPO EDAD");//"100">#{rowX.column6}</p:column>
        createCell(cellStyle, row, 13, "EDAD");//"100">#{rowX.column7}</p:column>
        createCell(cellStyle, row, 14, "OCUPACION");//"100">#{rowX.column9}</p:column>                                
        createCell(cellStyle, row, 15, "TIPO IDENTIFICACION");//"200">#{rowX.column2}</p:column>
        createCell(cellStyle, row, 16, "IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
        createCell(cellStyle, row, 17, "EXTRANJERO");//"100">#{rowX.column5}</p:column>
        createCell(cellStyle, row, 18, "DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
        createCell(cellStyle, row, 19, "MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
        createCell(cellStyle, row, 20, "BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
        createCell(cellStyle, row, 21, "PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
        createCell(cellStyle, row, 22, "DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
        createCell(cellStyle, row, 23, "MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>                                        
        createCell(cellStyle, row, 24, "ARMA O CAUSA DE MUERTE");//"100">#{rowX.column29}</p:column>        
        createCell(cellStyle, row, 25, "CONTEXTO RELACIONADO CON EL HECHO");//"250">#{rowX.column28}</p:column>                                        
        createCell(cellStyle, row, 26, "NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
        createCell(cellStyle, row, 27, "NIVEL DE ALCOHOL");//"100">#{rowX.column21}</p:column>
        createCell(cellStyle, row, 28, "TIPO NIVEL DE ALCOHOL");//"100">#{rowX.column22}</p:column>



        for (int i = 0; i < rowDataTableList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, rowDataTableList.get(i).getColumn1());//"CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
            createCell(row, 1, rowDataTableList.get(i).getColumn23());//"CODIGO");//"100">#{rowX.column23}</p:column>
            createCell(row, 2, rowDataTableList.get(i).getColumn13());//"FECHA HECHO");//"100">#{rowX.column13}</p:column>
            createCell(row, 3, rowDataTableList.get(i).getColumn20());//"DIA EN SEMANA");//"100">#{rowX.column20}</p:column>                                
            createCell(row, 4, rowDataTableList.get(i).getColumn14());//"HORA HECHO");//"100">#{rowX.column14}</p:column>
            createCell(row, 5, rowDataTableList.get(i).getColumn15());//"DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
            createCell(row, 6, rowDataTableList.get(i).getColumn16());//"BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(row, 7, rowDataTableList.get(i).getColumn24());//"AREA DEL HECHO");//"100">#{rowX.column24}</p:column>
            createCell(row, 8, rowDataTableList.get(i).getColumn17());//"CLASE DE LUGAR");//"250">#{rowX.column17}</p:column>
            createCell(row, 9, rowDataTableList.get(i).getColumn18());//"NUMERO DE VICTIMAS");//"100">#{rowX.column18}</p:column>                                
            createCell(row, 10, rowDataTableList.get(i).getColumn4());//"NOMBRES APELLIDOS");//"400">#{rowX.column4}</p:column>
            createCell(row, 11, rowDataTableList.get(i).getColumn8());//"SEXO");//"100">#{rowX.column8}</p:column>
            createCell(row, 12, rowDataTableList.get(i).getColumn6());//"TIPO EDAD");//"100">#{rowX.column6}</p:column>
            createCell(row, 13, rowDataTableList.get(i).getColumn7());//"EDAD");//"100">#{rowX.column7}</p:column>
            createCell(row, 14, rowDataTableList.get(i).getColumn9());//"OCUPACION");//"100">#{rowX.column9}</p:column>                                
            createCell(row, 15, rowDataTableList.get(i).getColumn2());//"TIPO IDENTIFICACION");//"200">#{rowX.column2}</p:column>
            createCell(row, 16, rowDataTableList.get(i).getColumn3());//"IDENTIFICACION");//"100">#{rowX.column3}</p:column>                                
            createCell(row, 17, rowDataTableList.get(i).getColumn5());//"EXTRANJERO");//"100">#{rowX.column5}</p:column>
            createCell(row, 18, rowDataTableList.get(i).getColumn12());//"DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
            createCell(row, 19, rowDataTableList.get(i).getColumn11());//"MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
            createCell(row, 20, rowDataTableList.get(i).getColumn10());//"BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(row, 21, rowDataTableList.get(i).getColumn25());//"PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
            createCell(row, 22, rowDataTableList.get(i).getColumn26());//"DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
            createCell(row, 23, rowDataTableList.get(i).getColumn27());//"MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>                                        
            createCell(row, 24, rowDataTableList.get(i).getColumn29());//"ARMA O CAUSA DE MUERTE");//"100">#{rowX.column29}</p:column>        
            createCell(row, 25, rowDataTableList.get(i).getColumn28());//"CONTEXTO RELACIONADO CON EL HECHO");//"250">#{rowX.column28}</p:column>                                        
            createCell(row, 26, rowDataTableList.get(i).getColumn19());//"NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
            createCell(row, 27, rowDataTableList.get(i).getColumn21());//"NIVEL DE ALCOHOL");//"100">#{rowX.column21}</p:column>
            createCell(row, 28, rowDataTableList.get(i).getColumn22());//"TIPO NIVEL DE ALCOHOL");//"100">#{rowX.column22}</p:column>

        }

    }

    private RowDataTable loadValues(FatalInjuryMurder currentFatalInjuryMurder) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        btnEditDisabled = true;
        btnRemoveDisabled = true;
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
                newRowDataTable.setColumn5(currentFatalInjuryMurder.getFatalInjuries().getVictimId().getStranger().toString());
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

    public void load() {
        currentFatalInjuryMurder = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjuryMurder = fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<FatalInjuryMurder> fatalInjuryMurderList = new ArrayList<FatalInjuryMurder>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryMurderList.add(fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (fatalInjuryMurderList != null) {
                for (int j = 0; j < fatalInjuryMurderList.size(); j++) {
                    FatalInjuries auxFatalInjuries = fatalInjuryMurderList.get(j).getFatalInjuries();
                    Victims auxVictims = fatalInjuryMurderList.get(j).getFatalInjuries().getVictimId();
                    fatalInjuryMurderFacade.remove(fatalInjuryMurderList.get(j));
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
