package de.fh_zwickau.pti.geobe.repository;

import de.fh_zwickau.pti.geobe.domain.Project;
import de.fh_zwickau.pti.geobe.domain.ScrumRole;
import de.fh_zwickau.pti.geobe.domain.ScrumRoleID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author georg beier
 */
public interface ScrumRoleRepository extends JpaRepository<ScrumRole, ScrumRoleID> {
}