package fixers.jBugger.DatabaseEntitites;

import fixers.jBugger.DatabaseEnums.BugSeverityEnum;
import fixers.jBugger.DatabaseEnums.BugStatusEnum;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(exclude = {"createdBy", "assignedTo", "notification"})
@Getter
@Setter
@Entity
public class Bug implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NotNull(message = "Bug title can't be null!")
    private String title;

    @NotNull(message = "Description can't be null!")
    @Size(min = 10, message = "Description must have at least 10 characters!")
    private String description;

    @Version
    private long version = 0L;

    private long fixingVersion;

    private Date targetDate;

    @Enumerated(EnumType.STRING)
    private BugSeverityEnum severity;

    @Enumerated(EnumType.STRING)
    private BugStatusEnum status;

    @OneToOne
    @JoinColumn(name = "CREATED_BY")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "ASSIGNED_TO")
    private User assignedTo;

    @ManyToOne(cascade = CascadeType.ALL)
    private Attachment attachment;

    @ManyToOne
    @JoinColumn(name = "NOTIFICATION_ID")
    private Notification notification;

}
