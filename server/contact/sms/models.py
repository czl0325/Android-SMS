from django.db import models
import datetime


class BaseModel(models.Model):
    create_time = models.DateTimeField(auto_now_add=True, verbose_name="创建时间")
    update_time = models.DateTimeField(auto_now=True, verbose_name="更新时间")
    is_delete = models.BooleanField(default=False, verbose_name="删除标记")

    class Meta:
        abstract = True


class SmsInfo(BaseModel):
    sender = models.CharField(max_length=30, verbose_name="发件人号码")
    person = models.CharField(max_length=30, verbose_name="发件人姓名")
    date = models.BigIntegerField(verbose_name="发件日期")
    read = models.BooleanField(default=False, verbose_name="是否已读")
    content = models.CharField(max_length=1024, verbose_name="短信内容")
    mobile = models.CharField(max_length=30, verbose_name="短信主人")

    def __str__(self):
        return "发件人: {sender}, 日期: {date}, 内容: {content}"

    class Meta:
        db_table = "sms"
