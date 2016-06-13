package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Role
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.User
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
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
class UserService {
    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private RoleService roleService
    @Autowired
    private UserRepository userRepository
    @Autowired
    private RoleService userRoleService
    @Autowired
    private TaskService taskService
    @Autowired
    private TaskRepository taskRepository

    public UserDto.QList getUsers() {
        UserDto.QList qList = new UserDto.QList()
        userRepository.findAll().each { User sp ->
            UserDto.QNode node = new UserDto.QNode(id: sp.id, nick: sp.nick, firstName: sp.firstName, lastName: sp.lastName)
            sp.roles.getAll().each {
                Role sr ->
                    RoleDto.QNode usDto = new RoleDto.QNode(id: sr.id, userRole: sr.userRole)
                    node.roles.add(usDto)
            }
            sp.tasks.getAll().each {
                Task ts ->
                    TaskDto.QNode tusDto = new TaskDto.QNode(id: ts.id)
                    node.tasks.add(tusDto)

            }
            qList.all[sp.id] = node
        }
        qList

    }

    public UserDto.QFull getUserDetails(Long pid) {
        User p = userRepository.findOne(pid)
        makeQFull(p)
    }

    private makeQFull(User user) {
        if (user) {
            UserDto.QFull qFull = new UserDto.QFull()
            qFull.roles = new RoleDto.QList()
            user.roles.getAll().each {
                Role sr ->
                    qFull.roles.all[sr.id] = sr.id
            }
            qFull.tasks = new TaskDto.QList()
            user.tasks.getAll().each {
                Task ts ->
                    qFull.tasks.all[ts.id] = ts.id

            }
            //qFull.roles=p.roles.all
            //qFull.tasks=p.tasks.all
            qFull.id = user.id
            qFull.birthdate = user.birthdate
            qFull.firstName = user.firstName
            qFull.lastName = user.lastName
            qFull.nick = user.nick
            qFull
        } else {
            new RoleDto.QFull()
        }
    }

    public deleteUser(UserDto.CDelete command) {
        def user = userRepository.getOne(command.id)
        user.roles.all.each { Role role ->
            userRoleService.deleteRole(new RoleDto.CDelete(id: role.id))
        }
        user.tasks.removeAll()
        userRepository.delete(command.id)
    }


    public createOrUpdateUser(UserDto.CSet command) {
        User u
        if (command.id) {
            u = userRepository.getOne(command.id)
            u.firstName = command.firstName
            u.lastName = command.lastName
            u.nick = command.nick
            u.birthdate = command.birthdate
            u.password = command.password
            command.taskIds.each {
                u.tasks.add(taskRepository.getOne(it))
            }
            command.roleIds.each {
                u.roles.add(roleRepository.getOne(it))
            }
        } else {

            u = new User(firstName: command.firstName, lastName: command.lastName, nick: command.nick, birthdate: command.birthdate, password: command.password)
            command.taskIds.each {
                u.tasks.add(taskRepository.getOne(it))
            }
            command.roleIds.each {
                u.roles.add(roleRepository.getOne(it))
            }
        }
        makeQFull(userRepository.saveAndFlush(u))
    }

    public UserDto.QList getUsersNotInProject(Long command) {
        UserDto.QList qList = new UserDto.QList()
        userRepository.findAll().each { User sp ->
            if(!roleRepository.findByProjectIdAndScrumUserId(command,sp.id).isEmpty()){

            }
            else {
                UserDto.QNode node = new UserDto.QNode(id: sp.id, nick: sp.nick, firstName: sp.firstName, lastName: sp.lastName)
                sp.roles.getAll().each {
                    Role sr ->
                        RoleDto.QNode usDto = new RoleDto.QNode(id: sr.id, userRole: sr.userRole)
                        node.roles.add(usDto)
                }
                sp.tasks.getAll().each {
                    Task ts ->
                        TaskDto.QNode tusDto = new TaskDto.QNode(id: ts.id)
                        node.tasks.add(tusDto)

                }
                qList.all[sp.id] = node

            }

        }
        qList

    }







    public UserDto.QList getUsersInProject(Long command) {
        UserDto.QList qList = new UserDto.QList()
        userRepository.findAll().each { User sp ->
            if(roleRepository.findByProjectIdAndScrumUserId(command,sp.id).isEmpty()){

            }
            else {
                UserDto.QNode node = new UserDto.QNode(id: sp.id, nick: sp.nick, firstName: sp.firstName, lastName: sp.lastName)
                sp.roles.getAll().each {
                    Role sr ->
                        RoleDto.QNode usDto = new RoleDto.QNode(id: sr.id, userRole: sr.userRole)
                        node.roles.add(usDto)
                }
                sp.tasks.getAll().each {
                    Task ts ->
                        TaskDto.QNode tusDto = new TaskDto.QNode(id: ts.id)
                        node.tasks.add(tusDto)

                }
                qList.all[sp.id] = node

            }

        }
        qList

    }
}
