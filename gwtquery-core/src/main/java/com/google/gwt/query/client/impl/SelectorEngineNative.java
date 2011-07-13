/*
 * Copyright 2011, The gwtquery team.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.query.client.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

/**
 * Runtime selector engine implementation for browsers with native
 * querySelectorAll support.
 */
public class SelectorEngineNative extends SelectorEngineImpl {

  public static String NATIVE_EXCEPTIONS_REGEXP = ".*(:contains|!=|:first|:last|:even|:odd).*";
  
  private static HasSelector impl;
  
  public SelectorEngineNative() {
    if (impl == null) {
      impl = GWT.create(HasSelector.class);
    }
  }
  
  public NodeList<Element> select(String selector, Node ctx) {
    // querySelectorAllImpl does not support ids starting with a digit.
    if (selector.matches("#[\\w\\-]+")) {
      return SelectorEngine.veryQuickId(ctx, selector.substring(1));
    } else if (!SelectorEngine.hasQuerySelector || selector.matches(NATIVE_EXCEPTIONS_REGEXP)) {
      return impl.select(selector, ctx); 
    } else {
      try {
        return SelectorEngine.querySelectorAllImpl(selector, ctx);
      } catch (Exception e) {
        System.err.println("ERROR SelectorEngineNative " + e.getMessage()
            + " " + selector + ", falling back to "
            + impl.getClass().getName().replaceAll(".*\\.", ""));
        return impl.select(selector, ctx); 
      }
    }
  }
}
