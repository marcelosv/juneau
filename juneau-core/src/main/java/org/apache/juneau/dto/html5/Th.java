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
package org.apache.juneau.dto.html5;

import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML <a href='https://www.w3.org/TR/html5/tabular-data.html#the-th-element'>&lt;th&gt;</a> element.
 * <p>
 */
@Bean(typeName="th")
@SuppressWarnings("hiding")
public class Th extends HtmlElementMixed {

	/**
	 * <a class='doclink' href='https://www.w3.org/TR/html5/tabular-data.html#attr-th-abbr'>abbr</a> attribute.
	 * Alternative label to use for the header cell when referencing the cell in other contexts.
	 * @param abbr - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th abbr(String abbr) {
		attrs.put("abbr", abbr);
		return this;
	}

	/**
	 * <a class='doclink' href='https://www.w3.org/TR/html5/tabular-data.html#attr-tdth-colspan'>colspan</a> attribute.
	 * Number of columns that the cell is to span.
	 * @param colspan - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th colspan(String colspan) {
		attrs.put("colspan", colspan);
		return this;
	}

	/**
	 * <a class='doclink' href='https://www.w3.org/TR/html5/tabular-data.html#attr-tdth-headers'>headers</a> attribute.
	 * The headers for this cell.
	 * @param headers - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th headers(String headers) {
		attrs.put("headers", headers);
		return this;
	}

	/**
	 * <a class='doclink' href='https://www.w3.org/TR/html5/tabular-data.html#attr-tdth-rowspan'>rowspan</a> attribute.
	 * Number of rows that the cell is to span.
	 * @param rowspan - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th rowspan(String rowspan) {
		attrs.put("rowspan", rowspan);
		return this;
	}

	/**
	 * <a class='doclink' href='https://www.w3.org/TR/html5/tabular-data.html#attr-th-scope'>scope</a> attribute.
	 * Specifies which cells the header cell applies to.
	 * @param scope - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th scope(String scope) {
		attrs.put("scope", scope);
		return this;
	}

	/**
	 * <a class='doclink' href='-'>sorted</a> attribute.
	 * Column sort direction and ordinality.
	 * @param sorted - The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Th sorted(String sorted) {
		attrs.put("sorted", sorted);
		return this;
	}

	//--------------------------------------------------------------------------------
	// Overridden methods
	//--------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Th _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Th id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Th children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Th child(Object child) {
		this.children.add(child);
		return this;
	}
}
