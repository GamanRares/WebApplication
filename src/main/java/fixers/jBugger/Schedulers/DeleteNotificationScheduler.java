package fixers.jBugger.Schedulers;

import fixers.jBugger.BusinessLogic.NotificationEJB;

import javax.ejb.*;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;

@Startup
@Stateless
public class DeleteNotificationScheduler {

    @Inject
    private NotificationEJB notificationEJB;

    @Schedule
    public void deleteNotifications() {
        this.notificationEJB.getNotifications().forEach(notification -> {
            if (isOldEnough(notification.getAddedDate()))
                this.notificationEJB.deleteNotification(notification.getNotification_id());
        });

    }

    private boolean isOldEnough(LocalDate date) {
        LocalDate now = LocalDate.now();

        Duration difference = Duration.between(date.atStartOfDay(), now.atStartOfDay());

        return difference.toDays() > 30;
    }

}
