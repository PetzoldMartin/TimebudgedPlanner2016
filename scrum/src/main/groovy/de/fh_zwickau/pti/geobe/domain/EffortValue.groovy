package de.fh_zwickau.pti.geobe.domain

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created by Heliosana on 04.05.2016.
 */

@Entity
class EffortValue {

    //TODO implementation between ScrumUser and Task

    // references
    //private ScrumUser user
    //private Task task

    @Id
    private long id

    //domain values
    private BigDecimal value
}
