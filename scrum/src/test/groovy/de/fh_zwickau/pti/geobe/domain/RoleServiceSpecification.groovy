package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.RoleRepository
import de.fh_zwickau.pti.geobe.repository.UserRepository
import de.fh_zwickau.pti.geobe.service.UserRoleService
import de.fh_zwickau.pti.geobe.service.StartupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import spock.lang.Specification

import javax.persistence.Entity
import javax.transaction.Transactional

/**
 *
 * @author georg beier
 */
//TODO Test rewrite

@SpringApplicationConfiguration(classes = GroovaaApplication)
class RoleServiceSpecification extends Specification {

    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private UserRoleService UserRoleService

    Project project,project2
    User user,user2
    ScrumRole role,role2

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project2 = new Project()
        project2.name = "auch ein Projekt"
        user =new User(firstName: "heinz",lastName: "karl")
        user2 =new User(firstName: "heinz",lastName: "karl2")

        role = new ScrumRole(userRole: ROLETYPE.Developer)
        role2 = new ScrumRole(userRole: ROLETYPE.ProjectOwner)

    }

    @Autowired
    private StartupService startupService

    public cleanup() {
        //TODO Fix CleanupALL for test when role is assigned
        //startupService.cleanupAll()
    }



    def "Fill Role"() {
        setup:
        cleanup()
        when: 'role has project'
        role.getProject().add(project)
        and: 'role has user'
        role.getScrumUser().add(user)
        then:
        assert role.getProject().one==project
        assert role.getScrumUser().one==user
        assert true
    }

    @Transactional
    def "Save Role"() {
        setup:
        cleanup()

        when:
        roleRepository.save(role2)
        then:
        assert roleRepository.findAll()
    }

    @Transactional
    def "Update WithProjectAndUser Role"() {
        setup:
        cleanup()
        role2.getProject().add(project2)
        role2.getScrumUser().add(user2)
        when:
        roleRepository.saveAndFlush(role2)
        then:
        assert roleRepository.findAll()
        assert projectRepository.getOne(role2.getProject().one.getId())
        assert userRepository.getOne(role2.getScrumUser().one.getId())

    }





}
