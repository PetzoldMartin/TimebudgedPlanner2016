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
    @JoinColumn(name = "userStory_id")
    protected UserStory userStory
    @Transient
    private ToOne<Task, UserStory> toUserStory = new ToOne<>(
            { this.@userStory } as IToAny.IGet,
            { UserStory u -> this.@userStory = u } as IToAny.ISet,
            this, { o -> o.task } as IGetOther
    )

    public IToAny<UserStory> getUserStory() { toUserStory }


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
}
