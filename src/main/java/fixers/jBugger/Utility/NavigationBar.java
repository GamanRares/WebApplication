package fixers.jBugger.Utility;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class NavigationBar {

    private String orientation = "orizontal";

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
}