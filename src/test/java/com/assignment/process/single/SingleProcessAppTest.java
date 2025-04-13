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
 import org.junit.jupiter.api.Test;
 import static org.junit.jupiter.api.Assertions.*;
 
 import java.util.concurrent.BlockingQueue;
 import java.util.concurrent.LinkedBlockingQueue;
 
 public class SingleProcessAppTest {
 
     @Test
     void testMessageExchange() throws InterruptedException {
         // Set up players
         Player initiator = new Player("Initiator");
         Player responder = new Player("Responder");
 
         // Set up communication queues
         BlockingQueue<String> toResponderQueue = new LinkedBlockingQueue<>();
         BlockingQueue<String> toInitiatorQueue = new LinkedBlockingQueue<>();
 
         // Test initial message
         String initialMessage = "ping " + initiator.incrementAndGetSentCount();
         toResponderQueue.put(initialMessage);
 
         // Verify initial message
         assertEquals("ping 1", toResponderQueue.take());
         assertEquals(1, initiator.getSentCount());
 
         // Test responder reply
         responder.logMessageReceived(initialMessage);
         String reply = initialMessage + " " + responder.incrementAndGetSentCount();
         toInitiatorQueue.put(reply);
 
         // Verify reply
         assertEquals("ping 1 1", toInitiatorQueue.take());
         assertEquals(1, responder.getSentCount());
         assertEquals(1, responder.getReceivedCount());
     }
 
     @Test
     void testMessageLimits() {
         Player player = new Player("TestPlayer");
 
         // Test message count limits
         for (int i = 0; i < 10; i++) { // Using literal 10 instead of MESSAGE_LIMIT
             assertEquals(i + 1, player.incrementAndGetSentCount());
         }
 
         assertEquals(10, player.getSentCount());
         // Verify that count doesn't increase beyond limit
         assertEquals(10, player.incrementAndGetSentCount());
         assertEquals(10, player.getSentCount());
 
     }
 }
 