/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.forms;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.DepartamentsFacade;
import model.dao.DestinationsOfPatientFacade;
import model.dao.DiagnosesFacade;
import model.dao.HealthProfessionalsFacade;
import model.dao.MunicipalitiesFacade;
import model.dao.NeighborhoodsFacade;
import model.pojo.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "lcenfMB")
@SessionScoped
public class LcenfMB {

    private SelectItem[] healthInstitutions;
    private String currentHealthInstitution;
    private SelectItem[] identifications;
    private String currentIdentification;
    private SelectItem[] measuresOfAge;
    private String currentMeasureOfAge;
    private String currentDepartamentHome;
    private SelectItem[] departaments;
    private String currentMunicipalities;
    private SelectItem[] municipalities;
    private SelectItem[] destinationsPatient;
    private String currentDestinationPatient;
    private String txtCIE10_1;
    private String txtCIE10_2;
    private String txtCIE10_3;
    private String txtCIE10_4;
    private String idCIE10_1;
    private String idCIE10_2;
    private String idCIE10_3;
    private String idCIE10_4;
    private String currentGender;
    private String currentMedicalHistory;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private String currentNeighborhoodHome;
    private String currentNeighborhoodEvent;
    private String currentHealthProfessionals;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DiagnosesFacade diagnosesFacade;
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;

    /**
     * Creates a new instance of LcenfMB
     */
    public LcenfMB() {
    }

