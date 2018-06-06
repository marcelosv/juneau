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
package org.apache.juneau.rest.annotation;

/**
 * Extended annotation for {@link RestResource#swagger() @RestResource.swagger()}.
 * 
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'><a class="doclink" href="../../../../../overview-summary.html#juneau-rest-server.OptionsPages">Overview &gt; juneau-rest-server &gt; OPTIONS pages</a>
 * </ul>
 */
public @interface ResourceSwagger {
	
	/**
	 * Defines the the free-form contents of the swagger.
	 * 
	 * <p>
	 * Used to populate the auto-generated OPTIONS swagger documentation.
	 * 
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		<jc>// Swagger info.</jc>
	 * 		swagger=@ResourceSwagger({
	 * 			<js>"title:'Petstore application',"</js>,
	 * 			<js>"description:"</js>,
	 * 				<js>"'This is a sample server Petstore server based on the Petstore sample at Swagger.io."</js>,
	 * 				+ <js>"\nYou can find out more about Swagger at &lt;a class=\'link\' href=\'http://swagger.io\'&gt;http://swagger.io&lt;/a&gt;.',"</js>,
	 * 			<js>"contact:{name:'John Smith',email:'john@smith.com'},"</js>,
	 * 			<js>"license:{name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'},"</js>,
	 * 			<js>"version:'2.0',</js>,
	 * 			<js>"termsOfService:'You are on your own.',"</js>,
	 * 			<js>"tags:[{name:'Java',description:'Java utility',externalDocs:{description:'Home page',url:'http://juneau.apache.org'}}],"</js>,
	 * 			<js>"externalDocs:{description:'Home page',url:'http://juneau.apache.org'}"</js>
	 * 		})
	 * 	)
	 * </p>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		<jc>// Swagger info pulled from file.</jc>
	 * 		swagger=@ResourceSwagger("$F{MyResource.json}")
	 * 	)
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is a JSON object.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		The starting and ending <js>'{'</js>/<js>'}'</js> characters are optional.
	 * 	<li>
	 * 		If a Swagger JSON file <code>{resource-class-simple-name}_{locale}.json</code> is present in the same package on the classpath, the values
	 * 		defined in this annotation will be superimposed on the values pulled from the Swagger JSON file.
	 * 	<li>
	 * 		The other annotation values defined here are superimposed on the values defined by this value.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String[] value() default {};
	
	/**
	 * Defines the swagger field <code>/info/title</code>.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@ResourceSwagger</ja>(
	 * 			title=<js>"Petstore application"</js>
	 * 		)
	 * 	)
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is plain-text.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		The precedence of lookup for this field is:
	 * 		<ol>
	 * 			<li><code>{resource-class}.title</code> property in resource bundle.
	 * 			<li>{@link ResourceSwagger#title()} on this class, then any parent classes.
	 * 			<li>Value defined in Swagger JSON file.
	 * 			<li>{@link RestResource#title()} on this class, then any parent classes.
	 * 			<li>
	 * 		</ol>
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String[] title() default {};
	
	/**
	 * Defines the swagger field <code>/info/description</code>.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@ResourceSwagger</ja>(
	 * 			description={
	 * 				<js>"This is a sample server Petstore server based on the Petstore sample at Swagger.io."<js>,
	 * 				<js>"You can find out more about Swagger at &lt;a class='link' href='http://swagger.io'&gt;http://swagger.io&lt;/a&gt;."</js>
	 * 			}
	 * 		)
	 * 	)
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is plain text.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		The precedence of lookup for this field is:
	 * 		<ol>
	 * 			<li><code>{resource-class}.description</code> property in resource bundle.
	 * 			<li>{@link ResourceSwagger#description()} on this class, then any parent classes.
	 * 			<li>Value defined in Swagger JSON file.
	 * 			<li>{@link RestResource#description()} on this class, then any parent classes.
	 * 			<li>
	 * 		</ol>
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String[] description() default {};

	/**
	 * Defines the swagger field <code>/info/contact</code>.
	 * 
	 * <p>
	 * A simplified JSON string with the following fields:
	 * <p class='bcode'>
	 * 	{
	 * 		name: string,
	 * 		url: string,
	 * 		email: string
	 * 	}
	 * </p>
	 * 
	 * <p>
	 * The default value pulls the description from the <code>contact</code> entry in the servlet resource bundle.
	 * (e.g. <js>"contact = {name:'John Smith',email:'john.smith@foo.bar'}"</js> or
	 * <js>"MyServlet.contact = {name:'John Smith',email:'john.smith@foo.bar'}"</js>).
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@MethodSwagger</ja>(
	 * 			contact=<js>"{name:'John Smith',email:'john.smith@foo.bar'}"</js>
	 * 		)
	 * 	)
	 * </p>
	swagger={
		"info: {",
			"contact:{name:'Juneau Developer',email:'dev@juneau.apache.org'},",
			"license:{name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'},",
			"version:'2.0',",
			"termsOfService:'You are on your own.'",
		"},",
		"externalDocs:{description:'Apache Juneau',url:'http://juneau.apache.org'}"
	}
	swagger=@ResourceSwagger(
		contact="name:'Juneau Developer',email:'dev@juneau.apache.org'",
		license="name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'",
		version="2.0",
		termsOfService="You are on your own.",
		externalDocs="description:'Apache Juneau',url:'http://juneau.apache.org'}"
	)
	swagger=@ResourceSwagger("$F{PetStoreResource.json}"),
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is a JSON object.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	Contact contact() default @Contact;

	/**
	 * Defines the swagger field <code>/externalDocs</code>.
	 * 
	 * <p>
	 * It is used to populate the Swagger external documentation field and to display on HTML pages.
	 * 	 * 
	 * <p>
	 * The default value pulls the description from the <code>externalDocs</code> entry in the servlet resource bundle.
	 * (e.g. <js>"externalDocs = {url:'http://juneau.apache.org'}"</js> or
	 * <js>"MyServlet.externalDocs = {url:'http://juneau.apache.org'}"</js>).
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@MethodSwagger</ja>(
	 * 			externalDocs=<ja>@ExternalDocs</ja>(url=<js>"http://juneau.apache.org"</js>)
	 * 		)
	 * 	)
	 * </p>
	 */
	ExternalDocs externalDocs() default @ExternalDocs;

