package com.xibin.core.pojo;


/**
 * @author: 张锦标
 * @date: 2023/4/2 15:03
 * ReceiveMessage类
 */

public class ReplyMessage {
    /**
     * 开发者微信号
     */
    private String ToUserName;
    /**
     * 发送方账号(一个openid）
     */
    private String FromUserName;
    /**
     * 消息创建时间（整形）
     */
    private String CreateTime;
    /**
     * 消息类型
     */
    private String MsgType;
    /**
     * 文本消息内容
     */
    private String Content;
    /**
     * 消息ID 64位
     */
    String MsgId;
    /**
     * 消息的数据ID 消息来自文章才有
     */
    private String MsgDataId;
    /**
     * 多图文时第几篇文章，从1开始 消息如果来自文章才有
     */
    private String Idx;
    /**
     * 订阅事件 subscribe订阅 unsbscribe取消订阅
     */
    private String Event;


    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getMsgId() {
        return MsgId;
    }

    public void setMsgId(String msgId) {
        MsgId = msgId;
    }

    public String getMsgDataId() {
        return MsgDataId;
    }

    public void setMsgDataId(String msgDataId) {
        MsgDataId = msgDataId;
    }

    public String getIdx() {
        return Idx;
    }

    public void setIdx(String idx) {
        Idx = idx;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }
}


