package fixers.jBugger.BackingBeans.BugManagementBeans;

import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.Bug;
import fixers.jBugger.DatabaseEnums.BugStatusEnum;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import lombok.Data;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

import javax.annotation.PostConstruct;
import javax.faces.component.UIOutput;
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
public class UpdateBugStatusBackingBean implements Serializable {

    private String usernameAssignedToCloseBug;
    private String usernameAssignedToChangeStatus;
    private List<Bug> bugsForChange = new ArrayList<>();
    private List<Bug> bugsForClose = new ArrayList<>();

    private String myStatus;

    private Bug editedBug;

    private Bug selectedBug = null;

    private List<String> users = new ArrayList<>();

    @Inject
    private BugEJB bugEJB;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private UserEJB userEJB;

    @PostConstruct
    public void init() {

        this.users = this.userEJB.getUsernames();

    }

    public void handleCloseChange(AjaxBehaviorEvent event) {

        this.usernameAssignedToCloseBug = (String) ((UIOutput) event.getSource()).getValue();

        if (isUsernameSelected("Close")) {
            this.searchUsername("Close");
            GrowlMessage.sendMessage("Info !", "User selected  : " + this.usernameAssignedToCloseBug);
        } else {
            this.setBugsAssignedToNull("Close");
            GrowlMessage.sendMessage("Error !", "You must select a user");
        }

    }

    public void handleUpdateChange(AjaxBehaviorEvent event) {

        this.usernameAssignedToChangeStatus = (String) ((UIOutput) event.getSource()).getValue();

        if (isUsernameSelected("")) {
            this.searchUsername("");
            GrowlMessage.sendMessage("Info !", "User selected  : " + this.usernameAssignedToChangeStatus);
        } else {
            this.setBugsAssignedToNull("");
            GrowlMessage.sendMessage("Error !", "You must select a user");
        }

    }

    private void setBugsAssignedToNull(String forWhat) {

        if (forWhat.equals("Close"))
            this.bugsForClose = null;
        else
            this.bugsForChange = null;

    }

    private boolean isUsernameSelected(String forWhat) {

        if (forWhat.equals("Close"))
            return this.usernameAssignedToCloseBug != null && !this.usernameAssignedToCloseBug.equals("");
        else
            return this.usernameAssignedToChangeStatus != null && !this.usernameAssignedToChangeStatus.equals("");

    }

    private void searchUsername(String forWhat) {

        if (forWhat.equals("Close")) {
            List<Bug> bugs = bugEJB.findClosableBugsAssignedTo(usernameAssignedToCloseBug);
            if (bugs != null) {
                this.bugsForClose = bugs;
            } else {
                this.setBugsAssignedToNull("Close");
                GrowlMessage.sendMessage("Info !", this.usernameAssignedToCloseBug + " doesn't have bugs to be closed");
            }
        } else {
            List<Bug> bugs = this.bugEJB.findUnClosableBugsAssignedTo(this.usernameAssignedToChangeStatus);
            if (bugs != null) {
                this.bugsForChange = bugs;
            } else {
                this.setBugsAssignedToNull("");
                GrowlMessage.sendMessage("Info !", this.usernameAssignedToChangeStatus + " doesn't have bugs to be changed");
            }
        }

    }

    public boolean acceptsStatusNew(BugStatusEnum status) {
        return status.equals(BugStatusEnum.INFO_NEEDED) || status.equals(BugStatusEnum.IN_PROGRESS) || status.equals(BugStatusEnum.REJECTED) || status.equals(BugStatusEnum.CLOSED);
    }

    public boolean acceptsStatusInProgress(BugStatusEnum status) {
        return status.equals(BugStatusEnum.FIXED) || status.equals(BugStatusEnum.REJECTED) || status.equals(BugStatusEnum.CLOSED);
    }

    public boolean acceptsStatusInfoNeeded(BugStatusEnum status) {
        return status.equals(BugStatusEnum.FIXED) || status.equals(BugStatusEnum.REJECTED) || status.equals(BugStatusEnum.CLOSED) || status.equals(BugStatusEnum.NEW);
    }

    public boolean acceptsStatusFixed(BugStatusEnum status) {
        return status.equals(BugStatusEnum.INFO_NEEDED) || status.equals(BugStatusEnum.REJECTED) || status.equals(BugStatusEnum.CLOSED) || status.equals(BugStatusEnum.NEW);
    }

