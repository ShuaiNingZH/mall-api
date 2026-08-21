package com.atguigu.meet.mapper.permission.invite;

import com.atguigu.meet.model.entity.permission.invite.SysInviteCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * 邀请码 Mapper
 */
public interface SysInviteCodeMapper extends BaseMapper<SysInviteCode> {

    /**
     * 查询 sys_invite_code 中 seq 的最大值（包含逻辑删除行）
     * <p>
     * 使用原生 SQL 绕开 MyBatis-Plus @TableLogic 过滤，因为逻辑删除的行其 seq 也已"消费"，
     * 启动同步时必须包含，否则下次发号可能撞到已删除行的 invite_code 唯一索引。
     *
     * @return 最大 seq；表为空时返回 null
     */
    @Select("SELECT MAX(seq) FROM sys_invite_code")
    Long selectMaxSeq();
}
