/*
 * http://quebrandoparadigmas.com/?p=751
 */
package beans.util;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.login.ApplicationControlMB;
import managedBeans.login.LoginMB;

/**
 *
 * @author santos
 */

/*
 * ingresa a esta clase cada que hay un cambio de fase
 * (fase = solicitud al servidor)
 */
public class DefaultPhaseListener implements PhaseListener {

    ApplicationControlMB applicationControlMB;
    private LoginMB loginMB;

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        ExternalContext ext = facesContext.getExternalContext();
        boolean continueProcces = true;
        //---------------------------------------------------------------------------
        //buscar ID session actual dentro de lista de sesiones en ApplicationControl
        //---------------------------------------------------------------------------
        try {
            applicationControlMB = (ApplicationControlMB) ext.getApplicationMap().get("applicationControlMB");
            loginMB = (LoginMB) facesContext.getApplication().evaluateExpressionGet(facesContext, "#{loginMB}", LoginMB.class);
            if (loginMB.isAutenticado()) {
                if (!applicationControlMB.findIdSession(loginMB.getIdSession())) {//System.out.println("salida del programa por que se inicio nueva sesion en otro equipo");
                    continueProcces = false;
                    loginMB.logout1();
                }
            }
        } catch (Exception e) {
        }

        //---------------------------------------------------------------------------
        //--se determina si hubo inactividad o no se a iniciado session
        //---------------------------------------------------------------------------        
        if (continueProcces) {
            String ctxPath = ((ServletContext) ext.getContext()).getContextPath();
            String currentPage = facesContext.getViewRoot().getViewId();
            boolean isLoginPage;
            isLoginPage = (currentPage.lastIndexOf("indexUser.xhtml") > -1);//determinar si es index para usuario del sistema
            if (!isLoginPage) {
                isLoginPage = (currentPage.lastIndexOf("indexInvited.xhtml") > -1);//determinar si es index para usuario invitado
            }
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
            
            if (!isLoginPage) {
                if (session == null) {
                    try {//System.out.println("salida del programa por que sesion es null" + ctxPath);
                        ext.redirect(ctxPath + "/index2.html?v=timeout");//System.out.println("enviado a: " + ctxPath + "/index.html?v=timeout");
                    } catch (Throwable t) {//System.out.println("Fallo al expirar sesion " + t.toString());
                        throw new FacesException("Session timed out", t);
                    }
                } else {
                    Object currentUser = session.getAttribute("username");
                    if (!isLoginPage && (currentUser == null || currentUser == "")) {
                        try {//System.out.println("salida del programa por que no hay usuario registrado" + ctxPath);
                            ext.redirect(ctxPath + "/index2.html?v=nosession");
                        } catch (Exception e) {//System.out.println("Fallo al expirar sesion " + e.toString());
                            throw new FacesException("Session no login", e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
