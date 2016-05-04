package de.fh_zwickau.pti.geobe.domain

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created by Heliosana on 04.05.2016.
 */

@Entity
class UserStory {

    //TODO implementation between Project and Task

    @Id
    private long id

    // references
    //private Project project
    //private List<Tasks> tasks

    // domain values
    String name
    String description

}
