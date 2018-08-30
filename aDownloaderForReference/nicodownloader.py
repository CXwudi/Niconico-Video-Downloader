# coding=utf-8
import os
import pycurl
import shutil
import subprocess
import threading
import time
import traceback

from urlparse import urlparse

import selenium.common.exceptions
from selenium import webdriver
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities

from nicologger import logger
from weekly_maiden_config import config

curl_max_threads = 2
curl_semaphore = threading.Semaphore(curl_max_threads)


def parse_top_rank_list_file(path):
    ret = []
    with open(path) as f:
        reading_title = True
        title = None
        for line in f.readlines():
            line = line.strip()
            if line == '':
                continue
            if reading_title:
                title = line
                reading_title = False
            else:
                ret.append((title, line[line.rfind('/') + 1:]))
                reading_title = True
    return ret


def remove_starts_at_begin(str):
    start = 0
    for i in range(0, len(str)):
        if (str[i] != '*'):
            break
        start += 1
    return str[start:]


def parse_new_song_list_file(path):
    ret = []
    with open(path) as f:
        index = 0
        title = None
        for line in f.readlines():
            line = line.strip()
            if line == '':
                continue
            if index == 0:
                title = remove_starts_at_begin(line)
                index = 1
            elif index == 1:
                ret.append((title, line))
                index = 2
            else:
                index = 0
    return ret


def copy_from_store_if_exists(video_code, extension, dst_path):
    src = config.nico_store_path + video_code + extension
    if os.path.isfile(src):
        logger.info('copy file from %s to %s', src, dst_path)
        shutil.copy(src, dst_path + extension)
        return True
    return False


def download_video(browser, video_code, referer, dst_path, mp3_path):
    if os.path.isfile(dst_path + '.mp4') or os.path.isfile(dst_path + '.flv'):
        logger.info('file %s already exists, skip' % dst_path)
        return False
    url, title, video_type, author = fetch_video_info(browser, video_code)
    if copy_from_store_if_exists(video_code, '.mp4', dst_path):
        dst_path += '.mp4'
        subprocess.Popen(
            ['ffmpeg', '-i', dst_path, '-vn', '-acodec', 'libmp3lame', '-ac', '2', '-ab',
             '320k', '-metadata', 'title=' + title, '-metadata', 'artist=' + author,
             mp3_path])
        return True
    if copy_from_store_if_exists(video_code, '.flv', dst_path):
        dst_path += '.flv'
        subprocess.Popen(
            ['ffmpeg', '-i', dst_path, '-vn', '-acodec', 'libmp3lame', '-ac', '2', '-ab',
             '320k', '-metadata', 'title=' + title, '-metadata', 'artist=' + author,
             mp3_path])
        return dst_path, title, author
    dst_path += '.' + video_type.encode('utf-8')
    if os.path.isfile(dst_path):
        logger.info('file %s already exists, skip' % dst_path)
        return True
    cookies = browser.get_cookies()
    browser.get('about:blank')  # 防止浏览器继续拉视频, 节省带宽
    curl_semaphore.acquire()
    curl_semaphore.release()
    if url.endswith('low'):
        dst_path += '.low'
        mp3_path += '.low'
    CurlDownloadThread(curl_semaphore, url, cookies, referer, dst_path, title, author, mp3_path).start()
    return True


last_downloaded_bytes = None
last_byte_changed_time = None


def download_progress(dl_total, dl_now, ul_total, ul_now, context, dst_path):
    if context.last_downloaded_bytes is None:
        context.last_downloaded_bytes = 0
    time_now = time.time()
    if context.last_byte_changed_time is None:
        context.last_byte_changed_time = time_now
    if context.last_log_progress_time is None:
        context.last_log_progress_time = 0
    if time_now - context.last_log_progress_time > 2.0:
        logger.info('downloading %d/%d %s' % (dl_now, dl_total, dst_path))
        context.last_log_progress_time = time_now
    if dl_now != context.last_downloaded_bytes:
        context.last_byte_changed_time = time_now
    elif dl_now == context.last_downloaded_bytes:
        idle_time = time_now - context.last_byte_changed_time
        if idle_time > 30.0:
            context.last_downloaded_bytes = None
            context.last_byte_changed_time = None
            raise Exception("curl timeout", None)
    context.last_downloaded_bytes = dl_now


def build_cookie_header(selenium_cookies, domain):
    ret = ''
    first = True
    for cookie in selenium_cookies:
        if not (domain.endswith(cookie['domain']) or domain == cookie['domain'][1:]):
            continue
        if not first:
            ret += "; "
        else:
            first = False
        ret += cookie['name'] + '=' + cookie['value']
    return ret


