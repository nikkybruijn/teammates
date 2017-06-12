package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionStats;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.TimeHelperExtension;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsLogicTest extends BaseLogicTest {
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    @Override
    protected void prepareTestData() {
        dataBundle = loadDataBundle("/FeedbackSessionsLogicTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Test
    public void testAll() throws Exception {
        testGetFeedbackSessionsForCourse();
        testGetFeedbackSessionsListForInstructor();
        testGetFeedbackSessionsClosingWithinTimeLimit();
        testGetFeedbackSessionsWhichNeedOpenMailsToBeSent();
        testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent();
        testGetFeedbackSessionDetailsForInstructor();
        testGetFeedbackSessionQuestionsForStudent();
        testGetFeedbackSessionQuestionsForInstructor();
        testIsFeedbackSessionViewableToStudents();

        testCreateAndDeleteFeedbackSession();
        testCopyFeedbackSession();

        testUpdateFeedbackSession();
        testPublishUnpublishFeedbackSession();

        testIsFeedbackSessionHasQuestionForStudents();
        testIsFeedbackSessionCompletedByStudent();
        testIsFeedbackSessionCompletedByInstructor();
        testIsFeedbackSessionFullyCompletedByStudent();

        testDeleteFeedbackSessionsForCourse();
    }

    private void testGetFeedbackSessionsListForInstructor() {
        List<FeedbackSessionAttributes> finalFsa = new ArrayList<FeedbackSessionAttributes>();
        Collection<FeedbackSessionAttributes> allFsa = dataBundle.feedbackSessions.values();

        String courseId = dataBundle.courses.get("typicalCourse1").getId();
        String instructorGoogleId = dataBundle.instructors.get("instructor1OfCourse1").googleId;

        for (FeedbackSessionAttributes fsa : allFsa) {
            if (fsa.getCourseId().equals(courseId)) {
                finalFsa.add(fsa);
            }
        }
        AssertHelper.assertSameContentIgnoreOrder(
                finalFsa, fsLogic.getFeedbackSessionsListForInstructor(instructorGoogleId, false));

    }

    private void testIsFeedbackSessionHasQuestionForStudents() throws Exception {
        // no need to removeAndRestoreTypicalDataInDatastore() as the previous test does not change the db

        FeedbackSessionAttributes sessionWithStudents = dataBundle.feedbackSessions.get("gracePeriodSession");
        FeedbackSessionAttributes sessionWithoutStudents = dataBundle.feedbackSessions.get("closedSession");

        ______TS("non-existent session/courseId");

        try {
            fsLogic.isFeedbackSessionHasQuestionForStudents("nOnEXistEnT session", "someCourse");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a non-existent feedback session: "
                         + "someCourse" + "/" + "nOnEXistEnT session",
                         edne.getMessage());
        }

        ______TS("session contains students");
        assertTrue(fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithStudents.getFeedbackSessionName(),
                                                                   sessionWithStudents.getCourseId()));

        ______TS("session does not contain students");
        assertFalse(fsLogic.isFeedbackSessionHasQuestionForStudents(sessionWithoutStudents.getFeedbackSessionName(),
                                                                    sessionWithoutStudents.getCourseId()));
    }

    private void testGetFeedbackSessionsClosingWithinTimeLimit() throws Exception {

        ______TS("init : 0 non private sessions closing within time-limit");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(0, sessionList.size());

        ______TS("typical case : 1 non private session closing within time limit");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone(0);
        session.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        session.setSessionVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session.setEndTime(TimeHelper.getDateOffsetToCurrentTime(1));
        ThreadHelper.waitBriefly(); // this one is correctly used
        fsLogic.createFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();

        assertEquals(1, sessionList.size());
        assertEquals(session.getFeedbackSessionName(),
                sessionList.get(0).getFeedbackSessionName());

        ______TS("case : 1 private session closing within time limit");
        session.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
        fsLogic.updateFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsClosingWithinTimeLimit();
        assertEquals(0, sessionList.size());

        // delete the newly added session as removeAndRestoreTypicalDataInDatastore()
        // wont do it
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(),
                session.getCourseId());
    }

    private void testGetFeedbackSessionsWhichNeedOpenMailsToBeSent() throws Exception {

        ______TS("init : 0 open sessions");
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 open session with mail unsent");
        FeedbackSessionAttributes session = getNewFeedbackSession();
        session.setTimeZone(0);
        session.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        session.setSessionVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session.setStartTime(TimeHelperExtension.getHoursOffsetToCurrentTime(-47));
        session.setEndTime(TimeHelper.getDateOffsetToCurrentTime(1));
        session.setSentOpenEmail(false);
        fsLogic.createFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).getFeedbackSessionName(),
                session.getFeedbackSessionName());

        ______TS("typical case : 1 open session with mail sent");
        session.setSentOpenEmail(true);
        fsLogic.updateFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 closed session with mail unsent");
        session.setEndTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        fsLogic.updateFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
        assertEquals(0, sessionList.size());

        //delete the newly added session as removeAndRestoreTypicalDataInDatastore()
        //wont do it
        fsLogic.deleteFeedbackSessionCascade(session.getFeedbackSessionName(),
                session.getCourseId());
    }

    private void testGetFeedbackSessionWhichNeedPublishedEmailsToBeSent() throws Exception {

        ______TS("init : no published sessions");
        unpublishAllSessions();
        List<FeedbackSessionAttributes> sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();

        assertEquals(0, sessionList.size());

        ______TS("case : 1 published session with mail unsent");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        session.setTimeZone(0);
        session.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-2));
        session.setEndTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        session.setResultsVisibleFromTime(TimeHelper.getDateOffsetToCurrentTime(-1));

        session.setSentPublishedEmail(false);
        fsLogic.updateFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        assertEquals(1, sessionList.size());
        assertEquals(sessionList.get(0).getFeedbackSessionName(),
                session.getFeedbackSessionName());

        ______TS("case : 1 published session with mail sent");
        session.setSentPublishedEmail(true);
        fsLogic.updateFeedbackSession(session);

        sessionList = fsLogic
                .getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        assertEquals(0, sessionList.size());
    }

    private void testCreateAndDeleteFeedbackSession() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("test create");
        FeedbackSessionAttributes fs = getNewFeedbackSession();
        fsLogic.createFeedbackSession(fs);
        verifyPresentInDatastore(fs);

        ______TS("test create with invalid session name");
        fs.setFeedbackSessionName("test & test");
        try {
            fsLogic.createFeedbackSession(fs);
            signalFailureToDetectException();
        } catch (Exception e) {
            assertEquals("The provided feedback session name is not acceptable to TEAMMATES "
                             + "as it cannot contain the following special html characters in brackets: "
                             + "(&lt; &gt; \\ &#x2f; &#39; &amp;)",
                         e.getMessage());
        }

        fs.setFeedbackSessionName("test %| test");
        try {
            fsLogic.createFeedbackSession(fs);
            signalFailureToDetectException();
        } catch (Exception e) {
            assertEquals("\"test %| test\" is not acceptable to TEAMMATES as a/an feedback session name "
                             + "because it contains invalid characters. All feedback session name "
                             + "must start with an alphanumeric character, and cannot contain "
                             + "any vertical bar (|) or percent sign (%).",
                         e.getMessage());
        }

        ______TS("test delete");
        fs = getNewFeedbackSession();
        // Create a question under the session to test for cascading during delete.
        FeedbackQuestionAttributes fq = new FeedbackQuestionAttributes();
        fq.feedbackSessionName = fs.getFeedbackSessionName();
        fq.courseId = fs.getCourseId();
        fq.questionNumber = 1;
        fq.creatorEmail = fs.getCreatorEmail();
        fq.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
        fq.giverType = FeedbackParticipantType.STUDENTS;
        fq.recipientType = FeedbackParticipantType.TEAMS;
        fq.questionMetaData = new Text("question to be deleted through cascade");
        fq.questionType = FeedbackQuestionType.TEXT;
        fq.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        fq.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fq.showGiverNameTo = new ArrayList<FeedbackParticipantType>();

        fqLogic.createFeedbackQuestion(fq);

        fsLogic.deleteFeedbackSessionCascade(fs.getFeedbackSessionName(), fs.getCourseId());
        verifyAbsentInDatastore(fs);
        verifyAbsentInDatastore(fq);
    }

    private void testCopyFeedbackSession() throws Exception {
        ______TS("Test copy");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor2OfCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        CourseAttributes typicalCourse2 = dataBundle.courses.get("typicalCourse2");
        FeedbackSessionAttributes copiedSession = fsLogic.copyFeedbackSession(
                "Copied Session", typicalCourse2.getId(),
                session1InCourse1.getFeedbackSessionName(),
                session1InCourse1.getCourseId(), instructor2OfCourse1.email);
        verifyPresentInDatastore(copiedSession);

        assertEquals("Copied Session", copiedSession.getFeedbackSessionName());
        assertEquals(typicalCourse2.getId(), copiedSession.getCourseId());
        List<FeedbackQuestionAttributes> questions1 =
                fqLogic.getFeedbackQuestionsForSession(session1InCourse1.getFeedbackSessionName(),
                                                       session1InCourse1.getCourseId());
        List<FeedbackQuestionAttributes> questions2 =
                fqLogic.getFeedbackQuestionsForSession(copiedSession.getFeedbackSessionName(), copiedSession.getCourseId());

        assertEquals(questions1.size(), questions2.size());
        for (int i = 0; i < questions1.size(); i++) {
            FeedbackQuestionAttributes question1 = questions1.get(i);
            FeedbackQuestionDetails questionDetails1 = question1.getQuestionDetails();
            FeedbackQuestionAttributes question2 = questions2.get(i);
            FeedbackQuestionDetails questionDetails2 = question2.getQuestionDetails();

            assertEquals(questionDetails1.getQuestionText(), questionDetails2.getQuestionText());
            assertEquals(question1.giverType, question2.giverType);
            assertEquals(question1.recipientType, question2.recipientType);
            assertEquals(question1.questionType, question2.questionType);
            assertEquals(question1.numberOfEntitiesToGiveFeedbackTo, question2.numberOfEntitiesToGiveFeedbackTo);
        }
        assertEquals(0, copiedSession.getRespondingInstructorList().size());
        assertEquals(0, copiedSession.getRespondingStudentList().size());

        ______TS("Failure case: duplicate session");
        try {
            fsLogic.copyFeedbackSession(
                    session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(),
                    session1InCourse1.getFeedbackSessionName(),
                    session1InCourse1.getCourseId(), instructor2OfCourse1.email);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }

        fsLogic.deleteFeedbackSessionCascade(copiedSession.getFeedbackSessionName(), copiedSession.getCourseId());
    }

    private void testGetFeedbackSessionDetailsForInstructor() throws Exception {
        // This file contains a session with a private session + a standard
        // session + a special session with all questions without recipients.
        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionDetailsTest.json");
        removeAndRestoreDataBundle(newDataBundle);

        Map<String, FeedbackSessionDetailsBundle> detailsMap =
                new HashMap<String, FeedbackSessionDetailsBundle>();

        String instrGoogleId = newDataBundle.instructors.get("instructor1OfCourse1").googleId;
        List<FeedbackSessionDetailsBundle> detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(instrGoogleId);

        List<String> expectedSessions = new ArrayList<String>();
        expectedSessions.add(newDataBundle.feedbackSessions.get("standard.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.responses.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("no.recipients.session").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session").toString());

        StringBuilder actualSessionsBuilder = new StringBuilder();
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessionsBuilder.append(details.feedbackSession.toString());
            detailsMap.put(
                    details.feedbackSession.getFeedbackSessionName() + "%" + details.feedbackSession.getCourseId(),
                    details);
        }

        String actualSessions = actualSessionsBuilder.toString();
        ______TS("standard session");

        assertEquals(4, detailsList.size());
        AssertHelper.assertContains(expectedSessions, actualSessions);

        FeedbackSessionStats stats =
                detailsMap.get(newDataBundle.feedbackSessions.get("standard.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("standard.session").getCourseId()).stats;

        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // 1 instructor, 1 student, did not respond => 8-2=6
        assertEquals(6, stats.submittedTotal);

        ______TS("No recipients session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.recipients.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("no.recipients.session").getCourseId()).stats;

        // 2 instructors, 6 students = 8
        assertEquals(8, stats.expectedTotal);
        // only 1 student responded
        assertEquals(1, stats.submittedTotal);

        ______TS("No responses session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("no.responses.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("no.responses.session").getCourseId()).stats;

        // 1 instructors, 1 students = 2
        assertEquals(2, stats.expectedTotal);
        // no responses
        assertEquals(0, stats.submittedTotal);

        ______TS("private session with questions");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session").getFeedbackSessionName() + "%"
                               + newDataBundle.feedbackSessions.get("private.session").getCourseId()).stats;
        assertEquals(1, stats.expectedTotal);
        // For private sessions, we mark as completed only when creator has finished all questions.
        assertEquals(0, stats.submittedTotal);

        ______TS("change private session to non-private");
        FeedbackSessionAttributes privateSession =
                newDataBundle.feedbackSessions.get("private.session");
        privateSession.setSessionVisibleFromTime(privateSession.getStartTime());
        privateSession.setEndTime(TimeHelper.convertToDate("2015-04-01 10:00 PM UTC"));
        privateSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        fsLogic.updateFeedbackSession(privateSession);

        // Re-read details
        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor1OfCourse1").googleId);
        for (FeedbackSessionDetailsBundle details : detailsList) {
            if (details.feedbackSession.getFeedbackSessionName().equals(
                    newDataBundle.feedbackSessions.get("private.session").getFeedbackSessionName())) {
                stats = details.stats;
                break;
            }
        }
        // 1 instructor (creator only), 6 students = 8
        assertEquals(7, stats.expectedTotal);
        // 1 instructor, 1 student responded
        assertEquals(2, stats.submittedTotal);

        ______TS("private session without questions");
        expectedSessions.clear();
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.noquestions").toString());
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.done").toString());

        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor2OfCourse1").googleId);

        detailsMap.clear();
        actualSessionsBuilder = new StringBuilder();
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessionsBuilder.append(details.feedbackSession.toString());
            detailsMap.put(
                    details.feedbackSession.getFeedbackSessionName() + "%" + details.feedbackSession.getCourseId(),
                    details);
        }
        actualSessions = actualSessionsBuilder.toString();

        AssertHelper.assertContains(expectedSessions, actualSessions);

        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.noquestions")
                                                             .getFeedbackSessionName()
                + "%" + newDataBundle.feedbackSessions.get("private.session.noquestions")
                                                      .getCourseId()).stats;

        assertEquals(0, stats.expectedTotal);
        assertEquals(0, stats.submittedTotal);

        ______TS("completed private session");
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.done").getFeedbackSessionName() + "%"
                + newDataBundle.feedbackSessions.get("private.session.done").getCourseId()).stats;

        assertEquals(1, stats.expectedTotal);
        assertEquals(1, stats.submittedTotal);

        ______TS("private session with questions with no recipients");
        expectedSessions.clear();
        expectedSessions.add(newDataBundle.feedbackSessions.get("private.session.norecipients").toString());

        detailsList = fsLogic.getFeedbackSessionDetailsForInstructor(
                newDataBundle.instructors.get("instructor1OfCourse3").googleId);

        detailsMap.clear();
        actualSessions = "";
        actualSessionsBuilder = new StringBuilder();
        for (FeedbackSessionDetailsBundle details : detailsList) {
            actualSessionsBuilder.append(details.feedbackSession.toString());
            detailsMap.put(
                    details.feedbackSession.getFeedbackSessionName() + "%" + details.feedbackSession.getCourseId(),
                    details);
        }
        actualSessions = actualSessionsBuilder.toString();
        AssertHelper.assertContains(expectedSessions, actualSessions);
        stats = detailsMap.get(newDataBundle.feedbackSessions.get("private.session.norecipients")
                                                             .getFeedbackSessionName()
                + "%" + newDataBundle.feedbackSessions.get("private.session.norecipients")
                                                      .getCourseId()).stats;

        assertEquals(0, stats.expectedTotal);
        assertEquals(0, stats.submittedTotal);

        ______TS("instructor does not exist");
        assertTrue(fsLogic.getFeedbackSessionDetailsForInstructor("non-existent.google.id").isEmpty());

    }

    private void testGetFeedbackSessionsForCourse() throws Exception {
        List<FeedbackSessionAttributes> actualSessions = null;

        ______TS("non-existent course");
        try {
            fsLogic.getFeedbackSessionsForUserInCourse("NonExistentCourseId", "randomUserId");
            signalFailureToDetectException("Did not detect that course does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Error getting feedback session(s): Course does not exist.", edne.getMessage());
        }

        ______TS("Student viewing: 2 visible, 1 awaiting, 1 no questions");
        // 2 valid sessions in course 1, 0 in course 2.

        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        // Student can see sessions 1 and 2. Session 3 has no questions. Session 4 is not yet visible for students.
        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;

        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(3, actualSessions.size());

        // Course 2 only has an instructor session and a private session.
        // The private session is not viewable to students,
        // but the instructor session has questions where responses are visible
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "student1InCourse2@gmail.tmt");
        assertEquals(1, actualSessions.size());

        ______TS("Instructor viewing");
        // 3 valid sessions in course 1, 1 in course 2.

        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse1", "instructor1@course1.tmt");

        // Instructors should be able to see all sessions for the course
        expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("empty.session").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("closedSession").toString() + Const.EOL
                + dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;

        for (FeedbackSessionAttributes session : actualSessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        assertEquals(6, actualSessions.size());

        // We should only have one session here as session 2 is private and this instructor is not the creator.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor2@course2.tmt");

        assertEquals(actualSessions.get(0).toString(),
                dataBundle.feedbackSessions.get("session2InCourse2").toString());
        assertEquals(1, actualSessions.size());

        ______TS("Private session viewing");

        // This is the creator for the private session.
        // We have already tested above that other instructors cannot see it.
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfTypicalCourse2", "instructor1@course2.tmt");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("session1InCourse2").toString(),
                actualSessions.toString());

        ______TS("Feedback session without questions for students but with visible responses are visible");
        actualSessions = fsLogic.getFeedbackSessionsForUserInCourse("idOfArchivedCourse", "student1InCourse1@gmail.tmt");
        AssertHelper.assertContains(dataBundle.feedbackSessions.get("archiveCourse.session1").toString(),
                actualSessions.toString());
    }

    private void testGetFeedbackSessionQuestionsForStudent() throws Exception {

        ______TS("standard test");

        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForStudent(
                        "First feedback session", "idOfTypicalCourse1", "student1InCourse1@gmail.tmt");

        // We just test this once.
        assertEquals(actual.feedbackSession.toString(),
                dataBundle.feedbackSessions.get("session1InCourse1").toString());

        // There should be 3 questions for students to do in session 1.
        // Other questions are set for instructors.
        assertEquals(3, actual.questionResponseBundle.size());

        // Question 1
        FeedbackQuestionAttributes expectedQuestion =
                getQuestionFromDatastore("qn1InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S1C1", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore("response2ForQ2S1C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Question for students to instructors
        expectedQuestion = getQuestionFromDatastore("qn5InSession1InCourse1");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        // Check that instructors (except the one who is not displayed to student) appear as recipients
        Map<String, String> recipients = actual.recipientList.get(expectedQuestion.getId());
        assertTrue(recipients.containsKey("instructor1@course1.tmt"));
        assertTrue(recipients.containsKey("instructor2@course1.tmt"));
        assertTrue(recipients.containsKey("instructor3@course1.tmt"));
        assertTrue(recipients.containsKey("instructorNotYetJoinedCourse1@email.tmt"));
        assertFalse(recipients.containsKey("helper@course1.tmt"));

        ______TS("team feedback test");

        // Check that student3 get team member's (student4) feedback response as well (for team question).
        actual = fsLogic.getFeedbackSessionQuestionsForStudent(
                        "Second feedback session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt");

        assertEquals(2, actual.questionResponseBundle.size());

        // Question 1
        expectedQuestion = getQuestionFromDatastore("team.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        expectedResponsesString = getResponseFromDatastore(
                "response1ForQ1S2C1", dataBundle).toString();
        actualResponses.clear();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle
                .get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Question 2, no responses from this student yet
        expectedQuestion = getQuestionFromDatastore("team.members.feedback");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());

        ______TS("failure: invalid session");

        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "invalid session", "idOfTypicalCourse1", "student3InCourse1@gmail.tmt");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a non-existent feedback session: "
                         + "idOfTypicalCourse1" + "/" + "invalid session",
                         e.getMessage());
        }

        ______TS("failure: non-existent student");

        try {
            fsLogic.getFeedbackSessionQuestionsForStudent(
                    "Second feedback session", "idOfTypicalCourse1", "randomUserId");
            signalFailureToDetectException("Did not detect that student does not exist.");
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Error getting feedback session(s): Student does not exist.", edne.getMessage());
        }

    }

    private void testGetFeedbackSessionQuestionsForInstructor() throws Exception {
        ______TS("standard test");

        FeedbackSessionQuestionsBundle actual =
                fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Instructor feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");

        // We just test this once.
        assertEquals(dataBundle.feedbackSessions.get("session2InCourse2").toString(),
                actual.feedbackSession.toString());

        // There should be 2 question for students to do in session 1.
        // The final question is set for SELF (creator) only.
        assertEquals(2, actual.questionResponseBundle.size());

        // Question 1
        FeedbackQuestionAttributes expectedQuestion =
                getQuestionFromDatastore("qn1InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        String expectedResponsesString = getResponseFromDatastore("response1ForQ1S2C2", dataBundle).toString();
        List<String> actualResponses = new ArrayList<String>();
        for (FeedbackResponseAttributes responsesForQn : actual.questionResponseBundle.get(expectedQuestion)) {
            actualResponses.add(responsesForQn.toString());
        }
        assertEquals(1, actualResponses.size());
        AssertHelper.assertContains(actualResponses, expectedResponsesString);

        // Question 2
        expectedQuestion = getQuestionFromDatastore("qn2InSession2InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));
        assertTrue(actual.questionResponseBundle.get(expectedQuestion).isEmpty());

        ______TS("private test: not creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor2@course2.tmt");
        assertEquals(0, actual.questionResponseBundle.size());

        ______TS("private test: is creator");
        actual = fsLogic.getFeedbackSessionQuestionsForInstructor(
                        "Private feedback session", "idOfTypicalCourse2", "instructor1@course2.tmt");
        assertEquals(1, actual.questionResponseBundle.size());
        expectedQuestion = getQuestionFromDatastore("qn1InSession1InCourse2");
        assertTrue(actual.questionResponseBundle.containsKey(expectedQuestion));

        ______TS("failure: invalid session");

        try {
            fsLogic.getFeedbackSessionQuestionsForInstructor(
                    "invalid session", "idOfTypicalCourse1", "instructor1@course1.tmt");
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to get a non-existent feedback session: "
                         + "idOfTypicalCourse1" + "/" + "invalid session",
                         e.getMessage());
        }
    }

    private String getStudentAnonEmail(DataBundle dataBundle, String studentKey) {
        return FeedbackSessionResultsBundle.getAnonEmail(FeedbackParticipantType.STUDENTS,
                                                         dataBundle.students.get(studentKey).name);
    }

    private String getStudentAnonName(DataBundle dataBundle, String studentKey) {
        return FeedbackSessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS,
                                                        dataBundle.students.get(studentKey).name);
    }

    private void testIsFeedbackSessionViewableToStudents() {
        ______TS("Session with questions for students to answer");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        ______TS("Session without questions for students, but with visible responses");
        session = dataBundle.feedbackSessions.get("archiveCourse.session1");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        session = dataBundle.feedbackSessions.get("session2InCourse2");
        assertTrue(fsLogic.isFeedbackSessionViewableToStudents(session));

        ______TS("private session");
        session = dataBundle.feedbackSessions.get("session1InCourse2");
        assertFalse(fsLogic.isFeedbackSessionViewableToStudents(session));

        ______TS("empty session");
        session = dataBundle.feedbackSessions.get("empty.session");
        assertFalse(fsLogic.isFeedbackSessionViewableToStudents(session));
    }

    private void testUpdateFeedbackSession() throws Exception {
        ______TS("failure 1: null object");
        try {
            fsLogic.updateFeedbackSession(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            AssertHelper.assertContains(Const.StatusCodes.NULL_PARAMETER, ae.getMessage());
        }

        ______TS("failure 2: non-existent session name");
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.setFeedbackSessionName("asdf_randomName1423");
        fsa.setCourseId("idOfTypicalCourse1");

        try {
            fsLogic.updateFeedbackSession(fsa);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to update a non-existent feedback session: "
                         + fsa.getCourseId() + "/" + fsa.getFeedbackSessionName(),
                         edne.getMessage());
        }

        ______TS("success 1: all changeable values sent are null");
        fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        fsa.setInstructions(null);
        fsa.setStartTime(null);
        fsa.setEndTime(null);
        fsa.setFeedbackSessionType(null);
        fsa.setSessionVisibleFromTime(null);
        fsa.setResultsVisibleFromTime(null);

        fsLogic.updateFeedbackSession(fsa);

        assertEquals(fsa.toString(), fsLogic.getFeedbackSession(fsa.getFeedbackSessionName(), fsa.getCourseId()).toString());
    }

    private void testPublishUnpublishFeedbackSession() throws Exception {
        ______TS("success: publish");
        FeedbackSessionAttributes sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse1");

        // set as manual publish

        sessionUnderTest.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
        fsLogic.updateFeedbackSession(sessionUnderTest);

        fsLogic.publishFeedbackSession(sessionUnderTest);

        // Set real time of publishing
        FeedbackSessionAttributes sessionPublished =
                fsLogic.getFeedbackSession(sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId());
        sessionUnderTest.setResultsVisibleFromTime(sessionPublished.getResultsVisibleFromTime());

        assertEquals(sessionUnderTest.toString(), sessionPublished.toString());

        ______TS("failure: already published");

        try {
            fsLogic.publishFeedbackSession(sessionUnderTest);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is already published.");
        } catch (InvalidParametersException e) {
            assertEquals("Error publishing feedback session: Session has already been published.", e.getMessage());
        }

        ______TS("success: unpublish");

        fsLogic.unpublishFeedbackSession(sessionUnderTest);

        sessionUnderTest.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);

        assertEquals(
                sessionUnderTest.toString(),
                fsLogic.getFeedbackSession(
                        sessionUnderTest.getFeedbackSessionName(), sessionUnderTest.getCourseId()).toString());

        ______TS("failure: not published");

        try {
            fsLogic.unpublishFeedbackSession(sessionUnderTest);
            signalFailureToDetectException(
                    "Did not catch exception signalling that session is not published.");
        } catch (InvalidParametersException e) {
            assertEquals("Error unpublishing feedback session: Session has already been unpublished.", e.getMessage());
        }

        ______TS("failure: private session");

        sessionUnderTest = dataBundle.feedbackSessions.get("session1InCourse2");

        try {
            fsLogic.publishFeedbackSession(sessionUnderTest);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session can't "
                    + "be published.");
        } catch (InvalidParametersException e) {
            assertEquals("Error publishing feedback session: Session is private and can't be published.", e.getMessage());
        }

        try {
            fsLogic.unpublishFeedbackSession(sessionUnderTest);
            signalFailureToDetectException(
                    "Did not catch exception signalling that private session should "
                    + "not be published");
        } catch (InvalidParametersException e) {
            assertEquals("Error unpublishing feedback session: Session is private and can't be unpublished.",
                         e.getMessage());
        }
    }

    private void testIsFeedbackSessionCompletedByInstructor() throws Exception {
        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");

        assertTrue(fsLogic.isFeedbackSessionCompletedByInstructor(fs, instructor.email));
    }

    private void testIsFeedbackSessionCompletedByStudent() {
        ______TS("success: empty session");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("empty.session");
        StudentAttributes student = dataBundle.students.get("student2InCourse1");

        assertTrue(fsLogic.isFeedbackSessionCompletedByStudent(fs, student.email));
    }

    private void testIsFeedbackSessionFullyCompletedByStudent() throws Exception {
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        StudentAttributes student1OfCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student3OfCourse1 = dataBundle.students.get("student3InCourse1");

        ______TS("failure: non-existent feedback session for student");

        try {
            fsLogic.isFeedbackSessionFullyCompletedByStudent("nonExistentFSName", fs.getCourseId(), "random.student@email");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("Trying to check a non-existent feedback session: "
                         + fs.getCourseId() + "/" + "nonExistentFSName",
                         edne.getMessage());
        }

        ______TS("success case: fully done by student 1");
        assertTrue(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.getFeedbackSessionName(), fs.getCourseId(),
                                                                    student1OfCourse1.email));

        ______TS("success case: partially done by student 3");
        assertFalse(fsLogic.isFeedbackSessionFullyCompletedByStudent(fs.getFeedbackSessionName(), fs.getCourseId(),
                                                                     student3OfCourse1.email));
    }

    private FeedbackSessionAttributes getNewFeedbackSession() {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        fsa.setFeedbackSessionName("fsTest1");
        fsa.setCourseId("testCourse");
        fsa.setCreatorEmail("valid@email.tmt");
        fsa.setCreatedTime(new Date());
        fsa.setStartTime(new Date());
        fsa.setEndTime(new Date());
        fsa.setSessionVisibleFromTime(new Date());
        fsa.setResultsVisibleFromTime(new Date());
        fsa.setGracePeriod(5);
        fsa.setSentOpenEmail(true);
        fsa.setInstructions(new Text("Give feedback."));
        return fsa;
    }

    private FeedbackQuestionAttributes getQuestionFromDatastore(String jsonId) {
        FeedbackQuestionAttributes questionToGet = dataBundle.feedbackQuestions.get(jsonId);
        questionToGet = fqLogic.getFeedbackQuestion(
                questionToGet.feedbackSessionName,
                questionToGet.courseId,
                questionToGet.questionNumber);

        return questionToGet;
    }

    // Extract response id from datastore based on json key.
    private String getResponseId(String jsonId, DataBundle bundle) {
        return getResponseFromDatastore(jsonId, bundle).getId();
    }

    private FeedbackResponseAttributes getResponseFromDatastore(String jsonId, DataBundle bundle) {
        FeedbackResponseAttributes response = bundle.feedbackResponses.get(jsonId);

        String questionId = null;
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);
            questionId = fqLogic.getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            questionId = response.feedbackQuestionId;
        }

        return frLogic.getFeedbackResponse(questionId,
                response.giver, response.recipient);
    }

    private void unpublishAllSessions() throws InvalidParametersException, EntityDoesNotExistException {
        for (FeedbackSessionAttributes fs : dataBundle.feedbackSessions.values()) {
            if (fs.isPublished()) {
                fsLogic.unpublishFeedbackSession(fs);
            }
        }
    }

    // Stringifies the visibility table for easy testing/comparison.
    private String tableToString(Map<String, boolean[]> table) {
        StringBuilder tableStringBuilder = new StringBuilder();
        for (Map.Entry<String, boolean[]> entry : table.entrySet()) {
            tableStringBuilder.append('{' + entry.getKey() + "={"
                                      + entry.getValue()[0] + ','
                                      + entry.getValue()[1] + "}},");
        }
        String tableString = tableStringBuilder.toString();
        if (!tableString.isEmpty()) {
            tableString = tableString.substring(0, tableString.length() - 1);
        }
        return tableString;
    }

    private void testDeleteFeedbackSessionsForCourse() {

        assertFalse(fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1").isEmpty());
        fsLogic.deleteFeedbackSessionsForCourseCascade("idOfTypicalCourse1");
        assertTrue(fsLogic.getFeedbackSessionsForCourse("idOfTypicalCourse1").isEmpty());
    }
}