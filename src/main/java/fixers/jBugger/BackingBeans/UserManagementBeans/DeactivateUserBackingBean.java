package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.DatabaseEnums.BugStatusEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.Bug;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.DatabaseEntitites.User;
import lombok.Data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Named
@RequestScoped
public class DeactivateUserBackingBean implements Serializable {

    @Inject
    private UserEJB userEJB;

    @Inject
    private BugEJB bugEJB;

    @Inject
    private NotificationEJB notificationEJB;

    private String usernameToUpdate;

    private List<String> users = new ArrayList<>();

    @PostConstruct
    public void init() {

        this.userEJB.getActiveUsers().forEach(user -> this.users.add(user.getUsername()));

    }

    public void saveData() {

        if (isUsernameSelected()) {
            if (!haveUserUnclosedBugs(usernameToUpdate)) {
                LocalDate now = LocalDate.now();

                String message = this.generateNotificationMessage();

                this.userEJB.deactivateUser(this.usernameToUpdate);
                this.notificationEJB.sendNotificationToUserManagers(now, message, NotificationTypeEnum.USER_DELETED);

                GrowlMessage.sendMessage("Info !", "User deactivated successfully");
            } else
                GrowlMessage.sendMessage("Error !", "User have unfinished bugs");
        } else
            GrowlMessage.sendMessage("Error !", "You must select a user");

    }

    private String generateNotificationMessage() {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        User user = this.userEJB.findUserByUsername(this.usernameToUpdate);

        String nCommaSeparatedRoles = user.getRole().stream()
                .map(role -> role.getType().toString())
                .collect(Collectors.joining(","));

        stringBuilder.append("User Deleted : ").append(this.usernameToUpdate).append(newLine)
                .append("First Name : ").append(user.getFirstName()).append(newLine)
                .append("Last Name : ").append(user.getLastName()).append(newLine)
                .append("Email : ").append(user.getEmail()).append(newLine)
                .append("Mobile Number : ").append(user.getMobileNumber()).append(newLine)
                .append("Roles : ").append(nCommaSeparatedRoles).append(newLine)
                .append("Active : ").append(user.isActive());

        return stringBuilder.toString();

    }

    private boolean isUsernameSelected() {
        return usernameToUpdate != null && !usernameToUpdate.equals("");
    }

    private boolean haveUserUnclosedBugs(String username) {

        List<Bug> bugs = this.bugEJB.findBugsAssignedTo(username);

        if (bugs != null)
            for (Bug bug : bugs)
                if (!bug.getStatus().equals(BugStatusEnum.CLOSED))
                    return true;

        return false;
    }

    public void handleChange(AjaxBehaviorEvent event) {

        this.usernameToUpdate = (String) ((UIOutput) event.getSource()).getValue();

        if (isUsernameSelected()) {
            GrowlMessage.sendMessage("Info !", "User selected : " + this.usernameToUpdate);
        } else
            GrowlMessage.sendMessage("Error !", "You must select a user");

    }

}