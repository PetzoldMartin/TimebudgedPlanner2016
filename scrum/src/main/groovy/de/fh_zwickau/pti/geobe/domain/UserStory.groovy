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
class UserStory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id
    // domain values
    String name
    String description
    int priority

    // references
    @ManyToOne
    @JoinColumn(name = "project_id")
    protected Project project;
    @Transient
    private ToOne<UserStory, Project> toProject = new ToOne<>(
            { this.@project } as IToAny.IGet,
            { Project p -> this.@project = p } as IToAny.ISet,
            this, { o -> o.userStorys } as IGetOther
    )

    public IToAny<Project> getProject() {
        return toProject;
    }

    @OneToMany(mappedBy = "userStory", cascade = CascadeType.PERSIST)
    private Set<Task> tasks = new HashSet<>();
    @Transient
    private ToMany<UserStory, Task> toTask = new ToMany<>(
            { this.@tasks } as IToAny.IGet, this,
            { Task o -> o.userStory } as IGetOther
    )

    public IToAny<Task> getTask() {
        return toTask;
    }

}
