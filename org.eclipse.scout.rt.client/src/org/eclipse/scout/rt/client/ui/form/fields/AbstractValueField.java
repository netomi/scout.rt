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
package org.eclipse.scout.rt.client.ui.form.fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.eclipse.scout.commons.CompareUtility;
import org.eclipse.scout.commons.ConfigurationUtility;
import org.eclipse.scout.commons.EventListenerList;
import org.eclipse.scout.commons.StringUtility;
import org.eclipse.scout.commons.TypeCastUtility;
import org.eclipse.scout.commons.VerboseUtility;
import org.eclipse.scout.commons.annotations.ClassId;
import org.eclipse.scout.commons.annotations.ConfigOperation;
import org.eclipse.scout.commons.annotations.ConfigProperty;
import org.eclipse.scout.commons.annotations.FormData;
import org.eclipse.scout.commons.annotations.FormData.DefaultSubtypeSdkCommand;
import org.eclipse.scout.commons.annotations.FormData.SdkCommand;
import org.eclipse.scout.commons.annotations.Order;
import org.eclipse.scout.commons.annotations.ScoutSdkIgnore;
import org.eclipse.scout.commons.exception.IProcessingStatus;
import org.eclipse.scout.commons.exception.ProcessingException;
import org.eclipse.scout.commons.exception.VetoException;
import org.eclipse.scout.commons.holders.IHolder;
import org.eclipse.scout.commons.logger.IScoutLogger;
import org.eclipse.scout.commons.logger.ScoutLogManager;
import org.eclipse.scout.commons.xmlparser.SimpleXmlElement;
import org.eclipse.scout.rt.client.ClientSyncJob;
import org.eclipse.scout.rt.client.ui.action.ActionUtility;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.IContextMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.MenuUtility;
import org.eclipse.scout.rt.client.ui.action.menu.ValueFieldContextMenu;
import org.eclipse.scout.rt.shared.ScoutTexts;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractFormFieldData;
import org.eclipse.scout.rt.shared.data.form.fields.AbstractValueFieldData;
import org.eclipse.scout.rt.shared.services.common.exceptionhandler.IExceptionHandlerService;
import org.eclipse.scout.service.SERVICES;

@ScoutSdkIgnore
@ClassId("dfc4615d-a38d-450a-8592-e4d2c536d7cb")
@FormData(value = AbstractValueFieldData.class, defaultSubtypeSdkCommand = DefaultSubtypeSdkCommand.CREATE, sdkCommand = SdkCommand.USE, genericOrdinal = 0)
public abstract class AbstractValueField<T> extends AbstractFormField implements IValueField<T> {
  private static final IScoutLogger LOG = ScoutLogManager.getLogger(AbstractValueField.class);

  private int m_valueChanging;
  private int m_valueParsing;
  private int m_valueValidating;
  private T m_initValue;
  private EventListenerList m_listenerList;
  private EventListenerList m_listeningSlaves;// my slaves

  public AbstractValueField() {
    this(true);
  }

  public AbstractValueField(boolean callInitializer) {
    super(callInitializer);
  }

  /*
   * Configuration
   */

  @Order(200)
  @ConfigProperty(ConfigProperty.BOOLEAN)
  protected boolean getConfiguredAutoDisplayText() {
    return true;
  }

  /**
   * Specifies if the default system menus (cut, copy, paste) should be available on this field.
   * 
   * @return true if the default system menus should be available, false otherwise.
   */
  @Order(210)
  @ConfigProperty(ConfigProperty.BOOLEAN)
  protected boolean getConfiguredAutoAddDefaultMenus() {
    return true;
  }

