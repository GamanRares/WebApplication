package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEnums.BugSeverityEnum;
import fixers.jBugger.DatabaseEnums.BugStatusEnum;
import fixers.jBugger.DatabaseEntitites.*;
import lombok.Data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Stateless
public class BugEJB implements Serializable {
    @Inject
    private BusinessLogic businessLogic;

    public void addBug(String title, String description, Date targetDate, BugSeverityEnum bugSeverityEnum, User assignedTo, User createdBy, byte[] attachment, String attachmentName, Notification notification) {

        Bug bug = new Bug();
        bug.setTitle(title);
        bug.setDescription(description);
        bug.setTargetDate(targetDate);
        bug.setSeverity(bugSeverityEnum);
        bug.setAssignedTo(assignedTo);
        bug.setStatus(BugStatusEnum.NEW);
        bug.setCreatedBy(createdBy);
        bug.setNotification(notification);

        if (attachment != null) {
            Attachment bugAttachment = new Attachment();
            bugAttachment.setAttachedFile(attachment);
            bugAttachment.setAttachmentName(attachmentName);
            businessLogic.getEm().persist(bugAttachment);
            bug.setAttachment(bugAttachment);
        }

        businessLogic.getEm().persist(bug);
    }

    public void updateBug(Bug updated) {
        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Bug> update = builder.createQuery(Bug.class);
        Root<Bug> e = update.from(Bug.class);

        update.where(builder.equal(e.get(Bug_.id), updated.getId()));

        Bug bug = businessLogic.getEm().createQuery(update).getSingleResult();

        bug.setTitle(updated.getTitle());
        bug.setDescription(updated.getDescription());
        bug.setVersion(updated.getVersion());
        bug.setAssignedTo(updated.getAssignedTo());
        bug.setSeverity(updated.getSeverity());
        bug.setNotification(updated.getNotification());
        bug.setStatus(updated.getStatus());

    }

    public List<Bug> findAllBugs() {
        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();
        CriteriaQuery<Bug> allBugsQuery = builder.createQuery(Bug.class);
        Root<Bug> fromBug = allBugsQuery.from(Bug.class);
        allBugsQuery.select(fromBug);
        return businessLogic.getEm().createQuery(allBugsQuery).getResultList();
    }

    public List<Bug> findBugsAssignedTo(String username) {
        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();
        CriteriaQuery<Bug> allUserBugsQuery = builder.createQuery(Bug.class);
        Root<User> fromUser = allUserBugsQuery.from(User.class);
        Join<User, Bug> userBugJoin = fromUser.join(User_.bugsAssigned);
        allUserBugsQuery.select(userBugJoin);
        allUserBugsQuery.where(builder.equal(fromUser.get(User_.username), username));
        List<Bug> bugs = businessLogic.getEm().createQuery(allUserBugsQuery).getResultList();
        return (bugs.isEmpty()) ? null : bugs;
    }

    public List<Bug> findClosableBugsAssignedTo(String username) {

        BugStatusEnum rejectedBug = BugStatusEnum.REJECTED;
        BugStatusEnum fixedBug = BugStatusEnum.FIXED;

        List<Bug> bugs = this.findBugsAssignedTo(username).stream()
                .filter(bug -> (bug.getStatus().equals(rejectedBug) || bug.getStatus().equals(fixedBug)))
                .collect(Collectors.toList());
        return (bugs.isEmpty()) ? null : bugs;
    }

    public List<Bug> findUnClosableBugsAssignedTo(String username) {

        BugStatusEnum newBug = BugStatusEnum.NEW;
        BugStatusEnum infoBug = BugStatusEnum.INFO_NEEDED;
        BugStatusEnum progressBug = BugStatusEnum.IN_PROGRESS;

        List<Bug> bugs = this.findBugsAssignedTo(username).stream()
                .filter(bug -> bug.getStatus().equals(newBug) || bug.getStatus().equals(infoBug) || bug.getStatus().equals(progressBug))
                .collect(Collectors.toList());
        return (bugs.isEmpty()) ? null : bugs;
    }

    public Bug findBugWithID(int id) {
        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();
        CriteriaQuery<Bug> allBugsQuery = builder.createQuery(Bug.class);
        Root<Bug> fromBug = allBugsQuery.from(Bug.class);
        allBugsQuery.select(fromBug);
        allBugsQuery.where(builder.equal(fromBug.get(Bug_.id), id));
        List<Bug> bugs = businessLogic.getEm().createQuery(allBugsQuery).getResultList();
        if (bugs.size() == 1) return bugs.get(0);
        return null;
    }

}
