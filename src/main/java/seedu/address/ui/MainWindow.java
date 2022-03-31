package seedu.address.ui;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.ui.general.GeneralDisplay;
import seedu.address.ui.general.Profile;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static Profile profile;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private PersonListPanel personListPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;
    private AddTagWindow addTagWindow;
    private AddProfileWindow addProfileWindow;
    private GeneralDisplay generalDisplay;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane searchBarPlaceholder;

    @FXML
    private StackPane generalDisplayPlaceholder;

    @FXML
    private Menu addMenu;

    @FXML
    private Menu newTagMenu;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) throws FileNotFoundException {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
        addTagWindow = new AddTagWindow(logic);
        addTagWindow.getRoot().initOwner(primaryStage);
        addTagWindow.getRoot().initModality(Modality.WINDOW_MODAL);
        addProfileWindow = new AddProfileWindow(logic);
        addMenu.setVisible(false);
        newTagMenu.setVisible(false);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        personListPanel = new PersonListPanel(logic.getFilteredPersonList(), logic);
        personListPanelPlaceholder.getChildren().add(personListPanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());

        SearchBar searchBar = new SearchBar();
        searchBarPlaceholder.getChildren().add(searchBar.getRoot());

        generalDisplay = new GeneralDisplay(logic);
        generalDisplayPlaceholder.getChildren().add(generalDisplay.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleAddTag() {
        if (!addTagWindow.isShowing()) {
            addTagWindow.show();
        } else {
            addTagWindow.focus();
        }
    }

    /**
     * Opens the add profile window or focuses on it if it's already opened
     */
    @FXML
    public void handleAddProfile() {
        if (!addProfileWindow.isShowing()) {
            addProfileWindow.show();
        } else {
            addProfileWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());
            System.out.println(logic.getModel().isMouseUxEnabled());
            if (logic.getModel().isMouseUxEnabled()) {
                addMenu.setVisible(true);
                newTagMenu.setVisible(true);
            } else {
                addMenu.setVisible(false);
                newTagMenu.setVisible(false);
            }

            if (commandResult.isRemoveProfile() && this.generalDisplay.getProfile().getPerson() != null
                    && this.generalDisplay.getProfile().getPerson().isSamePerson(commandResult.getPerson())) {
                this.generalDisplay.resetProfile();
            }

            if (commandResult.isShowProfile()) {
                this.generalDisplay.setProfile(commandResult.getPerson());
                this.personListPanel.getPersonListView().scrollTo(this.generalDisplay
                        .getProfile().getIndex().getZeroBased());
                this.personListPanel.getPersonListView().getSelectionModel().select(this.generalDisplay
                        .getProfile().getIndex().getZeroBased());
            }

            if (commandResult.isShowTagList()) {
                this.generalDisplay.setTagList(logic.getModel().getTagList());
            }

            if (commandResult.isSwitchTheme()) {
                commandResult.getTheme().applyTheme(primaryStage);
            }

            if (commandResult.isShowGrabResult()) {
                this.generalDisplay.setGrabResult(commandResult.getGrabResult());
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    public GeneralDisplay getGeneralDisplay() {
        return this.generalDisplay;
    }

    public PersonListPanel getPersonListPanel() {
        return this.personListPanel;
    }

    public ResultDisplay getResultDisplay() {
        return this.resultDisplay;
    }

    public Logic getLogic() {
        return this.logic;
    }
}
