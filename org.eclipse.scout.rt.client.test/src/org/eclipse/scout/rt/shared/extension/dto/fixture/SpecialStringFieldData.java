/*******************************************************************************
 * Copyright (c) 2014 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.shared.extension.dto.fixture;

import java.util.Map;

import javax.annotation.Generated;

import org.eclipse.scout.commons.annotations.Extends;
import org.eclipse.scout.rt.shared.data.form.ValidationRule;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications recommended.
 * 
 * @generated
 */
@Extends(OrigFormData.class)
@Generated(value = "org.eclipse.scout.sdk.workspace.dto.formdata.FormDataDtoUpdateOperation", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SpecialStringFieldData extends AbstractValueFieldData<String> {

  private static final long serialVersionUID = 1L;

  public SpecialStringFieldData() {
  }

  /**
   * list of derived validation rules.
   */
  @Override
  protected void initValidationRules(Map<String, Object> ruleMap) {
    super.initValidationRules(ruleMap);
    ruleMap.put(ValidationRule.MAX_LENGTH, 4000);
  }
}
