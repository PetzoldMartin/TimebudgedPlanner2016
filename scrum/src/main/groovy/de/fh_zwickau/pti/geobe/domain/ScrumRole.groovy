package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther;
import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToOne;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class ScrumRole  {


    @EmbeddedId
    ScrumRoleID scrumRoleID = new ScrumRoleID();

    public void setType(ROLETYPE userRole) {
        this.scrumRoleID.setUserRole(userRole);
    }


    // references
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id",insertable=false, updatable=false)
    private Project project;

    @Transient
    private ToOne<ScrumRole,Project> toProject=  new ToOne<>(
            { this.@project } as IToAny.IGet,
            { Project p -> this.setProject(p) } as IToAny.ISet,
            this, { o -> o.roles } as IGetOther
    )

    public IToAny<Project> getProject(){return toProject;}

    @ManyToOne(optional = false)
    @JoinColumn(name = "scrumUser_id", referencedColumnName = "scrumUser_id",insertable=false, updatable=false)
    private ScrumUser scrumUser;

    @Transient
    private ToOne<ScrumRole,ScrumUser> toScrumUser=  new ToOne<>(
            { this.@scrumUser } as IToAny.IGet,
            { ScrumUser su -> this.setScrumUser(su) } as IToAny.ISet,
            this, { o -> o.roles } as IGetOther
    )

    public IToAny<ScrumUser> getScrumUser(){return toScrumUser;}

    // setter (needed!)
    private void setProject(Project project) {
        this.project = project;
        this.scrumRoleID.project_id=project.getId();
    }

    private void setScrumUser(ScrumUser scrumUser) {
        this.scrumUser = scrumUser;
        this.scrumRoleID.scrumUser_id=scrumUser.getId();
    }

    public String toString() {
        return scrumRoleID.toString();
    }
}
