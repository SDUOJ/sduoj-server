package cn.edu.sdu.qd.oj.email.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer object for email sending in MQ
 * @author zhangt2333
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmailMessageDTO extends BaseDTO {

    private String subject;

    private String to;

    private String text;

}
