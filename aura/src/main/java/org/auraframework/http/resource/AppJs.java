/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.auraframework.http.resource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.auraframework.adapter.AppJsUtilAdapter;
import org.auraframework.annotations.Annotations.ServiceComponent;
import org.auraframework.def.DefDescriptor;
import org.auraframework.system.AuraContext;
import org.auraframework.system.AuraContext.Format;

@ServiceComponent
public class AppJs extends AuraResourceImpl {
    private static final String APPJS_APPEND = "\nAura.appJsReady = true;Aura.appDefsReady&&Aura.appDefsReady();";
    private AppJsUtilAdapter appJsUtilAdapter;

    public AppJs() {
        super("app.js", Format.JS);
    }

    @Override
    public void write(HttpServletRequest request, HttpServletResponse response, AuraContext context) throws IOException {
        Set<DefDescriptor<?>> dependencies = appJsUtilAdapter.getPartDependencies(request, response, context, 1);
        if (dependencies == null) {
            return;
        }

        try {
            PrintWriter writer = response.getWriter();
            serverService.writeDefinitions(dependencies, writer, true, 1);
            writer.append(APPJS_APPEND);
        } catch (Throwable t) {
            servletUtilAdapter.handleServletException(t, false, context, request, response, false);
            exceptionAdapter.handleException(new AuraResourceException(getName(), response.getStatus(), t));
        }
    }

    @Inject
    public void setAppJsUtilAdapter(AppJsUtilAdapter appJsUtilAdapter) {
        this.appJsUtilAdapter = appJsUtilAdapter;
    }
}
