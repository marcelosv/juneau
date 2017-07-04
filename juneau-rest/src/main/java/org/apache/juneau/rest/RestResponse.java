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
package org.apache.juneau.rest;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.encoders.*;
import org.apache.juneau.html.*;
import org.apache.juneau.http.*;
import org.apache.juneau.jena.*;
import org.apache.juneau.json.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.urlencoding.*;
import org.apache.juneau.utils.*;
import org.apache.juneau.xml.*;

/**
 * Represents an HTTP response for a REST resource.
 *
 * <p>
 * Essentially an extended {@link HttpServletResponse} with some special convenience methods that allow you to easily
 * output POJOs as responses.
 *
 * <p>
 * Since this class extends {@link HttpServletResponse}, developers are free to use these convenience methods, or
 * revert to using lower level methods like any other servlet response.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode'>
 * 	<ja>@RestMethod</ja>(name=<js>"GET"</js>)
 * 	<jk>public void</jk> doGet(RestRequest req, RestResponse res) {
 * 		res.setPageTitle(<js>"My title"</js>)
 * 			.setOutput(<js>"Simple string response"</js>);
 * 	}
 * </p>
 *
 * <p>
 * Refer to <a class="doclink" href="package-summary.html#TOC">REST Servlet API</a> for information about using this
 * class.
 */
public final class RestResponse extends HttpServletResponseWrapper {

	private final RestRequest request;
	private Object output;                       // The POJO being sent to the output.
	private boolean isNullOutput;                // The output is null (as opposed to not being set at all)
	private ObjectMap properties;                // Response properties
	SerializerGroup serializerGroup;
	UrlEncodingSerializer urlEncodingSerializer; // The serializer used to convert arguments passed into Redirect objects.
	private EncoderGroup encoders;
	private ServletOutputStream os;
	private PrintWriter w;

	/**
	 * Constructor.
	 */
	RestResponse(RestContext context, RestRequest req, HttpServletResponse res) {
		super(res);
		this.request = req;

		for (Map.Entry<String,Object> e : context.getDefaultResponseHeaders().entrySet())
			setHeader(e.getKey(), e.getValue().toString());

		try {
			String passThroughHeaders = req.getHeader("x-response-headers");
			if (passThroughHeaders != null) {
				PartParser p = context.getUrlEncodingParser();
				ObjectMap m = p.parse(PartType.HEADER, passThroughHeaders, context.getBeanContext().getClassMeta(ObjectMap.class));
				for (Map.Entry<String,Object> e : m.entrySet())
					setHeader(e.getKey(), e.getValue().toString());
			}
		} catch (Exception e1) {
			throw new RestException(SC_BAD_REQUEST, "Invalid format for header 'x-response-headers'.  Must be in URL-encoded format.").initCause(e1);
		}
	}

	/*
	 * Called from RestServlet after a match has been made but before the guard or method invocation.
	 */
	@SuppressWarnings("hiding")
	final void init(ObjectMap properties, String defaultCharset, SerializerGroup mSerializers, UrlEncodingSerializer mUrlEncodingSerializer, EncoderGroup encoders) {
		this.properties = properties;
		this.serializerGroup = mSerializers;
		this.urlEncodingSerializer = mUrlEncodingSerializer;
		this.encoders = encoders;

		// Find acceptable charset
		String h = request.getHeader("accept-charset");
		String charset = null;
		if (h == null)
			charset = defaultCharset;
		else for (MediaTypeRange r : MediaTypeRange.parse(h)) {
			if (r.getQValue() > 0) {
				MediaType mt = r.getMediaType();
				if (mt.getType().equals("*"))
					charset = defaultCharset;
				else if (Charset.isSupported(mt.getType()))
					charset = mt.getType();
				if (charset != null)
					break;
			}
		}

		if (charset == null)
			throw new RestException(SC_NOT_ACCEPTABLE, "No supported charsets in header ''Accept-Charset'': ''{0}''", request.getHeader("Accept-Charset"));
		super.setCharacterEncoding(charset);
	}

	/**
	 * Gets the serializer group for the response.
	 *
	 * @return The serializer group for the response.
	 */
	public SerializerGroup getSerializerGroup() {
		return serializerGroup;
	}

	/**
	 * Returns the media types that are valid for <code>Accept</code> headers on the request.
	 *
	 * @return The set of media types registered in the parser group of this request.
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return serializerGroup.getSupportedMediaTypes();
	}

	/**
	 * Returns the codings that are valid for <code>Accept-Encoding</code> and <code>Content-Encoding</code> headers on
	 * the request.
	 *
	 * @return The set of media types registered in the parser group of this request.
	 * @throws RestServletException
	 */
	public List<String> getSupportedEncodings() throws RestServletException {
		return encoders.getSupportedEncodings();
	}

