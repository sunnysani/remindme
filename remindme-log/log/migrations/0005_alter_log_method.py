# Generated by Django 3.2.13 on 2022-06-18 21:07

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('log', '0004_auto_20220526_2041'),
    ]

    operations = [
        migrations.AlterField(
            model_name='log',
            name='method',
            field=models.CharField(max_length=10),
        ),
    ]
