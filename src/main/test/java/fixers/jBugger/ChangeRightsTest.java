package fixers.jBugger;

import fixers.jBugger.DatabaseEnums.RightsEnum;
import fixers.jBugger.DatabaseEntitites.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@RunWith(Arquillian.class)
public class ChangeRightsTest extends TeamFixersBaseTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "TeamFixers.war")
                .addPackages(true, "fixers.jBugger")
                //  .addAsLibraries(MavenArtifactLoader.resolve("mysql:mysql-connector-java"))
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("../classes/META-INF/beans.xml", "META-INF/beans.xml");
    }

    @Test
    public void testChangeRights() {
        try {


        } catch (ConstraintViolationException e) {
            Assert.assertTrue(true);
        }
    }


    @Override
    protected void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();

        Role administrator = new Role();

        Right bugManagement = new Right(RightsEnum.BUG_MANAGEMENT);
        Right bugExportPDF = new Right(RightsEnum.BUG_EXPORT_PDF);
        Right bugClose = new Right(RightsEnum.BUG_CLOSE);
        Right permissionManagement = new Right(RightsEnum.PERMISSION_MANAGEMENT);
        Right userManagement = new Right(RightsEnum.USER_MANAGEMENT);

        List<Right> rightList = new ArrayList<>();
        rightList.add(bugManagement);
        rightList.add(permissionManagement);
        administrator.setRights(rightList);
        em.persist(bugManagement);
        em.persist(bugExportPDF);
        em.persist(bugClose);
        em.persist(permissionManagement);
        em.persist(userManagement);

        em.persist(administrator);

        utx.commit();

        em.clear();
    }

    @Override
    protected void internalClearData() {
        em.createQuery("delete from Right").executeUpdate();
        em.createQuery("delete from Role").executeUpdate();
    }
}
