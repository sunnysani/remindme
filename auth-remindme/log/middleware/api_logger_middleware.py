import json
from django.urls import resolve
import datetime
import pytz

from log import API_LOGGER_SIGNAL
from log.utils import get_headers, mask_sensitive_data

import jwt
from auth_remindme.settings import SIMPLE_JWT

class APILoggerMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):

        namespace = resolve(request.path).namespace

        # Always skip Admin panel
        if namespace == 'admin':
            return self.get_response(request)

        request_data = ''
        try:
            request_data = json.loads(request.body) if request.body else ''
        except:
            pass

        # Code to be executed for each request before
        # the view (and later middleware) are called.
        response = self.get_response(request)

        headers = get_headers(request=request)
        method = request.method

        if response.get('content-type') in ('application/json', 'application/vnd.api+json',):
            if getattr(response, 'streaming', False):
                response_body = '** Streaming **'
            else:
                if type(response.content) == bytes:
                    response_body = json.loads(response.content.decode())
                else:
                    response_body = json.loads(response.content)

            api = request.get_full_path()

            status_code=response.status_code

            if status_code >= 500:
                type_log = 2
            elif status_code >=400 and status_code < 500:
                type_log = 3
            else:
                type_log = 1

            token = request.META.get('HTTP_AUTHORIZATION', " ").split(' ')[1]

            username = 'guest'
            try:
                valid_data = jwt.decode(token, SIMPLE_JWT['SIGNING_KEY'], algorithms=[SIMPLE_JWT['ALGORITHM']],)
                username = valid_data['username']
            except Exception as v:
                print("validation error", v)

            time_now_zone = datetime.datetime.now(pytz.timezone("Asia/Jakarta"))
            date_time_str = time_now_zone.strftime("%Y-%m-%d %H:%M:%S")
            data = dict(
                time= date_time_str,
                service="auth-remindme",
                type=type_log,
                host=mask_sensitive_data(headers).get("HOST"),
                user=username,
                method=method,
                path=api,
                code=status_code,
                requestBody=str(mask_sensitive_data(request_data)),
                response=str(mask_sensitive_data(response_body)),
            )
            API_LOGGER_SIGNAL.listen(**data)
        else:
            return response

        return response
