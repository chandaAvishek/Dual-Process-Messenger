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

 package com.assignment.dto;

 import static com.assignment.process.single.SingleProcessApp.MESSAGE_LIMIT;
 import java.util.concurrent.atomic.AtomicInteger;
 
 public class Player {
 
     private final String name;
     private final AtomicInteger sentCounter = new AtomicInteger(0);
     private final AtomicInteger receivedCounter = new AtomicInteger(0);
 
     public Player(String name) {
         this.name = name;
     }
 
     public String getName() {
         return name;
     }
 
     public int incrementAndGetSentCount() {
         int currentCount = sentCounter.get();
         if (currentCount >= MESSAGE_LIMIT) {
             return currentCount;
         }
         return sentCounter.incrementAndGet();
     }
 
     public int incrementAndGetReceivedCount() {
         return receivedCounter.incrementAndGet();
     }
 
     public int getSentCount() {
         return sentCounter.get();
     }
 
     public int getReceivedCount() {
         return receivedCounter.get();
     }
 
     public void logMessageReceived(String messageContent) {
         int count = this.incrementAndGetReceivedCount();
         System.out.println("[" + this.name + "] Received: " + messageContent + " (Total Received: " + count + ")");
     }
 }
 