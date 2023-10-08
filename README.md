Letschat API
Letschat API is a real-time chatting API built upon Websocket and STOMP using a simple message broker provided by Spring. It's designed to provide a robust and efficient solution for real-time communication needs.

Features
User Authentication: Ensures that only authenticated users can access the chat.
Email Verification: Verifies the user's email during signup and password recovery.
JWT Support: Uses JSON Web Tokens (JWT) for secure transmission of information.
Message Support: Supports sending text messages and images.
User Status: Displays online status, typing status, message sent, read, and delivered status.
Message Deletion: Allows users to delete messages.
Chat History: Fetches previous chats on login and undelivered chats when the user comes online.
Private Conversations: Maintains private conversations with users. It's not a chat room, so your conversations are always private.
