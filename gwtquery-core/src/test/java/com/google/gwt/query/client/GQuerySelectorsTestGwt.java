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
package com.google.gwt.query.client;

import static com.google.gwt.query.client.GQuery.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.query.client.impl.SelectorEngineCssToXPath;
import com.google.gwt.query.client.impl.SelectorEngineImpl;
import com.google.gwt.query.client.impl.SelectorEngineNative;
import com.google.gwt.query.client.impl.SelectorEngineSizzle;
import com.google.gwt.query.client.impl.research.SelectorEngineJS;
import com.google.gwt.query.client.impl.research.SelectorEngineSizzleGwt;
import com.google.gwt.query.client.impl.research.SelectorEngineXPath;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Test for selectors
 */
public class GQuerySelectorsTestGwt extends GWTTestCase {

  boolean runSlow = false;

  protected interface ParentSelector extends Selectors {
    @Selector("#parent")
    GQuery parentSelector();

  }

  protected interface ExtendedSelector extends ParentSelector {

    @Selector("#child")
    GQuery childSelector();
  }


  protected interface AllSelectors extends Selectors {
    @Selector("h1[id]:contains(Selectors)")
    NodeList<Element> h1IdContainsSelectors();
    @Selector("tr:first")
    NodeList<Element> trFirst();
    @Selector("tr:last")
    NodeList<Element> trLast();
    // @Selector("div[class!='madeup']")
    // NodeList<Element> divWithClassNotContainsMadeup();
    // @Selector("div, p a")
    // NodeList<Element> divCommaPA();
    @Selector("p:contains(selectors)")
    NodeList<Element> pContainsSelectors();
    @Selector("a[href][lang][class]")
    NodeList<Element> aHrefLangClass();
    @Selector("*:checked")
    NodeList<Element> allChecked();
    @Selector("body")
    NodeList<Element> body();
    @Selector("body div")
    NodeList<Element> bodyDiv();
    @Selector("div .example")
    NodeList<Element> divExample();
    @Selector("div > div")
    NodeList<Element> divGtP();
    @Selector("div:not(.example)")
    NodeList<Element> divNotExample();
    @Selector("div p")
    NodeList<Element> divP();
    @Selector("div p a")
    NodeList<Element> divPA();
    @Selector("div + p")
    NodeList<Element> divPlusP();
    @Selector("div[class^=exa][class$=mple]")
    NodeList<Element> divPrefixExaSuffixMple();
    @Selector("div #title")
    NodeList<Element> divSpaceTitle();
    @Selector("div ~ p")
    NodeList<Element> divTildeP();
    @Selector("div[class]")
    NodeList<Element> divWithClass();
    @Selector("div[class~=dialog]")
    NodeList<Element> divWithClassContainsDialog();
    @Selector("div[class*=e]")
    NodeList<Element> divWithClassContainsE();
    @Selector("div[class=example]")
    NodeList<Element> divWithClassExample();
    @Selector("div[class~=dialog]")
    NodeList<Element> divWithClassListContainsDialog();
    @Selector("div[class^=exa]")
    NodeList<Element> divWithClassPrefixExa();
    @Selector("div[class$=mple]")
    NodeList<Element> divWithClassSuffixMple();
    @Selector("p:first-child")
    NodeList<Element> firstChild();
    @Selector("h1#title")
    NodeList<Element> h1Title();
    @Selector("h1#title + em > span")
    NodeList<Element> h1TitlePlusEmGtSpan();
    @Selector("p:last-child")
    NodeList<Element> lastChild();
    @Selector(".note")
    NodeList<Element> note();
    @Selector("p:nth-child(n)")
    NodeList<Element> nthChild();
    @Selector("p:nth-child(2n)")
    NodeList<Element> nThChild2n();
    @Selector("p:nth-child(2n+1)")
    NodeList<Element> nThChild2nPlus1();
    @Selector("p:nth-child(even)")
    NodeList<Element> nThChildEven();
    @Selector("p:nth-child(odd)")
    NodeList<Element> nThChildOdd();
    @Selector("p:only-child")
    NodeList<Element> onlyChild();
    @Selector("#title")
    NodeList<Element> title();
    @Selector("#title,h1#title")
    NodeList<Element> titleAndh1Title();
    @Selector("ul .tocline2")
    NodeList<Element> ulTocline2();
    @Selector("ul.toc li.tocline2")
    NodeList<Element> ulTocLiTocLine2();
  }

  protected interface TestSelectors extends Selectors {
    @Selector("*:checked")
    GQuery allChecked();
    @Selector("*:checked")
    GQuery allChecked(Node n);
    @Selector(".branchA")
    GQuery branchA();
    @Selector(".branchA")
    GQuery branchA(Node n);
    @Selector(".branchA .target")
    GQuery branchAtarget();
    @Selector(".branchA .target")
    GQuery branchAtarget(Node n);
    @Selector(".branchB")
    GQuery branchB();
    @Selector(".branchB")
    GQuery branchB(Node n);

    @Selector("div .target")
    GQuery divTarget();
    @Selector("div .target")
    GQuery divTarget(Node n);
    @Selector(".target")
    GQuery target();
    @Selector(".target")
    GQuery target(Node n);
  }

  static Element e = null;
  static HTML testPanel = null;

  private static native boolean hasNativeSelector() /*-{
    return !!(document.querySelectorAll && /native/.test(String(document.querySelectorAll)));
  }-*/;

  public String getModuleName() {
    return "com.google.gwt.query.Query";
  }

  public void gwtTearDown() {
    $(e).remove();
    e = null;
  }

  public void gwtSetUp() {
    if (e == null) {
      testPanel = new HTML();
      RootPanel.get().add(testPanel);
      e = testPanel.getElement();
      e.setId("select-tst");
    } else {
      e.setInnerHTML("");
    }
  }

  public void testInhiterance(){
    final ExtendedSelector selector = GWT.create(ExtendedSelector.class);

    $(e).html("<div id=\"parent\">parent<div id=\"child\">child</div></div>");

    assertEquals(1, selector.parentSelector().length());
    assertEquals(1, selector.childSelector().length());

    assertEquals("parentchild", selector.parentSelector().text());
    assertEquals("child", selector.childSelector().text());
  }

  public void testJQueryPseudoselectors() {
    $(e).html("<table border=1 id=idtest width=440><tr><td width=50%>A Text</td><td width=50%><a></a><p id=a></p><p id=b style='display: none'><span id=c>s</span></p></td></tr></table>");
    assertEquals(9, $("* ", e).size());
    assertEquals(1, $("p:hidden ", e).size());
    assertEquals(2, $("td:visible ", e).size());
    $(e).html("<input type='checkbox' id='cb' name='cb' value='1' /><input type='radio' id='cb' name='cb' value='1' />");
    assertEquals(1, $("input:checkbox ", e).size());
    assertEquals(1, $(":radio ", e).size());
    assertEquals(2, $("*:radio, *:checkbox", e).size());
  }

  public void testCompiledSelectors() {
    final AllSelectors sel = GWT.create(AllSelectors.class);
    $(e).html(getTestContent());

    // TODO: fix these selectors
    // sel.divWithClassNotContainsMadeup().getLength()
    // sel.divCommaPA().getLength()

    // assertArrayContains(sel.title().getLength(), 1);
    assertEquals(1, sel.body().getLength());
    assertArrayContains(sel.bodyDiv().getLength(), 53, 55);
    sel.setRoot(e);
    assertArrayContains(sel.trFirst().getLength(), 5);
    assertArrayContains(sel.trLast().getLength(), 5);
    assertArrayContains(sel.pContainsSelectors().getLength(), 54);
    assertArrayContains(sel.h1IdContainsSelectors().getLength(), 1);
    assertArrayContains(sel.aHrefLangClass().getLength(), 0, 1);
    assertArrayContains(sel.allChecked().getLength(), 1);
    assertArrayContains(sel.divExample().getLength(), 43);
    assertArrayContains(sel.divGtP().getLength(), 51, 52);
    assertArrayContains(sel.divNotExample().getLength(), 9, 10);
    assertArrayContains(sel.divP().getLength(), 324);
    assertArrayContains(sel.divPA().getLength(), 84);
    assertArrayContains(sel.divPlusP().getLength(), 22);
    assertArrayContains(sel.divPrefixExaSuffixMple().getLength(), 43);
    assertArrayContains(sel.divSpaceTitle().getLength(), 1);
    assertArrayContains(sel.divTildeP().getLength(), 183);
    assertArrayContains(sel.divWithClass().getLength(), 51, 52);
    assertArrayContains(sel.divWithClassContainsDialog().getLength(), 1);
    assertArrayContains(sel.divWithClassContainsE().getLength(), 50);
    assertArrayContains(sel.divWithClassExample().getLength(), 43);
    assertArrayContains(sel.divWithClassListContainsDialog().getLength(), 1);
    assertArrayContains(sel.divWithClassPrefixExa().getLength(), 43);
    assertArrayContains(sel.divWithClassSuffixMple().getLength(), 43);
    assertArrayContains(sel.firstChild().getLength(), 54);
    assertArrayContains(sel.h1Title().getLength(), 1);
    assertArrayContains(sel.h1TitlePlusEmGtSpan().getLength(), 1);
    assertArrayContains(sel.lastChild().getLength(), 19);
    assertArrayContains(sel.note().getLength(), 14);
    assertArrayContains(sel.nthChild().getLength(), 324);
    assertArrayContains(sel.nThChild2n().getLength(), 159);
    assertArrayContains(sel.nThChild2nPlus1().getLength(), 165);
    assertArrayContains(sel.nThChildEven().getLength(), 159);
    assertArrayContains(sel.nThChildOdd().getLength(), 165);
    assertArrayContains(sel.onlyChild().getLength(), 3);
    assertArrayContains(sel.titleAndh1Title().getLength(), 0, 1, 2); //FIXME
    assertArrayContains(sel.ulTocline2().getLength(), 12);
    assertArrayContains(sel.ulTocLiTocLine2().getLength(), 12);
  }

  public void testIssue12() {
    $(e).html("<table><tr><td><p myCustomAttr='whatever'><input disabled='disabled' type='radio' name='wantedName' value='v1'>1</input></p><input type='radio' name='n' value='v2' checked='checked'>2</input></td><td><button myCustomAttr='val'>Click</button></tr><td></table>");
    executeSelectInAllImplementations(":checked", e, 1);
    executeSelectInAllImplementations(":disabled", e, 1);
    executeSelectInAllImplementations("input:enabled", e, 1);
// FIXME, these two selectors fails in prod with chrome when using both Xpath engines
//    executeSelectInAllImplementations("[myCustomAttr]", e, 2);
//    executeSelectInAllImplementations("*[myCustomAttr]", e, 2);
    executeSelectInAllImplementations("input[name=wantedName]", e, 1);
    executeSelectInAllImplementations("input[name='wantedName']", e, 1);
    executeSelectInAllImplementations("input[name=\"wantedName\"]", e, 1);
  }

  public void testSelectElementsInsideContext() {
    $(e).html("<spam><p>s</p></spam>");
    GQuery q = $("spam", e);
    // TODO: in XPath engine returns 2 when it should return 1
    executeSelectInAllImplementations("*", q.get(0), 1, 2);
  }

  public void testSelectorEngineCssToXpath() {
    SelectorEngineImpl selEng = new SelectorEngineCssToXPath();
    executeSelectorEngineTests(selEng);
  }

  public void testSelectorEngineDomAssistant() {
    if (!runSlow) return;
    // This test runs very slow in chrome
    SelectorEngineImpl selEng = new SelectorEngineJS();
    executeSelectorEngineTests(selEng);
  }

  public void testSelectorEngineNative() {
    SelectorEngineImpl selEng = new SelectorEngineNative();
    if (hasNativeSelector()) {
      executeSelectorEngineTests(selEng);
    }
  }

  public void ignore_testSelectorEngineSizzle() {
    SelectorEngineImpl selEng = new SelectorEngineSizzle();
    executeSelectorEngineTests(selEng);
  }

  public void testSelectorEngineSizzleGwt() {
    if (!runSlow) return;
    SelectorEngineImpl selEng = new SelectorEngineSizzleGwt();
    executeSelectorEngineTests(selEng);
  }

  public void testSelectorEngineXpath() {
    SelectorEngineImpl selEng = new SelectorEngineXPath();
    executeSelectorEngineTests(selEng);
  }

  public void testSelectorsGeneratorNative() {
    $(e).html(
            "<input type='radio' name='n' value='v1'>1</input>"
                + "<input type='radio' name='n' value='v2' checked='checked'>2</input>");

    TestSelectors selectors = GWT.create(TestSelectors.class);
    assertEquals(1, selectors.allChecked().size());
  }

  public void testSelectorsInIframe() {
    $(e).html("<iframe name='miframe' id='miframe' src=\"javascript:''\">");
    Element d = $("#miframe").contents().empty().get(0);
    assertNotNull(d);

    $(d).html(
            "<div class='branchA'><div class='target'>branchA target</div></div>"
                + "<div class='branchB'><div class='target'>branchB target</div></div>");


    executeSelectInAllImplementations(".branchA .target", d, 1, 2); //FIXME:
    executeSelectInAllImplementations(".branchA .target", body, 0);
    executeSelectInAllImplementations("div .target", d, 2);
    executeSelectInAllImplementations("div .target", body, 0);

    TestSelectors selectors = GWT.create(TestSelectors.class);
    assertEquals(1, selectors.branchAtarget(d).length());
    assertEquals(0, selectors.branchAtarget().length());
    assertEquals(2, selectors.divTarget(d).length());
    assertEquals(0, selectors.divTarget().length());

  }

  public void testSelectorsWithContext() {
    $(e).append(
            "<div class='branchA'><div class='target'>branchA target</div></div>"
                + "<div class='branchB'><div class='target'>branchB target</div></div>");

    TestSelectors selectors = GWT.create(TestSelectors.class);

    assertEquals(2, selectors.target().length());
    Element branchA = selectors.branchA().get(0);
    Element branchB = selectors.branchB().get(0);
    assertNotNull(selectors.branchA().get(0));
    assertNotNull(selectors.branchB().get(0));

    assertEquals(2, selectors.target(body).length());
    branchA = selectors.branchA(body).get(0);
    branchB = selectors.branchB(body).get(0);
    assertNotNull(branchA);
    assertNotNull(branchB);
    assertEquals("branchA target", selectors.target(branchA).text());
    assertEquals("branchB target", selectors.target(branchB).text());

    selectors.setRoot(branchA);
    assertEquals(1, selectors.target().length());
    assertEquals("branchA target", selectors.target().text());

    selectors.setRoot(branchB);
    assertEquals(1, selectors.target().length());
    assertEquals("branchB target", selectors.target().text());
  }

  public void testUnique() {
    SelectorEngineImpl selSizz = new SelectorEngineSizzleGwt();
    $(e).html(getTestContent());

    JsArray<Element> a;
    a = selSizz.select("p", e).cast();
    int n = a.length();
    assertTrue(n > 300);
    for (int i=0; i<n; i++) {
      a.push(a.get(i));
    }
    assertEquals(n * 2 , a.length());
    a = JsUtils.unique(a);
    assertEquals(n, a.length());
  }

