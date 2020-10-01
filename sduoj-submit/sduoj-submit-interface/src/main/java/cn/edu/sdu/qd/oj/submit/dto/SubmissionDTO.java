/**
 * The GNU General Public License
 * Copyright (c) 2020-2020 zhangt2333@gmail.com
 **/

package cn.edu.sdu.qd.oj.submit.dto;

import cn.edu.sdu.qd.oj.common.util.DateToTimestampSerializer;
import cn.edu.sdu.qd.oj.common.entity.BaseDTO;
import cn.edu.sdu.qd.oj.common.util.HexStringToLongDeserializer;
import cn.edu.sdu.qd.oj.common.util.LongToHexStringSerializer;
import cn.edu.sdu.qd.oj.submit.util.BytesToCheckpointResultsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;

/**
 * @Author zhangt2333
 * @Date 2020/3/6 16:03
 * @Version V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubmissionDTO extends BaseDTO {

    @JsonSerialize(using = LongToHexStringSerializer.class)
    @JsonDeserialize(using = HexStringToLongDeserializer.class)
    private Long submissionId;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer isPublic;

    private Integer valid;

    private Long problemId;

    private Long userId;

    private String language;

    private Integer judgeResult;

    private Integer judgeScore;

    private Integer usedTime;

    private Integer usedMemory;

    private Integer codeLength;

    private String judgeLog;

    private String code;

    @JsonSerialize(using = BytesToCheckpointResultsSerializer.class)
    private byte[] checkpointResults;

    // -----------------------

    private Integer checkpointNum;

    private String username;
}