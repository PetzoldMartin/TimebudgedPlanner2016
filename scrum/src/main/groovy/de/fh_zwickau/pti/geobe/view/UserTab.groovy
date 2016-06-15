package de.fh_zwickau.pti.geobe.view

import com.vaadin.shared.ui.MarginInfo
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.Reindeer
import de.fh_zwickau.pti.geobe.dto.UserDto
import de.fh_zwickau.pti.geobe.service.IAuthorizationService
import de.fh_zwickau.pti.geobe.service.ProjectService
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
 * show a user on its own tab
 * @author georg beier
 */
@SpringComponent
@UIScope
class UserTab extends TabBase
        implements VaadinSelectionListener, VaadinTreeRootChangeListener, Serializable {
    private TextField nick, firstName, lastName
    private DateField birthdate
    private Button newButton, editButton, saveButton, cancelButton, deleteButton
    private Map<String, Serializable> currentUserItemId
    private Map<String, Serializable> currentProjectItemId
    private UserDto.QFull currentDto

    private DeleteDialog deleteDialog = new DeleteDialog()

    @Autowired
    private IAuthorizationService authorizationService

    @Autowired
    private UserService userService
    @Autowired
    private ProjectService projectService

    @Override
    Component build() {
        Component c = vaadin."$C.gridlayout"('Users',
                [uikey  : 'grid', columns: 2, rows: 4,
                 margin : new MarginInfo(false, false, false, true),
                 spacing: true]) {
            "$F.text"('<b>User</b>', [uikey: 'nick', captionAsHtml: true, enabled: false])
            "$F.text"('Vorname', [uikey: 'firstName', enabled: false])
            "$F.text"('Nachname', [uikey: 'lastName', enabled: false])
            "$F.date"('Geburtstag', [uikey: 'birthdate', enabled: false])
            "$C.hlayout"([uikey: 'buttonfield', spacing: true, gridPosition: [0, 3, 1, 3]]) {
                "$F.button"('New', [uikey        : 'newbutton', disableOnClick: true,
                                    clickListener: { sm.execute(Event.Create) }])
                "$F.button"('Edit', [uikey        : 'editbutton', disableOnClick: true,
                                     clickListener: { sm.execute(Event.Edit) }])
                "$F.button"('Delete', [uikey         : 'deleteButton',
//                                       visible       : authorizationService.hasRole('ROLE_ADMIN'),
                                       disableOnClick: true,
                                       clickListener : { sm.execute(Event.Delete) }])
                "$F.button"('Cancel', [uikey        : 'cancelbutton', disableOnClick: true, enabled: false,
                                       clickListener: { sm.execute(Event.Cancel) }])
                "$F.button"('Save', [uikey        : 'savebutton', disableOnClick: true, enabled: false,
//                                     clickShortcut: ShortcutAction.KeyCode.ENTER,
                                     styleName    : Reindeer.BUTTON_DEFAULT,
                                     clickListener: {
                                         sm.execute(Event.Save)
                                     }])
            }
        }
//        init()
        c
    }

    @Override
    void init(Object... value) {
        uiComponents = vaadin.uiComponents
        nick = uiComponents."${subkeyPrefix}nick"
        firstName = uiComponents."${subkeyPrefix}firstName"
        lastName = uiComponents."${subkeyPrefix}lastName"
        birthdate = uiComponents."${subkeyPrefix}birthdate"
        newButton = uiComponents."${subkeyPrefix}newbutton"
        editButton = uiComponents."${subkeyPrefix}editbutton"
        saveButton = uiComponents."${subkeyPrefix}savebutton"
        cancelButton = uiComponents."${subkeyPrefix}cancelbutton"
        deleteButton = uiComponents."${subkeyPrefix}deleteButton"
        projectTree.selectionModel.addListenerForKey(this, 'User')
        projectTree.selectionModel.addRootChangeListener(this)

        // build dialog window
        deleteDialog.build()
        // build state machine
        sm = new TabViewStateMachine(TabViewStateMachine.State.SUBTAB, 'UserTab')
        configureSm()
        sm.execute(Event.Init)
    }

    @Override
    void onItemSelected(Map<String, Serializable> userItemId) {
        currentUserItemId = userItemId
        initItem((Long) userItemId['id'])
        sm.execute(Event.Select, userItemId['id'])
    }

    @Override
    void onRootChanged(Map<String, Serializable> projectItemId) {
        currentProjectItemId = projectItemId
        sm.execute(Event.Root)
    }

    @Override
    protected getCurrentItemId() { currentUserItemId }

    @Override
    protected Long getCurrentDomainId() { (Long) currentUserItemId['id'] }

    @Override
    protected String getCurrentCaption() { currentDto.nick }

    @Override
    protected getMatchForNewItem() { [type: ProjectTree.USER_TYPE, id: currentDto.id] }

    /** prepare INIT state */
    @Override
    protected initmode() {
        [nick, firstName, lastName, birthdate, saveButton, cancelButton, editButton, deleteButton, newButton].each {
            it.enabled = false
        }
    }

    /** prepare EMPTY state */
    @Override
    protected emptymode() {
        clearFields()
        [nick, firstName, lastName, birthdate, saveButton, cancelButton, editButton, deleteButton].each {
            it.enabled = false
        }
        [newButton].each { it.enabled = true }
    }

    /** prepare SHOW state */
    @Override
    protected showmode() {
        [nick, firstName, lastName, birthdate, saveButton, cancelButton].each { it.enabled = false }
        [editButton, deleteButton, newButton].each { it.enabled = true }
    }

    /** prepare for editing in EDIT, CREATE, CREATEEMPTY states */
    @Override
    protected editmode() {
        projectTree.onEditItem()
        [nick, firstName, lastName, birthdate, saveButton, cancelButton].each { it.enabled = true }
        [editButton, deleteButton, newButton].each { it.enabled = false }
    }

    /** clear all editable fields */
    @Override
    protected clearFields() {
        [nick, firstName, lastName, birthdate,].each { it.clear() }
    }

    @Override
    protected deletemode() {
        projectTree.onEditItem()
        deleteDialog.id.value = currentDto.id.toString()
        deleteDialog.nick.value = currentDto.nick
        deleteDialog.taskCount.value = currentDto.tasks.all.size().toString()
        deleteDialog.projectCount.value = currentDto.roles.all.size().toString()
        [deleteDialog.acceptButton, deleteDialog.cancelButton].each { it.enabled = true }
        UI.getCurrent().addWindow(deleteDialog.window)
    }

    @Override
    protected cancelDelete() {
        deleteDialog.window.close()

    }

    @Override
    protected deleteItem() {
        userService.deleteUser(new UserDto.CDelete(id: currentDto.id))
        deleteDialog.window.close()
    }

    /**
     * for the given persistent object id, fetch the full dto and save it in field currentDto
     * @param itemId object id
     */
    @Override
    protected void initItem(Long itemId) {
        currentDto = userService.getUserDetails(itemId)
        setFieldValues()
    }

    /**
     * set all fields from the current full dto object
     */
    @Override
    protected void setFieldValues() {
        nick.value = currentDto.nick
        firstName.value = currentDto.firstName
        lastName.value = currentDto.lastName
        birthdate.value = currentDto.birthdate
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
        UserDto.CSet command = new UserDto.CSet()
        command.id = id
        command.nick = nick.value
        command.firstName = firstName.getValue()
        command.lastName = lastName.getValue()
        command.birthdate = birthdate.value
        currentDto = userService.createOrUpdateUser(command)
    }

    private class DeleteDialog {
        TextField nick, id, taskCount, projectCount
        Button acceptButton, cancelButton
        Window window

        private VaadinBuilder winBuilder = new VaadinBuilder()

        public Window build() {
            String keyPrefix = "${subkeyPrefix}deleteDialog."
            winBuilder.keyPrefix = keyPrefix
            window = winBuilder."$C.window"('Wollen Sie wirklich diesen User Löschen?',
                    [spacing: true, margin: true,
                     modal  : true, closable: false]) {
                "$C.vlayout"('top', [spacing: true, margin: true]) {
                    "$F.text"('id', [uikey: 'id'])
                    "$F.text"('Nick', [uikey: 'nick'])
                    "$F.text"('Anzahl der verwalteten Tasks', [uikey: 'taskCount'])
                    "$F.text"('Anzahl der zugeortneten Projekte', [uikey: 'projectCount'])
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
            nick = dialogComponents."${keyPrefix}nick"
            taskCount = dialogComponents."${keyPrefix}taskCount"
            projectCount = dialogComponents."${keyPrefix}projectCount"
            acceptButton = dialogComponents."${keyPrefix}acceptButton"
            cancelButton = dialogComponents."${keyPrefix}cancelbutton"
            window.center()
        }
    }
}
