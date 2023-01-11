from rest_framework import serializers
from .models import Log, LogType

class LogTypeSerializer(serializers.ModelSerializer):
    class Meta:
        model = LogType
        fields = "__all__"

class LogSerializer(serializers.ModelSerializer):
    class Meta:
        model = Log
        fields = "__all__"