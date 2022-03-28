package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COURSE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRICCARD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEGRAM;

import seedu.address.logic.commands.GrabCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class GrabCommandParser implements Parser<GrabCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the GrabCommand
     * and returns an GrabCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public GrabCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_COURSE, PREFIX_MATRICCARD, PREFIX_TELEGRAM);

        int numOfArgument = argMultimap.getSize();

        if (numOfArgument > 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GrabCommand.MESSAGE_TOO_MANY_ARGUMENT));
        }

        if (numOfArgument == 0) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GrabCommand.MESSAGE_USAGE));
        }

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_NAME).get();
            return new GrabCommand(PREFIX_NAME, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_PHONE).get();
            return new GrabCommand(PREFIX_PHONE, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_EMAIL).get();
            return new GrabCommand(PREFIX_EMAIL, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_NAME).get();
            return new GrabCommand(PREFIX_ADDRESS, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_COURSE).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_COURSE).get();
            return new GrabCommand(PREFIX_COURSE, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_MATRICCARD).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_MATRICCARD).get();
            return new GrabCommand(PREFIX_MATRICCARD, indexToBeGrabbed);
        } else if (argMultimap.getValue(PREFIX_TELEGRAM).isPresent()) {
            String indexToBeGrabbed = argMultimap.getValue(PREFIX_TELEGRAM).get();
            return new GrabCommand(PREFIX_TELEGRAM, indexToBeGrabbed);
        } else {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GrabCommand.MESSAGE_USAGE));
        }
    }
}