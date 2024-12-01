package vn.com.atomi.loyalty.common.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import vn.com.atomi.loyalty.base.constant.DateConstant;
import vn.com.atomi.loyalty.common.enums.PointType;
import vn.com.atomi.loyalty.common.enums.Status;

import java.time.LocalDate;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RulePOCOutput {
    @Schema(description = "ID bản ghi")
    private Long id;

    @Schema(description = "Ma cong thuc")
    private String code;

    @Schema(description = "Ten cong thuc")
    private String name;

    @Schema(description = "Ap dung cho loai giao dich")
    private String type;

    @Schema(description = "So tien giao dich toi thieu")
    private Long minTransaction;

    @Schema(description = "So tien quy dinh")
    private Long exchangeValue;

    @Schema(description = "So diem quy doi tuong ung")
    private Long exchangePoint;

    @Schema(description = "Limit diem cho giao dich")
    private Long limitPointPerTransaction;

    @Schema(description = "Limit diem cho 1 user tren he thong")
    private Long limitPointPerUser;

    @Schema(description = "Limit diem cho 1 user tren 1 su kien")
    private Long limitEventPerUser;

    @Schema(
            description =
                    "Loại điểm:</br> ALL: Tất cả loại điẻm</br> RANK_POINT: Điểm xếp hạng</br> CONSUMPTION_POINT: Điểm tích lũy dùng để tiêu dùng")
    private PointType pointType;

    @Schema(description = "Ngày bắt đầu hiệu lực (dd/MM/yyyy)", example = "01/01/2024")
    @JsonFormat(pattern = DateConstant.STR_PLAN_DD_MM_YYYY_STROKE)
    private LocalDate startDate;

    @Schema(description = "Ngày kết thúc hiệu lực (dd/MM/yyyy)", example = "31/12/2024")
    @JsonFormat(pattern = DateConstant.STR_PLAN_DD_MM_YYYY_STROKE)
    private LocalDate endDate;

    @Schema(description = "Trạng thái:</br> ACTIVE: Hiệu lực</br> INACTIVE: Không hiệu lực")
    private Status status;

    @Schema(description = "Id chien dich")
    private Long campaignId;

    @Schema(description = "Ma chien dich")
    private String campaignCode;

    @Schema(description = "Id ngan sach")
    private Long budgetId;

    @Schema(description = "Ma ngan sach")
    private String budgetCode;
}
