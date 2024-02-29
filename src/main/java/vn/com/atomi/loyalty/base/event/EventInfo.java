package vn.com.atomi.loyalty.base.event;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public class EventInfo {

  public static final int DEFAULT_RETRY_EVENT = 3;

  private final String id;

  private final Object what;

  private final CoreEvent event;

  private final boolean isSync;

  private final int retry;

  public EventInfo(Object what, CoreEvent event) {
    this(what, event, DEFAULT_RETRY_EVENT);
  }

  public EventInfo(Object what, CoreEvent event, int retry) {
    this(what, false, event, retry);
  }

  public EventInfo(Object what, boolean isSync, CoreEvent event) {
    this(what, isSync, event, DEFAULT_RETRY_EVENT);
  }

  public EventInfo(Object what, boolean isSync, CoreEvent event, int retry) {
    this(UUID.randomUUID().toString(), what, event, isSync, retry);
  }
}
