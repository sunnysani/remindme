from rest_framework import serializers
from .models import UserAccount
from django.contrib.auth.password_validation import validate_password
from rest_framework.exceptions import ValidationError
from django_grpc_framework import proto_serializers
from oauth_proto import resource_pb2

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserAccount
        fields = ('username', 'first_name', 'last_name', 'password')
        extra_kwargs = {'password': {'write_only': True}}

    def create(self, validated_data):
        password = validated_data.pop('password', None)
        instance = self.Meta.model(**validated_data)
        if password is not None:
            try:
                validate_password(password=password, user=instance)
                instance.set_password(password)
                instance.save()
                return instance
            except Exception as err:
                raise err

class LogoutSerializer(serializers.Serializer):
    refresh_token = serializers.CharField()


class UserProtoSerializer(proto_serializers.ModelProtoSerializer):
    class Meta:
        model = UserAccount
        proto_class = resource_pb2.ResourceResponse
        fields = ('username', 'first_name', 'last_name', 'password')
        extra_kwargs = {'password': {'write_only': True}}
