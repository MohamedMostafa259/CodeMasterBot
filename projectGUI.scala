import scala.util.matching.Regex
import scala.io.Source
import scala.io.StdIn._
import scala.util.Random

/*  Helper Methods  */
/****************************************************/
val dataset = loadDataFromCSV("Python_dataset.csv") // it's a global variable to follow the DRY principle (Don't Repeat Yourself)
def loadDataFromCSV(filePath: String): Map[List[String], List[String]] = {
	val bufferedSource = Source.fromFile(filePath)
	val lines = bufferedSource.getLines.toList
	bufferedSource.close()

	// acc is a map that maps lists of keywords to their corresponding list of answers
	def loadLines(lines: List[String], acc: Map[List[String], List[String]]): Map[List[String], List[String]] = {
		lines match {
			case Nil => acc
			case line :: rest =>
				val List(keywords, answer1, answer2) = line.split(",").toList

				val keywordsList = keywords.toLowerCase().split("\\s*\\$\\s*").toList // \\s* matches zero or more whitespace characters

				// In the answers in the CSV file (dataset), each "$" represents ", " and each "@#" represents "\n"
				val cleanedAnswer1 = answer1.replaceAll("\\s*\\$\\s*", ", ").replaceAll("\\s*@#\\s*", "<br>") // <br> in web is like '\n' in console 
				val cleanedAnswer2 = answer2.replaceAll("\\s*\\$\\s*", ", ").replaceAll("\\s*@#\\s*", "<br>")

				val answerList = List(cleanedAnswer1, cleanedAnswer2)

				loadLines(rest, acc + (keywordsList -> answerList))
		}
	}

	loadLines(lines.tail, Map[List[String], List[String]]()) // starts reading from lines.tail as lines.head is the header line
}

def extractDatasetKeywords(): List[String] = {
	dataset.keys.toList.flatten.distinct // returns a unique version of dataset keywords 
}

/* Example:
	Q: How to install Python?
	words = List(how, to, install, python)
	filteredWords = List(install, python)
	Output: List(install, python, install python, python install)
*/
def parseQuestion(question: String): List[String] = {
	val words = question.toLowerCase.split("[\\s,.?!]+").toList.distinct // split the question into lowercase unique words
	val filteredWords = words.filterNot(Set("a", "an", "the", "are", "was", "were", "has", "have", "what", "which", "where", "when", "could", "can",
											"difference", "between", "among", "please", "at", "to", "of", "on", "it", "its", "other", "another",
											"their", "compare", "vs", "any", "anything", "everything", "every", "each", "all", "that", "how",  
											"most", "does", "do", "did", "work", "works", "implemented", "implement", "implementation",
											"sorry", "I", "you", "question", "answer", "today"))

	def combinationsHelper(words: List[String], n: Int): List[String] = {
		if (n <= 0) List("")
		else {
			for {
				subWords <- words.combinations(n).toList
				combo <- subWords.permutations.toList
			} yield combo.mkString(" ")
		}
	}

	Range.inclusive(1, filteredWords.length).flatMap(n => combinationsHelper(filteredWords, n)).toList // generate all possible combinations
}

def commonResponses(): List[(Regex, String)] = {
	List(
		"(?i)\\b(my name)\\b".r -> Random.shuffle(List(
			"Hello, How are you today?",
			"I'm doing well, thank you for asking.",
			"Nice to meet you! What can I do for you today?"
		)).head,

		"(?i)\\b(your name)\\b".r -> Random.shuffle(List(
			"My name is CodeMasterBot, but you can just call me robot and I'm a chatbot.",
			"I'm CodeMasterBot, your friendly chatbot assistant.",
			"I go by the name CodeMasterBot. How can I assist you today?"
		)).head,

		"(?i)\\b(how are you)\\b".r -> Random.shuffle(List(
			"I am great!",
			"Feeling good today, thank you.",
			"I'm doing well, thanks for asking."
		)).head,

		"(?i)\\b(hi|hey|hello|hola)\\b".r -> Random.shuffle(List(
			greetUser()
		)).head,

		"(?i)\\b(created you)\\b".r -> Random.shuffle(List(
			"Top secret ;)",
			"I was created by a team of developers.",
			"I came into existence through programming."
		)).head,

		"(?i)\\b(I'm|I am)\\b.*\\b(fine|well|great|happy|good)\\b".r -> Random.shuffle(List(
			"That's nice to hear. Do you have any questions about Python?",
			"Glad to hear that! Anything specific you'd like to discuss?",
			"Great! Feel free to ask me anything."
		)).head,

		"(?i)\\b(No)\\b".r -> Random.shuffle(List(
			"Sorry, my answer was wrong. I appreciate your patience. Could you repeat your question again?",
			"No problem, let's try again.",
			"Thanks for letting me know. Let's give it another shot."
		)).head,

		"(?i)\\b(are you ready)\\b".r -> Random.shuffle(List(
			"Yeah, go ahead!",
			"I'm ready when you are!",
			"Ready and waiting!"
		)).head,

		"(?i)\\b(great|well done|nice work)\\b".r -> Random.shuffle(List(
			"Thanks!",
			"Glad you think so!",
			"Great indeed!"
		)).head,
		"(?i)\\b(what can you do)\\b".r -> Random.shuffle(List(
			"I can provide information, answer questions, and assist with various topics about Python. Feel free to ask!",
			"I'm here to help you with whatever you need. Just let me know!",
			"I can help you by explaining concepts about Python. What would you like to know?",
			greetUser()
		)).head,
		"(?i)\\b(how old are you)\\b".r -> Random.shuffle(List(
			"Age is just a number for me. I'm here to assist you!",
			"I don't have an age. I'm just a program designed to help.",
			"I'm a timeless entity here to assist you whenever you need!"
		)).head,
		"(?i)\\b(thank you|thanks|appreciate it|thank u)\\b".r -> Random.shuffle(List(
			"You're welcome!",
			"No problem! Happy to help.",
			"Anytime! Let me know if you need anything else."
		)).head
	)
}

