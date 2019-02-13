package com.yuhangTao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "users_report")
@ApiModel(value = "举报用户对象",description = "这是举报用户对象")
public class UsersReport {
    @Id
    @ApiModelProperty(hidden = true)
    private String id;

    /**
     * 被举报用户id
     */
    @Column(name = "deal_user_id")
    @ApiModelProperty(value = "被举报用户id",name = "dealUserId",dataType = "String",required = true)
    private String dealUserId;

    @Column(name = "deal_video_id")
    @ApiModelProperty(value = "被举报视频id",name = "dealVideoId",dataType = "String",required = true)
    private String dealVideoId;

    /**
     * 类型标题，让用户选择，详情见 枚举
     */
    @ApiModelProperty(value = "举报类型标题",name = "title",dataType = "String",required = true)
    private String title;

    /**
     * 内容
     */
    @ApiModelProperty(hidden = true)
    private String content;

    /**
     * 举报人的id
     */
    @ApiModelProperty(value = "举报人的id",name = "userid",dataType = "String",required = true)
    private String userid;

    /**
     * 举报时间
     */
    @Column(name = "create_date")
    @ApiModelProperty(hidden = true)
    private Date createDate;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取被举报用户id
     *
     * @return deal_user_id - 被举报用户id
     */
    public String getDealUserId() {
        return dealUserId;
    }

    /**
     * 设置被举报用户id
     *
     * @param dealUserId 被举报用户id
     */
    public void setDealUserId(String dealUserId) {
        this.dealUserId = dealUserId;
    }

    /**
     * @return deal_video_id
     */
    public String getDealVideoId() {
        return dealVideoId;
    }

    /**
     * @param dealVideoId
     */
    public void setDealVideoId(String dealVideoId) {
        this.dealVideoId = dealVideoId;
    }

    /**
     * 获取类型标题，让用户选择，详情见 枚举
     *
     * @return title - 类型标题，让用户选择，详情见 枚举
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置类型标题，让用户选择，详情见 枚举
     *
     * @param title 类型标题，让用户选择，详情见 枚举
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取举报人的id
     *
     * @return userid - 举报人的id
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 设置举报人的id
     *
     * @param userid 举报人的id
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * 获取举报时间
     *
     * @return create_date - 举报时间
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置举报时间
     *
     * @param createDate 举报时间
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}