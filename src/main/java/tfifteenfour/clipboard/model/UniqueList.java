package tfifteenfour.clipboard.model;

import static java.util.Objects.requireNonNull;
import static tfifteenfour.clipboard.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import tfifteenfour.clipboard.model.course.ElementNotFoundException;
import tfifteenfour.clipboard.model.course.UniqueCoursesList;
import tfifteenfour.clipboard.model.course.exceptions.DuplicateGroupException;

public abstract class UniqueList<T> implements Iterable<T> {

    protected final ObservableList<T> internalList = FXCollections.observableArrayList();
    protected final ObservableList<T> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    protected final FilteredList<T> filteredList = new FilteredList<>(internalList);

    /**
     * Returns true if the list contains an equivalent course as the given argument.
     */
    public abstract boolean contains(T toCheck);

    /**
     * Adds a course to the list.
     * The course must not already exist in the list.
     */

    public abstract void add(T toAdd);
    public abstract void set(T target, T edited);
	protected abstract boolean elementsAreUnique(List<T> items);

    public void remove(T toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new ElementNotFoundException();
        }
    }

    public void setInternalList(UniqueList<T> replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    public void setInternalList(List<T> courses) {
        requireAllNonNull(courses);
        if (!elementsAreUnique(courses)) {
            throw new DuplicateGroupException();
        }

        internalList.setAll(courses);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<T> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns the backing list as a modifiable {@code ObservableList}.
     */
    public ObservableList<T> asModifiableObservableList() {
        return internalList;
    }

    public ObservableList<T> asUnmodifiableFilteredList() {
        return FXCollections.unmodifiableObservableList(filteredList);
    }

	public void updateFilterPredicate(Predicate<T> predicate) {
		this.filteredList.setPredicate(predicate);
	}

    @Override
    public Iterator<T> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueCoursesList // instanceof handles nulls
                        && internalList.equals(((UniqueCoursesList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }


}
