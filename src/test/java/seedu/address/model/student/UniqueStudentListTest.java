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
    public void findAvailableClass_noClass_success() {
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

        LocalTime classStartTime2 = LocalTime.of(10, 0);
        LocalTime classEndTime2 = LocalTime.of(11, 0);
        Class class2 = new Class(LocalDate.now(), classStartTime2, classEndTime2);

        assertEquals(class2, uniqueStudentList.findAvailableClass(tr2, currTime2));


        // halfway through time
        LocalTime currTime3 = LocalTime.of(10, 30);
        LocalTime startTimeRange3 = LocalTime.of(10, 0);
        LocalTime endTimeRange3 = LocalTime.of(11, 0);
        Integer duration3 = 60;
        TimeRange tr3 = new TimeRange(startTimeRange3, endTimeRange3, duration3);

        LocalTime classStartTime3 = LocalTime.of(10, 0);
        LocalTime classEndTime3 = LocalTime.of(11, 0);
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
    public void findAvailableClass_currTimeAfterClassAndSingleClass_success() {
        LocalTime currTime = LocalTime.of(16, 0);
        TimeRange timeWindow = new TimeRange(
                LocalTime.of(10, 0), LocalTime.of(14, 0), 60);

        // class slot endTime before start of timeWindow
        UniqueStudentList uniqueStudentList1 = new UniqueStudentList();
        Class class1 = new Class(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(9, 30));
        Class outputClass1 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass1 = ALICE;
        aliceWithClass1.setClass(class1);
        uniqueStudentList1.add(aliceWithClass1);
        assertEquals(outputClass1, uniqueStudentList1.findAvailableClass(timeWindow, currTime));

        // class slot endTime after start of timeWindow
        UniqueStudentList uniqueStudentList2 = new UniqueStudentList();
        Class class2 = new Class(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 30));
        Class outputClass2 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass2 = ALICE;
        aliceWithClass2.setClass(class2);
        uniqueStudentList2.add(aliceWithClass2);
        assertEquals(outputClass2, uniqueStudentList2.findAvailableClass(timeWindow, currTime));

        // class slot endTime after start of timeWindow and so is startTime
        UniqueStudentList uniqueStudentList3 = new UniqueStudentList();
        Class class3 = new Class(LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(14, 0));
        Class outputClass3 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass3 = ALICE;
        aliceWithClass3.setClass(class3);
        uniqueStudentList3.add(aliceWithClass3);
        assertEquals(outputClass3, uniqueStudentList3.findAvailableClass(timeWindow, currTime));

        // class slot endTime after end of timeWindow and startTime is after start of timeWindow but before
        // end of timeWindow
        UniqueStudentList uniqueStudentList4 = new UniqueStudentList();
        Class class4 = new Class(LocalDate.now(), LocalTime.of(10, 30), LocalTime.of(14, 30));
        Class outputClass4 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass4 = ALICE;
        aliceWithClass4.setClass(class4);
        uniqueStudentList4.add(aliceWithClass4);
        assertEquals(outputClass4, uniqueStudentList4.findAvailableClass(timeWindow, currTime));

        // class slot endTime after end of timeWindow and so is startTime
        UniqueStudentList uniqueStudentList5 = new UniqueStudentList();
        Class class5 = new Class(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(15, 0));
        Class outputClass5 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass5 = ALICE;
        aliceWithClass5.setClass(class5);
        uniqueStudentList5.add(aliceWithClass5);
        assertEquals(outputClass5, uniqueStudentList5.findAvailableClass(timeWindow, currTime));
    }

    @Test
    public void findAvailableClass_currTimeBeforeClassAndSingleClass_success() {
        LocalTime currTime = LocalTime.of(8, 0);
        TimeRange timeWindow = new TimeRange(
                LocalTime.of(10, 0), LocalTime.of(14, 0), 60);

        // class slot endTime before start of timeWindow
        UniqueStudentList uniqueStudentList1 = new UniqueStudentList();
        Class class1 = new Class(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(9, 30));
        Class outputClass1 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass1 = ALICE;
        aliceWithClass1.setClass(class1);
        uniqueStudentList1.add(aliceWithClass1);
        assertEquals(outputClass1, uniqueStudentList1.findAvailableClass(timeWindow, currTime));

        // class slot endTime after start of timeWindow
        UniqueStudentList uniqueStudentList2 = new UniqueStudentList();
        Class class2 = new Class(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 30));
        Class outputClass2 = new Class(LocalDate.now(), LocalTime.of(10, 30),
                LocalTime.of(11, 30));
        Student aliceWithClass2 = ALICE;
        aliceWithClass2.setClass(class2);
        uniqueStudentList2.add(aliceWithClass2);
        assertEquals(outputClass2, uniqueStudentList2.findAvailableClass(timeWindow, currTime));

        // class slot endTime after start of timeWindow and so is startTime, but before end of window,
        // but enough space after class.
        UniqueStudentList uniqueStudentList3 = new UniqueStudentList();
        Class class3 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(13, 0));
        Class outputClass3 = new Class(LocalDate.now(), LocalTime.of(13, 0),
                LocalTime.of(14, 0));
        Student aliceWithClass3 = ALICE;
        aliceWithClass3.setClass(class3);
        uniqueStudentList3.add(aliceWithClass3);
        assertEquals(outputClass3, uniqueStudentList3.findAvailableClass(timeWindow, currTime));

        // class slot endTime after start of timeWindow and so is startTime, but before end of window,
        // but not enough space after class.
        UniqueStudentList uniqueStudentList4 = new UniqueStudentList();
        Class class4 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(13, 1));
        Class outputClass4 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass4 = ALICE;
        aliceWithClass4.setClass(class4);
        uniqueStudentList4.add(aliceWithClass4);
        assertEquals(outputClass4, uniqueStudentList4.findAvailableClass(timeWindow, currTime));

        // class slot endTime before the end of time && class slot start time after start of time range
        // no space before the start, not enough space after class
        UniqueStudentList uniqueStudentList5 = new UniqueStudentList();
        Class class5 = new Class(LocalDate.now(), LocalTime.of(10, 9), LocalTime.of(13, 1));
        Class outputClass5 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass5 = ALICE;
        aliceWithClass5.setClass(class5);
        uniqueStudentList5.add(aliceWithClass5);
        assertEquals(outputClass5, uniqueStudentList5.findAvailableClass(timeWindow, currTime));

        // class slot endTime before the end of time && class slot start time after start of time range
        // no space before the start, enough space after class
        UniqueStudentList uniqueStudentList6 = new UniqueStudentList();
        Class class6 = new Class(LocalDate.now(), LocalTime.of(10, 9), LocalTime.of(13, 0));
        Class outputClass6 = new Class(LocalDate.now(), LocalTime.of(13, 0),
                LocalTime.of(14, 0));
        Student aliceWithClass6 = ALICE;
        aliceWithClass6.setClass(class6);
        uniqueStudentList6.add(aliceWithClass6);
        assertEquals(outputClass6, uniqueStudentList6.findAvailableClass(timeWindow, currTime));

        // class slot endTime before the end of time && class slot start time after start of time range
        // enough space before the start
        UniqueStudentList uniqueStudentList7 = new UniqueStudentList();
        Class class7 = new Class(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(13, 1));
        Class outputClass7 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass7 = ALICE;
        aliceWithClass7.setClass(class7);
        uniqueStudentList7.add(aliceWithClass7);
        assertEquals(outputClass7, uniqueStudentList7.findAvailableClass(timeWindow, currTime));

        // class slot endTime ends on endTimeRange
        // enough space before the start
        UniqueStudentList uniqueStudentList8 = new UniqueStudentList();
        Class class8 = new Class(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(14, 0));
        Class outputClass8 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass8 = ALICE;
        aliceWithClass8.setClass(class8);
        uniqueStudentList8.add(aliceWithClass8);
        assertEquals(outputClass8, uniqueStudentList8.findAvailableClass(timeWindow, currTime));

        // class slot endTime ends on endTimeRange
        // not enough space before the start
        UniqueStudentList uniqueStudentList9 = new UniqueStudentList();
        Class class9 = new Class(LocalDate.now(), LocalTime.of(10, 59), LocalTime.of(14, 0));
        Class outputClass9 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass9 = ALICE;
        aliceWithClass9.setClass(class9);
        uniqueStudentList9.add(aliceWithClass9);
        assertEquals(outputClass9, uniqueStudentList9.findAvailableClass(timeWindow, currTime));

        // class slot startTime after endTimeRange
        // enough space
        UniqueStudentList uniqueStudentList10 = new UniqueStudentList();
        Class class10 = new Class(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(15, 0));
        Class outputClass10 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass10 = ALICE;
        aliceWithClass10.setClass(class10);
        uniqueStudentList10.add(aliceWithClass10);
        assertEquals(outputClass10, uniqueStudentList10.findAvailableClass(timeWindow, currTime));
    }

    @Test
    public void findAvailableClass_currTimeBetweenClassAndSingleClass_success() {
        LocalTime currTime1 = LocalTime.of(10, 0);
        LocalTime currTime2 = LocalTime.of(12, 0);
        LocalTime currTime3 = LocalTime.of(13, 5);

        TimeRange timeWindow = new TimeRange(
                LocalTime.of(10, 0), LocalTime.of(14, 0), 60);

        // class slot endTime before start of timeWindow
        UniqueStudentList uniqueStudentList1 = new UniqueStudentList();
        Class class1 = new Class(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(9, 30));
        Class outputClass1 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass1 = ALICE;
        aliceWithClass1.setClass(class1);
        uniqueStudentList1.add(aliceWithClass1);
        assertEquals(outputClass1, uniqueStudentList1.findAvailableClass(timeWindow, currTime1));

        // class slot endTime at start of timeWindow
        UniqueStudentList uniqueStudentList2 = new UniqueStudentList();
        Class class2 = new Class(LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(10, 0));
        Class outputClass2 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass2 = ALICE;
        aliceWithClass2.setClass(class2);
        uniqueStudentList2.add(aliceWithClass2);
        assertEquals(outputClass2, uniqueStudentList1.findAvailableClass(timeWindow, currTime1));

        // class slot endTime after start of timeWindow: currTime1
        UniqueStudentList uniqueStudentList3 = new UniqueStudentList();
        Class class3 = new Class(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 30));
        Class outputClass3 = new Class(LocalDate.now(), LocalTime.of(10, 30),
                LocalTime.of(11, 30));
        Student aliceWithClass3 = ALICE;
        aliceWithClass3.setClass(class3);
        uniqueStudentList3.add(aliceWithClass3);
        assertEquals(outputClass3, uniqueStudentList3.findAvailableClass(timeWindow, currTime1));

        // class slot endTime after start of timeWindow: currTime2
        UniqueStudentList uniqueStudentList4 = new UniqueStudentList();
        Class class4 = new Class(LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 30));
        Class outputClass4 = new Class(LocalDate.now(), LocalTime.of(12, 0),
                LocalTime.of(13, 0));
        Student aliceWithClass4 = ALICE;
        aliceWithClass4.setClass(class4);
        uniqueStudentList4.add(aliceWithClass4);
        assertEquals(outputClass4, uniqueStudentList4.findAvailableClass(timeWindow, currTime2));


        // class slot endTime after start of timeWindow and startTime at start of timeWindow, but before end of window,
        // but enough space after class: currTime1
        UniqueStudentList uniqueStudentList5 = new UniqueStudentList();
        Class class5 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(13, 0));
        Class outputClass5 = new Class(LocalDate.now(), LocalTime.of(13, 0),
                LocalTime.of(14, 0));
        Student aliceWithClass5 = ALICE;
        aliceWithClass5.setClass(class5);
        uniqueStudentList5.add(aliceWithClass5);
        assertEquals(outputClass5, uniqueStudentList5.findAvailableClass(timeWindow, currTime1));

        // class slot endTime at start of timeWindow and startTime at start of timeWindow, but before end of window,
        // but not enough space after class: currTime1
        UniqueStudentList uniqueStudentList6 = new UniqueStudentList();
        Class class6 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(13, 1));
        Class outputClass6 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass6 = ALICE;
        aliceWithClass6.setClass(class6);
        uniqueStudentList6.add(aliceWithClass6);
        assertEquals(outputClass6, uniqueStudentList6.findAvailableClass(timeWindow, currTime1));

        // class slot endTime after start of timeWindow and startTime at start of timeWindow, but before end of window,
        // but enough space after class: currTime2
        UniqueStudentList uniqueStudentList7 = new UniqueStudentList();
        Class class7 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 58));
        Class outputClass7 = new Class(LocalDate.now(), LocalTime.of(12, 0),
                LocalTime.of(13, 0));
        Student aliceWithClass7 = ALICE;
        aliceWithClass7.setClass(class7);
        uniqueStudentList7.add(aliceWithClass7);
        assertEquals(outputClass7, uniqueStudentList5.findAvailableClass(timeWindow, currTime2));

        // class slot endTime at start of timeWindow and startTime at start of timeWindow, but before end of window,
        // but not enough space after class: currTime3
        UniqueStudentList uniqueStudentList8 = new UniqueStudentList();
        Class class8 = new Class(LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(13, 1));
        Class outputClass8 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass8 = ALICE;
        aliceWithClass8.setClass(class8);
        uniqueStudentList8.add(aliceWithClass8);
        assertEquals(outputClass8, uniqueStudentList4.findAvailableClass(timeWindow, currTime3));


        // class slot endTime before the end of time && class slot start time after start of time range
        // no space before the start, not enough space after class
        UniqueStudentList uniqueStudentList9 = new UniqueStudentList();
        Class class9 = new Class(LocalDate.now(), LocalTime.of(10, 59), LocalTime.of(13, 1));
        Class outputClass9 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass9 = ALICE;
        aliceWithClass9.setClass(class9);
        uniqueStudentList9.add(aliceWithClass9);
        assertEquals(outputClass9, uniqueStudentList5.findAvailableClass(timeWindow, currTime1));

        // class slot endTime before the end of time && class slot start time after start of time range
        // no space before the start, enough space after class
        UniqueStudentList uniqueStudentList10 = new UniqueStudentList();
        Class class10 = new Class(LocalDate.now(), LocalTime.of(10, 59), LocalTime.of(13, 0));
        Class outputClass10 = new Class(LocalDate.now(), LocalTime.of(13, 0),
                LocalTime.of(14, 0));
        Student aliceWithClass10 = ALICE;
        aliceWithClass10.setClass(class10);
        uniqueStudentList10.add(aliceWithClass10);
        assertEquals(outputClass10, uniqueStudentList10.findAvailableClass(timeWindow, currTime2));

        // class slot endTime before the end of time && class slot start time after start of time range
        // enough space before the start but: currTime3
        UniqueStudentList uniqueStudentList11 = new UniqueStudentList();
        Class class11 = new Class(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(13, 1));
        Class outputClass11 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass11 = ALICE;
        aliceWithClass11.setClass(class11);
        uniqueStudentList11.add(aliceWithClass11);
        assertEquals(outputClass11, uniqueStudentList11.findAvailableClass(timeWindow, currTime3));

        // class slot endTime ends on endTimeRange
        // enough space before the start: currTime1
        UniqueStudentList uniqueStudentList12 = new UniqueStudentList();
        Class class12 = new Class(LocalDate.now(), LocalTime.of(11, 0), LocalTime.of(14, 0));
        Class outputClass12 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass12 = ALICE;
        aliceWithClass12.setClass(class12);
        uniqueStudentList12.add(aliceWithClass12);
        assertEquals(outputClass12, uniqueStudentList12.findAvailableClass(timeWindow, currTime1));

        // class slot endTime ends on endTimeRange
        // not enough space before the start: currTime1
        UniqueStudentList uniqueStudentList13 = new UniqueStudentList();
        Class class13 = new Class(LocalDate.now(), LocalTime.of(10, 59), LocalTime.of(14, 0));
        Class outputClass13 = new Class(LocalDate.now().plusDays(1), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass13 = ALICE;
        aliceWithClass13.setClass(class13);
        uniqueStudentList13.add(aliceWithClass13);
        assertEquals(outputClass13, uniqueStudentList13.findAvailableClass(timeWindow, currTime1));

        // class slot startTime after endTimeRange
        // enough space
        UniqueStudentList uniqueStudentList14 = new UniqueStudentList();
        Class class14 = new Class(LocalDate.now(), LocalTime.of(14, 0), LocalTime.of(15, 0));
        Class outputClass14 = new Class(LocalDate.now(), LocalTime.of(10, 0),
                LocalTime.of(11, 0));
        Student aliceWithClass14 = ALICE;
        aliceWithClass14.setClass(class14);
        uniqueStudentList14.add(aliceWithClass14);
        assertEquals(outputClass14, uniqueStudentList14.findAvailableClass(timeWindow, currTime1));



    }
}
