/*******************************************************************************
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.job;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.scout.commons.Assertions;
import org.eclipse.scout.commons.ToStringBuilder;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.context.ClientContext;
import org.eclipse.scout.rt.platform.job.JobInput;
import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.ui.UserAgent;

/**
 * Describes a client-side job with context information to be applied onto the executing worker thread during the time
 * of the job's execution.
 * <p/>
 * The 'setter-methods' returns <code>this</code> in order to support for method chaining. The context has the following
 * characteristics:
 *
 * @see ClientContext
 * @see ClientJobs
 * @see IJobManager
 * @since 5.1
 */
public class ClientJobInput extends JobInput<ClientContext> {

  protected ClientJobInput(final JobInput origin) {
    super(origin);
  }

  @Override
  public ClientJobInput setId(final String id) {
    return (ClientJobInput) super.setId(id);
  }

  @Override
  public ClientJobInput setName(final String name) {
    return (ClientJobInput) super.setName(name);
  }

  @Override
  public ClientJobInput setMutex(final Object mutexObject) {
    Assertions.assertFalse(mutexObject instanceof ISession, "The session cannot be used as mutex object to not interfere with model jobs");
    return (ClientJobInput) super.setMutex(mutexObject);
  }

  @Override
  public ClientJobInput setExpirationTime(final long time, final TimeUnit timeUnit) {
    return (ClientJobInput) super.setExpirationTime(time, timeUnit);
  }

  @Override
  public ClientJobInput setContext(final ClientContext context) {
    return (ClientJobInput) super.setContext(context);
  }

  @Override
  public ClientJobInput setSubject(final Subject subject) {
    return (ClientJobInput) super.setSubject(subject);
  }

  @Override
  public ClientJobInput setLocale(final Locale locale) {
    return (ClientJobInput) super.setLocale(locale);
  }

  public IClientSession getSession() {
    return getContext().getSession();
  }

  /**
   * Set the session and its Locale and UserAgent as derived values.
   */
  public ClientJobInput setSession(final IClientSession session) {
    getContext().setSession(session);
    return this;
  }

  public boolean isSessionRequired() {
    return getContext().isSessionRequired();
  }

  /**
   * Set to <code>false</code> if the context does not require a session. By default, a session is required.
   */
  public ClientJobInput setSessionRequired(final boolean sessionRequired) {
    getContext().setSessionRequired(sessionRequired);
    return this;
  }

  public UserAgent getUserAgent() {
    return getContext().getUserAgent();
  }

  public ClientJobInput setUserAgent(final UserAgent userAgent) {
    getContext().setUserAgent(userAgent);
    return this;
  }

  @Override
  public String getThreadName() {
    return "scout-client-thread";
  }

  // === construction methods ===

  @Override
  public ClientJobInput copy() {
    return new ClientJobInput(this).setContext(getContext().copy());
  }

  public static ClientJobInput defaults() {
    final ClientJobInput defaults = new ClientJobInput(JobInput.defaults());
    defaults.setContext(ClientContext.defaults());
    defaults.setSessionRequired(true);
    return defaults;
  }

  public static ClientJobInput empty() {
    final ClientJobInput empty = new ClientJobInput(JobInput.empty());
    empty.setContext(ClientContext.empty());
    empty.setSessionRequired(true);
    return empty;
  }

  @Override
  public String toString() {
    final ToStringBuilder builder = new ToStringBuilder(this);
    builder.attr("id", getId());
    builder.attr("name", getName());
    builder.ref("session", getSession());
    return builder.toString();
  }
}
