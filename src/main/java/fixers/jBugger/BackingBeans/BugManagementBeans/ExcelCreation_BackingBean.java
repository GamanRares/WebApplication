package fixers.jBugger.BackingBeans.BugManagementBeans;

import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugExcelEJB;
import lombok.Data;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

@Data
@Named
@SessionScoped
public class ExcelCreation_BackingBean implements Serializable {

    @Inject
    private BugExcelEJB bugExcelEJB;

    private StreamedContent file;

    public DefaultStreamedContent getFile() {
        //TODO FIX filtration, pagination, sorting for the PDF/VIEW of BUG TABLES!!!!

        try {
            byte[] excel = bugExcelEJB.createBugExcel();
            return new DefaultStreamedContent(new ByteArrayInputStream(excel), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "DownloadedBugs" + ".xlsx");
        } catch (Exception e) {
            GrowlMessage.sendMessage("Error", "You must select a bug!");
            e.printStackTrace();
        }

        return null;
    }

}
