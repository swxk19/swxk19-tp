package tfifteenfour.clipboard.logic.commands.findcommand;

import static java.util.Objects.requireNonNull;

import tfifteenfour.clipboard.logic.CurrentSelection;
import tfifteenfour.clipboard.logic.commands.CommandResult;
import tfifteenfour.clipboard.logic.commands.exceptions.CommandException;
import tfifteenfour.clipboard.logic.predicates.SessionNameContainsPredicate;
import tfifteenfour.clipboard.model.Model;
import tfifteenfour.clipboard.model.course.Group;

public class FindSessionCommand extends FindCommand {
	public static final String COMMAND_TYPE_WORD = "session";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " " + COMMAND_TYPE_WORD
            + ": Finds a session. "
            + "Parameters: "
            + "SESSION_SEARCH_TERM\n"
            + "Example: " + COMMAND_WORD
            + " " + COMMAND_TYPE_WORD
            + " " + "SESSON1 ";

	public static final String MESSAGE_SUCCESS = "Found %1$s results";
    private final SessionNameContainsPredicate predicate;
	private final CurrentSelection currentSelection;



    public FindSessionCommand(SessionNameContainsPredicate predicate, CurrentSelection currentSelection) {
        this.predicate = predicate;
		this.currentSelection = currentSelection;
    }
	/**
     * Executes the command and returns the result message.
     * @param model {@code Model} which the command should operate on.
     * @param currentSelection of the {@code LogicManager}.
     * @throws CommandException If an error occurs during command execution.
     */
    public CommandResult execute(Model model, CurrentSelection currentSelection) throws CommandException {
        requireNonNull(model);
		Group selectedGroup = currentSelection.getSelectedGroup();
        selectedGroup.updateFilteredSessions(predicate);

        return new CommandResult(this, String.format(MESSAGE_SUCCESS,
                selectedGroup.getUnmodifiableFilteredSessionList().size()), willModifyState);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindSessionCommand // instanceof handles nulls
                && predicate.equals(((FindSessionCommand) other).predicate));
    }
}