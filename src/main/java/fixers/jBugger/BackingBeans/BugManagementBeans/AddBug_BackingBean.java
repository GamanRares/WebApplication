package fixers.jBugger.BackingBeans.BugManagementBeans;

import fixers.jBugger.BackingBeans.MainPagesBeans.Login_BackingBean;
import fixers.jBugger.DatabaseEnums.BugSeverityEnum;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.Notification;
import fixers.jBugger.DatabaseEntitites.User;
import lombok.Data;
import org.primefaces.PrimeFaces;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;


@Data
@Named
@SessionScoped
public class AddBug_BackingBean implements Serializable {
    private String title;
    private String description;
    private Date targetDate;
    private String selectOneSeverity;
    private String assignedToUsername;
    private User createdByUser;
    private UploadedFile uploadedFile;
    private String attachmentName = "";

    private BugSeverityEnum severity;
    private User assignedToUser;
    private byte[] attachment;

    @Inject
    private Login_BackingBean loginBackingBean;


    @Inject
    private UserEJB userEJB;

    @Inject
    private BugEJB bugEJB;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private ViewBugs_BackingBean viewBugsBackingBean;

    private static final Logger LOGGER = Logger.getLogger(AddBug_BackingBean.class.getName());

    public void clickCalendar() {
        PrimeFaces.current().ajax().update("form:display");
        PrimeFaces.current().executeScript("PF('dlg').show()");
    }

    public void upload() {
        //TODO when adding a bug in database you must also set the type : image/document for now
        //TODO if the same attachment wants to be uploaded we should not create another instance for it if it exists
        //TODO must have a different type for each type of file, if he wants to upload pdf he selects pdf

        if (uploadedFile != null) {

            String[] extension = uploadedFile.getFileName().split("\\.");
            if (extension.length == 2 && extension[1].matches("^(pdf|doc|docx|odf|xlsx|xls|jpg|jpeg|png|gif|bmp)$")) {
                LOGGER.info("Uploaded " + uploadedFile.getFileName());
                attachment = uploadedFile.getContents();
                attachmentName = uploadedFile.getFileName();
                FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else
                GrowlMessage.sendMessage("Error", "File not supported!");

        }
    }

    private void convertSeverity() {
        switch (selectOneSeverity) {
            case "low":
                severity = BugSeverityEnum.LOW;
                break;
            case "medium":
                severity = BugSeverityEnum.MEDIUM;
                break;
            case "high":
                severity = BugSeverityEnum.HIGH;
                break;
            case "critical":
                severity = BugSeverityEnum.CRITICAL;
                break;

        }
    }

    private void convertCreatedBy() {
        String createdByUsername = loginBackingBean.getCurrentlyLoggedInUsername();
        createdByUser = userEJB.findUserByUsername(createdByUsername);
    }

    private void convertAssignedTo() {
        assignedToUser = userEJB.findUserByUsername(assignedToUsername);
    }

    private void convertBugFields() {
        convertSeverity();
        convertCreatedBy();
        convertAssignedTo();
    }

    public void saveBug() {
        convertBugFields();
        if (this.assignedToUser != null) {

            LocalDate now = LocalDate.now();

            String message = this.generateNotificationMessage();

            try {
                //TODO check if the input is correct, else output a specific growl

                if (this.assignedToUsername.equals(this.loginBackingBean.getUsername()))
                    this.notificationEJB.sendNotificationToOneUser(this.assignedToUsername, now, message, NotificationTypeEnum.BUG_UPDATED);
                else
                    this.notificationEJB.sendNotificationToTwoUsers(this.assignedToUsername, this.loginBackingBean.getUsername(), now, message, NotificationTypeEnum.BUG_UPDATED);

                Notification notification = this.notificationEJB.getMaxNotification();

                bugEJB.addBug(title, description, targetDate, severity, assignedToUser, createdByUser, attachment, attachmentName, notification);

                GrowlMessage.sendMessage("Info !", "Bug successfully added");

            } catch (EJBException e) {
                ConstraintViolationException constraintViolation = (ConstraintViolationException) e.getCausedByException();
                constraintViolation.getConstraintViolations().forEach(err -> GrowlMessage.sendMessage("Error !", err.getMessage()));
            }
        } else
            GrowlMessage.sendMessage("Error !", "User assigned to doesn't exist");
    }

    private String generateNotificationMessage() {

        StringBuilder stringBuilder = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        stringBuilder.append("New Bug !").append(newLine)
                .append("Title : ").append(this.title).append(newLine)
                .append("Description : ").append(this.description).append(newLine)
                .append("Target date : ").append(this.targetDate).append(newLine)
                .append("Severity : ").append(this.severity.toString()).append(newLine)
                .append("Status : NEW").append(newLine)
                .append("Assigned to : ").append(this.assignedToUsername).append(newLine)
                .append("Created by : ").append(this.createdByUser.getUsername());

        return stringBuilder.toString();

    }
}