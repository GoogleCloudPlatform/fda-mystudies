/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.ClientInfoBO;
import com.google.cloud.healthcare.fdamystudies.repository.ClientInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SessionRepository;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@Service
public class ClientInfoServiceImpl implements ClientService {
  private static final Logger logger = LoggerFactory.getLogger(ClientInfoServiceImpl.class);

  @Autowired private ClientInfoRepository clientInfoRepo;

  @Autowired private SessionRepository sessionRepository;

  @Override
  public boolean isValidClient(String clientToken, String userId) throws SystemException {

    logger.info("ClientInfoServiceImpl isValidClient() - starts");
    try {
      if (userId != null) {
        AuthInfoBO sessionDetails = sessionRepository.findByUserId(userId);
        if (sessionDetails != null) {
          if (clientToken.equals(sessionDetails.getClientToken())) {
            logger.info("ClientInfoServiceImpl checkClientInfo().....VERIFIED as true");
            return true;
          } else return false;
        } else return false;
      }
    } catch (Exception e) {
      logger.error("ClientInfoServiceImpl checkClientInfo() - error ", e);
      throw new SystemException();
    }
    logger.info("ClientInfoServiceImpl checkClientInfo() - ends ");
    return false;
  }

  @Override
  public String checkClientInfo(String clientId, String secretKey) throws SystemException {
    logger.info("ClientInfoServiceImpl checkClientInfo() - starts");
    try {
      if (clientId != null && secretKey != null) {
        ClientInfoBO clientInfo = clientInfoRepo.findByClientId(clientId);
        if (clientInfo != null) {
          String hashedSecretkey = MyStudiesUserRegUtil.getHashedValue(clientInfo.getSecretKey());

          if (clientId.equals(clientInfo.getClientId()) && secretKey.equals(hashedSecretkey)) {
            logger.info("ClientInfoServiceImpl checkClientInfo().....VERIFIED as true");
            return clientInfo.getAppCode();
          } else return null;
        } else return null;
      }
    } catch (Exception e) {
      logger.error("ClientInfoServiceImpl checkClientInfo() - error ", e);
      throw new SystemException();
    }
    logger.info("ClientInfoServiceImpl checkClientInfo() - ends ");
    return null;
  }
}
