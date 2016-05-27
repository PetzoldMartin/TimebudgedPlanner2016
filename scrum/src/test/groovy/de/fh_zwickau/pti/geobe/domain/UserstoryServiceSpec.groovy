package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.UserStoryRepository
import de.fh_zwickau.pti.geobe.service.UserstoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

@SpringApplicationConfiguration(classes = GroovaaApplication)
class UserstoryServiceSpec extends Specification {

    @Autowired
    ProjectRepository projectRepository

    @Autowired
    UserStoryRepository userStoryRepository

    @Autowired
    UserstoryService userstoryService

    private Project project, project2
    private UserStory us1, us2


    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        us1 = new UserStory(name: 'us1', description: 'userstory 1')
        us2 = new UserStory(name: 'us2', description: 'userstory 2')
        project.userStorys.add(us1)
        project.userStorys.add(us2)

        ['bla', 'blub', 'tralala'].each {
            us1.task.add(new Subtask(description: it))
        }
        projectRepository.saveAndFlush(project)

        project2 = new Project(name: 'zweites Projekt')
        us1 = new UserStory(name: 'us1', description: 'userstory 1')
        project2.userStorys.add(us1)
        projectRepository.saveAndFlush(project2)

    }

    //TODO Service testing
//    @Ignore
    public
    def "test get backlock"() {

        when:
        setup()
        def tasks1 = userstoryService.getProjectBacklog(project.id)
        def tasks2 = userstoryService.getProjectBacklog(project2.id)
        then:
        tasks1.size() == 3
        tasks2.size() == 0
        //println tasks
    }

}