    public List<String> suggestNeighborhoods(String entered) {
        List<Neighborhoods> neighborhoodsList = neighborhoodsFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < neighborhoodsList.size(); i++) {
            if (neighborhoodsList.get(i).getNeighborhoodName().startsWith(entered)) {
                list.add(neighborhoodsList.get(i).getNeighborhoodName());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    public List<String> suggestCIE10(String entered) {
        List<Diagnoses> diagnosesesList = diagnosesFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < diagnosesesList.size(); i++) {
            if (diagnosesesList.get(i).getDiagnosisId().startsWith(entered)) {
                list.add(diagnosesesList.get(i).getDiagnosisId());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    public List<String> suggestHealthProfessionals(String entered) {
        List<HealthProfessionals> professionalsList = healthProfessionalsFacade.findAll();
        List<String> list = new ArrayList<String>();
        entered = entered.toUpperCase();
        int amount = 0;
        for (int i = 0; i < professionalsList.size(); i++) {
            if (professionalsList.get(i).getHealthProfessionalName().startsWith(entered)) {
                list.add(professionalsList.get(i).getHealthProfessionalName());
                amount++;
            }
            if (amount == 10) {
                break;
            }
        }
        return list;
    }

    public void reset() {
        try {
            ArrayList<String> category;
            //cargo las instituciones de salud
            category = formsAndFieldsDataMB.categoricalNameList("instisal", 0);
            healthInstitutions = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                healthInstitutions[i] = new SelectItem(category.get(i).toString());
            }
            //cargo los tipos de identificacion
            category = formsAndFieldsDataMB.categoricalNameList("tid", 0);
            identifications = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                identifications[i] = new SelectItem(category.get(i).toString());
            }
            //cargo las medidas de edad
            category = formsAndFieldsDataMB.categoricalNameList("medad", 0);
            measuresOfAge = new SelectItem[category.size()];
            for (int i = 0; i < category.size(); i++) {
                measuresOfAge[i] = new SelectItem(category.get(i).toString());
            }
            //cargo los destinos del paciente
            List<DestinationsOfPatient> destinationsList = destinationsOfPatientFacade.findAll();
            destinationsPatient = new SelectItem[destinationsList.size()];
            for (int i = 0; i < destinationsList.size(); i++) {
                destinationsPatient[i] = new SelectItem(destinationsList.get(i).getDestinationPatientName());
            }
            //cargo los departamentos
            List<Departaments> departamentsList = departamentsFacade.findAll();
            departaments = new SelectItem[departamentsList.size()];
            for (int i = 0; i < departamentsList.size(); i++) {
                departaments[i] = new SelectItem(departamentsList.get(i).getDepartamentName());
            }
            //cargo los municipios
            List<Municipalities> municipalitiesList = municipalitiesFacade.findAll();
            municipalities = new SelectItem[municipalitiesList.size()];
            for (int i = 0; i < municipalitiesList.size(); i++) {
                municipalities[i] = new SelectItem(municipalitiesList.get(i).getMunicipalityName());
            }

        } catch (Exception e) {
            System.out.println("*******************************************ERROR: " + e.toString());
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public String getCurrentHealthInstitution() {
        return currentHealthInstitution;
    }

    public void setCurrentHealthInstitution(String currentHealthInstitution) {
        this.currentHealthInstitution = currentHealthInstitution;
    }

    public SelectItem[] getHealthInstitutions() {
        return healthInstitutions;
    }

    public void setHealthInstitutions(SelectItem[] healthInstitutions) {
        this.healthInstitutions = healthInstitutions;
    }

    public FormsAndFieldsDataMB getFormsAndFieldsDataMB() {
        return formsAndFieldsDataMB;
    }

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

    public String getCurrentMedicalHistory() {
        return currentMedicalHistory;
    }

    public void setCurrentMedicalHistory(String currentMedicalHistory) {
        this.currentMedicalHistory = currentMedicalHistory;
    }

    public String getCurrentIdentification() {
        return currentIdentification;
    }

    public void setCurrentIdentification(String currentIdentification) {
        this.currentIdentification = currentIdentification;
    }

    public SelectItem[] getIdentifications() {
        return identifications;
    }

    public void setIdentifications(SelectItem[] identifications) {
        this.identifications = identifications;
    }

    public String getCurrentMeasureOfAge() {
        return currentMeasureOfAge;
    }

    public void setCurrentMeasureOfAge(String currentMeasureOfAge) {
        this.currentMeasureOfAge = currentMeasureOfAge;
    }

    public SelectItem[] getMeasuresOfAge() {
        return measuresOfAge;
    }

    public void setMeasuresOfAge(SelectItem[] measuresOfAge) {
        this.measuresOfAge = measuresOfAge;
    }

    public String getCurrentGender() {
        return currentGender;
    }

    public void setCurrentGender(String currentGender) {
        this.currentGender = currentGender;
    }

//    public SelectItem[] getGenders() {
//        return genders;
//    }
//
//    public void setGenders(SelectItem[] genders) {
//        this.genders = genders;
//    }
    public String getCurrentDepartamentHome() {
        return currentDepartamentHome;
    }

    public void setCurrentDepartamentHome(String currentDepartamentHome) {
        this.currentDepartamentHome = currentDepartamentHome;
    }

    public SelectItem[] getDepartaments() {
        return departaments;
    }

    public void setDepartaments(SelectItem[] departaments) {
        this.departaments = departaments;
    }

    public SelectItem[] getMunicipalities() {
        return municipalities;
    }

    public void setMunicipalities(SelectItem[] municipalities) {
        this.municipalities = municipalities;
    }

    public String getCurrentMunicipalities() {
        return currentMunicipalities;
    }

    public void setCurrentMunicipalities(String currentMunicipalities) {
        this.currentMunicipalities = currentMunicipalities;
    }

    public String getCurrentNeighborhoodEvent() {
        return currentNeighborhoodEvent;
    }

    public void setCurrentNeighborhoodEvent(String currentNeighborhoodEvent) {
        this.currentNeighborhoodEvent = currentNeighborhoodEvent;
    }

    public String getCurrentNeighborhoodHome() {
        return currentNeighborhoodHome;
    }

    public void setCurrentNeighborhoodHome(String currentNeighborhoodHome) {
        this.currentNeighborhoodHome = currentNeighborhoodHome;
    }

//    public void setCurrentCIE10_2(String currentCIE10_2) {
//        this.currentCIE10_2 = currentCIE10_2;
//        if (currentCIE10_2.trim().length() != 0) {
//            System.out.println("tecla----");
//            List<Diagnoses> diagnosesesList = diagnosesFacade.findAll();
//            for (int i = 0; i < diagnosesesList.size(); i++) {
//                if (diagnosesesList.get(i).getDiagnosisId().compareTo(currentCIE10_2) == 0) {
//                    txtCIE10_2 = diagnosesesList.get(i).getDiagnosisName();
//                    break;
//                }
//                else
//                {
//                    txtCIE10_2 ="";
//                }
//            }
//        }
//    }
    public String getCurrentHealthProfessionals() {
        return currentHealthProfessionals;
    }

    public void setCurrentHealthProfessionals(String currentHealthProfessionals) {
        this.currentHealthProfessionals = currentHealthProfessionals;
    }

    public String getCurrentDestinationPatient() {
        return currentDestinationPatient;
    }

    public void setCurrentDestinationPatient(String currentDestinationPatient) {
        this.currentDestinationPatient = currentDestinationPatient;
    }

    public SelectItem[] getDestinationsPatient() {
        return destinationsPatient;
    }

    public void setDestinationsPatient(SelectItem[] destinationsPatient) {
        this.destinationsPatient = destinationsPatient;
    }

    public String getTxtCIE10_1() {
        return txtCIE10_1;
    }

    public void setTxtCIE10_1(String txtCIE10_1) {
        this.txtCIE10_1 = txtCIE10_1;
    }

    public String getTxtCIE10_2() {
        return txtCIE10_2;
    }

    public void setTxtCIE10_2(String txtCIE10_2) {
        this.txtCIE10_2 = txtCIE10_2;
    }

    public String getIdCIE10_1() {
        return idCIE10_1;
    }

    public void setIdCIE10_1(String idCIE10_1) {
        this.idCIE10_1 = idCIE10_1;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_1);
        if (selectDiagnoses != null) {
            txtCIE10_1 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_1 = "";
        }
    }

    public String getIdCIE10_2() {
        return idCIE10_2;
    }

    public void setIdCIE10_2(String idCIE10_2) {
        this.idCIE10_2 = idCIE10_2;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_2);
        if (selectDiagnoses != null) {
            txtCIE10_2 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_2 = "";
        }
    }

    public String getIdCIE10_3() {
        return idCIE10_3;
    }

    public void setIdCIE10_3(String idCIE10_3) {
        this.idCIE10_3 = idCIE10_3;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_3);
        if (selectDiagnoses != null) {
            txtCIE10_3 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_3 = "";
        }
    }

    public String getIdCIE10_4() {
        return idCIE10_4;
    }

    public void setIdCIE10_4(String idCIE10_4) {
        this.idCIE10_4 = idCIE10_4;
        Diagnoses selectDiagnoses = diagnosesFacade.findByFormId(this.idCIE10_4);
        if (selectDiagnoses != null) {
            txtCIE10_4 = selectDiagnoses.getDiagnosisName();
        } else {
            txtCIE10_4 = "";
        }
    }

    public String getTxtCIE10_3() {
        return txtCIE10_3;
    }
    
    

    public String getTxtCIE10_4() {
        return txtCIE10_4;
    }

    public void setTxtCIE10_4(String txtCIE10_4) {
        this.txtCIE10_4 = txtCIE10_4;
    }
}