def curl_download_video(url, selenium_cookies, referer, dst_path):
    curl = pycurl.Curl()
    curl.setopt(pycurl.URL, url.encode())
    curl.setopt(pycurl.NOPROGRESS, 0)
    download_context = DownloadContext()
    curl.setopt(pycurl.PROGRESSFUNCTION,
                lambda dl_total, dl_now, ul_total, ul_now: download_progress(dl_total, dl_now, ul_total, ul_now,
                                                                             download_context, dst_path))
    # curl.setopt(pycurl.WRITEFUNCTION, write_progress)
    parsed_uri = urlparse(url)
    domain = '{uri.netloc}'.format(uri=parsed_uri)
    cookie_header = build_cookie_header(selenium_cookies, domain)
    curl.setopt(pycurl.HTTPHEADER, [
        'Accept:*/*',
        'Accept-Encoding:gzip, deflate, sdch',
        'Accept-Language:zh-CN,zh;q=0.8,en;q=0.6',
        'Connection:keep-alive',
        ('Referer: ' + referer).encode(),
        'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko)'
        ' Chrome/50.0.2661.102 Safari/537.36',
        ('Cookie: ' + cookie_header).encode()])
    temp_path = config.nico_downloader_conf_root + os.path.basename(dst_path) + '.nicodownloading'
    with open(temp_path, 'wb') as dstFile:
        curl.setopt(pycurl.WRITEDATA, dstFile)
        curl.perform()
        response_code = curl.getinfo(pycurl.HTTP_CODE)
        if response_code >= 400:
            raise Exception('download failed, response code is ' + response_code)
    shutil.move(temp_path, dst_path)


def download_nico_videos(browser, download_list, store_path, mp3_path):
    processed = False
    for item in download_list:
        for i in range(0, 5):
            try:
                filename = item[0].replace('/', '-').replace('\\', '-').replace('?', ' ').replace('*', '') + ' - ' + \
                           item[1]
                processed = download_video(browser, item[1], browser.current_url,
                                           store_path + '/' +
                                           filename, mp3_path + '/' + filename + '.mp3') or processed
                break
            except Exception, e:
                logger.exception(
                    'download failed %s, %s, %s retry %d' % (
                        item[0].decode('utf-8'), item[1].decode('utf-8'), e, i))
                traceback.print_exc()
    return processed


def login_nicovideo(driver):
    driver.get('https://account.nicovideo.jp/login?site=niconico&facebook=1&message=login_lock&twitter=1')
    driver.find_element_by_id('input__mailtel').send_keys(config.nico_username)
    driver.find_element_by_id('input__password').send_keys(config.nico_password)
    driver.find_element_by_id('login__submit').click()


def fetch_video_info(driver, video_code):
    logger.info('start fetching video url for %s' % video_code)
    for i in range(1, 5):
        driver.get('http://www.nicovideo.jp/watch/' + video_code)
        try:
            driver.find_element_by_id('siteHeaderUserNickNameContainer')
            break
        except selenium.common.exceptions.NoSuchElementException:
            try:
                logger.info('not login, start login')
                login_nicovideo(driver)
            except selenium.common.exceptions.TimeoutException:
                logger.exception('login timeout')
    watch_api_content = driver.find_element_by_id('watchAPIDataContainer').get_attribute('innerHTML')
    movie_type = get_movie_type_from_watch_api_content(watch_api_content)
    url = get_url_from_watch_api_content(watch_api_content, movie_type)
    title = driver.find_element_by_class_name('videoHeaderTitle').text
    author = driver.find_element_by_class_name('userName').get_attribute('innerHTML')
    author = author[:author.find(u' ')]
    logger.info('end fetching video url for %s, url=%s' % (video_code, url))
    return url, title, movie_type, author


def get_url_from_watch_api_content(watch_api_content, movie_type):
    typecode = 'v' if movie_type == u'flv' else 'm'
    find_str = '.nicovideo.jp%252Fsmile%253F' + typecode + '%253D'
    p1 = watch_api_content.find(find_str)
    start_pos = p1 + len(find_str)
    end_pos = watch_api_content.find('%', start_pos)
    smile_code = watch_api_content[start_pos:end_pos]
    end_pos = p1
    start_pos = watch_api_content.rfind('smile', None, p1)
    smile_domain = watch_api_content[start_pos: end_pos] + '.nicovideo.jp'
    url = 'http://' + smile_domain + '/smile?' + typecode + '=' + smile_code
    return url


def get_movie_type_from_watch_api_content(watch_api_content):
    find_str = '"movie_type":"'
    start = watch_api_content.find(find_str)
    start += len(find_str)
    end = watch_api_content.find('"', start)
    return watch_api_content[start:end]


def make_dir_if_not_exist(path):
    if not os.path.exists(path):
        os.mkdir(path)


