package tfifteenfour.clipboard.logic.predicates;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import tfifteenfour.clipboard.commons.util.StringUtil;
import tfifteenfour.clipboard.model.student.Student;

/**
 * Tests that a {@code Student}'s {@code Name} matches any of the keywords given.
 */
public class StudentNameContainsPredicate implements Predicate<Student> {
    private final List<String> keywords;

    public StudentNameContainsPredicate(String[] keywords) {
        this.keywords = Arrays.asList(keywords);
    }

    @Override
    public boolean test(Student student) {
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(student.getName().toString(), keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof StudentNameContainsPredicate // instanceof handles nulls
                && keywords.equals(((StudentNameContainsPredicate) other).keywords)); // state check
    }

}