  public void testOddEvenNthChild(){

	  $(e).append(
	            "<div id='parent'><div id='first' class='evenClass' role='treeItem'>branchA target</div><div id='second' class='oddClass' role='treeItem'>branchA target</div><div id='third' class='evenClass' role='treeItem'>branchA target</div><div id='fourth' class='oddClass' role='treeItem'>branchA target</div></div>");

	  GQuery odd = $("#parent > div:odd",e);
	  assertEquals(2, odd.size());
	  for (Element el : odd.elements()){
		  assertEquals("oddClass", el.getClassName());
	  }

	  GQuery even = $("#parent > div:even", e);
	  assertEquals(2, even.size());
	  for (Element el : even.elements()){
		  assertEquals("evenClass", el.getClassName());
	  }

	  //test filter
	  odd = $("#parent > div",e).filter(":odd");
	  assertEquals(2, odd.size());
	  for (Element el : odd.elements()){
		  assertEquals("oddClass", el.getClassName());
	  }

	  even = $("#parent > div", e).filter(":even");
	  assertEquals(2, even.size());
	  for (Element el : even.elements()){
		  assertEquals("evenClass", el.getClassName());
	  }

	  //test nth-child
	  GQuery second = $("#parent > div",e).filter(":nth-child(2)");
	  assertEquals(1, second.size());
	  assertEquals("second", second.attr("id"));

	  GQuery third =$("#parent > div:nth-child(3)", e);
	  assertEquals(1, third.size());
	  assertEquals("third", third.attr("id"));

	  //test nth-child with function
	  GQuery secondAndFourth = $("#parent > div",e).filter(":nth-child(2n)");
	  assertEquals(2, secondAndFourth.size());
	  assertEquals("second", secondAndFourth.eq(0).attr("id"));
	  assertEquals("fourth", secondAndFourth.eq(1).attr("id"));

	  // odd and even with attribute filters
    secondAndFourth = $("div[role=treeItem]:odd", e);
    assertEquals("second", secondAndFourth.eq(0).attr("id"));
    assertEquals("fourth", secondAndFourth.eq(1).attr("id"));

    GQuery treeItemFirstAndThird = $("div[role=treeItem]:even", e);
    assertEquals("first", treeItemFirstAndThird.eq(0).attr("id"));
    assertEquals("third", treeItemFirstAndThird.eq(1).attr("id"));
  }

  private void assertArrayContains(Object result, Object... array) {
    assertArrayContains("", result, array);
  }

  private void assertArrayContains(String message, Object result, Object... array) {
    String values = "";
    boolean done = false;
    for (Object o : array) {
      values += o.toString() + " ";
      if (result.equals(o)) {
        done = true;
      }
    }
    message = message + ", value (" + result + ") not found in: " + values;
    assertTrue(message, done);
  }

  private void executeSelectInAllImplementations(String selector, Element elem, Object... array) {
    SelectorEngineImpl selSizz = new SelectorEngineSizzle();
    SelectorEngineImpl selSizzGwt = new SelectorEngineSizzleGwt();
    SelectorEngineImpl selJS = new SelectorEngineJS();
    SelectorEngineImpl selXpath = new SelectorEngineXPath();
    SelectorEngineImpl selC2X = new SelectorEngineCssToXPath();
    SelectorEngineImpl selNative = new SelectorEngineNative();
    // FIXME: this fails when running in iframe since 2.5.0
//    assertArrayContains(selector + " - (selSizz)", selSizz.select(selector, elem).getLength(), array);
//    assertArrayContains(selector + " - (selSizzGwt) ", selSizzGwt.select(selector, elem).getLength(), array);
    assertArrayContains(selector + " - (selJS)", selJS.select(selector, elem).getLength(), array);
    if (hasNativeSelector()) {
      assertArrayContains(selector + " - (selNative)", selNative.select(selector, elem).getLength(), array);
    }
    assertArrayContains(selector + " - (selXpath)", selXpath.select(selector, elem).getLength(), array);
    assertArrayContains(selector + " - (selC2X)", selC2X.select(selector, elem).getLength(), array);
  }

  private void executeSelectorEngineTests(SelectorEngineImpl selEng) {
    $(e).html(getTestContent());

    assertArrayContains(selEng.select("body", document).getLength(), 1);
    assertArrayContains(selEng.select("body div", document).getLength(), 53, 55);

    assertArrayContains(selEng.select("tr:first", e).getLength(), 0, 1, 5);
    assertArrayContains(selEng.select("tr:last", e).getLength(), 0, 1, 5);
    assertArrayContains(selEng.select("p:contains(selectors)", e).getLength(), 54);
    assertArrayContains(selEng.select("h1[id]:contains(Selectors)", e).getLength(), 1);
    assertArrayContains(selEng.select("div[class!=madeup]", e).getLength(), 52, 53);
    assertArrayContains(selEng.select("div, p a", e).getLength(), 136, 137, 138);
    assertArrayContains(selEng.select("p:contains(selectors)", e).getLength(), 54, 55);
    assertArrayContains(selEng.select("a[href][lang][class]", e).getLength(), 1);
    assertArrayContains(selEng.select("*:checked", e).getLength(), 1);
    assertArrayContains(selEng.select("div .example", e).getLength(), 43);
    assertArrayContains(selEng.select("div > div", e).getLength(), 51, 52);
    assertArrayContains(selEng.select("div:not(.example)", e).getLength(), 9, 10);
    assertArrayContains(selEng.select("div p", e).getLength(), 324);
    assertArrayContains(selEng.select("div p a", e).getLength(), 85, 84);
    assertArrayContains(selEng.select("div + p", e).getLength(), 22);
    assertArrayContains(selEng.select("div[class^=exa][class$=mple]", e).getLength(), 43);
    assertArrayContains(selEng.select("div #title", e).getLength(), 1);
    assertArrayContains(selEng.select("div ~ p", e).getLength(), 183);
    assertArrayContains(selEng.select("div[class]", e).getLength(), 51, 52);
    assertArrayContains(selEng.select("div[class~=dialog]", e).getLength(), 1);
    assertArrayContains(selEng.select("div[class*=e]", e).getLength(), 50);
    assertArrayContains(selEng.select("div[class=example]", e).getLength(), 43);
    assertArrayContains(selEng.select("div[class~=dialog]", e).getLength(), 1);
    assertArrayContains(selEng.select("div[class^=exa]", e).getLength(), 43);
    assertArrayContains(selEng.select("div[class$=mple]", e).getLength(), 43);
    assertArrayContains(selEng.select("p:first-child", e).getLength(), 54);
    assertArrayContains(selEng.select("h1#title", e).getLength(), 1);
    assertArrayContains(selEng.select("h1#title + em > span", e).getLength(), 1);
    assertArrayContains(selEng.select("p:last-child", e).getLength(), 19, 22);
    assertArrayContains(selEng.select(".note", e).getLength(), 14);
    assertArrayContains(selEng.select("p:nth-child(n)", e).getLength(), 324);
    assertArrayContains(selEng.select("p:nth-child(2n)", e).getLength(), 159);
    assertArrayContains(selEng.select("p:nth-child(2n+1)", e).getLength(), 165);
    assertArrayContains(selEng.select("p:nth-child(even)", e).getLength(), 159);
    assertArrayContains(selEng.select("p:nth-child(odd)", e).getLength(), 165);
    assertArrayContains(selEng.select("p:only-child", e).getLength(), 3);
    assertArrayContains(selEng.select("#title", e).getLength(), 1);
    // TODO: sizze_gwt returns 2
    assertArrayContains(selEng.select("#title, h1#title", e).getLength(), 1, 2);
    assertArrayContains(selEng.select("ul.toc li.tocline2", e).getLength(), 12);
    assertArrayContains(selEng.select("h1[id]:contains(Selectors)", e).getLength(),  1);
  }

