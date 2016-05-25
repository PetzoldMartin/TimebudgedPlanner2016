package de.fh_zwickau.pti.geobe.dto

import de.fh_zwickau.pti.geobe.domain.Project
import de.fh_zwickau.pti.geobe.domain.ROLETYPE

/**
 * Created by aisma on 24.05.2016.
 */
class ScrumRoleDto {
    public static class QList {
        LinkedHashMap<Long, QNode> all = [:]

        Long getFirstId() {
            if (all) {
                all.keySet().iterator().next()
            } else {
                0
            }
        }
    }

    public static class QFull {
        Long id
        ROLETYPE userRole=new ROLETYPE()
        ProjectDto.QNode project = new ProjectDto.QNode()
        ScrumUserDto.QNode developer=new ScrumUserDto.QNode()
    }

    public static class CSet {
        Long id
        ROLETYPE userRole=new ROLETYPE()
        Long ProjectId
        Long DeveloperId

    }
    public static class QNode {
        Long id
        ROLETYPE userRole
        ProjectDto.QNode project
        ScrumUserDto.QNode developer

    }

}
