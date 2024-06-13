const chatBox = document.getElementById('chat-box');
const userInput = document.getElementById('user-input');
const sendButton = document.getElementById('send-button');

sendButton.addEventListener('click', function () {
    const userMessage = userInput.value.trim();
    if (userMessage !== '') {
        sendMessage(userMessage);
        userInput.value = '';
    }
});

userInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        const userMessage = userInput.value.trim();
        if (userMessage !== '') {
            sendMessage(userMessage);
            userInput.value = '';
        }
    }
});

function sendMessage(message) {
    fetch('http://localhost:8000/submit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ message: message }),
    })
    .then(response => response.text())
    .then(data => {
        displayMessage(data); // Display the bot's response
    })
    .catch(error => console.error('Error:', error));
}

function displayMessage(message) {
    const messageElement = document.createElement('div');
    messageElement.classList.add('message');

    // Regex pattern to match URLs in the message
    const urlPattern = /(https?:\/\/[^\s]+)/g;
    
    // Replace URLs with clickable anchor tags
    const formattedMessage = message.replace(urlPattern, '<a href="$1" target="_blank">$1</a>');

    // Set innerHTML instead of textContent to render HTML tags
    messageElement.innerHTML = formattedMessage;

    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight; // Scroll to bottom after adding a new message
}
