package tfifteenfour.clipboard.logic.parser;

import static tfifteenfour.clipboard.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_EMAIL;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_NAME;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_PHONE;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_REMARK;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_STUDENTID;
import static tfifteenfour.clipboard.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.stream.Stream;

import tfifteenfour.clipboard.logic.commands.addCommand.AddCourseCommand;
import tfifteenfour.clipboard.logic.commands.addCommand.AddGroupCommand;
import tfifteenfour.clipboard.logic.commands.addCommand.AddSessionCommand;
import tfifteenfour.clipboard.logic.commands.addCommand.AddStudentCommand;
import tfifteenfour.clipboard.logic.commands.deleteCommand.DeleteCommand;
import tfifteenfour.clipboard.logic.commands.deleteCommand.DeleteCourseCommand;
import tfifteenfour.clipboard.logic.parser.exceptions.ParseException;
import tfifteenfour.clipboard.model.course.Course;
import tfifteenfour.clipboard.model.course.Group;
import tfifteenfour.clipboard.model.student.Email;
import tfifteenfour.clipboard.model.student.Name;
import tfifteenfour.clipboard.model.student.Phone;
import tfifteenfour.clipboard.model.student.Remark;
import tfifteenfour.clipboard.model.student.Student;
import tfifteenfour.clipboard.model.student.StudentId;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {

        CommandTargetType deleteCommandType;

        try {
            deleteCommandType = CommandTargetType.fromString(ArgumentTokenizer.tokenizeString(args)[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParseException("Delete type missing");
        }

        switch (deleteCommandType) {
        case MODULE:
            Course course = parseCourseInfo(args);
            return new DeleteCourseCommand(course);
        case GROUP:
            Group group = parseGroupInfo(args);
            return new AddGroupCommand(group);
        case SESSION:
            return new AddSessionCommand();
        case STUDENT:
            Student student = parseStudentInfo(args);
            return new AddStudentCommand(student);
        default:
            throw new ParseException("Invalid argument for add command");
        }
    }


    private Course parseCourseInfo(String args) throws ParseException {
        String[] tokens = ArgumentTokenizer.tokenizeString(args);
        if (tokens.length != 3) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCourseCommand.MESSAGE_USAGE));
        }

        Course course = ParserUtil.parseCourse(tokens[2]);
        return course;
    }

    private Group parseGroupInfo(String args) throws ParseException {
        String[] tokens = ArgumentTokenizer.tokenizeString(args);
        if (tokens.length != 3) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddGroupCommand.MESSAGE_USAGE));
        }

        Group group = ParserUtil.parseGroup(tokens[2]);
        return group;
    }

    private Student parseStudentInfo(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenizePrefixes(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDENTID, PREFIX_REMARK, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_STUDENTID, PREFIX_PHONE, PREFIX_EMAIL)
                || !CommandTargetType.isValidAddType(argMultimap.getPreamble())) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        StudentId studentId = ParserUtil.parseStudentId(argMultimap.getValue(PREFIX_STUDENTID).get());
        // Set<Course> course = ParserUtil.parseModules(argMultimap.getAllValues(PREFIX_COURSE));
        Remark remark = new Remark("");
        // Set<Tag> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Student student = new Student(name, phone, email, studentId, remark);

        return student;
    }
    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}