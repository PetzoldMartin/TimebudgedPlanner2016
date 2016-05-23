package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.ROLETYPE
import de.fh_zwickau.pti.geobe.domain.ScrumUser
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author georg beier
 */
public interface ScrumUserRepository extends JpaRepository<ScrumUser, Long> {
    //List<ScrumUser> findByRoletype(ROLETYPE type)
    //List<ScrumUser> findByProjectId(Long pId)
    //List<ScrumUser> findByProjectIdAndFindByTaskId(Long pId, Long tId)
}
