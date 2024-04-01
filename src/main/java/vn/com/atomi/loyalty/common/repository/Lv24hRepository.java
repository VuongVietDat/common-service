package vn.com.atomi.loyalty.common.repository;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Lv24hRepository {
  private final JdbcTemplate template;

  public Lv24hRepository() {
    template =
        new JdbcTemplate(
            DataSourceBuilder.create()
                .url("jdbc:oracle:thin:@10.36.209.95:1521:lv24uat2")
                .username("ESMAC_VV_TE")
                .password("esmac_vv_te")
                .build());
  }

  public void selects() {
    var list =
        template.queryForList(
            """
                SELECT cus.*,
                    mu.USER_NAME,
                    mu.CUST_NO, -- CIF NO
                    mu.DATE_OF_BIRTH,
                    mu.SEX,
                    mu.FULL_ADDRESS,
                    mu.USER_TYPE , -- KHDN = 9
                    mu.USER_STATUS FROM (
                SELECT
                    c.CUSTOMER_ID,
                    c.CUSTOMER_NO cif,	--cif
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
                    select CUST_NO, max(CUST_ID) USER_ID
                    from MASTER_USER
                    WHERE USER_STATUS != 'C'
                    group by CUST_NO
                    ) firstU
                on (firstU.CUST_NO = c.CUSTOMER_NO)
                WHERE c.CUSTOMER_ID > 56345 AND ROWNUM <= 10
                ORDER BY c.CUSTOMER_ID
                ) cus JOIN MASTER_USER mu ON cus.cif = mu.CUST_NO
                """);
    System.out.println("zxc " + list.size());
  }
  /*public void selects() {
    template.query(
        """
            SELECT COUNT(*) FROM
            	MASTER_USER mu,
            	CUSTOMER c
            WHERE mu.CUST_NO = c.customer_no
            AND mu.USER_STATUS != 'C'
            """,
        rs -> {
          System.out.println("zxc " + rs);
        });
  }*/
}
