package fixers.jBugger.BusinessLogic;

import fixers.jBugger.DatabaseEntitites.Role;
import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import fixers.jBugger.DatabaseEntitites.User;
import fixers.jBugger.DatabaseEntitites.User_;
import lombok.Data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Data
@Stateless
public class UserEJB implements Serializable {

    @Inject
    private BusinessLogic businessLogic;

    @Inject
    private RoleEJB roleEJB;

    @Inject
    private Logger logger;

    public void addUser(String firstName, String lastName, String mobileNumber, String email, String password, List<Role> roles, String username) throws ConstraintViolationException {
        User user = new User();
        setUserAttributes(firstName, lastName, mobileNumber, email, true, password, roles, username, user);

        businessLogic.getEm().persist(user);
    }

    public List<User> getAdministrators() {

        Role administratorRole = this.roleEJB.getRole(RoleTypeEnum.ADMINISTRATOR);

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> administratorQuery = builder.createQuery(User.class);
        Root<User> fromUser = administratorQuery.from(User.class);

        administratorQuery.where(builder.equal(fromUser.get(User_.role), administratorRole));

        return this.businessLogic.getEm().createQuery(administratorQuery).getResultList();

    }

    public List<User> getUsers() {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> usernameQuery = builder.createQuery(User.class);
        Root<User> fromUser = usernameQuery.from(User.class);

        return businessLogic.getEm().createQuery(usernameQuery).getResultList();

    }

    public List<User> getActiveUsers() {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> usernameQuery = builder.createQuery(User.class);
        Root<User> fromUser = usernameQuery.from(User.class);

        usernameQuery.where(builder.equal(fromUser.get(User_.isActive), true));

        return businessLogic.getEm().createQuery(usernameQuery).getResultList();

    }

    public List<String> getUsernames() {

        List<User> users = this.getUsers();
        List<String> usernames = new ArrayList<>();

        users.forEach(user -> usernames.add(user.getUsername()));

        return usernames;

    }


    public User findUserByUsername(String username) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> usernameQuery = builder.createQuery(User.class);
        Root<User> fromUser = usernameQuery.from(User.class);

        usernameQuery.where(builder.equal(fromUser.get(User_.username), username));

        List<User> users = businessLogic.getEm().createQuery(usernameQuery)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return (users.isEmpty()) ? null : users.get(0);

    }

    public boolean getIsUserActive(String username) {

        User user = this.findUserByUsername(username);

        if (user != null)
            return user.isActive();

        return false;

    }

    public String getPasswordByUsername(String username) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<String> passwordQuery = builder.createQuery(String.class);
        Root<User> fromUser = passwordQuery.from(User.class);

        passwordQuery.select(fromUser.get(User_.password));
        passwordQuery.where(builder.equal(fromUser.get(User_.username), username));

        List<String> passwords = businessLogic.getEm().createQuery(passwordQuery)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();

        return (passwords.isEmpty()) ? null : passwords.get(0);

    }

    public void updateUser(String username, String firstName, String lastName, String mobileNumber, String email, boolean isActive, String password, List<Role> roles) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> update = builder.createQuery(User.class);
        Root<User> fromUser = update.from(User.class);

        update.where(builder.equal(fromUser.get(User_.username), username));

        User user = businessLogic.getEm().createQuery(update).getSingleResult();
        setUserAttributes(firstName, lastName, mobileNumber, email, isActive, password, roles, "", user);

    }

    public void deactivateUser(String username) {

        CriteriaBuilder builder = businessLogic.getEm().getCriteriaBuilder();

        CriteriaQuery<User> update = builder.createQuery(User.class);
        Root<User> e = update.from(User.class);

        update.where(builder.equal(e.get(User_.username), username));

        User user = businessLogic.getEm().createQuery(update).getSingleResult();
        user.setActive(false);

        businessLogic.getEm().persist(user);

    }

    private void setUserAttributes(String firstName, String lastName, String mobileNumber, String email, boolean isActive, String password, List<Role> roles, String username, User user) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobileNumber(mobileNumber);
        user.setEmail(email);
        if (!password.equals(""))
            user.setPassword(password);
        user.setRole(roles);
        if (!username.equals(""))
            user.setUsername(username);
        user.setActive(isActive);
    }

}
