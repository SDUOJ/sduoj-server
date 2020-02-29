/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

import cn.edu.sdu.qd.oj.auth.entity.UserInfo;
import cn.edu.sdu.qd.oj.auth.utils.JwtUtils;
import cn.edu.sdu.qd.oj.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @ClassName JwtTest
 * @Description TODO
 * @Author zhangt2333
 * @Date 2020/2/27 13:53
 * @Version V1.0
 **/

public class JwtTest {

    private static final String pubKeyPath = "C:\\Users\\tttt\\Desktop\\tempProjects\\rsa.pub";

    private static final String priKeyPath = "C:\\Users\\tttt\\Desktop\\tempProjects\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        // 测该方法前，注释掉`testGetRsa()`的Before注解
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "2333");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(1000, "tttt"), privateKey, 5);
        System.out.println("String token = \"" + token + "\";");
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MTAwMCwiYWNjb3VudCI6InR0dHQiLCJleHAiOjE1ODI3ODM2NDV9.sCk3nzIJQhK2NfY1hmbLM-AVN6HYpf3Frt9sPYLhb02dZ3KSuyPTsphCEH_YITrFW9lI58rGS4eukmur02Q0BbOkrz7Hi8jUICmSBmGHwxTfoZVCHgrSx8Jr_4u5f3mFz7kxZuGNhYt8VsvLSJwoRZCnPOLznjOAud_6Zt8zgKE";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("username: " + user.getUsername());
    }
}