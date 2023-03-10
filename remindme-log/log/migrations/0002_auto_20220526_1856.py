# Generated by Django 3.2.13 on 2022-05-26 11:56

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('log', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='LogType',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(choices=[('INFO', 'INFO'), ('WARNING', 'WARNING'), ('ERROR', 'ERROR')], max_length=7)),
            ],
        ),
        migrations.RenameField(
            model_name='log',
            old_name='size',
            new_name='code',
        ),
        migrations.AddField(
            model_name='log',
            name='request_body',
            field=models.TextField(default=''),
            preserve_default=False,
        ),
        migrations.AddField(
            model_name='log',
            name='response',
            field=models.TextField(default=''),
            preserve_default=False,
        ),
        migrations.AlterField(
            model_name='log',
            name='path',
            field=models.CharField(max_length=100),
        ),
        migrations.AlterField(
            model_name='log',
            name='service',
            field=models.CharField(max_length=100),
        ),
        migrations.AddField(
            model_name='log',
            name='type',
            field=models.ForeignKey(default='', on_delete=django.db.models.deletion.CASCADE, to='log.logtype'),
            preserve_default=False,
        ),
    ]
