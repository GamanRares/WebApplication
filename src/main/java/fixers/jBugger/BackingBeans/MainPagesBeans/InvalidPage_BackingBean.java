package fixers.jBugger.BackingBeans.MainPagesBeans;

import lombok.Data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;

@Data
@Named
@RequestScoped
public class InvalidPage_BackingBean implements Serializable {

    private String page;

    @PostConstruct
    public void init() {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

    }

}
