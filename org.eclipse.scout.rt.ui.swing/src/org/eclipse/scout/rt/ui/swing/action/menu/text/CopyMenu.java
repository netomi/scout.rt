/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.ui.swing.action.menu.text;

import javax.swing.text.JTextComponent;

import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.ui.swing.SwingUtility;

/**
 *
 */
public class CopyMenu extends AbstractMenu {

  private final JTextComponent m_textComponent;

  public CopyMenu(JTextComponent textComponent) {
    m_textComponent = textComponent;
  }

  @Override
  protected String getConfiguredText() {
    return SwingUtility.getNlsText("Copy");
  }

  @Override
  protected void execAction() throws ProcessingException {
    JTextComponent txt = getTextComponent();
    if (txt.isEnabled() && txt.isEditable()) {
      txt.copy();
    }
    else {
      boolean hasSelection = StringUtility.hasText(txt.getSelectedText());
      if (hasSelection) {
        txt.copy();
      }
      else {
        txt.selectAll();
        txt.copy();
        txt.select(0, 0);
      }
    }
  }

  public JTextComponent getTextComponent() {
    return m_textComponent;
  }
}
