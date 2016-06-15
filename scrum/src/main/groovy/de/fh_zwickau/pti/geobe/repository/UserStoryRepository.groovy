package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.Userstory
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by aisma on 09.05.2016.
 */
interface UserstoryRepository extends JpaRepository<Userstory, Long> {
//    List<Userstory> findByUserStoryId(Long id);
//    List<Userstory> findByProjectId(Long pid);
//    List<Userstory> findByTasksId(Long tid);
//    List<Task> findByProjectIdAndIdNotIn(Long pid, Collection<Long> userStoryIds);
//    List<Task> findByTasksIdAndIdNotIn(Long tid, Collection<Long> userStoryIds);
//    List<Userstory> findAllByOrderByTagAsc();

}