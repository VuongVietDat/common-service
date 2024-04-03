package vn.com.atomi.loyalty.common.event;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import vn.com.atomi.loyalty.base.event.DrivenEventListener;
import vn.com.atomi.loyalty.base.event.EventInfo;

/**
 * @author haidv
 * @version 1.0
 */
@NoArgsConstructor
@Service
public class ServiceDrivenEventListener extends DrivenEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDrivenEventListener.class);

  @Autowired
  private ServiceDrivenEventListener(
      ApplicationContext applicationContext,
      @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
    super(applicationContext, taskExecutor);
  }

  @Override
  protected void processHandleErrorEventAsync(EventInfo eventInfo) {}

  @Override
  protected void processLogHandleEventAsync(EventInfo eventInfo) {}
}
