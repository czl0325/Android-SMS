from django.urls import path

from . import views

urlpatterns = [
    path("sms/add", views.sms_add, name='sms_add'),
]