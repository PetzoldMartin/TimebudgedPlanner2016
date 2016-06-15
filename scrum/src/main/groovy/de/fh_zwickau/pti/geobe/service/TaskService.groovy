package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.*
import de.fh_zwickau.pti.geobe.dto.SprintDto
import de.fh_zwickau.pti.geobe.dto.TaskDto
import de.fh_zwickau.pti.geobe.dto.TaskDto.CSet
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.repository.SprintRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserRepository
import de.fh_zwickau.pti.geobe.repository.UserstoryRepository
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

/**
 *
 * @author georg beier
 */
@Slf4j
@Service
@Transactional
class TaskService {
    @Autowired
    private UserstoryRepository userstoryRepository
    @Autowired
    private TaskRepository taskRepository
    @Autowired
    private SprintRepository sprintRepository
    @Autowired
    private UserRepository userRepository

    TaskDto.QList getTasks() {
        TaskDto.QList qList = new TaskDto.QList()
        taskRepository.findAllByOrderByTagAsc().each { qList.all[it.id] = it.tag }
        qList
    }

    TaskDto.QFull getTaskDetails(Long id) {
        Task t = taskRepository.findOne(id)
        makeQFull(t)
    }

    TaskDto.QFull createSubtask(CSet cmd) {
        Task t = taskRepository.findOne(cmd.supertaskId)
        if (t instanceof Subtask) {
            CompoundTask ct = new CompoundTask(t)
            taskRepository.saveAndFlush(ct)
            Task st = ct.supertask.one
            if (st) {
                taskRepository.saveAndFlush(st)
            }
            t.supertask.removeAll()
            t.sprint.removeAll()
            t.userstory.removeAll()
            taskRepository.delete(t)
            cmd.supertaskId = ct.id
        }
        createOrUpdate(cmd)
    }

    TaskDto.QFull createOrUpdate(CSet cmd) {
        Task task
        if (cmd.id) {
            task = taskRepository.findOne(cmd.id)
            if (!task) {
                log.error("no task found for id $cmd.id")
                return new TaskDto.QFull()
            }
            task.tag = cmd.tag
            task.estimate = cmd.estimate
            task.description = cmd.description
            if (task instanceof Subtask) {
                task.spent = cmd.spent
                task.completed = cmd.completed
            }

        } else if (cmd.userstoryId || cmd.supertaskId) {
            if (cmd.classname.endsWith('Subtask')) {
                task = new Subtask()
                task.tag = cmd.tag ?: 'tagless'
                task.description = cmd.description ?: ''
                task.estimate = cmd.estimate ?: 0
                task.spent = cmd.spent ?: 0
                task.completed = cmd.completed ?: false
            } else {
                task = new CompoundTask()
                task.tag = cmd.tag ?: 'tagless'
                task.description = cmd.description ?: ''
                task.estimate = cmd.estimate ?: 0
            }
            if (cmd.userstoryId) {
                Userstory userstory = userstoryRepository.findOne(cmd.userstoryId)
                if (userstory) {
                    task.userstory.add(userstory)
                } else {
                    log.error("no userstory found for $cmd.userstoryId")
                    return new TaskDto.QFull()
                }
            }
            if (cmd.sprintIds) {
                Sprint sprint = sprintRepository.findOne(cmd.sprintIds)
                if (p) {
                    task.sprint.add(sprint)
                } else {
                    log.error("no sprint found for $cmd.sprintIds")
                    return new TaskDto.QFull()
                }
            }


        } else {
            log.error("no project or supertask defined for new task")
            return new TaskDto.QFull()
        }

        task.developers.removeAll() //fix: first remove all old devs before add new
        if (cmd.developersIds) {
            cmd.developersIds.each {
                User d = userRepository.findOne(it)
                if (d) {
                    task.developers.add(d)
                } else {
                    log.error("no user found for $cmd.developersIds")
                    return new TaskDto.QFull()
                }
            }
        }

        if (cmd.subtaskIds)
            taskRepository.findAllByOrderByTagAsc(cmd.subtaskIds).forEach { task.subtask.add(it) }
        if (cmd.supertaskId)
            task.supertask.add(taskRepository.findOne(cmd.supertaskId))
        if (cmd.sprintIds)
            sprintRepository.findAll(cmd.sprintIds).forEach { task.sprint.add(it) }
        if (cmd.developersIds) {
            userRepository.findAll(cmd.developersIds).forEach {
                task.developers.add(it)
            }
        }
        //TODO redo recursive assignment of devs

        if (!cmd.subtaskIds.empty) {
            cmd.subtaskIds.each {
                Task sT = taskRepository.getOne(it)
                CSet TCS = new CSet(id: sT.id, subtaskIds: getSubtaskIDs(sT.id), developersIds: cmd.developersIds)
                createOrUpdate(TCS);
            }
        }
        makeQFull(taskRepository.saveAndFlush(task))
    }

