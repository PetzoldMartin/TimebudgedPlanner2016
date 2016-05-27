package de.fh_zwickau.pti.geobe.repository

import de.fh_zwickau.pti.geobe.domain.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author georg beier
 */
public interface UserRepository extends JpaRepository<User, Long> {
    //List<User> findByRoletype(ROLETYPE type)
    //List<User> findByProjectId(Long pId)
    //List<User> findByProjectIdAndFindByTaskId(Long pId, Long tId)
}
