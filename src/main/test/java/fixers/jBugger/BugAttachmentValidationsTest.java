package fixers.jBugger;

import fixers.jBugger.DatabaseEntitites.Attachment;
import fixers.jBugger.DatabaseEntitites.Bug;
import fixers.jBugger.DatabaseEnums.BugSeverityEnum;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(Arquillian.class)
public class BugAttachmentValidationsTest extends TeamFixersBaseTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "TeamFixers.war")
                .addPackages(true, "fixers.jBugger")
                //  .addAsLibraries(MavenArtifactLoader.resolve("mysql:mysql-connector-java"))
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("../classes/META-INF/beans.xml", "META-INF/beans.xml");

    }

    @Test
    public void testSizeDescription() {
        try {
            Bug bug2 = new Bug();
            bug2.setTitle("Second");
            bug2.setDescription("I hope");
            bug2.setSeverity(BugSeverityEnum.LOW);
            em.persist(bug2);
        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testNotNullTitle() {
        try {
            Bug bug2 = new Bug();
            bug2.setDescription("I hope I have 10 characters");
            bug2.setSeverity(BugSeverityEnum.LOW);
            em.persist(bug2);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testNotNullDescription() {
        try {
            Bug bug2 = new Bug();
            bug2.setTitle("Second");
            bug2.setSeverity(BugSeverityEnum.LOW);
            em.persist(bug2);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testAttachmentSize() {
        try {
            Attachment attachment2 = new Attachment();
            attachment2.setAttachedFile(Files.readAllBytes(new File("C:\\Users\\ursum\\IdeaProjects\\TeamFixers\\src\\main\\resources\\testAttachment.txt").toPath()));
            em.persist(attachment2);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ConstraintViolationException e2) {
            e2.printStackTrace();
            Assert.assertTrue(true);
        }
    }


    @Override
    protected void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();

        Bug bug1 = new Bug();
        bug1.setTitle("First");
        bug1.setDescription("I hope I have ten characters");
        bug1.setSeverity(BugSeverityEnum.LOW);

        Attachment attachment1 = new Attachment();
        attachment1.setAttachedFile(Files.readAllBytes(new File("C:\\Users\\ursum\\IdeaProjects\\TeamFixers\\src\\main\\test\\resources\\myAttachment.txt").toPath()));
        em.persist(attachment1);

        bug1.setAttachment(attachment1);
        em.persist(bug1);

        utx.commit();

        em.clear();
    }

    @Override
    protected void internalClearData() {
        em.createQuery("delete from Bug").executeUpdate();
//        em.createQuery("delete from Role").executeUpdate();
        em.createQuery("delete from Attachment").executeUpdate();
    }
}
