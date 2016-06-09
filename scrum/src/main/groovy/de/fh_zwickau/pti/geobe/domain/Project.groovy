package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther
import de.geobe.util.association.IToAny
import de.geobe.util.association.ToMany

import javax.persistence.*

/**
 * @author georg beier
 */
@Entity
public class Project {
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    // domain values
     String name;
     String description;
     BigDecimal budget;
     Date startDate;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Userstory> storys = new HashSet<>();
    @Transient
    private ToMany<Project, Userstory> toStory = new ToMany<>(
            { this.@storys } as IToAny.IGet, this,
            { Userstory o -> o.project } as IGetOther
    )

    public IToAny<Userstory> getUserstorys() {
        return toStory;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Sprint> sprints = new HashSet<>();
    @Transient
    private ToMany<Project, Sprint> toSprint = new ToMany<>(
            { this.@sprints } as IToAny.IGet, this,
            { Sprint o -> o.project } as IGetOther
    )

    public IToAny<Sprint> getSprint() {
        return toSprint;
    }


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();
    @Transient
    private ToMany<Project, Role> toRoles = new ToMany<>(
            { this.@roles } as IToAny.IGet, this,
            { Role o -> o.project } as IGetOther
    )

    public IToAny<Role> getRoles() { return toRoles; }
}
