package de.fh_zwickau.pti.geobe.dto

/**
 * @author Heliosana
 * @date 10.05.2016.
 *
 */
class UserstoryDto {
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
        String name = ''
        String description = ''
        int priority = 1
        ProjectDto.QNode project = new ProjectDto.QNode()
        TaskDto.QList backlog = new TaskDto.QList()
//        def available = new TaskDto.QList()
    }

    public static class CSet {
        Long id = 0
        Long projectId = 0
        int priority = 1
        String name = ''
        String description = ''
        List<Long> taskIds = []
    }

    public static class QNode {
        Long id
        String name
        List<TaskDto.QNode> backlog = []
//        def available = new TaskDto.QList()

        @Override
        String toString() {
            this.@name
        }
    }
}
