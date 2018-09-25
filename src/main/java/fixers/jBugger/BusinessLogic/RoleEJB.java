package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEntitites.Role;
import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import fixers.jBugger.DatabaseEntitites.Role_;
import lombok.Data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Data
@Stateless
public class RoleEJB implements Serializable {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    private Logger logger;

    public Role getRole(RoleTypeEnum roleTypeEnum) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Role> roleQuery = builder.createQuery(Role.class);
        Root<Role> fromRole = roleQuery.from(Role.class);

        roleQuery.where(builder.equal(fromRole.get(Role_.type), roleTypeEnum));

        return businessLogic.getEm().createQuery(roleQuery).getSingleResult();

    }

    public List<Role> getRoles() {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Role> roleQuery = builder.createQuery(Role.class);
        Root<Role> fromRole = roleQuery.from(Role.class);

        return businessLogic.getEm().createQuery(roleQuery).getResultList();

    }

}
