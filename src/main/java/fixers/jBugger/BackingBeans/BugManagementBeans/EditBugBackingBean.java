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

    @Inject
    private BugEJB bugEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    private NotificationEJB notificationEJB;

    private String outputMessage = "";

    @PostConstruct
    public void init() {
        bugSeverities.add(BugSeverityEnum.LOW);
        bugSeverities.add(BugSeverityEnum.CRITICAL);
        bugSeverities.add(BugSeverityEnum.HIGH);
        bugSeverities.add(BugSeverityEnum.MEDIUM);
    }

    public void searchUsername() {
        List<Bug> bugs = bugEJB.findBugsAssignedTo(usernameAssignedTo);
        if (bugs != null) {
            bugsAssignedToUser = bugs;
            outputMessage = usernameAssignedTo + " list of bugs: ";
        } else {
            outputMessage = "Searched user doesn't have bugs assigned!";
        }

    }


    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            LocalDate now = LocalDate.now();

            String columnHeader = event.getColumn().getHeaderText();

            Bug editedBug = (Bug) ((DataTable) event.getComponent()).getRowData();

            String message;
            NotificationTypeEnum notificationTypeEnum;
            String bugCreatedByUsername = editedBug.getCreatedBy().getUsername();
            String bugAssignedToUsername = editedBug.getAssignedTo().getUsername();

            bugEJB.updateBug(editedBug);

            if (columnHeader.equals("STATUS")) {
                message = this.generateNotificationMessage(editedBug, true, oldValue.toString());
                notificationTypeEnum = NotificationTypeEnum.BUG_STATUS_UPDATED;
            } else {
                message = this.generateNotificationMessage(editedBug, false, "");
                notificationTypeEnum = NotificationTypeEnum.BUG_UPDATED;
            }

            if (bugAssignedToUsername.equals(bugCreatedByUsername))
                this.notificationEJB.sendNotificationToOneUser(bugCreatedByUsername, now, message, notificationTypeEnum);
            else
                this.notificationEJB.sendNotificationToTwoUsers(bugAssignedToUsername, bugCreatedByUsername, now, message, notificationTypeEnum);


            GrowlMessage.sendMessage("Info !", "Bug updated successfully !");
        }
    }

    private String generateNotificationMessage(Bug editedBug, boolean isStatusUpdated, String oldStatus) {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        if (!isStatusUpdated)
            stringBuilder.append("Bug Updated !").append(newLine);

        stringBuilder.append("Title : ").append(editedBug.getTitle()).append(newLine)
                .append("Description : ").append(editedBug.getDescription()).append(newLine)
                .append("Target date : ").append(editedBug.getTargetDate()).append(newLine)
                .append("Severity : ").append(editedBug.getSeverity().toString()).append(newLine);

        if (!isStatusUpdated)
            stringBuilder.append("Status : ").append(editedBug.getStatus().toString()).append(newLine);
        else
            stringBuilder.append("New Status : ").append(editedBug.getStatus().toString()).append(newLine)
                    .append("Old Status : ").append(oldStatus).append(newLine);

        stringBuilder.append("Assigned to : ").append(editedBug.getAssignedTo().getUsername()).append(newLine)
                .append("Created by : ").append(editedBug.getCreatedBy().getUsername());

        return stringBuilder.toString();

    }
}
