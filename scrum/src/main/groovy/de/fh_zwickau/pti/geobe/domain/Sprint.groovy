package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany
import de.geobe.util.association.ToOne

import javax.persistence.*

/**
 *
 * @author georg beier
 */
@Entity
class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    public Long getId() {
        id
    }

    // domain values
    private String name
    private Date start = new Date()
    private Date end = new Date() + 7

    // references
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    private Project project
    @Transient
    private ToOne<Sprint, Project> toProject = new ToOne<>(
            { this.@project } as IToAny.IGet,
            { Project p -> this.@project = p } as IToAny.ISet,
            this, { o -> o.sprint } as IGetOther
    )

    public IToAny<Project> getProject() { toProject }

    @ManyToMany(mappedBy = 'sprints', cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST])
//    @JoinTable(name = 'join_sprint_task',
//            joinColumns = @JoinColumn(name = 'sprint_id'),
//            inverseJoinColumns = @JoinColumn(name = 'task_id'),
//            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
//            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Set<Task> backlog = new HashSet<>()
    @Transient
    private ToMany<Sprint, Task> toBacklog = new ToMany<>(
            { this.@backlog } as IToAny.IGet, this,
            { Task o -> o.sprint } as IGetOther
    )

    public IToAny<Task> getBacklog() { toBacklog }

}