	/**
	 * Sets the HTTP output on the response.
	 *
	 * <p>
	 * Calling this method is functionally equivalent to returning the object in the REST Java method.
	 *
	 * <p>
	 * Can be of any of the following types:
	 * <ul>
	 * 	<li> {@link InputStream}
	 * 	<li> {@link Reader}
	 * 	<li> Any serializable type defined in <a class="doclink"
	 * 		href="../../../../overview-summary.html#Core.PojoCategories">POJO Categories</a>
	 * </ul>
	 *
	 * <p>
	 * If it's an {@link InputStream} or {@link Reader}, you must also specify the <code>Content-Type</code> using the
	 * {@link #setContentType(String)} method.
	 *
	 * @param output The output to serialize to the connection.
	 * @return This object (for method chaining).
	 */
	public RestResponse setOutput(Object output) {
		this.output = output;
		this.isNullOutput = output == null;
		return this;
	}

	/**
	 * Add a serializer property to send to the serializers to override a default value.
	 *
	 * <p>
	 * Can be any value specified in the following classes:
	 * <ul>
	 * 	<li>{@link SerializerContext}
	 * 	<li>{@link JsonSerializerContext}
	 * 	<li>{@link XmlSerializerContext}
	 * 	<li>{@link RdfSerializerContext}
	 * </ul>
	 *
	 * @param key The setting name.
	 * @param value The setting value.
	 * @return This object (for method chaining).
	 */
	public RestResponse setProperty(String key, Object value) {
		properties.put(key, value);
		return this;
	}

	/**
	 * Returns the properties set via {@link #setProperty(String, Object)}.
	 *
	 * @return A map of all the property values set.
	 */
	public ObjectMap getProperties() {
		return properties;
	}

	/**
	 * Shortcut method that allows you to use var-args to simplify setting array output.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<jc>// Instead of...</jc>
	 * 	response.setOutput(<jk>new</jk> Object[]{x,y,z});
	 *
	 * 	<jc>// ...call this...</jc>
	 * 	response.setOutput(x,y,z);
	 * </p>
	 *
	 * @param output The output to serialize to the connection.
	 * @return This object (for method chaining).
	 */
	public RestResponse setOutputs(Object...output) {
		this.output = output;
		return this;
	}

	/**
	 * Returns the output that was set by calling {@link #setOutput(Object)}.
	 *
	 * @return The output object.
	 */
	public Object getOutput() {
		return output;
	}

	/**
	 * Returns <jk>true</jk> if this response has any output associated with it.
	 *
	 * @return <jk>true</jk> if {@code setInput()} has been called.
	 */
	public boolean hasOutput() {
		return output != null || isNullOutput;
	}

	/**
	 * Sets the output to a plain-text message regardless of the content type.
	 *
	 * @param text The output text to send.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred trying to write to the writer.
	 */
	public RestResponse sendPlainText(String text) throws IOException {
		setContentType("text/plain");
		getNegotiatedWriter().write(text);
		return this;
	}

	/**
	 * Equivalent to {@link HttpServletResponse#getOutputStream()}, except wraps the output stream if an {@link Encoder}
	 * was found that matched the <code>Accept-Encoding</code> header.
	 *
	 * @return A negotiated output stream.
	 * @throws IOException
	 */
	public ServletOutputStream getNegotiatedOutputStream() throws IOException {
		if (os == null) {
			Encoder encoder = null;

			String ae = request.getHeader("Accept-Encoding");
			if (! (ae == null || ae.isEmpty())) {
				EncoderMatch match = encoders != null ? encoders.getEncoderMatch(ae) : null;
				if (match == null) {
					// Identity should always match unless "identity;q=0" or "*;q=0" is specified.
					if (ae.matches(".*(identity|\\*)\\s*;\\s*q\\s*=\\s*(0(?!\\.)|0\\.0).*")) {
						throw new RestException(SC_NOT_ACCEPTABLE,
							"Unsupported encoding in request header ''Accept-Encoding'': ''{0}''\n\tSupported codings: {1}",
							ae, encoders.getSupportedEncodings()
						);
					}
				} else {
					encoder = match.getEncoder();
					String encoding = match.getEncoding().toString();

					// Some clients don't recognize identity as an encoding, so don't set it.
					if (! encoding.equals("identity"))
						setHeader("content-encoding", encoding);
				}
			}
			os = getOutputStream();
			if (encoder != null) {
				final OutputStream os2 = encoder.getOutputStream(os);
				os = new ServletOutputStream(){
					@Override /* OutputStream */
					public final void write(byte[] b, int off, int len) throws IOException {
						os2.write(b, off, len);
					}
					@Override /* OutputStream */
					public final void write(int b) throws IOException {
						os2.write(b);
					}
					@Override /* OutputStream */
					public final void flush() throws IOException {
						os2.flush();
					}
					@Override /* OutputStream */
					public final void close() throws IOException {
						os2.close();
					}
				};
			}
		}
		return os;
	}

