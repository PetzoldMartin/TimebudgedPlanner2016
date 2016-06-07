package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.ScrumRole
import de.fh_zwickau.pti.geobe.domain.User
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserDto
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
class UserService {
    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private UserRoleService userRoleService
    @Autowired
    private TaskService taskService

//    public UserDto.QList getUsers() {
//        RoleDto.QList qList = new RoleDto.QList()
//        roleRepository.findAllByOrderByIdDesc().each { User sp ->
//            def node = new UserDto.QNode(nick: sp.nick,firstName: sp.firstName,lastName: sp.lastName,
//                    roles: sp.getRoles().,tasks: taskService.getTasks(),
//            )
//
//            qList.all[sp.id] = node
//        }
//        qList
//
//    }

    public UserDto.QFull getUserDetails(Long pid) {
        ScrumRole p = roleRepository.findOne(pid)
        makeQFull(p)
    }
    private  makeQFull(ScrumRole p) {
        if (p) {
            RoleDto.QFull qFull = new RoleDto.QFull()
            qFull.project = new ProjectDto.QFull(name: ((Project)(p.project.one)).getName(),
                    id: ((Project)(p.project.one)).getId()
            )
            qFull.user = new UserDto.QFull(id: ((User)(p.scrumUser.one)).getId())
            qFull.id=p.id
            qFull.userRole=p.userRole
            qFull
        } else {
            new RoleDto.QFull()
        }
    }
    public deleteUser(UserDto.CDelete command) {
        userRepository.delete(command.id)
    }
}
