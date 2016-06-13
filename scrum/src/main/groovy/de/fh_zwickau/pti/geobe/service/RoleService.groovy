package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.Role
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.User
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.RoleRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Created by aisma on 24.05.2016.
 */
@Service
@Transactional
@Slf4j
class RoleService {
    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private ProjectService projectService
    @Autowired
    private UserService userService
    @Autowired
    private  TaskRepository taskRepository
    @Autowired
    private TaskService taskService

    public RoleDto.QList getRoles() {
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findAllByOrderByIdDesc().each { Role sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: ((User) (sp.scrumUser.one)).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }

    public RoleDto.QFull getRoleDetails(Long pid) {
        Role p = roleRepository.findOne(pid)
        makeQFull(p)
    }

    public  RoleDto.QFull getRoleofProjectAndUser(Long commandProject,long commandUser) {
        makeQFull(roleRepository.findByProjectIdAndScrumUserId(commandProject, commandUser))
    }

    private makeQFull(Role p) {
        if (p) {
            RoleDto.QFull qFull = new RoleDto.QFull()
            qFull.project = projectService.getProjectDetails(p.project.one.id)
            qFull.user = userService.getUserDetails(p.scrumUser.one.id)
            qFull.id = p.id
            qFull.userRole = p.userRole
            qFull
        } else {
            new RoleDto.QFull()
        }
    }

    @Transactional
    public deleteRole(RoleDto.CDelete command) {
        if (command.id) {
            Role delete = roleRepository.getOne(command.id)
            //FIXME
            delete.scrumUser.one.tasks.removeAll()
//            roleRepository.saveAndFlush(delete.scrumUser.one)
            delete.scrumUser.remove(delete.scrumUser.one)
            delete.project.remove(delete.project.one)
            roleRepository.delete(command.id)
        }
    }

    public createOrUpdateRole(RoleDto.CSet command) {
        if (!command.userId | !command.projectId | !command.userRole) return new RoleDto.QFull()
        else
         {
            Role sr
            if (command.id) {
                sr = roleRepository.getOne(command.id).each {
                    if (command.userId) it.scrumUser = userRepository.getOne(command.userId)
                    if (command.projectId) it.project = projectRepository.getOne(command.projectId)
                    it.userRole = command.userRole
                }
            } else {
                sr = new Role(
                        scrumUser: userRepository.getOne(command.userId),
                        project: projectRepository.getOne(command.projectId),
                        userRole: command.userRole
                )
            }
            makeQFull(roleRepository.saveAndFlush(sr))
        }

    }

    public RoleDto.QList getRolesInProject(Long command) {
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findByProjectId(command).each { Role sp ->
            def node = new RoleDto.QNode(id: sp.id, userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: sp.scrumUser.one.id, nick: sp.scrumUser.one.nick, firstName: sp.scrumUser.one.firstName, lastName: sp.scrumUser.one.lastName)
            )
            qList.all[sp.id] = node
        }
        qList

    }

    public getRolesNotInProject(Long command){
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findByProjectIdNotLike(command).each { Role sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: ((User) (sp.scrumUser.one)).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }
}
