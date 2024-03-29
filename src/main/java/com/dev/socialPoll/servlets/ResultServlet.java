package com.dev.socialPoll.servlets;

import com.dev.socialPoll.entity.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/results")
public class ResultServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getSession().getAttribute("user") == null) {
            request.getRequestDispatcher("/log-in").forward(request, response);
        }

        PollService pollService = ServiceFactory.getInstance().getPollService();
        OptionService optionService = ServiceFactory.getInstance().getOptionService();
        QuestionService questionService = ServiceFactory.getInstance().getQuestionService();
        PollResponseService pollResponseService = ServiceFactory.getInstance().getPollResponseService();

        try {
            long pollId = Long.parseLong(request.getParameter("pollId"));
            Optional<Poll> poll = pollService.retrievePollById(pollId);

            if (poll.isPresent()) {

                List<Question> questions = questionService.retrieveQuestionsByPoll(pollId);
                Map<Question, List<Option>> questionOptionsMap = new HashMap<>();

                for (Question question : questions) {
                    List<Option> options = optionService.retrieveOptionsByQuestion(question.getId());
                    question.setOptions(options);
                    questionOptionsMap.put(question, options);
                }

                for (Map.Entry<Question, List<Option>> entry : questionOptionsMap.entrySet()) {
                    List<Option> options = entry.getValue();
                    long totalVotesForQuestion = options.stream().mapToLong(Option::getNumParticipants).sum();

                    for (Option option : options) {
                        long votesForOption = option.getNumParticipants();
                        double percentage = totalVotesForQuestion == 0 ? 0 : (votesForOption * 100.0) / totalVotesForQuestion;
                        option.setNumParticipants((int) Math.round(percentage));
                    }
                }

                User user = (User) request.getSession().getAttribute("user");
                List<Long> userResponses = pollResponseService.getUserResponses(pollId, user.getId());

                request.setAttribute("poll", poll);
                request.setAttribute("questionOptionsMap", questionOptionsMap);
                request.setAttribute("userResponses", userResponses);
                request.getRequestDispatcher("html/user/results.jsp").forward(request, response);

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
}
