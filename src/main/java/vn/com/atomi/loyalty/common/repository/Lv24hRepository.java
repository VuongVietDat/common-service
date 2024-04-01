package vn.com.atomi.loyalty.common.repository;

import java.util.HashMap;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import vn.com.atomi.loyalty.base.utils.JsonUtils;
import vn.com.atomi.loyalty.common.event.MessageData;

@Repository
public class Lv24hRepository {
  private final JdbcTemplate template;

  @SuppressWarnings("rawtypes")
  private final KafkaTemplate kafkaTemplate;

  @SuppressWarnings("rawtypes")
  public Lv24hRepository(KafkaTemplate kafkaTemplate) {
    template =
        new JdbcTemplate(
            DataSourceBuilder.create()
                .url("jdbc:oracle:thin:@10.36.209.95:1521:lv24uat2")
                .username("ESMAC_VV_TE")
                .password("esmac_vv_te")
                .build());

    this.kafkaTemplate = kafkaTemplate;
  }

  @SuppressWarnings({"unchecked"})
  public void selects(long customerId) {
    var list =
        template.queryForList(
            String.format(
                """
                SELECT cus.*,
                    cc.CIF_NO,
                	mu.USER_NAME,
                	mu.CUST_NO, -- CIF NO
                	mu.DATE_OF_BIRTH,
                	mu.SEX,
                	mu.FULL_ADDRESS,
                	mu.USER_TYPE, -- KHDN = 9
                	mu.USER_STATUS FROM (
                SELECT
                	c.CUSTOMER_ID,
                	c.CUSTOMER_NO,	--cif
                	c.CUSTOMER_NAME,
                	c.PACKAGE_DEFAULT , -- Goi Khach hang
                	c.UNIQUE_ID, -- Loai giay to
                	c.UNIQUE_VALUE, -- So giay to
                	c.DATE_OF_ISSUE, -- Ngay cap
                	c.PLACE_OF_ISSUE, -- Noi cap
                	c.NATIONALITY_ID, -- Quoc tich
                	c.MOBILE_PHONE, -- So dien thoai
                	c.BRANCH_CODE,
                	firstU.USER_ID
                from CUSTOMER c
                join (
                    select CUST_NO, max(USER_ID) USER_ID
                    from MASTER_USER
                    WHERE USER_STATUS != 'C'
                    group by CUST_NO
                    ) firstU
                on (firstU.CUST_NO = c.CUSTOMER_NO)
                WHERE c.CUSTOMER_ID > %d AND ROWNUM <= 10
                ORDER BY c.CUSTOMER_ID
                ) cus JOIN MASTER_USER mu ON cus.USER_ID = mu.USER_ID
                JOIN CORE_CUSTOMER cc ON cus.CUSTOMER_NO = cc.CUST_NO
                """,
                customerId));

    var mapping =
        new HashMap<String, String>() {
          {
            put("CUSTOMER_NO", "cifBank");
            put("CIF_NO", "cifWallet");
            put("CUSTOMER_NAME", "customerName");
            put("DATE_OF_BIRTH", "dob");
            put("FULL_ADDRESS", "currentAddress");
            put("USER_TYPE", "customerType");
            put("SEX", "gender");
            put("NATIONALITY_ID", "nationality");
            put("BRANCH_CODE", "ownerBranch");
            put("MOBILE_PHONE", "phone");
            put("BRANCH_CODE", "registerBranch");
            put("FULL_ADDRESS", "residentialAddress");
            put("UNIQUE_ID", "uniqueType");
            put("UNIQUE_VALUE", "uniqueValue");
            put("DATE_OF_ISSUE", "issueDate");
            put("PLACE_OF_ISSUE", "issuePlace");
              //            put("", "rank");
              //            put("", "rmCode");
              //            put("", "rmName");
              //            put("", "segment");
          }
        };

    var msgData =
        new MessageData<>(
            list.stream()
                .map(
                    map -> {
                      System.out.println("zxc " + map);
                      return new HashMap<String, Object>() {
                        {
                          map.forEach(
                              (s, o) -> {
                                if (o != null) {
                                  var key = mapping.get(s);
                                  if (key != null) put(key, o);
                                }
                              });
                        }
                      };
                    })
                .toList());

    kafkaTemplate.send("CUSTOMER_CREATE_EVENT", JsonUtils.toJson(msgData));
  }
}
