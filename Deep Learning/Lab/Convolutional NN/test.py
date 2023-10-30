#use gpu for tensorflow
import os
os.environ["CUDA_VISIBLE_DEVICES"]="0"
import tensorflow as tf
import numpy as np

gpus = tf.config.list_physical_devices('GPU')
if not gpus:
    print("Nessuna GPU disponibile.")
else:
    print("GPU disponibile:", gpus)