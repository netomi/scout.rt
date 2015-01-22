// SCOUT GUI
// (c) Copyright 2013-2014, BSI Business Systems Integration AG
//

__include("jquery-scout.js");
// protects $ and undefined from being redefined by another library
(function(scout, $, undefined) {
__include("main.js");
__include("util/arrays.js");
__include("util/logging.js");
__include("util/EventSupport.js");
__include("util/keys.js");
__include("util/KeystrokeManager.js");
__include("util/ModelAdapter.js");
__include("util/NullAdapter.js");
__include("util/numbers.js");
__include("util/ObjectFactory.js");
__include("util/dates.js");
__include("util/Device.js");
__include("util/helpers.js");
__include("util/objects.js");
__include("util/strings.js");
__include("util/DetachHelper.js");
__include("util/URL.js");
__include("session/Session.js");
__include("session/Event.js");
__include("session/Locale.js");
__include("session/UserAgent.js");
__include("session/Reconnector.js");
__include("datamodel/DataModel.js");
__include("action/Action.js");
__include("menu/Menu.js");
__include("menu/FormMenuItemsOrder.js");
__include("menu/menus.js");
__include("menu/MenuBar.js");
__include("menu/Popup.js");
__include("menu/TableMenuItemsOrder.js");
__include("layout/AbstractLayout.js");
__include("layout/graphics.js");
__include("layout/HtmlComponent.js");
__include("layout/LayoutConstants.js");
__include("layout/LayoutValidator.js");
__include("layout/LogicalGridData.js");
__include("layout/LogicalGridLayoutInfo.js");
__include("layout/LogicalGridLayout.js");
__include("layout/NullLayout.js");
__include("layout/SingleLayout.js");
__include("table/Table.js");
__include("table/TableLayout.js");
__include("table/TableHeader.js");
__include("table/TableFooter.js");
__include("table/TableHeaderMenu.js");
__include("table/TableOrganizeMenu.js");
__include("table/TableKeystrokeAdapter.js");
__include("table/TableSelectionHandler.js");
__include("table/control/TableControl.js");
__include("table/control/ChartTableControl.js");
__include("table/control/ChartTableControlMatrix.js");
__include("table/control/GraphTableControl.js");
__include("table/control/MapTableControl.js");
__include("table/control/AnalysisTableControl.js");
__include("tree/Tree.js");
__include("tree/TreeCompact.js");
__include("tooltip/Tooltip.js");
__include("activitymap/ActivityMap.js");
__include("activitymap/ActivityMapLayout.js");
__include("calendar/Calendar.js");
__include("calendar/CalendarLayout.js");
__include("desktop/BaseDesktop.js");
__include("desktop/Desktop.js");
__include("desktop/DesktopKeystrokeAdapter.js");
__include("desktop/DesktopNavigation.js");
__include("desktop/FormToolButton.js");
__include("desktop/OutlineViewButton.js");
__include("desktop/Outline.js");
__include("desktop/SearchOutline.js");
__include("messagebox/MessageBox.js");
__include("messagebox/MessageBoxModelAdapter.js");
__include("form/Form.js");
__include("form/FormLayout.js");
__include("form/fields/fields.js");
__include("form/fields/FormField.js");
__include("form/fields/FormFieldLayout.js");
__include("form/fields/TextFieldLayout.js");
__include("form/fields/radiobutton/RadioButtonLayout.js");
__include("form/fields/ValueField.js");
__include("form/fields/CompositeField.js");
__include("form/fields/button/Button.js");
__include("form/fields/button/ButtonLayout.js");
__include("form/fields/calendarfield/CalendarField.js");
__include("form/fields/checkbox/CheckBoxField.js");
__include("form/fields/radiobutton/RadioButton.js");
__include("form/fields/radiobutton/RadioButtonGroup.js");
__include("form/fields/labelfield/LabelField.js");
__include("form/fields/imagefield/ImageField.js");
__include("form/fields/numberfield/NumberField.js");
__include("form/fields/plannerfield/PlannerField.js");
__include("form/fields/richtextfield/RichTextField.js");
__include("form/fields/stringfield/StringField.js");
__include("form/fields/smartfield/AbstractSmartField.js");
__include("form/fields/smartfield/LookupStrategy.js");
__include("form/fields/smartfield/SmartField.js");
__include("form/fields/smartfield/SmartFieldMultiline.js");
__include("form/fields/smartfield/SmartFieldMultilineLayout.js");
__include("form/fields/tagcloudfield/TagCloudField.js");
__include("form/fields/datefield/DateField.js");
__include("form/fields/datefield/DatePicker.js");
__include("form/fields/groupbox/GroupBox.js");
__include("form/fields/groupbox/GroupBoxLayout.js");
__include("form/fields/sequencebox/SequenceBox.js");
__include("form/fields/tablefield/TableField.js");
__include("form/fields/treefield/TreeField.js");
__include("form/fields/tabbox/TabBox.js");
__include("form/fields/tabbox/TabBoxLayout.js");
__include("form/fields/tabbox/TabItem.js");
__include("menu/AbstractNavigationButton.js");
__include("menu/NavigateDownButton.js");
__include("menu/NavigateUpButton.js");
__include("scrollbar/scrollbars.js");
__include("scrollbar/Scrollbar.js");
__include("text/DecimalFormat.js");
__include("text/DateFormat.js");
}(window.scout = window.scout || {}, jQuery));
