/*
 * http://quebrandoparadigmas.com/?p=751
 */
package beans.util;

import beans.connection.ConnectionJdbcMB;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.login.LoginMB;

/**
 *
 * @author Desarrollo
 */
public class DefaultPhaseListener implements PhaseListener {

    private static final long serialVersionUID = -1065005858605121693L;

    @Override
    public void afterPhase(PhaseEvent event) {
        //System.out.println("AfterPhase: " + event.getPhaseId());
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        //System.out.println("BeforePhase: " + event.getPhaseId());
        FacesContext context = event.getFacesContext();
        ExternalContext ext = context.getExternalContext();
        String ctxPath = ((ServletContext) ext.getContext()).getContextPath();
        HttpSession session = (HttpSession) ext.getSession(false);
        boolean newSession = (session == null) || (session.isNew());//no sea nula ni sea nueva
        boolean postback = !ext.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;
        try {
            if (timedout) {//expiro sesion
                System.out.println("salida del programa por que expiro la session");
                ext.redirect(ctxPath + "/index.html?v=timeout");
                System.out.println("redireccion completa2");
            } 
        } catch (Throwable t) {
            System.out.println("Fallo al expirar sesion " + t.toString());
            throw new FacesException("Session timed out", t);
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
