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
import {Action, arrays, Column, Form, FormField, FormModel, GroupBox, Mode, ModeSelector, RadioButton, scout, Session, StringField, TabBox, TabItem, Table, TableField, Widget} from '../../index';
import $ from 'jquery';
import {ObjectType} from '../../ObjectFactory';
import {Optional, SomeRequired} from '../../types';
import {ModelOf} from '../../scout';
import SpecForm from './SpecForm';
import SpecRadioButtonGroup from './SpecRadioButtonGroup';

export default class FormSpecHelper {
  session: Session;

  constructor(session: Session) {
    this.session = session;
  }

  closeMessageBoxes() {
    if (!this.session || !this.session.$entryPoint) {
      return;
    }
    let $messageBoxButtons = this.session.$entryPoint.find('.messagebox .box-button');
    for (let i = 0; i < $messageBoxButtons.length; i++) {
      scout.widget($messageBoxButtons[i], Action).doAction();
    }
  }

  createViewWithOneField(model?: Optional<FormModel, 'parent'>): SpecForm {
    let form = this.createFormWithOneField(model);
    form.displayHint = Form.DisplayHint.VIEW;
    return form;
  }

  createFormWithOneField(model?: Optional<FormModel, 'parent'>): SpecForm {
    let defaults = {
      parent: this.session.desktop
    };
    model = $.extend({}, defaults, model);
    let form = scout.create(SpecForm, model as SomeRequired<FormModel, 'parent'>);
    let rootGroupBox = this.createGroupBoxWithFields(form, 1);
    form.setRootGroupBox(rootGroupBox);
    return form;
  }

  createFormWithFieldsAndTabBoxes(model?: Optional<FormModel, 'parent'>): Form {
    let fieldModelPart = (id, mandatory) => ({
        id: id,
        objectType: StringField,
        label: id,
        mandatory: mandatory
      }),
      tabBoxModelPart = (id, tabItems) => ({
        id: id,
        objectType: TabBox,
        tabItems: tabItems
      }),
      tabItemModelPart = (id, fields) => ({
        id: id,
        objectType: TabItem,
        label: 'id',
        fields: fields
      }),
      tableFieldModelPart = (id, columns) => ({
        id: id,
        objectType: TableField,
        label: id,
        table: {
          id: id + 'Table',
          objectType: Table,
          columns: columns
        }
      }),
      columnModelPart = (id, mandatory) => ({
        id: id,
        objectType: Column,
        text: id,
        editable: true,
        mandatory: mandatory
      });

    let defaults = {
      parent: this.session.desktop,
      id: 'Form',
      title: 'Form',
      rootGroupBox: {
        id: 'RootGroupBox',
        objectType: GroupBox,
        fields: [
          fieldModelPart('Field1', false),
          fieldModelPart('Field2', false),
          fieldModelPart('Field3', true),
          fieldModelPart('Field4', true),
          tabBoxModelPart('TabBox', [
            tabItemModelPart('TabA', [
              fieldModelPart('FieldA1', false),
              fieldModelPart('FieldA2', true),
              tabBoxModelPart('TabBoxA', [
                tabItemModelPart('TabAA', [
                  fieldModelPart('FieldAA1', false),
                  fieldModelPart('FieldAA2', true)
                ]),
                tabItemModelPart('TabAB', [
                  fieldModelPart('FieldAB1', false),
                  fieldModelPart('FieldAB2', true)
                ]),
                tabItemModelPart('TabAC', [
                  fieldModelPart('FieldAC1', false),
                  fieldModelPart('FieldAC2', true)
                ])
              ])
            ]),
            tabItemModelPart('TabB', [
              fieldModelPart('FieldB1', false),
              fieldModelPart('FieldB2', false),
              fieldModelPart('FieldB3', true),
              fieldModelPart('FieldB4', true),
              tableFieldModelPart('TableFieldB5', [
                columnModelPart('ColumnB51', false),
                columnModelPart('ColumnB52', true)
              ])
            ])
          ])
        ]
      }
    };

    model = $.extend({}, defaults, model);
    let form = scout.create(Form, model as SomeRequired<FormModel, 'parent'>);
    form.widget('TableFieldB5', TableField).table.insertRows([{cells: arrays.init(2, null)}, {cells: arrays.init(2, null)}]);
    return form;
  }

  createGroupBoxWithOneField(parent: Widget): GroupBox {
    return this.createGroupBoxWithFields(parent, 1);
  }

  createGroupBoxWithFields(parent?: Widget, numFields?: number): GroupBox {
    parent = scout.nvl(parent, this.session.desktop);
    numFields = scout.nvl(numFields, 1);
    let
      fields = [],
      groupBox = scout.create(GroupBox, {
        parent: parent
      });
    for (let i = 0; i < numFields; i++) {
      fields.push(scout.create(StringField, {
        parent: groupBox
      }));
    }
    groupBox.setProperty('fields', fields);
    return groupBox;
  }

  createRadioButtonGroup(parent?: Widget, numRadioButtons?: number): SpecRadioButtonGroup {
    parent = scout.nvl(parent, this.session.desktop);
    numRadioButtons = scout.nvl(numRadioButtons, 2);
    let fields = [];
    for (let i = 0; i < numRadioButtons; i++) {
      fields.push({
        objectType: RadioButton
      });
    }
    return scout.create(SpecRadioButtonGroup, {
      parent: parent,
      fields: fields
    });
  }

  createFormWithFields(parent: Widget, isModal: boolean, numFields?: number): Form {
    parent = scout.nvl(parent, this.session.desktop);
    let form = scout.create(Form, {
      parent: parent,
      displayHint: isModal ? 'dialog' : 'view'
    });
    let rootGroupBox = this.createGroupBoxWithFields(form, numFields);
    form.setRootGroupBox(rootGroupBox);
    return form;
  }

  createFieldModel<T>(objectType?: ObjectType<T>, parent?: Widget, modelProperties?: Record<string, any>): ModelOf<T> & { id: string; objectType: ObjectType<T>; parent: Widget; session: Session } {
    parent = scout.nvl(parent, this.session.desktop);
    let model = createSimpleModel(objectType || 'StringField', this.session);
    if (modelProperties) {
      $.extend(model, modelProperties);
    }
    return model as ModelOf<T> & { id: string; objectType: ObjectType<T>; parent: Widget; session: Session };
  }

  createField<T extends FormField>(objectType: ObjectType<T>, parent?: Widget, modelProperties?: Record<string, any>): T {
    parent = parent || this.session.desktop;
    return scout.create(objectType, this.createFieldModel(objectType, parent, modelProperties));
  }

  createModeSelector(parent?: Widget, numModes?: number): ModeSelector {
    parent = scout.nvl(parent, this.session.desktop);
    numModes = scout.nvl(numModes, 2);
    let modes = [];
    for (let i = 0; i < numModes; i++) {
      modes.push({
        objectType: Mode
      });
    }
    return scout.create(ModeSelector, {
      parent: parent,
      modes: modes
    });
  }
}