package fixers.jBugger.BackingBeans.RolesRightsBeans;

import fixers.jBugger.DatabaseEnums.RightsEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.RightsEJB;
import fixers.jBugger.BusinessLogic.RolesEJB;
import fixers.jBugger.DatabaseEntitites.Right;
import fixers.jBugger.DatabaseEntitites.Role;
import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import lombok.Data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

@SessionScoped
@Named
@Data
public class RolesRightsBackingBean implements Serializable {

    private Right bug_close;
    private Right bug_export_pdf;
    private Right bug_management;
    private Right permission_management;
    private Right user_management;

    private List<Right> rightList;

    private Role administratorRole;
    private Role developerRole;
    private Role projectManagerRole;
    private Role testerRole;
    private Role testManagerRole;
    private List<Role> roleList;

    @Inject
    private RolesEJB rolesEJB;

    @Inject
    private RightsEJB rightsEJB;

    public void submit() {
        Iterator<Role> roleIterator = roleList.iterator();

        while (roleIterator.hasNext()) {
            Role currentRole = roleIterator.next();

            List<Right> newRightsList = new ArrayList<>();
            if (currentRole.isBugCloseChecked())
                newRightsList.add(bug_close);

            if (currentRole.isBugExportChecked())
                newRightsList.add(bug_export_pdf);

            if (currentRole.isBugManagementChecked())
                newRightsList.add(bug_management);

            if (currentRole.isPermissionManagementChecked())
                newRightsList.add(permission_management);

            if (currentRole.isUserManagementChecked())
                newRightsList.add(user_management);

            currentRole.setRights(newRightsList);

            rolesEJB.updateRole(currentRole);
        }

        GrowlMessage.sendMessage("Info !", "Rights updated");

    }

    @PostConstruct
    public void updateChecked() {

        this.bug_close = this.rightsEJB.getRight(RightsEnum.BUG_CLOSE);
        this.bug_export_pdf = this.rightsEJB.getRight(RightsEnum.BUG_EXPORT_PDF);
        this.bug_management = this.rightsEJB.getRight(RightsEnum.BUG_MANAGEMENT);
        this.permission_management = this.rightsEJB.getRight(RightsEnum.PERMISSION_MANAGEMENT);
        this.user_management = this.rightsEJB.getRight(RightsEnum.USER_MANAGEMENT);

        this.rightList = new ArrayList<>(new ArrayList<>(Arrays.asList(bug_close, bug_export_pdf, bug_management, permission_management, user_management)));

        this.administratorRole = rolesEJB.getRole(RoleTypeEnum.ADMINISTRATOR);
        this.developerRole = rolesEJB.getRole(RoleTypeEnum.DEVELOPER);
        this.projectManagerRole = rolesEJB.getRole(RoleTypeEnum.PROJECT_MANAGER);
        this.testerRole = rolesEJB.getRole(RoleTypeEnum.TESTER);
        this.testManagerRole = rolesEJB.getRole(RoleTypeEnum.TEST_MANAGER);
        this.roleList = new ArrayList<>(new ArrayList<>(Arrays.asList(developerRole, projectManagerRole, testerRole, testManagerRole)));

        updateRoleRights(this.developerRole);
        updateRoleRights(this.projectManagerRole);
        updateRoleRights(this.testerRole);
        updateRoleRights(this.testManagerRole);

        setChecked(developerRole);
        setChecked(projectManagerRole);
        setChecked(testerRole);
        setChecked(testManagerRole);
    }

    public void updateRoleRights(Role givenRole) {
        List<Right> newRightList = new ArrayList<>();

        if (givenRole.isBugCloseChecked())
            newRightList.add(bug_close);

        if (givenRole.isBugExportChecked())
            newRightList.add(bug_export_pdf);

        if (givenRole.isBugManagementChecked())
            newRightList.add(bug_management);

        if (givenRole.isPermissionManagementChecked())
            newRightList.add(permission_management);

        if (givenRole.isUserManagementChecked())
            newRightList.add(user_management);

        givenRole.setRights(newRightList);
    }

    public void setChecked(Role role) {
        role.setBugCloseChecked(role.isBugCloseChecked());
        role.setBugExportChecked(role.isBugExportChecked());
        role.setBugManagementChecked(role.isBugManagementChecked());
        role.setPermissionManagementChecked(role.isPermissionManagementChecked());
        role.setUserManagementChecked(role.isUserManagementChecked());
    }
}
