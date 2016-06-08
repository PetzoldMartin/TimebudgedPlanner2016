package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.ScrumRole
import de.fh_zwickau.pti.geobe.domain.Sprint
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.User
import de.fh_zwickau.pti.geobe.domain.Userstory
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.SprintDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.RoleRepository
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
class UserRoleService {
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
    public RoleDto.QList getRoles() {
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findAllByOrderByIdDesc().each { ScrumRole sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: ((User) (sp.scrumUser.one)).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }

    public RoleDto.QFull getRoleDetails(Long pid) {
        ScrumRole p = roleRepository.findOne(pid)
        makeQFull(p)
    }

    private makeQFull(ScrumRole p) {
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

    public deleteUserRole(RoleDto.CDelete command) {
        roleRepository.getOne(command.id).each { ScrumRole it ->
            it.scrumUser.removeAll()
            it.project.removeAll()
        }
        roleRepository.delete(command.id)
    }

    public createOrUpdateRole(RoleDto.CSet command) {
        if (!command.userId | !command.projectId | !command.userRole) return new RoleDto.QFull()
        else
         {
            ScrumRole sr
            if (command.id) {
                roleRepository.getOne(command.id).each {
                    if (command.userId) it.scrumUser = userRepository.getOne(command.userId)
                    if (command.projectId) it.project = projectRepository.getOne(command.projectId)
                    it.userRole = command.userRole
                }
            } else {
                sr = new ScrumRole(
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
        roleRepository.findByProjectId(command).each { ScrumRole sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: ((User) (sp.scrumUser.one)).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }

    public getRolesNotInProject(Long command){
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findByProjectIdNotLike(command).each { ScrumRole sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: ((Project) (sp.project.one)).getName())
                    , user: new UserDto.QNode(id: ((User) (sp.scrumUser.one)).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }
}
