package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.TaskRepository
import de.fh_zwickau.pti.geobe.repository.UserstoryRepository
import de.fh_zwickau.pti.geobe.service.IAuthorizationService
import de.fh_zwickau.pti.geobe.service.StartupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

/**
 *
 * @author georg beier
 */
//TODO Test rewrite
@SpringApplicationConfiguration(classes = GroovaaApplication)
class EntitySpecification extends Specification {

    @Autowired
    private ProjectRepository projectRepository
    @Autowired
    private TaskRepository taskRepository
    @Autowired
    private UserstoryRepository UserStoryRepository
    @Autowired
    private IAuthorizationService authorizationService


    Project project
    CompoundTask task
    Userstory userstory

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        userstory = new Userstory()
        userstory.name = "a Story with two Boobs"
        userstory.description = "Boobs don't need any Description"
        task = new CompoundTask()
        task.description = "eine neue Aufgabe"
        println("setup called")
    }
    @Autowired
    private StartupService startupService

    public cleanup() {
        startupService.cleanupAll()
    }

    def "association of a userstory to a project"() {
        when:
        project.getUserstorys().add(userstory)
        then:
        project.getUserstorys().all.size() == 1
        userstory.getProject().one == project
    }

    def "save entities and clear "() {
        when:
        projectRepository.save(project)
        then:
        projectRepository.count() == 1
    }

    def "is cleanup really called"() {
        when:
        // nothing happened
        def x = 0
        then:
        projectRepository.count() == 0
    }
}