    public TaskDto.QNode taskTree(Task t, boolean notCompleted = false) {
        new TaskDto.QNode([id: t.id, tag: t.tag, children: taskSubtree(t, notCompleted)])
    }

    private makeQFull(Task t) {
        if (t) {
            TaskDto.QFull qFull = new TaskDto.QFull([
                    id       : t.id, tag: t.tag, description: t.description,
                    estimate : t.estimate, spent: t.spent,
                    completed: t.completed])
            if (t.supertask.one) {
                qFull.rootTaskId = getRootTask(t).id
            }
            qFull.classname = t.class.canonicalName.replaceAll(/.*\./, '')
            qFull.userstory = new UserstoryDto.QFull()
            qFull.supertask = new TaskDto.QList()
            qFull.sprints = new SprintDto.QList()
            qFull.developers = new UserDto.QList()

            if (t.developers.all) {
                t.developers.all.each { User u ->
                    qFull.developers.all[u.id] = new UserDto.QFull(id: u.id, nick: u.nick)
                }
            }
            if (t.userstory.one) {
                qFull.userstory.id = t.userstory.one.id
                qFull.userstory.name = t.userstory.one.name
                qFull.userstory.project.name = t.userstory.one.project.one.name
            }

            //t.userstory.all.each { qFull.userstorys.all[it.id] = it.name }
            t.supertask.all.each { qFull.supertask.all[it.id] = it.tag }
            t.sprint.all.sort { it.start }.each { qFull.sprints.all[it.id] = it.name }
            qFull.subtasks = taskSubtree(t)
            qFull.taskCount = countTasks(t) - 1
            return qFull
        } else {
            new TaskDto.QFull()
        }
    }

    public int countTasks(Task task) {
        if (task) {
            if (task instanceof CompoundTask) {
                int taskCount = 0
                task.subtask.all.each { Task it ->
                    taskCount += countTasks(it)
                }
                return taskCount + 1
            } else return 1
        } else return 0
    }

    public List<Long> getSubtaskIDs(Long Id) {
        Task sT = taskRepository.getOne(Id)
        def subtaskIds = []

        TaskDto.QFull q = makeQFull(sT);

        //Why CompundTask is no CompoundTask when it comes from repository
        if (sT.class == CompoundTask | sT instanceof CompoundTask) { //FIXME instance of not functionally
            sT.subtask.all.each {
                subtaskIds.add(it.id)
            }
        }
        return subtaskIds
    }

    private List<TaskDto.QNode> taskSubtree(Task t, boolean notCompleted = false) {
        List<TaskDto.QNode> subtree = new LinkedList<>()
        if (t instanceof CompoundTask) {
            t.subtask.all.sort { it.tag.toLowerCase() }.each { Task subtask ->
                if (!(subtask.completed && notCompleted)) {
                    TaskDto.QNode node = new TaskDto.QNode([id: subtask.id, tag: subtask.tag])
                    node.children = taskSubtree(subtask)
                    subtree.add(node)
                }
            }
        }
        subtree
    }


    @Transactional
    private Task getRootTask(Task task) {

        if (task.supertask.one) {
            getRootTask(task.supertask.one)
        } else {
            return task
        }
    }

    public deleteTasks(TaskDto.CDelete command) {
        Task delete = taskRepository.findOne(command.id)
        clearSubTask(delete)
        taskRepository.delete(command.id)
    }

    private void clearSubTask(Task task) {
        if (task) {
            if (task instanceof CompoundTask) {
                task.subtask.all.each { clearSubTask(it) }
            }
            task.developers.removeAll()
        }
    }
}