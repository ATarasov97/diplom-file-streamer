insert into store_descriptions(file_id, start_byte, end_byte, file_name, media_type, content_length, source, id, creation_date)
values ('1', 1048576, 3145728, '1_3145728', 'video/mp4', '10884591', 'server', '1', parsedatetime('17-09-2030 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'));
insert into store_descriptions(file_id, start_byte, end_byte, file_name, media_type, content_length, source, id, creation_date)
values ('2', 1048576, 3072000, '2_3072000', 'video/mp4', '10884591', 'server', '2', parsedatetime('17-09-2030 18:47:52.69', 'dd-MM-yyyy hh:mm:ss.SS'));

insert into sources(id, max_cache_amount)
values ('server', 16777216);
insert into sources(id, max_cache_amount)
values ('view', 16777216);
insert into sources(id, max_cache_amount)
values ('fragment-view', 16777216);