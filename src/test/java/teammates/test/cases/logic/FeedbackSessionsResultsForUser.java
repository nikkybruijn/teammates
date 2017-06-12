package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsResultsForUser extends BaseLogicTest {
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

        testGetFeedbackSessionResultsForUser();

    }

        private void testGetFeedbackSessionResultsForUser() throws Exception {

        // This file contains a session with a private session + a standard
        // session which needs to have enough qn/response combinations to cover as much
        // of the SUT as possible
        DataBundle responseBundle = loadDataBundle("/FeedbackSessionResultsTest.json");
        removeAndRestoreDataBundle(responseBundle);

        ______TS("standard session with varied visibilities");

        FeedbackSessionAttributes session =
                responseBundle.feedbackSessions.get("standard.session");

        /*** Test result bundle for student1 ***/
        StudentAttributes student =
                responseBundle.students.get("student1InCourse1");
        FeedbackSessionResultsBundle results =
                fsLogic.getFeedbackSessionResultsForStudent(session.getFeedbackSessionName(),
                        session.getCourseId(), student.email);

        // We just check for correct session once
        assertEquals(session.toString(), results.feedbackSession.toString());

        // Student can see responses: q1r1, q2r1,3, q3r1, qr4r2-3, q5r1, q7r1-2, q8r1-2
        // We don't check the actual IDs as this is also implicitly tested
        // later when checking the visibility table.
        assertEquals(11, results.responses.size());
        assertEquals(7, results.questions.size());

        // Test the user email-name maps used for display purposes
        String mapString = results.emailNameTable.toString();
        List<String> expectedStrings = new ArrayList<String>();

        String student2AnonEmail = getStudentAnonEmail(responseBundle, "student2InCourse1");
        String student2AnonName = getStudentAnonName(responseBundle, "student2InCourse1");
        String student4AnonEmail = getStudentAnonEmail(responseBundle, "student4InCourse1");
        String student4AnonName = getStudentAnonName(responseBundle, "student4InCourse1");
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "Team 1.1</td></div>'\"=Team 1.1</td></div>'\"",
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.student1InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student2InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.1",
                "FSRTest.student4InCourse1@gmail.tmt" + Const.TEAM_OF_EMAIL_OWNER + "=Team 1.2",
                student2AnonEmail + "=" + student2AnonName,
                student4AnonEmail + "=" + student4AnonName);
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student1InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt's Team=",
                "FSRTest.student4InCourse1@gmail.tmt's Team=",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "Team 1.1</td></div>'\"=",
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors",
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student4AnonEmail + "=" + student4AnonName + Const.TEAM_OF_EMAIL_OWNER);
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());

        // Test 'Append TeamName to Name' for display purposes with Typical Cases
        expectedStrings.clear();
        List<String> actualStrings = new ArrayList<String>();
        for (FeedbackResponseAttributes response : results.responses) {
            String giverName = results.getNameForEmail(response.giver);
            String giverTeamName = results.getTeamNameForEmail(response.giver);
            giverName = results.appendTeamNameToName(giverName, giverTeamName);
            String recipientName = results.getNameForEmail(response.recipient);
            String recipientTeamName = results.getTeamNameForEmail(response.recipient);
            recipientName = results.appendTeamNameToName(recipientName, recipientTeamName);
            actualStrings.add(giverName);
            actualStrings.add(recipientName);
        }
        Collections.addAll(expectedStrings,
                getStudentAnonName(responseBundle, "student2InCourse1"),
                getStudentAnonName(responseBundle, "student4InCourse1"),
                "student1 In Course1</td></div>'\" (Team 1.1</td></div>'\")",
                "student2 In Course1 (Team 1.1</td></div>'\")",
                "student4 In Course1 (Team 1.2)",
                "Instructor1 Course1 (Instructors)",
                "Team 1.1</td></div>'\"",
                "Team 1.2",
                "Team 1.3",
                "Team 1.4");
        AssertHelper.assertContains(expectedStrings, actualStrings.toString());

        // Test 'Append TeamName to Name' for display purposes with Special Cases
        expectedStrings.clear();
        actualStrings.clear();

        // case: Unknown User
        String unknownUserName = Const.USER_UNKNOWN_TEXT;
        String someTeamName = "Some Team Name";
        unknownUserName = results.appendTeamNameToName(unknownUserName, someTeamName);
        actualStrings.add(unknownUserName);

        // case: Nobody
        String nobodyUserName = Const.USER_NOBODY_TEXT;
        nobodyUserName = results.appendTeamNameToName(nobodyUserName, someTeamName);
        actualStrings.add(nobodyUserName);

        // case: Anonymous User
        String anonymousUserName = "Anonymous " + System.currentTimeMillis();
        anonymousUserName = results.appendTeamNameToName(anonymousUserName, someTeamName);
        actualStrings.add(anonymousUserName);
        Collections.addAll(expectedStrings,
                Const.USER_UNKNOWN_TEXT,
                Const.USER_NOBODY_TEXT,
                anonymousUserName);
        assertEquals(expectedStrings.toString(), actualStrings.toString());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn1.resp1", responseBundle) + "={true,true}",
                getResponseId("qn2.resp1", responseBundle) + "={true,true}",
                getResponseId("qn2.resp3", responseBundle) + "={true,true}",
                getResponseId("qn3.resp1", responseBundle) + "={true,true}",
                getResponseId("qn4.resp2", responseBundle) + "={true,true}",
                getResponseId("qn4.resp3", responseBundle) + "={false,true}",
                getResponseId("qn5.resp1", responseBundle) + "={true,false}",
                getResponseId("qn7.resp1", responseBundle) + "={true,true}",
                getResponseId("qn7.resp2", responseBundle) + "={true,true}",
                getResponseId("qn8.resp1", responseBundle) + "={true,true}",
                getResponseId("qn8.resp2", responseBundle) + "={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(11, results.visibilityTable.size());

        /*** Test result bundle for instructor1 within a course ***/
        InstructorAttributes instructor =
                responseBundle.instructors.get("instructor1OfCourse1");
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email);

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(10, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());

        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        String student1AnonEmail = getStudentAnonEmail(responseBundle, "student1InCourse1");
        String student1AnonName = getStudentAnonName(responseBundle, "student1InCourse1");
        String student3AnonEmail = getStudentAnonEmail(responseBundle, "student3InCourse1");
        String student3AnonName = getStudentAnonName(responseBundle, "student3InCourse1");
        String student6AnonEmail = getStudentAnonEmail(responseBundle, "student6InCourse1");
        String student6AnonName = getStudentAnonName(responseBundle, "student6InCourse1");
        String instructor1AnonEmail = FeedbackSessionResultsBundle.getAnonEmail(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor1OfCourse1").name);
        String instructor1AnonName = FeedbackSessionResultsBundle.getAnonName(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor1OfCourse1").name);
        String instructor2AnonEmail = FeedbackSessionResultsBundle.getAnonEmail(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor2OfCourse1").name);
        String instructor2AnonName = FeedbackSessionResultsBundle.getAnonName(
                                          FeedbackParticipantType.INSTRUCTORS,
                                          responseBundle.instructors.get("instructor2OfCourse1").name);
        Collections.addAll(expectedStrings,
                "%GENERAL%=%NOBODY%",
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1</td></div>'\"",
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "FSRTest.student3InCourse1@gmail.tmt=student3 In Course1",
                "FSRTest.student4InCourse1@gmail.tmt=student4 In Course1",
                "FSRTest.student5InCourse1@gmail.tmt=student5 In Course1",
                "FSRTest.student6InCourse1@gmail.tmt=student6 In Course1",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1",
                "FSRTest.instr2@course1.tmt=Instructor2 Course1",
                student1AnonEmail + "=" + student1AnonName,
                student2AnonEmail + "=" + student2AnonName,
                student3AnonEmail + "=" + student3AnonName,
                student6AnonEmail + "=" + student6AnonName,
                instructor1AnonEmail + "=" + instructor1AnonName,
                instructor2AnonEmail + "=" + instructor2AnonName,
                "Team 1.2=Team 1.2",
                "Team 1.3=Team 1.3",
                "Team 1.4=Team 1.4");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "%GENERAL%=",
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "FSRTest.student3InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student4InCourse1@gmail.tmt=Team 1.2",
                "FSRTest.student5InCourse1@gmail.tmt=Team 1.3",
                "FSRTest.student6InCourse1@gmail.tmt=Team 1.4",
                "FSRTest.instr2@course1.tmt=Instructors",
                "FSRTest.instr1@course1.tmt=Instructors",
                student1AnonEmail + "=" + student1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student3AnonEmail + "=" + student3AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student6AnonEmail + "=" + student6AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor1AnonEmail + "=" + instructor1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor2AnonEmail + "=" + instructor2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                "Team 1.3=",
                "Team 1.2=",
                "Team 1.4=");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(18, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn2.resp1", responseBundle) + "={false,false}",
                getResponseId("qn2.resp2", responseBundle) + "={false,false}",
                getResponseId("qn2.resp3", responseBundle) + "={false,false}",
                getResponseId("qn3.resp1", responseBundle) + "={true,false}",
                getResponseId("qn3.resp2", responseBundle) + "={false,false}",
                getResponseId("qn4.resp1", responseBundle) + "={true,true}",
                getResponseId("qn4.resp2", responseBundle) + "={true,true}",
                getResponseId("qn4.resp3", responseBundle) + "={true,true}",
                getResponseId("qn5.resp1", responseBundle) + "={false,true}",
                getResponseId("qn6.resp1", responseBundle) + "={true,true}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(10, results.visibilityTable.size());

        /*** Test result bundle for instructor1 within a section ***/

        results = fsLogic.getFeedbackSessionResultsForInstructorInSection(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email, "Section A");

        // Instructor can see responses: q2r1-3, q3r1-2, q4r1-3, q5r1, q6r1
        assertEquals(7, results.responses.size());
        //Instructor should still see all questions
        assertEquals(8, results.questions.size());

        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                student1AnonEmail + "=" + student1AnonName,
                student2AnonEmail + "=" + student2AnonName,
                student3AnonEmail + "=" + student3AnonName,
                student6AnonEmail + "=" + student6AnonName,
                instructor1AnonEmail + "=" + instructor1AnonName,
                "FSRTest.student2InCourse1@gmail.tmt=student2 In Course1",
                "Team 1.4=Team 1.4",
                "FSRTest.instr1@course1.tmt=Instructor1 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                student1AnonEmail + "=" + student1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student2AnonEmail + "=" + student2AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student3AnonEmail + "=" + student3AnonName + Const.TEAM_OF_EMAIL_OWNER,
                student6AnonEmail + "=" + student6AnonName + Const.TEAM_OF_EMAIL_OWNER,
                instructor1AnonEmail + "=" + instructor1AnonName + Const.TEAM_OF_EMAIL_OWNER,
                "FSRTest.student2InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "Team 1.4=",
                "FSRTest.instr1@course1.tmt=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(13, results.emailTeamNameTable.size());

        // Test the generated response visibilityTable for userNames.
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("qn3.resp1", responseBundle) + "={true,false}",
                getResponseId("qn4.resp3", responseBundle) + "={true,true}",
                getResponseId("qn2.resp3", responseBundle) + "={false,false}",
                getResponseId("qn2.resp1", responseBundle) + "={false,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(7, results.visibilityTable.size());
        // TODO: test student2 too.

        ______TS("private session");

        session = responseBundle.feedbackSessions.get("private.session");

        /*** Test result bundle for student1 ***/
        student = responseBundle.students.get("student1InCourse1");
        results = fsLogic.getFeedbackSessionResultsForStudent(session.getFeedbackSessionName(),
                        session.getCourseId(), student.email);

        assertEquals(0, results.questions.size());
        assertEquals(0, results.responses.size());
        assertEquals(0, results.emailNameTable.size());
        assertEquals(0, results.emailTeamNameTable.size());
        assertEquals(0, results.visibilityTable.size());

        /*** Test result bundle for instructor1 ***/

        instructor =
                responseBundle.instructors.get("instructor1OfCourse1");
        results = fsLogic.getFeedbackSessionResultsForInstructor(
                session.getFeedbackSessionName(),
                session.getCourseId(), instructor.email);

        // Can see all responses regardless of visibility settings.
        assertEquals(2, results.questions.size());
        assertEquals(2, results.responses.size());

        // Test the user email-name maps used for display purposes
        mapString = results.emailNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=student1 In Course1",
                "Team 1.2=Team 1.2",
                FeedbackSessionResultsBundle.getAnonEmail(FeedbackParticipantType.TEAMS,
                                                responseBundle.students.get("student3InCourse1").team),
                "FSRTest.instr1@course1.tmt=Instructor1 Course1");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(4, results.emailNameTable.size());

        // Test the user email-teamName maps used for display purposes
        mapString = results.emailTeamNameTable.toString();
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                "FSRTest.student1InCourse1@gmail.tmt=Team 1.1</td></div>'\"",
                "Team 1.2=",
                FeedbackSessionResultsBundle.getAnonEmail(FeedbackParticipantType.TEAMS,
                                                responseBundle.students.get("student3InCourse1").team),
                "FSRTest.instr1@course1.tmt=Instructors");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(4, results.emailTeamNameTable.size());

        // Test that name visibility is adhered to even when
        // it is a private session. (to protect anonymity during session type conversion)"
        mapString = tableToString(results.visibilityTable);
        expectedStrings.clear();
        Collections.addAll(expectedStrings,
                getResponseId("p.qn1.resp1", responseBundle) + "={true,true}",
                getResponseId("p.qn2.resp1", responseBundle) + "={true,false}");
        AssertHelper.assertContains(expectedStrings, mapString);
        assertEquals(2, results.visibilityTable.size());

        ______TS("failure: no session");

        try {
            fsLogic.getFeedbackSessionResultsForInstructor("invalid session", session.getCourseId(), instructor.email);
            signalFailureToDetectException("Did not detect that session does not exist.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to view a non-existent feedback session: "
                         + session.getCourseId() + "/" + "invalid session",
                         e.getMessage());
        }
        //TODO: check for cases where a person is both a student and an instructor
    }
 
}
