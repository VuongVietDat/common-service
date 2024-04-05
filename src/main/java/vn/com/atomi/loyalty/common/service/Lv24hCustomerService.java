package vn.com.atomi.loyalty.common.service;

import vn.com.atomi.loyalty.common.dto.message.Lv24hCustomerMessage;

public interface Lv24hCustomerService {
  int etl();

  void syncFromQueue(Lv24hCustomerMessage message, String messageId);
}
