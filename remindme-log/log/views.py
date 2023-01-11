from rest_framework import generics
from djangorestframework_camel_case.parser import (CamelCaseFormParser,
                                                   CamelCaseJSONParser,
                                                   CamelCaseMultiPartParser)
from djangorestframework_camel_case.render import (CamelCaseBrowsableAPIRenderer,
                                                   CamelCaseJSONRenderer)

from .serializers import LogTypeSerializer, LogSerializer
from .models import LogType, Log

class ListLog(generics.ListCreateAPIView):
    serializer_class = LogSerializer
    parser_classes = (CamelCaseMultiPartParser, CamelCaseFormParser, CamelCaseJSONParser)
    renderer_classes = (CamelCaseJSONRenderer, CamelCaseBrowsableAPIRenderer)

    def get_queryset(self):
        return Log.objects.all()

    def perform_create(self, serializer):
        if(self.request.data.get("service") == 'reminder-service'):

            date_reminder = self.request.data.get("time").split("+", 1)[0]
            date_reminder = date_reminder + "-07:00"
            code = int(self.request.data.get("code"))

            if code >= 200 and code < 400:
                type_log = LogType.objects.filter(id=1)[0]
            elif code >= 400 and code < 500:
                type_log = LogType.objects.filter(id=3)[0]
            else:
                type_log = LogType.objects.filter(id=2)[0]

            serializer.save(time = date_reminder, type = type_log)
        else:
            serializer.save()



class ListLogType(generics.ListCreateAPIView):
    serializer_class = LogTypeSerializer
    parser_classes = (CamelCaseMultiPartParser, CamelCaseFormParser, CamelCaseJSONParser)
    renderer_classes = (CamelCaseJSONRenderer, CamelCaseBrowsableAPIRenderer)

    def get_queryset(self):
        return LogType.objects.all()

    def perform_create(self, serializer):
        serializer.save()

