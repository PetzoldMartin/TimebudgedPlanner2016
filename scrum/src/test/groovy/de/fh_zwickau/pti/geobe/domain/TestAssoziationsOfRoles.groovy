package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.ScrumRoleRepository
import de.fh_zwickau.pti.geobe.repository.ScrumUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

/**
 * Created by aisma on 01.04.2016.
 */
@SpringApplicationConfiguration(classes = GroovaaApplication)
class TestAssoziationsOfRoles extends Specification{

    @Autowired
    ProjectRepository projectRepository
    @Autowired
    ScrumRoleRepository scrumRoleRepository
    @Autowired
    ScrumUserRepository scrumUserRepository

    private Project project,project1
    //@Autowired
    private ScrumRole scrumRole1, scrumRole2
    //@Autowired
    private ScrumUser scrumUser

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project1 = new Project()
        project1.name = "auch ein Projekt"
        scrumUser=new ScrumUser()

        scrumRole1=new ScrumRole();
        scrumRole2=new ScrumRole();
    }
    def "test independence of Roles"(){
        when:
        scrumRole1.setType(ROLETYPE.Developer)
        scrumRole2.setType(ROLETYPE.Developer)
        then:
        println(scrumRole1.toString())
        println(scrumRole2.toString())
        scrumRole1.toString()!=scrumRole2.toString()
    }

    def "test Datastructure wire"(){
        when:
        project.getRoles().add(scrumRole1)
        project.getRoles().add(scrumRole2)
        then:
        println(project.getRoles())
        project.getRoles().getAll().contains(scrumRole1)
        project.getRoles().getAll().contains(scrumRole2)
        project.getRoles().getAll().each {println(it) }

        println( scrumRole1.getProject())
        scrumRole1.getProject().getOne()==project;
    }

    def "save entities and clear two" () {
        when:
        projectRepository.save(project);
        projectRepository.save(project1);
        scrumUserRepository.save(scrumUser);

        scrumRole1.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        scrumRole2.setType(ROLETYPE.Developer)
        project1.getRoles().add(scrumRole2)
        scrumUser.getRoles().add(scrumRole2)
        scrumRoleRepository.save(scrumRole2)

        then:
        scrumRoleRepository.count() == 2
    }

    def "is cleanup really called" () {
        when:
        scrumRoleRepository.deleteAll()
        // nothing happened
        def x = 0
        then:
        scrumRoleRepository.count() == 0
    }

    def "save entities and load and save" () {
        when:
        projectRepository.save(project);
        projectRepository.save(project1);
        scrumUserRepository.save(scrumUser);

        scrumRole1.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        then:
        scrumRoleRepository.count() == 1


    }
    def "is cleanup really called2" () {
        when:
        scrumRoleRepository.deleteAll()
        // nothing happened
        def x = 0
        then:
        scrumRoleRepository.count() == 0
    }
    def "save entities and clear Same" () {
        when:
        projectRepository.save(project);
        projectRepository.save(project1);
        scrumUserRepository.save(scrumUser);

        scrumRole1.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        scrumRole2.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole2)
        scrumUser.getRoles().add(scrumRole2)
        scrumRoleRepository.save(scrumRole2)

        then:
        scrumRoleRepository.count() == 1
    }


}
