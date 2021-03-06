package de.fh_zwickau.pti.geobe.view

import com.vaadin.data.Property
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.Component
import com.vaadin.ui.Notification
import com.vaadin.ui.Tree
import de.fh_zwickau.pti.geobe.service.ProjectService
import de.fh_zwickau.pti.geobe.service.StartupService
import de.fh_zwickau.pti.geobe.service.UserService
import de.fh_zwickau.pti.geobe.util.view.VaadinSelectionModel
import de.fh_zwickau.pti.geobe.util.view.VaadinTreeHelper
import de.geobe.util.vaadin.SubTree
import org.springframework.beans.factory.annotation.Autowired
import org.vaadin.spring.security.VaadinSecurity

import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

/**
 * Main selection component is this tree
 *
 * @author georg beier
 */
@SpringComponent
@UIScope
class ProjectTree extends SubTree
        implements Serializable {

    public static final String PROJECT_TYPE = 'Project'
    public static final String SPRINT_TYPE = 'Sprint'
    public static final String TASK_TYPE = 'Task'
    public static final String USERSTORY_TYPE = 'Userstory'
    public static final String USER_TYPE = 'User'

    private static final String PTREE = 'ptree'
    private static final String MENU = 'logoutmenu'
    private Tree projectTree
    private VaadinTreeHelper treeHelper

    private uiComponents

    private Map<String, Serializable> selectedProjectId

    def getSelectedProjectId() { selectedProjectId }

    @Autowired
    private ProjectService projectService
    @Autowired
    private UserService userService
    @Autowired
    private StartupService startupService

    @Autowired
    private VaadinSecurity vaadinSecurity

    VaadinSelectionModel selectionModel = new VaadinSelectionModel()

    @Override
    Component build() {
        vaadin."$C.vlayout"() {
            "$F.menubar"([uikey: MENU]) {
                "$F.menuitem"('Logout', [command: { vaadinSecurity.logout() }])
                "$F.menuitem"('Reload', [command: { //TODO for testing only
                    startupService.cleanupAll()
                    startupService.initApplicationData()
                    onEditItemDone([type: 'reload', id: null], 'Reload', true)
                }])

            }
            "$C.panel"('Projekte', [spacing: true, margin: true]) {
                "$F.tree"('Projekte, Backlogs und Sprints',
                        [uikey              : PTREE, caption: 'MenuTree',
                         valueChangeListener: { treeValueChanged(it) }])
            }
        }
    }

    @Override
    void init(Object... value) {
        uiComponents = vaadin.uiComponents
        projectTree = uiComponents."${subkeyPrefix + PTREE}"
        treeHelper = new VaadinTreeHelper(projectTree)
        buildTree(projectTree)
    }

    /**
     * handle changes of tree selection
     * @param event info on the newly selected tree item
     */
    private void treeValueChanged(Property.ValueChangeEvent event) {
        def selectId = event.property.value
        if (selectId) {
            def topItemId = treeHelper.topParentForId(selectId)
            if (topItemId != selectedProjectId && topItemId instanceof Map) {
                selectionModel.notifyRootChange(topItemId)
                selectedProjectId = topItemId
            }
            if (selectId instanceof Map) {
                selectionModel.notifyChange(selectId)
            } else {
                Notification.show("not implemented: " + selectId)
//                selectionModel.notifyChange( [type: ((String)selectId).split(':')[0], id: 0])
            }
        }
    }

    /**
     * build a tree representing the domain model
     * @param projectTree
     */
    private void buildTree(Tree projectTree) {
        def projects = projectService.projects //getProjects
        //loop over all projects
        if (!projects.all.isEmpty()) {
            projects.all.each { projId, projNode ->
                def projectId = treeHelper.addNode([type: PROJECT_TYPE, id: projId],
                        null, projNode.name, true)
                def userstoryTagId = treeHelper.addNode([type: USERSTORY_TYPE, id: 0, pid: projectId], projectId,
                        'Userstorys', !projNode.userstory.isEmpty())
                if (projNode.userstory) {
                    projNode.userstory.each { userstoryNode ->
                        def userstory = treeHelper.addNode([type: USERSTORY_TYPE, id: userstoryNode.id],
                                userstoryTagId, userstoryNode.name, true)
                        // build a subtree for every backlog task
                        if (userstoryNode.backlog) {
                            userstoryNode.backlog.each { taskNode ->
                                treeHelper.descend(taskNode, userstory, TASK_TYPE, 'id',
                                        'tag', 'children')
                            }
                        } else {
                            def newTask = treeHelper.addNode([type: TASK_TYPE, id: 0, parenttype: USERSTORY_TYPE, parentId: userstoryNode.id], userstory, '+Neuer Task', false)
                        }
                    }
                }

                def sprintsTagId = treeHelper.addNode([type: SPRINT_TYPE, id: 0, pid: projectId], projectId,
                        'Sprints', !projNode.sprint.isEmpty())
                if (projNode.sprint) {
                    projNode.sprint.each { sprintNode ->
                        treeHelper.addNode([type: SPRINT_TYPE, id: sprintNode.id],
                                sprintsTagId, sprintNode.name, false)
                    }
                }
            }
        } else {
            def projectId = treeHelper.addNode([type: PROJECT_TYPE, id: 0],
                    null, '+Neues Projekt', false)
        }
        def users = userService.getUsers()
        def userstoryTagId = treeHelper.addNode([type: USER_TYPE, id: 0], null, 'User', true)
        users.all.each { userId, userNode ->
            treeHelper.addNode([type: USER_TYPE, id: userNode.id], userstoryTagId, userNode.nick, false)
        }
    }

    /**
     * disable the tree while a tree item is edited on one of the tab pages
     */
    public void onEditItem() {
        projectTree.enabled = false
    }

    /**
     * enable and update the tree after editing an item
     * @param itemId identifies edited item
     * @param caption eventually updated caption of the edited item
     * @param mustReload tree must reload after new item was created
     *        or structure changed
     */
    public void onEditItemDone(Object itemId, String caption, boolean mustReload = false) {
        if (mustReload) {
            def expandedNodes = treeHelper.allExpanded
            projectTree.removeAllItems()
            buildTree(projectTree)
            def select = treeHelper.findMatchingId(itemId)
            if (select)
                projectTree.select(select)
            treeHelper.reexpand(expandedNodes)
        } else {
            if (projectTree.getItemCaption(itemId) != caption) {
                projectTree.setItemCaption(itemId, caption)
            }
        }
        projectTree.enabled = true
    }
}