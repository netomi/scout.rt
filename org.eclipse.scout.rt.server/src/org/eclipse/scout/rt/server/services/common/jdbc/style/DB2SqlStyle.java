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
package org.eclipse.scout.rt.server.services.common.jdbc.style;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.scout.rt.server.services.common.jdbc.SqlBind;

public class DB2SqlStyle extends AbstractSqlStyle {

  private static final long serialVersionUID = 1L;

  @Override
  public String getConcatOp() {
    return "||";
  }

  @Override
  public String getLikeWildcard() {
    return "%";
  }

  @Override
  protected int getMaxListSize() {
    return 1000;
  }

  public boolean isLargeString(String s) {
    return (s.length() > 4000);
  }

  public boolean isBlobEnabled() {
    return true;
  }

  public boolean isClobEnabled() {
    return true;
  }

  public void testConnection(Connection conn) throws SQLException {
  }

  @Override
  public void writeBind(PreparedStatement ps, int jdbcBindIndex, SqlBind bind) throws SQLException {
    super.writeBind(ps, jdbcBindIndex, bind);
  }

  @Override
  protected String adaptBindNameTimeDateOp(String bindName) {
    return " TO_NUMBER(" + adaptBindName(bindName) + ") ";
  }
}
