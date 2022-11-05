package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_INTERMEDIATE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BOB;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.model.timerange.TimeRange;
import seedu.address.testutil.StudentBuilder;

public class UniqueStudentListTest {

    private final UniqueStudentList uniqueStudentList = new UniqueStudentList();

    @Test
    public void contains_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.contains(null));
    }

    @Test
    public void contains_studentNotInList_returnsFalse() {
        assertFalse(uniqueStudentList.contains(ALICE));
    }

    @Test
    public void contains_studentInList_returnsTrue() {
        uniqueStudentList.add(ALICE);
        assertTrue(uniqueStudentList.contains(ALICE));
    }

    @Test
    public void contains_studentWithSameIdentityFieldsInList_returnsTrue() {
        uniqueStudentList.add(ALICE);
        Student editedAlice = new StudentBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_INTERMEDIATE)
                .build();
        assertTrue(uniqueStudentList.contains(editedAlice));
    }

    @Test
    public void add_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.add(null));
    }

    @Test
    public void add_duplicateStudent_throwsDuplicateStudentException() {
        uniqueStudentList.add(ALICE);
        assertThrows(DuplicateStudentException.class, () -> uniqueStudentList.add(ALICE));
    }

    @Test
    public void setStudent_nullTargetStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudent(null, ALICE));
    }

    @Test
    public void setStudent_nullEditedStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudent(ALICE, null));
    }

    @Test
    public void setStudent_targetStudentNotInList_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> uniqueStudentList.setStudent(ALICE, ALICE));
    }

    @Test
    public void setStudent_editedStudentIsSameStudent_success() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.setStudent(ALICE, ALICE);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        expectedUniqueStudentList.add(ALICE);
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudent_editedStudentHasSameIdentity_success() {
        uniqueStudentList.add(ALICE);
        Student editedAlice = new StudentBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_INTERMEDIATE)
                .build();
        uniqueStudentList.setStudent(ALICE, editedAlice);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        expectedUniqueStudentList.add(editedAlice);
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudent_editedStudentHasDifferentIdentity_success() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.setStudent(ALICE, BOB);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        expectedUniqueStudentList.add(BOB);
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudent_editedStudentHasNonUniqueIdentity_throwsDuplicateStudentException() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.add(BOB);
        assertThrows(DuplicateStudentException.class, () -> uniqueStudentList.setStudent(ALICE, BOB));
    }

    @Test
    public void remove_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.remove(null));
    }

    @Test
    public void remove_studentDoesNotExist_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> uniqueStudentList.remove(ALICE));
    }

    @Test
    public void remove_existingStudent_removesStudent() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.remove(ALICE);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudents_nullUniqueStudentList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudents((UniqueStudentList) null));
    }

    @Test
    public void setStudents_uniqueStudentList_replacesOwnListWithProvidedUniqueStudentList() {
        uniqueStudentList.add(ALICE);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        expectedUniqueStudentList.add(BOB);
        uniqueStudentList.setStudents(expectedUniqueStudentList);
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudents_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudents((List<Student>) null));
    }

    @Test
    public void setStudents_list_replacesOwnListWithProvidedList() {
        uniqueStudentList.add(ALICE);
        List<Student> studentList = Collections.singletonList(BOB);
        uniqueStudentList.setStudents(studentList);
        UniqueStudentList expectedUniqueStudentList = new UniqueStudentList();
        expectedUniqueStudentList.add(BOB);
        assertEquals(expectedUniqueStudentList, uniqueStudentList);
    }

    @Test
    public void setStudents_listWithDuplicateStudents_throwsDuplicateStudentException() {
        List<Student> listWithDuplicateStudents = Arrays.asList(ALICE, ALICE);
        assertThrows(DuplicateStudentException.class, () -> uniqueStudentList.setStudents(listWithDuplicateStudents));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueStudentList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void findAvailableClassWithSingleRecord_noClass_success() {
        // pass time
        LocalTime currTime1 = LocalTime.of(11, 0);
        LocalTime startTimeRange1 = LocalTime.of(10, 0);
        LocalTime endTimeRange1 = LocalTime.of(11, 0);
        Integer duration1 = 60;
        TimeRange tr1 = new TimeRange(startTimeRange1, endTimeRange1, duration1);

        LocalTime classStartTime1 = LocalTime.of(10, 0);
        LocalTime classEndTime1 = LocalTime.of(11, 0);
        Class class1 = new Class(LocalDate.now().plusDays(1), classStartTime1, classEndTime1);

        assertEquals(class1, uniqueStudentList.findAvailableClass(tr1, currTime1));

        // not pass time
        LocalTime currTime2 = LocalTime.of(10, 0);
        LocalTime startTimeRange2 = LocalTime.of(10, 0);
        LocalTime endTimeRange2 = LocalTime.of(11, 0);
        Integer duration2 = 60;
        TimeRange tr2 = new TimeRange(startTimeRange2, endTimeRange2, duration2);

        LocalTime classStartTime2 = LocalTime.of(10, 00);
        LocalTime classEndTime2 = LocalTime.of(11, 00);
        Class class2 = new Class(LocalDate.now(), classStartTime2, classEndTime2);

        assertEquals(class2, uniqueStudentList.findAvailableClass(tr2, currTime2));


        // halfway through time
        LocalTime currTime3 = LocalTime.of(10, 30);
        LocalTime startTimeRange3 = LocalTime.of(10, 0);
        LocalTime endTimeRange3 = LocalTime.of(11, 0);
        Integer duration3 = 60;
        TimeRange tr3 = new TimeRange(startTimeRange3, endTimeRange3, duration3);

        LocalTime classStartTime3 = LocalTime.of(10, 00);
        LocalTime classEndTime3 = LocalTime.of(11, 00);
        Class class3 = new Class(LocalDate.now().plusDays(1), classStartTime3, classEndTime3);

        assertEquals(class3, uniqueStudentList.findAvailableClass(tr3, currTime3));

        // halfway through time but range long enough, should start from currTime
        LocalTime currTime4 = LocalTime.of(10, 30);
        LocalTime startTimeRange4 = LocalTime.of(10, 0);
        LocalTime endTimeRange4 = LocalTime.of(12, 0);
        Integer duration4 = 60;
        TimeRange tr4 = new TimeRange(startTimeRange4, endTimeRange4, duration4);

        LocalTime classStartTime4 = LocalTime.of(10, 30);
        LocalTime classEndTime4 = LocalTime.of(11, 30);
        Class class4 = new Class(LocalDate.now(), classStartTime4, classEndTime4);

        assertEquals(class4, uniqueStudentList.findAvailableClass(tr4, currTime4));
    }

    @Test
    public void findAvailableClassWithSingleRecord_singleClass_sucess() {
        Student AliceWithClass = ALICE;
        AliceWithClass.setClass(new Class(
                LocalDate.now(), LocalTime.of(12, 0), LocalTime.of(13, 0)));
        uniqueStudentList.add(AliceWithClass);


//
//        // not pass time - overlap
//        LocalTime currTime2 = LocalTime.of(4, 0);
//        LocalTime startTimeRange2 = LocalTime.of(10, 0);
//        LocalTime endTimeRange2 = LocalTime.of(11, 0);
//        Integer duration2 = 60;
//        TimeRange tr2 = new TimeRange(startTimeRange2, endTimeRange2, duration2);
//
//        LocalTime classStartTime2 = LocalTime.of(10, 30);
//        LocalTime classEndTime2 = LocalTime.of(11, 30);
//        Class class2 = new Class(LocalDate.now(), classStartTime2, classEndTime2);
//
//        assertEquals(class2, uniqueStudentList.findAvailableClass(tr2, currTime2));
//
//        // not pass time - before class starts
//        LocalTime currTime1 = LocalTime.of(10, 0);
//        LocalTime startTimeRange1 = LocalTime.of(10, 0);
//        LocalTime endTimeRange1 = LocalTime.of(11, 0);
//        Integer duration1 = 120;
//        TimeRange tr1 = new TimeRange(startTimeRange1, endTimeRange1, duration1);
//
//        LocalTime classStartTime1 = LocalTime.of(10, 0);
//        LocalTime classEndTime1 = LocalTime.of(11, 0);
//        Class class1 = new Class(LocalDate.now().plusDays(1), classStartTime1, classEndTime1);
//
//        assertEquals(class1, uniqueStudentList.findAvailableClass(tr1, currTime1));
//
//        // not pass time - after class starts
//        LocalTime currTime1 = LocalTime.of(10, 0);
//        LocalTime startTimeRange1 = LocalTime.of(10, 0);
//        LocalTime endTimeRange1 = LocalTime.of(11, 0);
//        Integer duration1 = 120;
//        TimeRange tr1 = new TimeRange(startTimeRange1, endTimeRange1, duration1);
//
//        LocalTime classStartTime1 = LocalTime.of(10, 0);
//        LocalTime classEndTime1 = LocalTime.of(11, 0);
//        Class class1 = new Class(LocalDate.now().plusDays(1), classStartTime1, classEndTime1);
//
//        assertEquals(class1, uniqueStudentList.findAvailableClass(tr1, currTime1));

    }
}
