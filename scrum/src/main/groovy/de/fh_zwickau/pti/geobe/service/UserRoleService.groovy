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

    public RoleDto.QList getRoles() {
        RoleDto.QList qList = new RoleDto.QList()
        roleRepository.findAllByOrderByIdDesc().each { ScrumRole sp ->
            def node = new RoleDto.QNode(userRole: sp.userRole,
                    project: new ProjectDto.QNode(name: (Project)(sp.project.one).getName())
                    ,developer: new UserDto.QNode(id: (User)(sp.scrumUser.one).getId())
            )

            qList.all[sp.id] = node
        }
        qList

    }

    public ProjectDto.QFull getRoleDetails(Long pid) {
        ScrumRole p = roleRepository.findOne(pid)
        makeQFull(p)
    }
    private makeQFull(ScrumRole p) {
        if (p) {
            RoleDto.QFull qFull = new RoleDto.QFull()
            qFull.project = new ProjectDto.QFull(name: (Project)(p.project.one).getName())
            qFull.developer = new UserDto.QFull(id: (User)(p.scrumUser.one).getId())

            qFull
        } else {
            new RoleDto.QFull()
        }
    }


}
