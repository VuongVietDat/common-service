package vn.com.atomi.loyalty.common.event;

import lombok.NoArgsConstructor;
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

  @SuppressWarnings("unused")
  private ApplicationContext applicationContext;

  @SuppressWarnings("unused")
  private ThreadPoolTaskExecutor taskExecutor;

  @Autowired
  private ServiceDrivenEventListener(
      ApplicationContext applicationContext,
      @Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor taskExecutor) {
    super(applicationContext, taskExecutor);
    this.applicationContext = applicationContext;
    this.taskExecutor = taskExecutor;
  }

  @Override
  protected void processHandleErrorEventAsync(EventInfo eventInfo) {}

  @Override
  protected void processLogHandleEventAsync(EventInfo eventInfo) {}
}
