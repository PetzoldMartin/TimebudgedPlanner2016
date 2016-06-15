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
        SprintDto.QList sprints = new SprintDto.QList()
        UserstoryDto.QList userstorys = new UserstoryDto.QList()
    }

    public static class CSet {
        Long id = 0
        String name = ''
        BigDecimal budget = 0
        List<Long> sprintIds = []
        List<Long> userstoryIds = []

    }

    public static class CDelete {
        Long id
    }

    public static class QNode {
        String name
        List<SprintDto.QNode> sprint = []
        List<UserstoryDto.QNode> userstory = []
        @Override
        String toString() {
            this.@name
        }
    }
}