  @Override
  protected void initConfig() {
    super.initConfig();
    m_listeningSlaves = new EventListenerList();
    m_listenerList = new EventListenerList();
    setAutoDisplayText(getConfiguredAutoDisplayText());
    setAutoAddDefaultMenus(getConfiguredAutoAddDefaultMenus());

    // menus
    List<Class<? extends IMenu>> declaredMenus = getDeclaredMenus();
    List<IMenu> menuList = new ArrayList<IMenu>(declaredMenus.size() + 4);
    for (Class<? extends IMenu> menuClazz : declaredMenus) {
      try {
        menuList.add(ConfigurationUtility.newInnerInstance(this, menuClazz));
      }
      catch (Exception e) {
        SERVICES.getService(IExceptionHandlerService.class).handleException(new ProcessingException("error creating instance of class '" + menuClazz.getName() + "'.", e));
      }
    }
    try {
      injectMenusInternal(menuList);
    }
    catch (Exception e) {
      LOG.error("error occured while dynamically contributing menus.", e);
    }
    //set container on menus
    IContextMenu contextMenu = new ValueFieldContextMenu(this);
    contextMenu.setContainerInternal(this);
    contextMenu.setChildActions(menuList);
    setContextMenu(contextMenu);
  }

  /**
   * Override this internal method only in order to make use of dynamic menus<br>
   * Used to manage menu list and add/remove menus
   * 
   * @param menuList
   *          live and mutable list of configured menus
   */
  protected void injectMenusInternal(List<IMenu> menuList) {
  }

  protected void setContextMenu(IContextMenu contextMenu) {
    propertySupport.setProperty(PROP_CONTEXT_MENU, contextMenu);
  }

  @Override
  public IContextMenu getContextMenu() {
    return (IContextMenu) propertySupport.getProperty(PROP_CONTEXT_MENU);
  }

  @Override
  public List<IMenu> getMenus() {
    return getContextMenu().getChildActions();
  }

  @Override
  protected void initFieldInternal() throws ProcessingException {
    super.initFieldInternal();
    // init actions
    ActionUtility.initActions(getMenus());
  }

  protected List<Class<? extends IMenu>> getDeclaredMenus() {
    Class[] dca = ConfigurationUtility.getDeclaredPublicClasses(getClass());
    List<Class<IMenu>> filtered = ConfigurationUtility.filterClasses(dca, IMenu.class);
    List<Class<? extends IMenu>> foca = ConfigurationUtility.sortFilteredClassesByOrderAnnotation(filtered, IMenu.class);
    return ConfigurationUtility.removeReplacedClasses(foca);
  }

  @Override
  public List<IKeyStroke> getContributedKeyStrokes() {
    return MenuUtility.getKeyStrokesFromMenus(getMenus());
  }

