package fixers.jBugger.BackingBeans.MainPagesBeans;

import fixers.jBugger.BackingBeans.RolesRightsBeans.RolesRightsBackingBean;
import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import fixers.jBugger.Loggers.GrowlMessage;
import fixers.jBugger.BusinessLogic.NotificationEJB;
import fixers.jBugger.BusinessLogic.UserEJB;
import fixers.jBugger.DatabaseEntitites.*;
import fixers.jBugger.Utility.MD5Encryption;
import lombok.Data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


@SessionScoped
@Named
@Data
public class LoginBackingBean implements Serializable {
    private String username;
    private String password;

    private String previousUsername = "";

    private Integer triesLeft = 5;

    private User user;
    private List<Right> allUserRights = new ArrayList<>();

    @Inject
    private RolesRightsBackingBean rolesBackingBean;

    @Inject
    private UserEJB userEJB;

    @Inject
    private NotificationEJB notificationEJB;

    @Inject
    private HomepageBackingBean homepageBackingBean;

    @Inject
    private InvalidPageBackingBean invalidPageBackingBean;

    public LoginBackingBean() {
    }

    @PostConstruct
    public void init() {
//        this.goTo();
    }

    private void goTo() {
        String userAgent = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get("User-Agent");

        if (!userAgent.matches(".*(Chrome/([0-9]{3,}|3[6-9]|[4-9][0-9])|Firefox/([0-9]{3,}|3[1-9]|[4-9][0-9])).*")) {
            this.redirect();
        }

    }

    private void redirect() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("InvalidPage.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String LoginCorrect() {

        this.user = userEJB.findUserByUsername(this.username);

        String encryptedPassword = MD5Encryption.encrypt(this.password);
        String databasePassword = this.userEJB.getPasswordByUsername(this.username);

        if (encryptedPassword.equals(databasePassword)) {

            if (!this.isActive(this.username)) {
                GrowlMessage.sendMessage("Info !", "User is deactivated ");
                return null;
            } else {
                this.triesLeft = 5;

                this.allUserRights.clear();

                Iterator<Role> iterator = this.user.getRole().iterator();

                while (iterator.hasNext()) {

                    Role role = iterator.next();
                    RoleTypeEnum roleTypeEnum = role.getType();

                    switch (roleTypeEnum) {
                        case ADMINISTRATOR:
                            this.allUserRights.addAll(rolesBackingBean.getAdministratorRole().getRights());
                            break;
                        case TEST_MANAGER:
                            this.allUserRights.addAll(rolesBackingBean.getTestManagerRole().getRights());
                            break;
                        case TESTER:
                            this.allUserRights.addAll(rolesBackingBean.getTesterRole().getRights());
                            break;
                        case PROJECT_MANAGER:
                            this.allUserRights.addAll(rolesBackingBean.getProjectManagerRole().getRights());
                            break;
                        case DEVELOPER:
                            this.allUserRights.addAll(rolesBackingBean.getDeveloperRole().getRights());
                            break;
                        default:
                            this.allUserRights = null;
                    }

                }

                //Eliminating the duplicate rights
                this.allUserRights = this.allUserRights.stream().distinct().collect(Collectors.toList());

                this.updatePreviousUsername();

                homepageBackingBean.setPage(null);
                return "homepage.xhtml";
            }

        } else {
            if(!this.username.equals(this.previousUsername)) {
                this.triesLeft = 5;
                GrowlMessage.sendMessage("Error !", "User or password are invalid. Tries left : " + --this.triesLeft);
                this.updatePreviousUsername();
            }
            else if (--this.triesLeft == 0) {
                if (this.user != null) {
                    this.userEJB.deactivateUser(this.username);
                    LocalDate now = LocalDate.now();

                    List<User> admins = this.userEJB.getAdministrators();

                    this.notificationEJB.sendNotificationToAdministrators(admins, now, "User Deactivated : " + this.username, NotificationTypeEnum.USER_DEACTIVATED);

                }
                this.triesLeft = 5;

                GrowlMessage.sendMessage("Info !", "User have been deactivated ");
            } else {
                GrowlMessage.sendMessage("Error !", "User or password are invalid. Tries left : " + this.triesLeft);
                this.updatePreviousUsername();
            }

            return null;
        }

    }

    private void updatePreviousUsername() {
        this.previousUsername = this.username;
    }

    private boolean isActive(String username) {

        return this.userEJB.getIsUserActive(username);

    }

    public boolean hasRight(String right) {
        boolean itHasIt = false;

        Iterator<Right> iterator = allUserRights.iterator();

        while (iterator.hasNext()) {
            Right currentRight = iterator.next();
            if (currentRight.getPermission().toString().equals(right))
                itHasIt = true;
        }

        return itHasIt;
    }

    public String getCurrentlyLoggedInUsername() {
        return username;
    }

    public void logout() {
        this.user = null;
        //TODO when he logs out we must cancel his chance of pressing back and relogging so
    }

}