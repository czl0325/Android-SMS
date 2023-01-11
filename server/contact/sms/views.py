from django.http import HttpResponse
import json
from .models import SmsInfo


def sms_add(request):
    lst = request.POST.get('list')
    lst = json.loads(lst)
    mobile = request.POST.get("mobile", "")
    if len(lst) > 0 and mobile != "":
        SmsInfo.objects.filter(mobile=mobile).delete()
        data = []
        for item in lst:
            info = SmsInfo()
            info.sender = item["sender"]
            if "personName" in item:
                info.person = item["personName"]
            info.date = item["date"]
            info.content = item["content"]
            info.read = item["read"]
            info.mobile = mobile
            data.append(info)
        SmsInfo.objects.bulk_create(data)
    return HttpResponse(json.dumps({
        "code": 0,
        "message": "保存短信成功"
    }))
