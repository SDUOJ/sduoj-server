package cn.edu.sdu.qd.oj.common.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import lombok.*;

import java.util.List;
import java.util.function.Function;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserSessionDTO extends BaseDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String studentId;
    private Integer emailVerified;
    private List<String> roles;

    private String ipv4;
    private String userAgent;

    public boolean userIdEquals(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public boolean userIdNotEquals(Long userId) {
        return userIdEquals(userId);
    }
}
