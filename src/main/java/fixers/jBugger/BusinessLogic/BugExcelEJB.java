package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEntitites.Bug;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BugExcelEJB implements Serializable {

    @Inject
    private BugEJB bugEJB;

    public byte[] createBugExcel() {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Bugs");

        List<List<String>> datatypes = new ArrayList<>();
        List<String> header = new ArrayList<>();
        List<Bug> bugList = this.bugEJB.findAllBugs();

        this.setHeader(header);
        datatypes.add(header);

        if (bugList != null)
            this.addBugsToDataTypes(bugList, datatypes);



        int rowNum = 0;
        System.out.println("Creating excel");

        for (List<String> datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String field : datatype) {
                row.createCell(colNum++).setCellValue(field);
            }
        }

        for(int i = 0; i < header.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    private void addBugsToDataTypes(List<Bug> bugList, List<List<String>> datatypes) {

        bugList.forEach(bug -> {
            datatypes.add(addBugToList(bug));
        });

    }

    private List<String> addBugToList(Bug bug) {

        List<String> bugString = new ArrayList<>();

        bugString.add(String.valueOf(bug.getId()));
        bugString.add(bug.getTitle());
        bugString.add(bug.getDescription());
        bugString.add(String.valueOf(bug.getVersion()));
        bugString.add(String.valueOf(bug.getFixingVersion()));
        bugString.add(bug.getTargetDate().toString());
        bugString.add(bug.getSeverity().toString());
        bugString.add(bug.getStatus().toString());
        bugString.add(bug.getCreatedBy().getUsername());
        bugString.add(bug.getAssignedTo().getUsername());
        bugString.add(bug.getAttachment().getAttachmentName());
        bugString.add(bug.getNotification().getNotificationMessage());

        return bugString;
    }

    private void setHeader(List<String> header) {
        header.add("Bug");
        header.add("Title");
        header.add("Description");
        header.add("Version");
        header.add("Fixing version");
        header.add("Target date");
        header.add("Severity");
        header.add("Status");
        header.add("Created by");
        header.add("Assigned to");
        header.add("Attachment");
        header.add("Notification");
    }

}


