package de.fh_zwickau.pti.geobe.dto
/**
 * @author georg beier
 */
class ProjectDto {
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
        BigDecimal budget = 0
        //TaskDto.QList backlog = new TaskDto.QList()
        SprintDto.QList sprints = new SprintDto.QList()
        UserStoryDto.QList userStorys = new UserStoryDto.QList()
    }

    public static class CSet {
        Long id = 0
        String name = ''
        BigDecimal budget = 0
        //List<Long> taskIds = []
        List<Long> sprintIds = []
        List<Long> userStoryIds = []

    }

    public static class QNode {
        String name
        //List<TaskDto.QNode> backlog = []
        List<SprintDto.QNode> sprint = []
        List<UserStoryDto.QNode> userStory = []

        @Override
        String toString() {
            this.@name
        }
    }
}
