package fixers.jBugger.BackingBeans.BugManagementBeans;

import fixers.jBugger.BackingBeans.BugExportPdfBeans.PdfCreationBackingBean;
import fixers.jBugger.BusinessLogic.BugEJB;
import fixers.jBugger.DatabaseEntitites.Bug;
import lombok.Data;
import lombok.ToString;
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
@ToString(exclude = "pdfCreationBackingBean")
public class ViewBugsBackingBean extends LazyDataModel<Bug> {
    private Bug selectedBug = null;
    private String selectedBugId = "none";
    private List<Bug> bugsList = new ArrayList<>();
    private String outputMessage;

    @Inject
    private BugEJB bugEJB;

    @Inject
    private PdfCreationBackingBean pdfCreationBackingBean;

    public ViewBugsBackingBean() {
    }

    @PostConstruct
    public void init() {
        bugsList = bugEJB.findAllBugs();
    }

    @Override
    public Bug getRowData(String rowKey) {
        Integer id = Integer.parseInt(rowKey);
        List<Bug> bugs = bugsList.stream().filter(a -> a.getId() == id).collect(Collectors.toList());
        return (bugs.isEmpty()) ? null : bugs.get(0);
    }

    @Override
    public Object getRowKey(Bug object) {
        return object.getId();
    }

    @Override
    public List<Bug> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<Bug> filteredList = new ArrayList<>();
        bugsList.forEach(bug -> {
            boolean match = true;
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext(); ) {
                    try {
                        String filterProperty = it.next();
                        Object filterValue = filters.get(filterProperty);

                        Field privateStringField = bug.getClass().getDeclaredField(filterProperty);
                        privateStringField.setAccessible(true);
                        String fieldValue = privateStringField.get(bug).toString();

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
                filteredList.add(bug);
            }
        });

        int dataSize = filteredList.size();
        if (sortField != null) {
            Collections.sort(filteredList, new ViewBugsBackingBean.BugSorter(sortField, sortOrder));
        }
        this.setRowCount(dataSize);

        //paginate
        if (dataSize > pageSize) {
            try {
                return filteredList.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return filteredList.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return filteredList;
        }
    }

    public static class BugSorter implements Comparator<Bug> {
        private String sortField;
        private SortOrder sortOrder;

        public BugSorter(String sortField, SortOrder sortOrder) {
            this.sortField = sortField;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Bug bug1, Bug bug2) {
            Field bugField = null;
            Field[] bugFields = Bug.class.getDeclaredFields();
            try {
                for (Field field : bugFields) {
                    if (field.getName().equals(sortField)) {
                        bugField = field;
                        bugField.setAccessible(true);
                    }
                }

                if (bugField != null) {
                    Object val1 = Bug.class.getField(this.sortField).get(bug1);
                    Object val2 = Bug.class.getField(this.sortField).get(bug2);

                    int comparationResult = ((Comparable) val1).compareTo(val2);

                    return SortOrder.ASCENDING.equals(sortOrder) ? comparationResult : (-1) * comparationResult;
                }
                else
                    return 1;
            } catch (Exception e) {
                return 1;
            }
        }
    }

    public void rowSelected(SelectEvent event) {
        outputMessage = selectedBug.getTitle();
        selectedBugId = String.valueOf(selectedBug.getId());
        pdfCreationBackingBean.setHasSelectedBug(true);
    }

}
