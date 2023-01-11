from django.urls import path
from rest_framework.urlpatterns import format_suffix_patterns
from .views import ListLog, ListLogType

urlpatterns = [
    path('log/', ListLog.as_view()),
    path('log-type/', ListLogType.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)
