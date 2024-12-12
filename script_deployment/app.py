from flask import Flask, request, jsonify, send_from_directory
import tensorflow as tf
from tensorflow.keras.preprocessing import image
import numpy as np
import os
from PIL import Image
from werkzeug.utils import secure_filename
import io
from google.cloud import firestore
from google.cloud import storage
import time

app = Flask(__name__)

# path to json credential
key_file = 'firestore-service-account.json'

# initial firestore credential file
db = firestore.Client.from_service_account_json(key_file)

# Initialize Google Cloud Storage client
storage_client = storage.Client()
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "./storage-service-account.json"

# Specify Google Cloud Storage bucket name
bucket_name = 'fishmate-bucket'
bucket = storage_client.get_bucket(bucket_name)

# Set the folder to store uploaded files and the allowed file types
app.config['UPLOAD_FOLDER'] = 'uploads/'
app.config['ALLOWED_EXTENSIONS'] = {'png', 'jpg', 'jpeg', 'gif'}

# Create the uploads directory if it doesn't exist
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

LABEL = ['Normal Nile Fish','Fungal diseases Saprolegniasis', 'Streptococcosis']

# Load the model
model = tf.keras.models.load_model('.\\model\\two_diseases_model_4.h5')

@app.route("/")
def index():
    return {"status": "SUCCESS", "message": "Service is up"}

@app.route("/sapa")
def sapa():
    args = request.args
    nama = args.get('nama', default="Cean")
    return {"status": "SUCCESS", "message": f"Selamat Datang {nama}"}

# Function to check allowed file extensions
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']

# Function to upload file to Google Cloud Storage with predicted label as filename
def upload_to_gcs(file, predicted_label):
    # Get the file extension (e.g., .jpg, .png)
    file_extension = file.filename.rsplit('.', 1)[1].lower()
    
    # Generate a filename based on the predicted label and original file extension
    filename = f"{secure_filename(predicted_label)}-{int(time.time())}.{file_extension}"
    # print(f"Generated filename: {filename}")
    
    # Create a blob in the specified bucket and upload the file
    blob = bucket.blob(f'uploads/{filename}')
    
    # Upload the file to Cloud Storage
    blob.upload_from_file(file)
    
    # Optionally, make the file publicly accessible
    # blob.make_public()
    
    return blob.public_url  # Return the URL of the uploaded file

# Function to preprocess the image
def preprocess_image(file):
    # with open(image_path, 'rb') as file:
    #     image_bytes = file.read()
    pillow_img = Image.open(file.stream).convert('RGB')

    # Transform image same as for training
    data = np.asarray(pillow_img)
    # data = data/255.0
    data = np.expand_dims(data, axis=0)  
    image_resize = tf.image.resize(data, [224, 224])
    return image_resize

@app.route('/upload', methods=['POST'])
def upload_and_classify():
    try:
        if 'file' not in request.files:
            return jsonify({"error": "No file part"}), 400

        file = request.files['file']
        
        if file.filename == '':
            return jsonify({"error": "No selected file"}), 400

        if file and allowed_file(file.filename):
            # Save the file
            # filename = secure_filename(file.filename)
            # file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            # file.save(file_path)

            # Reset file stream to the beginning before processing
            file.stream.seek(0)

            # Preprocess the image
            image_data = preprocess_image(file)

            # Perform prediction
            predictions = model.predict(image_data)
            #predictions = model.predict(image)
            predicted_class = np.argmax(predictions[0]) # Assuming softmax output
            confidence = float(np.max(predictions[0]))
            predictions_numpy = tf.Variable(predictions).numpy().tolist()
            # Get the label from the LABEL array
            predicted_label = LABEL[predicted_class]

            # Reset file stream to the beginning before uploading
            file.stream.seek(0)

            # Upload the file to Google Cloud Storage using predicted label
            file_url = upload_to_gcs(file, predicted_label)

            # inital path to connect to firestore
            diagnosis_ref = db.collection('diagnosis')
            query = diagnosis_ref.where('label', '==', predicted_label)
            results = query.stream()

            # take the result and convert to json
            diagnosis_data = []
            for doc in results:
                diagnosis_data.append(doc.to_dict())

            # Return the prediction result
            return jsonify({
                "message": "File uploaded and classified successfully",
                "file_url": file_url,
                "model_output": predictions_numpy,
                "diagnosis": diagnosis_data[0],
                "predicted_class": predicted_label,
                "confidence": confidence
            }), 200

        return jsonify({"error": "Invalid file type"}), 400
    except Exception as e:
        # Log the error and return a 500 response
        print(f"Error: {e}")
        return jsonify({"error": "Internal server error", "message": str(e)}), 500
    
@app.route('/uploads/<filename>') 
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

if __name__ == '__main__':
    app.run(debug=True)