package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.Userstory
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserstoryRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 * Facade class to access sprint entities
 * @author georg beier
 */
@Slf4j
@Service
@Transactional
class UserstoryService {
    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private TaskRepository taskRepository
    @Autowired
    private UserstoryRepository userstoryRepository
    @Autowired
    private TaskService taskService

    public UserstoryDto.QFull getUserstoryDetails(Long pid) {
        Userstory us = userstoryRepository.findOne(pid)
        makeQFull(us)
    }

    public UserstoryDto.QFull createOrUpdateUserstory(UserstoryDto.CSet command) {
        Userstory us
        if (command.id) {
            us = userstoryRepository.findOne(command.id)
            if (!us) {
                log.error("cannot find sprint for id $command.id")
                return new UserstoryDto.QFull()
            }
        } else {
            us = new Userstory()
            Project p = projectRepository.findOne(command.projectId)
            if (!p) {
                log.error("cannot find project for id $command.projectId")
                return new UserstoryDto.QFull()
            }
            us.project.add(p)
        }
        us.name = command.name
        us.description = command.description
        us.priority=command.priority
        taskRepository.findAll(command.taskIds)
                .sort { it.tag.toLowerCase() }.each { Task t -> us.task.add(t) }
        // findByUserstoryIdAndIdNotIn does not work with an empty list of ids, so add an invalid id 0
        taskRepository.findByUserstoryIdAndIdNotIn(us.id, command.taskIds ?: [0L])
                .sort { it.tag.toLowerCase() }.each { Task t ->
            us.task.remove(t)
        }
        makeQFull(projectRepository.saveAndFlush(us))
    }

    public List<TaskDto.QNode> getUserStoryTasks(Long pid) {
        List<TaskDto.QNode> nodes = []
        Project p = projectRepository.findOne(pid)
        p.userstorys.all.sort { it.id }.each {
            Userstory us ->
                us.task.all.sort { it.tag.toLowerCase() }.each { Task t ->
                    if (!t.completed)
                        nodes << taskService.taskTree(t)
                }
        }
        //p.backlog.all.sort { it.tag.toLowerCase() }.each { Task t ->
        //  if (!t.completed)
        //      nodes << taskService.taskTree(t)
        //}
        nodes
    }

    private makeQFull(Userstory us) {
        if (us) {
            UserstoryDto.QFull qFull =
                    new UserstoryDto.QFull(id: us.id, name: us.name, description: us.description, priority: us.priority)
            Project p = us.project.one
            qFull.project = new ProjectDto.QNode(name: p.name)
            us.task.all.forEach { Task t ->
                qFull.backlog.all[t.id] = t.tag
                qFull.taskCount+=taskService.countTasks(t)
            }
            // findByProjectIdAndIdNotIn does not work with an empty list of ids, so add an invalid id 0
//            taskRepository.findByProjectIdAndIdNotIn(sp.project.one.id, assigned ?: [0L]).each { Task t ->
//                qFull.available.all[t.id] = t.tag
//            }
            qFull
        } else {
            new UserstoryDto.QFull()
        }
    }

    public deleteUserstory(UserstoryDto.CDelete command) {
        Userstory userstory=userstoryRepository.getOne(command.id)
        userstory.task.all.each {
            taskService.deleteTasks(new TaskDto.CDelete(id: it.id))
        }
        userstoryRepository.delete(command.id)
    }

    public UserstoryDto.QList getUserstorys(){
        UserstoryDto.QList qList = new UserstoryDto.QList()
        userstoryRepository.findAll().sort { it.name.toLowerCase() }.each {
                def node = new UserstoryDto.QNode([id: it.id, name: it.name])
                qList.all[it.id] = node
        }
        qList

    }
}
