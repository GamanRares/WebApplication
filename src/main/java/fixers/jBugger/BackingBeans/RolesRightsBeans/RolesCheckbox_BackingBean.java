package fixers.jBugger.BackingBeans.RolesRightsBeans;

import fixers.jBugger.BusinessLogic.RoleEJB;
import fixers.jBugger.DatabaseEntitites.Role;
import lombok.Data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Named
@RequestScoped
public class RolesCheckbox_BackingBean implements Serializable {

    @Inject
    private RoleEJB roleEJB;

    private List<Role> roles = new ArrayList<>();

    @PostConstruct
    public void init() {

        this.roles.addAll(this.roleEJB.getRoles());

    }

}
