package de.fh_zwickau.pti.geobe.service

import de.fh_zwickau.pti.geobe.domain.*
import de.fh_zwickau.pti.geobe.repository.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.time.LocalDateTime

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
    private UserStoryRepository userStoryRepository
    @Autowired
    private ScrumUserRepository scrumUserRepository
    @Autowired
    private ScrumRoleRepository scrumRoleRepository

    @Override
    void initApplicationData() {

        // users
        if (!scrumUserRepository.findAll()) {
//            Project p = new Project([name: 'Projekt User', budget: 1000])
//            ScrumUser user = new ScrumUser([nick: 'user', password: 'user'])
//            ScrumRole userRoletype = new ScrumRole([userRole: ROLETYPE.Developer])
//            userRoletype.project.add(p)
//            userRoletype.scrumUser.add(user)
//            ScrumUser master = new ScrumUser([nick: 'master', password: 'master'])
//            ScrumRole masterRoletype = new ScrumRole([userRole: ROLETYPE.ScrumMaster])
//            ScrumUser owner = new ScrumUser([nick: 'owner', password: 'owner'])
//            ScrumRole ownerRoletype = new ScrumRole([userRole: ROLETYPE.ProjectOwner])
            //projectRepository.save(p)
            //scrumUserRepository.save([user, master, owner])

            //scrumUserRepository.flush()
            //scrumRoleRepository.saveAndFlush(userRoletype)

        }

        // project structure
        if (!projectRepository.findAll() && !taskRepository.findAll() && !sprintRepository.findAll()) { //TODO write new
            def tasksforUserStory = []
            int cpl = 0
            log.info("initializing data at ${LocalDateTime.now()}")
            Project p = new Project([name: 'Projekt Küche', budget: 1000])
            Sprint s = new Sprint([name: 'erster Sprint'])
            UserStory us = new UserStory([name: 'Sauber machen', description: 'alles muss sauber sein'])
            UserStory us2 = new UserStory([name: 'mache nix', description: 'nichts!'])

            p.sprint.add(s)

            p.userStorys.add(us)
            p.userStorys.add(us2)

            Task t = new Subtask(tag: 'Tee kochen', description: 'Kanne zum Wasser!', estimate: 42)
            s.backlog.add(t)

            tasksforUserStory << t

            CompoundTask hausarbeit = new CompoundTask(tag: 'Hausarbeit', description: 'Immer viel zu tun', estimate: 4711)
            hausarbeit.sprint.add(s)
            s.backlog.add(hausarbeit)

            tasksforUserStory << hausarbeit

            ['backen', 'kochen', 'abwaschen'].forEach {
                t = new CompoundTask([description: "Wir sollen $it", tag: it])
                t.supertask.add(hausarbeit)
//                t.project.add(p)
                cpl++
                tasksforUserStory << t
                ['dies', 'das', 'etwas anderes', 'nichts davon'].each { tag ->
                    def sub = new Subtask([description: "und dann noch $tag",
                                           tag        : tag, estimate: 250,
                                           completed  : (cpl % 2 == 0)])
                    t.subtask.add(sub)

                    tasksforUserStory << sub

                }
            }
            ['früh', 'mittag', 'abend'].each {
                new Sprint([name: it]).project.add(p)
            }
            // add to one userStory
            tasksforUserStory.forEach({ us.task.add(it) })

            // finally persist project
            projectRepository.saveAndFlush(p)



            // new project
            p = new Project([name: 'Projekt Garten', budget: 2000])
            us = new UserStory([name: 'Garten pflegen', description: 'alles muss schöm sein'])
            p.userStorys.add(us)


            def tl = []
            ['umgraben', 'Rasen mähen', 'Äpfel pflücken', 'ernten'].forEach {
                t = new CompoundTask([description: "Wir sollen $it", tag: it])
                //t.project.add(p)
                tl << t
            }

            int i = 0
            ['Frühling', 'Sommer', 'Herbst', 'Winter'].each {
                s = new Sprint([name: it])
                s.project.add(p)
                s.backlog.add(tl[(++i) % tl.size()])
                s.backlog.add(tl[(++i) % tl.size()])
            }
            // add to userStory
            tl.forEach({ us.task.add(it) })

            //persist
            projectRepository.saveAndFlush(p)

            def tasks = taskRepository.findAll()
            tasks.forEach({ log.info("task (${it.id}): $it.description") })
            userStoryRepository.findAll().forEach({ log.info("userStory (${it.id}): $it.description") })
        }
    }

    @Override
    @Transactional
    void cleanupAll() {
        def projects = projectRepository.findAll()
        def tasks = taskRepository.findAll()
        tasks.each { Task t ->
            t.supertask.removeAll()
            t.userStory.removeAll()
            t.sprint.removeAll()
        }
        taskRepository.save(tasks)
        projects.each { Project p ->
            p.sprint.removeAll()
        }
        projectRepository.save(projects)
        projectRepository.deleteAll()
        taskRepository.deleteAll()
        sprintRepository.deleteAll()

        // check cleanup
//        assert projectRepository.findAll().isEmpty()
//        assert userStoryRepository.findAll().isEmpty()
//        assert sprintRepository.findAll().isEmpty()
//        assert taskRepository.findAll().isEmpty()
    }
}