def askForExplanation_queries(): List[Regex] = {
	List(
		"(?i)\\b(I didn't understand|I did not understand|I do not understand|I haven't understood|I have not understood|I don't understand|n't understand|not understand|explain|elaborate|what do you mean|provide more information|more information|provide information|confused)\\b".r,
		"(?i)\\b(why)\\b".r 
	)
}

/****************************************************/

// find a match recursively
// There are two answers for each list of keywords in the dataset. The 1st answer is generated when the question asked for the 1st time and
// the 2nd answer 2 is generated when the user asks for more explanation. So, The answerType value could be 1 or 2
def searchAnswer(userKeywords: List[String], dataset: Map[List[String], List[String]], answerType: Int): List[String] = {
	def searchHelper(data: List[(List[String], List[String])], acc: List[String]): List[String] = {
		data.toList match {
			case Nil => acc.reverse 
			case (keys, values) :: tail =>
				if (userKeywords.intersect(keys).nonEmpty) {
					val result = values(answerType - 1) // Get the answer based on answerType
					searchHelper(tail, result :: acc) // Recurse with updated acc
				} 
				else {
					searchHelper(tail, acc) // Recurse with updated acc
				}
		}
	}

	searchHelper(dataset.toList, Nil)
}

def handleUserInput(userQuestion: String): List[String] = {
	val uniqueKeywords = extractDatasetKeywords()
	val userQuestionList = parseQuestion(userQuestion)
	uniqueKeywords.filter(word => userQuestionList.contains(word)) // returns the keywords in the user question
}

// The purpose of the "answerType" value is explained in the "searchAnswer" function documentation
def generateResponse(userQuestion: String, answerType: Int): List[String] = {
	val userKeywords = handleUserInput(userQuestion)
	if (userKeywords.isEmpty) 
		List("I'm sorry, I didn't understand that. ", "I appreciate your patience.<br>", "Feel free to ask me any question :)<br>") 
	else 
		searchAnswer(userKeywords, dataset, answerType)
}

def greetUser(): String = {
	"Hello! I'm CodeMasterBot. You can any ask me any question about Python.<br>" +
	"This class will guide you from beginner to mastery level in Python programming, helping you become a skillful programmer.<br>" +
	"Let's embark on an exciting journey towards mastering Python!<br>" +
	"How can I help you today?"
}

// ************** GUI Cooking **************//

import java.net.InetSocketAddress
import java.io.{File, FileInputStream, InputStreamReader, BufferedReader, OutputStream}
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.util.{Try, Success, Failure}
import scala.util.chaining._
import scala.jdk.CollectionConverters._
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}


@main def main(): Unit = {
	val server = HttpServer.create(new InetSocketAddress(8000), 0)
	server.createContext("/", new StaticHandler("index.html")) // Serve static files from root
	server.createContext("/styles.css", new StaticHandler("styles.css")) // Serve CSS file
	server.createContext("/script.js", new StaticHandler("script.js")) // Serve JavaScript file
	server.createContext("/submit", new ChatbotHandler()) // Handle chatbot requests
	server.setExecutor(null)
	server.start()
	println("CodeMasterBot UI is running on http://localhost:8000")
}

