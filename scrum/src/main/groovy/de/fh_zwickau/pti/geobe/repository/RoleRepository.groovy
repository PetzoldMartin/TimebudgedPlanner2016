package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.ScrumRole
import de.fh_zwickau.pti.geobe.domain.Sprint
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author georg beier
 */
public interface RoleRepository extends JpaRepository<ScrumRole, Long> {
    List<ScrumRole> findAllByOrderByIdDesc();
    List<ScrumRole> findByProjectId(Long pid);
    List<ScrumRole> findByProjectIdNotLike(Long pid);
    List<ScrumRole> findByProjectIdAndScrumUserId(Long pid,Long uid);
}
