package fixers.jBugger.Loggers;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class GrowlMessage {

    public static void sendMessage(String summary, String detail) {

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary, detail));

    }

}
