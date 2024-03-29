let questionCounter = 0;
let removedQuestions = []; // List to store removed question IDs


function removeQuestion(questionId) {
  const questionsContainer = document.getElementById("questions-container");
  const questionDiv = document.getElementById(`question-${questionId}`);

  // Check if the questionDiv is not null before trying to remove it
  if (questionDiv) {
    questionsContainer.removeChild(questionDiv);

    removedQuestions.push(questionId);
  }
}


// Event delegation for dynamically generated data
const questionsContainer = document.getElementById("questions-container");
questionsContainer.addEventListener("click", function (event) {
  const target = event.target;
  if (target.tagName === "BUTTON" && target.textContent === "Remove Question") {
    const questionDiv = target.closest(".question-container");
    removeQuestion(questionDiv);
  }
});

function addQuestion() {
  console.log("addQuestion is working");
  if (questionCounter >= 20) {
    alert("You can create up to 20 questions.");
    return;
  }

  questionCounter++;

  const questionsContainer = document.getElementById("questions-container");

  const questionDiv = document.createElement("div");
  questionDiv.classList.add("question");

  const questionLabel = document.createElement("label");
  questionLabel.textContent = `Question ${questionCounter}:`;

  const questionInput = document.createElement("input");
  questionInput.type = "text";
  questionInput.name = `question${questionCounter}`;
  questionInput.placeholder = "Write your question here";
  questionInput.required = true;

  const answerOptionsLabel = document.createElement("label");
  answerOptionsLabel.textContent = "Answer Options:";

  const answerOptions = document.createElement("div");
  answerOptions.classList.add("answer-options");

  for (let i = 1; i <= 3; i++) {
    const optionInput = document.createElement("input");
    optionInput.type = "text";
    optionInput.name = `question${questionCounter}-option${i}`;
    optionInput.placeholder = `Option ${i}`;
    optionInput.required = true;
    answerOptions.appendChild(optionInput);
  }

  const addOptionButton = document.createElement("button");
  addOptionButton.textContent = "Add Option";
  addOptionButton.type = "button";
  addOptionButton.onclick = function () {
    addOption(answerOptions);
  };

  questionDiv.appendChild(questionLabel);
  questionDiv.appendChild(questionInput);
  questionDiv.appendChild(answerOptionsLabel);
  questionDiv.appendChild(answerOptions);
  questionDiv.appendChild(addOptionButton);

  questionsContainer.appendChild(questionDiv);
}

function addOption(answerOptions) {
  console.log("addOption is working");
  if (answerOptions.children.length >= 5) {
    alert("You can add up to 5 answer options.");
    return;
  }

  const optionInput = document.createElement("input");
  optionInput.type = "text";
  optionInput.name = `question${questionCounter}-option${answerOptions.children.length + 1}`;
  optionInput.placeholder = `Option ${answerOptions.children.length + 1}`;
  optionInput.required = true;
  answerOptions.appendChild(optionInput);
}

const pollForm = document.getElementById("manage-poll-form");
pollForm.addEventListener("submit", saveChanges);


function saveChanges(event) {
  console.log("saveChanges is working");
  event.preventDefault();

  const pollForm = event.target;
  const questionCountInput = pollForm.querySelector("#questionCount");
  const questionsContainer = document.getElementById("questions-container");
  const removedQuestionsInput = document.getElementById("removedQuestionsInput");

  questionCountInput.value = questionCounter;
  removedQuestionsInput.value = removedQuestions.join(","); // Convert the array to a comma-separated string
  console.log("Removed question IDs:", removedQuestions);

  // Submit the form
  pollForm.submit();
}


document.addEventListener("DOMContentLoaded", function () {
    var deletePollButton = document.getElementById("delete-poll-button");
    var actionInput = document.getElementById("action");

    deletePollButton.addEventListener("click", function () {
        actionInput.value = "deletePoll";
    });
});
