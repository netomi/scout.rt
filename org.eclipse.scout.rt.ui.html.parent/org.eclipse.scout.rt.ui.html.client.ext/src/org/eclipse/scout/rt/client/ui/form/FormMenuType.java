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
package org.eclipse.scout.rt.client.ui.form;

import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;

// FIXME AWE: delete this enum
// --> new IToolButton

public enum FormMenuType implements IMenuType {
  Regular,
  System, //Not sure if necessary. We have a system_type on IMenu5. Maybe we should better configure the buttons in the right order and use separators
  Tool
}
