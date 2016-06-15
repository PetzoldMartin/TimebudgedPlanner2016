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
        RoleDto.QList developers = new RoleDto.QList() //TODO remove or use
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
        Long id = 0
        String name
        List<SprintDto.QNode> sprint = []
        List<UserstoryDto.QNode> userstory = []
        List<RoleDto.QNode> developers = [] //TODO remove or use
        @Override
        String toString() {
            this.@name
        }
    }
}
