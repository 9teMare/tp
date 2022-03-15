package seedu.address.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Window that shows all tag related information. It shows the full tag current tag list and
 * provides functionalities such as adding new tags and delete multiple tags in one go.
 */
public class AddTagWindow extends UiPart<Stage> {

    private static final String FXML = "AddTagWindow.fxml";
    private Logic logic;
    private ResultDisplay resultDisplay;
    private ArrayList<String> selectedTags = new ArrayList<>();
    private boolean selectButtonPressed;

    @FXML
    private TextField tagNameInput;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private FlowPane tagsPane;

    /**
     * Creates a new AddTagWindow.
     *
     * @param root Stage to use as the root of the AddTagWindow.
     */
    public AddTagWindow(Stage root, Logic logic) {
        super(FXML, root);
        this.logic = logic;
        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());
        setupTags();
        selectButtonPressed = false;
    }

    /**
     * Creates a new AddTagWindow.
     */
    public AddTagWindow(Logic logic) throws FileNotFoundException {
        this(new Stage(), logic);
    }

    /**
     * Sets up the tags into tag bar for display.
     */
    private void setupTags() {
        List<Tag> tagList = logic.getModel().getTagList();
        for (int t = 0; t < tagList.size(); t++) {
            tagsPane.getChildren().add(new TagBar(tagList.get(t), t).getRoot());
        }
        selectedTags.clear();
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     * <ul>
     *     <li>
     *         if this method is called on a thread other than the JavaFX Application Thread.
     *     </li>
     *     <li>
     *         if this method is called during animation or layout processing.
     *     </li>
     *     <li>
     *         if this method is called on the primary stage.
     *     </li>
     *     <li>
     *         if {@code dialogStage} is already showing.
     *     </li>
     * </ul>
     */
    public void show() {
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Handles the event where 'select' button is pressed.
     */
    @FXML
    private void selectButtonHandler() {
        selectButtonPressed = !selectButtonPressed;
        tagsPane.getChildren().clear();
        setupTags();
    }

    /**
     * Handles the event where 'Add' button is pressed.
     */
    @FXML
    private void addTagHandler() throws CommandException, ParseException {
        String tagName = tagNameInput.getText();
        String fullCommand = "add_tag t/" + tagName;
        try {
            CommandResult result = logic.execute(fullCommand);
            resultDisplay.setFeedbackToUser(result.getFeedbackToUser());
            tagsPane.getChildren().clear();
            setupTags();
            tagNameInput.clear();
        } catch (ParseException | CommandException e) {
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * Handles the event where 'Delete' button is pressed.
     */
    @FXML
    private void handleDelete() throws CommandException, ParseException {
        try {
            if (selectedTags.isEmpty()) {
                resultDisplay.setFeedbackToUser("No tags selected.");
            } else {
                Collections.sort(selectedTags);
                int deleteTagCount = selectedTags.size();
                for (int i = 0; i < deleteTagCount; i++) {
                    System.out.println(selectedTags.get(i));
                    int idxToDelete = Integer.parseInt(selectedTags.get(i)) - i + 1;
                    String fullDeleteCommand = "delete_tag " + idxToDelete;
                    System.out.println(fullDeleteCommand);
                    logic.execute(fullDeleteCommand);
                }
                tagsPane.getChildren().clear();
                selectButtonHandler();
                resultDisplay.setFeedbackToUser("Successfully deleted " + deleteTagCount + " tags.");
                selectedTags.clear();
            }
        } catch (ParseException | CommandException e) {
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * The Tag Bar. Includes the tag information such as name and index.
     */
    private class TagBar extends UiPart<Region> {
        private static final String FXML = "Tag.fxml";
        private Tag tag;
        private int tagIndex;

        @FXML
        private ToggleButton tagBar;

        /**
         * Creates a {@code TagBar}
         */
        public TagBar(Tag tag, int t) {
            super(FXML);
            this.tag = tag;
            tagBar.setText(tag.getTagName());
            tagBar.setDisable(!selectButtonPressed);
            tagIndex = t;
        }

        /**
         * Handles the event where a tag bar is selected
         */
        @FXML
        private void handleSelect() {
            if (tagBar.isSelected()) {
                selectedTags.add(Integer.toString(tagIndex));
            } else {
                selectedTags.remove((Object) tagIndex);
            }
        }
    }



}
