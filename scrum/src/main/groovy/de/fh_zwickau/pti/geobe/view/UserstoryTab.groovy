package de.fh_zwickau.pti.geobe.view

import com.vaadin.shared.ui.MarginInfo
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import com.vaadin.ui.themes.Reindeer
import de.fh_zwickau.pti.geobe.dto.TaskDto.QNode
import de.fh_zwickau.pti.geobe.dto.UserstoryDto
import de.fh_zwickau.pti.geobe.service.ProjectService
import de.fh_zwickau.pti.geobe.service.UserstoryService
import de.fh_zwickau.pti.geobe.util.view.VaadinSelectionListener
import de.fh_zwickau.pti.geobe.util.view.VaadinTreeRootChangeListener
import de.geobe.util.vaadin.TabViewStateMachine
import de.geobe.util.vaadin.VaadinBuilder
import org.springframework.beans.factory.annotation.Autowired

import static TabViewStateMachine.Event
import static de.geobe.util.vaadin.VaadinBuilder.C
import static de.geobe.util.vaadin.VaadinBuilder.F

/**
 * show a userstory on its own tab
 * @author georg beier
 */
@SpringComponent
@UIScope
class UserstoryTab extends TabBase
        implements VaadinSelectionListener, VaadinTreeRootChangeListener, Serializable {
    private TextField name, project, priority
    private TextArea description

    private TwinColSelect backlog
    private Button newButton, editButton, saveButton, cancelButton, deleteButton
    private Map<String, Serializable> currentUserstoryItemId
    private Map<String, Serializable> currentProjectItemId
    private UserstoryDto.QFull currentDto

    private DeleteDialog deleteDialog = new DeleteDialog()

    @Autowired
    private UserstoryService userstoryService
    @Autowired
    private ProjectService projectService

    //TODO remove task selection
    //TODO make create Task from scrap avaiable
    @Override
    Component build() {
        Component c = vaadin."$C.gridlayout"('Userstorys',
                [uikey  : 'grid', columns: 2, rows: 4,
                 margin : new MarginInfo(false, false, false, true),
                 spacing: true]) {
            "$F.text"('<b>Userstory</b>', [uikey: 'name', captionAsHtml: true, enabled: false])
            "$F.text"('Projekt', [uikey: 'project', enabled: false])
            "$F.text"('Priorität', [uikey: 'priority', enabled: false])
            "$F.textarea"('Beschreibung', [uikey: 'description', enabled: false])
            "$F.twincol"('Backlog', [uikey             : 'backlog', rows: 8, width: '100%',
                                     leftColumnCaption : 'available', enabled: false,
                                     rightColumnCaption: 'selected', gridPosition: [0, 2, 1, 2]])
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
        name = uiComponents."${subkeyPrefix}name"
        priority = uiComponents."${subkeyPrefix}priority"
        project = uiComponents."${subkeyPrefix}project"
        description = uiComponents."${subkeyPrefix}description"
        backlog = uiComponents."${subkeyPrefix}backlog"
        newButton = uiComponents."${subkeyPrefix}newbutton"
        editButton = uiComponents."${subkeyPrefix}editbutton"
        saveButton = uiComponents."${subkeyPrefix}savebutton"
        cancelButton = uiComponents."${subkeyPrefix}cancelbutton"
        deleteButton = uiComponents."${subkeyPrefix}deleteButton"
        projectTree.selectionModel.addListenerForKey(this, 'Userstory')
        projectTree.selectionModel.addRootChangeListener(this)

        // build dialog window
        deleteDialog.build()
        // build state machine
        sm = new TabViewStateMachine(TabViewStateMachine.State.SUBTAB, 'UsTab')
        configureSm()
        sm.execute(Event.Init)
    }

    @Override
    void onItemSelected(Map<String, Serializable> userstoryItemId) {
        currentUserstoryItemId = userstoryItemId
        initItem((Long) userstoryItemId['id'])
        sm.execute(Event.Select, userstoryItemId['id'])
    }

    @Override
    void onRootChanged(Map<String, Serializable> projectItemId) {
        currentProjectItemId = projectItemId
        sm.execute(Event.Root)
    }

    @Override
    protected getCurrentItemId() { currentUserstoryItemId }

    @Override
    protected Long getCurrentDomainId() { (Long) currentUserstoryItemId['id'] }

    @Override
    protected String getCurrentCaption() { currentDto.name }

    @Override
    protected getMatchForNewItem() { [type: ProjectTree.USERSTORY_TYPE, id: currentDto.id] }

    /** prepare INIT state */
    @Override
    protected initmode() {
        [name, description, priority, backlog, saveButton, cancelButton, editButton, deleteButton, newButton].each {
            it.enabled = false
        }
    }

    /** prepare EMPTY state */
    @Override
    protected emptymode() {
        clearFields()
        [name, description, priority, backlog, saveButton, cancelButton, editButton, deleteButton].each {
            it.enabled = false
        }
        project.value = projectService.getProjectCaption((Long) currentProjectItemId['id'])
        setAvailableList()
        [newButton].each { it.enabled = true }
    }

    /** prepare SHOW state */
    @Override
    protected showmode() {
        [name, description, priority, backlog, saveButton, cancelButton].each { it.enabled = false }
        [editButton, deleteButton, newButton].each { it.enabled = true }
    }

    /** prepare for editing in EDIT, CREATE, CREATEEMPTY states */
    @Override
    protected editmode() {
        projectTree.onEditItem()
        [name, description, priority, backlog, saveButton, cancelButton].each { it.enabled = true }
        [editButton, deleteButton, newButton].each { it.enabled = false }
    }

    /** clear all editable fields */
    @Override
    protected clearFields() {
        [name, description, priority, backlog].each { it.clear() }
    }

    private void setAvailableList() {
        backlog.removeAllItems()
        userstoryService.getProjectBacklog((Long) currentProjectItemId['id']).each {
            makeAvailableList(it)
        }
    }

    private makeAvailableList(QNode taskNode, int level = 0) {
        backlog.addItem(taskNode.id)
        backlog.setItemCaption(taskNode.id, (level > 0 ? '  ' : '') + ('-' * level) + taskNode.tag)
        taskNode.children.each { makeAvailableList(it, level + 1) }
    }

    /**
     * for the given persistent object id, fetch the full dto and save it in field currentDto
     * @param itemId object id
     */
    @Override
    protected void initItem(Long itemId) {
        currentDto = userstoryService.getUserstoryDetails(itemId)
        setFieldValues()
    }

    @Override
    protected deletemode() {
        projectTree.onEditItem()
        deleteDialog.id.value = currentDto.id.toString()
        deleteDialog.name.value = currentDto.name
        deleteDialog.taskCount.value = currentDto.backlog.all.size().toString()
        [deleteDialog.acceptButton, deleteDialog.cancelButton].each { it.enabled = true }
        UI.getCurrent().addWindow(deleteDialog.window)
    }

    @Override
    protected cancelDelete() {
        deleteDialog.window.close()

    }

    @Override
    protected deleteItem() {
        userstoryService.deleteUserstory(new UserstoryDto.CDelete(id: currentDto.id))
        deleteDialog.window.close()
    }

    /**
     * set all fields from the current full dto object
     */
    @Override
    protected void setFieldValues() {
        name.value = currentDto.name
        project.value = currentDto.project.name
        priority.value = currentDto.priority.toString()
        description.value = currentDto.description
        setAvailableList()
        def select = []
        currentDto.backlog.all.each { k, v ->
            backlog.addItem(k)
            backlog.setItemCaption(k, v)
            select += k
        }
        backlog.setValue(select)
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
        UserstoryDto.CSet command = new UserstoryDto.CSet()
        command.id = id
        command.projectId = (Long) currentProjectItemId['id']
        command.name = name.getValue()
        command.description = description.getValue()
        def v = []
        backlog.value.each {
            v << it
        }
        command.taskIds = v
        currentDto = userstoryService.createOrUpdateUserstory(command)
    }

    private class DeleteDialog {
        TextField name, id, taskCount
        Button acceptButton, cancelButton
        Window window

        private VaadinBuilder winBuilder = new VaadinBuilder()

        public Window build() {
            String keyPrefix = "${subkeyPrefix}deleteDialog."
            winBuilder.keyPrefix = keyPrefix
            window = winBuilder."$C.window"('Wollen Sie wirklich diese Userstory Löschen?',
                    [spacing: true, margin: true,
                     modal  : true, closable: false]) {
                "$C.vlayout"('top', [spacing: true, margin: true]) {
                    "$F.text"('id', [uikey: 'id'])
                    "$F.text"('Name', [uikey: 'name'])
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
            acceptButton = dialogComponents."${keyPrefix}acceptButton"
            cancelButton = dialogComponents."${keyPrefix}cancelbutton"
            window.center()
        }
    }
}
