package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.Utility.UsernameGenerator;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.DatabaseEntitites.Role;
import fixers.jBugger.DatabaseEntitites.User;
import fixers.jBugger.Utility.MD5Encryption;
import lombok.Data;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Named
@RequestScoped
public class AddUserBackingBean implements Serializable {
    private String userFirstName;
    private String userLastName;
    private String userMobileNumber;
    private String userEmail;
    private String userPassword;
    private List<Role> userRoles;

    private ArrayList<User> users = new ArrayList<>();

    @Inject
    private UserEJB userEJB;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private UsernameGenerator usernameGenerator;

    public void saveUser() {

        String username = usernameGenerator.create(userFirstName, userLastName);
        String encryptedPassword = MD5Encryption.encrypt(this.userPassword);

        try {
            LocalDate now = LocalDate.now();
            String message = this.generateNotificationMessage(username);

            this.userEJB.addUser(this.userFirstName, this.userLastName, this.userMobileNumber, this.userEmail, encryptedPassword, this.userRoles, username);
            this.notificationEJB.sendNotificationToOneUser(username, now, message, NotificationTypeEnum.WELCOME_NEW_USER);
            this.clearData();

            GrowlMessage.sendMessage("Info !", "User added successfully");
        } catch (EJBException e) {
            ConstraintViolationException constraintViolation = (ConstraintViolationException) e.getCausedByException();
            constraintViolation.getConstraintViolations().forEach(err -> GrowlMessage.sendMessage("Error !", err.getMessage()));
        }

    }

    private void clearData() {

        this.userFirstName = null;
        this.userLastName = null;
        this.userMobileNumber = null;
        this.userEmail = null;
        this.userPassword = null;
        this.userRoles = null;
    }

    private String generateNotificationMessage(String username) {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        String commaSeparatedRoles = this.userRoles.stream().map(role -> role.getType().toString())
                .collect(Collectors.joining(","));

        stringBuilder.append("Welcome : ").append(username).append(newLine)
                .append("First Name : ").append(this.userFirstName).append(newLine)
                .append("Last Name : ").append(this.userLastName).append(newLine)
                .append("Email : ").append(this.userEmail).append(newLine)
                .append("Mobile Number : ").append(this.userMobileNumber).append(newLine)
                .append("Roles : ").append(commaSeparatedRoles).append(newLine)
                .append("Active : true");

        return stringBuilder.toString();

    }

    public void handleChange(AjaxBehaviorEvent event) {
        this.userMobileNumber = (String) ((UIOutput) event.getSource()).getValue();
    }

}
