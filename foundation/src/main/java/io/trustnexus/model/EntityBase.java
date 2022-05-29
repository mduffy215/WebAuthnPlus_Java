/*
 * (c) Copyright 2022 ~ Trust Nexus, Inc.
 * All technologies described here in are "Patent Pending". 
 * License information:  http://www.trustnexus.io/license.htm
 * 
 * AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
 * OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
 * 
 * Report License Violations:  trustnexus.io@austin.rr.com
 * 
 * This is a beta version:  0.0.1
 * Suggestions for code improvements:  trustnexus.io@austin.rr.com
 */

package io.trustnexus.model;

import java.sql.Timestamp;

public class EntityBase {

  private Timestamp created;
  private Timestamp updated;
  private Integer dataSourceTypeValue;
  private Integer updatedById;

  // ------------------------------------------------------------------------------------------------------------------

  public EntityBase() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Timestamp getUpdated() {
    return updated;
  }

  public void setUpdated(Timestamp updated) {
    this.updated = updated;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Integer updatedById) {
    this.updatedById = updatedById;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getDataSourceTypeValue() {
    return dataSourceTypeValue;
  }

  public void setDataSourceTypeValue(Integer dataSourceTypeValue) {
    this.dataSourceTypeValue = dataSourceTypeValue;
  }
}