	@Override /* ServletResponse */
	public ServletOutputStream getOutputStream() throws IOException {
		if (os == null)
			os = super.getOutputStream();
		return os;
	}

	/**
	 * Returns <jk>true</jk> if {@link #getOutputStream()} has been called.
	 *
	 * @return <jk>true</jk> if {@link #getOutputStream()} has been called.
	 */
	public boolean getOutputStreamCalled() {
		return os != null;
	}

	/**
	 * Returns the writer to the response body.
	 *
	 * <p>
	 * This methods bypasses any specified encoders and returns a regular unbuffered writer.
	 * Use the {@link #getNegotiatedWriter()} method if you want to use the matched encoder (if any).
	 */
	@Override /* ServletResponse */
	public PrintWriter getWriter() throws IOException {
		return getWriter(true);
	}

	/**
	 * Convenience method meant to be used when rendering directly to a browser with no buffering.
	 *
	 * <p>
	 * Sets the header <js>"x-content-type-options=nosniff"</js> so that output is rendered immediately on IE and Chrome
	 * without any buffering for content-type sniffing.
	 *
	 * @param contentType The value to set as the <code>Content-Type</code> on the response.
	 * @return The raw writer.
	 * @throws IOException
	 */
	public PrintWriter getDirectWriter(String contentType) throws IOException {
		setContentType(contentType);
		setHeader("x-content-type-options", "nosniff");
		return getWriter();
	}

	/**
	 * Equivalent to {@link HttpServletResponse#getWriter()}, except wraps the output stream if an {@link Encoder} was
	 * found that matched the <code>Accept-Encoding</code> header and sets the <code>Content-Encoding</code>
	 * header to the appropriate value.
	 *
	 * @return The negotiated writer.
	 * @throws IOException
	 */
	public PrintWriter getNegotiatedWriter() throws IOException {
		return getWriter(false);
	}

	private PrintWriter getWriter(boolean raw) throws IOException {
		if (w != null)
			return w;

		// If plain text requested, override it now.
		if (request.isPlainText())
			setHeader("Content-Type", "text/plain");

		try {
			OutputStream out = (raw ? getOutputStream() : getNegotiatedOutputStream());
			w = new PrintWriter(new OutputStreamWriter(out, getCharacterEncoding()));
			return w;
		} catch (UnsupportedEncodingException e) {
			String ce = getCharacterEncoding();
			setCharacterEncoding("UTF-8");
			throw new RestException(SC_NOT_ACCEPTABLE, "Unsupported charset in request header ''Accept-Charset'': ''{0}''", ce);
		}
	}

	/**
	 * Returns the <code>Content-Type</code> header stripped of the charset attribute if present.
	 *
	 * @return The <code>media-type</code> portion of the <code>Content-Type</code> header.
	 */
	public MediaType getMediaType() {
		return MediaType.forString(getContentType());
	}

	/**
	 * Redirects to the specified URI.
	 *
	 * <p>
	 * Relative URIs are always interpreted as relative to the context root.
	 * This is similar to how WAS handles redirect requests, and is different from how Tomcat handles redirect requests.
	 */
	@Override /* ServletResponse */
	public void sendRedirect(String uri) throws IOException {
		char c = (uri.length() > 0 ? uri.charAt(0) : 0);
		if (c != '/' && uri.indexOf("://") == -1)
			uri = request.getContextPath() + '/' + uri;
		super.sendRedirect(uri);
	}

	/**
	 * Returns the URL-encoding serializer associated with this response.
	 *
	 * @return The URL-encoding serializer associated with this response.
	 */
	public UrlEncodingSerializer getUrlEncodingSerializer() {
		return urlEncodingSerializer;
	}

	@Override /* ServletResponse */
	public void setHeader(String name, String value) {
		// Jetty doesn't set the content type correctly if set through this method.
		// Tomcat/WAS does.
		if (name.equalsIgnoreCase("Content-Type"))
			super.setContentType(value);
		else
			super.setHeader(name, value);
	}

