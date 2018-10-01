package fixers.jBugger.BackingBeans.UserManagementBeans;

import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.User;
import lombok.Data;
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
public class ViewUsersBackingBean extends LazyDataModel<User> {

    private List<User> userList = new ArrayList<>();

    @Inject
    private UserEJB userEJB;

    @PostConstruct
    public void init() {

        this.userList = this.userEJB.getUsers();

    }

    @Override
    public User getRowData(String rowKey) {
        return this.userList.stream()
                .filter(a -> a.getUsername().equals(rowKey))
                .collect(Collectors.toList())
                .get(0);
    }

    @Override
    public Object getRowKey(User user) {
        return user.getUsername();
    }

    @Override
    public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<User> filteredList = new ArrayList<>();
        this.userList.forEach(user -> {
            boolean match = true;
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                    try {
                        String filterProperty = it.next();
                        Object filterValue = filters.get(filterProperty);

                        Field privateStringField = user.getClass().getDeclaredField(filterProperty);
                        privateStringField.setAccessible(true);
                        String fieldValue = privateStringField.get(user).toString();

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
                filteredList.add(user);
            }
        });

        int dataSize = filteredList.size();
        if (sortField != null) {
            Collections.sort(filteredList, new ViewUsersBackingBean.UserSorter(sortField, sortOrder));
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

    public static class UserSorter implements Comparator<User> {
        private String sortField;
        private SortOrder sortOrder;

        public UserSorter(String sortField, SortOrder sortOrder) {
            this.sortField = sortField;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(User user1, User user2) {
            try {
                Object val1 = User.class.getField(this.sortField).get(user1);
                Object val2 = User.class.getField(this.sortField).get(user2);

                int comparationResult = ((Comparable) val1).compareTo(val2);

                return SortOrder.ASCENDING.equals(sortOrder) ? comparationResult : (-1) * comparationResult;
            } catch (Exception e) {
                return 1;
            }
        }
    }

}