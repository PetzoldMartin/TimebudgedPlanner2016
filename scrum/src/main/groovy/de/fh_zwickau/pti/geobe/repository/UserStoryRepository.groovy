package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.CompoundTask
import de.fh_zwickau.pti.geobe.domain.Subtask
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.UserStory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Created by aisma on 09.05.2016.
 */
//TODO Build Repository with more funktions
interface UserStoryRepository extends JpaRepository<UserStory, Long> {
//    List<UserStory> findByUserStoryId(Long id);
//    List<UserStory> findByProjectId(Long pid);
//    List<UserStory> findByTasksId(Long tid);
//    List<Task> findByProjectIdAndIdNotIn(Long pid, Collection<Long> userStoryIds);
//    List<Task> findByTasksIdAndIdNotIn(Long tid, Collection<Long> userStoryIds);
//    List<UserStory> findAllByOrderByTagAsc();

}