package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COURSE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICCARD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_TAG;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddTagCommand;
import seedu.address.logic.commands.AttachTagCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ClearEmptyTagCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteTagCommand;
import seedu.address.logic.commands.DetachTagCommand;
import seedu.address.logic.commands.DisableMouseUxCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EnableMouseUxCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GrabCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListTagCommand;
import seedu.address.logic.commands.ProfileCommand;
import seedu.address.logic.commands.RemarkTagCommand;
import seedu.address.logic.commands.SwitchThemeCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.TagBuilder;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addTag() throws Exception {
        Tag tag = new TagBuilder().build();
        AddTagCommand command = (AddTagCommand) parser.parseCommand(
                AddTagCommand.COMMAND_WORD + " " + PREFIX_TAG + tag.tagName);
        assertEquals(new AddTagCommand(tag), command);
    }

    @Test
    public void parseCommand_attachTag() throws Exception {
        Tag tag = new TagBuilder().build();
        AttachTagCommand command = (AttachTagCommand) parser.parseCommand(
                AttachTagCommand.COMMAND_WORD + " " + PREFIX_TAG + tag.tagName
                        + " " + PREFIX_INDEX + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new AttachTagCommand(tag, INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteTag() throws Exception {
        DeleteTagCommand command = (DeleteTagCommand) parser.parseCommand(
                DeleteTagCommand.COMMAND_WORD + " " + INDEX_FIRST_TAG.getOneBased());
        assertEquals(new DeleteTagCommand(INDEX_FIRST_TAG), command);
    }

    @Test
    public void parseCommand_detatchTag() throws Exception {
        Tag tag = new TagBuilder().build();
        DetachTagCommand command = (DetachTagCommand) parser.parseCommand(
                DetachTagCommand.COMMAND_WORD + " " + PREFIX_TAG + tag.tagName
                        + " " + PREFIX_INDEX + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DetachTagCommand(tag, INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        System.out.println(person);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        System.out.println(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_listTag() throws Exception {
        assertTrue(parser.parseCommand(ListTagCommand.COMMAND_WORD) instanceof ListTagCommand);
        assertTrue(parser.parseCommand(ListTagCommand.COMMAND_WORD + " 3") instanceof ListTagCommand);
    }

    @Test
    public void parseCommand_filter() throws Exception {
        Tag tag = new TagBuilder().build();
        assertTrue(parser.parseCommand(FilterCommand.COMMAND_WORD + " " + tag.tagName) instanceof FilterCommand);
    }

    @Test
    public void parseCommand_switchTheme() throws Exception {
        assertTrue(parser.parseCommand(SwitchThemeCommand.COMMAND_WORD + " light") instanceof SwitchThemeCommand);
        assertTrue(parser.parseCommand(SwitchThemeCommand.COMMAND_WORD + " dark") instanceof SwitchThemeCommand);
    }

    @Test
    public void parseCommand_profile() throws Exception {
        ProfileCommand command = (ProfileCommand) parser.parseCommand(
                ProfileCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ProfileCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_remarkTag() throws Exception {
        Tag tag = new TagBuilder().build();
        Command command = parser.parseCommand(
                RemarkTagCommand.COMMAND_WORD + " " + PREFIX_TAG + tag.tagName + " " + PREFIX_REMARK + " ");
        assertTrue(command instanceof RemarkTagCommand);
    }

    @Test
    public void parseCommand_grab() throws Exception {
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_NAME) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_ADDRESS) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_EMAIL) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_PHONE) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_COURSE) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_MATRICCARD) instanceof GrabCommand);
        assertTrue(parser.parseCommand(GrabCommand.COMMAND_WORD + " " + PREFIX_TELEGRAM) instanceof GrabCommand);

        //parse with a tag
        Tag tag = new TagBuilder().build();
        Command command = parser.parseCommand(GrabCommand.COMMAND_WORD
                + " " + PREFIX_TELEGRAM + " " + PREFIX_TAG + tag.tagName);
        assertTrue(command instanceof GrabCommand);
    }

    @Test
    public void parseCommand_clearEmptyTag() throws Exception {
        assertTrue(parser.parseCommand(ClearEmptyTagCommand.COMMAND_WORD) instanceof ClearEmptyTagCommand);
        assertTrue(parser.parseCommand(ClearEmptyTagCommand.COMMAND_WORD + " 3") instanceof ClearEmptyTagCommand);
    }

    @Test
    public void parseCommand_enableMouseUX() throws Exception {
        assertTrue(parser.parseCommand(EnableMouseUxCommand.COMMAND_WORD) instanceof EnableMouseUxCommand);
        assertTrue(parser.parseCommand(EnableMouseUxCommand.COMMAND_WORD + " 3") instanceof EnableMouseUxCommand);
    }

    @Test
    public void parseCommand_disableMouseUX() throws Exception {
        assertTrue(parser.parseCommand(DisableMouseUxCommand.COMMAND_WORD) instanceof DisableMouseUxCommand);
        assertTrue(parser.parseCommand(DisableMouseUxCommand.COMMAND_WORD + " 3") instanceof DisableMouseUxCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
