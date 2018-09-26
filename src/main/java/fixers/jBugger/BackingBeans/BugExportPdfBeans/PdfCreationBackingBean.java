package fixers.jBugger.BackingBeans.BugExportPdfBeans;

import fixers.jBugger.BackingBeans.BugManagementBeans.ViewBugsBackingBean;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.BugPdfEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import lombok.Data;
import lombok.ToString;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

@SessionScoped
@Named
@Data
@ToString(exclude = "bugTableBackingBean")
public class PdfCreationBackingBean implements Serializable {

    private StreamedContent file;
    private boolean hasSelectedBug = false;

    public PdfCreationBackingBean() {
    }

    @Inject
    private BugPdfEJB bugPdfEJB;

    @Inject
    private UserEJB userEJB;

    @Inject
    ViewBugsBackingBean viewBugsBackingBean;

    public DefaultStreamedContent getFile() {
        //TODO FIX filtration, pagination, sorting for the PDF/VIEW of BUG TABLES!!!!

        try {
            byte[] pdf = bugPdfEJB.createBugPdf();
            return new DefaultStreamedContent(new ByteArrayInputStream(pdf), "application/pdf", "DownloadedBug" + viewBugsBackingBean.getSelectedBug().getId()+".pdf");
        } catch (Exception e) {
            GrowlMessage.sendMessage("Error", "You must select a bug!");
            e.printStackTrace();
        }

        return null;
    }
}
