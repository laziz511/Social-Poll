package com.dev.socialPoll.service.impl;

import com.dev.socialPoll.dao.DaoFactory;
import com.dev.socialPoll.dao.TopicDao;
import com.dev.socialPoll.entity.Topic;
import com.dev.socialPoll.exception.DaoException;
import com.dev.socialPoll.exception.ServiceException;
import com.dev.socialPoll.service.TopicService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class TopicServiceImpl implements TopicService {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<Topic> retrieveTopics() throws ServiceException {
        try {
            TopicDao topicDao = DaoFactory.getInstance().getTopicDao();
            return topicDao.findAll();
        } catch (DaoException e) {
            logger.error("Unable to retrieve topics!");
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
    public Optional<Topic> retrieveTopicById(long topicId) throws ServiceException {
        try {
            TopicDao topicDao = DaoFactory.getInstance().getTopicDao();
            Optional<Topic> result;
            result = topicDao.findById(topicId);
            return result;
        } catch (DaoException e) {
            logger.error("Unable to retrieve topic by id!");
            throw new ServiceException(e.getMessage(),e);
        }
    }

    @Override
        public void updateNumPollsForTopic(long topicId) throws ServiceException {
        try {
                TopicDao topicDao = DaoFactory.getInstance().getTopicDao();

                Optional<Topic> optionalTopic = topicDao.findById(topicId);
                if (optionalTopic.isPresent()) {
                    Topic topic = optionalTopic.get();

                int currentNumPolls = topic.getNumPolls();
                topic.setNumPolls(currentNumPolls + 1);

                topicDao.update(topic);
            }
        } catch (DaoException e) {
            logger.error("Unable to update number of polls for the topic!", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void incrementNumParticipantsForTopic(long topicId) throws ServiceException {
        try {
            TopicDao topicDao = DaoFactory.getInstance().getTopicDao();
            topicDao.incrementNumParticipants(topicId);
        } catch (DaoException e) {
            logger.error("Unable to increment the number of participants for the topic!", e);
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void decreaseNumParticipants(long topicId, int numParticipants) throws ServiceException {
        try {
            TopicDao topicDao = DaoFactory.getInstance().getTopicDao();
            topicDao.decreaseNumParticipants(topicId, numParticipants);
        } catch (DaoException e) {
            logger.error("Error occurred while decreasing the number of participants for topic with ID: " + topicId, e);
            throw new ServiceException("Failed to decrease the number of participants for topic with ID: " + topicId, e);
        }
    }

    @Override
    public void decreaseNumPolls(long topicId) throws ServiceException {
        try {
            TopicDao topicDao = DaoFactory.getInstance().getTopicDao();
            topicDao.decreaseNumPolls(topicId);
        } catch (DaoException e) {
            logger.error("Error occurred while decreasing the number of polls for topic with ID: " + topicId, e);
            throw new ServiceException("Failed to decrease the number of polls for topic with ID: " + topicId, e);
        }
    }

}
