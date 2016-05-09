package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther;
import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToMany;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author georg beier
 */
@Entity
public class Project implements Serializable {
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    // domain values
    private String name;
    private String description;
    private BigDecimal budget;
    private Date startDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<UserStory> storys = new HashSet<>();
    @Transient
    private ToMany<Project, UserStory> toStory = new ToMany<>(
            { this.@storys } as IToAny.IGet, this,
            { UserStory o -> o.project } as IGetOther
    )

    public IToAny<UserStory> getUserStorys() {
        return toStory;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private Set<Sprint> sprints = new HashSet<>();
    @Transient
    private ToMany<Project, Sprint> toSprint = new ToMany<>(
            { this.@sprints } as IToAny.IGet, this,
            { Sprint o -> o.project } as IGetOther
    )

    public IToAny<Sprint> getSprint() {
        return toSprint;
    }



    @OneToMany(mappedBy = "project",cascade = CascadeType.PERSIST)
    private Set<ScrumRole> roles =new HashSet<>();
    @Transient
    private ToMany<Project,ScrumRole> toRoles = new ToMany<>(
            { this.@roles } as IToAny.IGet, this,
            { ScrumRole o -> o.project } as IGetOther
    )

    public IToAny<ScrumRole> getRoles() {return toRoles;}
}
