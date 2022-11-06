package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.model.timerange.TimeRange;
import seedu.address.storage.ClassStorage;

/**
 * A list of students that enforces uniqueness between its elements and does not allow nulls.
 * A student is considered unique by comparing using {@code Student#isSameStudent(Student)}.
 * As such, adding and updating of students uses Student#isSameStudent(Student) for equality
 * to ensure that the student being added or updated is unique in terms of identity in the UniqueStudentList.
 * However, the removal of a student uses Student#equals(Object) so the student with exactly the same fields will be
 * removed.
 * Supports a minimal set of list operations.
 *
 * @see Student#isSameStudent(Student)
 */
public class UniqueStudentList implements Iterable<Student> {

    private final ObservableList<Student> internalList = FXCollections.observableArrayList();
    private final ObservableList<Student> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent student as the given argument.
     */
    public boolean contains(Student toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameStudent);
    }

    /**
     * Adds a student to the list.
     * The student must not already exist in the list.
     */
    public void add(Student toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateStudentException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the student {@code target} in the list with {@code editedStudent}.
     * {@code target} must exist in the list.
     * The student identity of {@code editedStudent} must not be the same as another existing student in the list.
     */
    public void setStudent(Student target, Student editedStudent) {
        requireAllNonNull(target, editedStudent);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new StudentNotFoundException();
        }

        if (!target.isSameStudent(editedStudent) && contains(editedStudent)) {
            throw new DuplicateStudentException();
        }

        internalList.set(index, editedStudent);
    }

    /**
     * Removes the equivalent student from the list.
     * The student must exist in the list.
     */
    public void remove(Student toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new StudentNotFoundException();
        }
    }

    public void setStudents(UniqueStudentList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code students}.
     * {@code students} must not contain duplicate students.
     */
    public void setStudents(List<Student> students) {
        requireAllNonNull(students);
        if (!studentsAreUnique(students)) {
            throw new DuplicateStudentException();
        }

        internalList.setAll(students);
    }

    /**
     * Sorts the {@code internalList} by the given {@code comparator}.
     */
    public void sortStudents(Comparator<Student> comparator) {
        requireNonNull(comparator);
        ArrayList<Student> sortedList = replaceSort(internalList, comparator);
        internalList.setAll(sortedList);
    }


    private static ArrayList<Student> replaceSort(
            ObservableList<Student> observableList, Comparator<Student> comparator) {
        ArrayList<Student> duplicatedList = new ArrayList<>();
        for (int i = 0; i < observableList.size(); i++) {
            duplicatedList.add(observableList.get(i));
        }
        duplicatedList.sort(comparator);
        return duplicatedList;
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Student> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns the current list.
     */
    public ObservableList<Student> getInternalList() {
        return internalList;
    }

    /**
     * Returns the next first available class.
     *
     * @param tr a timeRange object containing the {@code startTime}, {@code endTime} and {@code duration}.
     * @return the next first available Class.
     */
    public Class findAvailableClass(TimeRange tr, LocalTime currTime) {
        LocalDate currDate = LocalDate.now();
        List<Class> listAfterToday = internalList
                .stream()
                .filter(student -> student.getAClass().startTime != null
                        && student.getAClass().endTime != null
                        && student.getAClass().date != null
                        && student.getAClass().date.compareTo(currDate) > 0)
                .sorted(Student::compareToByClassAsc)
                .map((element) -> element.getAClass())
                .collect(Collectors.toList());
        List<Class> listSameDay = internalList
                .stream()
                .filter(student -> student.getDisplayedClass().startTime != null
                        && student.getDisplayedClass().endTime != null
                        && student.getDisplayedClass().date != null
                        && student.getDisplayedClass().date.compareTo(currDate) == 0)
                .sorted(Student::compareToByClassAsc)
                .map((element) -> element.getDisplayedClass())
                .collect(Collectors.toList());
        List<Class> list = Stream.concat(listSameDay.stream(), listAfterToday.stream())
                .collect(Collectors.toList());

        Class newClass = null;


        if (list.size() == 0) {
            // Case where there is only no classes
            LocalTime startTime = tr.startTimeRange;
            LocalTime endTime = startTime.plusMinutes(tr.duration);
            // Link to design: https://arc.net/e/B15E0B60-817D-4B24-8397-FC0E0B37F8C1
            if (currTime.compareTo(tr.endTimeRange) >= 0) {
                // this is the situation where the current time is after and including the endTimeRange
                newClass = new Class(currDate.plusDays(1), startTime, endTime);
            } else if (currTime.compareTo(tr.startTimeRange) <= 0) {
                // this is the situation where the current time is before the startTimeRange
                newClass = new Class(currDate, startTime, endTime);
            } else {
                // this is the situation where the current time is after the startTimeRange and before the endTimeRange
                LocalTime startTimeFromCurrTime = currTime;
                LocalTime endTimeFromCurrTime = startTimeFromCurrTime.plusMinutes(tr.duration);

                if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0) {
                    newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                } else {
                    newClass = new Class(currDate.plusDays(1), startTime, endTime);
                }
            }

            return newClass;
        } else if (list.size() == 1) {
            // Link to design for 1 class: https://arc.net/e/4F0065DB-C858-4B6D-91D0-50B2879AC26F
            return findAvailableClassWithSingleRecord(tr, currDate, list, currTime);
        }


        // Link to design for more than 2 classes: https://arc.net/e/8D5B34DF-70C5-4A22-8D26-4BFC6CEEABC5
        for (int i = 0; i < list.size(); i++) {
            Class aFirstClass = list.get(i);

            if (i == list.size() - 1) {
                /*


                THIS NEEDS TO BE FIXED

                 */
                /*
                    if the list.size() - 1, that means that you are only looking at the last element in the list.
                    in which case, you are looking at a few cases
                    Case 1: The startTimeRange is before the startTime of the class, in which case, you should
                            create a class from the start time and check to see if it exceeds the startTime of the
                            original class.
                    Case 2: If the startTimeRange is not before the startTime of the class, then it is either the same
                            or after the class. So you can try to create a class, from the end of the class.
                    Case 3: If it exceeds the endTimeRange then you look at the next day, but will be handled by next
                            iteration
                 */
                if (currTime.compareTo(tr.endTimeRange) >= 0 && aFirstClass.date.equals(currDate)) {
                    // you want to skip all the classes on the same day if currTime is outside the time window
                    break;
                }

                boolean isFirstClassEndOfDay = aFirstClass.endTime.equals(LocalTime.of(0, 0));
                if (isFirstClassEndOfDay) {
                    break;
                }

                Class previousClass = list.get(i - 1);
                boolean hasConflictWithPreviousClass = previousClass.endTime.until(
                        aFirstClass.startTime, ChronoUnit.MINUTES) < tr.duration
                        && previousClass.date.equals(aFirstClass.date);
                if (!hasConflictWithPreviousClass && tr.startTimeRange.compareTo(aFirstClass.startTime) < 0) {
                    newClass = new Class(aFirstClass.date, tr.startTimeRange,
                            tr.startTimeRange.plusMinutes(tr.duration));
                    assert newClass.endTime != null;
                    if (newClass.endTime.compareTo(aFirstClass.startTime) <= 0) {
                        break;
                    }
                }
                newClass = new Class(aFirstClass.date, aFirstClass.endTime,
                        aFirstClass.endTime.plusMinutes(tr.duration));
                assert newClass.endTime != null;
                if (newClass.endTime.compareTo(tr.endTimeRange) <= 0) {
                    break;
                } else {
                    assert aFirstClass.date != null;
                    newClass = new Class(aFirstClass.date.plusDays(1),
                            tr.startTimeRange, tr.startTimeRange.plusMinutes(tr.duration));
                }
                break;
            }
            if (currTime.compareTo(tr.endTimeRange) >= 0 && aFirstClass.date.equals(currDate)) {
                // you want to skip all the classes on the same day if currTime is outside the time window
                continue;
            }
            Class aSecondClass = list.get(i + 1);

            LocalTime startTimeFromTr = tr.startTimeRange;
            LocalTime endTimeFromTr = startTimeFromTr.plusMinutes(tr.duration);
            LocalTime startTimeFromFirstClass = aFirstClass.endTime;
            LocalTime endTimeFromFirstClass = startTimeFromFirstClass.plusMinutes(tr.duration);
            LocalTime startTimeFromSecondClass = aSecondClass.endTime;
            LocalTime endTimeFromSecondClass = startTimeFromSecondClass.plusMinutes(tr.duration);
            LocalTime startTimeFromCurrTime = currTime;
            LocalTime endTimeFromCurrTime = startTimeFromCurrTime.plusMinutes(tr.duration);

            Class aThirdClass = i + 2 == list.size() ? null : list.get(i + 2);
            boolean isNotClashWithThirdClass = aThirdClass == null
                    ? true
                    : endTimeFromSecondClass.compareTo(aThirdClass.startTime) <= 0;
            boolean isNotClashWithThirdClassFromCurrTime = aThirdClass == null
                    ? true
                    : endTimeFromCurrTime.compareTo(aThirdClass.startTime) <= 0;

            // boolean which checks whether the endTime is at the end of the day, it if it, then it is at the end of
            // the day and no timeRange can be larger than it
            boolean isSecondClassEndOfDay = aSecondClass.endTime.equals(LocalTime.of(0, 0));
            boolean isFirstClassEndOfDay = aFirstClass.endTime.equals(LocalTime.of(0, 0));

            if (aFirstClass.date.equals(aSecondClass.date)) {
                if (isFirstClassEndOfDay || isSecondClassEndOfDay) {
                    continue;
                }

                if (aFirstClass.date.equals(currDate)) {
                    if (currTime.compareTo(tr.startTimeRange) <= 0 && i == 0) {
                        // before the tr.startTimeRange
                        if (isGapBetweenClassesLargerThanDuration(aFirstClass, aSecondClass, tr.duration)) {
                            // the gap is larger than the duration, so no need to check whether if
                            // endTimeFromFirstClass.compareTo(startTimeFromSecondClass) <= 0
                            if (tr.startTimeRange.compareTo(aSecondClass.endTime) >= 0) {
                                newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                            } else if (aSecondClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0 && isNotClashWithThirdClass) {
                                    newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                }
                            } else if (aSecondClass.startTime.compareTo(tr.startTimeRange) > 0
                                    && aFirstClass.endTime.compareTo(tr.startTimeRange) <= 0) {
                                if (endTimeFromTr.compareTo(aSecondClass.startTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                                }
                            } else if (aFirstClass.endTime.compareTo(tr.startTimeRange) > 0
                                    && aFirstClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                newClass = new Class(currDate, startTimeFromFirstClass, endTimeFromFirstClass);
                            } else if (aFirstClass.startTime.compareTo(tr.startTimeRange) > 0) {
                                if (endTimeFromTr.compareTo(aFirstClass.startTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                                } else {
                                    newClass = new Class(currDate, startTimeFromFirstClass, endTimeFromFirstClass);
                                }
                            }
                        } else {
                            // view the class as a single class since there is no way there can be a slot between the 2
                            Class tempClass = new Class(aFirstClass.date, aFirstClass.startTime, aSecondClass.endTime);
                            boolean isTempClassEndOfDay = tempClass.endTime.equals(LocalTime.of(0, 0));
                            if (isTempClassEndOfDay) {
                                continue;
                            }

                            if (tr.startTimeRange.compareTo(tempClass.endTime) >= 0 && isNotClashWithThirdClass) {
                                newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                            } else if (tempClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                        && isNotClashWithThirdClass) {
                                    newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                }
                            } else if (tempClass.startTime.compareTo(tr.startTimeRange) > 0) {
                                if (endTimeFromTr.compareTo(tempClass.startTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                                } else {
                                    if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClass) {
                                        newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                    }
                                }
                            } else if (tempClass.endTime.compareTo(tr.endTimeRange) >= 0) {
                                if (endTimeFromTr.compareTo(tempClass.startTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                                }
                            }
                        }
                    } else if (currTime.compareTo(tr.startTimeRange) > 0 && currTime.compareTo(tr.endTimeRange) < 0) {
                        if (isGapBetweenClassesLargerThanDuration(aFirstClass, aSecondClass, tr.duration)) {
                            // the gap is larger is smaller than the duration, so need to check whether if
                            // endTimeFromFirstClass.compareTo(startTimeFromSecondClass) <= 0
                            if (tr.startTimeRange.compareTo(aSecondClass.endTime) >= 0) {
                                newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                            } else if (aSecondClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                if (currTime.compareTo(aSecondClass.endTime) <= 0) {
                                    if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClass) {
                                        newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                    }
                                } else {
                                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClassFromCurrTime) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                }
                            } else if (aSecondClass.startTime.compareTo(tr.startTimeRange) > 0
                                    && aFirstClass.endTime.compareTo(tr.startTimeRange) <= 0) {
                                if (currTime.compareTo(aSecondClass.startTime) < 0) {
                                    if (endTimeFromCurrTime.compareTo(aSecondClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                } else if (currTime.compareTo(aSecondClass.endTime) <= 0) {
                                    if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClass) {
                                        newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                    }
                                } else {
                                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClassFromCurrTime) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                }
                            } else if (aFirstClass.endTime.compareTo(tr.startTimeRange) > 0
                                    && aFirstClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                if (currTime.compareTo(aFirstClass.startTime) == 0
                                        && currTime.compareTo(aFirstClass.endTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromFirstClass, endTimeFromFirstClass);
                                } else if (currTime.compareTo(aSecondClass.startTime) <= 0) {
                                    if (endTimeFromCurrTime.compareTo(aSecondClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                }
                            } else if (aFirstClass.startTime.compareTo(tr.startTimeRange) > 0) {
                                if (currTime.compareTo(aFirstClass.startTime) < 0) {
                                    if (endTimeFromCurrTime.compareTo(aFirstClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                } else if (currTime.compareTo(aFirstClass.startTime) >= 0
                                        && currTime.compareTo(aFirstClass.endTime) <= 0) {
                                    newClass = new Class(currDate, startTimeFromFirstClass, endTimeFromFirstClass);
                                } else if (currTime.compareTo(aFirstClass.endTime) > 0
                                        && currTime.compareTo(aSecondClass.startTime) < 0) {
                                    if (endTimeFromCurrTime.compareTo(aFirstClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                } else {
                                    // NEED TO HANDLE CASE WHERE IT IS AFTER THE SECOND CLASS??
                                }
                            }
                        } else {
                            // view the class as a single class since there is no way there can be a slot between the 2
                            Class tempClass = new Class(aFirstClass.date, aFirstClass.startTime, aSecondClass.endTime);

                            if (tr.startTimeRange.compareTo(tempClass.endTime) >= 0) {
                                if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0
                                        && isNotClashWithThirdClassFromCurrTime) {
                                    newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                }
                            } else if (tempClass.startTime.compareTo(tr.startTimeRange) <= 0) {
                                if (currTime.compareTo(tempClass.endTime) < 0
                                        && currTime.compareTo(tempClass.startTime) > 0) {
                                    if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClass) {
                                        newClass = new Class(currDate, startTimeFromSecondClass, endTimeFromSecondClass);
                                    }
                                } else {
                                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClassFromCurrTime) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                }
                            } else if (tempClass.startTime.compareTo(tr.startTimeRange) > 0) {
                                if (currTime.compareTo(tempClass.startTime) < 0) {
                                    if (endTimeFromCurrTime.compareTo(tempClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                } else if (currTime.compareTo(tempClass.endTime) > 0) {
                                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClassFromCurrTime) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                } else {
                                    if (endTimeFromSecondClass.compareTo(tr.endTimeRange) <= 0
                                            && isNotClashWithThirdClass) {
                                        newClass = new Class(currDate,startTimeFromSecondClass, endTimeFromSecondClass);
                                    }
                                }
                            } else if (tempClass.endTime.compareTo(tr.endTimeRange) >= 0) {
                                if (currTime.compareTo(tempClass.startTime) < 0) {
                                    if (endTimeFromCurrTime.compareTo(tempClass.startTime) <= 0) {
                                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    /*
                     * That means they are on the same day
                     * 1st case: When they are side by side. Since the case where it is before the class has been handled,
                     *           we try finding a slot after the end of the secondClass.
                     * 2nd case: When there is a gap, but it is not big enough. If there is a gap, then it is actually
                     *           the same situation as the first case so, it becomes <= tr.duration rather than just == 0
                     *           for the initial first case.
                     * 3rd case: When there is a gap just nice or too big
                     */
                    assert aFirstClass.endTime != null;
                    assert aSecondClass.startTime != null;
                    if (aFirstClass.endTime.until(aSecondClass.startTime, ChronoUnit.MINUTES) > tr.duration) {
                        // you are now handling the 3rd case, the 1st case does not matter since if it is outside of the
                        // duration, it is the next iteration's problem
                        newClass = new Class(aFirstClass.date, aFirstClass.endTime,
                                aFirstClass.endTime.plusMinutes(tr.duration));
                        break;
                    }
                }
            } else {
                /*
                 * That means they are not on the same day.
                 * Case 1: In which case, you try creating a class which starts after the end of the same class,
                 *         since there wouldn't be a conflict between the 2 class dates.
                 * Case 2: If there are a number of days gap, more than 1, and the timing clashes, then you can
                 *         definitely go to the next day.
                 */
                assert aFirstClass.endTime != null;
                if (tr.startTimeRange.plusMinutes(tr.duration).compareTo(aFirstClass.startTime) <= 0) {
                    newClass = new Class(aFirstClass.date, tr.startTimeRange,
                            tr.startTimeRange.plusMinutes(tr.duration));
                } else if (aFirstClass.endTime.plusMinutes(tr.duration).compareTo(tr.endTimeRange) <= 0) {
                    newClass = new Class(aFirstClass.date, aFirstClass.endTime,
                            aFirstClass.endTime.plusMinutes(tr.duration));
                    break;
                } else if (aFirstClass.date.until(aSecondClass.date, ChronoUnit.DAYS) > 1) {
                    newClass = new Class(aFirstClass.date.plusDays(1), tr.startTimeRange,
                            tr.startTimeRange.plusMinutes(tr.duration));
                    break;
                }
            }

            if (newClass != null) {
                break;
            }


            //
            //            // check whether a class before the first class is possible
            //            Class fromTrStartTime = new Class(aFirstClass.date, tr.startTimeRange,
            //                    tr.startTimeRange.plusMinutes(tr.duration));
            //            if (!ClassStorage.hasConflict(fromTrStartTime.startTime, fromTrStartTime.endTime,
            //                    aFirstClass.startTime, aFirstClass.endTime)
            //                    && !ClassStorage.hasConflict(fromTrStartTime.startTime, fromTrStartTime.endTime,
            //                    aSecondClass.startTime, aSecondClass.endTime)) {
            //                assert fromTrStartTime.endTime != null;
            //                newClass = fromTrStartTime;
            //            }
            //
            //            assert aFirstClass.date != null;
            //            if (aFirstClass.date.equals(aSecondClass.date)) {
            //                /*
            //                 * That means they are on the same day
            //                 * 1st case: When they are side by side. Since the case where it is before the class has been handled,
            //                 *           we try finding a slot after the end of the secondClass.
            //                 * 2nd case: When there is a gap, but it is not big enough. If there is a gap, then it is actually
            //                 *           the same situation as the first case so, it becomes <= tr.duration rather than just == 0
            //                 *           for the initial first case.
            //                 * 3rd case: When there is a gap just nice or too big
            //                 */
            //                assert aFirstClass.endTime != null;
            //                assert aSecondClass.startTime != null;
            //                if (aFirstClass.endTime.until(aSecondClass.startTime, ChronoUnit.MINUTES) > tr.duration) {
            //                    // you are now handling the 3rd case, the 1st case does not matter since if it is outside of the
            //                    // duration, it is the next iteration's problem
            //                    newClass = new Class(aFirstClass.date, aFirstClass.endTime,
            //                            aFirstClass.endTime.plusMinutes(tr.duration));
            //                    break;
            //                }
            //            }
            //            else {
            //                /*
            //                 * That means they are not on the same day.
            //                 * Case 1: In which case, you try creating a class which starts after the end of the same class,
            //                 *         since there wouldn't be a conflict between the 2 class dates.
            //                 * Case 2: If there are a number of days gap, more than 1, and the timing clashes, then you can
            //                 *         definitely go to the next day.
            //                 */
            //                assert aFirstClass.endTime != null;
            //                newClass = new Class(aFirstClass.date, aFirstClass.endTime,
            //                        aFirstClass.endTime.plusMinutes(tr.duration));
            //                assert newClass.endTime != null;
            //                if (newClass.endTime.compareTo(tr.endTimeRange) <= 0) {
            //                    break;
            //                } else {
            //                    assert newClass.date != null;
            //                    assert aSecondClass.date != null;
            //                    if (newClass.date.until(aSecondClass.date, ChronoUnit.DAYS) > 1) {
            //                        newClass = new Class(aFirstClass.date.plusDays(1), tr.startTimeRange,
            //                                tr.startTimeRange.plusMinutes(tr.duration));
            //                        break;
            //                    }
            //                }
            //            }
        }

        if (newClass == null) {
            newClass = new Class(currDate.plusDays(1), tr.startTimeRange,
                    tr.startTimeRange.plusMinutes(tr.duration));
        }

        return newClass;
    }

    private static Class findAvailableClassWithSingleRecord(TimeRange tr, LocalDate currDate, List<Class> list,
                                                            LocalTime currTime) {
        Class newClass = null;
        Class classToCompare = list.get(0);
        // When the startTimeRange is before the earliest slot
        assert classToCompare.endTime != null;
        assert classToCompare.startTime != null;

        LocalTime startTimeFromTr = tr.startTimeRange;
        LocalTime endTimeFromTr = startTimeFromTr.plusMinutes(tr.duration);
        LocalTime startTimeFromClass = classToCompare.endTime;
        LocalTime endTimeFromClass = startTimeFromClass.plusMinutes(tr.duration);
        LocalTime startTimeFromCurrTime = currTime;
        LocalTime endTimeFromCurrTime = startTimeFromCurrTime.plusMinutes(tr.duration);

        if (!classToCompare.date.equals(currDate)) {
            newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
        } else if (currTime.compareTo(tr.endTimeRange) >= 0) {
            // if the currentTime is after and including the end of the time range, just go to the next day
            newClass = new Class(currDate.plusDays(1), startTimeFromTr, endTimeFromTr);
        } else if (currTime.compareTo(tr.startTimeRange) <= 0) {
            // if the currentTime is before and including the start time range
            if (classToCompare.endTime.compareTo(tr.startTimeRange) <= 0) {
                // the sitaution where the endTime of the class is before the tr.startTimeRange
                newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
            } else if (classToCompare.endTime.compareTo(tr.startTimeRange) > 0
                    && classToCompare.endTime.compareTo(tr.endTimeRange) < 0
                    && classToCompare.startTime.compareTo(tr.startTimeRange) <= 0) {
                // getting the case where the endTime is after the tr.startTimeRange but satisfies the above conditions
                if (endTimeFromClass.compareTo(tr.endTimeRange) <= 0) {
                    newClass = new Class(currDate, startTimeFromClass, endTimeFromClass);
                }
            } else if (classToCompare.endTime.compareTo(tr.startTimeRange) > 0
                    && classToCompare.endTime.compareTo(tr.endTimeRange) < 0
                    && classToCompare.startTime.compareTo(tr.startTimeRange) > 0) {
                if (endTimeFromTr.compareTo(classToCompare.startTime) <= 0) {
                    // trying to fit a slot from the start of the class
                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                } else if (classToCompare.endTime.plusMinutes(tr.duration).compareTo(tr.endTimeRange) <= 0) {
                    // fit a class after the end of the class
                    newClass = new Class(currDate, startTimeFromClass, endTimeFromClass);
                }
            } else if (classToCompare.startTime.compareTo(tr.startTimeRange) > 0) {
                if (endTimeFromTr.compareTo(classToCompare.startTime) <= 0) {
                    newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                }
            } else if (classToCompare.startTime.compareTo(tr.endTimeRange) >= 0) {
                newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
            } else {
                newClass = new Class(currDate.plusDays(1), startTimeFromTr, endTimeFromTr);
            }
        } else if (currTime.compareTo(tr.startTimeRange) > 0 && currTime.compareTo(tr.endTimeRange) < 0) {
            // this means that the currentTime is between the start and end time range
            if (classToCompare.endTime.compareTo(tr.startTimeRange) <= 0) {
                if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0) {
                    newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                }
            } else if (classToCompare.endTime.compareTo(tr.startTimeRange) > 0
                    && classToCompare.endTime.compareTo(tr.endTimeRange) < 0
                    && classToCompare.startTime.compareTo(tr.startTimeRange) <= 0) {
                if (currTime.compareTo(classToCompare.endTime) <= 0) {
                    newClass = new Class(currDate, startTimeFromClass, endTimeFromClass);
                } else {
                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0) {
                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                    }
                }
            } else if (classToCompare.endTime.compareTo(tr.endTimeRange) < 0
                    && classToCompare.startTime.compareTo(tr.startTimeRange) > 0) {
                if (currTime.compareTo(classToCompare.startTime) < 0) {
                    if (endTimeFromTr.compareTo(classToCompare.startTime) <= 0) {
                        newClass = new Class(currDate, startTimeFromTr, endTimeFromTr);
                    }
                } else if (currTime.compareTo(classToCompare.startTime) >= 0
                    && currTime.compareTo(classToCompare.endTime) <= 0) {
                    if (endTimeFromClass.compareTo(tr.endTimeRange) <= 0) {
                        newClass = new Class(currDate, startTimeFromClass, endTimeFromClass);
                    }
                } else if (currTime.compareTo(classToCompare.endTime) > 0) {
                    if (endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0) {
                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                    }
                }
            } else if (classToCompare.startTime.compareTo(tr.startTimeRange) > 0
                    && classToCompare.endTime.compareTo(tr.endTimeRange) >= 0) {
                if (currTime.compareTo(classToCompare.startTime) < 0) {
                    if (endTimeFromCurrTime.compareTo(classToCompare.startTime) <= 0
                        && endTimeFromCurrTime.compareTo(tr.endTimeRange) <= 0) {
                        newClass = new Class(currDate, startTimeFromCurrTime, endTimeFromCurrTime);
                    }
                }
            } else {
                newClass = new Class(currDate.plusDays(1), tr.startTimeRange,
                        tr.startTimeRange.plusMinutes(tr.duration));
            }
        } else {
            newClass = new Class(currDate.plusDays(1), tr.startTimeRange,
                    tr.startTimeRange.plusMinutes(tr.duration));
        }

        if (newClass == null) {
            newClass = new Class(currDate.plusDays(1), tr.startTimeRange,
                    tr.startTimeRange.plusMinutes(tr.duration));
        }

        return newClass;
    }

    private static boolean isGapBetweenClassesLargerThanDuration(Class firstClass, Class secondClass,
                                                                  Integer duration) {
        return firstClass.endTime.until(secondClass.startTime, ChronoUnit.MINUTES) >= duration;
    }

    @Override
    public Iterator<Student> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueStudentList // instanceof handles nulls
                        && internalList.equals(((UniqueStudentList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code students} contains only unique students.
     */
    private boolean studentsAreUnique(List<Student> students) {
        for (int i = 0; i < students.size() - 1; i++) {
            for (int j = i + 1; j < students.size(); j++) {
                if (students.get(i).isSameStudent(students.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
