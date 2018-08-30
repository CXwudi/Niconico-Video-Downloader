import logging
import logging.handlers


def create_logger():
    logger = logging.getLogger('nicodownloader')
    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter('%(asctime)s - %(filename)s:%(lineno)d - %(levelname)s - %(message)s'))
    logger.setLevel(logging.DEBUG)
    logger.addHandler(handler)
    return logger


logger = create_logger()
