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
        Long id=0
        ROLETYPE userRole
        ProjectDto.QFull project = new ProjectDto.QFull()
        UserDto.QFull user = new UserDto.QFull()
    }

    public static class CSet {
        Long id=0
        ROLETYPE userRole
        Long projectId
        Long userId

    }
    public static class QNode {
        Long id=0
        ROLETYPE userRole
        ProjectDto.QNode project
        UserDto.QNode user
        @Override
        String toString() {
           this.@user.nick+' ('+this.@userRole+')'+this.user.id
        }
    }
    public static class CDelete {
        Long id
    }

}
