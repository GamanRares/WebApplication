package fixers.jBugger.BackingBeans.MainPagesBeans;

import lombok.Data;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Data
@Named
@SessionScoped
public class HomepageBackingBean implements Serializable {
    private String page;

    public String goTo(String page) {
        return page;
    }

    public void navigateToPage(String page) {
        this.page = page;
    }

}