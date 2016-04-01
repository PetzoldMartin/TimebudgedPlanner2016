package de.fh_zwickau.pti.geobe.domain

import de.fh_zwickau.pti.geobe.GroovaaApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

/**
 * Created by aisma on 01.04.2016.
 */
@SpringApplicationConfiguration(classes = GroovaaApplication)
class TestAssoziationsOfRoles extends Specification{

    //@Autowired
    private Project project
    //@Autowired
    private Roles roles1,roles2
    //@Autowired
    private ScrumUser scrumUser

    public setup() {
        project = new Project()
        project.name = "ein Projekt"
        roles1=new Roles();
        roles2=new Roles();
        scrumUser=new ScrumUser()
    }
    def "test independence of Roles"(){
        when:
        roles1.setType(ROLETYPE.Developer)
        roles2.setType(ROLETYPE.Developer)
        then:
        println(roles1.toString())
        println(roles2.toString())
        roles1.toString()!=roles2.toString()
    }

    def "test Datastructure wire"(){
        when:
        project.getRoles().add(roles1)
        project.getRoles().add(roles2)
        then:
        println(project.getRoles())
        project.getRoles().getOne()==roles1
        project.getRoles().getOne()!=roles2
        project.getRoles().getAll().contains(roles2)
        project.getRoles().getAll().each {println(it) }

        println( roles1.getProject())
        roles1.getProject().getOne()==project;
    }
}
