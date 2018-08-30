# coding=utf-8
class WeeklyMaidenConfig:
    def __init__(self):
        pass

    nico_username = 'your_nico_username_here'
    nico_password = 'your_nico_password_here'
    nico_downloader_conf_root = '/Volumes/sdcard/vocaloid-weekly-ranking-downloader/conf/'
    nico_downloader_store_root = '/Volumes/sdcard/百度云同步盘/nicoDownloader/'
    tiezi_id_file = nico_downloader_conf_root + 'tiezi_id.txt'
    number_file = nico_downloader_conf_root + 'no.txt'
    vocaloid_download_list_path = nico_downloader_conf_root + 'vocaloid.txt'
    utau_download_list_path = nico_downloader_conf_root + 'utau.txt'
    new_song_download_list_path = nico_downloader_conf_root + 'new.txt'
    recommend_download_list_path = nico_downloader_conf_root + 'recommend.txt'
    nico_store_path = '/Volumes/sdcard/nicoStore1/'
    remote_root = '/Vocaloid_Weekly_Ranking'

config = WeeklyMaidenConfig()
