# %%
# General Library
import cv2
import numpy as np
import tensorflow as tf
import matplotlib.pyplot as plt
# Data Handling
import os
import zipfile
from PIL import Image

# %%
# For saving model
import keras

# %%
# You can modify this, ganti ganti sj kalau directorynya berbeda, ini pake struktur fish diseases->jenis jenis penyakitnya
# Jadi langsung satu file dipisah train sama validasinya ada di line selanjutnya
DATA_DIR = './Fish Diseases/'

# %%
def train_val_dataset():
    training_dataset, validation_dataset = tf.keras.preprocessing.image_dataset_from_directory(
        directory=DATA_DIR, 
        image_size=(220, 220), 
        batch_size=20,
        label_mode = 'categorical',
        validation_split=0.2,
        subset='both',
        shuffle=True,
        seed=42)
    return training_dataset, validation_dataset

# %%
training_dataset, validation_dataset = train_val_dataset()

# %%
def create_model():
    model = tf.keras.models.Sequential([
        tf.keras.layers.Input(shape=(220,220,3)),
        tf.keras.layers.Rescaling(1./255),

        tf.keras.layers.Conv2D(16, (3,3), activation='relu'),
        tf.keras.layers.MaxPooling2D(2,2),
        tf.keras.layers.Conv2D(8, (3,3), activation='relu'),
        tf.keras.layers.MaxPooling2D(2,2),
        
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dropout(0.3),
        tf.keras.layers.Dense(512, activation='relu'),
        tf.keras.layers.Dense(7, activation='softmax')
    ])
    
    model.compile( 
        optimizer=tf.keras.optimizers.Adam(),
        loss='categorical_crossentropy',
        metrics=['accuracy'] 
	)
    return model

# %%
model = create_model()

# %%
model.summary()

# %%
# Define a Callbacks for model.fit later
class EarlyStoppingCallback(tf.keras.callbacks.Callback):
    def on_epoch_end(self, epoch, logs=None):
        if logs['accuracy'] >= 0.95:
            self.model.stop_training = True
            print("\n Reached 95% accuracy!")

# %%
history = model.fit(
    training_dataset,
    epochs=10,
    validation_data=validation_dataset,
    callbacks = [EarlyStoppingCallback()]
)

# %%
# Get training and validation accuracies
acc = history.history['accuracy']
val_acc = history.history['val_accuracy']
loss = history.history['loss']
val_loss = history.history['val_loss']

# Get number of epochs
epochs = range(len(acc))

fig, ax = plt.subplots(1, 2, figsize=(10, 5))
fig.suptitle('Training and validation accuracy')

for i, (data, label) in enumerate(zip([(acc, val_acc), (loss, val_loss)], ["Accuracy", "Loss"])):
    ax[i].plot(epochs, data[0], 'r', label="Training " + label)
    ax[i].plot(epochs, data[1], 'b', label="Validation " + label)
    ax[i].legend()
    ax[i].set_xlabel('epochs')

plt.show()

# %%
model.save("my_model1.keras")

# %%



