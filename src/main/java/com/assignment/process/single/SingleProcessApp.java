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


 package com.assignment.process.single;

 import com.assignment.dto.Player;
 
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.LinkedBlockingQueue;
 import java.util.concurrent.TimeUnit;
 
 public class SingleProcessApp {
 
     public static final int MESSAGE_LIMIT = 10;
     private static final long POLL_TIMEOUT_MS = 100;
 
     public static void main(String[] args) {
         // Create players (using the simple POJO version)
         Player initiator = new Player("Initiator");
         Player responder = new Player("Responder");
 
         // Create BlockingQueues for String communication
         BlockingQueue<String> toResponderQueue = new LinkedBlockingQueue<>();
         BlockingQueue<String> toInitiatorQueue = new LinkedBlockingQueue<>();
 
         System.out.println("--- Starting Sequential Simulation with String Queues and Poll ---");
 
         boolean initiatorTurn = true; // Start with initiator's turn to send
         String messageToSend = null; // Holds the message content to be sent
 
         try {
             // *** Initial Send by Initiator ***
             messageToSend = "ping " + initiator.incrementAndGetSentCount();
             System.out.println("[" + initiator.getName() + "] Sent: " + messageToSend + " (Total Sent: "
                     + initiator.getSentCount() + ")");
             toResponderQueue.put(messageToSend);
             initiatorTurn = false; // Now it's responder's turn to receive/reply
 
             while (initiator.getReceivedCount() < MESSAGE_LIMIT) {
                 if (initiatorTurn) {
                     // *** Initiator's Turn to Receive/Send ***
                     System.out.println("[" + initiator.getName() + "]: Waiting for reply...");
                     String contentToInitiator = toInitiatorQueue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
 
                     if (contentToInitiator != null) {
                         initiator.logMessageReceived(contentToInitiator);
 
                         if (initiator.getReceivedCount() < MESSAGE_LIMIT && initiator.getSentCount() < MESSAGE_LIMIT) {
                             messageToSend = contentToInitiator + " " + initiator.incrementAndGetSentCount();
                             System.out.println("[" + initiator.getName() + "] Sent: " + messageToSend + " (Total Sent: "
                                     + initiator.getSentCount() + ")");
                             toResponderQueue.put(messageToSend);
                             initiatorTurn = false; // Switch turn
                         } else {
                             System.out.println("[" + initiator.getName() + "] Reached message limit. Stopping sends.");
                             // Keep polling queue to allow responder to finish its last send if needed
                             initiatorTurn = false; // Switch turn
                         }
                     } else {
                         System.out.println("[" + initiator.getName() + "]: Poll timed out waiting for reply.");
                         // Stay on initiator's turn to try polling again
                     }
 
                 } else {
                     // *** Responder's Turn to Receive/Send ***
                     System.out.println("[" + responder.getName() + "]: Waiting for message...");
                     String contentToResponder = toResponderQueue.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
 
                     if (contentToResponder != null) {
                         responder.logMessageReceived(contentToResponder);
 
                         if (responder.getSentCount() < MESSAGE_LIMIT) {
                             messageToSend = contentToResponder + " " + responder.incrementAndGetSentCount();
                             System.out.println("[" + responder.getName() + "] Sent: " + messageToSend + " (Total Sent: "
                                     + responder.getSentCount() + ")");
                             toInitiatorQueue.put(messageToSend);
                             initiatorTurn = true; // Switch turn
                         } else {
                             System.out.println("[" + responder.getName() + "] Reached send limit. Stopping sends.");
                             // Need to let initiator poll for the last message
                             initiatorTurn = true; // Switch turn
                         }
                     } else {
                         System.out.println("[" + responder.getName() + "]: Poll timed out waiting for message.");
                         // Stay on responder's turn to try polling again
                     }
                 }
             }
         } catch (InterruptedException e) {
             System.err.println("Simulation interrupted.");
             Thread.currentThread().interrupt(); //
         }
 
         System.out.println("\n--- Simulation Finished ---");
         System.out.println("Final Stats:");
         System.out.println("  Initiator Sent: " + initiator.getSentCount());
         System.out.println("  Initiator Received: " + initiator.getReceivedCount());
         System.out.println("  Responder Sent: " + responder.getSentCount());
         System.out.println("  Responder Received: " + responder.getReceivedCount());
         System.out.println("-----------------------------");
     }
 }
 