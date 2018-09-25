package fixers.jBugger.BackingBeans.NotificationsBeans;

import fixers.jBugger.BackingBeans.MainPagesBeans.Login_BackingBean;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.DatabaseEntitites.Notification;
import lombok.Data;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Named
@RequestScoped
public class ViewNotifications_BackingBean extends LazyDataModel<Notification> {

    private Notification selectedNotification;
    private String outputMessage;

    private List<Notification> notificationList;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private Login_BackingBean loginBackingBean;

    @PostConstruct
    public void init() {

        String username = this.loginBackingBean.getUsername();

        this.notificationList = this.notificationEJB.getNotificationsAssignedTo(username);

        this.notificationList.forEach(notification -> notification.setNotificationMessage(notification.getNotificationMessage().replaceAll("\n", "<br />")));

    }

    public void rowSelected(SelectEvent event) {
        outputMessage = selectedNotification.getNotificationMessage();
    }

    @Override
    public Notification getRowData(String rowKey) {
        return this.notificationList.stream()
                .filter(notification -> String.valueOf(notification.getNotification_id()).equals(rowKey))
                .collect(Collectors.toList())
                .get(0);
    }

    @Override
    public Object getRowKey(Notification notification) {
        return notification.getNotification_id();
    }

    @Override
    public List<Notification> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<Notification> filteredList = new ArrayList<>();
        this.notificationList.forEach(notification -> {
            boolean match = true;
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                    try {
                        String filterProperty = it.next();
                        Object filterValue = filters.get(filterProperty);

                        Field privateStringField = notification.getClass().getDeclaredField(filterProperty);
                        privateStringField.setAccessible(true);
                        String fieldValue = privateStringField.get(notification).toString();

                        if (filterValue == null || fieldValue.contains(filterValue.toString())) {
                            match = true;
                        } else {
                            match = false;
                            break;
                        }
                    } catch (Exception e) {
                        match = false;
                    }
                }
            }
            if (match) {
                filteredList.add(notification);
            }
        });

        int dataSize = filteredList.size();
        if (sortField != null) {
            Collections.sort(filteredList, new ViewNotifications_BackingBean.NotificationSorter(sortField, sortOrder));
        }
        this.setRowCount(dataSize);

        //paginate
        if (dataSize <= pageSize) {
            return filteredList;
        } else {
            try {
                return filteredList.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return filteredList.subList(first, first + (dataSize % pageSize));
            }
        }
    }

    public static class NotificationSorter implements Comparator<Notification> {
        private String sortField;
        private SortOrder sortOrder;

        public NotificationSorter(String sortField, SortOrder sortOrder) {
            this.sortField = sortField;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Notification movie1, Notification movie2) {
            try {
                Object val1 = Notification.class.getField(this.sortField).get(movie1);
                Object val2 = Notification.class.getField(this.sortField).get(movie2);

                int comparationResult = ((Comparable) val1).compareTo(val2);

                return SortOrder.ASCENDING.equals(sortOrder) ? comparationResult : (-1) * comparationResult;
            } catch (Exception e) {
                return 1;
            }
        }
    }

}
