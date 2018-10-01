package fixers.jBugger.BackingBeans.BugManagementBeans;

import fixers.jBugger.DatabaseEnums.BugSeverityEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.Bug;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import lombok.Data;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import javax.annotation.PostConstruct;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Named
@ViewScoped
public class EditBugBackingBean implements Serializable {
    private String usernameAssignedTo;
    private List<Bug> bugsAssignedToUser = new ArrayList<>();
    private List<BugSeverityEnum> bugSeverities = new ArrayList<>();
    private List<String> users = new ArrayList<>();

    @Inject
    private BugEJB bugEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    private NotificationEJB notificationEJB;

    @PostConstruct
    public void init() {

        bugSeverities.add(BugSeverityEnum.LOW);
        bugSeverities.add(BugSeverityEnum.CRITICAL);
        bugSeverities.add(BugSeverityEnum.HIGH);
        bugSeverities.add(BugSeverityEnum.MEDIUM);
        this.users = this.userEJB.getUsernames();

    }

    private void searchUsername() {
        List<Bug> bugs = bugEJB.findBugsAssignedTo(usernameAssignedTo);
        if (bugs != null) {
            bugsAssignedToUser = bugs;
        } else {
            this.setBugsAssignedToNull();
            GrowlMessage.sendMessage("Info !", this.usernameAssignedTo + " doesn't have bugs assigned");
        }

    }

    public void handleChange(AjaxBehaviorEvent event) {

        this.usernameAssignedTo = (String) ((UIOutput) event.getSource()).getValue();

        if (isUsernameSelected()) {
            this.searchUsername();
            GrowlMessage.sendMessage("Info !", "User selected : " + this.usernameAssignedTo);
        } else {
            this.setBugsAssignedToNull();
            GrowlMessage.sendMessage("Error !", "You must select a user");
        }

    }

    private void setBugsAssignedToNull() {
        this.bugsAssignedToUser = null;
    }

    private boolean isUsernameSelected() {

        return this.usernameAssignedTo != null && !this.usernameAssignedTo.equals("");

    }

    public void onCellEdit(CellEditEvent event) {

        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            LocalDate now = LocalDate.now();

            Bug editedBug = (Bug) ((DataTable) event.getComponent()).getRowData();

            String bugCreatedByUsername = editedBug.getCreatedBy().getUsername();

            bugEJB.updateBug(editedBug);

            this.bugsAssignedToUser = bugEJB.findBugsAssignedTo(usernameAssignedTo);


            String message = this.generateNotificationMessage(editedBug);
            NotificationTypeEnum notificationTypeEnum = NotificationTypeEnum.BUG_UPDATED;

            if (this.usernameAssignedTo.equals(bugCreatedByUsername))
                this.notificationEJB.sendNotificationToOneUser(bugCreatedByUsername, now, message, notificationTypeEnum);
            else
                this.notificationEJB.sendNotificationToTwoUsers(this.usernameAssignedTo, bugCreatedByUsername, now, message, notificationTypeEnum);

            GrowlMessage.sendMessage("Info !", "Bug updated successfully !");

        }

    }

    private String generateNotificationMessage(Bug editedBug) {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        stringBuilder.append("Bug Updated !").append(newLine)
                .append("Title : ").append(editedBug.getTitle()).append(newLine)
                .append("Description : ").append(editedBug.getDescription()).append(newLine)
                .append("Target date : ").append(editedBug.getTargetDate()).append(newLine)
                .append("Severity : ").append(editedBug.getSeverity().toString()).append(newLine)
                .append("Status : ").append(editedBug.getStatus().toString()).append(newLine)
                .append("Assigned to : ").append(editedBug.getAssignedTo().getUsername()).append(newLine)
                .append("Created by : ").append(editedBug.getCreatedBy().getUsername());

        return stringBuilder.toString();

    }

}