import os
from log.events import Events

if os.environ.get('RUN_MAIN', None) != 'true':
    default_app_config = 'log.apps.LoggerConfig'

API_LOGGER_SIGNAL = Events()
