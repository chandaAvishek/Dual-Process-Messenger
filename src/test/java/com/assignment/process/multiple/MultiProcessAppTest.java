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

 import org.junit.jupiter.api.Test;
 import java.io.*;
 import java.util.concurrent.TimeUnit;
 
 import static org.junit.jupiter.api.Assertions.assertTrue;
 
 public class MultiProcessAppTest {
 
     private static final String JAVA_CMD = System.getProperty("java.home") + "/bin/java";
     private static final String CLASS_PATH = System.getProperty("java.class.path");
 
     @Test
     void testMultiprocessCommunication() throws IOException, InterruptedException {
         // Start responder process
         Process responderProcess = startProcess("responder");
 
         // Wait to ensure the server socket is ready
         Thread.sleep(1000);
 
         // Start initiator process
         Process initiatorProcess = startProcess("initiator");
 
         // Capture output from both processes
         String initiatorOutput = captureOutput(initiatorProcess);
         String responderOutput = captureOutput(responderProcess);
 
         // Wait for both processes to finish
         boolean initiatorFinished = initiatorProcess.waitFor(15, TimeUnit.SECONDS);
         boolean responderFinished = responderProcess.waitFor(15, TimeUnit.SECONDS);
 
         // Assertions
         assertTrue(initiatorFinished, "Initiator process should finish");
         assertTrue(responderFinished, "Responder process should finish");
 
         assertTrue(initiatorOutput.contains("SENDING: ping 1"), "Initiator should have sent initial message");
         assertTrue(initiatorOutput.contains("Received 10 replies"), "Initiator should receive 10 replies");
         assertTrue(responderOutput.contains("Listening on port"), "Responder should listen for connections");
         assertTrue(responderOutput.contains("SENDING: ping 1 1"), "Responder should respond to message");
     }
 
     private Process startProcess(String role) throws IOException {
         ProcessBuilder builder = new ProcessBuilder(
                 JAVA_CMD,
                 "-cp",
                 CLASS_PATH,
                 MultiProcessApp.class.getName(),
                 role);
         builder.redirectErrorStream(true);
         return builder.start();
     }
 
     private String captureOutput(Process process) throws IOException {
         StringBuilder output = new StringBuilder();
         try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(process.getInputStream()))) {
             String line;
             while ((line = reader.readLine()) != null) {
                 output.append(line).append("\n");
             }
         }
         return output.toString();
     }
 }
 