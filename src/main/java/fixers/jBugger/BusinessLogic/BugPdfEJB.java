package fixers.jBugger.BusinessLogic;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import fixers.jBugger.BackingBeans.BugManagementBeans.ViewBugs_BackingBean;
import fixers.jBugger.DatabaseEntitites.Attachment;
import fixers.jBugger.DatabaseEntitites.Bug;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.net.MalformedURLException;

@Stateless
public class BugPdfEJB implements Serializable {

    @Inject
    private UserEJB userEJB;

    @Inject
    private ViewBugs_BackingBean viewBugsBackingBean;

    public byte[] createBugPdf() throws Exception {
        Bug selectedBug = viewBugsBackingBean.getSelectedBug();

        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph content = new Paragraph(this.bugContent(selectedBug), font);

        try {

            if (selectedBug == null)
                throw new Exception();

            if (selectedBug.getAttachment() == null) {
                content.add("\nBug attachment: NONE");

                document.add(content);
                document.close();

                return byteArrayOutputStream.toByteArray();
            }

            String attachmentName = selectedBug.getAttachment().getAttachmentName();
            content.add("\nBug attachment name: " + selectedBug.getAttachment().getAttachmentName());

            if (attachmentName.endsWith(".bmp") || attachmentName.endsWith(".jpg")
                    || attachmentName.endsWith(".jpeg") || attachmentName.endsWith(".png")
                    || attachmentName.endsWith(".gif")) {
                Attachment chosenBugAttachment = selectedBug.getAttachment();
                byte[] chosenBugValue = chosenBugAttachment.getAttachedFile();

                Image image = Image.getInstance(chosenBugValue);

                content.add("\nBug attachment content:\n\n");
                content.add(image);

                document.add(content);
                document.close();

                return byteArrayOutputStream.toByteArray();
            } else if (attachmentName.endsWith(".pdf")) {
                Attachment chosenBugAttachment = selectedBug.getAttachment();
                byte[] chosenBugValue = chosenBugAttachment.getAttachedFile();

                PdfReader reader = new PdfReader(chosenBugValue);
                int pages = reader.getNumberOfPages();

                String attachmentText = "";
                for (int ctr = 1; ctr < pages + 1; ctr++)
                    attachmentText += PdfTextExtractor.getTextFromPage(reader, ctr);

                reader.close();

                content.add("\nBug attachment content:\n\n");
                content.add(attachmentText);

                document.add(content);
                document.close();

                return byteArrayOutputStream.toByteArray();
            } else {

                document.add(content);
                document.close();

                return byteArrayOutputStream.toByteArray();
            }

        } catch (DocumentException e) {
            System.out.println("Document exception hit!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String bugContent(Bug bug) {
        return  "Bug ID: " + bug.getId() + "\nBug title: " + bug.getTitle() + "\nBug description: " + bug.getDescription()
                + "\nBug version: " + String.valueOf(bug.getVersion()) + "\nBug fixing version: " + String.valueOf(bug.getFixingVersion()) + "\nBug target date: "
                + String.valueOf(bug.getTargetDate()) + "\nBug severity: " + String.valueOf(bug.getSeverity()) + "\nBug status: " + String.valueOf(bug.getStatus())
                + "\nBug created by: " + bug.getCreatedBy().getUsername() + "\nBug assigned to: " + bug.getAssignedTo().getUsername()
                + "\nBug notification type:\n" + String.valueOf(bug.getNotification().getNotificationTypeEnum()) + "\n"
                + "\nBug notification message:\n" + bug.getNotification().getNotificationMessage()+ "\n";
    }

}
