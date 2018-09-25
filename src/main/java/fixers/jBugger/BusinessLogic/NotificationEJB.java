package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.DatabaseEntitites.*;
import lombok.Data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
@Stateless
public class NotificationEJB implements Serializable {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    private UserEJB userEJB;

    @Inject
    private RoleEJB roleEJB;

    @Inject
    private Logger logger;

    public void sendNotificationToOneUser(String username, LocalDate date, String message, NotificationTypeEnum notificationTypeEnum) {

        List<User> users = new ArrayList<>();
        User user = this.userEJB.findUserByUsername(username);

        if (user != null) {
            users.add(user);

            Notification notification = createNotification(date, message, notificationTypeEnum, users);

            this.businessLogic.getEm().persist(notification);
        }

    }

    public void sendNotificationToTwoUsers(String username1, String username2, LocalDate date, String message, NotificationTypeEnum notificationTypeEnum) {

        List<User> users = new ArrayList<>();
        User user1 = this.userEJB.findUserByUsername(username1);
        User user2 = this.userEJB.findUserByUsername(username2);

        if (user1 != null) {
            users.add(user1);
            users.add(user2);

            Notification notification = createNotification(date, message, notificationTypeEnum, users);

            this.businessLogic.getEm().persist(notification);
        }

    }

    public void sendNotificationToAdministrators(List<User> administrators, LocalDate date, String message, NotificationTypeEnum notificationTypeEnum) {

        Notification notification = createNotification(date, message, notificationTypeEnum, administrators);

        this.businessLogic.getEm().persist(notification);
    }

    public void sendNotificationToUserManagers(LocalDate date, String message, NotificationTypeEnum notificationTypeEnum) {

        List<User> managers = new ArrayList<>();

        this.roleEJB.getRoles().stream()
                .filter(Role::isUserManagementChecked)
                .forEach(role -> managers.addAll(role.getUsers()));

        List<User> uniqueManagers = managers.stream()
                .distinct()
                .collect(Collectors.toList());

        Notification notification = createNotification(date, message, notificationTypeEnum, uniqueManagers);

        this.businessLogic.getEm().persist(notification);
    }

    private Notification createNotification(LocalDate date, String message, NotificationTypeEnum notificationTypeEnum, List<User> users) {

        Notification notification = new Notification();

        notification.setUsers(users);
        notification.setAddedDate(date);
        notification.setNotificationMessage(message);
        notification.setNotificationTypeEnum(notificationTypeEnum);

        return notification;
    }

    public List<Notification> getNotifications() {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Notification> notificationQuery = builder.createQuery(Notification.class);
        Root<Notification> fromNotification = notificationQuery.from(Notification.class);


        return businessLogic.getEm().createQuery(notificationQuery).getResultList();

    }

    public Notification getMaxNotification() {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Notification> notificationQuery = builder.createQuery(Notification.class);
        Root<Notification> fromNotification = notificationQuery.from(Notification.class);

        Expression<Integer> number = fromNotification.get(Notification_.notification_id);
        Expression<Integer> max = builder.max(number);

        notificationQuery.orderBy(builder.desc(fromNotification.get(Notification_.notification_id)));

        return businessLogic.getEm().createQuery(notificationQuery).setMaxResults(1).getSingleResult();

    }

    public List<Notification> getNotificationsAssignedTo(String username) {

        User user = this.userEJB.findUserByUsername(username);

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Notification> usernameQuery = builder.createQuery(Notification.class);
        Root<Notification> fromNotification = usernameQuery.from(Notification.class);

        Expression<Collection<User>> users = fromNotification.get(Notification_.users);
        Predicate containsUser = builder.isMember(user, users);

        usernameQuery.where(containsUser);

        return businessLogic.getEm().createQuery(usernameQuery).getResultList();

    }

    public void deleteNotification(Integer notificationId) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaDelete<Notification> delete = builder.createCriteriaDelete(Notification.class);
        Root<Notification> notification = delete.from(Notification.class);

        delete.where(builder.equal(notification.get(Notification_.notification_id), notificationId));

        this.businessLogic.getEm().createQuery(delete).executeUpdate();

    }

}
