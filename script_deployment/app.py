from flask import Flask, request, jsonify, send_from_directory
import tensorflow as tf
from tensorflow.keras.preprocessing import image
import numpy as np
import os
from PIL import Image
from werkzeug.utils import secure_filename

app = Flask(__name__)

# Set the folder to store uploaded files and the allowed file types
app.config['UPLOAD_FOLDER'] = 'uploads/'
app.config['ALLOWED_EXTENSIONS'] = {'png', 'jpg', 'jpeg', 'gif'}

# Create the uploads directory if it doesn't exist
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

LABEL = ['streptococcosis','Fungal diseases Saprolegniasis', 'Healthy Nila Fish']

# Load the model
model = tf.keras.models.load_model('https://storage.googleapis.com/testing-caps12/model-ml/two_diseases_model_4.h5')

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

# Function to preprocess the image
def preprocess_image(image_path):
    image = Image.open(image_path).convert('RGB')  # Ensure the image is in RGB format
    image = image.resize((224, 224))  # Resize to match model input size (e.g., 224x224)
    image = np.array(image) / 255.0  # Normalize pixel values to [0, 1]
    image = np.expand_dims(image, axis=0)  # Add batch dimension (1, 224, 224, 3)
    return image

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
            filename = secure_filename(file.filename)
            file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            file.save(file_path)

            # Preprocess the image
            image = preprocess_image(file_path)

            # Perform prediction
            predictions = model.predict(image, verbose=0)[0]
            #predictions = model.predict(image)
            predicted_class = np.argmax(predictions[0]) # Assuming softmax output
            confidence = float(np.max(predictions[0]))

            # Get the label from the LABEL array
            predicted_label = LABEL[predicted_class]

            # Return the prediction result
            return jsonify({
                "message": "File uploaded and classified successfully",
                "file_path": file_path,
                "model_output": predictions.tolist(),
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
