package de.fh_zwickau.pti.geobe.dto

import de.fh_zwickau.pti.geobe.domain.ROLETYPE

/**
 * Created by aisma on 24.05.2016.
 */
class RoleDto {
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
        ROLETYPE userRole
        ProjectDto.QNode project = new ProjectDto.QNode()
        UserDto.QNode developer = new UserDto.QNode()
    }

    public static class CSet {
        Long id
        ROLETYPE userRole
        Long ProjectId
        Long DeveloperId

    }
    public static class QNode {
        Long id
        ROLETYPE userRole
        ProjectDto.QNode project
        UserDto.QNode developer

    }

}
