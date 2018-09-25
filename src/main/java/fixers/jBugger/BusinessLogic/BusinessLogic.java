package fixers.jBugger.BusinessLogic;

import lombok.Data;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

@Data
@Stateless
public class BusinessLogic implements Serializable {

    @PersistenceContext
    protected EntityManager em;

}