  // This method is used to initialize a huge html String, because
  // java 1.5 has a limitation in the size of static strings.
  private String getTestContent() {
    String ret = "";
    ret += "<html><head>      </head><body><div>";
    ret += "      <div class='head dialog'>";
    ret += "          <p><a href='http://www.w3.org/'><img alt='W3C' src='' height='48' width='72'></a></p>";
    ret += "          <h1 id='title'>Selectors</h1>";
    ret += "          <em><span>.</span></em>";
    ret += "          <h2>W3C Working Draft 15 December 2005</h2>";
    ret += "          <dl>";
    ret += "              <dt>This version:</dt>";
    ret += "              <dd><a href='http://www.w3.org/TR/2005/WD-css3-selectors-20051215'>";
    ret += "                  http://www.w3.org/TR/2005/WD-css3-selectors-20051215</a></dd>";
    ret += "              <dt>Latest version:";
    ret += "              </dt><dd><a href='http://www.w3.org/TR/css3-selectors'>";
    ret += "                  http://www.w3.org/TR/css3-selectors</a>";
    ret += "              </dd><dt>Previous version:";
    ret += "              </dt><dd><a href='http://www.w3.org/TR/2001/CR-css3-selectors-20011113'>";
    ret += "                  http://www.w3.org/TR/2001/CR-css3-selectors-20011113</a>";
    ret += "              </dd><dt><a name='editors-list'></a>Editors:";
    ret += "              </dt><dd class='vcard'><span class='fn'>Daniel Glazman</span> (Invited";
    ret += "              </dd>";
    ret += "              <dd class='vcard'><a class='url fn' href='http://www.tantek.com/' lang='tr'>Tantek Çelik</a>";
    ret += "              </dd><dd class='vcard'><a href='mailto:ian@hixie.ch' class='url fn'>Ian";
    ret += "                  Hickson</a> (<span class='company'><a href='http://www.google.com/'>Google</a></span>)";
    ret += "              </dd><dd class='vcard'><span class='fn'>Peter Linss</span> (former";
    ret += "                  editor, <span class='company'><a href='http://www.netscape.com/'>Netscape/AOL</a></span>)";
    ret += "              </dd><dd class='vcard'><span class='fn'>John Williams</span> (former editor, <span class='company'><a href='http://www.quark.com/'>Quark, Inc.</a></span>)";
    ret += "          </dd></dl>";
    ret += "          <p class='copyright'><a href='http://www.w3.org/Consortium/Legal/ipr-notice#Copyright'>";
    ret += "              Copyright</a> © 2005 <a href='http://www.w3.org/'><abbr title='World Wide Web Consortium'>W3C</abbr></a><sup>®</sup>";
    ret += "              (<a href='http://www.csail.mit.edu/'><abbr title='Massachusetts";
    ret += "         Institute of Technology'>MIT</abbr></a>, <a href='http://www.ercim.org/'><acronym title='European Research";
    ret += "         Consortium for Informatics and Mathematics'>ERCIM</acronym></a>, <a href='http://www.keio.ac.jp/'>Keio</a>), All Rights Reserved.";
    ret += "              <a href='http://www.w3.org/Consortium/Legal/ipr-notice#Legal_Disclaimer'>liability</a>,";
    ret += "              <a href='http://www.w3.org/Consortium/Legal/ipr-notice#W3C_Trademarks'>trademark</a>,";
    ret += "              <a href='http://www.w3.org/Consortium/Legal/copyright-documents'>document";
    ret += "                  use</a> rules apply.";
    ret += "          </p><hr title='Separator for header'>";
    ret += "      </div>";
    ret += "      <h2><a name='abstract'></a>Abstract</h2>";
    ret += "      <p><em>Selectors</em> are patterns that match against elements in a";
    ret += "          tree. Selectors have been optimized for use with HTML and XML, and";
    ret += "          are designed to be usable in performance-critical code.</p>";
    ret += "      <p><acronym title='Cascading Style Sheets'>CSS</acronym> (Cascading";
    ret += "          Style Sheets) is a language for describing the rendering of <acronym title='Hypertext Markup Language'>HTML</acronym> and <acronym title='Extensible Markup Language'>XML</acronym> documents on";
    ret += "          screen, on paper, in speech, etc. CSS uses Selectors for binding";
    ret += "          describes extensions to the selectors defined in CSS level 2. These";
    ret += "          extended selectors will be used by CSS level 3.";
    ret += "      </p><p>Selectors define the following function:</p>";
    ret += "      <pre>expression ∗ element → boolean</pre>";
    ret += "      <p>That is, given an element and a selector, this specification";
    ret += "          defines whether that element matches the selector.</p>";
    ret += "      <p>These expressions can also be used, for instance, to select a set";
    ret += "          subtree. <acronym title='Simple Tree Transformation";
    ret += "        Sheets'>STTS</acronym> (Simple Tree Transformation Sheets), a";
    ret += "          language for transforming XML trees, uses this mechanism. <a href='#refsSTTS'>[STTS]</a></p>";
    ret += "      <h2><a name='status'></a>Status of this document</h2>";
    ret += "      <p><em>This section describes the status of this document at the";
    ret += "          of this technical report can be found in the <a href='http://www.w3.org/TR/'>W3C technical reports index at";
    ret += "              http://www.w3.org/TR/.</a></em></p>";
    ret += "      <p>This document describes the selectors that already exist in <a href='#refsCSS1'><abbr title='CSS level 1'>CSS1</abbr></a> and <a href='#refsCSS21'><abbr title='CSS level 2'>CSS2</abbr></a>, and";
    ret += "          also proposes new selectors for <abbr title='CSS level";
    ret += "        3'>CSS3</abbr> and other languages that may need them.</p>";
    ret += "      <p>The CSS Working Group doesn't expect that all implementations of";
    ret += "          CSS3 will have to implement all selectors. Instead, there will";
    ret += "          will include all of the selectors.</p>";
    ret += "      <p>This specification is a last call working draft for the the <a href='http://www.w3.org/Style/CSS/members'>CSS Working Group</a>";
    ret += "          (<a href='/Style/'>Style Activity</a>). This";
    ret += "          document is a revision of the <a href='http://www.w3.org/TR/2001/CR-css3-selectors-20011113/'>Candidate";
    ret += "              Recommendation dated 2001 November 13</a>, and has incorporated";
    ret += "          be demonstrable.</p>";
    ret += "      <p>All persons are encouraged to review and implement this";
    ret += "          specification and return comments to the (<a href='http://lists.w3.org/Archives/Public/www-style/'>archived</a>)";
    ret += "          public mailing list <a href='http://www.w3.org/Mail/Lists.html#www-style'>www-style</a>";
    ret += "          (see <a href='http://www.w3.org/Mail/Request'>instructions</a>). W3C";
    ret += "          The deadline for comments is 14 January 2006.</p>";
    ret += "      <p>This is still a draft document and may be updated, replaced, or";
    ret += "      </p><p>This document may be available in <a href='http://www.w3.org/Style/css3-selectors-updates/translations'>translation</a>.";
    ret += "      </p><div class='subtoc'>";
    ret += "          <h2><a name='contents'>Table of contents</a></h2>";
    ret += "          <ul class='toc'>";
    ret += "              <li class='tocline2'><a href='#context'>1. Introduction</a>";
    ret += "                  <ul>";
    ret += "                      <li><a href='#dependencies'>1.1. Dependencies</a></li>";
    ret += "                      <li><a href='#terminology'>1.2. Terminology</a></li>";
    ret += "                      <li><a href='#changesFromCSS2'>1.3. Changes from CSS2</a></li>";
    ret += "                  </ul>";
    ret += "              </li><li class='tocline2'><a href='#selectors'>2. Selectors</a>";
    ret += "              </li><li class='tocline2'><a href='#casesens'>3. Case sensitivity</a>";
    ret += "              </li><li class='tocline2'><a href='#selector-syntax'>4. Selector syntax</a>";
    ret += "              </li><li class='tocline2'><a href='#grouping'>5. Groups of selectors</a>";
    ret += "              </li><li class='tocline2'><a href='#simple-selectors'>6. Simple selectors</a>";
    ret += "                  <ul class='toc'>";
    ret += "                      <li class='tocline3'><a href='#type-selectors'>6.1. Type";
    ret += "                          selectors</a>";
    ret += "                          <ul class='toc'>";
    ret += "                              <li class='tocline4'><a href='#typenmsp'>6.1.1. Type";
    ret += "                                  selectors and namespaces</a></li>";
    ret += "                          </ul>";
    ret += "                      </li><li class='tocline3'><a href='#universal-selector'>6.2.";
    ret += "                          Universal selector</a>";
    ret += "                          <ul>";
    ret += "                              <li><a href='#univnmsp'>6.2.1. Universal selector and";
    ret += "                                  namespaces</a></li>";
    ret += "                          </ul>";
    ret += "                      </li><li class='tocline3'><a href='#attribute-selectors'>6.3.";
    ret += "                          Attribute selectors</a>";
    ret += "                          <ul class='toc'>";
    ret += "                              <li class='tocline4'><a href='#attribute-representation'>6.3.1.";
    ret += "                                  values</a>";
    ret += "                              </li><li><a href='#attribute-substrings'>6.3.2. Substring";
    ret += "                                  matching attribute selectors</a>";
    ret += "                              </li><li class='tocline4'><a href='#attrnmsp'>6.3.3.";
    ret += "                                  Attribute selectors and namespaces</a>";
    ret += "                              </li><li class='tocline4'><a href='#def-values'>6.3.4.";
    ret += "                                  Default attribute values in DTDs</a></li>";
    ret += "                          </ul>";
    ret += "                      </li><li class='tocline3'><a href='#class-html'>6.4. Class";
    ret += "                          selectors</a>";
    ret += "                      </li><li class='tocline3'><a href='#id-selectors'>6.5. ID";
    ret += "                          selectors</a>";
    ret += "                      </li><li class='tocline3'><a href='#pseudo-classes'>6.6.";
    ret += "                          Pseudo-classes</a>";
    ret += "                          <ul class='toc'>";
    ret += "                              <li class='tocline4'><a href='#dynamic-pseudos'>6.6.1.";
    ret += "                                  Dynamic pseudo-classes</a>";
    ret += "                              </li><li class='tocline4'><a href='#target-pseudo'>6.6.2. The";
    ret += "                                  :target pseudo-class</a>";
    ret += "                              </li><li class='tocline4'><a href='#lang-pseudo'>6.6.3. The";
    ret += "                                  :lang() pseudo-class</a>";
    ret += "                              </li><li class='tocline4'><a href='#UIstates'>6.6.4. UI";
    ret += "                                  element states pseudo-classes</a>";
    ret += "                              </li><li class='tocline4'><a href='#structural-pseudos'>6.6.5.";
    ret += "                                  Structural pseudo-classes</a>";
    ret += "                                  <ul>";
    ret += "                                      <li><a href='#root-pseudo'>:root";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#nth-child-pseudo'>:nth-child()";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#nth-last-child-pseudo'>:nth-last-child()</a>";
    ret += "                                      </li><li><a href='#nth-of-type-pseudo'>:nth-of-type()";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#nth-last-of-type-pseudo'>:nth-last-of-type()</a>";
    ret += "                                      </li><li><a href='#first-child-pseudo'>:first-child";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#last-child-pseudo'>:last-child";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#first-of-type-pseudo'>:first-of-type";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#last-of-type-pseudo'>:last-of-type";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#only-child-pseudo'>:only-child";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#only-of-type-pseudo'>:only-of-type";
    ret += "                                          pseudo-class</a>";
    ret += "                                      </li><li><a href='#empty-pseudo'>:empty";
    ret += "                                          pseudo-class</a></li>";
    ret += "                                  </ul>";
    ret += "                              </li><li class='tocline4'><a href='#negation'>6.6.7. The";
    ret += "                                  negation pseudo-class</a></li>";
    ret += "                          </ul>";
    ret += "                      </li>";
    ret += "                  </ul>";
    ret += "              </li><li><a href='#pseudo-elements'>7. Pseudo-elements</a>";
    ret += "                  <ul>";
    ret += "                      <li><a href='#first-line'>7.1. The ::first-line";
    ret += "                          pseudo-element</a>";
    ret += "                      </li><li><a href='#first-letter'>7.2. The ::first-letter";
    ret += "                          pseudo-element</a>";
    ret += "                      </li><li><a href='#UIfragments'>7.3. The ::selection";
    ret += "                          pseudo-element</a>";
    ret += "                      </li><li><a href='#gen-content'>7.4. The ::before and ::after";
    ret += "                          pseudo-elements</a></li>";
    ret += "                  </ul>";
    ret += "              </li><li class='tocline2'><a href='#combinators'>8. Combinators</a>";
    ret += "                  <ul class='toc'>";
    ret += "                      <li class='tocline3'><a href='#descendant-combinators'>8.1.";
    ret += "                          Descendant combinators</a>";
    ret += "                      </li><li class='tocline3'><a href='#child-combinators'>8.2. Child";
    ret += "                          combinators</a>";
    ret += "                      </li><li class='tocline3'><a href='#sibling-combinators'>8.3. Sibling";
    ret += "                          combinators</a>";
    ret += "                          <ul class='toc'>";
    ret += "                              <li class='tocline4'><a href='#adjacent-sibling-combinators'>8.3.1.";
    ret += "                                  Adjacent sibling combinator</a>";
    ret += "                              </li><li class='tocline4'><a href='#general-sibling-combinators'>8.3.2.";
    ret += "                                  General sibling combinator</a></li>";
    ret += "                          </ul>";
    ret += "                      </li>";
    ret += "                  </ul>";
    ret += "              </li><li class='tocline2'><a href='#specificity'>9. Calculating a selector's";
    ret += "                  specificity</a>";
    ret += "              </li><li class='tocline2'><a href='#w3cselgrammar'>10. The grammar of";
    ret += "                  Selectors</a>";
    ret += "                  <ul class='toc'>";
    ret += "                      <li class='tocline3'><a href='#grammar'>10.1. Grammar</a>";
    ret += "                      </li><li class='tocline3'><a href='#lex'>10.2. Lexical scanner</a>";
    ret += "                      </li>";
    ret += "                  </ul>";
    ret += "              </li><li class='tocline2'><a href='#downlevel'>11. Namespaces and down-level";
    ret += "                  clients</a>";
    ret += "              </li><li class='tocline2'><a href='#profiling'>12. Profiles</a>";
    ret += "              </li><li><a href='#Conformance'>13. Conformance and requirements</a>";
    ret += "              </li><li><a href='#Tests'>14. Tests</a>";
    ret += "              </li><li><a href='#ACKS'>15. Acknowledgements</a>";
    ret += "              </li><li class='tocline2'><a href='#references'>16. References</a>";
    ret += "          </li></ul>";
    ret += "      </div>";
    ret += "      <h2><a name='context'>1. Introduction</a></h2>";
    ret += "      <h3><a name='dependencies'></a>1.1. Dependencies</h3>";
    ret += "      <p>Some features of this specification are specific to CSS, or have";
    ret += "          specification, these have been described in terms of CSS2.1. <a href='#refsCSS21'>[CSS21]</a></p>";
    ret += "      <h3><a name='terminology'></a>1.2. Terminology</h3>";
    ret += "      <p>All of the text of this specification is normative except";
    ret += "          non-normative.</p>";
    ret += "      <h3><a name='changesFromCSS2'></a>1.3. Changes from CSS2</h3>";
    ret += "      <p><em>This section is non-normative.</em></p>";
    ret += "      <p>The main differences between the selectors in CSS2 and those in";
    ret += "          Selectors are:";
    ret += "      </p><ul>";
    ret += "          <li>the list of basic definitions (selector, group of selectors,";
    ret += "              of simple selectors, and the term 'simple selector' is now used for";
    ret += "          </li>";
    ret += "          <li>an optional namespace component is now allowed in type element";
    ret += "              selectors, the universal selector and attribute selectors";
    ret += "          </li>";
    ret += "          <li>a <a href='#general-sibling-combinators'>new combinator</a> has been";
    ret += "          </li>";
    ret += "          <li>new simple selectors including substring matching attribute";
    ret += "              selectors, and new pseudo-classes";
    ret += "          </li>";
    ret += "          <li>new pseudo-elements, and introduction of the '::' convention";
    ret += "          </li>";
    ret += "          <li>the grammar has been rewritten</li>";
    ret += "          <li>profiles to be added to specifications integrating Selectors";
    ret += "              and defining the set of selectors which is actually supported by";
    ret += "          </li>";
    ret += "          <li>Selectors are now a CSS3 Module and an independent";
    ret += "          </li>";
    ret += "          <li>the specification now has its own test suite</li>";
    ret += "      </ul>";
    ret += "      <h2><a name='selectors'></a>2. Selectors</h2>";
    ret += "      <p><em>This section is non-normative, as it merely summarizes the";
    ret += "          following sections.</em></p>";
    ret += "      <p>A Selector represents a structure. This structure can be used as a";
    ret += "          HTML or XML fragment corresponding to that structure.</p>";
    ret += "      <p>Selectors may range from simple element names to rich contextual";
    ret += "          representations.</p>";
    ret += "      <p>The following table summarizes the Selector syntax:</p>";
    ret += "      <table class='selectorsReview'>";
    ret += "      <thead>";
    ret += "      <tr>";
    ret += "          <th class='pattern'>Pattern</th>";
    ret += "          <th class='meaning'>Meaning</th>";
    ret += "          <th class='described'>Described in section</th>";
    ret += "          <th class='origin'>First defined in CSS level</th>";
    ret += "      </tr>";
    ret += "      </thead><tbody>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>*</td>";
    ret += "          <td class='meaning'>any element</td>";
    ret += "          <td class='described'><a href='#universal-selector'>Universal";
    ret += "              selector</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E</td>";
    ret += "          <td class='meaning'>an element of type E</td>";
    ret += "          <td class='described'><a href='#type-selectors'>Type selector</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo]</td>";
    ret += "          <td class='meaning'>an E element with a 'foo' attribute</td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo='bar']</td>";
    ret += "          <td class='meaning'>an E element whose 'foo' attribute value is exactly";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo~='bar']</td>";
    ret += "          <td class='meaning'>an E element whose 'foo' attribute value is a list of";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo^='bar']</td>";
    ret += "          <td class='meaning'>an E element whose 'foo' attribute value begins exactly";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo$='bar']</td>";
    ret += "          <td class='meaning'>an E element whose 'foo' attribute value ends exactly";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[foo*='bar']</td>";
    ret += "          <td class='meaning'>an E element whose 'foo' attribute value contains the";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E[hreflang|='en']</td>";
    ret += "          <td class='meaning'>an E element whose 'hreflang' attribute has a";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#attribute-selectors'>Attribute";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:root</td>";
    ret += "          <td class='meaning'>an E element, root of the document</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:nth-child(n)</td>";
    ret += "          <td class='meaning'>an E element, the n-th child of its parent</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:nth-last-child(n)</td>";
    ret += "          <td class='meaning'>an E element, the n-th child of its parent, counting";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:nth-of-type(n)</td>";
    ret += "          <td class='meaning'>an E element, the n-th sibling of its type</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:nth-last-of-type(n)</td>";
    ret += "          <td class='meaning'>an E element, the n-th sibling of its type, counting";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:first-child</td>";
    ret += "          <td class='meaning'>an E element, first child of its parent</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:last-child</td>";
    ret += "          <td class='meaning'>an E element, last child of its parent</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:first-of-type</td>";
    ret += "          <td class='meaning'>an E element, first sibling of its type</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:last-of-type</td>";
    ret += "          <td class='meaning'>an E element, last sibling of its type</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:only-child</td>";
    ret += "          <td class='meaning'>an E element, only child of its parent</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:only-of-type</td>";
    ret += "          <td class='meaning'>an E element, only sibling of its type</td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:empty</td>";
    ret += "          <td class='meaning'>an E element that has no children (including text";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#structural-pseudos'>Structural";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:link<br>E:visited</td>";
    ret += "          <td class='meaning'>an E element being the source anchor of a hyperlink of";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#link'>The link";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:active<br>E:hover<br>E:focus</td>";
    ret += "          <td class='meaning'>an E element during certain user actions</td>";
    ret += "          <td class='described'><a href='#useraction-pseudos'>The user";
    ret += "              action pseudo-classes</a></td>";
    ret += "          <td class='origin'>1 and 2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:target</td>";
    ret += "          <td class='meaning'>an E element being the target of the referring URI</td>";
    ret += "          <td class='described'><a href='#target-pseudo'>The target";
    ret += "              pseudo-class</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:lang(fr)</td>";
    ret += "          <td class='meaning'>an element of type E in language 'fr' (the document";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#lang-pseudo'>The :lang()";
    ret += "              pseudo-class</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:enabled<br>E:disabled</td>";
    ret += "          <td class='meaning'>a user interface element E which is enabled or";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#UIstates'>The UI element states";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:checked<!--<br>E:indeterminate--></td>";
    ret += "          <td class='meaning'>a user interface element E which is checked<!-- or in an";
    ret += "            indeterminate state--> (for instance a radio-button or checkbox)";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#UIstates'>The UI element states";
    ret += "              pseudo-classes</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E::first-line</td>";
    ret += "          <td class='meaning'>the first formatted line of an E element</td>";
    ret += "          <td class='described'><a href='#first-line'>The ::first-line";
    ret += "              pseudo-element</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E::first-letter</td>";
    ret += "          <td class='meaning'>the first formatted letter of an E element</td>";
    ret += "          <td class='described'><a href='#first-letter'>The ::first-letter";
    ret += "              pseudo-element</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E::selection</td>";
    ret += "          <td class='meaning'>the portion of an E element that is currently";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#UIfragments'>The UI element";
    ret += "              fragments pseudo-elements</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E::before</td>";
    ret += "          <td class='meaning'>generated content before an E element</td>";
    ret += "          <td class='described'><a href='#gen-content'>The ::before";
    ret += "              pseudo-element</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E::after</td>";
    ret += "          <td class='meaning'>generated content after an E element</td>";
    ret += "          <td class='described'><a href='#gen-content'>The ::after";
    ret += "              pseudo-element</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E.warning</td>";
    ret += "          <td class='meaning'>an E element whose class is";
    ret += "          </td>";
    ret += "          <td class='described'><a href='#class-html'>Class";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E#myid</td>";
    ret += "          <td class='meaning'>an E element with ID equal to 'myid'.</td>";
    ret += "          <td class='described'><a href='#id-selectors'>ID";
    ret += "              selectors</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E:not(s)</td>";
    ret += "          <td class='meaning'>an E element that does not match simple selector s</td>";
    ret += "          <td class='described'><a href='#negation'>Negation";
    ret += "              pseudo-class</a></td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E F</td>";
    ret += "          <td class='meaning'>an F element descendant of an E element</td>";
    ret += "          <td class='described'><a href='#descendant-combinators'>Descendant";
    ret += "              combinator</a></td>";
    ret += "          <td class='origin'>1</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E &gt; F</td>";
    ret += "          <td class='meaning'>an F element child of an E element</td>";
    ret += "          <td class='described'><a href='#child-combinators'>Child";
    ret += "              combinator</a></td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E + F</td>";
    ret += "          <td class='meaning'>an F element immediately preceded by an E element</td>";
    ret += "          <td class='described'><a href='#adjacent-sibling-combinators'>Adjacent sibling combinator</a>";
    ret += "          </td>";
    ret += "          <td class='origin'>2</td>";
    ret += "      </tr>";
    ret += "      <tr>";
    ret += "          <td class='pattern'>E ~ F</td>";
    ret += "          <td class='meaning'>an F element preceded by an E element</td>";
    ret += "          <td class='described'><a href='#general-sibling-combinators'>General sibling combinator</a>";
    ret += "          </td>";
    ret += "          <td class='origin'>3</td>";
    ret += "      </tr>";
    ret += "      </tbody>";
    ret += "      </table>";
    ret += "      <p>The meaning of each selector is derived from the table above by";
    ret += "          column.</p>";
    ret += "      <h2><a name='casesens'>3. Case sensitivity</a></h2>";
    ret += "      <p>The case sensitivity of document language element names, attribute";
    ret += "          names, and attribute values in selectors depends on the document";
    ret += "          but in XML, they are case-sensitive.</p>";
    ret += "      <h2><a name='selector-syntax'>4. Selector syntax</a></h2>";
    ret += "      <p>A <dfn><a name='selector'>selector</a></dfn> is a chain of one";
    ret += "          or more <a href='#sequence'>sequences of simple selectors</a>";
    ret += "          separated by <a href='#combinators'>combinators</a>.</p>";
    ret += "      <p>A <dfn><a name='sequence'>sequence of simple selectors</a></dfn>";
    ret += "          is a chain of <a href='#simple-selectors-dfn'>simple selectors</a>";
    ret += "          that are not separated by a <a href='#combinators'>combinator</a>. It";
    ret += "          always begins with a <a href='#type-selectors'>type selector</a> or a";
    ret += "          <a href='#universal-selector'>universal selector</a>. No other type";
    ret += "          selector or universal selector is allowed in the sequence.</p>";
    ret += "      <p>A <dfn><a name='simple-selectors-dfn'></a><a href='#simple-selectors'>simple selector</a></dfn> is either a <a href='#type-selectors'>type selector</a>, <a href='#universal-selector'>universal selector</a>, <a href='#attribute-selectors'>attribute selector</a>, <a href='#class-html'>class selector</a>, <a href='#id-selectors'>ID selector</a>, <a href='#content-selectors'>content selector</a>, or <a href='#pseudo-classes'>pseudo-class</a>. One <a href='#pseudo-elements'>pseudo-element</a> may be appended to the last";
    ret += "          sequence of simple selectors.</p>";
    ret += "      <p><dfn>Combinators</dfn> are: white space, 'greater-than";
    ret += "          sign' (U+003E, <code>&gt;</code>), 'plus sign' (U+002B,";
    ret += "          <code>+</code>) and 'tilde' (U+007E, <code>~</code>). White";
    ret += "          space may appear between a combinator and the simple selectors around";
    ret += "          it. <a name='whitespace'></a>Only the characters 'space' (U+0020), 'tab'";
    ret += "          never part of white space.</p>";
    ret += "      <p>The elements of a document tree that are represented by a selector";
    ret += "          are the <dfn><a name='subject'></a>subjects of the selector</dfn>. A";
    ret += "          selector consisting of a single sequence of simple selectors";
    ret += "          sequence of simple selectors and a combinator to a sequence imposes";
    ret += "          simple selectors.</p>";
    ret += "      <p>An empty selector, containing no sequence of simple selectors and";
    ret += "          no pseudo-element, is an <a href='#Conformance'>invalid";
    ret += "              selector</a>.</p>";
    ret += "      <h2><a name='grouping'>5. Groups of selectors</a></h2>";
    ret += "      <p>When several selectors share the same declarations, they may be";
    ret += "          grouped into a comma-separated list. (A comma is U+002C.)</p>";
    ret += "      <div class='example'>";
    ret += "          <p>CSS examples:</p>";
    ret += "          <p>In this example, we condense three rules with identical";
    ret += "              declarations into one. Thus,</p>";
    ret += "      <pre>h1 { font-family: sans-serif }";
    ret += "      h3 { font-family: sans-serif }</pre>";
    ret += "          <p>is equivalent to:</p>";
    ret += "          <pre>h1, h2, h3 { font-family: sans-serif }</pre>";
    ret += "      </div>";
    ret += "      <p><strong>Warning</strong>: the equivalence is true in this example";
    ret += "          because all the selectors are valid selectors. If just one of these";
    ret += "          selectors were invalid, the entire group of selectors would be";
    ret += "          heading rules would be invalidated.</p>";
    ret += "      <h2><a name='simple-selectors'>6. Simple selectors</a></h2>";
    ret += "      <h3><a name='type-selectors'>6.1. Type selector</a></h3>";
    ret += "      <p>A <dfn>type selector</dfn> is the name of a document language";
    ret += "          type in the document tree.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The following selector represents an <code>h1</code> element in the";
    ret += "              document tree:</p>";
    ret += "          <pre>h1</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='typenmsp'>6.1.1. Type selectors and namespaces</a></h4>";
    ret += "      <p>Type selectors allow an optional namespace (<a href='#refsXMLNAMES'>[XMLNAMES]</a>) component. A namespace prefix";
    ret += "          (U+007C, <code>|</code>).</p>";
    ret += "      <p>The namespace component may be left empty to indicate that the";
    ret += "          selector is only to represent elements with no declared namespace.</p>";
    ret += "      <p>An asterisk may be used for the namespace prefix, indicating that";
    ret += "          with no namespace).</p>";
    ret += "      <p>Element type selectors that have no namespace component (no";
    ret += "          element's namespace (equivalent to '<code>*|</code>') unless a default";
    ret += "          namespace.</p>";
    ret += "      <p>A type selector containing a namespace prefix that has not been";
    ret += "          previously declared is an <a href='#Conformance'>invalid</a> selector.";
    ret += "          language implementing Selectors. In CSS, such a mechanism is defined";
    ret += "          in the General Syntax module.</p>";
    ret += "      <p>In a namespace-aware client, element type selectors will only match";
    ret += "          against the <a href='http://www.w3.org/TR/REC-xml-names/#NT-LocalPart'>local";
    ret += "              part</a>";
    ret += "          of the element's <a href='http://www.w3.org/TR/REC-xml-names/#ns-qualnames'>qualified";
    ret += "              name</a>. See <a href='#downlevel'>below</a> for notes about matching";
    ret += "          behaviors in down-level clients.</p>";
    ret += "      <p>In summary:</p>";
    ret += "      <dl>";
    ret += "          <dt><code>ns|E</code></dt>";
    ret += "          <dd>elements with name E in namespace ns</dd>";
    ret += "          <dt><code>*|E</code></dt>";
    ret += "          <dd>elements with name E in any namespace, including those without any";
    ret += "          </dd>";
    ret += "          <dt><code>|E</code></dt>";
    ret += "          <dd>elements with name E without any declared namespace</dd>";
    ret += "          <dt><code>E</code></dt>";
    ret += "          <dd>if no default namespace has been specified, this is equivalent to *|E.";
    ret += "          </dd>";
    ret += "      </dl>";
    ret += "      <div class='example'>";
    ret += "          <p>CSS examples:</p>";
    ret += "       <pre>@namespace foo url(http://www.example.com);";
    ret += "       h1 { color: green }</pre>";
    ret += "          <p>The first rule will match only <code>h1</code> elements in the";
    ret += "              'http://www.example.com' namespace.</p>";
    ret += "          <p>The second rule will match all elements in the";
    ret += "              'http://www.example.com' namespace.</p>";
    ret += "          <p>The third rule will match only <code>h1</code> elements without";
    ret += "              any declared namespace.</p>";
    ret += "          <p>The fourth rule will match <code>h1</code> elements in any";
    ret += "              namespace (including those without any declared namespace).</p>";
    ret += "          <p>The last rule is equivalent to the fourth rule because no default";
    ret += "              namespace has been defined.</p>";
    ret += "      </div>";
    ret += "      <h3><a name='universal-selector'>6.2. Universal selector</a></h3>";
    ret += "      <p>The <dfn>universal selector</dfn>, written 'asterisk'";
    ret += "          (<code>*</code>), represents the qualified name of any element";
    ret += "          specified, see <a href='#univnmsp'>Universal selector and";
    ret += "              Namespaces</a> below.</p>";
    ret += "      <p>If the universal selector is not the only component of a sequence";
    ret += "          of simple selectors, the <code>*</code> may be omitted.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <ul>";
    ret += "              <li><code>*[hreflang|=en]</code> and <code>[hreflang|=en]</code> are";
    ret += "              </li>";
    ret += "              <li><code>*.warning</code> and <code>.warning</code> are equivalent,";
    ret += "              </li>";
    ret += "              <li><code>*#myid</code> and <code>#myid</code> are equivalent.</li>";
    ret += "          </ul>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note:</strong> it is recommended that the";
    ret += "          <code>*</code>, representing the universal selector, not be";
    ret += "          omitted.</p>";
    ret += "      <h4><a name='univnmsp'>6.2.1. Universal selector and namespaces</a></h4>";
    ret += "      <p>The universal selector allows an optional namespace component. It";
    ret += "          is used as follows:</p>";
    ret += "      <dl>";
    ret += "          <dt><code>ns|*</code></dt>";
    ret += "          <dd>all elements in namespace ns</dd>";
    ret += "          <dt><code>*|*</code></dt>";
    ret += "          <dd>all elements</dd>";
    ret += "          <dt><code>|*</code></dt>";
    ret += "          <dd>all elements without any declared namespace</dd>";
    ret += "          <dt><code>*</code></dt>";
    ret += "          <dd>if no default namespace has been specified, this is equivalent to *|*.";
    ret += "          </dd>";
    ret += "      </dl>";
    ret += "      <p>A universal selector containing a namespace prefix that has not";
    ret += "          been previously declared is an <a href='#Conformance'>invalid</a>";
    ret += "          to the language implementing Selectors. In CSS, such a mechanism is";
    ret += "          defined in the General Syntax module.</p>";
    ret += "      <h3><a name='attribute-selectors'>6.3. Attribute selectors</a></h3>";
    ret += "      <p>Selectors allow the representation of an element's attributes. When";
    ret += "          attribute selectors must be considered to match an element if that";
    ret += "          attribute selector.</p>";
    ret += "      <h4><a name='attribute-representation'>6.3.1. Attribute presence and values";
    ret += "          selectors</a></h4>";
    ret += "      <p>CSS2 introduced four attribute selectors:</p>";
    ret += "      <dl>";
    ret += "          <dt><code>[att]</code>";
    ret += "          </dt><dd>Represents an element with the <code>att</code> attribute, whatever the";
    ret += "          </dd>";
    ret += "          <dt><code>[att=val]</code></dt>";
    ret += "          <dd>Represents an element with the <code>att</code> attribute whose value is";
    ret += "          </dd>";
    ret += "          <dt><code>[att~=val]</code></dt>";
    ret += "          <dd>Represents an element with the <code>att</code> attribute whose value is";
    ret += "              a <a href='#whitespace'>whitespace</a>-separated list of words, one";
    ret += "              represent anything (since the words are <em>separated</em> by";
    ret += "          </dd>";
    ret += "          <dt><code>[att|=val]</code>";
    ret += "          </dt><dd>Represents an element with the <code>att</code> attribute, its value";
    ret += "              matches (e.g., the <code>hreflang</code> attribute on the";
    ret += "              <code>link</code> element in HTML) as described in RFC 3066 (<a href='#refsRFC3066'>[RFC3066]</a>). For <code>lang</code> (or";
    ret += "              <code>xml:lang</code>) language subcode matching, please see <a href='#lang-pseudo'>the <code>:lang</code> pseudo-class</a>.";
    ret += "          </dd>";
    ret += "      </dl>";
    ret += "      <p>Attribute values must be identifiers or strings. The";
    ret += "          case-sensitivity of attribute names and values in selectors depends on";
    ret += "          the document language.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following attribute selector represents an <code>h1</code>";
    ret += "              element that carries the <code>title</code> attribute, whatever its";
    ret += "              value:</p>";
    ret += "          <pre>h1[title]</pre>";
    ret += "          <p>In the following example, the selector represents a";
    ret += "              <code>span</code> element whose <code>class</code> attribute has";
    ret += "              exactly the value 'example':</p>";
    ret += "          <pre>span[class='example']</pre>";
    ret += "          <p>Multiple attribute selectors can be used to represent several";
    ret += "              attribute. Here, the selector represents a <code>span</code> element";
    ret += "              whose <code>hello</code> attribute has exactly the value 'Cleveland'";
    ret += "              and whose <code>goodbye</code> attribute has exactly the value";
    ret += "              'Columbus':</p>";
    ret += "          <pre>span[hello='Cleveland'][goodbye='Columbus']</pre>";
    ret += "          <p>The following selectors illustrate the differences between '='";
    ret += "              'copyright copyleft copyeditor' on a <code>rel</code> attribute. The";
    ret += "              second selector will only represent an <code>a</code> element with";
    ret += "              an <code>href</code> attribute having the exact value";
    ret += "              'http://www.w3.org/'.</p>";
    ret += "        <pre>a[rel~='copyright']";
    ret += "      a[href='http://www.w3.org/']</pre>";
    ret += "          <p>The following selector represents a <code>link</code> element";
    ret += "              whose <code>hreflang</code> attribute is exactly 'fr'.</p>";
    ret += "          <pre>link[hreflang=fr]</pre>";
    ret += "          <p>The following selector represents a <code>link</code> element for";
    ret += "              which the values of the <code>hreflang</code> attribute begins with";
    ret += "              'en', including 'en', 'en-US', and 'en-cockney':</p>";
    ret += "          <pre>link[hreflang|='en']</pre>";
    ret += "          <p>Similarly, the following selectors represents a";
    ret += "              <code>DIALOGUE</code> element whenever it has one of two different";
    ret += "              values for an attribute <code>character</code>:</p>";
    ret += "        <pre>DIALOGUE[character=romeo]";
    ret += "      DIALOGUE[character=juliet]</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='attribute-substrings'></a>6.3.2. Substring matching attribute";
    ret += "          selectors</h4>";
    ret += "      <p>Three additional attribute selectors are provided for matching";
    ret += "          substrings in the value of an attribute:</p>";
    ret += "      <dl>";
    ret += "          <dt><code>[att^=val]</code></dt>";
    ret += "          <dd>Represents an element with the <code>att</code> attribute whose value";
    ret += "          </dd>";
    ret += "          <dt><code>[att$=val]</code>";
    ret += "          </dt><dd>Represents an element with the <code>att</code> attribute whose value";
    ret += "          </dd>";
    ret += "          <dt><code>[att*=val]</code>";
    ret += "          </dt><dd>Represents an element with the <code>att</code> attribute whose value";
    ret += "          </dd>";
    ret += "      </dl>";
    ret += "      <p>Attribute values must be identifiers or strings. The";
    ret += "          case-sensitivity of attribute names in selectors depends on the";
    ret += "          document language.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following selector represents an HTML <code>object</code>,";
    ret += "              image:</p>";
    ret += "          <pre>object[type^='image/']</pre>";
    ret += "          <p>The following selector represents an HTML anchor <code>a</code> with an";
    ret += "              <code>href</code> attribute whose value ends with '.html'.</p>";
    ret += "          <pre>a[href$='.html']</pre>";
    ret += "          <p>The following selector represents an HTML paragraph with a";
    ret += "              <code>title</code>";
    ret += "              attribute whose value contains the substring 'hello'</p>";
    ret += "          <pre>p[title*='hello']</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='attrnmsp'>6.3.3. Attribute selectors and namespaces</a></h4>";
    ret += "      <p>Attribute selectors allow an optional namespace component to the";
    ret += "          separator 'vertical bar' (<code>|</code>). In keeping with";
    ret += "          apply to attributes, therefore attribute selectors without a namespace";
    ret += "          (equivalent to '<code>|attr</code>'). An asterisk may be used for the";
    ret += "      </p><p>An attribute selector with an attribute name containing a namespace";
    ret += "          prefix that has not been previously declared is an <a href='#Conformance'>invalid</a> selector. The mechanism for";
    ret += "          a namespace prefix is left up to the language implementing Selectors.";
    ret += "      </p><div class='example'>";
    ret += "          <p>CSS examples:</p>";
    ret += "        <pre>@namespace foo 'http://www.example.com';";
    ret += "      [att] { color: green }</pre>";
    ret += "          <p>The first rule will match only elements with the attribute";
    ret += "              <code>att</code> in the 'http://www.example.com' namespace with the";
    ret += "              value 'val'.</p>";
    ret += "          <p>The second rule will match only elements with the attribute";
    ret += "              <code>att</code> regardless of the namespace of the attribute";
    ret += "              (including no declared namespace).</p>";
    ret += "          <p>The last two rules are equivalent and will match only elements";
    ret += "              with the attribute <code>att</code> where the attribute is not";
    ret += "              declared to be in a namespace.</p>";
    ret += "      </div>";
    ret += "      <h4><a name='def-values'>6.3.4. Default attribute values in DTDs</a></h4>";
    ret += "      <p>Attribute selectors represent explicitly set attribute values in";
    ret += "          selectors. Selectors should be designed so that they work even if the";
    ret += "          default values are not included in the document tree.</p>";
    ret += "      <p>More precisely, a UA is <em>not</em> required to read an 'external";
    ret += "          subset' of the DTD but <em>is</em> required to look for default";
    ret += "          attribute values in the document's 'internal subset.' (See <a href='#refsXML10'>[XML10]</a> for definitions of these subsets.)</p>";
    ret += "      <p>A UA that recognizes an XML namespace <a href='#refsXMLNAMES'>[XMLNAMES]</a> is not required to use its";
    ret += "          required to use its built-in knowledge of the XHTML DTD.)</p>";
    ret += "      <p class='note'><strong>Note:</strong> Typically, implementations";
    ret += "          choose to ignore external subsets.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>Consider an element EXAMPLE with an attribute 'notation' that has a";
    ret += "              default value of 'decimal'. The DTD fragment might be</p>";
    ret += "          <pre class='dtd-example'>&lt;!ATTLIST EXAMPLE notation (decimal,octal) 'decimal'&gt;</pre>";
    ret += "          <p>If the style sheet contains the rules</p>";
    ret += "      <pre>EXAMPLE[notation=decimal] { /*... default property settings ...*/ }";
    ret += "      EXAMPLE[notation=octal]   { /*... other settings...*/ }</pre>";
    ret += "          <p>the first rule will not match elements whose 'notation' attribute";
    ret += "              attribute selector for the default value must be dropped:</p>";
    ret += "      <pre>EXAMPLE                   { /*... default property settings ...*/ }";
    ret += "      EXAMPLE[notation=octal]   { /*... other settings...*/ }</pre>";
    ret += "          <p>Here, because the selector <code>EXAMPLE[notation=octal]</code> is";
    ret += "              cases' style rules.</p>";
    ret += "      </div>";
    ret += "      <h3><a name='class-html'>6.4. Class selectors</a></h3>";
    ret += "      <p>Working with HTML, authors may use the period (U+002E,";
    ret += "          <code>.</code>) notation as an alternative to the <code>~=</code>";
    ret += "          notation when representing the <code>class</code> attribute. Thus, for";
    ret += "          HTML, <code>div.value</code> and <code>div[class~=value]</code> have";
    ret += "          'period' (<code>.</code>).</p>";
    ret += "      <p>UAs may apply selectors using the period (.) notation in XML";
    ret += "          1.0 <a href='#refsSVG'>[SVG]</a> describes the <a href='http://www.w3.org/TR/2001/PR-SVG-20010719/styling.html#ClassAttribute'>SVG";
    ret += "              'class' attribute</a> and how a UA should interpret it, and";
    ret += "          similarly MathML 1.01 <a href='#refsMATH'>[MATH]</a> describes the <a href='http://www.w3.org/1999/07/REC-MathML-19990707/chapter2.html#sec2.3.4'>MathML";
    ret += "              'class' attribute</a>.)</p>";
    ret += "      <div class='example'>";
    ret += "          <p>CSS examples:</p>";
    ret += "          <p>We can assign style information to all elements with";
    ret += "              <code>class~='pastoral'</code> as follows:</p>";
    ret += "          <pre>*.pastoral { color: green }  /* all elements with class~=pastoral */</pre>";
    ret += "          <p>or just</p>";
    ret += "          <pre>.pastoral { color: green }  /* all elements with class~=pastoral */</pre>";
    ret += "          <p>The following assigns style only to H1 elements with";
    ret += "              <code>class~='pastoral'</code>:</p>";
    ret += "          <pre>H1.pastoral { color: green }  /* H1 elements with class~=pastoral */</pre>";
    ret += "          <p>Given these rules, the first H1 instance below would not have";
    ret += "              green text, while the second would:</p>";
    ret += "        <pre>&lt;H1&gt;Not green&lt;/H1&gt;";
    ret += "      &lt;H1 class='pastoral'&gt;Very green&lt;/H1&gt;</pre>";
    ret += "      </div>";
    ret += "      <p>To represent a subset of 'class' values, each value must be preceded";
    ret += "          by a '.', in any order.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>CSS example:</p>";
    ret += "          <p>The following rule matches any P element whose 'class' attribute";
    ret += "              has been assigned a list of <a href='#whitespace'>whitespace</a>-separated values that includes";
    ret += "              'pastoral' and 'marine':</p>";
    ret += "          <pre>p.pastoral.marine { color: green }</pre>";
    ret += "          <p>This rule matches when <code>class='pastoral blue aqua";
    ret += "              marine'</code> but does not match for <code>class='pastoral";
    ret += "              blue'</code>.</p>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note:</strong> Because CSS gives considerable";
    ret += "          not.</p>";
    ret += "      <p class='note'><strong>Note:</strong> If an element has multiple";
    ret += "          this specification.</p>";
    ret += "      <h3><a name='id-selectors'>6.5. ID selectors</a></h3>";
    ret += "      <p>Document languages may contain attributes that are declared to be";
    ret += "          applies.</p>";
    ret += "      <p>An ID-typed attribute of a document language allows authors to";
    ret += "          ID selectors represent an element instance based on its identifier. An";
    ret += "          <code>#</code>) immediately followed by the ID value, which must be an";
    ret += "          identifier.</p>";
    ret += "      <p>Selectors does not specify how a UA knows the ID-typed attribute of";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following ID selector represents an <code>h1</code> element";
    ret += "              whose ID-typed attribute has the value 'chapter1':</p>";
    ret += "          <pre>h1#chapter1</pre>";
    ret += "          <p>The following ID selector represents any element whose ID-typed";
    ret += "              attribute has the value 'chapter1':</p>";
    ret += "          <pre>#chapter1</pre>";
    ret += "          <p>The following selector represents any element whose ID-typed";
    ret += "              attribute has the value 'z98y'.</p>";
    ret += "          <pre>*#z98y</pre>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note.</strong> In XML 1.0 <a href='#refsXML10'>[XML10]</a>, the information about which attribute";
    ret += "          should use normal attribute selectors instead:";
    ret += "          <code>[name=p371]</code> instead of <code>#p371</code>. Elements in";
    ret += "          XML 1.0 documents without a DTD do not have IDs at all.</p>";
    ret += "      <p>If an element has multiple ID attributes, all of them must be";
    ret += "          DOM3 Core, XML DTDs, and namespace-specific knowledge.</p>";
    ret += "      <h3><a name='pseudo-classes'>6.6. Pseudo-classes</a></h3>";
    ret += "      <p>The pseudo-class concept is introduced to permit selection based on";
    ret += "          expressed using the other simple selectors.</p>";
    ret += "      <p>A pseudo-class always consists of a 'colon'";
    ret += "          (<code>:</code>) followed by the name of the pseudo-class and";
    ret += "          optionally by a value between parentheses.</p>";
    ret += "      <p>Pseudo-classes are allowed in all sequences of simple selectors";
    ret += "          sequences of simple selectors, after the leading type selector or";
    ret += "          document.</p>";
    ret += "      <h4><a name='dynamic-pseudos'>6.6.1. Dynamic pseudo-classes</a></h4>";
    ret += "      <p>Dynamic pseudo-classes classify elements on characteristics other";
    ret += "          that cannot be deduced from the document tree.</p>";
    ret += "      <p>Dynamic pseudo-classes do not appear in the document source or";
    ret += "          document tree.</p>";
    ret += "      <h5>The <a name='link'>link pseudo-classes: :link and :visited</a></h5>";
    ret += "      <p>User agents commonly display unvisited links differently from";
    ret += "          previously visited ones. Selectors";
    ret += "          provides the pseudo-classes <code>:link</code> and";
    ret += "          <code>:visited</code> to distinguish them:</p>";
    ret += "      <ul>";
    ret += "          <li>The <code>:link</code> pseudo-class applies to links that have";
    ret += "          </li>";
    ret += "          <li>The <code>:visited</code> pseudo-class applies once the link has";
    ret += "          </li>";
    ret += "      </ul>";
    ret += "      <p>After some amount of time, user agents may choose to return a";
    ret += "          visited link to the (unvisited) ':link' state.</p>";
    ret += "      <p>The two states are mutually exclusive.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The following selector represents links carrying class";
    ret += "              <code>external</code> and already visited:</p>";
    ret += "          <pre>a.external:visited</pre>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note:</strong> It is possible for style sheet";
    ret += "      </p><p>UAs may therefore treat all links as unvisited links, or implement";
    ret += "          and unvisited links differently.</p>";
    ret += "      <h5>The <a name='useraction-pseudos'>user action pseudo-classes";
    ret += "          :hover, :active, and :focus</a></h5>";
    ret += "      <p>Interactive user agents sometimes change the rendering in response";
    ret += "          to user actions. Selectors provides";
    ret += "          acting on.</p>";
    ret += "      <ul>";
    ret += "          <li>The <code>:hover</code> pseudo-class applies while the user";
    ret += "              element. User agents not that do not support <a href='http://www.w3.org/TR/REC-CSS2/media.html#interactive-media-group'>interactive";
    ret += "                  media</a> do not have to support this pseudo-class. Some conforming";
    ret += "              user agents that support <a href='http://www.w3.org/TR/REC-CSS2/media.html#interactive-media-group'>interactive";
    ret += "                  media</a> may not be able to support this pseudo-class (e.g., a pen";
    ret += "          </li>";
    ret += "          <li>The <code>:active</code> pseudo-class applies while an element";
    ret += "          </li>";
    ret += "          <li>The <code>:focus</code> pseudo-class applies while an element";
    ret += "          </li>";
    ret += "      </ul>";
    ret += "      <p>There may be document language or implementation specific limits on";
    ret += "          which elements can become <code>:active</code> or acquire";
    ret += "          <code>:focus</code>.</p>";
    ret += "      <p>These pseudo-classes are not mutually exclusive. An element may";
    ret += "          match several pseudo-classes at the same time.</p>";
    ret += "      <p>Selectors doesn't define if the parent of an element that is";
    ret += "          ':active' or ':hover' is also in that state.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "        <pre>a:link    /* unvisited links */";
    ret += "      a:active  /* active links */</pre>";
    ret += "          <p>An example of combining dynamic pseudo-classes:</p>";
    ret += "        <pre>a:focus";
    ret += "      a:focus:hover</pre>";
    ret += "          <p>The last selector matches <code>a</code> elements that are in";
    ret += "              the pseudo-class :focus and in the pseudo-class :hover.</p>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note:</strong> An element can be both ':visited'";
    ret += "          and ':active' (or ':link' and ':active').</p>";
    ret += "      <h4><a name='target-pseudo'>6.6.2. The target pseudo-class :target</a></h4>";
    ret += "      <p>Some URIs refer to a location within a resource. This kind of URI";
    ret += "          identifier (called the fragment identifier).</p>";
    ret += "      <p>URIs with fragment identifiers link to a certain element within the";
    ret += "          pointing to an anchor named <code>section_2</code> in an HTML";
    ret += "          document:</p>";
    ret += "      <pre>http://example.com/html/top.html#section_2</pre>";
    ret += "      <p>A target element can be represented by the <code>:target</code>";
    ret += "          the document has no target element.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <pre>p.note:target</pre>";
    ret += "          <p>This selector represents a <code>p</code> element of class";
    ret += "              <code>note</code> that is the target element of the referring";
    ret += "              URI.</p>";
    ret += "      </div>";
    ret += "      <div class='example'>";
    ret += "          <p>CSS example:</p>";
    ret += "          <p>Here, the <code>:target</code> pseudo-class is used to make the";
    ret += "              target element red and place an image before it, if there is one:</p>";
    ret += "       <pre>*:target { color : red }";
    ret += "      *:target::before { content : url(target.png) }</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='lang-pseudo'>6.6.3. The language pseudo-class :lang</a></h4>";
    ret += "      <p>If the document language specifies how the human language of an";
    ret += "          element is determined, it is possible to write selectors that";
    ret += "          represent an element based on its language. For example, in HTML <a href='#refsHTML4'>[HTML4]</a>, the language is determined by a";
    ret += "          combination of the <code>lang</code> attribute, the <code>meta</code>";
    ret += "          headers). XML uses an attribute called <code>xml:lang</code>, and";
    ret += "          the language.</p>";
    ret += "      <p>The pseudo-class <code>:lang(C)</code> represents an element that";
    ret += "          <code>:lang()</code> selector is based solely on the identifier C";
    ret += "          element's language value, in the same way as if performed by the <a href='#attribute-representation'>'|='</a> operator in attribute";
    ret += "          selectors. The identifier C does not have to be a valid language";
    ret += "          name.</p>";
    ret += "      <p>C must not be empty. (If it is, the selector is invalid.)</p>";
    ret += "      <p class='note'><strong>Note:</strong> It is recommended that";
    ret += "          documents and protocols indicate language using codes from RFC 3066 <a href='#refsRFC3066'>[RFC3066]</a> or its successor, and by means of";
    ret += "          'xml:lang' attributes in the case of XML-based documents <a href='#refsXML10'>[XML10]</a>. See <a href='http://www.w3.org/International/questions/qa-lang-2or3.html'>";
    ret += "              'FAQ: Two-letter or three-letter language codes.'</a></p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The two following selectors represent an HTML document that is in";
    ret += "              Belgian, French, or German. The two next selectors represent";
    ret += "              <code>q</code> quotations in an arbitrary element in Belgian, French,";
    ret += "              or German.</p>";
    ret += "        <pre>html:lang(fr-be)";
    ret += "      :lang(de) &gt; q</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='UIstates'>6.6.4. The UI element states pseudo-classes</a></h4>";
    ret += "      <h5><a name='enableddisabled'>The :enabled and :disabled pseudo-classes</a></h5>";
    ret += "      <p>The <code>:enabled</code> pseudo-class allows authors to customize";
    ret += "          an enabled <code>input</code> element without also specifying what it";
    ret += "          would look like when it was disabled.</p>";
    ret += "      <p>Similar to <code>:enabled</code>, <code>:disabled</code> allows the";
    ret += "          element should look.</p>";
    ret += "      <p>Most elements will be neither enabled nor disabled. An element is";
    ret += "          presently activate it or transfer focus to it.</p>";
    ret += "      <h5><a name='checked'>The :checked pseudo-class</a></h5>";
    ret += "      <p>Radio and checkbox elements can be toggled by the user. Some menu";
    ret += "          toggled 'on' the <code>:checked</code> pseudo-class applies. The";
    ret += "          <code>:checked</code> pseudo-class initially applies to such elements";
    ret += "          that have the HTML4 <code>selected</code> and <code>checked</code>";
    ret += "          attributes as described in <a href='http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.2.1'>Section";
    ret += "              17.2.1 of HTML4</a>, but of course the user can toggle 'off' such";
    ret += "          elements in which case the <code>:checked</code> pseudo-class would no";
    ret += "          longer apply. While the <code>:checked</code> pseudo-class is dynamic";
    ret += "          on the presence of the semantic HTML4 <code>selected</code> and";
    ret += "          <code>checked</code> attributes, it applies to all media.";
    ret += "      </p><h5><a name='indeterminate'>The :indeterminate pseudo-class</a></h5>";
    ret += "      <div class='note'>";
    ret += "          <p>Radio and checkbox elements can be toggled by the user, but are";
    ret += "              This can be due to an element attribute, or DOM manipulation.</p>";
    ret += "          <p>A future version of this specification may introduce an";
    ret += "              <code>:indeterminate</code> pseudo-class that applies to such elements.";
    ret += "              <!--While the <code>:indeterminate</code> pseudo-class is dynamic in";
    ret += "         the presence of an element attribute, it applies to all media.</p>";
    ret += "         <p>Components of a radio-group initialized with no pre-selected choice";
    ret += "         are an example of :indeterminate state.--></p>";
    ret += "      </div>";
    ret += "      <h4><a name='structural-pseudos'>6.6.5. Structural pseudo-classes</a></h4>";
    ret += "      <p>Selectors introduces the concept of <dfn>structural";
    ret += "          pseudo-classes</dfn> to permit selection based on extra information that";
    ret += "          the document tree but cannot be represented by other simple selectors or";
    ret += "      </p><p>Note that standalone pieces of PCDATA (text nodes in the DOM) are";
    ret += "      </p><h5><a name='root-pseudo'>:root pseudo-class</a></h5>";
    ret += "      <p>The <code>:root</code> pseudo-class represents an element that is";
    ret += "          <code>HTML</code> element.";
    ret += "      </p><h5><a name='nth-child-pseudo'>:nth-child() pseudo-class</a></h5>";
    ret += "      <p>The";
    ret += "          <code>:nth-child(<var>a</var><code>n</code>+<var>b</var>)</code>";
    ret += "          <var>a</var><code>n</code>+<var>b</var>-1 siblings";
    ret += "          <strong>before</strong> it in the document tree, for a given positive";
    ret += "          integer or zero value of <code>n</code>, and has a parent element. In";
    ret += "          other words, this matches the <var>b</var>th child of an element after";
    ret += "          all the children have been split into groups of <var>a</var> elements";
    ret += "          each. For example, this allows the selectors to address every other";
    ret += "          of paragraph text in a cycle of four. The <var>a</var> and";
    ret += "          <var>b</var> values must be zero, negative integers or positive";
    ret += "      </p><p>In addition to this, <code>:nth-child()</code> can take";
    ret += "          '<code>odd</code>' and '<code>even</code>' as arguments instead.";
    ret += "          '<code>odd</code>' has the same signification as <code>2n+1</code>,";
    ret += "          and '<code>even</code>' has the same signification as <code>2n</code>.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>tr:nth-child(2n+1) /* represents every odd row of an HTML table */";
    ret += "      p:nth-child(4n+4) { color: purple; }</pre>";
    ret += "      </div>";
    ret += "      <p>When <var>a</var>=0, no repeating is used, so for example";
    ret += "          <code>:nth-child(0n+5)</code> matches only the fifth child. When";
    ret += "          <var>a</var>=0, the <var>a</var><code>n</code> part need not be";
    ret += "          <code>:nth-child(<var>b</var>)</code> and the last example simplifies";
    ret += "          to <code>:nth-child(5)</code>.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>foo:nth-child(0n+1)   /* represents an element foo, first child of its parent element */";
    ret += "      foo:nth-child(1)      /* same */</pre>";
    ret += "      </div>";
    ret += "      <p>When <var>a</var>=1, the number may be omitted from the rule.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following selectors are therefore equivalent:</p>";
    ret += "      <pre>bar:nth-child(1n+0)   /* represents all bar elements, specificity (0,1,1) */";
    ret += "      bar                   /* same but lower specificity (0,0,1) */</pre>";
    ret += "      </div>";
    ret += "      <p>If <var>b</var>=0, then every <var>a</var>th element is picked. In";
    ret += "          such a case, the <var>b</var> part may be omitted.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>tr:nth-child(2n+0) /* represents every even row of an HTML table */";
    ret += "      tr:nth-child(2n) /* same */</pre>";
    ret += "      </div>";
    ret += "      <p>If both <var>a</var> and <var>b</var> are equal to zero, the";
    ret += "          pseudo-class represents no element in the document tree.</p>";
    ret += "      <p>The value <var>a</var> can be negative, but only the positive";
    ret += "          values of <var>a</var><code>n</code>+<var>b</var>, for";
    ret += "          <code>n</code>≥0, may represent an element in the document";
    ret += "          tree.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <pre>html|tr:nth-child(-n+6)  /* represents the 6 first rows of XHTML tables */</pre>";
    ret += "      </div>";
    ret += "      <p>When the value <var>b</var> is negative, the '+' character in the";
    ret += "          character indicating the negative value of <var>b</var>).</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>:nth-child(10n-1)  /* represents the 9th, 19th, 29th, etc, element */";
    ret += "      :nth-child(10n+-1) /* Syntactically invalid, and would be ignored */</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='nth-last-child-pseudo'>:nth-last-child() pseudo-class</a></h5>";
    ret += "      <p>The <code>:nth-last-child(<var>a</var>n+<var>b</var>)</code>";
    ret += "          <var>a</var><code>n</code>+<var>b</var>-1 siblings";
    ret += "          <strong>after</strong> it in the document tree, for a given positive";
    ret += "          integer or zero value of <code>n</code>, and has a parent element. See";
    ret += "          <code>:nth-child()</code> pseudo-class for the syntax of its argument.";
    ret += "          It also accepts the '<code>even</code>' and '<code>odd</code>' values";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>tr:nth-last-child(-n+2)    /* represents the two last rows of an HTML table */";
    ret += "                                    counting from the last one */</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='nth-of-type-pseudo'>:nth-of-type() pseudo-class</a></h5>";
    ret += "      <p>The <code>:nth-of-type(<var>a</var>n+<var>b</var>)</code>";
    ret += "          <var>a</var><code>n</code>+<var>b</var>-1 siblings with the same";
    ret += "          element name <strong>before</strong> it in the document tree, for a";
    ret += "          given zero or positive integer value of <code>n</code>, and has a";
    ret += "          parent element. In other words, this matches the <var>b</var>th child";
    ret += "          groups of a elements each. See <code>:nth-child()</code> pseudo-class";
    ret += "          '<code>even</code>' and '<code>odd</code>' values.";
    ret += "      </p><div class='example'>";
    ret += "          <p>CSS example:</p>";
    ret += "          <p>This allows an author to alternate the position of floated images:</p>";
    ret += "      <pre>img:nth-of-type(2n+1) { float: right; }";
    ret += "      img:nth-of-type(2n) { float: left; }</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='nth-last-of-type-pseudo'>:nth-last-of-type() pseudo-class</a></h5>";
    ret += "      <p>The <code>:nth-last-of-type(<var>a</var>n+<var>b</var>)</code>";
    ret += "          <var>a</var><code>n</code>+<var>b</var>-1 siblings with the same";
    ret += "          element name <strong>after</strong> it in the document tree, for a";
    ret += "          given zero or positive integer value of <code>n</code>, and has a";
    ret += "          parent element. See <code>:nth-child()</code> pseudo-class for the";
    ret += "          syntax of its argument. It also accepts the '<code>even</code>' and '<code>odd</code>'";
    ret += "      </p><div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>To represent all <code>h2</code> children of an XHTML";
    ret += "              <code>body</code> except the first and last, one could use the";
    ret += "              following selector:</p>";
    ret += "          <pre>body &gt; h2:nth-of-type(n+2):nth-last-of-type(n+2)</pre>";
    ret += "          <p>In this case, one could also use <code>:not()</code>, although the";
    ret += "              selector ends up being just as long:</p>";
    ret += "          <pre>body &gt; h2:not(:first-of-type):not(:last-of-type)</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='first-child-pseudo'>:first-child pseudo-class</a></h5>";
    ret += "      <p>Same as <code>:nth-child(1)</code>. The <code>:first-child</code>";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following selector represents a <code>p</code> element that is";
    ret += "              the first child of a <code>div</code> element:</p>";
    ret += "          <pre>div &gt; p:first-child</pre>";
    ret += "          <p>This selector can represent the <code>p</code> inside the";
    ret += "              <code>div</code> of the following fragment:</p>";
    ret += "        <pre>&lt;p&gt; The last P before the note.&lt;/p&gt;";
    ret += "      &lt;/div&gt;</pre>";
    ret += "          but cannot represent the second <code>p</code> in the following";
    ret += "        <pre>&lt;p&gt; The last P before the note.&lt;/p&gt;";
    ret += "      &lt;/div&gt;</pre>";
    ret += "          <p>The following two selectors are usually equivalent:</p>";
    ret += "        <pre>* &gt; a:first-child /* a is first child of any element */";
    ret += "      a:first-child /* Same (assuming a is not the root element) */</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='last-child-pseudo'>:last-child pseudo-class</a></h5>";
    ret += "      <p>Same as <code>:nth-last-child(1)</code>. The <code>:last-child</code>";
    ret += "      </p><div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The following selector represents a list item <code>li</code> that";
    ret += "              is the last child of an ordered list <code>ol</code>.";
    ret += "          </p><pre>ol &gt; li:last-child</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='first-of-type-pseudo'>:first-of-type pseudo-class</a></h5>";
    ret += "      <p>Same as <code>:nth-of-type(1)</code>. The <code>:first-of-type</code>";
    ret += "      </p><div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The following selector represents a definition title";
    ret += "              <code>dt</code> inside a definition list <code>dl</code>, this";
    ret += "              <code>dt</code> being the first of its type in the list of children of";
    ret += "              its parent element.</p>";
    ret += "          <pre>dl dt:first-of-type</pre>";
    ret += "          <p>It is a valid description for the first two <code>dt</code>";
    ret += "              elements in the following example but not for the third one:</p>";
    ret += "      <pre>&lt;dl&gt;";
    ret += "      &lt;/dl&gt;</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='last-of-type-pseudo'>:last-of-type pseudo-class</a></h5>";
    ret += "      <p>Same as <code>:nth-last-of-type(1)</code>. The";
    ret += "          <code>:last-of-type</code> pseudo-class represents an element that is";
    ret += "          element.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The following selector represents the last data cell";
    ret += "              <code>td</code> of a table row.</p>";
    ret += "          <pre>tr &gt; td:last-of-type</pre>";
    ret += "      </div>";
    ret += "      <h5><a name='only-child-pseudo'>:only-child pseudo-class</a></h5>";
    ret += "      <p>Represents an element that has a parent element and whose parent";
    ret += "          <code>:first-child:last-child</code> or";
    ret += "          <code>:nth-child(1):nth-last-child(1)</code>, but with a lower";
    ret += "          specificity.</p>";
    ret += "      <h5><a name='only-of-type-pseudo'>:only-of-type pseudo-class</a></h5>";
    ret += "      <p>Represents an element that has a parent element and whose parent";
    ret += "          as <code>:first-of-type:last-of-type</code> or";
    ret += "          <code>:nth-of-type(1):nth-last-of-type(1)</code>, but with a lower";
    ret += "          specificity.</p>";
    ret += "      <h5><a name='empty-pseudo'></a>:empty pseudo-class</h5>";
    ret += "      <p>The <code>:empty</code> pseudo-class represents an element that has";
    ret += "          empty or not.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p><code>p:empty</code> is a valid representation of the following fragment:";
    ret += "          </p>";
    ret += "          <pre>&lt;p&gt;&lt;/p&gt;</pre>";
    ret += "          <p><code>foo:empty</code> is not a valid representation for the";
    ret += "              following fragments:</p>";
    ret += "          <pre>&lt;foo&gt;bar&lt;/foo&gt;</pre>";
    ret += "          <pre>&lt;foo&gt;&lt;bar&gt;bla&lt;/bar&gt;&lt;/foo&gt;</pre>";
    ret += "          <pre>&lt;foo&gt;this is not &lt;bar&gt;:empty&lt;/bar&gt;&lt;/foo&gt;</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='content-selectors'>6.6.6. Blank</a></h4>";
    ret += "      <!-- It's the Return of Appendix H!!! Run away! -->";
    ret += "      <p>This section intentionally left blank.</p>";
    ret += "      <!-- (used to be :contains()) -->";
    ret += "      <h4><a name='negation'></a>6.6.7. The negation pseudo-class</h4>";
    ret += "      <p>The negation pseudo-class, <code>:not(<var>X</var>)</code>, is a";
    ret += "          functional notation taking a <a href='#simple-selectors-dfn'>simple";
    ret += "              selector</a> (excluding the negation pseudo-class itself and";
    ret += "          <!-- pseudo-elements are not simple selectors, so the above paragraph";
    ret += "      may be a bit confusing -->";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following CSS selector matches all <code>button</code>";
    ret += "              elements in an HTML document that are not disabled.</p>";
    ret += "          <pre>button:not([DISABLED])</pre>";
    ret += "          <p>The following selector represents all but <code>FOO</code>";
    ret += "              elements.</p>";
    ret += "          <pre>*:not(FOO)</pre>";
    ret += "          <p>The following group of selectors represents all HTML elements";
    ret += "              except links.</p>";
    ret += "          <pre>html|*:not(:link):not(:visited)</pre>";
    ret += "      </div>";
    ret += "      <p>Default namespace declarations do not affect the argument of the";
    ret += "          type selector.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>Assuming that the default namespace is bound to";
    ret += "              elements that are not in that namespace:</p>";
    ret += "          <pre>*|*:not(*)</pre>";
    ret += "          <p>The following CSS selector matches any element being hovered,";
    ret += "              rule when they <em>are</em> being hovered.</p>";
    ret += "          <pre>*|*:not(:hover)</pre>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note</strong>: the :not() pseudo allows";
    ret += "          useless selectors to be written. For instance <code>:not(*|*)</code>,";
    ret += "          which represents no element at all, or <code>foo:not(bar)</code>,";
    ret += "          which is equivalent to <code>foo</code> but with a higher";
    ret += "          specificity.</p>";
    ret += "      <h3><a name='pseudo-elements'>7. Pseudo-elements</a></h3>";
    ret += "      <p>Pseudo-elements create abstractions about the document tree beyond";
    ret += "          source document (e.g., the <code>::before</code> and";
    ret += "          <code>::after</code> pseudo-elements give access to generated";
    ret += "          content).</p>";
    ret += "      <p>A pseudo-element is made of two colons (<code>::</code>) followed";
    ret += "          by the name of the pseudo-element.</p>";
    ret += "      <p>This <code>::</code> notation is introduced by the current document";
    ret += "          <code>:first-line</code>, <code>:first-letter</code>,";
    ret += "          <code>:before</code> and <code>:after</code>). This compatibility is";
    ret += "          not allowed for the new pseudo-elements introduced in CSS level 3.</p>";
    ret += "      <p>Only one pseudo-element may appear per selector, and if present it";
    ret += "          must appear after the sequence of simple selectors that represents the";
    ret += "          <a href='#subject'>subjects</a> of the selector. <span class='note'>A";
    ret += "      pesudo-elements per selector.</span></p>";
    ret += "      <h4><a name='first-line'>7.1. The ::first-line pseudo-element</a></h4>";
    ret += "      <p>The <code>::first-line</code> pseudo-element describes the contents";
    ret += "      </p><div class='example'>";
    ret += "          <p>CSS example:</p>";
    ret += "          <pre>p::first-line { text-transform: uppercase }</pre>";
    ret += "          <p>The above rule means 'change the letters of the first line of every";
    ret += "              paragraph to uppercase'.</p>";
    ret += "      </div>";
    ret += "      <p>The selector <code>p::first-line</code> does not match any real";
    ret += "          agents will insert at the beginning of every paragraph.</p>";
    ret += "      <p>Note that the length of the first line depends on a number of";
    ret += "          an ordinary HTML paragraph such as:</p>";
    ret += "      <pre>      &lt;P&gt;This is a somewhat long HTML ";
    ret += "      </pre>";
    ret += "      <p>the lines of which happen to be broken as follows:";
    ret += "      </p><pre>      THIS IS A SOMEWHAT LONG HTML PARAGRAPH THAT";
    ret += "      </pre>";
    ret += "      <p>This paragraph might be 'rewritten' by user agents to include the";
    ret += "          <em>fictional tag sequence</em> for <code>::first-line</code>. This";
    ret += "          fictional tag sequence helps to show how properties are inherited.</p>";
    ret += "      <pre>      &lt;P&gt;<b>&lt;P::first-line&gt;</b> This is a somewhat long HTML ";
    ret += "      paragraph that <b>&lt;/P::first-line&gt;</b> will be broken into several";
    ret += "      </pre>";
    ret += "      <p>If a pseudo-element breaks up a real element, the desired effect";
    ret += "          with a <code>span</code> element:</p>";
    ret += "      <pre>      &lt;P&gt;<b>&lt;SPAN class='test'&gt;</b> This is a somewhat long HTML";
    ret += "      lines.<b>&lt;/SPAN&gt;</b> The first line will be identified";
    ret += "      </pre>";
    ret += "      <p>the user agent could simulate start and end tags for";
    ret += "          <code>span</code> when inserting the fictional tag sequence for";
    ret += "          <code>::first-line</code>.";
    ret += "      </p><pre>      &lt;P&gt;&lt;P::first-line&gt;<b>&lt;SPAN class='test'&gt;</b> This is a";
    ret += "      paragraph that will <b>&lt;/SPAN&gt;</b>&lt;/P::first-line&gt;<b>&lt;SPAN";
    ret += "          class='test'&gt;</b> be";
    ret += "      lines.<b>&lt;/SPAN&gt;</b> The first line will be identified";
    ret += "      </pre>";
    ret += "      <p>In CSS, the <code>::first-line</code> pseudo-element can only be";
    ret += "          or a table-cell.</p>";
    ret += "      <p><a name='first-formatted-line'></a>The 'first formatted line' of an";
    ret += "          line of the <code>div</code> in <code>&lt;DIV&gt;&lt;P&gt;This";
    ret += "              line...&lt;/P&gt;&lt;/DIV&gt;</code> is the first line of the <code>p</code>";
    ret += "          that both <code>p</code> and <code>div</code> are block-level).";
    ret += "      </p><p>The first line of a table-cell or inline-block cannot be the first";
    ret += "          formatted line of an ancestor element. Thus, in <code>&lt;DIV&gt;&lt;P";
    ret += "              etcetera&lt;/DIV&gt;</code> the first formatted line of the";
    ret += "          <code>div</code> is not the line 'Hello'.";
    ret += "      </p><p class='note'>Note that the first line of the <code>p</code> in this";
    ret += "          fragment: <code>&lt;p&gt;&lt;br&gt;First...</code> doesn't contain any";
    ret += "          letters (assuming the default style for <code>br</code> in HTML";
    ret += "      </p><p>A UA should act as if the fictional start tags of the";
    ret += "          <code>::first-line</code> pseudo-elements were nested just inside the";
    ret += "          is an example. The fictional tag sequence for</p>";
    ret += "      <pre>      &lt;DIV&gt;";
    ret += "      </pre>";
    ret += "      <p>is</p>";
    ret += "      <pre>      &lt;DIV&gt;";
    ret += "      </pre>";
    ret += "      <p>The <code>::first-line</code> pseudo-element is similar to an";
    ret += "          following properties apply to a <code>::first-line</code>";
    ret += "          properties as well.</p>";
    ret += "      <h4><a name='first-letter'>7.2. The ::first-letter pseudo-element</a></h4>";
    ret += "      <p>The <code>::first-letter</code> pseudo-element represents the first";
    ret += "          is 'none'; otherwise, it is similar to a floated element.</p>";
    ret += "      <p>In CSS, these are the properties that apply to <code>::first-letter</code>";
    ret += "          of the letter, unlike for normal elements.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>This example shows a possible rendering of an initial cap. Note";
    ret += "              <code>::first-letter</code>";
    ret += "              fictional start tag of the first letter is inside the <span>span</span>,";
    ret += "              the font weight of the first letter is normal, not bold as the <span>span</span>:";
    ret += "      </p><pre>      p { line-height: 1.1 }";
    ret += "      </pre>";
    ret += "          <div class='figure'>";
    ret += "              <p><img src='' alt='Image illustrating the ::first-letter pseudo-element'>";
    ret += "          </p></div>";
    ret += "      </div>";
    ret += "      <div class='example'>";
    ret += "          <p>The following CSS will make a drop cap initial letter span about two";
    ret += "              lines:</p>";
    ret += "      <pre>      &lt;!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN'&gt;";
    ret += "      </pre>";
    ret += "          <p>This example might be formatted as follows:</p>";
    ret += "          <div class='figure'>";
    ret += "              <p><img src='' alt='Image illustrating the combined effect of the ::first-letter and ::first-line pseudo-elements'>";
    ret += "              </p>";
    ret += "          </div>";
    ret += "          <p>The <span class='index-inst' title='fictional tag";
    ret += "      sequence'>fictional tag sequence</span> is:</p>";
    ret += "      <pre>      &lt;P&gt;";
    ret += "      </pre>";
    ret += "          <p>Note that the <code>::first-letter</code> pseudo-element tags abut";
    ret += "              block element.</p></div>";
    ret += "      <p>In order to achieve traditional drop caps formatting, user agents";
    ret += "          glyph outline may be taken into account when formatting.</p>";
    ret += "      <p>Punctuation (i.e, characters defined in Unicode in the 'open' (Ps),";
    ret += "          be included. <a href='#refsUNICODE'>[UNICODE]</a></p>";
    ret += "      <div class='figure'>";
    ret += "          <p><img src='' alt='Quotes that precede the";
    ret += "      first letter should be included.'></p>";
    ret += "      </div>";
    ret += "      <p>The <code>::first-letter</code> also applies if the first letter is";
    ret += "          money.'</p>";
    ret += "      <p>In CSS, the <code>::first-letter</code> pseudo-element applies to";
    ret += "          elements. <span class='note'>A future version of this specification";
    ret += "      types.</span></p>";
    ret += "      <p>The <code>::first-letter</code> pseudo-element can be used with all";
    ret += "          the element, even if that first text is in a descendant.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>The fictional tag sequence for this HTMLfragment:";
    ret += "      </p><pre>&lt;div&gt;";
    ret += "      &lt;p&gt;The first text.</pre>";
    ret += "          <p>is:";
    ret += "      </p><pre>&lt;div&gt;";
    ret += "      &lt;p&gt;&lt;div::first-letter&gt;&lt;p::first-letter&gt;T&lt;/...&gt;&lt;/...&gt;he first text.</pre>";
    ret += "      </div>";
    ret += "      <p>The first letter of a table-cell or inline-block cannot be the";
    ret += "          first letter of an ancestor element. Thus, in <code>&lt;DIV&gt;&lt;P";
    ret += "              etcetera&lt;/DIV&gt;</code> the first letter of the <code>div</code> is";
    ret += "          letter 'H'. In fact, the <code>div</code> doesn't have a first letter.";
    ret += "      </p><p>The first letter must occur on the <a href='#first-formatted-line'>first formatted line.</a> For example, in";
    ret += "          this fragment: <code>&lt;p&gt;&lt;br&gt;First...</code> the first line";
    ret += "          doesn't contain any letters and <code>::first-letter</code> doesn't";
    ret += "          match anything (assuming the default style for <code>br</code> in HTML";
    ret += "      </p><p>In CSS, if an element is a list item ('display: list-item'), the";
    ret += "          <code>::first-letter</code> applies to the first letter in the";
    ret += "          <code>::first-letter</code> on list items with 'list-style-position:";
    ret += "          inside'. If an element has <code>::before</code> or";
    ret += "          <code>::after</code> content, the <code>::first-letter</code> applies";
    ret += "          to the first letter of the element <em>including</em> that content.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p>After the rule 'p::before {content: 'Note: '}', the selector";
    ret += "              'p::first-letter' matches the 'N' of 'Note'.</p>";
    ret += "      </div>";
    ret += "      <p>Some languages may have specific rules about how to treat certain";
    ret += "          considered within the <code>::first-letter</code> pseudo-element.";
    ret += "      </p><p>If the letters that would form the ::first-letter are not in the";
    ret += "          same element, such as ''T' in <code>&lt;p&gt;'&lt;em&gt;T...</code>, the UA";
    ret += "          both elements, or simply not create a pseudo-element.</p>";
    ret += "      <p>Similarly, if the first letter(s) of the block are not at the start";
    ret += "      </p><div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <p><a name='overlapping-example'>The following example</a> illustrates";
    ret += "              paragraph will be 'red'.</p>";
    ret += "      <pre>p { color: red; font-size: 12pt }";
    ret += "      &lt;P&gt;Some text that ends up on two lines&lt;/P&gt;</pre>";
    ret += "          <p>Assuming that a line break will occur before the word 'ends', the";
    ret += "      <span class='index-inst' title='fictional tag sequence'>fictional tag";
    ret += "      sequence</span> for this fragment might be:</p>";
    ret += "      <pre>&lt;P&gt;";
    ret += "      &lt;/P&gt;</pre>";
    ret += "          <p>Note that the <code>::first-letter</code> element is inside the <code>::first-line</code>";
    ret += "              element. Properties set on <code>::first-line</code> are inherited by";
    ret += "              <code>::first-letter</code>, but are overridden if the same property is";
    ret += "              <code>::first-letter</code>.</p>";
    ret += "      </div>";
    ret += "      <h4><a name='UIfragments'>7.3.</a> <a name='selection'>The ::selection";
    ret += "          pseudo-element</a></h4>";
    ret += "      <p>The <code>::selection</code> pseudo-element applies to the portion";
    ret += "          field. This pseudo-element should not be confused with the <code><a href='#checked'>:checked</a></code> pseudo-class (which used to be";
    ret += "          named <code>:selected</code>)";
    ret += "      </p><p>Although the <code>::selection</code> pseudo-element is dynamic in";
    ret += "          <a href='#refsCSS21'>[CSS21]</a>) which was originally rendered to a";
    ret += "          <code>::selection</code> state to that other medium, and have all the";
    ret += "          required — UAs may omit the <code>::selection</code>";
    ret += "      </p><p>These are the CSS properties that apply to <code>::selection</code>";
    ret += "          <code>::selection</code> may be ignored.";
    ret += "      </p><h4><a name='gen-content'>7.4. The ::before and ::after pseudo-elements</a></h4>";
    ret += "      <p>The <code>::before</code> and <code>::after</code> pseudo-elements";
    ret += "          content. They are explained in CSS 2.1 <a href='#refsCSS21'>[CSS21]</a>.</p>";
    ret += "      <p>When the <code>::first-letter</code> and <code>::first-line</code>";
    ret += "          pseudo-elements are combined with <code>::before</code> and";
    ret += "          <code>::after</code>, they apply to the first letter or line of the";
    ret += "          element including the inserted text.</p>";
    ret += "      <h2><a name='combinators'>8. Combinators</a></h2>";
    ret += "      <h3><a name='descendant-combinators'>8.1. Descendant combinator</a></h3>";
    ret += "      <p>At times, authors may want selectors to describe an element that is";
    ret += "          <code>EM</code> element that is contained within an <code>H1</code>";
    ret += "          descendant combinator is <a href='#whitespace'>white space</a> that";
    ret += "          separates two sequences of simple selectors. A selector of the form";
    ret += "          '<code>A B</code>' represents an element <code>B</code> that is an";
    ret += "          arbitrary descendant of some ancestor element <code>A</code>.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>For example, consider the following selector:</p>";
    ret += "          <pre>h1 em</pre>";
    ret += "          <p>It represents an <code>em</code> element being the descendant of";
    ret += "              an <code>h1</code> element. It is a correct and valid, but partial,";
    ret += "              description of the following fragment:</p>";
    ret += "       <pre>&lt;h1&gt;This &lt;span class='myclass'&gt;headline";
    ret += "      is &lt;em&gt;very&lt;/em&gt; important&lt;/span&gt;&lt;/h1&gt;</pre>";
    ret += "          <p>The following selector:</p>";
    ret += "          <pre>div * p</pre>";
    ret += "          <p>represents a <code>p</code> element that is a grandchild or later";
    ret += "              descendant of a <code>div</code> element. Note the whitespace on";
    ret += "              of the P.</p>";
    ret += "          <p>The following selector, which combines descendant combinators and";
    ret += "              <a href='#attribute-selectors'>attribute selectors</a>, represents an";
    ret += "              element that (1) has the <code>href</code> attribute set and (2) is";
    ret += "              inside a <code>p</code> that is itself inside a <code>div</code>:</p>";
    ret += "          <pre>div p *[href]</pre>";
    ret += "      </div>";
    ret += "      <h3><a name='child-combinators'>8.2. Child combinators</a></h3>";
    ret += "      <p>A <dfn>child combinator</dfn> describes a childhood relationship";
    ret += "          'greater-than sign' (<code>&gt;</code>) character and";
    ret += "          separates two sequences of simple selectors.";
    ret += "      </p><div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following selector represents a <code>p</code> element that is";
    ret += "              child of <code>body</code>:</p>";
    ret += "          <pre>body &gt; p</pre>";
    ret += "          <p>The following example combines descendant combinators and child";
    ret += "              combinators.</p>";
    ret += "          <pre>div ol&gt;li p</pre>";
    ret += "          <!-- LEAVE THOSE SPACES OUT! see below -->";
    ret += "          <p>It represents a <code>p</code> element that is a descendant of an";
    ret += "              <code>li</code> element; the <code>li</code> element must be the";
    ret += "              child of an <code>ol</code> element; the <code>ol</code> element must";
    ret += "              be a descendant of a <code>div</code>. Notice that the optional white";
    ret += "              space around the '&gt;' combinator has been left out.</p>";
    ret += "      </div>";
    ret += "      <p>For information on selecting the first child of an element, please";
    ret += "          see the section on the <code><a href='#structural-pseudos'>:first-child</a></code> pseudo-class";
    ret += "          above.</p>";
    ret += "      <h3><a name='sibling-combinators'>8.3. Sibling combinators</a></h3>";
    ret += "      <p>There are two different sibling combinators: the adjacent sibling";
    ret += "          considering adjacency of elements.</p>";
    ret += "      <h4><a name='adjacent-sibling-combinators'>8.3.1. Adjacent sibling combinator</a>";
    ret += "      </h4>";
    ret += "      <p>The adjacent sibling combinator is made of the 'plus";
    ret += "          sign' (U+002B, <code>+</code>) character that separates two";
    ret += "          sequences of simple selectors. The elements represented by the two";
    ret += "          represented by the second one.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "          <p>The following selector represents a <code>p</code> element";
    ret += "              immediately following a <code>math</code> element:</p>";
    ret += "          <pre>math + p</pre>";
    ret += "          <p>The following selector is conceptually similar to the one in the";
    ret += "              adds a constraint to the <code>h1</code> element, that it must have";
    ret += "              <code>class='opener'</code>:</p>";
    ret += "          <pre>h1.opener + h2</pre>";
    ret += "      </div>";
    ret += "      <h4><a name='general-sibling-combinators'>8.3.2. General sibling combinator</a>";
    ret += "      </h4>";
    ret += "      <p>The general sibling combinator is made of the 'tilde'";
    ret += "          (U+007E, <code>~</code>) character that separates two sequences of";
    ret += "          simple selectors. The elements represented by the two sequences share";
    ret += "          represented by the second one.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Example:</p>";
    ret += "          <pre>h1 ~ pre</pre>";
    ret += "          <p>represents a <code>pre</code> element following an <code>h1</code>. It";
    ret += "              is a correct and valid, but partial, description of:</p>";
    ret += "       <pre>&lt;h1&gt;Definition of the function a&lt;/h1&gt;";
    ret += "      &lt;pre&gt;function a(x) = 12x/13.5&lt;/pre&gt;</pre>";
    ret += "      </div>";
    ret += "      <h2><a name='specificity'>9. Calculating a selector's specificity</a></h2>";
    ret += "      <p>A selector's specificity is calculated as follows:</p>";
    ret += "      <ul>";
    ret += "          <li>count the number of ID selectors in the selector (= a)</li>";
    ret += "          <li>count the number of class selectors, attributes selectors, and";
    ret += "          </li>";
    ret += "          <li>count the number of element names in the selector (= c)</li>";
    ret += "          <li>ignore pseudo-elements</li>";
    ret += "      </ul>";
    ret += "      <p>Selectors inside <a href='#negation'>the negation pseudo-class</a>";
    ret += "          a pseudo-class.</p>";
    ret += "      <p>Concatenating the three numbers a-b-c (in a number system with a";
    ret += "          large base) gives the specificity.</p>";
    ret += "      <div class='example'>";
    ret += "          <p>Examples:</p>";
    ret += "      <pre>*               /* a=0 b=0 c=0 -&gt; specificity =   0 */";
    ret += "      </pre>";
    ret += "      </div>";
    ret += "      <p class='note'><strong>Note:</strong> the specificity of the styles";
    ret += "          specified in an HTML <code>style</code> attribute is described in CSS";
    ret += "          2.1. <a href='#refsCSS21'>[CSS21]</a>.</p>";
    ret += "      <h2><a name='w3cselgrammar'>10. The grammar of Selectors</a></h2>";
    ret += "      <h3><a name='grammar'>10.1. Grammar</a></h3>";
    ret += "      <p>The grammar below defines the syntax of Selectors. It is globally";
    ret += "          shorthand notations beyond Yacc (see <a href='#refsYACC'>[YACC]</a>)";
    ret += "          are used:</p>";
    ret += "      <ul>";
    ret += "          <li><b>*</b>: 0 or more";
    ret += "          </li><li><b>+</b>: 1 or more";
    ret += "          </li><li><b>?</b>: 0 or 1";
    ret += "          </li><li><b>|</b>: separates alternatives";
    ret += "          </li><li><b>[ ]</b>: grouping</li>";
    ret += "      </ul>";
    ret += "      <p>The productions are:</p>";
    ret += "      <pre>selectors_group";
    ret += "        ;</pre>";
    ret += "      <h3><a name='lex'>10.2. Lexical scanner</a></h3>";
    ret += "      <p>The following is the <a name='x3'>tokenizer</a>, written in Flex (see";
    ret += "          <a href='#refsFLEX'>[FLEX]</a>) notation. The tokenizer is";
    ret += "          case-insensitive.</p>";
    ret += "      <p>The two occurrences of '\377' represent the highest character";
    ret += "          possible code point in Unicode/ISO-10646. <a href='#refsUNICODE'>[UNICODE]</a></p>";
    ret += "      <pre>%option case-insensitive";
    ret += "      .                return *yytext;</pre>";
    ret += "      <h2><a name='downlevel'>11. Namespaces and down-level clients</a></h2>";
    ret += "      <p>An important issue is the interaction of CSS selectors with XML";
    ret += "          to construct a CSS style sheet which will properly match selectors in";
    ret += "          is possible to construct a style sheet in which selectors would match";
    ret += "          elements and attributes correctly.</p>";
    ret += "      <p>It should be noted that a down-level CSS client will (if it";
    ret += "          <code>@namespace</code> at-rules, as well as all style rules that make";
    ret += "          use of namespace qualified element type or attribute selectors. The";
    ret += "          than possibly match them incorrectly.</p>";
    ret += "      <p>The use of default namespaces in CSS makes it possible to write";
    ret += "          element type selectors that will function in both namespace aware CSS";
    ret += "          down-level clients may incorrectly match selectors against XML";
    ret += "          elements in other namespaces.</p>";
    ret += "      <p>The following are scenarios and examples in which it is possible to";
    ret += "          that do not implement this proposal.</p>";
    ret += "      <ol>";
    ret += "          <li>";
    ret += "              <p>The XML document does not use namespaces.</p>";
    ret += "              <ul>";
    ret += "                  <li>In this case, it is obviously not necessary to declare or use";
    ret += "                      attribute selectors will function adequately in a down-level";
    ret += "                  </li>";
    ret += "                  <li>In a CSS namespace aware client, the default behavior of";
    ret += "                      element selectors matching without regard to namespace will";
    ret += "                      present. However, the use of specific element type selectors";
    ret += "                      match only elements that have no namespace ('<code>|name</code>')";
    ret += "                      will guarantee that selectors will match only XML elements that";
    ret += "                  </li>";
    ret += "              </ul>";
    ret += "          </li>";
    ret += "          <li>";
    ret += "              <p>The XML document defines a single, default namespace used";
    ret += "                  names.</p>";
    ret += "              <ul>";
    ret += "                  <li>In this case, a down-level client will function as if";
    ret += "                      element type and attribute selectors will match against all";
    ret += "                  </li>";
    ret += "              </ul>";
    ret += "          </li>";
    ret += "          <li>";
    ret += "              <p>The XML document does <b>not</b> use a default namespace, all";
    ret += "                  to the same URI).</p>";
    ret += "              <ul>";
    ret += "                  <li>In this case, the down-level client will view and match";
    ret += "                      element type and attribute selectors based on their fully";
    ret += "                      qualified name, not the local part as outlined in the <a href='#typenmsp'>Type selectors and Namespaces</a>";
    ret += "                      selectors may be declared using an escaped colon";
    ret += "                      '<code>\\:</code>'";
    ret += "                      '<code>html\\:h1</code>' will match";
    ret += "                      <code>&lt;html:h1&gt;</code>. Selectors using the qualified name";
    ret += "                  </li>";
    ret += "                  <li>Note that selectors declared in this fashion will";
    ret += "                      <em>only</em> match in down-level clients. A CSS namespace aware";
    ret += "                      client will match element type and attribute selectors based on";
    ret += "                      the name's local part. Selectors declared with the fully";
    ret += "                  </li>";
    ret += "              </ul>";
    ret += "          </li>";
    ret += "      </ol>";
    ret += "      <p>In other scenarios: when the namespace prefixes used in the XML are";
    ret += "          <em>different</em> namespace URIs within the same document, or in";
    ret += "          a CSS and XML namespace aware client.</p>";
    ret += "      <h2><a name='profiling'>12. Profiles</a></h2>";
    ret += "      <p>Each specification using Selectors must define the subset of W3C";
    ret += "          Selectors it allows and excludes, and describe the local meaning of";
    ret += "          all the components of that subset.</p>";
    ret += "      <p>Non normative examples:";
    ret += "      </p><div class='profile'>";
    ret += "          <table class='tprofile'>";
    ret += "              <tbody>";
    ret += "              <tr>";
    ret += "                  <th class='title' colspan='2'>Selectors profile</th>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Specification</th>";
    ret += "                  <td>CSS level 1</td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Accepts</th>";
    ret += "                  <td>type selectors<br>class selectors<br>ID selectors<br>:link,";
    ret += "                      :visited and :active pseudo-classes<br>descendant combinator";
    ret += "                      <br>::first-line and ::first-letter pseudo-elements";
    ret += "                  </td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Excludes</th>";
    ret += "                  <td>";
    ret += "                      <p>universal selector<br>attribute selectors<br>:hover and";
    ret += "                          pseudo-classes<br>:target pseudo-class<br>:lang()";
    ret += "                          pseudo-class<br>all UI";
    ret += "                          element states pseudo-classes<br>all structural";
    ret += "                          pseudo-classes<br>negation pseudo-class<br>all";
    ret += "                          UI element fragments pseudo-elements<br>::before and ::after";
    ret += "                          pseudo-elements<br>child combinators<br>sibling combinators";
    ret += "                      </p><p>namespaces</p></td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Extra constraints</th>";
    ret += "                  <td>only one class selector allowed per sequence of simple";
    ret += "                      selectors";
    ret += "                  </td>";
    ret += "              </tr>";
    ret += "              </tbody>";
    ret += "          </table>";
    ret += "          <br><br>";
    ret += "          <table class='tprofile'>";
    ret += "              <tbody>";
    ret += "              <tr>";
    ret += "                  <th class='title' colspan='2'>Selectors profile</th>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Specification</th>";
    ret += "                  <td>CSS level 2</td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Accepts</th>";
    ret += "                  <td>type selectors<br>universal selector<br>attribute presence and";
    ret += "                      values selectors<br>class selectors<br>ID selectors<br>:link,";
    ret += "                      <br>descendant combinator<br>child combinator<br>adjacent";
    ret += "                      combinator<br>::first-line and ::first-letter";
    ret += "                      pseudo-elements<br>::before";
    ret += "                  </td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Excludes</th>";
    ret += "                  <td>";
    ret += "                      <p>content selectors<br>substring matching attribute";
    ret += "                          selectors<br>:target pseudo-classes<br>all UI element";
    ret += "                          states pseudo-classes<br>all structural pseudo-classes other";
    ret += "                          than :first-child<br>negation pseudo-class<br>all UI element";
    ret += "                          fragments pseudo-elements<br>general sibling combinators";
    ret += "                      </p><p>namespaces</p></td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Extra constraints</th>";
    ret += "                  <td>more than one class selector per sequence of simple selectors";
    ret += "                  </td>";
    ret += "              </tr>";
    ret += "              </tbody>";
    ret += "          </table>";
    ret += "          <p>In CSS, selectors express pattern matching rules that determine which";
    ret += "          </p><p>The following selector (CSS level 2) will <b>match</b> all anchors <code>a</code>";
    ret += "              with attribute <code>name</code> set inside a section 1 header";
    ret += "              <code>h1</code>:";
    ret += "          </p><pre>h1 a[name]</pre>";
    ret += "          <p>All CSS declarations attached to such a selector are applied to elements";
    ret += "              matching it.</p></div>";
    ret += "      <div class='profile'>";
    ret += "          <table class='tprofile'>";
    ret += "              <tbody>";
    ret += "              <tr>";
    ret += "                  <th class='title' colspan='2'>Selectors profile</th>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Specification</th>";
    ret += "                  <td>STTS 3</td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Accepts</th>";
    ret += "                  <td>";
    ret += "                      <p>type selectors<br>universal selectors<br>attribute";
    ret += "                          selectors<br>class";
    ret += "                          selectors<br>ID selectors<br>all structural";
    ret += "                          pseudo-classes<br>";
    ret += "                      </p><p>namespaces</p></td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Excludes</th>";
    ret += "                  <td>non-accepted pseudo-classes<br>pseudo-elements<br></td>";
    ret += "              </tr>";
    ret += "              <tr>";
    ret += "                  <th>Extra constraints</th>";
    ret += "                  <td>some selectors and combinators are not allowed in fragment";
    ret += "                  </td>";
    ret += "              </tr>";
    ret += "              </tbody>";
    ret += "          </table>";
    ret += "          <p>Selectors can be used in STTS 3 in two different";
    ret += "          </p><ol>";
    ret += "              <li>a selection mechanism equivalent to CSS selection mechanism:";
    ret += "              </li><li>fragment descriptions that appear on the right side of declarations.";
    ret += "              </li>";
    ret += "          </ol>";
    ret += "      </div>";
    ret += "      <h2><a name='Conformance'></a>13. Conformance and requirements</h2>";
    ret += "      <p>This section defines conformance with the present specification only.";
    ret += "      </p><p>The inability of a user agent to implement part of this specification due to";
    ret += "      </p><p>All specifications reusing Selectors must contain a <a href='#profiling'>Profile</a> listing the";
    ret += "          subset of Selectors it accepts or excludes, and describing the constraints";
    ret += "      </p><p>Invalidity is caused by a parsing error, e.g. an unrecognized token or a";
    ret += "      </p><p>User agents must observe the rules for handling parsing errors:";
    ret += "      </p><ul>";
    ret += "          <li>a simple selector containing an undeclared namespace prefix is invalid";
    ret += "          </li>";
    ret += "          <li>a selector containing an invalid simple selector, an invalid combinator";
    ret += "          </li>";
    ret += "          <li>a group of selectors containing an invalid selector is invalid.</li>";
    ret += "      </ul>";
    ret += "      <p>Specifications reusing Selectors must define how to handle parsing";
    ret += "          used is dropped.)</p>";
    ret += "      <!-- Apparently all these references are out of date:";
    ret += "      <p>Implementations of this specification must behave as";
    ret += "      'recipients of text data' as defined by <a href='#refsCWWW'>[CWWW]</a>";
    ret += "      when parsing selectors and attempting matches. (In particular,";
    ret += "      <a href='#refsCWWW'>[CWWW]</a> and <a";
    ret += "      href='#refsUNICODE'>[UNICODE]</a> and apply to implementations of this";
    ret += "      specification.</p>-->";
    ret += "      <h2><a name='Tests'></a>14. Tests</h2>";
    ret += "      <p>This specification has <a href='http://www.w3.org/Style/CSS/Test/CSS3/Selectors/current/'>a test";
    ret += "          suite</a> allowing user agents to verify their basic conformance to";
    ret += "          and does not cover all possible combined cases of Selectors.</p>";
    ret += "      <h2><a name='ACKS'></a>15. Acknowledgements</h2>";
    ret += "      <p>The CSS working group would like to thank everyone who has sent";
    ret += "          comments on this specification over the years.</p>";
    ret += "      <p>The working group would like to extend special thanks to Donna";
    ret += "          the final editorial review.</p>";
    ret += "      <h2><a name='references'>16. References</a></h2>";
    ret += "      <dl class='refs'>";
    ret += "          <dt>[CSS1]";
    ret += "          </dt><dd><a name='refsCSS1'></a> Bert Bos, Håkon Wium Lie; '<cite>Cascading";
    ret += "              Style Sheets, level 1</cite>', W3C Recommendation, 17 Dec 1996, revised";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/REC-CSS1'>http://www.w3.org/TR/REC-CSS1</a></code>)";
    ret += "          </dd><dt>[CSS21]";
    ret += "          </dt><dd><a name='refsCSS21'></a> Bert Bos, Tantek Çelik, Ian Hickson, Håkon";
    ret += "              Wium Lie, editors; '<cite>Cascading Style Sheets, level 2 revision";
    ret += "                  1</cite>', W3C Working Draft, 13 June 2005";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/CSS21'>http://www.w3.org/TR/CSS21</a></code>)";
    ret += "          </dd><dt>[CWWW]";
    ret += "          </dt><dd><a name='refsCWWW'></a> Martin J. Dürst, François Yergeau,";
    ret += "              Misha Wolf, Asmus Freytag, Tex Texin, editors; '<cite>Character Model";
    ret += "                  for the World Wide Web</cite>', W3C Recommendation, 15 February 2005";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/charmod/'>http://www.w3.org/TR/charmod/</a></code>)";
    ret += "          </dd><dt>[FLEX]";
    ret += "          </dt><dd><a name='refsFLEX'></a> '<cite>Flex: The Lexical Scanner";
    ret += "              Generator</cite>', Version 2.3.7, ISBN 1882114213";
    ret += "          </dd><dt>[HTML4]";
    ret += "          </dt><dd><a name='refsHTML4'></a> Dave Ragget, Arnaud Le Hors, Ian Jacobs,";
    ret += "              editors; '<cite>HTML 4.01 Specification</cite>', W3C Recommendation, 24";
    ret += "          </dd><dd>";
    ret += "              (<a href='http://www.w3.org/TR/html4/'><code>http://www.w3.org/TR/html4/</code></a>)";
    ret += "          </dd><dt>[MATH]";
    ret += "          </dt><dd><a name='refsMATH'></a> Patrick Ion, Robert Miner, editors; '<cite>Mathematical";
    ret += "              Markup Language (MathML) 1.01</cite>', W3C Recommendation, revision of 7";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/REC-MathML/'>http://www.w3.org/TR/REC-MathML/</a></code>)";
    ret += "          </dd><dt>[RFC3066]";
    ret += "          </dt><dd><a name='refsRFC3066'></a> H. Alvestrand; '<cite>Tags for the";
    ret += "              Identification of Languages</cite>', Request for Comments 3066, January";
    ret += "          </dd><dd>(<a href='http://www.ietf.org/rfc/rfc3066.txt'><code>http://www.ietf.org/rfc/rfc3066.txt</code></a>)";
    ret += "          </dd><dt>[STTS]";
    ret += "          </dt><dd><a name='refsSTTS'></a> Daniel Glazman; '<cite>Simple Tree Transformation";
    ret += "              Sheets 3</cite>', Electricité de France, submission to the W3C,";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/NOTE-STTS3'>http://www.w3.org/TR/NOTE-STTS3</a></code>)";
    ret += "          </dd><dt>[SVG]";
    ret += "          </dt><dd><a name='refsSVG'></a> Jon Ferraiolo, 藤沢 淳, Dean";
    ret += "              Jackson, editors; '<cite>Scalable Vector Graphics (SVG) 1.1";
    ret += "                  Specification</cite>', W3C Recommendation, 14 January 2003";
    ret += "          </dd><dd>(<code><a href='http://www.w3.org/TR/SVG/'>http://www.w3.org/TR/SVG/</a></code>)";
    ret += "          </dd><dt>[UNICODE]</dt>";
    ret += "          <dd><a name='refsUNICODE'></a> <cite><a href='http://www.unicode.org/versions/Unicode4.1.0/'>The Unicode";
    ret += "              Standard, Version 4.1</a></cite>, The Unicode Consortium. Boston, MA,";
    ret += "              Addison-Wesley, March 2005. ISBN 0-321-18578-1, as amended by <a href='http://www.unicode.org/versions/Unicode4.0.1/'>Unicode";
    ret += "                  4.0.1</a> and <a href='http://www.unicode.org/versions/Unicode4.1.0/'>Unicode";
    ret += "                  4.1.0</a>.";
    ret += "          </dd><dd>(<code><a href='http://www.unicode.org/versions/'>http://www.unicode.org/versions/</a></code>)";
    ret += "          </dd>";
    ret += "          <dt>[XML10]";
    ret += "          </dt><dd><a name='refsXML10'></a> Tim Bray, Jean Paoli, C. M. Sperberg-McQueen,";
    ret += "              Eve Maler, François Yergeau, editors; '<cite>Extensible Markup";
    ret += "                  Language (XML) 1.0 (Third Edition)</cite>', W3C Recommendation, 4";
    ret += "          </dd><dd>(<a href='http://www.w3.org/TR/REC-xml/'><code>http://www.w3.org/TR/REC-xml/</code></a>)";
    ret += "          </dd><dt>[XMLNAMES]";
    ret += "          </dt><dd><a name='refsXMLNAMES'></a> Tim Bray, Dave Hollander, Andrew Layman,";
    ret += "              editors; '<cite>Namespaces in XML</cite>', W3C Recommendation, 14";
    ret += "          </dd><dd>(<a href='http://www.w3.org/TR/REC-xml-names/'><code>http://www.w3.org/TR/REC-xml-names/</code></a>)";
    ret += "          </dd><dt>[YACC]";
    ret += "          </dt><dd><a name='refsYACC'></a> S. C. Johnson; '<cite>YACC — Yet another";
    ret += "              compiler compiler</cite>', Technical Report, Murray Hill, 1975";
    ret += "      </dd></dl>'; </div>";
    ret += "      <input name='n' value='v1' type='radio'>1";
    ret += "      <input name='n' value='v2' checked='checked' type='radio'>2";
    ret += "</body></html>";
    return ret;
  }

}
