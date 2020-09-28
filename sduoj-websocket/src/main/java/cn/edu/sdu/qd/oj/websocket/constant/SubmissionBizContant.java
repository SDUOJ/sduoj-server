package cn.edu.sdu.qd.oj.websocket.constant;

public class SubmissionBizContant {

    public static final String REDIS_CHANNEL_PATTERN = "/submission/*";

    public static final int REDIS_SUBMISSION_RESULT_EXPIRE = 30;

    public static String getRedisSubmissionKey(String submissionId) {
        return "submission:" + submissionId;
    }

    public static String getRedisChannelKey(String submissionId) {
        return "/submission/" + submissionId;
     }


}
