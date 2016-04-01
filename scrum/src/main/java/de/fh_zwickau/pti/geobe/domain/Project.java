package de.fh_zwickau.pti.geobe.domain;

import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToMany;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author georg beier
 */
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    private String name;
    private BigDecimal budget;

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

    private Date startDate;
    private String description;

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
    private ToMany<Project, Task> toBacklog =
            new ToMany<>(() -> backlog, this, Task::getProject);

    public IToAny<Task> getBacklog() {
        return toBacklog;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST)
    private Set<Sprint> sprints = new HashSet<>();
    @Transient
    private ToMany<Project, Sprint> toSprint =
            new ToMany<>(() -> sprints, this, Sprint::getProject);

    public IToAny<Sprint> getSprint() {
        return toSprint;
    }

    @OneToMany(mappedBy = "project",cascade = CascadeType.PERSIST)
    private Set<Roles> roles =new HashSet<>();
    @Transient
    private ToMany<Project,Roles> toRoles=
            new ToMany<>(()->roles,this,Roles::getProject);

    public IToAny<Roles> getRoles() {return toRoles;}


}