	/**
	 * Sets the HTML page title.
	 *
	 * <p>
	 * The format of this value is plain text.
	 *
	 * <p>
	 * It gets wrapped in a <code><xt>&lt;h3&gt; <xa>class</xa>=<xs>'title'</xs>&gt;</xt></code> element and then added
	 * to the <code><xt>&lt;header&gt;</code> section on the page.
	 *
	 * <p>
	 * If not specified, the page title is pulled from one of the following locations:
	 * <ol>
	 * 	<li><code>{servletClass}.{methodName}.pageTitle</code> resource bundle value.
	 * 	<li><code>{servletClass}.pageTitle</code> resource bundle value.
	 * 	<li><code><ja>@RestResource</ja>(title)</code> annotation.
	 * 	<li><code>{servletClass}.title</code> resource bundle value.
	 * 	<li><code>info/title</code> entry in swagger file.
	 * </ol>
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <ul class='doctree'>
	 * 	<li class='info'>
	 * 		In most cases, you'll simply want to use the <code>@RestResource(title)</code> annotation to specify the
	 * 		page title.
	 * 		However, this annotation is provided in cases where you want the page title to be different that the one
	 * 		shown in the swagger document.
	 * </ul>
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#title() @HtmlDoc.title()} annotation.
	 *
	 * @param value
	 * 	The HTML page title.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlTitle(Object value) {
		return setProperty(HtmlDocSerializerContext.HTMLDOC_title, value);
	}

	/**
	 * Sets the HTML page description.
	 *
	 * <p>
	 * The format of this value is plain text.
	 *
	 * <p>
	 * It gets wrapped in a <code><xt>&lt;h5&gt; <xa>class</xa>=<xs>'description'</xs>&gt;</xt></code> element and then
	 * added to the <code><xt>&lt;header&gt;</code> section on the page.
	 *
	 * <p>
	 * If not specified, the page title is pulled from one of the following locations:
	 * <ol>
	 * 	<li><code>{servletClass}.{methodName}.pageText</code> resource bundle value.
	 * 	<li><code>{servletClass}.pageText</code> resource bundle value.
	 * 	<li><code><ja>@RestMethod</ja>(summary)</code> annotation.
	 * 	<li><code>{servletClass}.{methodName}.summary</code> resource bundle value.
	 * 	<li><code>summary</code> entry in swagger file for method.
	 * 	<li><code>{servletClass}.description</code> resource bundle value.
	 * 	<li><code>info/description</code> entry in swagger file.
	 * </ol>
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <ul class='doctree'>
	 * 	<li class='info'>
	 * 		In most cases, you'll simply want to use the <code>@RestResource(description)</code> or
	 * 		<code>@RestMethod(summary)</code> annotations to specify the page text.
	 * 		However, this annotation is provided in cases where you want the text to be different that the values shown
	 * 		in the swagger document.
	 * </ul>
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#description() @HtmlDoc.description()} annotation.
	 *
	 * @param value
	 * 	The HTML page description.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlDescription(Object value) {
		return setProperty(HtmlDocSerializerContext.HTMLDOC_description, value);
	}

	/**
	 * Sets the HTML page branding in the header section of the page generated by the default HTML doc template.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * This is arbitrary HTML that can be added to the header section to provide basic custom branding on the page.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#branding() @HtmlDoc.branding()} annotation.
	 *
	 * @param value
	 * 	The HTML page branding.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlBranding(Object value) {
		return setProperty(HtmlDocSerializerContext.HTMLDOC_branding, value);
	}

	/**
	 * Sets the HTML header section contents.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * The page header normally contains the title and description, but this value can be used to override the contents
	 * to be whatever you want.
	 *
	 * <p>
	 * When a value is specified, the {@link #setHtmlTitle(Object)} and {@link #setHtmlDescription(Object)} values will
	 * be ignored.
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no header.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#header() @HtmlDoc.header()} annotation.
	 *
	 * @param value
	 * 	The HTML header section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlHeader(Object value) {
		return setProperty(HtmlDocSerializerContext.HTMLDOC_title, value);
	}

	/**
	 * Sets the links in the HTML nav section.
	 *
	 * <p>
	 * The format of this value is a lax-JSON map of key/value pairs where the keys are the link text and the values are
	 * relative (to the servlet) or absolute URLs.
	 *
	 * <p>
	 * The page links are positioned immediately under the title and text.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This field can also use URIs of any support type in {@link UriResolver}.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#links() @HtmlDoc.links()} annotation.
	 *
	 * @param value
	 * 	The HTML nav section links links.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlLinks(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_links, value);
		return this;
	}

	/**
	 * Sets the HTML nav section contents.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * The nav section of the page contains the links.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * When a value is specified, the {@link #setHtmlLinks(Object)} value will be ignored.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#nav() @HtmlDoc.nav()} annotation.
	 *
	 * @param value
	 * 	The HTML nav section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlNav(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_nav, value);
		return this;
	}

	/**
	 * Sets the HTML aside section contents.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * The aside section typically floats on the right side of the page.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#aside() @HtmlDoc.aside()} annotation.
	 *
	 * @param value
	 * 	The HTML aside section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to waste
	 * 				string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlAside(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_aside, value);
		return this;
	}

	/**
	 * Sets the HTML footer section contents.
	 *
	 * <p>
	 * The format of this value is HTML.
	 *
	 * <p>
	 * The footer section typically floats on the bottom of the page.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#footer() @HtmlDoc.footer()} annotation.
	 *
	 * @param value
	 * 	The HTML footer section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlFooter(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_footer, value);
		return this;
	}

	/**
	 * Sets the HTML CSS style section contents.
	 *
	 * <p>
	 * The format of this value is CSS.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#style() @HtmlDoc.style()} annotation.
	 *
	 * @param value
	 * 	The HTML CSS style section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlStyle(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_style, value);
		return this;
	}

	/**
	 * Sets the CSS URL in the HTML CSS style section.
	 *
	 * <p>
	 * The format of this value is a comma-delimited list of URLs.
	 *
	 * <p>
	 * Specifies the URL to the stylesheet to add as a link in the style tag in the header.
	 *
	 * <p>
	 * The format of this value is CSS.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>) and can use URL protocols defined
	 * by {@link UriResolver}.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#stylesheet() @HtmlDoc.stylesheet()} annotation.
	 *
	 * @param value
	 * 	The CSS URL in the HTML CSS style section.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlStylesheet(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_stylesheet, value);
		return this;
	}

	/**
	 * Sets the HTML script section contents.
	 *
	 * <p>
	 * The format of this value is Javascript.
	 *
	 * <p>
	 * This field can contain variables (e.g. <js>"$L{my.localized.variable}"</js>).
	 *
	 * <p>
	 * A value of <js>"NONE"</js> can be used to force no value.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#script() @HtmlDoc.script()} annotation.
	 *
	 * @param value
	 * 	The HTML script section contents.
	 * 	Object will be converted to a string using {@link Object#toString()}.
	 * 	<p>
	 * 	<ul class='doctree'>
	 * 		<li class='info'>
	 * 			<b>Tip:</b>  Use {@link StringMessage} to generate value with delayed serialization so as not to
	 * 				waste string concatenation cycles on non-HTML views.
	 * 	</ul>
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlScript(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_script, value);
		return this;
	}

	/**
	 * Shorthand method for forcing the rendered HTML content to be no-wrap.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#nowrap() @HtmlDoc.nowrap()} annotation.
	 *
	 * @param value The new nowrap setting.
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlNoWrap(boolean value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_nowrap, value);
		return this;
	}

	/**
	 * Specifies the text to display when serializing an empty array or collection.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#noResultsMessage() @HtmlDoc.noResultsMessage()}
	 * annotation.
	 *
	 * @param value The text to display when serializing an empty array or collection.
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlNoResultsMessage(Object value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_noResultsMessage, value);
		return this;
	}

	/**
	 * Specifies the template class to use for rendering the HTML page.
	 *
	 * <p>
	 * By default, uses {@link HtmlDocTemplateBasic} to render the contents, although you can provide your own custom
	 * renderer or subclasses from the basic class to have full control over how the page is rendered.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#template() @HtmlDoc.template()} annotation.
	 *
	 * @param value The HTML page template to use to render the HTML page.
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlTemplate(Class<? extends HtmlDocTemplate> value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_template, value);
		return this;
	}

	/**
	 * Specifies the template class to use for rendering the HTML page.
	 *
	 * <p>
	 * By default, uses {@link HtmlDocTemplateBasic} to render the contents, although you can provide your own custom
	 * renderer or subclasses from the basic class to have full control over how the page is rendered.
	 *
	 * <p>
	 * This is the programmatic equivalent to the {@link HtmlDoc#template() @HtmlDoc.template()} annotation.
	 *
	 * @param value The HTML page template to use to render the HTML page.
	 * @return This object (for method chaining).
	 */
	public RestResponse setHtmlTemplate(HtmlDocTemplate value) {
		properties.put(HtmlDocSerializerContext.HTMLDOC_template, value);
		return this;
	}

	@Override /* ServletResponse */
	public void flushBuffer() throws IOException {
		if (w != null)
			w.flush();
		if (os != null)
			os.flush();
		super.flushBuffer();
	}
}