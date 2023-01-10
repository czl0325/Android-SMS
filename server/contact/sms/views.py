from django.http import HttpResponse
import json
from .models import SmsInfo


def sms_add(request):
    info = SmsInfo()
    info.sender = request.POST.get("sender", "")
    info.person = request.POST.get("personName", "")
    info.date = request.POST.get("date", "")
    info.content = request.POST.get("content", "")
    info.read = request.POST.get("isRead", "")
    info.mobile = request.POST.get("mobile", "")
    return HttpResponse(json.dumps(info, ensure_ascii=False, default=lambda obj: obj.__dict__))
