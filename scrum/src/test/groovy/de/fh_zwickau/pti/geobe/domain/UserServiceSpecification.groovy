package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.RoleRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserRepository
import de.fh_zwickau.pti.geobe.service.StartupService
import de.fh_zwickau.pti.geobe.service.UserRoleService
import de.fh_zwickau.pti.geobe.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Ignore
import spock.lang.Specification

import javax.transaction.Transactional

/**
 *
 * @author georg beier
 */
//TODO Test rewrite

@SpringApplicationConfiguration(classes = GroovaaApplication)
class UserServiceSpecification extends Specification {

    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private UserRoleService userRoleService
    @Autowired
    private UserService userService
    @Autowired
    private TaskRepository taskRepository


    Project project,project2
    User user,user2
    ScrumRole role,role2
    Task task

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project2 = new Project()
        project2.name = "auch ein Projekt"
        user =new User(firstName: "heinz",lastName: "karl")
        user2 =new User(firstName: "heinz",lastName: "karl2")

        role = new ScrumRole(userRole: ROLETYPE.Developer)
        role2 = new ScrumRole(userRole: ROLETYPE.ProjectOwner)
        task = new CompoundTask(description: 'toptask', estimate: 7000)
    }

    @Autowired
    private StartupService startupService

    public cleanup() {
        //TODO Fix CleanupALL for test when role is assigned
        startupService.cleanupAll()
    }




    @Transactional
    def "get a dto from a user role"() {
        setup:
        cleanup()
        when: 'a project with a task is in the database'
        role.getProject().add(project)
        role.getScrumUser().add(user)
        projectRepository.saveAndFlush(project)
        and: 'we ask for query dtos'
        UserDto.QList qList = userService.getUsers()
        UserDto.QFull qFull = userService.getUserDetails(user.id)
        then:
        assert qList.all.size() == 1
        assert qList.all.keySet().contains(user.id)
        assert qFull.id == user.id


    }
    @Transactional
    def "delete from "() {
        setup:
        cleanup()
        when: 'a project with a task is in the database'
        role.getProject().add(project)
        role.getScrumUser().add(user)
        projectRepository.saveAndFlush(project)
        and: 'we ask for query dtos'

        userService.deleteUser(new UserDto.CDelete(id: user.id))
        projectRepository.deleteAll()
        then:
        assert userRepository.findAll().isEmpty()
        assert roleRepository.findAll().isEmpty()
        assert projectRepository.findAll().isEmpty()

    }
    @Transactional
    def "save and update "() {
        setup:
        cleanup()
        when: 'a project with a task is in the database'
        role.getProject().add(project)
        roleRepository.saveAndFlush(role)
        role.getScrumUser().add(user)
        taskRepository.saveAndFlush(task)
        task.getDevelopers().add(user)
        List<Long> taskIds=[]
        List<Long> roleIds=[]
        user.tasks.all.each {
            taskIds.add(it.id)
        }
        user.roles.all.each {
            roleIds.add(it.id)
        }
        userService.createOrUpdateUser(new UserDto.CSet(
                nick: user.nick,firstName: user.firstName,lastName: user.lastName,
                roleIds: roleIds,taskIds: taskIds
        ))

        then:
        assert !user.tasks.all.isEmpty()
        assert !taskRepository.findAll().isEmpty()
        assert !roleRepository.findAll().isEmpty()
        assert !projectRepository.findAll().isEmpty()
        assert !userRepository.findAll().isEmpty()


    }







}
