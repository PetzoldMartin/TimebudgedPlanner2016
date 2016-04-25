package de.fh_zwickau.pti.geobe.domain;

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


    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id",insertable=false, updatable=false)
    private Project project;



    @Transient
    private ToOne<ScrumRole,Project> toProject=  new ToOne<>(
            () -> project,(Project p) -> setProject(p),
            this,Project::getRoles);

    public IToAny<Project> getProject(){return toProject;}


    @ManyToOne(optional = false)
    @JoinColumn(name = "scrumUser_id", referencedColumnName = "scrumUser_id",insertable=false, updatable=false)
    private ScrumUser scrumUser;

    @Transient
    private ToOne<ScrumRole,ScrumUser> toScrumUser=  new ToOne<>(
            () -> scrumUser,(ScrumUser su) -> setScrumUser(su),
            this,ScrumUser::getRoles);

    public IToAny<ScrumUser> getScrumUser(){return toScrumUser;}

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
