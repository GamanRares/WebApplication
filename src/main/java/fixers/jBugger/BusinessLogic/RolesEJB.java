package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import fixers.jBugger.DatabaseEntitites.*;
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
public class RolesEJB implements Serializable {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    private Logger logger;

    public Role getRole(RoleTypeEnum type) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Role> roleQuery = builder.createQuery(Role.class);
        Root<Role> selectedRole = roleQuery.from(Role.class);

        roleQuery.where(builder.equal(selectedRole.get(Role_.type), type));

        List<Role> roles = businessLogic.getEm().createQuery(roleQuery)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return (roles.isEmpty()) ? null : roles.get(0);
    }

    public void updateRole(Role role) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<Role> roleQuery = builder.createQuery(Role.class);
        Root<Role> e = roleQuery.from(Role.class);
        roleQuery.where(builder.equal(e.get(Role_.type), role.getType()));

        Role roleToUpdate = this.businessLogic.getEm().createQuery(roleQuery).getSingleResult();

        roleToUpdate.setRights(role.getRights());
        roleToUpdate.setBugManagementChecked(role.isBugManagementChecked());
        roleToUpdate.setBugCloseChecked(role.isBugCloseChecked());
        roleToUpdate.setBugExportChecked(role.isBugExportChecked());
        roleToUpdate.setPermissionManagementChecked(role.isPermissionManagementChecked());
        roleToUpdate.setUserManagementChecked(role.isUserManagementChecked());

        this.getBusinessLogic().getEm().persist(roleToUpdate);
    }

}

