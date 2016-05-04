package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther;
import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToMany;
import de.geobe.util.association.ToOne;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    protected String tag = "";
    protected String description = "Task ist noch nicht beschrieben";

    public Long getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // abstract task methods
    public abstract long getEstimate();

    public abstract long getSummedEstimate();

    public abstract long getSpent();

    public abstract boolean isCompleted();

    // references
    @ManyToOne
    @JoinColumn(name = "project_id")
    protected Project project;
    @Transient
    private ToOne<Task, Project> toProject = new ToOne<>(
            {this.@project } as IToAny.IGet,
            { Project p -> this.@project = p } as IToAny.ISet,
            this, { o -> o.backlog } as IGetOther
    )

    public IToAny<Project> getProject() {
        return toProject;
    }

    @ManyToMany(mappedBy = "backlog")
    protected Set<Sprint> sprints = new HashSet<>();
    @Transient
    private ToMany<Task, Sprint> toSprint = new ToMany<>(
            { this.@sprints } as IToAny.IGet, this,
            { Sprint o -> o.backlog} as IGetOther
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
