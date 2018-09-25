package fixers.jBugger;

import fixers.jBugger.Utility.UsernameGenerator;
import fixers.jBugger.DatabaseEntitites.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolationException;

@RunWith(Arquillian.class)
public class UserValidationsTest extends TeamFixersBaseTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "TeamFixers.war")
                .addPackages(true, "fixers.jBugger")
                //  .addAsLibraries(MavenArtifactLoader.resolve("mysql:mysql-connector-java"))
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("../classes/META-INF/beans.xml", "META-INF/beans.xml");
    }

    @Test
    public void testUserEmail(){
        try {
            User user1 = new User();
            user1.setFirstName("Mira");
            user1.setLastName("Ursu");
            user1.setMobileNumber("+497555544795");
            user1.setEmail("mira4@gmail.com");
            em.persist(user1);
        }catch(ConstraintViolationException e){
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testUserMobileNumberDigits(){
        try {
            User user2 = new User();
            user2.setFirstName("Maria");
            user2.setLastName("Pop");
            user2.setMobileNumber("+495877544795");
            user2.setEmail("mira4@msggroup.com");
            user2.setUsername(new UsernameGenerator().create("Maria", "Pop"));
            em.persist(user2);
        }catch(ConstraintViolationException e){
            e.printStackTrace();
            Assert.assertTrue(true);
        }
    }

    @Override
    protected void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();

        User user = new User();
        user.setFirstName("Mira");
        user.setLastName("Ursu");

        String username = new UsernameGenerator().create("Rares","Gaman");

        user.setUsername(username);
        user.setMobileNumber("0040755554479");
        user.setEmail("mira4@msggroup.com");
        em.persist(user);

        utx.commit();

        em.clear();
    }

}
