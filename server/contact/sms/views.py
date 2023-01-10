from django.http import HttpResponse


def index(request):
    print(request)
    return HttpResponse("你好，这是首页")
