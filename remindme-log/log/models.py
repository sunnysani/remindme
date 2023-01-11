from django.db import models

class LogType(models.Model):
    name = models.CharField(max_length=7)

class Log(models.Model):
    time = models.DateTimeField()
    service = models.CharField(max_length=100)
    type = models.ForeignKey(
        LogType,
        on_delete=models.CASCADE
    )
    host = models.CharField(max_length=100)
    user = models.CharField(max_length=100, default="guest")
    method = models.CharField(max_length=10)
    path = models.CharField(max_length=100)
    code = models.IntegerField()
    request_body = models.TextField(blank=True)
    response = models.TextField(blank=True)


