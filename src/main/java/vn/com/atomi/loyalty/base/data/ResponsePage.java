package vn.com.atomi.loyalty.base.data;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

/**
 * @author haidv
 * @version 1.0
 */
@Getter
@NoArgsConstructor
public class ResponsePage<T> {

  private int pageNo;

  private int pageSize;

  private long totalCount;

  private int totalPage;

  private List<T> data;

  public ResponsePage(int pageNo, int pageSize, long totalCount, int totalPage, List<T> data) {
    this.pageNo = pageNo;
    this.pageSize = pageSize;
    this.totalCount = totalCount;
    this.totalPage = totalPage;
    this.data = data;
  }

  public ResponsePage(Page<T> page) {
    this(page, page.getContent());
  }

  @SuppressWarnings("rawtypes")
  public ResponsePage(Page page, List<T> data) {
    this(page.getNumber() + 1, page.getSize(), page.getTotalElements(), page.getTotalPages(), data);
  }
}
