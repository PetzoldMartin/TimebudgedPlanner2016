package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.Role
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author georg beier
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByOrderByIdDesc();
    List<Role> findByProjectId(Long pid);
    List<Role> findByProjectIdNotLike(Long pid);
    List<Role> findByProjectIdAndScrumUserId(Long pid, Long uid);
}
