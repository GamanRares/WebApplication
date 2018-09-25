package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.DatabaseEntitites.Role;
import lombok.Data;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Data
@Named
@ViewScoped
public class MemorizeValues_BackingBean implements Serializable {

    private String oldFirstName;
    private String oldLastName;
    private String oldMobileNumber;
    private String oldEmail;
    private String oldPassword;
    private boolean oldIsActive;
    private List<Role> oldRoles;

}
