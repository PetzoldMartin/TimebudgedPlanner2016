package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany

import javax.persistence.*

/**
 * Created by aisma on 01.04.2016.
 */
@Entity
public class User {
    @Id
    @Column(name = "scrumUser_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    // domain values
    String nick
    String password
    String firstName
    String lastName
    Date birthdate

    // references
    @OneToMany(mappedBy = "scrumUser", cascade = CascadeType.ALL)
    private Set<ScrumRole> roles = new HashSet<>();
    @Transient
    private ToMany<User, ScrumRole> toRoles = new ToMany<>(

            { this.@roles } as IToAny.IGet, this,
            { ScrumRole o -> o.scrumUser } as IGetOther

    )

    public IToAny<ScrumRole> getRoles() { return toRoles; }

    @ManyToMany(mappedBy = "developers",cascade = CascadeType.PERSIST)
    protected Set<Task> tasks = new HashSet<>();
    @Transient
    private ToMany<User, Task> toTask = new ToMany<>(
            { this.@tasks } as IToAny.IGet, this,
            { Task o -> o.developers } as IGetOther
    )

    public IToAny<Sprint> getSprint() {
        return toTask;
    }
}
