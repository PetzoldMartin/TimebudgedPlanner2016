package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToOne

import javax.persistence.*

enum ROLETYPE {
    ProjectOwner,
    ScrumMaster,
    Developer
}

@Entity
public class ScrumRole {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    //domain values
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Roletype", columnDefinition = "int default 1")
    ROLETYPE userRole;


    // references
    @ManyToOne(optional = true,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project_id")
    //, referencedColumnName = "project_id",insertable=false, updatable=false)
    private Project project;

    @Transient
    private ToOne<ScrumRole, Project> toProject = new ToOne<>(
            { this.@project } as IToAny.IGet,
            { Project p -> this.setProject(p) } as IToAny.ISet,
            this, { o -> o.roles } as IGetOther
    )

    public IToAny<Project> getProject() { return toProject; }

    @ManyToOne(optional = true,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "scrumUser_id")
    //, referencedColumnName = "scrumUser_id",insertable=false, updatable=false)
    private User scrumUser;

    @Transient
    private ToOne<ScrumRole, User> toScrumUser = new ToOne<>(
            { this.@scrumUser } as IToAny.IGet,
            { User su -> this.setScrumUser(su) } as IToAny.ISet,
            this, { o -> o.roles } as IGetOther
    )

    public IToAny<User> getScrumUser() { return toScrumUser; }

    // setter (needed!)
    private void setProject(Project project) {
        this.project = project;
        //this.scrumRoleID.project_id=project.getId();
    }

    private void setScrumUser(User scrumUser) {
        this.scrumUser = scrumUser;
        //this.scrumRoleID.scrumUser_id=scrumUser.getId();
    }

    public String toString() {
        return "Role: $userRole \tProject: $project \tUser: $scrumUser"
    }
}
