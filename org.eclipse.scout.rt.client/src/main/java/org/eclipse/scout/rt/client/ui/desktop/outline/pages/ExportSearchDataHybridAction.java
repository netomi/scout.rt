/*
 * Copyright (c) 2010, 2024 BSI Business Systems Integration AG
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.scout.rt.client.ui.desktop.outline.pages;

import org.eclipse.scout.rt.client.ui.desktop.hybrid.AbstractHybridAction;
import org.eclipse.scout.rt.client.ui.desktop.hybrid.HybridActionContextElement;
import org.eclipse.scout.rt.client.ui.desktop.hybrid.HybridActionType;
import org.eclipse.scout.rt.dataobject.IDoEntity;

@HybridActionType(ExportSearchDataHybridAction.TYPE)
public class ExportSearchDataHybridAction extends AbstractHybridAction<IDoEntity> {

  protected static final String TYPE = "ExportSearchData";

  @Override
  public void execute(IDoEntity data) {
    IDoEntity searchData = null;

    HybridActionContextElement contextElement = getContextElement();
    if (contextElement != null && contextElement.getElement() instanceof IPageWithTable) {
      IPageWithTable tablePage = (IPageWithTable) contextElement.getElement();
      searchData = tablePage.getSearchFilter().getData();
    }

    fireHybridActionEndEvent(searchData);
  }
}
