# RemindMe! Log Service
Log Sentral Aplikasi RemindMe!

#### Nama Anggota Kelompok 3 :
1. Irham Ilman Zhafir - 1806191061
2. I Gede Aditya Premana Putra - 1906350566
3. Naufal Sani - 1906398723
4. Annisa Devi Nurmalasari - 1906292894
5. Alfina Megasiwi - 1906305455

#### Keterangan :
<br/>Komunikasi Auth-Service Lain : REST

#### Link Service : http://remindme-log.herokuapp.com/admin/
* username = admin
* pasword = admin

#### End-point: 
1. Method: POST <br>
Memasukkan log dari service lain ke service log sentral
```shell
   http://remindme-log.herokuapp.com/api/log/
```
2. Method: POST <br>
Menambahkan level log ("INFO", "WARNING", "ERROR")
```shell
   http://remindme-log.herokuapp.com/api/log-type/
```
