# Generated by Django 3.2.13 on 2022-05-26 13:41

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('log', '0003_alter_logtype_name'),
    ]

    operations = [
        migrations.AlterField(
            model_name='log',
            name='request_body',
            field=models.TextField(blank=True),
        ),
        migrations.AlterField(
            model_name='log',
            name='response',
            field=models.TextField(blank=True),
        ),
    ]
