
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<!DOCTYPE html>

<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="css/user/home.css">

        fetchpriority="low" />
</head>

<body>
    <!-- navbar start -->
    <div class="navbar">
        <div class="navbar-container">

            <div class="logo-container">
                <svg fill="none" viewBox="0 0 245 55" width="100"></svg>
                <h1>My Voice</h1>
            </div>

            <div class="navbar-menu">
                <a href="home" class="navbar-menu-element">Home</a>
                <a href="admin-dashboard" class="navbar-menu-element"> My polls</a>
                <a href="#" class="navbar-menu-element">My Submissions</a>
                <a href="log-in" class="navbar-menu-element">Log in</a>
            </div>

        </div>
    </div>
    <!-- navbar end -->


    <!-- main-section start -->
    <div class="main-section">

        <!-- landing-page start -->
        <div class="landing-page-section">
            <div class="landing-page background">
                <section class="introduction-section">
                    <h1 class="title">My Voice: Empowering Social Polls</h1>
                    <div class="description">
                        <p>Don't stay indifferent – let your opinions shape the future.
                        Take part in our engaging social polls and make a difference in your community.
                         Unleash the power of your voice today! </p>
                    </div>

                </section>
            </div>
        </div>
        <!-- landing-page end -->


        <section>
            <div class="balanced-section center1 background">
                <div class="image-container">
                    <img src="images/image1.JPG"
                        class="image-align" />
                </div>

                <div class="heading-container">
                    <h2 class="text-box">Personalized Insights, Better Data</h2>
                </div>

                <div class="para-box">
                    <p>My Voice' delivers tailored questions for a better respondent experience.
                    Get quality data, clear reports, and valuable insights for impactful decisions in your society.</p>
                </div>
            </div>
        </section>


        <!-- Survey topics start -->
        <section>
            <div class="topic-section">
                    <c:if test="${not empty requestScope.pollTopics}">
                    <h2 class="text-box">Poll Topics</h2>
                    </c:if>

                    <div class="cards">
                        <c:if test="${not empty requestScope.pollTopics}">
                            <c:forEach items="${requestScope.pollTopics}" var="topic">
                                <div class="card">
                                    <h2 class="topic">${topic.topicName}</h2>
                                    <p class="description">${topic.description}</p>
                                    <div class="info">
                                        <div class="info-text">
                                            <c:if test="${not empty topic.numPolls}">
                                                <p class="survey-count">Polls: <span>${topic.numPolls}</span></p>
                                            </c:if>
                                            <c:if test="${not empty topic.numParticipants}">
                                                <p class="participant-count">Participants: <span>${topic.numParticipants}</span></p>
                                            </c:if>
                                        </div>
                                        <div class="info-button">
                                            <div>
                                                <a href="/SocialPoll/polls?topicId=${topic.id}" class="button">See Polls</a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
            </div>
        </section>
        <!-- Survey topics end -->




        <section>
            <div class="balanced-section center2 background">
                <div class="image-container"><img
                        src="images/image2.JPG"
                        width="1016" height="1052" class="image-align" /></div>

                <div class="heading-container">
                    <h2 class="text-box">Effortless and Efficient</h2>
                </div>

                <div class="para-box">
                    <p> My Voice' offers a hassle-free survey creation process,
                     allowing you to create and share beautiful polls within minutes.
                    Seamlessly integrate data with ease, no coding or complex onboarding required.</p>
                </div>
            </div>
        </section>

        <!-- footer start -->
        <section>
            <footer class="footer-section">

                <span>This is a footer</span>

                <span> My Voice</span>
            </footer>
        </section>
        <!-- footer end -->

    </div>

</body>

</html>