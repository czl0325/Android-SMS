from django.http import HttpResponse
from django.shortcuts import render


def index(request):
    return HttpResponse("你好，这是首页")
