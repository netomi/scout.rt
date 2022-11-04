/*
 * Copyright (c) 2010-2022 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
import {FormField, FormFieldModel} from '../../../index';
import {RefModel} from '../../../types';

export default interface SplitBoxModel extends FormFieldModel {
  firstField: FormField | RefModel<FormFieldModel>;
  secondField: FormField | RefModel<FormFieldModel>;
  collapsibleField: FormField | string;
  fieldCollapsed?: boolean;
  toggleCollapseKeyStroke?: string;
  firstCollapseKeyStroke?: string;
  secondCollapseKeyStroke?: string;
  /** true = split x-axis, false = split y-axis */
  splitHorizontal?: boolean;
  splitterEnabled?: boolean;
  splitterPosition?: number;
  minSplitterPosition?: number;
  splitterPositionType?: string;
  fieldMinimized?: boolean;
  minimizeEnabled?: boolean;
}