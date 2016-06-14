package de.fh_zwickau.pti.geobe.view

import com.vaadin.data.Property
import com.vaadin.event.ShortcutAction
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.Reindeer
import de.fh_zwickau.pti.geobe.domain.ROLETYPE
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.service.IAuthorizationService
import de.fh_zwickau.pti.geobe.service.ProjectService
import de.fh_zwickau.pti.geobe.service.RoleService
import de.fh_zwickau.pti.geobe.service.UserService
import de.fh_zwickau.pti.geobe.util.view.VaadinSelectionListener
import de.fh_zwickau.pti.geobe.util.view.VaadinTreeRootChangeListener
import de.geobe.util.vaadin.TabViewStateMachine
import de.geobe.util.vaadin.VaadinBuilder
import org.springframework.beans.factory.annotation.Autowired

import static TabViewStateMachine.Event
import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

/**
 * show and edit projects in their own tab
 *
 * @author georg beier
 */
@SpringComponent
@UIScope
class ProjectTab extends TabBase implements VaadinSelectionListener,
        VaadinTreeRootChangeListener, Serializable {

    private static final String PID = 'pid'
    private static final String PNAME = 'pname'
    private static final String PBUDGET = 'pbudget'

    private TextField pid, pname, pbudget
    private NativeSelect roleTypeSelect
    private ListSelect availableList, assignedList
    private Button newButton, editButton, saveButton, cancelButton, deleteButton, addButton, removeButton
    private Map<String, Serializable> currentItemId
    private ProjectDto.QFull currentDto


    private DeleteDialog deleteDialog = new DeleteDialog()

    @Autowired
    private ProjectService projectService
    @Autowired
    private RoleService roleService
    @Autowired
    private UserService userService
    @Autowired
    private ProjectTree projectTree
    @Autowired
    private IAuthorizationService authorizationService

    @Override
    Component build() {
        // Caption shows on the tab
        def c = vaadin."$C.vlayout"('Projekt',
                [spacing: true, margin: true]) {
            "$F.text"('id', [uikey: PID, enabled: false])
            "$F.text"('Name', [uikey: PNAME])
            "$F.text"('Budget', [uikey: PBUDGET])
            "$C.hlayout"([uikey: 'assignedField', spacing: true]) {
                "$F.list"('Avaiable User', [uikey               : 'leftColumn', rows: 12, width: '500', enabled: false,
                                            itemCaptionMode     : AbstractSelect.ItemCaptionMode.ID,
                                            nullSelectionAllowed: false, multiSelect: true
                ])
                "$C.vlayout"([uikey: 'selectField', spacing: true]) {
                    "$F.label"('')
                    "$F.nativeselect"('Rollentypauswahl', [uikey               : 'roleTypeSelect', enabled: true, width: '150',
                                                           itemCaptionMode     : AbstractSelect.ItemCaptionMode.ID,
                                                           nullSelectionAllowed: false,
                                                           items               : ROLETYPE.values(), value: ROLETYPE.Developer
                    ])
                    "$F.button"('>> Add >>', [uikey         : 'removeButton', width: '150',
                                              //                                        visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                              disableOnClick: false,
                                              clickListener : { addRoles() }
                    ])
                    "$F.label"('')
                    "$F.button"('<< Remove <<', [uikey         : 'addButton', width: '150',
//                                         visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                                 disableOnClick: false,
                                                 clickListener : { removeRoles() }
                    ])
                }
                "$F.list"('Assigned User', [uikey               : 'rightColumn', rows: 12, width: '500', enabled: false,
                                            itemCaptionMode     : AbstractSelect.ItemCaptionMode.ID,
                                            nullSelectionAllowed: false, multiSelect: true
                ])
            }
            "$C.hlayout"([uikey: 'buttonfield', spacing: true]) {
                "$F.button"('New', [uikey         : 'newbutton',
                                    visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                    disableOnClick: true,
                                    clickListener : { sm.execute(Event.Create) }])
                "$F.button"('Edit', [uikey         : 'editbutton',
                                     visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                     disableOnClick: true,
                                     clickListener : { sm.execute(Event.Edit) }])
                "$F.button"('Delete', [uikey         : 'deleteButton',
                                       visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                       disableOnClick: true,
                                       clickListener : { sm.execute(Event.Delete) }])
                "$F.button"('Cancel', [uikey         : 'cancelbutton',
                                       disableOnClick: true, enabled: false,
                                       clickListener : { sm.execute(Event.Cancel) }])
                "$F.button"('Save', [uikey         : 'savebutton',
                                     disableOnClick: true, enabled: false,
                                     clickShortcut : ShortcutAction.KeyCode.ENTER,
                                     styleName     : Reindeer.BUTTON_DEFAULT,
                                     clickListener : { sm.execute(Event.Save) }])
            }
        }
    }

    @Override
    void init(Object... value) {
        uiComponents = vaadin.uiComponents
        pid = uiComponents."${subkeyPrefix + PID}"
        pname = uiComponents."${subkeyPrefix + PNAME}"
        pbudget = uiComponents."${subkeyPrefix + PBUDGET}"
        assignedList = uiComponents."${subkeyPrefix}rightColumn"
        availableList = uiComponents."${subkeyPrefix}leftColumn"
        addButton = uiComponents."${subkeyPrefix}addButton"
        removeButton = uiComponents."${subkeyPrefix}removeButton"
        roleTypeSelect = uiComponents."${subkeyPrefix}roleTypeSelect"
        newButton = uiComponents."${subkeyPrefix}newbutton"
        editButton = uiComponents."${subkeyPrefix}editbutton"
        deleteButton = uiComponents."${subkeyPrefix}deleteButton"
        saveButton = uiComponents."${subkeyPrefix}savebutton"
        cancelButton = uiComponents."${subkeyPrefix}cancelbutton"
        projectTree.selectionModel.addListenerForKey(this, 'Project')
        projectTree.selectionModel.addRootChangeListener(this)
        // build dialog window
        deleteDialog.build()
        // build state machine
        sm = new TabViewStateMachine(TabViewStateMachine.State.TOPTAB, 'PrjTab')
        configureSm()
        sm.execute(Event.Init)
        roleTypeSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            void valueChange(Property.ValueChangeEvent event) {
//                Notification.show("Value changed:",
//                        String.valueOf(event.property.value,
//                        Notification.Type.TRAY_NOTIFICATION))
            }
        })
    }

    @Override
    void onItemSelected(Map<String, Serializable> projectItemId) {
        currentItemId = projectItemId
        initItem((Long) projectItemId['id'])
        sm.execute(Event.Select, projectItemId['id'])
    }

    @Override
    void onRootChanged(Map<String, Serializable> projectItemId) {
        onItemSelected(projectItemId)
    }

    @Override
    protected getCurrentItemId() { currentItemId }

    @Override
    protected Long getCurrentDomainId() { (Long) currentItemId['id'] }

    @Override
    protected String getCurrentCaption() { currentDto.name }

    @Override
    protected getMatchForNewItem() {
        [type: ProjectTree.PROJECT_TYPE,
         id  : currentDto.id]
    }

    @Override
    protected deletemode() {
        projectTree.onEditItem()
        deleteDialog.id.value = currentDto.id.toString()
        deleteDialog.name.value = currentDto.name
        deleteDialog.storyCount.value = currentDto.userstorys.all.size().toString()
        deleteDialog.sprintCount.value = currentDto.sprints.all.size().toString()
        //TODO add task count
//        int count=0;
//        currentDto.userstorys.all.each
//                {Userstory us ->
//            us.task.all.size()}
//        deleteDialog.taskCount.value= count.toString()
        [deleteDialog.acceptButton, deleteDialog.cancelButton].each { it.enabled = true }
        UI.getCurrent().addWindow(deleteDialog.window)
    }

    @Override
    protected cancelDelete() {
        deleteDialog.window.close()

    }

    @Override
    protected deleteItem() {
        projectService.deleteProject(new ProjectDto.CDelete(id: currentDto.id))
        deleteDialog.window.close()
    }


    @Override
    protected createemptymode() {
//        authorizationService.roles
        def user = authorizationService.user
        if (authorizationService.hasRole('ROLE_ADMIN')) {
            super.createemptymode()
        } else {
            Notification.show("Sorry, you don't have the rights to do that.");
            newButton.enabled = true
            sm.currentState
        }
    }

    /** prepare EMPTY state */
    @Override
    protected emptymode() {
        clearFields()
        [pname, pbudget, saveButton, cancelButton, editButton, deleteButton, assignedList, availableList, addButton, removeButton, roleTypeSelect]
                .each { it.enabled = false }
        newButton.enabled = true
    }

    /** prepare SHOW state */
    @Override
    protected showmode() {
        initItem(currentDto.id)
        [pname, pbudget, saveButton, cancelButton, assignedList, availableList, addButton, removeButton, roleTypeSelect]
                .each { it.enabled = false }
        [editButton, newButton, deleteButton].each { it.enabled = true }
    }

    /** prepare for editing in EDIT, CREATE, CREATEEMPTY states */
    @Override
    protected editmode() {
        projectTree.onEditItem()
        [pname, pbudget, saveButton, cancelButton, assignedList, availableList, addButton, removeButton, roleTypeSelect].each { it.enabled = true }
        [editButton, newButton, deleteButton].each { it.enabled = false }
    }

    /** clear all editable fields */
    @Override
    protected clearFields() {
        [pname, pbudget].each { it.clear() }
        setAssignedList(0l)
    }

    /**
     * for the given persistent object id, fetch the full dto
     * and save it in field currentDto
     * @param itemId object id
     */
    @Override
    protected void initItem(Long itemId) {
        currentDto = projectService.getProjectDetails((Long) itemId)
        setFieldValues()
    }

    /**
     * set all fields from the current full dto object
     */
    @Override
    protected void setFieldValues() {
        pid.value = currentDto.id.toString()
        pname.value = currentDto.name
        pbudget.value = currentDto.budget.toString()
        setAssignedList(currentDto.id)
    }
    //TODO refresh with right id when in creation mode
    private void setAssignedList(Long pid) {
        availableList.removeAllItems() //availableList side
        userService.getUsersNotInProject(pid).all.each { id, userNode ->
            availableList.addItem(new RoleDto.QNode(user: userNode))
        }
        assignedList.removeAllItems() // assignedList side
//        currentDto.developers.all.each { id, roleNode ->
        roleService.getRolesInProject(pid).all.each { id, roleNode ->
            assignedList.addItem(roleNode)
        }
    }

    private addRoles() {
        if (availableList.value.empty) {
            Notification.show("Keine Avaiable User ausgewählt")
        } else {
            availableList.value.each { RoleDto.QNode node ->
                availableList.removeItem(node)
                node.userRole = roleTypeSelect.value
                assignedList.addItem(node)
            }
//            Notification.show(availableList.value.toString()+roleTypeSelect.value)
        }
    }

    private removeRoles() {
        if (assignedList.value.empty) {
            Notification.show("Keine Assigned User ausgewählt")
        } else {
            assignedList.value.each { RoleDto.QNode node ->
                assignedList.removeItem(node)
                node.userRole = null
                availableList.addItem(node)

            }
//            Notification.show(assignedList.value.toString())
        }
    }

    /**
     * create or update a domain object from the current field values and
     * update the current dto from the saved domain object
     *
     * @param id domain id of domain object or 0 (zero) to create a new
     * @return updated current dto
     */
    @Override
    protected saveItem(Long id) {
        ProjectDto.CSet command = new ProjectDto.CSet()
        command.id = id
        command.name = pname.value
        command.budget = new BigDecimal(longFrom(pbudget.value))
        currentDto = projectService.createOrUpdateProject(command)
        availableList.getItemIds().toArray().each { RoleDto.QNode it ->
            roleService.deleteRole(new RoleDto.CDelete(id: it.id))
        }
        assignedList.getItemIds().toArray().each { RoleDto.QNode it ->
            roleService.createOrUpdateRole(new RoleDto.CSet(id: it.id, userRole: it.userRole, projectId: id, userId: it.user.id))
        }
    }

    private class DeleteDialog {
        TextField name, id, storyCount, sprintCount, taskCount
        Button acceptButton, cancelButton
        Window window

        private VaadinBuilder winBuilder = new VaadinBuilder()

        public Window build() {
            String keyPrefix = "${subkeyPrefix}deleteDialog."
            winBuilder.keyPrefix = keyPrefix
            window = winBuilder."$C.window"('Wollen Sie wirklich dieses Projekt Löschen?',
                    [spacing: true, margin: true,
                     modal  : true, closable: false]) {
                "$C.vlayout"('top', [spacing: true, margin: true]) {
                    "$F.text"('id', [uikey: 'id'])
                    "$F.text"('Name', [uikey: 'name'])
                    "$F.text"('Anzahl der abhängigen Userstorys', [uikey: 'storyCount'])
                    "$F.text"('Anzahl der abhängigen Sprints', [uikey: 'sprintCount'])
                    "$F.text"('Anzahl der abhängigen Tasks', [uikey: 'taskCount', visible: false])
                    "$C.hlayout"([uikey: 'buttonfield', spacing: true]) {
                        "$F.button"('Accept',
                                [uikey         : 'acceptButton',
                                 disableOnClick: true, enabled: true,
                                 clickListener : { sm.execute(Event.Root) }])
                        "$F.button"('Cancel',
                                [uikey         : 'cancelbutton',
                                 disableOnClick: true, enabled: true,
                                 clickListener : { sm.execute(Event.Cancel) }])
                    }
                }
            }

            def dialogComponents = winBuilder.uiComponents

            id = dialogComponents."${keyPrefix}id"
            name = dialogComponents."${keyPrefix}name"
            taskCount = dialogComponents."${keyPrefix}taskCount"
            storyCount = dialogComponents."${keyPrefix}storyCount"
            sprintCount = dialogComponents."${keyPrefix}sprintCount"
            acceptButton = dialogComponents."${keyPrefix}acceptButton"
            cancelButton = dialogComponents."${keyPrefix}cancelbutton"
            window.center()
        }
    }

}
