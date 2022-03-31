package seedu.address.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ProfileCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private Logic logic;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList, Logic logic) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        this.logic = logic;
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    static class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }

        protected int getPersonCardIndex() {
            return getIndex() + 1;
        }
    }

    /**
     * Handles the event whenever the selected person card changes.
     */
    public void handleSelect() throws CommandException {
        try {
            Index personIndexSelected = Index.fromZeroBased(personListView.getSelectionModel().getSelectedIndex() + 1);
            Person personSelected = personListView.getSelectionModel().getSelectedItem();

            String profileCommandInput = String.format("%s %s", ProfileCommand.COMMAND_WORD, personIndexSelected);
            CommandResult commandResult = logic.execute(profileCommandInput);
            //update general display to show profile
            UiManager.getMainWindow().getGeneralDisplay().setProfile(personSelected);
            //set command feedback
            UiManager.getMainWindow().getResultDisplay().setFeedbackToUser(commandResult.getFeedbackToUser());
        } catch (ParseException | CommandException e) {
            UiManager.getMainWindow().getResultDisplay().setFeedbackToUser(e.getMessage());
        }
    }

    /**
     * Handles the event where user right-clicks on a person card.
     */
    public void handleContextMenu() throws CommandException, ParseException {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            try {
                int personToDeleteIndex = personListView.getSelectionModel().getSelectedIndex() + 1;
                String deleteCommand = "delete " + personToDeleteIndex;
                CommandResult commandResult = logic.execute(deleteCommand);
                UiManager.getMainWindow().getResultDisplay().setFeedbackToUser(commandResult.getFeedbackToUser());
            } catch (ParseException | CommandException e) {
                UiManager.getMainWindow().getResultDisplay().setFeedbackToUser(e.getMessage());
            }
        });
        contextMenu.getItems().addAll(delete);
        personListView.setContextMenu(contextMenu);
    }

    /**
     * Gets person list view.
     *
     * @return the person list view
     */
    public ListView<Person> getPersonListView() {
        return this.personListView;
    }
}
