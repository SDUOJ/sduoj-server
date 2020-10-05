package cn.edu.sdu.qd.oj.contest.entity;

import cn.edu.sdu.qd.oj.common.entity.BaseDO;
import cn.edu.sdu.qd.oj.contest.converter.ContestConvertUtils;
import cn.edu.sdu.qd.oj.contest.dto.ContestProblemListDTO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(ContestDOField.TABLE_NAME)
public class ContestDO extends BaseDO {

    @TableId(value = ContestDOField.ID, type = IdType.AUTO)
    private Long contestId;

    @TableField(value = ContestDOField.GMT_CREATE, fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = ContestDOField.GMT_MODIFIED, fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    @TableField(ContestDOField.FEATURES)
    private String features;

    @TableField(ContestDOField.DELETED)
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @TableField(ContestDOField.VERSION)
    private Integer version;

    @TableField(ContestDOField.TITLE)
    private String contestTitle;

    @TableField(ContestDOField.USER_ID)
    private Long userId;

    @TableField(ContestDOField.GMT_START)
    private Date gmtStart;

    @TableField(ContestDOField.GMT_END)
    private Date gmtEnd;

    @TableField(ContestDOField.PASSWORD)
    private String password;

    @TableField(ContestDOField.SOURCE)
    private String source;

    @TableField(ContestDOField.PARTICIPANT_NUM)
    private Integer participantNum;

    @TableField(ContestDOField.MARKDOWN_DESCRIPTION)
    private String markdownDescription;

    @TableField(ContestDOField.PROBLEMS)
    private String problems;     // problemId problemWeight

    @TableField(ContestDOField.PARTICIPANTS)
    private byte[] participants; // userId 列表


    /**
    * @Description 加入一个比赛参与者，已存在则加入失败
    **/
    public boolean addOneParticipant(long userId) {
        List<Long> participantsUserIdList = ContestConvertUtils.participantsToUserIdList(participants);
        if (participantsUserIdList.contains(userId)) {
            return false;
        }
        participantsUserIdList.add(userId);
        participants = ContestConvertUtils.participantsFromUserIdList(participantsUserIdList);
        participantNum = participantsUserIdList.size();
        return true;
    }

    /**
    * @Description 判断用户是否在比赛内
    **/
    public boolean containsUserIdInParticipants(long userId) {
        List<Long> participantsUserIdList = ContestConvertUtils.participantsToUserIdList(participants);
        return participantsUserIdList.contains(userId);
    }

    /**
    * @Description 通过下标获取比赛中题目的 problemCode，下标从 1 开始算
    */
    public ContestProblemListDTO getProblemCodeByIndex(Integer problemIndex) {
        if (problemIndex == null) {
            return null;
        }
        List<ContestProblemListDTO> contestProblemListDTOList = ContestConvertUtils.problemsTo(problems);
        if (problemIndex > contestProblemListDTOList.size() || problemIndex < 1) {
            return null;
        }
        return contestProblemListDTOList.get(problemIndex-1);
    }
}
