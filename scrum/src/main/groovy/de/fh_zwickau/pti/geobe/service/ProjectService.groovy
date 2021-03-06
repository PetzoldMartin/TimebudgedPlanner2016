package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.Sprint
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.Userstory
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.ProjectDto.CDelete
import de.fh_zwickau.pti.geobe.dto.ProjectDto.CSet
import de.fh_zwickau.pti.geobe.dto.SprintDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.SprintRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserstoryRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Facade class to access project entities
 * @author georg beier
 */
@Service
@Transactional
@Slf4j
class ProjectService {

    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private TaskRepository taskRepository
    @Autowired
    private SprintRepository sprintRepository
    @Autowired
    private TaskService taskService
    @Autowired
    private RoleService userRoleService

    public ProjectDto.QList getProjects() {
        ProjectDto.QList qList = new ProjectDto.QList()
        projectRepository.findAll().sort { it.name.toLowerCase() }.each { Project p ->
            def node = new ProjectDto.QNode([id: p.id, name: p.name])
            p.userstorys.all.sort { it.priority }.each { Userstory us ->
                UserstoryDto.QNode usDto = new UserstoryDto.QNode([id: us.id, name: us.name])
                node.userstory.add(usDto)
                us.task.all.sort { it.tag.toLowerCase() }.each { Task t ->
                    usDto.backlog.add(taskService.taskTree(t))
                }
            }
            //p.backlog.all.sort {it.tag.toLowerCase()}.each { Task t ->
            //    node.backlog.add(taskService.taskTree(t))
            //}
            p.sprint.all.sort { it.start }.each { Sprint sp ->
                node.sprint.add(new SprintDto.QNode([id: sp.id, name: sp.name]))
            }
            qList.all[p.id] = node
        }
        qList
    }

    public String getProjectCaption(Long pid) {
        Project project = projectRepository.findOne(pid)
        if (project) {
            project.name
        } else {
            '--?--'
            log.error("no project found for id $pid")
        }
    }

    public ProjectDto.QFull getProjectDetails(Long pid) {
        Project p = projectRepository.findOne(pid)
        makeQFull(p)
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProjectDto.QFull createOrUpdateProject(CSet command) {
        Project project
        if (command.id) {
            project = projectRepository.findOne(command.id)
            if (!project) return new ProjectDto.QFull()
//            project.roles.each {
//                userRoleService.createOrUpdateRole(new RoleDto.CSet(userId: it.one.scrumUser.one.id,projectId: project.id,userRole: it.one.userRole))
//            }
        } else {
            project = new Project()
        }
        project.name = command.name
        project.budget = command.budget
        if (command.userstoryIds)
            UserstoryRepository.findAll(command.userstoryIds).forEach { Userstory us -> project.userstorys.add(us) }
        if (command.sprintIds)
            sprintRepository.findAll(command.sprintIds).forEach { Sprint s -> project.sprint.add(s) }
        makeQFull(projectRepository.saveAndFlush(project))
    }

    public deleteProject(CDelete command) {
        Project delete = projectRepository.findOne(command.id)
        if (delete) {
            delete.sprint.all.each { it.backlog.removeAll() }
            //delete.roles.removeAll()
            delete.userstorys.all.each {
                it.task.all.each {
                    taskService.deleteTasks(new TaskDto.CDelete(id: it.id))
                }
            }
        }
        projectRepository.delete(command.id)
//        Project project
//        if (command.id) {
//            project = projectRepository.findOne(command.id)
//            if (!project) return new ProjectDto.QFull()
//            projectRepository.delete(project)
//        }
    }

    private makeQFull(Project p) {
        if (p) {
            ProjectDto.QFull qFull = new ProjectDto.QFull(id: p.id, name: p.name, budget: p.budget)
            qFull.userstorys = new UserstoryDto.QList()
            qFull.sprints = new SprintDto.QList()
            p.userstorys.all.sort { it.priority }.forEach { Userstory userstory ->
                qFull.userstorys.all[userstory.id] = userstory.name
            }
            p.sprint.all.sort { it.start }.forEach { Sprint s -> qFull.sprints.all[s.id] = s.name }
            qFull
        } else {
            new ProjectDto.QFull()
        }
    }
}
