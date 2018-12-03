// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.microservice.config.resources;

import static org.apache.juneau.http.HttpMethodName.*;

import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;

@RestResource(
        title="Config",
        description="",
        path="/config",
        htmldoc=@HtmlDoc(
                aside={
                        "<div style='max-width:400px' class='text'>",
                        "	<p>This page shows a resource that simply response with a 'Hello world!' message</p>",
                        "	<p>The POJO serialized is a simple String.</p>",
                        "</div>"
                }
        )
)

public class ConfigResources implements BasicRestConfig {

    @RestMethod(name=GET, path="/{name}/{label}")
    public String configs() {
        return "Hello world!";
    }
}
