package com.atguigu.meet.controller.notice;

import com.atguigu.meet.annotation.RequirePermission;
import com.atguigu.meet.common.Response;
import com.atguigu.meet.constant.PermissionConst;
import com.atguigu.meet.model.dto.notice.NoticePageQueryDTO;
import com.atguigu.meet.model.dto.notice.NoticeSaveDTO;
import com.atguigu.meet.model.dto.notice.NoticeUpdateDTO;
import com.atguigu.meet.service.notice.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告管理接口
 */
@RestController
@RequestMapping("/notices")
@Validated
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /** 公告分页列表 */
    @GetMapping
    @RequirePermission(PermissionConst.NOTICE_QUERY)
    public Response getPageList(@Valid NoticePageQueryDTO parameter) {
        return noticeService.getPageList(parameter);
    }

    /** 所有启用公告（C端展示/下拉用） */
    @GetMapping("/enabled")
    @RequirePermission(PermissionConst.NOTICE_QUERY)
    public Response getAllEnabledNotices() {
        return noticeService.getAllEnabledNotices();
    }

    /** 根据ID查公告（含阅读次数） */
    @GetMapping("/{id}")
    @RequirePermission(PermissionConst.NOTICE_QUERY)
    public Response getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    /** 新增公告 */
    @PostMapping
    @RequirePermission(PermissionConst.NOTICE_ADD)
    public Response addNotice(@RequestBody @Valid NoticeSaveDTO dto) {
        return noticeService.addNotice(dto);
    }

    /** 修改公告 */
    @PutMapping
    @RequirePermission(PermissionConst.NOTICE_UPDATE)
    public Response updateNotice(@RequestBody @Valid NoticeUpdateDTO dto) {
        return noticeService.updateNotice(dto);
    }

    /** 删除公告（逻辑删除） */
    @DeleteMapping("/{id}")
    @RequirePermission(PermissionConst.NOTICE_DELETE)
    public Response deleteNotice(@PathVariable Long id) {
        return noticeService.deleteNotice(id);
    }
}
