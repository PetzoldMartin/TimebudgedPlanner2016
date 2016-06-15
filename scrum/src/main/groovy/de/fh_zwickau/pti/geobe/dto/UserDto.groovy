package de.fh_zwickau.pti.geobe.dto
/**
 * Created by aisma on 24.05.2016.
 */
class UserDto {
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
        String nick = ''
        String password = ''
        String firstName = ''
        String lastName = ''
        Date birthdate = new Date()
        RoleDto.QList roles = new RoleDto.QList();
        TaskDto.QList tasks = new TaskDto.QList();
    }

    public static class CSet {
        Long id = 0
        String nick = ''
        String password = ''
        String firstName = ''
        String lastName = ''
        Date birthdate = new Date()
        List<Long> taskIds = []
        List<Long> roleIds = []

    }


    public static class QNode {
        Long id
        String nick
        String firstName = ''
        String lastName = ''
        List<RoleDto.QNode> roles = []
        List<TaskDto.QNode> tasks = []

        @Override
        String toString() {
            this.@nick + '(' + this.@firstName + ' ' + this.@lastName + ')'
        }
    }

    public static class CDelete {
        Long id
    }

}
