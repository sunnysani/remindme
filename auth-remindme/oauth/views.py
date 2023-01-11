from rest_framework import generics
from rest_framework.exceptions import ValidationError
from rest_framework_simplejwt.tokens import RefreshToken
from .models import UserAccount
from .serializers import LogoutSerializer, UserSerializer
from rest_framework.permissions import AllowAny, IsAuthenticated
from djangorestframework_camel_case.parser import (CamelCaseJSONParser,
                                                   CamelCaseMultiPartParser,
                                                   CamelCaseFormParser)
from djangorestframework_camel_case.render import (CamelCaseJSONRenderer,
                                                   CamelCaseBrowsableAPIRenderer)

from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from rest_framework_simplejwt.views import TokenObtainPairView

from log import API_LOGGER_SIGNAL
import requests

class UserCreate(generics.CreateAPIView):
    serializer_class = UserSerializer
    permission_classes = [AllowAny]
    parser_classes = (CamelCaseJSONParser, CamelCaseFormParser, CamelCaseMultiPartParser, )
    renderer_classes = (CamelCaseJSONRenderer, CamelCaseBrowsableAPIRenderer, )

    def perform_create(self, serializer):
        try:
            serializer = serializer.save()
            return serializer
        except Exception as err:
            error_message = {'error': err}
            raise ValidationError(error_message)

# Untuk get user
class Resource(generics.ListAPIView):
    serializer_class = UserSerializer
    permission_classes = [IsAuthenticated]
    parser_classes = (CamelCaseJSONParser, CamelCaseFormParser, CamelCaseMultiPartParser, )
    renderer_classes = (CamelCaseJSONRenderer, CamelCaseBrowsableAPIRenderer, )

    def get_queryset(self):
        user = UserAccount.objects.get(username = self.request.user)
        return [user]

# Antara pakai logout ini atau langsung di react
class Logout(generics.CreateAPIView):
    serializer_class = LogoutSerializer
    permission_classes = [IsAuthenticated]
    parser_classes = (CamelCaseJSONParser, CamelCaseFormParser, CamelCaseMultiPartParser, )
    renderer_classes = (CamelCaseJSONRenderer, CamelCaseBrowsableAPIRenderer, )

    def perform_create(self, request):
        try:
            refresh_token = request.data["refresh_token"]
            token = RefreshToken(refresh_token)
            token.blacklist()
        except Exception as err:
            error_message = {'error': err}
            raise ValidationError(error_message)

class MyTokenObtainPairSerializer(TokenObtainPairSerializer):

    @classmethod
    def get_token(cls, user):
        token = super().get_token(user)

        # Add custom claims
        token['username'] = user.username

        return token

class MyTokenObtainPairView(TokenObtainPairView):
    serializer_class = MyTokenObtainPairSerializer


# LOGGING
def listener_log(**kwargs):
    data_to_send = kwargs
    url = "http://remindme-log.herokuapp.com/api/log/"
    requests.post(url, data = data_to_send)

API_LOGGER_SIGNAL.listen += listener_log