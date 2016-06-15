package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.RoleRepository
import de.fh_zwickau.pti.geobe.repository.UserRepository
import de.fh_zwickau.pti.geobe.service.RoleService
import de.fh_zwickau.pti.geobe.service.StartupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

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
    private RoleService userRoleService

    Project project, project2
    User user, user2
    Role role, role2

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project2 = new Project()
        project2.name = "auch ein Projekt"
        user = new User(firstName: "heinz", lastName: "karl")
        user2 = new User(firstName: "heinz", lastName: "karl2")

        role = new Role(userRole: ROLETYPE.Developer)
        role2 = new Role(userRole: ROLETYPE.ProjectOwner)

    }

    @Autowired
    private StartupService startupService

    public cleanup() {
        //TODO Fix CleanupALL for test when role is assignedList
        startupService.cleanupAll()
    }


    def "Fill Role"() {
        setup:
        cleanup()
        when: 'role has project'
        role.getProject().add(project)
        role2.getProject().add(project)

        and: 'role has user'
        role.getScrumUser().add(user)
        role2.getScrumUser().add(user)

        then:
        assert role.getProject().one == project
        assert role.getScrumUser().one == user
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
    def "failed Save Role with Service "() {
        setup:
        cleanup()

        when:
        userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: 0, projectId: 0, userRole: ROLETYPE.Developer))
        then:
        assert roleRepository.findAll().empty
    }

    @Transactional
    def "Save Role with Service"() {
        setup:
        cleanup()
        when: 'role has project'
        projectRepository.saveAndFlush(project)
        role.getProject().add(project)

        and: 'role has user'
        userRepository.saveAndFlush(user)
        role.getScrumUser().add(user)

        and: 'save'
        userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: user.id, projectId: project.id, userRole: ROLETYPE.Developer))
        then:
        assert roleRepository.findAll()
        assert roleRepository.findOne(role.id).project.one == project
        assert roleRepository.findOne(role.id).scrumUser.one == user

    }

    @Transactional
    def "update Role with Service "() {
        setup:
        cleanup()
        when: 'role has project'
        projectRepository.saveAndFlush(project)
        role.getProject().add(project)
        and: 'role has user'
        userRepository.saveAndFlush(user2)
        userRepository.saveAndFlush(user)
        role.getScrumUser().add(user)
        and: 'save'
        userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: user.id, projectId: project.id, userRole: ROLETYPE.Developer))
        role.getScrumUser().add(user2)
        then:
        assert roleRepository.findAll()
        assert roleRepository.findOne(role.id).project.one == project
        assert roleRepository.findOne(role.id).scrumUser.one == user2
    }

    @Transactional
    def "Update WithProjectAndUser Role"() {
        setup:
        cleanup()
        role2.getProject().add(project2)
        role2.getScrumUser().add(user2)
        when:
        userRepository.save(user2)
        projectRepository.saveAndFlush(project2)
        then:
        assert roleRepository.findAll()
        assert projectRepository.getOne(role2.getProject().one.getId())
        assert userRepository.getOne(role2.getScrumUser().one.getId())

    }

    def "get a dto from a user role"() {
        setup:
        cleanup()
        when: 'a project with a task is in the database'
        userRepository.save(user)
        projectRepository.saveAndFlush(project)
        role.getProject().add(project)
        role.getScrumUser().add(user)
        roleRepository.saveAndFlush(role)


        and: 'we ask for query dtos'
        RoleDto.QList qList = userRoleService.getRoles()
        RoleDto.QFull qFull = userRoleService.getRoleDetails(role.id)
        then:
        assert qList.all.size() == 1
        assert qList.all.keySet().contains(role.id)
        assert qList.all.values().userRole.any { it == role.userRole }
        assert qFull.id == role.id
        assert qFull.project.id == role.project.one.id
        assert qFull.user.id == role.scrumUser.one.id
        assert qFull.userRole == role.userRole

    }

    @Transactional
    def "delete from "() {
        setup:
        cleanup()
        when: 'a project with a task is in the database'
        userRepository.save(user)
        projectRepository.saveAndFlush(project)
        role.getProject().add(project)
        role.getScrumUser().add(user)
        roleRepository.saveAndFlush(role)
        and: 'we ask for query dtos'
        RoleDto.QList qList = userRoleService.getRoles()
        RoleDto.QFull qFull = userRoleService.getRoleDetails(role.id)
        userRoleService.deleteRole(new RoleDto.CDelete(id: role.id))

        userRepository.deleteAll()
        projectRepository.deleteAll()
        then:
        assert userRepository.findAll().isEmpty()
        assert roleRepository.findAll().isEmpty()
        assert projectRepository.findAll().isEmpty()

    }


    @Transactional
    def "Save Role with Service and find by Project"() {
        setup:
        cleanup()
        when: 'role has project'
        projectRepository.saveAndFlush(project)
        role.getProject().add(project)

        and: 'role has user'
        userRepository.saveAndFlush(user)
        role.getScrumUser().add(user)

        and: 'save'
        userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: user.id, projectId: project.id, userRole: ROLETYPE.Developer))
        then:
        assert userRoleService.getRolesInProject(project.id)


    }

    @Transactional
    def "Save Role with Service and find not by Project"() {
        setup:
        cleanup()
        when: 'role has project'
        projectRepository.saveAndFlush(project)
        projectRepository.saveAndFlush(project2)

        role.getProject().add(project)

        and: 'role has user'
        userRepository.saveAndFlush(user)
        role.getScrumUser().add(user)

        and: 'save'
        userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: user.id, projectId: project.id, userRole: ROLETYPE.Developer))
        then:
        assert userRoleService.getRolesNotInProject(project2.id)


    }

}
