package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.ScrumRoleRepository
import de.fh_zwickau.pti.geobe.repository.ScrumUserRepository
import de.fh_zwickau.pti.geobe.repository.UserStoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Ignore
import spock.lang.Specification


@SpringApplicationConfiguration(classes = GroovaaApplication)
class UserstoryServiceSpec extends Specification {

    @Autowired
    ProjectRepository projectRepository

    @Autowired
    UserStoryRepository userStoryRepository

    private Project project, project2


    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project2 = new Project()
        project2.name = "auch ein Projekt"

    }

    //TODO Service testing

}
