/*******************************************************************************
 * Copyright (c) 2010-2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.ui.form.fields.smartfield2;

import org.eclipse.scout.rt.client.ui.form.fields.ParsingFailedStatus;
import org.eclipse.scout.rt.platform.util.TriState;

public class SmartField2UIFacade implements ISmartField2UIFacade {

  private AbstractSmartField2<?> m_smartField;

  public SmartField2UIFacade(AbstractSmartField2<?> smartField) {
    this.m_smartField = smartField;
  }

  @Override
  public void setDisplayTextFromUI(String text) {
    m_smartField.setDisplayText(text);
  }

  @Override
  public void setErrorStatusFromUI(ParsingFailedStatus errorStatus) {
    m_smartField.removeErrorStatus(ParsingFailedStatus.class);
    if (errorStatus != null) {
      m_smartField.addErrorStatus(errorStatus);
    }
  }

  @Override
  public void setActiveFilterFromUI(TriState activeFilter) {
    m_smartField.setActiveFilter(activeFilter);
  }

}
