package cn.edu.sdu.qd.oj.user.dto;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserListReqDTO extends BaseDTO {
    private int pageNow;
    private int pageSize;

    private String username;
    private String studentId;
    private String phone;
    private String email;


}
