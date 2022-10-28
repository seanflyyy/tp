package seedu.address.model.student.predicate;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.student.Student;


/**
 * Tests that a {@code Student}'s {@code Tag} matches the keywords given
 */
public class TagContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> keywords;

    public TagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Student student) {
        Set<String> set = student.getTags().stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.toSet());
        String tagsString = String.join(" ", set);
        return keywords.stream()
                .allMatch(keyword -> StringUtil.containsWordIgnoreCase(tagsString, keyword));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof TagContainsKeywordsPredicate // instanceof handles nulls
                && keywords.equals(((TagContainsKeywordsPredicate) other).keywords)); // state check
    }

}
