package fixers.jBugger.DatabaseEntitites;

import fixers.jBugger.DatabaseEnums.NotificationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;

@Data
@Entity
@EqualsAndHashCode(exclude = {"users", "bugs"})
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer notification_id;

    @ManyToMany
    @JoinTable(name = "USER_NOTIFICATIONS",
            joinColumns = @JoinColumn(name = "NOTIFICATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "USERNAME"))
    private Collection<User> users;

    @OneToMany(mappedBy = "notification")
    private Collection<Bug> bugs;

    @NotNull
    @Column(name = "NOTIFICATION_TYPE")
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum notificationTypeEnum;

    @Lob
    @NotNull
    @Column(name = "NOTIFICATION_MESSAGE")
    private String notificationMessage;

    @NotNull
    @Column(name = "ADDED_DATE")
    private LocalDate addedDate;

    public String toString(){
        return this.getNotificationTypeEnum().toString();
    }

}
