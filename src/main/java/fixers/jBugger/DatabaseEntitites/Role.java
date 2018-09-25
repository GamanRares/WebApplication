package fixers.jBugger.DatabaseEntitites;

import fixers.jBugger.DatabaseEnums.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@EqualsAndHashCode(exclude = {"users", "rights"})
@Table(name = "roles")
public class Role implements Serializable {

    public Role(RoleTypeEnum roleTypeEnum, ArrayList<Right> rightList) {
        this.rights = rightList;
        this.type = roleTypeEnum;
    }

    public Role() {
    }

    @Id
    @Enumerated(EnumType.STRING)
    private RoleTypeEnum type;

    @ManyToMany
    @JoinTable(name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "USERNAME"))
    private Collection<User> users;

    @ManyToMany
    @JoinTable(name = "ROLE_RIGHTS",
            joinColumns = @JoinColumn(name = "RIGHT_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    private Collection<Right> rights;

    @Column(name = "BUG_CLOSE_CHECKED")
    private boolean bugCloseChecked;

    @Column(name = "BUG_EXPORT_CHECKED")
    private boolean bugExportChecked;

    @Column(name = "BUG_MANAGEMENT_CHECKED")
    private boolean bugManagementChecked;

    @Column(name = "PERMISSION_MANAGEMENT_CHECKED")
    private boolean permissionManagementChecked;

    @Column(name = "USER_MANAGEMENT_CHECKED")
    private boolean userManagementChecked;

    public String toString() {
        return this.type.toString();
    }

}