def download_nico_loop():
    make_dir_if_not_exist(nico_downloader_store_week_root)
    make_dir_if_not_exist(vocaloid_mp4_path)
    make_dir_if_not_exist(utau_mp4_path)
    make_dir_if_not_exist(vocaloid_mp3_path)
    make_dir_if_not_exist(utau_mp3_path)
    make_dir_if_not_exist(new_song_mp4_path)
    make_dir_if_not_exist(new_song_mp3_path)
    make_dir_if_not_exist(recommend_song_mp4_path)
    make_dir_if_not_exist(recommend_song_mp3_path)
    vocaloid_list = parse_top_rank_list_file(config.vocaloid_download_list_path)
    utau_list = parse_top_rank_list_file(config.utau_download_list_path)
    new_song_list = parse_new_song_list_file(config.new_song_download_list_path)
    recommend_list = parse_new_song_list_file(config.recommend_download_list_path)
    dcap = dict(DesiredCapabilities.PHANTOMJS)
    # dcap['phantomjs.page.settings.userAgent'] = (
    #     'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 '
    #     '(KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'
    # )
    driver = webdriver.Chrome()
    # driver = webdriver.PhantomJS(desired_capabilities=dcap)
    driver.set_page_load_timeout(60)
    vocaloid_downloaded = download_nico_videos(driver, vocaloid_list, vocaloid_mp4_path, vocaloid_mp3_path)
    utau_downloaded = download_nico_videos(driver, utau_list, utau_mp4_path, utau_mp3_path)
    new_song_downloaded = download_nico_videos(driver, new_song_list, new_song_mp4_path, new_song_mp3_path)
    recommend_song_downloaded = download_nico_videos(driver, recommend_list, recommend_song_mp4_path,
                                                     recommend_song_mp3_path)
    if not (vocaloid_downloaded or utau_downloaded or new_song_downloaded or recommend_song_downloaded):
        return False
    time.sleep(10)
    wait_thread_count = curl_max_threads
    for i in range(0, wait_thread_count):
        curl_semaphore.acquire()
    for i in range(0, wait_thread_count):
        curl_semaphore.release()
    driver.close()
    return True


def read_weekly_number(path):
    with open(path) as f:
        return f.readline().translate(None, '\r\n')


class DownloadContext:
    last_downloaded_bytes = None
    last_byte_changed_time = None
    last_log_progress_time = None


class CurlDownloadThread(threading.Thread):
    def __init__(self, semaphore, url, cookies, referer, dst_path, title, author, mp3_path):
        self.semaphore = semaphore
        self.url = url
        self.cookies = cookies[:]
        self.referer = referer
        self.dst_path = dst_path
        self.title = title
        self.author = author
        self.mp3_path = mp3_path
        threading.Thread.__init__(self)

    def run(self):
        self.semaphore.acquire()
        try:
            logger.info('start download dst_path: %s, url %s' % (self.dst_path, self.url.encode('utf-8')))
            for i in range(0, 5):
                try:
                    curl_download_video(self.url, self.cookies, self.referer, self.dst_path)
                    subprocess.Popen(
                        ['ffmpeg', '-i', self.dst_path, '-vn', '-acodec', 'libmp3lame', '-ac', '2', '-ab',
                         '320k', '-metadata', 'title=' + self.title, '-metadata', 'artist=' + self.author,
                         self.mp3_path])
                    break
                except Exception, e:
                    logger.exception(
                        'curl download failed %s, %s, %s retry %d' % (
                            self.url.decode('utf-8'), self.dst_path.decode('utf-8'), e, i))
                    traceback.print_exc()
            logger.info('end download dst_path: %s, url %s' % (self.dst_path, self.url.encode('utf-8')))
        finally:
            self.semaphore.release()


if __name__ == '__main__':
    # while time.strftime('%H%M') != '0202':
    #     time.sleep(10)
    print 'started'
    # pcs = PCS(config.baidupan_user, config.baidupan_password)
    logger.info('start fetch ranking from tieba')
    # ranking_fetcher.fetch_ranking_main()
    if not os.path.exists(config.number_file):
        logger.info('no weekly number file, exit')
        exit()
    weekly_number = read_weekly_number(config.number_file)
    nico_downloader_store_week_root = config.nico_downloader_store_root + weekly_number
    vocaloid_mp4_path = nico_downloader_store_week_root + '/周刊Vocaloid ' + weekly_number + ' Top 30 PV'
    utau_mp4_path = vocaloid_mp4_path + '/UTAU'
    vocaloid_mp3_path = nico_downloader_store_week_root + '/周刊Vocaloid ' + weekly_number + ' Top 30 MP3'
    utau_mp3_path = vocaloid_mp3_path + '/UTAU'
    new_song_mp4_path = nico_downloader_store_week_root + '/周刊Vocaloid ' + weekly_number + ' 新曲 PV'
    recommend_song_mp4_path = new_song_mp4_path + '/未计入推荐'
    new_song_mp3_path = nico_downloader_store_week_root + '/周刊Vocaloid ' + weekly_number + ' 新曲 MP3'
    recommend_song_mp3_path = new_song_mp3_path + '/未计入推荐'
    logger.info('start downloading from nico')
    for i in range(1, 20):  # 防止网络不稳定有些文件下载失败, 跑多次
        if not download_nico_loop():
            break
            # upload_weekly_folder(pcs)

            # os.system('cp -r ' + nico_downloader_store_week_root + ' /Users/xx3bits/百度云同步盘/')
