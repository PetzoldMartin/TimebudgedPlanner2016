package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany
import de.geobe.util.association.ToOne

import javax.persistence.*

/**
 * @author Heliosana
 * @date 04.05.2016.
 */

@Entity
class Userstory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id
    // domain values
    String name
    String description
    int priority

    // references
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    protected Project project;
    @Transient
    private ToOne<Userstory, Project> toProject = new ToOne<>(
            { this.@project } as IToAny.IGet,
            { Project p -> this.@project = p } as IToAny.ISet,
            this, { o -> o.userstorys } as IGetOther
    )

    public IToAny<Project> getProject() {
        return toProject;
    }

    @OneToMany(mappedBy = "userstory", cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();
    @Transient
    private ToMany<Userstory, Task> toTask = new ToMany<>(
            { this.@tasks } as IToAny.IGet, this,
            { Task o -> o.userstory } as IGetOther
    )

    public IToAny<Task> getTask() {
        return toTask;
    }

}
