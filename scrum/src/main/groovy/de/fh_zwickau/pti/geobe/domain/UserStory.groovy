package de.fh_zwickau.pti.geobe.domain

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created by Heliosana on 04.05.2016.
 */

@Entity
class UserStory {

    @Id
    private long id

    String name
    String description

}
