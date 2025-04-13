/*
 * Copyright 2025 Avishek Chanda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package com.assignment.process.multiple;

 import com.assignment.dto.Player;
 import java.io.*;
 import java.net.*;
 
 public class MultiProcessApp {
     private static final int PORT = 8080;
     private static final String HOST = "localhost";
     private static final int MESSAGE_LIMIT = 10;
 
     public static void main(String[] args) throws IOException {
         if (args.length == 0) {
             System.err.println("Error: Role required ('initiator' or 'responder').");
             System.exit(1);
         }
         String role = args[0];
         Player player = new Player(role);
 
         System.out.println("[" + player.getName() + " Process] Starting...");
 
         if ("initiator".equalsIgnoreCase(role)) {
             initiateCommunication(player);
         } else if ("responder".equalsIgnoreCase(role)) {
             respondToCommunication(player);
         } else {
             System.err.println("Error: Invalid role specified: " + role);
             System.exit(1);
         }
         System.out.println("[" + player.getName() + " Process] Finished. Sent: " + player.getSentCount()
                 + ", Received: " + player.getReceivedCount());
     }
 
     /**
      * Runs the initiator logic: connects, sends initial message, loops
      * send/receive.
      */
     private static void initiateCommunication(Player player) throws IOException {
         System.out.println("[" + player.getName() + "] Attempting to connect to " + HOST + ":" + PORT + "...");
         try (Socket socket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
 
             System.out.println("[" + player.getName() + "] Connection established.");
 
             // Prepare and send the very first message
             String currentMessageContent = "ping " + player.incrementAndGetSentCount();
             System.out.println("[" + player.getName() + "] SENDING: " + currentMessageContent + " (Sent Count: "
                     + player.getSentCount() + ")");
             out.println(currentMessageContent);
 
             // Loop until 10 replies are received
             while (player.getReceivedCount() < MESSAGE_LIMIT) {
                 // Wait for the reply from the Responder
                 String replyContent = in.readLine();
                 if (replyContent == null) {
                     System.err.println("[" + player.getName() + "] Error: Connection closed by responder prematurely.");
                     break;
                 }
                 player.logMessageReceived(replyContent);
 
                 // Check if we need to send another message (stop after receiving 10th reply)
                 if (player.getReceivedCount() < MESSAGE_LIMIT) {
                     // Prepare next message based on the reply, adding our next sent count
                     currentMessageContent = replyContent + " " + player.incrementAndGetSentCount();
                     System.out.println("[" + player.getName() + "] SENDING: " + currentMessageContent + " (Sent Count: "
                             + player.getSentCount() + ")");
                     out.println(currentMessageContent);
                 } else {
                     System.out.println("[" + player.getName() + "] Received " + MESSAGE_LIMIT
                             + " replies. Stopping communication.");
                 }
             }
 
         } catch (ConnectException e) {
             System.err.println("[" + player.getName() + "] Error: Connection refused. Is the responder running on port "
                     + PORT + "?");
         } catch (IOException e) {
             System.err.println("[" + player.getName() + "] I/O Error: " + e.getMessage());
             throw e;
         }
     }
 
     
     private static void respondToCommunication(Player player) throws IOException {
         System.out.println("[" + player.getName() + "] Listening on port " + PORT + "...");
         try (ServerSocket serverSocket = new ServerSocket(PORT);
                 Socket clientSocket = serverSocket.accept(); // Wait for initiator connection
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
 
             System.out.println("[" + player.getName() + "] Initiator connected from "
                     + clientSocket.getRemoteSocketAddress() + ".");
 
             String receivedContent;
             // Loop reading messages from the initiator
             while ((receivedContent = in.readLine()) != null) {
                 // Use logMessageReceived from the Player POJO
                 player.logMessageReceived(receivedContent);
 
                 // Optional check for a stop signal
                 if ("STOP".equalsIgnoreCase(receivedContent)) {
                     System.out.println("[" + player.getName() + "] Received STOP signal. Closing connection.");
                     break;
                 }
 
                 // Prepare the reply: received content + this player's next sent count
                 String replyContent = receivedContent + " " + player.incrementAndGetSentCount();
                 System.out.println("[" + player.getName() + "] SENDING: " + replyContent + " (Sent Count: "
                         + player.getSentCount() + ")");
                 out.println(replyContent);
 
                 // Optional check for termination condition
                 if (player.getReceivedCount() >= MESSAGE_LIMIT) {
                     System.out.println("[" + player.getName() + "] Processed " + MESSAGE_LIMIT
                             + " messages. Waiting for initiator to close or send STOP.");
                 }
             }
             System.out.println("[" + player.getName() + "] Connection closed by initiator or STOP received.");
 
         } catch (IOException e) {
             System.err.println("[" + player.getName() + "] I/O Error: " + e.getMessage());
             throw e;
         }
     }
 }
 