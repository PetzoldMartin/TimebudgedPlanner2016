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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private Set<Task> backlog = new HashSet<>();
    @Transient
    private ToMany<Project, Task> toBacklog = new ToMany<>(
            { this.@backlog } as IToAny.IGet, this,
            { Task o -> o.project } as IGetOther
    )

    public IToAny<Task> getBacklog() {
        return toBacklog;
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