	/**
	 * Defines the swagger field <code>/info/license</code>.
	 * 
	 * <p>
	 * It is used to populate the Swagger license field and to display on HTML pages.
	 * 
	 * <p>
	 * A simplified JSON string with the following fields:
	 * <p class='bcode'>
	 * 	{
	 * 		name: string,
	 * 		url: string
	 * 	}
	 * </p>
	 * 
	 * <p>
	 * The default value pulls the description from the <code>license</code> entry in the servlet resource bundle.
	 * (e.g. <js>"license = {name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'}"</js> or
	 * <js>"MyServlet.license = {name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'}"</js>).
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@MethodSwagger</ja>(
	 * 			license=<js>"{name:'Apache 2.0',url:'http://www.apache.org/licenses/LICENSE-2.0.html'}"</js>
	 * 		)
	 * 	)
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is a JSON object.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	License license() default @License;

	/**
	 * Defines the swagger field <code>/tags</code>.
	 * 
	 * 
	 * Optional tagging information for the exposed API.
	 * 
	 * <p>
	 * It is used to populate the Swagger tags field and to display on HTML pages.
	 * 
	 * <p>
	 * A simplified JSON string with the following fields:
	 * <p class='bcode'>
	 * 	[
	 * 		{
	 * 			name: string,
	 * 			description: string,
	 * 			externalDocs: {
	 * 				description: string,
	 * 				url: string
	 * 			}
	 * 		}
	 * 	]
	 * </p>
	 * 
	 * <p>
	 * The default value pulls the description from the <code>tags</code> entry in the servlet resource bundle.
	 * (e.g. <js>"tags = [{name:'Foo',description:'Foobar'}]"</js> or
	 * <js>"MyServlet.tags = [{name:'Foo',description:'Foobar'}]"</js>).
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestResource</ja>(
	 * 		swagger=<ja>@MethodSwagger</ja>(
	 * 			tags=<js>"[{name:'Foo',description:'Foobar'}]"</js>
	 * 		)
	 * 	)
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is a JSON array.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	Tag[] tags() default {};
	
	/**
	 * Defines the swagger field <code>/info/termsOfService</code>.
	 * 
	 * 
	 * Optional servlet terms-of-service for this API.
	 * 
	 * <p>
	 * It is used to populate the Swagger terms-of-service field.
	 * 
	 * <p>
	 * The default value pulls the description from the <code>termsOfService</code> entry in the servlet resource bundle.
	 * (e.g. <js>"termsOfService = foo"</js> or <js>"MyServlet.termsOfService = foo"</js>).
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is plain text.
	 * 		<br>Multiple lines are concatenated with newlines.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String[] termsOfService() default {};

	/**
	 * Defines the swagger field <code>/info/version</code>.
	 * 
	 * 
	 * 
	 * Provides the version of the application API (not to be confused with the specification version).
	 * 
	 * <p>
	 * It is used to populate the Swagger version field and to display on HTML pages.
	 * 
	 * <p>
	 * The default value pulls the description from the <code>version</code> entry in the servlet resource bundle.
	 * (e.g. <js>"version = 2.0"</js> or <js>"MyServlet.version = 2.0"</js>).
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		The format is plain text.
	 * 	<li>
	 * 		Supports <a class="doclink" href="../../../../../overview-summary.html#DefaultRestSvlVariables">initialization-time and request-time variables</a> 
	 * 		(e.g. <js>"$L{my.localized.variable}"</js>).
	 * </ul>
	 */
	String version() default "";
}
