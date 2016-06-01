package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.Transient

/**
 * a task that has subtasks
 * @author georg beier
 */
@Entity
public class CompoundTask extends Task {

    // domain values
    long estimate

    public CompoundTask() {}

    /**
     * create a Compoundtask from a subtask and put it into the task hierarchy
     *
     * @param stask
     */
    public CompoundTask(Subtask stask) {
        tag = stask.tag
        estimate = stask.estimate
        description = stask.description
        stask.description = ''
        if (stask.sprint.one)
            sprint.add(stask.sprint.one)
        if (stask.supertask.one) {
            supertask.add(stask.supertask.one)
        }
        if (stask.userstory.one) {
            userstory.add(stask.userstory.one)
        }
        //TODO extend by other references
//        if (stask.scrumUser.one) {
//            scrumuser.add(stask.scrumUser.one)
//        }
        subtask.add(stask)
    }

    public long getSummedEstimate() {
        subtask.all.empty ? this.@estimate : subtask.all.sum { it.summedEstimate }
    }

    public long getSpent() {
        subtask.all.sum(0) { it.spent }
    }

    @Override
    public boolean isCompleted() {
        subtask.all ?
                subtask.all.every { it.isCompleted() } :
                false
    }

    // references
    @OneToMany(mappedBy = "supertask", cascade = CascadeType.ALL)
    private Set<Task> subtasks = new HashSet<>();
    @Transient
    private ToMany<CompoundTask, Task> toSubtask = new ToMany<>(
            { this.@subtasks } as IToAny.IGet, this,
            { Task o -> o.supertask } as IGetOther
    )

    public IToAny<Task> getSubtask() {
        return toSubtask;
    }


}
