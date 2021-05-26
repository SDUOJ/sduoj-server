package cn.edu.sdu.qd.oj.user.enums;

import lombok.AllArgsConstructor;

/**
 * third party for login
 * @author zhangt2333
 */
@AllArgsConstructor
public enum ThirdPartyEnum {

    SDUCAS("SDUCAS"),
    QQ("QQ"),
    WECHAT("WECHAT"),

    ;

    // 全大写
    public String name;

    public static ThirdPartyEnum of(String name) {
        for (ThirdPartyEnum value : ThirdPartyEnum.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
