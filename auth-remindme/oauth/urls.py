from django.urls import path, include
from rest_framework_simplejwt.views import (
    TokenRefreshView,
    TokenVerifyView
)
from . import views

app_name = 'oauth'

urlpatterns = [
    path('api-auth/', include('rest_framework.urls')),
    path('api/token/', views.MyTokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('api/token/verify/', TokenVerifyView.as_view(), name='token_verify'),
    path('api/create-user/', views.UserCreate.as_view()),
    path('api/resource/', views.Resource.as_view()),
    path('api/logout/', views.Logout.as_view()),
]

