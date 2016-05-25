package de.fh_zwickau.pti.geobe.dto

import de.fh_zwickau.pti.geobe.domain.ScrumRole
import de.fh_zwickau.pti.geobe.domain.ScrumUser
import de.geobe.util.association.ToMany

/**
 * Created by aisma on 24.05.2016.
 */
class ScrumUserDto {
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
        Long id = 0
        String nick=''
        String password=''
        String firstName=''
        String lastName=''
        Date birthdate=new Date()
        ScrumRoleDto.QList roles= new ScrumRoleDto.QList();
        TaskDto.QList tasks= new TaskDto.QList();
    }

    public static class CSet {
        Long id = 0
        String nick=''
        String password=''
        String firstName=''
        String lastName=''
        Date birthdate=new Date()
        List<Long> taskIds=[]
    }


    public static class QNode {
        Long id
        String nick
        String firstName=''
        String lastName=''
        List<ScrumRoleDto.QNode> roles = []
        List<TaskDto.QNode> tasks = []
        @Override
        String toString() {
            this.@nick+':'+this.@firstName+':'+this.@lastName
        }
    }


}
