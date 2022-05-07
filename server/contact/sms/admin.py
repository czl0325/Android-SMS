from django.contrib import admin
from .models import SmsInfo


class SmsInfoAdmin(admin.ModelAdmin):
    list_display = ['id', 'mobile', 'sender', 'content']


admin.site.register(SmsInfo, SmsInfoAdmin)
