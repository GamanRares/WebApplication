package fixers.jBugger;

import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

public abstract class TeamFixersBaseTest {

  @PersistenceContext(unitName = "test")
  protected EntityManager em;

  @Inject
  protected UserTransaction utx;

  @Before
  public void preparePersistenceTest() throws Exception {
    clearData();
    insertData();
    startTransaction();
  }

  protected void insertData() throws Exception {
    // No data to insert by default
  }

  protected void internalClearData() {
    // No data to clear by default
  }

  private void clearData() throws Exception {
    utx.begin();
    em.joinTransaction();
    System.out.println("Dumping old records...");
    internalClearData();
    utx.commit();
  }

  protected void startTransaction() throws Exception {
    utx.begin();
    em.joinTransaction();
  }

  @After
  public void commitTransaction() throws Exception {
//    utx.commit();
  }
}
