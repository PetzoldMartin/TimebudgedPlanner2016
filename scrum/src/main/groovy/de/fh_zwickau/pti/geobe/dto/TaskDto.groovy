package de.fh_zwickau.pti.geobe.dto

/**
 * @author georg beier
 */
class TaskDto {
    public static class QList {
        LinkedHashMap<Long, String> all = [:]

        Long getFirstId() {
            if (all) {
                all.keySet().iterator().next()
            } else {
                0
            }
        }
    }

    public static class QFull {
        Long rootTaskId
        Long id
        String classname
        String tag
        String description
        Long estimate
        Long summedEstimate
        Long spent
        Boolean completed
        UserstoryDto.QFull userstory = new UserstoryDto.QFull()
        SprintDto.QList sprints = new SprintDto.QList()
        QList supertask = new QList()
        List<QNode> subtasks = []
        UserDto.QList developers = new UserDto.QList()
    }

    public static class QNode {
        Long id
        String tag
        List<QNode> children = []
        List<UserDto.QNode> developers = []
    }

    public static class CSet {
        Long id = 0
        String classname
        String tag
        String description
        Long estimate
        Long spent
        Boolean completed
        Long userstoryId
        List<Long> sprintIds = []
        Long supertaskId
        List<Long> subtaskIds = []
        List<Long> developersIds = []
    }

    public static class CDelete {
        Long id
    }
}
