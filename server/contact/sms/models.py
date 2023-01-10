from django.db import models


class BaseModel(models.Model):
    createdTime = models.DateTimeField(auto_now_add=True)
    updateTime = models.DateTimeField(auto_now=True)
    deleted = models.BooleanField(default=False)


class SmsInfo(BaseModel):
    sender = models.CharField(max_length=30)
    date = models.BigIntegerField()
    read = models.BooleanField(default=False)
    content = models.CharField(max_length=300)
    mobile = models.CharField(max_length=30)

    def __str__(self):
        return "发件人: {sender}, 日期: {date}, 内容: {content}"

    class Meta:
        db_table = "sms"
