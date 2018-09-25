package fixers.jBugger.DatabaseEntitites;

import fixers.jBugger.DatabaseEnums.RightsEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Data
@Entity
@Table(name = "RIGHTS")
@EqualsAndHashCode(exclude = "roles")
public class Right implements Serializable {

    public Right(RightsEnum permission) {
        this.permission = permission;
    }

    public Right() {
    }

    @Id
    @Enumerated(EnumType.STRING)
    private RightsEnum permission;

    @ManyToMany
    @JoinTable(name = "ROLE_RIGHTS",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "RIGHT_ID"))
    private Collection<Role> roles;
}
