package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.Task
import de.fh_zwickau.pti.geobe.domain.UserStory
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.UserStoryDto
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserStoryRepository
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
    private UserStoryRepository userstoryRepository
    @Autowired
    private TaskService taskService

    public UserStoryDto.QFull getUserstoryDetails(Long pid) {
        UserStory us = userstoryRepository.findOne(pid)
        makeQFull(us)
    }

    public UserStoryDto.QFull createOrUpdateUserstory(UserStoryDto.CSet command) {
        UserStory us
        if (command.id) {
            us = userstoryRepository.findOne(command.id)
            if (!us) {
                log.error("cannot find sprint for id $command.id")
                return new UserStoryDto.QFull()
            }
        } else {
            us = new UserStory()
            Project p = projectRepository.findOne(command.projectId)
            if (!p) {
                log.error("cannot find project for id $command.projectId")
                return new UserStoryDto.QFull()
            }
            us.project.add(p)
        }
        us.name = command.name
        us.description = command.description
        us.priority=command.priority
        taskRepository.findAll(command.taskIds)
                .sort { it.tag.toLowerCase() }.each { Task t -> us.task.add(t) }
        // findByUserstoryIdAndIdNotIn does not work with an empty list of ids, so add an invalid id 0
        taskRepository.findByUserStoryIdAndIdNotIn(us.id, command.taskIds ?: [0L]) //TODO Beierei
                .sort { it.tag.toLowerCase() }.each { Task t ->
            us.task.remove(t)
        }
        makeQFull(projectRepository.saveAndFlush(us))
    }

    public List<TaskDto.QNode> getProjectBacklog(Long pid) {
        List<TaskDto.QNode> nodes = []
        Project p = projectRepository.findOne(pid)
        p.userStorys.all.sort { it.id }.each {
            UserStory us ->
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

    private makeQFull(UserStory us) {
        if (us) {
            UserStoryDto.QFull qFull =
                    new UserStoryDto.QFull(id: us.id, name: us.name, description: us.description)
            Project p = us.project.one
            qFull.project = new ProjectDto.QNode(name: p.name)
            def assigned = []
            us.task.all.forEach { Task t ->
                qFull.backlog.all[t.id] = t.tag
                assigned.add(t.id)
            }
            // findByProjectIdAndIdNotIn does not work with an empty list of ids, so add an invalid id 0
//            taskRepository.findByProjectIdAndIdNotIn(sp.project.one.id, assigned ?: [0L]).each { Task t ->
//                qFull.available.all[t.id] = t.tag
//            }
            qFull
        } else {
            new UserStoryDto().QFull()
        }
    }
}
