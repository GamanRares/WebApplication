package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.BackingBeans.MainPagesBeans.LoginBackingBean;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.RoleEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.*;
import lombok.Data;

import javax.annotation.PostConstruct;
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
public class EditUserBackingBean implements Serializable {

    @Inject
    private UserEJB userEJB;

    @Inject
    private BugEJB bugEJB;

    @Inject
    private LoginBackingBean loginBackingBean;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private RoleEJB roleEJB;

    @Inject
    private MemorizeValuesBackingBean memorizeValuesBackingBean;

    private String usernameToUpdate;

    private String userFirstName;
    private String userLastName;
    private String userMobileNumber;
    private String userEmail;
    private String userPassword;
    private boolean userIsActive = true;
    private List<Role> userRoles;

    private List<String> users = new ArrayList<>();

    @PostConstruct
    public void init() {

        this.userEJB.getUsers().forEach(user -> this.users.add(user.getUsername()));

    }

    public void saveData() {

        if (isUsernameSelected()) {
            try {
                LocalDate now = LocalDate.now();

                String message = this.generateNotificationMessage();

                this.userEJB.updateUser(usernameToUpdate, userFirstName, userLastName, userMobileNumber, userEmail, userIsActive, userPassword, userRoles);

                if (this.usernameToUpdate.equals(this.loginBackingBean.getUsername()))
                    this.notificationEJB.sendNotificationToOneUser(this.usernameToUpdate, now, message, NotificationTypeEnum.USER_UPDATED);
                else
                    this.notificationEJB.sendNotificationToTwoUsers(this.usernameToUpdate, this.loginBackingBean.getUsername(), now, message, NotificationTypeEnum.USER_UPDATED);

                GrowlMessage.sendMessage("Info !", "User updated successfully");
            } catch (EJBException e) {
                ConstraintViolationException constraintViolation = (ConstraintViolationException) e.getCausedByException().getCause();
                constraintViolation.getConstraintViolations().forEach(err -> GrowlMessage.sendMessage("Error !", err.getMessage()));
            }
        } else
            GrowlMessage.sendMessage("Error !", "You must select a user");

    }

    private void setValues(User user) {

        this.userFirstName = user.getFirstName();
        this.userLastName = user.getLastName();
        this.userMobileNumber = user.getMobileNumber();
        this.userEmail = user.getEmail();
        this.userPassword = user.getPassword();
        this.userRoles = new ArrayList<>(user.getRole());
        this.userIsActive = user.isActive();

        this.memorizeValuesBackingBean.setOldFirstName(this.userFirstName);
        this.memorizeValuesBackingBean.setOldLastName(this.userLastName);
        this.memorizeValuesBackingBean.setOldMobileNumber(this.userMobileNumber);
        this.memorizeValuesBackingBean.setOldEmail(this.userEmail);
        this.memorizeValuesBackingBean.setOldPassword(this.userPassword);
        this.memorizeValuesBackingBean.setOldRoles(this.userRoles);
        this.memorizeValuesBackingBean.setOldIsActive(this.userIsActive);

    }

    private boolean isUsernameSelected() {

        return this.usernameToUpdate != null && !this.usernameToUpdate.equals("");

    }

    private String generateNotificationMessage() {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        String newCommaSeparatedRoles = this.userRoles.stream().map(role -> role.getType().toString())
                .collect(Collectors.joining(","));
        String oldCommaSeparatedRoles = this.memorizeValuesBackingBean.getOldRoles().stream().map(role -> role.getType().toString())
                .collect(Collectors.joining(","));

        stringBuilder.append("New First Name : ").append(this.userFirstName).append(newLine)
                .append("New Last Name : ").append(this.userLastName).append(newLine)
                .append("New Email : ").append(this.userEmail).append(newLine)
                .append("New Mobile Number : ").append(this.userMobileNumber).append(newLine)
                .append("New Roles : ").append(newCommaSeparatedRoles).append(newLine)
                .append("New Active : ").append(this.userIsActive).append(newLine).append(newLine)
                .append("Old First Name : ").append(this.memorizeValuesBackingBean.getOldFirstName()).append(newLine)
                .append("Old Last Name : ").append(this.memorizeValuesBackingBean.getOldLastName()).append(newLine)
                .append("Old Mobile Number : ").append(this.memorizeValuesBackingBean.getOldMobileNumber()).append(newLine)
                .append("Old Email : ").append(this.memorizeValuesBackingBean.getOldEmail()).append(newLine)
                .append("Old Roles : ").append(oldCommaSeparatedRoles).append(newLine)
                .append("Old Active : ").append(this.memorizeValuesBackingBean.isOldIsActive());

        return stringBuilder.toString();

    }

    public void handleChange(AjaxBehaviorEvent event) {

        this.usernameToUpdate = (String) ((UIOutput) event.getSource()).getValue();

        if (isUsernameSelected()) {
            this.setValues(this.userEJB.findUserByUsername(this.usernameToUpdate));

            GrowlMessage.sendMessage("Info !", "User selected : " + this.usernameToUpdate);
        } else
            GrowlMessage.sendMessage("Error !", "You must select a user");

    }

    public void handleMobileNumberChange(AjaxBehaviorEvent event) {

        this.userMobileNumber = (String) ((UIOutput) event.getSource()).getValue();

    }

}
