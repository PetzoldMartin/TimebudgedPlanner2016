package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.Task
import org.springframework.data.jpa.repository.JpaRepository

/**
 *
 * @author georg beier
 */
//TODO Build Repository with more funktions

public interface TaskRepository extends JpaRepository<Task, Long> {
        //   List<Task> findByProjectId(Long id);
        //   List<Task> findByProjectIdAndIdNotIn(Long pid, Collection<Long> taskIds);
    List<Task> findBySprintsIdAndIdNotIn(Long spid, Collection<Long> taskIds);
        //   List<Task> findBySprintsId(Long spid);
        //   List<Task> findAllByOrderByTagAsc();
//    @Query(value = "SELECT * FROM TASK NATURAL JOIN COMPOUND_TASK", nativeQuery = true)
//    List<CompoundTask> findAllCompoundTask();
//    @Query(value = "SELECT * FROM TASK NATURAL JOIN SUBTASK", nativeQuery = true)
//    List<Subtask> findAllSubtask();
}
