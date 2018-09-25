package fixers.jBugger.DatabaseEntitites;

import fixers.jBugger.Validators.EmailValidation;
import fixers.jBugger.Validators.MobileNumberValidation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = {"role", "notifications"})
@Table(name = "User_Entity")
public class User implements Serializable {


    @NotNull
    @Id
    private String username = "asd";

    @NotNull
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull
    @Column(name = "LAST_NAME")
    private String lastName;

    @NotNull
    @MobileNumberValidation
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @NotNull
    @EmailValidation
    @Column(name = "EMAIL")
    private String email;

    @NotNull
    private String password;

    @NotNull
    @Column(name = "IS_ACTIVE")
    private boolean isActive;

    @ManyToMany
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "USERNAME"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Collection<Role> role;

    @ManyToMany
    @JoinTable(name = "USER_NOTIFICATIONS",
            joinColumns = @JoinColumn(name = "USERNAME"),
            inverseJoinColumns = @JoinColumn(name = "NOTIFICATION_ID"))
    private Collection<Notification> notifications;

    @OneToMany(mappedBy = "assignedTo")
    private List<Bug> bugsAssigned;

    public String toString() {
        return this.username;
    }

}
