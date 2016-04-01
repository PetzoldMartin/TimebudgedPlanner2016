package de.fh_zwickau.pti.geobe.domain;

import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToOne;

import javax.persistence.*;

/**
 * Created by aisma on 01.04.2016.
 */
enum ROLETYPE{ProjectOwner,ScrumMaster,Developer};
@Entity
public class Roles {

    public void setType(ROLETYPE type) {
        this.type = type;
    }

    @Id
    private ROLETYPE type;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @Transient
    private ToOne<Roles,Project> toProject=  new ToOne<>(
            () -> project,(Project p) -> project=p,
            this,Project::getRoles);

    public IToAny<Project> getProject(){return toProject;}


    @ManyToOne
    @JoinColumn(name = "scrumUser_id")
    private ScrumUser scrumUser;
    @Transient
    private ToOne<Roles,ScrumUser> toScrumUser=  new ToOne<>(
            () -> scrumUser,(ScrumUser su) -> scrumUser=su,
            this,ScrumUser::getRoles);

    public IToAny<ScrumUser> getScrumUser(){return toScrumUser;}
}
