package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEnums.RightsEnum;
import fixers.jBugger.DatabaseEntitites.Right;
import fixers.jBugger.DatabaseEntitites.Right_;
import lombok.Data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.logging.Logger;

@Data
@Stateless
public class RightsEJB implements Serializable {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    private Logger logger;

    public Right getRight(RightsEnum permission) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Right> rightQuery = builder.createQuery(Right.class);
        Root<Right> fromRights = rightQuery.from(Right.class);

        rightQuery.where(builder.equal(fromRights.get(Right_.permission),permission));

        return businessLogic.getEm().createQuery(rightQuery).getSingleResult();

    }

}
