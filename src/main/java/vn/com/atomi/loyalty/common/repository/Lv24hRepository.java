package vn.com.atomi.loyalty.common.repository;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.common.utils.Constants;

@Repository
public class Lv24hRepository {
  private final JdbcTemplate template;

  private static final String pattern =
      """
              SELECT cus.*,
                  cc.CUST_NO CIF_WALLET,
                  mu.USER_NAME,
                  mu.CUST_NO, -- CIF NO
                  mu.DATE_OF_BIRTH,
                  mu.SEX,
                  mu.FULL_ADDRESS,
                  mu.USER_TYPE , -- KHDN = 9
                  mu.USER_STATUS FROM (
              SELECT
                  c.CUSTOMER_ID,
                  c.CUSTOMER_NO,	--cif
                  c.CUSTOMER_NAME,	--cif
                  c.PACKAGE_DEFAULT , -- Goi Khach hang
                  c.UNIQUE_ID, -- Loai giay to
                  c.UNIQUE_VALUE, -- So giay to
                  c.DATE_OF_ISSUE, -- Ngay cap
                  c.PLACE_OF_ISSUE, -- Noi cap
                  c.NATIONALITY_ID, -- Quoc tich
                  c.MOBILE_PHONE, -- So dien thoai
                  c.BRANCH_CODE,
                  c.REG_BRANCH,
                  firstU.USER_ID
              from CUSTOMER c
              join (
                  select CUST_NO, max(USER_ID) USER_ID
                  from MASTER_USER
                  WHERE USER_STATUS != 'C'
                  group by CUST_NO
                  ) firstU
              on firstU.CUST_NO = c.CUSTOMER_NO
              ORDER BY c.CUSTOMER_ID
              ) cus JOIN MASTER_USER mu ON cus.USER_ID = mu.USER_ID
              JOIN CORE_CUSTOMER cc ON cus.CUSTOMER_NO = cc.CIF_NO
              WHERE CUSTOMER_ID > %d AND ROWNUM <= %d
              """;

  public Lv24hRepository(
      @Value("${custom.lv24h.datasource.url}") String url,
      @Value("${custom.lv24h.datasource.username}") String username,
      @Value("${custom.lv24h.datasource.password}") String password) {
    template =
        new JdbcTemplate(
            DataSourceBuilder.create().url(url).username(username).password(password).build());
  }

  public List<Map<String, Object>> selects(long lastCustomerId) {
    Logger logger = LoggerFactory.getLogger(Lv24hRepository.class);
    String query = String.format(pattern, lastCustomerId, Constants.BATCH_SIZE);
    List<Map<String, Object>> results = template.queryForList(query);
    logger.info("Query executed successfully. Number of records fetched: {}", results.size());
    return results;
  }
}
