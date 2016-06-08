package de.fh_zwickau.pti.geobe.view

import com.vaadin.data.Property
import com.vaadin.event.ShortcutAction
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.Reindeer
import de.fh_zwickau.pti.geobe.dto.ProjectDto
import de.fh_zwickau.pti.geobe.dto.RoleDto
import de.fh_zwickau.pti.geobe.service.IAuthorizationService
import de.fh_zwickau.pti.geobe.service.ProjectService
import de.fh_zwickau.pti.geobe.service.UserRoleService
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
    private TwinColSelect roleAssignment
    private Button newButton, editButton, saveButton, cancelButton, deleteButton
    private Map<String, Serializable> currentItemId
    private ProjectDto.QFull currentDto

    private DeleteDialog deleteDialog = new DeleteDialog()
    
    @Autowired
    private ProjectService projectService
    @Autowired
    private UserRoleService roleService
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
            "$F.twincol"('Userrollen', [uikey             : 'roleAssignment', rows: 8, width: '100%',
                                        leftColumnCaption : 'Avaiable User', enabled: false,
                                        rightColumnCaption: 'Assigned User', gridPosition: [0, 2, 1, 2]])
            "$C.hlayout"([uikey       : 'buttonfield', spacing: true,
                          gridPosition: [0, 3, 1, 3]]) {
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
        roleAssignment = uiComponents."${subkeyPrefix}roleAssignment"
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

//        roleAssignment.setItemCaptionMode( AbstractSelect.ItemCaptionMode.PROPERTY)
        roleAssignment.addListener(new Property.ValueChangeListener() {
            @Override
            void valueChange(Property.ValueChangeEvent event) {
                if (event.getProperty().getValue() != null) {
                    ScrumView.DebugField.value="$event.property.value+\n"
                }
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
        deleteDialog.id.value= currentDto.id.toString()
        deleteDialog.name.value= currentDto.name
        deleteDialog.storyCount.value= currentDto.userstorys.all.size().toString()
        deleteDialog.sprintCount.value= currentDto.sprints.all.size().toString()
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
        [pname, pbudget, roleAssignment, saveButton, cancelButton, editButton, deleteButton]
                .each { it.enabled = false }
        newButton.enabled = true
    }

    /** prepare SHOW state */
    @Override
    protected showmode() {
        initItem(currentDto.id)
        [pname, pbudget, roleAssignment, saveButton, cancelButton]
                .each { it.enabled = false }
        [editButton, newButton, deleteButton].each { it.enabled = true }
    }

    /** prepare for editing in EDIT, CREATE, CREATEEMPTY states */
    @Override
    protected editmode() {
        projectTree.onEditItem()
        [pname, pbudget, roleAssignment, saveButton, cancelButton].each { it.enabled = true }
        [editButton, newButton, deleteButton].each { it.enabled = false }
    }

    /** clear all editable fields */
    @Override
    protected clearFields() {
        [pname, pbudget, roleAssignment].each { it.clear() }
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

        setAssignedList()

    }
    //TODO Role assignment
    private void setAssignedList() {
        roleAssignment.removeAllItems() //available side
        userService.getUsersNotInProject(currentDto.id).all.each { id, userNode ->
            roleAssignment.addItem(id)
            roleAssignment.setItemCaption(id, "$userNode.nick ($userNode.firstName)" + " userID: $userNode.id")
        }
        def select = [] // assigned side
        roleService.getRolesInProject(currentDto.id).all.each { id, roleNode ->
            roleAssignment.addItem(roleNode.user.id)
            roleAssignment.setItemCaption(roleNode.user.id, "$roleNode.user.nick : $roleNode.userRole" + " userID: $roleNode.user.id")
            select << id
        }
        roleAssignment.setValue(select)
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
                    "$F.text"('Anzahl der abhängigen Tasks', [uikey: 'taskCount'])
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
