package teammates.test.cases.logic;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * SUT: {@link FeedbackSessionsLogic}.
 */
public class FeedbackSessionsResultsSummaryTest extends BaseLogicTest {
    private static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();

    @Override
    protected void prepareTestData() {
        dataBundle = loadDataBundle("/FeedbackSessionsLogicTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Test
    public void testAll() throws Exception {

        testGetFeedbackSessionResultsSummaryAsCsv();

    }

        private void testGetFeedbackSessionResultsSummaryAsCsv() throws ExceedingRangeException, EntityDoesNotExistException {

        ______TS("typical case: get all results");

        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session1InCourse1");
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");

        String export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        String[] expected = {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"What is the best selling point of your product?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Student 1 self feedback.\"",
                // checking single quotes inside cell
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"I'm cool'\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"Rate 1 other student's product\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 1 to student 2.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Response from student 2 to student 1.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 3 \"\"to\"\" student 2. Multiline test.\"",
                "",
                "",
                "Question 3,\"My comments on the class\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                // checking comma inside cell
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"Good work, keep it up!\"",
                "",
                "",
                "Question 4,\"Instructor comments on the class\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"No Response\"",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"No Response\"",
                "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"No Response\"",
                "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"-\",\"-\",\"-\",\"-\",\"No Response\"",
                "\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"-\",\"-\",\"-\",\"-\",\"No Response\"",
                "",
                "",
                "Question 5,\"Students' comments to the instructors\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Response from student 1 to instructor 1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Instructors\",\"Helper Course1\",\"Helper Course1\",\"helper@course1.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Instructors\",\"Instructor Not Yet Joined Course 1\",\"Instructor Not Yet Joined Course 1\",\"instructorNotYetJoinedCourse1@email.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("typical case: get results for single question");
        final int questionNum = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").getQuestionNumber();
        final String questionId = fqLogic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNum).getId();

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, questionId, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 2,\"Rate 1 other student's product\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 1 to student 2.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Response from student 2 to student 1.\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Response from student 3 \"\"to\"\" student 2. Multiline test.\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };
        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("MCQ results");

        DataBundle newDataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(newDataBundle);
        session = newDataBundle.feedbackSessions.get("mcqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"What do you like best about our product?\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"It's good\",1,50",
                "\"It's perfect\",1,50",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"It's good\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"It's perfect\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"What do you like best about the class' product?\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"It's good\",1,50",
                "\"It's perfect\",1,50",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"It's good\"",
                "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"It's perfect\"",
                "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "",
                "",
                "Question 3,\"What can be improved for this class?\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"Content\",0,0",
                "\"Teaching style\",0,0",
                "\"Other\",1,100",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Lecture notes\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("MSQ results");

        session = newDataBundle.feedbackSessions.get("msqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"What do you like best about our product?\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"It's good\",2,66.67",
                "\"It's perfect\",1,33.33",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"It's good\",\"It's perfect\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",,\"It's good\",\"It's perfect\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",,\"It's good\",",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"What do you like best about the class' product?\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"It's good\",1,33.33",
                "\"It's perfect\",2,66.67",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"It's good\",\"It's perfect\"",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",,\"It's good\",\"It's perfect\"",
                "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",,,\"It's perfect\"",
                "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",",
                "",
                "",
                "Question 3,\"Choose all the food you like\"",
                "",
                "Summary Statistics,",
                "Choice, Response Count, Percentage",
                "\"Pizza\",1,16.67",
                "\"Pasta\",2,33.33",
                "\"Chicken rice\",1,16.67",
                "\"Other\",2,33.33",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"Pizza\",\"Pasta\",\"Chicken rice\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",,\"Pizza\",\"Pasta\",\"Chicken rice\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",,,\"Pasta\",",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",,,,",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("NUMSCALE results");

        session = newDataBundle.feedbackSessions.get("numscaleSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"Rate our product.\"",
                "",
                "Summary Statistics,",
                "Team, Recipient, Average, Minimum, Maximum",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",2,2,2",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",3.5,3.5,3.5",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",3.5",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",2",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"Rate our product.\"",
                "",
                "Summary Statistics,",
                "Team, Recipient, Average, Minimum, Maximum",
                "\"Instructors\",\"Instructor1 Course1\",4.5,4.5,4.5",
                "\"Instructors\",\"Instructor2 Course1\",1,1,1",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",4.5",
                "\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",\"Instructors\",\"Instructor2 Course1\",\"Instructor2 Course1\",\"instructor2@course1.tmt\",1",
                "\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"Instructors\",\"Instructor3 Course1\",\"Instructor3 Course1\",\"instructor3@course1.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("CONSTSUM results");

        session = newDataBundle.feedbackSessions.get("constSumSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"How important are the following factors to you? Give points accordingly.\"",
                "",
                "Summary Statistics,",
                "Option, Average Points",
                "\"Fun\",50.5",
                "\"Grades\",49.5",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"Grades\",\"Fun\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",,19,81",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",,80,20",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"Split points among the teams\"",
                "",
                "Summary Statistics,",
                "Team, Recipient, Average Points",
                "\"\",\"Team 1.1</td></div>'\"\"\",80",
                "\"\",\"Team 1.2\",20",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"\",\"Team 1.1</td></div>'\"\"\",\"Team 1.1</td></div>'\"\"\",\"-\",80",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"\",\"Team 1.2\",\"Team 1.2\",\"-\",20",
                "",
                "",
                "Question 3,\"How much has each student worked?\"",
                "",
                "Summary Statistics,",
                "Team, Recipient, Average Points",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",30",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",20",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",30",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",10",
                "\"Team 1.2\",\"student5 In Course1\",10",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",30",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",20",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",30",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",10",
                "\"Instructors\",\"Instructor1 Course1\",\"Instructor1 Course1\",\"instructor1@course1.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",10",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("Instructor without privilege to view responses");

        instructor = newDataBundle.instructors.get("instructor2OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"FSQTT.idOfTypicalCourse1\"",
                "Session Name,\"CONSTSUM Session\"",
                "",
                "",
                "Question 1,\"How important are the following factors to you? Give points accordingly.\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedbacks:,\"Grades\",\"Fun\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                "Question 2,\"Split points among the teams\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "",
                "",
                "Question 3,\"How much has each student worked?\"",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("CONTRIB results");

        session = newDataBundle.feedbackSessions.get("contribSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"How much has each team member including yourself, contributed to the project?\"",
                "",
                "Summary Statistics,",
                "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".",
                "Claimed Contribution (CC) = the contribution claimed by the student.",
                "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.",
                "Team, Name, Email, CC, PC, Ratings Recieved",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"95\",\"N/A\",\"N/A, N/A, N/A\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"student2InCourse1@gmail.tmt\",\"Not Submitted\",\"75\",\"75, N/A, N/A\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"student3InCourse1@gmail.tmt\",\"Not Submitted\",\"103\",\"103, N/A, N/A\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"student4InCourse1@gmail.tmt\",\"Not Submitted\",\"122\",\"122, N/A, N/A\"",
                "\"Team 1.2\",\"student5 In Course1\",\"student5InCourse1@gmail.tmt\",\"Not Submitted\",\"N/A\",\"N/A\"",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Equal share - 5%\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Equal share - 25%\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Equal share + 3%\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Equal share + 22%\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("CONTRIB summary visibility variations");

        // instructor not allowed to see student
        session = newDataBundle.feedbackSessions.get("contribSessionStudentAnonymised");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        String student1AnonName = getStudentAnonName(newDataBundle, "student1InCourse1");
        String student2AnonName = getStudentAnonName(newDataBundle, "student2InCourse1");
        String student3AnonName = getStudentAnonName(newDataBundle, "student3InCourse1");
        String student4AnonName = getStudentAnonName(newDataBundle, "student4InCourse1");
        String student5AnonName = getStudentAnonName(newDataBundle, "student5InCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"How much has each team member including yourself, contributed to the project?\"",
                "",
                "Summary Statistics,",
                "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".",
                "Claimed Contribution (CC) = the contribution claimed by the student.",
                "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.",
                "Team, Name, Email, CC, PC, Ratings Recieved",
                "\"" + student1AnonName + "'s Team\",\"" + student1AnonName + "\",\"-\",\"100\",\"N/A\",\"N/A, N/A, N/A\"",
                "\"" + student2AnonName + "'s Team\",\"" + student2AnonName + "\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"",
                "\"" + student3AnonName + "'s Team\",\"" + student3AnonName + "\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"",
                "\"" + student4AnonName + "'s Team\",\"" + student4AnonName + "\",\"-\",\"Not Submitted\",\"N/A\",\"N/A, N/A, N/A\"",
                "\"" + student5AnonName + "'s Team\",\"" + student5AnonName + "\",\"-\",\"Not Submitted\",\"N/A\",\"N/A\"",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"" + student1AnonName + "'s Team\",\"" + student1AnonName + "\",\"Unknown user\",\"-\",\"\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        // instructor not allowed to view student responses in section
        session = newDataBundle.feedbackSessions.get("contribSessionInstructorSectionRestricted");
        instructor = newDataBundle.instructors.get("instructor1OfCourseWithSections");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"How much has each team member including yourself, contributed to the project?\"",
                "",
                "Summary Statistics,",
                "In the points given below, an equal share is equal to 100 points. e.g. 80 means \"Equal share - 20%\" and 110 means \"Equal share + 10%\".",
                "Claimed Contribution (CC) = the contribution claimed by the student.",
                "Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members.",
                "Team, Name, Email, CC, PC, Ratings Recieved",
                "\"Team 2\",\"student3 In Course With Sections\",\"student3InCourseWithSections@gmail.tmt\",\"100\",\"N/A\",\"N/A\"",
                "\"Team 3\",\"student4 In Course With Sections\",\"student4InCourseWithSections@gmail.tmt\",\"Not Submitted\",\"N/A\",\"N/A\"",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 2\",\"student3 In Course With Sections\",\"Sections\",\"student3InCourseWithSections@gmail.tmt\",\"Team 2\",\"student3 In Course With Sections\",\"Sections\",\"student3InCourseWithSections@gmail.tmt\",\"Equal share\"",
                "\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"No Response\"",
                "\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"No Response\"",
                "\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"Team 1\",\"student1 In Course With Sections\",\"Sections\",\"student1InCourseWithSections@gmail.tmt\",\"No Response\"",
                "\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"Team 1\",\"student2 In Course With Sections\",\"Sections\",\"student2InCourseWithSections@gmail.tmt\",\"No Response\"",
                "\"Team 3\",\"student4 In Course With Sections\",\"Sections\",\"student4InCourseWithSections@gmail.tmt\",\"Team 3\",\"student4 In Course With Sections\",\"Sections\",\"student4InCourseWithSections@gmail.tmt\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("RUBRIC results");

        session = newDataBundle.feedbackSessions.get("rubricSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"Please choose the best choice for the following sub-questions.\"",
                "",
                "Summary Statistics,",
                ",\"Yes (Weight: 1.25)\",\"No (Weight: -1.7)\",Average",
                "\"a) This student has done a good job.\",67% (2),33% (1),0.27",
                "\"b) This student has tried his/her best.\",75% (3),25% (1),0.51",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Sub Question,Choice Value,Choice Number",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"b\",\"No\",\"2\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"a\",\"No\",\"2\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"a\",\"No Response\",\"\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"b\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "",
                "",
                "Question 2,\"Please choose the best choice for the following sub-questions. Only first subquestion has responses\"",
                "",
                "Summary Statistics,",
                ",\"Yes (Weight: 1.25)\",\"No (Weight: 1)\",Average",
                "\"a) This student has done a good job.\",67% (2),33% (1),1.17",
                "\"b) This student has tried his/her best.\",- (0),- (0),-",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Sub Question,Choice Value,Choice Number",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"b\",\"No Response\",\"\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"a\",\"No\",\"2\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"b\",\"No Response\",\"\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"a\",\"Yes\",\"1\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"b\",\"No Response\",\"\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"All Sub-Questions\",\"No Response\"",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("RANK results");

        session = newDataBundle.feedbackSessions.get("rankSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, true);

        expected = new String[] {
                // CHECKSTYLE.OFF:LineLength csv lines can exceed character limit
                "Course,\"" + session.getCourseId() + "\"",
                "Session Name,\"" + session.getFeedbackSessionName() + "\"",
                "",
                "",
                "Question 1,\"Rank the other students.\"",
                "",
                "Summary Statistics,",
                "Team, Recipient, Average Rank",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",1",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",3",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",4",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",2",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Feedback",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",4",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",3",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",2",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",1",
                "",
                "",
                "Question 2,\"Rank the areas of improvement you think your team should make progress in.\"",
                "",
                "Summary Statistics,",
                "Option, Average Rank",
                "\"Teamwork and communication\",1.75",
                "\"Time management\",2",
                "\"Quality of progress reports\",2.5",
                "\"Quality of work\",2.4",
                "",
                "",
                "Team,Giver's Full Name,Giver's Last Name,Giver's Email,Recipient's Team,Recipient's Full Name,Recipient's Last Name,Recipient's Email,Rank 1,Rank 2,Rank 3,Rank 4",
                "\"Team 1.1</td></div>'\"\"\",\"student1 In Course1</td></div>'\"\"\",\"Course1</td></div>'\"\"\",\"student1InCourse1@gmail.tmt\",\"\",\"Team 1.1</td></div>'\"\"\",\"Team 1.1</td></div>'\"\"\",\"-\",\"Quality of work\",\"Quality of progress reports\",\"Time management\",\"Teamwork and communication\"",
                "\"Team 1.1</td></div>'\"\"\",\"student2 In Course1\",\"Course1\",\"student2InCourse1@gmail.tmt\",\"\",\"Team 1.1</td></div>'\"\"\",\"Team 1.1</td></div>'\"\"\",\"-\",\"Teamwork and communication\",\"Time management\",\"Quality of progress reports\",\"Quality of work\"",
                "\"Team 1.1</td></div>'\"\"\",\"student3 In Course1\",\"Course1\",\"student3InCourse1@gmail.tmt\",\"\",\"Team 1.1</td></div>'\"\"\",\"Team 1.1</td></div>'\"\"\",\"-\",\"Time management, Teamwork and communication\",\"Quality of work\",,",
                "\"Team 1.1</td></div>'\"\"\",\"student4 In Course1\",\"Course1\",\"student4InCourse1@gmail.tmt\",\"\",\"Team 1.1</td></div>'\"\"\",\"Team 1.1</td></div>'\"\"\",\"-\",\"Teamwork and communication\",\"Time management\",\"Quality of work\",",
                "\"Team 1.2\",\"student5 In Course1\",\"Course1\",\"student5InCourse1@gmail.tmt\",\"\",\"Team 1.2\",\"Team 1.2\",\"-\",\"Quality of work\",,,",
                "",
                "",
                ""
                // CHECKSTYLE.ON:LineLength
        };

        assertEquals(StringUtils.join(expected, Const.EOL), export);

        ______TS("MSQ results without statistics");

        session = newDataBundle.feedbackSessions.get("msqSession");
        instructor = newDataBundle.instructors.get("instructor1OfCourse1");

        export = fsLogic.getFeedbackSessionResultsSummaryAsCsv(
                session.getFeedbackSessionName(), session.getCourseId(), instructor.email, null, null, true, false);

        assertFalse(export.contains("Summary Statistics"));

        ______TS("Non-existent Course/Session");

        try {
            fsLogic.getFeedbackSessionResultsSummaryAsCsv("non.existent", "no course",
                    instructor.email, null, null, true, true);
            signalFailureToDetectException("Failed to detect non-existent feedback session.");
        } catch (EntityDoesNotExistException e) {
            assertEquals("Trying to view a non-existent feedback session: "
                         + "no course" + "/" + "non.existent",
                         e.getMessage());
            throw e;
        }
    }

  private String getStudentAnonName(DataBundle dataBundle, String studentKey) {
    return FeedbackSessionResultsBundle.getAnonName(FeedbackParticipantType.STUDENTS,
      dataBundle.students.get(studentKey).name);
  }

}
