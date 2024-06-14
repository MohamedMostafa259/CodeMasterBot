# CodeMasterBot
![Background](https://github.com/MohamedMostafa259/CodeMasterBot/blob/main/Background.png)
## Table of Contents
- [Introdunction](introduction)
- [Features](features)
- [Conversation Flow](#conversation-flow)
- [Example Interaction](#example-interaction)
- [Dataset Format](#dataset-format)
- [How To Run The Chatbot](#how-to-run-the-chatbot)
- [Project Demo](#project-demo)

## Introduction
CodeMasterBot is a Scala 3 project designed to provide a flexible interactive chatbot experience for users seeking information about Python programming. 
The bot uses a dataset to answer questions about Python, delivering responses in a friendly and educational manner. 
The project includes a GUI to enhance user interaction, making it easy to ask questions and receive answers in real time.

## Features
  - **Interactive Chatbot:** Answer questions related to Python programming.
  - **Dataset-Driven Responses:** Uses a CSV dataset to generate accurate and informative answers.
  - **Multi-Answer Capability:** Provides detailed responses for initial questions and additional explanations if needed.
  - **Common Responses:** Handles common conversational phrases and greetings.
  - **Web-Based GUI:** Accessible through a web browser for a user-friendly experience.

## Conversation Flow:
1. When a user enters a greeting message, like "Hello", it greets the user and asks how it can help them.
2. The user asks for clarifications about any ambiguous concepts about Python programming accordingly.
3. Upon receiving a query, the chatbot provides an answer.
4. If the user indicates that they do not understand the response, like saying "Explain", "I don't understand", ..., the 
chatbot offers an alternative explanation to clarify the concept.
5. The conversation will end when the user enters a closing message, for example, "Goodbye".

## Example Interaction
**User Question:**

How do I use the if statement in Python?

**Bot Response:**

if statements in Python:

If statements are used to execute different code blocks based on conditions. If a condition is true, the associated block of code is executed; otherwise, it skips to the next condition or the else block if provided.

## Dataset Format
The question-answer dataset is stored in a CSV file named python_dataset.csv. Each line in the CSV file represents a question-answer pair with the following format:<br><br>
keywords, answer1, answer2<br>
interpreter, Answer for the first response, Answer for the second response<br>
if Statement, Answer for the first response, Answer for the second response<br>
...<br>

keywords: A comma-separated list of keywords associated with the question.<br>
answer1: The primary answer to the question, provided when the question is first asked.<br>
answer2: A more elaborate answer or clarification is provided when the user requests a further explanation.<br>

**Note:** The CSV file uses special characters to represent special formatting within the answers:<br>
$: Represents a comma (",") in the answer text.<br>
@#: Represents a line break ("\<br>") in the answer text.<br>

## How To Run The Chatbot
1. Ensure you have Scala 3 and the required libraries installed
2. Run the program using the following command: `scala projectGUI.scala`
3. Access the Web GUI: click on the following link that will appear in the console: http://localhost:8000.

## Project Demo
[Click Here](https://drive.google.com/file/d/1JnUarRMrLOCEFEFKRJw2sVbabeV_1q8L/view?usp=drive_link) To Watch My Demo

**_____________________________________________________________________________________________________________________________**

***Enjoy using CodeMasterBot to participate in meaningful conversations and embark on an exciting journey towards mastering Python!***
**_____________________________________________________________________________________________________________________________**
