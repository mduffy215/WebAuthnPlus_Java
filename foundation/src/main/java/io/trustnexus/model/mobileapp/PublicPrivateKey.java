/*
 * (c) Copyright 2021 ~ Trust Nexus, Inc.
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

package io.trustnexus.model.mobileapp;

import io.trustnexus.model.EntityBase;

public class PublicPrivateKey extends EntityBase {

  private Integer publicPrivateKeyId;

  private String keyOwner;
  private String publicKeyHex;
  private String privateKeyHex;
  private boolean inactiveFlag;
  private String uuid;

  // ------------------------------------------------------------------------------------------------------------------

  public PublicPrivateKey() {
  }

  // ------------------------------------------------------------------------------------------------------------------

  public Integer getPublicPrivateKeyId() {
    return publicPrivateKeyId;
  }

  public void setPublicPrivateKeyId(Integer publicPrivateKeyId) {
    this.publicPrivateKeyId = publicPrivateKeyId;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getKeyOwner() {
    return keyOwner;
  }

  public void setKeyOwner(String keyOwner) {
    this.keyOwner = keyOwner;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPublicKeyHex() {
    return publicKeyHex;
  }

  public void setPublicKeyHex(String publicKey) {
    this.publicKeyHex = publicKey;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getPrivateKeyHex() {
    return privateKeyHex;
  }

  public void setPrivateKeyHex(String privateKey) {
    this.privateKeyHex = privateKey;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public boolean isInactiveFlag() {
    return inactiveFlag;
  }

  public void setInactiveFlag(boolean inactiveFlag) {
    this.inactiveFlag = inactiveFlag;
  }

  // ------------------------------------------------------------------------------------------------------------------

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  // ------------------------------------------------------------------------------------------------------------------

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (inactiveFlag ? 1231 : 1237);
    result = prime * result + ((keyOwner == null) ? 0 : keyOwner.hashCode());
    result = prime * result + ((privateKeyHex == null) ? 0 : privateKeyHex.hashCode());
    result = prime * result + ((publicKeyHex == null) ? 0 : publicKeyHex.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  // ------------------------------------------------------------------------------------------------------------------

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PublicPrivateKey other = (PublicPrivateKey) obj;
    if (inactiveFlag != other.inactiveFlag)
      return false;
    if (keyOwner == null) {
      if (other.keyOwner != null)
        return false;
    } else if (!keyOwner.equals(other.keyOwner))
      return false;
    if (privateKeyHex == null) {
      if (other.privateKeyHex != null)
        return false;
    } else if (!privateKeyHex.equals(other.privateKeyHex))
      return false;
    if (publicKeyHex == null) {
      if (other.publicKeyHex != null)
        return false;
    } else if (!publicKeyHex.equals(other.publicKeyHex))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  // ------------------------------------------------------------------------------------------------------------------

  @Override
  public String toString() {
    return "\n      publicPrivateKeyId: " + publicPrivateKeyId + 
           "\n                 created: " + getCreated() + 
           "\n                 updated: " + getUpdated() + 
           "\n             updatedById: " + getUpdatedById() + 
           "\n     dataSourceTypeValue: " + getDataSourceTypeValue() + 
           "\n                keyOwner: " + keyOwner + 
         "\n\n            publicKeyHex: " + publicKeyHex + 
         "\n\n           privateKeyHex: " + privateKeyHex + 
         "\n\n            inactiveFlag: " + inactiveFlag + 
           "\n                    uuid: " + uuid;
  }
}







