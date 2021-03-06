/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.marconi.v1.domain;

import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

/**
 * Queue statistics, including how many messages are in the queue.
 *
 * @author Everett Toews
 */
public class QueueStats {

   private MessagesStats messages;

   protected QueueStats(MessagesStats messages) {
      this.messages = messages;
   }

   /**
    * @return The statistics of the messages in this queue.
    */
   public MessagesStats getMessagesStats() {
      return messages;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(messages);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      QueueStats that = QueueStats.class.cast(obj);
      return Objects.equal(this.messages, that.messages);
   }

   protected Objects.ToStringHelper string() {
      return Objects.toStringHelper(this)
         .add("messagesStats", messages);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
