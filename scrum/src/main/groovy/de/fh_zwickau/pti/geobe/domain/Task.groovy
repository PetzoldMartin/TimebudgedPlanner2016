package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany
import de.geobe.util.association.ToOne

import javax.persistence.*

/**
 * An abstract superclass for tasks
 * @author georg beier
 */

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    // domain values
    protected String tag = ""
    protected String description = "Task ist noch nicht beschrieben";

    // abstract task methods
    public abstract boolean isCompleted();

    // references
    @ManyToOne
    @JoinColumn(name = "userstory_id")
    protected Userstory userstory
    @Transient
    private ToOne<Task, Userstory> toUserStory = new ToOne<>(
            { this.@userstory } as IToAny.IGet,
            { Userstory u -> this.@userstory = u } as IToAny.ISet,
            this, { o -> o.task } as IGetOther
    )

    public IToAny<Userstory> getUserstory() { toUserStory }


    @ManyToMany(mappedBy = "backlog")
    protected Set<Sprint> sprints = new HashSet<>();
    @Transient
    private ToMany<Task, Sprint> toSprint = new ToMany<>(
            { this.@sprints } as IToAny.IGet, this,
            { Sprint o -> o.backlog } as IGetOther
    )

    public IToAny<Sprint> getSprint() {
        return toSprint;
    }

    @ManyToOne
    @JoinColumn(name = "supertask_id")
    protected CompoundTask supertask;
    @Transient
    private ToOne<Task, CompoundTask> toSupertask = new ToOne<>(
            { this.@supertask } as IToAny.IGet,
            { CompoundTask p -> this.@supertask = p } as IToAny.ISet,
            this, { o -> o.subtask } as IGetOther
    )

    public IToAny<CompoundTask> getSupertask() {
        return toSupertask;
    }

    @ManyToMany
    @JoinTable(name = 'join_task_developer',
            joinColumns = @JoinColumn(name = 'task_id'),
            inverseJoinColumns = @JoinColumn(name = 'developer_id'))
    private Set<ScrumUser> developers = new HashSet<>()
    @Transient
    private ToMany<Task, ScrumUser> toDeveloper = new ToMany<>(
            { this.@developers } as IToAny.IGet, this,
            { ScrumUser o -> o.tasks} as IGetOther
    )

    public IToAny<Task> getBacklog() { toDeveloper }
}
