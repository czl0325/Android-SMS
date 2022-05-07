from django.db import models


class SmsInfo(models.Model):
    sender = models.CharField(max_length=30)
    date = models.BigIntegerField()
    read = models.BooleanField(default=False)
    content = models.CharField(max_length=300)
    mobile = models.CharField(max_length=30)

    def __str__(self):
        return "发件人: {sender}, 日期: {date}, 内容: {content}"

    class Meta:
        db_table = "sms"
