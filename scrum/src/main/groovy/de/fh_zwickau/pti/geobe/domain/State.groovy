package de.fh_zwickau.pti.geobe.domain

import javax.persistence.Entity
import javax.persistence.Id


@Entity
class State {

    //TODO implementation between Sprint and Task instead of sprint task relation

    @Id
    private long id

    // references
    //private Sprint sprint
    //private Task task

    // domain values
    private TASKSTATE state
}
