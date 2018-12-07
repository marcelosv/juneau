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
package org.apache.juneau.microservice;

import org.eclipse.jetty.server.*;

/**
 * Interface for creating Jetty servers.
 */
public interface JettyServerFactory {

	/**
	 * Create a new initialized Jetty server.
	 *
	 * @param config The microservice INI file.
	 * @param manifestFile The microservice. manifest file.
	 * @param varResolver The microservice variable resolver.
	 * @param logger The microservice logger.
	 * @param ports Requested ports to use.  Can be <jk>null</jk>.
	 * @param jettyXml The contents of the <code>jetty.xml</code> file.
	 * @param resolveVars Whether we should resolve SVL variables in the <code>jetty.xml</code> file.
	 * @return A newly-created but not-yet-started server.
	 * @throws Exception
	 */
	Server create(String jettyXml) throws Exception;
}