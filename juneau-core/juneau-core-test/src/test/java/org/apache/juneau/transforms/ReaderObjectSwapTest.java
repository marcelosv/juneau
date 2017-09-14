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
package org.apache.juneau.transforms;

import java.io.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.http.*;
import org.apache.juneau.transform.*;
import org.apache.juneau.utils.*;
import org.junit.runner.*;
import org.junit.runners.*;

/**
 * Verifies that Reader and InputStream objects are serialized correctly.
 * Note that these are one-way serializations and you're not guaranteed to produce parsable output.
 */
@RunWith(Parameterized.class)
@SuppressWarnings({"javadoc"})
public class ReaderObjectSwapTest extends ComboSerializeTest {

	@Parameterized.Parameters
	public static Collection<Object[]> getParameters() {
		return Arrays.asList(new Object[][] {
			{ 	/* 0 */
				new ComboInput<PojoToSimpleReader>(
					"PojoToSimpleReader",
					PojoToSimpleReader.class,
					new PojoToSimpleReader(),
					/* Json */		"foo",
					/* JsonT */		"foo",
					/* JsonR */		"foo",
					/* Xml */		"foo",
					/* XmlT */		"foo",
					/* XmlR */		"foo\n",
					/* XmlNs */		"foo",
					/* Html */		"foo",
					/* HtmlT */		"foo",
					/* HtmlR */		"foo",
					/* Uon */		"foo",
					/* UonT */		"foo",
					/* UonR */		"foo",
					/* UrlEnc */	"foo",
					/* UrlEncT */	"foo",
					/* UrlEncR */	"foo",
					/* MsgPack */	"666F6F",
					/* MsgPackT */	"666F6F",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>foo</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 1 */
				new ComboInput<PojoToDynamicReader>(
					"PojoToDynamicReader",
					PojoToDynamicReader.class,
					new PojoToDynamicReader("foo"),
					/* Json */		"foo-json",
					/* JsonT */		"foo-json",
					/* JsonR */		"foo-json",
					/* Xml */		"foo-xml",
					/* XmlT */		"foo-xml",
					/* XmlR */		"foo-xml\n",
					/* XmlNs */		"foo-xml",
					/* Html */		"foo-html",
					/* HtmlT */		"foo-html",
					/* HtmlR */		"foo-html",
					/* Uon */		"foo-uon",
					/* UonT */		"foo-uon",
					/* UonR */		"foo-uon",
					/* UrlEnc */	"foo-x-www-form-urlencoded",
					/* UrlEncT */	"foo-x-www-form-urlencoded",
					/* UrlEncR */	"foo-x-www-form-urlencoded",
					/* MsgPack */	"666F6F2D6D73677061636B",
					/* MsgPackT */	"666F6F2D6D73677061636B",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo-xml</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo-xml</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>foo-xml</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 2 */
				new ComboInput<SometimesSwappedBean1>(
					"SometimesSwappedBean1",
					SometimesSwappedBean1.class,
					new SometimesSwappedBean1("foo"),
					/* Json */		"foo-application/json",
					/* JsonT */		"foo-application/json",
					/* JsonR */		"foo-application/json",
					/* Xml */		"foo-text/xml",
					/* XmlT */		"foo-text/xml",
					/* XmlR */		"foo-text/xml\n",
					/* XmlNs */		"foo-text/xml",
					/* Html */		"<table><tr><td>f</td><td>foo</td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td>foo</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>foo</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=foo)",
					/* UonT */		"(f=foo)",
					/* UonR */		"(\n\tf=foo\n)",
					/* UrlEnc */	"f=foo",
					/* UrlEncT */	"f=foo",
					/* UrlEncR */	"f=foo",
					/* MsgPack */	"81A166A3666F6F",
					/* MsgPackT */	"81A166A3666F6F",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo-text/xml+rdf</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<j:value>foo-text/xml+rdf</j:value>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <j:value>foo-text/xml+rdf</j:value>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 3 */
				new ComboInput<SometimesSwappedBean2>(
					"SometimesSwappedBean2",
					SometimesSwappedBean2.class,
					new SometimesSwappedBean2("foo"),
					/* Json */		"{f:'foo'}",
					/* JsonT */		"{f:'foo'}",
					/* JsonR */		"{\n\tf: 'foo'\n}",
					/* Xml */		"<object><f>foo</f></object>",
					/* XmlT */		"<object><f>foo</f></object>",
					/* XmlR */		"<object>\n\t<f>foo</f>\n</object>\n",
					/* XmlNs */		"<object><f>foo</f></object>",
					/* Html */		"foo-text/html",
					/* HtmlT */		"foo-text/html",
					/* HtmlR */		"foo-text/html",
					/* Uon */		"foo-text/uon",
					/* UonT */		"foo-text/uon",
					/* UonR */		"foo-text/uon",
					/* UrlEnc */	"foo-application/x-www-form-urlencoded",
					/* UrlEncT */	"foo-application/x-www-form-urlencoded",
					/* UrlEncR */	"foo-application/x-www-form-urlencoded",
					/* MsgPack */	"666F6F2D6F6374616C2F6D73677061636B",
					/* MsgPackT */	"666F6F2D6F6374616C2F6D73677061636B",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>foo</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>foo</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f>foo</jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 4 */
				new ComboInput<BeanWithSwappedField>(
					"BeanWithSwappedField",
					BeanWithSwappedField.class,
					new BeanWithSwappedField("x"),
					/* Json */		"{f:x-json}",
					/* JsonT */		"{f:x-json}",
					/* JsonR */		"{\n\tf: x-json\n}",
					/* Xml */		"<object><f>x-xml</f></object>",
					/* XmlT */		"<object><f>x-xml</f></object>",
					/* XmlR */		"<object>\n\t<f>x-xml</f>\n</object>\n",
					/* XmlNs */		"<object><f>x-xml</f></object>",
					/* Html */		"<table><tr><td>f</td><td>x-html</td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td>x-html</td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>x-html</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=x-uon)",
					/* UonT */		"(f=x-uon)",
					/* UonR */		"(\n\tf=x-uon\n)",
					/* UrlEnc */	"f=x-x-www-form-urlencoded",
					/* UrlEncT */	"f=x-x-www-form-urlencoded",
					/* UrlEncR */	"f=x-x-www-form-urlencoded",
					/* MsgPack */	"81A166782D6D73677061636B",
					/* MsgPackT */	"81A166782D6D73677061636B",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>x-xml</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>x-xml</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f>x-xml</jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 5 */
				new ComboInput<BeanWithSwapped1dField>(
					"BeanWithSwapped1dField",
					BeanWithSwapped1dField.class,
					new BeanWithSwapped1dField("x"),
					/* Json */		"{f:[x1-json,x2-json,null]}",
					/* JsonT */		"{f:[x1-json,x2-json,null]}",
					/* JsonR */		"{\n\tf: [\n\t\tx1-json,\n\t\tx2-json,\n\t\tnull\n\t]\n}",
					/* Xml */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* XmlT */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* XmlR */		"<object>\n\t<f>\n\t\tx1-xml\n\t\tx2-xml\n\t\t<null/>\n\t</f>\n</object>\n",
					/* XmlNs */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* Html */		"<table><tr><td>f</td><td><ul><li>x1-html</li><li>x2-html</li><li><null/></li></ul></td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td><ul><li>x1-html</li><li>x2-html</li><li><null/></li></ul></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>\n\t\t\t<ul>\n\t\t\t\t<li>x1-html</li>\n\t\t\t\t<li>x2-html</li>\n\t\t\t\t<li><null/></li>\n\t\t\t</ul>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=@(x1-uon,x2-uon,null))",
					/* UonT */		"(f=@(x1-uon,x2-uon,null))",
					/* UonR */		"(\n\tf=@(\n\t\tx1-uon,\n\t\tx2-uon,\n\t\tnull\n\t)\n)",
					/* UrlEnc */	"f=@(x1-x-www-form-urlencoded,x2-x-www-form-urlencoded,null)",
					/* UrlEncT */	"f=@(x1-x-www-form-urlencoded,x2-x-www-form-urlencoded,null)",
					/* UrlEncR */	"f=@(\n\tx1-x-www-form-urlencoded,\n\tx2-x-www-form-urlencoded,\n\tnull\n)",
					/* MsgPack */	"81A1669378312D6D73677061636B78322D6D73677061636BC0",
					/* MsgPackT */	"81A1669378312D6D73677061636B78322D6D73677061636BC0",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li>x1-xml</rdf:li>\n<rdf:li>x2-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li>x1-xml</rdf:li>\n<rdf:li>x2-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f>\n      <rdf:Seq>\n        <rdf:li>x1-xml</rdf:li>\n        <rdf:li>x2-xml</rdf:li>\n        <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n      </rdf:Seq>\n    </jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 6 */
				new ComboInput<BeanWithSwappedNullField>(
					"BeanWithSwappedNullField",
					BeanWithSwappedNullField.class,
					new BeanWithSwappedNullField(),
					/* Json */		"{}",
					/* JsonT */		"{}",
					/* JsonR */		"{\n}",
					/* Xml */		"<object/>",
					/* XmlT */		"<object/>",
					/* XmlR */		"<object/>\n",
					/* XmlNs */		"<object/>",
					/* Html */		"<table></table>",
					/* HtmlT */		"<table></table>",
					/* HtmlR */		"<table>\n</table>\n",
					/* Uon */		"()",
					/* UonT */		"()",
					/* UonR */		"(\n)",
					/* UrlEnc */	"",
					/* UrlEncT */	"",
					/* UrlEncR */	"",
					/* MsgPack */	"80",
					/* MsgPackT */	"80",
					/* RdfXml */	"<rdf:RDF>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 7 */
				new ComboInput<BeanWithSwappedListField>(
					"BeanWithSwappedListField",
					BeanWithSwappedListField.class,
					new BeanWithSwappedListField("x"),
					/* Json */		"{f:[x1-json,x2-json,null]}",
					/* JsonT */		"{f:[x1-json,x2-json,null]}",
					/* JsonR */		"{\n\tf: [\n\t\tx1-json,\n\t\tx2-json,\n\t\tnull\n\t]\n}",
					/* Xml */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* XmlT */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* XmlR */		"<object>\n\t<f>\n\t\tx1-xml\n\t\tx2-xml\n\t\t<null/>\n\t</f>\n</object>\n",
					/* XmlNs */		"<object><f>x1-xmlx2-xml<null/></f></object>",
					/* Html */		"<table><tr><td>f</td><td><ul><li>x1-html</li><li>x2-html</li><li><null/></li></ul></td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td><ul><li>x1-html</li><li>x2-html</li><li><null/></li></ul></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>\n\t\t\t<ul>\n\t\t\t\t<li>x1-html</li>\n\t\t\t\t<li>x2-html</li>\n\t\t\t\t<li><null/></li>\n\t\t\t</ul>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=@(x1-uon,x2-uon,null))",
					/* UonT */		"(f=@(x1-uon,x2-uon,null))",
					/* UonR */		"(\n\tf=@(\n\t\tx1-uon,\n\t\tx2-uon,\n\t\tnull\n\t)\n)",
					/* UrlEnc */	"f=@(x1-x-www-form-urlencoded,x2-x-www-form-urlencoded,null)",
					/* UrlEncT */	"f=@(x1-x-www-form-urlencoded,x2-x-www-form-urlencoded,null)",
					/* UrlEncR */	"f=@(\n\tx1-x-www-form-urlencoded,\n\tx2-x-www-form-urlencoded,\n\tnull\n)",
					/* MsgPack */	"81A1669378312D6D73677061636B78322D6D73677061636BC0",
					/* MsgPackT */	"81A1669378312D6D73677061636B78322D6D73677061636BC0",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li>x1-xml</rdf:li>\n<rdf:li>x2-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li>x1-xml</rdf:li>\n<rdf:li>x2-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f>\n      <rdf:Seq>\n        <rdf:li>x1-xml</rdf:li>\n        <rdf:li>x2-xml</rdf:li>\n        <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n      </rdf:Seq>\n    </jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 8 */
				new ComboInput<BeanWithSwappedMapField>(
					"BeanWithSwappedMapField",
					BeanWithSwappedMapField.class,
					new BeanWithSwappedMapField("x"),
					/* Json */		"{f:{foo:x1-json,bar:null,null:x2-json}}",
					/* JsonT */		"{f:{foo:x1-json,bar:null,null:x2-json}}",
					/* JsonR */		"{\n\tf: {\n\t\tfoo: x1-json,\n\t\tbar: null,\n\t\tnull: x2-json\n\t}\n}",
					/* Xml */		"<object><f><foo>x1-xml</foo><bar _type='null'/><_x0000_>x2-xml</_x0000_></f></object>",
					/* XmlT */		"<object><f><foo>x1-xml</foo><bar t='null'/><_x0000_>x2-xml</_x0000_></f></object>",
					/* XmlR */		"<object>\n\t<f>\n\t\t<foo>x1-xml</foo>\n\t\t<bar _type='null'/>\n\t\t<_x0000_>x2-xml</_x0000_>\n\t</f>\n</object>\n",
					/* XmlNs */		"<object><f><foo>x1-xml</foo><bar _type='null'/><_x0000_>x2-xml</_x0000_></f></object>",
					/* Html */		"<table><tr><td>f</td><td><table><tr><td>foo</td><td>x1-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x2-html</td></tr></table></td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td><table><tr><td>foo</td><td>x1-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x2-html</td></tr></table></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>\n\t\t\t<table>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>foo</td>\n\t\t\t\t\t<td>x1-html</td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>bar</td>\n\t\t\t\t\t<td><null/></td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t<td>x2-html</td>\n\t\t\t\t</tr>\n\t\t\t</table>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=(foo=x1-uon,bar=null,null=x2-uon))",
					/* UonT */		"(f=(foo=x1-uon,bar=null,null=x2-uon))",
					/* UonR */		"(\n\tf=(\n\t\tfoo=x1-uon,\n\t\tbar=null,\n\t\tnull=x2-uon\n\t)\n)",
					/* UrlEnc */	"f=(foo=x1-x-www-form-urlencoded,bar=null,null=x2-x-www-form-urlencoded)",
					/* UrlEncT */	"f=(foo=x1-x-www-form-urlencoded,bar=null,null=x2-x-www-form-urlencoded)",
					/* UrlEncR */	"f=(\n\tfoo=x1-x-www-form-urlencoded,\n\tbar=null,\n\tnull=x2-x-www-form-urlencoded\n)",
					/* MsgPack */	"81A16683A3666F6F78312D6D73677061636BA3626172C0C078322D6D73677061636B",
					/* MsgPackT */	"81A16683A3666F6F78312D6D73677061636BA3626172C0C078322D6D73677061636B",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f rdf:parseType='Resource'>\n<jp:foo>x1-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x2-xml</jp:_x0000_>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f rdf:parseType='Resource'>\n<jp:foo>x1-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x2-xml</jp:_x0000_>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f rdf:parseType='Resource'>\n      <jp:foo>x1-xml</jp:foo>\n      <jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n      <jp:_x0000_>x2-xml</jp:_x0000_>\n    </jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 9 */
				new ComboInput<BeanWithListBeanSwappedField>(
					"BeanWithListBeanSwappedField",
					BeanWithListBeanSwappedField.class,
					new BeanWithListBeanSwappedField("x"),
					/* Json */		"{f:[{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}},null]}",
					/* JsonT */		"{f:[{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}},null]}",
					/* JsonR */		"{\n\tf: [\n\t\t{\n\t\t\tf1: x1a-json,\n\t\t\tf2: [\n\t\t\t\tx2a-json,\n\t\t\t\tx2b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf4: [\n\t\t\t\tx4a-json,\n\t\t\t\tx4b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf5: {\n\t\t\t\tfoo: x5a-json,\n\t\t\t\tbar: null,\n\t\t\t\tnull: x5c-json\n\t\t\t}\n\t\t},\n\t\tnull\n\t]\n}",
					/* Xml */		"<object><f><object><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></object><null/></f></object>",
					/* XmlT */		"<object><f><object><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar t='null'/><_x0000_>x5c-xml</_x0000_></f5></object><null/></f></object>",
					/* XmlR */		"<object>\n\t<f>\n\t\t<object>\n\t\t\t<f1>x1a-xml</f1>\n\t\t\t<f2>\n\t\t\t\tx2a-xml\n\t\t\t\tx2b-xml\n\t\t\t\t<null/>\n\t\t\t</f2>\n\t\t\t<f4>\n\t\t\t\tx4a-xml\n\t\t\t\tx4b-xml\n\t\t\t\t<null/>\n\t\t\t</f4>\n\t\t\t<f5>\n\t\t\t\t<foo>x5a-xml</foo>\n\t\t\t\t<bar _type='null'/>\n\t\t\t\t<_x0000_>x5c-xml</_x0000_>\n\t\t\t</f5>\n\t\t</object>\n\t\t<null/>\n\t</f>\n</object>\n",
					/* XmlNs */		"<object><f><object><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></object><null/></f></object>",
					/* Html */		"<table><tr><td>f</td><td><table _type='array'><tr><th>f1</th><th>f2</th><th>f4</th><th>f5</th></tr><tr><td>x1a-html</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr><tr><null/></tr></table></td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td><table t='array'><tr><th>f1</th><th>f2</th><th>f4</th><th>f5</th></tr><tr><td>x1a-html</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr><tr><null/></tr></table></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>\n\t\t\t<table _type='array'>\n\t\t\t\t<tr>\n\t\t\t\t\t<th>f1</th>\n\t\t\t\t\t<th>f2</th>\n\t\t\t\t\t<th>f4</th>\n\t\t\t\t\t<th>f5</th>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>x1a-html</td>\n\t\t\t\t\t<td>\n\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t<li>x2a-html</li>\n\t\t\t\t\t\t\t<li>x2b-html</li>\n\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t</ul>\n\t\t\t\t\t</td>\n\t\t\t\t\t<td>\n\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t<li>x4a-html</li>\n\t\t\t\t\t\t\t<li>x4b-html</li>\n\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t</ul>\n\t\t\t\t\t</td>\n\t\t\t\t\t<td>\n\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>foo</td>\n\t\t\t\t\t\t\t\t<td>x5a-html</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>bar</td>\n\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t\t<td>x5c-html</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</table>\n\t\t\t\t\t</td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<null/>\n\t\t\t\t</tr>\n\t\t\t</table>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=@((f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon)),null))",
					/* UonT */		"(f=@((f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon)),null))",
					/* UonR */		"(\n\tf=@(\n\t\t(\n\t\t\tf1=x1a-uon,\n\t\t\tf2=@(\n\t\t\t\tx2a-uon,\n\t\t\t\tx2b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf4=@(\n\t\t\t\tx4a-uon,\n\t\t\t\tx4b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf5=(\n\t\t\t\tfoo=x5a-uon,\n\t\t\t\tbar=null,\n\t\t\t\tnull=x5c-uon\n\t\t\t)\n\t\t),\n\t\tnull\n\t)\n)",
					/* UrlEnc */	"f=@((f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)),null)",
					/* UrlEncT */	"f=@((f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)),null)",
					/* UrlEncR */	"f=@(\n\t(\n\t\tf1=x1a-x-www-form-urlencoded,\n\t\tf2=@(\n\t\t\tx2a-x-www-form-urlencoded,\n\t\t\tx2b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf4=@(\n\t\t\tx4a-x-www-form-urlencoded,\n\t\t\tx4b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf5=(\n\t\t\tfoo=x5a-x-www-form-urlencoded,\n\t\t\tbar=null,\n\t\t\tnull=x5c-x-www-form-urlencoded\n\t\t)\n\t),\n\tnull\n)",
					/* MsgPack */	"81A1669284A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636BC0",
					/* MsgPackT */	"81A1669284A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636BC0",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f>\n<rdf:Seq>\n<rdf:li rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f>\n      <rdf:Seq>\n        <rdf:li rdf:parseType='Resource'>\n          <jp:f1>x1a-xml</jp:f1>\n          <jp:f2>\n            <rdf:Seq>\n              <rdf:li>x2a-xml</rdf:li>\n              <rdf:li>x2b-xml</rdf:li>\n              <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n            </rdf:Seq>\n          </jp:f2>\n          <jp:f4>\n            <rdf:Seq>\n              <rdf:li>x4a-xml</rdf:li>\n              <rdf:li>x4b-xml</rdf:li>\n              <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n            </rdf:Seq>\n          </jp:f4>\n          <jp:f5 rdf:parseType='Resource'>\n            <jp:foo>x5a-xml</jp:foo>\n            <jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n            <jp:_x0000_>x5c-xml</jp:_x0000_>\n          </jp:f5>\n        </rdf:li>\n        <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n      </rdf:Seq>\n    </jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
			{ 	/* 10 */
				new ComboInput<BeanWithMapBeanSwappedField>(
					"BeanWithMapBeanSwappedField",
					BeanWithMapBeanSwappedField.class,
					new BeanWithMapBeanSwappedField("x"),
					/* Json */		"{f:{foo:{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}},bar:null,null:{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}}}}",
					/* JsonT */		"{f:{foo:{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}},bar:null,null:{f1:x1a-json,f2:[x2a-json,x2b-json,null],f4:[x4a-json,x4b-json,null],f5:{foo:x5a-json,bar:null,null:x5c-json}}}}",
					/* JsonR */		"{\n\tf: {\n\t\tfoo: {\n\t\t\tf1: x1a-json,\n\t\t\tf2: [\n\t\t\t\tx2a-json,\n\t\t\t\tx2b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf4: [\n\t\t\t\tx4a-json,\n\t\t\t\tx4b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf5: {\n\t\t\t\tfoo: x5a-json,\n\t\t\t\tbar: null,\n\t\t\t\tnull: x5c-json\n\t\t\t}\n\t\t},\n\t\tbar: null,\n\t\tnull: {\n\t\t\tf1: x1a-json,\n\t\t\tf2: [\n\t\t\t\tx2a-json,\n\t\t\t\tx2b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf4: [\n\t\t\t\tx4a-json,\n\t\t\t\tx4b-json,\n\t\t\t\tnull\n\t\t\t],\n\t\t\tf5: {\n\t\t\t\tfoo: x5a-json,\n\t\t\t\tbar: null,\n\t\t\t\tnull: x5c-json\n\t\t\t}\n\t\t}\n\t}\n}",
					/* Xml */		"<object><f><foo><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></foo><bar _type='null'/><_x0000_><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></_x0000_></f></object>",
					/* XmlT */		"<object><f><foo><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar t='null'/><_x0000_>x5c-xml</_x0000_></f5></foo><bar t='null'/><_x0000_><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar t='null'/><_x0000_>x5c-xml</_x0000_></f5></_x0000_></f></object>",
					/* XmlR */		"<object>\n\t<f>\n\t\t<foo>\n\t\t\t<f1>x1a-xml</f1>\n\t\t\t<f2>\n\t\t\t\tx2a-xml\n\t\t\t\tx2b-xml\n\t\t\t\t<null/>\n\t\t\t</f2>\n\t\t\t<f4>\n\t\t\t\tx4a-xml\n\t\t\t\tx4b-xml\n\t\t\t\t<null/>\n\t\t\t</f4>\n\t\t\t<f5>\n\t\t\t\t<foo>x5a-xml</foo>\n\t\t\t\t<bar _type='null'/>\n\t\t\t\t<_x0000_>x5c-xml</_x0000_>\n\t\t\t</f5>\n\t\t</foo>\n\t\t<bar _type='null'/>\n\t\t<_x0000_>\n\t\t\t<f1>x1a-xml</f1>\n\t\t\t<f2>\n\t\t\t\tx2a-xml\n\t\t\t\tx2b-xml\n\t\t\t\t<null/>\n\t\t\t</f2>\n\t\t\t<f4>\n\t\t\t\tx4a-xml\n\t\t\t\tx4b-xml\n\t\t\t\t<null/>\n\t\t\t</f4>\n\t\t\t<f5>\n\t\t\t\t<foo>x5a-xml</foo>\n\t\t\t\t<bar _type='null'/>\n\t\t\t\t<_x0000_>x5c-xml</_x0000_>\n\t\t\t</f5>\n\t\t</_x0000_>\n\t</f>\n</object>\n",
					/* XmlNs */		"<object><f><foo><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></foo><bar _type='null'/><_x0000_><f1>x1a-xml</f1><f2>x2a-xmlx2b-xml<null/></f2><f4>x4a-xmlx4b-xml<null/></f4><f5><foo>x5a-xml</foo><bar _type='null'/><_x0000_>x5c-xml</_x0000_></f5></_x0000_></f></object>",
					/* Html */		"<table><tr><td>f</td><td><table><tr><td>foo</td><td><table><tr><td>f1</td><td>x1a-html</td></tr><tr><td>f2</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td></tr><tr><td>f4</td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td></tr><tr><td>f5</td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr></table></td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td><table><tr><td>f1</td><td>x1a-html</td></tr><tr><td>f2</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td></tr><tr><td>f4</td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td></tr><tr><td>f5</td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr></table></td></tr></table></td></tr></table>",
					/* HtmlT */		"<table><tr><td>f</td><td><table><tr><td>foo</td><td><table><tr><td>f1</td><td>x1a-html</td></tr><tr><td>f2</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td></tr><tr><td>f4</td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td></tr><tr><td>f5</td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr></table></td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td><table><tr><td>f1</td><td>x1a-html</td></tr><tr><td>f2</td><td><ul><li>x2a-html</li><li>x2b-html</li><li><null/></li></ul></td></tr><tr><td>f4</td><td><ul><li>x4a-html</li><li>x4b-html</li><li><null/></li></ul></td></tr><tr><td>f5</td><td><table><tr><td>foo</td><td>x5a-html</td></tr><tr><td>bar</td><td><null/></td></tr><tr><td><null/></td><td>x5c-html</td></tr></table></td></tr></table></td></tr></table></td></tr></table>",
					/* HtmlR */		"<table>\n\t<tr>\n\t\t<td>f</td>\n\t\t<td>\n\t\t\t<table>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>foo</td>\n\t\t\t\t\t<td>\n\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f1</td>\n\t\t\t\t\t\t\t\t<td>x1a-html</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f2</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t\t\t\t<li>x2a-html</li>\n\t\t\t\t\t\t\t\t\t\t<li>x2b-html</li>\n\t\t\t\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f4</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t\t\t\t<li>x4a-html</li>\n\t\t\t\t\t\t\t\t\t\t<li>x4b-html</li>\n\t\t\t\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f5</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td>foo</td>\n\t\t\t\t\t\t\t\t\t\t\t<td>x5a-html</td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td>bar</td>\n\t\t\t\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t\t\t\t\t<td>x5c-html</td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</table>\n\t\t\t\t\t</td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td>bar</td>\n\t\t\t\t\t<td><null/></td>\n\t\t\t\t</tr>\n\t\t\t\t<tr>\n\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t<td>\n\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f1</td>\n\t\t\t\t\t\t\t\t<td>x1a-html</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f2</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t\t\t\t<li>x2a-html</li>\n\t\t\t\t\t\t\t\t\t\t<li>x2b-html</li>\n\t\t\t\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f4</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<ul>\n\t\t\t\t\t\t\t\t\t\t<li>x4a-html</li>\n\t\t\t\t\t\t\t\t\t\t<li>x4b-html</li>\n\t\t\t\t\t\t\t\t\t\t<li><null/></li>\n\t\t\t\t\t\t\t\t\t</ul>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t<td>f5</td>\n\t\t\t\t\t\t\t\t<td>\n\t\t\t\t\t\t\t\t\t<table>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td>foo</td>\n\t\t\t\t\t\t\t\t\t\t\t<td>x5a-html</td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td>bar</td>\n\t\t\t\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t<td><null/></td>\n\t\t\t\t\t\t\t\t\t\t\t<td>x5c-html</td>\n\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t</td>\n\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t</table>\n\t\t\t\t\t</td>\n\t\t\t\t</tr>\n\t\t\t</table>\n\t\t</td>\n\t</tr>\n</table>\n",
					/* Uon */		"(f=(foo=(f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon)),bar=null,null=(f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon))))",
					/* UonT */		"(f=(foo=(f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon)),bar=null,null=(f1=x1a-uon,f2=@(x2a-uon,x2b-uon,null),f4=@(x4a-uon,x4b-uon,null),f5=(foo=x5a-uon,bar=null,null=x5c-uon))))",
					/* UonR */		"(\n\tf=(\n\t\tfoo=(\n\t\t\tf1=x1a-uon,\n\t\t\tf2=@(\n\t\t\t\tx2a-uon,\n\t\t\t\tx2b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf4=@(\n\t\t\t\tx4a-uon,\n\t\t\t\tx4b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf5=(\n\t\t\t\tfoo=x5a-uon,\n\t\t\t\tbar=null,\n\t\t\t\tnull=x5c-uon\n\t\t\t)\n\t\t),\n\t\tbar=null,\n\t\tnull=(\n\t\t\tf1=x1a-uon,\n\t\t\tf2=@(\n\t\t\t\tx2a-uon,\n\t\t\t\tx2b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf4=@(\n\t\t\t\tx4a-uon,\n\t\t\t\tx4b-uon,\n\t\t\t\tnull\n\t\t\t),\n\t\t\tf5=(\n\t\t\t\tfoo=x5a-uon,\n\t\t\t\tbar=null,\n\t\t\t\tnull=x5c-uon\n\t\t\t)\n\t\t)\n\t)\n)",
					/* UrlEnc */	"f=(foo=(f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)),bar=null,null=(f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)))",
					/* UrlEncT */	"f=(foo=(f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)),bar=null,null=(f1=x1a-x-www-form-urlencoded,f2=@(x2a-x-www-form-urlencoded,x2b-x-www-form-urlencoded,null),f4=@(x4a-x-www-form-urlencoded,x4b-x-www-form-urlencoded,null),f5=(foo=x5a-x-www-form-urlencoded,bar=null,null=x5c-x-www-form-urlencoded)))",
					/* UrlEncR */	"f=(\n\tfoo=(\n\t\tf1=x1a-x-www-form-urlencoded,\n\t\tf2=@(\n\t\t\tx2a-x-www-form-urlencoded,\n\t\t\tx2b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf4=@(\n\t\t\tx4a-x-www-form-urlencoded,\n\t\t\tx4b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf5=(\n\t\t\tfoo=x5a-x-www-form-urlencoded,\n\t\t\tbar=null,\n\t\t\tnull=x5c-x-www-form-urlencoded\n\t\t)\n\t),\n\tbar=null,\n\tnull=(\n\t\tf1=x1a-x-www-form-urlencoded,\n\t\tf2=@(\n\t\t\tx2a-x-www-form-urlencoded,\n\t\t\tx2b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf4=@(\n\t\t\tx4a-x-www-form-urlencoded,\n\t\t\tx4b-x-www-form-urlencoded,\n\t\t\tnull\n\t\t),\n\t\tf5=(\n\t\t\tfoo=x5a-x-www-form-urlencoded,\n\t\t\tbar=null,\n\t\t\tnull=x5c-x-www-form-urlencoded\n\t\t)\n\t)\n)",
					/* MsgPack */	"81A16683A3666F6F84A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636BA3626172C0C084A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636B",
					/* MsgPackT */	"81A16683A3666F6F84A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636BA3626172C0C084A266317831612D6D73677061636BA26632937832612D6D73677061636B7832622D6D73677061636BC0A26634937834612D6D73677061636B7834622D6D73677061636BC0A2663583A3666F6F7835612D6D73677061636BA3626172C0C07835632D6D73677061636B",
					/* RdfXml */	"<rdf:RDF>\n<rdf:Description>\n<jp:f rdf:parseType='Resource'>\n<jp:foo rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_ rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</jp:_x0000_>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlT */	"<rdf:RDF>\n<rdf:Description>\n<jp:f rdf:parseType='Resource'>\n<jp:foo rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_ rdf:parseType='Resource'>\n<jp:f1>x1a-xml</jp:f1>\n<jp:f2>\n<rdf:Seq>\n<rdf:li>x2a-xml</rdf:li>\n<rdf:li>x2b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f2>\n<jp:f4>\n<rdf:Seq>\n<rdf:li>x4a-xml</rdf:li>\n<rdf:li>x4b-xml</rdf:li>\n<rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n</rdf:Seq>\n</jp:f4>\n<jp:f5 rdf:parseType='Resource'>\n<jp:foo>x5a-xml</jp:foo>\n<jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n<jp:_x0000_>x5c-xml</jp:_x0000_>\n</jp:f5>\n</jp:_x0000_>\n</jp:f>\n</rdf:Description>\n</rdf:RDF>\n",
					/* RdfXmlR */	"<rdf:RDF>\n  <rdf:Description>\n    <jp:f rdf:parseType='Resource'>\n      <jp:foo rdf:parseType='Resource'>\n        <jp:f1>x1a-xml</jp:f1>\n        <jp:f2>\n          <rdf:Seq>\n            <rdf:li>x2a-xml</rdf:li>\n            <rdf:li>x2b-xml</rdf:li>\n            <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          </rdf:Seq>\n        </jp:f2>\n        <jp:f4>\n          <rdf:Seq>\n            <rdf:li>x4a-xml</rdf:li>\n            <rdf:li>x4b-xml</rdf:li>\n            <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          </rdf:Seq>\n        </jp:f4>\n        <jp:f5 rdf:parseType='Resource'>\n          <jp:foo>x5a-xml</jp:foo>\n          <jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          <jp:_x0000_>x5c-xml</jp:_x0000_>\n        </jp:f5>\n      </jp:foo>\n      <jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n      <jp:_x0000_ rdf:parseType='Resource'>\n        <jp:f1>x1a-xml</jp:f1>\n        <jp:f2>\n          <rdf:Seq>\n            <rdf:li>x2a-xml</rdf:li>\n            <rdf:li>x2b-xml</rdf:li>\n            <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          </rdf:Seq>\n        </jp:f2>\n        <jp:f4>\n          <rdf:Seq>\n            <rdf:li>x4a-xml</rdf:li>\n            <rdf:li>x4b-xml</rdf:li>\n            <rdf:li rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          </rdf:Seq>\n        </jp:f4>\n        <jp:f5 rdf:parseType='Resource'>\n          <jp:foo>x5a-xml</jp:foo>\n          <jp:bar rdf:resource='http://www.w3.org/1999/02/22-rdf-syntax-ns#nil'/>\n          <jp:_x0000_>x5c-xml</jp:_x0000_>\n        </jp:f5>\n      </jp:_x0000_>\n    </jp:f>\n  </rdf:Description>\n</rdf:RDF>\n"
				) 
			},
		});
	}

	public ReaderObjectSwapTest(ComboInput<?> comboInput) {
		super(comboInput);
	}

	@Swap(PojoToSimpleReaderSwap.class)
	public static class PojoToSimpleReader {}
	
	public static class PojoToSimpleReaderSwap extends PojoSwap<PojoToSimpleReader,Reader> {
		public Reader swap(BeanSession session, PojoToSimpleReader o) throws Exception {
			return new StringReader("foo");
		}
	}
	
	@Swap(PojoToDynamicReaderSwap.class)
	public static class PojoToDynamicReader {
		private String f;
		public PojoToDynamicReader(String f) {
			this.f = f;
		}
	}
	
	public static class PojoToDynamicReaderSwap extends PojoSwap<PojoToDynamicReader,Object> {
		public Object swap(BeanSession session, PojoToDynamicReader o) throws Exception {
			return new StringReader(o.f + "-" + session.getMediaType().getSubTypes().get(0));
		}
	}
	
	@Swap(SometimesSwappedBeanSwap1.class)
	public static class SometimesSwappedBean1 {
		public String f;
		public SometimesSwappedBean1(String f) {
			this.f = f;
		}
	}
	
	public static class SometimesSwappedBeanSwap1 extends PojoSwap<SometimesSwappedBean1,Object> {
		public Object swap(BeanSession session, SometimesSwappedBean1 o) throws Exception {
			MediaType mt = session.getMediaType();
			if (mt.hasSubType("json") || mt.hasSubType("xml"))
				return new StringReader(o.f + "-" + mt);
			return o;
		}
	}
	
	@Swap(SometimesSwappedBeanSwap2.class)
	public static class SometimesSwappedBean2 {
		public String f;
		public SometimesSwappedBean2(String f) {
			this.f = f;
		}
	}
	
	public static class SometimesSwappedBeanSwap2 extends PojoSwap<SometimesSwappedBean2,Object> {
		public Object swap(BeanSession session, SometimesSwappedBean2 o) throws Exception {
			MediaType mt = session.getMediaType();
			if (mt.hasSubType("json") || mt.hasSubType("xml"))
				return o;
			return new StringReader(o.f + "-" + mt);
		}
	}
	
	
	public static class BeanWithSwappedField {
		public PojoToDynamicReader f;
		public BeanWithSwappedField(String f) {
			this.f = new PojoToDynamicReader(f);
		}
	}

	public static class BeanWithSwapped1dField {
		public PojoToDynamicReader[] f;
		public BeanWithSwapped1dField(String f) {
			this.f = new PojoToDynamicReader[]{new PojoToDynamicReader(f + "1"),new PojoToDynamicReader(f + 2),null};
		}
	}

	public static class BeanWithSwappedNullField {
		public PojoToDynamicReader f;
	}

	public static class BeanWithSwappedListField {
		public List<PojoToDynamicReader> f;
		public BeanWithSwappedListField(String f) {
			this.f = new AList<PojoToDynamicReader>()
				.append(new PojoToDynamicReader(f + "1"))
				.append(new PojoToDynamicReader(f + "2"))
				.append(null)
			;
		}
	}

	public static class BeanWithSwappedMapField {
		public Map<String,PojoToDynamicReader> f;
		public BeanWithSwappedMapField(String f) {
			this.f = new AMap<String,PojoToDynamicReader>()
				.append("foo", new PojoToDynamicReader(f + "1"))
				.append("bar", null)
				.append(null, new PojoToDynamicReader(f + "2"))
			;
		}
	}

	public static class BeanWithListBeanSwappedField {
		public List<B> f;
		public BeanWithListBeanSwappedField(String f) {
			this.f = new AList<B>()
				.append(new B(f))
				.append(null)
			;
		}
	}

	public static class BeanWithMapBeanSwappedField {
		public Map<String,B> f;
		public BeanWithMapBeanSwappedField(String f) {
			this.f = new AMap<String,B>()
				.append("foo", new B(f))
				.append("bar", null)
				.append(null, new B(f))
			;
		}
	}

	public static class B {
		public PojoToDynamicReader f1;
		public PojoToDynamicReader[] f2;
		public PojoToDynamicReader f3;
		public List<PojoToDynamicReader> f4;
		public Map<String,PojoToDynamicReader> f5;

		public B(String f) {
			f1 = new PojoToDynamicReader(f + "1a");
			f2 = new PojoToDynamicReader[]{new PojoToDynamicReader(f + "2a"),new PojoToDynamicReader(f + "2b"),null};
			f3 = null;
			f4 = new AList<PojoToDynamicReader>()
				.append(new PojoToDynamicReader(f + "4a"))
				.append(new PojoToDynamicReader(f + "4b"))
				.append(null)
			;
			f5 = new AMap<String,PojoToDynamicReader>()
				.append("foo", new PojoToDynamicReader(f + "5a"))
				.append("bar", null)
				.append(null, new PojoToDynamicReader(f + "5c"))
			;
		}
	}
}