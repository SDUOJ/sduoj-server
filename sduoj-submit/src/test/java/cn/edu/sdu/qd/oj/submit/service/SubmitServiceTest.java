package cn.edu.sdu.qd.oj.submit.service;

import cn.edu.sdu.qd.oj.SubmitApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SubmitApplication.class)
public class SubmitServiceTest {

    @Autowired
    private SubmitService submitService;

    @Test
    public void createSubmission() {
    }
}