  /*
   * Data i/o
   */
  @SuppressWarnings("unchecked")
  @Override
  public void exportFormFieldData(AbstractFormFieldData target) throws ProcessingException {
    AbstractValueFieldData<T> v = (AbstractValueFieldData<T>) target;
    v.setValue(this.getValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void importFormFieldData(AbstractFormFieldData source, boolean valueChangeTriggersEnabled) {
    AbstractValueFieldData<T> v = (AbstractValueFieldData<T>) source;
    if (v.isValueSet()) {
      try {
        if (!valueChangeTriggersEnabled) {
          setValueChangeTriggerEnabled(false);
        }
        //
        T newValue;
        Object o = v.getValue();
        if (o != null) {
          Class castType = getHolderType();
          if (castType.isAssignableFrom(o.getClass())) {
            newValue = (T) o;
          }
          else {
            newValue = (T) TypeCastUtility.castValue(o, castType);
          }
        }
        else {
          newValue = null;
        }
        this.setValue(newValue);
      }
      finally {
        if (!valueChangeTriggersEnabled) {
          setValueChangeTriggerEnabled(true);
        }
      }
    }
  }

  /*
   * XML i/o
   */
  @Override
  public void storeXML(SimpleXmlElement x) throws ProcessingException {
    super.storeXML(x);
    T value = getValue();
    try {
      x.setObjectAttribute("value", value);
    }
    catch (IOException e) {
      if (LOG.isInfoEnabled()) {
        LOG.info("not serializable value in field " + getClass().getName() + "/" + getLabel() + ": " + e);
      }
    }
  }

  @Override
  public void loadXML(SimpleXmlElement x) throws ProcessingException {
    super.loadXML(x);
    try {
      T value = TypeCastUtility.castValue(x.getObjectAttribute("value", null), getHolderType());
      setValue(value);
    }
    catch (Exception e) {
      // be lenient, maybe the field was changed
      LOG.warn(null, e);
    }
  }

  @Override
  public void resetValue() {
    T newValue = getInitValue();
    setValue(newValue);
    checkSaveNeeded();
    checkEmpty();
  }

  @Override
  public void refreshDisplayText() {
    if (shouldUpdateDisplayText(false)) {
      String t = execFormatValue(getValue());
      setDisplayText(t);
    }
  }

  @Override
  public void addMasterListener(MasterListener listener) {
    m_listeningSlaves.add(MasterListener.class, listener);
  }

  @Override
  public void removeMasterListener(MasterListener listener) {
    m_listeningSlaves.remove(MasterListener.class, listener);
  }

  private void fireMasterChanged() {
    // fire listeners
    EventListener[] a = m_listeningSlaves.getListeners(MasterListener.class);
    if (a != null && a.length > 0) {
      T masterValue = getValue();
      for (int i = 0; i < a.length; i++) {
        ((MasterListener) a[i]).masterChanged(masterValue);
      }
    }
  }

  @Override
  public void setInitValue(T initValue) {
    m_initValue = initValue;
  }

  @Override
  public T getInitValue() {
    return m_initValue;
  }

  @Override
  protected boolean execIsSaveNeeded() throws ProcessingException {
    return !CompareUtility.equals(getValue(), getInitValue());
  }

  @Override
  protected void execMarkSaved() throws ProcessingException {
    super.execMarkSaved();
    T value = getValue();
    setInitValue(value);
  }

  @Override
  protected boolean execIsEmpty() throws ProcessingException {
    return getValue() == null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T getValue() {
    if (isValueValidating() && ClientSyncJob.isSyncClientJob()) {
      throw new IllegalStateException("The value of " + getClass().getSimpleName() + " can not be accessed while the value is beeing validated");
    }
    else {
      //caller from outside thread (ui)
      //wait at most 10 seconds
      int i = 0;
      while (isValueValidating() && i < 100) {
        try {
          Thread.sleep(100);
        }
        catch (InterruptedException e) {
          //nop
        }
        i++;
      }
    }
    return (T) propertySupport.getProperty(PROP_VALUE);
  }

  @Override
  public final void setValue(T rawValue) {
    /**
     * @rn imo, 22.02.2006, set verifyInput flag while firing triggers when a
     *     message box is shown, the doOK of the form might overrun this
     *     command. setting isVerifyInput() cancels the ok task
     */
    if (isValueChanging()) {
      Exception caller1 = new Exception();
      LOG.warn("Loop detection in " + getClass().getName() + " with value " + rawValue, caller1);
      return;
    }
    try {
      setFieldChanging(true);
      setValueChanging(true);
      //
      T validatedValue = null;

      if (getErrorStatus() instanceof ValidationFailedStatus) {
        clearErrorStatus();
      }
      try {
        validatedValue = validateValue(rawValue);

        //parsing error may be cleared after successful validation
        if (getErrorStatus() instanceof ParsingFailedStatus) {
          clearErrorStatus();
        }
      }
      catch (Throwable t) {
        ProcessingException e = (t instanceof ProcessingException ? (ProcessingException) t : new ProcessingException("Unexpected", t));

        //parsing error remains unchanged, regardless of validation error
        if (!(getErrorStatus() instanceof ParsingFailedStatus)) {
          setErrorStatus(new ValidationFailedStatus(e.getStatus()));
        }

        e.consume();
        e.addContextMessage(getLabel() + " = " + rawValue);
        if (!(e instanceof VetoException)) {
          SERVICES.getService(IExceptionHandlerService.class).handleException(e);
        }
        // break up
        return;
      }
      //
      T oldValue = getValue();
      boolean changed = propertySupport.setPropertyNoFire(PROP_VALUE, validatedValue);
      // change text if auto-set-text enabled
      if (shouldUpdateDisplayText(CompareUtility.notEquals(rawValue, validatedValue))) {
        String t = execFormatValue(validatedValue);
        setDisplayText(t);
      }
      if (changed) {
        propertySupport.firePropertyChange(PROP_VALUE, oldValue, validatedValue);
        //
        valueChangedInternal();
        checkSaveNeeded();
        checkEmpty();
        fireMasterChanged();
        if (isValueChangeTriggerEnabled()) {
          try {
            execChangedValue();
          }
          catch (ProcessingException ex) {
            SERVICES.getService(IExceptionHandlerService.class).handleException(ex);
          }
        }
      }
    }
    finally {
      setValueChanging(false);
      setFieldChanging(false);
    }
  }

  /**
   * Computes if the displayText should be computed and displayed in the field.
   * 
   * @param validValueDiffersFromRawValue
   *          indicates if there is business logic in {@link #validateValue(Object)} that changed the value of the field
   *          (in comparison to what has been parsed).
   * @return true if the displayText should be displayed, false if not.
   */
  protected boolean shouldUpdateDisplayText(boolean validValueDiffersFromRawValue) {
    return isAutoDisplayText();
  }

  /**
   * internal single observer for value changed triggers
   */
  protected void valueChangedInternal() {
    if (LOG.isDebugEnabled()) {
      LOG.debug(getLabel() + " " + VerboseUtility.dumpObject(getValue()));
    }
  }

  @Override
  public boolean isValueChanging() {
    return m_valueChanging > 0;
  }

  private void setValueChanging(boolean b) {
    if (b) {
      m_valueChanging++;
    }
    else {
      m_valueChanging--;
    }
  }

  @Override
  public boolean isValueParsing() {
    return m_valueParsing > 0;
  }

  private void setValueParsing(boolean b) {
    if (b) {
      m_valueParsing++;
    }
    else {
      m_valueParsing--;
    }
  }

  @Override
  public boolean isValueValidating() {
    return m_valueValidating > 0;
  }

  @Override
  public void addValueFieldListener(ValueFieldListener listener) {
    m_listenerList.add(ValueFieldListener.class, listener);
  }

  @Override
  public void removeValueFieldListener(ValueFieldListener listener) {
    m_listenerList.remove(ValueFieldListener.class, listener);
  }

  private void setValueValidating(boolean b) {
    if (b) {
      m_valueValidating++;
    }
    else {
      m_valueValidating--;
    }
  }

  @Override
  public final void fireValueChanged() {
    try {
      setValueChanging(true);
      //
      try {
        execChangedValue();
      }
      catch (ProcessingException ex) {
        SERVICES.getService(IExceptionHandlerService.class).handleException(ex);
      }
      fireMasterChanged();
    }
    finally {
      setValueChanging(false);
    }
  }

  private T validateValue(T rawValue) throws ProcessingException {
    try {
      setValueValidating(true);
      //
      T o = rawValue;
      o = validateValueInternal(o);
      o = execValidateValue(o);
      return o;
    }
    finally {
      setValueValidating(false);
    }
  }

  /**
   * override this method to perform detailed validation in subclasses
   */
  protected T validateValueInternal(T rawValue) throws ProcessingException {
    return rawValue;
  }

  /**
   * WHILE (not after) a new value is validating (that means the new value has
   * not yet been set), this methode is called.<br>
   * Check the new proposed value and either make it valid by returning this or
   * another valid value or reject by throwing a {@link VetoException}, it will
   * then appear red in the gui.
   * 
   * @return the validated value or throws an exception
   */
  @ConfigOperation
  @Order(190)
  protected T execValidateValue(T rawValue) throws ProcessingException {
    return rawValue;
  }

  /**
   * AFTER a new valid value was stored (that means the value is valid), this
   * method is called and can be used to broadcast this change to other fields
   * by for example calling {@link IValueField#setValue(Object)} on another
   * field.<br>
   * If this new value seems to be invalid (even though it has been validated
   * correctly) use {@link #setErrorStatus(IProcessingStatus)} to mark the value
   * as incorrect. It will appear red in the gui.<br>
   * In case this method throws exceptions, this will NOT invalidate the value
   * of the field (like {@link #execValidateValue(Object)} does)
   */
  @ConfigOperation
  @Order(220)
  protected void execChangedValue() throws ProcessingException {
  }

  @Override
  public final boolean parseValue(String text) {
    if (isValueParsing()) {
      LOG.warn("Loop detection in " + getLabel() + " with text " + text);
      return false;
    }
    try {
      setFieldChanging(true);
      setValueParsing(true);
      //
      T parsedValue = execParseValue(text);

      //
      IProcessingStatus oldErrorStatus = getErrorStatus();
      String oldDisplayText = getDisplayText();

      if (getErrorStatus() instanceof ParsingFailedStatus) {
        clearErrorStatus();
      }

      setValue(parsedValue);

      //do not clear validation errors, if the display text has not changed
      if (oldErrorStatus instanceof ValidationFailedStatus && getErrorStatus() == null &&
          StringUtility.nvl(text, "").equals(StringUtility.nvl(oldDisplayText, ""))) {
        setErrorStatus(oldErrorStatus);
      }
      //convert validation error to parsing error
      else if (getErrorStatus() instanceof ValidationFailedStatus) {
        setErrorStatus(new ParsingFailedStatus(getErrorStatus(), text));
      }
      return true;
    }
    catch (Throwable t) {
      ProcessingException e;
      if (t instanceof ProcessingException) {
        e = (ProcessingException) t;
      }
      else {
        LOG.warn(null, t);
        e = new ProcessingException(ScoutTexts.get("InvalidValueMessageX", text), t);
      }
      ParsingFailedStatus internalStatus = new ParsingFailedStatus(e.getStatus(), text);
      setErrorStatus(internalStatus);
      return false;
    }
    finally {
      setValueParsing(false);
      setFieldChanging(false);
    }
  }

  /**
   * override this method to perform detailed parsing in subclasses
   */
  protected T parseValueInternal(String text) throws ProcessingException {
    throw new ProcessingException("Not implemented");
  }

  /**
   * parse input text and create an appropriate value
   * 
   * @return parsed value, not yet validated
   */
  @ConfigOperation
  @Order(200)
  protected T execParseValue(String text) throws ProcessingException {
    return parseValueInternal(text);
  }

  /**
   * format a valid value for display
   * 
   * @return formatted value
   */
  @ConfigOperation
  @Order(210)
  protected String execFormatValue(T validValue) {
    return formatValueInternal(validValue);
  }

  /**
   * override this method to perform detailed formatting in subclasses
   */
  protected String formatValueInternal(T validValue) {
    return validValue != null ? validValue.toString() : "";
  }

  @Override
  public String getDisplayText() {
    return propertySupport.getPropertyString(PROP_DISPLAY_TEXT);
  }

  @Override
  public void setDisplayText(String s) {
    propertySupport.setPropertyStringAlwaysFire(PROP_DISPLAY_TEXT, s);
  }

  @Override
  public boolean isAutoDisplayText() {
    return propertySupport.getPropertyBool(PROP_AUTO_DISPLAY_TEXT);
  }

  @Override
  public void setAutoDisplayText(boolean b) {
    propertySupport.setPropertyBool(PROP_AUTO_DISPLAY_TEXT, b);
  }

  @Override
  public boolean isAutoAddDefaultMenus() {
    return propertySupport.getPropertyBool(PROP_AUTO_ADD_DEFAULT_MENUS);
  }

  @Override
  public void setAutoAddDefaultMenus(boolean b) {
    propertySupport.setPropertyBool(PROP_AUTO_ADD_DEFAULT_MENUS, b);
  }

  /**
   * Full override: set my value to null
   */
  @Override
  protected void execChangedMasterValue(Object newMasterValue) throws ProcessingException {
    setValue(null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<T> getHolderType() {
    return TypeCastUtility.getGenericsParameterClass(getClass(), IHolder.class);
  }

  public void updateFrom(IHolder<T> other) {
    setValue(other.getValue());
  }

  @Override
  public boolean isContentValid() {
    boolean b = super.isContentValid();
    if (b) {
      if (isMandatory()) {
        if (getValue() == null) {
          return false;
        }
      }
    }
    return b;
  }

}
