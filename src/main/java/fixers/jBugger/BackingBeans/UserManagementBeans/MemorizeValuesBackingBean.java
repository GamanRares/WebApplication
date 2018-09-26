package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.DatabaseEntitites.Role;
import lombok.Data;

import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Data
@Named
@ViewScoped
public class MemorizeValuesBackingBean implements Serializable {

    private String oldFirstName;
    private String oldLastName;
    private String oldMobileNumber;
    private String oldEmail;
    private String oldPassword;
    private boolean oldIsActive;
    private List<Role> oldRoles;
    private String oldAttachmentName;
    private byte[] oldAttachment;

    public void deleteAll() {
        this.oldFirstName = null;
        this.oldLastName = null;
        this.oldMobileNumber = null;
        this.oldEmail = null;
        this.oldPassword = null;
        this.oldRoles = null;
        this.oldAttachment = null;
        this.oldAttachmentName = null;
    }

}
