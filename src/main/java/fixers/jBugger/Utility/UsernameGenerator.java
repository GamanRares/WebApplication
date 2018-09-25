package fixers.jBugger.Utility;

import fixers.jBugger.BusinessLogic.UserEJB;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Logger;

@Stateless
public class UsernameGenerator implements Serializable {

    @Inject
    private UserEJB userEJB;

    @Inject
    private Logger logger;

    public String create(String firstName, String lastName) {

        StringBuilder username = new StringBuilder();
        StringBuilder unspacedLastName = new StringBuilder();
        boolean isUsernameOK = false;
        Integer lastNameLetters = 5;
        Integer maxLetters = 6;

        getUnspacedLastName(firstName, unspacedLastName);

        while (!isUsernameOK) {

            username.delete(0, username.length());

            Integer endingIndex = Math.min(lastNameLetters + 1, lastName.length());

            username.append(lastName, 0, endingIndex);

            for (int i = 0; i < maxLetters - endingIndex; i++) {
                Integer min = Math.min(i, unspacedLastName.length() - 1);

                username.append(unspacedLastName.charAt(min));
            }

            isUsernameOK = isUsernameUnique(username.toString().toLowerCase());

            lastNameLetters--;
        }

        return username.toString().toLowerCase();

    }

    private boolean isUsernameUnique(String username){
        return userEJB.findUserByUsername(username) == null;
    }

    private void getUnspacedLastName(String firstName, StringBuilder unspacedLastName) {
        String[] lastNames = firstName.split(" ");
        for (String aux : lastNames)
            unspacedLastName.append(aux);
    }

}
