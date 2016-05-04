package de.fh_zwickau.pti.geobe.domain

import de.geobe.util.association.IGetOther;
import de.geobe.util.association.IToAny;
import de.geobe.util.association.ToMany;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by aisma on 01.04.2016.
 */
@Entity
public class ScrumUser implements Serializable{
    @Id
    @Column(name="scrumUser_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }
    // references
    @OneToMany(mappedBy = "scrumUser",cascade = CascadeType.PERSIST)
    private Set<ScrumRole> roles =new HashSet<>();
    @Transient
    private ToMany<ScrumUser,ScrumRole> toRoles = new ToMany<>(

            { this.@roles } as IToAny.IGet, this,
            { ScrumRole o -> o.scrumUser } as IGetOther

    )
    public IToAny<ScrumRole> getRoles() {return toRoles;}

    // domain values
    private String firstName;
    private String lastName;
    private String nick;
    private Date birthdate;


}
