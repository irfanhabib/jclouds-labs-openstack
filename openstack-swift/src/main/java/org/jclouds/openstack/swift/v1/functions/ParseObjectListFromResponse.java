/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift.v1.functions;

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.openstack.swift.v1.domain.Container;
import org.jclouds.openstack.swift.v1.domain.ObjectList;
import org.jclouds.openstack.swift.v1.domain.SwiftObject;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ParseObjectListFromResponse implements Function<HttpResponse, ObjectList>,
      InvocationContext<ParseObjectListFromResponse> {

   private static final class InternalObject {
      String name;
      String hash;
      long bytes;
      String content_type;
      Date last_modified;
   }

   private final ParseJson<List<InternalObject>> json;
   private final ParseContainerFromHeaders parseContainer;

   @Inject
   ParseObjectListFromResponse(ParseJson<List<InternalObject>> json, ParseContainerFromHeaders parseContainer) {
      this.json = json;
      this.parseContainer = parseContainer;
   }

   private ToSwiftObject toSwiftObject;

   @Override
   public ObjectList apply(HttpResponse from) {
      List<SwiftObject> objects = Lists.transform(json.apply(from), toSwiftObject);
      Container container = parseContainer.apply(from);
      return ObjectList.create(objects, container);
   }

   static class ToSwiftObject implements Function<InternalObject, SwiftObject> {
      private final String containerUri;

      ToSwiftObject(String containerUri) {
         this.containerUri = containerUri;
      }

      @Override
      public SwiftObject apply(InternalObject input) {
         return SwiftObject.builder() //
               .uri(URI.create(String.format("%s%s", containerUri, input.name))) //
               .name(input.name) //
               .etag(input.hash) //
               .payload(payload(input.bytes, input.content_type)) //
               .lastModified(input.last_modified).build();
      }
   }

   @Override
   public ParseObjectListFromResponse setContext(HttpRequest request) {
      parseContainer.name = GeneratedHttpRequest.class.cast(request).getCaller().get().getArgs().get(1).toString();
      String containerUri = request.getEndpoint().toString();
      int queryIndex = containerUri.indexOf('?');
      if (queryIndex != -1) {
         containerUri = containerUri.substring(0, queryIndex);
      }
      toSwiftObject = new ToSwiftObject(containerUri);
      return this;
   }

   private static final byte[] NO_CONTENT = new byte[] {};

   private static Payload payload(long bytes, String contentType) {
      Payload payload = Payloads.newByteArrayPayload(NO_CONTENT);
      payload.getContentMetadata().setContentLength(bytes);
      payload.getContentMetadata().setContentType(contentType);
      return payload;
   }
}
