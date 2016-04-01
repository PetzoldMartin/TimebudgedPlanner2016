package de.fh_zwickau.pti.geobe.domain;

import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToMany;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aisma on 01.04.2016.
 */
@Entity
public class ScrumUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    private String name;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    private Date date;
    private String nick;
    //TODO add DATAVALUES

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy = "scrumUser",cascade = CascadeType.PERSIST)
    private Set<ScrumRole> roles =new HashSet<>();
    @Transient
    private ToMany<ScrumUser,ScrumRole> toRoles=
            new ToMany<>(()->roles,this, ScrumRole::getScrumUser);

    public IToAny<ScrumRole> getRoles() {return toRoles;}


}