    public boolean acceptsStatusRejected(BugStatusEnum status) {
        return status.equals(BugStatusEnum.FIXED) || status.equals(BugStatusEnum.INFO_NEEDED) || status.equals(BugStatusEnum.CLOSED);
    }


    public void onCellEdit(CellEditEvent event) {

        String newValue = (String) event.getNewValue();

        editedBug = (Bug) ((DataTable) event.getComponent()).getRowData();
        String oldValue = editedBug.getStatus().toString();


        if (newValue != null && !newValue.equals(oldValue)) {
            LocalDate now = LocalDate.now();

            BugStatusEnum newValueEnum = convertStatus(newValue);

            String bugCreatedByUsername = editedBug.getCreatedBy().getUsername();
            String bugAssignedToUsername = editedBug.getAssignedTo().getUsername();

            editedBug.setStatus(newValueEnum);

            String message = this.generateNotificationMessage(editedBug, oldValue);


            if (bugAssignedToUsername.equals(bugCreatedByUsername))
                this.notificationEJB.sendNotificationToOneUser(bugCreatedByUsername, now, message, NotificationTypeEnum.BUG_STATUS_UPDATED);
            else
                this.notificationEJB.sendNotificationToTwoUsers(bugAssignedToUsername, bugCreatedByUsername, now, message, NotificationTypeEnum.BUG_STATUS_UPDATED);

            bugEJB.updateBug(editedBug);

            this.bugsForChange = this.bugEJB.findUnClosableBugsAssignedTo(this.usernameAssignedToChangeStatus);

            GrowlMessage.sendMessage("Info !", "Bug updated successfully !");
        }

    }

    public void closeBugStatus() {

        if (selectedBug == null) {
            GrowlMessage.sendMessage("Invalid", "You have to select a bug!");
        } else {
            LocalDate now = LocalDate.now();

            selectedBug.setStatus(BugStatusEnum.CLOSED);
            selectedBug.setFixingVersion(selectedBug.getVersion() + 1);
            bugEJB.updateBug(selectedBug);

            String bugCreatedByUsername = selectedBug.getCreatedBy().getUsername();

            String message = this.generateNotificationMessage(selectedBug, "");
            NotificationTypeEnum notificationTypeEnum = NotificationTypeEnum.BUG_CLOSED;

            this.bugsForClose = this.bugEJB.findClosableBugsAssignedTo(this.usernameAssignedToCloseBug);

            if (this.usernameAssignedToCloseBug.equals(bugCreatedByUsername))
                this.notificationEJB.sendNotificationToOneUser(bugCreatedByUsername, now, message, notificationTypeEnum);
            else
                this.notificationEJB.sendNotificationToTwoUsers(this.usernameAssignedToCloseBug, bugCreatedByUsername, now, message, notificationTypeEnum);

            GrowlMessage.sendMessage("Success", "Bug updated !");
        }

    }

    private String generateNotificationMessage(Bug editedBug, String oldStatus) {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        if (oldStatus.equals(""))
            stringBuilder.append("Bug Closed !").append(newLine);

        stringBuilder.append("Title : ").append(editedBug.getTitle()).append(newLine)
                .append("Description : ").append(editedBug.getDescription()).append(newLine)
                .append("Target date : ").append(editedBug.getTargetDate()).append(newLine);

        if (oldStatus.equals(""))
            stringBuilder.append("Fixing version : ").append(editedBug.getFixingVersion()).append(newLine);

        stringBuilder.append("Severity : ").append(editedBug.getSeverity().toString()).append(newLine)
                .append("New Status : ").append(editedBug.getStatus().toString()).append(newLine);

        if (!oldStatus.equals(""))
            stringBuilder.append("Old Status : ").append(oldStatus).append(newLine);

        stringBuilder.append("Assigned to : ").append(editedBug.getAssignedTo().getUsername()).append(newLine)
                .append("Created by : ").append(editedBug.getCreatedBy().getUsername());

        return stringBuilder.toString();

    }

    private BugStatusEnum convertStatus(String value) {

        switch (value) {
            case "new":
                return BugStatusEnum.NEW;
            case "fixed":
                return BugStatusEnum.FIXED;
            case "info_needed":
                return BugStatusEnum.INFO_NEEDED;
            case "in_progress":
                return BugStatusEnum.IN_PROGRESS;
            case "rejected":
                return BugStatusEnum.REJECTED;
        }
        return null;
    }

}