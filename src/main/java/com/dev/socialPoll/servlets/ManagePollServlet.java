package com.dev.socialPoll.servlets;

import com.dev.socialPoll.entity.Option;
import com.dev.socialPoll.entity.Poll;
import com.dev.socialPoll.entity.Question;
import com.dev.socialPoll.entity.User;
import com.dev.socialPoll.exception.ServiceException;
import com.dev.socialPoll.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@WebServlet("/manage-poll")
public class ManagePollServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect("log-in");
            return;
        }

        try {
            long pollId = Long.parseLong(request.getParameter("pollId"));

            PollService pollService = ServiceFactory.getInstance().getPollService();
            OptionService optionService = ServiceFactory.getInstance().getOptionService();
            QuestionService questionService = ServiceFactory.getInstance().getQuestionService();

            Optional<Poll> poll = pollService.retrievePollById(pollId);

            if (poll.isPresent()) {
                Poll currentPoll = poll.get();

                // Get the questions of the poll from the database
                List<Question> questions = questionService.retrieveQuestionsByPoll(pollId);

                // Get the options of each question from the database
                for (Question question : questions) {
                    List<Option> options = optionService.retrieveOptionsByQuestion(question.getId());
                    question.setOptions(options);
                }

                currentPoll.setQuestions(questions);

                request.setAttribute("poll", currentPoll);
                request.getRequestDispatcher("html/admin/manage-poll.jsp").forward(request, response);
            } else {
                response.sendRedirect("/SocialPoll/error");
            }
        } catch (NumberFormatException e) {
            logger.info("NumberFormatException occurred while retrieving poll details");
            response.sendRedirect("/SocialPoll/error");
        } catch (ServiceException e) {
            logger.info("Error occurred while retrieving poll details");
            response.sendRedirect("/SocialPoll/error");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendRedirect("log-in");
            return;
        }

        String action = request.getParameter("action");

        if (action.equals("deletePoll")) {
            long pollId = Long.parseLong(request.getParameter("pollId"));

            try {
                deletePollAndRelatedData(pollId);
            } catch (ServiceException e) {
                logger.error("Error occurred while deleting poll and associated data!", e);
                response.sendRedirect("/SocialPoll/error");
                return;
            }
        } else {
            long pollId = Long.parseLong(request.getParameter("pollId"));
            String pollName = request.getParameter("pollName");
            String description = request.getParameter("description");
            int questionCount = Integer.parseInt(request.getParameter("questionCount"));


            String removedQuestionsString = request.getParameter("removedQuestions");
            List<Long> removedQuestions = new ArrayList<>();

            if (removedQuestionsString != null && !removedQuestionsString.isEmpty()) {
                removedQuestions = Arrays.stream(removedQuestionsString.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            }

            PollService pollService = ServiceFactory.getInstance().getPollService();
            OptionService optionService = ServiceFactory.getInstance().getOptionService();
            QuestionService questionService = ServiceFactory.getInstance().getQuestionService();
            PollResponseService pollResponseService = ServiceFactory.getInstance().getPollResponseService();

            try {
                pollService.decreaseNumQuestions(pollId, removedQuestions.size());
            } catch (ServiceException e) {
                logger.error("Error occurred while decresing number of questions in the poll!", e);
                response.sendRedirect("/SocialPoll/error");
                return;
            }

            Map<String, List<String>> questionOptionsMap = new HashMap<>();
            for (int i = 1; i <= questionCount; i++) {
                String questionKey = "question" + i;
                String questionText = request.getParameter(questionKey);
                if (questionText != null && !questionText.isEmpty()) {
                    List<String> options = new ArrayList<>();
                    for (int j = 1; j <= 5; j++) {
                        String optionKey = questionKey + "-option" + j;
                        String optionValue = request.getParameter(optionKey);
                        if (optionValue != null) {
                            options.add(optionValue);
                        }
                    }
                    questionOptionsMap.put(questionText, options);
                }
            }

            // Remove poll responses, options, and questions from the database for each removed question ID

            for (Long removedQuestionId : removedQuestions) {
                try {
                    // Remove poll responses for the question
                    pollResponseService.deleteResponsesByQuestion(removedQuestionId);

                    // Retrieve options for the question and remove them
                    List<Option> options = optionService.retrieveOptionsByQuestion(removedQuestionId);
                    for (Option option : options) {
                        optionService.deleteOption(option.getId());
                    }

                    // Remove the question itself
                    questionService.deleteQuestion(removedQuestionId);
                } catch (ServiceException e) {
                    logger.error("Error occurred while removing question with ID: " + removedQuestionId, e);
                    response.sendRedirect("/SocialPoll/error");
                    return;
                }
            }

            // Update the poll information for the remaining questions
            try {
                boolean success = pollService.updatePollInformation(pollId, pollName, description, questionCount, questionOptionsMap);
                if (!success) {
                    response.sendRedirect("/SocialPoll/error");
                    return;
                }
            } catch (ServiceException e) {
                logger.error("Error occurred while updating the poll!", e);
                response.sendRedirect("/SocialPoll/error");
                return;
            }
        }



        response.sendRedirect("/SocialPoll/admin-dashboard");
    }

    private void deletePollAndRelatedData(long pollId) throws ServiceException {

        PollService pollService = ServiceFactory.getInstance().getPollService();
        TopicService topicService = ServiceFactory.getInstance().getTopicService();
        OptionService optionService = ServiceFactory.getInstance().getOptionService();
        QuestionService questionService = ServiceFactory.getInstance().getQuestionService();
        PollResponseService pollResponseService = ServiceFactory.getInstance().getPollResponseService();


        List<Question> questions = questionService.retrieveQuestionsByPoll(pollId);
        for (Question question : questions) {

            // Delete poll responses for each question
            pollResponseService.deleteResponsesByQuestion(question.getId());

            List<Option> options = optionService.retrieveOptionsByQuestion(question.getId());
            for (Option option : options) {
                // Delete options for each question
                optionService.deleteOption(option.getId());
            }

            // Delete the question itself
            questionService.deleteQuestion(question.getId());
        }


        // update topic info
        Optional<Poll> currentPoll = pollService.retrievePollById(pollId);
        if (currentPoll.isPresent()) {
            long topicId = currentPoll.get().getTopicId();
            int numParticipants = currentPoll.get().getNumParticipants();

            topicService.decreaseNumParticipants(topicId, numParticipants);
            topicService.decreaseNumPolls(topicId);
        }

        // Delete the poll itself
        pollService.deletePoll(pollId);

    }
}



