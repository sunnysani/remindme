from django.contrib import admin
from django.contrib.auth.models import User
from django.contrib.auth.models import Group
from .models import Log, LogType

# Register your models here.
class LogAdmin(admin.ModelAdmin):
    list_display = ('id',
                    'time',
                    'service',
                    'get_log_type',
                    'host',
                    'user',
                    'method',
                    'path',
                    'code',
                    'request_body',
                    'response')
    readonly_fields = list_display

    def get_log_type(self, obj):
         logging_type = LogType.objects.get(id=obj.type.id)
         return str(logging_type.name)

    def has_add_permission(self, request, obj=None):
            return

    def has_delete_permission(self, request, obj=None):
        return False

    def has_change_permission(self, request, obj=None):
        return False

class LogTypeAdmin(admin.ModelAdmin):
    list_display = ('id',
                    'name')
    readonly_fields = list_display


admin.site.register(Log, LogAdmin)
admin.site.register(LogType, LogTypeAdmin)
admin.site.unregister(User)
admin.site.unregister(Group)

admin.site.site_header  =  "RemindMe! Log Site"
admin.site.site_title  =  "Log"
admin.site.index_title  =  "RemindMe!"