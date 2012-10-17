/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.util.RowDataTable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.ActivitiesFacade;
import model.dao.UsersFacade;
import model.pojo.Activities;
import model.pojo.Users;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "usersMB")
@SessionScoped
public class UsersMB {

    /**
     * ACTIVIDADES DURANTE EL HECHO
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    UsersFacade usersFacade;
    private List<Users> usersList;
    private Users currentUser;
    private String name = "";
    private String newName = "";
    private String job = "";
    private String newJob = "";
    private String institution = "";
    private String newInstitution = "";
    private String telephone = "";
    private String newtelephone = "";
    private String email = "";
    private String newEmail = "";
    private String password = "";
    private String newPasword = "";
    private String address = "";
    private String newAddress = "";
    private String login = "";
    private String newLogin = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;

    /**
     * Creates a new instance of UsersMB
     */
    public UsersMB() {
        /*
         * edicion creacion y modificacion de usuarios del sistema
         *
         */
    }

    public void load() {
        currentUser = null;
        if (selectedRowDataTable != null) {
            currentUser = usersFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentUser != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            
            if (currentUser.getUserName() != null) {
                name = currentUser.getUserName();
            } else {
                name = "";
            }
            if (currentUser.getUserJob() != null) {
                job = currentUser.getUserJob();
            } else {
                job = "";
            }
            if (currentUser.getUserInstitution() != null) {
                institution = currentUser.getUserInstitution();
            } else {
                institution = "";
            }
            if (currentUser.getUserTelephone() != null) {
                institution = currentUser.getUserTelephone();
            } else {
                institution = "";
            }
            if (currentUser.getUserEmail() != null) {
                email = currentUser.getUserEmail();
            } else {
                email = "";
            }
            if (currentUser.getUserPassword() != null) {
                password = currentUser.getUserPassword();
            } else {
                password = "";
            }
            if (currentUser.getUserAddress() != null) {
                address = currentUser.getUserAddress();
            } else {
                address = "";
            }
            if (currentUser.getUserLogin() != null) {
                login = currentUser.getUserLogin();
            } else {
                login = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentUser != null) {
            usersFacade.remove(currentUser);
            currentUser = null;
            selectedRowDataTable = null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentUser != null) {
            if (name.trim().length() != 0) {
                name = name.toUpperCase();
                currentUser.setUserName(name);
                currentUser.setUserLogin(login);
                currentUser.setUserName(name);
                currentUser.setUserJob(job);
                currentUser.setUserInstitution(institution);
                currentUser.setUserTelephone(telephone);
                currentUser.setUserEmail(email);
                currentUser.setUserAddress(address);
                currentUser.setUserPassword(password);
                usersFacade.edit(currentUser);
                name = "";
                currentUser = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void saveRegistry() {
        if (newName.trim().length() != 0) {
            newName = newName.toUpperCase();
            Users newRegistry = new Users();
            newRegistry.setUserId(usersFacade.findMax() + 1);
            newRegistry.setUserLogin(newLogin);
            newRegistry.setUserName(newName);
            newRegistry.setUserJob(newJob);
            newRegistry.setUserInstitution(newInstitution);
            newRegistry.setUserTelephone(newtelephone);
            newRegistry.setUserEmail(newEmail);
            newRegistry.setUserAddress(newAddress);
            newRegistry.setUserPassword(newPasword);
            usersFacade.create(newRegistry);
            newRegistry();
            currentUser = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void newRegistry() {
        name = "";
        newName = "";
        job = "";
        newJob = "";
        institution = "";
        newInstitution = "";
        telephone = "";
        newtelephone = "";
        email = "";
        newEmail = "";
        password = "";
        newPasword = "";
        address = "";
        newAddress = "";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            usersList = usersFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (usersList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < usersList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        usersList.get(i).getUserId().toString(),
                        usersList.get(i).getUserLogin(),
                        usersList.get(i).getUserName(),
                        usersList.get(i).getUserJob(),
                        usersList.get(i).getUserInstitution(),
                        usersList.get(i).getUserTelephone(),
                        usersList.get(i).getUserEmail(),
                        usersList.get(i).getUserAddress()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        usersList = usersFacade.findAll();
        for (int i = 0; i < usersList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    usersList.get(i).getUserId().toString(),
                    usersList.get(i).getUserLogin(),
                    usersList.get(i).getUserName(),
                    usersList.get(i).getUserJob(),
                    usersList.get(i).getUserInstitution(),
                    usersList.get(i).getUserTelephone(),
                    usersList.get(i).getUserEmail(),
                    usersList.get(i).getUserAddress()));
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
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

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentActivitiy(Users currentUser) {
        this.currentUser = currentUser;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(String newInstitution) {
        this.newInstitution = newInstitution;
    }

    public String getNewJob() {
        return newJob;
    }

    public void setNewJob(String newJob) {
        this.newJob = newJob;
    }

    public String getNewPasword() {
        return newPasword;
    }

    public void setNewPasword(String newPasword) {
        this.newPasword = newPasword;
    }

    public String getNewtelephone() {
        return newtelephone;
    }

    public void setNewtelephone(String newtelephone) {
        this.newtelephone = newtelephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNewLogin() {
        return newLogin;
    }

    public void setNewLogin(String newLogin) {
        this.newLogin = newLogin;
    }
}
