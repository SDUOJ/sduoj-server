package cn.edu.sdu.qd.oj.user.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserExtensionDTO extends BaseDTO {

    private Long id;

    private Long userId;

    private String key;

    private String value;
}
