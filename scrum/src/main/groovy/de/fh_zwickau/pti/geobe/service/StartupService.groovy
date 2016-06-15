package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.ROLETYPE
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.dto.SprintDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.repository.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 *
 * @author georg beier
 */
@Service
@Slf4j
class StartupService implements IStartupService {
    private boolean isInitialized = false

    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private TaskRepository taskRepository
    @Autowired
    private SprintRepository sprintRepository
    @Autowired
    private UserstoryRepository userstoryRepository
    @Autowired
    private UserRepository userRepository
    @Autowired
    private RoleRepository roleRepository
    @Autowired
    private SprintService sprintService
    @Autowired
    private RoleService userRoleService
    @Autowired
    private ProjectService projectService
    @Autowired
    private UserService userService
    @Autowired
    private TaskService taskService
    @Autowired
    private UserstoryService userstoryService
    @Autowired
    private RoleService roleService


    @Override
    void initApplicationData() {
        projectService.createOrUpdateProject(new ProjectDto.CSet(name: 'Project_one', budget: 200))
        projectService.createOrUpdateProject(new ProjectDto.CSet(name: 'Project_two', budget: 300))
        def userIds = []
        userIds += userService.createOrUpdateUser(new UserDto.CSet(birthdate: new Date(), firstName: 'Marown_one', lastName: 'Kastanie', nick: 'OneMhKay', password: '123')).id
        userIds += userService.createOrUpdateUser(new UserDto.CSet(birthdate: new Date(), firstName: 'Marown_two', lastName: 'Kastanie', nick: 'TwoMhKay', password: '123')).id
        userService.createOrUpdateUser(new UserDto.CSet(birthdate: new Date(), firstName: 'Marown_three', lastName: 'Kastanie', nick: 'ThreeMhKay', password: '123'))
        projectService.getProjects().all.each {
            userstoryService.createOrUpdateUserstory(new UserstoryDto.CSet(name: 'Story_one', description: 'describesth', priority: 3l, projectId: it.value.id))
            userstoryService.createOrUpdateUserstory(new UserstoryDto.CSet(name: 'Story_two', description: 'describesth', priority: 3l, projectId: it.value.id))
            sprintService.createOrUpdateSprint(new SprintDto.CSet(name: 'SP_one', projectId: it.value.id, start: new Date(), end: new Date()))
            sprintService.createOrUpdateSprint(new SprintDto.CSet(name: 'SP_two', projectId: it.value.id, start: new Date(), end: new Date()))
            userService.getUsers().all.each { it2 ->
                userRoleService.createOrUpdateRole(new RoleDto.CSet(userRole: ROLETYPE.Developer, userId: it2.value.id, projectId: it.value.id))
            }
        }
        List<TaskDto.QFull> tlist = []
        Long i
        userstoryService.getUserstorys().all.each {
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'CompoundTask', tag: 'machen_one', description: 'arbeiten', estimate: 20, spent: 10, userstoryId: it.value.id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'CompoundTask', tag: 'machen_two', description: 'arbeiten', estimate: 20, spent: 10, userstoryId: it.value.id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'CompoundTask', tag: 'machen_three', description: 'arbeiten', estimate: 20, spent: 10, userstoryId: it.value.id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'CompoundTask', tag: 'machen_four', description: 'arbeiten', estimate: 20, spent: 10, supertaskId: tlist.get(tlist.size() - 2).id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'Subtask', tag: 'machen_five', description: 'arbeiten', estimate: 20, spent: 10, supertaskId: tlist.get(tlist.size() - 2).id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'Subtask', tag: 'machen_six', description: 'arbeiten', estimate: 20, spent: 10, supertaskId: tlist.get(tlist.size() - 2).id))
            tlist += taskService.createOrUpdate(new TaskDto.CSet(classname: 'Subtask', tag: 'machen_seven', description: 'arbeiten', estimate: 20, spent: 10, supertaskId: tlist.get(tlist.size() - 3).id))
        }
        tlist.eachWithIndex { TaskDto.QFull entry, int i2 ->
            if (i2.mod(2) == 0) {
                taskService.createOrUpdate(new TaskDto.CSet(id: entry.id, classname: entry.classname, tag: entry.tag, description: entry.description, estimate: entry.estimate, spent: entry.spent, userstoryId: entry.userstory.id, developersIds: userIds, completed: false))
            } else {
                sprintService.getSprints().all.each {
                    def x = new SprintDto.CSet(name: it.value.name)
                    x.taskIds += entry.id
                    sprintService.createOrUpdateSprint(x)

                }
            }
        }


    }


    @Override
    void cleanupAll() {

        if (!roleRepository.findAll().empty) {
            roleRepository.findAll().each {
                roleService.deleteRole(new RoleDto.CDelete(id: it.id))
            }
        }
        if (!projectService.findAll().empty) {
            projectRepository.findAll().each {
                projectService.deleteProject(new ProjectDto.CDelete(id: it.id))
            }
        }

        if (!userstoryRepository.findAll().empty) {
            userstoryRepository.findAll().each {
                userstoryService.deleteUserstory(new UserstoryDto.CDelete(id: it.id))
            }
        }
        if (!sprintRepository.findAll().empty) {
            sprintRepository.findAll().each {
                sprintService.deleteSprint(new SprintDto.CDelete(id: it.id))
            }
        }


        if (!userRepository.findAll().empty) {
            userRepository.findAll().each {
                userService.deleteUser(new UserDto.CDelete(id: it.id))
            }
        }
        if (!taskRepository.findAll().empty) {
            taskRepository.findAll().each {
                if (it.userstory) {
                    if (it.userstory.one) {
                        taskService.deleteTasks(new TaskDto.CDelete(id: it.id))
                    }
                }
            }
        }


        projectRepository.deleteAll()
        sprintRepository.deleteAll()
        taskRepository.findAll().each {
            if(taskRepository.findOne(it.id)) {
                taskRepository.delete(it.id)
            }
        }
        userRepository.deleteAll()
        roleRepository.deleteAll()
        assert projectRepository.findAll().empty
        assert userstoryRepository.findAll().empty
        assert sprintRepository.findAll().empty
        assert taskRepository.findAll().empty
        assert userRepository.findAll().empty
        assert roleRepository.findAll().empty
    }
}
