package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import de.fh_zwickau.pti.geobe.repository.ProjectRepository
import de.fh_zwickau.pti.geobe.repository.ScrumRoleRepository
import de.fh_zwickau.pti.geobe.repository.ScrumUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by aisma on 01.04.2016.
 */
//TODO Test rewrite

@SpringApplicationConfiguration(classes = GroovaaApplication)
class TestAssoziationsOfRoles extends Specification {

    @Autowired
    ProjectRepository projectRepository
    @Autowired
    ScrumRoleRepository scrumRoleRepository
    @Autowired
    ScrumUserRepository scrumUserRepository

    private Project project, project2
    //@Autowired
    private ScrumRole scrumRole1, scrumRole2
    //@Autowired
    private ScrumUser scrumUser1

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        project2 = new Project()
        project2.name = "auch ein Projekt"
        scrumUser1 = new ScrumUser()

        scrumRole1 = new ScrumRole();
        scrumRole2 = new ScrumRole();
    }

    @Ignore
    public
    def "test independence of Roles"() {
        when:
        scrumRole1.setType(ROLETYPE.Developer)
        scrumRole2.setType(ROLETYPE.Developer)
        then:
        println(scrumRole1.toString())
        println(scrumRole2.toString())
        !scrumRole1.equals(scrumRole2)
    }

    def "test Datastructure wire"() {
        when:
        project.getRoles().add(scrumRole1)
        project.getRoles().add(scrumRole2)
        then:
        println(project.getRoles())
        project.getRoles().getAll().contains(scrumRole1)
        project.getRoles().getAll().contains(scrumRole2)
        project.getRoles().getAll().each { println(it) }

        println(scrumRole1.getProject())
        scrumRole1.getProject().getOne() == project;
    }

    @Ignore
    def "save entities and clear two"() {
        when:
        projectRepository.save(project);
        projectRepository.save(project2);
        scrumUserRepository.save(scrumUser1);

        scrumRole1.setProperty(roletype: ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser1.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        scrumRole2.setType(ROLETYPE.Developer)
        project2.getRoles().add(scrumRole2)
        scrumUser1.getRoles().add(scrumRole2)
        scrumRoleRepository.save(scrumRole2)

        then:
        scrumRoleRepository.count() == 2
    }

    def "is cleanup really called"() {
        when:
        scrumRoleRepository.deleteAll()
        // nothing happened
        def x = 0
        then:
        scrumRoleRepository.count() == 0
    }

    @Ignore
    def "save entities and load and save"() {
        when:
        projectRepository.save(project);
        projectRepository.save(project2);
        scrumUserRepository.save(scrumUser1);

        scrumRole1.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser1.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        then:
        scrumRoleRepository.count() == 1


    }

    def "is cleanup really called2"() {
        when:
        scrumRoleRepository.deleteAll()
        // nothing happened
        def x = 0
        then:
        scrumRoleRepository.count() == 0
    }

    @Ignore
    def "save two scrum roles with same role type"() {
        when:
        projectRepository.save(project);
        projectRepository.save(project2);
        scrumUserRepository.save(scrumUser1);

        scrumRole1.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole1)
        scrumUser1.getRoles().add(scrumRole1)
        scrumRoleRepository.save(scrumRole1)

        scrumRole2.setType(ROLETYPE.Developer)
        project.getRoles().add(scrumRole2)
        scrumUser1.getRoles().add(scrumRole2)
        scrumRoleRepository.save(scrumRole2)
        scrumRoleRepository.flush()
        then:
        scrumRoleRepository.count() == 1
    }

    @Ignore
    def "save two scrum roles with different role types"() {
        when:
        scrumRole2 = scrumRoleRepository.findOne(scrumRole1.scrumRoleID)
        scrumRole2 = scrumRoleRepository.findAll().first()
        println(scrumRole2) // data in DB first role
        scrumRole1.setType(ROLETYPE.ScrumMaster)

        project = scrumRole2.getProject().getOne()
        scrumUser1 = scrumRole2.getScrumUser().getOne()

        ScrumRole role = new ScrumRole()

        //role.scrumRoleID = new ScrumRoleID(project.getId(), scrumUser1.getId(), ROLETYPE.ScrumMaster)

        scrumRoleRepository.save(role) // second role in DB

        then:
        scrumRoleRepository.count() == 2
    }


    def "test deleting DB"() {
        when:
        scrumRoleRepository.deleteAll()
        then:
        scrumRoleRepository.count() == 0
    }

}