// Handler to serve static files
class StaticHandler(fileName: String) extends HttpHandler {
	override def handle(exchange: HttpExchange): Unit = {
		val resourceStream = getClass.getResourceAsStream(s"/$fileName")
		if (resourceStream != null) {
			val responseBytes = resourceStream.readAllBytes()
			exchange.sendResponseHeaders(200, responseBytes.length)
			val os = exchange.getResponseBody
			os.write(responseBytes)
			os.close()
		}
		else {
			exchange.sendResponseHeaders(404, 0) // File not found
			exchange.getResponseBody.close()
		}
	}
}

object JsonParser {
  private val jsonPattern: Regex = """\{.*?"message"\s*:\s*"([^"]*)".*?\}""".r

  def extractMessage(jsonString: String): String = {
    jsonString match {
      case jsonPattern(message) => message
      case _ => jsonString
    }
  }
}

var lastQuestion = ""
class ChatbotHandler extends HttpHandler {
	override def handle(exchange: HttpExchange): Unit = {
		val requestMethod = exchange.getRequestMethod
		if (requestMethod.equalsIgnoreCase("POST")) {
			val inputStream = exchange.getRequestBody
			val bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			val requestBody = JsonParser.extractMessage(bufferedReader.readLine()) 
	
			// if a user wants to exit the conversation
			val exitPattern = "\\b(exit|pause|quit|goodbye|good bye|bye|see you later|talk to you soon|that's all for now|i'm done|see you soon)\\b".r // \b is a word boundary that ensures the word is matched as a whole word, not as part of a larger word.
			if (exitPattern.findFirstIn(requestBody.toLowerCase).isDefined) {
				sendResponse(exchange, formattedResponse(requestBody, "It was nice talking to you. See you soon :)")) // Send termination message
				System.exit(0) // Terminate the program
			} 

			val commonResponse = commonResponses().find((pattern, answer) => pattern.findFirstIn(requestBody.toLowerCase).isDefined)		
			if (commonResponse.isDefined) {
				val (_, answer) = commonResponse.get
				sendResponse(exchange, formattedResponse(requestBody, answer))
			}

			else {
				val askForExplanation_query = askForExplanation_queries().find(pattern => pattern.findFirstIn(requestBody.toLowerCase).isDefined)
				// The purpose of the 2nd parameter in generateResponse() function, which is the "answerType" value, 
				// is explained in the "searchAnswer" function documentation. It can equal 1 or 2
				if (askForExplanation_query.isDefined) {
					val botResponse = generateResponse(lastQuestion + " " + requestBody, 2).mkString("<br>") 
					sendResponse(exchange, formattedResponse(requestBody, botResponse))
				}
				else {
					lastQuestion = requestBody 
					val botResponse = generateResponse(requestBody, 1).mkString("<br>")
					sendResponse(exchange, formattedResponse(requestBody, botResponse))
				}
			}
		}
		else {
			exchange.sendResponseHeaders(405, 0) // Method Not Allowed for other HTTP methods
			exchange.getResponseBody.close()
		}
	}

	private def sendResponse(exchange: HttpExchange, response: String): Unit = {
		exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length)
		val os: OutputStream = exchange.getResponseBody
		os.write(response.getBytes(StandardCharsets.UTF_8))
		os.close()
	}

	// written in HTML style as it deals with a web client
	private def formattedResponse(userInput: String, botResponse: String): String = {
		// To extract the actual text content from the JSON string like {"message":"Hello!"}. So, it will be "Hello!" only
		val message = JsonParser.extractMessage(userInput)
		s"""
		<html>
		<head>
			<style>
			.chat-container {
				border: 1px solid #ccc;
				border-radius: 5px;
				padding: 10px;
				margin: 10px 0;
				background-color: rgba(0, 0, 0, 0.5); /* Transparent white color */
			}
			.user-message {
				font-weight: bold;
				color: #008B8B;
			}
			.bot-message {
				font-weight: bold;
				color: #008B8B;
			}
			</style>
		</head>
		<body>
			<div class="chat-container">
			<span class="user-message">You:</span> $message <br><br>
			<span class="bot-message">CodeMasterBot:<br></span> $botResponse
			</div>
		</body>
		</html>
		"""
	}

	// version of formattedResponse() function for console client
	// private def formattedResponse(userInput: String, botResponse: String): String = {
	// 	"\n" +
	// 	"*****************************************************************************\n" +
	// 	"You: " + userInput + "\n" +
	// 	"CodeMasterBot: " + botResponse + "\n" +
	// 	"*****************************************************************************\n" +
	// 	"\n"
	// }
}